package PoliceTaskAllocation;

import TaskAllocation.Agent;
import TaskAllocation.Assignment;
import TaskAllocation.Task;

public class AgentLeavesEvent extends DiaryEvent {

	public AgentLeavesEvent(Assignment assignment, double tnow) {
		super(assignment);
		time=tnow+assignment.getDuration();
	}

	public String toString() {
		return ("Agent: "+assignment.getAgent().getId()+" leaves event: " + assignment.getTask().getId()+" time: "+time);
	}

}
