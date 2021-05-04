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
        updateDuration();
        calcEstimateUtility();
    }

    public Execution(Triage triage, Activity activity,Casualty cas,double startTime){
        super(triage,activity);
        this.cas=cas;
        this.lastUpdate=0;
        this.startTime=startTime;
        updateDuration();
        calcEstimateUtility();

    }
    public Execution(Triage triage, Activity activity,Casualty cas,double startTime,double utility){
        super(triage,activity);
        this.cas=cas;
        this.lastUpdate=0;
        this.startTime=startTime;
        this.utility=utility;
        updateDuration();

    }

//*** methods ***//
    private void calcEstimateUtility(){
    //take the current survival
    //take the estimate survival at the end time of the execution
        double startActivitySurvival =cas.calcSurvivalByTime(this.startTime);
    this.utility= startActivitySurvival;
    }

    private void updateDuration(){
        RPM rmp=cas.getCurrentRPM();
        double casualtyTBorn=cas.getTBorn();
        RPM rpmToDuration = rmp.returnRpmByTime(this.startTime-casualtyTBorn);
        if (this.getActivity() == Activity.UPLOADING){
            this.duration=rpmToDuration.getUploadingTime();
        }
        else if (this.getActivity()==Activity.TREATMENT){
            duration= rpmToDuration.getCareTime();
        }
        else if (this.getActivity() == Activity.TRANSPORT){
           duration= calcTransferTime();
        }
    }

    private double calcTransferTime(){
       return getCas().getDisasterSite().getEvacuationTime();
    }


    public void updatePenalty(double factor){
        this.penalty=this.utility*factor;
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

    public double getStartTime() {
        return startTime;
    }



    //-------------------------------------------------------------------------------------//
}
