package CTTD;

import Helpers.ReadXLSheet;
import PoliceTaskAllocation.*;
import TaskAllocation.Location;
import TaskAllocation.Task;

import java.util.*;

public class Init {

//TODO update write to excel by sheets
        private Vector<MedicalUnit> medicalUnits;
        private Vector<Casualty> casualties;
        private Vector<Task> activeEvent;
        private Vector<DisasterSite> disasterSites;
         private Vector<Hospital> hospitals;


    //TODO Constraints
        private int[][] constraints;
        private TreeSet<DiaryEvent> diary;
        private int numOfAgents;
        private int maxDisasterSite;

        public Init(int agents, int maxCasPerSite) {
            medicalUnits = new Vector<MedicalUnit>();
            activeEvent = new Vector<Task>();
            diary = new TreeSet<DiaryEvent>();
            this.numOfAgents = agents;
            this.maxDisasterSite = maxCasPerSite;


        }

        // -----------------------------Getters-----------------------------

        public Vector<MedicalUnit> getMedicalUnits() {
            return medicalUnits;
        }

        public Vector<Task> getActiveEvent() {
            return activeEvent;
        }
        public Vector<Casualty> getCasualties() {
            return casualties;
        }


        public TreeSet<DiaryEvent> getDiary() {
            return diary;
        }

        // -----------------------Create-----------------------------------

        public void createDisasterSite(String fileName, String sheetName) {

            ReadXLSheet xlReader = new ReadXLSheet(fileName, sheetName);

            for (int i = 1; i < xlReader.getRowsNum(); i++) {
                //Reset
                int id = 0;
                double lat = 0;
                double lng = 0;
                double startTime = 0;
                String[] s = xlReader.rowReading(i);
                for (int j = 0; j < s.length; j++) {
                    switch (j) {
                        case 0:
                            id = Integer.parseInt(s[j]);
                            break;
                        case 1:
                            lat = Double.parseDouble(s[j]);
                            break;
                        case 2:
                            lng = Double.parseDouble(s[j]);
                            break;
                        case 3:
                            startTime = Double.parseDouble(s[j]);
                            break;
                        default:
                            break;
                    }

                }
                Location loc =new Location(lat,lng);
                disasterSites.add(new DisasterSite(loc,id,startTime));
                createCas (fileName,sheetName,id);

            }
            xlReader.closeXLFile();
        }

        public void createMedicalUnits(String fileName, String sheetName) {
            ReadXLSheet xlReader = new ReadXLSheet(fileName, sheetName);

            for (int i = 1; i < xlReader.getRowsNum(); i++) {
                //Reset
                int id = 0;
                double lat = 0;
                double lng = 0;
                AgentType agt=AgentType.TYPE1;
                String[] s = xlReader.rowReading(i);
                for (int j = 0; j < s.length; j++) {
                    switch (j) {
                        case 0:
                            id = Integer.parseInt(s[j]);
                            break;
                        case 1:
                            lat = Double.parseDouble(s[j]);
                            break;
                        case 2:
                            lng = Double.parseDouble(s[j]);
                            break;
                        case 3:
                            agt =AgentType.valueOf(s[j]);
                            break;
                        default:
                            break;
                    }

                }
                Location loc =new Location(lat,lng);
                medicalUnits.add(new MedicalUnit(id,agt,loc));
            }
            xlReader.closeXLFile();
        }

        public void createHospitals(String fileName, String sheetName) {

        ReadXLSheet xlReader = new ReadXLSheet(fileName, sheetName);

        for (int i = 1; i < xlReader.getRowsNum(); i++) {
            //Reset
            //id	lng	lat	capacity
            int id = 0;
            double lat = 0;
            double lng = 0;
            int capacity = 2000;//default capacity
            String[] s = xlReader.rowReading(i);
            for (int j = 0; j < s.length; j++) {
                switch (j) {
                    case 0:
                        id = Integer.parseInt(s[j]);
                        break;
                    case 1:
                        lat = Double.parseDouble(s[j]);
                        break;

                    case 2:
                        lng = Double.parseDouble(s[j])*-1;
                        break;
                    case 3:
                        capacity = Integer.parseInt(s[j]);
                        break;
                    default:
                        break;
                }

            }
            Location l = new Location(lat, lng);
            hospitals.add(new Hospital(l,id,capacity));
        }
        xlReader.closeXLFile();
    }

        private void createCas (String fileName,String sheetName,int disasterSiteID){

        ReadXLSheet xlReaderC = new ReadXLSheet(fileName, sheetName);
        for (int k = 1; k < xlReaderC.getRowsNum(); k++) {
            //reset
            //id	DS	triage	survival
            int Cid;
            Triage trg;
            double survival;
            double Tborn=0;
            String[] sC = xlReaderC.rowReading(k);
            if(Integer.parseInt(sC[1])==disasterSiteID) {
                trg = Triage.valueOf(sC[4]);
                survival = Double.parseDouble(sC[3]);
                Cid = Integer.parseInt(sC[0]);
                Tborn=Double.parseDouble(sC[2]);
                casualties.add(new Casualty(trg, Casualty.Status.WATING, survival, Cid,disasterSiteID,Tborn));
            }

        }
        xlReaderC.closeXLFile();
    }


}


