package traditional;

import peersim.core.*;

/**
 * 
 *
 * @author Fatima De Villa, Janeri Ongcol, Bryan Tan
 * @version 1.0
 */

public interface TraditionalOverlay {


	/**
	 * 0 = CDN; 1 = SupplyingPeer; 2 = Regular Peer
	 */
	public void setNodeTag (int tag);
	
	
	/**
	 * 
	 * @return nodeTag
	 */
	public int getNodeTag ();
	
	/**
	 * Set node n as one of the 3 CDNs
	 * @param cdnID - it cdnID 1, 2 or 3
	 * @param n - the node to act as CDN
	 *//*
	public void setAsCDN(int cdnID, Node n);
	*/
	
	/**
	 * set the CDN node that the node is under (randomized)
	 */
	public void setConnectedCDN(int cdnID);
	
	
	/**
	 * the CDN node that the node is under
	 */
	public Node getConnectedCDN();
	
	/**
	 * 
	 * @param cdnID - which CDN to get (1, 2 or 3)
	 * @return the CDN Node
	 */
	public Node getCDN (int cdnID);
	
	/**
	 * applicable to Regular Peers
	 */
	public Node getSupplyingPeer (int connectedCDN);
	
	
	/**
	 * Get list of peers 
	 */	
	public Node [] getPeerList ();
	
	
	/**
	 * Applicable to CDN Servers
	 */
	public Node [] getClientList ();
		
	
	/**
	 * CDN = 100 Mbps
	 * SupplyingPeer/Regular = 125 kbps
	 *
	 *	Set the max download speed
	 */
	public void setDownloadSpd (int bw);
	
	/**
	 * return the max download speed
	 */	
	public int getDownloadSpd ();
	
	
	 /**
	 *	Set the used download speed
	 *
	 * */	
	public void setUsedDownloadSpd (int bw);
		
	
	/**
	 * return the used download speed
	 */	
	public int getUsedDownloadSpd ();

	
	 /**
	 *	Set the max upload speed
	 */
	public void setUploadSpd (int bw);
	
	
	 /**
     *	return the max upload speed
     */	
	public int getUploadSpd ();
		
	
	/**
	 *	Set the used upload speed
	 *
	 **/	
	public void setUsedUploadSpd (int bw);
	
	
	/**
	 * return the used download speed
	 */	
	public int getUsedUploadSpd ();	
	
	
	/**
	 * Set its RTT relative to its CDN
	 */
	public void setCDNRTT (int RTT);	
	
	
	/**
	 * Get RTT relative to its CDN
	 */
	public int getCDNRTT();	
		
	
	/**
	 * Get video list
	 */
	public int[] getVideoList(); //dunno how

}
