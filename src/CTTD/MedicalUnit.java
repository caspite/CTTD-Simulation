package CTTD;

import CttdSolver.FirstMessage;
import DCOP.AgentMessageBox;
import DCOP.Mailer;
import DCOP.Message;
import PoliceTaskAllocation.AgentType;
import TaskAllocation.*;


import java.util.*;

public class MedicalUnit extends Agent implements Messageable{

    //***Medical Unit variables***//
    Capacity skills;
    AgentType agentType;
    boolean isFull;
    Vector<Casualty>casualties; // the casualties that uploaded to the agent
    private int decisionCounter;


    //***Messages Variables***//
    AgentMessageBox agentMessageBox;
    ArrayList<Message> messagesToBeSent;
    Mailer mailer;

    //*** execution variables***//
    private TreeMap<Double, Skill> currentTaskSchedule;// the current task schedule-upcoming
    protected Vector<Task> relevantTasks;//the current relevant tasks


//---------------------------------------------Methods--------------------------------------------



    //***constructor***//
    public MedicalUnit(Location location, int id, HashSet<AgentType> agentType) {
        super(location, id, agentType);
    }

    public MedicalUnit(int id, AgentType agentType, Location loc) {
        super();
       this.id=id;
       this.agentType=agentType;
       this.location=loc;
       skills=new Capacity(agentType);
       isFull=false;
       casualties=new Vector<>();
       currentTaskSchedule=new TreeMap<>();
    }






    //*** skills methods ****//
    public void reduceCapacity(Skill skill){
        Activity act = skill.getActivity();
        Triage trg =skill.getTriage();

        skills.reduceCap(trg,act);
        if(skills.getCurrentScore()<=0){
            setIsFull(true);
        }

    }
    public void reloadCapacity(){
        this.skills.initializeCapacity(agentType);
    }
    public void updateCapacity(double time){
        //check which skills done reduce the capacity and remove from upcoming.

        // Get a set of the entries
        Set set = currentTaskSchedule.entrySet();

        // Get an iterator
        Iterator it = set.iterator();

        // Display elements
        while(it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();
            if((double)me.getKey()<time)
                continue;
            else if((double)me.getKey()<time){
                reduceCapacity((Skill)me.getValue());
                currentTaskSchedule.remove(me.getKey());
            }
            System.out.print("Key is: "+me.getKey() + " & ");
            System.out.println("Value is: "+me.getValue());
        }
    }


    //*** main Methods ****//



    //*** getters & setters ***//

    //Travel duration depends on the agent's type
    @Override
    public void setMovingTime(double dis) {
        Probabilities.getTravelTime(dis,agentType);
        }

    public double getTravelTime(double dis) {
        return Probabilities.getTravelTime(dis,agentType);

    }

    public AgentType getOneAgentType(){return  this.agentType;}



    public Capacity getAgentSkills(){
        return skills;
    }

    //update activities schedule
    private void setCurrentTaskSchedule(double time,Skill skill){
        currentTaskSchedule.put(time,skill);
    }



    public double getActivityTime(Triage trg, Activity act){

    return skills.getduration(trg,act);
    }

    private void setIsFull(boolean b){
        isFull=b;
    }

    public void setCasualties(Casualty cas){
        casualties.add(cas);
    }

    public void reduceCasualties(Casualty cas){
        casualties.removeElement(cas);
    }
    public Vector<Casualty> getCasualties(){
        return casualties;
    }

    //agent lowering casualties on arrival to hospital
    public void loweringCasualties(Hospital hospital,double tnow){
        //update casualties finite survival
        for(Casualty cas:casualties){
            cas.setFiniteSurvival(tnow);
        }
        //add casualties to hospital
        hospital.addCasualties(casualties);
        //delete all casualties
        casualties.clear();
    }

    //*** Messages Methods ***//
    public void sendFirstMassage(DisasterSite disasterSite){
        double timeArrival =Distance.travelTime(this,disasterSite);
        Message newMessage = new FirstMessage(this.id,disasterSite.getId(),timeArrival,skills);
        messagesToBeSent.add(newMessage);
        putMessagesInMailerMailBox();

        //for a-synchrony algorithm
        mailer.
    }
    public AgentMessageBox getAgentMessageBox(){return null;}
    protected void putMessagesInMailerMailBox() {

        mailer.collectMailFromAgent(this, messagesToBeSent);

    }




    public String toString(){
        return "\nMedical unit: "+this.id+" type : "+this.agentType+" "+this.skills;

 }


    @Override
    public void recieveMessage(List<TaskAllocation.Message> msgs) {

    }

    @Override
    public void createMessage(Messageable reciver, double context) {

    }
}

