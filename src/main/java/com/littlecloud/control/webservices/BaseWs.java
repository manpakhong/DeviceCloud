package com.littlecloud.control.webservices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.littlecloud.ac.ACService;
import com.littlecloud.ac.health.info.WebServiceInfo;
import com.littlecloud.ac.util.ACLicenseManager;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.ACLicenseObject;

public class BaseWs {
	private static final Logger log = Logger.getLogger(BaseWs.class);

	protected static ConcurrentHashMap<String, String> callerMap = new ConcurrentHashMap<String, String>();
	protected static ConcurrentHashMap<String, String> serverMap = new ConcurrentHashMap<String, String>();
	protected static ConcurrentHashMap<String, String> serverSuccessMap = new ConcurrentHashMap<String, String>(); // for simulation

	protected String genCallerRef() {
		return genServerRef();
	}

	protected String genServerRef() {
		return JsonUtils.genServerRef();
	}

	protected boolean randomSuccess() {
		Random rand = new Random(System.nanoTime());
		return (Math.abs(rand.nextLong()) + 1) % 1 == 0;
	}

	protected static <REQUEST, RESPONSE_ENTITY> String fetch
			(String json, Class<REQUEST> clazzREQ, Class<?> clazzRES, Class<?> clazzHAN, String methodName)
	{
		if (log.isInfoEnabled())
			log.infof(clazzHAN.getSimpleName()+"("+methodName+") is called with json = %s ", json);
				
		REQUEST request = JsonUtils.getRequestObject(json, clazzREQ);
		
		String ws_name = clazzHAN.getSimpleName()+"_"+methodName;
		WsTasksInfo taskInfo = new WsTasksInfo(json, clazzREQ, ws_name);		
		String key = taskInfo.startTask();
		
		JsonResponse<RESPONSE_ENTITY> response = JsonUtils.<RESPONSE_ENTITY, REQUEST> getResponseObject(request);
		
		if (ACLicenseManager.isLicenseExpired())
		{
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			response.setError(99999);
			response.setMessage("LICENSE_EXPIRED");
			taskInfo.endTask(key);
			return JsonUtils.toJson(response);
		}		
		
		if (request == null)
		{
			log.debug("json="+json);
			response.setResp_code(ResponseCode.INVALID_INPUT);
			response.setError(10001);
			taskInfo.endTask(key);
			return JsonUtils.toJson(response);
		}

		try {
			Method method = clazzHAN.getDeclaredMethod(methodName, clazzREQ, clazzRES);
			
			return (String) method.invoke(null, request, response);

		} catch (NoSuchMethodException | SecurityException e) {
			log.error("Exception for getDeclaredMethod " + e,e);

			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			response.setError(10002);
			return JsonUtils.toJson(response);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.error("Exception for invoke " + e,e);

			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			response.setError(10003);
			return JsonUtils.toJson(response);
		}
		finally
		{
			taskInfo.endTask(key);
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			String json = "{\"caller_ref\":\"2014060508523266112\",\"server_ref\":0,\"version\":\"0.1\",\"organization_id\":\"riMA5x\",\"device_id\":\"5799\"}";
			JsonParser jp = new JsonParser();
			JsonElement ele = jp.parse(json);
			JsonObject obj = ele.getAsJsonObject();
			ele = obj.get("organization_id");
			System.out.println(ele.getAsString());
		}
		catch(Exception e)
		{
			System.out.print("sd");
		}
	}
}
