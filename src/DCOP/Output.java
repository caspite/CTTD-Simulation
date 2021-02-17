package DCOP;

import CTTD.Casualty;
import TaskAllocation.Task;

import java.util.HashMap;

public class Output {

    HashMap<Integer,Double> globalCost;//iteration. global cost

    //*** constructor ***//

    public void Output(){
        globalCost=new HashMap<>();
    }

    public void addGlobalCost(int iteration,double cost){
        globalCost.put(iteration,cost);
    }

}
