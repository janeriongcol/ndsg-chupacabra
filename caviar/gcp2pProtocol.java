package caviar;

//protocol

public class gcp2pProtocol implements Overlay, CDProtocol, EDProtocol{

//cycle chuchu, ewan ko kung gagawin natin, feeling ko hindi
public void nextCycle( Node node, int pid ){


}
//eto yung magproprocess ng messages
public void processEvent( Node node, int pid, Object event ) {
ArrivedMessage aem = (ArrivedMessage)event;



}


}
//eto yung class natin for message
class ArrivedMessage {

	final int    msgType;
	final Node   sender;
	final int    chunkNo;
	final int    chunkSize;
	public ArrivedMessage(int typeOfMsg, Node sender, int chunkNo, int chunkSize)
	{
		this.typeOfMsg    = msgType;
		this.sender       = sender;
		this.stripeId     = chunkNo;
		this.msgParameter = chunkSize;
	}
}