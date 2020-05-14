package Solver;

import java.util.Vector;

import TaskAllocation.Assignment;

public abstract class TaskOrdering {
	
	public TaskOrdering() {
		super();
	}

	public abstract Vector<Assignment>[] TaskPrioritization(Vector<Assignment>[] allocation);
}
