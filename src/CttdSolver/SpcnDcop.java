package CttdSolver;

import CTTD.DisasterSite;
import CTTD.MedicalUnit;
import Helpers.WriteToFile;
import TaskAllocation.Agent;
import TaskAllocation.Assignment;
import DCOP.*;
import TaskAllocation.Task;

import java.util.Vector;

public class SpcnDcop extends Solver{

    Vector<Agent> medicalUnits;
    Vector<Task> disasterSites;
    int iteration=1000;//num of max iteration
    Mailer mailer;
    Output output;
    double tnow;

    //*** constructor ***//

    public SpcnDcop (Vector<Agent> medicalUnits,Vector<Task> disasterSites,Mailer mailer,double tnow){
        super();
        this.disasterSites=disasterSites;
        this.medicalUnits=medicalUnits;
        this.mailer=mailer;
        output=new Output();
        this.tnow=tnow;
    }
    //*** Create Constrain Graph Methods ***//

    @Override
    public void createConstraintGraph(double tnow) {
        //for each agent check if each task is relevant
        for(Agent medicalUnit:medicalUnits){
            initializeVariables(medicalUnit);
            for( Task disasterSite: disasterSites){
                //check if the agent relevant to task and update available task for each agent and each task

                if(isAgentRelevant(medicalUnit,disasterSite,tnow)){
                    ((MedicalUnit) medicalUnit).addRelevantDisasterSite(disasterSite);
                    ((DisasterSite)disasterSite).addRelevantAgent(medicalUnit);
                }
                //TODO initailze vectors in disaster site and medical units- hashmaps - utility to -1 and arrival time to 0/

            }
        }
    }


    private boolean isAgentRelevant(Agent agent,Task task,double tnow){
        if(task instanceof DisasterSite){
           return  ((DisasterSite)task).isAgentRequired(agent,tnow);
        }
        return false;
    }

    private void initializeVariables(Agent agent){
        ((MedicalUnit)agent).setNextTimeToAllocation(((MedicalUnit)agent).getLastTimeUpdate());
        ((MedicalUnit)agent).setAvailableSkillsForAllocation(((MedicalUnit)agent).getAgentSkills().getCapacity());
        Assignment[] currentAssignment =new Assignment[((MedicalUnit)agent).getNumOfAllocateTask()];
        ((MedicalUnit)agent).setCurrentAssignment(currentAssignment);
    }

    protected void runSyncSPCN(){
        createFirstMessages();
        for(int currentIteration=0;currentIteration<=iteration;currentIteration++){
            agentsSentMessages();
            taskSentMessages();
            mailerPutMessagesInMailBox();
            updateOutput(currentIteration);
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

    }

    private double calcGlobalCost(){
        double globalCost=0;
        for(Task task:disasterSites){
            globalCost+=((DisasterSite)task).getRemainCoverByCurrentAllocation();
        }
        return globalCost;
    }







    @Override
    public Vector<Assignment> solve() {
        Vector <Assignment> finalAssignment=new Vector<Assignment>();
        runSyncSPCN();
        for(Agent agent:medicalUnits){
            for(int i=0;i<((MedicalUnit)agent).getCurrentAssignment().length;i++){
                if(((MedicalUnit)agent).getCurrentAssignment()[i]!=null){
                    finalAssignment.add(((MedicalUnit)agent).getCurrentAssignment()[i]);
                }
            }
        }
        output.writeToFile(tnow);

        return finalAssignment;
    }
}
