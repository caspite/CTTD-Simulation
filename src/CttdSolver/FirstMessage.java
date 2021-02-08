package CttdSolver;

import CTTD.Capacity;
import DCOP.*;
import TaskAllocation.Messageable;

public class FirstMessage extends Message {



    double timeArrival;
    Capacity capacity;
    int ordering;//the number of variable

    //----------------------------methods---------------------------------//

    //*** constructor ***//

    public FirstMessage(int senderId, int receiverId, double timeArrival, Capacity capacity) {
        super(senderId, receiverId);
        this.capacity=capacity;
        this.timeArrival=timeArrival;

    }

    //*** getters &setters***//

    public void setCapacity(Capacity capacity) {
        this.capacity = capacity;
    }

    public Capacity getCapacity() {
        return capacity;
    }

    public double getTimeArrival() {
        return timeArrival;
    }

    public void setTimeArrival(double timeArrival) {
        this.timeArrival = timeArrival;
    }
}
