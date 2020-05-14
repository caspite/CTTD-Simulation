package fisher;

import TaskAllocation.Utility;

public class FisherDistributedHetro extends FisherDistributed2 {
    protected double moneyOfAgents[];
    public FisherDistributedHetro(Utility[][] utilities, double [] money) {
        super(utilities);
        this.moneyOfAgents = money;

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
                    bids[i][j] = (utilities[i][j].getUtility(1)/valuationSums[i])*moneyOfAgents[i];
                }
            }
        }





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

                bids[i][j] = (utilities[i][j] / utilitySum[i])*moneyOfAgents[i];
            }
        }
        generateAllocations();

        return currentAllocation;
    }


}