package Helpers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;

import fisher.FisherPolinom;
import TaskAllocation.Location;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[][]time=new double[20][2];
		for (int i = 0; i < 10 ; i++) {
			time[i][0]=10*(i+1);
			for (int j = 0; j < 50; j++) {
				Double[][]input=RandomInput.inputUniform(10*(i+1), 10*(i+1));
				FisherPolinom f=new FisherPolinom(input);
				long start = System.currentTimeMillis( );
				f.algorithm();
				long end = System.currentTimeMillis( );
				
				time[i][1] = time [i][1]+(end - start);
					
			}		
		}
		BufferedWriter out = null;
		try {
			FileWriter s = new FileWriter("time.csv");
			out = new BufferedWriter(s);
			out.newLine();
			for (int i = 0; i < time.length; i++) {
				String o=""+time[i][0]+","+(time[i][1]/50);
				out.write(o);
				out.newLine();
			}
			out.close();
		}catch (Exception e) {
			System.err.println("Couldn't open the file");
		}
		
		
	}

}
