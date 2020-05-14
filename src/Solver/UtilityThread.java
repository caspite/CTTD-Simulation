package Solver;

import java.util.Vector;

import SW.SW;
import TaskAllocation.Agent;
import TaskAllocation.Assignment;
import TaskAllocation.Task;
import TaskAllocation.Utility;

public class UtilityThread extends Thread implements Comparable<UtilityThread> {

	private Vector<Assignment> vec;
	private PermUtil<Assignment> per;
	protected double utility;
	private Vector<Assignment> best;
	private double tnow;

	public UtilityThread(PermUtil<Assignment> per, double utility, double tnow) {
		super();
		this.tnow = tnow;
		this.per = per;
		this.utility = utility;
	}

	public void run() {
		
		while (true) {
			vec = cloneAllocation(per.next());
			if (vec == null) {
				break;
			}
			
			double newUtility = utility();
			if (newUtility > utility) {
				utility = newUtility;
				best = vec;
			}
		}
	}

	
	public Vector<Assignment> getBest() {
		return best;
	}

	public double utility() {
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

	@Override
	public int compareTo(UtilityThread o) {
		// TODO Auto-generated method stub
		return (int) (utility - o.utility);
	}

	private Vector<Assignment> cloneAllocation(Assignment[] all) {
		if(all == null){
			return null;
		}
		Vector<Assignment> newAll = new Vector<Assignment>();
		for (int i = 0; i < all.length; i++) {

			newAll.add(all[i].clone());
		}
		return newAll;
	}
	public double getUtility(){
		return utility;
	}

}
