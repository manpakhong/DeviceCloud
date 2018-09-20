package com.littlecloud.control.json.model.config.util;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import com.littlecloud.control.entity.ConfigurationSettings;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.json.model.config.JsonConf_Admin;
import com.littlecloud.control.json.model.config.util.info.AdminInfo;
import com.littlecloud.control.json.util.JsonUtils;

public class AdminConfigDeviceLevel extends AdminConfigBase {

	private static final Logger log = Logger.getLogger(AdminConfigDeviceLevel.class);
	
	public AdminConfigDeviceLevel(AdminInfo ctr) throws Exception {
		super(ctr);	
	}
	
	@Override
	public JsonConf_Admin getDatabaseConfig() {
		String errMsg;
		Devices dev = null;
		
		ctr.getSbSummary().append("\n>>> Admin device level config >>>");
		
		try {
			/* validate device */
			dev = ctr.getDev();
			if (dev == null)
			{
				ctr.getSbSummary().append("\n* err - device not found!");
				return null;
			}
			
			/* only group settings exists for admin */
			ConfigurationSettingsUtils settingsUtils = ctr.getSettingsUtils();
			ConfigurationSettings netSet = settingsUtils.getNetworkConfigSettings();
			String jsonAdminConf = netSet.getWebadmin();
			if (jsonAdminConf==null || jsonAdminConf.isEmpty())
			{				
				ctr.getSbSummary().append("\n* no admin settings");
				return null;
			}
			
			JsonConf_Admin adminConf = JsonUtils.<JsonConf_Admin>fromJson(jsonAdminConf, JsonConf_Admin.class);						
			if (!adminConf.isManaged())
			{				
				ctr.getSbSummary().append("\n* admin config is not managed");
				return adminConf;
			}
			
			/* handle admin */
			switch (adminConf.getAdmin_auth())
			{
				case device_managed:
				{
					ctr.getSbSummary().append(String.format("\n* admin auth is not managed by IC2 %s", adminConf));
				} break;
				case random_passwd:
				{
					adminConf.setAdmin_name(JsonConf_Admin.ADMIN_DEFAULT_USER_NAME);
					adminConf.setAdmin_password(dev.getWebadmin_password());
					
					if (adminConf.isManaged()) {
						if (StringUtils.isEmpty(adminConf.getAdmin_name()) || StringUtils.isEmpty(adminConf.getAdmin_password())) {
							errMsg = String.format("Err - invalid admin config (empty admin name or password for admin enable config) for %s", adminConf);
							log.error(errMsg);
							ctr.getSbSummary().append(errMsg);
							return null;
						}
					}					
				} break;
				default:
				{
					ctr.getSbSummary().append(String.format("\n* unknown admin auth for %s", adminConf));
				} break;
			}			
			
			/* handle readonly user */
			switch (adminConf.getAdmin_user_auth())
			{
				case device_managed:
				{
					ctr.getSbSummary().append(String.format("\n* admin auth is not managed by IC2 %s", adminConf));
				} break;
				case random_passwd:
				{
					adminConf.setAdmin_readonly_name(JsonConf_Admin.ADMIN_DEFAULT_READONLY_USER_NAME);
					adminConf.setAdmin_readonly_password(dev.getWebadmin_user_password());
					
					if (StringUtils.isEmpty(adminConf.getAdmin_readonly_name()) || StringUtils.isEmpty(adminConf.getAdmin_readonly_password())) {
						errMsg = String.format("Err - invalid admin config (empty user name or password for user enable config) for %s", adminConf);
						log.error(errMsg);
						ctr.getSbSummary().append(errMsg);
						return null;
					}					
				} break;
				case disable:
				{
					adminConf.setAdmin_readonly_name(JsonConf_Admin.ADMIN_DEFAULT_READONLY_USER_NAME);
					adminConf.setAdmin_readonly_password(null);
				}break;
				default:
				{
					ctr.getSbSummary().append(String.format("\n* unknown admin user auth for %s", adminConf));
				} break;
			}

			return adminConf;
		} catch (Exception e)
		{
			log.error("getDatabaseSystemFullConfig - error converting admin record to json object", e);
			return null;
		}
	}

	@Override
	public boolean saveConfigPassword(JsonConf_Admin adminConf) {
		return true;
	}


}
