package com.littlecloud.control.webservices;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.model.Json_Devices;
import com.littlecloud.control.json.model.config.JsonConf_Admin;
import com.littlecloud.control.json.request.JsonConfigRequest_Admin;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.handler.ConfigWsHandler;

@WebService()
public class ConfigWs extends BaseWs {
	
	private static final Logger log = Logger.getLogger(ConfigWs.class);
	
	private enum HANDLER_NAME {
		getWebadminConfig, putWebadminConfig, getDevicesPassword
	};
	
	private static <REQUEST, RESPONSE_ENTITY> String fetchRequestWrapper(String json, HANDLER_NAME handlerName, Class<REQUEST> requestClazz)
	{	
		return BaseWs.<REQUEST, RESPONSE_ENTITY>
			fetch(json, requestClazz, JsonResponse.class, ConfigWsHandler.class, handlerName.toString());
	}
	
	@WebMethod()
	public String getRequest() {
		Gson gson = new Gson();

		JsonConfigRequest_Admin request = new JsonConfigRequest_Admin();
		request.setCaller_ref(JsonUtils.genServerRef());
		request.setVersion("0.1");
		request.setOrganization_id("oVPZkS");
		request.setNetwork_id(10);

		String json = gson.toJson(request);
		return json;
	}

	@WebMethod()
	public String getWebadminConfig(String json) {

		return ConfigWs.<JsonConfigRequest_Admin, JsonConf_Admin>fetchRequestWrapper(json, HANDLER_NAME.getWebadminConfig, JsonConfigRequest_Admin.class);
	}
	
	@WebMethod()
	public String putWebadminConfig(String json) {

		return ConfigWs.<JsonConfigRequest_Admin, JsonConf_Admin>fetchRequestWrapper(json, HANDLER_NAME.putWebadminConfig, JsonConfigRequest_Admin.class);
	}
	
	@WebMethod()
	public String getDevicesPassword(String json) {

		return ConfigWs.<JsonConfigRequest_Admin, List<Json_Devices>>fetchRequestWrapper(json, HANDLER_NAME.getDevicesPassword, JsonConfigRequest_Admin.class);
	}
}
