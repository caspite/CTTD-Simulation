package PoliceTaskAllocation;

public class NewDiaryEvent extends DiaryEvent {

	public NewDiaryEvent(MissionEvent ms) {
		super(ms);
		time=ms.getStartTime();
		// TODO Auto-generated constructor stub
	}

	public String toString() {
		return ("New Task: " + getEvent().getId()+ " time: "+time+"Task hard constraint: "+getEvent().getHardConstraintTime()+"////////////////////////////////////////////////////////");
	}

}
