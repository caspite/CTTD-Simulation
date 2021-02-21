package CTTD;

import CttdSolver.SpcnDcop;
import DCOP.Mailer;
import Helpers.Metrics;
import Helpers.MetricsSummary;
import PoliceTaskAllocation.*;
import TaskAllocation.*;

import java.util.*;

import static PoliceTaskAllocation.Status.*;

public class mainSimulation {


    // ------------------/simulation parameters//-------------------------------------------------------//

    protected TreeSet<DiaryEvent> diary = new TreeSet<DiaryEvent>();// diary of incoming
    // events
    protected Vector<MedicalUnit> medicalUnits = new Vector<MedicalUnit>();// medical
    // units
    protected Vector<Task> activeEvents = new Vector<Task>();// active events
    // in the system
    protected Vector<Task> allEventsForAllocation;
    protected DiaryEvent currentDiaryEvent = null;
    protected Vector<Assignment>[] currentAllocation;
    protected Vector<Assignment>[] oldAllocation;
    protected Vector<Agent> availableAgent;
    protected Vector<Hospital> hospitals;

    protected int numOfPatrols = 0;
    protected double Tnow, Told, pow = 0.9;// current time of the simulation
    public static final double Tmax = 8000;// the end time of the simulation

    int algorithmType=1; //0- greedy 1-SpcnDcop
    Mailer mailer;




    //----------------Constructor for real simulation------------------------------------------//

    public mainSimulation(TreeSet<DiaryEvent> diary,
                          Vector<Task> activeEvents, Vector<MedicalUnit> mu, Vector<Hospital> hos) {
        this.medicalUnits = mu;
        this.diary = diary;
        this.hospitals = hos;
        this.allEventsForAllocation = activeEvents;

        createFirstAllocation();
        checkNewAllocation();

        diary.add(new EndShiftEvent(Tmax));

        //this.metrics2 = metrics2;
        //metrics = metrics2.createNewMetrics(1);
    }
    public mainSimulation(TreeSet<DiaryEvent> diary,
                          Vector<Task> activeEvents, Vector<MedicalUnit> mu, Vector<Hospital> hos,int algorithmType,Mailer mailer) {
        this.medicalUnits = mu;
        this.diary = diary;
        this.hospitals = hos;
        this.allEventsForAllocation = activeEvents;
        this.algorithmType=algorithmType;
        this.mailer=mailer;
        createFirstAllocation();
        checkNewAllocation();

        diary.add(new EndShiftEvent(Tmax));

        //this.metrics2 = metrics2;
        //metrics = metrics2.createNewMetrics(1);
    }


    /***
     * all medical units waiting and available
     */
    private void createFirstAllocation() {
        int numOfAgents = medicalUnits.size();
        availableAgent = new Vector<Agent>();
        currentAllocation = new Vector[numOfAgents];
        // update agent's status to waiting
        for (int i = 0; i < numOfAgents; i++) {
            currentAllocation[i] = new Vector<Assignment>();
            medicalUnits.get(i).setStatus(WAITING);
            availableAgent.add(medicalUnits.get(i));
        }


    }



    public void runSimulation() {
        Told = 0;

        do {
            currentDiaryEvent = diary.pollFirst(); // extract the next event
            printCurrentEvent();
//            printCurrentAllocation();
            updateSimulation(currentDiaryEvent.getTime());
            if (currentDiaryEvent instanceof EndShiftEvent) {
                break;
            }
            // checks event's type
            if (currentDiaryEvent instanceof NewDiaryEvent) {//new disaster site
                oldAllocation = currentAllocation;
                handleNewEvent();
            } else if (currentDiaryEvent instanceof AgentArrivesToEvent) {
                handleAgentArrivesEvent();
            } else if (currentDiaryEvent instanceof AgentLeavesEvent) {
                handleAgentLeavesEvent();
            } else if (currentDiaryEvent instanceof HospitalEvent) {
                handleHospitalEvent();
            }
           // printCurrentAllocation();

        } while (Tnow < Tmax && !diary.isEmpty());
        printCurrentAllocation();

    }



    //-------------------- handel new event------------------------------------------------//

