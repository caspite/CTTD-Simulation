package Solver;

import java.util.*;
import java.util.Map.Entry;

import Comparators.IDComperator;
import PoliceTaskAllocation.*;
import SW.*;
import TaskAllocation.*;

/*
 * Simulated Annealing solver that solves police allocation problem .
 * The initial state based on the current allocation.
 * From one state to other it changes the allocation and the 
 * internal ordering. 
 * 
 */
public class SATotalSolver extends SASolver {

	protected Vector<Assignment>[] allocation;
	protected static Random random = new Random();
	protected Task currentTask;

	public SATotalSolver(Utility[][] input, Vector<PoliceUnit> policeUnits,
			Vector<Task> tasks, Task currentTask,
			Vector<Assignment>[] currentAllocation, double tnow) {
		super(input, policeUnits, tasks, null, currentAllocation, tnow);
		this.allocation = currentAllocation;
		this.tnow = tnow;
		this.currentTask = currentTask;
		removePatrols();
		checkOldAllocation();
		addNewTask(currentTask);
		updateAllRatios();
	}

	// checks for agents that finished their part and reallocates that
	// mission to rest of the working units
	protected void checkOldAllocation() {
		Vector<Assignment> taskAss;
		for (int i = policeUnits.size(); i < tasks.size() - 1; i++) {
			taskAss = findAllAllocations(i);
			if (taskAss.isEmpty()) {
				return;
			}
			double sum = 0;
			for (Assignment as : taskAss) {
				sum += as.getRatio();
			}
			if ((1.0 - sum) > Double.MIN_VALUE) {
				int t = (int) (taskAss.size() * Math.random());
				taskAss.get(t)
						.setRatio(taskAss.get(t).getRatio() + (1.0 - sum));
			}

		}
		// TODO Auto-generated method stub

	}

	private Vector<Assignment> findAllAllocations(int i) {

		Vector<Assignment> taskAss = new Vector<Assignment>();
		for (int j = 0; j < allocation.length; j++) {
			for (Iterator iterator = allocation[j].iterator(); iterator
					.hasNext();) {
				Assignment ass = (Assignment) iterator.next();
				if (ass.getTask().equals(tasks.get(i))) {
					taskAss.add(ass);
				}
			}

		}
		return taskAss;
	}

	private void removePatrols() {
		for (int i = 0; i < allocation.length; i++) {
			for (Iterator<Assignment> it = allocation[i].iterator(); it
					.hasNext();) {
				if (it.next().getTask() instanceof PatrolEvent)
					it.remove();

			}
		}

	}

	// Adds new assignment to random agents at at random schedule
	private void addNewTask(Task currentTask) {
		int numOfAgents = currentTask.getNumAgentsRequiered();
		HashSet<Integer> assignedAgents = new HashSet<Integer>();

		for (Entry<AgentType, Integer> entry : currentTask.getAgentsRequiered().entrySet()) {
			for (int i = 0; i < entry.getValue(); i++) {
				
				int a = random.nextInt(policeUnits.size());
				assignedAgents.add(a);
				int schedual = random.nextInt(allocation[a].size() + 1);
				allocation[a].add(schedual, new Assignment(policeUnits.get(a), 
						currentTask, 1.0 / numOfAgents ,entry.getKey()));
			}

		}

	}

	public Vector<Assignment>[] solve() {

		double utility = Double.NEGATIVE_INFINITY;
		utility = SW.currentAllocationUtility(
				checkDuplication(cloneSol(allocation)), tnow, policeUnits,
				tasks);
		double tempUtility = 0;
		// long start = System.currentTimeMillis();
		while (temperature > EPSILON) {

			nextState();

			temperature = temperature * cooler;

			tempUtility = SW.currentAllocationUtility(
					checkDuplication(cloneSol(tempSolution)), tnow,
					policeUnits, tasks);

			if (tempUtility > utility) {
				utility = tempUtility;
				allocation = tempSolution;
			}

		}
		// long stop = System.currentTimeMillis();
		// System.out.println(stop-start);

		// || getProbability(Math.abs(utility - tempUtility)) > Math.random()
		returnPatrols();

		return checkDuplication(allocation);
	}

