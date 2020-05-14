package Solver;

import java.util.Vector;

import fisher.FisherDistributed;
import fisher.FisherPolinom;
import fisher.FisherSemiDistributed;
import TaskAllocation.Assignment;
import TaskAllocation.Task;
import TaskAllocation.Utility;

public class FisherDistributedSolver extends FisherSolver {

	public FisherDistributedSolver(Utility[][] input,
			TaskOrdering taskOrdering, Vector<Task> tasks) {
		super(input, taskOrdering, tasks);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public Vector<Assignment>[] solve() {
		FisherDistributed f2 = new FisherDistributed(input);
		creatFisherSolution(f2.algorithm());
		return taskPrioritization(allocation);
	}

}
