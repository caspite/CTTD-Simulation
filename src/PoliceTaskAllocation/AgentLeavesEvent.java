package PoliceTaskAllocation;

import TaskAllocation.Agent;
import TaskAllocation.Assignment;
import TaskAllocation.Task;

public class AgentLeavesEvent extends DiaryEvent {

	public AgentLeavesEvent(Assignment assignment, double tnow) {
		super(assignment);
		Task t=assignment.getTask();
		time=tnow+t.getWorkingTime(assignment.getRatio());
		
	}

}
