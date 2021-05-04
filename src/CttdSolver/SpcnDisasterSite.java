package CttdSolver;

import CTTD.*;
import DCOP.Message;
import DCOP.MessageBox;
import PoliceTaskAllocation.AgentType;
import TaskAllocation.Agent;
import TaskAllocation.Location;
import jdk.jshell.execution.Util;

import java.util.*;

public class SpcnDisasterSite extends DisasterSite {




    int version ; // 1- survival 2- Weighted survival 3-Shapli value


    //***constructor***//

    public SpcnDisasterSite(double duration, int id, double startTime, int priority){
        super(duration,  id,startTime,  priority);
    }

    public SpcnDisasterSite(Location location, int id, double startTime, int priority){
        super(location,id,startTime,priority);
        relevantAgentsUtility=new HashMap<>();
        relevantAgentsTimeArrival=new HashMap<>();
        messageBox=new MessageBox(this.id);
        messageToBeSent=new Vector<>();

    }
    public SpcnDisasterSite(Location location, double duration, double startTime, int id,
                            int priority, double utility,
                            HashMap<AgentType, Integer> agentsRequired, ArrayList<Casualty> cas)
    {
        super(location,duration,startTime,id,
                priority,utility,agentsRequired,cas);

    }
    public SpcnDisasterSite(Location location, int id, double startTime) {
        super(location,id,startTime);
    }

    public SpcnDisasterSite(){
        super();
    }

    public SpcnDisasterSite(int version){
        super();
        this.version=version;
    }

    //*** Message Box ***//
    public MessageBox getAgentMessageBox() {
        return messageBox;
    }

   //*** getters & setters ***//

    public void setVersion(int version) {
        this.version = version;
    }


    //*** SPCN methods ***//


    public void createFirstMessages(){
        messageToBeSent.clear();
        updateDemands();//update the demands according to casualties on site

        for(Agent agent: relevantAgentsTimeArrival.keySet()){
            Message newMessage = new UtilityMessage(this.id,agent.getId(),-1);
            messageToBeSent.add(newMessage);
        }
        putMessageInMailer();
//    System.out.println("remain cover: "+remainCoverByCurrentAllocation);

    }
    public void createNewMessageSPCN(){
        relevantAgentsUtilityClearUtility();
        allocatedCasualties.clear();
        messageToBeSent.clear();
        updateDemands();//update the demands according to casualties on site
//    System.out.println("remain cover: "+remainCoverByCurrentAllocation);
        updateAgentsTimeArrivalMap();
        //sort all the relevant agents by the time arrival
        HashMap<Agent,Double> sortedAgentsByTimeArrival =sortByValue(relevantAgentsTimeArrival);

//    for(Agent agent: sortedAgentsByTimeArrival.keySet()){
//    Message message = messageBox.getMessages().get(agent.getId());
//     double utility= createUtilityMessage(message);//execution is null if agent not relevant
//      updateAgentsUtility(agent,utility);
////      updateRemainCoverByCurrentAllocation(utility);
////      System.out.println("agent: "+agent.getId()+ "utility"+ utility);
//
//    }
        while(!finishedAllocation()){
            sortedAgentsByTimeArrival =sortByValue(relevantAgentsTimeArrival);
            for(Agent agent: sortedAgentsByTimeArrival.keySet()){
                Message message = messageBox.getMessages().get(agent.getId());
                Capacity availableCapacity = calcAvailableCapacity(agent,((ServiceMessage)message).getCapacity());
                double arrivalTime=sortedAgentsByTimeArrival.get(agent);
                Casualty casualty = getNextCasualty(agent,arrivalTime);
                if(casualty==null){
                    relevantAgentsTimeArrival.replace(agent,-1.0);
                    continue;
                }
                Vector<Skill> allocation = allocateActivitiesToCasualty(casualty,availableCapacity,arrivalTime);

                if(allocation.size()<=0){
                    relevantAgentsTimeArrival.replace(agent,-1.0);
                    continue;
                }

                updateAgent(allocation,agent);
                break;
            }
        }
        createUtilityMessages();


        putMessageInMailer();

    }

    private Vector<Skill>allocateNextCas(Casualty casualty,Capacity availableCapacity,double arrivalTime){
        Vector<Skill> allocation=new Vector<>();
        this.sortCasualtiesBySurvival();
        for(int i=0; i< casualties.size();i++) {
            Casualty cas = casualties.get(i);
            if (CasualtyAllocated(cas)|| cas.equals(casualty)){
                continue;
            } else{
                allocation = allocateActivitiesToCasualty(cas,availableCapacity,arrivalTime);
                if(allocation.size()<=0){
                    continue;
                }
                else return allocation;
            }
        }
        return allocation;
    }

