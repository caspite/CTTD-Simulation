package CttdSolver;

import CTTD.Skill;
import DCOP.Message;

import java.util.Vector;

public class UtilityMessage extends Message {

    double utility;
    Vector<Skill> execution;


    //*** constructor ***//
    public UtilityMessage(int senderId, int receiverId,double utility) {
        super(senderId, receiverId);
        this.utility=utility;
    }

    public UtilityMessage(int senderId, int receiverId,double utility, Vector<Skill> execution) {
        super(senderId, receiverId);
        this.utility=utility;
        this.execution=execution;

    }
}
