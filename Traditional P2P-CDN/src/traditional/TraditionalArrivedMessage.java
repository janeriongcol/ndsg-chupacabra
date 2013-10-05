package traditional;

import peersim.core.Node;

public class TraditionalArrivedMessage {
	
	public static final int GIVE_SP_LIST = 0;
	public static final int CDN_RP_CONNECT = 1;
	public static final int CDN_RP_DISCONNECT = 2;
	public static final int CONTRACT_SET = 3;
	public static final int CONTRACT_EXPIRED = 4;
	public static final int SP_RP_CONNECT = 5;
	public static final int SP_RP_DISCONNECT = 6;
	public static final int CONFIRM_CONNECT = 7;
	public static final int CONFIRM_DISCONNECT = 8;
	public static final int RECEIVE_SP_LIST = 9;
	public static final int REJECT = 10;
	public static final int CDN_RP_CONNECT_CONFIRM = 11;
	
	final int    msgType;
	final Node   sender;
	public int	 data;
	public int   data2;
	public Node	 nodeList[];
	public Node	 node;
	
	public TraditionalArrivedMessage(int msgType, Node sender){
		this.msgType = msgType;
		this.sender = sender;
		
	}
	
	public TraditionalArrivedMessage(int msgType, Node sender, int data){
		this.msgType = msgType;
		this.sender = sender;
		this.data = data;
	}
	public TraditionalArrivedMessage(int msgType, Node sender, int data, int data2){
		this.msgType = msgType;
		this.sender = sender;
		this.data = data;
		this.data2 = data2;
	}
	public TraditionalArrivedMessage(int msgType, Node sender, Node[] nodeList){
		this.msgType = msgType;
		this.sender = sender;
		this.nodeList = nodeList;
		
	}
	public TraditionalArrivedMessage(int msgType, Node sender, Node node){
		this.msgType = msgType;
		this.sender = sender;
		this.node = node;
		
	}
}
