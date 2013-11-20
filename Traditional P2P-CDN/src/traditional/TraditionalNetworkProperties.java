package traditional;

import peersim.config.Configuration;
import peersim.core.Control;

public final class TraditionalNetworkProperties implements Control {
	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------
	private static final String PAR_MAXCLIENTS = "maxClients";
	private static final String PAR_MAXCDNRTT = "maxCdnRtt";
	private static final String PAR_MINCDNRTT = "minCdnRtt";
	private static final String PAR_NUMVIDS = "numVids";
	private static final String PAR_MINVIDSIZE = "minVidSize";
	private static final String PAR_MAXVIDSIZE = "maxVidSize";
	private static final String PAR_MAXUPSPEED = "maxUpSpeed";
	private static final String PAR_MINDLSPEED = "minDlSpeed";
	private static final String PAR_MAXDLSPEED = "maxDlSpeed";
	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
	/**
	 * Maximum number of clients, obtained from config property {@link #PAR_MAXCLIENTS}.*/
	public static int maxClients;
	/** Maximum CDN RTT, obtained from config property {@link #PAR_MAXCDNRTT}. */
	public static int maxCdnRtt;
	/** Minimum CDN RTT, obtained from config property {@link #PAR_MINCDNRTT}. */
	public static int minCdnRtt;
	/** Total Number of Videos in the network, obtained from config property {@link #PAR_NUMVIDS}. 	 */
	public static int numVids;
	/** Minimum Video Size, obtained from config property {@link #PAR_MINVIDSIZE}. 	 */
	public static int minVidSize;
	/** Maximum Video Size, obtained from config property {@link #PAR_MAXVIDSIZE}. 	 */
	public static int maxVidSize;
	/** Maximum Upload Speed, obtained from config property {@link #PAR_MAXUPSPEED}. 	 */
	public static int maxUpSpeed;
	/** Minimum Download Speed, obtained from config property {@link #PAR_MINDLSPEED}. 	 */
	public static int minDlSpeed;
	/** Maximum Download Speed, obtained from config property {@link #PAR_MINDLSPEED}. 	 */
	public static int maxDlSpeed;

	// ------------------------------------------------------------------------
	// Constructor 
	// ------------------------------------------------------------------------
	public TraditionalNetworkProperties(String prefix) {
		maxClients = Configuration.getInt(prefix + "." + PAR_MAXCLIENTS);
		maxCdnRtt = Configuration.getInt(prefix + "." + PAR_MAXCDNRTT);
		minCdnRtt = Configuration.getInt(prefix + "." + PAR_MINCDNRTT);
		numVids = Configuration.getInt(prefix + "." + PAR_NUMVIDS);
		minVidSize = Configuration.getInt(prefix + "." + PAR_MINVIDSIZE);
		maxVidSize = Configuration.getInt(prefix + "." + PAR_MAXVIDSIZE);
		maxUpSpeed = Configuration.getInt(prefix + "." + PAR_MAXUPSPEED);
		minDlSpeed = Configuration.getInt(prefix + "." + PAR_MINDLSPEED);
		maxDlSpeed = Configuration.getInt(prefix + "." + PAR_MAXDLSPEED);
	}

	@Override
	public boolean execute() {
		// Just print out the constants
		System.out.println("Max number of clients: " + maxClients);
		System.out.println("Max CDN RTT: " + maxCdnRtt);
		System.out.println("Min CDN RTT: " + minCdnRtt);
		System.out.println("Number of Videos: " + numVids);
		System.out.println("Minimum Video Size: " + minVidSize);
		System.out.println("Maximum Video Size: " + maxVidSize);
		System.out.println("Maximum Upload Speed: " + maxUpSpeed);
		System.out.println("Minimum Download Speed: " + minDlSpeed);
		System.out.println("Maximum Download Speed: " + maxDlSpeed);
		return false;
	}

}
