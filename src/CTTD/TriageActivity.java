package CTTD;

public class TriageActivity {
    private Triage triage;
    private Activity activity;

    public TriageActivity(Triage trg,Activity act){
        this.activity=act;
        this.triage=trg;
    }

    //----------------getters and setters-----------------------------//
    public Triage getTriage(){return this.triage;}
    public Activity getActivity(){return this.activity;}
    public void setTriage(Triage trg){this.triage=trg;}
    public void setActivity(Activity act){this.activity=act;}
}
