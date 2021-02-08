package CttdSolver;

import TaskAllocation.Message;
import TaskAllocation.Messageable;

public class RelevanceMessage  extends Message {
    public RelevanceMessage(Messageable sender, int decisionCounter, Messageable reciever, double context, int delay) {
        super(sender, decisionCounter, reciever, context, delay);
    }
}
