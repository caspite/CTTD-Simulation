package TaskAllocation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import Helpers.URLConnectionReader;
import PoliceTaskAllocation.AgentType;
import PoliceTaskAllocation.MainSimulationForThreads;
import PoliceTaskAllocation.PoliceUnit;

public class Task implements Distancable, Serializable, Comparable<Task>, Messageable{
	protected Location location;// the location of the mission

	protected double totalDuration;// the remaining duration of the mission
	protected double workload;// remained workload (remained time)

	protected  TreeMap<AgentType, Double> durationDivision;
	protected double missionArrivalTime;// when the mission arrives to the sysytem
	protected double freezTime;// when the mission was abandoned
	protected double DFTime;// how long the mission waits to handling
	protected double hardConstraintTime;// The latest time. The can start
										// before.

	protected int id;// mission's id
	protected double utility;// gross utility from performing task
	protected TreeMap<Integer, Double> PLUtility;// Piecewise linear utility
	protected int priority;// priority of the mission, 1 is the highest priority
	protected double SW = 0;
	
	// Agents that are working on the task currently
	protected transient Vector<Assignment> agents;
	protected int numOfAllocatedAgents;
	protected HashMap<AgentType, Integer> allocatedAgents;
	
	//Required agents to the mission
	protected int numOfAgentsRequired;
	protected HashMap<AgentType, Integer> agentsRequiered;
	
	// Saves working times and number of agents on the mission
	protected TreeMap<Integer, Double> workingTime;
	protected double lastChange; // time of the last change in the mission
	protected boolean isAllocated = false;// checks if mission started and was abandoned
	protected boolean isStarted = false;// checks if mission started

	// for FMC_TA CA
	protected Map<PoliceUnit,Double>bidsRecieved;
	protected Mailer mailer;
	protected int decisionCounter;
	protected Map<PoliceUnit,Message> messageRecived;
	protected double taskChange;
	protected Map<PoliceUnit,Double> allocation;
	
	public Task(double duration, int id, int priority) {
		super();
		this.totalDuration = duration;
		this.id = id;
		this.priority = priority;
		agents = new Vector<Assignment>();

	}

	public Task(Location location,int id) {
		super();
		this.location = location;
		this.id = id;
	}

	public Task(Location location, double duration, double startTime, int id,
			int priority, double utility,
			HashMap<AgentType, Integer> agentsRequired) {
		super();
		this.location = location;
		this.totalDuration = duration;
		this.missionArrivalTime = startTime;
		this.id = id;
		this.priority = priority;
		this.workload = duration;
		this.freezTime = startTime;
		this.DFTime = -1;
		this.agents = new Vector<Assignment>();
		this.utility = utility;
		this.workingTime = new TreeMap<Integer, Double>();
		this.agentsRequiered = agentsRequired;
		this.allocatedAgents = new HashMap<AgentType, Integer>();
		this.workingTime.put(0, 0.0);
		for (Map.Entry<AgentType, Integer> num : agentsRequired.entrySet()) {
			numOfAgentsRequired = numOfAgentsRequired + num.getValue();
			allocatedAgents.put(num.getKey(), 0);			
		}
		for (int i = 1; i <= numOfAgentsRequired; i++) {
			this.workingTime.put(i, 0.0);
		}
		this.durationDivision  = new TreeMap<AgentType, Double>();
		for (Map.Entry<AgentType, Integer> en : agentsRequired.entrySet()) {
			durationDivision.put(en.getKey(), workload*en.getValue()/numOfAgentsRequired);
		}

		createsPLUtility();
		lastChange = startTime;
	}

	private void createsPLUtility() {
		PLUtility = new TreeMap<Integer, Double>();
		for (int i = 1; i < numOfAgentsRequired; i++) {

			this.PLUtility.put(i,
					((double) (i) / ((double) (numOfAgentsRequired)+1 ))
							* utility);
		}
		this.PLUtility.put(numOfAgentsRequired, utility);
		// this.PLUtility.put(agentsRequiered,utility);

	}

	private void updateAllocatedAgents(AgentType type, int i) {
		if (allocatedAgents.containsKey(type)) {
			allocatedAgents.put(type, allocatedAgents.get(type) + i);
			numOfAllocatedAgents += i;
		}	
	}

	public int getNumOfAllocatedAgents() {
		return numOfAllocatedAgents;
	}

	public Location getLocation() {
		return location;
	}

	public double getDuration() {
		return totalDuration;
	}

	public int getId() {
		return id;
	}

	// A new agent arrives to the task. If it the first agent calculates DF time
	public void addAgent(Assignment a, double tnow) {
		AgentType type = a.getType();
		if(agentsRequiered.containsKey(type)){
			if(agentsRequiered.get(type)>allocatedAgents.get(type)){
				updateWorkingTimes(tnow);
				agents.add(a);
				updateAllocatedAgents(type,1);
			}
		}
		
		/*
		 * if(agents.size()==numOfAllocatedAgents){ arrivalTime = tnow; }
		 */
		if (DFTime == -1) {
			DFTime = (tnow - freezTime);
		}
	}

