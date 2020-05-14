package PoliceTaskAllocation;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import Helpers.*;
import TaskAllocation.Location;
import TaskAllocation.Task;

public class Initialize {

	private Vector<PoliceUnit> policeUnits;
	private Vector<Task> activeEvent;
	private int[][] constraints;
	private TreeSet<DiaryEvent> diary;
	private int numOfPatrols;
	private int numOfAgents;
	private Random random;

	public Initialize(int agents, int numOfPatrols, int seed) {
		policeUnits = new Vector<PoliceUnit>();
		activeEvent = new Vector<Task>();
		diary = new TreeSet<DiaryEvent>();
		this.numOfAgents = agents;
		this.numOfPatrols = numOfPatrols;
		random = new Random(seed);

	}

	// -----------------------------Getters-----------------------------

	public Vector<PoliceUnit> getPoliceUnits() {
		return policeUnits;
	}

	public Vector<Task> getActiveEvent() {
		return activeEvent;
	}

	public int[] getConstraints() {
		return constraints[0];
	}

	public TreeSet<DiaryEvent> getDiary() {
		return diary;
	}

	// -----------------------Create-----------------------------------

	public void createPatrolAreas(String fileName, String sheetName) {

		ReadXLSheet xlReader = new ReadXLSheet(fileName, sheetName);

		for (int i = 1; i < xlReader.getRowsNum(); i++) {
			//Reset
			int code = 0;
			double lat = 0;
			double lng = 0;
			int name = 0;
			int utility = 0;
			String[] s = xlReader.rowReading(i);
			for (int j = 0; j < s.length; j++) {
				switch (j) {
				case 0:
					code = Integer.parseInt(s[j]);
					break;
				case 1:
					name = Integer.parseInt(s[j]);//*-1;
					break;
				case 2:
					utility = Integer.parseInt(s[j]);
					break;
				case 3:
					lat = Double.parseDouble(s[j]);
					break;
				case 4:
					lng = Double.parseDouble(s[j]);
					break;
				default:
					break;
				}
			}
			HashMap<AgentType, Integer> agentsRequired = new HashMap<AgentType, Integer>();
			//agentsRequired.put(AgentType.TYPE1,1);
			for (AgentType agent : AgentType.values()) {
				agentsRequired.put(agent,1);
			}
			activeEvent.add(new PatrolEvent(new Location(lat, lng), 3600, 0,
					name, 100, utility, agentsRequired));

		}
		createPoliceUnits();
		xlReader.closeXLFile();
	}

	private void createPoliceUnits() {
		for (int i = 1; i <= numOfAgents; i++) {
			HashSet<AgentType> agentType = new HashSet<AgentType>();
			agentType.add(AgentType.TYPE1);
			/*
			 * int ran = random.nextInt(2); agentType.add(AgentType.values()[ran]); ran =
			 * random.nextInt(3) + 2; agentType.add(AgentType.values()[ran]);
			 */
//			if(i<6){
//				agentType.add(AgentType.TYPE1);
//			}
//			else{
//				agentType.add(AgentType.TYPE2);
//			}
			policeUnits.add(new PoliceUnit(i,agentType));
		}

	}

	public void createMissionEvents(String fileName) {

		BufferedReader br = null;
		String str;
		String cvsSplitBy = ",";
		int code = 0;
		try {
			br = new BufferedReader(new FileReader(fileName));
			str = br.readLine();

			while ((str = br.readLine()) != null) {

				// use comma as separator
				String[] data = str.split(cvsSplitBy);

				code++;
				int priority = Integer.parseInt(data[0]);
				
				double time = Double.parseDouble(data[1]);
				double duration = Double.parseDouble(data[2]);
				double lat = Double.parseDouble(data[3]);
				double lng = Double.parseDouble(data[4]);
				
				
				MissionEvent me = new MissionEvent(new Location(lat, lng),
						duration, time, code, priority,
						constraints[0][priority - 1], unitsForMissionHomoge(priority));
				diary.add(new NewDiaryEvent(me));
			}

		} catch (IOException e) {
			System.out.println("CreatMissionEvent: Couldn't read file");
		}
	}

	private HashMap<AgentType, Integer> unitsForMissionHomoge(int priority) {
		HashMap<AgentType, Integer> unitsNeed = new HashMap<AgentType, Integer>();
		//int ran = 0;
		 
		if (priority == 1) {
			unitsNeed.put(AgentType.TYPE1, 3);
		}		
		if (priority == 2) {
			unitsNeed.put(AgentType.TYPE1, 2);
		}
		if (priority >= 3) {
			unitsNeed.put(AgentType.TYPE1, 1);
		}
		return unitsNeed;
	}

	// crates random assignment of capabilities for missions
	private HashMap<AgentType, Integer> unitsForMissionHetero(int priority) {
		HashMap<AgentType, Integer> unitsNeed = new HashMap<AgentType, Integer>();
		int ran = 0;
		
		 
		if (priority == 1) {
			unitsNeed.put(AgentType.TYPE1, 2);
			ran = random.nextInt(3)+2;
			unitsNeed.put(AgentType.values()[ran], 1);
		}		
		if (priority == 2) {
			unitsNeed.put(AgentType.TYPE2, 1);
			ran = random.nextInt(3)+2;
			unitsNeed.put(AgentType.values()[ran], 1);
		}
		if (priority == 3) {
			unitsNeed.put(AgentType.TYPE1, 1);
		}
		if (priority == 4) {
			unitsNeed.put(AgentType.TYPE2, 1);
		}
		return unitsNeed;
	}
	
	private HashMap<AgentType, Integer> unitsForMissionHetero2(int priority) {
		HashMap<AgentType, Integer> unitsNeed = new HashMap<AgentType, Integer>();
		int ran = 0;
		 
		if (priority == 1) {
			unitsNeed.put(AgentType.TYPE1, 2);
			unitsNeed.put(AgentType.TYPE2, 1);

		}		
		if (priority == 2) {
			unitsNeed.put(AgentType.TYPE1, 1);
			unitsNeed.put(AgentType.TYPE2, 1);
		}
		if (priority == 3) {
			unitsNeed.put(AgentType.TYPE1, 1);
		}
		if (priority == 4) {
			unitsNeed.put(AgentType.TYPE1, 1);
		}
		return unitsNeed;
	}

	public void createConstraints(String fileName, String sheetName) {
		ReadXLSheet xlReader = new ReadXLSheet(fileName, sheetName);
		constraints = new int[2][xlReader.getRowsNum() - 1];

		for (int i = 1; i < xlReader.getRowsNum(); i++) {

			String[] s = xlReader.rowReading(i);
			for (int j = 0; j < s.length; j++) {
				switch (j) {
				case 0:
					break;
				case 1:
					constraints[j - 1][i - 1] = Integer.parseInt(s[j]);
					break;
				case 2:
					constraints[j - 1][i - 1] = Integer.parseInt(s[j]);
					break;

				}
			}
		}
	}

}
