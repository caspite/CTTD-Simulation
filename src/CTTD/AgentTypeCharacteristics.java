package CTTD;

import PoliceTaskAllocation.AgentType;

public class AgentTypeCharacteristics {
    public static double getSpeedDrive(AgentType agentType){
        switch (agentType){
            case TYPE2:
                return 60;
            case TYPE3:
                return 100;
            case TYPE1:
                return 50;
            case TYPE4:
                return 60;
            case TYPE5:
                return 80;
            default:
                return 0;
        }

    }
}
