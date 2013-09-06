package caviar;

//protocol

public class gcp2pProtocol implements Overlay, CDProtocol, EDProtocol{

public final string PAR_PROT = "protocol";

/**
*	Message TYPES
*/
private int HELLO = 0;			//	HELLO MSG
private int GOODBYE = 1;		//	LEAVING MSG
private int UPLOAD = 2;			//	DATA TRANSFER
private int CONNECT = 3;		//	REQUEST FOR CONNECTION
private int REQUEST_PEERS_FROM_THIS_BIN = 4;	
private int NOONE_STREAMS_IN_THIS_BIN = 5;
private int REQUEST_PEERS_FROM_OTHER_BINS = 6;
private int GET_SUPERPEER = 7;
private int YOUR_SUPERPEER = 8;
private int DO_YOU_HAVE_THIS = 9;	// do you have this video? applicable message for CDN
private int I_HAVE_IT = 10;
private int I_DONT_HAVE_IT = 11;
/*
*	GLOBALS
*/
int pid;
int nodeTag;			// Type of node: 0 - CDN, 1 - Superpeer, 2 - Regular
int closestCDN;			// ID of the closest CDN server
int landmark1RTT;		// RTT to landmark 1
int landmark2RTT;		// RTT to landmark 2
int landmark3RTT;		// RTT to landmark 3
int CDN1RTT;			// RTT to CDN 1
int CDN2RTT;			// RTT to CDN 2
int CDN3RTT;			// RTT to CDN 3
int uploadSpd;			// maximum upload capacity
int downloadSpd;		// maximum download capacity
int usedUploadSpd;		// used upload speed
int usedDownloadSpd;	// used download speed
int videoID;			// ID of the video it is streaming
int categoryID;			// category of the video it is streaming
int numClients;			// number of clients
int[] videoList;			// list of videos

Node[] superPeerList;	// list of SuperPeers
Node[] clientList;		// applicable to CDN and SuperPeer
Node[] peerList;		// list of peers the node uploads to
Node[] sourcePeerList;	// list of peers that contribute to the node
Node[] Bin0;			// list of peers in Bin0
Node[] Bin1;			// list of peers in Bin1
Node[] Bin2;			// list of peers in Bin2
Node[] Bin3;			// list of peers in Bin3
Node[] Bin4;			// list of peers in Bin4
Node[] Bin5;			// list of peers in Bin5
boolean streaming = false; // true if the node is already streaming

public gcp2pProtocol(string prefix){
	pid = Configuration.getPid(prefix + "." + PAR_PROT);
}


//cycle chuchu, ewan ko kung gagawin natin, feeling ko hindi
public void nextCycle( Node node, int pid ){


	
}
//eto yung magproprocess ng messages
public void processEvent( Node node, int pid, Object event ) {
	ArrivedMessage aem = (ArrivedMessage)event;
	/*
	*	CDN messages
	*/
	if (nodeTag == 0){
		if (aem.msgType == GET_SUPERPEER){
			((Transport)node.getProtocol(FastConfig.getTransport(pid))).
						send(
							node,
							aem.sender,
							new ArrivedMessage(YOUR_SUPERPEER, node, superPeerList[aem.data]),
							pid);
				
		}
		else if (aem.msgType == DO_YOU_HAVE_THIS){
			int i;
			int reply = I_DONT_HAVE_IT;
			for(i = 0; i<videoList.size(); i++){
				if(aem.data == videoList[i])
					reply = I_HAVE_IT;
			}
			
			((Transport)node.getProtocol(FastConfig.getTransport(pid))).
						send(
							node,
							aem.sender,
							new ArrivedMessage(reply, node, null),
							pid);
				
		}
		
	}
	else if (nodeTag == 1){


	}

	else
		



}

/**
*	COMPUTE THE BIN
*		binID values:
*		0 if L1>L2>L3
*		1 if L1>L3>L2
*		2 if L2>L1>L3
*		3 if L2>L3>L1
*		4 if L3>L1>L2
*		5 if L3>L2>L1
*/

public void computeBin(){
	if(landmark1RTT>=landmark2RTT && landmark2RTT >= landmark3RTT){
		setBinID(0);
	}
	else if (landmark1RTT>=landmark3RTT && landmark3RTT >= landmark2RTT){
		setBinID(1);
	}
	else if (landmark2RTT>=landmark1RTT && landmark1RTT >= landmark3RTT){
		setBinID(2);
	}
	else if (landmark2RTT>=landmark3RTT && landmark3RTT >= landmark1RTT){
		setBinID(3);
	}
	else if (landmark3RTT>=landmark1RTT && landmark1RTT >= landmark2RTT){
		setBinID(4);
	}
	else if (landmark3RTT>=landmark2RTT && landmark2RTT >= landmark1RTT){
		setBinID(5);
	}
}

/*
*	OVERLAY METHODS
*/

public void setSuperPeerSize(int size){
	superPeerList = new Node[size];
	for (i = 0; i < size; i++)
		Node[i] = null;
}
public void setSuperPeer (Node peer, int binID){
	superPeerList[binID] = peer;
}
public Node getSuperpeer (int binID){
	return superPeerList[binID];
}

public Node [] getPeerList (){
	Node[] toReturn = new Node[20];
	for ()
}

/**
 * Applicable to SuperPeers and CDN Servers
 */
public Node [] getClientList ();

/**
 * CDN = 100 Mbps
 * SuperPeer/Regular = 125 kbps
 */
public void setDownloadSpd (int bw){
	this.downloadSpd = bw;
}

public void setUploadSpd (int bw){
	this.uploadSpd = bw;
}
public void setUsedDownloadSpd (int bw){
	this.usedDownloadSpd = bw;
}

public void setUsedUploadSpd (int bw){
	this.usedUploadSpd = bw;
}

public int setDownloadSpd (){
	return downloadSpd;
}

public int setUploadSpd (){
	return uploadSpd;
}
public int setUsedDownloadSpd (){
	return usedDownloadSpd;
}

public int setUsedUploadSpd (){
	return usedUploadSpd;
}

/**
 * Set RTT
 */
public void setRTT (int RTT);

/**
 * Get RTT
 */
public int getRTT ();

/**
 * Set bin ID
 */
public void setBinID (int binID){
	this.binID = binID;
}

/**
 * Get bin ID
 */
public int getBinID (){
	return binID;
}

/**
 * Get video list
 */

//eto yung class natin for message
class ArrivedMessage {

	final int    msgType;
	final Node   sender;
	final int    chunkNo;
	final int    data;
	final Node[] nodeList;
	final Node	 superPeer;
	public ArrivedMessage(int typeOfMsg, Node sender, int chunkNo, int data)
	{
		this.typeOfMsg    = msgType;
		this.sender       = sender;
		this.chunkNo     = chunkNo;
		this.data = data;
	}
	public ArrivedMessage(int typeOfMsg, Node sender, Node[] nodeList)
	{
		this.typeOfMsg    = msgType;
		this.sender       = sender;
		this.nodeList = nodeList;
	}
	public ArrivedMessage(int typeOfMsg, Node sender, Node superPeer)
	{
		this.typeOfMsg    = msgType;
		this.sender       = sender;
		this.superPeer = superPeer;
	}
}