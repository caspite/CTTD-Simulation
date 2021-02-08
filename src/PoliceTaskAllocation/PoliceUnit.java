package PoliceTaskAllocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import TaskAllocation.Agent;
import TaskAllocation.ConcaveUtilityThresholds;
import TaskAllocation.Location;
import DCOP.Mailer;
import TaskAllocation.Message;
import TaskAllocation.Messageable;
import TaskAllocation.Task;
import TaskAllocation.Utility;

public class PoliceUnit extends Agent implements Messageable {

	private int decisionCounter;
	
	private Map<Task, Message> messageRecived;

	private Map<Task, Utility> utilitiesMap;
	private Map<Task, Double> updatedUtilitiesMap;
	private Map<Task, Double> bidsMap;
	private Mailer mailer;
	public PoliceUnit(Location location, int id, HashSet<AgentType> agentType) {
		super(location, id, agentType);
	}

	public PoliceUnit(int i, HashSet<AgentType> agentType) {
		super(i, agentType);
		// TODO Auto-generated constructor stub
	}

	public void createUtiliesBidsAndSendBids(Vector<Task> tasks, Mailer mailer, double Tnow) {
		this.mailer = mailer;
		initMapsInVariables();
		placeUtilitiesInMap(tasks, Tnow);
		placeBidsWithUpdatedUtilityInMap();	
		sendBidsToTasks();
	}

	private void initMapsInVariables() {
		this.decisionCounter= 0;
		this.messageRecived = new HashMap<Task, Message>();
		this.bidsMap = new HashMap<Task, Double>();
		this.utilitiesMap = new HashMap<Task, Utility>();
		this.updatedUtilitiesMap = new HashMap<Task, Double>();
	}

	
	private void sendBidsToTasks() {	
		for (Entry<Task, Double> e : bidsMap.entrySet()) {
			Task task = e.getKey();
			Double bid = e.getValue();
			this.createMessage(task,bid);
		}
	}



	private void placeBidsWithUpdatedUtilityInMap() {
		double sumUtilities = calcSumUtils();
		for (Entry<Task, Double> e : updatedUtilitiesMap.entrySet()) {
			Task task = e.getKey();
			Double updetedUtility  = e.getValue();
			Double bid = updetedUtility/sumUtilities;
			this.bidsMap.put(task,bid);
		}	
	}

	private double calcSumUtils() {
		double ans = 0.0;
		for (Double d: updatedUtilitiesMap.values()) {
			ans = ans+ d;
		}
		return ans;
	}

	private void placeUtilitiesInMap(Vector<Task> tasks, double Tnow) {
	
		for (Task task : tasks) {
			Utility u = new ConcaveUtilityThresholds(this, task, Tnow, 1);
			this.utilitiesMap.put(task, u);
			this.updatedUtilitiesMap.put(task, u.getUtility(1));
		}
	}

	@Override
	public void recieveMessage(List<Message> msgs) {
		this.decisionCounter++;
		updateUtilitesUsingAllocation(msgs);
		placeBidsWithUpdatedUtilityInMap();	
		sendBidsToTasks();
	}

	private void updateUtilitesUsingAllocation(List<Message> msgs) {
		
		for (Message m : msgs) {
			boolean ignoreMessage = shouldIgnore(m);	
		//-------- extract info from msg	
			Messageable sender= m.getSender();
			checkIfBug(sender);
			Task t = (Task)sender;

			if (!ignoreMessage || !MainSimulationForThreads.considerDecisionCounter) {		
				this.messageRecived.put(t, m);
				double allocation  = m.getContext();
				Utility u = utilitiesMap.get(t);
				Double updatedUtility= u.getUtility(allocation);
				this.updatedUtilitiesMap.put(t, updatedUtility);
			}
		}
		
	}

	private boolean shouldIgnore(Message m) {
		Messageable sender= m.getSender();
		checkIfBug(sender);
		Task t = (Task)sender;
		int messageDecisionCounter = m.getDecisionCounter();
		if (!messageRecived.containsKey(t)) {
			return false;
		}else {
			
			int currentMessageDecisionCounter = messageRecived.get(t).getDecisionCounter();
			if (currentMessageDecisionCounter < messageDecisionCounter) {
				return false;
			}else {
				return true;
			}	
		}

	}

	private void checkIfBug(Messageable sender) {
		if (!(sender instanceof Task)) {
			System.err.println("logical bug in creating message in task");
		}
		
	}

	@Override
	public void createMessage(Messageable task, double bid) {
	//	this.mailer.createMessage(this, this.decisionCounter, task, bid);
	}

}



/* unexplained if in utility calculcation
 * if (task.getPriority() <= 2) { u = new ConcaveUtilityThresholds( this, task,
 * Tnow,pow); } else {
 */
