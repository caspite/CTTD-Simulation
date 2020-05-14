package fisher;


import TaskAllocation.*;

public class FisherDistributed2 {
	protected int numberOfIteration; //number of iteration until the algorithm converges
	protected static final double THRESHOLD = 1E-1;
	protected Utility[][] valuations;// utilities of buyers over the goods
	protected Double[][] currentAllocation;// current allocation of the goods
	protected double[][] bids;// buyers bids over the goods



	protected double[] prices;// prices of the goods in the market

	protected int nofAgents;
	protected int nofGoods;
	protected double change;

	public int getNumberOfIteration() {
		return numberOfIteration;
	}

	public FisherDistributed2(Utility[][] utilities) {

		this.numberOfIteration = 0;
		this.nofGoods = utilities[0].length;
		this.nofAgents = utilities.length;
		currentAllocation = new Double[nofAgents][nofGoods];
		bids = new double[nofAgents][nofGoods];
		this.valuations = new Utility[nofAgents][nofGoods];
		prices = new double[nofGoods];

		final double[] valuationSums = new double[nofAgents];
		for (int i = 0; i < nofAgents; i++) {
			for (int j = 0; j < nofGoods; j++) {
				if (utilities[i][j] != null) {
					this.valuations[i][j] = (Utility) utilities[i][j].clone();
					valuationSums[i] += utilities[i][j].getUtility(1);
				}
			}
		}

		for (int i = 0; i < nofAgents; i++) {
			for (int j = 0; j < nofGoods; j++) {
				if (utilities[i][j] != null) {
					bids[i][j] = utilities[i][j].getUtility(1)/valuationSums[i];
				}
			}
		}
		//((ConcaveUtility)utilities[i][j]).getLinearUtility();
		generateAllocations();
	}

	// algorithm
	public Double[][] algorithm() {
		iterate();
		numberOfIteration ++;
		while (!isStable()) {
			iterate();
			numberOfIteration++;
		}

		return currentAllocation;
	}

	// calculate prices(sum bids) generates allocation according to current bids and prices
	protected Double[][] generateAllocations() {

		for (int j = 0; j < nofGoods; j++) {
			prices[j] = 0;
			for (int i = 0; i < nofAgents; i++) {
				prices[j] += bids[i][j];
			}
		}
		change = 0;
		for (int i = 0; i < nofAgents; i++) {
			for (int j = 0; j < nofGoods; j++) {
				double newAllocation = bids[i][j]/ prices[j];
				if (currentAllocation[i][j] != null) {
					change += Math
							.abs(((newAllocation) - currentAllocation[i][j]));///aaaa
				}
				if (Math.abs(newAllocation) > THRESHOLD) {
					currentAllocation[i][j] = newAllocation;//aaaaa
				} else {
					currentAllocation[i][j] = null;
				}
			}
		}
		return currentAllocation;

	}

	public double[][] getBids() {
		return bids;
	}

	// next iteration: calculates current valuation according to current allocation
	//and update the bids
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

				bids[i][j] = (utilities[i][j] / utilitySum[i]);
			}
		}
		generateAllocations();

		return currentAllocation;
	}

	// checks if the prices are stable
	public boolean isStable() {
		return change < THRESHOLD;
	}

	public double getChange() {
		return change;
	}

	public Double[][] getAllocations() {

		return currentAllocation;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("FisherPrd:").append("\n");
		sb.append("valuations:").append("\n");
		for (int i = 0; i < nofAgents; i++) {
			for (int j = 0; j < nofGoods; j++) {
				sb.append(valuations[i][j] + "\t");
			}
			sb.append("\n");
		}
		sb.append("bids:").append("\n");
		for (int i = 0; i < nofAgents; i++) {
			for (int j = 0; j < nofGoods; j++) {
				sb.append(Math.round(bids[i][j] * 1000.0) / 1000.0 + "\t");
			}
			sb.append("\n");
		}
		sb.append("allocations:").append("\n");
		for (int i = 0; i < nofAgents; i++) {
			for (int j = 0; j < nofGoods; j++) {
				sb.append(Math.round(currentAllocation[i][j] * 1000.0) / 1000.0
						+ "\t");
				// sb.append(currentAllocation[i][j] + "\t");
			}
			sb.append("\n");
		}

		return sb.toString();
	}

	public double[] getPrices() {
		return prices;
	}
}