	// Updates working times in every status change
	private void updateWorkingTimes(double tnow) {
		double time = workingTime.get(numOfAllocatedAgents) + (tnow - lastChange);
		workingTime.put(numOfAllocatedAgents, time);
		lastChange = tnow;
	}

	// Updates mission status after an agent finished his part of the mission
	// removes agent after he finished his part
	public void removeAgentWhenFinished(Assignment a, double tnow) {
		AgentType type = a.getType();
		if(agents.contains(a)){
			updateWorkingTimes(tnow);
			agents.remove(a);
			updateAllocatedAgents(type, -1);
			double workingTime = getWorkingTime(a.getRatio());
			double tempWorkload = durationDivision.get(type) - workingTime;
			workload = workload - workingTime;

			durationDivision.put(type, tempWorkload);
			if(tempWorkload<5 && allocatedAgents.get(type)<=0){
				durationDivision.remove(type);
				workload = workload - tempWorkload;
				agentsRequiered.remove(type);
			}
		}
		if (agents.isEmpty()) {// if the mission abandoned
			missionAbandoned(false, tnow);
		}

	}

	// Updates mission status if an agent leaves before he has finished his
	// part (in the case of reallocation)
	public void removeAgentBeforeFinish(Assignment a, double tnow) {
		AgentType type = a.getType();
		if(agents.contains(a)){
			updateWorkingTimes(tnow);
			agents.remove(a);
			updateAllocatedAgents(a.getType(), -1);
			if (a.getArrivalTime() > 0) {
				double tempWorkload = durationDivision.get(type) - (tnow - a.getArrivalTime());
				workload = workload - (tnow - a.getArrivalTime());

				durationDivision.put(type, tempWorkload);
			}
		}
	}

	// All agents deleted from the mission. It receives true in the case of
	// reallocation.
	public void missionAbandoned(boolean isReallocation, double tnow) {

		utility = utility * (workload / totalDuration);
		createsPLUtility();

		totalDuration = workload;
		isAllocated = false;
		if (!isReallocation) {
			freezTime = tnow;
		}
	}

	public TreeMap<AgentType, Double> getDurationDivision() {
		return durationDivision;
	}

	// removes agents before the task is ended, before reallocation
	public void clearAgents(double tnow) {
		while (!agents.isEmpty()) {
			removeAgentBeforeFinish(agents.elementAt(0), tnow);
		}
		missionAbandoned(true, tnow);
		numOfAllocatedAgents = 0;

	}

