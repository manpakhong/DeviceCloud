package com.littlecloud.ac.messagehandler.util;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.jboss.logging.Logger;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;
import com.google.code.geocoder.model.LatLng;

public class GeoUtils {
	public static final double PI = 3.14159265;
	public static final double EARTH_RADIUS = 6378137;
	private static final Logger log = Logger.getLogger(GeoUtils.class);
	
	public static void main(String [] argc) {
/*
		// Hong Kong
		float lat1 = 22.337304f;
		float lon1 = 114.147969f;
		float lat2 = 22.33868f;
		float lon2 = 114.152387f;
		float lat3 = 22.335627f;
		float lon3 = 114.156016f;

		float checkLon = 114.15163f;
		float checkLat = 22.334653f;
	
		// United State
		float lat1 = 39.0993032f;
		float lon1 = -94.4209558f;
		float lat2 = 39.0997345f;
		float lon2 = -94.4197138f;
		float lat3 = 39.0997387f;
		float lon3 = -94.4197138f;

		float checkLat = 39.0996299f;
		float checkLon = -94.4202275f;
  
		// Gabon
		float lat1 = -1.8271066f;
		float lon1 = 12.3023632f;
		float lat2 = -1.8295083f;
		float lon2 = 12.3091713f;
		float lat3 = -1.8333762f;
		float lon3 = 12.3041544f;

		float checkLat = -1.8441637f;
		float checkLon = 12.3153298f;
	  
		float [] lats = {lat1, lat2, lat3};
		float [] lngs = {lon1, lon2, lon3};
		
		int radius = 300;
	  
		System.out.println(isIntersectsPolygon(lats, lngs, checkLat, checkLon, radius));
		printOffset(checkLat, checkLon, radius);
*/			
	}
	
	public static float distanceMiles(float lat1, float lng1, float lat2, float lng2) {
		double earthRadius = 3958.75; // 3958.75 Miles, KM = 6373;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				   Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		return new Float(dist * 1000).floatValue();
    }
	
	public static float distanceMeters(float lat1, float lng1, float lat2, float lng2) {
		float earthRadius = 6371; // km
		lat1 = (float) Math.toRadians(lat1);
		lng1 = (float) Math.toRadians(lng1);
		lat2 = (float) Math.toRadians(lat2);
		lng2 = (float) Math.toRadians(lng2);
		float d = (float) Math.acos(Math.sin(lat1)*Math.sin(lat2) + 
						  Math.cos(lat1)*Math.cos(lat2) *
						  Math.cos(lng2-lng1)) * earthRadius;
						  
		return d * 1000;
    }

	public static float offsetLatitude(float latitude, float longitude, int meter) {
//		double result = meter * 180 / PI * EARTH_RADIUS;

		double rlat = Math.toRadians(latitude) * PI / 180;
		
		double meterPerLat = 111132.92 - 559.82 * Math.cos(2 * rlat) + 1.175*Math.cos(4*rlat);

		double result = (meter / meterPerLat);
		if (longitude < 0)
			result = latitude - result;
		else
			result += latitude;
		
		return (float) result;
	}
	
	public static float offsetLongitude(float latitude, float longitude, int meter) {
		//dLon = de/(R*Cos(Pi*lat/180))
		double result = meter / (EARTH_RADIUS * PI * Math.cos(latitude) / 180);
		if (longitude < 0)
			result = longitude - result;
		else
			result += longitude;
		
		return (float) result;
	}
	
	public static void printOffset(float lat, float lng, int radius) {
		log.debug("Org: " + lat + ", " + lng);
		float oLat = offsetLatitude(lat, lng, radius);
		float oLng = offsetLongitude(lat, lng, radius);
		log.debug("Offset: " + oLat);
		log.debug(", " + oLng);
		
		log.debug("Distance: " + distanceMeters(lat, lng, oLat, oLng));
		
	}
	
	public static boolean isIntersectsPolygon(float[] lats, float[] lngs, int[] radius, float lat, float lng) {
		boolean intersect = false;
//		double PI = 3.1416;
		
		if (lats == null || lngs == null || lats.length < 2 || lats.length != lngs.length || lats.length != radius.length) {
			return false;
		}
		
		Rectangle2D.Float area = null;
		Line2D.Float line = null;
		
		for (int i=0; i<lats.length - 1 && !intersect; i++) {
			float offsetLat = offsetLatitude(lat, lng, radius[i]);
			float offsetLng = offsetLongitude(lat, lng, radius[i]);
			float width = (offsetLat > lat? offsetLat - lat: lat - offsetLat) * 2;
			float height = (offsetLng > lng? offsetLng - lng: lng - offsetLng) * 2;
			
			area = new Rectangle2D.Float(offsetLat, offsetLng, width, height);

			line = new Line2D.Float(lats[i], lngs[i], lats[i+1], lngs[i+1]);
			intersect = line.intersects(area);
			
//			System.out.println("line: [" + line.getX1() + "," + line.getY1() + "," + line.getX2() + "," + line.getY2() + 
//				"], area: [" + area.getX() + "," + area.getY() + "," + (area.getX() + area.getWidth()) + "," + (area.getY() + area.getHeight()) + "]");
		}

		return intersect;
	}
	
	public static boolean isIntersectsCircle(float[] lats, float[] lngs, int[] radius, float lat, float lng) {
		boolean intersect = false;
		
		if (lats == null || lngs == null || lats.length < 1 || lats.length != lngs.length || lats.length != radius.length) {
			return false;
		}

		for (int i=0; i<lats.length && !intersect; i++) {
			float distance = distanceMeters(lats[i], lngs[i], lat, lng);

			if (distance <= radius[i])
				intersect = true;
		}
		
		return intersect;
	}
	
	public static String getAddress(float latitude, float longitude)
	{
		try {
			Geocoder geocoder = new Geocoder();
			GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setLocation(new LatLng(String.valueOf(latitude), String.valueOf(longitude))).setLanguage("en").getGeocoderRequest();
			GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);	
			if (geocoderResponse.getStatus() == GeocoderStatus.OK) {
				for (GeocoderResult result : geocoderResponse.getResults()) {
					return result.getFormattedAddress();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
} // end class
