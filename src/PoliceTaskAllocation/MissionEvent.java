package PoliceTaskAllocation;

import java.util.HashMap;

import TaskAllocation.*;


public class MissionEvent extends Task{
	


	public MissionEvent(Location location, double duration, double startTime,
			int id, int priority,double utility,HashMap<AgentType, Integer> agentsRequired) {
		super(location, duration, startTime, id, priority,utility,agentsRequired);

	}

	public double getStartTime() {
		
		return missionArrivalTime;
	}

	@Override
	public Object clone() {
		MissionEvent m=new MissionEvent(location, totalDuration, missionArrivalTime, id, priority, utility, agentsRequiered);
		return m;
	}



}
