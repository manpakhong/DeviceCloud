package com.littlecloud.services;

import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.root.RootBranchesDAO;

public class RootBranchesMgr {
	private static final Logger log = Logger.getLogger(RootBranchesMgr.class);
	private RootBranchesDAO rootBranchesDao;
	public RootBranchesMgr(){
		init();
	}
	private void init(){
		try{
			rootBranchesDao = new RootBranchesDAO();
		} catch (Exception e){
			log.error("SnsOrganizationMgr.init() - ", e);
		}
	}
	
	public Integer getMonitorResult(){ 			
		Integer result = null;
		try{
			result = rootBranchesDao.monitorRootDb();
		} catch (Exception e){
			log.error("RootBranchesMgr.getMonitorResult() - ", e);
		}
		return result;
	}
}
