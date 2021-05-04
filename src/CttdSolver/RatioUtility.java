package CttdSolver;

public class RatioUtility  extends  Utility{
    double utility;

    public RatioUtility(){
        super();
        utility=-1;
    }
    public RatioUtility(double ratio){
        super();
        utility=ratio;
    }

    @Override
    public void calculateUtility() {

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
