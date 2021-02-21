package DCOP;

import CTTD.Casualty;
import Helpers.WriteToFile;
import TaskAllocation.Task;

import java.util.HashMap;

public class Output {

    HashMap<Integer,Double> globalCost;//iteration. global cost
    String algorithm="Spcn";

    //*** constructor ***//

    public Output(){
        globalCost=new HashMap<Integer,Double>();
    }

    public void addGlobalCost(int iteration,double cost){
        globalCost.put(iteration,cost);
    }

    public void writeToFile(double tnow){
        WriteToFile.writeOutput("SpcnDcop.csv"+tnow,globalCost,algorithm);
    }

}
