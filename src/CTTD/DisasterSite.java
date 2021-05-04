package CTTD;

import CttdSolver.ServiceMessage;
import CttdSolver.UtilityMessage;
import DCOP.*;
import PoliceTaskAllocation.AgentType;
import PoliceTaskAllocation.MissionEvent;
import TaskAllocation.Agent;
import TaskAllocation.Assignment;
import TaskAllocation.Location;

import java.lang.reflect.Array;
import java.util.*;


public class DisasterSite extends MissionEvent  {

  protected boolean isInformed;//complete info activity
  protected ArrayList<Casualty> casualties= new ArrayList();  //casualties list
  protected ArrayList<Casualty> finishedCasualties= new ArrayList();  //casualties list

  protected double remainCover;//the current score of the disaster site according to casualties
  protected double remainCoverByCurrentAllocation;
  protected double initScore;// the init disaster site score
  protected Vector <Skill> demands;//the disaster site demands to casualties
  protected TreeMap<Double, Skill> activitiesSchedule;
  protected boolean finished;
  protected double evacuationTime;//Estimated average travel time to a nearby hospital

  //*** Message Variables ***//
  protected MessageBox messageBox;// income last message from each agent
  protected Vector<Message> messageToBeSent;

  //*** allocation variables ***//
  protected HashMap<Integer,Vector<Execution>> currentAllocation;//agents id,  execution - after confirm message
  protected HashMap<Agent,Double> relevantAgentsTimeArrival;//relevant agents and time arrival according to confirm massage -1 for non allocation
  protected HashMap<Agent,Vector<Skill>> relevantAgentsUtility;
  protected HashMap<Casualty,Activity[]> allocatedCasualties=new HashMap<>();



// *** methods *** //

  //***constructors***//
  public DisasterSite(){
    super();
  }
  public DisasterSite(double duration, int id,double startTime, int priority){
    super(duration,  id,  priority);
    this.missionArrivalTime=startTime;
  }

  public DisasterSite(Location location, int id,double startTime, int priority){
    super(location,id,startTime);
    this.missionArrivalTime=startTime;
    this.finished=false;
    this.priority=priority;
    relevantAgentsUtility=new HashMap<>();
    relevantAgentsTimeArrival=new HashMap<>();
    messageToBeSent=new Vector<>();
    messageBox=new MessageBox(this.id);
  }
  public DisasterSite(Location location, double duration, double startTime, int id,
                      int priority, double utility,
                      HashMap<AgentType, Integer> agentsRequired, ArrayList<Casualty> cas)
  {
    super(location,duration,startTime,id,
            priority, utility,agentsRequired);
    this.setInitCasualties(cas);
    sortCasualtiesBySurvival();
  }
  public DisasterSite(Location location,int id,double startTime) {
    super(location,id,startTime);
//    this.initializeTriageNum();
    this.finished=false;
    relevantAgentsUtility=new HashMap<>();
    relevantAgentsTimeArrival=new HashMap<>();
    messageBox=new MessageBox(this.id);


  }
  // *** getters and setters *** //


  public ArrayList<Casualty> getCasualties() {
    return casualties;
  }

  public void setStarted(boolean isStarted) {
    this.isStarted = isStarted;
    this.isInformed=true;
  }


  public void setInitCasualties(List<Casualty> casualties)
  {
    this.casualties.addAll(casualties);
    updateRemainCover();
    this.initScore=this.remainCover;
    initDemands();
    hardConstraintTime();
//    this.setTriageActivityHashMap();
  }

  public void setEvacuationTime(double evacuationTime){
    this.evacuationTime=evacuationTime;
  }

  public double getEvacuationTime() {
    return evacuationTime;
  }

  private void initDemands(){
    demands=new Vector<>();
    activitiesSchedule=new TreeMap<>();
    sortCasualtiesByTriage();
    for(Casualty cas:casualties){
      Triage trg=cas.getTriage();
      Activity[] act =cas.getActivity();
      for(int i=0;i<act.length;i++){
        Activity a=(Activity)Array.get(act,i);
        demands.add(new Skill(trg,a));
      }

    }
  }
//priority equals to the number of urgent casualties on the site
  public void setPriority(){
      int urg=0;
      //get all urgent casualties
    for (Casualty i:this.casualties){
      if (i.getTriage()==Triage.URGENT) {urg+=1;}
    }
    this.priority=urg;
  }

