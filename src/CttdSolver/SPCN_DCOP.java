package CttdSolver;

import CTTD.DisasterSite;
import CTTD.MedicalUnit;
import TaskAllocation.Agent;
import TaskAllocation.Assignment;
import DCOP.*;
import TaskAllocation.Task;

import java.util.Vector;

public class SpcnDcop extends Solver{

    Vector<Agent> medicalUnits;
    Vector<DisasterSite> disasterSites;
    int iteration;//num of max iteration
    Mailer mailer;

    Output output;

    //*** constructor ***//
    public void SpscnDcop (Vector<Agent> medicalUnits,Vector<DisasterSite> disasterSites,Mailer mailer){
        this.disasterSites=disasterSites;
        this.medicalUnits=medicalUnits;
        this.mailer=mailer;
        output=new Output();
    }
    //*** Create Constrain Graph Methods ***//

    @Override
    protected void createConstraintGraph() {
        //for each agent check if each task is relevant
        for(Agent medicalUnit:medicalUnits){
            for( DisasterSite disasterSite: disasterSites){
                isAgentRelevant(medicalUnit,disasterSite);//check if the agent relevant to task and update available task for each agent and each task

            }
        }
    }

    protected void runSyncSPCN(){
        for(int currentIteration=0;currentIteration<=iteration;currentIteration++){
            agentsSentMessages();
            taskSentMasseges();
            mailerPutMessagesInMailBox();
            updateOutput(currentIteration);
        }
    }
    protected void agentsSentMessages(){
        for(Agent agent:agents){
            agent.newMessageSPCN();
        }
    }

    protected void taskSentMasseges(){
        for(DisasterSite disasterSite:disasterSites){
            disasterSite.CreateNewMessageSPCN();
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
            globalCost+=((DisasterSite)task).getRemainCover();
        }
        return globalCost;
    }







    @Override
    protected Vector<Assignment> solve() {
        return null;
    }
}
