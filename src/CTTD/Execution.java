package CTTD;

public class Execution extends Skill {
    Casualty cas;
    boolean finished;//true= finished this execution
    double utility;//the estimate utility for full execution
    double penalty;//Price from abandonment
    double lastUpdate;//the last time the execution updates
    double startTime;//the execution start time


    public Execution(Triage triage, Activity activity,Casualty cas){
        super(triage,activity);
        this.cas=cas;
        this.lastUpdate=0;
        calcEstimateUtility();
    }

    public Execution(Triage triage, Activity activity,Casualty cas,double startTime){
        super(triage,activity);
        this.cas=cas;
        this.lastUpdate=0;
        this.startTime=startTime;
        calcEstimateUtility();
    }

//*** methods ***//
    private void calcEstimateUtility(){
    //take the current survival
        double currentSurvival= cas.getSurvival();
    //take the estimate survival at the end time of the execution
        double endSurvival =cas.getSurvivalByTime(this.startTime+this.getDuration());
    this.utility=currentSurvival-endSurvival;
    }

    //***getters & setters***//
    public Casualty getCas(){return  cas;}

    public void setCas(Casualty cas) {
        this.cas = cas;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public double getPenalty() {
        return penalty;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }

    public void setUtility(double utility) {
        this.utility = utility;
    }

    public double getUtility() {
        return utility;
    }
    //-------------------------------------------------------------------------------------//
}
