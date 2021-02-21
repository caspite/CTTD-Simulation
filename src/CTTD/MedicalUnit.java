package CTTD;

import CttdSolver.FirstMessage;
import CttdSolver.ServiceMessage;
import CttdSolver.UtilityMessage;
import DCOP.MessageBox;
import DCOP.Mailer;
import DCOP.Message;
import PoliceTaskAllocation.AgentType;
import TaskAllocation.*;


import java.util.*;

public class MedicalUnit extends Agent implements Messageable {

    //***Medical Unit variables***//
    Capacity skills;
    AgentType agentType;
    boolean isFull;
    Vector<Casualty> casualties; // the casualties that uploaded to the agent
    private int decisionCounter;


    //***Messages Variables***//
    MessageBox messageBox;
    Vector<Message> messageToBeSent;

    //*** allocation variables ***//
    HashMap<Task, Double> relevantTasksUtility;//relevant tasks and current provider utility
    HashMap<Task, Double> relevantTasksArrivalTime;//relevant tasks and current provider arrivalTime

    int numOfAllocateTask = 3;// the max num of task allocation
    Assignment currentAssignment[] = new Assignment[numOfAllocateTask];
    double lastTimeUpdate = 0;
    double nextTimeToAllocation;
    Vector<Skill> availableSkillsForAllocation;


    //*** execution variables***//
    private TreeMap<Double, Skill> currentTaskSchedule;// the current task schedule-upcoming
    protected Vector<Task> relevantTasks;//the current relevant tasks


//---------------------------------------------Methods--------------------------------------------


    //***constructor***//
    public MedicalUnit(Location location, int id, HashSet<AgentType> agentType) {
        super(location, id, agentType);
    }

    public MedicalUnit(int id, AgentType agentType, Location loc) {
        super();
        this.id = id;
        this.agentType = agentType;
        this.location = loc;
        skills = new Capacity(agentType);
        isFull = false;
        casualties = new Vector<>();
        currentTaskSchedule = new TreeMap<>();
        this.schedule = new Vector<Assignment>();
        relevantTasksUtility=new HashMap<Task, Double> ();
        relevantTasksArrivalTime=new HashMap<Task, Double>();
        availableSkillsForAllocation=new Vector<Skill>();
        currentTaskSchedule=new TreeMap<Double, Skill> ();
        relevantTasks=new Vector<Task>();
        messageToBeSent=new Vector<>();
        messageBox=new MessageBox(this.id);
    }

    //*** main Methods ****//


    //*** skills methods ****//
    public void reduceCapacity(Skill skill) {
        Activity act = skill.getActivity();
        Triage trg = skill.getTriage();

        skills.reduceCap(trg, act);
        if (skills.getCurrentScore() <= 0) {
            setIsFull(true);
        }

    }

    public void reloadCapacity() {
        this.skills.initializeCapacity(agentType);
    }

    public void updateCapacity(double time) {
        //check which skills done reduce the capacity and remove from upcoming.

        // Get a set of the entries
        Set set = currentTaskSchedule.entrySet();

        // Get an iterator
        Iterator it = set.iterator();

        // Display elements
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            if ((double) me.getKey() < time)
                continue;
            else if ((double) me.getKey() < time) {
                reduceCapacity((Skill) me.getValue());
                currentTaskSchedule.remove(me.getKey());
            }
            System.out.print("Key is: " + me.getKey() + " & ");
            System.out.println("Value is: " + me.getValue());
        }
    }
