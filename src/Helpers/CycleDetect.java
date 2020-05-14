package Helpers;

import java.util.*;



import org.jgrapht.alg.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;

import PoliceTaskAllocation.*;
import TaskAllocation.*;


public class CycleDetect {
	private Vector<Assignment>[] allocation;
	private Vector<Task> events;
	private DirectedGraph<Integer, DefaultEdge> directedGraph;
	private CycleDetector<Integer, DefaultEdge> cycleDetector;
	private Vector<PoliceUnit> units;
	public CycleDetect(Vector<Assignment>[] allocation, Vector<Task> activeEvents, Vector<PoliceUnit> units) {
		this.allocation = allocation;
		this.events = activeEvents;
		this.units  = units;
	}
	
	public Vector<Assignment>[] solve(){
		createGraph();
		int counter = 0;
		while(findCycles() ){
			/*System.out.println("true");
			System.out.println(cycleDetector.findCycles());
			if(counter>4){
				System.out.println("true");
				System.out.println(cycleDetector.findCycles());
			}*/
			breakDeadlock();
			createGraph();
			counter++;
		}
		return allocation;
		
  	}
// finds the vertices in the cycle and breaks the deadlock
	private void breakDeadlock() {
		
		 Set<Integer> s = cycleDetector.findCycles();
		 ArrayList<Integer> list =new ArrayList<Integer>(s);
		 Collections.sort(list);
		 for (Iterator it = list.iterator(); it.hasNext();) {
			Integer i = (Integer) it.next();
			/*if(i<=0){
				it.remove();
			}*/
		}
		 for (Vector<Assignment> as : allocation) {
			 reorderAllocation(list,as);
		 }
	}
	
	//reorder
	private void reorderAllocation(List<Integer> list, Vector<Assignment> as) {
		int id = 0;
		int ind = -1;
		
		TreeMap <Integer,Integer> map =  new TreeMap<Integer, Integer>();
		for (int i = 0; i < as.size(); i++) {
			id = as.get(i).getTask().getId();
			if(list.contains(id)){
				if(list.indexOf(id) == 0){
					if(ind==-1){
						return;
					}
					moveUp(as,ind,i);
					
				}else if(ind==-1){
					ind = i;
				}
			}
		}
	}

private void moveUp(Vector<Assignment> as, int newind, int indexOf) {
		Assignment a =as.remove(indexOf);
		as.add(newind,a);
	}

//finds cycles, return true if there are cycles
	private boolean findCycles() {
		cycleDetector = new CycleDetector<Integer, DefaultEdge>(directedGraph);
		return (cycleDetector.detectCycles() && cycleDetector.findCycles().size()>1) ;
	}

	//creates graph according to allocation
	public void createGraph(){
		directedGraph = new DefaultDirectedGraph<Integer, DefaultEdge> (DefaultEdge.class);
		//creates agent vertices 
		for (PoliceUnit p : units) {
			Integer s =p.getId()*-1000;
			directedGraph.addVertex(s);			
		}
		// creates event vertices 
		for (Task e : events) {
			Integer s =e.getId();
			directedGraph.addVertex(s);

		}
		//creates directed edges according to current allocation
		for (Vector<Assignment> as : allocation) {
			if(as.isEmpty()){
				continue;
			}
			Integer s =as.get(0).getAgent().getId()*-1000;
			Integer t =as.get(0).getTask().getId();
			directedGraph.addEdge(s, t);
			s=t;
			for (int i = 1; i <as.size(); i++) {
				t=as.get(i).getTask().getId();
				directedGraph.addEdge(s, t);
				s=t;
			}
		}
	}
	
	/*public static void main(String[] args) {
		DirectedGraph<String, DefaultEdge> d= new DefaultDirectedGraph<String, DefaultEdge> (DefaultEdge.class);
		d.addVertex("a");
		d.addVertex("b");
		d.addVertex("c");
		d.addVertex("d");
		d.addVertex("e");
		d.addVertex("f");
		d.addEdge("a", "d");
		d.addEdge("c", "f");
		d.addEdge("b", "e");
		d.addEdge("d", "e");
		d.addEdge("e", "f");
		d.addEdge("f", "d");
		CycleDetector<String, DefaultEdge> c = new CycleDetector<String, DefaultEdge>(d);
		System.out.println(c.detectCycles());
		System.out.println(c.findCycles());
		
		
		
	}*/

}
