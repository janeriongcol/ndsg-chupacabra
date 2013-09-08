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
	private final String PAR_CATEGORY = "category";
	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
	/** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
	private static int pid;
	private static int category;
	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	public Gcp2pNetworkInitializer(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		category = Configuration.getInt(prefix + "." + PAR_CATEGORY);
	}
	
		
	@Override
	public boolean execute() {
		// Set first 3 nodes as CDNs
		Node n;
		Gcp2pProtocol prot, prot2;
		
		for (int i = 0; i < Network.getCapacity(); i++) {
			n = Network.get(i);
			if(i < 3) {
				prot = (Gcp2pProtocol) n.getProtocol(pid);
				prot.nodeTag = 1;	//tag as a CDN node
				switch(i){		//set references to the CDN in Gcp2pProtocol
			
					case 0: Gcp2pProtocol.CDN1 = n;
							prot.setConnectedCDN(1);
							prot.setCDNRTT(0);
							break;
					case 1: Gcp2pProtocol.CDN2 = n;
							prot.setConnectedCDN(2);
							prot.setCDNRTT(0);
							break;
					case 2: Gcp2pProtocol.CDN3= n;
							prot.setConnectedCDN(3);
							prot.setCDNRTT(0);
							break;
				}//endswitch
			}
			initialize(n);				
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
	
	/**
	 * Initialize a regular node in the G-CP2P Network. Randomly choose among the three
	 * CDN servers as its closest CDN. Randomly generate its RTT values for each of the three
	 * landmarks (30-70 ms). Set the node's upload and download speed by randomly choosing
	 * from values between 0-1000 Kbps and 1000-2000 Kbps, respectively. Set the used upload
	 * and download speed to 0 since the node has not yet started streaming. Get a random video
	 * the node wants to stream from a list of 20 per category then put it in a category.   
	 */
	
	public void initialize (Node n) {
		// This never happens since the Network starts with CDNs as initial nodes
		if (Network.size() == 0) { return; }
		
		Gcp2pProtocol prot = (Gcp2pProtocol) n.getProtocol(pid);
		
		// Set the node as Regular 
		prot.setNodeTag(2); 
		
		// Randomly set the nodes connected CDN (CDN1, CDN2, or CDN3)
		prot.setConnectedCDN(CommonState.r.nextInt(3));
		
		// Get the CDN the node is connected to and add it as its client
		Node cdn;
		Gcp2pProtocol prot2;
		cdn = prot.getConnectedCDN();
		prot2 = (Gcp2pProtocol) cdn.getProtocol(pid);
		prot2.addClient(n);
		
		// Set the node's RTT to each of the three predefined landmarks
		for (i=1; i<=3; i++) { prot.setLandmarkRTT(i); }
		
		// Set the node's RTT to its CDN
		prot.setCDNRTT(CommonState.r.nextInt((maxLandmarkRTT-minLandmarkRTT)+1) + minLandmarkRTT);
		
		// Set the node's speed
		prot.setUploadSpd(CommonState.r.nextInt(1001));
		prot.setDownloadSpd(CommonState.r.nextInt(1001) + 1000);
		prot.setUsedUploadSpd(0);
		prot.setUsedDownloadSpd(0);
		
		// Set the video the node wants to stream and the category it is under
		prot.videoID = CommonState.r.nextInt(category*20);
		prot.categoryID = prot.videoID/20;
	}

}
