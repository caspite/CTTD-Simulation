package Comparators;

import java.util.Comparator;

import TaskAllocation.*;;

/*
 * compares between 2 assignments according to fisher utility/reminded workload 
 */
public class UtilityBPBComparator implements Comparator<Assignment>{
	public static final UtilityBPBComparator com=new UtilityBPBComparator();
	@Override
	
	public int compare(Assignment arg0, Assignment arg1) {
		if(arg0.getFisherUtility()/(arg0.getTask().getWorkload()*arg0.getRatio())>arg1.getFisherUtility()/(arg1.getTask().getWorkload()*arg1.getRatio())){
		return 1;
		}
		else if(arg0.getFisherUtility()/(arg0.getTask().getWorkload()*arg0.getRatio())<arg1.getFisherUtility()/(arg1.getTask().getWorkload()*arg1.getRatio())){
			return -1;
		}
		return 0;
	}

}
