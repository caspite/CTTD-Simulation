package TaskAllocation;

import java.io.Serializable;

public class Location implements Serializable{
	
	private double lat;// latitude
	private double lng;//longitude
	
	public Location(double lat1,double lng1) {
		lat=lat1;
		lng=lng1;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}
	
	@Override
	public String toString() {

		return (lat+","+lng);
	}
	
	public boolean equals(Object o){
		if(o instanceof Location){
			return ((Location)o).lat==lat && ((Location)o).lng==lng;
		}
		return false;
	}
}
