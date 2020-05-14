package Solver;

import java.util.Vector;

import PoliceTaskAllocation.PoliceUnit;
import fisher.FisherDistributed;
import fisher.FisherDistributedCA;
import fisher.FisherPolinom;
import fisher.FisherSemiDistributed;
import TaskAllocation.Assignment;
import TaskAllocation.Mailer;
import TaskAllocation.Task;
import TaskAllocation.Utility;

public class FisherDistributedSolverCA extends FisherSolver {

	private Vector<PoliceUnit>agents;
	private double Tnow;
	private Mailer mailer;
	
	public FisherDistributedSolverCA(Utility[][] input,
			TaskOrdering taskOrdering, Vector<Task> tasks, Vector<PoliceUnit> agents, double Tnow, Mailer mailer) {
		super(input, taskOrdering, tasks);
		this.agents = agents;
		this.Tnow = Tnow;
		this.mailer = mailer;

	}

	@Override
	public Vector<Assignment>[] solve() {
		FisherDistributedCA f2 = new FisherDistributedCA(agents, tasks, Tnow, mailer);
		creatFisherSolution(f2.algorithm());
		return taskPrioritization(allocation);
	}

}