  //remain cover according to casualties
  public void updateRemainCover(){
    this.remainCover=0;
    for(Casualty cas:this.casualties){
      this.remainCover+=cas.getSurvival();
    }
    for(Casualty cas:this.finishedCasualties){
      this.remainCover += cas.getFiniteSurvival();
    }
  }



  /*sort by triage*/
  private void sortCasualtiesByTriage(){
    Collections.sort(casualties, new Comparator<Casualty>() {
      @Override
      public int compare(Casualty o1, Casualty o2) {

        return Integer.valueOf(o1.priority).compareTo(o2.priority);
      }
    });
   // System.out.println(casualties);
  }
  /*sort by survival*/
  protected void sortCasualtiesBySurvival(){

    Collections.sort(casualties, new Comparator<Casualty>() {
      @Override
      public int compare(Casualty o1, Casualty o2) {

        return Double.valueOf(o1.getSurvival()).compareTo(o2.getSurvival());
      }
    });
  }
  //update task hardConstraintTime -The latest time that can wait the urgent casualty at th DS
  public void hardConstraintTime(){
      this.sortCasualtiesBySurvival();
      this.hardConstraintTime=this.casualties.get(0).timeToSurvive;
  }

  public double getRemainCover(){return remainCover;}

  public double getRemainCoverByCurrentAllocation() {
    return remainCoverByCurrentAllocation;
  }

  //----------------------------------------------------------------------------//

  private void updateActivitiesSchedule(double time,Skill skill){
    activitiesSchedule.put(time,skill);
  }

  public void removeCasualty(Casualty cas){
    //check casualty status - end
    if(cas.status.equals(Casualty.Status.FINISHED))
    {
      //search the cas at the casualties list
      for(Casualty c:casualties){
        if(c.equals(cas)){
          casualties.remove(c);
          finishedCasualties.add(cas);
        }
      }
      //update remain cover according to the casualties on the site
      updateRemainCover();
    }
    else{
      System.out.println("casualty not finished");
    }

  }
  public boolean isAgentRequired(Agent agent, double Tnow){
    MedicalUnit mu=(MedicalUnit)agent;
    //check time arrival
    if(isTimeArrivalRelevant(AgentTimeToTravel(mu)+Tnow)){
      //check if disaster site demands  fits to agent skills.
      for(Skill s: demands){
        if(mu.skills.getSkills().contains(s)){
          return true;
        }
      }
    }
      return false;
  }


public void reduceDemands(Skill skill){
    if(demands.contains(skill))
      this.demands.remove(skill);
    else
      System.out.println("Skill not exist!!!");
}

public void updateDemands(){
    this.demands.clear();
    this.allocatedCasualties.clear();
  for(Casualty cas:casualties) {
    Triage trg = cas.getTriage();
    Activity[] act = cas.getActivity();
    for (int i = 0; i < act.length; i++) {
      Activity a = (Activity) Array.get(act, i);
      demands.add(new Skill(trg, a));
    }
    updateRemainCover();
    remainCoverByCurrentAllocation=remainCover;
  }

}
  private double AgentTimeToTravel(MedicalUnit medicalUnit){

   return Distance.travelTime(medicalUnit,this);
  }
  protected boolean isTimeArrivalRelevant(double time){
    if(this.getHardConstraintTime()>time){
      return true;
    }
    else{
      return false;
    }
  }

  public boolean finished(){
    return finished;
  }

  /***
   * assignment agent skill regardless of the schedule
   * @param as assignment agent to task
   * @return TreeMap - start time for each skill
   */
  public TreeMap<Double,Skill> activitiesAssignment(Assignment as) {
    TreeMap<Double,Skill> activitiesAssignment=new TreeMap<>();//the return
    MedicalUnit mu= (MedicalUnit)as.getAgent();//the agent
    Capacity capacity = mu.getAgentSkills();//agent free skills
    double time=as.getArrivalTime();//the current start time for each activity
    if(isAgentRequired(mu,time)){
      activitiesAssignment = allocateSkillsToCasualties(capacity,time);
    }
    return activitiesAssignment;
  }

