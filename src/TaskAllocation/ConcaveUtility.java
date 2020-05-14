package TaskAllocation;

public class ConcaveUtility extends LinearUtility {
	static public double ro;

	public ConcaveUtility(Agent agent, Task task,
			double Tnow,double ro) {
		super(agent, task, Tnow);
		this.ro=ro;
		
	}
	public ConcaveUtility(double linearUtility,double ro){
		super(linearUtility);
		this.ro=ro;
	}
	
	public double getUtility(double ratio) {// returns related part of the utility
		return Math.pow(ratio*linearUtility,ro);
	}
	
	public double getLinearUtility(){
		return linearUtility;
	}
	
	@Override
	public Object clone() {
		ConcaveUtility l=new ConcaveUtility( linearUtility,ro);
		return l;
	}




}