    /***
     *     Handles with new event that arrives to the system.
     *     add the current events to active events and solve greedy
     */
    private void handleNewEvent() {
        activeEvents.add(currentDiaryEvent.getEvent());
        switch (algorithmType){
            case 0:
                solveGreedyAlgorithm();
                break;
            case 1:
                solveSpcnDcop();
                break;

        }
    }

    /***
     * for all active event - allocate available agent if agent is relevant
     */
    public void solveGreedyAlgorithm() {

        // check relevant to task each available agent
        for (Task task : activeEvents) {
            if (availableAgent.size() <= 0) {
                break;
            }
            for (int i = 0; i < availableAgent.size(); i++) {
                Agent agent = availableAgent.get(i);
                if (task.isAgentRequired(agent, Tnow)) {
                    if(agent.getCurrentTask()==null){
                        allocatedAgentToTask(agent, task);
                        continue;
                    }
                    else if(!agent.getCurrentTask().getTask().equals(task)){
                        allocatedAgentToTask(agent, task);

                    }
                }
            }
        }
    }

    public void solveSpcnDcop(){
        SpcnDcop s=new SpcnDcop(this.availableAgent,this.activeEvents,this.mailer,this.Tnow);
        s.createConstraintGraph(Tnow);
        Vector<Assignment> newAllocation = s.solve();
        updateCurrentAllocation(newAllocation);
    }

    private void updateCurrentAllocation(Vector<Assignment> allocation){
        for(Assignment as:allocation){
            //TODO- if reallocate - remove previous diary event for this agent
            Agent agent = as.getAgent();
            currentAllocation[agent.getId()].add(as);
            agent.setCurrentTask(as);
            //update available agent
            reduceAvailableAgent(agent);
            moveUnit(as);
        }
    }

    /***
     * task allocation phase phase - allocated agent to task
     * @param agent
     * @param task
     */
    private void allocatedAgentToTask(Agent agent, Task task) {
        //allocated the agent to the task
        Assignment as = new Assignment(agent, task, 0, Tnow);
        currentAllocation[agent.getId()].add(as);
        agent.setCurrentTask(as);
        //update available agent
        reduceAvailableAgent(agent);

        moveUnit(as);
    }

    //*** handel agent arrive to task event ***//

    /***
     *     agent arrives to the task
     *     activities assignment
     */
    private void handleAgentArrivesEvent() {
        Assignment as = currentDiaryEvent.getAssignment();

        if (as.getTask() instanceof MissionEvent) {
            //if is the first agent - information task
            if (!as.getTask().isStarted()) {
                as.getTask().setStarted(true);
//                as.setActivity(Activity.INFO);
//                as.setEndTime(as.getTask().getHardConstraintTime());
//                //create agent leave event
//                createLeavingEvent(as);
            }
                activitiesAssignment(as);
                //if agent not working
                if(as.getAgent().getStatus()== WAITING){
                    addAvailableAgent(as.getAgent());
                    switch (algorithmType){
                        case 0:
                            solveGreedyAlgorithm();
                            break;
                        case 1:
                            solveSpcnDcop();
                            break;
                    }
                createLeavingEvent(as);
            }
        }
        // adds the arriving agent to set of working agents on the event
        as.getTask().addAgent(as, Tnow);
        as.setArrivalTime(Tnow);

        // agent no longer on the way
        as.getAgent().setOnTheWay(false);
        as.getAgent().setLocation(as.getTask().getLocation());
        createLeavingEvent(as);

    }

    /***
     * for assignment - allocate activities and casualties
     * @param as
     */
    private void activitiesAssignment(Assignment as) {
        as.activitiesAssignment();//for each assignment which activities


    }


    //--------------- handel agent leave event--------------------------------------------//

