package com.littlecloud.pool.utils;

import java.util.Comparator;

import com.littlecloud.control.entity.report.DeviceGpsLocations;
import com.littlecloud.control.json.model.Json_Device_Locations;

public class ComparatorDevGpsLocation implements Comparator<Object>{

	@Override
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		DeviceGpsLocations loc1 = (DeviceGpsLocations)o1;
		DeviceGpsLocations loc2 = (DeviceGpsLocations)o2;
		Integer unixtime1 = loc1.getId().getUnixtime();
		Integer unixtime2 = loc2.getId().getUnixtime();
		return unixtime1.compareTo(unixtime2);
	}

}