package caviar;

/**
 * 
 *
 * @author Fatima De Villa, Janeri Ongcol, Bryan Tan
 * @version 1.0
 */

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class Gcp2pNetworkInitializer implements Control {

	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------
	private static final String PAR_PROT = "protocol";
	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
	/** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
	private static int pid;
	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	public Gcp2pNetworkInitializer(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}
	
		
	@Override
	public boolean execute() {
		// Set first 3 nodes as CDNs
		Node n;
		Gcp2pProtocol prot, prot2;
		
		for (int i = 0; i < 3; i++) {
			n = Network.get(i);
			prot = (Gcp2pProtocol) n.getProtocol(pid);
			prot.nodeTag = 1;	//tag as a CDN node
			
			switch(i){		//set references to the CDN in Gcp2pProtocol
			
				case 0: Gcp2pProtocol.CDN1 = n;
						prot.setConnectedCDN(1);
						break;
				case 1: Gcp2pProtocol.CDN2 = n;
						prot.setConnectedCDN(2);
						break;
				case 2: Gcp2pProtocol.CDN3= n;
						prot.setConnectedCDN(3);	
						break;
			}//endswitch
							
		}//endfor
		
		/**
		 * binning for the regular nodes (Note: binning is specific to the cdn
		 * groups and the cdn of the node is set by Gcp2pNodeInit)
		 */
		for (int i = 3; i < Network.size(); i++){
			n = Network.get(i);
			prot = (Gcp2pProtocol) n.getProtocol(pid);
			prot.computeBin();
		}
		
		/**
		 * For each bin in each CDN group, choose a SuperPeer (the one with the lowest RTT in the bin)
		 */
		return false; 
	}

}