    /***
     *     agent finished his share and leaves the task
     *     if capacity empty- to go hospital
     */
    private void handleAgentLeavesEvent() {
        Assignment as = currentDiaryEvent.getAssignment();
        MedicalUnit mu = (MedicalUnit) as.getAgent();
        //if agent is full - go to hospital event for refill
        if (mu.isFull) {
            createGoToHospitalEvent(as);
        }
        //if agent have casualties - create gotohospital event
        else if (mu.getCasualties().size() > 0) {
            createGoToHospitalEvent(as);
        } else {
            //add agent to available agent and call the greedy algorithm
            mu.setStatus(WAITING);
            addAvailableAgent(mu);
            switch (algorithmType){
                case 0:
                    solveGreedyAlgorithm();
                    break;
                case 1:
                    solveSpcnDcop();
                    break;
            }        }
        if(((DisasterSite)as.getTask()).finished()){
            removeTaskFromActiveEvent(as.getTask());
        }


//            AgentLeavesEvent e = (AgentLeavesEvent) currentDiaryEvent;
//            Assignment as = e.getAssignment();
//            //TODO update removeAgentWhenFinished function
//            as.getTask().removeAgentWhenFinished(as, Tnow);
//            nextAllocation(as.getAgent());
//            //System.out.print("as.getAgent()"+as.getAgent());
//            checkIfTaskEnded(as.getTask());
//

    }

    /***
     * add to diary go to the nearest hospital event
     * @param as
     */
    private void createGoToHospitalEvent(Assignment as) {
        //find the nearest hospital
        Hospital hospital = allocateHospital(as.getAgent());
        //go to hospital update agent status and location to hospital's location
        //create new assignment
        Assignment newAs = new Assignment(as.getAgent(), hospital, 0, Tnow);
        createHospitalEvent(newAs);

    }

    /***
     *
     * @param agent
     * @return the nearest hospital
     */
    private Hospital allocateHospital(Agent agent) {
        double dis = Double.POSITIVE_INFINITY;
        Hospital hospital = hospitals.get(0);
        for (Hospital h : hospitals) {
            double currentDis = Distance.travelTime(agent,h);
            if (currentDis < dis) {
                dis = currentDis;
                hospital = h;
            }
        }
        return hospital;
    }



    //-------------------handle agent arrive to hospital--------------------------------//


    /***
     * Unload casualties, fill capacity and call for a greedy algorithm
     */
    private void handleHospitalEvent() {

        MedicalUnit mu = (MedicalUnit) currentDiaryEvent.getAssignment().getAgent();
        Hospital h = (Hospital) currentDiaryEvent.getAssignment().getTask();
        //save agent's casualties survival on arrival to hospital - move casualties to hospital
        mu.loweringCasualties(h, Tnow);
        //reload agent capacity
        mu.reloadCapacity();
        //change agent's status to wait and add to available agent
        mu.setStatus(WAITING);
        addAvailableAgent(mu);
        mu.setCurrentTask(currentDiaryEvent.getAssignment());
        //call greedy algorithm
        switch (algorithmType){
            case 0:
                solveGreedyAlgorithm();
                break;
            case 1:
                solveSpcnDcop();
                break;
        }    }



    //--------------------------helpers-------------------------------------------------//

    /***
     *
     */
    private void updateSimulation(double tnow){
        Tnow = tnow; // set simulation  Time - hours
        Told = Tnow;

        //update agent's capacity+ utilities for the current task - to add for each agent the estimate utility
        for(Agent a: medicalUnits){
            a.upateAgent(tnow);
            //clear diary events
        }



    }
    /***
     *     prints current allocation of the units to the missions
     */
    private void printCurrentAllocation() {

        System.out.println("");


        for (MedicalUnit m : medicalUnits) {
            System.out.print("" +  m.toString() + " - Disaster Site ID "
                    + m.getCurrentTaskID());
        }

        System.out.println("");

    }

    /***
     * print diary log
     */
    private void printCurrentEvent() {

        System.out.println("");

        System.out.print(currentDiaryEvent.toString());

        System.out.println("");

    }



    /***
     *    creates leaving event and update agent status to working/patroling
     */
    private void createLeavingEvent(Assignment a) {

        if (a.getTask() instanceof MissionEvent) {
            AgentLeavesEvent al = new AgentLeavesEvent(a, Tnow);
            a.getAgent().setStatus(WORKING);
            int i = 0;
            while (diary.contains(al)) {
                i++;
                al = new AgentLeavesEvent(a,  Tnow+1.0 * i);
            }
            diary.add(al);
        } else {
            a.getAgent().setStatus(PATROLING);
        }
    }
    /***
     * add hospital event to diary
     * @param as
     */
    private void createHospitalEvent(Assignment as) {
        HospitalEvent h = new HospitalEvent(as, Tnow);
        int i = 0;
        //if the key of m in diary add i=1 to key
        while (diary.contains(h)) {
            i++;
            h = new HospitalEvent(as, Tnow + i);
        }
        as.getAgent().setStatus(MOVING);
        diary.add(h);
    }

