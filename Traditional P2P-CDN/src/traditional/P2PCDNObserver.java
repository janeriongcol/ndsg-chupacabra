package traditional;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.CommonState;

import java.io.*;

public class P2PCDNObserver implements Control {

	private static final String PAR_PROT = "protocol";
	private int pid;

	private PrintWriter writer1, writer2, writer3, writer4, writer5, writer6, writer7, writer8;
	File f1, f2, f3, f4, f5, f6, f7, f8;
	String filebase = "data_traditional_";
	String UtilizationFilename = filebase + "Utilization" + ".txt";
	String ConnectionSetUpTime = filebase + "ConnectionSetUpTime" + ".txt";
	String PlaybackDelayTime = filebase + "PlaybackDelayTime" + ".txt";
	String AverageRTT = filebase + "AverageRTT" + ".txt";
	String FirstConnectedPeers = filebase + "FirstConnectedPeers" + ".txt";
	String ConnectedPeers = filebase + "ConnectedPeers" + ".txt";
	String population = filebase + "Network.txt";
	
	
	String AverageReject = filebase + "AverageReject" + ".txt";

	public P2PCDNObserver(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		initFiles();
	}

	public boolean execute() {
		double networkTotalUtilization = 0;
		long networkTotalConnect = 0;
		long totalPlayback = 0;
		double totalAveRTT = 0;
		int activeLeechers = 0;
		int activeSources = 0;
		int totalPeersPlayback = 0;
		int totalPeersConnected = 0;
		long time = CommonState.getTime();
		int totalConnectionsAttempted = 0;
		int totalConnectionsAccepted = 0;
		int numSuppliers = 0;
		int totalFirstConnectedPeers = 0;
		int totalConnectedSourcePeers = 0;
		
		int total = 0;
		
		for(int i = 0; i < 3; i++){
			Node n = Network.get(i);
			TraditionalProtocol prot = (TraditionalProtocol) n.getProtocol(pid);
			totalAveRTT += prot.averageRTT;
			numSuppliers++;
		}

		for (int i = 3; i < Network.size(); i++) {
			Node n = Network.get(i);
			TraditionalProtocol prot = (TraditionalProtocol) n.getProtocol(pid);
			
			if (prot.startedStreaming) {
				if (prot.nodeTag == 2) {
					networkTotalUtilization += (double) prot
							.getUsedDownloadSpd() / prot.getDownloadSpd() * 100;
					activeLeechers++;
					if (prot.firstConnect) {
						networkTotalConnect += prot.getTimeElapsed();
						totalPeersConnected++;
					}
					if (prot.firstPlayback) {
						totalPlayback += prot.firstPlay;
						totalPeersPlayback++;
					}
					else{
						totalFirstConnectedPeers += prot.numFirstConnectedPeers;
						if(prot.numFirstConnectedPeers > 0) total++;
					}				
				}
				

				if (prot.nodeTag == 1){
					activeSources++;
					if(prot.averageRTT!=0){
						//System.out.println(prot.averageRTT);
						totalAveRTT += prot.averageRTT;
						numSuppliers++;
					}
				}
				
				totalConnectedSourcePeers += prot.numSource;
			}
			
			totalConnectionsAttempted += prot.numConnectionsAttempted;
			totalConnectionsAccepted += prot.numConnectionsAccepted;
			
		}

		double averageUtilization = 0;
		long averageConnect = 0;
		long averagePlayback = 0;
		double averageRTT = 0;
		double aveRejectionRate = 0;
		double aveFirstConnectedPeers = 0;
		double aveConnectedSourcePeers = 0;

		if (activeLeechers != 0) {
			averageUtilization = networkTotalUtilization / activeLeechers;

		}
		if (totalPeersConnected != 0) {

			averageRTT = totalAveRTT / (numSuppliers);
			averageConnect = networkTotalConnect / (totalPeersConnected);
			//aveFirstConnectedPeers = totalFirstConnectedPeers / totalPeersConnected;
			aveConnectedSourcePeers = totalConnectedSourcePeers / totalPeersConnected;
			
		}
		
		if(total!=0) aveFirstConnectedPeers= totalFirstConnectedPeers/total;

		if (totalPeersPlayback != 0) {
			averagePlayback = totalPlayback / totalPeersPlayback;
		}
		
		if (totalConnectionsAttempted != 0) {
			aveRejectionRate = (double) (totalConnectionsAttempted-totalConnectionsAccepted)/totalConnectionsAttempted*100;
		}

		System.out.println("----------------------------------------------");

		System.out.println("Average Utilization:  " + averageUtilization);

		System.out.println("Average Connection Set-up Time: " + averageConnect);

		System.out.println("Average Playback Time: " + averagePlayback);

		System.out.println("Average RTT: " + averageRTT);

		System.out.println("Active Seeders: " + activeSources);

		System.out.println("Active Leechers: " + activeLeechers);
		
		System.out.println("Connections Attempted: " + totalConnectionsAttempted);
		
		System.out.println("Connections Accepted: " + totalConnectionsAccepted);
		
		System.out.println("Rejection Rate: " + aveRejectionRate);
		
		System.out.println("Network Size: "+Network.size());
		
		System.out.println("Average Connected Peers Before First Playback: " + aveFirstConnectedPeers);
		
		System.out.println("Average Connected Peers Per Node: " + aveConnectedSourcePeers);
		
		writer1.println(time + " " + averageUtilization);	
		writer2.println(time + " " + averageConnect);
		writer3.println(time + " " + averagePlayback);
		writer4.println(time + " " + averageRTT);
		writer5.println(time + " " + aveRejectionRate);
		writer6.println(time + " " + aveFirstConnectedPeers);
		writer7.println(time + " " + aveConnectedSourcePeers);
		writer8.println(time + " " + Network.size() + " "+ activeLeechers);
		flushAllWriters();

		return false;
	}

