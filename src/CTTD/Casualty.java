package CTTD;

import TaskAllocation.Task;

import java.lang.reflect.Array;
import java.util.*;

public class Casualty {
public Triage triage;
public double survival;//the current survival
public double timeToSurvive;
public Status status;
private Activity activity[]={Activity.TRANSPORT,Activity.UPLOADING,Activity.TREATMENT};//the list of the casualty's activities to allocate
public int priority;
private double StandbyTime;
private double TBorn;//time that the casualty born
public int id;
public int DS_Id;
private double initSurvival; // the init survival of the cas
private double finiteSurvival;
private DisasterSite disasterSite;


    public enum Status{
        WATING,RECEIVEDTREATMENT,LOADED,FINISHED
    }
    //-------------------------constructors---------------------------------------------//
    public Casualty(){}
    public Casualty(Triage triage,Status status,double survival,int id,int DS_ID,double TBorn,DisasterSite disasterSite)
    {
        this.triage=triage;
        this.status=status;
        this.survival=survival;
        this.id=id;
        this.DS_Id=DS_ID;
        this.TBorn=TBorn;
        this.initSurvival=survival;
        this.disasterSite=disasterSite;
        setTimeToSurvive();
        setPriority();
    }

    //calculate survival-------------------------------------//
    //TODO - different init survival
    public void setSurvival(double Tnow) {
       this.survival=Probabilities.getSurvival(triage,TBorn, Tnow, status);
    }

public void setTimeToSurvive(){

        this.timeToSurvive= Probabilities.getTimeToSurvive(this.triage,this.TBorn);
}

public double getSurvivalByTime(double time){
        double survival=this.survival;
        Probabilities.getSurvival(this.triage,this.TBorn,time);

    return survival;

}

    public void setPriority() {
        switch (triage) {
            case URGENT:
                this.priority = 1;
            case MEDIUM:
                this.priority = 2;
            case NONURGENT:
                this.priority = 3;
        }
    }

    //getters
    public double getSurvival() {return survival;}

    public double getTBorn() {return TBorn;}

    public Triage getTriage() {return triage;}

    public Status getStatus() {return status;}

    public Activity[] getActivity() {return activity;}



    //hashmap with activities duration
    public HashMap<Activity,Double> getActivityDuration(){
        HashMap<Activity,Double> ActDuration=new HashMap<Activity,Double>();
        ActDuration.put(Activity.TRANSPORT,Probabilities.getActivity(triage,survival,Activity.TRANSPORT));//

        switch (status){
            case WATING:
                ActDuration.put(Activity.TREATMENT,Probabilities.getActivity(triage,survival,Activity.TREATMENT));
                ActDuration.put(Activity.UPLOADING,Probabilities.getActivity(triage,survival,Activity.UPLOADING));
            case RECEIVEDTREATMENT:
                ActDuration.put(Activity.UPLOADING,Probabilities.getActivity(triage,survival,Activity.UPLOADING));
            case LOADED:
            case FINISHED:
                return null;
        }
        return ActDuration;
    }
    public boolean isAgentRequired(MedicalUnit medicalUnit,double timeArrival){
        //check if casualty will survive at time arrival
        if(timeArrival>this.timeToSurvive)
            return false;
        else{
            //Compare agent skills and casualty demands
            return compareAgentCasualtyActivities(medicalUnit);
        }

    }
    private boolean compareAgentCasualtyActivities(MedicalUnit mu){
        //check if agent skill fit to casualty
        Vector <Skill> skills =mu.getAgentSkills().getCapacity();
        for(int i=0;i<getActivity().length;i++){
           Skill s=new Skill(triage, (Activity)Array.get(getActivity(),i));
           if(skills.contains(s))
               return true;
        }
        return false;

    }
    public Activity getNextActivity(){
        switch (this.getStatus()){
            case WATING:
                return Activity.TREATMENT;
            case RECEIVEDTREATMENT:
                return Activity.UPLOADING;
            case LOADED:
                return Activity.TRANSPORT;
            case FINISHED:
                return null;

        }
    return null;
    }

    public void setStatus(Status s){
        switch (status){
            case WATING:
                this.status=Status.RECEIVEDTREATMENT;
                break;
            case RECEIVEDTREATMENT:
                this.status=Status.LOADED;
                break;
            case LOADED:
                this.status=Status.FINISHED;
                break;


        }
    }

    //reduce set of needed activities
    public void setActivity(Activity act){
        if(activity.length>0){
            Activity[] temp=new Activity[activity.length-1];
            int j=0;
            for(int i=0;i<activity.length;i++){
                if(!activity[i].equals(act)&&j<temp.length){
                    temp[j]=activity[i];
                    j++;
                }
            }
            activity=temp;
        }

    }

    public void setActivitiesByStatus(){
        Activity temp[];

        switch (status){

            case WATING:
                temp=new Activity[]{Activity.TRANSPORT,Activity.UPLOADING,Activity.TREATMENT};
                break;
            case RECEIVEDTREATMENT:
                temp=new Activity[]{Activity.TRANSPORT,Activity.UPLOADING};
                break;
            case LOADED:
                temp=new Activity[]{Activity.TRANSPORT};
                break;

            default:
                temp=new Activity[]{};

        }
        activity=temp;

    }

    public void setFiniteSurvival(double Tnow){
        this.finiteSurvival=Probabilities.getSurvival(triage,TBorn, Tnow, status);
    }

    public boolean isServiceRequired(Capacity capacity,double timeArrival){
        //check if casualty will survive at time arrival
        if(timeArrival>this.timeToSurvive)
            return false;
        else{
            //Compare agent skills and casualty demands
            return compareServiceCasualtyActivities(capacity);
        }

    }
    private boolean compareServiceCasualtyActivities(Capacity capacity){
        //check if agent skill fit to casualty
        Vector <Skill> skills =capacity.getCapacity();
        for(int i=0;i<getActivity().length;i++){
            Skill s=new Skill(triage, (Activity)Array.get(getActivity(),i));
            if(skills.contains(s))
                return true;
        }
        return false;
    }


    public void updateTaskScore(){
        disasterSite.removeCasualty(this);
    }

//    public HashMap<Casualty, Activity> getActivitiesForAgent(MedicalUnit medicalUnit){
//        HashMap<Casualty, Activity> casualtyActivityHashMap=new HashMap<>();
//        HashMap<TriageActivity,Integer> tempAgentSkills = medicalUnit.getAgentSkills();
//
//        //remove empty capacity
//        for (Map.Entry<TriageActivity,Integer> entry :tempAgentSkills.entrySet()) {
//            if (entry.getValue() == 0) {
//                tempAgentSkills.remove(entry.getKey());
//            }
//        }
//        HashSet agentSkills=new HashSet<TriageActivity>();
//        agentSkills.addAll(tempAgentSkills.keySet());
//        for(Activity act:activity){
//            TriageActivity tempTriage=new TriageActivity(this.triage,act);
//            if (agentSkills.contains(tempTriage)){
//                casualtyActivityHashMap.put(this,act);
//            }
//        }
//        return casualtyActivityHashMap;
//    }

//    public Vector<TriageActivity> getTriageAct (MedicalUnit medicalUnit){
//        Vector<TriageActivity> triageActivity = new Vector<>();
//        for(Activity act:activity){
//            TriageActivity tempTriage=new TriageActivity(this.triage,act);
//            triageActivity.add(tempTriage);
//        }
//        return triageActivity;
//
//    }
    public String toString(){
        return "\nCasualty: "+this.id+" init survival : "+this.initSurvival+" current survival: "+this.survival;
    }










}
