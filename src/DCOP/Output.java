package DCOP;

import CTTD.Casualty;
import Helpers.WriteToFile;
import TaskAllocation.Task;

import java.util.HashMap;

public class Output {

    HashMap<Integer,Double> globalCost;//iteration. global cost
    String algorithm="Spcn";

    //*** constructor ***//

    public Output(int algorithm){

        globalCost=new HashMap<Integer,Double>();
        if(algorithm==1){
            this.algorithm="SpncDcop";
        }

    }

    public void addGlobalCost(int iteration,double cost){
        globalCost.put(iteration,cost);
    }

    public int getLength(){
        return this.globalCost.size();
    }

    public HashMap<Integer, Double> getGlobalCost() {
        return globalCost;
    }

    public void writeToFile(int agentNum, int taskNum, double tnow,int algorithmVer){
        WriteToFile.writeOutput(this.algorithm+" "+algorithmVer+"_"+agentNum+" agents_"+taskNum+" task_"+tnow+"_.csv",globalCost,algorithm);
    }

}
