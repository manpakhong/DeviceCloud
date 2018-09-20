package com.littlecloud.services;

import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.branch.FirmwaresDAO;
import com.littlecloud.control.entity.branch.Firmwares;

public class FirmwareMgr {
	private static final Logger log = Logger.getLogger(FirmwareMgr.class);
	private FirmwaresDAO firmwaresDao;
	public FirmwareMgr(){
		init();
	}
	private void init(){
		try{
			firmwaresDao = new FirmwaresDAO(true);
		} catch (Exception e){
			log.error("FirmwareMgr.init() - ", e);
		}
	}
	
	public boolean isVersionExisted(Integer productId, String version){
		boolean isVersionExisted = false;
		try{
			List<Firmwares> firmwareList = getFirmwaresByProductId(productId);
			if (firmwareList != null && firmwareList.size() > 0){
				for (Firmwares firmware: firmwareList){
					if (firmware != null && firmware.getVersion() != null){
						if (firmware.getVersion().equals(version)){
							isVersionExisted = true;
							break;
						}
					}
				}
			}
		} catch (Exception e){
			log.error("FirmwareMgr.getFirmwaresByProductId() - ", e);			
		}
		return isVersionExisted;
	}
	
	public List<Firmwares> getFirmwaresByProductId(Integer productId){
		List<Firmwares> firmwareList = null;
		try{
			firmwareList = firmwaresDao.getFirmwaresListByProductId(productId);
		} catch (Exception e){
			log.error("FirmwareMgr.getFirmwaresByProductId() - ", e);
		}
		return firmwareList;
	}
	
}
