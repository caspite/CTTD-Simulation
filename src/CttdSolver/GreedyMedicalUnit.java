package CttdSolver;

import CTTD.DisasterSite;
import CTTD.Execution;
import CTTD.MedicalUnit;
import CTTD.Skill;
import PoliceTaskAllocation.AgentType;
import TaskAllocation.Location;
import TaskAllocation.Task;

import java.util.Vector;

public class GreedyMedicalUnit extends MedicalUnit {


    //*** constructor ***//
    public GreedyMedicalUnit(int ID, AgentType agt, Location loc){
        super(ID,agt,loc);
    }



    public void addAllocation(Task task, Vector<Skill> execution,double startTime){
        int ranking=nextTaskRanking();
        if(ranking!=-1) {


            double utility = calcRatio(execution);
            updateExecutionPenalty(execution, 1);
            allocateTask(ranking, task, startTime, execution, utility);
            updateAvailableSkillsForAllocation(execution);
            updateNextTimeToAllocation(startTime, execution);
            ((DisasterSite) task).updateRemainCoverByCurrentAllocation(utility);
        }
    }

    private double calcRatio(Vector<Skill> executions){
            double utility=0;
            if(executions==null){
                return -1;
            }
            else{
                for(Skill ex:executions){
                    utility+= ((Execution)ex).getUtility();
                }
            }
            return utility;
    }

    private int nextTaskRanking(){
        for(int i=0;i<getCurrentAssignment().length;i++){
            if(getCurrentAssignment()[i]==null ){
                return i;
            }
        }
        return -1;
    }
}


