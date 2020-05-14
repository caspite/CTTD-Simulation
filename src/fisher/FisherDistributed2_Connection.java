package fisher;

import TaskAllocation.Utility;

import java.util.Random;
import java.util.RandomAccess;

public class FisherDistributed2_Connection extends FisherDistributed2{
	protected  double p1 ;
	protected Random random;
	public FisherDistributed2_Connection(Utility[][] utilities,double p1) {
		super(utilities);
		this.p1 = p1;
		this.random = new Random(42);

	}

	public Double[][] iterate() {
		double[][] utilities = new double[nofAgents][nofGoods];
		// calculate current utilities and sum the utility for each agent
		final double[] utilitySum = new double[nofAgents];
		for (int i = 0; i < nofAgents; i++) {
			for (int j = 0; j < nofGoods; j++) {
				if (currentAllocation[i][j] != null) {
					utilities[i][j] = valuations[i][j]
							.getUtility(currentAllocation[i][j]);
					utilitySum[i] += valuations[i][j]
							.getUtility(currentAllocation[i][j]);
				}
			}
		}

		for (int i = 0; i < nofAgents; i++) {
			for (int j = 0; j < nofGoods; j++) {
				if (random.nextDouble() < p1) {
					bids[i][j] = utilities[i][j] / utilitySum[i];
				}
			}
		}
		generateAllocations();

		return currentAllocation;
	}
}
