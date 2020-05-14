package Helpers;


import java.net.*;
import java.util.Scanner;
import java.awt.Desktop;
import java.io.*;

import TaskAllocation.Location;

public class URLConnectionReader  {
	
	public double getDistance(Location from, Location to) throws Exception{
		String myUrl = new String("http://open.mapquestapi.com/directions/v0/route?outFormat=xml&unit=k&from=");
    	myUrl = new StringBuffer(myUrl).append(from.toString()+"&to="+to.toString()+"&callback=renderNarrative").toString(); 	
    	return readDistance(myUrl);
	}
	
	public double getDemoDistance(Location from, Location to) {
		double lat = (Math.abs(from.getLat()-to.getLat()));
		double lng = (Math.abs(from.getLng()-to.getLng()));
		return Math.sqrt(Math.pow(lat,2)+Math.pow(lng,2));
	}
	public double getDemoDistance2(Location from, Location to) {
		double lat =(Math.abs(from.getLat()-to.getLat()));
		double lng =(Math.abs(from.getLng()-to.getLng()));
		return Math.sqrt(Math.pow(lat,2)+Math.pow(lng,2));
	}
	
	private double readDistance(String url1) throws Exception {
        URL path = new URL(url1);
        URLConnection yc = path.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                yc.getInputStream()));
        String inputLine;
        String stringDistance = null;
        
        while ((inputLine = in.readLine()) != null) {
            //System.out.println(inputLine);
            Scanner scanner = new Scanner(inputLine);
            scanner.findInLine("<distance>");
            
            stringDistance =scanner.nextLine().substring(0, 3);
	
        }
        double dis = Double.parseDouble(stringDistance);
        in.close();
        return dis;
    }
	
    public void openXML(String url1) throws Exception {
        URL path = new URL(url1);
        URLConnection yc = path.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                yc.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            System.out.println(inputLine);
        in.close();
    }
    
    public void openURL(String urlText){
        if (Desktop.isDesktopSupported())
        {
            URI uri = URI.create(urlText);
            try
            {
                Desktop.getDesktop().browse(uri);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
//String coordinate1="31.24801,34.7899";
//String coordinate2="31.253,34.7915";
//http://maps.google.com/?q=From+31.24801,34.7899+to+31.253,34.7915
//http://maps.googleapis.com/maps/api/directions/xml?origin=31.24801&34.7899&destination=31.253&34.7915&sensor=false