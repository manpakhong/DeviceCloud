package com.littlecloud.pool.object.utils;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.pool.object.StationUsageObject;

public class StationUsageObjectUtils {
	private static final Logger log = Logger.getLogger(StationUsageObjectUtils.class);
	
	public static StationUsageObject getStationUsageObject(Integer ianaId, String sn){
		StationUsageObject stationUsageObject = null;
		try{
			stationUsageObject = new StationUsageObject();
			stationUsageObject.setIana_id(ianaId);
			stationUsageObject.setSn(sn);	
			stationUsageObject = ACUtil.<StationUsageObject>getPoolObjectBySn(stationUsageObject, StationUsageObject.class);
		} catch (Exception e){
			log.error("StationUsageObjectUtils.getStationUsageObject() - Exception:", e);
		}
		return stationUsageObject;
	}
	
	public static boolean updateStationUsageObject(StationUsageObject stationUsageObject){
		boolean isUpdated = false;
		try{
			ACUtil.<StationUsageObject>cachePoolObjectBySn(stationUsageObject, StationUsageObject.class);
			isUpdated = true;
		} catch (Exception e){
			log.error("StationUsageObjectUtils.updateStationUsageObject() - Exception:", e);
		}
		return isUpdated;
	}
}
