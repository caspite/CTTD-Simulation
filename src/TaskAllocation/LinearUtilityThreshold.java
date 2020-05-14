package TaskAllocation;

import PoliceTaskAllocation.MissionEvent;
import PoliceTaskAllocation.PatrolEvent;

public class LinearUtilityThreshold extends LinearUtility {

	public static final double threshold = 540;

	public LinearUtilityThreshold(Agent agent, Task task, double Tnow) {
		super(agent, task, Tnow);
		// TODO Auto-generated constructor stub
	}

	public LinearUtilityThreshold() {
		super();
	}

	public void calculateParameters(double Tnow) {//
		double distance = task.getDistance(agent);

		if (distance < threshold) {
			super.calculateParameters(Tnow);
		}
		else{
			if(task instanceof PatrolEvent)
				linearUtility=50;
			else
				linearUtility=0;
		}
	}

}
