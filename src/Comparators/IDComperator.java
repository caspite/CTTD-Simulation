package Comparators;

import java.util.Comparator;

import TaskAllocation.Assignment;


public class IDComperator implements Comparator<Assignment> {
	public static final IDComperator com=new IDComperator();
	@Override
	public int compare(Assignment arg0, Assignment arg1) {
		if(arg0.getAgent().getId()>arg1.getAgent().getId()){
		return 1;
		}
		else if(arg0.getAgent().getId()<arg1.getAgent().getId()){
			return -1;
		}
		return 0;
	}

}
