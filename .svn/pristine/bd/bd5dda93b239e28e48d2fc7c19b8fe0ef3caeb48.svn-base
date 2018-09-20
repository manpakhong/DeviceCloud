package com.littlecloud.pool.object.utils;

import java.util.List;

import org.apache.log4j.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.pool.object.ConfigSettingsObject;

public class ConfigSettingsUtils 
{
	private static Logger log = Logger.getLogger(ConfigSettingsUtils.class);
	
	public static ConfigSettingsObject getPoolObject(Integer iana_id, String sn)
	{
		ConfigSettingsObject configSettingsO = new ConfigSettingsObject();
		
		try 
		{
			configSettingsO.setIana_id(iana_id);
			configSettingsO.setSn(sn);
			return ACUtil.<ConfigSettingsObject>getPoolObjectBySn(configSettingsO, ConfigSettingsObject.class);
		} 
		catch (Exception e) 
		{
			log.error("Get config settings object from cache error - "+e,e);
			return null;
		}
		
	}
	
	public static void putPoolObject(ConfigSettingsObject configSettingsObject) throws Exception
	{
		ACUtil.<ConfigSettingsObject>cachePoolObjectBySn(configSettingsObject, ConfigSettingsObject.class);
	}
	
	public static void removePoolObject(ConfigSettingsObject configSettingsObject) throws InstantiationException, IllegalAccessException
	{
		ACUtil.<ConfigSettingsObject>removePoolObjectBySn(configSettingsObject, ConfigSettingsObject.class);
	}
	
	public static void removePoolObject(Integer iana_id, String sn) throws InstantiationException, IllegalAccessException
	{
		ConfigSettingsObject configSettingsObject = new ConfigSettingsObject();
		configSettingsObject.setIana_id(iana_id);
		configSettingsObject.setSn(sn);
		removePoolObject(configSettingsObject);
	}
	
	public static void removePoolObjectByNetId(String orgId, Integer netId) throws Exception
	{
		DevicesDAO devDAO = new DevicesDAO(orgId);
		List<Devices> devLst = devDAO.getDevicesList(netId);
		if (devLst==null)
			return;
		
		for (Devices dev:devLst)
		{
			if (dev!=null)
				removePoolObject(dev.getIanaId(), dev.getSn());
		}
	}

	public static void removePoolObjectByDevIdLst(String orgId, List<Integer> devIdLst) throws Exception
	{
		DevicesDAO devDAO = new DevicesDAO(orgId);
		List<Devices> devLst = devDAO.getDevicesList(devIdLst);
		if (devLst==null)
			return;
		
		for (Devices dev:devLst)
		{
			if (dev!=null)
				removePoolObject(dev.getIanaId(), dev.getSn());
		}
	}
	
	public static void removePoolObjectByDevId(String orgId, Integer devId) throws Exception
	{
		DevicesDAO devDAO = new DevicesDAO(orgId);
		Devices dev = devDAO.findById(devId);
		if (dev==null)
			return;
		
		removePoolObject(dev.getIanaId(), dev.getSn());
	}
	

}