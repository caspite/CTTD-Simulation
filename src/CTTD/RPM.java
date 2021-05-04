package CTTD;

import java.util.Random;

public class RPM {
    double survival;
    Triage triage;
    double RpmCareTime;
    double timeToSurvive;
    double uploadingTime;
    int rpm;

    //*** constructor ***//

    public RPM ( double survival,Triage triage,double careTime,double timeToSurvive,int rpm){
        this.RpmCareTime =careTime;
        this.rpm=rpm;
        this.survival=survival;
        this.triage=triage;
        this.uploadingTime=RpmCareTime*0.1;
        calcTriageByRPM(rpm);
        calcTimeToSurvive();

    }

    public RPM ( int rpm,Triage trg){
        this.triage=trg;
        this.rpm=rpm;
        calcTriageByRPM(rpm);
        calcParams();
        calcTimeToSurvive();

    }
    public RPM ( int rpm){
        this.rpm=rpm;
        calcTriageByRPM(rpm);
        calcParams();
        calcTimeToSurvive();
    }

    public RPM (Triage trg){
        this.triage=trg;
        this.rpm=returnRPM(trg);
        calcTriageByRPM(rpm);
        calcParams();
        calcTimeToSurvive();

    }

    //*************************************//

    //*** methods ***//

    private void calcTriageByRPM(int rpm){
        if(rpm>0&&rpm<7){
            this.triage=Triage.URGENT;
        }
        else if(rpm<10){
            this.triage=Triage.MEDIUM;
        }
        else if(rpm>10){
            this.triage=Triage.NONURGENT;
        }

    }

    public void calcParams(){

        this.survival= getSurvival(rpm);
        this.RpmCareTime = getCareTime(rpm);
        this.uploadingTime=RpmCareTime*0.1;

    }
    public void calcTimeToSurvive(){

        for(int i=1;i<deterioration[this.rpm].length+1;i++){
            double finalRPM=this.deterioration[this.rpm][i-1];
            if(finalRPM<=0){
                this.timeToSurvive=i*30;
                return;
            }
            this.timeToSurvive=i*30;
        }


    }
    public int returnRPM(Triage trg){
        //rnd
        Random rnd=new Random();
        double d=rnd.nextDouble();
        int rpm=0;
        switch (trg){
            case URGENT:
                rpm = (int)getRpmByProbability(this.URGENTrpmProbability,d);
                break;
            case MEDIUM:
                rpm = (int)getRpmByProbability(this.MEDUIMrpmProbability,d);
                break;
            case NONURGENT:
                rpm = (int)getRpmByProbability(this.NONURGENTrpmProbability,d);

        }

        return rpm;
    }

    public double calcSurvivalByTimeInterval(double time){
        double survival=0;
        int tempRpm=this.rpm;
        //find in the deterioration the rpm
        for(int i=1;i<deterioration[this.rpm].length+1;i++){
            if(i*30<=time){
               continue;
            }
            else {
                tempRpm=(int)this.deterioration[this.rpm][i-1];
                break;
            }
        }
        survival=survivalProbability[tempRpm];

        return survival;
    }

   public void updateRpmByTime(double time){
       int tempRpm=this.rpm;
       //find in the deterioration the rpm
       for(int i=1;i<deterioration[this.rpm].length+1;i++) {
           if (i * 30 <= time) {
               continue;
           } else {
               this.rpm = (int) this.deterioration[this.rpm][i - 1];
               break;
           }
       }
       calcTriageByRPM(rpm);
           calcParams();
           calcTimeToSurvive();
   }



    private double getRpmByProbability(double[][] array,double d){
        double prop=0;
        for(int i=0;i<array.length;i++){
            if(array[i][1]+prop>d){
                return array[i][0];
            }
            else{
                prop+=array[i][1];
            }
        }
        return array[array.length-1][0];
    }

    private double  getSurvival(int rpm){
      return this.survivalProbability[rpm];
    }
    private double getCareTime(int rpm){
        return this.careTime[rpm];
    }

    public RPM returnRpmByTime(double time){
        int tempRpm=this.rpm;
        //find in the deterioration the rpm
        for(int i=1;i<deterioration[this.rpm].length+1;i++) {
            if (i * 30 <= time) {
                continue;
            } else {
                tempRpm = (int) this.deterioration[this.rpm][i - 1];
                break;
            }
        }
        RPM temp = new RPM(tempRpm);

        return temp;
    }


    //*** getters && setters ***//


    public void setTriage(Triage triage) {
        this.triage = triage;
    }

    public void setSurvival(double survival) {
        this.survival = survival;
    }

    public void setCareTime(double careTime) {
        this.RpmCareTime = careTime;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public Triage getTriage() {
        return triage;
    }

    public double getSurvival() {
        return survival;
    }

    public double getCareTime() {
        return RpmCareTime;
    }

    public double getTimeToSurvive() {
        return timeToSurvive;
    }

    public int getRpm() {
        return rpm;
    }

    public double getUploadingTime(){return uploadingTime;}


    final double[]careTime = {180, 170, 160, 150, 140, 130, 120, 110, 90, 60, 50, 40, 30};
    final double[]survivalProbability = {0.052,0.089,0.15,0.23,0.35,0.49,0.63,0.75,0.84,0.9,0.94,0.97,0.98};
    final double[][] URGENTrpmProbability = {{0, 0.0476}, {1, 0.0714}, {2, 0.0952}, {3, 0.119}, {4, 0.19}, {5, 0.238}, {6, 0.238}};
    final double[][] MEDUIMrpmProbability = {{7, 0.32}, {8, 0.32}, {9, 0.36}};
    final double[][] NONURGENTrpmProbability = {{10, 0.38}, {11, 0.38}, {12, 0.238}};
    final double[][] deterioration = {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {4, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {6, 5, 4, 3, 2, 1, 0, 0, 0, 0, 0, 0},
                {8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 0, 0},
                {9, 8, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0},
                {10, 9, 9, 8, 8, 7, 6, 6, 5, 5, 4, 4},
                {11, 11, 10, 10, 9, 8, 8, 7, 7, 6, 6, 5},
                {12, 12, 11, 11, 10, 10, 10, 10, 9, 9, 8, 8}};

}
