package fisher;

import TaskAllocation.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;


public class Main {
	private static double pow = 0.5;
	public static void main(String[] args) throws IOException {
		int numOfGoods = 10;
		int numOfBuyers = 6;
		Random random = new Random(1);
		Double[][] input = new Double[numOfBuyers][numOfGoods];
		FileWriter s = new FileWriter("output_iterations.csv", false);
		BufferedWriter out = new BufferedWriter(s);
		out.newLine();

		System.out.println("input  rij");
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].length; j++) {
				double ran = random.nextDouble();
				if (ran > 0.0) {
					input[i][j] = 50+ran*50;
				} else {
					input[i][j] = (double) 0;
				}
			}
		}

		for (Double[] doubles : input) {
			for (Double aDouble : doubles) {
				System.out.print(aDouble + "\t");
			}
			System.out.println("");
		}

		System.out.println("");
		System.out.println("");
		Utility[][] input3 = new LinearUtility[numOfBuyers][numOfGoods];
		Utility[][] input4 = new LinearUtility[numOfBuyers][numOfGoods];
		Utility[][] input5 = new LinearUtility[numOfBuyers-1][numOfGoods];
		for (int i = 0; i < input.length; i++) {

			for (int j = 0; j < input[i].length; j++) {
				
				if (input[i][j] > 0) {
					input3[i][j] = new LinearUtility(input[i][j]);
					input4[i][j] = new LinearUtility(input[i][j]);
				}
				if (i==0) {
					input5[i][j] = new LinearUtility(input[i][j]+input[i+1][j]);
				}
				if (i>=2){
					input5[i-1][j] = new LinearUtility(input[i][j]);
				}
			}
		}
		runAlgorithm(input3, 1, out);
		runAlgorithm2(input3, 1, out);
		for (int j = 0; j < input[0].length; j++) {

			if (input[0][j] > 0) {
				input4[0][j] = new LinearUtility(2*input[0][j]);
			}
		}

		runAlgorithm(input4, 1, out);
		runAlgorithm2(input4, 1, out);
		runAlgorithm2(input5,1,out);


		out.close();

	}

	private static void runAlgorithm2(Utility[][] input, double p, BufferedWriter out) throws IOException {
		double [] money = new double[input.length];
		for (int i = 0; i < money.length; i++) {
			money[i] = 1;
		}
		money[0] = 2;
		FisherDistributedHetro f3 = new FisherDistributedHetro(input, money);
		f3.algorithm();
		printResults(f3, p, input, out);


	}

	private static void runAlgorithm(Utility[][] input, double p, BufferedWriter out) throws IOException {
		FisherDistributed2 f3 = new FisherDistributed2(input);
		f3.algorithm();
		printResults(f3, p, input, out);


	}

	private static void printResults(FisherDistributed2 f3, double p, Utility[][] input, BufferedWriter out) throws IOException {
		System.out.println("Allocation  xij");
		for (int i = 0; i < f3.getAllocations().length; i++) {
			for (int j = 0; j < f3.getAllocations()[i].length; j++) {
				System.out.print(f3.getAllocations()[i][j] + "\t");
			}
			System.out.println("");
		}

		System.out.println("prices  xij");
		String ouput = ""+p+"," + +f3.getNumberOfIteration();
		for (int i = 0; i < f3.getPrices().length; i++) {
			BigDecimal bd = BigDecimal.valueOf(f3.getPrices()[i]);
			bd = bd.setScale(3, RoundingMode.HALF_UP);
			ouput = ouput +","+ bd.doubleValue();
			System.out.println("");
		}
		double utility = getUtility(f3.getAllocations(),input);
		ouput += ","+utility;
		out.write(ouput);
		out.newLine();


		printEF(f3.getAllocations(),input);

		/*System.out.println("Bids  bij");
		for (int i = 0; i < f3.getBids().length; i++) {
			for (int j = 0; j < f3.getBids()[i].length; j++) {
				System.out.print(f3.getBids()[i][j] + "\t");
			}
			System.out.println("");
		}*/


	}

	private static double getUtility(Double[][] output, Utility[][] input) {
		double u = 0;
		for (int i = 0; i < output.length; i++) {

			for (int j = 0; j < output[i].length; j++) {
				if (output[i][j] != null) {
					u = u + input[i][j].getUtility(output[i][j]);
				}
			}
		}
		return u;
	}

	private static void printEF(Double[][] output, Utility[][] input3) {
		for (int k = 0; k < input3.length; k++) {
			for (int i = 0; i < output.length; i++) {
				double u = 0;
				for (int j = 0; j < output[i].length; j++) {
						if(output[i][j]!=null){
							u = u + input3[k][j].getUtility(output[i][j]);
						}
				}
				//u= Math.pow(u,1.0/pow);
				if (i == k) {
					System.out.println("my utility " + u);
				} else {
					System.out.println("others utility " + u);
				}
			}
			System.out.println("");
			System.out.println("");

		}

	}
}
