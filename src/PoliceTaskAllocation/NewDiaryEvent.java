package PoliceTaskAllocation;

public class NewDiaryEvent extends DiaryEvent {

	public NewDiaryEvent(MissionEvent ms) {
		super(ms);
		time=ms.getStartTime();
		// TODO Auto-generated constructor stub
	}

}
