package orange;

import peersim.config.*;
import peersim.core.*;
import peersim.dynamics.*;
import peersim.edsim.EDSimulator;

/*
* @author Fatima De Villa, Janeri Ongcol, Bryan Tan
* @version 1.0
*/
public class DynamicPop implements Control{
	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------		
	private static final String PAR_PROTOCOL = "protocol";
	private static final String PAR_MAXSIZE = "maxsize";
	private static final String PAR_INIT = "init";
	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------			
	private int pid;
	private int maxsize;
	protected final NodeInitializer[] inits;
	protected int joinedPeerSize = 100;
	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------	
	public DynamicPop(String prefix){
		pid = Configuration.getPid(prefix + "." + PAR_PROTOCOL);
		maxsize = Configuration.getInt(prefix + "." + PAR_MAXSIZE);
		Object[] tmp = Configuration.getInstanceArray(prefix + "." + PAR_INIT);
		inits = new NodeInitializer[tmp.length];
		for (int i = 0; i < tmp.length; ++i) {
			inits[i] = (NodeInitializer) tmp[i];
		}
	}
	
	//true if success, otherwise false
	public final boolean execute(){
		if(joinedPeerSize<maxsize){
			int n = CommonState.r.nextInt(200);
			System.out.println("To be added: "+n);
			if(n>maxsize-joinedPeerSize)
				n = maxsize-joinedPeerSize;
			n = 9997;
			add(n);
			joinedPeerSize+=n;
		}
		
		return false;
	}
	
	protected void add(int n)
	{
		for (int i = 0; i < n; ++i) {
			Node newnode = (Node) Network.prototype.clone();
			for (int j = 0; j < inits.length; ++j) {
				inits[j].initialize(newnode);
			}
			Network.add(newnode);
			OrangeProtocol prot = (OrangeProtocol) newnode.getProtocol(pid);
			prot.start(newnode);
			//EDSimulator.add(10, new ArrivedMessage(ArrivedMessage.GET_SUPERPEER, newnode, prot.binID), prot.connectedCDN, pid);
		}
	}


}