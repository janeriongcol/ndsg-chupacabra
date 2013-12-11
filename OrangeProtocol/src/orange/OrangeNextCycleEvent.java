package orange;
import peersim.core.*;
import peersim.cdsim.CDProtocol;
import peersim.edsim.CDScheduler;
import peersim.edsim.EDSimulator;
import peersim.edsim.NextCycleEvent;
public class OrangeNextCycleEvent extends NextCycleEvent{


	// =============================== initialization ======================
	// =====================================================================


	/**
	* Reads configuration to initialize the object. Extending classes should
	* have a constructor with the same signature, often as simple as
	* <code>super(n)</code>.
	*/
	public OrangeNextCycleEvent(String n) {
		super(n);
		
	}
	
	// --------------------------------------------------------------------

	/**
	* Returns a clone of the object. Overriding this method is necessary and
	* typically is as simple as <code>return super.clone()</code>. In general,
	* always use <code>super.clone()</code> to obtain the object to be returned
	* on which you can perform optional deep cloning operations (arrays, etc).
	*/
	public Object clone() throws CloneNotSupportedException {
		
		return super.clone();
	}


	// ========================== methods ==================================
	// =====================================================================


	/**
	* Executes the nextCycle method of the protocol, and schedules the next call
	* using the delay returned by {@link #nextDelay}.
	* If the next execution time as defined by the delay is outside of the
	* valid times as defined by {@link CDScheduler#sch}, then the next event is not scheduled.
	* Note that this means that this protocol will no longer be scheduled because
	* the next event after the next event is scheduled by the next event.
	*/
	/*@Override
	public final void execute() {

		int pid = CommonState.getPid();
		Node node = CommonState.getNode();
		CDProtocol cdp = (CDProtocol)node.getProtocol(pid);
		cdp.nextCycle(node,pid);
		CDProtocol clientCDP;
		Gcp2pProtocol prot = (Gcp2pProtocol)node.getProtocol(pid);
		for(int i = 0; i < prot.numClients; i++){
			clientCDP = (CDProtocol)prot.clientList[i].getProtocol(pid);
			clientCDP.nextCycle(prot.clientList[i], pid);
		}
		
		long delay = nextDelay(CDScheduler.sch[pid].step);
		if( CommonState.getTime()+delay < CDScheduler.sch[pid].until )
			EDSimulator.add(delay, this, node, pid);

	}*/

	// --------------------------------------------------------------------

	/**
	* Calculates the delay until the next execution of the protocol.
	* This default implementation returns a constant delay equal to the step
	* parameter (cycle length in this case) of the schedule of this event
	* (as set in the config file).
	*/
	protected long nextDelay(long step) {
		
		return step;
	}

}
