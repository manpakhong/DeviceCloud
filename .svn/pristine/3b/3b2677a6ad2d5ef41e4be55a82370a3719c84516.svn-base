package com.littlecloud.control.json.model.config.util;

import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import com.littlecloud.control.json.model.config.JsonConf;
import com.littlecloud.control.json.model.config.JsonConf_AdminRadius;
import com.littlecloud.control.json.model.config.util.exception.SystemConfigException;
import com.littlecloud.control.json.model.config.util.info.AdminRadiusInfo;
import com.littlecloud.control.json.util.JsonUtils;

@Deprecated
public class AdminRadiusConfigUtils {

	private static final Logger log = Logger.getLogger(AdminRadiusConfigUtils.class);
		
	public static JsonConf_AdminRadius getDatabaseAdminRadiusFullConfig(AdminRadiusInfo ctr) throws SystemConfigException 
	{	
		String errMsg;
		
		ctr.getSbSummary().append("\n>>> Admin Radius config >>>");
		JsonConf.CONFIG_TYPE configType = ctr.getConfigType();
		if (configType != JsonConf.CONFIG_TYPE.MAX)
		{
			ctr.getSbSummary().append("\n* AP does not support admin radius config");
			return null;
		}
		
		try {
			JsonConf_AdminRadius radJson = JsonUtils.<JsonConf_AdminRadius>fromJson(ctr.getSzAdminRadius(), JsonConf_AdminRadius.class);
			
			/* logic to be handled */
			if (radJson.isManaged())
			{
				ctr.getSbSummary().append("\n* admin radius config is not managed");
				return null;
			}
			
			
			
			switch (radJson.getAdmin_auth())
			{
				case device_managed:
					break;
				case random_passwd:
					break;
				case by_radius:
				{
					if (radJson.isRadius_enable()==false)
					{
						errMsg = String.format("Err - inconsistent config admin_auth %s radius_enable %s!!", radJson.getAdmin_auth(), radJson.isRadius_enable());
						log.error(errMsg);
						ctr.getSbSummary().append(errMsg);
						return null;
					}
					
					if (!isValidAdminRadiusConfig(radJson))
					{
						errMsg = String.format("Err - invalid admin radius config for %s", radJson);
						log.error(errMsg);
						ctr.getSbSummary().append(errMsg);
						return null;
					}
					
					
				} break;
				default:
				{
					ctr.getSbSummary().append(String.format("\n* unknown admin auth for %s", radJson));
				}break;
			}			
			
			radJson.isAdmin_pass_generate();
			radJson.isUser_pass_enable();
				radJson.isUser_pass_generate();
			radJson.isRadius_enable();
			
			return radJson;
		} catch (Exception e)
		{
			log.error("getDatabaseSystemFullConfig - error converting admin radius record to json object", e);
			return null;
		}
	}
	
	public static Properties getHardwareProperties(AdminRadiusInfo ctr)
	{
		Properties result = null;
		
		JsonConf.CONFIG_TYPE configType = ctr.getConfigType();
		
		if (ctr.getSzAdminRadius()==null || ctr.getSzAdminRadius().isEmpty())
		{
			ctr.getSbSummary().append("\n* no admin radius settings");
			return null;
		}
		
		try {
			JsonConf_AdminRadius radJson = getDatabaseAdminRadiusFullConfig(ctr);
			result = CommonConfigUtils.parsePropertiesString(radJson.generateHardwareConfig(JsonConf_AdminRadius.class, configType));			
			return result;
		} catch (Exception e)
		{
			log.error("getHardwareProperties - error converting admin radius record to json object", e);
			return null;
		}		
	}
	
	public static String getHardwareSystemConfigMd5(Properties fullconfig) 
	{
		Properties hwsysconf = getHardwareSystemConfig(fullconfig);				
		return CommonConfigUtils.getConfChecksumFromHwConf(hwsysconf);	
	}
	
	private static boolean isValidAdminRadiusConfig(JsonConf_AdminRadius radJson)
	{
		if (radJson.isRadius_enable())
		{
			if (StringUtils.isEmpty(radJson.getAuth_host()) || radJson.getAuth_port()==null)
			{
				log.error("empty auth host or port for radius enable config");
				return false;
			}
		}
		
		return true;
	}
	
	private static Properties getHardwareSystemConfig(Properties fullconfig) {
		Properties hwconf = new Properties();

		Iterator<Object> itr = fullconfig.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			if (JsonConf_AdminRadius.FULL_CONFIG_KEY.contains(key)) {
				hwconf.put(key, fullconfig.get(key));
			}
		}
		
		return hwconf;
	}
}
