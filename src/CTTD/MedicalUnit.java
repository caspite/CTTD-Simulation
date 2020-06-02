package CTTD;

import PoliceTaskAllocation.AgentType;
import TaskAllocation.Agent;
import TaskAllocation.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MedicalUnit extends Agent  {

    List<Activity> Abilities;
    AgentType agentType;
    public MedicalUnit(Location location, int id, HashSet<AgentType> agentType) {
        super(location, id, agentType);
    }

    //Agent abilities depending on agent type
    public void SetAbilities(AgentType agentType){
        switch(agentType) {
            case TYPE1:
                Abilities = Arrays.asList(Activity.values());
            case TYPE2:
                Abilities = Arrays.asList(Activity.INFO, Activity.TREATMENT, Activity.UPLOADING);
            case TYPE3:
                Abilities = Arrays.asList(Activity.INFO, Activity.TREATMENT);
            case TYPE4:
                Abilities = Arrays.asList(Activity.TRANSPORT);
        }

    }
    //Travel duration depends on the type of agent
    @Override
    //TODO change the moving time according to the real ratio
    public void setMovingTime(double dis) {
        switch (agentType){
            case TYPE1:
                this.movingTime = dis*0.9;
            case TYPE2:
                this.movingTime = dis*1.1;
            case TYPE3:
                this.movingTime = dis*0.8;
            case TYPE4:
                this.movingTime = dis*0.97;


        }

    }
}
