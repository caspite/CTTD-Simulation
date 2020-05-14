package Helpers;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import PoliceTaskAllocation.*;
import TaskAllocation.Assignment;
import TaskAllocation.Task;
import TaskAllocation.Utility;
import SW.SW;

public class Metrics {

	protected String algorithmName;
	protected int shiftNum;
	// Number of incoming events for every type
	protected int numOfEvents1 = 0, numOfEvents2 = 0, numOfEvents3 = 0,
			numOfEvents4 = 0, numOfEvents;
	// Number of allocated events
	protected int numOfAlloctedEvent1 = 0;
	protected int numOfAlloctedEvent2 = 0;
	protected int numOfAlloctedEvent3 = 0;
	protected int numOfAlloctedEvent4 = 0;
	protected int numOfAlloctedEvent = 0;
	//Number of completed events
	protected int numOfCompletedEvent1 = 0;
	protected int numOfCompletedEvent2 = 0;
	protected int numOfCompletedEvent3 = 0;
	protected int numOfCompletedEvent4 = 0;
	protected int numOfCompletedEvent = 0;
	// Number of abandoned events
	protected int abandonedEvents = 0, abandonedEvents1 = 0,
			abandonedEvents2 = 0, abandonedEvents3 = 0, abandonedEvents4 = 0;
	// The time that mission waits until the agents start handling with
	protected double sumTime1, sumTime2, sumTime3, sumTime4, sumTime;
	// Sum of SW
	protected double SW1=0, SW2=0, SW3=0, SW4=0, SWT=0, SWP=0, SWPenalty=0;
	// Sum of copleted SW
    protected double SWC1=0, SWC2=0, SWC3=0, SWC4=0, SWCT=0;
	// Number of sharing events
	protected int sharing, sharing1, sharing2;
	//Percentage of sharing for type 1 and type 2
	protected double sharing1_1=0, sharing1_2=0, sharing1_3 = 0, sharing2_1 = 0,sharing2_2 = 0;
	//Percentage of working time on a mission from total time in the system;
	protected double workTimeType1 = 0, workTimeType2 =0,workTimeType3 = 0, workTimeType4 =0, workTime = 0;
	// Division of the time
	protected double movingTime, waitingTime, workingTime, workingPatrols;
	//Percentage of reallocation
	protected double reallocation;
	protected double reallocationCount;
	
	protected TreeMap<Double, Double> cumulative = new TreeMap<Double, Double>();

	protected HashSet<Integer> allocated = new HashSet<Integer>();
	protected HashSet<Integer> completed = new HashSet<Integer>();
	protected HashSet<Integer> abandoned = new HashSet<Integer>();
	protected HashSet<Integer> share = new HashSet<Integer>();
	

	public Metrics(int numOfEvents, String algorithmName, int shiftNum) {
		this.numOfEvents = numOfEvents;
		this.algorithmName = algorithmName;
	
		
	}
	// counts diary events according to type of the event
	public void countEvents(DiaryEvent d) {

		if (d.getEvent().getPriority() == 1) {
			numOfEvents1++;
		} else if (d.getEvent().getPriority() == 2) {
			numOfEvents2++;
		} else if (d.getEvent().getPriority() == 3) {
			numOfEvents3++;
		} else if (d.getEvent().getPriority() == 4) {
			numOfEvents4++;
		}

	}

	// sums the time until first arrival to the event divided to types of events
	public void sumArrivalTime(double d, Assignment a) {
		sumTime = sumTime + d;
		if (a.getTask().getPriority() == 1) {
			sumTime1 = sumTime1 + d;
		} else if (a.getTask().getPriority() == 2) {
			sumTime2 = sumTime2 + d;
		} else if (a.getTask().getPriority() == 3) {
			sumTime3 = sumTime3 + d;
		} else if (a.getTask().getPriority() == 4) {
			sumTime4 = sumTime4 + d;
		}

	}

