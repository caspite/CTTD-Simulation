package Helpers;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import PoliceTaskAllocation.*;
import TaskAllocation.Assignment;
import TaskAllocation.Task;

public class MetricsSummary {

	
	private String algorithmName;
	
	// Number of incoming events for every type
	private int numOfEvents;
	private double numOfShifts;
	
	// Number of allocated events
	private double alloctedEvent1 = 0, alloctedEvent2 = 0,
			alloctedEvent3 = 0, alloctedEvent4 = 0,
			alloctedEvent = 0;
	
	//Number of completed events
	private double percentageOfCompletedEvent1 = 0;
	private double percentageOfCompletedEvent2 = 0;
	private double percentageOfCompletedEvent3 = 0;
	private double percentageOfCompletedEvent4 = 0;
	private double percentageOfCompletedEvent = 0;
	
	// Number of abandoned events
	private double abandonedEvents = 0, abandonedEvents1 = 0,
			abandonedEvents2 = 0, abandonedEvents3 = 0, abandonedEvents4 = 0;
	
	//Percentage of sharing for type 1 and type 2
	private double sharing1_1=0, sharing1_2=0, sharing1_3 = 0, sharing2_1 = 0,sharing2_2 = 0;
	
	//Percentage of working time on a mission from total time in the system;
	private double workTimeType1 = 0, workTimeType2 =0,workTimeType3 = 0, workTimeType4 =0, workTime = 0;
	
	// The time that mission waits until the agents start handling with
	private double sumTime1, sumTime2, sumTime3, sumTime4, sumTime;
	
	// Sum of SW
	private double SW1, SW2, SW3, SW4, SWP, SW, SWPenalty, SWPercent1,SWPercent2, SWPercent3, SWPercent4,SWPPercent,
	SWC1, SWC2, SWC3, SWC4 ;
	
	// Number of sharing events
	private double sharing, sharing1, sharing2;

	// Division of the time
	private double movingTime, waitingTime, workingTime, movingTimePercent, waitingTimePercent, 
					workingTimePercent,workingPatrols ,workingPatrolsPercent ;
	//Percentage of reallocation
	private double reallocation;
	private double reallocationCount;
	//Constructor that receives the name of the algorithm
	public MetricsSummary( String algorithmName, int numOfShifts) {
		this.algorithmName = algorithmName;
		this.numOfShifts = numOfShifts;
		createFile1();
		
		}
	
	private void createFile1() {
		BufferedWriter out = null;
		try {
			FileWriter s = new FileWriter("" + algorithmName + "_summury_parmeter.csv",
					true);
			out = new BufferedWriter(s);
			String h = "numOfEvents ,allocated1,allocated2,allocated3,allocated4,allocated,abandoned1,abandoned2,abandoned3,abandoned4,abandoned,"
					+ "Arrival time1,Arrival time2,Arrival time3,Arrival time4,Arrival time,sharing1,sharing2,sharing,SW1,SW2,SW3,SW4,SWP,SWpenalty, SW,SW1 %,"
					+ "SW2 %, SW3 %,SW4 %,SWP %,Working,Waiting,Moving,Patrolong,Working%,Waiting%,Moving%,Patrolong%,Reallocation%,"
					+"completed1,completed2,completed3,completed4,completed,working1, working2, working3, working4, working,"
					+"sharing1_1,sharing1_2,sharing1_3,sharing2_1,sharing2_2,SW1_average,SW2_average,SW3_average,SW4_average";
			out.write(h);
			out.close();
		} catch (IOException e) {
			System.err.println("Couldn't write to file");
		}
		
	}

