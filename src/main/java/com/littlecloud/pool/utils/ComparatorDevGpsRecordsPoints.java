package com.littlecloud.pool.utils;

import java.util.Comparator;

import com.littlecloud.control.entity.report.DeviceGpsRecordsPoints;


public class ComparatorDevGpsRecordsPoints implements Comparator<Object>{

	@Override
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		DeviceGpsRecordsPoints loc1 = (DeviceGpsRecordsPoints)o1;
		DeviceGpsRecordsPoints loc2 = (DeviceGpsRecordsPoints)o2;
		return loc1.getTimestamp().compareTo(loc2.getTimestamp());
	}

}