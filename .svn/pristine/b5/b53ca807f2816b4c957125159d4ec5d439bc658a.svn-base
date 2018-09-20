package com.littlecloud.services;

import java.util.Map;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.branch.OuiInfosDAO;

public class OuiInfosMgr {
	private static final Logger log = Logger.getLogger(OuiInfosDAO.class);
	private OuiInfosDAO ouiInfosDao;
	private Map<Long, String> ouiMap;
	public OuiInfosMgr(){
		init();
	}
	private void init(){
		try{
			OuiInfosDAO ouiInfosDAO = new OuiInfosDAO(true);
			ouiMap = ouiInfosDAO.getOuiInfosMap();
		} catch (Exception e){
			log.error("OuiInfosMgr.init() - Exception:", e);
		}
	}
	public String getManufacturer(String mac){
		String manufacturer = null;
		try{
			if (ouiMap != null){
				Long macLong = convertMacString2ValidLong(mac);
				manufacturer = ouiMap.get(macLong);
			}
		} catch (Exception e){
			log.error("OuiInfosMgr.getManufacturer() - Exception: ", e);
		}
		return manufacturer;
	}
	public Long convertMacString2ValidLong(String mac){
		Long macLong = null;
		try{
			if(mac != null && !mac.isEmpty() && mac.indexOf(".") < 0) {
				String macHex = mac.substring(0, 8);								
				macHex = macHex.replaceAll(":", "");
				macHex = macHex + "000000";
				macLong = Long.parseLong(macHex, 16);
			}
			
		} catch (Exception e){
			log.error("OuiInfosMgr.convertMacString2ValidLong() - Exception: ", e);
		}
		return macLong;
	}
	
}
