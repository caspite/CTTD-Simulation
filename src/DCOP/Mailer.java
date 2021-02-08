package DCOP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import PoliceTaskAllocation.MainSimulationForThreads;
import PoliceTaskAllocation.PoliceUnit;
import TaskAllocation.Agent;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class Mailer {

	int mailerId;
	HashMap<Agent,ArrayList<Message>> agentsMessage; //The mailer know all the agents by this map.
	Vector<Agent> agents;

	///// ******* Constructor ******* ////

	public Mailer(Vector<Agent> agents) {

		this.mailerId = 1;
		agentsMessage = new HashMap<Agent,ArrayList<Message>>();
		this.agents = agents;
		meetAllAgents(agents);

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

	//OmerP - To put messages in each agent mail box.
	protected void putMessagesInAgentsMailBox() {

		for(Agent senderAgent: agentsMessage.keySet()) {

			ArrayList<Message> messagesToBeSent = agentsMessage.get(senderAgent);

			for(int i = 0 ; i < messagesToBeSent.size() ; i++) {

				Message messageToBeSent = messagesToBeSent.get(i);
				int receiverId = messageToBeSent.getReceiverId();
				Agent agent = agents.get(receiverId);
				AgentMessageBox agentMessageBox = agent.getAgentMessageBox();
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
