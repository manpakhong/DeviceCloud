package com.littlecloud.services;

import org.jboss.logging.Logger;

import com.littlecloud.alert.client.AlertClient;
import com.littlecloud.dtos.json.EmailTemplateObjectDto;
import com.littlecloud.pool.utils.PropertyService;

public class AlertClientMgr {
	private static final Logger log = Logger.getLogger(AlertClientMgr.class);
	private static PropertyService<AlertClientMgr> ps = new PropertyService<AlertClientMgr>(AlertClientMgr.class);
	private static String ALERT_SERVER_ADDR;
	private static Integer ALERT_SERVER_PORT;
	
	public AlertClientMgr(){
		init();
	}
	
	private void init(){
		try{
			if (ALERT_SERVER_ADDR == null || ALERT_SERVER_ADDR.isEmpty()){
				ALERT_SERVER_ADDR = ps.getString("alert.server.addr");
			}
			if (ALERT_SERVER_PORT == null){
				ALERT_SERVER_PORT = ps.getInteger("alert.server.port");
			}
		} catch (Exception e){
			log.error("ALERT201408211034, GEO20140204 - AlertclientMgr.init() - ", e);
		}
	}
	
	public boolean sendEmailAlert(EmailTemplateObjectDto eto){
		RailsMailMgr railsMailMgr = new RailsMailMgr();
		boolean isSent = false;
		String template = railsMailMgr.getTemplateJson(eto);
		if (template != null && !template.isEmpty()){
			isSent = sendEmailAlert(template);
		}
		return isSent;
	}
	
	public boolean sendEmailAlert(String template) {
		boolean isSent = false;
		try {
			if (template != null && !template.isEmpty()) {
				if (ALERT_SERVER_ADDR != null && !ALERT_SERVER_ADDR.isEmpty() && ALERT_SERVER_PORT != null) {
					AlertClient alertClient = new AlertClient();
					if (alertClient.sendCommand(ALERT_SERVER_ADDR, ALERT_SERVER_PORT, "add -j " + template) == 1) {
						log.warnf("ALERT201408211034, GEO20140204 - AlertClientMgr.sendEmailAlert() - mail sent, template: %s", template);
						isSent = true;
					} else {
						log.warnf("ALERT201408211034, GEO20140204 - AlertClientMgr.sendEmailAlert() - mail not sent!!!! template: %s" + template);
					}
				} else {
					log.error("ALERT201408211034, GEO20140204 - AlertClientMgr.sendEmailAlert() - Config not set for adding Alert message, ALERT_SERVER_ADDR, ALERT_SERVER_PORT!!!");
				}
			}
		} catch (Exception e) {
			log.error("ALERT201408211034, GEO20140204 - AlertClientMgr.sendEmailAlert()", e);
		}

		return isSent;
	}
}
