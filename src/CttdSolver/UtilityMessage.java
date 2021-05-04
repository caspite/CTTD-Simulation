package CttdSolver;

import CTTD.Skill;
import DCOP.Message;

import java.util.Vector;

public class UtilityMessage extends Message {

    double ratio;
    Vector<Skill> execution;
    Utility utility;


    //*** constructor ***//
    public UtilityMessage(int senderId, int receiverId,double ratio,Utility utility) {
        super(senderId, receiverId);
        this.ratio=ratio;
        this.utility=utility;
    }
    public UtilityMessage(int senderId, int receiverId,double ratio) {
        super(senderId, receiverId);
        this.ratio=ratio;
        this.utility=utility;
    }


    public UtilityMessage(int senderId, int receiverId,double ratio, Vector<Skill> execution,Utility utility) {
        super(senderId, receiverId);
        this.ratio=ratio;
        this.execution=execution;
        this.utility=utility;

    }
    public UtilityMessage(int senderId, int receiverId,double ratio, Vector<Skill> execution) {
        super(senderId, receiverId);
        this.ratio=ratio;
        this.execution=execution;
        this.utility=utility;

    }


    //*** getters && setters ***//

    public double getRatio() {
        return ratio;
    }

    public double getUtility(){
        return this.utility.getUtility();
    }

    public Vector<Skill> getExecution() {
        return execution;
    }
}
