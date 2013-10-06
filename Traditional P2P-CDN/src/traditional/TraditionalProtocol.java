package traditional;

import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Control;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.edsim.*;
import peersim.cdsim.*;
import peersim.transport.*;
import peersim.core.Node;

public class TraditionalProtocol implements EDProtocol, CDProtocol, TraditionalOverlay{

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
	 * String name of the parameter, assigned to maxClients
	 */
	private static final String PAR_MAXCLIENTS = "maxclients";
	
	// ------------------------------------------------------------------------
	// Static Fields
	// ------------------------------------------------------------------------
		
	/**
	 *  Protocol identifier, obtained from config property {@link #PAR_PROT}. 
	 **/
	private static int pid;	
	
	/**
	 *  Transport Protocol identifier, obtained from config property {@link #PAR_TRANS}. 
	 **/
	private static int tid;	
		
	
	/**
	 * max number of possible clients for CDNs and SuperPeers, 
	 * obtained from config property {@link #PAR_MAXCLIENTS}. 
	 ***/
	public static int maxClients; 		
	
	/***
	 * The nodes corresponding to the 3 CDNs in the set-up
	 */
	public static Node CDN1;
	public static Node CDN2;
	public static Node CDN3;	
	
	/**
	*Attributes
	*/
	int nodeTag;			// Type of node: 0 - CDN, 1 - Superpeer, 2 - Regular
	Node connectedCDN;		// the CDN node it is closest/connected to
	int CID;				// Which CDN range [1, 3]
	int cdnRTT;				// RTT of a client to its CDN; 
	int uploadSpd;			// maximum upload capacity
	int downloadSpd;		// maximum download capacity
	int usedUploadSpd;		// used upload speed
	int uploadSpdBuffer;	// reserved upload spd for peers requesting connection, to be alloted when the peer accepts the upload spd
	int usedDownloadSpd;	// used download speed
	int videoID;			// ID of the video it is streaming
	int videoSize;			// size of the video it is watching
	int streamedVideoSize;	// size already streamed
	int numClients;			// number of clients
	int videoSpdAlloted[];
	int[] videoList;			// list of videos
	long startTime;			// Time the node was initialized
	long elapsedTime;		// Time it took from initialization until completion of stream
	int numCandidates;
	int candidateReplies = 0;
	int uploaded = 0;
	int cdnID;
	double averageRTT;
	int maxVideoSpd = 0;
	boolean firstConnect = false;
	boolean firstPlayback = false;
	long firstPlay;
	
	Node[] supplyingPeerList;	// list of supplying peer under the CDN
	int numSupplier = 0;
	Node[] clientList;		// applicable to CDN (both regular and supplying are here)
	Node[] peerList;		// list of peers the node uploads to, applicable to supplying peer and CDN
	int[] peerSpdAlloted;	// speed alloted to peers
	int numPeers;			// number of peers it contributes to
	Node[] sourcePeerList;	// list of peers that contribute to a regular node
	Node[] candidateSupplyingPeers;	// sent by the CDN to a regular peer
	int numSource;			// number of source peers that contribute to the node
	boolean startedStreaming = false; // true if the node is already streaming
	boolean doneStreaming = false;	// true if videoSize<= streamedVideoSize
	int contractSize= 0;
	boolean contractExpired = false;
		
	public TraditionalProtocol(String prefix){
		maxClients = Configuration.getInt(prefix + "." + PAR_MAXCLIENTS);
		tid = Configuration.getPid(prefix + "." + PAR_TRANS);
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}
	
	public Object clone(){
		TraditionalProtocol prot = null;
		try{
			prot = (TraditionalProtocol) super.clone();
		}catch( CloneNotSupportedException e ) {} // never happens
		return prot;
	}

