package PoliceTaskAllocation;

import java.util.HashMap;

import TaskAllocation.*;


public class MissionEvent extends Task{
	
//------------------------------------------------constructor------------------------------------------//

	public MissionEvent(){
		super();
	};
	public MissionEvent(Location location, double duration, double startTime,
			int id, int priority,double utility,HashMap<AgentType, Integer> agentsRequired) {
		super(location, duration, startTime, id, priority,utility,agentsRequired);

	}

	public MissionEvent(Location location, double startTime,
						int id, int priority,double remainCover) {
		super(location, id,startTime, priority,remainCover);
	}
	public MissionEvent(double duration, int id, int priority) {
		super(duration, id, priority);
	}
	public MissionEvent(Location location,int id, double startTime) {
		super(location, id,startTime);
	}
//-------------------------------getters & setters --------------------------------------//
	public double getStartTime() {
		
		return missionArrivalTime;
	}

	@Override
	public Object clone() {
		MissionEvent m=new MissionEvent(location, totalDuration, missionArrivalTime, id, priority, utility, agentsRequiered);
		return m;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}




}
