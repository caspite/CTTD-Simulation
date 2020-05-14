package TaskAllocation;

import PoliceTaskAllocation.MissionEvent;
import PoliceTaskAllocation.PatrolEvent;
import SW.SW;

public class DBAUtility extends LinearUtility implements Comparable<DBAUtility> {

	private double DBAUtility;
	private double DBAProbability;

	public DBAUtility(Agent agent, Task task, double Tnow) {
		super(agent, task,Tnow);
	}



	@Override
	public void calculateParameters(double Tnow) {
		double distance = task.getDistance(agent);

		if (!task.equals(agent.getCurrentTask().getTask())
				&& task instanceof MissionEvent
				&& agent.getCurrentTask().getTask() instanceof MissionEvent) {
			DBAUtility = (task.getTotalUtility() - SW
					.calculatePenaltyForAbandonment(Tnow, task))
					/ ((0.05+(distance / timeUnit))/0.05);
		} else {
			DBAUtility = task.getTotalUtility() /((0.05+(distance / timeUnit))/0.05);
		}

		DBAUtility = Math.max(0.0, DBAUtility);
		DBAUtility =  Math.pow(DBAUtility, 2);
	}

	@Override
	public String toString() {
		return "DBAUtility [DBAUtility=" + DBAUtility + ", DBAProbability="
				+ DBAProbability + "]";
	}

	@Override
	public double getUtility(double ratio) {
		// TODO Auto-generated method stub
		return DBAUtility * ratio;
	}

	@Override
	public DBAUtility clone() {
		DBAUtility db = new DBAUtility(agent, task, 0);
		db.DBAUtility = DBAUtility;
		return db;
	}

	public double getDBAProbability() {
		return DBAProbability;
	}

	public void setDBAProbability(double dBAProbability) {
		DBAProbability = dBAProbability;
	}

	@Override
	public int compareTo(DBAUtility arg0) {
		if (DBAProbability > arg0.DBAProbability) {
			return 1;
		} else if (DBAProbability < arg0.DBAProbability) {
			return -1;
		}
		return 0;
	}
	

}
