package orange;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import orange.Gcp2pProtocol;

public class OrangeTransport implements Transport {
	
	// ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------
	private static final int minDelaySameCDNSameBin = 20;
	private static final int maxDelaySameCDNSameBin = 500;
	private static final int minDelaySameCDNDiffBin = 500;
	private static final int maxDelaySameCDNDiffBin = 1000;
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
	
	public OrangeTransport (String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}
	
	public Object clone() {
		return this;
	}

	@Override
	public long getLatency(Node sender, Node receiver) {
		// TODO Auto-generated method stub
		
		Gcp2pProtocol sprot = (Gcp2pProtocol) sender.getProtocol(pid);
		Gcp2pProtocol rprot = (Gcp2pProtocol) receiver.getProtocol(pid);
		
		// Same CDN
		if(sprot.CID == rprot.CID) {
			// Same CDN and Same Bin
			if(sprot.getbinID() == rprot.getbinID())
				return CommonState.r.nextInt(maxDelaySameCDNSameBin - minDelaySameCDNSameBin) + minDelaySameCDNSameBin;
			
			return CommonState.r.nextInt(maxDelaySameCDNDiffBin - minDelaySameCDNDiffBin) + minDelaySameCDNDiffBin; 
		}
		
		return CommonState.r.nextInt(maxDelayDiffCDN - minDelayDiffCDN) + minDelayDiffCDN;
	}

	@Override
	public void send(Node sender, Node receiver, Object msg, int pid) {
		// TODO Auto-generated method stub

		long delay = getLatency (sender, receiver);
		EDSimulator.add(delay, msg, receiver, pid);
	}

}
