package CttdSolver;

public class WeightedUtility extends Utility {

    double utility;
    double ratio;

    public WeightedUtility(){
        super();
        utility=-1;
    }
    public WeightedUtility(double ratio){
        super();
        this.ratio=ratio;
    }
    @Override
    public void calculateUtility() {
        utility=(1-this.ratio)*ratio;
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
