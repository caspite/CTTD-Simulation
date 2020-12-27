package PoliceTaskAllocation;

import TaskAllocation.Assignment;
import TaskAllocation.Task;



public abstract class DiaryEvent implements Comparable<DiaryEvent>{
	//diary time, time when event appears or ends
	protected double time;//diary time
	protected Assignment assignment;// relevant assignment 
	protected Task event;
	
	protected DiaryEvent () {
		assignment=null;
	}
	public DiaryEvent(Assignment assignment) {
		super();
		this.assignment = assignment;
	}
	public DiaryEvent(Task ms) {
		event=ms;
	}
	public double getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public Task getEvent() {
		return event;
	}
	@Override
	public int compareTo(DiaryEvent o) {
		// TODO Auto-generated method stub
		if(this.getTime() > o.getTime())return 1;
		if(this.getTime() < o.getTime())return -1;
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		long temp;
		temp = Double.doubleToLongBits(time);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DiaryEvent))
			return false;
		DiaryEvent other = (DiaryEvent) obj;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		if (Double.doubleToLongBits(time) != Double
				.doubleToLongBits(other.time))
			return false;
		return true;
	}
	public String toString() {
		 return ("Diary event Time: "+time);
		//return ("Mission Code: "+event.getMissionCode()+", Priority: "+event.getPriority()+", Time: "+event.getArrivalTime()+", Duration: "+event.getDuration()+", Units Required: "+event.getUnitsRequired()+", Location: "+event.getLocation().toString()+"\n");
	}	
	public Assignment getAssignment() {
		return assignment;
	}

	private void setTime(double time2) {
		time=time2;
		
	}
	
	
	
}