	// writes to file the final parameters to file (average time, abandoned,
	// allocated,sharing)
	public void writeParametersForFiles() {
		
		BufferedWriter out = null;
		try {
			FileWriter s = new FileWriter("" + algorithmName +"_"+numOfEvents+ "_parmeter.csv",
					true);
			out = new BufferedWriter(s);
			out.newLine();
			String o = "" + shiftNum + ","
					+ (double) numOfAlloctedEvent1 / (double) numOfEvents1
					+ ","
					+ (double) numOfAlloctedEvent2 / (double) numOfEvents2
					+ ","
					+ (double) numOfAlloctedEvent3 / (double) numOfEvents3
					+ ","
					+ (double) numOfAlloctedEvent4 / (double) numOfEvents4
					+ "," + (double) numOfAlloctedEvent / numOfEvents + ","
					+ (double) abandonedEvents1 / (double) numOfAlloctedEvent1
					+ ","
					+ (double) abandonedEvents2 / (double) numOfAlloctedEvent2
					+ ","
					+ (double) abandonedEvents3 / (double) numOfAlloctedEvent3
					+ ","
					+ (double) abandonedEvents4 / (double) numOfAlloctedEvent4
					+ ","
					+ (double) abandonedEvents / (double) numOfAlloctedEvent
					+ "," + (double) sumTime1 / (double) numOfAlloctedEvent1
					+ "," + (double) sumTime2 / (double) numOfAlloctedEvent2
					+ "," + (double) sumTime3 / (double) numOfAlloctedEvent3
					+ "," + (double) sumTime4 / (double) numOfAlloctedEvent4
					+ "," + (double) sumTime / (double) numOfAlloctedEvent
					+ "," + (double) sharing1 / (double) (numOfAlloctedEvent1)
					+ "," + (double) sharing2 / (double) (numOfAlloctedEvent2)
					+ "," + (double) sharing
					/ (double) (numOfAlloctedEvent1 + numOfAlloctedEvent2)
					+ "," + SW1 + "," + SW2 + "," + SW3 + "," + SW4+ "," + SWP+ "," + SWPenalty
					+ "," + SWT + "," + SW1 / SWT + "," + SW2 / SWT
					+ "," + SW3 / SWT + "," + SW4 / SWT + "," + SWP / SWT
					+","+workingTime+","+waitingTime+","+movingTime+","+workingPatrols
					+","+workingTime/(workingTime+waitingTime+movingTime+workingPatrols)
					+","+waitingTime/(workingTime+waitingTime+movingTime+workingPatrols)
					+","+movingTime/(workingTime+waitingTime+movingTime+workingPatrols)
					+","+workingPatrols/(workingTime+waitingTime+movingTime+workingPatrols)+","+reallocation/reallocationCount
					+","+(double)numOfCompletedEvent1/(double)numOfAlloctedEvent1
					+","+(double)numOfCompletedEvent2/(double)numOfAlloctedEvent2
					+","+(double)numOfCompletedEvent3/(double)numOfAlloctedEvent3
					+","+(double)numOfCompletedEvent4/(double)numOfAlloctedEvent4
					+","+(double)numOfCompletedEvent/numOfAlloctedEvent
					+","+workTimeType1/numOfCompletedEvent1
					+","+workTimeType2/numOfCompletedEvent2
					+","+workTimeType3/numOfCompletedEvent3
					+","+workTimeType4/numOfCompletedEvent4
					+","+workTime/numOfCompletedEvent
					+","+sharing1_1/numOfCompletedEvent1
					+","+sharing1_2/numOfCompletedEvent1
					+","+sharing1_3/numOfCompletedEvent1
					+","+sharing2_1/numOfCompletedEvent2
					+","+sharing2_2/numOfCompletedEvent2
					+","+SWC1/numOfCompletedEvent1
					+","+SWC2/numOfCompletedEvent2
					+","+SWC3/numOfCompletedEvent3
					+","+SWC4/numOfCompletedEvent4;
			

			out.write(o);
			out.close();
		} catch (IOException e) {
			System.err.println("Metrics: Couldn't write to file");
		}

	}


	// counts allocated events when these arrive to the system
	public void countAllocatedEvents(Assignment a, double Tnow) {
		if (a.getTask() instanceof PatrolEvent) {
			return;
		}
		if (allocated.contains(a.getTask().getId())) {
			if (a.getTask().getNumAgentsRequiered() > 1 && a.getTask().getNumOfAllocatedAgents()>0) {
				countSharing(a.getTask());
				return;
			}
			return;
		}
		double time = a.getTask().getDFTime(Tnow)/10.0;
		sumArrivalTime(time, a);
		allocated.add(a.getTask().getId());
		numOfAlloctedEvent++;
		if (a.getTask().getPriority() == 1) {
			numOfAlloctedEvent1++;
		} else if (a.getTask().getPriority() == 2) {
			numOfAlloctedEvent2++;
		} else if (a.getTask().getPriority() == 3) {
			numOfAlloctedEvent3++;
		} else if (a.getTask().getPriority() == 4) {
			numOfAlloctedEvent4++;
		}

	}

	// counts abandoned events according to type of the event
	public void countAbandoned(Task as,double tnow) {
		if (abandoned.contains(as.getId())) {
			return;
		}

		abandoned.add(as.getId());
		abandonedEvents++;
		if (as.getPriority() == 1) {
			abandonedEvents1++;
		} else if (as.getPriority() == 2) {
			abandonedEvents2++;
		} else if (as.getPriority() == 3) {
			abandonedEvents3++;
		} else if (as.getPriority() == 4) {
			abandonedEvents4++;
		}
		penaltyForAbandoning(as, tnow);
	}

