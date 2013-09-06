package caviar;

import peersim.core.*;
import peersim.config.Configuration;

public class gcp2pNodeInit implements NodeInitializer{


/*
*	Protocol which this initializer will be used for
*/
private final string PAR_PROT = "protocol";

/*
*	Number of categories
* 	i.e. config
*	Control.dynamicPop.init.0 gcp2pNodeInit
*	Control.dynamicPop.init.0.category 10
*/

private final string PAR_CATEGORY = "category"




private int pid;
private int category;
/*
*	GLOBALS
*/




public gcp2pNodeInit(String prefix){
	pid = Configuration.getPid(prefix + "." + PAR_PROT);
	category = Configuration.getInt(prefix + "." + PAR_CATEGORY);
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
	gcp2pProtocol prot = (gcp2pProtocol) n.getProtocol(pid);
	prot.nodeTag = 2; // initialize the node to be Regular
	prot.closestCDN = CommonState.r.nextInt(3); //random CID from 0-2
	prot.landmark1RTT = CommonState.r.nextInt(71) + 30; //Landmark 1, random RTT from 30-70
	prot.landmark2RTT = CommonState.r.nextInt(71) + 30; //Landmark 2
	prot.landmark3RTT = CommonState.r.nextInt(71) + 30; //Landmark 3
	prot.uploadSpd = CommonState.r.nextInt(1001); //Random upload speed from 0-1000Kbps
	prot.downloadSpd = CommonState.r.nextInt(1001) + 1000; //Random download speed from 1000-2000Kbps
	prot.usedUploadSpd = 0; // initialize to zero since it is not yet seeding
	prot.usedDownloadSpd = 0; // initialize to zero since it is not yet streaming
	prot.videoID = CommonState.r.nextInt(category*20) // get a random video ID, each category has 20 videos each. Range [0, 19]
	prot.categoryID = prot.videoID/20;
}



}