	@Override
	public void nextCycle(Node node, int pid) {
		// TODO Auto-generated method stub
		System.out.println("Nagnext Cycle");
		if(startedStreaming == true && !contractExpired){
			if(nodeTag == SupplyingPeerTag || nodeTag == CDNTag){
				for (int i = 0; i < numPeers; i++)
					if(peerList[i] != null){
						((Transport)node.getProtocol(tid)).
						send(
							node,
							peerList[i],
							new TraditionalArrivedMessage(TraditionalArrivedMessage.UPLOAD, node, peerSpdAlloted[i]),
							pid);
						uploaded += peerSpdAlloted[i];
						if(nodeTag == SupplyingPeerTag && uploaded > contractSize){
							((Transport)node.getProtocol(tid)).
							send(
								node,
								connectedCDN,
								new TraditionalArrivedMessage(TraditionalArrivedMessage.CONTRACT_EXPIRED, node),
								pid);
							for(int j = 0; j < numPeers; i++){
								((Transport)node.getProtocol(tid)).
								send(
									node,
									peerList[j],
									new TraditionalArrivedMessage(TraditionalArrivedMessage.SP_RP_DISCONNECT, node, peerSpdAlloted[j]),
									pid);
							}
							contractExpired = true;
							break;
						}
					}
			}
			if(nodeTag == RegularTag){
				if(usedDownloadSpd < downloadSpd){
					((Transport)node.getProtocol(tid)).
					send(
						node,
						connectedCDN,
						new TraditionalArrivedMessage(TraditionalArrivedMessage.GIVE_SP_LIST, node, videoID),
						pid);
					
				}
			}
		}
	}

