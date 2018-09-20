package com.littlecloud.services;

import java.util.Calendar;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.DeviceSsidUsagesDAO;
import com.littlecloud.control.entity.report.DailyDeviceSsidUsages;
import com.littlecloud.pool.model.DailyDevSsidUsageResult;

public class DeviceSsidUsagesMgr {
	private static final Logger log = Logger.getLogger(DeviceSsidUsagesMgr.class);
	private String orgId;
	private DeviceSsidUsagesDAO deviceSsidUsagesDao;
	public DeviceSsidUsagesMgr(String orgId){
		this.orgId = orgId;
		init();
	}
	private void init(){
		try{
			deviceSsidUsagesDao = new DeviceSsidUsagesDAO(orgId);
		} catch (Exception e){
			log.error("DeviceSsidUsagesMgr.init() - Exception: ", e);
		}
	}
	
	public List<DailyDeviceSsidUsages> getEssidEncryptionTotalTxRxWithTimePeriod(Integer networkId, Calendar calFrom, Calendar calTo){
		List<DailyDeviceSsidUsages> dailyDeviceSsidUsageList = null;
		try{
			dailyDeviceSsidUsageList = deviceSsidUsagesDao.getEssidEncryptionTotalTxRxWithTimePeriod(networkId, calFrom, calTo);
		} catch (Exception e){
			log.error("DeviceSsidUsagesMgr.getEssidEncryptionTotalTxRxWithTimePeriod()",e);
		}
		
		return dailyDeviceSsidUsageList;
	}
	
}
