package Comparators;

import java.util.Comparator;

import TaskAllocation.Assignment;

public class PersonalUtilityComparator implements Comparator<Assignment> {
	public static final PersonalUtilityComparator com=new PersonalUtilityComparator();
	@Override
	public int compare(Assignment arg0, Assignment arg1) {
		if(arg0.getFisherUtility()*arg0.getRatio()>arg1.getFisherUtility()*arg1.getRatio()){
			return 1;
			}
			else if(arg0.getFisherUtility()*arg0.getRatio()<arg1.getFisherUtility()*arg1.getRatio()){
				return -1;
			}
			return 0;
	}


}
