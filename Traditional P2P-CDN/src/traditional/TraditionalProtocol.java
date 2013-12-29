package traditional;

import java.util.LinkedList;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.edsim.*;
import peersim.cdsim.*;
import peersim.core.Node;

public class TraditionalProtocol implements EDProtocol, CDProtocol,
		TraditionalOverlay {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------
	public static final int maxLandmarkRTT = 70;
	public static final int minLandmarkRTT = 30;

	/**
	 * Node Tags
	 */
	public static final int CDNTag = 0;
	public static final int SupplyingPeerTag = 1;
	public static final int RegularTag = 2;

	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------
	/**
	 * String name of the parameter, assigned to pid
	 */
	private static final String PAR_PROT = "protocol";

	/**
	 * String name of the parameter, assigned to tid
	 */
	private static final String PAR_TRANS = "transport";

	/**
	 * String name of the parameter, assigned to nid
	 */
	private static final String PAR_NET = "network";
	
	/**
	 * String name of the parameter, assigned to maxClients
	 */
	private static final String PAR_MAXCLIENTS = "maxclients";

	// ------------------------------------------------------------------------
	// Static Fields
	// ------------------------------------------------------------------------

	/**
	 * Protocol identifier, obtained from config property {@link #PAR_PROT}.
	 **/
	private static int pid;

	/**
	 * Transport Protocol identifier, obtained from config property
	 * {@link #PAR_TRANS}.
	 **/
	private static int tid;
	/**
	 *  Network Protocol identifier, obtained from config property {@link #PAR_TRANS}. 
	 **/
	private static int nid;	
	/**
	 * max number of possible clients for CDNs and SuperPeers, obtained from
	 * config property {@link #PAR_MAXCLIENTS}.
	 ***/
	public static int maxClients;

	/***
	 * The nodes corresponding to the 3 CDNs in the set-up
	 */
	public static Node CDN1;
	public static Node CDN2;
	public static Node CDN3;

	/**
	 * Attributes
	 */
	int nodeTag; // Type of node: 0 - CDN, 1 - Superpeer, 2 - Regular
	Node connectedCDN; // the CDN node it is closest/connected to
	int CID; // Which CDN range [1, 3]
	int cdnRTT; // RTT of a client to its CDN;
	int uploadSpd; // maximum upload capacity
	int downloadSpd; // maximum download capacity
	int usedUploadSpd; // used upload speed
	int uploadSpdBuffer; // reserved upload spd for peers requesting connection,
							// to be alloted when the peer accepts the upload
							// spd
	int usedDownloadSpd; // used download speed
	int videoID; // ID of the video it is streaming
	int videoSize; // size of the video it is watching
	int streamedVideoSize; // size already streamed
	int numClients; // number of clients
	int videoSpdAlloted[];
	int[] videoList; // list of videos
	long startTime; // Time the node was initialized
	long elapsedTime; // Time it took from initialization until completion of
						// stream
	int numCandidates;
	int candidateReplies = 0;
	int uploaded = 0;
	int cdnID;
	double averageRTT;
	int maxVideoSpd = 400;
	boolean firstConnect = false;
	boolean firstPlayback = false;
	long firstPlay;
	int numConnectionsAttempted = 0;
	int numConnectionsAccepted = 0;
	int numFirstConnectedPeers = 0; //number of parent peers connected contributing to the first playback	

	Node[] supplyingPeerList; // list of supplying peer under the CDN
	int numSupplier = 0;
	Node[] clientList; // applicable to CDN (both regular and supplying are
						// here)
	Node[] peerList; // list of peers the node uploads to, applicable to
						// supplying peer and CDN
	int[] peerSpdAlloted; // speed alloted to peers
	int numPeers = 0; // number of peers it contributes to
	Node[] sourcePeerList; // list of peers that contribute to a regular node
	Node[] candidateSupplyingPeers; // sent by the CDN to a regular peer
	int numSource; // number of source peers that contribute to the node
	boolean startedStreaming = false; // true if the node is already streaming
	boolean doneStreaming = false; // true if videoSize<= streamedVideoSize
	int contractSize = 0;
	boolean contractExpired = false;
	LinkedList<Node> peerPool = new LinkedList<Node>();
	int[] peerRTT;

	public TraditionalProtocol(String prefix) {
		maxClients = Configuration.getInt(prefix + "." + PAR_MAXCLIENTS);
		tid = Configuration.getPid(prefix + "." + PAR_TRANS);
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		nid = Configuration.getPid(prefix + "." + PAR_NET);
	}

	public Object clone() {
		TraditionalProtocol prot = null;
		try {
			prot = (TraditionalProtocol) super.clone();
		} catch (CloneNotSupportedException e) {
		} // never happens
		return prot;
	}

	@Override
	public void nextCycle(Node node, int pid) {
		
		Router router = (Router)node.getProtocol(nid);
		router.nextCycle(node, pid);
		
		// System.out.println("Nagnext Cycle");
		TraditionalProtocol prot = (TraditionalProtocol) node.getProtocol(pid);
		if (startedStreaming == true && !contractExpired) {
			// System.out.println("Pumasok?");

			if (nodeTag == SupplyingPeerTag || nodeTag == CDNTag) {

				for (int i = 0; i < numPeers; i++)
					if (peerList[i] != null) {
						//if(nodeTag == SupplyingPeerTag)
						//	System.out.println("incomplete");
						int given = 0;
						while (given < peerSpdAlloted[i]){
							int toGive= 0;
							if(peerSpdAlloted[i] - given <= 856)
								toGive = peerSpdAlloted[i] - given + 168;
							else toGive = 856;
							prot.sendMsg(new TraditionalMessage(TraditionalMessage.UPLOAD, node, peerList[i], toGive+168,
									peerSpdAlloted[i]), peerRTT[i]);
							
							given += toGive;
						}
						uploaded += peerSpdAlloted[i];
						if (nodeTag == SupplyingPeerTag && uploaded > contractSize) {
							prot.sendMsg(new TraditionalMessage(TraditionalMessage.CONTRACT_EXPIRED, node, connectedCDN, 168), cdnRTT);
							
							for (int j = 0; j < numPeers; j++) {
								if (peerList[j] != null) {								
									prot.sendMsg(new TraditionalMessage (TraditionalMessage.SP_RP_DISCONNECT_FULFILLED,
											node, peerList[j], 296, peerSpdAlloted[j]), peerRTT[j]);
								}
							}
							contractExpired = true;
							break;
						}
					}
			}
			if (nodeTag == RegularTag) {
				if (usedDownloadSpd < downloadSpd && peerPool.isEmpty()) {
					prot.sendMsg(new TraditionalMessage (TraditionalMessage.GIVE_SP_LIST, 
							node, connectedCDN, 200, videoID), cdnRTT);
				}
			}
		}
	}

	@Override
	public void processEvent(Node node, int pid, Object event) {
		TraditionalMessage aem = (TraditionalMessage) event;		
		TraditionalProtocol prot = (TraditionalProtocol) node.getProtocol(pid);

		if (nodeTag == CDNTag)// messages received by the CDN 
		{

			if (aem.msgType == TraditionalMessage.GIVE_SP_LIST) {
				// In the message: the videoID of the Requesting/Regular Peer
				// send back results of getSupplyingPeers(int video) - a list of
				// supplying peers with that video
				Node[] tobeRet = getSupplyingPeers(aem.data);
				//prot.sendMsg(new TraditionalMessage(TraditionalMessage.RECEIVE_SP_LIST,
					//	node, aem.sender, 0, tobeRet), aem.delay);
				
				LinkedList<Node> spBuffer = new LinkedList<Node>();
				int spBatchNum = 0;
				if (tobeRet!=null)
				for (int i = 0; i < tobeRet.length ; i++){
					if(tobeRet[i]!=null){
						spBuffer.add(tobeRet[i]);
						spBatchNum++;
						if(spBatchNum == 6){
							prot.sendMsg(new TraditionalMessage(TraditionalMessage.INCOMPLETE_RECEIVE_SP_LIST,
									node, aem.sender, 936, spBuffer), aem.delay);
							spBatchNum = 0;
							spBuffer.removeAll(spBuffer);
						}
					}
				}
				prot.sendMsg(new TraditionalMessage(TraditionalMessage.COMPLETE_RECEIVE_SP_LIST,
						node, aem.sender, spBuffer.size()*128+168, spBuffer), aem.delay);
						
				// System.out.println("Received: GIVE_SP_LIST");
			} else if (aem.msgType == TraditionalMessage.CDN_RP_CONNECT) {
				// A Requesting Peer wants to connect directly to a CDN (i.e. no
				// supplying peers for the video the node needs)
				// Reply with a confirm connect connection message
				// System.out.println("CDN connect");
				int spdAvail = maxVideoSpd - videoSpdAlloted[aem.data];

				if (spdAvail <= 0) {				
					prot.sendMsg(new TraditionalMessage (TraditionalMessage.REJECT, node,
							aem.sender, 168), aem.delay);
					
				} else {
					videoSpdAlloted[aem.data] = maxVideoSpd;
					if (aem.data2 < spdAvail) {
						videoSpdAlloted[aem.data] = videoSpdAlloted[aem.data]
								- spdAvail + aem.data2;
						spdAvail = aem.data2;
					}				
					prot.sendMsg(new TraditionalMessage (TraditionalMessage.CDN_RP_CONNECT_CONFIRM,
							node, aem.sender, 296, spdAvail), aem.delay);
				}
			} else if (aem.msgType == TraditionalMessage.CDN_RP_DISCONNECT) {
				// Reply with a confirm disconnect message
				TraditionalProtocol p = (TraditionalProtocol) aem.sender
						.getProtocol(pid);
				int allotedSpd = 0;
				int i = 0;
				for (i = 0; i < numPeers; i++) {
					if (peerList[i] != null && peerList[i].equals(aem.sender)) {
						allotedSpd = peerSpdAlloted[i];
						break;
					}
				}
				peerList[i] = null;
				peerSpdAlloted[i] = 0;
				videoSpdAlloted[p.videoID] -= allotedSpd;
			} else if (aem.msgType == TraditionalMessage.CDN_RP_CONNECT_ACCEPT) {
				// uploadSpdBuffer -= aem.data;
				// usedUploadSpd -= uploadSpdBuffer;
				videoSpdAlloted[aem.data2] -= aem.data3;
				peerList[numPeers] = aem.sender;
				peerSpdAlloted[numPeers] = aem.data;
				peerRTT[numPeers] = aem.delay;
				numPeers++;
				averageRTT = ((numPeers-1)*averageRTT + aem.delay)/numPeers;
				
				// System.out.println("Accept");
			} else if (aem.msgType == TraditionalMessage.SP_RP_CONNECT) {
				// Reply with a confirm connect message
				prot.sendMsg(new TraditionalMessage (TraditionalMessage.CONFIRM_CONNECT, node, aem.sender, 296, aem.node), aem.delay);
			} else if (aem.msgType == TraditionalMessage.SP_RP_DISCONNECT) {
				// if(aem.node == null) System.out.println("May sala");
				Node tobesent = aem.node;
				// if(tobesent != null) System.out.println("WHY?");
				// System.out.println("eto ba yung next?");
				prot.sendMsg(new TraditionalMessage(TraditionalMessage.SP_RP_DISCONNECT_CONFIRM, node,
						aem.sender, 296, tobesent), aem.delay);
			} else if (aem.msgType == TraditionalMessage.CONTRACT_SET) {
				// Reply with a message saying how long the node stays if it
				// becomes a Supplying Peer
			} else if (aem.msgType == TraditionalMessage.CONTRACT_EXPIRED) {
				// Reply with a confirm disconnect message, supplying peer has
				// finished serving
				for (int i = 0; i < numSupplier; i++) {
					if (aem.sender.equals(supplyingPeerList[i])) {
						supplyingPeerList[i] = null;
						break;
					}

				}
			} else if (aem.msgType == TraditionalMessage.RP_DONE_STREAMING) {
				supplyingPeerList[numSupplier] = aem.sender;
				numSupplier++;
				prot.sendMsg(new TraditionalMessage (TraditionalMessage.CONTRACT_SET, node,
						aem.sender, 170, 4), aem.delay);
				// System.out.println("CDN: Done Streaming");
			}
		} else if (nodeTag == SupplyingPeerTag)// messages received by the SP
		{
			if (aem.msgType == TraditionalMessage.CONFIRM_CONNECT) {
				// CDN has approved the connection
				// Send a CONFIRM_CONNECT to RP to start connection

				if (aem.node != null) {
					if (uploadSpd - usedUploadSpd - uploadSpdBuffer > 0) {
						uploadSpdBuffer += uploadSpd - usedUploadSpd;
						TraditionalProtocol p = (TraditionalProtocol) aem.node
								.getProtocol(pid);
						if (p.nodeTag == RegularTag) {
							prot.sendMsg(new TraditionalMessage (TraditionalMessage.CONFIRM_CONNECT, node,
									aem.node, 232, uploadSpdBuffer), aem.delay);
						}
					} else {
						prot.sendMsg(new TraditionalMessage(TraditionalMessage.REJECT, node, aem.node, 232, uploadSpdBuffer), aem.delay);

					}
				}
			} else if (aem.msgType == TraditionalMessage.SP_RP_CONNECT) {
				// Send a SP_RP_CONNECT message to CDN to get its approval
				if (uploadSpd - usedUploadSpd - uploadSpdBuffer > 0) {
					prot.sendMsg(new TraditionalMessage(TraditionalMessage.SP_RP_CONNECT, node, connectedCDN, 296, aem.sender), cdnRTT);
				} else {
					prot.sendMsg(new TraditionalMessage(TraditionalMessage.REJECT, node, aem.sender, 168), aem.delay);
				}
			} else if (aem.msgType == TraditionalMessage.SP_RP_DISCONNECT) {
				// Reply SP_RP_DISCONNECT message to CDN to get its approval
				// System.out.println("twice?");
				prot.sendMsg(new TraditionalMessage(TraditionalMessage.SP_RP_DISCONNECT, node, 
						connectedCDN, 296, aem.sender), cdnRTT);
				for (int i = 0; i < numPeers; i++) {
					if (peerList[i] != null)
						if (peerList[i].equals(aem.sender)) {
							usedUploadSpd -= peerSpdAlloted[i];
							peerSpdAlloted[i] = 0;
							peerList[i] = null;
							
							break;
						}

				}
			} else if (aem.msgType == TraditionalMessage.SP_RP_DISCONNECT_CONFIRM) {
				// asda
				// if(aem.node == null) System.out.println("error");
				// if(aem.sender.equals(connectedCDN))
				// System.out.println("yeah");
				if (aem.node != null) {
					prot.sendMsg(new TraditionalMessage(TraditionalMessage.SP_RP_DISCONNECT_CONFIRM, node,
							aem.node, 168), aem.delay);
				}
			} else if (aem.msgType == TraditionalMessage.CONFIRM_ACCEPT) {
				// sada
				uploadSpdBuffer -= aem.data;
				usedUploadSpd += uploadSpdBuffer;
				uploadSpdBuffer = 0;
				peerList[numPeers] = aem.sender;
				peerSpdAlloted[numPeers] = aem.data;
				peerRTT[numPeers] = aem.delay;
				numPeers++;
				averageRTT = ((numPeers-1)*averageRTT + aem.delay)/numPeers;
				System.out.println(averageRTT);
				
				// System.out.println("May nakapagconnect?");
			}
		} else if (nodeTag == RegularTag)// messages received by Regular Node
		{
			if (aem.msgType == TraditionalMessage.RECEIVE_SP_LIST) {
				// The Supplying Peers with the video
				// if not null, request connection with these Supplying Peers
				// else request connection with CDN itself (CDN_RP_CONNECT)
				if (aem.nodeList == null) {
					
					prot.sendMsg(new TraditionalMessage(TraditionalMessage.CDN_RP_CONNECT, node, connectedCDN, 
							264, videoID, downloadSpd-usedDownloadSpd),cdnRTT);
					numConnectionsAttempted++;

				} else {
					int sent = 0;
					for (int i = 0; i < aem.nodeList.length; i++) {
						if (aem.nodeList[i] != null) {
							// TODO size
							prot.sendMsg(new TraditionalMessage(TraditionalMessage.SP_RP_CONNECT, node, aem.nodeList[i], 0), CommonState.r.nextInt(1470)+30);
							sent++;
							numConnectionsAttempted++;
						}
					}
					if (sent == 0) {
						// TODO size
						prot.sendMsg(new TraditionalMessage(TraditionalMessage.CDN_RP_CONNECT, node, connectedCDN,
								264, videoID, downloadSpd-usedDownloadSpd), cdnRTT);
						numConnectionsAttempted++;
					}
				}
				// System.out.println("Received: RECEIVE_SP_LIST");
			} else if (aem.msgType == TraditionalMessage.INCOMPLETE_RECEIVE_SP_LIST){
				while(!aem.spList.isEmpty()){
					peerPool.add(aem.spList.remove());
				}
				
			}else if (aem.msgType == TraditionalMessage.COMPLETE_RECEIVE_SP_LIST){
				while(!aem.spList.isEmpty()){
					peerPool.add(aem.spList.remove());
					
				}
				if (peerPool.isEmpty()){
					prot.sendMsg(new TraditionalMessage(TraditionalMessage.CDN_RP_CONNECT, node, connectedCDN, 
							264, videoID, downloadSpd-usedDownloadSpd), cdnRTT);
					numConnectionsAttempted++;
				}
				else{
					
					int sent = 0;
					Node sp;
					while(downloadSpd-usedDownloadSpd>0&&!peerPool.isEmpty()){
						sp = peerPool.remove();
						if(sp!=null){
							int delay;
							if(CommonState.r.nextInt(3) == 1)
								delay = CommonState.r.nextInt(970) + 20;
							else{
								delay = CommonState.r.nextInt(500)+1000;
								//System.out.println("Happened?");
							}
							prot.sendMsg(new TraditionalMessage(TraditionalMessage.SP_RP_CONNECT, node, sp, 168), delay);
							sent++;
							numConnectionsAttempted++;
						}
					}
					if (sent == 0) {
						prot.sendMsg(new TraditionalMessage(TraditionalMessage.CDN_RP_CONNECT, node, connectedCDN,
								264, videoID, downloadSpd-usedDownloadSpd), cdnRTT);
						numConnectionsAttempted++;
					}
					//else
						//System.out.println("incomplete");
				}
			}else if (aem.msgType == TraditionalMessage.CDN_RP_CONNECT_CONFIRM) {
				// System.out.println("Confirmed");
				sourcePeerList[numSource] = aem.sender;
				/*averageRTT = (averageRTT * numSource + cdnRTT)
						/ (numSource + 1);*/
				numSource++;
				
				if(!firstPlayback)
				{
					numFirstConnectedPeers++;
				}
				
				int spdAvail = downloadSpd - usedDownloadSpd;
				if (spdAvail > aem.data) {
					spdAvail = aem.data;
				}
				if (!firstConnect) {
					setTimeElapsed();
					firstConnect = true;
					// System.out.println("elapsed: "+elapsedTime+"   |  Current: "+System.currentTimeMillis());
					// if(spdAvail <= 0) System.out.println("MALI");
				}
				prot.sendMsg(new TraditionalMessage(TraditionalMessage.CDN_RP_CONNECT_ACCEPT, node, connectedCDN,
						328, spdAvail, videoID, aem.data-spdAvail), aem.delay);
				usedDownloadSpd += spdAvail;
				startedStreaming = true;
				
				numConnectionsAccepted++;
			} else if (aem.msgType == TraditionalMessage.CONTRACT_SET) {
				contractSize = videoSize * aem.data;
				nodeTag = SupplyingPeerTag;
			} else if (aem.msgType == TraditionalMessage.CONFIRM_CONNECT) {
				// Start streaming, CDN is your only source\
				sourcePeerList[numSource] = aem.sender;
				int tobeAdded;
				/*if (cdnRTT >= 1000)
					tobeAdded = CommonState.r.nextInt(500) + 1000;
				else
					tobeAdded = CommonState.r.nextInt(1000);
				averageRTT = (averageRTT * numSource + tobeAdded)
						/ (numSource + 1);*/
				numSource++;
								
				int spdAvail = downloadSpd - usedDownloadSpd;
				if (spdAvail > aem.data) {
					spdAvail = aem.data;
				}
				if (!firstConnect && spdAvail > 0) {
					setTimeElapsed();
					firstConnect = true;
					// if(spdAvail <= 0) System.out.println("MALI");
					// System.out.println("elapsed: "+elapsedTime+"   |  Current: "+System.currentTimeMillis());
					// System.out.println("Confirm");
				}
				prot.sendMsg(new TraditionalMessage(TraditionalMessage.CONFIRM_ACCEPT, node, aem.sender,
						268, spdAvail, videoID), aem.delay);
				// paano na malalaman yung mga speed cheverloo?
				usedDownloadSpd += spdAvail;
				startedStreaming = true;
				
				numConnectionsAccepted++;
			} else if (aem.msgType == TraditionalMessage.SP_RP_DISCONNECT_CONFIRM) {
				// Remove from source peers, annyeong
				usedDownloadSpd -= aem.data;
			} else if (aem.msgType == TraditionalMessage.SP_RP_DISCONNECT_FULFILLED) {
				usedDownloadSpd -= aem.data;
				// System.out.println(aem.data);
			} else if (aem.msgType == TraditionalMessage.UPLOAD) {
				// System.out.println("Upload");
				if (!firstPlayback && streamedVideoSize >= 400) {
					firstPlay = System.currentTimeMillis() - startTime
							- getTimeElapsed();
					firstPlayback = true;
				}
				streamedVideoSize += aem.data;
				if (streamedVideoSize > videoSize) {
					for (int i = 0; i < numSource; i++) {
						if (!sourcePeerList[i].equals(connectedCDN)) {
							if (sourcePeerList[i] != null) {
								prot.sendMsg(new TraditionalMessage(TraditionalMessage.SP_RP_DISCONNECT, node, 
										sourcePeerList[i], 168), aem.delay);
							}
						} else {
							prot.sendMsg(new TraditionalMessage(TraditionalMessage.CDN_RP_DISCONNECT, node, connectedCDN, 168), cdnRTT);
						}
					}
					prot.sendMsg(new TraditionalMessage(TraditionalMessage.RP_DONE_STREAMING, node, connectedCDN, 168), cdnRTT);
					doneStreaming = true;
				}

			}

		}
	}

	@Override
	public void setNodeTag(int tag) {
		this.nodeTag = tag;
	}

	@Override
	public int getNodeTag() {
		return this.nodeTag;
	}

	@Override
	public void setConnectedCDN(int cdnID) {
		switch (cdnID) {
		case 0:
			connectedCDN = null; // the node itself is the CDN
			break;
		case 1:
			connectedCDN = TraditionalProtocol.CDN1; // connected to CDN1
			CID = 1;
			break;
		case 2:
			connectedCDN = TraditionalProtocol.CDN2; // connected to CDN2
			CID = 2;
			break;
		case 3:
			connectedCDN = TraditionalProtocol.CDN3; // connected to CDN3
			CID = 3;
			break;
		}
	}

	@Override
	public Node getConnectedCDN() {
		return this.connectedCDN;
	}

	@Override
	public Node getCDN(int cdnID) {
		switch (cdnID) {
		case 1:
			return CDN1;
		case 2:
			return CDN2;
		case 3:
			return CDN3;
		default:
			return null;
		}
	}

	@Override
	public void setCDNRTT(int rtt) {
		cdnRTT = rtt;
	}

	@Override
	public int getCDNRTT() {
		return this.cdnRTT;
	}

	@Override
	// applicable to a CDN serving a regular node's request
	public Node[] getSupplyingPeers(int video) {
		Node[] supplyingPeers = new Node[maxClients];
		int i = 0;
		Node node;
		TraditionalProtocol prot;
		for (int j = 0; j < supplyingPeerList.length; j++) {
			node = supplyingPeerList[j];
			if (node != null) {
				prot = (TraditionalProtocol) node.getProtocol(pid);
				if (prot.videoID == video) {
					supplyingPeers[i] = node;
					i++;
				}
			}
		}
		if (i == 0)
			supplyingPeers = null;
		// if(supplyingPeers == null)
		// System.out.println("No Supplier");
		return supplyingPeers;
	}

	@Override
	public Node[] getPeerList() {
		return this.peerList;
	}

	@Override
	public Node[] getClientList() {
		return this.clientList;
	}

	/**
	 * CDN = 100 Mbps SuperPeer/Regular = 125 kbps
	 */
	@Override
	public void setDownloadSpd(int bw) {
		this.downloadSpd = bw;
	}

	@Override
	public int getDownloadSpd() {
		return downloadSpd;
	}

	@Override
	public void setUsedDownloadSpd(int bw) {
		this.usedDownloadSpd = bw;
	}

	@Override
	public int getUsedDownloadSpd() {
		return usedDownloadSpd;
	}

	@Override
	public void setUploadSpd(int bw) {
		this.uploadSpd = bw;
	}

	@Override
	public int getUploadSpd() {
		return uploadSpd;
	}

	@Override
	public void setUsedUploadSpd(int bw) {
		this.usedUploadSpd = bw;
	}

	@Override
	public int getUsedUploadSpd() {
		return usedUploadSpd;
	}

	@Override
	public int[] getVideoList() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStartTime() {
		startTime = System.currentTimeMillis();
	}

	public void setTimeElapsed() {
		elapsedTime = System.currentTimeMillis() - startTime;
	}

	public long getTimeElapsed() {
		return elapsedTime;
	}

	// Set as supplying peer
	public void setAsSP() {
		setNodeTag(TraditionalProtocol.SupplyingPeerTag); // tag as CDN

		int maxClients = TraditionalProtocol.maxClients;

		/**
		 * initialize lists to be used by a supplying peer node
		 */
		clientList = new Node[maxClients];
		supplyingPeerList = new Node[maxClients];
	}

	// applicable to CDN and Supplying Peer
	public boolean addClient(Node n) {

		clientList[numClients] = n;
		numClients++;

		EDSimulator.add(0, new NextCycleEvent(null), n, pid);
		return true;
	}

	public void start(Node node) {
		TraditionalProtocol prot = (TraditionalProtocol) node.getProtocol(pid);

		// SEND GET_SP_LIST to CDN

		if (prot.connectedCDN == null)
			System.out.println("up  " + node.getIndex());
		if (sourcePeerList == null)
			sourcePeerList = new Node[maxClients];
		if (peerList == null)
			peerList = new Node[maxClients];
		if (peerSpdAlloted == null)
			peerSpdAlloted = new int[maxClients];
		
		prot.sendMsg(new TraditionalMessage(TraditionalMessage.GIVE_SP_LIST, node, connectedCDN, 200, videoID), cdnRTT);
	}
	
	public void sendMsg (TraditionalMessage tradMsg, int delay) {

		((TraditionalMessage) tradMsg).addDelay(delay);
		Node sender = tradMsg.sender;		
		Router router = (Router)sender.getProtocol(nid);
		router.insertMsg(tradMsg);	
	}

}
