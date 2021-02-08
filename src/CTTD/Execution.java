package CTTD;

public class Execution extends Skill {
    Casualty cas;
    boolean finished;//true= finished this execution
    double utility;//the estimate utility for full execution
    double penalty;//Price from abandonment


    public Execution(Triage triage, Activity activity,Casualty cas){
        super(triage,activity);
        this.cas=cas;
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