    private void relevantAgentsUtilityClearUtility(){
        for(Agent a:relevantAgentsUtility.keySet()){
            relevantAgentsUtility.get(a).removeAllElements();
        }
    }

    private void createUtilityMessages(){
        for(Agent agent:relevantAgentsUtility.keySet()){
            Vector <Skill> executions = relevantAgentsUtility.get(agent);
            Vector<Skill> s=new Vector<>();
            for(Skill sk:executions){
                Execution ex=new Execution(sk.getTriage(),sk.getActivity(),((Execution)sk).getCas(),((Execution)sk).getStartTime(),((Execution)sk).getUtility());
                s.add(ex);
            }
            double ratio = calcRatio(executions);

            Utility utility;
            if (version==1) {// the survival is the utility
                utility= new RatioUtility(ratio);
            }
            else if(version==2){
                utility=new WeightedUtility(ratio);

            }
            else if(version==3){
                utility=new ShapleyUtility(this,agent);

            }
            else{
                utility= new RatioUtility(ratio);
            }
            utility.calculateUtility();



            Message newMessage = new UtilityMessage(this.id,agent.getId(),ratio,s,utility);
            messageToBeSent.add(newMessage);
            System.out.println("utility message: task: "+this.id+" ratio: "+ratio+ "for agent: "+agent.getId());
        }
    }
    private void updateAgent(Vector<Skill> allocation,Agent agent){
        double duration=0;
        for(int i=0;i<allocation.size();i++){
            duration+=allocation.get(i).getDuration();
        }
        duration+=relevantAgentsTimeArrival.get(agent);
        relevantAgentsTimeArrival.replace(agent,duration);
        updateAgentsUtility(agent,allocation);

    }

    private  boolean finishedAllocation(){
        for(double a:relevantAgentsTimeArrival.values()){
            if (a>=0)
                return false;
        }
        return true;

    }

    private Capacity calcAvailableCapacity(Agent agent,Capacity capacity){
        Vector<Skill> skill = relevantAgentsUtility.get(agent);
        capacity.getSkills().removeAll(skill);
        return capacity;
    }

    private Casualty getNextCasualty(Agent agent,double time){
        //start with the urgent casualty
        this.sortCasualtiesBySurvival();
        for(int i=0; i< casualties.size();i++) {
            Casualty cas = casualties.get(i);
            if (CasualtyAllocated(cas)||!cas.isAgentRequired((MedicalUnit) agent,time)) {
                continue;
            } else
                return cas;
        }
        return null;
    }

    private boolean CasualtyAllocated(Casualty cas){
        if(allocatedCasualties.containsKey(cas)){
            //TODO- add activities allocation
            return true;
        }
        return false;
    }

    private Vector<Skill> allocateActivitiesToCasualty(Casualty cas,Capacity capacity,double arrivalTime) {
        Vector<Skill> activitiesAssignment = new Vector<>();
        double capacityScore = capacity.getCurrentScore();
        double time = arrivalTime;
        int j = 0;
        Activity[] activity = cas.getActivity();
        //if agent have free capacity
        if (capacityScore > 0) {
            if (cas.status != Casualty.Status.FINISHED) {
                //while tha cas has open activities
                while (j < activity.length && capacityScore > 0) {
                    //get cas next activity
                    Activity act = activity[activity.length - (1 + j)];
                    //check if the agent skill contains the demand and add to the vector
                    Skill s = new Execution(cas.getTriage(), act, cas, time);
                    if (isContains(s, capacity.getSkills()) && this.demands.contains(s)) {
                        activitiesAssignment.add(s);
                        //reduce agent capacity
                        capacityScore -= s.getScore();
                        //set time
                        time += s.getDuration();
                        //update site demands
                        this.reduceDemands(s);
                    }
                    if(j>0){
                        ((Execution)s).setUtility(0);
                    }
                    j++;
                }
            }
        }
        allocatedCasualties.put(cas,null);
        return activitiesAssignment;
    }

    public void updateRemainCoverByCurrentAllocation(double utility){
        remainCoverByCurrentAllocation-=(utility);
    }

