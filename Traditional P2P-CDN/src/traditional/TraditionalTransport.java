package traditional;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;

public class TraditionalTransport implements Transport {

	// ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------
	private static final int minDelaySameCDN = 20;
	private static final int maxDelaySameCDN = 1000;
	private static final int minDelayDiffCDN = 1000;
	private static final int maxDelayDiffCDN = 1500;
	
	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------
	/**
	 * String name of the parameter, assigned to pid
	 */
	private static final String PAR_PROT = "protocol";
	
	// ------------------------------------------------------------------------
	// Static Fields
	// ------------------------------------------------------------------------

	/**
	 * Protocol identifier, obtained from config property {@link #PAR_PROT}.
	 **/
	private static int pid;
	
	public TraditionalTransport (String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}
	
	public Object clone() {
		return this;
	}
	
	@Override
	public long getLatency(Node sender, Node receiver) {
		// TODO Auto-generated method stub
		
		TraditionalProtocol sprot = (TraditionalProtocol) sender.getProtocol(pid);
		TraditionalProtocol rprot = (TraditionalProtocol) receiver.getProtocol(pid);
		
		if(sprot.CID == rprot.CID)
			return CommonState.r.nextInt(maxDelaySameCDN - minDelaySameCDN) + minDelaySameCDN;
		
		return CommonState.r.nextInt(maxDelayDiffCDN - minDelayDiffCDN) + minDelayDiffCDN;
	}

	@Override
	public void send(Node sender, Node receiver, Object msg, int pid) {
		// TODO Auto-generated method stub

		long delay = getLatency (sender, receiver);
		EDSimulator.add(delay, msg, receiver, pid);
	}
	
	

}
