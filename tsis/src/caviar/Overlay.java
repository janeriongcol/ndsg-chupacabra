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
	public void setNodeTag (int tag);
	
	
	/**
	 * 
	 * @return nodeTag
	 */
	public int getNodeTag ();
	
	
	/**
	 * set the CDN group that the node belongs to (randomized)
	 */
	public void setConnectedCDN(int cdnID);
	
	
	/**
	 * the CDN group that the node belongs to 
	 */
	public int getConnectedCDN();
	
	/**
	 * 
	 * @param cdnID - which CDN to get (1, 2 or 3)
	 * @return the CDN Node
	 */
	public Node getCDN (int cdnID);
	
	/**
	 * Set bin ID (computed based on RTT from landmarks in the CDN group)
	 */
	public void setBinID (int binID);
	
	
	/**
	 * Get bin ID
	 */
	public int getbinID();
	
	
	/**
	*	set the number of bins (HINDI BA FIXED?) O:
	*/
	public void setSuperPeerSize(int size);
	
	
	/**
	*	set the superpeer of a bin, known by the CDN
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
	 * Set its RTT relative to its CDN
	 *
	public void setCDNRTT (int RTT);	
	*/
	
	/**
	 * Get RTT relative to its CDN
	 *
	public int getCDNRTT();	
	*/
	
	
	/**
	 * Get video list
	 */
	public int[] getVideoList(); //dunno how
}
