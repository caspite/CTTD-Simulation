package PoliceTaskAllocation;

import CTTD.Distance;
import TaskAllocation.Assignment;

import static PoliceTaskAllocation.Status.MOVING;

public class HospitalEvent extends DiaryEvent{
    public HospitalEvent(Assignment as, double tnow){

        super(as);
        double dis = Distance.travelTime(as.getAgent(),as.getTask());
        time=dis+tnow;
        as.setArrivalTime(time);
//        as.getAgent().setLocation(as.getTask().getLocation());
//        as.getAgent().setMovingTime(dis);
//        as.getAgent().setStartMovingTime(tnow);
//        as.getAgent().setOnTheWay(true);
//        as.getAgent().setStatus(MOVING);
    }


    public String toString() {
        return (" agent: "+assignment.getAgent().getId()+ "arrive to hospital: " + assignment.getTask().getId()+" time: "+time);
    }

}
