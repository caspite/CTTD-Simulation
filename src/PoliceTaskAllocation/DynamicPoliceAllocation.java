package PoliceTaskAllocation;

import fisher.FisherSemiDistributed;

import java.util.*;
import java.util.Map.Entry;
import Comparators.UtilityBPBComparator2;
import Helpers.*;
import SW.*;
import Solver.*;
import TaskAllocation.*;
import static PoliceTaskAllocation.Status.*;

public class DynamicPoliceAllocation {
	// /initial parameters//////
	static final double tresholdsDistance = 450;
	static final double ratioTresholds = 0.5;
	protected int numOfUnits;// number of police units

	// ------------------/simulation
	// parameters//-------------------------------------------------------
	protected TreeSet<DiaryEvent> diary = new TreeSet<DiaryEvent>();// diary of
																	// incoming
																	// events
	protected TreeMap<Double, Double> cumulativeSW;// cumulative
													// SW
	protected Vector<PoliceUnit> policeUnits = new Vector<PoliceUnit>();// police
																		// units
	protected Vector<Task> activeEvents = new Vector<Task>();// active events
																// in the system
	protected Vector<Task> activeEventsForAllocation;
	protected DiaryEvent currentDiaryEvent = null;
	protected Utility[][] utilities;
	protected Vector<Assignment>[] currentAllocation;
	protected Vector<Assignment>[] oldAllocation;

	protected int numOfPatrols;
	protected double Tnow, Told, pow = 0.9;// current time of the simulation
	public static final double Tmax = 8000;// the end time of the simulation  28900
	
	protected Metrics metrics;

	// ////////////////
	protected double fraction;

	private MetricsSummary metrics2;
	
	private Mailer mailer;

	// Constructor for real simulation///////////////////////

	public DynamicPoliceAllocation(TreeSet<DiaryEvent> diary,
			Vector<Task> activeEvents, Vector<PoliceUnit> p,
			TreeMap<Double, Double> cumulativeSW, int shift,
			MetricsSummary metrics2) {
		this.cumulativeSW = cumulativeSW;
		this.policeUnits = p;
		this.diary = diary;
		this.activeEvents = activeEvents;
		createFirstAllocation();
		// solveAlgorithm();
		checkNewAllocation();
		cumulativeSW.put(0.0, 0.0);
		diary.add(new EndShiftEvent(Tmax));
		this.metrics2 = metrics2;
		metrics = metrics2.createNewMetrics(shift);
		this.mailer = new Mailer (MainSimulationForThreads.shift,  MainSimulationForThreads.p3,  
				MainSimulationForThreads.p4,  MainSimulationForThreads.UB);

	}

	// locates units at patrol areas at the beginning of the shift
	private void createFirstAllocation() {
		int numOfAgents = policeUnits.size();
		numOfPatrols = activeEvents.size();
		currentAllocation = new Vector[numOfAgents];
		for (int i = 0; i < numOfAgents; i++) {
			currentAllocation[i] = new Vector<Assignment>();
			policeUnits.get(i).setStatus(PATROLING);
		}

		for (int i = 0; i < numOfAgents; i++) {
			currentAllocation[i].add(new Assignment(policeUnits.elementAt(i),
					activeEvents.elementAt(i), 1, AgentType.TYPE1));
			policeUnits.get(i).setCurrentTask(
					currentAllocation[i].firstElement());
			policeUnits.get(i)
					.setLocation(
							currentAllocation[i].firstElement().getTask()
									.getLocation());
		}
	}

	public void runSimulation() throws Exception {
		Told = 0;
		//printCurrentAllocation();
		
		do {
			
			
			currentDiaryEvent = diary.pollFirst(); // extract the next event
			if (currentDiaryEvent instanceof EndShiftEvent) {
				break;
			}
			Tnow = currentDiaryEvent.getTime(); // the begining of the pulled event from the diary
			SW.discountedSW(cumulativeSW, Tnow, Told, activeEvents, metrics);
			metrics.sumTime(Tnow, Told, policeUnits);
			Told = Tnow; // 
			
			// checks what type of event and use the relevent method
			if (currentDiaryEvent instanceof NewDiaryEvent) {
				oldAllocation = currentAllocation;
				handleNewEvent();
			} else if (currentDiaryEvent instanceof AgentArrivesToEvent) {
				handleAgentArrivesEvent();
			} else if (currentDiaryEvent instanceof AgentLeavesEvent) {
				handleAgentLeavesEvent();
			}

			// printCurrentAllocation();

		} while (Tnow < Tmax && !diary.isEmpty());
		synchronized (metrics2) {
			metrics.writeParametersForFiles();
			metrics2.updateParameters(metrics);
		}

		return;

	}

