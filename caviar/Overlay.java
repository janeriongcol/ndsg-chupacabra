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
public abstract int nodeTag ();

/**
 * applicable to Regular Peers
 */
public abstract Node getSuperpeer ();

/**
 * Get list of peers 
 */
public abstract Node [] getPeerList ();

/**
 * Applicable to SuperPeers and CDN Servers
 */
public abstract Node [] getClientList ();

/**
 * CDN = 100 Mbps
 * SuperPeer/Regular = 125 kbps
 */
public abstract void setBandwidth (int bw);

/**
 * Returns bandwidth
 */
public abstract int getBandwidth ();

/**
 * Set RTT
 */
public abstract void setRTT (int RTT);

/**
 * Get RTT
 */
public abstract int getRTT ();

/**
 * Set bin ID
 */
public abstract void setBinID (int binID);

/**
 * Get bin ID
 */
public abstract void getBinID ();

/**
 * Get video list
 */
public abstract Content [] getVidList ();

}
