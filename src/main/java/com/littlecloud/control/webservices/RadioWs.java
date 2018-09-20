package com.littlecloud.control.webservices;

import javax.jws.WebMethod;
import javax.jws.WebService;

import net.sf.json.JSONObject;

import org.jboss.logging.Logger;

import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.control.json.JsonRequest;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings;
import com.littlecloud.control.json.request.JsonRadioRequest;
import com.littlecloud.control.json.request.JsonRadioRequest_DataRadioSettings;
import com.littlecloud.control.json.request.JsonRadioRequest_DataSsidProfiles;
import com.littlecloud.control.json.request.JsonRadioRequest_DataSsidProfilesList;
import com.littlecloud.control.json.request.JsonRadioRequest_SsidAvailability;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.handler.RadioWsHandler;

@WebService
public class RadioWs {
	private static final Logger log = Logger.getLogger(RadioWs.class);

	private enum HANDLER_NAME {
		getRequest, getRadioConfig, putSsidProfileConfig, putSsidProfileConfigList, putSsidAvailability, putRadioConfig, updateAutoChannel
	};	
	
	private static <RESPONSE_ENTITY> String fetchWrapper(String json, HANDLER_NAME handlerName)
	{	
		return BaseWs.<JsonRadioRequest, RESPONSE_ENTITY>
			fetch(json, JsonRadioRequest.class, JsonResponse.class, RadioWsHandler.class, handlerName.toString());
	}
	
	private static <REQUEST, RESPONSE_ENTITY> String fetchRequestWrapper(String json, HANDLER_NAME handlerName, Class<REQUEST> requestClazz)
	{		
		return BaseWs.<REQUEST, RESPONSE_ENTITY>
			fetch(json, requestClazz, JsonResponse.class, RadioWsHandler.class, handlerName.toString());
	}
	
	@WebMethod()
	public String getRequest() {
		return RadioWs.<JsonConf_RadioSettings>fetchWrapper("{'organization_id':'test','network_id':1,'caller_ref':'2013041815310179498','server_ref':'0','version':'0.1'}", HANDLER_NAME.getRequest);
	}
	
	@WebMethod()
	public String getRadioConfig(String json)
	{	
		return RadioWs.<JsonConf_RadioSettings>fetchWrapper(json, HANDLER_NAME.getRadioConfig);
	}
	
	@WebMethod()
	public String putSsidProfileConfig(String json)
	{	
		return RadioWs.<JsonRadioRequest_DataSsidProfiles, JsonConf_RadioSettings>fetchRequestWrapper(json, HANDLER_NAME.putSsidProfileConfig, JsonRadioRequest_DataSsidProfiles.class);
	}
	
	@WebMethod()
	public String putSsidProfileConfigList(String json)
	{	
//		JsonRadioRequest req = JsonUtils.<JsonRadioRequest>fromJson(json, JsonRadioRequest.class);
//		req.getVersion();
		return RadioWs.<JsonRadioRequest_DataSsidProfilesList, JsonConf_RadioSettings>fetchRequestWrapper(json, HANDLER_NAME.putSsidProfileConfigList, JsonRadioRequest_DataSsidProfilesList.class);
	}
	
	@WebMethod()
	public String putSsidAvailability(String json)
	{	
		return RadioWs.<JsonRadioRequest_SsidAvailability, JsonConf_RadioSettings>fetchRequestWrapper(json, HANDLER_NAME.putSsidAvailability, JsonRadioRequest_SsidAvailability.class);
	}
	
	@WebMethod()
	public String putRadioConfig(String json)
	{	
		return RadioWs.<JsonRadioRequest_DataRadioSettings, JsonConf_RadioSettings>fetchRequestWrapper(json, HANDLER_NAME.putRadioConfig, JsonRadioRequest_DataRadioSettings.class);
	}
	
	@WebMethod()
	public String updateAutoChannel(String json)
	{	
		return RadioWs.fetchWrapper(json, HANDLER_NAME.updateAutoChannel);
	}
}