//********************************************************//




    //*** getters & setters ***//

    //Travel duration depends on the agent's type
    @Override
    public void setMovingTime(double dis) {
        Probabilities.getTravelTime(dis, agentType);
    }

    public double getTravelTime(double dis) {
        return Probabilities.getTravelTime(dis, agentType);

    }

    public AgentType getOneAgentType() {
        return this.agentType;
    }


    public Capacity getAgentSkills() {
        return skills;
    }

    //update activities schedule
    private void setCurrentTaskSchedule(double time, Skill skill) {
        currentTaskSchedule.put(time, skill);
    }


    public double getActivityTime(Triage trg, Activity act) {

        return skills.getduration(trg, act);
    }

    private void setIsFull(boolean b) {
        isFull = b;
    }

    public void setCasualties(Casualty cas) {
        casualties.add(cas);
    }

    public void reduceCasualties(Casualty cas) {
        casualties.removeElement(cas);
    }

    public Vector<Casualty> getCasualties() {
        return casualties;
    }

    //agent lowering casualties on arrival to hospital
    public void loweringCasualties(Hospital hospital, double tnow) {
        //update casualties finite survival
        for (Casualty cas : casualties) {
            cas.setFiniteSurvival(tnow);
        }
        //add casualties to hospital
        hospital.addCasualties(casualties);
        //delete all casualties
        casualties.clear();
    }

    //*** Messages Methods ***//

    public MessageBox getAgentMessageBox() {
        return messageBox;
    }

    protected void putMessagesInMailerMailBox() {
        mailer.collectMailFromAgent(this, messageToBeSent);

    }


    //*** SPCN methods ***//

    public void createFirstMessages(){
        for(Task task:relevantTasksUtility.keySet()){
            createServiceMessage(-1.0, null, task);
        }
        putMessageInMailer();
    }
    public void createNewMessageSPCN() {
        messageToBeSent.clear();
        updateRelevantTaskUtility();
        HashMap<Task, Double> sortedTaskByUtility = sortByValue(relevantTasksUtility);
        int ranking = 0;
        setAvailableSkillsForAllocation(this.skills.getCapacity());
        for (Task task : sortedTaskByUtility.keySet()) {
            Message message = messageBox.getMessages().get(task.getId());
            if (IsItDoable(message, task)) {

                double startTime = relevantTasksArrivalTime.get(task);
                Vector<Skill> execution = ((UtilityMessage) message).getExecution();
                Capacity capacity = new Capacity(execution);
                updateExecutionPenalty(execution);
                allocateTask(ranking, task, startTime,execution);
                updateAvailableSkillsForAllocation(execution);
                updateNextTimeToAllocation(startTime, execution);
                createServiceMessage(startTime, capacity, task);
                ranking++;
            } else {
                double startTime = ArrivalTimeAccordingToCurrentAllocation(task);
                Vector<Skill> AvailableSkillsForAllocation = this.getAvailableSkillsForAllocation();
                Capacity capacity = new Capacity(AvailableSkillsForAllocation);
                createServiceMessage(startTime, capacity, task);
                updateRelevantTaskArrivalTime(startTime, task);
            }
        }
        putMessageInMailer();

    }

    private boolean IsItDoable(Message message, Task task) {
        double startTime = ArrivalTimeAccordingToCurrentAllocation(task);
        Vector<Skill> skills =((UtilityMessage) message).getExecution();
        if (startTime >= nextTimeToAllocation&&skills!=null) {
            if (AllSkillsAvailable(skills)) {
                return true;
            }
        }

        return false;
    }

    private void updateExecutionPenalty(Vector<Skill> skills){
        for(Skill skill:skills){
            if(skill instanceof Execution){
                ((Execution)skill).updatePenalty();
            }
        }
    }

    private double ArrivalTimeAccordingToCurrentAllocation(Task task) {
        double distance = getDistance(task);
        double timeArrival = nextTimeToAllocation + Probabilities.getTravelTime(distance, this.agentType);
        return timeArrival;
    }

    private boolean AllSkillsAvailable(Vector<Skill> skills) {
        for (Skill s : skills) {
            if (!availableSkillsForAllocation.contains(s)) {
                return false;
            }

        }
        return true;
    }

    private void updateRelevantTaskUtility() {
        for (Task task : this.relevantTasksUtility.keySet()) {
            Message message = messageBox.getMessages().get(task.getId());
            double utility = ((UtilityMessage) message).getUtility();
            relevantTasksUtility.replace(task,utility);
        }

    }

    private void updateAvailableSkillsForAllocation(Vector<Skill> execution) {
        availableSkillsForAllocation.removeAll(execution);
    }

    private void createServiceMessage(double timeArrival, Capacity skills, Task task) {
        ServiceMessage newMessage = new ServiceMessage(this.getId(), task.getId(), timeArrival, skills);
        messageToBeSent.add(newMessage);
    }

    private void allocateTask(int index, Task task, double timeArrival,Vector<Skill> execution) {
        Assignment assignment = new Assignment(this, task, 0, timeArrival,execution);
        currentAssignment[index] = assignment;
    }

    private void updateNextTimeToAllocation(double startTime, Vector<Skill> execution) {
        nextTimeToAllocation = startTime;
        for (Skill s : execution) {
            nextTimeToAllocation += s.getDuration();
        }
    }

    public void putMessageInMailer() {
        this.mailer.collectMailFromAgent(this, messageToBeSent);
    }

    private void updateRelevantTaskArrivalTime(double time, Task task) {
        relevantTasksArrivalTime.replace(task, time);
    }

    public static HashMap<Task, Double> sortByValue(HashMap<Task, Double> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<Task, Double>> list =
                new LinkedList<Map.Entry<Task, Double>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Task, Double>>() {
            public int compare(Map.Entry<Task, Double> o1,
                               Map.Entry<Task, Double> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Task, Double> temp = new LinkedHashMap<Task, Double>();
        for (Map.Entry<Task, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public void addRelevantDisasterSite(Task disasterSite) {
        this.relevantTasksArrivalTime.put(disasterSite, -1.0);
        this.relevantTasks.add(disasterSite);
        this.relevantTasksUtility.put(disasterSite, -1.0);
    }

    //********************************************************//


    @Override
    public void recieveMessage(List<TaskAllocation.Message> msgs) {

    }

    @Override
    public void createMessage(Messageable reciver, double context) {

    }


    //*** getters && setters ***//


    public void setAvailableSkillsForAllocation(Vector<Skill> availableSkillsForAllocation) {
        this.availableSkillsForAllocation = availableSkillsForAllocation;
    }

    public void setNextTimeToAllocation(double nextTimeToAllocation) {
        this.nextTimeToAllocation = nextTimeToAllocation;
    }


    public void setCurrentAssignment(Assignment[] currentAssignment) {
        this.currentAssignment = currentAssignment;
    }


    public double getLastTimeUpdate() {
        return lastTimeUpdate;
    }

    public int getNumOfAllocateTask() {
        return numOfAllocateTask;
    }

    public Vector<Skill> getAvailableSkillsForAllocation() {
        return availableSkillsForAllocation;
    }

    public Assignment[] getCurrentAssignment() {
        return currentAssignment;
    }

    public String toString() {
        return "\nMedical unit: " + this.id + " type : " + this.agentType + " " + this.skills;

    }
}


