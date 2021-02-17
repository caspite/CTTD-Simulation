package DCOP;

import java.util.ArrayList;
import java.util.Vector;

import TaskAllocation.Agent;
import TaskAllocation.Task;


import java.util.HashMap;

public class Mailer {

	int mailerId;
	HashMap<Agent,ArrayList<Message>> agentsMessage; //The mailer know all the agents by this map.
	Vector<Agent> agents;
	Vector<Task> tasks;
	HashMap<Task,ArrayList<Message>> tasksMessage; //The mailer know all the tasks by this map.

	///// ******* Constructor ******* ////

	public Mailer(Vector<Agent> agents,Vector<Task> tasks) {

		this.mailerId = 1;
		agentsMessage = new HashMap<Agent,ArrayList<Message>>();
		tasksMessage = new HashMap<>();
		this.agents = agents;
		this.tasks =tasks;
		meetAllAgents(agents);
		meetAllTasks(tasks);

	}

	///// ******* Main Methods ******* ////

	//OmerP - When initializing the problem the mailer will need to meet all the agent.
	protected void meetAllAgents(Vector<Agent> agents) {

		for(int i = 0 ; i < agents.size() ; i++) {

			Agent agent = (Agent) agents.get(i);
			ArrayList<Message> agentMessage = new ArrayList<Message>();
			agentsMessage.put(agent, agentMessage);

		}


	}

	//OmerP - When initializing the problem the mailer will need to meet all the tasks.
	protected void meetAllTasks(Vector<Task>tasks) {

		for(int i = 0 ; i < tasks.size() ; i++) {

			Task task = tasks.get(i);
			ArrayList<Message> agentMessage = new ArrayList<Message>();
			tasksMessage.put(task, agentMessage);

		}


	}




	//OmerP - To put messages in each agent mail box.
	public void putMessagesInAgentsMailBox() {

		for(Agent senderAgent: agentsMessage.keySet()) {

			ArrayList<Message> messagesToBeSent = agentsMessage.get(senderAgent);

			for(int i = 0 ; i < messagesToBeSent.size() ; i++) {

				Message messageToBeSent = messagesToBeSent.get(i);
				int receiverId = messageToBeSent.getReceiverId();
				Agent agent = agents.get(receiverId);
				MessageBox agentMessageBox = agent.getAgentMessageBox();
				agentMessageBox.receiveMessageFromMailer(messageToBeSent);

			}

		}

	}
	//TC - agent represent the sender
	public void collectMailFromAgent(Agent agent, ArrayList<Message> messages) {

		agentsMessage.put(agent, messages);
	}

	public int getMailerId() {

		return this.mailerId;

	}



}
