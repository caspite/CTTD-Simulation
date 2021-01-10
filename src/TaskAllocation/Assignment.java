package TaskAllocation;

import CTTD.*;
import PoliceTaskAllocation.AgentType;
import PoliceTaskAllocation.MissionEvent;
import PoliceTaskAllocation.PoliceUnit;
import PoliceTaskAllocation.Status;

import java.util.*;

import static PoliceTaskAllocation.Status.MOVING;
import static PoliceTaskAllocation.Status.WAITING;

public class Assignment {

	private double ratio;
	private Agent agent;// agent for the task
	private Task task;
	private AgentType type;
	public double contribution;//the agent contribution to the task-static assignment

	private double endTime; // estimated end time
	private double arrivalTime;// the time that the agent arrived to task
	private double fisherUtility;
	private double duration;//the assignment contribution
	private Vector <Skill> agentAssignment;//the specific allocation
	private TreeMap<Double,Skill> schedule =new TreeMap<>();//allocation order start time + skill - update - remove skill after execution

	private Activity activity;
	private double estimateUtility; // the estimate utility for assignment
	private double utility;//the real utility
	private double penalty;

	//---------------------------------------constructor-------------------------------------//

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
		this.type=agentType;

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




	public Assignment(Agent agent, Task task, double contribution, double arrivalTime) {
		super();
		this.contribution = contribution;
		this.agent = agent;
		this.task = task;
		this.arrivalTime = arrivalTime;
	}


//------------------------------------------------------------------------------------------//


	//------------------------getters and setters----------------------------------------//
	public double getRatio() {
		return ratio;
	}

	public AgentType getType() {
		return type;
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

	public double getFisherUtility() {
		return fisherUtility;
	}//not in use CTTD

	public void setFisherUtility(double fisherUtility) {
		this.fisherUtility = fisherUtility;
	}//not in use CTTD

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
	}//not in use CTTD

	public void setContribution(double contribution) {
		this.contribution = contribution;
	}

//-----------------------------------------------------------------------------------------------//

//-------------------------- Activities Assignments---------------------------------------------//

	public void activitiesAssignment() {
		if (this.getTask() instanceof DisasterSite) {
			TreeMap<Double,Skill> actAs = ((DisasterSite) this.getTask()).activitiesAssignment(this);
			schedule=new TreeMap<>();
			schedule.putAll(actAs);
			if(schedule.size()<=0){
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


//------------------------------------------------------------------------------------------//

//---------------------------- update assignment-----------------------------------------//

	public void updateAs(double tnow){
		//clear penalty
		this.penalty=0;

		//loop all schedule- check finished, middle,
		for (Map.Entry<Double,Skill> entry:schedule.entrySet()){
			//if execution begin
			if(entry.getKey()<tnow){
				//if finished
				if(entry.getKey()+entry.getValue().getDuration()<=tnow)
					finishedExecution((Execution)entry.getValue(),entry.getValue().getDuration());
				//if in middle
				else if (entry.getKey()+entry.getValue().getDuration()>tnow){
					//calc ratio
					double ratio=(tnow-entry.getKey()/entry.getValue().getDuration());
					stopExecution((Execution)entry.getValue(),ratio);

				}
			}
				// if not started
				else
					updatePenalty((Execution)entry.getValue());
		}

		//update task - remove this skill from demands and update casualty
		//else - do nothing! ( can't stop in the middle.)

	}

	private void finishedExecution(Execution execution,double endTime){
		//close execution
		execution.setFinished(true);
		//update casualty status+activity
		Casualty casualty=execution.getCas();
		casualty.setActivity(execution.getActivity());
		casualty.setStatus(casualty.getStatus());

		//reduce agent capacity
		((MedicalUnit)this.agent).reduceCapacity(execution);

		//reduce disaster site demands
		((DisasterSite)this.task).reduceDemands(execution);


		//if activity is uploading- update agent loaded casualties
		if(execution.getActivity()==Activity.UPLOADING){
			((MedicalUnit)this.agent).setCasualties(casualty);
			((DisasterSite)this.task).reduceCasualties(casualty);
		}
		//if task is hospital task
		if(this.task instanceof Hospital){
		casualty.setFiniteSurvival(endTime);
		((MedicalUnit) this.agent).reloadCapacity();
		this.agent.setStatus(WAITING);
		//TODO - Update indices
		}

	}
	private void stopExecution(Execution execution,double ratio){
		execution.setPenalty((1-ratio)*execution.getUtility());
		//update assignment penalty for abandoned
		this.penalty+=execution.getPenalty();


	}
	private void updatePenalty(Execution execution){
		this.penalty+=execution.getPenalty();

	}


//----------------------------- general methods------------------------------------------//

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

	@Override
	public String toString() {

		return "Allocating [ agent=" + agent.id + ", task="
				+ task.id + " arrival time: " + arrivalTime + "contribution: " + contribution + " ]";
	}



//-------------------------------------------------------------------------------------------//
}