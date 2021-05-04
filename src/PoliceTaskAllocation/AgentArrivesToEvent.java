package PoliceTaskAllocation;

import static PoliceTaskAllocation.Status.MOVING;

import CTTD.Distance;
import TaskAllocation.*;

// diary event of agent arriving to task
public class AgentArrivesToEvent extends DiaryEvent {
	 
	public AgentArrivesToEvent( Assignment as, double tnow) {
		super(as);
		time=as.getArrivalTime();
		as.getAgent().setMovingTime(time);//todo-remove this function
		as.getAgent().setStartMovingTime(tnow);
		as.getAgent().setOnTheWay(true);
		as.getAgent().setStatus(MOVING);
	}

	public String toString() {
		return ("Agent: "+assignment.getAgent().getId()+" arrives to disaster site: "+ assignment.getTask().getId()+" Time: "+time+".");
		//return ("Mission Code: "+event.getMissionCode()+", Priority: "+event.getPriority()+", Time: "+event.getArrivalTime()+", Duration: "+event.getDuration()+", Units Required: "+event.getUnitsRequired()+", Location: "+event.getLocation().toString()+"\n");
	}


}
