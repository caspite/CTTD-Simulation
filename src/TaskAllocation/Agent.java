package TaskAllocation;

import java.io.Serializable;
import java.util.*;

import CTTD.Activity;
import CTTD.Triage;
import CTTD.TriageActivity;
import Helpers.URLConnectionReader;
import PoliceTaskAllocation.*;


public class Agent implements Distancable, Serializable {

	protected Location location;// current location of the agent
	protected int id;// agents id
	protected boolean onTheWay;
	protected double startMovingTime;
	protected double movingTime;
	protected boolean isWaiting;
	protected transient Assignment currentTask;// / his current task
	protected int currentTaskID;
	protected double speed=60;
	protected Status status;//
	private TreeMap<Integer,TriageActivity> skills;
	//For CTTD agent Type is the agets' Type - not the capabilities
	protected HashSet<AgentType> agentType; // agents' capabilities

	public Agent(Location location, int id, HashSet<AgentType> agentType) {
		super();
		this.location = location;
		this.id = id;
		this.agentType = agentType;
		this.currentTask =  null;
		this.speed = 60;
	}
	public Agent(){};
	public Agent(int id, HashSet<AgentType> agentType) {
		this(null, id, agentType);
		this.speed = 60;
	}

	public HashSet<AgentType> getAgentType() {
		return agentType;
	}
	
	public boolean isAgentTypeOf(AgentType type){
		return agentType.contains(type);
	}

	public boolean isWaiting() {
		return isWaiting;
	}

	public void setWaiting(boolean isWaiting) {
		this.isWaiting = isWaiting;
	}

	public Assignment getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(Assignment currentTask) {
		this.currentTask = currentTask;
		this.currentTaskID = currentTask.getTask().getId();
	}

	public boolean isOnTheWay() {
		return onTheWay;
	}

	public void setOnTheWay(boolean onTheWay) {

		this.onTheWay = onTheWay;

	}

	public Location getLocation() {

		return location;

	}

	public double getSpeed(){
		return speed;
	}
	public void setLocation(double Tnow) {
		if (!onTheWay)
			return;
		Location TaskLocation = currentTask.getTask().location;
		double p = (Tnow - startMovingTime) / movingTime;
		double randLng = (TaskLocation.getLng() - location.getLng()) * p;
		double randLtd = (TaskLocation.getLat() - location.getLat()) * p;
		location = new Location(location.getLat() + randLtd, location.getLng()
				+ randLng);
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Agent [location=" + location + ", id=" + id + " type "+agentType+"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Agent))
			return false;
		Agent other = (Agent) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public double getDistance(Distancable d) {
		URLConnectionReader u = new URLConnectionReader();
		double distance = u.getDemoDistance2(d.getLocation(), location);
		return (distance / 40.0) * 3600;
	}

	public Object clone() {
		Agent a = new Agent(location, id, agentType);
		a.setCurrentTask(currentTask);
		return a;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public double getStartMovingTime() {
		return startMovingTime;
	}

	public void setStartMovingTime(double startMovingTime) {
		this.startMovingTime = startMovingTime;
	}

	public double getMovingTime() {
		return movingTime;
	}



	public void setMovingTime(double movingTime) {
		this.movingTime = movingTime;
	}

	public int getCurrentTaskID() {
		return currentTaskID;
	}

public double getActivityTime(Triage trg, Activity act){
		return 0;

}

}
