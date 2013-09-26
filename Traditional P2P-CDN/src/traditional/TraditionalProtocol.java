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
	int[] videoList;			// list of videos
	long startTime;			// Time the node was initialized
	long elapsedTime;		// Time it took from initialization until completion of stream
	int numCandidates;
	int candidateReplies = 0;
	int uploaded = 0;
	
	Node[] supplyingPeerList;	// list of SupplyingPeers
	Node[] clientList;		// applicable to CDN
	Node[] peerList;		// list of peers the node uploads to
	int[] peerSpdAlloted;	// speed alloted to peers
	int numPeers;			// number of peers it contributes to
	Node[] sourcePeerList;	// list of peers that contribute to the node
	Node[] candidatePeers;	// sent by the SuperPeer to a regular peer
	int numSource;			// number of source peers that contribute to the node
	boolean startedStreaming = false; // true if the node is already streaming
	boolean doneStreaming = false;	// true if videoSize<= streamedVideoSize
	
		
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
	public void nextCycle(Node arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processEvent(Node arg0, int arg1, Object arg2) {
		// TODO Auto-generated method stub
		
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
	
	@Override
	public Node getSupplyingPeer(int connectedCDN) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node[] getPeerList() {
		// TODO Auto-generated method stub
		return null;
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

	public boolean addClient(Node n) {
		// TODO Auto-generated method stub
		//QUESTON: Should check first if client is alive? Else return false.
		//Check if SuperPeer, initialize, relevant lists
		//do we still need clientRTT?
		/*if(clientList == null && ){

		}
		 */		
		
		clientList[numClients] = n;
		TraditionalProtocol prot = (TraditionalProtocol) n.getProtocol(pid);
		numClients++;
		
	    EDSimulator.add(0, new NextCycleEvent(null), n, pid);
		return true;		
	}
}
