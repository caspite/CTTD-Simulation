package SW;

import java.util.*;

import Helpers.Metrics;
import PoliceTaskAllocation.*;
import TaskAllocation.*;

public class SW {

	public static void discountedSW(TreeMap<Double, Double> cumulativeSW,
			double Tnow, double Told, Vector<Task> activeEvents,
			Metrics metrics) {

		double newutility = 0;
		for (Task e : activeEvents) {
			double temp = e.getUtilityBetweenTimes(Told, Tnow);
			newutility = newutility + temp;
			if(temp>0.0){
				metrics.sumSW(temp, e);
			}
		}
		synchronized (cumulativeSW) {
			if (cumulativeSW.containsKey(Tnow)) {
				newutility = cumulativeSW.get(Tnow) + newutility;
			}
				cumulativeSW.put(Tnow, newutility);
		}
	
	}

	public static double currentAllocationUtility(Vector<Assignment>[] allocation, double tnow, Vector<PoliceUnit> policeUnits, Vector<Task> tasks) {

		Vector <AgentsOnMission> missions = new Vector<AgentsOnMission>();
		for (Task e : tasks) {
			if(e instanceof PatrolEvent){
				continue;
			}
			missions.add(new AgentsOnMission(e, tnow));
		}
		double utility = 0;
		for (int i = 0; i < allocation.length; i++) {
			if (allocation[i].isEmpty()) {
				continue;
			}
			double time = tnow;
			Assignment next = allocation[i].get(0);			
			time = time+ policeUnits.get(i).getDistance(next.getTask());
			AgentsOnMission nextA = new AgentsOnMission(next.getTask(), tnow);
			int in = missions.indexOf(nextA);
			if(next.getAgent().isAgentTypeOf(next.getType())){
				missions.get(in).arrival(time,next.getType());
			}
			Assignment previous = null;
			
			for (int j = 0; j < allocation[i].size(); j++) {

				time = time + next.getTask().getWorkingTime(next.getRatio());
				if(next.getAgent().isAgentTypeOf(next.getType())){
					missions.get(in).leaving(time,next.getType());
				}
				if ((j + 1) < allocation[i].size()) {
					previous = next;
					next = allocation[i].get(j + 1);
					nextA = new AgentsOnMission(next.getTask(), tnow);
					in = missions.indexOf(nextA);
					if(in==-1){
						System.out.println("stop");
					}
					time = time + previous.getTask().getDistance(next.getTask());
					if(next.getAgent().isAgentTypeOf(next.getType())){//if the the can handle this part of the mission
						missions.get(in).arrival(time,next.getType());
					}
				}
			}

		}
		for (AgentsOnMission a : missions) {
			utility = utility + a.calculateUtility();
		}
		utility -= penaltyForAbandonment(allocation,tnow);
		return utility;
	}

	private static double penaltyForAbandonment(Vector<Assignment>[] allocation, double tnow) {
		double penalty = 0;
		for (int i = 0; i < allocation.length; i++) {
			if(allocation[i].isEmpty()){
				continue;
			}
			if(!allocation[i].get(0).getAgent().getCurrentTask().equals(allocation[i].get(0)) && allocation[i].get(0).getAgent().getCurrentTask().getTask() instanceof MissionEvent ){
				penalty += calculatePenaltyForAbandonment(tnow, allocation[i].get(0).getTask());
			}			
		}
		return penalty;
	}
	
	public static double calculatePenaltyForAbandonment(double tnow, Task task) {
		double h=(5-task.getPriority());
		double uti= task.getTotalUtility();		
		return Math.max(h*Utility.minAbanPenalty, (uti/10)*Math.pow(Utility.abandFactor,task.getDoneWorkload()/Utility.timeUnit));
		
	}

	///Penalizes if the agents goes to patrol before task
	
	
	
}
