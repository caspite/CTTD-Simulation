package TaskAllocation;


public abstract class Utility {
	
	public static final double ditanceFactor=1;
	public static final double timeUnit=60;
	public static final double DF=0.95;
	public static final double abandFactor=0.5;
	public static final int minAbanPenalty=30;
	protected Agent agent;
	protected Task task;
	public Agent getAgent() {
		return agent;
	}
	public Task getTask() {
		return task;
	}
	public Utility(){
		super();
	}
	public Utility(Agent agent, Task task) {
		super();
		this.agent = agent;
		this.task = task;
	}
	

	
	public abstract void calculateParameters(double Tnow);
	public abstract double getUtility(double ratio);	
	public abstract Object clone();
}
