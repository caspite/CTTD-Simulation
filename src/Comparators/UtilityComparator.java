package Comparators;

import java.util.Comparator;

import TaskAllocation.*;;

public class UtilityComparator implements Comparator<Assignment>{
	public static final UtilityComparator com=new UtilityComparator();
	@Override
	public int compare(Assignment arg0, Assignment arg1) {
		if(arg0.getFisherUtility()>arg1.getFisherUtility()){
		return 1;
		}
		else if(arg0.getFisherUtility()<arg1.getFisherUtility()){
			return -1;
		}
		return 0;
	}

}
