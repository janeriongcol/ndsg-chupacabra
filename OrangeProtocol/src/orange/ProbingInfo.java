package orange;

public class ProbingInfo {
	
	public static int size = 1024;
	public static int B = 300000;
	public static final int N = 5; //Number of probing packets
	public static final double theta = 0.01; // the threshold
			
	public int candidate_id;	//index of the candidate peer in the list of the requesting peer for candidates	
	public int sending_gap;	//the gap in between sending the probing packets
	public long previous_received_time = 0;
	public long[] receiving_gaps; //the gaps in between receiving the probing packets
	public int rg_index = -1; 	//index in the receiving_gap array
	public long rg_sum = 0;
	
	public ProbingInfo(int candidate_id)
	{
		 this.candidate_id = candidate_id;
		 receiving_gaps = new long[N-1];		 
	}
		
	public void addReceivingGap(){
		long receivedTime = System.currentTimeMillis();
		long gap;
		
		if(rg_index != -1) //not the first receive because the first receive has no gap bet. prev
		{	
			gap = receivedTime - previous_received_time;
			receiving_gaps[rg_index] = gap;
			rg_sum = rg_sum + gap;
		}
				
		rg_index++;
		previous_received_time = receivedTime;	//update for next receive
		
		
	}
	
	/**
	 * Final computation to determine if ABW passes the threshold theta
	 * @return
	 */
	public boolean confirmABW(int sending_gap)
	{
		this.sending_gap = sending_gap;
		double numerator = 0;
		int denominator = (int) Math.max(N-1 * sending_gap, rg_sum);
		
		for(int i = 0; i < N-1; i++){
			numerator = numerator + (receiving_gaps[i] - sending_gap);
		}
		
		if(numerator/denominator <= theta)
			return true;
		else 		
			return false;
		
		//return true;
	}
	
	
	
}
