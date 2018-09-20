package com.littlecloud.services;

import java.util.Calendar;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.DeviceUsagesDAO;
import com.littlecloud.control.entity.report.DeviceUsages;

public class DeviceUsagesMgr {
	private static final Logger log = Logger.getLogger(DeviceUsagesMgr.class);
	private String orgId;
	private DeviceUsagesDAO deviceUsagesDao;
	public DeviceUsagesMgr(String orgId){
		this.orgId = orgId;
		init();
	}
	private void init(){
		try{
			deviceUsagesDao = new DeviceUsagesDAO(orgId);
		} catch (Exception e){
			log.error("DeviceUsagesMgr.init() - ", e);
		}
	}
	
	public List<DeviceUsages> getRecordsByDeviceIdAndWanIdAndTimeV2(Integer networkId, Integer deviceId, Integer wanId, Integer fromUnixTime,Integer toUnixTime){
		List<DeviceUsages> deviceUsageList = null;
		try{
			// !!!! the method fromUnixTime and toUnixTime is reversed !!! - don't want to change Junyuan code at this moment
			deviceUsageList = deviceUsagesDao.getRecordsByDeviceIdAndWanIdAndTimeV2(networkId, deviceId, wanId, toUnixTime, fromUnixTime);
		} catch (Exception e){
			log.error("DeviceUsagesMgr.getRecordsByDeviceIdAndWanIdAndTimeV2");
		}
		
		return deviceUsageList;
	}
	
	public List<Integer> getDistinctWanList(Integer netId, Integer deviceId){
		List<Integer> distinctWanIdList = null;
		try{
			distinctWanIdList = deviceUsagesDao.getDistinctWanIds(netId, deviceId);
		} catch (Exception e){
			log.error("DeviceMonthlyUsagesMgr.init() - ", e);
		}
		return distinctWanIdList;
	}
	
	public List<DeviceUsages> getDailyUsagesRecords(Integer networkId, Calendar calFrom, Calendar calTo){
		List<DeviceUsages> dailyUsageList = null;
		try{
			dailyUsageList = deviceUsagesDao.getDailyUsagesRecords(networkId, calFrom, calTo);
		} catch (Exception e){
			log.error("DeviceUsagesMgr.getDailyUsagesRecords() - ", e);
		}
		return dailyUsageList;
	}
	
}
