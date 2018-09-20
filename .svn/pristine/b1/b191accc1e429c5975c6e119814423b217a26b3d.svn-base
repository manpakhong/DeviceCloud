package com.littlecloud.control.json.model.config.util;

import org.apache.commons.lang.RandomStringUtils;
import org.jboss.logging.Logger;

import com.littlecloud.control.entity.ConfigurationSettings;
import com.littlecloud.control.json.model.config.JsonConf_Admin;
import com.littlecloud.control.json.model.config.JsonConf_Admin.ADMIN_AUTHEN;
import com.littlecloud.control.json.model.config.util.info.AdminInfo;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.services.DeviceMgr;
import com.littlecloud.services.DeviceMgr.PasswordGenerateAction;

public class AdminConfigGroupLevel extends AdminConfigBase {

	private static final Logger log = Logger.getLogger(AdminConfigGroupLevel.class);
	private static final int DEFAULT_PASSWORD_LENGTH = 20;
	
	public AdminConfigGroupLevel(AdminInfo ctr) throws Exception {
		super(ctr);	
		
		if (ctr.getSettingsUtils()==null)
			ctr.setSettingsUtils(new ConfigurationSettingsUtils(ctr.getOrgId(), ctr.getNetId(), 0));
	}
	
	@Override
	public JsonConf_Admin getDatabaseConfig() {
		JsonConf_Admin adminConf = null;
		
		ctr.getSbSummary().append("\n>>> Admin group level config >>>");
		
		try {
			/* only group settings exists for admin */
			ConfigurationSettingsUtils settingsUtils = ctr.getSettingsUtils();
			ConfigurationSettings netSet = settingsUtils.getNetworkConfigSettings();
			String jsonAdminConf = netSet.getWebadmin();
			if (jsonAdminConf==null || jsonAdminConf.isEmpty())
			{				
				ctr.getSbSummary().append("\n* no admin settings - create default");
				adminConf = getDefaultAdminConfig();
			}
			else
			{
				adminConf = JsonUtils.<JsonConf_Admin>fromJson(jsonAdminConf, JsonConf_Admin.class);
			}			
									
			return adminConf;
		} catch (Exception e)
		{
			log.error("getDatabaseSystemFullConfig - error converting admin record to json object", e);
			return null;
		}
	}

	private JsonConf_Admin getDefaultAdminConfig() {
		JsonConf_Admin adminConf = new JsonConf_Admin();
		adminConf.setManaged(false);
		return adminConf;
	}

	@Override
	protected boolean saveConfigPassword(JsonConf_Admin adminConf) {
		DeviceMgr devMgr = new DeviceMgr(ctr.getOrgId());
		PasswordGenerateAction passGenAction = new PasswordGenerateAction();
		
		if (adminConf.isManaged())
		{	
			if (adminConf.getAdmin_auth()==ADMIN_AUTHEN.random_passwd && adminConf.isUpdate_admin_pass())
				passGenAction.setGenAdmin(true);
			
			if (adminConf.getAdmin_auth()==ADMIN_AUTHEN.device_managed)
				passGenAction.setResetAdmin(true);
			
			if (adminConf.getAdmin_user_auth()==ADMIN_AUTHEN.random_passwd && adminConf.isUpdate_user_pass())
				passGenAction.setGenUser(true);
			
			if (adminConf.getAdmin_user_auth()==ADMIN_AUTHEN.disable && adminConf.isUpdate_user_pass())
				passGenAction.setResetUser(true);			
		}
		else
		{
			passGenAction.setResetAdmin(true);
			passGenAction.setResetUser(true);
		}
		
		return devMgr.generateRandomPasswordForNetwork(ctr.getNetId(), passGenAction);		
	}

	public static String generatePassword()
	{		
		return RandomStringUtils.randomAlphanumeric(DEFAULT_PASSWORD_LENGTH);
	}
}
