package com.littlecloud.control.json.model.config.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.ConfigurationSettingsDAO;
import com.littlecloud.control.dao.ConfigurationSsidsDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.entity.ConfigurationSettings;
import com.littlecloud.control.entity.ConfigurationSettingsId;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.pool.object.utils.DeviceUtils;

public class ConfigurationSettingsUtils {

	private static final Logger log = Logger.getLogger(ConfigurationSettingsUtils.class);

	ConfigurationSettingsDAO settingsDAO = null;
	
	private String orgId;
	private Integer netId;
	private Integer devId;
	
	private String logInfo; 
	
	/* In case new settings does not exist, util to generate settings in new table */
	public ConfigurationSettingsUtils(String orgId, Integer netId, Integer devId) throws Exception {
		super();
		this.orgId = orgId;
		this.netId = netId;
		this.devId = devId;
		
		this.settingsDAO = new ConfigurationSettingsDAO(orgId, false);
		
		this.logInfo = String.format("[orgId %s netId %d devId %d]", orgId, netId, devId);
	}

	public boolean deleteConfigurationSettings(ConfigurationSettings settings) 
	{
		if (settings == null)
			return true;

		try {			
			settingsDAO.delete(settings);
		} catch (Exception e)
		{
			log.error("deleteConfigurationSettings ", e);
			return false;
		}
		
		return true;
	}
	
	public boolean saveOrUpdateConfigurationSettings(ConfigurationSettings settings) 
	{
		try {
			settingsDAO.saveOrUpdate(settings);
		} catch (Exception e)
		{
			log.error("saveOrUpdateConfigurationSettings ", e);
			return false;
		}
		
		return true;
	}
	
	/* Conversion of old config to new config table */
	public boolean convertToNewSettingsAllDevices() 
	{
		boolean isSuccess = true;		
		
		DevicesDAO devDAO = null;
		List<Devices> devLst = null;
		
		try {			
			devDAO = new DevicesDAO(orgId);
			devLst = devDAO.getDevicesListByNetworkId(netId);
			
			isSuccess = convertToNewSettingsForNetDev(netId, 0);	// convert network settings
			for (Devices dev:devLst)
			{
				if (!convertToNewSettingsForNetDev(netId, dev.getId()))	// convert device settings
					isSuccess = false;
			}
			
		} catch (Exception e)
		{
			log.error("Exception convertToNewSettingsAllDevices "+logInfo, e);
			isSuccess = false;
			
		}		
		return isSuccess;
	}
		
	/* Conversion of old config to new config table */
	private boolean convertToNewSettingsForNetDev(Integer netId, Integer devId) 
	{
		boolean isSuccess = true;		
		Boolean isWifiManaged = null;
		Boolean isFollowNetwork = null;
		
		ConfigurationSettingsId settingsId = null;
		ConfigurationSettings settings = null;
		ConfigurationSsidsDAO ssidDAO = null;
		DevicesDAO devDAO = null;
		Devices dev = null;

		try {
			devDAO = new DevicesDAO(orgId, false);
			settingsId = new ConfigurationSettingsId(netId, devId);
			settings = settingsDAO.findById(settingsId);
			
			if (settings!=null)
			{
				log.debugf("new settings already existed %s", logInfo);
				return true;
			}
			
			settings = new ConfigurationSettings();
			settings.setId(settingsId);
			
			/* convert to new settings from old tables */
			ssidDAO = new ConfigurationSsidsDAO(orgId, false);
			isWifiManaged = ssidDAO.isWifiManageable(netId, devId);
			settings.setWifi_enabled(isWifiManaged);
			
			if (devId != 0)
			{
				/* check individual settings */
				dev = devDAO.findById(devId);
				if (dev == null)
				{
					log.errorf("devId is not found in orgId %s", logInfo);
					return false;
				}
				
				isFollowNetwork = !DeviceUtils.isDevLevelWifiEnabled(dev);
				//DeviceUtils.isDevLevelMvpnEnabled(dev);
			}
			else
			{
				isFollowNetwork = false;
			}
			settings.setFollow_network(isFollowNetwork);
			settingsDAO.saveOrUpdate(settings);
		} catch (Exception e)
		{
			log.error("Exception convertToNewSettings "+logInfo, e);
			isSuccess = false;
			
		}		
		return isSuccess;
	}
		
	public Map<Integer, ConfigurationSettings> getDevConfigSettingsOfNetwork() throws Exception
	{
		Map<Integer, ConfigurationSettings> devConfMap = new HashMap<Integer, ConfigurationSettings>();
		
		List<ConfigurationSettings> settingsLst = null;
		
		try {
			settingsLst =  settingsDAO.getConfigSettingsLstFromNetwork(netId);
			
			for (ConfigurationSettings settings:settingsLst)
			{
				if (settings!=null)
				{
					devConfMap.put(settings.getId().getDeviceId(), settings);
				}
			}
		} catch (Exception e)
		{
			log.error("Exception getDevConfigSettingsOfNetwork "+logInfo, e);
			throw new Exception(e);
			
		}
		return devConfMap;		
	}
	
	public ConfigurationSettings getNetworkConfigSettings() throws Exception
	{
		ConfigurationSettingsId settingsId = null;
		ConfigurationSettings settings = null;
		try {			
			settingsId = new ConfigurationSettingsId(netId, 0);			
			settings = settingsDAO.findById(settingsId);
			return settings;
		} catch (Exception e)
		{
			log.error("Exception getNetworkConfigSettings "+logInfo, e);
			throw new Exception(e);			
		}	
	}
	
	public ConfigurationSettings initNetConfigSettings() throws Exception
	{
		ConfigurationSettings settings = getNetworkConfigSettings();
		if (settings==null)
		{
			settings = new ConfigurationSettings();
			ConfigurationSettingsId settingsId = new ConfigurationSettingsId();
			settingsId.setNetworkId(netId);
			settingsId.setDeviceId(0);
			settings.setId(settingsId);
		}
		settings.setFollow_network(false);
		settings.setWifi_enabled(false);
		saveOrUpdateConfigurationSettings(settings);

		return settings;
	}
	
	public ConfigurationSettings initDevConfigSettings() throws Exception
	{
		ConfigurationSettings settings = getDevConfigSettings();
		if (settings==null)
		{
			settings = new ConfigurationSettings();
			ConfigurationSettingsId settingsId = new ConfigurationSettingsId();
			settingsId.setNetworkId(netId);
			settingsId.setDeviceId(devId);
			settings.setId(settingsId);
		}
		settings.setFollow_network(true);
		settings.setWifi_enabled(false);
		saveOrUpdateConfigurationSettings(settings);

		return settings;
	}
	
	public void removeDevConfigSettings() throws Exception
	{
		ConfigurationSettings settings = getDevConfigSettings();
		if (settings==null)
		{
			return;
		}			
		deleteConfigurationSettings(settings);
	}
	
	public ConfigurationSettings getDevConfigSettings() throws Exception
	{
		if (devId == 0)
		{
			log.warnf("devId %d has no individual config settings", devId);
			return null;
		}
				
		ConfigurationSettingsId settingsId = null;
		ConfigurationSettings settings = null;
		try {
			settingsId = new ConfigurationSettingsId(netId, devId);			
			settings = settingsDAO.findById(settingsId);
			return settings;
		} catch (Exception e)
		{
			log.error("Exception getNetworkConfigSettings "+logInfo, e);
			throw new Exception(e);			
		}
	}
}
