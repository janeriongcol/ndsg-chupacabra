package gcp2p;

import gcp2p.GCP2PProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;

public class GCP2PTransport implements Transport {
	
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
	
	public GCP2PTransport (String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}
	
	public Object clone() {
		return this;
	}

	@Override
	public long getLatency(Node sender, Node receiver) {
		// TODO Auto-generated method stub
		
		GCP2PProtocol sprot = (GCP2PProtocol) sender.getProtocol(pid);
		GCP2PProtocol rprot = (GCP2PProtocol) receiver.getProtocol(pid);
		
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

		/*long delay = getLatency (sender, receiver);
		EDSimulator.add(delay, msg, receiver, pid);*/
		int delay = getDelayFluctuation();
		int initial = ((GCP2PMessage) msg).delay/2;
		EDSimulator.add(delay + initial, msg, receiver, pid);
	}
	public int getDelayFluctuation (){
		return CommonState.r.nextInt(100);
	}
}
