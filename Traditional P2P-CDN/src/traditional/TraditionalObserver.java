package traditional;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.CommonState;

import java.io.*;

public class TraditionalObserver implements Control{
	
	private static final String PAR_PROT = "protocol";
	private int pid;
	
	private PrintWriter writer1, writer2, writer3, writer4, writer5;
	File f1, f2, f3, f4, f5;
	String filebase = "data_traditional_";
	String UtilizationFilename = filebase + "Utilization" + ".txt";
	String ConnectionSetUpTime = filebase + "ConnectionSetUpTime" + ".txt";
	String PlaybackDelayTime = filebase + "PlaybackDelayTime" + ".txt";
	String RTT = filebase + "AverageRTT.txt";
	String population = filebase + "Network.txt";
	
	public TraditionalObserver(String prefix){
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		initFiles();
	}
	
	public boolean execute(){
		double networkTotalUtilization = 0;
		long networkTotalConnect = 0;
		long totalPlayback = 0;
		int activeLeechers = 0;
		int activeSources = 0;
		int totalPeersPlayback = 0;
		long time = CommonState.getTime();
		
		for(int i=3; i < Network.size(); i++){
			Node n = Network.get(i);
			TraditionalProtocol prot = (TraditionalProtocol) n.getProtocol(pid);
			
			if(prot.startedStreaming){
				if(!prot.doneStreaming){
					networkTotalUtilization += (double) prot.getUsedDownloadSpd()/prot.getDownloadSpd()*100;
					activeLeechers++;
				}
				
				
				if(prot.firstPlayback){
					totalPlayback += prot.firstPlay;
					totalPeersPlayback++;
				}
				
				
				networkTotalConnect += prot.getTimeElapsed();
				activeSources++;
			}
		}
		
		double averageUtilization = 0;
		long averageConnect = 0;
		long averagePlayback = 0;
		
		if(activeLeechers != 0){
			averageUtilization = networkTotalUtilization/activeLeechers;
		}
		
		if(activeSources != 0){
			averageConnect = networkTotalConnect/activeSources;
		}
		
		if(totalPeersPlayback != 0){
			averagePlayback = totalPlayback/totalPeersPlayback;
		}
		
		System.out.println("----------------------------------------------");
		
		System.out.println("Average Utilization:  " + averageUtilization);
		
		System.out.println("Average Connection Set-up Time: " + averageConnect);
		
		System.out.println("Average Playback Time: " + averagePlayback);
		
		System.out.println("Active Seeders: " + activeSources);
		
		System.out.println("Active Leechers: " + activeLeechers);
		
		System.out.println("----------------------------------------------");
		
		writer1.println(time + " " + averageUtilization);	
		writer2.println(time + " " + averageConnect);
		writer3.println(time + " " + averagePlayback);
		//writer4.println(time + " "+ averageRTT);
		flushAllWriters();
		
		return true;
	}
	
	public void initFiles(){
		f1 = new File(UtilizationFilename);
		f2 = new File(ConnectionSetUpTime);
		f3 = new File(PlaybackDelayTime);
		
		try {
			writer1 = new PrintWriter(f1);
			writer2 = new PrintWriter(f2);
			writer3 = new PrintWriter(f3);
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			e.printStackTrace();
		}
		
		fileHeader(writer1, "Average Utilization", "Time", "Utilization (%)");
		fileHeader(writer2, "Average Connection Set-up Time", "Time", "Connection Set-up Time");
		fileHeader(writer3, "Average Playback Delay Time", "Time", "Playback Delay Time");
	}
	
	public void fileHeader(PrintWriter w, String title, String x, String y){
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
	}
}