	// prints current allocation of the units to the missions
	private void printCurrentAllocation() {

		System.out.println("");

		for (PoliceUnit p : policeUnits) {
			System.out.print(" u" + p.getId() + " - "
					+ p.getCurrentTaskID());
		}

		System.out.println("");

	}

	// / agent arrives to the task
	private void handleAgentArrivesEvent() {
		Assignment a = currentDiaryEvent.getAssignment();

		if (a.getTask() instanceof MissionEvent) {
			metrics.countAllocatedEvents(a, Tnow);
			a.getTask().setStarted(true);
		}
		// adds the arriving agent to set of working agents on the event
		a.getTask().addAgent(a, Tnow);
		a.setArrivalTime(Tnow);

		// agent no longer on the way
		a.getAgent().setOnTheWay(false);
		a.getAgent().setLocation(a.getTask().getLocation());

		createLeavingEvent(a);
	}

	// creates leaving event
	private void createLeavingEvent(Assignment a) {

		if (a.getTask() instanceof MissionEvent) {
			AgentLeavesEvent al = new AgentLeavesEvent(a, Tnow);
			a.getAgent().setStatus(WORKING);
			int i = 0;
			while (diary.contains(al)) {
				i++;
				al = new AgentLeavesEvent(a, Tnow - 1.0 * i);
			}
			diary.add(al);
		} else {
			a.getAgent().setStatus(PATROLING);
		}
	}

	// agent finished his share and leaves the task
	private void handleAgentLeavesEvent() {

		AgentLeavesEvent e = (AgentLeavesEvent) currentDiaryEvent;
		Assignment as = e.getAssignment();
		as.getTask().removeAgentWhenFinished(as, Tnow);
		nextAllocation(as.getAgent());
		//System.out.print("as.getAgent()"+as.getAgent());
		checkIfTaskEnded(as.getTask());
		
//		if(checkIfTaskEnded(as.getTask())) {
//			reallocation();
//			solveAlgorithm();// solve
//			checkNewAllocation();
//			metrics.calculateRealocation(oldAllocation, currentAllocation,
//					activeEvents);
//		}else {
//			nextAllocation(as.getAgent());
//		}

	}

	// send agents to his next task
	private void nextAllocation(Agent agent) {
		if (currentAllocation[agent.getId() - 1].size() == 1) {
			currentAllocation[agent.getId() - 1].remove(0);
			allocatePatrol(agent);
		} else if (currentAllocation[agent.getId() - 1].size() == 0) {
			//System.out.println("currentAllocation" + currentAllocation[agent.getId() - 1].size());
			allocatePatrol(agent);
		} else {
			currentAllocation[agent.getId() - 1].remove(0);
		}
		Assignment as = currentAllocation[agent.getId() - 1].get(0);// next
																	// assignment
		agent.setCurrentTask(as);
		moveUnit(as);

	}

	// allocates unit to patrol, if its schedule is empty
	private boolean allocatePatrol(Agent agent) {
		int i = -1;
		if (agent.getCurrentTaskID() < 0) {
			Assignment as = agent.getCurrentTask();
			System.out.println("current task " + as);
			currentAllocation[agent.getId() - 1].add(as);
			if (!agent.isOnTheWay()) {
				return false;
			} else {
				return true;
			}

		}
		//TC: what this for?
		//TC: Small change to stay within range of j
			while (true) {
			int j = (int) (Math.random() * numOfPatrols) ;

			System.out.println("j: "+ j);
			int temp = 1 + policeUnits.size() / numOfPatrols;
			System.out.println("temp: "+temp);
			System.out.println("activeEvents: "+activeEvents.size());

			if (activeEvents.get(j).getAgents().size() <= temp) {
				i=j;

				break;
			}
		}

		Assignment as = new Assignment(agent, activeEvents.get(i), 1,AgentType.TYPE1);
		currentAllocation[agent.getId() - 1].add(as);
		agent.setCurrentTask(as);
		return true;
	}

	// Checks if the the events was ended
	private boolean checkIfTaskEnded(Task task) {
		if (task.getWorkload() < 3) {
			activeEvents.remove(task);
			metrics.missionCompleted(task, Tnow);
			clearEvent(task);
			return true;
		}
		return false;

	}

