package CttdSolver;

import TaskAllocation.Agent;
import TaskAllocation.Task;

public abstract class Utility {
    double utility;

    public Utility(){
        super();
    }



    public abstract void calculateUtility();
    public abstract double getUtility();
    public abstract Object clone();

}
