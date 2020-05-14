package PoliceTaskAllocation;

import java.util.TreeMap;

import Helpers.MetricsSummary;

public class ThreadSimulation extends Thread {
	

	public void run(){
		DynamicPoliceAllocation d = MainSimulationForThreads.newSimulation();
		while(d!= null){
			try {
				d.runSimulation();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			d = MainSimulationForThreads.newSimulation();
		}
		
	}

}
