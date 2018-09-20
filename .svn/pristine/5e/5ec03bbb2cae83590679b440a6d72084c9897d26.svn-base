package com.littlecloud.control.webservices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.json.JsonExclusionStrategy;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.model.Json_Devices;
import com.littlecloud.control.json.model.Json_Operation_Logs;
import com.littlecloud.control.json.model.Json_Organizations;
import com.littlecloud.control.json.request.JsonOrganizationRequest;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.handler.OrganizationWsHandler;

@WebService()
public class OrganizationWs extends BaseWs {

	private static final Logger log = Logger.getLogger(OrganizationWs.class);
	
	private enum HANDLER_NAME {
		getPepvpHubCount, getNetworks, updateNetworks, removeDevices, removeNetworks, loadTest, addNetwork, getOrganizationFeatures, updateFusionHubLicense
	};
	
	private static <RESPONSE_ENTITY> String fetchWrapper(String json, HANDLER_NAME handlerName)
	{		
		return BaseWs.<JsonOrganizationRequest, RESPONSE_ENTITY>
			fetch(json, JsonOrganizationRequest.class, JsonResponse.class, OrganizationWsHandler.class, handlerName.toString());
	}
	
	@WebMethod()
	public String getRequest() {
		Gson gson = new Gson();

		/* create request */
		JsonOrganizationRequest request = new JsonOrganizationRequest();
		request.setCaller_ref(genCallerRef());
		request.setVersion("0.1");
		request.setOrganization_id("test");

		String json = gson.toJson(request);
		return json;
	}
	
	@WebMethod
	public String getPepvpHubCount(String json)
	{
		return OrganizationWs.<List<Json_Devices>>fetchWrapper(json, HANDLER_NAME.getPepvpHubCount);
	}
	
	@WebMethod
	public String getOperationLogs(String json)
	{
		SimpleDateFormat smf = new java.text.SimpleDateFormat(JsonUtils.jsonUtcDateFormat);
		
		String serverRef = genServerRef();

		Gson gsonResp = JsonUtils.createGsonToBuilder(new JsonExclusionStrategy(Devices.class));
		JsonResponse<List<Json_Operation_Logs>> response = new JsonResponse<List<Json_Operation_Logs>>();
		response.setEllapse_time(100);

		JsonOrganizationRequest request = null;
		try {
			Gson gsonReq = new Gson();
			request = gsonReq.fromJson(json, JsonOrganizationRequest.class);
		} catch (Exception e) {
			response.setResp_code(ResponseCode.INVALID_INPUT);
			response.setError(10001);
			return gsonResp.toJson(response);
		}

		if (!request.isValidRequest()) {
			response.setResp_code(ResponseCode.MISSING_INPUT);
			response.setError(10002);
			return gsonResp.toJson(response);
		}

		if (request.getServer_ref().compareTo("0") != 0) {
			/* 2nd request for full response */
			serverRef = request.getServer_ref();

			/* check if caller_ref and server_ref exists */
			String server_ref = "0";
			try {
				server_ref = callerMap.get(request.getCaller_ref());
			} catch (NullPointerException e) {
				response.setResp_code(ResponseCode.INVALID_INPUT);
				response.setError(10003);
				return gsonResp.toJson(response);
			}

			if (request.getServer_ref().compareTo(server_ref) != 0) {
				response.setResp_code(ResponseCode.INVALID_INPUT);
				response.setError(10004);
				return gsonResp.toJson(response);
			}

			/* set old server_ref */
			response.setCaller_ref(request.getCaller_ref());
			response.setServer_ref(request.getServer_ref());
		} else {

			response.setResp_code(ResponseCode.SUCCESS);
			serverSuccessMap.put(request.getServer_ref().compareTo("0") == 0 ? serverRef : request.getServer_ref(), "");
			
			String param_orgId = request.getOrganization_id();
			
			List<Json_Operation_Logs> oplogList = new ArrayList<Json_Operation_Logs>();
			for (int i = 1;i<2000;i++)
			{
				Json_Operation_Logs oplog = new Json_Operation_Logs();
				oplog.setLabel("SSID Name");
				oplog.setNetwork_id(1);
				oplog.setNew_val("SID"+i);
				oplog.setOld_val("SID"+(i-1));
				oplog.setPage("SSID Page");
				oplog.setTimestamp(smf.format(new Date()));
				oplog.setUser_id("50YD1R");
				
				oplogList.add(oplog);
			}

			response.setData(oplogList);
			
			return gsonResp.toJson(oplogList);
		}

		return gsonResp.toJson(response);
	}
	
	@WebMethod
	public String getNetworks(String json)
	{
		Date start = new Date();
		String s = OrganizationWs.<Json_Organizations>fetchWrapper(json, HANDLER_NAME.getNetworks);
		Date end = new Date();
		log.info("Organization getNetworks costed " + (end.getTime() - start.getTime()) + "ms");
		return s;
	}
	
	@WebMethod
	public String updateNetworks(String json)
	{
		return OrganizationWs.<Json_Organizations>fetchWrapper(json, HANDLER_NAME.updateNetworks);
	}

	@WebMethod()
	public String removeDevices(String json)
	{
		return OrganizationWs.<Json_Organizations>fetchWrapper(json, HANDLER_NAME.removeDevices);
	}
	
	@WebMethod()
	public String removeNetworks(String json)
	{
		return OrganizationWs.<Json_Organizations>fetchWrapper(json, HANDLER_NAME.removeNetworks);
	}
	
	
	public enum LOAD_ACTION {
		BASIC("BASIC"), REQUEST("REQUEST"), SQL("SQL"), RESPONSE("RESPONSE");
		
        private final String abbr;
        
        private static final Map<String, LOAD_ACTION> lookup = new HashMap<String, LOAD_ACTION>();
        static {
            for (LOAD_ACTION d : LOAD_ACTION.values())
                lookup.put(d.getAbbreviation(), d);
        }

        private LOAD_ACTION(String abbreviation) {
            this.abbr = abbreviation;
        }

        public String getAbbreviation() {
            return abbr;
        }

        public static LOAD_ACTION get(String abbreviation) {
            return lookup.get(abbreviation);
        }
    }
	
	/*2.0.10*/
	@WebMethod()
	public String addNetwork(String json)
	{
		return OrganizationWs.fetchWrapper(json, HANDLER_NAME.addNetwork);
	}
	
	@WebMethod
	public String loadTestBasic(String json)
	{
		return "success";
	}
	
	@WebMethod
	public String getOrganizationFeatures(String json)
	{
		String s = OrganizationWs.<Json_Organizations>fetchWrapper(json, HANDLER_NAME.getOrganizationFeatures);
		return s;
	}
	
	@WebMethod()
	public String updateFusionHubLicense(String json)
	{
		return OrganizationWs.<Json_Organizations>fetchWrapper(json, HANDLER_NAME.updateFusionHubLicense);
	}
	
	@WebMethod 
	public String loadTest(String json)
	{	
		JsonOrganizationRequest request = JsonUtils.getRequestObject(json, JsonOrganizationRequest.class);

		request.getServer_ref().compareToIgnoreCase("BASIC");
		
		Date start = new Date();
		String s = OrganizationWs.<Json_Organizations>fetchWrapper(json, HANDLER_NAME.loadTest);
		Date end = new Date();
		//log.info("Organization getNetworks costed " + (end.getTime() - start.getTime()) + "ms");
		//
		//request.getServer_ref()
		return s;
		//return "success";
		
	}
	
//	@WebMethod
//	public String getNetwork(String json){
//		return OrganizationWs.fetchWrapper(json, HANDLER_NAME.getNetwork);
//	}
}
