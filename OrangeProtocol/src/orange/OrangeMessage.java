package orange;

import java.util.LinkedList;

import peersim.core.Node;

public class OrangeMessage extends SimpleMessage{
	/**
	*	Message TYPES
	*/
	static final int HELLO = 0;			//	HELLO MSG
	static final int GOODBYE = 1;		//	LEAVING MSG
	static final int UPLOAD = 2;			//	DATA TRANSFER
	static final int CONNECT = 3;		//	REQUEST FOR CONNECTION
	static final int REQUEST_PEERS_FROM_THIS_BIN = 4;	
	static final int NOONE_STREAMS_IN_THIS_BIN = 5;
	static final int REQUEST_PEERS_FROM_OTHER_BINS = 6;
	static final int GET_SUPERPEER = 7;
	static final int YOUR_SUPERPEER = 8;
	static final int DO_YOU_HAVE_THIS = 9;	// do you have this video? applicable message for CDN
	static final int I_HAVE_IT = 10;			// reply to DO_YOU_HAVE_THIS
	static final int I_DONT_HAVE_IT = 11;	// reply to DO_YOU_HAVE_THIS
	static final int REQUEST_MY_CLIENTS = 12;// new super peer asks for his clients
	static final int YOUR_CLIENTS = 13;		// reply to REQUEST_MY_CLIENTS
	static final int YOUR_PEERS = 14;
	static final int GOODBYE_LEECHER = 15;	// sent by a leaving node to its peer leechers
	static final int UPLOAD_SPEED_THAT_CAN_BE_GIVEN = 16;
	static final int ACCEPT_SPEED = 17;
	static final int REJECT_SPEED = 18;		// sent by the requesting peer when its capacity is maxed
	static final int FELLOW_SP_REQUEST_FOR_PEERS = 19;
	static final int FIRED = 20;	//sent by a CDN to a SP when a new peer is nearer
	static final int YOU_ARE_SUPERPEER = 21 ;
	static final int GET_MY_CLIENTS = 22;
	static final int REJECT = 23;
	static final int UPDATE_SP = 24;		// To be sent to other SPs when a new peer is upgraded to SP
	static final int GOODBYE_AS_SP = 25;
	static final int GOODBYE_AS_SOURCE = 26;
	static final int GOODBYE_AS_NORMAL = 27;
	static final int GET_SUPERPEER_2 = 28;
	static final int ANOTHER_BIN_PEER_REQUEST = 29;
	static final int INCOMPLETE_PEER_LIST = 30;
	static final int COMPLETE_PEER_LIST = 31;
	static final int OTHER_BIN_NUMBER_OF_PEERS = 32;
	
	public int    data0;
	public int    data;
	public Node[] nodeList;
	public Node	 superPeer;
	public int[] peerWatching;
	public int [][] index;
	public Node node1;
	public Node node2;
	public LinkedList<Node> linkedNodeList;
	
	// DO_YOU_HAVE_THIS, REQUEST_PEERS_FROM_THIS_BIN, REQUEST_PEERS_FROM_OTHER_BINS, ACCEPT_SPEED
	public OrangeMessage(int typeOfMsg, Node sender, Node receiver, int size, int data0, int data) 
	{
		super(typeOfMsg, sender, receiver, size);
		this.data0     = data0;
		this.data = data;
	}
	
	//UPDATE_SP
	public OrangeMessage(int typeOfMsg, Node sender, Node receiver, int size, Node node1, Node node2) 
	{
		super(typeOfMsg, sender, receiver, size);
		this.node1     = node1;
		this.node2 = node2;
	}
	
	public OrangeMessage(int typeOfMsg, Node sender, Node receiver, int size,Node[] nodeList)
	{
		super(typeOfMsg, sender, receiver, size);
		this.nodeList = nodeList;
	}
	
	// YOUR_PEERS
	public OrangeMessage(int typeOfMsg, Node sender, Node receiver, int size, Node[] nodeList, int data)
	{
		super(typeOfMsg, sender, receiver, size);
		this.nodeList = nodeList;
		this.data 		= data;
	}
	public OrangeMessage(int typeOfMsg, Node sender, Node receiver, int size, LinkedList<Node> linkedNodeList)
	{
		super(typeOfMsg, sender, receiver, size);
		this.linkedNodeList = linkedNodeList;
	}
	
	// YOUR_SUPERPEER, I_DONT_HAVE_IT, I_HAVE_IT, FIRED
	public OrangeMessage(int typeOfMsg, Node sender, Node receiver, int size, Node superPeer) 
	{
		super(typeOfMsg, sender, receiver, size);
		this.superPeer = superPeer;
	}
	
	//GET_SUPERPEER, GET_MY_CLIENTS, UPLOAD, GOODBYE, CONNECT, UPLOAD_SPEED_THAT_CAN_BE_GIVEN, REJECT_SPEED
	public OrangeMessage(int typeOfMsg, Node sender, Node receiver, int size, int data) 
	{
		super(typeOfMsg, sender, receiver, size);
		this.data = data;
	}
	public OrangeMessage(int typeOfMsg, Node sender, Node receiver, int size, Node[] nodeList, int[] peerWatching)
	{
		super(typeOfMsg, sender, receiver, size);
		this.nodeList = nodeList;
		this.peerWatching = peerWatching;
	}
	
	//YOUR_CLIENTS
	public OrangeMessage(int typeOfMsg, Node sender, Node receiver, int size, Node[] nodeList, int[] peerWatching, int[][] index) 
	{
		super(typeOfMsg, sender, receiver, size);
		this.nodeList = nodeList;
		this.peerWatching = peerWatching;
		this.index = index;
	}
	
	//YOUR_CLIENTS
	public OrangeMessage(int typeOfMsg, Node sender, Node receiver, int size, Node[] nodeList, int[] peerWatching, int[][] index, int data) 
	{
		super(typeOfMsg, sender, receiver, size);
		this.nodeList = nodeList;
		this.peerWatching = peerWatching;
		this.index = index;
		this.data = data;
	}

}
