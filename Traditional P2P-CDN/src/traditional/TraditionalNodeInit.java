package traditional;

import peersim.core.*;
import peersim.config.Configuration;
import peersim.dynamics.*;

public class TraditionalNodeInit implements NodeInitializer {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

	private static final int minCDNRTT = TraditionalNetworkProperties.minCdnRtt;
	private static final int maxCDNRTT = TraditionalNetworkProperties.maxCdnRtt;
	private static final int numVids = TraditionalNetworkProperties.numVids;
	private static final int maxClients = TraditionalNetworkProperties.maxClients;
	private static final int minVidSize = TraditionalNetworkProperties.minVidSize;
	private static final int maxVidSize = TraditionalNetworkProperties.maxVidSize;
	private static final int maxUpSpeed = TraditionalNetworkProperties.maxUpSpeed;
	private static final int minDlSpeed = TraditionalNetworkProperties.minDlSpeed;
	private static final int maxDlSpeed = TraditionalNetworkProperties.maxDlSpeed;
	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------
	private final String PAR_PROT = "protocol";
	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
	// Protocol which this initializer will be used for
	private int pid;

	public TraditionalNodeInit(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}

	/**
	 * Initialize a regular node in the traditional P2P-CDN Network. Randomly
	 * choose among the three CDN servers as its closest CDN. Set the node's
	 * upload and download speed by randomly choosing from values between 0-1000
	 * Kbps and 1000-2000 Kbps, respectively. Set the used upload and download
	 * speed to 0 since the node has not yet started streaming. Get a random
	 * video the node wants to stream.
	 */

	public void initialize(Node n) {
		if (Network.size() == 0)
			return; // never happens since the Network starts with CDNs as
					// initial nodes
		TraditionalProtocol prot = (TraditionalProtocol) n.getProtocol(pid);

		prot.setNodeTag(TraditionalProtocol.RegularTag); // initialize the node
															// to be Regular

		prot.setConnectedCDN(CommonState.r.nextInt(3) + 1); // random pick a CDN
															// group to belong
															// to range[1-3]

		Node cdn = prot.getConnectedCDN();
		TraditionalProtocol prot2;

		// add to the clientlist of its CDN
		prot2 = (TraditionalProtocol) cdn.getProtocol(pid);
		prot2.addClient(n);
		int rtt;
		if (CommonState.r.nextInt(3) == 1)
			rtt = CommonState.r.nextInt(970) + 30;
		else 
			rtt = CommonState.r.nextInt(500) + 1000;
		prot.cdnRTT = rtt;// RTT from client to
														// CDN;
		// If same area as the CDN: 30 - 1 K
		// If different area with the CDN: 1-1.5 K

		prot.uploadSpd = CommonState.r.nextInt(maxUpSpeed + 1) + 500; // Random upload speed
														// from 0-1000Kbps
		prot.downloadSpd = CommonState.r.nextInt(maxDlSpeed - minDlSpeed + 1) + minDlSpeed; // Random
																// download
																// speed from
																// 1000-2000Kbps
		prot.usedUploadSpd = 0; // initialize to zero since it is not yet
								// seeding
		prot.usedDownloadSpd = 0; // initialize to zero since it is not yet
									// streaming
		prot.videoID = CommonState.r.nextInt(numVids); // Set the video the node
														// wants to stream
		prot.videoSize = CommonState.r.nextInt(maxVidSize - minVidSize + 1) + minVidSize;
		prot.numPeers = 0;
		prot.numSource = 0;
		prot.setStartTime();
		prot.sourcePeerList = new Node[maxClients];
		prot.peerList = new Node[maxClients];
		prot.peerSpdAlloted = new int[maxClients];
		prot.peerRTT = new int[maxClients];
	}

}
