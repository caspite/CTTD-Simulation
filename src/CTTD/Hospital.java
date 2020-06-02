package CTTD;

import TaskAllocation.Location;
import TaskAllocation.Task;

public class Hospital extends Task {
    int Capacity;
    int Availability;
    Hospital(Location location, int id,int capacity){
        	super(location,id);
        	this.Capacity=capacity;
    }
    private void setAvailability(int Patients){
        Availability=Availability-Patients;
    }



}
