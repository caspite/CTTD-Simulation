package CttdSolver;

import TaskAllocation.Agent;

public class ShapleyUtility  extends Utility{

    double utility;
    SpcnDisasterSite disasterSite;
    Agent agent;

    public ShapleyUtility(SpcnDisasterSite disasterSite, Agent agent){
        super();
        utility=-1;
        this.disasterSite=disasterSite;
        this.agent=agent;
    }

    @Override
    public void calculateUtility() {

       utility= disasterSite.calcShapleyValue(agent);

    }

    @Override
    public double getUtility() {
        return utility;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public Object clone() {
        return null;
    }
}
