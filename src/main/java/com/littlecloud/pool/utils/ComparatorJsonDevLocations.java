package com.littlecloud.pool.utils;

import java.util.Comparator;

import com.littlecloud.control.json.model.Json_Device_Locations;

public class ComparatorJsonDevLocations implements Comparator<Object>{

	@Override
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		Json_Device_Locations loc1 = (Json_Device_Locations)o1;
		Json_Device_Locations loc2 = (Json_Device_Locations)o2;
		return loc1.getTimestamp().compareTo(loc2.getTimestamp());
	}

}
