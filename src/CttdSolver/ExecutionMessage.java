package CttdSolver;

import TaskAllocation.Messageable;
import DCOP.*;

public class ExecutionMessage extends Message {


    public ExecutionMessage(int senderId, int receiverId) {
        super(senderId, receiverId);
    }
}
