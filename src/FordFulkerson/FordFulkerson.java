/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FordFulkerson;



import java.util.Vector;

/**
 *
 * @author Sofi
 */
public class FordFulkerson {

    private boolean[] marked;     // marked[v] = true iff s->v path in residual graph
    private FlowEdge[] edgeTo;    // edgeTo[v] = last edge on shortest residual s->v path
    private Double value;         // current value of max flow

    // max flow in flow network G from s to t
    public FordFulkerson(FlowNetwork G, int s, int t) {
        value = excess(G, t);
        if (!isFeasible(G, s, t)) {
            throw new RuntimeException("Initial flow is infeasible");
        }

        // while there exists an augmenting path, use it
        while (hasAugmentingPath(G, s, t)) {

            // compute bottleneck capacity
            Double bottle = new Double(Double.POSITIVE_INFINITY);
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
            }

            // augment flow
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                edgeTo[v].addResidualFlowTo(v, bottle);
            }

            value=value+bottle;
        }

        // check optimality conditions
        assert check(G, s, t);
    }

    // return value of max flow
    public Double value() {
        return value;
    }

    // is v in the s side of the min s-t cut?
    public boolean inCut(int v) {
        return marked[v];
    }

    public Vector<Integer> cutVec() {
        Vector<Integer> cut = new Vector<Integer>();
        for (int i = 0; i < marked.length; i++) {
            if (marked[i]) {
                cut.add(i);
            }
        }
        return cut;
    }

    public boolean[] cut() {
        return marked;
    }
    // return an augmenting path if one exists, otherwise return null

    private boolean hasAugmentingPath(FlowNetwork G, int s, int t) {
        edgeTo = new FlowEdge[G.V()];
        marked = new boolean[G.V()];
        Double EPSILON = new Double(1E-11);
        // breadth-first search
        Queue<Integer> q = new Queue<Integer>();
        q.enqueue(s);
        marked[s] = true;
        while (!q.isEmpty()) {
            int v = q.dequeue();

            for (FlowEdge e : G.adj(v)) {
                int w = e.other(v);
                
                // if residual capacity from v to w
                if (e.residualCapacityTo(w)> EPSILON) {
                    if (!marked[w]) {
                        edgeTo[w] = e;
                        marked[w] = true;
                        q.enqueue(w);
                    }
                }
            }
        }

        // is there an augmenting path?
        return marked[t];
    }

    // return excess flow at vertex v
    private Double excess(FlowNetwork G, int v) {
        Double excess = new Double(0.0);
        for (FlowEdge e : G.adj(v)) {
            if (v == e.from()) {
                excess=excess-e.flow();
            } else {
                excess=excess+e.flow();
            }
        }
        return excess;
    }

    // return excess flow at vertex v
    private boolean isFeasible(FlowNetwork G, int s, int t) {
        Double EPSILON = new Double(1E-11);

        // check that capacity constraints are satisfied
        for (int v = 0; v < G.V(); v++) {
            for (FlowEdge e : G.adj(v)) {
                if (e.flow().doubleValue() < 0 || e.flow().compareTo(e.capacity())==1) {
                    System.err.println("Edge does not satisfy capacity constraints: " + e);
                    return false;
                }
            }
        }

        // check that net flow into a vertex equals zero, except at source and sink
        if (Math.abs(value + excess(G, s)) > EPSILON) {
            System.err.println("Excess at source = " + excess(G, s));
            System.err.println("Max flow         = " + value);
            return false;
        }
        if (Math.abs(value - excess(G, t)) > EPSILON) {
            System.err.println("Excess at sink   = " + excess(G, t));
            System.err.println("Max flow         = " + value);
            return false;
        }
        for (int v = 0; v < G.V(); v++) {
            if (v == s || v == t) {
                continue;
            } else if (Math.abs(excess(G, v))>EPSILON) {
                System.err.println("Net flow out of " + v + " doesn't equal zero");
                return false;
            }
        }
        return true;
    }

    // check optimality conditions
    private boolean check(FlowNetwork G, int s, int t) {

        // check that flow is feasible
        if (!isFeasible(G, s, t)) {
            System.err.println("Flow is infeasible");
            return false;
        }

        // check that s is on the source side of min cut and that t is not on source side
        if (!inCut(s)) {
            System.err.println("source " + s + " is not on source side of min cut");
            return false;
        }
        if (inCut(t)) {
            System.err.println("sink " + t + " is on source side of min cut");
            return false;
        }

        // check that value of min cut = value of max flow
        Double mincutValue = new Double(0.0);
        for (int v = 0; v < G.V(); v++) {
            for (FlowEdge e : G.adj(v)) {
                if ((v == e.from()) && inCut(e.from()) && !inCut(e.to())) {
                    mincutValue=mincutValue+e.capacity();
                }
            }
        }

        Double EPSILON = new Double(1E-11);
        if (Math.abs(mincutValue - value) > EPSILON) {
            System.err.println("Max flow value = " + value + ", min cut value = " + mincutValue);
            return false;
        }

        return true;
    }

    // test client that creates random network, solves max flow, and prints results
    public static void main(String[] args) {

        // create flow network with V vertices and E edges
        /*int V = Integer.parseInt(args[0]);
        int E = Integer.parseInt(args[1]);
        int s = 0, t = V-1;
        FlowNetwork G = new FlowNetwork(V, E);*/
        int V = 10;
        int s = 0, t = 9;
        FlowNetwork G = new FlowNetwork(V);
        G.addEdge(new FlowEdge(0, 1, new Double(108.0/23.0)));
        G.addEdge(new FlowEdge(0, 2, new Double(72.0/23.0)));
        G.addEdge(new FlowEdge(0, 3, new Double(96.0/23.0)));
        G.addEdge(new FlowEdge(0, 4, new Double(4.5)));
        G.addEdge(new FlowEdge(5, 9, new Double(4.0)));
        G.addEdge(new FlowEdge(6, 9, new Double(4.0)));
        //G.addEdge(new FlowEdge(7, 9, new Double(4.0)));
        G.addEdge(new FlowEdge(8, 9, new Double(4.0)));
        G.addEdge(new FlowEdge(1, 8, new Double(100)));
       G.addEdge(new FlowEdge(1, 6, new Double(100)));
        G.addEdge(new FlowEdge(2, 5, new Double(100)));
        G.addEdge(new FlowEdge(2, 6, new Double(100)));
        //G.addEdge(new FlowEdge(4, 5, new Double(100)));
        //G.addEdge(new FlowEdge(4, 7, new Double(100)));
        G.addEdge(new FlowEdge(3, 5, new Double(100)));
        System.out.println(G);

        // compute maximum flow and minimum cut
        FordFulkerson maxflow = new FordFulkerson(G, s, t);
        System.out.println("Max flow from " + s + " to " + t);
        for (int v = 0; v < G.V(); v++) {
            for (FlowEdge e : G.adj(v)) {
                if ((v == e.from()) && e.flow().doubleValue() > 0) {
                    System.out.println("   " + e);
                }
            }
        }

        // print min-cut
        System.out.print("Min cut: ");
        for (int v = 0; v < G.V(); v++) {
            if (maxflow.inCut(v)) {
                System.out.print(v + " ");
            }
        }
        System.out.println();

        System.out.println("Max flow value = " + maxflow.value());
    }
}
