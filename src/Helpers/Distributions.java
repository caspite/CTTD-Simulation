package Helpers;

import java.util.Random;

public class Distributions {
	// uniform distribution
	public static double uniform(double a, double b) {
		double rand = Math.random();
		double x = (a + rand * (b - a));
		return x;
	}

	// normal Distribution
	public static Double normalDistribution(double mean, double stdv) {
		Double randNorDist = new Double(0.0);
		if (stdv <= 0) {

			throw new IllegalArgumentException("Gaussian std dev must be > 0");
		}

		Random rand = new Random();

		randNorDist = stdv * rand.nextGaussian() + mean;
		return randNorDist;

	}

}
