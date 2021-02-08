package CttdSolver;

import TaskAllocation.Message;
import TaskAllocation.Messageable;

public class ServiceProviderMessage extends Message {
    public ServiceProviderMessage(Messageable sender, int decisionCounter, Messageable reciever, double context, int delay) {
        super(sender, decisionCounter, reciever, context, delay);
    }
}
