package caviar;

import peersim.config.*;
import peersim.core.*;
import peersim.vector.*;
import peersim.dynamics.*;

public class dynamicPopulation extends DynamicNetwork{
		private static final String PAR_PROTOCOL = "protocol";
		private static final String PAR_MAXSIZE = "maxsize";
		private static final String PAR_INIT = "init";
		
		
		private int pid;
		private int maxsize;
		protected final NodeInitializer[] inits;
		protected int joinedPeerSize = 0;
		
		
		public dynamicPopulation(String prefix){
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
		
		
		
		
		public final boolean execute(){
			if(joinedPeerSize<maxsize){
				int n = CommonState.r.nextInt(200);
				if(n>maxsize-joinedPeerSize)
					n = maxsize-joinedPeerSize;
				add(n);
			}
		}
		
		


}