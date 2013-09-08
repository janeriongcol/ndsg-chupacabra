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
						prot.cdnRTT = 0;
						break;
				case 1: Gcp2pProtocol.CDN2 = n;
						prot.setConnectedCDN(2);
						prot.cdnRTT = 0;
						break;
				case 2: Gcp2pProtocol.CDN3= n;
						prot.setConnectedCDN(3);
						prot.cdnRTT = 0;
						break;
			}//endswitch
							
		}//endfor
		
		
		/**
		 * binning for the regular nodes (Note: binning is specific to the cdn
		 * groups and the cdn of the node is set by Gcp2pNodeInit)
		 */
		int binID, cdnID;
		
		for (int i = 3; i < Network.size(); i++){
			n = Network.get(i);
			prot = (Gcp2pProtocol) n.getProtocol(pid);
			prot.computeBin();
			binID = prot.getbinID();				//get its binID
			
			cdnID = prot.getConnectedCDN();
			Node cdn = prot.getCDN(cdnID);			//get the CDN node its connected to
			prot2 = (Gcp2pProtocol) cdn.getProtocol(pid);
			
			prot2.addToBin(binID, n);				//add the node to the corresponding bin in its CDN
		}
		
		
		/**
		 * For each bin in each CDN group, choose a SuperPeer (the one with the lowest RTT in the bin)
		 */
				
		prot = (Gcp2pProtocol) Gcp2pProtocol.CDN1.getProtocol(pid);
		prot.setSuperPeers();
		
		prot = (Gcp2pProtocol) Gcp2pProtocol.CDN2.getProtocol(pid);
		prot.setSuperPeers();
		
		prot = (Gcp2pProtocol) Gcp2pProtocol.CDN3.getProtocol(pid);
		prot.setSuperPeers();
		
		return false; 
	}

}
