package com.littlecloud.helpers;

import java.util.Calendar;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.pool.object.StationList;
import com.littlecloud.pool.object.StationListObject;
import com.littlecloud.pool.object.utils.StationListObjectUtils;
import com.littlecloud.utils.CalendarUtils;

public class ClientSsidUsagesHelper {
	private static final Logger log = Logger.getLogger(ClientUsagesHelper.class);
	private Integer ianaId;
	private String sn;
	private Integer networkId;
	private StationListObject stationListObject;

	
	public ClientSsidUsagesHelper(Integer ianaId, String sn, Integer networkId){
		this.ianaId = ianaId;
		this.sn = sn;
		this.networkId = networkId;
		init();
	}
	
	private void init(){
		try{
			getStationListObjectFromCacheAndDoAllChecking();
		} catch (Exception e){
			log.error("ClientSsidUsagesHelper.init() - Exception:", e);
		}
	}

	
	private void getStationListObjectFromCacheAndDoAllChecking(){
		try{
			getStationListObjectFromCache();
			removeStationListObjectFromCacheIfUnhealthy();
			checkAndFixStationListObjectTimestampProblem();		
		} catch (Exception e){
			log.error("ClientSsidUsagesHelper.getStationListObjectFromCacheAndDoAllChecking() - Exception:", e);
		}
	}
	
	private void getStationListObjectFromCache(){
		stationListObject = StationListObjectUtils.getStationListObject(ianaId, sn);
		if (stationListObject != null){
			stationListObject.setNetwork_id(networkId);
		}
	}
	private void removeStationListObjectFromCacheIfUnhealthy(){
		if( stationListObject.getNetwork_id() == null || stationListObject.getDevice_id() == null ) {
			boolean isRemoved = StationListObjectUtils.removeStationListObject(stationListObject);
			if (!isRemoved){
				log.warnf("ClientSsidUsagesHelper.removeStationListObjectFromCacheIfUnhealthy() - StationListObject cannot be removed! stationListObject: %s", stationListObject);
			}
		}
	}
	private void checkAndFixStationListObjectTimestampProblem(){
		if (stationListObject != null && stationListObject.getStation_list() != null){
			if( stationListObject.getNetwork_id() != null && stationListObject.getDevice_id() != null ) {
				if(stationListObject.getTimestamp() == null) {
					if (stationListObject.getCreateTime() != null){
						int createTime = (int) (stationListObject.getCreateTime() / 1000);
						stationListObject.setTimestamp(createTime);
						log.warnf("StartPersistReportFromCache: Persist clientUsages abnormal Station List with null timestamp for sn: %s and replace with createTime: %s", sn, createTime);
					} else {
						Calendar utcCalToday = CalendarUtils.getUtcCalendarToday();
						int timestamp = CalendarUtils.changeDate2Unixtime(utcCalToday.getTime());
						stationListObject.setTimestamp(timestamp);
					}
				}
			} 
		} // end if (stationListObject != null)
	}
	public StationListObject getStationListObject(){
		return stationListObject;
	}

}