	@Override
	public void processEvent(Node node, int pid, Object event) {
		// TODO Auto-generated method stub
		TraditionalArrivedMessage aem = (TraditionalArrivedMessage)event;
		
		if(nodeTag == CDNTag)//messages received by the CDN
		{
			if (aem.msgType == TraditionalArrivedMessage.GIVE_SP_LIST)
			{
				//In the message: the videoID of the Requesting/Regular Peer
				//send back results of getSupplyingPeers(int video) - a list of supplying peers with that video
				((Transport)node.getProtocol(tid)).
				send(
					node,
					aem.sender,
					new TraditionalArrivedMessage(TraditionalArrivedMessage.RECEIVE_SP_LIST, node, getSupplyingPeers(aem.data)),
					pid);
				System.out.println("Received: GIVE_SP_LIST");
			}
			else if(aem.msgType == TraditionalArrivedMessage.CDN_RP_CONNECT)
			{
				//A Requesting Peer wants to connect directly to a CDN (i.e. no supplying peers for the video the node needs)
				//Reply with a confirm connect connection message
				int spdAvail = maxVideoSpd - videoSpdAlloted[aem.data];
				if(spdAvail <= 0)
					((Transport)node.getProtocol(tid)).
					send(
						node,
						aem.sender,
						new TraditionalArrivedMessage(TraditionalArrivedMessage.REJECT, node),
						pid);
				else {
					if(aem.data2 < spdAvail)
						spdAvail = aem.data2;
					((Transport)node.getProtocol(tid)).
					send(
						node,
						aem.sender,
						new TraditionalArrivedMessage(TraditionalArrivedMessage.CDN_RP_CONNECT_CONFIRM, node, spdAvail),
						pid);
					
					
				}
			}
			else if(aem.msgType == TraditionalArrivedMessage.CDN_RP_DISCONNECT)
			{	
				//Reply with a confirm disconnect message
				TraditionalProtocol prot = (TraditionalProtocol) aem.sender.getProtocol(pid);
				int allotedSpd = 0;
				int i;
				for(i = 0; i < numPeers; i++){
					if(peerList[i].equals(aem.sender)){
						allotedSpd = peerSpdAlloted[i];
						break;
					}
				}
				peerList[i] = null;
				peerSpdAlloted[i] = 0;
				videoSpdAlloted[prot.videoID] -= allotedSpd;
			}
			else if (aem.msgType == TraditionalArrivedMessage.CDN_RP_CONNECT_ACCEPT){
				uploadSpdBuffer -= aem.data;
				usedUploadSpd -= uploadSpdBuffer;
				videoSpdAlloted[aem.data2] += aem.data;
				peerList[numPeers] = aem.sender;
				peerSpdAlloted[numPeers] = aem.data;
				numPeers++;
			}
			else if(aem.msgType == TraditionalArrivedMessage.SP_RP_CONNECT)
			{	
				//Reply with a confirm connect message
				((Transport)node.getProtocol(tid)).
				send(
					node,
					aem.sender,
					new TraditionalArrivedMessage(TraditionalArrivedMessage.CONFIRM_CONNECT, node, aem.node),
					pid);
			}
			else if (aem.msgType == TraditionalArrivedMessage.SP_RP_DISCONNECT){
				((Transport)node.getProtocol(tid)).
				send(
					node,
					aem.sender,
					new TraditionalArrivedMessage(TraditionalArrivedMessage.SP_RP_DISCONNECT_CONFIRM, node, aem.node),
					pid);
			}
			else if(aem.msgType == TraditionalArrivedMessage.CONTRACT_SET)
			{	
				//Reply with a message saying how long the node stays if it becomes a Supplying Peer
			}
			else if(aem.msgType == TraditionalArrivedMessage.CONTRACT_EXPIRED)
			{	
				//Reply with a confirm disconnect message, supplying peer has finished serving
				for(int i = 0; i < numSupplier; i++){
					if(aem.sender.equals(supplyingPeerList[i])){
						supplyingPeerList[i] = null;
						break;
					}
					
				}
			}
			else if (aem.msgType == TraditionalArrivedMessage.RP_DONE_STREAMING){
				supplyingPeerList[numSupplier] = aem.sender;
				numSupplier++;
				((Transport)node.getProtocol(tid)).
				send(
					node,
					connectedCDN,
					new TraditionalArrivedMessage(TraditionalArrivedMessage.CONTRACT_SET, node, 4),
					pid);
			}
		}
		else if(nodeTag == SupplyingPeerTag)//messages received by the SP
		{
			if(aem.msgType == TraditionalArrivedMessage.CONFIRM_CONNECT)
			{	
				//CDN has approved the connection 
				//Send a CONFIRM_CONNECT to RP to start connection
				uploadSpdBuffer += uploadSpd - usedUploadSpd;
				((Transport)node.getProtocol(tid)).
				send(
					node,
					aem.node,
					new TraditionalArrivedMessage(TraditionalArrivedMessage.CONFIRM_CONNECT, node, uploadSpdBuffer),
					pid);
				
			}
			else if(aem.msgType == TraditionalArrivedMessage.SP_RP_CONNECT)
			{	
				//Send a SP_RP_CONNECT message to CDN to get its approval
				if(uploadSpd -usedUploadSpd-uploadSpdBuffer > 0)
					((Transport)node.getProtocol(tid)).
					send(
						node,
						connectedCDN,
						new TraditionalArrivedMessage(TraditionalArrivedMessage.SP_RP_CONNECT, node, aem.sender),
						pid);
				else 
					((Transport)node.getProtocol(tid)).
					send(
						node,
						connectedCDN,
						new TraditionalArrivedMessage(TraditionalArrivedMessage.REJECT, node),
						pid);
			}
			else if(aem.msgType == TraditionalArrivedMessage.SP_RP_DISCONNECT)
			{	
				//Reply SP_RP_DISCONNECT message to CDN to get its approval
				((Transport)node.getProtocol(tid)).
				send(
					node,
					connectedCDN,
					new TraditionalArrivedMessage(TraditionalArrivedMessage.SP_RP_DISCONNECT, node, aem.sender),
					pid);
			}
			else if (aem.msgType == TraditionalArrivedMessage.SP_RP_DISCONNECT_CONFIRM){
				((Transport)node.getProtocol(tid)).
				send(
					node,
					aem.node,
					new TraditionalArrivedMessage(TraditionalArrivedMessage.SP_RP_DISCONNECT_CONFIRM, node),
					pid);
				
			}
			else if (aem.msgType == TraditionalArrivedMessage.CONFIRM_ACCEPT){
				uploadSpdBuffer -= aem.data;
				usedUploadSpd -= uploadSpdBuffer;
				peerList[numPeers] = aem.sender;
				peerSpdAlloted[numPeers] = aem.data;
				numPeers++;
				
			}
		}
		else if(nodeTag ==RegularTag)//messages received by Regular Node
		{
			if(aem.msgType == TraditionalArrivedMessage.RECEIVE_SP_LIST)
			{	
				//The Supplying Peers with the video
				//if not null, request connection with these Supplying Peers
				//else request connection with CDN itself (CDN_RP_CONNECT)
				if(aem.nodeList == null){
					((Transport)node.getProtocol(tid)).
					send(
						node,
						connectedCDN,
						new TraditionalArrivedMessage(TraditionalArrivedMessage.CDN_RP_CONNECT, node, videoID, downloadSpd - usedDownloadSpd),
						pid);
					
				}
				else {
					int sent = 0;
					for(int i = 0; i < aem.nodeList.length; i++){
						if(aem.nodeList[i] != null){
							((Transport)node.getProtocol(tid)).
							send(
								node,
								aem.nodeList[i],
								new TraditionalArrivedMessage(TraditionalArrivedMessage.SP_RP_CONNECT, node),
								pid);
							sent++;
						}
					}
					if (sent == 0){
						((Transport)node.getProtocol(tid)).
						send(
							node,
							connectedCDN,
							new TraditionalArrivedMessage(TraditionalArrivedMessage.CDN_RP_CONNECT, node, videoID, downloadSpd - usedDownloadSpd),
							pid);
					}
				}
				System.out.println("Received: RECEIVE_SP_LIST");
			}
			else if (aem.msgType == TraditionalArrivedMessage.CDN_RP_CONNECT_CONFIRM){
				sourcePeerList[numSource] = aem.sender;
				averageRTT = (averageRTT*numSource + cdnRTT)/(numSource+1);
				numSource++;
				int spdAvail = downloadSpd - usedDownloadSpd;
				if (spdAvail < aem.data){
					spdAvail = aem.data;
				}
				if(!firstConnect) {
					setTimeElapsed();
					firstConnect = true;
				}
				if(!firstPlayback && streamedVideoSize >= 400) {
					firstPlay = System.currentTimeMillis() - startTime;
					firstPlayback = true;
				}
				((Transport)node.getProtocol(tid)).
				send(
					node,
					aem.node,
					new TraditionalArrivedMessage(TraditionalArrivedMessage.CDN_RP_CONNECT_ACCEPT, node, spdAvail, videoID),
					pid);
			}
			else if (aem.msgType == TraditionalArrivedMessage.CONTRACT_SET){
				contractSize = videoSize*aem.data;
				nodeTag = SupplyingPeerTag;
			}
			else if(aem.msgType == TraditionalArrivedMessage.CONFIRM_CONNECT)
			{	
				//Start streaming, CDN is your only source\
				sourcePeerList[numSource] = aem.sender;
				averageRTT = (averageRTT*numSource + cdnRTT)/(numSource+1);
				numSource++;
				int spdAvail = downloadSpd - usedDownloadSpd;
				if (spdAvail < aem.data){
					spdAvail = aem.data;
				}
				((Transport)node.getProtocol(tid)).
				send(
					node,
					aem.node,
					new TraditionalArrivedMessage(TraditionalArrivedMessage.CONFIRM_ACCEPT, node, spdAvail, videoID),
					pid);
				//paano na malalaman yung mga speed cheverloo?
				startedStreaming = true;
			}
			else if(aem.msgType == TraditionalArrivedMessage.SP_RP_DISCONNECT)
			{	
				//Remove from source peers, annyeong
			}
			else if (aem.msgType == TraditionalArrivedMessage.UPLOAD){
				uploaded += aem.data;
				if (uploaded > videoSize){
					for(int i = 0; i < numSource; i++){
						if(!sourcePeerList[i].equals(connectedCDN))
							((Transport)node.getProtocol(tid)).
							send(
								node,
								sourcePeerList[i],
								new TraditionalArrivedMessage(TraditionalArrivedMessage.SP_RP_DISCONNECT, node),
								pid);
						else
							((Transport)node.getProtocol(tid)).
							send(
								node,
								connectedCDN,
								new TraditionalArrivedMessage(TraditionalArrivedMessage.CDN_RP_DISCONNECT, node),
								pid);
					}
					((Transport)node.getProtocol(tid)).
					send(
						node,
						connectedCDN,
						new TraditionalArrivedMessage(TraditionalArrivedMessage.RP_DONE_STREAMING, node),
						pid);
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
	public void setConnectedCDN(int cdnID)
	{
		switch(cdnID)
		{
			case 0: connectedCDN = null; //the node itself is the CDN
					break;
			case 1: connectedCDN = TraditionalProtocol.CDN1;		//connected to CDN1
					CID = 1;
					break;
			case 2: connectedCDN = TraditionalProtocol.CDN2;		//connected to CDN2
					CID = 2;
					break;
			case 3: connectedCDN = TraditionalProtocol.CDN3;		//connected to CDN3
					CID = 3;
					break;
		}
	}
	
	@Override
	public Node getConnectedCDN()
	{
		return this.connectedCDN;
	}
	
	@Override
	public Node getCDN(int cdnID)
	{
		switch(cdnID)
		{
			case 1: return CDN1;
			case 2: return CDN2;
			case 3: return CDN3;
			default: return null;
		}
	}
	
	@Override
	public void setCDNRTT(int rtt) {
		cdnRTT = rtt;
	}
	
	@Override
	public int getCDNRTT()
	{
		return this.cdnRTT;
	}
	
	@Override  //applicable to a CDN serving a regular node's request
	public Node [] getSupplyingPeers(int video) {
		Node []  supplyingPeers = new Node[maxClients];
		int i = 0;
		Node node;
		TraditionalProtocol prot;
		for(int j = 0; j < supplyingPeerList.length; j++)
		{
			node = supplyingPeerList[j];
			if(node != null){
				prot = (TraditionalProtocol) node.getProtocol(pid);			
				if (prot.videoID == video)	
				{
					supplyingPeers[i] = node;
					i++;
				}
			}
		}
		if (i == 0)
			supplyingPeers = null;
		if(supplyingPeers == null)
			System.out.println("No Supplier");
		return supplyingPeers;
	}

	@Override
	public Node[] getPeerList() {
		return this.peerList;
	}

	@Override
	public Node [] getClientList ()
	{
		return this.clientList;
	}

	/**
	 * CDN = 100 Mbps
	 * SuperPeer/Regular = 125 kbps
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

	public void setStartTime () {
		startTime = System.currentTimeMillis();
	}
	
	public void setTimeElapsed () {
		elapsedTime = System.currentTimeMillis() - startTime;
	}
	
	public long getTimeElapsed () {
		return elapsedTime;
	}
	
	//Set as supplying peer
	public void setAsSP()
	{
		setNodeTag(TraditionalProtocol.SupplyingPeerTag);	//tag as CDN
		
		int maxClients = TraditionalProtocol.maxClients;
	
		/**
		 * initialize lists to be used by a supplying peer node
		 */
		clientList = new Node[maxClients];
		supplyingPeerList = new Node[maxClients]; 
	}

	//applicable to CDN and Supplying Peer		
	public boolean addClient(Node n) {
		
		clientList[numClients] = n;
		numClients++;
		
	    EDSimulator.add(0, new NextCycleEvent(null), n, pid);
		return true;	
	}
	
	public void start (Node node)
	{
		TraditionalProtocol prot = (TraditionalProtocol) node.getProtocol(pid);
		
		//SEND GET_SP_LIST to CDN
		
		if(prot.connectedCDN == null)
			System.out.println("up  " + node.getIndex());
		if(sourcePeerList == null)
			sourcePeerList = new Node[prot.maxClients];
		if(peerList == null)
			peerList = new Node[prot.maxClients];
		if( peerSpdAlloted == null)
			peerSpdAlloted = new int [prot.maxClients];
		((Transport)node.getProtocol(tid)).
		send(
			node,
			connectedCDN,
			new TraditionalArrivedMessage(TraditionalArrivedMessage.GIVE_SP_LIST, node, videoID),
			pid);
	}

	
	public Node getSupplyingPeer(int connectedCDN) {
		// TODO Auto-generated method stub
		return null;
	}
}
