package caviar;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.CommonState;

import java.io.*;

public class OrangeObserver implements Control {
	
	/**
	 * String name of the parameter, assigned to pid
	 */
	private static final String PAR_PROT = "protocol";
	private int pid;
	private PrintWriter writer1, writer2, writer3, writer4, writer5;
	File f1, f2, f3, f4, f5;
	String filebase = "data_gcp2p_";
	String UsedDlSpeedFileName = filebase + "UsedDlSpeed" + ".txt";
	String DlSpeedFilename = filebase + "DlSpeed" + ".txt";
	String UtilizationFilename = filebase + "Utilization" + ".txt";
	String UsedUPSpeedFilename = filebase + "UploadSpeed" + ".txt";
	String ElapsedTimeFilename = filebase + "ElapsedTime" + ".txt";
		
	public OrangeObserver(String prefix)
	{
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		initFiles();
		//System.out.printf("File is located at %s%n", f1.getAbsolutePath());
	}
	public boolean execute() {
		double networkTotalUsedDlSpeed = 0;
		double networkTotalDlSpeed = 0;
		double networkTotalUtilization = 0;
		double networkTotalUsedUPSpeed = 0;
		double networkTotalElapsedTime = 0;
		int totalnodes = Network.size() - 3;
		
		long time = CommonState.getTime();
		
		for(int i=3; i < Network.size(); i++) {
			Node n = Network.get(i);
			Gcp2pProtocol prot = (Gcp2pProtocol) n.getProtocol(pid);
						
			//System.out.println("--------------"+n.getIndex()+"--------------------");
			
			//System.out.println("Used DL Spd: " + prot.getUsedDownloadSpd());
			networkTotalUsedDlSpeed += prot.getUsedDownloadSpd();
			
			//System.out.println("DL Spd: " + prot.getDownloadSpd());
			networkTotalDlSpeed += prot.getDownloadSpd();
			
			//System.out.println("Utilization %: " + (double) prot.getUsedDownloadSpd()/prot.getDownloadSpd()*100);			
			networkTotalUtilization += (double) prot.getUsedDownloadSpd()/prot.getDownloadSpd()*100;
			
			//System.out.println("Used UP Spd: " + prot.getUploadSpd());
			networkTotalUsedUPSpeed += prot.getUploadSpd();
			
			//System.out.println("Elapsed Time: " + prot.getTimeElapsed());
			networkTotalElapsedTime += prot.getTimeElapsed();
			
			//System.out.println("----------------------------------------------");
			
		}
		
		double averageUsedDlSpd = networkTotalUsedDlSpeed / totalnodes;
		double averageDlSpeed = networkTotalDlSpeed/ totalnodes;
		double averageUtilization = networkTotalUtilization/totalnodes;
		double averageUploadSpd = networkTotalUsedUPSpeed/ totalnodes;
		double averageTimeElapsed =networkTotalElapsedTime/ totalnodes;
		
		System.out.println("----------------------------------------------");
		
		System.out.println("Average Used Dl Speed: " + averageUsedDlSpd);
		
		System.out.println("Average Dl Speed: " + averageDlSpeed);
		
		System.out.println("Average Utilization:  " + averageUtilization);
		
		System.out.println("Average Used UP Speed: " + averageUploadSpd);
		
		System.out.println("AverageTimeElapsed: " + averageTimeElapsed);

		System.out.println("----------------------------------------------");
		
		//writer.println("----------------------------------------------");
		
		writer1.println(time + " " + averageUsedDlSpd);		
		writer2.println(time + " " + averageDlSpeed);		
		writer3.println(time + " " + averageUtilization);		
		writer4.println(time + " " + averageUploadSpd);		
		writer5.println(time + " " + averageTimeElapsed);
		
		flushAllWriters();

		//writer.println("----------------------------------------------");
		
		return false;
	}
	
	public void initFiles()
	{
		f1 = new File(UsedDlSpeedFileName);
		f2 = new File(DlSpeedFilename);
		f3 = new File(UtilizationFilename);
		f4 = new File(UsedUPSpeedFilename);
		f5 = new File(ElapsedTimeFilename);
	
		try {
			writer1 = new PrintWriter(f1);
			writer2 = new PrintWriter(f2);
			writer3 = new PrintWriter(f3);
			writer4 = new PrintWriter(f4);
			writer5 = new PrintWriter(f5);
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			e.printStackTrace();
		}
		
		fileHeader(writer1, "Average Used Download Speed", "Time", "Used Download Speed (kbps)");
		fileHeader(writer2, "Average Download Speed", "Time", "Download Speed (kbps)");
		fileHeader(writer3, "Average Utilization", "Time", "Utilization (%)");
		fileHeader(writer4, "Average Used Upload Speed", "Time", "Upload Speed (kbps)");
		fileHeader(writer5, "Average Time Streaming", "Time", "Time Streaming  (seconds)");
	
	}
	
	public void fileHeader(PrintWriter w, String title, String x, String y)
	{
		w.println(title);
		w.println(x);
		w.println(y);
		w.println();
		w.flush();
	}
	
	public void flushAllWriters(){
		writer1.flush();
		writer2.flush();
		writer3.flush();
		writer4.flush();
		writer5.flush();
	}

}
