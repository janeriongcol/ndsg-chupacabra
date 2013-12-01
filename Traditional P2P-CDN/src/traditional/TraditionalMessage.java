package traditional;

import peersim.core.Node;

public class TraditionalMessage extends SimpleMessage {
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
	public static final int CDN_RP_CONNECT_ACCEPT = 12;
	public static final int CONFIRM_ACCEPT = 13;
	public static final int UPLOAD = 14;
	public static final int CDN_RP_DISCONNECT_CONFIRM = 15;
	public static final int SP_RP_DISCONNECT_CONFIRM = 16;
	public static final int RP_DONE_STREAMING = 17;
	public static final int SP_RP_DISCONNECT_FULFILLED = 18;

	public int data;
	public int data2;
	public int data3;
	public Node nodeList[];
	public Node node;

	public TraditionalMessage(int typeOfMsg, Node sender, Node receiver, int size) {
		super(typeOfMsg, sender, receiver, size);
	}

	public TraditionalMessage(int typeOfMsg, Node sender, Node receiver, int size, int data) {
		super(typeOfMsg, sender, receiver, size);
		this.data = data;
	}

	public TraditionalMessage(int typeOfMsg, Node sender, Node receiver, int size, int data, int data2) {
		super(typeOfMsg, sender, receiver, size);
		this.data = data;
		this.data2 = data2;
	}

	public TraditionalMessage(int typeOfMsg, Node sender, Node receiver, int size, int data, int data2, int data3) {
		super(typeOfMsg, sender, receiver, size);
		this.data = data;
		this.data2 = data2;
		this.data3 = data3;
	}

	public TraditionalMessage(int typeOfMsg, Node sender, Node receiver, int size, Node[] nodeList) {
		super(typeOfMsg, sender, receiver, size);
		this.nodeList = nodeList;

	}

	public TraditionalMessage(int typeOfMsg, Node sender, Node receiver, int size, Node node) {
		super(typeOfMsg, sender, receiver, size);
		this.node = node;

	}

}
