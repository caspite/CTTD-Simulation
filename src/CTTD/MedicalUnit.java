package CTTD;

import PoliceTaskAllocation.AgentType;
import TaskAllocation.Agent;
import TaskAllocation.Assignment;
import TaskAllocation.Location;

import java.util.*;

public class MedicalUnit extends Agent  {

    Capacity skills;
    AgentType agentType;
    boolean isFull;
    Vector<Casualty>casualties; // the casualties that uploaded to the agent
    private TreeMap<Double, Skill> currentTaskSchedule;// the current task schedule-upcoming


    //--------------------constructor----------------------------------//

    public MedicalUnit(Location location, int id, HashSet<AgentType> agentType) {
        super(location, id, agentType);
    }

    public MedicalUnit(int id, AgentType agentType, Location loc) {
        super();
       this.id=id;
       this.agentType=agentType;
       this.location=loc;
       skills=new Capacity(agentType);
       isFull=false;
       casualties=new Vector<>();
       currentTaskSchedule=new TreeMap<>();
    }


    //------------------------------- getters & setters ------------------------------------//
    //Travel duration depends on the agent's type
    @Override
    public void setMovingTime(double dis) {
        Probabilities.getTravelTime(dis,agentType);
        }

    public double getTravelTime(double dis) {
        return Probabilities.getTravelTime(dis,agentType);

    }

    public AgentType getOneAgentType(){return  this.agentType;}



    public Capacity getAgentSkills(){
        return skills;
    }

    //update activities schedule
    private void setCurrentTaskSchedule(double time,Skill skill){
        currentTaskSchedule.put(time,skill);
    }


    public void reduceCapacity(Skill skill){
        Activity act = skill.getActivity();
        Triage trg =skill.getTriage();

        skills.reduceCap(trg,act);
        if(skills.getCurrentScore()<=0){
            setIsFull(true);
        }

    }


    public double getActivityTime(Triage trg, Activity act){

    return skills.getduration(trg,act);
    }

    private void setIsFull(boolean b){
        isFull=b;
    }

    public void setCasualties(Casualty cas){
        casualties.add(cas);
    }

    public void reduceCasualties(Casualty cas){
        casualties.removeElement(cas);
    }
    public Vector<Casualty> getCasualties(){
        return casualties;
    }

    //agent lowering casualties on arrival to hospital
    public void loweringCasualties(Hospital hospital,double tnow){
        //update casualties finite survival
        for(Casualty cas:casualties){
            cas.setFiniteSurvival(tnow);
        }
        //add casualties to hospital
        hospital.addCasualties(casualties);
        //delete all casualties
        casualties.clear();
    }
 public void reloadCapacity(){
        this.skills.initializeCapacity(agentType);
 }
    public void updateCapacity(double time){
        //check which skills done reduce the capacity and remove from upcoming.

        // Get a set of the entries
        Set set = currentTaskSchedule.entrySet();

        // Get an iterator
        Iterator it = set.iterator();

        // Display elements
        while(it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();
            if((double)me.getKey()<time)
                continue;
            else if((double)me.getKey()<time){
                reduceCapacity((Skill)me.getValue());
                currentTaskSchedule.remove(me.getKey());
            }
            System.out.print("Key is: "+me.getKey() + " & ");
            System.out.println("Value is: "+me.getValue());
        }
    }



 public String toString(){
        return "\nMedical unit: "+this.id+" type : "+this.agentType+" "+this.skills;

 }






}
//     private int[] getActivityMatrix(Activity activity,Triage triage) {
//        int index[]=new int[2];
//         //activity triage matrix
//         switch (activity) {
//             case UPLOADING:
//                 index[1]=2;
//             case TREATMENT:
//                 index[1]=1;
//             case TRANSPORT:
//                 index[1]=3;
//             case INFO:
//                 index[1]=0;
//         }
//         switch (triage){
//             case NONURGENT:
//                 index[0]=3;
//             case URGENT:
//                 index[0]=1;
//             case MEDIUM:
//                 index[0]=2;
//         }
//         return index;
//     }
//    private void setActivityByTriage(Activity activity,Triage triage){
//
//        int[] index = this.getActivityMatrix(activity,triage);
//        //TODO change time according to problem demands
//
//        switch (activity) {
//            case UPLOADING:
//                switch (triage){
//                    case NONURGENT:
//                        timeActivityTriage[index[0]][index[1]]=3;
//                    case URGENT:
//                        timeActivityTriage[index[0]][index[1]]=5;
//                    case MEDIUM:
//                        timeActivityTriage[index[0]][index[1]]=4;
//                }
//            case TREATMENT:
//                switch (triage){
//                    case NONURGENT:
//                        timeActivityTriage[index[0]][index[1]]=2;
//                    case URGENT:
//                        timeActivityTriage[index[0]][index[1]]=6;
//                    case MEDIUM:
//                        timeActivityTriage[index[0]][index[1]]=4;
//                }
//            case TRANSPORT:
//                //TODO add time travel for transfer...
//                timeActivityTriage[index[0]][index[1]]=10;
//            case INFO:
//                timeActivityTriage[index[0]][index[1]]=5;
//        }
//
//    }
//Agent time for each activity depending on agent type
//    private void setActivitiesTime(){
//        for (Activity act: activeAbilities){
//            for(Triage trg: triageAbilities) {
//                this.setActivityByTriage(act,trg);
//
//            }
//        }
//    }
//    private void setTriageActivity(int capacity,Triage trg,Activity act){
//        skills=new HashMap<TriageActivity,Integer>();
//        TriageActivity temp=new TriageActivity(trg,act);
//        skills.put(temp,capacity);
//    }
