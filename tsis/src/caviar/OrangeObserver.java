package caviar;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class OrangeObserver implements Control {
	
	/**
	 * String name of the parameter, assigned to pid
	 */
	private static final String PAR_PROT = "protocol";
	private int pid;
	
	public OrangeObserver(String prefix)
	{
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}
	public boolean execute() {
		//System.out.println("Observer");
		for(int i=0; i < Network.size(); i++) {
			Node n = Network.get(i);
			Gcp2pProtocol prot = (Gcp2pProtocol) n.getProtocol(pid);
			
			
			System.out.println("--------------"+n.getIndex()+"--------------------");
			//System.out.println("Used DL Spd: " + prot.getUsedDownloadSpd());
			//System.out.println("DL Spd: " + prot.getDownloadSpd());
			//System.out.println("Utilization %: " + prot.getUsedDownloadSpd()/prot.getDownloadSpd());
			//System.out.println("Used UP Spd: " + prot.getUploadSpd());
			System.out.println("Elapsed Time: " + prot.getTimeElapsed());
			System.out.println("----------------------------------------------");
			
		}
		return false;
	}

}
