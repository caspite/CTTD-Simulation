/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fisher;


import FordFulkerson.*;

import java.io.*;
import java.util.*;

//find minimum x  that tight set become active set
/**
 *
 * @author Sofi
 */
public class FisherAlgorithm {

    private Double[][] input,output;
    private int A, B;
    private Double[] prices, efficiency;
    private FlowNetwork division;
    private boolean[] tightSet, tSettemp, activeSet;
    private Vector<FlowEdge> tempActive;
    private Double EPSILON = new Double(1E-11);

    public Double[] getPrices() {
        return prices;
    }

    public FisherAlgorithm(Double[][] input) {

        this.input = input;
        this.A = input[0].length;
        this.B = input.length;
        output=new Double[B][A];
        algorithm();
    }

    public void algorithm() {
        init();
        int check1 = 1;

        while (check1 != 0) {
            division();
            check1 = checkDivision();
            if (check1 != 0) {
                initPrices(check1 - 1);
            } else {
                check1 = 0;
            }
        }
        Double x1 = new Double(1);
        Double x2 = new Double(1);
        FlowNetwork temp = division.clone();
        x1 = findX1(temp, activeSet);
        updatePrices(activeSet, x1);
        tightSet = tSettemp;
        creatAcriveSet(tightSet);
        while (!marketClearing()) {

            clearFlow(division);
            temp = division.clone();

            x1 = findX1(temp, activeSet);
            x2 = findX2(tightSet);

            if ((x1<(x2+EPSILON) && x1>1)|| x2==1) {
                updatePrices(activeSet, x1);
                updatTightSet(tSettemp);
            } else {
                updatePrices(activeSet, x2);
                updateActiveSet();
            }
        }
        output();
    }

    //check if  every buyer want any item and for every item has a demand
    
    private void init() {
        prices = new Double[A];
        efficiency = new Double[B];
        tightSet = new boolean[A + B + 1];
        activeSet = new boolean[A + B + 1];
        division = new FlowNetwork(A + B + 2);
        tempActive = new Vector<FlowEdge>();
        for (int i = 0; i < A; i++) {
            prices[i] = new Double(1.0);
        }
        for (int i = 0; i < tightSet.length; i++) {
            activeSet[i] = true;
        }
    }
    // make division according to KKT
    private void division() {
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
             build(i, places);
        }
        build();
    }
    //create edges from buyer i to all item that he prefers
    private void build(int i, Vector<Integer> places) {
        for (int j = 0; j < places.size(); j++) {
            division.addEdge(new FlowEdge(places.elementAt(j) + 1, i + 1 + A, new Double(100*A)));
        }

    }
//create edges from start to all goods, and edges from all buyers to target
    private void build() {

        for (int i = 0; i < B; i++) {
            division.addEdge(new FlowEdge(i + A + 1, A + B + 1, new Double(A)));
        }
    }
    private void buildForX1(boolean[] set, Double x, FlowNetwork activeSet) {
        activeSet.deleteEdges(0);
        for (int i = 1; i <= A; i++) {
            if (set[i]) {
                activeSet.addEdge(new FlowEdge(0, i, prices[i - 1]*x));
            }
        }

    }
    //check if at first division for each item has a buyer
    private int checkDivision() {
        for (int i = 1; i <= A; i++) {
            if (((Bag<FlowEdge>) division.adj(i)).isEmpty()) {
                return i;
            }
        }
        return 0;
    }
    //updates the price for the item that has no buyer
    private void initPrices(int item) {
        Double max = new Double(0.0);
        for (int i = 0; i < B; i++) {

            if (input[i][item]/efficiency[i]>max) {
                max = input[i][item]/efficiency[i];
            }
        }
        prices[item] = max;
    }
// checks if market clearing exists
    private boolean marketClearing() {

        Double sum = new Double(0);
        for (int i = 0; i < A; i++) {
            sum = sum+prices[i];
        }
        if(Math.abs(new Double(A * B)-sum)<EPSILON)
            return true;
        return false;
     
    }
    //find minimum x  that active set become tight set
    private Double findX2(boolean[] set) {
        Double x =new Double(1.0);
        for (int i = A + 1; i <= A + B; i++) {
            if (!set[i]) {
                Double temp = findX2(set, i, x);
                if (x.compareTo(temp)==1 || x.compareTo(new Double(1.0))==0) {
                    x = temp;
                } 
            }
        }
        return x;
    }
//find minimum x  that active set become tight set for specific buyer in active set

    private Double findX2(boolean[] set, int i, Double minx) {

        for (int j = 0; j < A; j++) {
            Double temp = input[i - 1 - A][j]/prices[j];
            if (set[j + 1] && temp.doubleValue() > 0) {

                for (FlowEdge e : division.adj(i)) {
                    if (e.to() == i) {
                        Double minTemp = input[i - A - 1][e.from() - 1]/(prices[e.from() - 1]*temp);
                        if (minx>minTemp || minx==1.0) {
                            minx = minTemp;
                            tempActive.clear();
                            tempActive.add(new FlowEdge(j + 1, i, new Double(100*A)));
                        } else if (Math.abs(minTemp-minx)<EPSILON && minx>1) {
                            if(!tempActive.contains(new FlowEdge(j + 1, i, new Double(100*A))))
                                tempActive.add(new FlowEdge(j + 1, i, new Double(100*A)));
                        }
                    }
                }
            }
        }
        return minx;
    }
