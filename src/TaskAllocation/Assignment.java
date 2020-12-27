package TaskAllocation;

import CTTD.*;
import PoliceTaskAllocation.AgentType;
import PoliceTaskAllocation.MissionEvent;
import PoliceTaskAllocation.PoliceUnit;
import PoliceTaskAllocation.Status;

import java.util.*;

public class Assignment {

	private double ratio;
	private Agent agent;// agent for the task
	private Task task;
	private AgentType type;
	public boolean agentContribute;// did the contribute reduce the remain cover?-static assignment
	public double contribution;//the agent contribution to the task-static assignment

	public Assignment(MedicalUnit medicalUnit, Task task, double contribution, double arrivalTime) {
		super();
		this.contribution = contribution;
		this.agent = medicalUnit;
		this.task = task;
		this.arrivalTime = arrivalTime;
	}

	public Assignment(PoliceUnit pu, Task task, double contribution, AgentType agentType) {
		super();
		this.contribution = contribution;
		this.agent = pu;
		this.task = task;

	}

	public Assignment(PoliceUnit pu, Task task, double ratio) {
		super();
		this.ratio = ratio;
		this.agent = pu;
		this.task = task;

	}

	public Assignment(Agent agent, Task task, double contribution, AgentType agentType) {
		super();
		this.contribution = contribution;
		this.agent = agent;
		this.task = task;
		this.arrivalTime = arrivalTime;
	}

	public AgentType getType() {
		return type;
	}

	private double endTime; // estimated end time
	private double arrivalTime;// the time that the agent arrived to task
	private double fisherUtility;
	private double duration;//the assignment contribution
	private Vector <Skill> agentAssignment;//the specific allocation
	private Activity activity;

	public Assignment(Agent agent, Task task, double contribution, double arrivalTime) {
		super();
		this.contribution = contribution;
		this.agent = agent;
		this.task = task;
		this.arrivalTime = arrivalTime;
	}


	@Override
	public String toString() {

		return "Allocating [ agent=" + agent.id + ", task="
				+ task.id + " arrival time: " + arrivalTime + "contribution: " + contribution + " ]";
	}

	//------------------------getters and setters----------------------------------------//
	public double getRatio() {
		return ratio;
	}

	public Agent getAgent() {
		return agent;
	}

	public Task getTask() {
		return task;
	}

	public double getEndTime() {
		return endTime;
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

	public void setActivity(Activity act) {
		this.activity = act;
	}

	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public void setEndTime(double endTime) {
		if (this.endTime < endTime) {
			this.endTime = endTime;
			duration=endTime-arrivalTime;
		}
	}

	public void setContribution(double contribution) {
		this.contribution = contribution;
	}

	@Override
	//?????
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		result = prime * result + ((task == null) ? 0 : task.hashCode());
		return result;
	}

	public Assignment clone() {
		Assignment a = new Assignment(agent, task, ratio, arrivalTime);
		a.arrivalTime = arrivalTime;
		return a;
	}

	public void activitiesAssignment() {
		if (this.getTask() instanceof DisasterSite) {
			Vector <Skill> actAs = ((DisasterSite) this.getTask()).activitiesAssignment(this);
			agentAssignment=new Vector<>();
			agentAssignment.addAll(actAs);
			if(agentAssignment.size()<=0){
				getAgent().setStatus(Status.WAITING);
			}
		}
		this.calculateDuration();

		setEndTime(arrivalTime + duration);
	}

	private void calculateDuration() {
		List<Skill> activities = new ArrayList<Skill>(agentAssignment);
		for (int i = 0; i < activities.size(); i++) {
			duration += activities.get(i).getDuration();
		}
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

	public double getDuration(){
		return duration;
	}

	public double BPB1(double tnow) {
		double dis = agent.getDistance(task);
		if (task instanceof MissionEvent) {
			return fisherUtility / (task.getWorkload() * ratio + dis);
		} else {
			return (1.0 - (dis / (28880 - tnow))) * (task.getCurrentUtility() / task.getWorkload());
		}
	}

}