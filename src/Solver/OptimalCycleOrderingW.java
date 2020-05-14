package Solver;


import java.util.Vector;



import Helpers.CycleDetect;
import PoliceTaskAllocation.*;
import TaskAllocation.*;


public class OptimalCycleOrderingW extends CooperativeCycleOrdering {
	protected double timeTo;

	public OptimalCycleOrderingW(Vector<Task> activeEvents, double tnow, Vector<PoliceUnit> policeUnits) {
		super(activeEvents, tnow, policeUnits);
	}
	
	public void priorazation(Vector<Assignment>[] allocation){
		for (int i = 0; i < allocation.length; i++) {
			allocation[i]=optimalPriorazation(allocation[i]);
		}

	}

	private Vector<Assignment> optimalPriorazation(Vector<Assignment> vector) {
		Vector<Assignment>newAssignment=new Vector<Assignment>();
		if(vector.isEmpty()){
			return vector;
		}
		Assignment temp;
		//Collections.max(vector,UtilityComparator.com);
		//vector.remove(temp);
		//newAssignment.add(temp);
		if(vector.isEmpty()){
			return newAssignment;
		}
		double time = tnow;
		
		temp = optimalNextAssignment(time,vector,vector.firstElement().getAgent());
		time =time+timeTo+temp.getTask().getWorkingTime(temp.getRatio());
		vector.remove(temp);
		newAssignment.add(temp);
		
		while(vector.size()>1){
			temp = optimalNextAssignment(time,vector,newAssignment.lastElement().getTask());
			time =time+timeTo+temp.getTask().getWorkingTime(temp.getRatio());
			vector.remove(temp);
			newAssignment.add(temp);
		}
		if(!vector.isEmpty()){
			newAssignment.add(vector.remove(0));
		}
		
		return newAssignment;
	}

	public Assignment optimalNextAssignment(double time, Vector<Assignment> vector,
			Distancable lastElement) {
		double maxUtility=-1;
		Assignment bestNext = null;
		for (Assignment as : vector) {
			double dis = lastElement.getDistance(as.getTask());
			double tempUtility = calculateUtilityForMission(as,time,dis);
			if(tempUtility>maxUtility){
				timeTo=dis;
				maxUtility = tempUtility;
				bestNext = as;
			}
		}
		return bestNext;
	}

	public double calculateUtilityForMission(Assignment as, double time, double dis) {
		double r = (((double)as.getTask().getNumOfAllocatedAgents())/as.getTask().getNumAgentsRequiered())*as.getRatio();
		
		double tempUtility = as.getTask().getTotalUtility()*r*Math.pow(Utility.DF,(as.getTask().getDFTime(time)/Utility.timeUnit));
		tempUtility = tempUtility/(as.getTask().getWorkload()*as.getRatio());
		return tempUtility;
	}

}
