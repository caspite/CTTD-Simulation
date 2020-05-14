/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fisher;

import FordFulkerson.*;
import java.util.*;


/**
 *
 * @author sofi
 */
public class FisherPolinom {

    private int B;
    private int A;
    private Double[][] input,output;
    private Double[] prices;
    private Double EPSILON = new Double(1E-11);
    private Double[] efficiency;
    private boolean[] tempSet;
    private Vector<FlowEdge> tempActive;
    private boolean[] tightSet;

    public Double[] getPrices() {
        return prices;
    }


    public FisherPolinom(Double[][] input) {
        this.input = input;
        A=input[0].length;
        B=input.length;
        output=new Double[B][A];
        algorithm();
    }

    public void algorithm() {
  
        FlowNetwork division = new FlowNetwork(A + B + 2);

        boolean[] activeSet = new boolean[A + B + 1];
        init();
        int check1 = 1;
        while (check1 != 0) {
            division = division(division);
            check1 = checkDivision(division);
            if (check1 != 0) {
                initPrices(check1 - 1);
            } else {
                check1 = 0;
            }
            build(division);
        }
        while (!marketClearing()) {
            boolean[] balancedSet = new boolean[A + B + 1];
            balancedSet = convertSet(balancedSet);
            division = balancedFlow(division, balancedSet);
            activeSet = creatH(division);
            updateTarget(division, tempSet, 0.0);
            boolean flagPhaseEnd = true;
            while (flagPhaseEnd) {
                FlowNetwork f1=division.clone();
                FlowNetwork f2=division.clone();
                Double x1 = findX1(f1, activeSet);
                Double x2 = findX2(f2, activeSet);

                if ((x1<x2+EPSILON && x1>1.0)|| (x2==1.0 && tempActive.isEmpty())) {
                    updatePrices(activeSet, x1);
                    updatTightSet(division);
                    tempActive.clear();
                    flagPhaseEnd = false;
                } else {
                    updatePrices(activeSet, x2);
                    activeSet = updateActiveSet(division, activeSet);
                    tempActive.clear();
                }
            }
        }
        output(division);
    }

    private Double findX2(FlowNetwork set, boolean[] activeSet) {
        Double x = new Double(1.0);
        for (int i = A + 1; i <= A + B; i++) {
            if (activeSet[i]) {
                Double temp = findX2(set, activeSet, i, x);
                if (x > temp  || (x == 1.0  && !tempActive.isEmpty())) {
                    
                    x = temp;
                }
            }
        }
        return x;
    }
//find minimum x  that active set become tight set for specific buyer in active set

    private Double findX2(FlowNetwork set, boolean[] activeSet, int i, Double minx) {
        Double EPSILON = new Double(1E-11);
        for (int j = 0; j < A; j++) {
            Double temp = input[i - 1 - A][j] / prices[j];
            if (!activeSet[j + 1] && temp.doubleValue() > 0) {
                for (FlowEdge e : set.adj(i)) {
                    if (e.to() == i) {
                        Double minTemp = input[i - A - 1][e.from() - 1] / (prices[e.from() - 1] * temp);
                        if (minx > minTemp || (minx == 1.0 && tempActive.isEmpty())) {
                            minx = minTemp;
                            tempActive.clear();
                            tempActive.add(new FlowEdge(j + 1, i, new Double(100*A)));
                        } else if (Math.abs(minTemp-minx)<EPSILON && minx > 1.0) {
                            if (!tempActive.contains(new FlowEdge(j + 1, i, new Double(100*A)))) {
                                tempActive.add(new FlowEdge(j + 1, i, new Double(100*A)));
                            }
                        }
                    }
                }
            }
        }
        return minx;
    }
// finds minimum x that crate an edge from active set to tight set

    private Double findX1(FlowNetwork set, boolean[] minCutSet) {

        boolean flag = false;
        Double x = sumMoney(minCutSet) / (sumPrices(minCutSet));
        updateSink(set, minCutSet, x);
        clearFlow(set);
        FordFulkerson ford = new FordFulkerson(set, 0, A + B + 1);
        boolean[] temp = ford.cut();
        flag = checkTight(temp);
        if (flag) {
            tightSet=temp;
            return x;
        } else {
            return findX1(set, temp);
        }
    }
    //find balanced flow

    private FlowNetwork balancedFlow(FlowNetwork network, boolean[] set) {

        Double x = (sumMoney(set) - sumPrices(set)) / countActive(set);
        clearFlow(network);
        updateSink(network, set, new Double(1.0));
        updateTarget(network, set, x);
        FordFulkerson ford = new FordFulkerson(network, 0, B + A + 1);
        //network=checkFlow(network);
        if (!checkTight(ford.cut())) {
            boolean[] set1 = ford.cut();
            boolean[] set2 = convertTightSet(set1, set);

            FlowNetwork f1 = network.clone();
            FlowNetwork f2 = network.clone();
            f1 = balancedFlow(f1, set1);
            f2 = balancedFlow(f2, set2);
            network = combine(f1, f2, set1, set2);
        }
        return network;
    }

