package com.littlecloud.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.DeviceDailyDpiUsagesDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.report.DeviceDailyDpiUsages;
import com.littlecloud.control.entity.report.DeviceMonthlyDpiUsages;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.utils.CalendarUtils;

public class DeviceDpiDailyUsagesMgr {
	private static final Logger log = Logger.getLogger(DeviceDpiDailyUsagesMgr.class);
	private String orgId;
	private DeviceDailyDpiUsagesDAO deviceDpiDailyUsagesDao;
	public DeviceDpiDailyUsagesMgr(String orgId){
		this.orgId = orgId;
		init();
	}
	private void init(){
		try{
			deviceDpiDailyUsagesDao = new DeviceDailyDpiUsagesDAO(orgId);
		}catch (Exception e){
			log.error("DeviceDpiDailyUsagesMgr.init() - Exception:", e);
		}
	}
	
	public int doDeviceDpiDailyUsagesConsolidation(Calendar calFrom, Calendar calTo){
		int noOfRecordsDone = 0;
		try{
			noOfRecordsDone = doDeviceDpiDailyUsagesConsolidation(calFrom, calTo, null);
		} catch (Exception e){
			log.error("DeviceDpiDailyUsagesMgr.doDeviceDailyUsagesConsolidation() - Exception: " + orgId, e);
		}
		return noOfRecordsDone;
	}
	
	public boolean saveDeviceDpiDailyUsagesList(List<DeviceDailyDpiUsages> deviceDailyDpiUsageList) {
		boolean areSaved = false;
		try{
			areSaved = deviceDpiDailyUsagesDao.saveDeviceDpiDailyUsagesList(deviceDailyDpiUsageList);
		} catch (Exception e){
			log.error("DeviceDpiDailyUsagesMgr.saveDeviceDpiDailyUsagesList() - Exception: ", e);
		}
		return areSaved;
	}
	
	public int doDeviceDpiDailyUsagesConsolidation(Calendar calFrom, Calendar calTo, List<Networks> networkList){
		int noOfRecordsDone = 0;
		try{
			DeviceDpiUsagesMgr deviceDpiUsagesMgr = new DeviceDpiUsagesMgr(orgId);			

			if (networkList == null){
				NetworksDAO networksDao = new NetworksDAO(orgId);
				networkList = networksDao.getAllNetworks();
			}
			
			if (networkList != null && networkList.size() > 0){
				for (Networks network: networkList){
					String timezone = network.getTimezone();
					String timezoneId = DateUtils.getTimezoneFromId(Integer.valueOf(timezone));
					TimeZone tz = TimeZone.getTimeZone(timezoneId);										
					
					Calendar calFromLocal = CalendarUtils.convertCalendar2TimeZoneCalendar(calFrom, tz);
					Calendar calToLocal = CalendarUtils.convertCalendar2TimeZoneCalendar(calTo, tz);
					List<DeviceDailyDpiUsages> deviceDailyDpiUsagesList = deviceDpiUsagesMgr.getDeviceDpiDailyUsagesRecords(network.getId(), calFromLocal, calToLocal);
					if(deviceDailyDpiUsagesList != null  && deviceDailyDpiUsagesList.size() > 0){
						List<DeviceDailyDpiUsages> deviceDailyDpiUsageList = new ArrayList<DeviceDailyDpiUsages>();
						
						for(DeviceDailyDpiUsages dpi : deviceDailyDpiUsagesList){							
							List<DeviceDailyDpiUsages> dailyDpiUsageList = 
									findByNetworkIdDeviceIdUnixtimeAndProtocolAndCalendarRange(dpi.getNetwork_id(), dpi.getDevice_id(), dpi.getService(),calFromLocal, calToLocal);
							
							DeviceDailyDpiUsages dailyDpiUsage = null;
							if (dailyDpiUsageList != null && dailyDpiUsageList.size() > 0){
								dailyDpiUsage = dailyDpiUsageList.get(0);
							}
							
							if( dailyDpiUsage != null ){
								dailyDpiUsage.setDevice_id(dpi.getDevice_id());
								dailyDpiUsage.setNetwork_id(dpi.getNetwork_id());
								dailyDpiUsage.setService(dpi.getService());
								dailyDpiUsage.setSize(dpi.getSize());
								dailyDpiUsage.setUnixtime(CalendarUtils.changeDate2Unixtime(calFromLocal.getTime()));
								dailyDpiUsage.replace();
								deviceDailyDpiUsageList.add(dailyDpiUsage);
							}
							else{
								dailyDpiUsage = new DeviceDailyDpiUsages();
								dailyDpiUsage.setDevice_id(dpi.getDevice_id());
								dailyDpiUsage.setNetwork_id(dpi.getNetwork_id());
								dailyDpiUsage.setService(dpi.getService());
								dailyDpiUsage.setSize(dpi.getSize());
								dailyDpiUsage.setUnixtime(CalendarUtils.changeDate2Unixtime(calFromLocal.getTime()));
								dailyDpiUsage.create();
								deviceDailyDpiUsageList.add(dailyDpiUsage);
							}
						}
						
						if (deviceDailyDpiUsageList != null && deviceDailyDpiUsageList.size() > 0){
							boolean areSaved = saveDeviceDpiDailyUsagesList(deviceDailyDpiUsageList);
							if (!areSaved){
								if (log.isDebugEnabled()){
									log.debugf("DeviceDpiDailyUsagesMgr.doDeviceDailyUsagesConsolidation()- areSaved: %s, orgId:%s, networkId:%s",areSaved, orgId, network.getId());
								}
							} else {
								noOfRecordsDone = deviceDailyDpiUsageList.size();
							}
						}
					}			
				}
			}
		} catch (Exception e){
			log.error("DeviceDpiDailyUsagesMgr.doDeviceDailyUsagesConsolidation() - Exception: " + orgId, e);
		}
		return noOfRecordsDone;
	}
	
	public List<DeviceMonthlyDpiUsages> getDeviceDpiMonthlyUsagesRecords(Integer networkId, Calendar calFrom, Calendar calTo){
		List<DeviceMonthlyDpiUsages> deviceMonthlyDpiUsageList = null;
		try{
			deviceMonthlyDpiUsageList = deviceDpiDailyUsagesDao.getDeviceDpiMonthlyUsagesRecords(networkId, calFrom, calTo);
		}catch (Exception e){
			log.error("DeviceDpiDailyUsagesMgr.getDeviceDpiMonthlyUsagesRecords()", e);
		}
		return deviceMonthlyDpiUsageList;
	}
	
	public List<DeviceDailyDpiUsages> findByNetworkIdDeviceIdUnixtimeAndProtocolAndCalendarRange(
			Integer networkId,Integer deviceId,String protocol, Calendar calFrom, Calendar calTo) {
		List<DeviceDailyDpiUsages> deviceDailyDpiUsageList = null;
		try{
			deviceDailyDpiUsageList = deviceDpiDailyUsagesDao.findByNetworkIdDeviceIdProtocolAndCalendarRange(networkId, deviceId, protocol, calFrom, calTo);
		} catch (Exception e){
			log.error("DeviceDpiDailyUsagesMgr.findByNetworkIdDeviceIdUnixtimeAndProtocolAndCalendarRange()", e);
		}
		return deviceDailyDpiUsageList;
	}

	
}
