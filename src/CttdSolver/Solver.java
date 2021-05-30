package CttdSolver;

import DCOP.Output;
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

    public abstract void createConstraintGraph(double tnow);

    public abstract Vector<Assignment> solve();
    public abstract Output getOutput();



}
