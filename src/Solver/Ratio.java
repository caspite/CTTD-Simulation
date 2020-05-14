package Solver;

public class Ratio implements Comparable<Ratio>{

	private double ratio;
	private int AgentId;

	public int getAgentId() {
		return AgentId;
	}

	public double getRatio() {
		return ratio;
	}

	public void setRatio(double d) {
		this.ratio = d;
	}

	public Ratio(double ratio, int id) {
		super();
		this.ratio = ratio;
		this.AgentId = id;
	}

	@Override
	public String toString() {
		return "Ratio [ratio=" + ratio + "]";
	}

	@Override
	public int compareTo(Ratio o) {
		if(ratio < o.ratio){
			return 1;
		}else if(ratio > o.ratio)
			return -1;
		return 0;
	}
 
	
	
	

}