	public void initFiles() {
		f1 = new File(UtilizationFilename);
		f2 = new File(ConnectionSetUpTime);
		f3 = new File(PlaybackDelayTime);
		f4 = new File(AverageRTT);
		f5 = new File(AverageReject);
		f6 = new File(FirstConnectedPeers);
		f7 = new File(ConnectedPeers);
		f8 = new File(population);
		try {
			writer1 = new PrintWriter(f1);
			writer2 = new PrintWriter(f2);
			writer3 = new PrintWriter(f3);
			writer4 = new PrintWriter(f4);
			writer5 = new PrintWriter(f5);
			writer6 = new PrintWriter(f6);
			writer7 = new PrintWriter(f7);
			writer8 = new PrintWriter(f8);
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			e.printStackTrace();
		}

		fileHeader(writer1, "Average Utilization", "Time", "Utilization (%)");
		fileHeader(writer2, "Average Connection Set-up Time", "Time",
				"Connection Set-up Time");
		fileHeader(writer3, "Average Playback Delay Time", "Time",
				"Playback Delay Time");
		fileHeader(writer4, "Average Round Trip Time", "Time", "Average RTT");
		fileHeader(writer5, "Average Rejection Rate", "Time", "Rejection Rate");
		fileHeader(writer6, "Average First Connected Peers", "Time", "First Connected Peers");
		fileHeader(writer7, "Average Connected Peers", "Time", "Connecte Peers");
		fileHeader(writer8, "Network Population", "Time", "Number of Peers");
	}

	public void fileHeader(PrintWriter w, String title, String x, String y) {
		w.println(title);
		w.println(x);
		w.println(y);
		w.println();
		w.flush();
	}

	public void flushAllWriters() {
		writer1.flush();
		writer2.flush();
		writer3.flush();
		writer4.flush();
		writer5.flush();
		writer6.flush();
		writer7.flush();
		writer8.flush();
	}
}