    /***
     *   add AgentArrivesToEvent task
     */
    private void moveUnit(Assignment as) {

        AgentArrivesToEvent m = new AgentArrivesToEvent(as, Tnow);
        int i = 0;
        //if the key of m in diary add i=1 to key
        while (diary.contains(m)) {
            i++;
            m = new AgentArrivesToEvent(as, Tnow + i);
        }
        as.getAgent().setStatus(MOVING);
        diary.add(m);
    }

    /***
     * remove agent to available agent
     * @param agent
     */
    private void reduceAvailableAgent(Agent agent) {
        if (availableAgent.contains(agent)) {
            availableAgent.removeElement(agent);

        }

    }

    /***
     * add agent to available agent
     * @param agent
     */
    private void addAvailableAgent(Agent agent) {
        if (!availableAgent.contains(agent)) {
            availableAgent.add(agent);
        }

    }



    /***
     *     allocates unit to patrol, if its schedule is empty
     */
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
        while (true) {
            int j = (int) (Math.random() * numOfPatrols);

            System.out.println("j: " + j);
            int temp = 1 + medicalUnits.size() / numOfPatrols;
            System.out.println("temp: " + temp);
            System.out.println("activeEvents: " + activeEvents.size());

            if (activeEvents.get(j).getAgents().size() <= temp) {
                i = j;

                break;
            }
        }

        Assignment as = new Assignment(agent, activeEvents.get(i), 0, Tnow);
        currentAllocation[agent.getId() - 1].add(as);
        agent.setCurrentTask(as);
        return true;
    }



    /***
     * Creates new leaving and arriving events according
     * to new allocation. If there are agents without allocation to allocate
     */
    private void checkNewAllocation() {
        //create agent leave event and agent arrive to event tasks + move agent to task
        for (int i = 0; i < currentAllocation.length; i++) {
            if (currentAllocation[i].isEmpty()) {
                continue;
            }
            Assignment as = currentAllocation[i].get(0);
            Task t = as.getTask();
            Agent a = as.getAgent();
            if (a.getCurrentTask().getTask().equals(t)) {
                if (!a.isOnTheWay()) {
                    //   t.addAgent(as, Tnow); - not relevant to CTTD
                    as.setArrivalTime(Tnow);
                    createLeavingEvent(as);
                } else {
                    //create arrival events + update agent status to "MOVING"
                    moveUnit(as);
                }
            } else {
                moveUnit(as);
            }
            t.setAllocated(true);
            a.setCurrentTask(as);
        }

//        for (int i = 0; i < currentAllocation.length; i++) {
//
//            if (currentAllocation[i].isEmpty()) {
//                if (allocatePatrol(medicalUnits.elementAt(i))) {
//                    //System.out.print("policeUnits.elementAt(i))"+policeUnits.elementAt(i));
//                    Assignment as = currentAllocation[i].get(0);
//                    medicalUnits.elementAt(i).setCurrentTask(as);
//                    moveUnit(as);
//                } else {
//                    Assignment as = currentAllocation[i].get(0);
//                    medicalUnits.elementAt(i).setCurrentTask(as);
//                }
//            }
//        }
//        checkForAbandonedEvents();
    }

    /***
     *
     * @return the remain cover on all sites
     */
    public double getScore(){
        double count=0;
        for (Task t: allEventsForAllocation){
            count+=((DisasterSite)t).getRemainCover();
        }
        return count;
    }

    /***
     * when task finished - remove from active event
     * @param task
     */
    private void removeTaskFromActiveEvent(Task task){
        for(int i=0;i<activeEvents.size();i++){
            Task t=activeEvents.get(i);
            if(t.equals(task)){
                activeEvents.remove(t);
            }
        }

    }

    //*** getters && setters ***//

    public void setMailer(Mailer mailer) {
        this.mailer = mailer;
    }


    //-------------- unused functions--------------------------------//

    // Clears the event from  currentAllocation
    private void clearEvent(Task task) {
        for (int i = 0; i < currentAllocation.length; i++) {
            for (Iterator<Assignment> it = currentAllocation[i].iterator(); it
                    .hasNext(); ) {
                if (it.next().getTask().equals(task)) {
                    it.remove();
                }
            }
        }

    }


    // Checks if the the events was ended by workload-not relevant for CTTD
    private boolean checkIfTaskEnded(Task task) {
        if (task.getWorkload() < 3) {
            activeEvents.remove(task);
            //metrics.missionCompleted(task, Tnow);
            clearEvent(task);
            return true;
        }
        return false;

    }


    // Updates moving medical units location
    private void updateLocation() {
        for (MedicalUnit m : medicalUnits) {
            m.setLocation(Tnow);
        }
    }

    // Checks if there are events that are totally abandoned
    private void checkForAbandonedEvents() {
        for (Iterator<Task> it = activeEvents.iterator(); it.hasNext(); ) {
            Task e = it.next();

            if (e instanceof MissionEvent && !e.isAllocated() && e.isStarted()) {
                e.missionAbandoned(false, Tnow);
                //metrics.countAbandoned(e, Tnow);
                e.setAllocated(false);
            }
        }

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
}


