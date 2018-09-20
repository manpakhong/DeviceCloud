package com.littlecloud.pool.utils;

import java.util.Comparator;

import com.littlecloud.pool.object.LocationList;

public class ComparatorLocationList implements Comparator<Object>{

	public ComparatorLocationList(){
		
	}
	@Override
	public int compare(Object o1, Object o2) {
		int flag = 0;
		LocationList ll1 = (LocationList)o1;
		LocationList ll2 = (LocationList)o2;
		if(ll1.getTimestamp() != null && ll2.getTimestamp() != null)
			return ll1.getTimestamp().compareTo(ll2.getTimestamp());		
		else if(ll1.getTimestamp() != null && ll2.getTimestamp() == null)
			return 1;
		else if(ll1.getTimestamp() == null && ll2.getTimestamp() != null)
			return -1;
		else
			return 0;		
	}

}
