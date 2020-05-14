package Solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import Helpers.WriteToFile;
import PoliceTaskAllocation.*;
import TaskAllocation.*;
import fisher.*;


public class FisherSolverHetro extends FisherSolver {

	private Utility[][] inputForFisherAllocation;
	private int[] numOfagentsForAllocation;

	public FisherSolverHetro(Utility[][] input, TaskOrdering taskOrdering,
			Vector<Task> tasks) {
		super(input, taskOrdering, tasks);
	}

	public Vector<Assignment>[] solve() {
		createHetroFisherInput();
		//WriteToFile.writeUtilitiesToFile(inputForFisherAllocation);
		FisherDistributed f2 = new FisherDistributed(inputForFisherAllocation);
		creatFisherSolution(f2.algorithm());
		return taskPrioritization(allocation);
	}

	private int countTaskForFisherInput() {
		int sum = 0;
		numOfagentsForAllocation = new int[tasks.size()];
		for (int i = 0; i < tasks.size(); i++) {
				sum = sum + tasks.get(i).getNumOfAgentTypesRequiered();
				numOfagentsForAllocation[i] = tasks.get(i)
						.getNumOfAgentTypesRequiered();
		}
		return sum;
	}

	protected void createHetroFisherInput() {
		int taskForAllocation = countTaskForFisherInput();
		inputForFisherAllocation = new Utility[input.length][taskForAllocation];
		for (int i = 0; i < input.length; i++) {
			int index = 0;
			for (int j = 0; j < input[0].length; j++) {
				if (input[i][j] != null) {
				Agent a = input[i][j].getAgent();
				Task t = input[i][j].getTask();
				if (t instanceof MissionEvent) {
					for (Map.Entry<AgentType, Integer> entry : t.getAgentsRequiered().entrySet()) {
						if (a.isAgentTypeOf(entry.getKey())) {
							inputForFisherAllocation[i][index] = input[i][j];
						} else {
							inputForFisherAllocation[i][index] = null;
						}
						index++;
					}
				} else {
					inputForFisherAllocation[i][index] = input[i][j];
					index++;
				}
			}else{
					index = index + tasks.get(j).getAgentsRequiered().size();
				}
			}
		}
	}

	protected Vector<Assignment>[] creatFisherSolution(Double[][] output) {
		allocation = new Vector[output.length];
		for (int i = 0; i < allocation.length; i++) {
			allocation[i] = new Vector<Assignment>();
		}
		// sort the fraction allocation to task j
		int index = 0;
		for (int j = 0; j < tasks.size(); j++) {
			Task t = tasks.get(j);
			HashMap<AgentType, Integer> AgentsRequired = tasks.get(j)
					.getAgentsRequiered();
			if(t instanceof PatrolEvent){
				AgentsRequired = new HashMap<AgentType, Integer>();
				AgentsRequired.put(AgentType.TYPE1, 1);
			}
			
			for (Map.Entry<AgentType, Integer> entry : AgentsRequired
					.entrySet()) {
				List<Ratio> all = new ArrayList<Ratio>();
				for (int i = 0; i < output.length; i++) {
					if (output[i][index] != null && output[i][index] > minValue) {
						all.add(new Ratio(output[i][index], i));
					}
				}
				Collections.sort(all);
				// allocates only the required number of units

				while (all.size() > entry.getValue()) {
					all.remove(all.size() - 1);
				}

				all = createAllocationForMission(all, entry.getKey(),
						t);

				for (Ratio e : all) {
					int an = e.getAgentId();
					Assignment as = new Assignment(
							inputForFisherAllocation[an][index].getAgent(),
							inputForFisherAllocation[an][index].getTask(),
							e.getRatio(),entry.getKey() );
					as.setFisherUtility(input[an][j].getUtility(1));
					allocation[an].add(as);
				}
				index++;
			}
		}

		return allocation;
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
