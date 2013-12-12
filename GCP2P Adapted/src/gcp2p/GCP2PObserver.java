package gcp2p;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.CommonState;

import java.io.*;

public class GCP2PObserver implements Control {
	
	/**
	 * String name of the parameter, assigned to pid
	 */
	private static final String PAR_PROT = "protocol";
	private int pid;
	private PrintWriter writer1, writer2, writer3, writer4, writer5;
	File f1, f2, f3, f4, f5;
	String filebase = "data_gcp2p_";
	String UtilizationFilename = filebase + "Utilization" + ".txt";
	String ConnectionSetUpTime = filebase + "ConnectionSetUpTime" + ".txt";
	String PlaybackDelayTime = filebase + "PlaybackDelayTime" + ".txt";
	String RTT = filebase + "AverageRTT.txt";
	String AverageReject = filebase + "AverageReject" + ".txt";
	
	public GCP2PObserver(String prefix)
	{
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		initFiles();
		//System.out.printf("File is located at %s%n", f1.getAbsolutePath());
	}
	public boolean execute() {
		double networkTotalUtilization = 0;
		long networkTotalConnect = 0;
		long totalPlayback = 0;
		int activeLeechers = 0;
		int activeSources = 0;
		int totalPeersPlayback = 0;
		int totalPeersConnected = 0;
		long time = CommonState.getTime();
		int totalConnectionsAttempted = 0;
		int totalConnectionsRejected = 0;
		int totalSupplying = 0;
		int totalAverage = 0;
		for(int i = 0; i < 3; i++){
			Node n = Network.get(i);
			GCP2PProtocol prot = (GCP2PProtocol) n.getProtocol(pid);
			totalAverage += prot.averageRTT;
			totalSupplying++;
		}
		
		for(int i=3; i < Network.size(); i++) {
			Node n = Network.get(i);
			GCP2PProtocol prot = (GCP2PProtocol) n.getProtocol(pid);
			if(prot.startedStreaming){
				//System.out.println("--------------"+n.getIndex()+"--------------------");
				
				//System.out.println("Used DL Spd: " + prot.getUsedDownloadSpd());
				if(!prot.doneStreaming){
					networkTotalUtilization += (double) prot.getUsedDownloadSpd()/prot.getDownloadSpd()*100;
		
					activeLeechers++;
					if(prot.firstConnect){
						networkTotalConnect += prot.getTimeElapsed();
						totalPeersConnected++;
					}
				}
				if(prot.averageRTT != 0){
					totalAverage += prot.averageRTT;
					totalSupplying++;
				}
				if(prot.firstPlayback){
					totalPlayback += prot.firstPlay;
					totalPeersPlayback++;
				}
				
				activeSources++;
				//System.out.println("----------------------------------------------");
			}
			
			totalConnectionsAttempted += prot.numConnectionsAttempted;
			totalConnectionsRejected += prot.numConnectionsRejected;
		}
		
		double averageUtilization = 0;
		long averageConnect = 0;
		long averagePlayback = 0;
		double aveRejectionRate = 0;
		double averageRTT = totalAverage/totalSupplying;
		
		if(activeLeechers!=0){
			averageUtilization = networkTotalUtilization/activeLeechers;
		}
		
		if(totalPeersConnected!=0){
			averageConnect = networkTotalConnect/totalPeersConnected;
		}
		
		if(totalPeersPlayback!=0){
			averagePlayback = totalPlayback/totalPeersPlayback;
		}
		
		if(totalConnectionsAttempted != 0) {
			aveRejectionRate = (double) totalConnectionsRejected/totalConnectionsAttempted*100;
		}
		
		System.out.println("----------------------------------------------");
		
		System.out.println("Average Utilization:  " + averageUtilization);
		
		System.out.println("Average Connection Set-up Time: " + averageConnect);
		
		System.out.println("Average Playback Time: " + averagePlayback);
		
		System.out.println("Active Seeders: " + activeSources);
		
		System.out.println("Active Leechers: " + activeLeechers);
		
		System.out.println("Connections Attempted: " + totalConnectionsAttempted);
		
		System.out.println("Connections Rejected: " + totalConnectionsRejected);
		
		System.out.println("Rejection Rate: " + aveRejectionRate);
		System.out.println("Average RTT: "+ averageRTT);
		
		GCP2PProtocol prot2 = (GCP2PProtocol) GCP2PProtocol.CDN1.getProtocol(pid);
		
		//System.out.println("Used Upload Speed CDN1 " + prot2.usedUploadSpd);

		System.out.println("----------------------------------------------");
		
		//writer.println("----------------------------------------------");
			
		writer1.println(time + " " + averageUtilization);	
		writer2.println(time + " " + averageConnect);
		writer3.println(time + " " + averagePlayback);
		writer4.println(time + " " + aveRejectionRate);
		writer5.println(time + " " + averageRTT);
		
		flushAllWriters();

		//writer.println("----------------------------------------------");
		
		return false;
	}
	
	public void initFiles()
	{
		f1 = new File(UtilizationFilename);
		f2 = new File(ConnectionSetUpTime);
		f3 = new File(PlaybackDelayTime);
		f4 = new File(AverageReject);
		f5 = new File(RTT);
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
		
		fileHeader(writer1, "Average Utilization", "Time", "Utilization (%)");
		fileHeader(writer2, "Average Connection Set-up Time", "Time", "Connection Set-up Time");
		fileHeader(writer3, "Average Playback Delay Time", "Time", "Playback Delay Time");
		fileHeader(writer4, "Average Rejection Rate", "Time", "Rejection (%)");
		fileHeader(writer5, "Averate RTT", "Time", "Time");
	
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
