package traditional;

import traditional.TraditionalProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.Control;
import peersim.dynamics.NodeInitializer;

public class TraditionalDynamicPop implements Control {
	private static final String PAR_PROTOCOL = "protocol";
	private static final String PAR_MAXSIZE = "maxsize";
	private static final String PAR_MAXJOINSIZE = "maxjoinsize";
	private static final String PAR_INIT = "init";

	private int pid;
	private int maxsize;
	private int maxjoinsize;
	protected final NodeInitializer[] inits;
	protected int joinedPeerSize = 100;

	public TraditionalDynamicPop(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROTOCOL);
		maxsize = Configuration.getInt(prefix + "." + PAR_MAXSIZE);
		maxjoinsize = Configuration.getInt(prefix + "." + PAR_MAXJOINSIZE);
		Object[] tmp = Configuration.getInstanceArray(prefix + "." + PAR_INIT);
		inits = new NodeInitializer[tmp.length];
		for (int i = 0; i < tmp.length; ++i) {
			inits[i] = (NodeInitializer) tmp[i];
		}
	}

	// true if success, otherwise false
	public final boolean execute() {
		if (joinedPeerSize < maxsize) {
			int n = CommonState.r.nextInt(200);
			// System.out.println("To be added: "+n);

			if (n > maxsize - joinedPeerSize)
				n = maxsize - joinedPeerSize;
			add(n);
			joinedPeerSize += n;
		}

		return false;
	}

	protected void add(int n) {
		for (int i = 0; i < n; ++i) {
			Node newnode = (Node) Network.prototype.clone();
			for (int j = 0; j < inits.length; ++j) {
				inits[j].initialize(newnode);
			}
			Network.add(newnode);
			TraditionalProtocol prot = (TraditionalProtocol) newnode
					.getProtocol(pid);
			prot.start(newnode);
		}
		// System.out.println("Add dynamic");
	}

}
