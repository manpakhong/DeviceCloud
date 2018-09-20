package com.littlecloud.control.json.model.config.util;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import com.littlecloud.control.entity.ConfigurationSettings;
import com.littlecloud.control.json.model.config.JsonConf_Admin;
import com.littlecloud.control.json.model.config.util.exception.FeatureNotFoundException;
import com.littlecloud.control.json.model.config.util.info.AdminInfo;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.utils.FeatureGetUtils;
import com.littlecloud.pool.object.utils.FeatureGetUtils.ATTRIBUTE;

public class AdminConfigApplicationMAX extends AdminConfigBase {

	private static final Logger log = Logger.getLogger(AdminConfigApplicationMAX.class);
	private String errMsg;
	
	public AdminConfigApplicationMAX(AdminInfo ctr) throws Exception {
		super(ctr);	
	}
	
	@Override
	public JsonConf_Admin getDatabaseConfig() throws Exception {		
		ctr.getSbSummary().append("\n>>> Admin config >>>");		
		
		/* validate device */
		if (ctr.getDev() == null)
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
			return null;
		}
		
		adminConf = handleAdminPassword(adminConf);
		adminConf = handleAdminProtocol(adminConf);
		
		return adminConf;
	}
	
	protected JsonConf_Admin handleAdminProtocol(JsonConf_Admin adminConf) throws FeatureNotFoundException {
		String strAdminWanIp;
		
		/* handle protocol */		
		switch(adminConf.getProtocol())
		{
			case disable:
				ctr.getSbSummary().append(String.format("\n* admin protocol is disable"));
				strAdminWanIp = createIplistFromWan();
				adminConf.setAdmin_http_enable(false);
				adminConf.setAdmin_iplist("");
				adminConf.setAdmin_lanonly(true);
				
				adminConf.setAdmin_https_enable(false);
				adminConf.setAdmin_https_iplist("");
				break;
			case http:
				ctr.getSbSummary().append(String.format("\n* admin protocol is http"));
				strAdminWanIp = createIplistFromWan();
				adminConf.setAdmin_http_enable(true);
				adminConf.setAdmin_port(String.valueOf(adminConf.getAdmin_auth_port()==null?JsonConf_Admin.ADMIN_DEFAULT_HTTP_PORT:adminConf.getAdmin_auth_port()));					
				adminConf.setAdmin_iplist(strAdminWanIp);
				adminConf.setAdmin_lanonly(false);
				
				adminConf.setAdmin_https_enable(false);
				adminConf.setAdmin_https_iplist(strAdminWanIp);
				break;
			case https:
				ctr.getSbSummary().append(String.format("\n* admin protocol is https"));			
				strAdminWanIp = createIplistFromWan();	
				adminConf.setAdmin_http_enable(false);
				adminConf.setAdmin_iplist(strAdminWanIp);
				adminConf.setAdmin_lanonly(false);
				
				adminConf.setAdmin_https_enable(true);
				adminConf.setAdmin_port(String.valueOf(adminConf.getAdmin_auth_port()==null?JsonConf_Admin.ADMIN_DEFAULT_HTTPS_PORT:adminConf.getAdmin_auth_port()));
				adminConf.setAdmin_https_iplist(strAdminWanIp);				
				break;
		}
		
		return adminConf;
	}

	protected JsonConf_Admin handleAdminPassword(JsonConf_Admin adminConf) {
		/* apply if password exists */
		ctr.getSbSummary().append(String.format("\n* admin auth managed by group settings (%s)", adminConf.getAdmin_auth()));
		if (!StringUtils.isEmpty(ctr.getDev().getWebadmin_password()))
		{			
			ctr.getSbSummary().append(String.format("\n* admin auth pwd found %s", ctr.getDev().getWebadmin_password()));
			adminConf.setAdmin_name(JsonConf_Admin.ADMIN_DEFAULT_USER_NAME);
			adminConf.setAdmin_password(ctr.getDev().getWebadmin_password());
			
			if (adminConf.isManaged()) {
				if (StringUtils.isEmpty(adminConf.getAdmin_name())) {
					errMsg = String.format("Err - invalid admin config for %s (empty admin name for admin password enable config)", adminConf);
					log.error(errMsg);
					ctr.getSbSummary().append(errMsg);
					return null;
				}
			}	
		}
		else
		{
			ctr.getSbSummary().append(String.format("\n* admin auth pwd empty and unmanaged/device_managed"));
			adminConf.setAdmin_name(null);
			adminConf.setAdmin_password(null);
		}
		
		ctr.getSbSummary().append(String.format("\n* admin user auth managed by group settings (%s)", adminConf.getAdmin_user_auth()));
		if (!StringUtils.isEmpty(ctr.getDev().getWebadmin_user_password()))
		{			
			ctr.getSbSummary().append(String.format("\n* admin user auth pwd found %s", ctr.getDev().getWebadmin_user_password()));
			adminConf.setAdmin_readonly_name(JsonConf_Admin.ADMIN_DEFAULT_READONLY_USER_NAME);
			adminConf.setAdmin_readonly_password(ctr.getDev().getWebadmin_user_password());
			
			if (StringUtils.isEmpty(adminConf.getAdmin_readonly_name())) {
				errMsg = String.format("Err - invalid admin config for %s (empty user name for user enable config)", adminConf);
				log.error(errMsg);
				ctr.getSbSummary().append(errMsg);
				return null;
			}
		}
		else
		{
			ctr.getSbSummary().append(String.format("\n* admin user auth pwd empty, follow group settings"));			
			switch(adminConf.getAdmin_user_auth())
			{
				case device_managed:
					ctr.getSbSummary().append(String.format("\n* not applying user auth password"));
					adminConf.setAdmin_readonly_name(null);
					adminConf.setAdmin_readonly_password(null);
					break;
				case disable:
					ctr.getSbSummary().append(String.format("\n* disable user auth password"));
					adminConf.setAdmin_readonly_name(JsonConf_Admin.ADMIN_DEFAULT_READONLY_USER_NAME);
					adminConf.setAdmin_readonly_password("");
					break;
				case random_passwd:
					ctr.getSbSummary().append(String.format("\n* data conflict that user auth random_password with password not found"));
					return null;
				default:
					ctr.getSbSummary().append(String.format("\n* unknown user auth for %s", adminConf));
					return null;
			}
		}
		return adminConf;
	}
	
	@Deprecated
	protected JsonConf_Admin handleAdminPasswordOld(JsonConf_Admin adminConf) {
		ctr.getSbSummary().append("\n* admin password");
		
		/* handle admin */
		switch (adminConf.getAdmin_auth())
		{
			case device_managed:
			{
				ctr.getSbSummary().append(String.format("\n* admin auth is not managed by IC2"));
			} break;
			case random_passwd:
			{
				ctr.getSbSummary().append(String.format("\n* admin auth managed by random password"));
				adminConf.setAdmin_name(JsonConf_Admin.ADMIN_DEFAULT_USER_NAME);
				adminConf.setAdmin_password(ctr.getDev().getWebadmin_password());
				
				if (adminConf.isManaged()) {
					if (StringUtils.isEmpty(adminConf.getAdmin_name()) || StringUtils.isEmpty(adminConf.getAdmin_password())) {
						errMsg = String.format("Err - invalid admin config for %s (empty admin name or password for admin enable config)", adminConf);
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
				ctr.getSbSummary().append(String.format("\n* admin user auth is not managed by IC2"));
			} break;
			case random_passwd:
			{
				ctr.getSbSummary().append(String.format("\n* admin user auth is random password"));
				
				adminConf.setAdmin_readonly_name(JsonConf_Admin.ADMIN_DEFAULT_READONLY_USER_NAME);
				adminConf.setAdmin_readonly_password(ctr.getDev().getWebadmin_user_password());
				
				if (StringUtils.isEmpty(adminConf.getAdmin_readonly_name()) || StringUtils.isEmpty(adminConf.getAdmin_readonly_password())) {
					errMsg = String.format("Err - invalid admin config for %s (empty user name or password for user enable config)", adminConf);
					log.error(errMsg);
					ctr.getSbSummary().append(errMsg);
					return null;
				}					
			} break;
			case disable:
			{
				ctr.getSbSummary().append(String.format("\n* admin user auth is disable"));
				
				adminConf.setAdmin_readonly_name(JsonConf_Admin.ADMIN_DEFAULT_READONLY_USER_NAME);
				adminConf.setAdmin_readonly_password("");
			}break;
			default:
			{
				ctr.getSbSummary().append(String.format("\n* admin user auth is unknown %s", adminConf));
			} break;
		}
		
		return adminConf;
	}
	
	private String createIplistFromWan() throws FeatureNotFoundException
	{		
		StringBuilder sb = new StringBuilder();
		
		Integer wanCnt = FeatureGetUtils.getFeatureAttributeAsInt(ctr.getFgo(), ATTRIBUTE.wan_cnt);
		ctr.getSbSummary().append("\n* wan count = "+wanCnt);
		if (wanCnt<=0)
		{
			throw new FeatureNotFoundException("Wan count should not be "+wanCnt);
		}		
		
		for (int i=1; i<=wanCnt; i++)
		{
			sb.append(i);
			sb.append(":default ");
		}
		sb.setLength(sb.length()-1);

		return sb.toString();
	}

	@Override
	public boolean saveConfigPassword(JsonConf_Admin adminConf) {
		return true;
	}


}
