package PoliceTaskAllocation;

import static PoliceTaskAllocation.Status.MOVING;
import TaskAllocation.*;

// diary event of agent arriving to task
public class AgentArrivesToEvent extends DiaryEvent {
	 
	public AgentArrivesToEvent( Assignment as, double tnow) {
		super(as);
		double dis = as.getTask().getDistance(as.getAgent());
		time=dis+tnow;
		as.getAgent().setMovingTime(dis);
		as.getAgent().setStartMovingTime(tnow);
		as.getAgent().setOnTheWay(true);
		as.getAgent().setStatus(MOVING);
	}


}
