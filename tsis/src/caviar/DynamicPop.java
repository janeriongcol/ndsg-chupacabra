package caviar;

import peersim.config.*;
import peersim.core.*;
import peersim.vector.*;
import peersim.dynamics.*;
import peersim.edsim.EDSimulator;

public class DynamicPop implements Control{
		private static final String PAR_PROTOCOL = "protocol";
		private static final String PAR_MAXSIZE = "maxsize";
		private static final String PAR_INIT = "init";
		
		
		private int pid;
		private int maxsize;
		protected final NodeInitializer[] inits;
		protected int joinedPeerSize = 100;
		
		protected void add(int n)
		{
			for (int i = 0; i < n; ++i) {
				Node newnode = (Node) Network.prototype.clone();
				for (int j = 0; j < inits.length; ++j) {
					inits[j].initialize(newnode);
				}
				Network.add(newnode);
				//System.out.println(newnode.getIndex());
				Gcp2pProtocol prot = (Gcp2pProtocol) newnode.getProtocol(pid);
				//prot.start(newnode);
				EDSimulator.add(10, new ArrivedMessage(ArrivedMessage.GET_SUPERPEER, newnode, prot.binID), prot.connectedCDN, pid);
				
			}
		}
		
		public DynamicPop(String prefix){
			pid = Configuration.getPid(prefix + "." + PAR_PROTOCOL);
			maxsize = Configuration.getInt(prefix + "." + PAR_MAXSIZE);
			Object[] tmp = Configuration.getInstanceArray(prefix + "." + PAR_INIT);
			inits = new NodeInitializer[tmp.length];
			for (int i = 0; i < tmp.length; ++i) {
				inits[i] = (NodeInitializer) tmp[i];
			}
		}
		
		/*
		*	METHODS
		*
		*/
		
		
		
		//true if success, otherwise false
		public final boolean execute(){
			if(joinedPeerSize<maxsize){
				int n = CommonState.r.nextInt(200);
				if(n>maxsize-joinedPeerSize)
					n = maxsize-joinedPeerSize;
				add(n);
				joinedPeerSize+=n;
				return true;
			}
			
			return false;
		}
		
		


}