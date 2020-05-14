package Solver;

import java.util.Collections;
import java.util.Vector;

import SW.SW;
import TaskAllocation.Agent;
import TaskAllocation.Assignment;
import TaskAllocation.Task;
import TaskAllocation.Utility;

public class SAJones {
	private Vector<Assignment> allocation;
	private double temperature = 100000;
	private double cooler = 0.99;
	private double tnow;

	private static Double EPSILON = new Double(1E-11);

	public SAJones(Vector<Assignment> allocation,double tnow) {
		super();
		this.tnow = tnow;
		this.allocation = allocation;
	}

	public void solve() {

		while (temperature > EPSILON) {
			nextState();
		}
	}

	public Vector<Assignment> getAllocation() {
		return allocation;
	}
	
	public double currentUtility(){
		return utility(allocation);
	}

	private void nextState() {
		double utility =utility(allocation);
		Vector<Assignment> newSolution = cloneAllocation(allocation);
		int i  =  (int) (Math.random()*allocation.size());
		int  j = i;
		while(j==i){
			j  =  (int) (Math.random()*allocation.size());
		}
		Assignment a = allocation.get(i);
		Assignment b = allocation.get(j);
		Collections.swap(allocation, i, i);
		double newUtility = utility(newSolution);
		if (newUtility > utility
				|| getProbability(utility - newUtility) > Math.random()) {
			allocation = newSolution;
		}
	
	}

	public double utility(Vector<Assignment> vec) {
		double utility = 0;
		Agent ag = vec.get(0).getAgent();
		Task next = vec.get(0).getTask();
		if(!ag.getCurrentTask().equals(vec.get(0))){
			utility -= SW.calculatePenaltyForAbandonment(tnow, ag.getCurrentTask().getTask());
		}
		double time = ag.getDistance(next);
		utility += next.getTotalUtility()
				* Math.pow(Utility.DF, time / Utility.timeUnit);
		for (int i = 1; i < vec.size(); i++) {
			Task previous = next;
			time = time + previous.getWorkload();
			next = vec.get(i).getTask();
			time = time + next.getDistance(previous);
			utility = utility + next.getTotalUtility()
					* Math.pow(Utility.DF, time / Utility.timeUnit);
		}
		
		return utility;

	}

	private Vector<Assignment> cloneAllocation( Vector<Assignment> all) {
		Vector<Assignment> newAll = new Vector<Assignment>();
		for (Assignment assignment : all) {
			
			newAll.add(assignment.clone());
		}
		return newAll;
	}

	private double getProbability(double deltaEnergy) {
		double x = Math.exp(-deltaEnergy / temperature);
		temperature = temperature * cooler;
		return x;
	}

}
