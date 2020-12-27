package TaskAllocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import PoliceTaskAllocation.MainSimulationForThreads;
import PoliceTaskAllocation.PoliceUnit;




public class Mailer {
	private int shift;
	private List<Message> messageBox;
	private double p3;
	private double p4;
	private int delayUb;
	
	private Random rP3,rP4,delayUbR;


	public Mailer(int shift, double p3, double p4, int delayUb) {
		this.shift = shift;
		
		this.p3 = p3;
		this.p4 = p4;
		this.delayUb = delayUb;		
		this.rP3 = new Random(shift);
		this.rP4= new Random(shift);
		this.delayUbR = new Random(shift);		
		this.messageBox= new ArrayList<Message>();
	}

	public void createMessage(Messageable sender, int decisionCounter, Messageable reciever, double context) {
		int delay = createDelay();
		Message m = new Message(sender,decisionCounter,reciever, context, delay);
		this.messageBox.add(m);	
	}

	
	
	
	private int createDelay() {
		int rndDelay;
		rndDelay = 0;	
		double rnd = rP3.nextDouble();
		if (rnd < this.p3) {
			rndDelay =getRandomInt(this.delayUbR, 1, this.delayUb);
			rnd = rP4.nextDouble();
			if (rnd < this.p4) {
				rndDelay = Integer.MAX_VALUE;
			}
		}
		return rndDelay;
	}
	
	
	private static int getRandomInt(Random r, int min, int max) {
		return r.nextInt(max - min + 1) + min;
	}

	public List<Message> handleDelay() {
		Collections.sort(this.messageBox);
		List<Message> msgToSend = new ArrayList<Message>();
		Iterator it = this.messageBox.iterator();

		while (it.hasNext()) {
			Message msg = (Message) it.next();
			if (msg.getDelay() == 0) {
				msgToSend.add(msg);
				it.remove();
			} else {
				msg.setDelay(msg.getDelay() - 1);
			}
		}
		return msgToSend;
	}
}
