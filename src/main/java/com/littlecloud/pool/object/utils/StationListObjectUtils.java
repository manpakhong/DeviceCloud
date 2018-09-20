package com.littlecloud.pool.object.utils;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.pool.object.StationListObject;

public class StationListObjectUtils {
	private static final Logger log = Logger.getLogger(StationListObjectUtils.class);
	public static StationListObject getStationListObject(Integer ianaId, String sn){
		StationListObject stationListObject = null;
		try{
			stationListObject = new StationListObject();
			stationListObject.setIana_id(ianaId);
			stationListObject.setSn(sn);
			stationListObject = ACUtil.<StationListObject>getPoolObjectBySn(stationListObject, StationListObject.class);
		} catch (Exception e){
			log.error("StationListObjectUtils.getStationListObject() - Exception:", e);
		}
		return stationListObject;
	}
	public static boolean removeStationListObject(StationListObject stationListObject){
		boolean isRemoved = false;
		try{
			ACUtil.removePoolObjectBySn(stationListObject, StationListObject.class);
			isRemoved = true;
		} catch (Exception e){
			log.error("StationListObjectUtils.removeStationListObject() - Exception:", e);
		}
		return isRemoved;
	}
	public static boolean updateStationListObject(StationListObject stationListObject){
		boolean isUpdated = false;
		try{
			ACUtil.cachePoolObjectBySn(stationListObject, StationListObject.class);
			isUpdated = true;
		} catch (Exception e){
			log.error("StationListObjectUtils.updateStationListObject() - Exception:", e);
		}
		return isUpdated;
	}
}
