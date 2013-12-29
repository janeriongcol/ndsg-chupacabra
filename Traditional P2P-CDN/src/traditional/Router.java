package traditional;

import java.util.LinkedList;
import java.util.Queue;

import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Node;
import peersim.transport.Transport;
import peersim.cdsim.*;

public class Router implements CDProtocol{
	
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
	
	// ------------------------------------------------------------------------
	// Static Fields
	// ------------------------------------------------------------------------
	/**
	 *  Protocol identifier, obtained from config property {@link #PAR_PROT}. GCP2P
	 **/
	private static int pid;	
	/**
	 * Queue representing the messages to be forwarded by the router
	 */
	Queue<TraditionalMessage> router;
	/**
	 * The node to which the router is linked with
	 */
	Node node;
	
	public Router (String prefix) {		
		router = new LinkedList<TraditionalMessage>();		
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}
	double maxUpload;
	@Override
	public void nextCycle(Node node, int pid) {
		TraditionalProtocol prot = (TraditionalProtocol) node.getProtocol(pid);
		maxUpload = prot.getUploadSpd();
		totSize = 0;
		emptyBuffer(node, maxUpload);
		//System.out.println("EMPTY");
	}
	
	/**
	 * The router forwards the messages in its buffer every cycle up to it available upload bandwidth
	 * @param node	- this node
	 * @param maxUpload - max upload speed of this node
	 */
	double totSize = 0;
	public void emptyBuffer (Node node, double maxUpload) {
		
		while(totSize <= maxUpload && !router.isEmpty()){
			TraditionalMessage peek = router.peek();
			if((double)peek.size/1000 <= maxUpload - totSize){
				totSize += (double)sendMsg()/1000;
			}
			else
				break;
		}
	}
	
	/**
	 * Called by Gcp2pProtocol, message is first queued up in the buffer of the router 
	 * before being sent
	 * @param msg - the message to be sent
	 */
	public void insertMsg (TraditionalMessage msg) {
		
		router.add(msg);
		TraditionalMessage peek = router.peek();
		if ((double)peek.size/1000 <= maxUpload - totSize){
			totSize+= (double)sendMsg()/1000;
		}
	
	}
	
	/**
	 * Actual message sending, the router finally forwards the message to 
	 * the designated receiver
	 * @param node - the node containing the protocol
	 * @return the size of the message sent 
	 */
	public int sendMsg () {
		TraditionalMessage msg;
		
		msg = router.poll(); // Get message at the head of router
		
		//Get sender and receiver to pass it to the transport layer
		Node sender = msg.sender;
		Node receiver = msg.receiver;
		
		// Send message		
		((Transport)sender.getProtocol(FastConfig.getTransport(pid))).
		send(
			sender,
			receiver,
			msg,
			pid);
				
		return msg.size;
	}

	/**
	 * I really don't know.
	 */
	public Object clone(){
		Router prot = null;
		try{
			prot = (Router) super.clone();
		}catch( CloneNotSupportedException e ) {} // never happens
		return prot;
	}

}