	private void updateAllRatios() {
		for (Task t : tasks) {
			if(t instanceof MissionEvent){
				updateRatio(t, allocation);
			}
			
		}
		
	}

	private void returnPatrols() {
		for (int i = 0; i < allocation.length; i++) {
			Assignment a = new Assignment(policeUnits.get(i), tasks.get(i
					% MainSimulationForThreads.patrols), 1, AgentType.TYPE1);
			allocation[i].add(a);
		}

	}

	private void nextState() {
		boolean flag = false;
		tempSolution = cloneSol(allocation);

		while (!flag) {
			f = 1;
			int s = random.nextInt(3);
			if (s == 0) {
				flag = nextState1();
			} else if (s == 1) {
				flag = nextState2();
			} else if (s == 2) {
				flag = nextState3();
			}
		}

	}

	// Changes state. Chooses randomly an agent (with more than 1 task)
	// and changes its schedule randomly
	private boolean nextState1() {

		Vector<Integer> ag = new Vector<Integer>();
		for (int i = 0; i < allocation.length; i++) {
			if (allocation[i].size() > 1) {
				ag.add(i);
			}
		}
		if (ag.isEmpty()) {
			return false;
		}

		int agent = ag.get(random.nextInt(ag.size()));

		int i = random.nextInt(tempSolution[agent].size());
		int j = random.nextInt(tempSolution[agent].size() - 1);
		if (i <= j) {
			j++;
		}
		checkForAbandonment(i, tempSolution[agent]);
		checkForAbandonment(j, tempSolution[agent]);
		Collections.swap(tempSolution[agent], i, j);

		return true;
	}

	// Chooses randomly 2 agent and transform an assignment from agent 1 to
	// agent 2
	private boolean nextState2() {

		Vector<Integer> ag = new Vector<Integer>();
		for (int i = 0; i < allocation.length; i++) {
			if (allocation[i].size() > 0) {
				ag.add(i);
			}
		}

		int agent1 = ag.get(random.nextInt(ag.size()));

		int i = random.nextInt(tempSolution[agent1].size());
		checkForAbandonment(i, tempSolution[agent1]);
		Assignment old = tempSolution[agent1].remove(i);
		int agent2 = random.nextInt(policeUnits.size() - 1);

		if (agent1 <= agent2) {
			agent2++;
		}
		
//		int type = random.nextInt(old.getTask().getNumOfAgentTypesRequiered());
//		Iterator<AgentType> iter = old.getTask().getAgentsRequiered().keySet().iterator();
//		for (int j = 1; j < type; j++) {
//		    iter.next();
//		}
	 
		
		Assignment a = new Assignment(policeUnits.get(agent2), old.getTask(),
				old.getRatio(),old.getType());

		int j = random.nextInt(tempSolution[agent2].size() + 1);
		checkForAbandonment(j, tempSolution[agent2]);
		tempSolution[agent2].add(j, a);
		updateRatio(old.getTask(), tempSolution);

		return true;
	}