// finds minimum x that crate a adge from active set to tight set

    private Double findX1(FlowNetwork activeSet, boolean[] minCutSet) {

        updateTightSet(minCutSet, activeSet);
        boolean flag = false;
        Double x = sumMoney(minCutSet)/sumPrices(minCutSet);
        buildForX1(minCutSet, x, activeSet);
        FordFulkerson ford = new FordFulkerson(activeSet, 0, A + B + 1);
        boolean[] tempSet = ford.cut();
        flag = checkTight(tempSet);
        if (flag) {
            tSettemp = minCutSet;
            return x;
        } else {
            return findX1(activeSet, tempSet);
        }
    }
//updates prices of goods during the process of finding x1

    public void updatePrices(boolean[] minCutSet, Double x) {
        for (int i = 1; i <= A; i++) {
            if (minCutSet[i]) {
                prices[i - 1] = x*prices[i - 1];
            }
        }

    }
//sum money of buyers in active set, helps to find x1

    private Double sumMoney(boolean[] minCutSet) {
        int count = 0;
        for (int i = 1 + A; i <= (A + B); i++) {
            if (minCutSet[i]) {
                count++;
            }
        }
        return new Double(count * A);
    }
//sum prices of goods in active set, helps to find x1

    private Double sumPrices(boolean[] priceFlag) {
        Double sum = new Double(0);
        for (int i = 1; i <= A; i++) {
            if (priceFlag[i]) {
                sum=sum+prices[i - 1];
            }
        }
        return sum;
    }
// checks if the new set is tight, helps to find x1

    private boolean checkTight(boolean[] flags) {
        for (int i = 1; i < flags.length-1; i++) {
            if (flags[i]) {
                return false;
            }
        }

        return true;
    }
// update network on purpose to find x1

    private void updateTightSet(boolean[] minCutSet, FlowNetwork set) {
        for (int i = 0; i <= A + B; i++) {
            if (!minCutSet[i]) {
                set.deleteEdges(i);
            }
            
        }
        clearFlow(set);
    }
    //the final division of items

    private void output() {
        clearFlow(division);
        boolean[] b=new boolean[A+B+1];
        b=convertSet(b);
        buildForX1(b, new Double(1), division);
        FordFulkerson ford = new FordFulkerson(division, 0, A + B + 1);

        division.deleteEdges(0);
        division.deleteEdges(A + B + 1);
        for (int i = A + 1; i <= B + A; i++) {
            for (FlowEdge e : division.adj(i)) {
                output[i - A - 1][e.from() - 1] = e.flow()/prices[e.from() - 1];
            }
        }
        //outpuToFile(output);
    }

//updates the tight set if x1 lower than x2
    private void updatTightSet(boolean[] tSettemp) {
        Vector<Integer> t=new Vector<Integer>();
        for (int i = 1; i <= B + A; i++) {
            if (tSettemp[i] ) {
                tightSet[i] = true;
                activeSet[i] = false;
            }
            if(tightSet[i]){
               t.add(i);
            }
        }
        updatTightSet(t);
    }
    private void updatTightSet(Vector<Integer> t) {
        t.add(0);
        t.add(A+B+1);
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

    private void updateActiveSet() {
        for (FlowEdge e : tempActive) {
            division.addEdge(e);
            tightSet[e.from()] = false;
            activeSet[e.from()] = true;
            updateActiveSet(e.from(), true);
        }
    }
// create active set
    private void creatAcriveSet(boolean[] tempSet) {
        Vector<Integer> t=new Vector<Integer>();
        for (int i = 1; i <= B + A; i++) {
            if (tightSet[i]) {
                t.add(i);
                activeSet[i] = false;
            } else {
                activeSet[i] = true;
            }
        }
        updatTightSet(t);
    }
// true=good, false=buyer

    private void updateActiveSet(int vertex, boolean direction) {
        for (FlowEdge e : division.adj(vertex)) {
            if (direction && e.from() == vertex && tightSet[e.to()]) {
                tightSet[e.to()] = false;
                activeSet[e.to()] = true;
                updateActiveSet(e.to(), false);
            } else if (direction==false && e.to() == vertex && tightSet[e.from()]) {
                tightSet[e.from()] = false;
                activeSet[e.from()] = true;
                updateActiveSet(e.from(), true);
            }
        }
    }

    private void outpuToFile(double [][] output) {
        BufferedWriter out = null;
        try {
            FileWriter s = new FileWriter("output.csv");
            out = new BufferedWriter(s);
            for (int i = 0; i < output.length; i++) {
                for (int j = 0; j < output[i].length; j++) {
                    String o=""+output[i][j]+",  ";
                    out.write(o);
                }
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            System.err.println("Couldn't write to file");
        }
    }

    public Double[][] getOutput() {
		return output;
	}

	private void clearFlow(FlowNetwork division) {
        for (int i = 0; i < A + B + 1; i++) {
            division.clearFlow(i);
        }
    }

    private boolean[] convertSet(boolean[] balancedSet) {
        boolean [] temp=new boolean[A+B+1];
        for (int i = 0; i <= B + A; i++) {
            if (balancedSet[i]) {
                temp[i] = false;
            }
            else{
                 temp[i] = true;
            }
        }
        return temp;
    }

}

