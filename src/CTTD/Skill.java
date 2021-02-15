package CTTD;

import PoliceTaskAllocation.AgentType;
import TaskAllocation.Agent;

import java.util.Objects;
import java.util.Vector;

public class Skill {
    private Activity activity;
    private double duration;
    private Triage triage;
    private double score;
//----------------------------constructors------------------------------------------------------------//
    public Skill(){}

    public Skill(Triage triage, Activity activity,double  duration,double score){
        this.activity=activity;
        this.triage=triage;
        this.duration=duration;
        this.score=score;
    }
    public Skill(Triage triage, Activity activity){
        this.activity=activity;
        this.triage=triage;
        setScore(triage);
        //TODO set duration by triage and activities
        duration=10;
    }

//--------------------------getters and setters------------------------------------------------//


    public Activity getActivity() {
        return activity;
    }

    public Triage getTriage() {
        return triage;
    }

    public double getDuration() {
        return duration;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public double getScore() {
        return score;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public void setTriage(Triage triage) {
        this.triage = triage;
    }

    public void setScore(Triage triage){
        switch (triage){
            case NONURGENT:
                this.score=10;
                break;
            case MEDIUM:
                this.score=15;
                break;

            case URGENT:
                this.score=30;
                break;

        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Skill skill = (Skill) o;
        return activity == skill.activity &&
                triage == skill.triage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(activity, triage);
    }


}
