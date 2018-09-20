package com.littlecloud.control.json.model.config.util;

import org.jboss.logging.Logger;

import com.littlecloud.control.json.model.config.util.info.AdminInfo;

public class AdminConfigApplicationFactory {

	private static final Logger log = Logger.getLogger(AdminConfigApplicationFactory.class);
	
	public static AdminConfigBase getAdminConfigApplication(AdminInfo ctr) throws Exception {
		switch (ctr.getConfigType())
		{
			case MAX:
			{
				AdminConfigApplicationMAX adminApp = new AdminConfigApplicationMAX(ctr);
				return adminApp;
			} 
			case AP:
			{
				AdminConfigApplicationAP adminApp = new AdminConfigApplicationAP(ctr);
				return adminApp;
			} 
			default:
			{
				log.errorf("Unknown config type %s", ctr.getConfigType());
				return null;
			} 
		}
	}

}
