package CTTD;

import PoliceTaskAllocation.AgentType;

public class Probabilities {

    //survival functions

    public static double getSurvival(Triage triage, double Tborn, Double Tnow, Casualty.Status status) {
        double Survival=0;
        return Survival;
    }


    //Activities functions
    public static double getActivity(Triage triage,double survival, Activity Act) {
        double Activity=0;
        return Activity;
    }


    //Agent travel time according to the agent type
    //TODO - don't use this function!
    public static double getTravelTime(double distance, AgentType at) {
        double TravelTime=AgentTypeCharacteristics.getSpeedDrive(at);
        return (distance/TravelTime)*60;
    }
    public static double getTimeToSurvive(Triage triage,double Tborn){

        double B1;
        double B2;
        switch (triage)
        {
            case MEDIUM:
                B1 = 5;
                B2 = 5;
                break;
            case URGENT:
                B1 = 10;
                B2 = 10;
                break;
            case NONURGENT:
                B1 = 1;
                B2 = 1;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + triage);
        }
        double Tnow;
        //Tnow = Math.pow(1,(1/B2))*B1+Tborn;
        Tnow=50+Tborn;

            return Tnow;
    }

    //calculate survival by triage and time
    public static double getSurvival (Triage triage,double Tnow,double Tborn){

        double B0;
        double B1;
        double B2;
        switch (triage)

        {
            case MEDIUM:
                B0 = 0.7;
                B1 = 5;
                B2 = 5;
                break;
            case URGENT:
                B0 = 0.6;
                B1 = 10;
                B2 = 10;
                break;
            case NONURGENT:
                B0 = 0.8;
                B1 = 1;
                B2 = 1;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + triage);
        }
        double survival;
        survival = B0/(Math.pow((Tnow-Tborn)/B1,B2)+1);
        if (survival<0){
            return 0;
        }
        else{
            return survival;
        }
    }
}
