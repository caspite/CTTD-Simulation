package CTTD;

import CttdSolver.FirstMessage;
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

  private boolean isInformed;//complete info activity
  protected ArrayList<Casualty> casualties= new ArrayList();  //casualties list
  private double remainCover;//the current score of the disaster site according to casualties
  private double initScore;// the init disaster site score
  private Vector <Skill> demands;//the disaster site demands according to casualties
  private TreeMap<Double, Skill> activitiesSchedule;
  private boolean finished;

  //*** Message Variables ***//
  MessageBox messageBox;// income last message from each agent
  Vector<Message> messageToBeSent;

  //*** allocation variables ***//
  HashMap<Integer,Vector<Execution>> currentAllocation;//agents id,  execution - after confirm message
  HashMap<Agent,Double> relevantAgentsTimeArrival;//relevant agents and time arrival according to confirm massage -1 for non allocation
  HashMap<Agent,Double> utilityForAgent;



//----------------------------------methods---------------------------------------------------//

  //***constructors***//
  public DisasterSite(double duration, int id,double startTime, int priority){
    super(duration,  id,  priority);
    this.missionArrivalTime=startTime;
  }

  public DisasterSite(Location location, int id,double startTime, int priority){
    super(location,id,startTime);
    this.missionArrivalTime=startTime;
    this.finished=false;

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

  }
  //-------------------------------getters and setters---------------------------//

  public void setStarted(boolean isStarted) {
    this.isStarted = isStarted;
    this.isInformed=true;
  }


  public void setInitCasualties(List<Casualty> casualties)
  {
    this.casualties.addAll(casualties);
    setRemainCover();
    this.initScore=this.remainCover;
    initDemands();
    hardConstraintTime();
//    this.setTriageActivityHashMap();
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
  public void setRemainCover(){
    this.remainCover=0;
    for(Casualty cas:this.casualties){
      this.remainCover+=cas.survival;
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
    System.out.println(casualties);
  }
  /*sort by survival*/
  private void sortCasualtiesBySurvival(){

    Collections.sort(casualties, new Comparator<Casualty>() {
      @Override
      public int compare(Casualty o1, Casualty o2) {

        return Double.valueOf(o1.survival).compareTo(o2.survival);
      }
    });
  }
  //update task hardConstraintTime -The latest time that can wait the urgent casualty at th DS
  public void hardConstraintTime(){
      this.sortCasualtiesBySurvival();
      this.hardConstraintTime=this.casualties.get(0).timeToSurvive;
  }

  public double getRemainCover(){return remainCover;}
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
        }
      }
      //update remain cover according to the casualties on the site
      setRemainCover();
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
        if(mu.skills.getCapacity().contains(s)){
          return true;
        }
      }
    }
      return false;
  }


public void reduceDemands(Skill skill){
    //update disaster site demands according to casualties on site
  this.updateDemands();
    if(demands.contains(skill))
      this.demands.remove(skill);
    else
      System.out.println("Skill not exist!!!");
}

public void updateDemands(){
    this.demands.clear();
  for(Casualty cas:casualties) {
    Triage trg = cas.getTriage();
    Activity[] act = cas.getActivity();
    for (int i = 0; i < act.length; i++) {
      Activity a = (Activity) Array.get(act, i);
      demands.add(new Skill(trg, a));
    }
  }
}
  private double AgentTimeToTravel(MedicalUnit medicalUnit){

   return Distance.travelTime(medicalUnit,this);
  }
  private boolean isTimeArrivalRelevant(double time){
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
    double capacityScore = capacity.getCurrentScore();//the agent capacity//TODO potential bug!! check if update capacity --> agent skills

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
                if (isContains(s,capacity.getCapacity())&&this.demands.contains(s)) {
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
  private boolean isContains(Skill s,Vector<Skill> skills){
    for(Skill skill:skills){
      if(s.equals(skill))
        return true;
    }
    return false;
  }

  public void reduceCasualties(Casualty cas){
    casualties.remove(cas);
    setRemainCover();
    if (casualties.size()<=0){
      finished=true;
      System.out.println("disaster site: "+this.id+" finished. finale score: "+remainCover+" ---------------------------------------------------------------------");

    }

  }

  //*** Message Methods ***//







  //*** SPCN methods ***//




  public void CreateNewMessageSPCN(){
    messageToBeSent.clear();
    updateAgentsTimeArrivalMap();
    //sort all the relevant agents by the time arrival
    HashMap<Agent,Double> sortedAgentsByTimeArrival =sortByValue(relevantAgentsTimeArrival);

    for(Agent agent: sortedAgentsByTimeArrival.keySet()){
    Message message = messageBox.getMessages().get(agent.getId());
      createUtilityMessage(message);//execution is null if agent not relevant
    }
    putMessageInMailer();

  }

  private void updateAgentsTimeArrivalMap(){

    for(Agent agent:this.relevantAgentsTimeArrival.keySet()){
      Message message = messageBox.getMessages().get(agent.getId());
     double timeArrival= ((ServiceMessage)message).getTimeArrival();
     relevantAgentsTimeArrival.replace(agent,timeArrival);
    }

  }

  public void putMessageInMailer(){}

  private void readAllMessages(){}


  public void createUtilityMessage(Message message){
    Vector<Skill> executions = calcExecution(((ServiceMessage)message).getCapacity(),((ServiceMessage)message).getTimeArrival());
    double utility =calcUtility(executions);
    Message newMessage = new UtilityMessage(this.id,message.getSenderId(),utility,executions);
    messageToBeSent.add(newMessage);
  }

  private Vector<Skill> calcExecution(Capacity capacity,double timeArrival){
    Vector<Skill> executions =new Vector<>();
    //check if the agent relevant
    if(isServiceRequired(capacity,timeArrival)&&timeArrival>-1){
      executions = new Vector<>();
      executions.addAll(allocateSkillsToCasualties(capacity,timeArrival).values());
    }
    else
      return null;
    return executions;
  }

  public boolean isServiceRequired(Capacity skills, double timeArrival){
    //check time arrival
    if(isTimeArrivalRelevant(timeArrival)){
      //check if disaster site demands  fits to agent skills.
      for(Skill s: demands){
        if(skills.getCapacity().contains(s)){
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



  public static HashMap<Agent, Double> sortByValue(HashMap<Agent, Double> hm)
  {
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






  public String toString(){
    return "\nDisaster site: "+this.id+" init score: "+this.initScore+" remain cover: "+this.remainCover+" number of casualties on site: "+casualties.size()+" time arrival: "+ this.getStartTime();
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
