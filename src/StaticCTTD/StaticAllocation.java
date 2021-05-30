package StaticCTTD;

import CTTD.DisasterSite;
import CTTD.GenerateProblem;
import CTTD.MedicalUnit;
import CTTD.Triage;
import CttdSolver.Greedy;
import CttdSolver.SpcnDcop;
import DCOP.Mailer;
import DCOP.Output;
import PoliceTaskAllocation.AgentType;
import TaskAllocation.Agent;
import TaskAllocation.Assignment;
import TaskAllocation.Task;
import CttdSolver.Solver;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class StaticAllocation {
    public static void main(String[] args) {

        //****variable*****//

        Vector<Output> outputs=new Vector<>();

        int numOfRuns=10;
        int disasterSiteAmount=10; //amount of sites
        Map<Double, Integer> priorities=new TreeMap<>();//prop for disasters site priority
        Map<Integer, Integer> casualtiesAmount=new TreeMap<>();//the amount of casualties by disaster site priority
        double newCasProp=1;
        Map<Double, Triage > prop1=new TreeMap<>();
        Map<Double, Triage> prop2=new TreeMap<>();
        Map<Double, Triage> prop3=new TreeMap<>();
        int medicalUnitAmount=20;

        Map<Double, AgentType > typeProp=new TreeMap<>();

        priorities.put(0.2,1);
        priorities.put(0.6,2);
        priorities.put(1.0,3);
        casualtiesAmount.put(1,5);
        casualtiesAmount.put(2,8);
        casualtiesAmount.put(3,10);
        prop1.put(0.1,Triage.URGENT);
        prop1.put(0.7,Triage.MEDIUM);
        prop1.put(1.0,Triage.NONURGENT);
        prop2.put(0.5,Triage.URGENT);
        prop2.put(0.7,Triage.MEDIUM);
        prop2.put(1.0,Triage.NONURGENT);
        prop3.put(0.8,Triage.URGENT);
        prop3.put(0.9,Triage.MEDIUM);
        prop3.put(1.0,Triage.NONURGENT);
        typeProp.put(0.2,AgentType.TYPE1);
        typeProp.put(0.5,AgentType.TYPE2);
        typeProp.put(0.8,AgentType.TYPE3);
        typeProp.put(1.0,AgentType.TYPE4);
        int algorithmType=1;//0-dbug,1-spnc,2-greedy
        int algorithmVersion=3;//for spnc 1- 1-ratio,0-survaival 3-shapely val



//***** main method*****//

        for(int i=0;i<numOfRuns;i++) {

            GenerateStaticProblem newProblem = new GenerateStaticProblem(disasterSiteAmount,
                    priorities, casualtiesAmount, newCasProp, prop1, prop2,
                    prop3, medicalUnitAmount, typeProp,algorithmType);
            newProblem.generateStaticProblem(0.0);
            Mailer mailer = new Mailer(newProblem.getAgents(), newProblem.getDisasterSites());

            agentAndMailerMeet(newProblem.getAgents(), newProblem.getDisasterSites(), mailer);
            newProblem.getDisasterSites();
            newProblem.getMedicalUnits();
            newProblem.printProp();
            newProblem.writeToFile();
            //*** solve ***//
            Solver s;
            if(algorithmType==1){
                s = new SpcnDcop(newProblem.getMedicalUnits(), newProblem.getDisasterSites(), mailer, 0.0,algorithmVersion);
            }
            else if(algorithmType==2){
                s=new Greedy(newProblem.getMedicalUnits(), newProblem.getDisasterSites(),0.0);
            }
            else {
                s = new SpcnDcop(newProblem.getMedicalUnits(), newProblem.getDisasterSites(), mailer, 0.0,algorithmVersion);
            }

            s.createConstraintGraph(0.0);
            Vector<Assignment> newAllocation = s.solve();
            updateOutput(s.getOutput(), outputs);
            printCurrentAllocation(newProblem.getMedicalUnits(), newProblem.getDisasterSites());
            newProblem.DS_ID=0;

        }
        writeToFile(outputs,numOfRuns,medicalUnitAmount,disasterSiteAmount,algorithmType,algorithmVersion);
    }

    static public void printCurrentAllocation(Vector<Agent>medicalUnits,Vector<Task> activeEvents) {
        for (Task task : activeEvents) {
            System.out.println("");
            System.out.println("Task: " + task.getId() + " , " + ((DisasterSite) task).getRemainCoverByCurrentAllocation());

        }
        for (Agent m : medicalUnits) {
            System.out.print("Agent" +  m.getId() + " assignments: ");
            for(int i=0;i<((MedicalUnit)m).getCurrentAssignment().length;i++){
                if(((MedicalUnit)m).getCurrentAssignment()[i] == null){
                    System.out.println("");
                }
                else
                    System.out.println(((MedicalUnit)m).getCurrentAssignment()[i].toString());
            }
        }

        System.out.println("");
    }

    static public void agentAndMailerMeet(Vector<Agent> agents, Vector<Task> tasks, Mailer mailer) {

        for (Task t:tasks) {
            t.setMailer(mailer);

        }

        for(Agent a:agents){
            a.setMailer(mailer);
        }

    }
    static private void updateOutput(Output output,Vector<Output> outputs){
        outputs.add(output);
    }

    static private void writeToFile(Vector<Output> outputs,int numOfRuns,int agentNum,int taskNum,int algorithm,int version){
        Output finalOutput=new Output(algorithm);
        for(int i=0;i<outputs.get(0).getLength();i++){
            double cost=0;
            for(Output output:outputs){
                cost+=output.getGlobalCost().get(i)*(1.0/numOfRuns);
            }
            finalOutput.addGlobalCost(i,cost);
        }
        finalOutput.writeToFile(agentNum,taskNum,0.0,version);
    }



}

