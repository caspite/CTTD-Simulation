package Helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class RandomInput {

	// create random input with normal distribution with best agent for each
	// good
	public static Double[][] inputNormalBestAgentBestGood(int a, int b) {

		Double[][] input = new Double[b][a];
		for (int i = 0; i < a; i++) {
			for (int j = 0; j < b; j++) {
				double e = 2 + 4 * ((i + j) % b);
				input[j][i] = Distributions.normalDistribution(e, 1);
				System.out.print(input[j][i] + " ");
			}
			System.out.println("");
		}
		return input;
	}

	// create random input with normal distribution with best agent for each
	// good
	public static Double[][] inputNormalBestAgent(int a, int b) {
		Vector<Integer> rand = new Vector<Integer>();
		Double[][] input = new Double[b][a];
		for (int i = 0; i < a; i++) {
			for (int t = 0; t < b; t++) {
				rand.add(5 + 5 * t);
			}
			for (int j = 0; j < b; j++) {
				double e = rand.remove((int) (rand.size() * Math.random()));
				input[j][i] = Distributions.normalDistribution(e, 1);
				System.out.print(input[j][i] + " ");
			}
			System.out.println("");
		}
		return input;
	}

	// create random input with normal distribution with best good for each
	// agent
	public static Double[][] inputNormalBestGood(int a, int b) {
		Vector<Integer> rand = new Vector<Integer>();
		Double[][] input = new Double[b][a];
		for (int i = 0; i < b; i++) {
			for (int t = 0; t < a; t++) {
				rand.add(5 + 5 * t);
			}
			for (int j = 0; j < a; j++) {
				double e = rand.remove((int) (rand.size() * Math.random()));
				input[i][j] = Distributions.normalDistribution(e, 1);
				System.out.print(input[i][j] + " ");
			}
			System.out.println("");
		}
		return input;
	}

	// create random input with normal distribution with increasing mean for
	// agent
	public static Double[][] inputNormalColumns(int a, int b) {

		Double[][] input = new Double[b][a];
		for (int i = 0; i < a; i++) {
			double e = 2 + i;
			for (int j = 0; j < b; j++) {
				input[j][i] = Distributions.normalDistribution(e, 1);
				System.out.print(input[j][i] + " ");
			}
			System.out.println("");
		}
		return input;
	}

	public static Double[][] inputNormalColumns2(int a, int b) {

		Double[][] input = new Double[b][a];
		for (int i = 0; i < a; i++) {
			double e = 5 + 20 * (i % 4);
			for (int j = 0; j < b; j++) {
				input[j][i] = Distributions.normalDistribution(e, 2);
				System.out.print(input[j][i] + " ");
			}
			System.out.println("");

		}
		return input;
	}

	public static Double[][] inputNormalColumns3(int a, int b) {

		Double[][] input = new Double[b][a];
		for (int i = 0; i < a; i++) {
			double e = 7 + Math.pow(10, (i / 4));
			for (int j = 0; j < b; j++) {
				input[j][i] = Distributions.normalDistribution(e, e / 10);
				System.out.print(input[j][i] + " ");
			}
			System.out.println("");

		}
		return input;
	}

	// create random input with normal distribution with increasing mean for
	// agent
	public static Double[][] inputNormalRows(int a, int b) {

		Double[][] input = new Double[b][a];
		for (int i = 0; i < b; i++) {
			double e = 2 + i;
			for (int j = 0; j < a; j++) {
				input[i][j] = Distributions.normalDistribution(e, 1);
				System.out.print(input[i][j] + " ");
			}
			System.out.println("");
		}
		return input;
	}

	// create random input with normal distribution
	public static Double[][] inputNormal(int a, int b, double e, double stdv) {
		Double[][] input = new Double[b][a];
		for (int i = 0; i < b; i++) {
			for (int j = 0; j < a; j++) {
				input[i][j] = Distributions.normalDistribution(e, stdv);
				System.out.print(input[i][j] + " ");
			}
			System.out.println("");
		}
		return input;
	}

	// create random input with uniform distribution
	public static Double[][] inputUniform(int a, int b) {
		Double[][] input = new Double[b][a];
		for (int i = 0; i < b; i++) {
			for (int j = 0; j < a; j++) {
				double temp = (Math.random() * 20.0);
				input[i][j] = new Double(temp);
				System.out.print(input[i][j] + " ");
			}
			System.out.println("");
		}
		return input;
	}

	// create random input with uniform distribution + i
	public static Double[][] inputUniform1(int a, int b) {
		Double[][] input = new Double[b][a];
		for (int i = 0; i < b; i++) {
			for (int j = 0; j < a; j++) {
				double temp = i + Math.random() * 30.0;
				input[i][j] = new Double(temp);
				System.out.print(input[i][j] + " ");
			}
			System.out.println("");
		}
		return input;
	}

	// get input from a file
	public static Double[][] input() {
		Double[][] input = new Double[10][10];
		System.out.println("Please enter file name");
		Scanner sc = new Scanner(System.in);
		String fileName = sc.next();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(fileName + ".txt"));
			int B = Integer.parseInt(in.readLine());
			int A = Integer.parseInt(in.readLine());
			input = new Double[A][B];
			for (int i = 0; i < A; i++) {
				Scanner line = new Scanner(in.readLine());
				for (int j = 0; j < B; j++) {
					input[i][j] = new Double(line.nextDouble());
				}
			}
		} catch (IOException e) {
			System.out.println("Couldn't read file");

		}
		return input;
	}
// check input
	public static boolean checkInput(Double[][] input) {
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[0].length; j++) {
				if (input[i][j] > 0) {
					break;
				}
				if (j + 1 == input.length) {
					System.out
							.println("Wrong input. There is a buyer that don't want any item");
					return false;
				}
			}
		}
		for (int i = 0; i < input[0].length; i++) {
			for (int j = 0; j < input.length; j++) {
				if (input[j][i] > 0) {
					break;
				}
				if (j + 1 == input[0].length) {
					System.out
							.println("Wrong input. There is a item without demand");
					return false;
				}
			}
		}
		return true;
	}
}
