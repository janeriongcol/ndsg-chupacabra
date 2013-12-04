package orange;

import peersim.core.*;
import peersim.config.Configuration;
import peersim.dynamics.*;

public class Gcp2pNodeInit implements NodeInitializer{
	
	
	/*
	*	Protocol which this initializer will be used for
	*/
	private final String PAR_PROT = "protocol";
	
	/*
	*	Number of categories
	* 	i.e. config
	*	Control.dynamicPop.init.0 gcp2pNodeInit
	*	Control.dynamicPop.init.0.category 10
	*/
	
	private final String PAR_CATEGORY = "category";
	private final String PAR_MINVIDEOSIZE = "minVideoSize";
	private final String PAR_RANGEVIDEOSIZE = "rangeVideoSize";
	
	
	
	private int pid;
	private int category;
	private int maxVideoSize;
	private int rangeVideoSize;
	/*
	*	GLOBALS
	*/
	
	
	
	
	public Gcp2pNodeInit(String prefix){
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		category = Configuration.getInt(prefix + "." + PAR_CATEGORY);
		maxVideoSize = Configuration.getInt(prefix + "." + PAR_MINVIDEOSIZE);
		rangeVideoSize = Configuration.getInt(prefix + "." + PAR_RANGEVIDEOSIZE);
	}
	
	/**
	 * Initialize a regular node in the G-CP2P Network. Randomly choose among the three
	 * CDN servers as its closest CDN. Randomly generate its RTT values for each of the three
	 * landmarks (30-70 ms). Set the node's upload and download speed by randomly choosing
	 * from values between 0-1000 Kbps and 1000-2000 Kbps, respectively. Set the used upload
	 * and download speed to 0 since the node has not yet started streaming. Get a random video
	 * the node wants to stream from a list of 20 per category then put it in a category.   
	 */
	
	public void initialize(Node n)
	{
		if (Network.size() == 0) return; // never happens since the Network starts with CDNs as initial nodes
		Gcp2pProtocol prot = (Gcp2pProtocol) n.getProtocol(pid);
		
		prot.setNodeTag(Gcp2pProtocol.RegularTag); // initialize the node to be Regular
		
		prot.setConnectedCDN(CommonState.r.nextInt(3) + 1); //random pick a CDN group to belong to range[1-3]
		
		Node cdn = prot.getConnectedCDN();
		Gcp2pProtocol prot2;
		
		//add to the clientlist of its CDN
		prot2 = (Gcp2pProtocol) cdn.getProtocol(pid);
		prot2.addClient(n);
			
		
		/**
		 * Within each CDN group, assume 3 landmarks (basis for binning), randomize RTT values of the 
		 * node to the landmarks
		 */
		prot.landmark1RTT = CommonState.r.nextInt(71) + 30; //Landmark 1, random RTT from 30-70 QUESTION: Hindi ba yung max na lalabas dito 100 hindi 70?
		prot.landmark2RTT = CommonState.r.nextInt(71) + 30; //Landmark 2
		prot.landmark3RTT = CommonState.r.nextInt(71) + 30; //Landmark 3
		
		prot.cdnRTT = CommonState.r.nextInt(71) + 30; 	//RTT from client to CDN;
		
		prot.uploadSpd = CommonState.r.nextInt(501) + 500; //Random upload speed from 0-1000Kbps
		prot.downloadSpd = CommonState.r.nextInt(1001) + 1000; //Random download speed from 1000-2000Kbps
		prot.usedUploadSpd = 0; // initialize to zero since it is not yet seeding
		prot.usedDownloadSpd = 0; // initialize to zero since it is not yet streaming
		prot.videoID = CommonState.r.nextInt(category*20); // get a random video ID, each category has 20 videos each. Range [0, 19]
		prot.categoryID = prot.videoID/20;
		prot.videoSize = CommonState.r.nextInt(rangeVideoSize)+maxVideoSize;
		prot.computeBin();
		prot.numPeers = 0;
		prot.numSource = 0;
		prot.sourcePeerList = new Node[prot.maxClients];
		prot.peerList = new Node[prot.maxClients];
		prot.peerSpdAlloted = new int [prot.maxClients];
		prot.setStartTime();
		prot2.addToBin(prot.binID, n);
		//prot.start(n);
		//EDSimulator.add(1, new ArrivedMessage(ArrivedMessage.GET_SUPERPEER, n, prot2.binID), prot2.connectedCDN, pid);
	}
		


}