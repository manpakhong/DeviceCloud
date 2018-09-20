package com.littlecloud.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.DailyDeviceUsagesDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.report.DailyDeviceUsages;
import com.littlecloud.control.entity.report.DeviceUsages;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.utils.PropertyLoader;
import com.littlecloud.utils.CalendarUtils;
import com.littlecloud.utils.CommonUtils;

// mapping to DailyDeviceUsagesDAO - 
// it is weird that DAO and physical table naming not consistance - daily_device_usages, device_monthly_usages, device_usages 
public class DeviceDailyUsagesMgr {
	private static final Logger log = Logger.getLogger(DeviceDailyUsagesMgr.class);
	private String orgId;
	private DailyDeviceUsagesDAO deviceDailyUsageDao; 
	private boolean isDailyDeviceUsagesReadOnly;
	public DeviceDailyUsagesMgr(String orgId){
		this.orgId = orgId;
		this.isDailyDeviceUsagesReadOnly = false;
		init();
	}
	public DeviceDailyUsagesMgr(String orgId, boolean isReadOnly){
		this.orgId = orgId;
		this.isDailyDeviceUsagesReadOnly = isReadOnly;
		init();
	}
	public void init(){
		try{
			deviceDailyUsageDao =new DailyDeviceUsagesDAO(this.orgId, isDailyDeviceUsagesReadOnly);
		} catch (Exception e){
			log.error("DeviceDailyUsagesMgr.init() - ", e);
		}
	}
	
	public List<Integer> getDistinctWanIdsWithDeviceId(Integer networkId,Integer deviceId){
		List<Integer> wanList = null;
		try{
			wanList = deviceDailyUsageDao.getDistinctWanIdsWithDeviceId(networkId, deviceId);
		} catch (Exception e){
			log.error("DeviceDailyUsagesMgr.getDistinctWanIdsWithDeviceId() - ", e);
		}
		return wanList;
	}
	
	public List<DailyDeviceUsages> getRecordsByDeviceIdAndWanIdV2(Integer networkId, Integer deviceId, Integer wanId, Integer unixtime){
		List<DailyDeviceUsages> deviceDailyUsageList = null;
		try{
			deviceDailyUsageList = deviceDailyUsageDao.getRecordsByDeviceIdAndWanIdV2(networkId, deviceId, wanId, unixtime);
		} catch (Exception e){
			log.error("DeviceDailyUsagesMgr.getRecordsByDeviceIdAndWanIdV2() - ", e);
		}
		return deviceDailyUsageList;
	}
	
	public boolean saveDeviceDailyUsagesList(List<DailyDeviceUsages> deviceDailyUsageList){
		boolean isSaved = false;
		try{
			isSaved = deviceDailyUsageDao.saveDeviceDailyUsagesList(deviceDailyUsageList);
		} catch (Exception e){
			log.error("DeviceDailyUsagesMgr.saveDeviceDailyUsagesList() - ", e);
		}
		return isSaved;
	}
	
	public int doDeviceDailyUsagesConsolidation(Calendar calFrom, Calendar calTo){
		int noOfRecordDone = 0;
		try{
			noOfRecordDone = doDeviceDailyUsagesConsolidation(calFrom, calTo, null);
		} catch (Exception e){
			log.error("DeviceDailyUsagesMgr.doDeviceDailyUsagesConsolidation() - Exception: " + orgId, e);
		}
		return noOfRecordDone;
	}
	