	// Handles with new event that arrives to the system.
	private void handleNewEvent() {
		metrics.countEvents(currentDiaryEvent);
		reallocation(); // clean the current allocation, the agents are aware that they are allocated
		activeEvents.add(currentDiaryEvent.getEvent()); // the current events that take place in the simulation 
		solveAlgorithm();// solve
		checkNewAllocation();
		metrics.calculateRealocation(oldAllocation, currentAllocation,
				activeEvents);
	}

	private void reallocation() {

		for (Task m : activeEvents) {
			m.clearAgents(Tnow);
			m.setAllocated(false);
		}

		if (Tnow == 0) {
			return;
		}
		for (Iterator<DiaryEvent> it = diary.iterator(); it.hasNext();) {
			if (!(it.next() instanceof NewDiaryEvent)) {
				it.remove();
			}
		}

	}

	/*
	 * Checks new allocation.Creates new leaving and arriving events according
	 * to new allocation. If there are agents without allocation to allocate
	 */
	private void checkNewAllocation() {
		for (int i = 0; i < currentAllocation.length; i++) {
			if (currentAllocation[i].isEmpty()) {
				continue;
			}
			Assignment as = currentAllocation[i].get(0);
			Task t = as.getTask();
			Agent a = as.getAgent();
			if (a.getCurrentTask().getTask().equals(t)) {
				if (!a.isOnTheWay()) {
					t.addAgent(as, Tnow);
					as.setArrivalTime(Tnow);
					createLeavingEvent(as);
				} else {
					moveUnit(as);
				}
			} else {
				moveUnit(as);
			}
			t.setAllocated(true);
			a.setCurrentTask(as);
		}

		for (int i = 0; i < currentAllocation.length; i++) {
			//System.out.print("i"+i);

			if (currentAllocation[i].isEmpty()) {
				if (allocatePatrol(policeUnits.elementAt(i))) {
					//System.out.print("policeUnits.elementAt(i))"+policeUnits.elementAt(i));
					Assignment as = currentAllocation[i].get(0);
					policeUnits.elementAt(i).setCurrentTask(as);
					moveUnit(as);
				} else {
					Assignment as = currentAllocation[i].get(0);
					policeUnits.elementAt(i).setCurrentTask(as);
				}
			}
		}
		checkForAbandonedEvents();
	}

	// Checks if there are events that are totally abandoned
	private void checkForAbandonedEvents() {
		for (Iterator<Task> it = activeEvents.iterator(); it.hasNext();) {
			Task e = it.next();

			if (e instanceof MissionEvent && !e.isAllocated() && e.isStarted()) {
				e.missionAbandoned(false, Tnow);
				metrics.countAbandoned(e, Tnow);
				e.setAllocated(false);
			}
		}

	}

	private void moveUnit(Assignment as) {

		AgentArrivesToEvent m = new AgentArrivesToEvent(as, Tnow);
		int i = 0;
		while (diary.contains(m)) {
			i++;
			m = new AgentArrivesToEvent(as, Tnow + i);
		}
		as.getAgent().setStatus(MOVING);
		diary.add(m);
	}

	// Clears the event from the system
	private void clearEvent(Task task) {
		for (int i = 0; i < currentAllocation.length; i++) {
			for (Iterator<Assignment> it = currentAllocation[i].iterator(); it
					.hasNext();) {
				if (it.next().getTask().equals(task)) {
					it.remove();
				}
			}
		}

	}

	public void solveAlgorithm(){
		
		//TC: Add DCOP algorithem
		//-----relevent for all
		updateLocation(); //  if im on the way for a task, where im relevent to the task
		creatUtilities(activeEvents); // creates the Utilities 
		
		//-----relevent for all
		creatLinearUtilitiesWithThreshold(activeEvents);
		
		checkZeroUtility(utilities); // if there is very little left so I make it zero by force so fisher will work
		// DistributedSolver s = new DistributedSolver(utilities, 2,policeUnits,
		// activeEvents, null);
		
		UtilityBPBComparator2.tnow = Tnow;
		
		CooperativeCycleOrdering cco = new CooperativeCycleOrdering(activeEventsForAllocation, Tnow,
				policeUnits);
		
		//FisherDistributedSolverCA s = new FisherDistributedSolverCA(utilities,
		//		cco, activeEventsForAllocation, policeUnits, Tnow, mailer);
		
		FisherDistributedSolver s = new FisherDistributedSolver(utilities,
		new CooperativeCycleOrdering(activeEventsForAllocation, Tnow,
		policeUnits), activeEventsForAllocation);
		
		//FisherSolverHetro s = new FisherSolverHetro(utilities,
				//new CooperativeCycleOrdering(activeEventsForAllocation, Tnow,
					//	policeUnits), activeEventsForAllocation);
/*		DBASolver s = new DBASolver((DBAUtility[][]) utilities,
				new CooperativeCycleOrdering(activeEvents, Tnow,
						policeUnits), activeEvents);*/
		//System.out.println( "Task #" + currentDiaryEvent.getEvent().getId());
		// LPsolver s = new LPsolver(utilities, new
		// CooperativeCycleOrdering(activeEvents,Tnow,
		// policeUnits),activeEvents);
		 //SATotalSolver s=new SATotalSolver(null, policeUnits, activeEvents,
				 //currentDiaryEvent.getEvent(), currentAllocation, Tnow);
		
		// JenningsMaxUtilityHeuristic s = new JenningsMaxUtilityHeuristic(utilities, policeUnits,
		// activeEvents,null);
		// JonesSolver s= new JonesSolver(utilities, null,
		// null,currentDiaryEvent.getEvent(), currentAllocation,
		// Tnow,policeUnits);
		currentAllocation = s.solve();

	}

