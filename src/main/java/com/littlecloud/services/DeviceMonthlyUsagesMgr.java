package com.littlecloud.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.DeviceMonthlyUsagesDAO;
import com.littlecloud.control.entity.report.DailyDeviceUsages;
import com.littlecloud.control.entity.report.DeviceMonthlyDpiUsages;
import com.littlecloud.control.entity.report.DeviceMonthlyUsages;
import com.littlecloud.utils.CalendarUtils;

public class DeviceMonthlyUsagesMgr {
	private static final Logger log = Logger.getLogger(DeviceMonthlyUsagesMgr.class);
	private DeviceMonthlyUsagesDAO deviceMonthlyUsagesDao;
	private String orgId;
	public DeviceMonthlyUsagesMgr(String orgId){
		this.orgId = orgId;
		init();
	}
	private void init(){
		try{
			deviceMonthlyUsagesDao = new DeviceMonthlyUsagesDAO(this.orgId);
		} catch (Exception e){
			log.error("DeviceMonthlyUsagesMgr.init() - ", e);
		}
	}
	
	public List<Integer> getDistinctWanList(Integer netId, Integer deviceId){
		List<Integer> distinctWanIdList = null;
		try{
			distinctWanIdList = deviceMonthlyUsagesDao.getDistinctWanIdsWithDeviceId(netId, deviceId);
		} catch (Exception e){
			log.error("DeviceMonthlyUsagesMgr.init() - ", e);
		}
		return distinctWanIdList;
	}
	
	public List<DeviceMonthlyUsages> getRecordsWithDeviceIdAndWanIdV2(Integer networkId, Integer deviceId, Integer wanId,Integer unixtime){
		List<DeviceMonthlyUsages> uList= null;
		try{
			uList = deviceMonthlyUsagesDao.getRecordsWithDeviceIdAndWanIdV2(networkId, deviceId, wanId, unixtime);
		} catch (Exception e){
			log.error("DeviceMonthlyUsagesMgr.getRecordsWithDeviceIdAndWanIdV2() - ", e);
		}
		
		return uList;
	}
	
	public boolean saveDeviceMonthlyUsagesList(List<DeviceMonthlyUsages> deviceMonthlyUsageList){
		boolean isSaved = false;
		try{
			isSaved = deviceMonthlyUsagesDao.saveDeviceMonthlyUsagesList(deviceMonthlyUsageList);
		} catch (Exception e){
			log.error("DeviceMonthlyUsagesMgr.saveDeviceMonthlyUsagesList() - ", e);
		}
		return isSaved;
	}
	
	public DeviceMonthlyUsages findByNetworkIdDeviceIdTimeAndWanId(Integer network_id, Integer device_id, Date datetime, Integer wan_id ){
		DeviceMonthlyUsages deviceMonthlyUsage = null;
		try{
			deviceMonthlyUsage = deviceMonthlyUsagesDao.findByNetworkIdDeviceIdTimeAndWanId(network_id, device_id, datetime, wan_id);
		} catch (Exception e){
			log.error("DeviceMonthlyUsagesMgr.findByNetworkIdDeviceIdTimeAndWanId() - Exception: ", e);
		}
		return deviceMonthlyUsage;
	}
	
	public int doDeviceMonthlyUsagesConsolidation(Calendar calFrom, Calendar calTo){
		int noOfRecordsDone = 0;
		try{

			DeviceDailyUsagesMgr deviceDailyUsagesMgr = new DeviceDailyUsagesMgr(orgId);
			if (log.isInfoEnabled()){
				log.infof("DeviceMonthlyUsagesMgr.doDeviceMonthlyUsagesConsolidation() - orgId: %s", orgId);
			}
						
			List<DailyDeviceUsages> deviceMonthlyRecordList = deviceDailyUsagesMgr.getDeviceMonthlyRecords(calFrom, calTo);
			if (deviceMonthlyRecordList != null && deviceMonthlyRecordList.size() > 0){
				DeviceMonthlyUsagesMgr deviceMonthlyUsagesMgr = new DeviceMonthlyUsagesMgr(orgId);
				List<DeviceMonthlyUsages> deviceMonthlyUsageList = new ArrayList<DeviceMonthlyUsages>();
				for(DailyDeviceUsages monthlyUsageLoop : deviceMonthlyRecordList){
					DeviceMonthlyUsages monthlyUsage = new DeviceMonthlyUsages();
					monthlyUsage.setDevice_id(monthlyUsageLoop.getDeviceId());
					monthlyUsage.setNetwork_id(monthlyUsageLoop.getNetworkId());
					monthlyUsage.setWan_id(monthlyUsageLoop.getWan_id());
					monthlyUsage.setWan_name(monthlyUsageLoop.getWan_name());
					monthlyUsage.setRx(monthlyUsageLoop.getRx());
					monthlyUsage.setTx(monthlyUsageLoop.getTx());
					monthlyUsage.setDatetime(monthlyUsageLoop.getDatetime());
					monthlyUsage.setUnixtime(monthlyUsageLoop.getUnixtime());
					
					DeviceMonthlyUsages preDevMonthlyUsage = findByNetworkIdDeviceIdTimeAndWanId(monthlyUsage.getNetwork_id(), monthlyUsage.getDevice_id(), monthlyUsage.getDatetime(), monthlyUsage.getWan_id());
					if( preDevMonthlyUsage != null ) {
						monthlyUsage.setId(preDevMonthlyUsage.getId());
					}
					monthlyUsage.replace();
					deviceMonthlyUsageList.add(monthlyUsage);
				}
				if (deviceMonthlyUsageList.size() > 0){
					boolean areSaved = deviceMonthlyUsagesMgr.saveDeviceMonthlyUsagesList(deviceMonthlyUsageList);
					noOfRecordsDone = deviceMonthlyUsageList.size();
					if (!areSaved){
						log.warnf("DeviceMonthlyUsagesMgr.doDeviceMonthlyUsagesConsolidation() - areSaved: %s", areSaved);
					}
				}
				
			}
		} catch (Exception e){
			log.error("DeviceMonthlyUsagesMgr.doDeviceMonthlyUsagesConsolidation() - Exception: ", e);
		}
		return noOfRecordsDone;
	}
	
}
