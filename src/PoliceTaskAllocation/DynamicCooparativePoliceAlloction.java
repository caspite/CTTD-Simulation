package PoliceTaskAllocation;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import Helpers.MetricsSummary;
import TaskAllocation.Assignment;
import TaskAllocation.Task;

public class DynamicCooparativePoliceAlloction extends DynamicPoliceAllocation {

	public DynamicCooparativePoliceAlloction(TreeSet<DiaryEvent> diary,
			Vector<Task> activeEvents, Vector<PoliceUnit> p,
			TreeMap<Double, Double> cumulativeSW, int shift, MetricsSummary metrics) {
		super(diary, activeEvents, p, cumulativeSW,shift,metrics);
		// TODO Auto-generated constructor stub
	}

	protected void handleAgentArrivesEvent() {
		Assignment a = currentDiaryEvent.getAssignment();
		
		// adds the arriving agent to set of working agents on the event
		a.getTask().addAgent(a, Tnow);
		// agent no longer on the way
		a.getAgent().setOnTheWay(false);
		a.getAgent().setLocation(a.getTask().getLocation());
		
		if (a.getTask() instanceof MissionEvent) {
			a.getAgent().setWaiting(true);
			/*if (a.getTask().areAllAllocatedUnitsArrived()) {		
				handleBeginingOfMission(a);
			}*/
		}
	}
// If all agents arrived to the event
	private void handleBeginingOfMission(Assignment a) {
		
		metrics.countAllocatedEvents(a, Tnow);
		for (Assignment as : a.getTask().getAgents()) {
			as.getAgent().setWaiting(false);
			as.setArrivalTime(Tnow);
			AgentLeavesEvent al = new AgentLeavesEvent(a, Tnow);
			diary.add(al);
		}
		
	}
}
