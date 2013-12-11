package orange;

/**
 * 
 *
 * @author Fatima De Villa, Janeri Ongcol, Bryan Tan
 * @version 1.0
 */

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

public class OrangeNetworkInitializer implements Control {
	
	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------
	private static final int maxLandmarkRTT = 70;
	private static final int minLandmarkRTT = 30;
	private static final int maxCDNRTT = 15;
	private static final int minCDNRTT = 0;
	private static final int maxBins = OrangeProtocol.maxBins;
	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------
	private static final String PAR_PROT = "protocol";
	private static final String PAR_TRANS = "transport";
	private final String PAR_CATEGORY = "category";
	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
	/** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
	private static int pid;
	/** Transport Protocol identifier, obtained from config property {@link #PAR_TRANS */	
	private static int tid;
	private static int category;
	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	public OrangeNetworkInitializer(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		tid = Configuration.getPid(prefix + "." + PAR_TRANS);
		category = Configuration.getInt(prefix + "." + PAR_CATEGORY);
	}
	
		
	@Override
	public boolean execute() {
		/**
		 * Set the first 3 nodes as the CDNs. 
		 * Initialize values contained by the nodes.
		 */
		Node n;
		OrangeProtocol prot, prot2;

		for (int i = 0; i < Network.size() ; i++) {
			n = Network.get(i);	//current node
			if(i < 3) {	//CDN node
				setAsCDN(i + 1, n); // Note: CID range [1, 3]
			}
			else{	//regular node
				initialize(n);	
			}
		}//endfor
		
		
		/**
		 * binning for the regular nodes (Note: binning is specific to the cdn
		 * groups (based on the landmark RTTs) and the cdn of the node is set by Gcp2pNodeInit)
		 */
		int binID;
		
		for (int i = 3; i < Network.size(); i++){
			n = Network.get(i);
			prot = (OrangeProtocol) n.getProtocol(pid);
			prot.computeBin();			//assign bin based on its landmark RTTS in its CDN area
			binID = prot.getbinID();	//get its binID
			
			Node cdn = prot.getConnectedCDN();			//get the CDN node its connected to
			prot2 = (OrangeProtocol) cdn.getProtocol(pid);
			
			prot2.addToBin(binID, n);				//add the node to the corresponding bin in its CDN
			
		}
		
		
		/**
		 * For each bin in each CDN group, choose a SuperPeer (the one with the lowest RTT in the bin).
		 * After which, initialize the connections among the Regular peers in each bin of each CDN group.
		 */
				
		prot = (OrangeProtocol) OrangeProtocol.CDN1.getProtocol(pid);
		//setInitSuperPeers(prot);
		//setInitRegularConnection(prot);
		
		prot = (OrangeProtocol) OrangeProtocol.CDN2.getProtocol(pid);
		//setInitSuperPeers(prot);
		//setInitRegularConnection(prot);
		
		prot = (OrangeProtocol) OrangeProtocol.CDN3.getProtocol(pid);
		//setInitSuperPeers(prot);
		//setInitRegularConnection(prot);

		
		return false; 
	}
	
	