//    private void handleNewEvent() {
//        // metrics.countEvents(currentDiaryEvent);
//        // reallocation(); // clean the current allocation the agents are aware that they are allocated
//        activeEvents.add(currentDiaryEvent.getEvent()); //add the current events to active events
//        // solveAlgorithm();// solve
//        solveGreedyAlgorithm();
//        checkNewAllocation();
////            metrics.calculateRealocation(oldAllocation, currentAllocation,
////                    activeEvents);
//    }


//        public void solveAlgorithm(){
//
//
//            //-----relevent for all
//            updateLocation(); //  if im on the way for a task, where im relevant to the task
//            creatUtilities(activeEvents); // creates the Utilities for each agent-task
//
//            //-----relevent for all
//            creatLinearUtilitiesWithThreshold(activeEvents);//relevant when the task priority is 1 or 2
//
//            checkZeroUtility(utilities); // if there is very little left so I make it zero by force so fisher will work
//            // DistributedSolver s = new DistributedSolver(utilities, 2,policeUnits,
//            // activeEvents, null);
//
//            UtilityBPBComparator2.tnow = Tnow;
//
//            CooperativeCycleOrdering cco = new CooperativeCycleOrdering(activeEventsForAllocation, Tnow,
//                    medicalUnits);
//
//
//
//            FisherDistributedSolver s = new FisherDistributedSolver(utilities,
//                    new CooperativeCycleOrdering(activeEventsForAllocation, Tnow,
//                            medicalUnits), activeEventsForAllocation);
//
//
//            currentAllocation = s.solve();
//
//        }


//    private void calculateContribution(Assignment as){
//        HashMap<Casualty, Activity> casAct=new HashMap<>();
//            if(as.getTask() instanceof DisasterSite){
//               casAct.putAll(((DisasterSite)as.getTask()).activitiesAssignment(as));
//            }
//        double contribution=0;
//            for(Map.Entry<Casualty, Activity> i:casAct.entrySet()){
//                //calculate time end activity
//               double activityTime = as.getAgent().getActivityTime(i.getKey().triage,i.getValue());
//                //calculate the survival at the arrival time and at the end activity time
//               double endTime = as.getArrivalTime()+activityTime;
//               as.setEndTime(endTime);
//               double endSurvival = Probabilities.getSurvival(i.getKey().getTriage(),endTime,i.getKey().getTBorn());
//               contribution+= i.getKey().getSurvival()-endSurvival;
//            }
//            as.setContribution(contribution);
//            //get casualties survival at the end of the of the activity
//
//            //add the interval init survival - end survival to the assignment contribution to the contribution
//
//
//    }

//    private void reallocation() {
//
//        for (Task m : activeEvents) {
//            m.clearAgents(Tnow);
//            m.setAllocated(false);
//        }
//
//        if (Tnow == 0) {
//            return;
//        }
//        for (Iterator<DiaryEvent> it = diary.iterator(); it.hasNext();) {
//            if (!(it.next() instanceof NewDiaryEvent)) {
//                it.remove();
//            }
//        }
//
//    }


