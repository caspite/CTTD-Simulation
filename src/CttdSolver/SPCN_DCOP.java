package CttdSolver;

import CTTD.DisasterSite;
import CTTD.MedicalUnit;
import TaskAllocation.Agent;
import TaskAllocation.Assignment;
import DCOP.*;

import java.util.Vector;

public class SPCN_DCOP extends Solver{

    Vector<MedicalUnit> medicalUnits;
    Vector<DisasterSite> disasterSites;

    //*** Create Constrain Graph Methods ***//

    @Override
    protected void createConstraintGraph() {
        //for each agent check if each task is relevant
        for(MedicalUnit medicalUnit:medicalUnits){
            for( DisasterSite disasterSite: disasterSites){
                medicalUnit.sendFirstMassage(disasterSite);

            }
        }
    }








    @Override
    protected Vector<Assignment> solve() {
        return null;
    }
}
