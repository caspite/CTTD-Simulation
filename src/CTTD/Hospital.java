package CTTD;

import TaskAllocation.Location;
import TaskAllocation.Task;

import java.util.Vector;

public class Hospital extends Task {
    int Capacity;
    int Availability;
    Vector<Casualty> casualties;
    static int HospitalNum;
    public Hospital(Location location, int id, int capacity){
        	super(location,id);
        	this.Capacity=capacity;
        	casualties=new Vector<>();
    }
    private void setAvailability(int Patients){
        Availability=Availability-Patients;
    }

    //-----------------------------------getters and setters--------------------------------------------------//


    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public int getCapacity() {
        return Capacity;
    }

    public Vector<Casualty> getCasualties() {
        return casualties;
    }

    public void setCasualties(Vector<Casualty> casualties) {
        this.casualties = casualties;
    }

    public void addCasualties(Vector<Casualty> casualties) {
        this.casualties.addAll(casualties);
    }
}


