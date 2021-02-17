package DCOP;


import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;

public class MessageBox {

    int agentId;
    HashMap<Integer, Message> messages;

    ///// ******* Constructor ******* ////

    public MessageBox(int agentId) {

        this.agentId = agentId;
        this.messages = new HashMap<Integer, Message>();

    }

    ///// ******* Main Methods ******* ////

    public void putMessagesInMessageBox(HashMap<Integer, Message> messages) {

        this.messages = messages;

    }

    protected void receiveMessageFromMailer(Message message) {

        int senderId = message.getSenderId();
        messages.put(senderId, message);

    }

    protected Message getMailFromAnotherAgent(int senderId) {

        Message receivedMessage = messages.get(senderId);
        return receivedMessage;

    }

    ///// ******* Setters ******* ////

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public void setMessages(HashMap<Integer, Message> messages) {
        this.messages = messages;
    }

    ///// ******* Getters ******* ////

    public HashMap<Integer, Message> getMessages() {
        return messages;
    }

    public int getAgentId() {
        return agentId;
    }








}
