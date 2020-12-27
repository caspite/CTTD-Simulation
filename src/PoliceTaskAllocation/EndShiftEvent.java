package PoliceTaskAllocation;

import TaskAllocation.Assignment;

public class EndShiftEvent extends DiaryEvent {

	public EndShiftEvent( double tmax) {
		time=tmax;
				
	}
	public String toString() {
		return ("End Shift Event" + getEvent()+ " time: "+time);
	}


}