	//Creates new metrics instance for every shift
	public synchronized Metrics createNewMetrics(int shiftNum) {
		Metrics metrics = new Metrics(numOfEvents, algorithmName, shiftNum);
		return metrics;
		
	}
	// create file with headlines for the full data
	private void createFile() {
			try {
			FileWriter s = new FileWriter("" + algorithmName +"_"+numOfEvents+ "_parmeter.csv",
					false);
			BufferedWriter out = new BufferedWriter(s);
			String h = "shift,allocated1,allocated2,allocated3,allocated4,allocated,abandoned1,abandoned2,abandoned3,abandoned4,abandoned,"
					+ "Arrival time1,Arrival time2,Arrival time3,Arrival time4,Arrival time,sharing1,sharing2,sharing,SW1, SW2, SW3,SW4,SWP,SWpenalty, SW,SW1 %,"
					+ "SW2 %, SW3 %,SW4 %,SWP %,Working,Waiting,Moving,Patroling,Working%,Waiting%,Moving%,Patroling%,Reallocation%,"
					+"completed1,completed2,completed3,completed4,completed,working1, working2, working3, working4, working,"
					+"sharing1_1,sharing1_2,sharing1_3,sharing2_1,sharing2_2,SW1_average,SW2_average,SW3_average,SW4_average";
			out.write(h);
			out.close();
		}catch (IOException e) {
			System.err.println("MetricsSummary1: Couldn't write to file");
		}
		
	}
	
	//Initialize the parameters 
	public synchronized void setup(long num){
		numOfEvents = (int) num;
		alloctedEvent1 = 0;
		alloctedEvent2 = 0; 
		alloctedEvent3 = 0;
		alloctedEvent4 = 0;
		alloctedEvent = 0;
		
		abandonedEvents1 = 0; 
		abandonedEvents2 = 0; 
		abandonedEvents3 = 0; 
		abandonedEvents4 = 0; 
		abandonedEvents = 0;
		
		sumTime1 = 0; 
		sumTime2 = 0; 
		sumTime3 = 0; 
		sumTime4 = 0; 
		sumTime = 0;
		sharing1 = 0; 
		sharing2 = 0; 
		sharing = 0;
		
		SW1 = 0;
		SW2 = 0;
		SW3 = 0;
		SW4 = 0;
		SWP = 0;
		SW = 0;
		
		SWPenalty = 0;
		SWC1 = 0;
		SWC2 = 0;
		SWC3 = 0;
		SWC4 = 0;
		SWPercent1 = 0;
		SWPercent2 = 0;
		SWPercent3 = 0;
		SWPercent4  = 0;
		SWPPercent = 0;
		
		workingTime = 0;
		waitingTime = 0;
		movingTime = 0;
		workingPatrols = 0;
		workingTimePercent = 0;
		waitingTimePercent = 0;
		movingTimePercent = 0; 
		workingPatrolsPercent = 0;
		reallocation = 0; 
		
		percentageOfCompletedEvent1 = 0;
		percentageOfCompletedEvent2 = 0;
		percentageOfCompletedEvent3 = 0;
		percentageOfCompletedEvent4 = 0;
		percentageOfCompletedEvent = 0;
		
		sharing1_1=0;
		sharing1_2=0;
		sharing1_3 = 0;
		sharing2_1 = 0;
		sharing2_2 = 0;
		
		workTimeType1 = 0;
		workTimeType2 =0;
		workTimeType3 = 0;
		workTimeType4 =0;
		workTime = 0;
		
		createFile();
		
		
	}


