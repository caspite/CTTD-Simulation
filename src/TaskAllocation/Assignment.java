package TaskAllocation;

import PoliceTaskAllocation.AgentType;
import PoliceTaskAllocation.MissionEvent;
import PoliceTaskAllocation.PoliceUnit;

public class Assignment {

	private double ratio;
	private Agent agent;// agent for the task
	private Task task;
	private AgentType type;

	public Assignment(PoliceUnit policeUnit, Task task, double v) {
		super();
		this.ratio = ratio;
		this.agent = agent;
		this.task = task;
		this.arrivalTime = -1;
	}

	public AgentType getType() {
		return type;
	}

	private double endTime; // estimated end time
	private double arrivalTime;// the time that the agent arrived to task
	private double fisherUtility;

	public Assignment(Agent agent, Task task, double ratio, AgentType type) {
		super();
		this.ratio = ratio;
		this.agent = agent;
		this.task = task;
		this.arrivalTime = -1;
		this.type = type;
	}

	@Override
	public String toString() {

		return "Allocating [ratio=" + ratio + ", agent=" + agent.id + ", task="
				+ task.id + " "+type+ "]";
	}

	// /Getters and Setters
	public double getRatio() {
		return ratio;
	}

	public Agent getAgent() {
		return agent;
	}

	public Task getTask() {
		return task;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		result = prime * result + ((task == null) ? 0 : task.hashCode());
		return result;
	}

	public Assignment clone() {
		Assignment a = new Assignment(agent, task, ratio,type);
		a.arrivalTime = arrivalTime;
		return a;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Assignment))
			return false;
		Assignment other = (Assignment) obj;
		if (agent == null) {
			if (other.agent != null)
				return false;
		} else if (!agent.equals(other.agent))
			return false;
		if (task == null) {
			if (other.task != null)
				return false;
		} else if (!task.equals(other.task))
			return false;
		return true;
	}

	public double getFisherUtility() {
		return fisherUtility;
	}

	public void setFisherUtility(double fisherUtility) {
		this.fisherUtility = fisherUtility;
	}
	
	public double BPB1(double tnow){
		double dis = agent.getDistance(task);
		if(task instanceof MissionEvent){
			return fisherUtility/(task.getWorkload()*ratio+dis);
		}
		else{
			return (1.0 - (dis/(28880-tnow)))*(task.getCurrentUtility()/task.getWorkload());
		}
	}

}
