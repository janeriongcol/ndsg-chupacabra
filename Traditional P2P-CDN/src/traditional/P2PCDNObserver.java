package traditional;

import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import traditional.TraditionalProtocol;

public class P2PCDNObserver {

	/**
	 * String name of the parameter, assigned to pid
	 */
	private static final String PAR_PROT = "protocol";
	private int pid;
	
	public P2PCDNObserver(String prefix)
	{
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}
	public boolean execute() {
		//System.out.println("Observer");
		for(int i=3; i < Network.size(); i++) {
			Node n = Network.get(i);
			TraditionalProtocol prot = (TraditionalProtocol) n.getProtocol(pid);			
			
			System.out.println("--------------"+n.getIndex()+"--------------------");
			System.out.println("Used DL Spd: " + prot.getUsedDownloadSpd());
			System.out.println("DL Spd: " + prot.getDownloadSpd());
			System.out.println("Utilization %: " + (double) prot.getUsedDownloadSpd()/prot.getDownloadSpd()*100);
			System.out.println("Used UP Spd: " + prot.getUploadSpd());
			System.out.println("Elapsed Time: " + prot.getTimeElapsed());
			System.out.println("----------------------------------------------");
			
		}
		return false;
	}


}
