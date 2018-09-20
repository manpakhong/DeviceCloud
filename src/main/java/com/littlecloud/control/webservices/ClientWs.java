package com.littlecloud.control.webservices;

import java.text.SimpleDateFormat;
import java.util.Random;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.model.Json_Clients;
import com.littlecloud.control.json.model.Json_Devices;
import com.littlecloud.control.json.request.JsonClientRequest;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.handler.ClientWsHandler;

@WebService()
public class ClientWs extends BaseWs {
	
	private static final Logger log = Logger.getLogger(ClientWs.class);
	
	private enum HANDLER_NAME {
		getDetail, getGPSLocation
	};
	
	private static <RESPONSE_ENTITY> String fetchWrapper(String json, HANDLER_NAME handlerName)
	{		
		String result = null;
		
		int callStartTime = DateUtils.getUnixtime();
		
		result = BaseWs.<JsonClientRequest, RESPONSE_ENTITY>
			fetch(json, JsonClientRequest.class, JsonResponse.class, ClientWsHandler.class, handlerName.toString());
		
		int callUseTime = DateUtils.getUnixtime() - callStartTime;
			
		if (callUseTime > 60)
			log.warnf("HANDLER_NAME %s ClientWs %s has process for %d seconds", handlerName, json, callUseTime);
		
		return result;
	}
	
	@WebMethod()
	public String getRequest() {
		Gson gson = new Gson();

		JsonClientRequest request = new JsonClientRequest();
		request.setCaller_ref(genCallerRef());
		request.setVersion("0.1");
		request.setOrganization_id("oVPZkS");
		request.setMac("11:22:33:44:55:66");

		String json = gson.toJson(request);
		return json;
	}

	@WebMethod()
	public String getDetail(String json) {
		return ClientWs.<JsonClientRequest>fetchWrapper(json, HANDLER_NAME.getDetail);
	}


	@WebMethod()
	public String getGpsLocation(String json) {
		
		return ClientWs.<JsonClientRequest>fetchWrapper(json, HANDLER_NAME.getGPSLocation);

//		SimpleDateFormat smf = new java.text.SimpleDateFormat(JsonUtils.jsonUtcDateFormat);
//		// String curDate = smf.format(new Date());
//		// String curDate = "20130313140000";
//
//		String serverRef = genServerRef();
//
//		//Gson gsonResp = new Gson();
//		Gson gsonResp = new GsonBuilder().create();
//		JsonResponse<Json_Clients> response = new JsonResponse<Json_Clients>();
//		response.setEllapse_time(100);
//
//		JsonClientRequest request = null;
//		try {
//			Gson gsonReq = new Gson();
//			request = gsonReq.fromJson(json, JsonClientRequest.class);
//		} catch (Exception e) {
//			response.setResp_code(ResponseCode.INVALID_INPUT);
//			response.setError(10001);
//			return gsonResp.toJson(response);
//		}
//
//		if (!request.isValidRequest()) {
//			response.setResp_code(ResponseCode.MISSING_INPUT);
//			response.setError(10002);
//			return gsonResp.toJson(response);
//		}
//
//		/* success */
//		response.setResp_code(ResponseCode.SUCCESS);
//		serverSuccessMap.put(request.getServer_ref().compareTo("0") == 0 ? serverRef : request.getServer_ref(), "");
//
//		response.setCaller_ref(request.getCaller_ref());
//		response.setServer_ref(request.getServer_ref());
//
//		Random rand = new Random();
//		Json_Devices dev = new Json_Devices();
//		dev.setLatitude(22.15F + rand.nextInt(100));
//		dev.setLongitude(114.10F + rand.nextInt(100));
//
//		Json_Clients c = new Json_Clients();
//		c.setDevice(dev);
//		c.setLatitude((float) (22.15 + rand.nextInt(100)));
//		c.setLongitude((float) (114.10 + rand.nextInt(100)));
//		c.setRadius((float) (12 + rand.nextInt(10)));
//
//		response.setData(c);
//		response.setEllapse_time(300);
//
//		return gsonResp.toJson(response);
	}
}
