package fisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import FordFulkerson.FlowNetwork;
import PoliceTaskAllocation.PoliceUnit;
import TaskAllocation.*;

public class FisherDistributedCA {
	protected static final double THRESHOLD = 1E-4;
	protected Utility[][] valuations;// utilities of buyers over the goods
	protected Double[][] currentAllocation;// current allocation of the goods
	protected double[][] bids;// buyers bids over the goods
	protected double[] prices;// prices of the goods in the market

	protected int nofAgents;
	protected int nofGoods;
	// protected double change;
	protected int numberOfIteration;
	protected double changes;
	protected Mailer mailer;
	protected Vector<Task> events;
	protected Vector<PoliceUnit> policeUnits;

	public FisherDistributedCA(Vector<PoliceUnit> agents, Vector<Task> tasks, double Tnow, Mailer mailer) {
		this.numberOfIteration = 0;
		this.mailer = mailer;
		changes = Double.MAX_VALUE;
		this.events = tasks;
		this.policeUnits = agents;

		for (Task t : events) {
			t.initFisherCA(this.mailer);
		}
		for (PoliceUnit a : policeUnits) {
			// each agent creates its initial utility per task and when finishes decides on
			// the bids
			a.createUtiliesBidsAndSendBids(tasks, this.mailer, Tnow);
		}
	}

	// algorithm
	public Double[][] algorithm() {
		do {
			iterate();
			numberOfIteration++;
		} while (!isStable());
		return createCentralisticAllocation();
		/*
		 * while (!isStable()) { iterate(); numberOfIteration++; }
		 */

	}

	private Double[][] createCentralisticAllocation() {
		Double[][] ans = new Double[this.policeUnits.size()][events.size()];

		for (int j = 0; j < events.size(); j++) {
			Task t = events.get(j);
			Map<PoliceUnit, Double> jAllocation = t.getAllocation();
			for (Entry<PoliceUnit, Double> e : jAllocation.entrySet()) {
				PoliceUnit p = e.getKey();
				int i = findI(p);
				double allocation = e.getValue();
				ans[i][j] = allocation;
			}
		}

		return ans;
	}

	private int findI(PoliceUnit p) {
		int ans = -1;
		for (int i = 0; i < policeUnits.size(); i++) {
			if (p.equals(policeUnits.get(i))) {
				ans = i;
			}
		}
		return ans;
	}

	public Double[][] iterate() {
		List<Message> msgToSend = mailer.handleDelay();
		Map<Messageable, List<Message>> receiversMap = createReciversMap(msgToSend);
		sendMessages(receiversMap);
		updateStability();
		return createCentralisticAllocation();
	}

	private void updateStability() {
		this.changes = 0;
		for (Task t : events) {
			this.changes = this.changes + t.getTaskChanges();
			;
		}
	}

	private void sendMessages(Map<Messageable, List<Message>> receiversMap) {
		for (Entry<Messageable, List<Message>> e : receiversMap.entrySet()) {
			Messageable reciever = e.getKey();
			List<Message> msgsRecieved = e.getValue();
			reciever.recieveMessage(msgsRecieved);
		}
	}

	private Map<Messageable, List<Message>> createReciversMap(List<Message> msgToSend) {
		Map<Messageable, List<Message>> ans = new HashMap<Messageable, List<Message>>();
		for (Message m : msgToSend) {
			Messageable reciever = m.getReciever();
			if (!ans.containsKey(reciever)) {
				List<Message> l = new ArrayList<Message>();
				ans.put(reciever, l);
			}
			ans.get(reciever).add(m);
		}
		return ans;
	}

	// next iteration: calculates prices, calculates current valuation and
	// update the bids

	/*
	 * public Double[][] iterate() { updateBidsUsingValutations();
	 * updatePriceVectorUsingBids(); updateCurrentChanges();
	 * updateCurrentAllocationMatrix();
	 * 
	 * //updateCurrentAllocationMatrixAndChanges(); //generateAllocations(); return
	 * currentAllocation; }
	 */

	public boolean isStable() {
		return this.changes < THRESHOLD;
	}

	// -----------METHODS OF iterate------

	// -----------METHODS OF updateCurrentAllocationMatrixAndChanges------

	// -----------Getters and Object methods------

	public Double[][] getAllocations() {
		return currentAllocation;
	}

}
