package Solver;

import java.util.*;
import Comparators.IDComperator;
import Comparators.UtilityComparator;
import PoliceTaskAllocation.MissionEvent;
import PoliceTaskAllocation.PoliceUnit;
import SW.SW;
import TaskAllocation.*;
/*
 * Class that solves police task allocation problem. 
 * Static allocation is made by using simulated annealing
 * and internal ordering is made by heuristic.
 */

public class SASolver extends Solver{
	

	protected int missions;
	protected double temperature=1000000;
	protected double cooler = 0.99;
	protected static Double EPSILON = new Double(1E-11);
	protected Vector<Assignment>[]tempSolution;
	protected double DF=0.95;
	protected double tnow;
	protected double f=1;
	
	

	public SASolver(Utility[][] input,
			Vector<PoliceUnit> policeUnits, Vector<Task> tasks,
			TaskOrdering taskOrdering,Vector<Assignment>[]currentAllocation, double tnow) {
		super(input, policeUnits, tasks, taskOrdering);
		
		//missions=input[0].length;
		//convertSolution(currentAllocation);
		this.tnow = tnow;
		}

	private void convertSolution(
			Vector<Assignment>[] curentAllocation) {
		
		tempSolution=new Vector[missions];
		for (int i = 0; i < tempSolution.length; i++) {
			tempSolution[i]=new Vector<Assignment>();
		}
		
		for (int i = 0; i < curentAllocation.length; i++) {
			for (Assignment a : curentAllocation[i]) {
				Assignment b=a.clone();
				b.setRatio(1.0/a.getTask().getNumAgentsRequiered());
				int index=tasks.indexOf(b.getTask());
				if(index==-1) continue;
				if(b.getTask().getId()<0 && tempSolution[index].size()>0) continue;
				tempSolution[index].add(b);
			}	
		}
		for (int i = 0; i < tempSolution.length; i++) {
			for(int j=tempSolution[i].size();j<tasks.get(i).getNumAgentsRequiered();j++){
				int p=(int) (Math.random()*input.length);
				tempSolution[i].add(new Assignment(policeUnits.get(p), tasks.get(i), 1.0/tasks.get(i).getNumAgentsRequiered()));
			}
		}
		

	}

	/*Solver for SA algorithm */
	public Vector<Assignment>[] solve() {
		
		Vector<Assignment>[] newSolution = null;
		double utility = -1000000;

		
		utility = SW.currentAllocationUtility(checkAllocation(cloneSol(tempSolution)),tnow,policeUnits,tasks);
		double tempUtility = 0;
		while (temperature > EPSILON) {
			
			int j = (int) (Math.random() * missions);
			int coop =tasks.get(j).getNumAgentsRequiered();
			int j2 = (int) (Math.random() * coop);
			newSolution = nextState(j, j2, utility, tempSolution);
			tempUtility = SW.currentAllocationUtility(checkAllocation(cloneSol(newSolution)),tnow,policeUnits,tasks);
			if (tempUtility != utility) {
				utility = tempUtility;
				tempSolution = cloneSol(newSolution);
			}
		}
		return checkAllocation(cloneSol(tempSolution));
	}


	protected Vector<Assignment>[] checkAllocation(
			Vector<Assignment>[] missionAllocation) {
		for (int i = 0; i < missionAllocation.length; i++) {
			Collections.sort(missionAllocation[i],IDComperator.com);
			int coop = tasks.get(i).getNumAgentsRequiered();
				for (int j = 0; j < coop-1; j++) {
				if (missionAllocation[i].get(j).getAgent().getId() == missionAllocation[i]
						.get(j + 1).getAgent().getId()) {
					missionAllocation[i].get(j).setRatio(
							missionAllocation[i].get(j).getRatio()
									+ missionAllocation[i].get(j + 1).getRatio());
					missionAllocation[i].remove(j + 1);
					j--;
					coop--;
				}

			}
		//tasks.get(i).setNumOfAllocatedAgents(coop);
		}
		return divideAllocation(missionAllocation);
	}


	protected Vector<Assignment>[] cloneSol(Vector<Assignment>[] tempSolution) {
		Vector<Assignment>[] solution = new Vector[tempSolution.length];
		for (int i = 0; i < solution.length; i++) {
			solution[i] = new Vector<Assignment>();
			for (Assignment e : tempSolution[i]) {
				solution[i].add(e.clone());
			}
		}
		return solution;
	}

	
	protected double getProbability(double deltaEnergy) {
		double x = Math.exp(-deltaEnergy*f / temperature);
		//temperature = temperature * cooler;
		return x;
	}

	// search better assignment for unit i
	private Vector<Assignment>[] nextState(int task, int part, double utility,
			Vector<Assignment>[] tempSolution) {

		Vector<Assignment>[] newSolution = cloneSol(tempSolution);
		double tempUtility = -1000000;
		int i = (int) (Math.random() * input.length);

			if (i != tempSolution[task].get(part).getAgent().getId()) {
			newSolution[task].get(part).setAgent(policeUnits.get(i));
			//newSolution[task].get(part).setUtility(input[i][task]);
			tempUtility = SW.currentAllocationUtility(checkAllocation(cloneSol(newSolution)),tnow,policeUnits, tasks);
			if (tempUtility > utility
					|| getProbability(utility - tempUtility) > Math.random()) {
				tempSolution[task].get(part).setAgent(policeUnits.get(i));
				//tempSolution[task].get(part).setUtility(input[i][task]);
				utility = tempUtility;
			}
		}
		// }
		return tempSolution;
	}

	


	

}
