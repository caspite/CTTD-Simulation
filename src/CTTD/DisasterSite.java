package CTTD;

import PoliceTaskAllocation.AgentType;
import PoliceTaskAllocation.MissionEvent;
import TaskAllocation.Agent;
import TaskAllocation.Assignment;
import TaskAllocation.Location;

import java.lang.reflect.Array;
import java.util.*;

public class DisasterSite extends MissionEvent {

  private boolean isInformed;//complete info activity
  protected ArrayList<Casualty> casualties= new ArrayList();  //casualties list
  private double remainCover;//the current score of the disaster site according to casualties
  private double initScore;// the init disaster site score
  private Vector <Skill> demands;//the disaster site demands according to casualties
  private TreeMap<Double, Skill> activitiesSchedule;
  private boolean finished;
  //---------------------------constructors---------------------------------------------//
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

  public Vector <Skill> activitiesAssignment(Assignment as) {
    Vector<Skill> actAs=new Vector<>();
    MedicalUnit mu= (MedicalUnit)as.getAgent();

    //if agent required to the task
    if(isAgentRequired(mu,as.getArrivalTime()))
    {
      //start with the urgent casualty
      this.sortCasualtiesBySurvival();
      for(int i=0; i< casualties.size();i++){
        Casualty cas=casualties.get(i);
        //if agent have free capacity
        if(!mu.isFull) {
          if (cas.status != Casualty.Status.FINISHED) {
            //check if agent skills and time arrival is relevant
            if (cas.isAgentRequired(mu, as.getArrivalTime())) {
              //while tha cas has open activities
              while(cas.getActivity().length>0&&!mu.isFull){
                //get cas next activity
                Activity act = cas.getNextActivity();
                //check if the agent skill contains the demand and add to the HashMap
                Skill s = new Skill(cas.getTriage(), act);
                if (mu.skills.getCapacity().contains(s)) {
                  actAs.add(s);
                  //reduce activity from cas and change status
                  Casualty.Status st=cas.getStatus();
                  cas.setStatus(st);
                  cas.setActivity(act);
                  //reduce agent capacity
                  mu.reduceCapacity(s);
                  // update casualties on the  if the action is uploading
                  if(act.equals(Activity.UPLOADING)){
                    mu.setCasualties(cas);
                    reduceCasualties(cas);
                  }
                }
                System.out.println("Agent: "+mu.getId()+" cas id: "+cas.id+" act: "+act);
              }
            }
          }
        }
      }
    }
    return actAs;
  }

  private void reduceCasualties(Casualty cas){
    casualties.remove(cas);
    setRemainCover();
    if (casualties.size()<=0){
      finished=true;
      System.out.println("disaster site: "+this.id+" finished. finale score: "+remainCover+" ---------------------------------------------------------------------");

    }

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