  private TreeMap<Double,Skill>  allocateSkillsToCasualties(Capacity capacity,double time){
    TreeMap<Double,Skill> activitiesAssignment=new TreeMap<>();//the return
    double capacityScore = capacity.getCurrentScore();//the agent capacity
//    this.updateDemands();
      //start with the urgent casualty
      this.sortCasualtiesBySurvival();
      for(int i=0; i< casualties.size();i++){
        int j=0;
        Casualty cas=casualties.get(i);
        Activity[] activity=cas.getActivity();
        //if agent have free capacity
        if(capacityScore>0) {
          if (cas.status != Casualty.Status.FINISHED) {
            //check if agent skills and time arrival is relevant
              //while tha cas has open activities
              while(j<activity.length&&capacityScore>0){
                //get cas next activity
                Activity act = activity[activity.length-(1+j)];
                //check if the agent skill contains the demand and add to the HashMap
                Skill s = new Execution(cas.getTriage(), act,cas,time);
                if (isContains(s,capacity.getSkills())&&this.demands.contains(s)) {
                  activitiesAssignment.put(time,s);
                  //reduce agent capacity
                  capacityScore-=s.getScore();
                  //set time
                  time+=s.getDuration();
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
  protected boolean isContains(Skill s,Vector<Skill> skills){
    for(Skill skill:skills){
      if(s.equals(skill))
        return true;
    }
    return false;
  }

  public void reduceCasualties(Casualty cas){
    casualties.remove(cas);
    updateRemainCover();
    if (casualties.size()<=0){
      finished=true;
      System.out.println("disaster site: "+this.id+" finished. finale score: "+remainCover+" ---------------------------------------------------------------------");

    }

  }


//*** Message Box ***//
public MessageBox getAgentMessageBox() {
  return messageBox;
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
      double utility =calcUtility(executions);
      Message newMessage = new UtilityMessage(this.id,agent.getId(),utility,s);
      messageToBeSent.add(newMessage);
      System.out.println("utility message: task: "+this.id+" utility: "+utility+ "for agent: "+agent.getId());
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
    double utility =calcUtility(executions);

    Message newMessage = new UtilityMessage(this.id,message.getSenderId(),utility,executions);
    messageToBeSent.add(newMessage);
    return utility;
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

  private double calcUtility(Vector<Skill> executions){
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

public void addRelevantAgent(Agent agent){
    Vector<Skill> skills=new Vector<>();
    this.relevantAgentsUtility.put(agent,skills);
    this.relevantAgentsTimeArrival.put(agent,0.0);
}

public int getUrgentCasAmount(){
    int count=0;
    for(Casualty cas:casualties){
      if (cas.triage==Triage.URGENT){
        count++;
      }
    }
    return count;
}

  public int getMediumCasAmount(){
    int count=0;
    for(Casualty cas:casualties){
      if (cas.triage==Triage.MEDIUM){
        count++;
      }
    }
    return count;
  }

  public int getNonUrgentCasAmount(){
    int count=0;
    for(Casualty cas:casualties){
      if (cas.triage==Triage.NONURGENT){
        count++;
      }
    }
    return count;
  }

//***********************************************************//


  public String toString(){
    return "\nDisaster site: "+this.id+" init score: "+this.initScore+" remain cover (algorithm): "+this.remainCoverByCurrentAllocation;
//            +" number of casualties on site: "+casualties.size()+" time arrival: "+ this.getStartTime();
  }
}
//  private HashMap<Triage,Integer> triageNum;
//  private HashMap<Triage,Activity> triageActivityHashMap;

//  public void setTriageNum(){
//    //TODO add number for each triage
//    for(Casualty cas:this.casualties) {
//      int temp=this.triageNum.get(cas.triage);
//      temp+=1;
//      this.triageNum.put(temp,)
//
//      System.out.println(this.triageNum.get(cas.triage));
//    }
//    }

//-----------------------------set methods---------------------------------//

//  public void setTriageActivityHashMap(){
//    for (Casualty cas: this.casualties){
//      for (Activity i:cas.getActivity()){
//        triageActivityHashMap.put(cas.triage,i);
//      }
//    }
//  }

//  //workload calculate according to the current time of the simulation
//  public void WorkloadCalculate(double Tnow) {
//    for(Casualty cas:casualties) {
//      for (double f:cas.getActivityDuration().values() ){
//       workload+=f;
//      }
//    }
//    lastChange=Tnow;
//  }

//  private void initializeTriageNum(){
//
//    this.triageNum=new HashMap<Triage,Integer>();
//    this.triageNum.put(Triage.URGENT,0);
//    this.triageNum.put(Triage.MEDIUM,0);
//    this.triageNum.put(Triage.NONURGENT,0);
//
//
//  }