	// Updates moving police units location
	private void updateLocation() {
		for (PoliceUnit p : policeUnits) {
			p.setLocation(Tnow);
		}

	}

	private void creatLinearUtilitiesWithThreshold(Vector<Task> events) {
		utilities = new ConcaveUtilityThresholds[policeUnits.size()][activeEvents
				.size()];
		for (int i = 0; i < utilities.length; i++) {
			for (int j = 0; j < utilities[i].length; j++) {
				if (policeUnits.get(i).getCurrentTaskID() == events.get(j)
						.getId() || j == i % numOfPatrols || j >= numOfUnits) {
					if (events.get(j).getPriority() <= 2) {
						utilities[i][j] = new ConcaveUtilityThresholds(
								policeUnits.get(i), activeEvents.get(j), Tnow,
								pow);
					} else {
						utilities[i][j] = new ConcaveUtilityThresholds(
								policeUnits.get(i), activeEvents.get(j), Tnow,
								1);
					}
				}

			}
		}

	}

	private void creatUtilities(Vector<Task> events) {
		utilities = new LinearUtility[policeUnits.size()][events.size()];
		for (int i = 0; i < utilities.length; i++) {
			for (int j = 0; j < utilities[i].length; j++) {
				
				if (policeUnits.get(i).getCurrentTaskID() == events.get(j)
						.getId() || j == i % numOfPatrols || j >= numOfUnits) {
					if(events.get(j).isAgentTypeRequired(policeUnits.get(i).getAgentType())){
						if (isConcavityRequired(events.get(j),events)) {
							utilities[i][j] = new ConcaveUtility(
									policeUnits.get(i), events.get(j), Tnow, pow);
						} else {
							utilities[i][j] = new ConcaveUtility(
									policeUnits.get(i), events.get(j), Tnow, 1);
						}
					}else{
						utilities[i][j] = null;
					}
						
				}
			}
		}
	}

	private boolean isConcavityRequired(Task task, Vector<Task> events) {
		if(task.getPriority() != 2 && task.getPriority() != 1){
			return false;
		}
		double distanceToClosestEvent=10000;
		double highestrUtilityRatio=0;
		for (Task task2 : events) {
			if(task2 instanceof MissionEvent && !task.equals(task2)){
				if(task.getDistance(task2) < distanceToClosestEvent){
					distanceToClosestEvent = task.getDistance(task2);
				}
				if(task.getTotalUtility()/task2.getTotalUtility() > highestrUtilityRatio){
					highestrUtilityRatio = task.getTotalUtility()/task2.getTotalUtility();
				}
			}
		}
		if(distanceToClosestEvent < tresholdsDistance
				|| highestrUtilityRatio > ratioTresholds){
//			System.out.println("concave");
			return true;
		}
		
		return false;
	}

	// removes events with zero utility
	private void checkZeroUtility(Utility[][] utilities2) {
		activeEventsForAllocation = new Vector<Task>();
		for (int i = 0; i < utilities2[0].length; i++) {
			boolean flag = false;
			for (int j = 0; j < utilities2.length; j++) {
				if (utilities2[j][i] != null
						&& utilities2[j][i].getUtility(1) > 5
						|| i < numOfPatrols) {
					flag = true;
					break;
				}
			}
			if (flag) {
				activeEventsForAllocation.add(activeEvents.get(i));
			} else {
				activeEvents.get(i).clearAgents(Tnow);
			}
		}
		if (activeEvents.size() > activeEventsForAllocation.size()) {
			creatUtilities(activeEventsForAllocation);
		}

	}

	public Entry<Double, Double> getSW() {
		return cumulativeSW.lastEntry();
	}
}
