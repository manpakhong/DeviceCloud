package com.littlecloud.services;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

import com.littlecloud.pool.utils.PropertyService;

public class CaptivePortalHealthMgr {
	private static final Logger log = Logger.getLogger(CaptivePortalHealthMgr.class);
	
	private static PropertyService<CaptivePortalHealthMgr> ps = new PropertyService<CaptivePortalHealthMgr>(CaptivePortalHealthMgr.class);
	private static String PORTAL_HEALTH_CHECK_URL;
	
	public CaptivePortalHealthMgr(){
		init();
	}
	private void init(){
		try{
			if (PORTAL_HEALTH_CHECK_URL == null || PORTAL_HEALTH_CHECK_URL.isEmpty()){
				PORTAL_HEALTH_CHECK_URL = ps.getString("PORTAL_HEALTH_CHECK_URL");
			}
		} catch (Exception e){
			log.error("CAPHEALTH20140911 - CaptivePortalHealthMgr.init() - ", e);
		}
	}
	public String askCaptivePortalServiceHealthStatus(){
		String rtn = "";
		try{
			if (PORTAL_HEALTH_CHECK_URL != null && !PORTAL_HEALTH_CHECK_URL.isEmpty()){
				URL url = new URL(PORTAL_HEALTH_CHECK_URL);
				URLConnection con = url.openConnection();
				InputStream in = con.getInputStream();
				String encoding = con.getContentEncoding();
				encoding = encoding == null ? "UTF-8" : encoding;
				rtn = IOUtils.toString(in, encoding);
			}
		} catch (Exception e){
			log.error("CAPHEALTH20140911 - CaptivePortalHealthMgr.askCaptivePortalServiceHealthStatus() - ", e);
		}
		return rtn;
	}
	
}
