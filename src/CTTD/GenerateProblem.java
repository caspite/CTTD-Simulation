package CTTD;

import Helpers.WriteToFile;
import PoliceTaskAllocation.AgentType;
import PoliceTaskAllocation.DiaryEvent;
import PoliceTaskAllocation.MissionEvent;
import PoliceTaskAllocation.NewDiaryEvent;
import TaskAllocation.Agent;
import TaskAllocation.Location;
import TaskAllocation.Task;

import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class GenerateProblem {
    //-------------------------------PARAMS------------------------------//
    //disasters site params
    private double newDS; //the probability that new site appear
    private TreeMap<Double,Integer> priorities;//the priority probabilities
    private double coordDS[][];//the coord options
    private static int DS_ID=0;
    Vector<Task> DisasterSites=new Vector<Task>();

    //casualties params
    private double newCas; //the probability that new casualty appear
    private TreeMap<Double,Triage> triageProbability;//the probabilities for each triage-upto1
    private static int Cas_ID=0;
    private int MaxCasForSite=0;

    Vector<Casualty> Casualties=new Vector<Casualty>();
    Vector<Casualty> tempCasualties=new Vector<Casualty>();


    //medicalUnits params
    private TreeMap<Double,AgentType> agentTypeProbability;; // the probability for each MU type - upto1
    private int amountMU; // the MU amount
    Vector<MedicalUnit> MedicalUnits=new Vector<MedicalUnit>();
    Vector<Agent> agents=new Vector<Agent>();


    //Hospitals params
    private int amountHospitals; // the MU amount
    private int capacity=20000;//Hospital capacity
    Vector<Hospital> Hospitals =new Vector<Hospital>();

    //general params
    protected double Tnow= 0.0;// current time of the simulation
    public static final double Tmax = 1000;
    private Random rNewDS;
    private Random rNewCas;
    private Random rTriage;
    private Random rAgentType;
    private Random rPriority;
    private TreeSet<DiaryEvent> diary;



    int tempID=0;


    //----------------------------Constructor------------------------------------------//
    public GenerateProblem(double newDS,double newCas,TreeMap<Double,Triage> triageProbability,TreeMap<Double,AgentType> agentTypeProbability,int amountMU,int MaxCasForSite,TreeMap<Double,Integer> priorities){
        super();
        this.amountMU=amountMU;
        this.newCas=newCas;
        this.newDS=newDS;
        this.triageProbability=new TreeMap<>();
        this.triageProbability=triageProbability;
        this.agentTypeProbability=new TreeMap<>();
        this.agentTypeProbability=agentTypeProbability;
        this.MaxCasForSite=MaxCasForSite;
        diary=new TreeSet<DiaryEvent>();
        this.priorities=priorities;
        amountHospitals=3;
    }

    public GenerateProblem(){super();}

    //----------------------------Create Methods------------------------------------------//


    private void CreateDS(int ID,int priority){
        //DS location
        Random rnd=new Random(143*ID);
        Location loc=Distance.randomLocation(rnd);
        DisasterSites.add(new DisasterSite(loc,ID,Tnow,priority));
        tempCasualties.clear();
    }


    private void CreateCas(Triage trg,int ID){
        double survival = Probabilities.getSurvival(trg,Tnow,Tnow);
        DisasterSite ds=(DisasterSite) getDisasterSiteById(ID);

        Casualties.add(new Casualty(trg, Casualty.Status.WATING,survival,ID,DS_ID,Tnow,ds));
        tempCasualties.add(new Casualty(trg, Casualty.Status.WATING,survival,ID,DS_ID,Tnow,ds));
    }
    private Task getDisasterSiteById(int id){
        for(Task ds:DisasterSites){
            if (ds.getId()==id){
                return ds;
            }

        }
        return null;
    }

    private void CreateMU(AgentType agt,int ID){
        //agent location
        Random rnd=new Random(130*ID);
        Location loc=Distance.randomLocation(rnd);
        Agent mu =new MedicalUnit(ID,agt,loc);
        MedicalUnits.add((MedicalUnit) mu);
        agents.add(mu);
        }

    private void CreateHospital(int id){

        Random rnd=new Random(10*id);
        Location loc=Distance.randomLocation(rnd);

        Hospitals.add(new Hospital(loc,id,capacity));
    }

    private void resetDsaSeed(int meanRun) {

        this.rNewDS = new Random(meanRun * 123);
        this.rNewCas = new Random(meanRun * 342);
        this.rTriage = new Random(meanRun *870);
        this.rAgentType = new Random(meanRun *86);
        this.rPriority = new Random(meanRun *100);

    }

    private void createDiaryEvent(Vector<Task> disasterSites){

        for (Task ds: disasterSites){
            diary.add(new NewDiaryEvent((MissionEvent)ds));
        }

    }
    //----------------------Main--------------------------------------//
    public void generateNewProblem() {
        double rnd;

        while (Tnow <= Tmax) {
            int seed = (int) Tnow;
            resetDsaSeed(seed);
            rnd = rNewDS.nextDouble();
            int priority = setPriority();
            if (this.newDS > rnd) {
                DS_ID += 1;
                int disasterSiteId =calcId(1,DS_ID);
                CreateDS(disasterSiteId,priority);

                rnd = rNewCas.nextDouble();
                boolean empty = true;
                for (int i=0; i<MaxCasForSite;i++) {
                    if (this.newCas > rnd){
                        empty=false;
                        Cas_ID = getCasId(tempID += 1,disasterSiteId);
                        rnd = rTriage.nextDouble();
                       Triage trg=getTriage(rnd);
                        CreateCas(trg, Cas_ID);
                    }
                }
                if (empty==true){
                    empty=false;
                    Cas_ID = getCasId(tempID += 1,disasterSiteId);
                    rnd = rTriage.nextDouble();
                    Triage trg=getTriage(rnd);
                    CreateCas(trg, Cas_ID);
                }
            //add casualties to disaster site
                ((DisasterSite)DisasterSites.elementAt(DisasterSites.size()-1)).setInitCasualties(tempCasualties);
            //set DS priority
                ((DisasterSite)DisasterSites.elementAt(DisasterSites.size()-1)).setPriority();

            }


            Tnow += 1;
        }
        //create new MU
        for (int i = 0; i < amountMU; i++) {
            rnd = rAgentType.nextDouble();

            //set agent type
            Object[] prop = triageProbability.keySet().toArray();

            AgentType agt = AgentType.TYPE1;
            if (rnd <= (double) prop[0]) {
                agt = agentTypeProbability.get((double) prop[0]);
            } else if ((double) prop[0] < rnd & rnd <=(double)prop[1]) {
                agt = agentTypeProbability.get((double) prop[1]);
            } else if ((double)prop[1] < rnd & rnd <= (double)prop[2]) {
                agt = agentTypeProbability.get((double) prop[2]);
            }
           //calc the Id
            int id = calcId(2,i);
            CreateMU(agt, id);
        }
        CreateHospital(calcId(3,1));
        createDiaryEvent(DisasterSites);
        WriteToFile.CTTD_MedicalUnits("CTTD_MedicalUnits.csv", this.MedicalUnits);
        WriteToFile.CTTD_DisasterSite("CTTD_DisasterSite.csv",this.DisasterSites);
        WriteToFile.CTTD_Casualties("CTTD_Casualties.csv",this.Casualties);
        System.out.println("finished generate new problem");
        System.out.println(DisasterSites);
        System.out.println(MedicalUnits);
        System.out.println(Casualties);

    }
    private int getCasId(int tempID,int DS_ID){
        String str = "" + DS_ID + tempID;
        Cas_ID = Integer.valueOf(str);
        return Cas_ID;
    }

    private Triage getTriage(double rnd){
        Object[] prop = triageProbability.keySet().toArray();
        Triage trg = Triage.NONURGENT;
        if (rnd <= (double) prop[0]) {
            trg = triageProbability.get((double) prop[0]);
        } else if ((double) prop[0] < rnd & rnd <= (double) prop[1]) {
            trg = triageProbability.get((double) prop[1]);
        } else if ((double) prop[1] < rnd & rnd <= (double) prop[2]) {
            trg = triageProbability.get((double) prop[2]);
        }
        return trg;
    }

    private int calcId(int idType,int numOfId){

        String idToReturn=""+idType+numOfId+"";
        int id=Integer.parseInt(idToReturn);

        return id;
    }




    //----------------getters-----------------------------------------------//
    public Vector<Casualty> getCasualties(){
        return Casualties;
    }

    public Vector<MedicalUnit> getMedicalUnits(){
        return MedicalUnits;
    }

    public Vector<Hospital> getHospitals(){
        return Hospitals;
    }

    public Vector<Task> getDisasterSites(){
        return DisasterSites;
    }
    public Vector<Agent> getAgents(){
        return agents;
    }


    public TreeSet<DiaryEvent> getDiary(){return diary;}

    private int setPriority(){
        double rnd;
        int p=1;

        rnd = rPriority.nextInt();
        Object[] prop = priorities.keySet().toArray();
        if (rnd <= (double) prop[0]) {
            p = priorities.get((double) prop[0]);
        } else if ((double) prop[0] < rnd & rnd <= (double) prop[1]) {
            p = priorities.get((double) prop[1]);
        } else if ((double) prop[1] < rnd & rnd <= (double) prop[2]) {
            p = priorities.get((double) prop[2]);

        }
        MaxCasForSite=p*2;
        return p;

    }









}



