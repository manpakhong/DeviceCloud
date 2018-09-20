package com.littlecloud.pool.object.utils;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.DebugManager;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.json.model.config.util.info.ConfigGetInfo;
import com.littlecloud.control.json.model.config.util.info.ConfigPutTaskInfo;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.ConfigGetObject;
import com.littlecloud.pool.object.FeatureGetObject;

public class ConfigGetUtils {
	
	private static boolean PROD_MODE = DebugManager.isPROD_MODE();
	public static String sampleConfig = null;
	
	private static final Logger log = Logger.getLogger(ConfigGetUtils.class);
	
	public static boolean removeObject(ConfigGetInfo ctr) throws Exception
	{		
		if (ctr==null || ctr.getIana_id()==null || ctr.getSn()==null)
			return false;
		
		Integer ianaId = ctr.getIana_id();
		String sn = ctr.getSn();
				
		try {
			ConfigGetObject configExample = new ConfigGetObject();
			configExample.setIana_id(ianaId);
			configExample.setSn(sn);
			
			ACUtil.<ConfigGetObject> removePoolObjectBySn(configExample, ConfigGetObject.class);
			return true;
		} catch (Exception e) {
			log.error("fail to remove config object for " + ctr, e);
			return false;
		}	
	}
	
	public static ConfigGetObject getObject(ConfigGetInfo ctr) throws Exception
	{		
		if (ctr==null || ctr.getIana_id()==null || ctr.getSn()==null)
			return null;
		
		ConfigGetObject configGetObject;
		FeatureGetObject fgo;
		boolean isDevConfigTextSupport = false;
		
		Integer ianaId = ctr.getIana_id();
		String sn = ctr.getSn();
		String sid = ctr.getSid();
		StringBuilder sbSummary = ctr.getSbSummary();
		int retry = ctr.getRetry();
		String sidRelate = null;
		
		if (sid == null)
			sid = JsonUtils.genServerRef();		
	
		try {
			ConfigGetObject configExample = new ConfigGetObject();
			configExample.setIana_id(ianaId);
			configExample.setSn(sn);
			
			if (PROD_MODE)
			{
				configGetObject = ACUtil.<ConfigGetObject> getPoolObjectBySn(configExample, ConfigGetObject.class);
			}
			else
			{
				configGetObject = configExample;
				configGetObject.setConfig(sampleConfig);
				configGetObject.setStatus(200);
			}
			
			fgo = FeatureGetUtils.getFeatureGetObject(ianaId, sn);								
			if (fgo==null || fgo.getFeatures()==null)
			{
				log.errorf("getConfig - FeatureGetObject is not loaded for dev %d %s", ianaId, sn);				
				return null;
			}			
			ctr.setOut_FeatureGetObject(fgo);
			
			isDevConfigTextSupport=FeatureGetUtils.isConfigTextSupported(fgo);
			ctr.setOut_isDevConfigTextSupport(isDevConfigTextSupport);
			if (sbSummary!=null)
				sbSummary.append("\n* isDevConfigTextSupport " + isDevConfigTextSupport);
			if (configGetObject == null || configGetObject.getStatus()!=200) 
			{
				while (retry > 0 && (configGetObject == null || configGetObject.getStatus()!=200)) 
				{
					sidRelate = sid + "0";
					
					if (isDevConfigTextSupport)
					{
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_GET_TEXT, sidRelate, ianaId, sn, sn);							
					}
					else
					{
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_GET, sidRelate, ianaId, sn, sn);
					}
					
					if (log.isDebugEnabled())
						log.debugf("dev %s retrying %d (sid, sn) = (%s, %s) ", sn, 3 - retry, sidRelate, sn);
					
					Thread.sleep(3000);
	
					configGetObject = ACUtil.<ConfigGetObject> getPoolObjectBySn(configExample, ConfigGetObject.class);
					retry--;
				}
				
				if (sbSummary!=null)
					sbSummary.append("\n* - CONFIG_GET retried " + (3 - retry) + " times");
			}
		} catch (Exception e)
		{
			log.error("getConfig exception ", e);
			throw e;
		}
		
		return configGetObject;
	}
}
