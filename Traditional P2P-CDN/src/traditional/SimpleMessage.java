package traditional;

import peersim.core.Node;

public class SimpleMessage {

	final int    msgType;
	final Node   sender;
	final Node   receiver;
	final int size;
	
	public SimpleMessage(){
		this.msgType = 0;
		this.sender = null;
		this.receiver = null;
		this.size = 0;
	}
	
	public SimpleMessage(int msgType, Node sender, Node receiver, int size){
		this.msgType = msgType;
		this.sender = sender;
		this.receiver = receiver;
		this.size = size;
	}
	
	
}
