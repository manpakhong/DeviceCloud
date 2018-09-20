package com.littlecloud.services;

import java.util.Calendar;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.CaptivePortalActivitiesDAO;
import com.littlecloud.control.dao.criteria.CaptivePortalActivitiesCriteria;
import com.littlecloud.control.entity.CaptivePortalActivity;
import com.littlecloud.control.entity.CaptivePortalDailyUsage;
import com.littlecloud.control.entity.CaptivePortalDailyUserUsage;

public class CaptivePortalActivitiesMgr {
	private static final Logger log = Logger.getLogger(CaptivePortalActivitiesMgr.class);
	private CaptivePortalActivitiesDAO captivePortalActivitiesDao;
	private String orgId;
	public CaptivePortalActivitiesMgr(String orgId){
		if (orgId != null && !orgId.isEmpty()){
			this.orgId = orgId;
			init();
		}
	}
	
	public void init(){
		try{
			captivePortalActivitiesDao = new CaptivePortalActivitiesDAO(orgId);
		} catch (Exception e){
			log.error("CAPMON20140917 - CaptivePortalActivitiesMgr.init() - ", e);
		}
	}
	
	public List<CaptivePortalActivity> getCaptivePortalActivityList(CaptivePortalActivitiesCriteria cpaCriteria){
		List<CaptivePortalActivity> captivePortalActivityList = null;
		try{
			captivePortalActivityList = captivePortalActivitiesDao.getCaptivePortalActivities(cpaCriteria);
		} catch (Exception e){
			log.error("CAPMON20140917 - CaptivePortalActivitiesMgr.getCaptivePortalActivityList() - ", e);
		}
		return captivePortalActivityList;
	}

	public List<CaptivePortalDailyUserUsage> generateCaptivePortalDailyUserUsageReportsList(Calendar calFrom, Calendar calTo){
		List<CaptivePortalDailyUserUsage> captivePortalDailyUserUsageReportList = null;
		try{
			if (calFrom != null && calTo != null){
				captivePortalDailyUserUsageReportList = captivePortalActivitiesDao.generateCaptivePortalDailyUserUsagesList(calFrom, calTo, null);
			}
		} catch (Exception e){
			log.error("CAPMON20140917 - CaptivePortalActivitiesMgr.generateCaptivePortalDailyUserUsagesList() - ", e);
		}
		return captivePortalDailyUserUsageReportList;
	}
	
	public List<CaptivePortalDailyUserUsage> generateCaptivePortalDailyUserUsageReportsList(Calendar calFrom, Calendar calTo, Integer networkId){
		List<CaptivePortalDailyUserUsage> captivePortalDailyUserUsageReportList = null;
		try{
			if (calFrom != null && calTo != null){
				captivePortalDailyUserUsageReportList = captivePortalActivitiesDao.generateCaptivePortalDailyUserUsagesList(calFrom, calTo, networkId);
			}
		} catch (Exception e){
			log.error("CAPMON20140917 - CaptivePortalActivitiesMgr.generateCaptivePortalDailyUserUsagesList() - ", e);
		}
		return captivePortalDailyUserUsageReportList;
	}
	
	public List<CaptivePortalDailyUsage> generateCaptivePortalDailyUsageReportsList(Calendar calFrom, Calendar calTo){
		List<CaptivePortalDailyUsage> captivePortalDailyUsageReportList = null;
		try{
			if (calFrom != null && calTo != null){
				captivePortalDailyUsageReportList = captivePortalActivitiesDao.generateCaptivePortalDailyUsagesList(calFrom, calTo, null);
			}
		} catch (Exception e){
			log.error("CAPMON20140917 - CaptivePortalActivitiesMgr.generateCaptivePortalDailyUsagesList() - ", e);
		}
		return captivePortalDailyUsageReportList;
	}
	
	public List<CaptivePortalDailyUsage> generateCaptivePortalDailyUsageReportsList(Calendar calFrom, Calendar calTo, Integer networkId){
		List<CaptivePortalDailyUsage> captivePortalDailyUsageReportList = null;
		try{
			if (calFrom != null && calTo != null){
				captivePortalDailyUsageReportList = captivePortalActivitiesDao.generateCaptivePortalDailyUsagesList(calFrom, calTo, networkId);
			}
		} catch (Exception e){
			log.error("CAPMON20140917 - CaptivePortalActivitiesMgr.generateCaptivePortalDailyUsagesList() - ", e);
		}
		return captivePortalDailyUsageReportList;
	}

}
