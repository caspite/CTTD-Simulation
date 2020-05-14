package Solver;

import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

public class EqualAllcoation {

	private List<Ratio> all;
	private static double treshold0 = 0.05;
	private static double treshold2 = 0.94;
	private static double treshold3 = 0.9;
	
	public EqualAllcoation(List<Ratio> all2) {
		this.all = all2;
		while(all.get(all.size()-1).getRatio()<treshold0){
			all.remove(all.size()-1);
			update();
		}
	}

	private void update() {
		
		double  sum = 0;
		for (Ratio e : all) {

			sum = sum + e.getRatio();
		}
		for (Ratio e : all) {			
			e.setRatio(e.getRatio()/ sum);
		}
	}

	public List<Ratio> divide() {
		if(all.size() == 1){
			return all;
		}if(all.size() == 2){
			if(Math.abs(all.get(0).getRatio() -all.get(0).getRatio())<treshold2 ){
				all.get(0).setRatio(0.5);
				all.get(1).setRatio(0.5);
			}
		}
		if(all.size() == 3){
			if(Math.abs(all.get(0).getRatio() -all.get(1).getRatio())<treshold3 
					&& Math.abs(all.get(1).getRatio() -all.get(2).getRatio())<treshold3 ){
				all.get(0).setRatio(1.0/3.0);
				all.get(1).setRatio(1.0/3.0);
				all.get(2).setRatio(1.0/3.0);
			}else if (Math.abs(all.get(0).getRatio() -all.get(0).getRatio())<treshold3){
				double av = all.get(0).getRatio() + all.get(1).getRatio();
				all.get(0).setRatio(av/2.0);
				all.get(1).setRatio(av/2.0);
			} 
			if (Math.abs(all.get(1).getRatio() -all.get(2).getRatio())<treshold3){
				double av = all.get(2).getRatio() + all.get(1).getRatio();
				all.get(2).setRatio(av/2.0);
				all.get(1).setRatio(av/2.0);
			}
		}
		
		return all;
	}

}
