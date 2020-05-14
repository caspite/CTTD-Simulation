package fisher;

import FordFulkerson.FlowNetwork;
import TaskAllocation.*;

public class FisherDistributed {
	protected static final double THRESHOLD = 1E-4;
	protected Utility[][] valuations;// utilities of buyers over the goods
	protected Double[][] currentAllocation;// current allocation of the goods
	protected double[][] bids;// buyers bids over the goods
	protected double[] prices;// prices of the goods in the market

	protected int nofAgents;
	protected int nofGoods;
	protected double change;

	public FisherDistributed(Utility[][] utilities) { // input R matrix

		initializeFields(utilities);
		initializeValuations(utilities);
		initializeBids(utilities);
		//((ConcaveUtility)utilities[i][j]).getLinearUtility();
		updatePriceVectorUsingBids();
		updateCurrentAllocationMatrixAndChanges();
		
		//generateAllocations();
	}

	
	// algorithm
	public Double[][] algorithm() {
		iterate();
		while (!isStable()) {
			iterate();
		}

		return currentAllocation;
	}

	



	// next iteration: calculates prices, calculates current valuation and
	// update the bids
	public Double[][] iterate() {
		updateBidsUsingUtilites();
		updatePriceVectorUsingBids();
		updateCurrentAllocationMatrixAndChanges();
		//generateAllocations();
		return currentAllocation;
	}

	public boolean isStable() {
		return change < THRESHOLD;
	}

	//-----------METHODS OF iterate------

	private void updateBidsUsingUtilites() {
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
				bids[i][j] = utilities[i][j] / utilitySum[i];
			}
		}
		
	}

	private void updateCurrentAllocationMatrixAndChanges() {
		change = 0;
		for (int i = 0; i < nofAgents; i++) {
			for (int j = 0; j < nofGoods; j++) {
				updateSumOfChanges(i,j);
				updateCurrentAllocation(i,j);
				
			}
		}		
	}

	private void updatePriceVectorUsingBids() {
		for (int j = 0; j < nofGoods; j++) {
			prices[j] = 0;
			for (int i = 0; i < nofAgents; i++) {
				prices[j] += bids[i][j];
			}
		}
	}

	//-----------METHODS OF updateCurrentAllocationMatrixAndChanges------

	private void updateCurrentAllocation(int i, int j) {
		if (bids[i][j]/prices[j] > THRESHOLD) {
			currentAllocation[i][j] = bids[i][j] / prices[j];//aaaaa
		} else {
			currentAllocation[i][j] = null;
		}
		
	}

	private void updateSumOfChanges(int i, int j) {
		if (currentAllocation[i][j] != null) {
			change += Math
					.abs(((bids[i][j]/ prices[j]) - currentAllocation[i][j]));///aaaa
		}
	}

	
	
	
	
	//-----------Getters and Object methods------

		

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



		//-----------METHODS OF CONSTRUCTOR------
		
		private void initializeBids(Utility[][] utilities) {
			for (int i = 0; i < nofAgents; i++) {
				for (int j = 0; j < nofGoods; j++) {
					if (utilities[i][j] != null) {
						bids[i][j] = 1*(i+1);
					}
				}
			}
			
		}

		private void initializeValuations(Utility[][] utilities) {
			final double[] valuationSums = new double[nofAgents]; // utilits sum of rows, 
			for (int i = 0; i < nofAgents; i++) {
				for (int j = 0; j < nofGoods; j++) {
					if (utilities[i][j] != null) {
						this.valuations[i][j] = (Utility) utilities[i][j].clone();
						valuationSums[i] += utilities[i][j].getUtility(1);
					}
				}
			}
			
		}

		private void initializeFields(Utility[][] utilities) {
			this.nofGoods = utilities[0].length; // number columns =  number of goods 
			this.nofAgents = utilities.length; // number of rows =  number of agents
			this.currentAllocation = new Double[nofAgents][nofGoods];// X
			this.bids = new double[nofAgents][nofGoods]; // the prices each agents offers per goods
			this.valuations = new Utility[nofAgents][nofGoods]; // ?
			this.prices = new double[nofGoods]; // price vector
		}


		//-----------METHODS OF GENERATE ALLOCATIONS------

		

		
		
		// generates allocation according to current bids and prices
		/*
		private Double[][] generateAllocations() {
			updatePriceVectorUsingBids();
			updateCurrentAllocationMatrixAndChanges();
			return currentAllocation;
		}
		*/
}
