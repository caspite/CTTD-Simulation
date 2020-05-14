package Solver;

import java.util.*;

import PoliceTaskAllocation.*;
import TaskAllocation.*;

public class DBASolver extends Solver {

	protected Vector<Assignment>[] allocation;
	private DBAUtility[][] inputForDBAAllocation;
	private int[] numOfagentsForAllocation;

	public DBASolver(DBAUtility[][] input, TaskOrdering taskOrdering2,
			Vector<Task> tasks) {
		super(input, taskOrdering2, tasks);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Vector<Assignment>[] solve() {
		createDBAInput();
		calculateProbability();
		createAllocation();
		return taskPrioritization(allocation);
	}

	private void createAllocation() {
		allocation = new Vector[input.length];
		// empty allocation
		for (int i = 0; i < allocation.length; i++) {
			allocation[i] = new Vector<Assignment>();
		}
		int index = 0;
		// allocate agents to missions
		for (int j = 0; j < tasks.size(); j++) {
			Task task = tasks.get(j);
			HashMap<AgentType, Integer> AgentsRequired = tasks.get(j)
					.getAgentsRequiered();
			if (task instanceof PatrolEvent) {
				AgentsRequired = new HashMap<AgentType, Integer>();
				AgentsRequired.put(AgentType.TYPE1, 1);
			}
			// allocates agents to mission j according to AgentsRequired
			HashSet<Integer> allocatedAgentsToMission = new HashSet<Integer>();
			for (Map.Entry<AgentType, Integer> entry : AgentsRequired
					.entrySet()) {
				// allocates required number of agents subTask
				List<Ratio> all = new ArrayList<Ratio>();
				for (int t = 0; t < entry.getValue(); t++) {
					int i = pickRandomAgent(index,allocatedAgentsToMission) - 1;
					if (i >= 0) {
						all.add(new Ratio(inputForDBAAllocation[i][index]
								.getDBAProbability(), i));
						allocatedAgentsToMission.add(i);
						inputForDBAAllocation[i][index] = null;
					}
					calculateProbability(index);
				}
				Collections.sort(all);
				all = createAllocationForMission(all, entry.getKey(), task);

				for (Ratio e : all) {
					int an = e.getAgentId();
					Assignment as = new Assignment(input[an][j].getAgent(),
							input[an][j].getTask(), e.getRatio(),
							entry.getKey());
					as.setFisherUtility(input[an][j].getUtility(1));
					allocation[an].add(as);
				}
				index++;
			}

		}
	}

	private int pickRandomAgent(int task, HashSet<Integer> allocatedAgentsToMission) {
		Vector<DBAUtility> vec = new Vector<DBAUtility>();
		for (int i = 0; i < inputForDBAAllocation.length; i++) {
			if (inputForDBAAllocation[i][task] != null 
					&& !allocatedAgentsToMission.contains(i)) {
				vec.add((DBAUtility) inputForDBAAllocation[i][task]);
			}
		}
		Collections.sort(vec);
		Collections.reverse(vec);

		double random = Math.random();
		double sum = 0;
		for (Iterator iterator = vec.iterator(); iterator.hasNext();) {
			DBAUtility dbaUtility = (DBAUtility) iterator.next();
			sum += dbaUtility.getDBAProbability();
			if (sum > random) {
				return dbaUtility.getAgent().getId();
			}
		}
		return -1;
	}

	/**
	 * Calculate for agent the probability for allocation of each task
	 */

	private void calculateProbability() {
		for (int j = 0; j < inputForDBAAllocation[0].length; j++) {
			calculateProbability(j);
		}

	}

	private void calculateProbability(int task) {
		double sum = 0;
		for (int i = 0; i < inputForDBAAllocation.length; i++) {
			if (inputForDBAAllocation[i][task] != null) {
				sum = sum + inputForDBAAllocation[i][task].getUtility(1);
			}
		}
		for (int i = 0; i < inputForDBAAllocation.length; i++) {
			if (inputForDBAAllocation[i][task] != null) {
				((DBAUtility) inputForDBAAllocation[i][task])
						.setDBAProbability(inputForDBAAllocation[i][task]
								.getUtility(1) / sum);

			}
		}
	}

	protected void createDBAInput() {
		int taskForAllocation = countTaskForInput();
		inputForDBAAllocation = new DBAUtility[input.length][taskForAllocation];
		for (int i = 0; i < input.length; i++) {
			int index = 0;
			for (int j = 0; j < input[0].length; j++) {
				if (input[i][j] != null) {
					Agent a = input[i][j].getAgent();
					Task t = input[i][j].getTask();
					if (t instanceof MissionEvent) {
						for (Map.Entry<AgentType, Integer> entry : t
								.getAgentsRequiered().entrySet()) {
							if (a.isAgentTypeOf(entry.getKey())) {
								inputForDBAAllocation[i][index] = ((DBAUtility) input[i][j]).clone();
							} else {
								inputForDBAAllocation[i][index] = null;
							}
							index++;
						}
					} else {
						inputForDBAAllocation[i][index] = (DBAUtility) input[i][j];
						index++;
					}
				} else {
					index = index + tasks.get(j).getAgentsRequiered().size();
				}
			}
		}
	}

	private int countTaskForInput() {
		int sum = 0;
		numOfagentsForAllocation = new int[tasks.size()];
		for (int i = 0; i < tasks.size(); i++) {
			sum = sum + tasks.get(i).getNumOfAgentTypesRequiered();
			numOfagentsForAllocation[i] = tasks.get(i)
					.getNumOfAgentTypesRequiered();
		}
		return sum;
	}

	private List<Ratio> createAllocationForMission(List<Ratio> all,
			AgentType type, Task task) {
		double sum = 0;
		for (Ratio e : all) {

			sum = sum + e.getRatio();
		}
		double portion = task.getDurationDivision().get(type)
				/ task.getDuration();
		for (Ratio e : all) {
			e.setRatio((e.getRatio() / sum) * portion);
		}
		return all;

	}

}
