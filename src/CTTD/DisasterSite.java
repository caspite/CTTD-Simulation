package CTTD;

import PoliceTaskAllocation.AgentType;
import TaskAllocation.Location;
import TaskAllocation.Task;

import java.util.*;

public class DisasterSite extends Task {

  private boolean InfoCompleted; //complete info activity
   //update task hardConstraintTime -The latest time that can wait the urgent casualty at th DS
  protected ArrayList<Casualty> casualties= new ArrayList();  //casualties list

  public void setCasualties(List<Casualty> casualties) {
    this.casualties.addAll(casualties);
  }
  //sort casualties
  /*sort by triage*/
  private void SortCasualtiesByTriage(){
    Collections.sort(casualties, new Comparator<Casualty>() {
      @Override
      public int compare(Casualty o1, Casualty o2) {

        return Integer.valueOf(o1.priority).compareTo(o2.priority);
      }
    });
    System.out.println(casualties);
}
  /*sort by survival*/
  private void SortCasualtiesBySurvival(){

    Collections.sort(casualties, new Comparator<Casualty>() {
      @Override
      public int compare(Casualty o1, Casualty o2) {

        return Double.valueOf(o1.survival).compareTo(o2.survival);
      }
    });
  }

  //workload calculate according to the current time of the simulation
  public void WorkloadCalculate(double Tnow) {
    for(Casualty cas:casualties) {
      for (double f:cas.getActivityDuration(cas.status,cas.survival).values() ){
       workload+=f;
      }
    }
    lastChange=Tnow;
  }


  //constructors---------------------------------------------//
    public DisasterSite(double duration, int id, int priority){
        super(duration,  id,  priority);
    }
    public DisasterSite(Location location, double duration, double startTime, int id,
                        int priority, double utility,
                        HashMap<AgentType, Integer> agentsRequired, ArrayList<Casualty> cas)
    {
        super(location,duration,startTime,id,
        priority, utility,agentsRequired);
      this.casualties.addAll(cas);
      SortCasualtiesBySurvival();
    }
   //----------------------------------------------------------//


















  }
