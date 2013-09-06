package caviar;

import peersim.core.*;

/**
 * 
 *
 * @author Fatima De Villa, Janeri Ongcol, Bryan Tan
 * @version 1.0
 */

public interface Overlay
{

/**
 * 0 = CDN; 1 = SuperPeer; 2 = Regular Peer
 */
public int nodeTag ();
/**
*	set the number of bins
*/
public void setSuperPeerSize(int size);
/**
*	set tbe superpeer of a bin
*/
public void setSuperPeer (Node peer, int binID);
/**
 * applicable to Regular Peers
 */
public Node getSuperpeer (int binID);
/**
 * Get list of peers 
 */
public Node [] getPeerList ();

/**
 * Applicable to SuperPeers and CDN Servers
 */
public Node [] getClientList ();

/**
 * CDN = 100 Mbps
 * SuperPeer/Regular = 125 kbps
 */
 /**
 *	Set the max download speed
 */
public void setDownloadSpd (int bw);
 /**
 *	Set the max upload speed
 */
public void setUploadSpd (int bw);
 /**
 *	Set the used upload speed
 */
public void setUsedDownloadSpd (int bw);
 /**
 *	Set the used upload speed
 */
public void setUsedUploadSpd (int bw);
/**
 * return the max download speed
 */
public int getDownloadSpd ();
 /**
 *	return the max upload speed
 */
public int getUploadSpd ();
/**
 * return the used download speed
 */
public int getUsedDownloadSpd ();
/**
 * return the used download speed
 */
public int getUsedUploadSpd ();


/**
 * Set RTT
 */
public void setRTT (int RTT);	//kailangan pa ba to?

/**
 * Get RTT
 */
public int getRTT ();	//kailangan pa ba to?

/**
 * Set bin ID
 */
public void setBinID (int binID);

/**
 * Get bin ID
 */
public void getBinID ();

/**
 * Get video list
 */
public int[] getVideoList(); //dunno how
}