	/**
	 * Set node n as one of the 3 CDNs
	 * @param cdnID - it cdnID 1, 2 or 3
	 * @param n - the node to act as CDN
	 */
	public void setAsCDN(int cdnID, Node n) {
		
		//other CDN properties?????	// TODO Auto-generated method stub
		
		/**
		 * Assign as either CDN1, CDN2 and CDN3 depending on its cdnID
		 */
		switch(cdnID)
		{
				case 1: OrangeProtocol.CDN1 = n;
						break;
				case 2: OrangeProtocol.CDN2 = n;
						break;
				case 3: OrangeProtocol.CDN3 = n;
						break;
		}
		
		OrangeProtocol prot = (OrangeProtocol) n.getProtocol(pid);
		prot.setNodeTag(OrangeProtocol.CDNTag);	//tag as CDN
		
		int maxClients = OrangeProtocol.maxClients;
				
		prot.setConnectedCDN(0);	//0 because it is the CDN istelf
		prot.setCDNRTT(0);	//distance to itself is 0
		
		/**
		 * initialize lists to be used by a CDN node
		 */
		prot.clientList = new Node[maxClients];
		prot.clientRTT = new int[maxClients];
		prot.binList = new Node[maxBins][maxClients];
		prot.startedStreaming = true;
		prot.binSize = new int[maxBins];
		prot.peerRTT = new int[maxClients];
		for(int i =0;i<6;i++)
			prot.binSize[i] = 0;
		prot.binWatchList = new int[maxBins][maxClients];
		prot.bestRTT = new int[maxBins];
		prot.peerList = new Node[maxClients];
		prot.uploadSpd = 10000;
		prot.peerSpdAlloted = new int[maxClients];
		prot.videoSpdAlloted = new int[category*20];
		for(int i = 0; i < prot.videoSpdAlloted.length ; i++)
			prot.videoSpdAlloted[i] = 0;
		prot.superPeerList = new Node[maxClients]; //tama ba?
		prot.binIndexPerCategory = new int[6][category][maxClients];
		for(int i = 0; i < 6; i++)
			for(int j = 0; j < category; j++)
				for(int k = 0; k < maxClients; k++)
					prot.binIndexPerCategory[i][j][k] = -1;
	}
	
	
	/**
	 * Set the initial SuperPeers (the one closest to the CDN) for each bin inside the CDN's group/area
	 * @param prot	- refers to the CDN node
	 * @return
	 */
	public void setInitSuperPeers(OrangeProtocol prot)
	{
		/**
		 * Set bestRTT in each bin to 101 so that when
		 * a lower (better) value comes it will be replaced.
		 */
		for (int i = 0; i < maxBins; i++)
		{
			prot.bestRTT[i] = 101;	//set to 101 first 
		}
		
		int binsize, bestRTT;
		OrangeProtocol prot2;
		
		for(int binID = 0; binID < maxBins; binID ++)	//iterate through each bin
		{
			binsize = prot.binSize[binID];
			bestRTT = prot.bestRTT[binID];
			Node tempSuperpeer = null;
			Node n;
			Node best = prot.connectedCDN;
			//Iterate through all the nodes in the bin and choose the one with the best RTT
			for(int j = 0; j < binsize; j++)
			{
				n = prot.binList[binID][j];
				prot2 = (OrangeProtocol) n.getProtocol(pid);
				
				//if better than the current bestRTT, set as the tempSuperpeer
				if(prot2.getCDNRTT() < bestRTT)
				{
					bestRTT = prot2.cdnRTT;
					tempSuperpeer = n;
					best = n;
				}
				
			}//endinnerfor
			
			//Set as superpeer for that bin and update the cdn's bestRTT list
			prot.bestRTT[binID] = bestRTT;
			prot.setSuperPeer(tempSuperpeer, binID);
			
			//Tag the node as SuperPeer
			prot2 = (OrangeProtocol) tempSuperpeer.getProtocol(pid);
			prot2.setNodeTag(OrangeProtocol.SuperPeerTag);
			//prot2.clientList = prot.binList[binID];
			prot2.setClientList(prot.binList[binID]);
			//System.arraycopy(prot.binList[binID], 0, prot2.clientList, 0, prot.binList[binID].length);
			//prot2.indexPerCategory = prot.binIndexPerCategory[binID];
			prot2.setIndexPerCategory(prot.binIndexPerCategory[binID]);
			//prot2.clientWatching = prot.binWatchList[binID];
			prot2.setClientWatching(prot.binWatchList[binID]);
			prot2.numClients = prot.binSize[binID];
			System.out.println("SuperPeer Index:" + best.getIndex()+ ": indexPerCategory[0][0] = "+prot2.indexPerCategory[0][0]+":SuperPeer numClients = "+prot2.numClients);
			
		}//endfor
	}
	
