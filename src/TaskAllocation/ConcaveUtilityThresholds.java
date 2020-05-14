package TaskAllocation;

public class ConcaveUtilityThresholds extends LinearUtilityThreshold {
	private double ro;
	public ConcaveUtilityThresholds(Agent agent, Task task, double Tnow,double ro) {
		super(agent, task, Tnow);
		this.ro=ro;
	}


	public ConcaveUtilityThresholds() {
		super();
	}


	public double getUtility(double ratio) {// returns related part of the utility
		return Math.pow(ratio*linearUtility,ConcaveUtility.ro);
	}
	
	public double getLinearUtility(){
		return linearUtility;
	}
	
	@Override
	public Object clone() {
		ConcaveUtilityThresholds l=new ConcaveUtilityThresholds();
		l.linearUtility=linearUtility;
		return l;
	}

}
