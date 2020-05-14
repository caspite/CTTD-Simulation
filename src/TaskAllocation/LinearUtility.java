package TaskAllocation;

import PoliceTaskAllocation.MissionEvent;
import PoliceTaskAllocation.PatrolEvent;
import SW.SW;



public class LinearUtility extends Utility {
	
	
	protected double linearUtility;	
	
	public LinearUtility(Agent agent, Task task,double linearUtility,double Tnow) {
		super(agent, task);
		this.linearUtility = linearUtility;
	}
	
	
	public LinearUtility(double linearUtility) {
		super();
		this.linearUtility = linearUtility;
	}


	public LinearUtility(Agent agent, Task task,double Tnow) {
		super(agent, task);
		calculateParameters(Tnow);
	}


	public LinearUtility() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public String toString() {
		return "LinearUtility [linearUtility=" + linearUtility + "]";
	}


	public double getUtility(double ratio) {// returns related part of the utility
		return ratio*linearUtility;
	}

	// calculates a linear utility using reward for performance,distance cost waiting time cost
	@Override
	public void calculateParameters(double Tnow) {//
		double distance=task.getDistance(agent);
		double DF=0.97;
		if(task instanceof PatrolEvent){
			linearUtility=task.getTotalUtility()*Math.pow(DF,distance/timeUnit);
		}else{
			linearUtility=task.getTotalUtility()*Math.pow(DF, task.getDFTime(Tnow)/timeUnit+ distance/timeUnit);
		}
		
		
		if(!task.equals(agent.getCurrentTask().getTask())&& task instanceof MissionEvent && agent.getCurrentTask().getTask() instanceof MissionEvent){
			linearUtility=linearUtility-SW.calculatePenaltyForAbandonment(Tnow,task);
			
		}

			linearUtility=Math.max(0.0, linearUtility);
			
	}

  // && agent.getStatus()==Status.WORKING
	/*private double calculatePenalty(double tnow) {
		double h=(5-task.getPriority());
		double uti= task.getTotalUtility();
			
		return Math.max(h*Utility.minAbanPenalty, (uti/10)*Math.pow(0.5,task.getDoneWorkload()/Utility.timeUnit));
		
	}*/


	@Override
	public Object clone() {
		LinearUtility l=new LinearUtility(agent, task, linearUtility,0);
		return l;
	}
	
	

}