    private void updateAgentsUtility(Agent agent,Vector<Skill> skills){
        Vector<Skill> s=new Vector<>();
        for(Skill sk:skills){
            Execution ex=new Execution(sk.getTriage(),sk.getActivity(),((Execution)sk).getCas(),((Execution)sk).getStartTime(),((Execution)sk).getUtility());
            s.add(ex);
        }
        if(this.relevantAgentsUtility.get(agent)==null){
            this.relevantAgentsUtility.replace(agent,s);
        }
        else{
            this.relevantAgentsUtility.get(agent).addAll(s);
        }

    }

    private void updateAgentsTimeArrivalMap(){

        for(Agent agent:this.relevantAgentsTimeArrival.keySet()){
            Message message = messageBox.getMessages().get(agent.getId());
            double timeArrival= ((ServiceMessage)message).getTimeArrival();
            relevantAgentsTimeArrival.replace(agent,timeArrival);
        }

    }

    public void putMessageInMailer(){
        this.mailer.collectMailFromTask(this,messageToBeSent);
    }

    public double createUtilityMessage(Message message){
        Vector<Skill> executions = calcExecution(((ServiceMessage)message).getCapacity(),((ServiceMessage)message).getTimeArrival());
        double ratio = calcRatio(executions);
        Utility utility;
        if (version==1) {// the survival is the utility
            utility= new RatioUtility(ratio);
        }
            else if(version==2){
            utility=new WeightedUtility(ratio);

        }
            else if(version==3){
            utility=new ShapleyUtility();

        }
            else{
            utility= new RatioUtility(ratio);
        }
            utility.calculateUtility();




        Message newMessage = new UtilityMessage(this.id,message.getSenderId(),ratio,executions,utility);
        messageToBeSent.add(newMessage);
        return ((UtilityMessage)newMessage).getUtility();
    }

    private Vector<Skill> calcExecution(Capacity capacity,double timeArrival){
        Vector<Skill> executions;
        //check if the agent relevant
        if(isServiceRequired(capacity,timeArrival)&&timeArrival>-1){
            executions = new Vector<>();
            executions.addAll(allocateSkillsToCasualty(capacity,timeArrival));
        }
        else
            return null;
        return executions;
    }

    private Vector<Skill> allocateSkillsToCasualty(Capacity capacity,double time){
        Vector<Skill> activitiesAssignment=new Vector<>();//the return
        double capacityScore = capacity.getCurrentScore();//the agent capacity
        //start with the urgent casualty
        this.sortCasualtiesBySurvival();
        for(int i=0; i< casualties.size();i++) {
            Casualty cas = casualties.get(i);

            if(allocatedCasualties.keySet().contains(cas)){
                continue;
            }
            int j = 0;
            Activity[] activity = cas.getActivity();
            //if agent have free capacity
            if (capacityScore > 0) {
                if (cas.status != Casualty.Status.FINISHED) {
                    //while tha cas has open activities
                    while (j < activity.length && capacityScore > 0) {
                        //get cas next activity
                        Activity act = activity[activity.length - (1 + j)];
                        //check if the agent skill contains the demand and add to the HashMap
                        Skill s = new Execution(cas.getTriage(), act, cas, time);
                        if (isContains(s, capacity.getSkills()) && this.demands.contains(s)) {
                            activitiesAssignment.add(s);
                            //reduce agent capacity
                            capacityScore -= s.getScore();
                            //set time
                            time += s.getDuration();
                            //update site demands
                            this.reduceDemands(s);
                        }
                        j++;
                    }
                }
            }
        }


        return activitiesAssignment;
    }


    public boolean isServiceRequired(Capacity skills, double timeArrival){
        //check time arrival
        if(isTimeArrivalRelevant(timeArrival)&&skills!=null){
            //check if disaster site demands  fits to agent skills.
            for(Skill s: demands){
                if(skills.getSkills().contains(s)){
                    return true;
                }
            }
        }
        return false;
    }

    private double calcRatio(Vector<Skill> executions){
        double utility=0;
        if(executions==null){
            return -1;
        }
        else{
            for(Skill ex:executions){
                utility+= ((Execution)ex).getUtility();
            }
        }
        return utility;
    }

    public static HashMap<Agent, Double> sortByValue(HashMap<Agent, Double> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<Agent, Double> > list =
                new LinkedList<Map.Entry<Agent, Double> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Agent, Double> >() {
            public int compare(Map.Entry<Agent, Double> o1,
                               Map.Entry<Agent, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });


        // put data from sorted list to hashmap
        HashMap<Agent, Double> temp = new LinkedHashMap<Agent, Double>();
        for (Map.Entry<Agent, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    //**** Shapley ****//

    public double calcShapleyValue(Agent agent){


    }




}
