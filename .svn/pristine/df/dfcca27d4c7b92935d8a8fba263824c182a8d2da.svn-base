package com.littlecloud.control.json.model.config.util;

import java.util.Iterator;
import java.util.Properties;

import org.jboss.logging.Logger;

import com.littlecloud.control.entity.ConfigurationSettings;
import com.littlecloud.control.json.model.config.JsonConf;
import com.littlecloud.control.json.model.config.JsonConf_Admin;
import com.littlecloud.control.json.model.config.util.exception.FeatureNotFoundException;
import com.littlecloud.control.json.model.config.util.info.AdminInfo;
import com.littlecloud.control.json.util.JsonUtils;

public abstract class AdminConfigBase {
	
	private static final Logger log = Logger.getLogger(AdminConfigBase.class);
	
	public abstract JsonConf_Admin getDatabaseConfig() throws Exception;
	protected abstract boolean saveConfigPassword(JsonConf_Admin adminConf);
	
	protected AdminInfo ctr;
		
	protected AdminConfigBase(AdminInfo ctr) throws Exception {
		super();
		
		if (ctr.getSbSummary()==null)
			ctr.setSbSummary(new StringBuilder());
				
		this.ctr = ctr;
	}
	
	public JsonConf_Admin saveConfig(JsonConf_Admin adminConf) throws Exception
	{
		String strAdminConf = null;
		
		/* handle group settings */
		ConfigurationSettingsUtils settingsUtils = ctr.getSettingsUtils();
		ConfigurationSettings netSet = settingsUtils.getNetworkConfigSettings();
		if (netSet == null)
			netSet = settingsUtils.initNetConfigSettings();
				
		strAdminConf = JsonUtils.toJson(adminConf);
		netSet.setWebadmin(strAdminConf);
		settingsUtils.saveOrUpdateConfigurationSettings(netSet);
		
		/* save password */
		if (!saveConfigPassword(adminConf))
			log.errorf("fail to saveConfigPassword for %s", ctr);
		
		return adminConf;
	}	
	
	public Properties getHardwareProperties() throws Exception
	{
		Properties result = null;		
		JsonConf.CONFIG_TYPE configType = null;
				
		try {
			configType = ctr.getConfigType();
			JsonConf_Admin adminConf = getDatabaseConfig();
			if (adminConf==null)
				return null;
			result = CommonConfigUtils.parsePropertiesString(adminConf.generateHardwareConfig(JsonConf_Admin.class, configType));			
			return result;
		} catch (Exception e)
		{
			log.error("getHardwareProperties - error converting admin record to json object", e);
			return null;
		}		
	}
	
	public static String getHardwareSystemConfigMd5(Properties fullconfig) 
	{
		Properties hwsysconf = getHardwareSystemConfig(fullconfig);				
		return CommonConfigUtils.getConfChecksumFromHwConf(hwsysconf);	
	}
	
	private static Properties getHardwareSystemConfig(Properties fullconfig) {
		Properties hwconf = new Properties();

		Iterator<Object> itr = fullconfig.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			if (JsonConf_Admin.FULL_CONFIG_KEY.contains(key)) {
				hwconf.put(key, fullconfig.get(key));
			}
		}
		
		return hwconf;
	}
}
