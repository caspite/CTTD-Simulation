package CTTD;

import PoliceTaskAllocation.AgentType;

import java.util.*;

public class MainTest {
    public static void main(String[] args) throws Exception {

        //generate a new problem params
        double newDS=0.009;
        double newCas=0.8;
        int MaxCasForSite=100;
        TreeMap<Double,Triage> triageProbability=new TreeMap<>();
        TreeMap<Double,AgentType> agentTypeProbability=new TreeMap<>();
        TreeMap<Double,Integer> priority=new TreeMap<>();

        int amountMU=3;
        triageProbability.put(0.2,Triage.URGENT);
        triageProbability.put(0.7,Triage.MEDIUM);
        triageProbability.put(1.0,Triage.NONURGENT);

        agentTypeProbability.put(0.2,AgentType.TYPE1);
        agentTypeProbability.put(0.4,AgentType.TYPE2);
        agentTypeProbability.put(0.7,AgentType.TYPE3);
        agentTypeProbability.put(1.0,AgentType.TYPE4);

        priority.put(0.5,1);
        priority.put(0.3,2);
        priority.put(0.2,3);


        GenerateProblem newProblem=new GenerateProblem(newDS,newCas,triageProbability,agentTypeProbability,amountMU,MaxCasForSite,priority);
        newProblem.generateNewProblem();
        System.out.println(newProblem.getDisasterSites().size());
        System.out.println(newProblem.getCasualties().size());
        System.out.println(newProblem.getDiary().size());
        newProblem.getDiary();
        newProblem.getDisasterSites();
        newProblem.getMedicalUnits();
        newProblem.getHospitals();
        mainSimulation greedy =new mainSimulation(newProblem.getDiary(),newProblem.getDisasterSites(),newProblem.getMedicalUnits(),
                newProblem.getHospitals());
        System.out.println("Score: "+ greedy.getScore());
            greedy.runSimulation();
        System.out.println("Score: "+ greedy.getScore());


    }




}
