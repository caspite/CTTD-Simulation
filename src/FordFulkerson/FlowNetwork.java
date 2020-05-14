/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FordFulkerson;


import java.util.Iterator;


/**
 *
 * @author Sofi
 */
public class FlowNetwork {

    private final int V;
    private int E;
    private Bag<FlowEdge>[] adj;

    // empty graph with V vertices
    @SuppressWarnings("unchecked")
	public FlowNetwork(int V) {
        this.V = V;
        this.E = 0;
        adj = (Bag<FlowEdge>[]) new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<FlowEdge>();
        }
    }

    // random graph with V vertices and E edges
    public FlowNetwork(int V, int E) {
        this(V);
        for (int i = 0; i < E; i++) {
            int v = StdRandom.uniform(V);
            int w = StdRandom.uniform(V);
            double capacity = StdRandom.uniform(100);
            addEdge(new FlowEdge(v, w, new Double(capacity)));
        }
    }

    public void clearEdges() {
        for (int i = 0; i < V; i++) {
            adj[i].clear();
        }
        this.E = 0;
    }



    public void deleteEdges(int v) {
        Iterator<FlowEdge> it;
        Iterator<FlowEdge> it1=adj(v).iterator();
        while (it1.hasNext()) {
            FlowEdge f=it1.next();
            if (f.from() == v) {
                it = adj[f.to()].iterator();
                while (it.hasNext()) {
                    FlowEdge t = it.next();
                    if (t.from() == v) {
                        it.remove();
                        it1.remove();
                        E--;
                        break;
                    }
                }
            } else {
                it = adj[f.from()].iterator();
                while (it.hasNext()) {
                    FlowEdge t = it.next();
                    if (t.to() == v) {
                        it.remove();
                        it1.remove();
                        E--;
                        break;
                    }
                }
            }
        }
         adj[v].clear();
    }

    public void deleteEdge(FlowEdge fl) {
          Iterator<FlowEdge> it;
            it = adj[fl.to()].iterator();
                while (it.hasNext()) {
                    FlowEdge t = it.next();
                    if (t.from() == fl.from() ){
                        it.remove();
                        break;
                    }
                }
            it = adj[fl.from()].iterator();
                while (it.hasNext()) {
                    FlowEdge t = it.next();
                    if (t.to() == fl.to() ){
                        it.remove();
                        break;
                    }
                }
            E--;
}
    public void updateEdge(FlowEdge fl){
        deleteEdge(fl);
        addEdge(fl);
    }

    // graph, read from input stream
 /*  public FlowNetwork(In in) {
    this(in.readInt());
    int E = in.readInt();
    for (int i = 0; i < E; i++) {
    int v = in.readInt();
    int w = in.readInt();
    double capacity = in.readDouble();
    addEdge(new FlowEdge(v, w, capacity));
    }
    }*/
    // number of vertices and edges
    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    // add edge e in both v's and w's adjacency lists
    public void addEdge(FlowEdge e) {
        E++;
        int v = e.from();
        int w = e.to();
        adj[v].add(e);
        adj[w].add(e);
    }

    // return list of edges incident to  v
    public Iterable<FlowEdge> adj(int v) {
        return adj[v];
    }

    // return list of all edges - excludes self loops
    public Iterable<FlowEdge> edges() {
        Bag<FlowEdge> list = new Bag<FlowEdge>();
        for (int v = 0; v < V; v++) {
            for (FlowEdge e : adj(v)) {
                if (e.to() != v) {
                    list.add(e);
                }
            }
        }
        return list;
    }
    @Override
public FlowNetwork clone(){
    FlowNetwork f= new FlowNetwork(V);
    f.E=E;
    for(int i=0; i<adj.length;i++){
        f.adj[i]=adj[i].clone();
    }
    return f;
}
    // string representation of Graph (excludes self loops) - takes quadratic time
    public String toString() {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(v + ":  ");
            for (FlowEdge e : adj[v]) {
                if (e.to() != v) {
                    s.append(e + "  ");
                }
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

    public void clearFlow(int v) {
        for (FlowEdge e : adj(v)) {
            if (e.from() == v) {
              e.addResidualFlowTo(v, e.flow());
            } else {
                e.addResidualFlowTo(v, e.flow()*-1.0);
            }
        }
    }
    // test client
/*    public static void main(String[] args) {
    In in = new In(args[0]);
    FlowNetwork G = new FlowNetwork(in);
    System.out.println(G);
    }*/
}
