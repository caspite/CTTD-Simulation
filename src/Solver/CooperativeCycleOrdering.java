package Solver;

import java.util.Vector;

import Helpers.CycleDetect;
import PoliceTaskAllocation.PoliceUnit;
import TaskAllocation.*;

public class CooperativeCycleOrdering extends CooparativeOrdering {

	protected Vector<PoliceUnit> units;

	public CooperativeCycleOrdering(Vector<Task> activeEvents, double tnow, Vector<PoliceUnit> policeUnits) {
		super(activeEvents, tnow);
		this.units = policeUnits;
	}
	
	public Vector<Assignment>[] TaskPrioritization(
			Vector<Assignment>[] allocation) {
		priorazation(allocation);
		//System.out.println("Done priorization");
		//CycleDetect c = new CycleDetect(allocation,activeEvents,units);
		//allocation=c.solve();
		//System.out.println("Done Cycle");
		for (Task e : activeEvents) {
			if (e.getNumAgentsRequiered() > 1) {
				hardConstraint(e, allocation);
			}
		}
		//System.out.println("Done hard constraint");
		for (Vector<Assignment> vector : allocation) {
			reorderingAllocation(vector);
		}
		//System.out.println("Done reordring");
		return allocation;
	}


}