	public Vector<Assignment> getAgents() {
		return agents;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 == null)
			return false;
		if (arg0 instanceof Task) {
			return ((Task) arg0).id == this.id;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Task [location=" + location + ", duration=" + totalDuration
				+ ", startTime=" + missionArrivalTime + ", arrivalTime="
				+ freezTime + ", id=" + id + ", utility=" + utility
				+ ", priority=" + priority + ", agentsRequiered="
				+ agentsRequiered + ", workload=" + workload + "]";
	}

	public int getPriority() {
		return priority;
	}

	// returns workload (duration) according to fraction from the task
	public double getWorkingTime(double ratio) {

		return totalDuration * ratio;
	}

	@Override
	public double getDistance(Distancable d) {
		URLConnectionReader u = new URLConnectionReader();
		double distance = u.getDemoDistance2(d.getLocation(), location);
		return (distance / 40.0) * 3600;
	}

	public double getHardConstraintTime() {
		return hardConstraintTime;
	}

	public void setHardConstraintTime(double hardConstraintTime) {
		this.hardConstraintTime = hardConstraintTime;
	}

	@Override
	public int compareTo(Task o) {
		if (utility > o.utility) {
			return 1;
		} else if (utility < o.utility) {
			return -1;
		} else {
			if (missionArrivalTime < missionArrivalTime) {
				return 1;
			} else if (missionArrivalTime > missionArrivalTime) {
				return -1;
			}
		}
		return 0;
	}

	public double getMissionArrivalTime() {
		return missionArrivalTime;
	}

	public double getSW() {
		return SW;
	}

	// Returns the utility for max number of agents
	public double getTotalUtility() {
		return utility;
	}

	// Returns the utility according to number of agents that are allocated.
	public double getCurrentUtility() {
		if (agents.size() == 0) {
			return 0;
		}
		if (agents.size() <= numOfAgentsRequired) {

			return agents.size() * PLUtility.get(agents.size())
					* Math.pow(Utility.DF, DFTime / Utility.timeUnit);
		}
		return numOfAgentsRequired * PLUtility.get(numOfAgentsRequired)
				* Math.pow(Utility.DF, DFTime / Utility.timeUnit);
	}

	public double getDFTime(double tnow) {
		if (DFTime == -1) {
			return tnow - freezTime;
		}
		return DFTime;
	}

	// Returns utility in specific time period
	public double getUtilityBetweenTimes(double from, double to) {

		double sw = (to - from) / getDuration() * getCurrentUtility();
		SW += sw;
		return sw;
	}

	public int getNumAgentsRequiered() {
		return numOfAgentsRequired;
	}
	
	public int getNumOfAgentTypesRequiered() {
		return agentsRequiered.size();
	}

	public int getNumAgentsRequiered(AgentType type) {
		return agentsRequiered.get(type);
	}

	public boolean isAgentTypeRequired(HashSet<AgentType> hashSet) {
		for (AgentType agentType : hashSet) {
			if(agentsRequiered.containsKey(agentType)){
				return true;
			}
		}
		return false;
	}

	public HashMap<AgentType, Integer> getAgentsRequiered() {
		return agentsRequiered;
	}

	public Object clone() {
		Task t = new Task(location, totalDuration, missionArrivalTime, id, priority,
				utility, agentsRequiered);
		t.workload = workload;
		t.freezTime = freezTime;

		return t;
	}

	public double getWorkload() {
		// TODO Auto-generated method stub
		return workload;
	}

	public TreeMap<Integer, Double> getWorkingTime() {
		return workingTime;
	}

	public boolean isAllocated() {
		return isAllocated;
	}

	public void setAllocated(boolean isAbandoned) {
		this.isAllocated = isAbandoned;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public double getDoneWorkload() {
		return totalDuration - workload;
	}

	public TreeMap<Integer, Double> getPLUtility() {
		return PLUtility;
	}

	public boolean isAgentTypeRequired(AgentType key) {
		return agentsRequiered.containsKey(key);
	}

	public void initFisherCA(Mailer mailer) {
		this.mailer = mailer;
		this.bidsRecieved = new HashMap<PoliceUnit,Double>();
		this.decisionCounter = 0;
		this.messageRecived = new HashMap<PoliceUnit,Message>();
		this.taskChange = -1;
		this.allocation = new HashMap<PoliceUnit,Double>();
		
	}

	public Map<PoliceUnit,Double> getAllocation(){
		return this.allocation;
	}
	
	@Override
	public void recieveMessage(List<Message> msgs) {
		updateBidsInMap(msgs);
		double price=calculatePrice();
		this.allocation = reallocation(price);
		updateChange(allocation);
		sendAllocation(allocation);
	}

	private void sendAllocation(Map<PoliceUnit, Double> allocation) {
		for (Entry<PoliceUnit, Double> e : allocation.entrySet()) {
			PoliceUnit p = e.getKey();
			Double allocationPerPoliceUnit = e.getValue();
			this.createMessage(p,allocationPerPoliceUnit);
		}
		
	}

	private void updateChange(Map<PoliceUnit, Double> allocation) {
		double sumAllocation = 0;
		for (Double singleAllocation: allocation.values()) {
			sumAllocation = sumAllocation + singleAllocation; 
		}
		
		if (this.taskChange == -1) {
			this.taskChange = sumAllocation;
		}else {
			double delta = sumAllocation-this.taskChange;
			this.taskChange = Math.abs(delta);
		}
		
	}

	private Map<PoliceUnit, Double> reallocation(double price) {
		Map<PoliceUnit, Double> ans = new HashMap<PoliceUnit, Double>();
		for (Entry<PoliceUnit, Double> e : this.bidsRecieved.entrySet()) {
			PoliceUnit p = e.getKey();
			Double bid = e.getValue();
			ans.put(p, bid/price);
		}
		return ans;
	}

	private double calculatePrice() {
		double price=0;
		for (Double singleBid : bidsRecieved.values()) {
			price=price+singleBid;
		}
		return price;
	}

	private void updateBidsInMap(List<Message> msgs) {
		for (Message m : msgs) {
			boolean ignoreMessage = shouldIgnore(m);	
			Messageable sender= m.getSender();
			PoliceUnit p = (PoliceUnit)sender;

			if (!ignoreMessage || !MainSimulationForThreads.considerDecisionCounter) {		
				this.messageRecived.put(p, m);
				double bid  = m.getContext();
				this.bidsRecieved.put(p, bid);
			}
		}
		
	}
	
	private boolean shouldIgnore(Message m) {
		Messageable sender= m.getSender();
		PoliceUnit p = (PoliceUnit)sender;
		int messageDecisionCounter = m.getDecisionCounter();
		if (!messageRecived.containsKey(p)) {
			return false;
		}else {
			
			int currentMessageDecisionCounter = messageRecived.get(p).getDecisionCounter();
			if (currentMessageDecisionCounter < messageDecisionCounter) {
				return false;
			}else {
				return true;
			}	
		}

	}

	
	@Override
	public void createMessage(Messageable p, double allocation) {
		this.mailer.createMessage(this, this.decisionCounter, p, allocation);
	}

	public double getTaskChanges() {
		return this.taskChange;
	}


		
}
