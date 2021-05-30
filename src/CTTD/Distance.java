package CTTD;

import TaskAllocation.Agent;
import TaskAllocation.Distancable;
import TaskAllocation.Location;
import TaskAllocation.Task;

import java.util.Random;

public class Distance {

    /***
     *
     * @param agent
     * @param task
     * @return travel time in hours
     */
    public static double travelTime(Agent agent, Distancable task){
        double timeToTravel=0;
        Location agentLocation = agent.getLocation(); //get the agent location
        Location taskLocation =task.getLocation(); //get the event location
        double distance = distance(agentLocation,taskLocation); //calc the distance between two points
        double agentSpeed = agent.getSpeed();// get the speed
        timeToTravel = (distance / agentSpeed)*60; //calc the travel time

        return timeToTravel;
    }

    /***
     *
     *Calculate the great circle distance between two points
     *on the earth (specified in decimal degrees)
     *
     * @param l1
     * @param l2
     * @return km distance
     */

    private static double distance(Location l1,Location l2)
    {
        double lat1=l1.getLat();
        double lat2=l2.getLat();
        double lon1=l1.getLng();
        double lon2=l2.getLng();

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }

    public static Location randomLocation(Random random) {

        //limits
        double northBound = 31.392910;
        double southBound = 31.169101;
        double eastBound = 34.613368;
        double westBound = 34.926536;

        double lng = southBound + (northBound - southBound) * random.nextDouble();
        double lat = eastBound + (westBound - eastBound) * random.nextDouble();

        Location location=new Location(lat,lng);

        return location;
    }



}
