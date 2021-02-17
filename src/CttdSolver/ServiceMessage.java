package CttdSolver;


import CTTD.Capacity;
import DCOP.Message;

public class ServiceMessage extends Message {
    double timeArrival;
    Capacity capacity;

public ServiceMessage(int senderId, int receiverId, double timeArrival, Capacity capacity){
    super(senderId,receiverId);
    this.capacity=capacity;
    this.timeArrival=timeArrival;

}
    //*** getters && setters ***//


    public double getTimeArrival() {
        return timeArrival;
    }

    public Capacity getCapacity() {
        return capacity;
    }

}
