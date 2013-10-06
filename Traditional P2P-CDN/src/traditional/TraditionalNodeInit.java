package traditional;

import peersim.core.*;
import peersim.config.Configuration;
import peersim.dynamics.*;

public class TraditionalNodeInit implements NodeInitializer{

	/*
	*	Protocol which this initializer will be used for
	*/
	private final String PAR_PROT = "protocol";
		
	private int pid;
	
	public TraditionalNodeInit(String prefix){
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}
	
	/**
	 * Initialize a regular node in the traditional P2P-CDN Network. Randomly choose among the three
	 * CDN servers as its closest CDN. Set the node's upload and download speed by randomly choosing
	 * from values between 0-1000 Kbps and 1000-2000 Kbps, respectively. Set the used upload
	 * and download speed to 0 since the node has not yet started streaming. Get a random video
	 * the node wants to stream.  
	 */
	
	public void initialize(Node n)
	{
		if (Network.size() == 0) return; // never happens since the Network starts with CDNs as initial nodes
		TraditionalProtocol prot = (TraditionalProtocol) n.getProtocol(pid);
		
		prot.setNodeTag(TraditionalProtocol.RegularTag); // initialize the node to be Regular
		
		
		prot.setConnectedCDN(CommonState.r.nextInt(3) + 1); //random pick a CDN group to belong to range[1-3]
		
		Node cdn = prot.getConnectedCDN();
		TraditionalProtocol prot2;
		
		//add to the clientlist of its CDN
		prot2 = (TraditionalProtocol) cdn.getProtocol(pid);
		prot2.addClient(n);			
				
		prot.cdnRTT = CommonState.r.nextInt(121) + 30; 	//RTT from client to CDN;	
		//If same area as the CDN: 30 - 1 K
		//If different area with the CDN: 1-1.5 K		
		
		prot.uploadSpd = CommonState.r.nextInt(1001); //Random upload speed from 0-1000Kbps
		prot.downloadSpd = CommonState.r.nextInt(1001) + 1000; //Random download speed from 1000-2000Kbps
		prot.usedUploadSpd = 0; // initialize to zero since it is not yet seeding
		prot.usedDownloadSpd = 0; // initialize to zero since it is not yet streaming
		prot.videoID = CommonState.r.nextInt(120); // Set the video the node wants to stream
		prot.videoSize = CommonState.r.nextInt(10000)+10000;
		prot.numPeers = 0;
		prot.numSource = 0;
		prot.sourcePeerList = new Node[prot.maxClients];
		prot.peerList = new Node[prot.maxClients];
		prot.peerSpdAlloted = new int [prot.maxClients];
		prot.start(n);
	}

}
