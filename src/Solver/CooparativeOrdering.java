package Solver;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.midi.SysexMessage;

import Comparators.PersonalUtilityComparator;
import Helpers.URLConnectionReader;
import TaskAllocation.*;

public class CooparativeOrdering extends GreedyOrdering {

	public final double EPS = 0.1;
	protected Vector<Task> activeEvents;
	protected double tnow;
	
	
	//private double maxImprovment;
	//private double utility;
	//private Vector<Assignment> tasks;

	public CooparativeOrdering(Vector<Task> activeEvents, double tnow) {
		this.activeEvents = activeEvents;
		this.tnow = tnow;
	}

	@Override
	public Vector<Assignment>[] TaskPrioritization(
			Vector<Assignment>[] allocation) {
		priorazation(allocation);
		
		
		for (Task e : activeEvents) {
			if (e.getNumAgentsRequiered() > 1) {
				hardConstraint(e, allocation);
			}
		}
		for (Vector<Assignment> vector : allocation) {
			reorderingAllocation(vector);
		}
		return allocation;
	}
	
	public void priorazation(Vector<Assignment>[] allocation){
		super.TaskPrioritization(allocation);
	}

	

	/*
	 * Reorders the schedule for each agent, subject to hard time constrains of
	 * the tasks
	 */

	protected void reorderingAllocation(Vector<Assignment> schedule) {
		
		
		if(schedule.isEmpty()){
			return;
		}
		Assignment as = schedule.get(0);
		double agentTime = tnow+as.getAgent().getDistance(as.getTask());// time to
																	// first
		double count = 0;													// task
		for (int i = 0; i < schedule.size(); i++) {
			count++;
			as = schedule.get(i);
			if (as.getTask().getHardConstraintTime() > 0
					&& agentTime < as.getTask().getHardConstraintTime()) {
				double previousTime;
				if (i > 0) {
					Task previos = schedule.get(i - 1).getTask();
					previousTime = agentTime
							- as.getTask().getDistance(previos);
				} else {
					previousTime = tnow;
				}
				if (rescheduleAssigment(schedule, i, agentTime, as.getTask())) {
					if (i == 0) {
						agentTime = previousTime;
					} else {
						Task previous = schedule.get(i - 1).getTask();
						as = schedule.get(i);
						agentTime = previousTime
								+ previous.getDistance(as.getTask());
						if(count<schedule.size()*2){
							i = i - 1;
						}
						
					}

				}
			}
			agentTime = agentTime + as.getRatio() * as.getTask().getWorkload();
			if (i + 1 < schedule.size()) {
				Task next = schedule.get(i + 1).getTask();
				agentTime = agentTime + as.getTask().getDistance(next);
			}

		}

	}

	/*
	 * Tries to precede tasks (if it possible) before the task with hard time
	 * constraint
	 */
	protected boolean rescheduleAssigment(Vector<Assignment> schedule, int i,
			double agentTime, Task task) {
		
		boolean b = false;
		double taskTime = task.getHardConstraintTime();
		Distancable previous;
		if (i == 0) {
			previous = schedule.get(0).getAgent();
		} else {
			previous = schedule.get(i - 1).getTask();
		}

		for (int j = i + 1; j < schedule.size(); j++) {
			Assignment current = schedule.get(j);
			
			agentTime = agentTime + current.getTask().getDistance(previous);
			agentTime = agentTime + current.getRatio()
					* current.getTask().getWorkload();
			if (agentTime + task.getDistance(current.getTask()) <= taskTime
					+ EPS) {
				switchTasks(j, schedule);
				b = true;
			} else {
				return false;
			}
			previous = current.getTask();
		}
		return b;
	}

	private void switchTasks(int j, Vector<Assignment> schedule) {
		Assignment a = schedule.remove(j - 1);
		schedule.add(j, a);

	}

	/*
	 * finds what is the latest time that agent (from all allocated agents) will
	 * arrive to the mission and assigns this as hard constraint time
	 */

	protected void hardConstraint(Task e, Vector<Assignment>[] allocation) {
		double taskTime = -1;
		for (int i = 0; i < allocation.length; i++) {
			Assignment as;
			double agentTime = tnow;
			if (!allocation[i].isEmpty()) {
				as = allocation[i].get(0);
				agentTime = as.getAgent().getDistance(as.getTask());
			}
			for (int j = 0; j < allocation[i].size(); j++) {
				Assignment a = allocation[i].get(j);
				if (a.getTask().equals(e) && taskTime < agentTime) {
					taskTime = agentTime;
				}
				agentTime = agentTime + a.getRatio()
						* a.getTask().getWorkload();
				if (j + 1 < allocation[i].size()) {
					Task next = allocation[i].get(j + 1).getTask();
					agentTime = agentTime + a.getTask().getDistance(next);
				}
			}
		}

		e.setHardConstraintTime(taskTime);

	}
	
	//initial optimal task ordering for each agent
/*	private void optimalTaskOrdering(Vector<Assignment>[] allocation) {
		for (int i = 0; i < allocation.length; i++) {
			findBestImprovment(i,allocation[i]);
		}
		
	}
	
	private void findBestImprovment(int i, Vector<Assignment> allocation) {
		maxImprovment = 0;
		utility=calculateUtility(allocation);
		Agent agent = allocation.get(0).getAgent();
		tasks = new Vector<Assignment>(allocation);		

		Assignment[] arr = new Assignment [tasks.size()];
		
		//creates permutation
		PermUtil<Assignment> per = new PermUtil<Assignment>(tasks.toArray(arr));
		Vector<UtilityThread> threads = new Vector<UtilityThread>();
		//for each permutation runs 
		//int count = 0;
		while(true){	
			if(maxAss>=10){
				count++;
				System.out.println(count);
			}
			Assignment [] all = per.next();
			if(all == null){
				break;
			}
			UtilityThread t =new UtilityThread(cloneAllocation(all));
			t.start();
			threads.add(t);	
			
			if(threads.size()==500000){
				for (UtilityThread ut : threads) {
					try {
						ut.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				Collections.sort(threads);
				Collections.reverse(threads);
				if(threads.get(0).getUtility()-utility>maxImprovment){
					maxImprovment = threads.get(0).getUtility()-utility;
					bestOrder[i] =threads.get(0).getVec(); 
				}
				threads.clear();
			}
		}
		
		
		
		for (UtilityThread ut : threads) {
			try {
				ut.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Collections.sort(threads);
		Collections.reverse(threads);
		if(threads.get(0).getUtility()-utility>0){
			maxImprovment = threads.get(0).getUtility()-utility;
			bestOrder[i] =threads.get(0).getVec(); 
		}
		
		
	}

	

	private Vector<Assignment> cloneAllocation(Assignment [] all) {
		Vector<Assignment> newAll = new Vector<Assignment>();
		for (int i = 0; i < all.length; i++) {
			
			newAll.add(all[i].clone());
		}
		return newAll;
	}

	private double calculateUtility(Vector<Assignment> vec) {
		
		Agent ag = vec.get(0).getAgent();
		Task next = vec.get(0).getTask();
		double time = ag.getDistance(next);
		double utility=next.getUtility()*Math.pow(0.99, time/Utility.timeUnit)*vec.get(0).getRatio();
		for (int i = 1; i < vec.size(); i++) {
			Task previous = next;
			time = time + previous.getWorkload()*vec.get(i-1).getRatio();
			next = vec.get(i).getTask();
			time = time + next.getDistance(previous);
			utility = utility + next.getUtility()*Math.pow(0.99, time/Utility.timeUnit)*vec.get(i).getRatio();
		}
		
		return utility;
	}*/

}
