package DCOP;


public class Message {

    double context;
    int senderId;
    int receiverId;

    ///// ******* Constructor ******* ////

    public Message(double context, int senderId, int receiverId) {

        this.context = context;
        this.senderId = senderId;
        this.receiverId = receiverId;

    }

    public Message( int senderId, int receiverId) {

        this.senderId = senderId;
        this.receiverId = receiverId;

    }


    ///// ******* Getters ******* ////

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public double getContext() {
        return context;
    }



    ///// ******* Setters ******* ////

    public void setContext(double context) {
        this.context = context;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

}

