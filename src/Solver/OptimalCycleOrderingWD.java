package Solver;

import java.util.Vector;

import PoliceTaskAllocation.DynamicPoliceAllocation;
import PoliceTaskAllocation.*;
import TaskAllocation.*;

public class OptimalCycleOrderingWD extends OptimalCycleOrderingW {

	public OptimalCycleOrderingWD(Vector<Task> activeEvents, double tnow,
			Vector<PoliceUnit> policeUnits) {
		super(activeEvents, tnow, policeUnits);
		// TODO Auto-generated constructor stub
	}

	
	public double calculateUtilityForMission(Assignment as, double time,double dis) {
		double tempUtility =0;
		if(as.getTask() instanceof MissionEvent){
			double r = (((double)as.getTask().getNumOfAllocatedAgents())/as.getTask().getNumAgentsRequiered())*as.getRatio();
			//double r = as.getRatio();
			tempUtility = as.getTask().getTotalUtility()*r*Math.pow(Utility.DF,(as.getTask().getDFTime(time+dis)/Utility.timeUnit));
			tempUtility = tempUtility/(as.getTask().getWorkload()*as.getRatio()+dis);
		}else{
			tempUtility = (1.0 - (dis/(DynamicPoliceAllocation.Tmax-tnow)))*(as.getTask().getTotalUtility()/as.getTask().getWorkload());
		}
		return tempUtility;
	}
	
	

}
