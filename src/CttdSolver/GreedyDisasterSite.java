package CttdSolver;

import CTTD.*;
import TaskAllocation.Agent;
import TaskAllocation.Location;

import java.util.HashMap;
import java.util.Vector;

public class GreedyDisasterSite extends DisasterSite {



    //*** constructor ***//

    public GreedyDisasterSite(Location loc, int ID, double Tnow,int priority){
        super(loc,ID,Tnow,priority);
        relevantAgentsUtility=new HashMap<>();
        relevantAgentsTimeArrival=new HashMap<>();
    }

    public void allocatedAgent(Agent agent,double  tnow){
        double arrivalTime=((MedicalUnit)agent).ArrivalTimeAccordingToCurrentAllocation(this);
        relevantAgentsTimeArrival.replace(agent,arrivalTime);
        Casualty casualty= getNextCasualty(agent,tnow);
        Capacity availableCapacity =calcAvailableCapacity (agent,((MedicalUnit)agent).getAgentSkills());
        Vector<Skill> allocation = allocateActivitiesToCasualty(casualty,availableCapacity,arrivalTime);
        updateAgent(allocation,agent);
        ((GreedyMedicalUnit)agent).addAllocation(this,allocation,arrivalTime);
    }



}
