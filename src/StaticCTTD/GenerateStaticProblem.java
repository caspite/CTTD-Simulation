package StaticCTTD;

import CTTD.*;
import CttdSolver.SpcnDisasterSite;
import CttdSolver.SpncMedicalUnit;
import DCOP.Output;
import Helpers.WriteToFile;
import PoliceTaskAllocation.AgentType;
import PoliceTaskAllocation.DiaryEvent;
import TaskAllocation.Agent;
import TaskAllocation.Location;
import TaskAllocation.Task;

import java.util.*;

public class GenerateStaticProblem {

    //*** parameters ***//
    int disasterSiteAmount;
    static int DS_ID;
    static int CAS_ID;
    int medicalUnitAmount;
    double newCas; //prop for new cas
    TreeMap<Double, Object> disasterSitePriority = new TreeMap<>();//the disaster site Priority 1- hight 3-low
    TreeMap<Double, Object> triageProbability1 = new TreeMap<>();
    TreeMap<Double, Object> triageProbability2 = new TreeMap<>();
    TreeMap<Double, Object> triageProbability3 = new TreeMap<>();
    TreeMap<Double, Object> agentTypeProbability = new TreeMap<>();
    TreeMap<Integer, Integer> amountCasualtiesBySitePriority = new TreeMap<>();//the disaster site casualties amount by disaster site priority

    //*** Hospitals params ****//
    private int amountHospitals; // the MU amount
    private int capacity=20000;//Hospital capacity

    //**** general params ****//
    protected double Tnow= 0.0;// current time of the simulation
    public static final double Tmax = 1000;
    private Random rNewDS;
    private Random rNewCas;
    private Random rTriage;
    private Random rAgentType;
    private Random rPriority;
    private TreeSet<DiaryEvent> diary;
    int tempID=0;

    //*** final returns ***//
    Vector<Casualty> Casualties=new Vector<Casualty>();
    Vector<Casualty> tempCasualties=new Vector<Casualty>();
    Vector<Hospital> Hospitals =new Vector<Hospital>();
    Vector<Agent> MedicalUnits=new Vector<Agent>();
    Vector<Agent> agents=new Vector<Agent>();
    Vector<Task> DisasterSites=new Vector<Task>();

    //*** algorithm variable***//
    int algorithmType=0;//1-spcn 2-greedy 0-dbug




    //*** constructor ***//

    public GenerateStaticProblem(int disasterSiteAmount, Map<Double, Integer> priorities, Map<Integer, Integer> casualtiesAmount,
                                 double prop, Map<Double, Triage> prop1, Map<Double, Triage> prop2, Map<Double, Triage> prop3,
                                 int medicalUnitAmount, Map<Double,AgentType> typeProp,int algorithmType){
        calcDisasterSiteParams(disasterSiteAmount,priorities,casualtiesAmount);
        calcCasualtiesParams(prop,prop1,prop2,prop3);
        calcMedicalUnitParams(medicalUnitAmount,typeProp);
        this.algorithmType=algorithmType;
    }


   //*** calc params methods ***//
    public void calcDisasterSiteParams(int disasterSiteAmount, Map<Double, Integer> priorities, Map<Integer, Integer> casualtiesAmount) {
        this.disasterSiteAmount = disasterSiteAmount;
        disasterSitePriority.putAll(priorities);
        amountCasualtiesBySitePriority.putAll(casualtiesAmount);
    }

    public void calcCasualtiesParams(double prop, Map<Double, Triage> prop1, Map<Double, Triage> prop2, Map<Double, Triage> prop3) {
        this.newCas = prop;
        triageProbability1.putAll(prop1);
        triageProbability2.putAll(prop2);
        triageProbability3.putAll(prop3);
    }

    public void calcMedicalUnitParams(int medicalUnitAmount, Map<Double,AgentType> typeProp){
        this.medicalUnitAmount=medicalUnitAmount;
        agentTypeProbability.putAll(typeProp);
    }

    //*** create Methods ***//


    private void CreateDS(int ID,int priority){
        //DS location
        Random rnd=new Random();
        Location loc= Distance.randomLocation(rnd);
        if(algorithmType==0){
            DisasterSites.add(new DisasterSite(loc,ID,Tnow,priority));
        }
        else if(algorithmType==1){
            DisasterSites.add(new SpcnDisasterSite(loc,ID,Tnow,priority));
        }

        tempCasualties.clear();
    }


    private void createCas(Triage trg,int id,int dsId){
        double survival = 1;
        DisasterSite ds=(DisasterSite) getDisasterSiteById(dsId);

        Casualties.add(new Casualty(trg, Casualty.Status.WATING,survival, id,dsId,Tnow,ds));
        tempCasualties.add(new Casualty(trg, Casualty.Status.WATING,survival, id,dsId,Tnow,ds));
    }
    private Task getDisasterSiteById(int id){

        for(Task ds:DisasterSites){
            if (ds.getId()==id){
                return ds;
            }

        }
        return null;
    }

    private void createMU(AgentType agt,int ID){
        //agent location
        Random rnd=new Random();
        Location loc=Distance.randomLocation(rnd);
        if(algorithmType==0){
            Agent mu =new MedicalUnit(ID,agt,loc);
            MedicalUnits.add((MedicalUnit) mu);
            agents.add(mu);
        }
        else if(algorithmType==1){
            Agent mu =new SpncMedicalUnit(ID,agt,loc);
            MedicalUnits.add((SpncMedicalUnit) mu);
            agents.add(mu);
        }

    }

    private void createHospital(int id){

        Random rnd=new Random();
        Location loc=Distance.randomLocation(rnd);

        Hospitals.add(new Hospital(loc,id,capacity));
    }