//        private void creatLinearUtilitiesWithThreshold(Vector<Task> events) {
//            utilities = new ConcaveUtilityThresholds[medicalUnits.size()][activeEvents
//                    .size()];
//            for (int i = 0; i < utilities.length; i++) {
//                for (int j = 0; j < utilities[i].length; j++) {
//                    if (medicalUnits.get(i).getCurrentTaskID() == events.get(j)
//                            .getId() || j == i % numOfPatrols || j >= numOfUnits) {
//                        if (events.get(j).getPriority() <= 2) {
//                            utilities[i][j] = new ConcaveUtilityThresholds(
//                                    medicalUnits.get(i), activeEvents.get(j), Tnow,
//                                    pow);
//                        } else {
//                            utilities[i][j] = new ConcaveUtilityThresholds(
//                                    medicalUnits.get(i), activeEvents.get(j), Tnow,
//                                    1);
//                        }
//                    }
//
//                }
//            }
//
//        }
// removes events with zero utility
//        private void checkZeroUtility(Utility[][] utilities2) {
//            activeEventsForAllocation = new Vector<Task>();
//            for (int i = 0; i < utilities2[0].length; i++) {
//                boolean flag = false;
//                for (int j = 0; j < utilities2.length; j++) {
//                    if (utilities2[j][i] != null
//                            && utilities2[j][i].getUtility(1) > 5
//                            || i < numOfPatrols) {
//                        flag = true;
//                        break;
//                    }
//                }
//                if (flag) {
//                    activeEventsForAllocation.add(activeEvents.get(i));
//                } else {
//                    activeEvents.get(i).clearAgents(Tnow);
//                }
//            }
//            if (activeEvents.size() > activeEventsForAllocation.size()) {
//                creatUtilities(activeEventsForAllocation);
//            }
//
//        }

//        public Map.Entry<Double, Double> getSW() {
//            return cumulativeSW.lastEntry();
//        }

//    private boolean isConcavityRequired(Task task, Vector<Task> events) {
//        if(task.getPriority() != 2 && task.getPriority() != 1){
//            return false;
//        }
//        double distanceToClosestEvent=10000;
//        double highestrUtilityRatio=0;
//        for (Task task2 : events) {
//            if(task2 instanceof MissionEvent && !task.equals(task2)){
//                if(task.getDistance(task2) < distanceToClosestEvent){
//                    distanceToClosestEvent = task.getDistance(task2);
//                }
//                if(task.getTotalUtility()/task2.getTotalUtility() > highestrUtilityRatio){
//                    highestrUtilityRatio = task.getTotalUtility()/task2.getTotalUtility();
//                }
//            }
//        }
//        if(distanceToClosestEvent < tresholdsDistance
//                || highestrUtilityRatio > ratioTresholds){
////			System.out.println("concave");
//            return true;
//        }
//
//        return false;
//    }

//        private void creatUtilities(Vector<Task> events) {
//            utilities = new CTTDUtility[medicalUnits.size()][events.size()];
//            for (int i = 0; i < utilities.length; i++) {
//                for (int j = 0; j < utilities[i].length; j++) {
//                    if (events.get(j) instanceof DisasterSite) {
//                        if (medicalUnits.get(i).getCurrentTaskID() == events.get(j)
//                                .getId() || j == i % numOfPatrols || j >= numOfUnits) {//???
//                            if (((DisasterSite) events.get(j)).isAgentRequired(medicalUnits.get(i), Tnow)) {
//                                utilities[i][j] = new CTTDUtility(
//                                        medicalUnits.get(i), events.get(j), Tnow);
//
//                            } else {
//                                utilities[i][j] = null;
//                            }
//
//                        }
//                    }
//                }
//            }
//        }
//
//    // /initial parameters//////
//    static final double tresholdsDistance = 450;
//    static final double ratioTresholds = 0.5;
//    protected int numOfUnits;// number of police units
//    protected Utility[][] utilities;
////outputs
//protected Metrics metrics;
//    private MetricsSummary metrics2;