    // updates capacity to all edges from sink
    private void updateSink(FlowNetwork network, boolean[] set, Double x) {
        network.deleteEdges(0);
        for (int i = 1; i <= A; i++) {
            if (set[i]) {
                network.addEdge(new FlowEdge(0, i, prices[i - 1] * x));
            } else {
                network.deleteEdges(i);
            }
        }
    }
    //reduces the flow in order to find balanced flow

    private void updateTarget(FlowNetwork network, boolean[] set, Double x) {
        network.deleteEdges(B + A + 1);
        for (int i = 0; i < B; i++) {
            if (set[A+i+1]) {
                network.addEdge(new FlowEdge(i + A + 1, A + B + 1, (A - x)));
            }else{
                network.deleteEdges(i+A+1);
            }
        }
    }
        //devide the network in 2 sub networks

    private

     boolean[] convertTightSet(boolean[] tSettemp, boolean[] set) {
        boolean[] temp = new boolean[A + B + 2];
        temp[0] = true;
        for (int i = 1; i <= B + A; i++) {
            if (!tSettemp[i] && set[i]) {
                temp[i] = true;
            }

        }
        return temp;
    }
    //checks if the set is tight

    private boolean checkTight(boolean[] flags) {
        for (int i = 1; i < flags.length - 1; i++) {
            if (flags[i]) {
                return false;
            }
        }
        return true;
    }
// count buyers in H

    private Double countActive(boolean[] minCutSet) {
        int count = 0;
        for (int i = 1 + A; i <= (A + B); i++) {
            if (minCutSet[i]) {
                count++;
            }
        }
        return new Double(count);
    }
//sum  money of buyers

    private Double sumMoney(boolean[] minCutSet) {
        int count = 0;
        for (int i = 1 + A; i <= (A + B); i++) {
            if (minCutSet[i]) {
                count++;
            }
        }
        return new Double(count * A);
    }
//sum prices of goods in active set

    private Double sumPrices(boolean[] priceFlag) {
        Double sum = new Double(0);
        for (int i = 1; i <= A; i++) {
            if (priceFlag[i]) {
                sum = sum + prices[i - 1];
            }
        }
        return sum;
    }
//find active set

    private boolean[] creatH(FlowNetwork division) {
        Double EPSILON = new Double(1E-11);
        boolean[] set = new boolean[A + B + 1];
        Double max = findMaxSurplus(division);
        for (FlowEdge e : division.adj(A + B + 1)) {

            if (Math.abs(A - e.flow() - max)<EPSILON) {
                set[e.from()] = true;
                for (FlowEdge f : division.adj(e.from())) {
                    if (f.flow() > 0) {
                        set[f.from()] = true;
                    }
                }
            }
        }
        return set;
    }
    // checks if market clearing exists

    private boolean marketClearing() {
        Double EPSILON = new Double(1E-9);
        Double sum = new Double(0);
        for (int i = 0; i < A; i++) {
            sum = sum + prices[i];
        }
        if (Math.abs(new Double(A * B) - sum) < EPSILON) {
            return true;
        }
        return false;
    }
//check if at first division for each item has a buyer

    private int checkDivision(FlowNetwork division) {
        for (int i = 1; i <= A; i++) {
            if (((Bag<FlowEdge>) division.adj(i)).isEmpty()) {
                return i;
            }
        }
        return 0;
    }
//updates the price for the item that has no buyer

    private void initPrices(int item) {
        Double max = new Double(0);
        for (int i = 0; i < B; i++) {
            if (input[i][item] / efficiency[i] > max) {
                max = input[i][item] / efficiency[i];
            }
        }
        prices[item] = max;
    }
//creat adges from buyer i to all item that he prefers

    private void build(FlowNetwork division, int i, Vector<Integer> places) {
        for (int j = 0; j < places.size(); j++) {
            division.addEdge(new FlowEdge(places.elementAt(j) + 1, i + 1 + A, new Double(100*A)));
        }

    }
//crrate adges from start to all goods, and edges from all buyers to target

    private void build(FlowNetwork division) {
        for (int i = 0; i < B; i++) {
            division.addEdge(new FlowEdge(i + A + 1, A + B + 1, new Double(A)));
        }
        for (int i = 0; i < A; i++) {
            division.addEdge(new FlowEdge(0, i + 1, prices[i]));
        }
    }
// make division according to KKT

