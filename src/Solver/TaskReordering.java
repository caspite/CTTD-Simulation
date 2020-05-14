package Solver;

import java.util.*;
import Comparators.*;
import Helpers.*;
import TaskAllocation.*;

public class TaskReordering extends TaskOrdering {
	protected Vector<Assignment>[] newAllocation;
	
	
	public TaskReordering() {
		super();
	}

	@Override
	public Vector<Assignment>[] TaskPrioritization(
			Vector<Assignment>[] allocation) {
		newAllocation= new Vector[allocation.length];
		for (int i = 0; i < allocation.length; i++) {
			Collections.sort(allocation[i], UtilityComparator.com);
			Collections.reverse(allocation[i]);
			newAllocation[i].add(allocation[i].get(0));
			
			while(!allocation[i].isEmpty()){
				Assignment a=findNextOrder(allocation[i].remove(0), allocation[i]);	
				newAllocation[i].add(a);
			}
			
		} 
		return allocation;
	}
	
	// finds the next closest task to current task r
	private Assignment findNextOrder(Assignment r, Vector<Assignment> vector) {
		URLConnectionReader url= new URLConnectionReader();
		double distance=10000000;
		Assignment as=vector.get(0);
		for (Assignment a : vector) {
			double tempDistance=url.getDemoDistance(r.getTask().getLocation(), a.getTask().getLocation());
			if(tempDistance<distance){
				distance=tempDistance;
				as=a;
			}		
		}
		return as;		
	}

}
