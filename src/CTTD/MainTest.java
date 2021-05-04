package CTTD;

import DCOP.Mailer;
import PoliceTaskAllocation.AgentType;
import TaskAllocation.Agent;
import TaskAllocation.Task;
import com.sun.source.tree.CaseTree;

import java.util.*;

public class MainTest {
    public static void main(String[] args) throws Exception {

        //generate a new problem params
        double newDS=0.09;
        double newCas=0.8;
        int MaxCasForSite=10;
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
        Mailer mailer = new Mailer(newProblem.getAgents(),newProblem.getDisasterSites());

        agentAndMailerMeet(newProblem.getAgents(),newProblem.getDisasterSites(),mailer);
        mainSimulation greedy =new mainSimulation(newProblem.getDiary(),newProblem.getDisasterSites(),newProblem.getMedicalUnits(),
                newProblem.getHospitals(),1,mailer);
        System.out.println("Score: "+ greedy.getScore());
            greedy.runSimulation();
//        PrintRemainCover(newProblem.getDisasterSites());
//        System.out.println("Score: "+ greedy.getScore());
//        printCasualtiesSurvival(newProblem.getCasualties());//

        //mailer meet agents - task & medical units


    }

    private static void PrintRemainCover( Vector<Task> disasterSite){
        double count=0;
        for (Task t: disasterSite){
            System.out.println("Task: "+t.getId()+" current score: "+((DisasterSite)t).getRemainCover());
        }
    }

    private static void printCasualtiesSurvival( Vector<Casualty> casualties){
        double count=0;
        for (Casualty c: casualties){
            System.out.println("Casualty: "+c.id+" current survival: "+c.getSurvival());
        }
    }

    static public void agentAndMailerMeet(Vector<Agent> agents,Vector<Task> tasks, Mailer mailer) {

        for (Task t:tasks) {
            t.setMailer(mailer);

        }

        for(Agent a:agents){
            a.setMailer(mailer);
        }

    }


}
