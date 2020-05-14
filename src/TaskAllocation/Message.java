package TaskAllocation;

public class Message implements Comparable<Message> {
	private Messageable sender; 
	private int decisionCounter; 
	private Messageable reciever; 
	private double context; 
	private int delay;
	
	public Message(Messageable sender, int decisionCounter, Messageable reciever, double context, int delay) {
		this.sender=sender; 
		this.decisionCounter=decisionCounter; 
		this.reciever=reciever; 
		this.context=context; 
		this.delay=delay;
	}

	public int getDelay() {
		return this.delay;
	}
	
	@Override
	public int compareTo(Message o) {
		return this.delay - o.delay;
	}

	public void setDelay(int input) {
		this.delay = input;
	}

	public Messageable getReciever() {
		// TODO Auto-generated method stub
		return this.reciever;
	}

	public double getContext() {
		// TODO Auto-generated method stub
		return this.context;
	}

	public Messageable getSender() {
		// TODO Auto-generated method stub
		return this.sender;
	}

	public int getDecisionCounter() {
		// TODO Auto-generated method stub
		return this.decisionCounter;
	}

	

}
