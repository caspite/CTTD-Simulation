package DCOP;

import java.util.Vector;

import CTTD.DisasterSite;
import TaskAllocation.Agent;
import TaskAllocation.Task;


import java.util.HashMap;

public class Mailer {

	int mailerId;
	HashMap<Agent,Vector<Message>> agentsMessage; //The mailer know all the agents by this map.
	Vector<Agent> agents;
	Vector<Task> tasks;
	HashMap<Task,Vector<Message>> tasksMessage; //The mailer know all the tasks by this map.

	///// ******* Constructor ******* ////

	public Mailer(Vector<Agent> agents,Vector<Task> tasks) {

		this.mailerId = 1;
		agentsMessage = new HashMap<Agent,Vector<Message>>();
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
			Vector<Message> agentMessage = new Vector<Message>();
			agentsMessage.put(agent, agentMessage);

		}


	}

	//OmerP - When initializing the problem the mailer will need to meet all the tasks.
	protected void meetAllTasks(Vector<Task>tasks) {

		for(int i = 0 ; i < tasks.size() ; i++) {

			Task task = tasks.get(i);
			Vector<Message> agentMessage = new Vector<Message>();
			tasksMessage.put(task, agentMessage);

		}


	}




	//OmerP - To put messages in each agent mail box.
	public void putMessagesInAgentsMailBox() {

		for(Agent senderAgent: agentsMessage.keySet()) {

			Vector<Message> messagesToBeSentAgents = agentsMessage.get(senderAgent);


			for(int i = 0; i < messagesToBeSentAgents.size() ; i++) {

				Message messageToBeSent = messagesToBeSentAgents.get(i);
				int receiverId = messageToBeSent.getReceiverId();
				Task task = tasks.get(receiverId-1);
				MessageBox agentMessageBox = ((DisasterSite)task).getAgentMessageBox();
				agentMessageBox.receiveMessageFromMailer(messageToBeSent);

			}


		}

		for(Task senderAgent: tasksMessage.keySet()) {

			Vector<Message> messagesToBeSentTasks = tasksMessage.get(senderAgent);

			for(int i = 0; i < messagesToBeSentTasks.size() ; i++) {

				Message messageToBeSent = messagesToBeSentTasks.get(i);
				int receiverId = messageToBeSent.getReceiverId();
				Agent agent = agents.get(receiverId);
				MessageBox agentMessageBox = agent.getAgentMessageBox();
				agentMessageBox.receiveMessageFromMailer(messageToBeSent);

			}

		}


	}
	//TC - agent represent the sender
	public void collectMailFromAgent(Agent agent, Vector<Message> messages) {

		agentsMessage.put(agent, messages);
	}


	//TC - agent represent the sender
	public void collectMailFromTask(Task task, Vector<Message> messages) {
		tasksMessage.put(task, messages);
	}

	public int getMailerId() {

		return this.mailerId;

	}



}
