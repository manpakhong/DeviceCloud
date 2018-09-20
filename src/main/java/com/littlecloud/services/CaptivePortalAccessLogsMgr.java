package com.littlecloud.services;

import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.CaptivePortalAccessLogsDAO;
import com.littlecloud.control.dao.criteria.CaptivePortalAccessLogCriteria;
import com.littlecloud.control.entity.CaptivePortalAccessLog;

public class CaptivePortalAccessLogsMgr {
	private static final Logger log = Logger.getLogger(CaptivePortalAccessLogsMgr.class);
	private CaptivePortalAccessLogsDAO captivePortalAccessLogsDao;
	private String orgId;
	public CaptivePortalAccessLogsMgr(String orgId){
		if (orgId != null){
			this.orgId = orgId;
			init();
		}
	}
	private void init(){
		try{
			captivePortalAccessLogsDao = new CaptivePortalAccessLogsDAO(this.orgId);
		} catch (Exception e){
			log.error("CaptivePortalAccessLogsMgr.init() - ", e); 
		}
	}
	public List<CaptivePortalAccessLog> getCaptivePortalAccessLogList(CaptivePortalAccessLogCriteria cpalCriteria){
		List<CaptivePortalAccessLog> captivePortalAccessLogList = null;
		try{
			captivePortalAccessLogList = captivePortalAccessLogsDao.getCaptivePortalAccessLogList(cpalCriteria);
		} catch (Exception e){
			log.error("CaptivePortalAccessLogsMgr.getCaptivePortalAccessLogList() - ", e); 
		}
		return captivePortalAccessLogList;
	}
	
}
