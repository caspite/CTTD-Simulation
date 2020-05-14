package Comparators;

import java.util.Comparator;

import TaskAllocation.*;

/*
 * Compare between 2 assignments according to highest type 
 * and the according to newest arrival time.
 */

public class TypeTimeComparator implements Comparator<Assignment> {
	public static final TypeTimeComparator com = new TypeTimeComparator();

	@Override
	public int compare(Assignment arg0, Assignment arg1) {
		if (arg0.getTask().getPriority() < arg1.getTask().getPriority()) {
			return 1;
		} else if (arg0.getTask().getPriority() > arg1.getTask().getPriority()) {
			return -1;
		}
		if (arg0.getTask().getMissionArrivalTime() < arg1.getTask().getMissionArrivalTime()) {
			return 1;
		} else if (arg0.getTask().getMissionArrivalTime() > arg1.getTask()
				.getMissionArrivalTime()) {
			return -1;
		}
		return 0;
	}

}
