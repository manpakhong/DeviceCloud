package com.littlecloud.control.webservices.util;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.littlecloud.control.dao.branch.OuiInfosDAO;

public class OuiInfoUtils 
{
	private static Logger logger = Logger.getLogger(OuiInfoUtils.class);
	public static HashMap<Long, String> ouiInfosMap = new HashMap<Long, String>();
	public static Date lastUpdateTime = null;
	
	public static HashMap<Long, String> getOuiInfosMap() {
		return ouiInfosMap;
	}
	
	public static void collectOuiInfosMap() 
	{
		try
		{
			OuiInfosDAO ouiDAO = new OuiInfosDAO(true);
			ouiInfosMap = ouiDAO.getOuiInfosMap();
			lastUpdateTime = new Date();
		}
		catch(Exception e)
		{
			logger.info("Collect oui info map error -"+e,e);
		}
	}
	
	public static Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	
	public static String getOrganization(String mac)
	{
		String org = null;
		String macHex = mac.substring(0, 8).replaceAll(":", "") + "000000";
		Long macLong = Long.parseLong(macHex, 16);
		
		return org;
	}
}
