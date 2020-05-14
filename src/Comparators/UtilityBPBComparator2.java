package Comparators;

import java.util.Comparator;

import TaskAllocation.*;;

/*
 * compares between 2 assignments according to fisher utility/(reminded workload+ dis)
 */
public class UtilityBPBComparator2 implements Comparator<Assignment>{
	public static final UtilityBPBComparator2 com=new UtilityBPBComparator2();
	public static double tnow = 0;
	@Override
	
	public int compare(Assignment arg0, Assignment arg1) {
		if(arg0.BPB1(tnow)>arg1.BPB1(tnow)){
		return 1;
		}
		else if(arg0.BPB1(tnow)<arg1.BPB1(tnow)){
			return -1;
		}
		return 0;
	}

}