    private FlowNetwork division(FlowNetwork division) {

        division.clearEdges();
        Vector<Integer> places = new Vector<Integer>();
        for (int i = 0; i < B; i++) {
            Double max = new Double(0);
            for (int j = 0; j < A; j++) {
                double temp=input[i][j]/prices[j];
                if (Math.abs(temp-max)<EPSILON) {
                	places.add(j);
                } else if (temp>max) {
                	max = input[i][j]/prices[j];
                    places.clear();
                    places.add(j);
                    
                }
            }
            efficiency[i] = max;
            build(division, i, places);
        }
        return division;
    }
//initialization

    private void init() {
        prices = new Double[A];
        efficiency = new Double[B];
        tempActive = new Vector<FlowEdge>();
        for (int i = 0; i < A; i++) {
            prices[i] = new Double(1.0);
        }
        tempSet=new boolean[A+B+2];
        tempSet=convertSet(tempSet);
    }


//clears flow in the network

    private void clearFlow(FlowNetwork division) {
        for (int i = 0; i < division.V(); i++) {
            division.clearFlow(i);
        }
    }
    //combines 2 networks in one

    private FlowNetwork combine(FlowNetwork f1, FlowNetwork f2, boolean[] set1, boolean[] set2) {
        FlowNetwork v = new FlowNetwork(B + A + 2);
        for (int i = 1; i <= A; i++) {
            for (FlowEdge e : f1.adj(i)) {
                v.addEdge(e);
            }
            for (FlowEdge e : f2.adj(i)) {
                v.addEdge(e);
            }
        }
        for (FlowEdge e : f1.adj(A + B + 1)) {
            v.addEdge(e);
        }
        for (FlowEdge e : f2.adj(A + B + 1)) {
            v.addEdge(e);
        }
        return v;
    }
//finds max  money surplus

    private Double findMaxSurplus(FlowNetwork division) {
        Double max = new Double(0.0);
        for (FlowEdge e : division.adj(A + B + 1)) {
            if (A - e.flow() >= max) {
                max = A - e.flow();
            }
        }
        return max;
    }
//convertS boolean arrays

    private boolean[] convertSet(boolean[] balancedSet) {
        boolean[] temp = new boolean[A + B + 1];
        for (int i = 0; i <= B + A; i++) {
            if (balancedSet[i]) {
                temp[i] = false;
            } else {
                temp[i] = true;
            }
        }
        return temp;
    }

    private void updatePrices(boolean[] minCutSet, Double x) {
        for (int i = 1; i <= A; i++) {
            if (minCutSet[i]) {
                prices[i - 1] = x * prices[i - 1];
            }
        }

    }

    private boolean[] updateActiveSet(FlowNetwork division, boolean[] activeSet) {
        for (FlowEdge e : tempActive) {
            division.addEdge(e);
            activeSet[e.from()] = true;
            activeSet = updateActiveSet(division, e.from(), activeSet, true);
        }
        return activeSet;
    }

    private boolean[] updateActiveSet(FlowNetwork division, int vertex, boolean[] activeSet, boolean direction) {
        for (FlowEdge e : division.adj(vertex)) {
            if (direction && e.from() == vertex && !activeSet[e.to()]) {
                activeSet[e.to()] = true;
                activeSet = updateActiveSet(division, e.to(), activeSet, false);
            } else if (direction == false && e.to() == vertex && !activeSet[e.from()]) {
                activeSet[e.from()] = true;
                activeSet = updateActiveSet(division, e.from(), activeSet, true);
            }
        }
        return activeSet;
    }

    private void output(FlowNetwork division) {
        clearFlow(division);
        boolean[] b = new boolean[A + B + 1];
        b = convertSet(b);
        updateSink(division, b, new Double(1));
        FordFulkerson ford = new FordFulkerson(division, 0, A + B + 1);
        division.deleteEdges(0);
        division.deleteEdges(A + B + 1);
        for (int i = A + 1; i <= B + A; i++) {
            for (FlowEdge e : division.adj(i)) {
                output[i - A - 1][e.from() - 1] = e.flow() / prices[e.from() - 1];
            }
        }
    }

    public Double[][] getOutput() {
		return output;
	}


	
    private void updatTightSet(FlowNetwork division) {
        Vector<Integer> t=new Vector<Integer>();
        t.add(0);
        t.add(A+B+1);
        for(int i=0;i<=A+B;i++){
            if(tightSet[i]){
                t.add(i);
            }
        }
       for(int i = 1; i <= B + A; i++){
           if(tightSet[i]){
               for (FlowEdge f: division.adj(i)){
                   if(!t.contains(f.from())||!t.contains(f.to())){
                       division.deleteEdge(f);
                   }
               }
           }

       }
    }

}
