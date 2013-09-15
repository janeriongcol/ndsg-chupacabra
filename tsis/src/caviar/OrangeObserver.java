package caviar;

import peersim.config.Configuration;
import peersim.core.Control;

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
		System.out.println("Observer");
		return false;
	}

}
