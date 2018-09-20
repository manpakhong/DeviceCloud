package com.littlecloud.control.json.model.config.util;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import com.littlecloud.control.json.model.config.JsonConf_Admin;
import com.littlecloud.control.json.model.config.util.info.AdminInfo;

public class AdminConfigApplicationAP extends AdminConfigApplicationMAX {

	private String errMsg;
	
	public AdminConfigApplicationAP(AdminInfo ctr) throws Exception {
		super(ctr);
	}

	private static final Logger log = Logger.getLogger(AdminConfigApplicationAP.class);
	
	protected JsonConf_Admin handleAdminProtocol(JsonConf_Admin adminConf) {
		/* handle protocol */		
		switch(adminConf.getProtocol())
		{
			case disable:
				ctr.getSbSummary().append(String.format("\n* admin protocol is disable"));
				adminConf.setWeb_administration(false);
				break;
			case http:
				ctr.getSbSummary().append(String.format("\n* admin protocol is http"));
				adminConf.setWeb_administration(true);
				adminConf.setWeb_access_protocol(adminConf.getProtocol().toString());
				adminConf.setWeb_management_port(String.valueOf(adminConf.getAdmin_auth_port()==null?JsonConf_Admin.ADMIN_DEFAULT_HTTP_PORT:adminConf.getAdmin_auth_port()));
				break;
			case https:
				ctr.getSbSummary().append(String.format("\n* admin protocol is https"));
				adminConf.setWeb_administration(true);
				adminConf.setWeb_access_protocol(adminConf.getProtocol().toString());
				adminConf.setWeb_management_port(String.valueOf(adminConf.getAdmin_auth_port()==null?JsonConf_Admin.ADMIN_DEFAULT_HTTPS_PORT:adminConf.getAdmin_auth_port()));
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
			adminConf.setWeb_admin_username(JsonConf_Admin.ADMIN_DEFAULT_USER_NAME);
			adminConf.setWeb_admin_password(ctr.getDev().getWebadmin_password());
			
			if (adminConf.isManaged()) {
				if (StringUtils.isEmpty(adminConf.getWeb_admin_username())) {
					errMsg = String.format("Err - invalid admin config for %s (empty admin name for admin enable config)", adminConf);
					log.error(errMsg);
					ctr.getSbSummary().append(errMsg);
					return null;
				}
			}
		}
		else
		{
			ctr.getSbSummary().append(String.format("\n* admin auth pwd empty and unmanaged/device_managed"));
			adminConf.setWeb_admin_username(null);
			adminConf.setWeb_admin_password(null);
		}
		return adminConf;
	}
	
	@Deprecated
	protected JsonConf_Admin handleAdminPasswordOld(JsonConf_Admin adminConf) {
		ctr.getSbSummary().append("\n* AP admin password");
		
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
				adminConf.setWeb_admin_username(JsonConf_Admin.ADMIN_DEFAULT_USER_NAME);
				adminConf.setWeb_admin_password(ctr.getDev().getWebadmin_password());
				
				if (adminConf.isManaged()) {
					if (StringUtils.isEmpty(adminConf.getWeb_admin_username()) || StringUtils.isEmpty(adminConf.getWeb_admin_password())) {
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

		/* no readonly user config */
		
		return adminConf;
	}
}