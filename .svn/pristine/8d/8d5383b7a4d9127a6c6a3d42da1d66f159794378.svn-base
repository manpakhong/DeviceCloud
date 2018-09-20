package com.littlecloud.services;

import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.branch.SnsOrganizationsDAO;

public class SnsOrganizationsMgr {
	private static final Logger log = Logger.getLogger(SnsOrganizationsMgr.class);
	private SnsOrganizationsDAO snsOrganizationsDao;
	public SnsOrganizationsMgr(){
		init();
	}
	private void init(){
		try{
			snsOrganizationsDao = new SnsOrganizationsDAO(true);
		} catch (Exception e){
			log.error("SnsOrganizationMgr.init() - ", e);
		}
	}
	
	public List<String> getSnsOrganizationsList(){ 			
		List<String> snsOrganizationsList = null;
		try{
			snsOrganizationsList = snsOrganizationsDao.getDistinctOrgList();
		} catch (Exception e){
			log.error("SnsOrganizationMgr.getSnsOrganizationsList() - ", e);
		}
		return snsOrganizationsList;
	}
}