	// counts shared missions
	public void countSharing(Task as) {
		if (share.contains(as.getId())) {
			return;
		}
		share.add(as.getId());
		if (as.getPriority() == 1) {
			sharing1++;
			sharing++;
		} else if (as.getPriority() == 2) {
			sharing2++;
			sharing++;
		}

	}
//sum SW
	public void sumSW(double sw, Task as) {
		SWT = SWT + sw;
		if(as instanceof PatrolEvent){
			SWP += sw;
			return;
		}
		if (as.getPriority() == 1) {
			SW1 += sw;
		} else if (as.getPriority() == 2) {
			SW2 += sw;
		} else if (as.getPriority() == 3) {
			SW3 += sw;;
		} else if (as.getPriority() == 4) {
			SW4 += sw;
		}
	}
//Sums time 
	public void sumTime(double time, Status type){
		switch (type) {
        case WORKING:
        	workingTime += time;
            break;
                
        case MOVING:
        	movingTime += time;		
            break;
                     
        case WAITING:
        	waitingTime += time;		
            break;
            
        case PATROLING:
        	workingPatrols += time;		
            break;
		}

		
	}
	public void sumTime(double tnow, double told, Vector<PoliceUnit> policeUnits) {
		for (PoliceUnit p : policeUnits) {
			  sumTime(tnow-told, p.getStatus());
		}
		
	}
	public void calculateRealocation(Vector<Assignment>[] oldAll,
			Vector<Assignment>[] newAll, Vector<Task> activeEvents) {
		for (int i = 0; i < newAll.length; i++) {
			int count=0;
			for (Assignment as : oldAll[i]) {
				if(as.getTask() instanceof MissionEvent && !newAll[i].contains(as) && activeEvents.contains(as.getTask())){
					count++;
				}
			}
			reallocationCount++;
			reallocation += (double)count/(double)oldAll.length;
			
		}
		
	}
	public  void checkNAN2() {
		if(numOfAlloctedEvent3 == 0){
			numOfAlloctedEvent3 =1;
		}
		if(numOfAlloctedEvent4 == 0){
			numOfAlloctedEvent4 =1;
		}
		if(numOfAlloctedEvent2 == 0){
			numOfAlloctedEvent2 =1;
		}
		if(numOfAlloctedEvent1 == 0){
			numOfAlloctedEvent1 =1;
		}
		
		if(numOfCompletedEvent1 == 0){
			numOfCompletedEvent1 =1;
			SWC1=1;
		}
		if(numOfCompletedEvent2 == 0){
			numOfCompletedEvent2 =1;
			SWC2=1;
		}
		if(numOfCompletedEvent3 == 0){
			numOfCompletedEvent3 =1;
			SWC3=1;
		}
		if(numOfCompletedEvent4 == 0){
			numOfCompletedEvent4 =1;
			SWC4=1;
		}
	
		
	}
	public void checkNAN1() {
		if(numOfEvents1 == 0){
			numOfEvents1 =1;
		}
		if(numOfEvents2 == 0){
			numOfEvents2 =1;
		}
		if(numOfEvents3 == 0){
			numOfEvents3 =1;
		}
		if(numOfEvents4 == 0){
			numOfEvents4 =1;
		}
		
		
	}
	public void penaltyForAbandoning(Task task, double tnow) {
		if(task instanceof PatrolEvent){
			return;
		}
		double penalty = SW.calculatePenaltyForAbandonment(tnow, task);
		
		if (task.getPriority() == 1) {
			SW1 -= penalty;
		} else if (task.getPriority() == 2) {
			SW2 -= penalty;
		} else if (task.getPriority() == 3) {
			SW3 -= penalty;
		} else if (task.getPriority() == 4) {
			SW4 -= penalty;
		}
		SWT -= penalty;
		SWPenalty -= penalty;
	}
	
	//Sums completed events
	public void missionCompleted(Task as, double tnow) {
		if(completed.contains(as.getId())){
			return;
		}
		completed.add(as.getId());
		numOfCompletedEvent++;
		if (as.getPriority() == 1) {
			SWC1 +=as.getSW();
			numOfCompletedEvent1++;
		} else if (as.getPriority() == 2) {
			SWC2 +=as.getSW();
			numOfCompletedEvent2++;
		} else if (as.getPriority() == 3) {
			SWC3 +=as.getSW();
			numOfCompletedEvent3++;
		} else if (as.getPriority() == 4) {
			SWC4 +=as.getSW();
			numOfCompletedEvent4++;
		}
		SWCT +=as.getSW();
		sumSharingTime( as,  tnow);
	}
	
	//Sums sharing time
	private void sumSharingTime(Task as, double tnow) {

		TreeMap<Integer, Double> times = as.getWorkingTime();
		double totalTime=0;
		double workTime =0;
		for (Double t : times.values()) {
			totalTime += t;
		}
		workTime = totalTime - times.firstEntry().getValue();
		if(as.getPriority()==1){
			sharing1_1 += times.get(1)/workTime;
			sharing1_2 += times.get(2)/workTime;
			sharing1_3 += times.get(3)/workTime;
			workTimeType1 += workTime/totalTime;
			
		}
		if(as.getPriority()==2){
			sharing2_1 += times.get(1)/workTime;
			sharing2_2 += times.get(2)/workTime;
			workTimeType2 += workTime/totalTime;
		}
		if(as.getPriority()==3){
			workTimeType3 += workTime/totalTime;
		}
		if(as.getPriority()==4){
			workTimeType4 += workTime/totalTime;
		}
		this.workTime += workTime/totalTime;
		
		
	}
	
	
	

}
