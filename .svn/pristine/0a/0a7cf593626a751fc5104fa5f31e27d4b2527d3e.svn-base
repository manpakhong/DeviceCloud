package com.littlecloud.services;

import java.sql.SQLException;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.entity.Networks;

public class NetworksMgr {
	private static final Logger log = Logger.getLogger(NetworksMgr.class);
	private String orgId;
	private NetworksDAO networkDao;
	public NetworksMgr(String orgId){
		this.orgId = orgId;
		init();
	}
	public void init(){
		try{
			networkDao = new NetworksDAO(orgId);
		} catch (Exception e){
			log.errorf("NetworksMgr.init() - Exception:", e);
		}
	}
	
	public List<Networks> getAllNetworks(){
		List<Networks> networkList = null;
		try{
			networkList = networkDao.getAllNetworks();
		} catch (Exception e){
			log.error("NetworksMgr.getAllNetworks() - Exception:", e);			
		}
		return networkList;
	}
	
	public Networks findById(Integer id) throws SQLException {
		return networkDao.findById(id);
	}
	
	public void update(Networks net) throws SQLException {
		networkDao.update(net);
	}		
}
