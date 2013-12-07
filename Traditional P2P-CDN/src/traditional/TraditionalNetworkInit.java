package traditional;

/*
 * @author Fatima De Villa, Janeri Ongcol, Bryan Tan
 * @version 1.0
 */

import traditional.TraditionalProtocol;
import traditional.TraditionalNodeInit;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

public class TraditionalNetworkInit implements Control {
	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------
	private static final int numVids = TraditionalNetworkProperties.numVids;
	private static final int maxClients = TraditionalNetworkProperties.maxClients;
	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------
	private static final String PAR_PROT = "protocol";
	private static final String PAR_TRANS = "transport";
	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
	/** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
	private static int pid;
	/**
	 * Transport Protocol identifier, obtained from config property
	 * {@link #PAR_TRANS 
	 */
	private static int tid;
	// ------------------------------------------------------------------------
	// Objects
	// ------------------------------------------------------------------------
	TraditionalNodeInit initializer;

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	public TraditionalNetworkInit(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		tid = Configuration.getPid(prefix + "." + PAR_TRANS);
		initializer = new TraditionalNodeInit(prefix);
	}

	@Override
	public boolean execute() {
		/**
		 * Set the first 3 nodes as the CDNs. Initialize values contained by the
		 * nodes.
		 */

		Node n;

		for (int i = 0; i < Network.size(); i++) {
			n = Network.get(i); // current node
			if (i < 3) { // CDN node
				setAsCDN(i + 1, n); // Note: CID range [1, 3]
			} else { // regular node
				initialize(n);
			}
		}// endfor

		return false;
	}

	/**
	 * Set node n as one of the 3 CDNs
	 * 
	 * @param cdnID
	 *            - it cdnID 1, 2 or 3
	 * @param n
	 *            - the node to act as CDN
	 */
	public void setAsCDN(int cdnID, Node n) {

		/**
		 * Assign as either CDN1, CDN2 and CDN3 depending on its cdnID
		 */
		switch (cdnID) {
		case 1:
			TraditionalProtocol.CDN1 = n;
			break;
		case 2:
			TraditionalProtocol.CDN2 = n;
			break;
		case 3:
			TraditionalProtocol.CDN3 = n;
			break;
		}

		TraditionalProtocol prot = (TraditionalProtocol) n.getProtocol(pid);
		prot.setNodeTag(TraditionalProtocol.CDNTag); // tag as CDN

		prot.setConnectedCDN(0); // 0 because it is the CDN istelf
		prot.setCDNRTT(0); // distance to itself is 0

		/**
		 * initialize lists to be used by a CDN node
		 */
		prot.clientList = new Node[maxClients];
		prot.supplyingPeerList = new Node[maxClients];
		prot.videoSpdAlloted = new int[numVids];
		for (int i = 0; i < numVids; i++)
			prot.videoSpdAlloted[i] = 0;
		prot.numPeers = 0;
		prot.peerList = new Node[maxClients];
		prot.peerSpdAlloted = new int[maxClients];
		prot.startedStreaming = true;
		prot.peerRTT = new int [maxClients];
	}

	/**
	 * Initialize a regular node in the P2P-CDn Network. Set its start time and
	 * use TraditionalNodeInit to initialize the other values that need to be
	 * set.
	 */

	public void initialize(Node n) {
		// This never happens since the Network starts with CDNs as initial
		// nodes
		if (Network.size() == 0) {
			return;
		}

		TraditionalProtocol prot = (TraditionalProtocol) n.getProtocol(pid);

		// initialize the other properties of the node (speed, etc.)
		initializer.initialize(n);
	}

}
