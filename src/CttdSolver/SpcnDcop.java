package CttdSolver;

import CTTD.DisasterSite;
import CTTD.MedicalUnit;
import StaticCTTD.StaticAllocation;
import TaskAllocation.Agent;
import TaskAllocation.Assignment;
import DCOP.*;
import TaskAllocation.Task;

import java.util.Vector;

public class SpcnDcop extends Solver{

    Vector<Agent> medicalUnits;
    Vector<Task> disasterSites;
    int iteration=50;//num of max iteration
    Mailer mailer;
    Output output;
    double tnow;
    int algorithmVersion;

    //*** constructor ***//

    public SpcnDcop (Vector<Agent> medicalUnits,Vector<Task> disasterSites,Mailer mailer,double tnow,int algorithmVersion){
        super();
        this.disasterSites=disasterSites;
        this.medicalUnits=medicalUnits;
        this.mailer=mailer;
        output=new Output(1);
        this.tnow=tnow;
        this.algorithmVersion=algorithmVersion;
    }
    //*** Create Constrain Graph Methods ***//

    @Override
    public void createConstraintGraph(double tnow) {
        //for each agent check if each task is relevant
        for(Agent medicalUnit:medicalUnits){
            initializeVariables(medicalUnit);
            for( Task disasterSite: disasterSites){
                ((SpcnDisasterSite)disasterSite).setVersion(algorithmVersion);
                //check if the agent relevant to task and update available task for each agent and each task

                if(isAgentRelevant(medicalUnit,disasterSite,tnow)){
                    ((SpncMedicalUnit) medicalUnit).addRelevantDisasterSite(disasterSite,tnow);
                    ((SpcnDisasterSite)disasterSite).addRelevantAgent(medicalUnit);
                    ((SpncMedicalUnit) medicalUnit).setNextTimeToAllocation(tnow);
                }

            }
        }
        //TODO - add remove not connected agents
    }


    private boolean isAgentRelevant(Agent agent,Task task,double tnow){
        if(task instanceof DisasterSite){
           return  ((DisasterSite)task).isAgentRequired(agent,tnow);
        }
        return false;
    }

    private void initializeVariables(Agent agent){
        ((SpncMedicalUnit)agent).setNextTimeToAllocation(((MedicalUnit)agent).getLastTimeUpdate());
        ((SpncMedicalUnit)agent).setAvailableSkillsForAllocation(((MedicalUnit)agent).getAgentSkills().getSkills());
        Assignment[] currentAssignment =new Assignment[((MedicalUnit)agent).getNumOfAllocateTask()];
        ((SpncMedicalUnit)agent).setCurrentAssignment(currentAssignment);
        ((SpncMedicalUnit)agent).setVersion(algorithmVersion);
    }

    protected void runSyncSPCN(){
        createFirstMessages();
        for(int currentIteration=1;currentIteration<=iteration;currentIteration++){
            taskSentMessages();

            mailerPutMessagesInMailBox();
            agentsSentMessages();
            mailerPutMessagesInMailBox();
            updateOutput(currentIteration);
            StaticAllocation.printCurrentAllocation(this.medicalUnits,this.disasterSites);

        }
    }

    private void createFirstMessages(){
        for(Agent agent:medicalUnits){
            ((MedicalUnit)agent).createFirstMessages();
        }
        for(Task task:disasterSites){
            ((DisasterSite)task).createFirstMessages();
        }
        mailerPutMessagesInMailBox();
        updateOutput(0);

    }


    protected void agentsSentMessages(){
        for(Agent agent:medicalUnits){
            ( (MedicalUnit)agent).createNewMessageSPCN();
        }
    }

    protected void taskSentMessages(){
        for(Task disasterSite:disasterSites){
            ((DisasterSite)disasterSite).createNewMessageSPCN();
        }
    }

    protected void mailerPutMessagesInMailBox(){
        mailer.putMessagesInAgentsMailBox();
    }

    protected void updateOutput(int currentIteration){
        double globalCost=calcGlobalCost();
        output.addGlobalCost(currentIteration,globalCost);
//        System.out.println("globalCost "+globalCost+" ,it "+currentIteration );
    }

    private double calcGlobalCost(){
        double globalCost=0;
        for(Task task:disasterSites){
            globalCost+=((DisasterSite)task).getRemainCoverByCurrentAllocation();
        }
        return globalCost;
    }


    //*** getters & setters ***//


    public Output getOutput() {
        return output;
    }

    @Override
    public Vector<Assignment> solve() {
        Vector <Assignment> finalAssignment=new Vector<Assignment>();
        runSyncSPCN();
        for(Agent agent:medicalUnits){
            for(int i=0;i<((MedicalUnit)agent).getCurrentAssignment().length;i++){
                Assignment as=((MedicalUnit)agent).getCurrentAssignment()[i];
                if(as!=null){
                    finalAssignment.add(as);
                    as.calcUtility();
                    as.setPenalty(as.getUtility());
                }
            }
        }
        output.writeToFile(medicalUnits.size(),disasterSites.size(),tnow,algorithmVersion);

        return finalAssignment;
    }
}
