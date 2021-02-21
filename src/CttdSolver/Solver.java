package CttdSolver;

import TaskAllocation.Agent;
import TaskAllocation.Assignment;
import TaskAllocation.Task;

import java.util.Vector;

public abstract class Solver {

    Vector<Task> activeEvents;
    Vector <Agent> agents;
    Vector<Assignment> schedule;


    //*** constructor ***//

    protected Solver(){}

    protected abstract void createConstraintGraph(double tnow);

    protected abstract Vector<Assignment> solve();



}
