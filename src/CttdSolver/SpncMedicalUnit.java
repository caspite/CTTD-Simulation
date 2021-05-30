package CttdSolver;

import CTTD.*;
import DCOP.Message;
import DCOP.MessageBox;
import PoliceTaskAllocation.AgentType;
import TaskAllocation.Assignment;
import TaskAllocation.Location;
import TaskAllocation.Task;

import java.util.*;

public class SpncMedicalUnit extends MedicalUnit{

    int version;// 1- survival 2- Weighted survival 3-Shapli value

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
    double currentCapacity;
    double currentSimulationTime;


    //*** execution variables***//
    private TreeMap<Double, Skill> currentTaskSchedule;// the current task schedule-upcoming
    protected Vector<Task> relevantTasks;//the current relevant tasks

//**************************************************************************************//

    //**** constructor ****//
    public SpncMedicalUnit(int version){
        super();

        this.version=version;
    }

    public SpncMedicalUnit(Location location, int id, HashSet<AgentType> agentType) {
        super(location, id, agentType);
        this.version=version;
    }

    public SpncMedicalUnit(int id, AgentType agentType, Location loc) {
        super(id, agentType, loc);
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

    //*** getters & setters ***//

    public void setVersion(int version) {
        this.version = version;
    }


    //*** Messages Methods ***//


    protected void putMessagesInMailerMailBox() {
        mailer.collectMailFromAgent(this, messageToBeSent);

    }


    //*** SPCN methods ***//

    public void createFirstMessages(){
        for(Task task:relevantTasksUtility.keySet()){
            updateCurrentCapacity();
            currentSimulationTime=nextTimeToAllocation;
            double time=ArrivalTimeAccordingToCurrentAllocation(task);
            Vector<Skill> AvailableSkillsForAllocation = getAvailableSkillsForAllocation();
            Capacity capacity = new Capacity(AvailableSkillsForAllocation,currentCapacity);
            createServiceMessage(time, capacity, task);
            updateRelevantTaskArrivalTime(time, task);

        }
        putMessageInMailer();
    }
    public void createNewMessageSPCN() {
        nextTimeToAllocation=currentSimulationTime;
        messageToBeSent.clear();
        updateRelevantTaskUtility();
        updateCurrentCapacity();
        HashMap<Task, Double> sortedTaskByUtility = sortByValue(relevantTasksUtility);

        int ranking = 0;
        setAvailableSkillsForAllocation(this.skills.getSkills());

        for (Task task : sortedTaskByUtility.keySet()) {
            Message message = messageBox.getMessages().get(task.getId());
            if (IsItDoable(message, task)&&ranking<getCurrentAssignment().length) {

                double startTime = relevantTasksArrivalTime.get(task);
                Vector<Skill> execution = ((UtilityMessage) message).getExecution();
                double ratio =((UtilityMessage) message).getRatio();
                //Capacity capacity = new Capacity(execution,currentCapacity); //TODO - all execution
                Capacity capacity = new Capacity(getAvailableSkillsForAllocation(),currentCapacity);


                updateExecutionPenalty(execution,1);
                allocateTask(ranking, task, startTime,execution, ratio);
                updateAvailableSkillsForAllocation(execution);
                updateNextTimeToAllocation(startTime, execution);
                createServiceMessage(startTime, capacity, task);
                ((SpcnDisasterSite)task).updateRemainCoverByCurrentAllocation(ratio);

                ranking++;
            } else {
                double startTime = ArrivalTimeAccordingToCurrentAllocation(task);
                Vector<Skill> AvailableSkillsForAllocation = getAvailableSkillsForAllocation();
                Capacity capacity = new Capacity(AvailableSkillsForAllocation,currentCapacity);
                createServiceMessage(startTime, capacity, task);
                updateRelevantTaskArrivalTime(startTime, task);
            }
        }
        putMessageInMailer();

    }

    private void updateCurrentCapacity(){
        this.currentCapacity=skills.getCurrentScore();
    }

    private boolean IsItDoable(Message message, Task task) {

        double startTime = ArrivalTimeAccordingToCurrentAllocation(task);


        Vector<Skill> skills =((UtilityMessage) message).getExecution();
        double timeArrival = relevantTasksArrivalTime.get(task);
        if (timeArrival>=startTime&&skills!=null) {
            if (AllSkillsAvailable(skills)&&skills.size()>0) {
                return true;
            }
        }

        return false;
    }

    protected void updateExecutionPenalty(Vector<Skill> skills,int factor){
        for(Skill skill:skills){
            if(skill instanceof Execution){
                ((Execution)skill).updatePenalty(factor);
            }
        }
    }

    public double ArrivalTimeAccordingToCurrentAllocation(Task task) {
        double distance = getDistance(task);
        for(int i=0;i<currentAssignment.length;i++) {
            if(currentAssignment[i]!=null){
                distance = currentAssignment[i].getTask().getDistance(task);
            }
        }
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

    protected void updateAvailableSkillsForAllocation(Vector<Skill> execution) {
        availableSkillsForAllocation.removeAll(execution);
        for(Skill skill:execution){
            currentCapacity-=skill.getScore();
        }


    }

    private void createServiceMessage(double timeArrival, Capacity skills, Task task) {
        ServiceMessage newMessage = new ServiceMessage(this.getId(), task.getId(), timeArrival, skills);
        messageToBeSent.add(newMessage);
        System.out.println("agent "+this.id+" agent type: "+this.agentType+" task: "+task.getId()+"arrival: "+ timeArrival);
    }

    protected void allocateTask(int index, Task task, double timeArrival,Vector<Skill> execution,double utility) {
        Assignment assignment = new Assignment(this, task, utility, timeArrival,execution);
        currentAssignment[index] = assignment;
    }

    protected void updateNextTimeToAllocation(double startTime, Vector<Skill> execution) {
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
        Collections.reverse(list);

        // put data from sorted list to hashmap
        HashMap<Task, Double> temp = new LinkedHashMap<Task, Double>();
        for (Map.Entry<Task, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }




    public void addRelevantDisasterSite(Task disasterSite,double tnow) {
        this.relevantTasksArrivalTime.put(disasterSite, tnow);
        this.relevantTasks.add(disasterSite);
        this.relevantTasksUtility.put(disasterSite, -1.0);
    }


    //*** getters && setters ***//
    public MessageBox getAgentMessageBox() {
        return messageBox;
    }


    public void setAvailableSkillsForAllocation(Vector<Skill> availableSkillsForAllocation) {
        this.availableSkillsForAllocation =new Vector<Skill>();
        for(Skill s:availableSkillsForAllocation){
            Skill newSkill = new Skill(s.getTriage(),s.getActivity(),s.getDuration(),s.getScore());
            this.availableSkillsForAllocation.add(newSkill);
        }
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


}
