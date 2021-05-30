package CTTD;

import PoliceTaskAllocation.AgentType;
import org.w3c.dom.ls.LSOutput;

import java.util.Vector;

public class Capacity {
   private Vector <Skill> skills;
   private double maxScore;//the maximum score that agent can achieve
   private double currentScore;


   //-----------constructor----------------------//

    public Capacity(AgentType type){
        skills=new Vector<>();
        this.initializeCapacity(type);
        currentScore=maxScore;

    }
    public Capacity(Vector<Skill> skills){
        this.skills=skills;
    }
    public Capacity(Vector<Skill> skills,double maxScore){
        Vector<Skill> newSkills=new Vector<>();
        for(Skill s:skills){
            newSkills.add(new Skill(s.getTriage(),s.getActivity(),s.getDuration(),s.getScore()));
        }
        this.skills=newSkills;
        this.maxScore=maxScore;
        this.currentScore=maxScore;
    }

    public void initializeCapacity(AgentType type){
        int urgent=0;
        int medium=0;
        int nonUrgent=0;
        Activity[] act=new Activity[4];

        switch (type){
            case TYPE1:
                //each urgent = 2 medium, 1 medium=1.5 nu
                urgent=2;
                medium=4;
                nonUrgent=6;
                act=new Activity[]{Activity.INFO,Activity.TREATMENT,Activity.UPLOADING,Activity.TRANSPORT};
                maxScore=180;
                break;

            case TYPE2:
                urgent=0;
                medium=2;
                nonUrgent=3;
                act=new Activity[]{Activity.INFO,Activity.TREATMENT,Activity.UPLOADING,Activity.TRANSPORT};
                maxScore=90;
                break;

            case TYPE3:
                urgent=1;
                medium=2;
                nonUrgent=3;
                act=new Activity[]{Activity.INFO,Activity.TREATMENT,Activity.UPLOADING,Activity.TRANSPORT};
                maxScore=90;
                break;

            case TYPE4:
                urgent=0;
                medium=0;
                nonUrgent=2;
                act=new Activity[]{Activity.INFO,Activity.TREATMENT};
                maxScore=60;
                break;

            case TYPE5:
                urgent=0;
                medium=2;
                nonUrgent=3;
                act=new  Activity[]{Activity.INFO,Activity.TREATMENT};
                maxScore=60;
                break;

        }
        updateCapacity(urgent,Triage.URGENT,act);
        updateCapacity(medium,Triage.MEDIUM,act);
        updateCapacity(nonUrgent,Triage.NONURGENT,act);
        currentScore=maxScore;

        System.out.println("agent reload capacity: "+this.toString());
    }
    public void reduceCap(Triage trg,Activity act){

        //fined the first skill and remove from capacity
        //reduce current score
        for(Skill s:skills){
            if (s.getActivity()==act&& s.getTriage()==trg){
                currentScore=currentScore-s.getScore();

                skills.removeElement(s);
                break;
            }
        }

    }
    private void updateCapacity(int cap,Triage trg,Activity[] act){
        if(cap>0){
            for (int i=0;i<act.length;i++){
                if(act[i]!= null) {
                    Skill skill = new Skill(trg, act[i]);
                    removeSkill(skill);

                    for (int j = 0; j < cap; j++) {
                        skills.add(skill);
                    }
                }
            }
         }
    }

    private void removeSkill(Skill skill){
        //search skill and remove
        for(int i=0;i<skills.size();i++){
            Skill s=skills.get(i);
            if (s.equals(skill)){
                skills.removeElement(s);
            }
        }
    }

    public double getduration(Triage trg,Activity act){
        double duration=0;
        for(Skill s:skills){
            if (s.getActivity()==act&& s.getTriage()==trg){
                return s.getDuration();
            }
        }
    return 0;
    }

    //----------------------getters and setters-----------------------------//
    public Vector<Skill> getSkills(){
        return skills;
    }

    public double getCurrentScore(){
        return currentScore;
    }
    public String toString(){
        return "Free capacity: "+ (currentScore/maxScore)*100;

    }


}