	public int doDeviceDailyUsagesConsolidation(Calendar calFrom, Calendar calTo, List<Networks> networkList){
		DeviceUsagesMgr deviceUsagesMgr = new DeviceUsagesMgr(orgId);
		NetworksDAO networksDAO = null;
		int noOfRecordDone = 0;

		String batchId = CommonUtils.genOrgTimestampString(orgId);
		if (log.isInfoEnabled()){
			log.infof("[%s] deviceDailyUsagesThread working on: orgId = %s", batchId, orgId);
		}
		try {
			List<Networks> networkIds = networkList;
			if (networkList == null){
				networksDAO = new NetworksDAO(orgId,true);
				networkIds = networksDAO.getAllNetworks();
			}
			if( networkIds != null ) {
				for( Networks network : networkIds ) {
					if( network != null ) {
						TimeZone tz = TimeZone.getTimeZone(DateUtils.getTimezoneFromId(Integer.parseInt(network.getTimezone())));
						// network calendar
						Calendar calNetTzFrom = CalendarUtils.convertCalendar2TimeZoneCalendar(calFrom, tz);
						String localStartStr = CalendarUtils.convertDate2SimpleDateFormatString(calNetTzFrom.getTime(), tz);
						
						// network calendar
						Calendar calNetTzTo = CalendarUtils.convertCalendar2TimeZoneCalendar(calTo, tz);
						String localToStr = CalendarUtils.convertDate2SimpleDateFormatString(calNetTzTo.getTime(), tz);
						if (log.isInfoEnabled()){
							log.infof("[%s] DeviceDailyUsagesMgr.doDeviceDailyUsagesConsolidation working on: orgId = %s, network = %s, start = %s, end = %s", batchId, orgId, network.getId(), localStartStr, localToStr);
						}
						
						List<DailyDeviceUsages> deviceDailyUsageList = null;
						List<DeviceUsages> dailyRecordList = deviceUsagesMgr.getDailyUsagesRecords(network.getId(), calNetTzFrom, calNetTzTo);
						
						if (dailyRecordList != null && dailyRecordList.size() > 0){
							deviceDailyUsageList = new ArrayList<DailyDeviceUsages>();
							for( DeviceUsages dailyUsage : dailyRecordList ) {
								DailyDeviceUsages daily_usage = new DailyDeviceUsages();
								daily_usage.setUnixtime((int)(dailyUsage.getDatetime().getTime()/1000));
								daily_usage.setNetworkId(dailyUsage.getNetworkId());
								daily_usage.setDeviceId(dailyUsage.getDeviceId());
								daily_usage.setWan_id(dailyUsage.getWan_id());
								daily_usage.setWan_name(dailyUsage.getWan_name());
								daily_usage.setRx(dailyUsage.getRx());
								daily_usage.setTx(dailyUsage.getTx());
								daily_usage.setDatetime(dailyUsage.getDatetime());
								DailyDeviceUsages preDailyUsage = deviceDailyUsageDao.findByNetworkIdDeviceIdTimeAndWanId(daily_usage.getNetworkId(), daily_usage.getDeviceId(), daily_usage.getId().getUnixtime(), daily_usage.getWan_id());
								if( preDailyUsage != null ) {
									daily_usage.setId(preDailyUsage.getId());
								}
								daily_usage.replace();
								deviceDailyUsageList.add(daily_usage);
								noOfRecordDone ++;
							}
						}
						if (dailyRecordList != null){
							boolean isDone = saveDeviceDailyUsagesList(deviceDailyUsageList);
							if (!isDone){
								log.warnf("DeviceDailyUsagesMgr.doDeviceDailyUsagesConsolidation() - isDone:%s, orgId:%s",isDone, orgId );
							}
						}
					}
				}

			}
		} catch(Exception e) {
			log.error("["+batchId+"] DeviceDailyUsagesMgr.doDeviceDailyUsagesConsolidation() sql error " + orgId, e);
		}
		return noOfRecordDone;
	}
	
	public List<DailyDeviceUsages> getDeviceMonthlyRecords(Calendar calFrom, Calendar calTo) {
		List<DailyDeviceUsages> dailyDeviceUsageList = null;
		try{
			dailyDeviceUsageList = deviceDailyUsageDao.getDeviceMonthlyRecords(calFrom, calTo);
		}catch (Exception e){
			log.error("DeviceDailyUsagesMgr.getDeviceMonthlyRecords() - Exception: ", e);
		}
		return dailyDeviceUsageList;
	}
	
}
