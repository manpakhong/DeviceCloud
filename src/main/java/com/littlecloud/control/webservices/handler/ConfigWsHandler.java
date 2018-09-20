package com.littlecloud.control.webservices.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.model.Json_Devices;
import com.littlecloud.control.json.model.config.JsonConf_Admin;
import com.littlecloud.control.json.model.config.util.AdminConfigGroupLevel;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerDeviceTask;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerDeviceTask.CONFIG_UPDATE_REASON;
import com.littlecloud.control.json.model.config.util.info.AdminInfo;
import com.littlecloud.control.json.request.JsonConfigRequest_Admin;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.utils.NetUtils;

/* Note: Method must be static to avoid NullPointerException from invoke which calls static method */
public class ConfigWsHandler {

	private static final Logger log = Logger.getLogger(ConfigWsHandler.class);

	public static String getWebadminConfig(JsonConfigRequest_Admin request, JsonResponse<JsonConf_Admin> response) {
		String orgId = request.getOrganization_id();
		Integer netId = request.getNetwork_id();
				
		if (StringUtils.isEmpty(orgId) || netId == null) {
			response.setResp_code(ResponseCode.INVALID_INPUT);
			return JsonUtils.toJson(response);
		}
		
		response.setCaller_ref(request.getCaller_ref());
		response.setServer_ref(JsonUtils.genServerRef());

		try {
			/* handle group settings */
			AdminInfo adminInfo = new AdminInfo();
			adminInfo.setOrgId(orgId);
			adminInfo.setNetId(netId);
			AdminConfigGroupLevel adminGL = new AdminConfigGroupLevel(adminInfo);			
			
			response.setResp_code(ResponseCode.SUCCESS);
			response.setData(adminGL.getDatabaseConfig());
		} catch (Exception e) {
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			log.error("putWebadminConfig", e);
		}

		return JsonUtils.toJson(response);
	}
	
	public static String putWebadminConfig(JsonConfigRequest_Admin request, JsonResponse<JsonConf_Admin> response) {		
		String orgId = request.getOrganization_id();
		Integer netId = request.getNetwork_id();
		JsonConf_Admin adminConfReq = request.getData();
		
		if (StringUtils.isEmpty(orgId) || netId == null) {
			response.setResp_code(ResponseCode.INVALID_INPUT);
			return JsonUtils.toJson(response);
		}
		
		response.setCaller_ref(request.getCaller_ref());
		response.setServer_ref(JsonUtils.genServerRef());

		try {
			/* handle group settings */
			AdminInfo adminInfo = new AdminInfo();
			adminInfo.setOrgId(orgId);
			adminInfo.setNetId(netId);
			AdminConfigGroupLevel adminGL = new AdminConfigGroupLevel(adminInfo);			
			adminGL.saveConfig(adminConfReq);
			
			response.setResp_code(ResponseCode.SUCCESS);
			response.setData(adminGL.getDatabaseConfig());
			
			new ConfigUpdatePerDeviceTask(orgId, netId).performConfigUpdateNowForNetwork(response.getServer_ref(), 
					CONFIG_UPDATE_REASON.put_webadmin_config.toString());
		} catch (Exception e) {
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			log.error("putWebadminConfig", e);
		}

		return JsonUtils.toJson(response);
	}
	
	public static String getDevicesPassword(JsonConfigRequest_Admin request, JsonResponse<List<Json_Devices>> response)
	{
		String orgId = request.getOrganization_id();
		Integer netId = request.getNetwork_id();
		List<Json_Devices> jsonDevLst = new ArrayList<Json_Devices>();
		
		try {
			List<Devices> devLst = NetUtils.getDeviceLstByNetId(orgId, netId);
			for (Devices dev:devLst)
			{
				Json_Devices devJson = new Json_Devices();
				devJson.setIana_id(dev.getIanaId());
				devJson.setSn(dev.getSn());
				
				JsonConf_Admin adminConf = new JsonConf_Admin();
				adminConf.setAdmin_password(dev.getWebadmin_password());
				if (!StringUtils.isEmpty(dev.getWebadmin_user_password()))
					adminConf.setAdmin_readonly_password(dev.getWebadmin_user_password());
				adminConf.setProductId(dev.getProductId());
				
				devJson.setAdminConf(adminConf);
				jsonDevLst.add(devJson);
			}
			
			response.setResp_code(ResponseCode.SUCCESS);
			response.setData(jsonDevLst);			
		} catch (Exception e) {
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			log.error("getDevicesPassword", e);
		}
		
		return JsonUtils.toJson(response);
	}
}
