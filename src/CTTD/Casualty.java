package CTTD;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.HashSet;

public class Casualty {
private Triage triage;
public double survival;
public Status status;
private Activity activity[];
public int priority;
private double StandbyTime;
    public enum Status{
        WATING,RECEIVEDTREATMENT,LOADED,FINISHED
    }
    //constructors---------------------------------------------//
    public Casualty(){}
    public Casualty(Triage triage,Status status,Activity activity,double survival)
    {
        this.triage=triage;
        this.status=status;
        this.activity=Activity.values();
        this.survival=survival;
    }

    //calculate survival
    public void setSurvival(double Tnow,Triage triage) {
        this.survival = 0 ;
    }

    //set activities time
    public void setActivityDuration(Activity activity,Triage triage) {
        this.activity = Activity.values();
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

    public Triage getTriage() {return triage;}

    public Status getStatus() {return status;}

    public Activity[] getActivity() {return activity;}

    /***
     *
     * @param status the current status of the casualty
     * @param survival the  survival according to the current time of the simulation
     * @return HashMap this the remained activity for the casualty
     * @UPDATE the calculation of the activities time
     */
    //TODO update the functions

    public HashMap<Activity,Double> getActivityDuration( Status status,double survival){
        HashMap<Activity,Double> ActDuration=new HashMap<Activity,Double>();
        ActDuration.put(Activity.TRANSPORT,0.0);// Arbitrary duration function

        switch (status){
            case WATING:
                ActDuration.put(Activity.TREATMENT,survival*5);// Arbitrary duration function
                ActDuration.put(Activity.UPLOADING,survival*3);// Arbitrary duration function
            case RECEIVEDTREATMENT:
                ActDuration.put(Activity.UPLOADING,survival*3);// Arbitrary duration function
            case LOADED:
            case FINISHED:
                return null;
        }
        return ActDuration;

    }










}
