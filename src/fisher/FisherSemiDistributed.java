package fisher;

import java.util.Vector;

import FordFulkerson.*;
import TaskAllocation.Utility;

public class FisherSemiDistributed extends FisherDistributed {
	private FlowNetwork division;

	public FisherSemiDistributed(Utility[][] utilities) {
		super(utilities);
		division = new FlowNetwork(nofAgents + nofGoods + 2);
		// TODO Auto-generated constructor stub
	}

	public Double[][] algorithm() {
		iterate();
		while (!isStable()) {
			iterate();
		}

		return createFinalAllocation();
	}

	// returns final allocation according to bang per buck
	private Double[][] createFinalAllocation() {
		createsFlowNet();
		build();
		createOutput();
		return currentAllocation;
	}

	// creates FlowNetwork for max flow
	private void createsFlowNet() {
		Vector<Integer> places = new Vector<Integer>();
		for (int i = 0; i < nofAgents; i++) {
			Double max = new Double(0);
			for (int j = 0; j < nofGoods; j++) {
				double temp=0; 
				if(valuations[i][j]!=null){
					temp= valuations[i][j].getUtility(1) / prices[j];
				}
				if (Math.abs(temp - max) < THRESHOLD) {
					places.add(j);
				} else if (temp > max) {
					max = valuations[i][j].getUtility(1) / prices[j];
					places.clear();
					places.add(j);

				}
			}
			build(i, places);
		}

	}

	// create edges from buyer i to all item that he prefers
	private void build(int i, Vector<Integer> places) {
		for (int j = 0; j < places.size(); j++) {
			division.addEdge(new FlowEdge(places.elementAt(j) + 1, i + 1+ nofGoods,
					 new Double(10000 * nofGoods)));
		}

	}

	// create edges from start to all goods, and edges from all buyers to target
	private void build() {
		double sum=0;
		for (int i = 0; i < nofGoods; i++) {
			division.addEdge(new FlowEdge(0, i + 1, prices[i]));
			sum+= prices[i];
		}

		double money=sum/nofAgents;
		for (int i = 0; i < nofAgents; i++) {
			division.addEdge(new FlowEdge(i + nofGoods + 1, nofGoods
					+ nofAgents + 1, new Double(money)));
		}
	}

	// runs Max Flow and creates final allocation
	private void createOutput() {
		 	currentAllocation=new Double[nofAgents][nofGoods];
	        FordFulkerson ford = new FordFulkerson(division, 0, nofGoods + nofAgents + 1);

	        division.deleteEdges(0);
	        division.deleteEdges(nofGoods + nofAgents + 1);
	        for (int i = nofGoods + 1; i <= nofAgents + nofGoods; i++) {
	            for (FlowEdge e : division.adj(i)) {
	                currentAllocation[i - nofGoods - 1][e.from() - 1] = e.flow()/prices[e.from() - 1];
	            }
	        }
	        //outpuToFile(output);
	    }

}
