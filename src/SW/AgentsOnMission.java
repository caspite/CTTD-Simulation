package SW;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import PoliceTaskAllocation.AgentType;
import PoliceTaskAllocation.MissionEvent;
import PoliceTaskAllocation.PatrolEvent;
import TaskAllocation.Task;
import TaskAllocation.Utility;
/*
 * Class that aims to calculate the utility for specific 
 * missions. The utility depends on number of agents at the mission
 * and duration.
 */
public class AgentsOnMission {
	
	private SortedMap<Double, HashMap<AgentType, Integer>> agentsTime; // agent on mission in any time
	private Task mission;
	private double startTime = 0;
	private  HashMap<AgentType, Integer> agentsRequiered;
	
	public AgentsOnMission (Task task, double tnow){
		mission = task;
		agentsTime = new  TreeMap<Double, HashMap<AgentType, Integer>>();
		agentsRequiered = task.getAgentsRequiered();
		HashMap<AgentType, Integer> agentsOnMission = new HashMap<AgentType, Integer>();
		for (AgentType type : agentsRequiered.keySet()) {
			agentsOnMission.put(type, 0);
		}
		agentsTime.put(tnow, agentsOnMission);
		startTime = tnow;
		
	}
	
	
	public void arrival(double time, AgentType type){
		if(time<startTime){
			System.out.println("time");
		}
		
		if(!agentsRequiered.containsKey(type)){
			return;
		}
		
		for (Map.Entry<Double, HashMap<AgentType, Integer>> ent : agentsTime.entrySet()) {
			if(ent.getKey()>=time){
				ent.getValue().put(type,ent.getValue().get(type)+1);
			}
		}
		
		if(!agentsTime.containsKey(time)){
			SortedMap<Double, HashMap<AgentType, Integer>> head =agentsTime.headMap(time);
			int agents = 1;
			HashMap<AgentType, Integer> agentsOnMission = new HashMap<AgentType, Integer>();
			if(!head.isEmpty()){
				
				for (Map.Entry<AgentType, Integer> t : head.get(head.lastKey()).entrySet()){
					agentsOnMission.put(t.getKey(), t.getValue());
				}
				Double lastTime = head.lastKey();
				agents = agentsTime.get(lastTime).get(type)+1;
			}else {
				for (AgentType t : agentsRequiered.keySet()) {
					agentsOnMission.put(t, 0);
				}
			}
			agentsOnMission.put(type, agents);
			agentsTime.put(time, agentsOnMission);
		}
	}	
	
	public void leaving(double time, AgentType type){
		if(time<startTime){
			System.out.println("time leaving");
		}
		if(!agentsRequiered.containsKey(type)){
			return;
		}
		for (Map.Entry<Double, HashMap<AgentType, Integer>> ent : agentsTime.entrySet()) {
			if(ent.getKey()>=time){
				ent.getValue().put(type,ent.getValue().get(type)-1);
			}
		}
		
		if(!agentsTime.containsKey(time)){
			SortedMap<Double, HashMap<AgentType, Integer>> head =agentsTime.headMap(time);
			int agents = 0;
			HashMap<AgentType, Integer> agentsOnMission = new HashMap<AgentType, Integer>();
			if(!head.isEmpty()){
				for (Map.Entry<AgentType, Integer> t : head.get(head.lastKey()).entrySet()){
					agentsOnMission.put(t.getKey(), t.getValue());
				}
				Double lastTime = head.lastKey();
				agents = agentsTime.get(lastTime).get(type)-1;
			}
			agentsOnMission.put(type, agents);
			agentsTime.put(time, agentsOnMission);

		}
	}
	
	public double calculateUtility(){
		double utility = 0;
		if(agentsTime.size()<2){
			return 0;
		}
		if(sumAgents(agentsTime.get(agentsTime.firstKey()))==0){
			agentsTime.remove(agentsTime.firstKey());
			startTime = agentsTime.firstKey();
		}
		double time1 = startTime;
		double time2=0;
		int agents = sumAgents(agentsTime.get(agentsTime.firstKey()));
		agentsTime.remove(agentsTime.firstKey());
		
		
		for (Map.Entry<Double, HashMap<AgentType, Integer>> e : agentsTime.entrySet()) {
			
			time2=e.getKey();
			double timeDF = 1;
			if(mission instanceof MissionEvent){
				timeDF = (time2-time1)/mission.getDuration();
			}
			double DF =Math.pow(Utility.DF,(mission.getDFTime(startTime))/Utility.timeUnit );
			
			TreeMap<Integer, Double> PLutility = mission.getPLUtility();
			double grossUtility = 0;
			if(PLutility.containsKey(agents)){
				grossUtility =  PLutility.get(agents);
			}else{
				grossUtility =  PLutility.lastEntry().getValue();
			}
			utility  += grossUtility*timeDF*DF*agents;
			agents = sumAgents(e.getValue());
			time1 = time2;
			
		}
		return utility;
		
	}
	
	private int sumAgents(HashMap<AgentType, Integer> agents) {
		int count =0;
		for (Map.Entry<AgentType, Integer> entry : agentsRequiered.entrySet() ) {
			if(!agents.containsKey(entry.getKey())){
				System.out.println("stop");
			}
			if(entry.getValue()<agents.get(entry.getKey())){
				count  = count +entry.getValue();
			}else{
				count  = count +agents.get(entry.getKey());
			}
		}
		return count;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mission == null) ? 0 : mission.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj instanceof AgentsOnMission) ){
			AgentsOnMission other = (AgentsOnMission) obj;
			return mission.equals(other.mission);
		}
		if ((obj instanceof Task) ){
			Task other = (Task) obj;
			return mission.equals(other);
		}
		return false;
	}

}
