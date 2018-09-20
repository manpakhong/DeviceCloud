package com.littlecloud.services;

import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.branch.HouseKeepLogsDAO;
import com.littlecloud.control.entity.branch.HouseKeepLogs;

public class HouseKeepLogsMgr {
	protected static final Logger log = Logger.getLogger(HouseKeepLogsMgr.class);
	private HouseKeepLogsDAO houseKeepLogsDao;
	public final static Integer HOUSE_KEEP_LOG_LIMIT = 5;
	public HouseKeepLogsMgr(){
		init();
	}
	
	private void init(){
		try{
			houseKeepLogsDao = new HouseKeepLogsDAO();
		} catch (Exception e){
			log.error("HouseKeepLogsMgr.init() - ", e);
		}
	}
	public List<HouseKeepLogs> getHouseKeepLogs(String orgId, String tableName){
		List<HouseKeepLogs> houseKeepLogList = null;
		try{
			houseKeepLogList = houseKeepLogsDao.getHouseKeepLogs(orgId, tableName, HOUSE_KEEP_LOG_LIMIT);
		} catch (Exception e){
			log.error("HouseKeepLogsMgr.getHouseKeepLogs() - ", e);
		}
		return houseKeepLogList;
	}
	
}
