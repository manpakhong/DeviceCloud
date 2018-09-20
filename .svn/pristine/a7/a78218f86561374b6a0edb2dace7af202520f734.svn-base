package com.littlecloud.services;

import java.util.Calendar;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.DeviceDpiUsagesDAO;
import com.littlecloud.control.entity.report.DeviceDailyDpiUsages;

public class DeviceDpiUsagesMgr {
	private static final Logger log = Logger.getLogger(DeviceDpiUsagesMgr.class);
	private String orgId;
	private DeviceDpiUsagesDAO deviceDpiUsagesDao;
	public DeviceDpiUsagesMgr(String orgId){
		this.orgId = orgId;
		init();
	}
	private void init(){
		try{
			deviceDpiUsagesDao = new DeviceDpiUsagesDAO(orgId);
		} catch (Exception e){
			log.error("DeviceDpiUsagesMgr.init() - Exception:", e);
		}
	}
	
	public List<DeviceDailyDpiUsages> getDeviceDpiDailyUsagesRecords(int networkId, Calendar calFrom, Calendar calTo){
		List<DeviceDailyDpiUsages> deviceDailyDpiUsageList = null;
		try{
			deviceDailyDpiUsageList = deviceDpiUsagesDao.getDeviceDpiDailyUsagesRecords(networkId, calFrom, calTo);
		} catch (Exception e){
			log.error("DeviceDpiUsagesMgr.getDeviceDpiDailyUsagesRecords() - Exception:", e);
		}
		return deviceDailyDpiUsageList;
	}
	
}
