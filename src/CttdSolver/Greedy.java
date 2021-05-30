package CttdSolver;

import CTTD.DisasterSite;
import CTTD.MedicalUnit;
import DCOP.Mailer;
import DCOP.Output;
import TaskAllocation.Agent;
import TaskAllocation.Assignment;
import TaskAllocation.Task;

import java.util.Vector;

public class Greedy extends Solver{



    Vector<Agent> medicalUnits;
    Vector<Agent> AvailableAgents;
    Vector<Task> disasterSites;
    Output output;
    double tnow;

    //*** constructor ***//

    public Greedy (Vector<Agent> medicalUnits, Vector<Task> disasterSites,double tnow){
        super();
        this.disasterSites=disasterSites;
        this.medicalUnits=medicalUnits;
        this.AvailableAgents=new Vector<Agent>();
        this.AvailableAgents.addAll(medicalUnits);
        output=new Output(2);
        this.tnow=tnow;
    }

    //*** Create Constrain Graph Methods ***//

    @Override
    public void createConstraintGraph(double tnow) {
        for(Agent medicalUnit:medicalUnits){
            initializeVariables(medicalUnit);
            for( Task disasterSite: disasterSites){
                //check if the agent relevant to task and update available task for each agent and each task

                if(isAgentRelevant(medicalUnit,disasterSite,tnow)){
                    ((GreedyMedicalUnit) medicalUnit).addRelevantDisasterSite(disasterSite,tnow);
                    ((GreedyDisasterSite)disasterSite).addRelevantAgent(medicalUnit);
                    ((GreedyMedicalUnit) medicalUnit).setNextTimeToAllocation(tnow);
                    ((GreedyDisasterSite) disasterSite).updateDemands();
                }

            }
        }
        //TODO - add remove not connected agents
        updateOutput(0);
    }

    private boolean isAgentRelevant(Agent agent,Task task,double tnow){
        if(task instanceof DisasterSite){
            return  ((DisasterSite)task).isAgentRequired(agent,tnow);
        }
        return false;
    }

    private void initializeVariables(Agent agent){
        ((GreedyMedicalUnit)agent).setNextTimeToAllocation(((MedicalUnit)agent).getLastTimeUpdate());
        ((GreedyMedicalUnit)agent).setAvailableSkillsForAllocation(((MedicalUnit)agent).getAgentSkills().getSkills());
        Assignment[] currentAssignment =new Assignment[((MedicalUnit)agent).getNumOfAllocateTask()];
        ((GreedyMedicalUnit)agent).setCurrentAssignment(currentAssignment);

    }


    //*** solve methods ***//

    public void runGreedy(){
       {
            // check relevant to task each available agent
            for (Task task : disasterSites) {
                if (AvailableAgents.size() <= 0) {
                    break;
                }
                for (int i = 0; i < AvailableAgents.size(); i++) {
                    Agent agent = AvailableAgents.get(i);
                    if (((DisasterSite)task).isAgentRequired(agent, tnow)) {
                        if(agent.getCurrentTask()==null){
                            allocatedAgentToTask(agent, task);
                            i--;
                            continue;
                        }
                        else if(!agent.getCurrentTask().getTask().equals(task)){
                            allocatedAgentToTask(agent, task);

                        }
                    }
                }
            }
        }
        updateOutput(1);

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

    private void allocatedAgentToTask(Agent agent, Task task) {

        ((GreedyDisasterSite)task).allocatedAgent(agent,tnow);
        removeAgentFromAvailableAgents(agent);


    }

    private void removeAgentFromAvailableAgents(Agent agent){
        this.AvailableAgents.remove(agent);
    }

    public Output getOutput() {
        return output;
    }

    @Override
    public Vector<Assignment> solve() {
        Vector <Assignment> finalAssignment=new Vector<Assignment>();
        runGreedy();
        for(Agent agent:medicalUnits){
            for(int i = 0; i<((MedicalUnit)agent).getCurrentAssignment().length; i++){
                Assignment as=((MedicalUnit)agent).getCurrentAssignment()[i];
                if(as!=null){
                    finalAssignment.add(as);
                    as.calcUtility();
                    as.setPenalty(as.getUtility());
                }
            }
        }
        output.writeToFile(medicalUnits.size(),disasterSites.size(),tnow,-1);

        return finalAssignment;
    }
}

