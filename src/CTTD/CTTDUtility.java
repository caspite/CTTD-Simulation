package CTTD;

import TaskAllocation.Agent;
import TaskAllocation.Task;
import TaskAllocation.Utility;

public class CTTDUtility  extends Utility {
    TriageActivity triageActivity;
    int capacity;

    //-------------------constructor---------------------------------//
    public CTTDUtility(Agent agent, Task task,double Tnow){
        super(agent,task);
        calculateParameters(Tnow);
    }
    @Override
    public void calculateParameters(double Tnow) {

    }

    @Override
    public double getUtility(double ratio) {
        return 0;
    }

    @Override
    public Object clone() {
        return null;
    }
}
