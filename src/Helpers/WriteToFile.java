package Helpers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import CTTD.Casualty;
import CTTD.MedicalUnit;
import TaskAllocation.Assignment;
import TaskAllocation.Task;
import TaskAllocation.Utility;

public class WriteToFile {

	//---------------static class write fo file - outputs and generate CTTD problam----------------//
	//TODO add titles for sheets + write to sheets

	public static void CTTD_DisasterSite(String fileName, Vector<Task> DiSasterSites) {
		try {
			BufferedWriter out = openFile2(fileName);
			for(Task ds :DiSasterSites){
				String o = ""+ ds.getId()+","+ ds.getLocation().getLat()+","+ ds.getLocation().getLng()+","+ ds.getMissionArrivalTime();
				out.write(o);
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Couldn't write to file");
		}
	}
	public static void CTTD_Casualties(String fileName, Vector<Casualty> Casualties) {
		try {
			BufferedWriter out = openFile2(fileName);
			for(Casualty cas:Casualties){
				String o = ""+cas.id+","+cas.DS_Id+","+cas.getTBorn()+","+cas.survival+","+cas.getTriage();
				out.write(o);
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Couldn't write to file");
		}
	}
	public static void CTTD_MedicalUnits(String fileName, Vector<MedicalUnit> MedicalUnits) {
		try {
			BufferedWriter out = openFile2(fileName);

			for(MedicalUnit MU:MedicalUnits){
				String o = ""+MU.getId()+","+MU.getLocation().getLat()+","+MU.getLocation().getLng()+","+MU.getOneAgentType();
				out.write(o);
				out.newLine();
			}

			out.close();
		} catch (IOException e) {
			System.err.println("Couldn't write to file");
		}
	}

	public static void writeOutput(String fileName, HashMap<Integer, Double> output1, String algorithm) {
		try {


			BufferedWriter out = openFile2(fileName);
			String o = "" + algorithm;
			out.write(o);
			out.newLine();
			o="iteration"+","+"global cost";
			out.write(o);
			out.newLine();

			for (Integer it : output1.keySet()) {
				o = "" + it + "," + output1.get(it);
				out.write(o);
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Couldn't write to file");
		}
	}

	/// write to file fishers algorithm output
	public static void writeFisherOutpuToFile(Double[][] ds) {
		try {
			BufferedWriter out = openFile("output_allocation.csv");
			for (int i = 0; i < ds.length; i++) {
				for (int j = 0; j < ds[i].length; j++) {
					String o = "" + ds[i][j] + ",";
					out.write(o);
				}
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Couldn't write to file");
		}
	}
	
	public static void writeUtilitiesToFile(Utility[][] ds) {
		try {
			BufferedWriter out = openFile("output_allocation.csv");
			for (int i = 0; i < ds.length; i++) {
				for (int j = 0; j < ds[i].length; j++) {
					if (ds[i][j] == null) {
						String o = "" + "null" + ",";
						out.write(o);
					}
					else{
						String o = "" + ds[i][j].getUtility(1) + ",";
						out.write(o);
					}
				}
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Couldn't write to file");
		}
	}
	
	/// prints all cumulative utilities by time to file
	public static void writeCumultiveUtilitiesToFile(Map<Double, Double> timesFis,
			Map<Double, Double> timesLP, Map<Double, Double> timesGreedy,
			Map<Double, Double> timesSA, TreeMap<Double, Double> timesGennigs, int run) {
		
		try {			
			BufferedWriter out = openFile("outputCUs.csv");
			for (Iterator<java.util.Map.Entry<Double, Double>> iterator = timesFis
					.entrySet().iterator(); iterator.hasNext();) {
				java.util.Map.Entry<Double, Double> t = iterator.next();
				String o = "Time," + t.getKey() + "," + t.getValue() / run
						+ "," + timesSA.get(t.getKey()) / run + ","
						+ timesLP.get(t.getKey()) / run + ","
						+ timesGreedy.get(t.getKey()) / run+ ","+ timesGennigs.get(t.getKey()) / run;
				out.write(o);
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Couldn't write to file");
		}

	}

	//print to file cumulative utility by time
	public static void writeCumultiveUtilitiyToFile(TreeMap<Double, Double> timesFis, int runs, String algorithm, int num) {
		TreeMap<Double, Double> times= new TreeMap<Double, Double>();
		double utility=0;
		for (Double d : timesFis.keySet()) {	
			utility=utility+timesFis.get(d);
			times.put(d,utility);
			
		}
		
		try {
			
			BufferedWriter out = openFile2(""+algorithm+"_"+num+"_"+"outputCU.csv");
			for (Iterator<java.util.Map.Entry<Double, Double>> iterator = times
					.entrySet().iterator(); iterator.hasNext();) {
				java.util.Map.Entry<Double, Double> t = iterator.next();
				String o = "Time," + t.getKey() + "," + t.getValue() / runs;
				out.write(o);
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Couldn't write to file");
		}

	}
	/// print total utilities to file
	public static void writeUtilitiesToFile(double fisherSW, double LPSW,
			double greedySW, double simulatedSW, double gennigsSW) {
		try {
			BufferedWriter out = openFile2("outputUtilities.csv");
			String o = "Fisher sw," + fisherSW + ",simulated sw," + simulatedSW
					+ ",LP sw," + LPSW + ",greedy sw," + greedySW+ ",Genningd sw," + gennigsSW;
			out.write(o);
			out.newLine();
			out.close();
		} catch (IOException e) {
			System.err.println("Couldn't write to file");
		}

	}

	public static BufferedWriter openFile(String fileName){
	BufferedWriter out = null;
		try {
			FileWriter s = new FileWriter(fileName, true);
			out = new BufferedWriter(s);
			out.newLine();
			
		}catch (Exception e) {
			System.err.println("Couldn't open the file");
		}
	return out;	
		
	}
	public static BufferedWriter openFile2(String fileName){
		BufferedWriter out = null;
			try {
				FileWriter s = new FileWriter(fileName, false);
				out = new BufferedWriter(s);
				out.newLine();
				
			}catch (Exception e) {
				System.err.println("Couldn't open the file");
			}
		return out;	
			
		}
//writes allocation to file
	public static void writeAlocationToFile(Vector<Assignment>[] allocation) {
		try {
			BufferedWriter out = openFile("outputUtilities.csv");
			String o="";
			for (int i = 0; i < allocation.length; i++) {
				for (Assignment a : allocation[i]) {
					o=o+i+","+a.getTask().getId()+","+a.getRatio()+",";
				}
			}
			o=o+"end";
			out.write(o);
			out.close();
		} catch (IOException e) {
			System.err.println("Couldn't write to file");
		}
		
	}
	
	public static void writeSmallProblemToFile(Entry<Double,Double> en, Entry<Double, Double> en1, double i) {
		try {
			BufferedWriter out = openFile("outputSmall.csv");
			String o=""+i+","+en.getKey()+","+en.getValue()+","+en1.getKey()+","+en1.getValue()+",";

			out.write(o);
			out.close();
		} catch (IOException e) {
			System.err.println("Couldn't write to file");
		}
		
	
	
	}	
}