	/**
	 * Set the initial connections among the regular nodes of each bin in each
	 * of the CDN servers.
	 * @param prot
	 */
	
	public void setInitRegularConnection (OrangeProtocol prot) {
		// Iterate through all the nodes in a bin
		for (int binID=0; binID<maxBins; binID++) {
			for (int i=0; i<prot.binSize[binID]; i++) {
				Node n = prot.binList[binID][i];
				OrangeProtocol prot2 = (OrangeProtocol) n.getProtocol(pid);
				
				// Initialize the global variables of the node
				prot2.indexPerCategory = null;
				prot2.superPeerList = null;
				prot2.clientList = null;
				prot2.peerList = null;
				prot2.peerSpdAlloted = null;		
				prot2.numPeers = 0;
				prot2.sourcePeerList = null;
				prot2.candidatePeers = null;
				prot2.numSource = 0;
				prot2.binSize = null;
				prot2.binList = null; 
				prot2.binWatchList = null;
				prot2.binIndexPerCategory = null;
				prot2.startedStreaming = true; // true if the node is already streaming
				prot2.doneStreaming = false;	// true if videoSize<= streamedVideoSize
				prot2.otherSP = null;
				prot2.numSource = 0;
				// Set the connection
				//prot2.start(n);
				// TODO size?
				EDSimulator.add(10, new OrangeMessage(OrangeMessage.GET_SUPERPEER, n, prot2.connectedCDN, 0, prot2.binID), prot2.connectedCDN, pid);
			}
		}
	}
	
	/**
	 * Initialize a regular node in the G-CP2P Network. Randomly choose among the three
	 * CDN servers as its closest CDN. Randomly generate its RTT values for each of the three
	 * landmarks (30-70 ms). Set the node's upload and download speed by randomly choosing
	 * from values between 0-1000 Kbps and 1000-2000 Kbps, respectively. Set the used upload
	 * and download speed to 0 since the node has not yet started streaming. Get a random video
	 * the node wants to stream from a list of 20 per category then put it in a category.  
	 * @param n - the node to be initialized
	 */
	
	public void initialize (Node n) {
		// This never happens since the Network starts with CDNs as initial nodes
		if (Network.size() == 0) { return; }
		
		OrangeProtocol prot = (OrangeProtocol) n.getProtocol(pid);
		
		// Set the start time
		
		
		// Set the node as Regular 
		prot.setNodeTag(OrangeProtocol.RegularTag); 
		
		// Randomly set the nodes connected CDN (CDN1, CDN2, or CDN3) cdnID range [1, 3]
		prot.setConnectedCDN(CommonState.r.nextInt(3) + 1);  
		
		// Set the node's RTT to each of the three predefined landmarks
		for (int i=1; i<=3; i++) { prot.setLandmarkRTT(i, maxLandmarkRTT, minLandmarkRTT ); }
		
		// Set the node's RTT to its CDN
		prot.setCDNRTT(CommonState.r.nextInt((maxCDNRTT-minCDNRTT)) + minCDNRTT);
		
		// Get the CDN the node is connected to and add it as its client
		Node cdn;
		OrangeProtocol prot2;
		cdn = prot.getConnectedCDN();
		prot2 = (OrangeProtocol) cdn.getProtocol(pid);
		prot2.addClient(n);	
		
		// Set the node's speed
		prot.setUploadSpd(CommonState.r.nextInt(1001));
		prot.setDownloadSpd(CommonState.r.nextInt(1001) + 1000);
		prot.setUsedUploadSpd(0);
		prot.setUsedDownloadSpd(0);
		
		// Set the video the node wants to stream and the category it is under
		prot.videoID = CommonState.r.nextInt(category*20);
		prot.categoryID = prot.videoID/20;
	}

}
