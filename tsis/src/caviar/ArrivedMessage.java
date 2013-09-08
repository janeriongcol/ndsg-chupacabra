package caviar;

import peersim.core.Node;

public class ArrivedMessage {
	final int    msgType;
	final Node   sender;
	public int    data0;
	public int    data;
	public Node[] nodeList;
	public Node	 superPeer;
	public int[] peerWatching;
	public int [][] index;
	
	public ArrivedMessage(int typeOfMsg, Node sender, int data0, int data)
	{
		this.msgType    = typeOfMsg;
		this.sender       = sender;
		this.data0     = data0;
		this.data = data;
	}
	public ArrivedMessage(int typeOfMsg, Node sender, Node[] nodeList)
	{
		this.msgType    = typeOfMsg;
		this.sender       = sender;
		this.nodeList = nodeList;
	}
	public ArrivedMessage(int typeOfMsg, Node sender, Node superPeer)
	{
		this.msgType    = typeOfMsg;
		this.sender       = sender;
		this.superPeer = superPeer;
	}
	public ArrivedMessage(int typeOfMsg, Node sender, int data)
	{
		this.msgType    = typeOfMsg;
		this.sender       = sender;
		this.data = data;
	}
	public ArrivedMessage(int typeOfMsg, Node sender, Node[] nodeList, int[] peerWatching)
	{
		this.msgType    = typeOfMsg;
		this.sender       = sender;
		this.nodeList = nodeList;
		this.peerWatching = peerWatching;
	}
	public ArrivedMessage(int typeOfMsg, Node sender, Node[] nodeList, int[] peerWatching, int[][] index)
	{
		this.msgType    = typeOfMsg;
		this.sender       = sender;
		this.nodeList = nodeList;
		this.peerWatching = peerWatching;
		this.index = index;
	}
}