    private void resetDsaSeed(int meanRun) {

        this.rNewDS = new Random();
        this.rNewCas = new Random(meanRun * 342);
        this.rTriage = new Random(meanRun *870);
        this.rAgentType = new Random(meanRun *86);
        this.rPriority = new Random(meanRun *100);

    }


    //*** main create problem method ***//

    public void generateStaticProblem(double time) {
        double rnd;
        int j = 0;
        int seed = (int) (time + 1) * j;
        for(int d=0;d<disasterSiteAmount;d++){
            resetDsaSeed(seed);
            rnd = rNewDS.nextDouble();
            int priority = setPriority(rnd);
            DS_ID += 1;
            int disasterSiteId = DS_ID;
            CreateDS(disasterSiteId, priority);

            rnd = rNewCas.nextDouble();
            boolean empty = true;
            for (int i = 0; i < calcCasualtiesAmount(priority); i++) {
                rnd = rNewCas.nextDouble();
                if (this.newCas > rnd) {
                    empty = false;
                    CAS_ID = calcCasId(tempID += 1, disasterSiteId);
                    rnd = rTriage.nextDouble();
                    Triage trg = calcTriage(rnd, priority);
                    createCas(trg, CAS_ID, disasterSiteId);
                }
            }
            if (empty == true) {
                empty = false;
                CAS_ID = calcCasId(tempID += 1, disasterSiteId);
                rnd = rTriage.nextDouble();
                Triage trg = calcTriage(rnd, priority);
                createCas(trg, CAS_ID, disasterSiteId);
            }
            //add casualties to disaster site
            ((DisasterSite) DisasterSites.elementAt(DisasterSites.size() - 1)).setInitCasualties(tempCasualties);

            j++;
        }



        createMedicalUnits();


        createHospital(calcId(3, 1));
        updateDisasterSiteEvacuationTime(getHospitals(), getDisasterSites());

        System.out.println("finished generate new problem");
    }

    private void updateDisasterSiteEvacuationTime(Vector<Hospital> hospitals,Vector<Task> tasks){
        for(Task task:tasks){
            Hospital hospital = getNearestHospital(task,hospitals);
            double evacuationTime=task.getDistance(hospital)/60;
            ((DisasterSite)task).setEvacuationTime(evacuationTime);
        }
    }

    private Hospital getNearestHospital(Task task, Vector<Hospital>hospitals) {
        double dis = Double.POSITIVE_INFINITY;
        Hospital hospital = hospitals.get(0);
        for (Hospital h : hospitals) {
            double currentDis = task.getDistance(h);
            if (currentDis < dis) {
                dis = currentDis;
                hospital = h;
            }
        }
        return hospital;
    }

    private int setPriority(double rnd){
     int p=0;
     p=(int)getObjByProp(rnd,disasterSitePriority);
    return p;

    }

    private int calcCasualtiesAmount(int priority){
        int amount=0;
        amount=amountCasualtiesBySitePriority.get(priority);
        return amount;
    }

    private Triage calcTriage(double rnd,int priority){
        Triage triage=Triage.NONURGENT;
         switch(priority){
             case 1:
               triage= (Triage) getObjByProp(rnd,triageProbability1);
                 break;
             case 2:
                 triage= (Triage) getObjByProp(rnd,triageProbability2);
                 break;
             case 3:
                 triage= (Triage) getObjByProp(rnd,triageProbability3);
                 break;
         }
        return triage;
    }

    private int calcId(int idType,int numOfId){

        String idToReturn=""+idType+numOfId+"";
        int id=Integer.parseInt(idToReturn);

        return id;
    }

    private int calcCasId(int tempID, int DS_ID){
        String str = "" + DS_ID + tempID;
        CAS_ID = Integer.valueOf(str);
        return CAS_ID;
    }

    private void createMedicalUnits(){
        double rnd;
        //create new MU
        for (int i = 0; i < medicalUnitAmount; i++) {
            rnd = rAgentType.nextDouble();

            //set agent type
            Object[] prop = agentTypeProbability.keySet().toArray();

            AgentType agt = AgentType.TYPE1;
            if (rnd <= (double) prop[0]) {
                agt =(AgentType) agentTypeProbability.get((double) prop[0]);
            } else if ((double) prop[0] < rnd & rnd <=(double)prop[1]) {
                agt = (AgentType)agentTypeProbability.get((double) prop[1]);
            } else if ((double)prop[1] < rnd & rnd <= (double)prop[2]) {
                agt = (AgentType)agentTypeProbability.get((double) prop[2]);
            }
            //calc the Id
            int id = i;
            createMU(agt, id);
        }
    }

    private Object getObjByProp(double rnd,Map<Double,Object> map){
        //set agent type
        Object[] prop = map.keySet().toArray();

        Object  theReturn=null;
        if (rnd <= (double) prop[0]) {
            theReturn = map.get((double) prop[0]);
        } else if ((double) prop[0] < rnd & rnd <=(double)prop[1]) {
            theReturn = map.get((double) prop[1]);
        } else if ((double)prop[1] < rnd & rnd <= (double)prop[2]) {
            theReturn = map.get((double) prop[2]);
        }
        return theReturn;
    }



    //*** getters && setters ***//

    public Vector<Casualty> getCasualties(){
        return Casualties;
    }

    public Vector<Agent> getMedicalUnits(){
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


    public void printProp(){
        System.out.println("finished generate new problem");
        System.out.println(DisasterSites);
        System.out.println(MedicalUnits);
        System.out.println(Casualties);
    }



    public void writeToFile(){
        WriteToFile.writeProblemLocations("problem mapping "+medicalUnitAmount+" agents "+disasterSiteAmount +" tasks"+".csv",this.getDisasterSites(),this.getMedicalUnits());
    }



}