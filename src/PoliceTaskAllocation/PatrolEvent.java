package PoliceTaskAllocation;

import java.util.HashMap;
import java.util.Map;

import TaskAllocation.*;

public class PatrolEvent extends Task {

	public PatrolEvent(Location location, double duration, double startTime,
			int id, int priority,double utility, HashMap<AgentType, Integer> numOfAgents) {
		super(location, duration, startTime, id, priority,utility,numOfAgents);
		isAllocated =false;
		isStarted =true;
		numOfAgentsRequired = 1;
		for (Map.Entry<AgentType, Integer> en : agentsRequiered.entrySet()) {
			durationDivision.put(en.getKey(), workload);
		}
		// TODO Auto-generated constructor stub
	}
	
	public double waitingTime(double Tnow) {
		return 0;
	}
	
	public double getRemainingPart() {// return the remaining part of the mission
		return 1;
	}
	public void removeAgent(Assignment a){
		agents.remove(a);

	}
	//removes agent before he finished his part
	public void removeAgentBeforeFinish(Assignment a,double tnow){
		agents.remove(a);
	}
	public double getCurrentUtility(double Tnow) {
		
		if(agents.size()==0){
			return 0;
		}
			return utility;

	}
	public void missionAbandoned(boolean isReallocation, double tnow) {

		return;
	}
	
	
	 public double getWorkload(){
		 return 3600;
	 }
	 
	 public double getDFTime(double tnow) {
			return 0;
		}
	 
	 public double getCurrentUtility() {
			if (agents.size() == 0) {
				return 0;
			}
			return utility;


		}

	 public Object clone() {
			PatrolEvent p=new PatrolEvent(location, totalDuration, missionArrivalTime, id, priority, utility,agentsRequiered);
			return super.clone();
		}
	 public void addAgent(Assignment a, double tnow) {
		 agents.add(a);
	 }
	 
	 public int getNumOfAgentTypesRequiered(){
		 return 1;
	 }


}
