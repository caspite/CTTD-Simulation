package Solver;

import java.util.Collections;
import java.util.Vector;

import Comparators.*;
import TaskAllocation.Assignment;

public class GreedyOrdering extends TaskOrdering {

	public GreedyOrdering() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Vector<Assignment>[] TaskPrioritization(Vector<Assignment>[] allocation){
		for (int i = 0; i < allocation.length; i++) {
			Collections.sort(allocation[i],UtilityBPBComparator2.com);
			Collections.reverse(allocation[i]);
		}
		
		return allocation;
	}

}
