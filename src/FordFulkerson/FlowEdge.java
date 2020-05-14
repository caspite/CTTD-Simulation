/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FordFulkerson;



/**
 *
 * @author Sofi
 */
public class FlowEdge {
    private final int v;             // from
    private final int w;             // to
    private final Double capacity;   // capacity
    private Double flow;             // flow

    public FlowEdge(int v, int w, Double capacity) {
        if (capacity.doubleValue() < 0) throw new RuntimeException("Negative edge capacity");
        this.v         = v;
        this.w         = w;
        this.capacity  = capacity;
        this.flow      = new Double(0);
    }

    public FlowEdge(int v, int w, Double capacity, Double flow) {
        if (capacity.doubleValue() < 0) throw new RuntimeException("Negative edge capacity");
        this.v         = v;
        this.w         = w;
        this.capacity  = capacity;
        this.flow      = flow;
    }

    // accessor methods
    public int from()         { return v;        }
    public int to()           { return w;        }
    public Double capacity()  { return capacity; }
    public Double flow()      { return flow;     }


    public int other(int vertex) {
        if      (vertex == v) return w;
        else if (vertex == w) return v;
        else throw new RuntimeException("Illegal endpoint");
    }

    public Double residualCapacityTo(int vertex) {
        if      (vertex == v) return flow;

        else if (vertex == w){
            return capacity-flow;
        }
        else throw new RuntimeException("Illegal endpoint");
    }

    public void addResidualFlowTo(int vertex, Double delta) {
        if      (vertex == v) flow=flow-delta;
        else if (vertex == w) flow=flow+delta;
        else throw new RuntimeException("Illegal endpoint");
    }


    @Override
    public String toString() {
        return v + "->" + w + " " + flow + "/" + capacity;
    }

    @Override
    public FlowEdge clone(){
        FlowEdge f=new FlowEdge(v, w, capacity, flow);
        return f;
    }

    @Override
    public boolean equals (Object o){
        if(o instanceof FlowEdge){
            return ((FlowEdge)o).v==v && ((FlowEdge)o).w==w;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.v;
        hash = 97 * hash + this.w;
        return hash;
    }


   /**
     * Test client.
     */
    /*public static void main(String[] args) {
        FlowEdge e = new FlowEdge(12, 23, 3.14);
        System.out.println(e);
    }*/

}
