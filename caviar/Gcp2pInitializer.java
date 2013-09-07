package caviar;

/**
 * 
 *
 * @author Fatima De Villa, Janeri Ongcol, Bryan Tan
 * @version 1.0
 */

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class Gcp2pInitializer implements Control {

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
	public Gcp2pInitializer(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}
	
	
	@Override
	public boolean execute() {
		// Set first 3 nodes as CDNs
		Node n;
		gcp2pProtocol prot;
		
				for (int i = 0; i < 3; i++) {
			n = Network.get(i);
			prot = (gcp2pProtocol) n.getProtocol(pid);
			prot.nodeTag = 1;
		}
				
		return false;
	}

}