	// Chooses randomly 2 agents and switch 2 assignments between the agents
	private boolean nextState3() {

		Vector<Integer> ag = new Vector<Integer>();
		for (int i = 0; i < allocation.length; i++) {
			if (allocation[i].size() > 0) {
				ag.add(i);
			}
		}
		if (ag.size() < 2) {
			return false;
		}

		int ind1 = random.nextInt(ag.size());
		int agent1 = ag.get(ind1);

		int i = random.nextInt(tempSolution[agent1].size());
		checkForAbandonment(i, tempSolution[agent1]);
		Assignment old1 = tempSolution[agent1].remove(i);

		int ind2 = random.nextInt(ag.size() - 1);
		if (ind1 <= ind2) {
			ind2++;
		}
		int agent2 = ag.get(ind2);
	

		Assignment a = new Assignment(policeUnits.get(agent2), old1.getTask(),
				old1.getRatio(),old1.getType());

		int j = random.nextInt(tempSolution[agent2].size());
		checkForAbandonment(j, tempSolution[agent2]);
		Assignment old2 = tempSolution[agent2].remove(j);
		
		Assignment b = new Assignment(policeUnits.get(agent1), old2.getTask(),
				old2.getRatio(),old2.getType());

		i = random.nextInt(tempSolution[agent1].size() + 1);
		j = random.nextInt(tempSolution[agent2].size() + 1);

		checkForAbandonment(i, tempSolution[agent1]);
		checkForAbandonment(j, tempSolution[agent2]);

		tempSolution[agent1].add(i, b);
		tempSolution[agent2].add(j, a);
		updateRatio(old1.getTask(), tempSolution);
		updateRatio(old2.getTask(), tempSolution);
		return true;

	}

	// checks if the new state causes to abandonment
	private void checkForAbandonment(int i, Vector<Assignment> vector) {
		if (vector.isEmpty()) {
			return;
		}
		if ((i == 0 && vector.get(i).getTask() instanceof MissionEvent && !vector
				.get(i).getTask().equals(currentTask))) {
			f = 10000.0;
		}

	}

	// Checks duplicate assignments of the the same mission to same agent and
	// combines the assignments
	protected Vector<Assignment>[] checkDuplication(Vector<Assignment>[] all) {
		for (int i = 0; i < all.length; i++) {
			for (int j = 0; j < all[i].size(); j++) {
				int in = all[i].lastIndexOf(all[i].get(j));
				if (in > j) {
					Assignment a = all[i].remove(in);
					if(a.getType()==all[i].get(j).getType()){
						all[i].get(j).setRatio(
								all[i].get(j).getRatio() + a.getRatio());
					}else{
						all[i].add(j+1, a);
					}
				}
			}
		}
		return all;
		// return updateNumOfAgents(all);
	}

	// Updates the ratio division of the task according to heterogenic allocation
	private void updateRatio(Task task, Vector<Assignment>[] tempSolution) {
		TreeMap<AgentType, Vector<Assignment>> taskAssignments = new TreeMap<AgentType, Vector<Assignment>>();
		for (AgentType type : AgentType.values()) {
			Vector<Assignment> v = new Vector<Assignment>();
			taskAssignments.put(type, v);
		}
		for (int i = 0; i < tempSolution.length; i++) {
			for (Assignment a : tempSolution[i]) {
				if (a.getTask().equals(task)) {
					taskAssignments.get(a.getType()).add(a);
				}
			}
		}
		for (Map.Entry<AgentType, Vector<Assignment>> entry : taskAssignments
				.entrySet()) {
			double ratio = 0;
			if (entry.getValue().size() > 0 && task.isAgentTypeRequired(entry.getKey())) {
				if(task.getAgentsRequiered().get(entry.getKey())<entry.getValue().size()){
					System.out.println("assignment");
				}
				ratio = (task.getDurationDivision().get(entry.getKey()) / task
						.getWorkload()) / entry.getValue().size();
			}
			if(ratio > 1 && (ratio-1)<0.0000001){
				ratio = 1;
			}

			for (Assignment a : entry.getValue()) {
				a.setRatio(ratio);
			}

		}
	}
	// Updates for each task the number of agents that were allocated.
	/*
	 * private Vector<Assignment>[] updateNumOfAgents(Vector<Assignment>[] all)
	 * { for (int i = policeUnits.size(); i < tasks.size(); i++) { Task t =
	 * tasks.get(i); int agentsCount = 0; for (int j = 0; j < all.length; j++) {
	 * for (Assignment a : all[j]) { if (a.getTask().equals(t)) ; agentsCount++;
	 * } } t.setNumOfAllocatedAgents(agentsCount); } return all; }
	 */

}