	// writes to file the final parameters to file 
	//Writes to file average metrics
	public void writeToFile() {
		BufferedWriter out = null;
		try {
			FileWriter s = new FileWriter("" + algorithmName + "_summury_parmeter.csv",
					true);
			out = new BufferedWriter(s);
			out.newLine();
			String o = "" + numOfEvents + "," 
			+ alloctedEvent1/numOfShifts + "," + alloctedEvent2/numOfShifts + "," + alloctedEvent3/numOfShifts + "," + alloctedEvent4/numOfShifts + "," +alloctedEvent/numOfShifts + "," 
			+ abandonedEvents1/numOfShifts+ "," + abandonedEvents2/numOfShifts + "," + abandonedEvents3/numOfShifts + "," + abandonedEvents4/numOfShifts + "," + abandonedEvents/numOfShifts + "," 
			+ sumTime1/numOfShifts + "," + sumTime2/numOfShifts + "," + sumTime3/numOfShifts + "," + sumTime4/numOfShifts + "," + sumTime/numOfShifts + "," 
			+ sharing1/numOfShifts + "," + sharing2/numOfShifts + "," + sharing/numOfShifts + "," 
			+ SW1/numOfShifts + "," + SW2/numOfShifts + "," + SW3/numOfShifts + "," + SW4/numOfShifts + ","+ SWP/numOfShifts  + ","+ SWPenalty/numOfShifts+ "," + SW/numOfShifts + "," 
			+ SWPercent1/numOfShifts + "," + SWPercent2/numOfShifts + "," + SWPercent3/numOfShifts + "," + SWPercent4/numOfShifts + "," + SWPPercent/numOfShifts + ","
			+ workingTime/numOfShifts + "," + waitingTime/numOfShifts + "," + movingTime/numOfShifts + "," + workingPatrols/numOfShifts + ","
			+ workingTimePercent/numOfShifts + "," + waitingTimePercent/numOfShifts + "," + movingTimePercent/numOfShifts + "," + workingPatrolsPercent/numOfShifts + "," 
			+ reallocation/numOfShifts + "," + percentageOfCompletedEvent1/numOfShifts+ "," + percentageOfCompletedEvent2/numOfShifts
			+ "," + percentageOfCompletedEvent3/numOfShifts+ "," + percentageOfCompletedEvent4/numOfShifts + "," + percentageOfCompletedEvent/numOfShifts
			+ "," + workTimeType1/numOfShifts+ "," + workTimeType2/numOfShifts+ "," + workTimeType3/numOfShifts+ "," + workTimeType4/numOfShifts+ "," + workTime/numOfShifts
			+ "," + sharing1_1/numOfShifts+ "," + sharing1_2/numOfShifts+ "," + sharing1_3/numOfShifts
			+ "," + sharing2_1/numOfShifts+ "," + sharing2_2/numOfShifts+ "," + SWC1/numOfShifts+ "," + SWC2/numOfShifts
			+ "," + SWC3/numOfShifts+ "," + SWC4/numOfShifts;

			out.write(o);
			out.close();
		} catch (IOException e) {
			System.err.println("MetricsSummary: Couldn't write to file");
		}

	}
	//updates summarized parameters when the shift ends
	public synchronized void updateParameters(Metrics metrics) {
			metrics.checkNAN1();
			metrics.checkNAN2();
			alloctedEvent1 += (double)metrics.numOfAlloctedEvent1 / (double)metrics.numOfEvents1;
			
			alloctedEvent2 += (double)metrics.numOfAlloctedEvent2 / (double) metrics.numOfEvents2;
	
			alloctedEvent3 += (double) metrics.numOfAlloctedEvent3 / (double) metrics.numOfEvents3;
			
			alloctedEvent4 += (double) metrics.numOfAlloctedEvent4 / (double) metrics.numOfEvents4;
			
			
			alloctedEvent += (double) metrics.numOfAlloctedEvent / metrics.numOfEvents;
			
			abandonedEvents1 += (double) metrics.abandonedEvents1 / (double) metrics.numOfAlloctedEvent1;
			
			abandonedEvents2 += (double) metrics.abandonedEvents2 / (double) metrics.numOfAlloctedEvent2;
			
			abandonedEvents3 += (double) metrics.abandonedEvents3 / (double) metrics.numOfAlloctedEvent3;
			
			abandonedEvents4 += (double) metrics.abandonedEvents4 / (double) metrics.numOfAlloctedEvent4;
			
			abandonedEvents += (double) metrics.abandonedEvents / (double) metrics.numOfAlloctedEvent;
			
			sumTime1 +=  (double) metrics.sumTime1 / (double) metrics.numOfAlloctedEvent1;
			sumTime2 += (double) metrics.sumTime2 / (double) metrics.numOfAlloctedEvent2;
			sumTime3 += (double) metrics.sumTime3 / (double) metrics.numOfAlloctedEvent3;
			sumTime4 += (double) metrics.sumTime4 / (double) metrics.numOfAlloctedEvent4;
			sumTime += (double) metrics.sumTime / (double) metrics.numOfAlloctedEvent;
			sharing1 += (double) metrics.sharing1 / (double) (metrics.numOfAlloctedEvent1);
			sharing2 += (double) metrics.sharing2 / (double) (metrics.numOfAlloctedEvent2);
			sharing += (double) metrics.sharing / (double) (metrics.numOfAlloctedEvent1 + metrics.numOfAlloctedEvent2);
			
			SW1 += metrics.SW1; SW2 += metrics.SW2; SW3 += metrics.SW3; SW4 += metrics.SW4;SWP += metrics.SWP;SWPenalty += metrics.SWPenalty;
			
			SW += metrics.SWT; SWPercent1 += metrics.SW1 / metrics.SWT; SWPercent2 += metrics.SW2 / metrics.SWT;
			SWPercent3 += metrics.SW3 / metrics.SWT; SWPercent4 += metrics.SW4 / metrics.SWT; SWPPercent += metrics.SWP / metrics.SWT;
			
			workingTime += metrics.workingTime; waitingTime += metrics.waitingTime; movingTime += metrics.movingTime;workingPatrols += metrics.workingPatrols;
			workingTimePercent += metrics.workingTime/(metrics.workingTime+metrics.waitingTime+metrics.movingTime+metrics.workingPatrols);
			waitingTimePercent += metrics.waitingTime/(metrics.workingTime+metrics.waitingTime+metrics.movingTime+metrics.workingPatrols);
			movingTimePercent +=  metrics.movingTime/(metrics.workingTime+metrics.waitingTime+metrics.movingTime+metrics.workingPatrols);
			workingPatrolsPercent += metrics.workingPatrols/(metrics.workingTime+metrics.waitingTime+metrics.movingTime+metrics.workingPatrols);
			reallocation += metrics.reallocation/metrics.reallocationCount;
			percentageOfCompletedEvent1 += (double)metrics.numOfCompletedEvent1/(double)metrics.numOfAlloctedEvent1;
			percentageOfCompletedEvent2 += (double)metrics.numOfCompletedEvent2/(double)metrics.numOfAlloctedEvent2;
			percentageOfCompletedEvent3 += (double)metrics.numOfCompletedEvent3/(double)metrics.numOfAlloctedEvent3;
			percentageOfCompletedEvent4 += (double)metrics.numOfCompletedEvent4/(double)metrics.numOfAlloctedEvent4;
			percentageOfCompletedEvent += (double)metrics.numOfCompletedEvent/(double)metrics.numOfAlloctedEvent;
			workTimeType1 += metrics.workTimeType1/metrics.numOfCompletedEvent1;
			workTimeType2 += metrics.workTimeType2/metrics.numOfCompletedEvent2;
			workTimeType3 += metrics.workTimeType3/metrics.numOfCompletedEvent3;
			workTimeType4 += metrics.workTimeType4/metrics.numOfCompletedEvent4;
			workTime += metrics.workTime/metrics.numOfCompletedEvent;

			sharing1_1 += metrics.sharing1_1/metrics.numOfCompletedEvent1;
			sharing1_2 += metrics.sharing1_2/metrics.numOfCompletedEvent1;
			sharing1_3 += metrics.sharing1_3/metrics.numOfCompletedEvent1;
			sharing2_1 += metrics.sharing2_1/metrics.numOfCompletedEvent2;
			sharing2_2 += metrics.sharing2_2/metrics.numOfCompletedEvent2;
			
			SWC1 += metrics.SWC1/metrics.numOfCompletedEvent1;
			SWC2 += metrics.SWC2/metrics.numOfCompletedEvent2;
			SWC3 += metrics.SWC3/metrics.numOfCompletedEvent3;
			SWC4 += metrics.SWC4/metrics.numOfCompletedEvent4;
	}
	
}
