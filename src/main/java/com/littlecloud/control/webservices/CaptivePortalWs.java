package com.littlecloud.control.webservices;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.logging.Logger;

import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.request.JsonCaptivePortalRequest;
import com.littlecloud.control.webservices.handler.CaptivePortalWsHandler;

@WebService
public class CaptivePortalWs extends BaseWs{
	private static final Logger log = Logger.getLogger(CaptivePortalWs.class);
	
	private enum HANDLER_NAME {
		putCpUserSession, getCpUserSession
	};
	
	private static <RESPONSE_ENTITY> String fetchWrapper(String json, HANDLER_NAME handlerName)
	{
		return BaseWs.<JsonCaptivePortalRequest, RESPONSE_ENTITY>
		fetch(json, JsonCaptivePortalRequest.class, JsonResponse.class, CaptivePortalWsHandler.class, handlerName.toString());
	}

	/* for configuration data type in request */
	private static <REQUEST, RESPONSE_ENTITY> String fetchRequestWrapper(String json, HANDLER_NAME handlerName, Class<REQUEST> requestClazz)
	{
		return BaseWs.<REQUEST, RESPONSE_ENTITY>
				fetch(json, requestClazz, JsonResponse.class, CaptivePortalWsHandler.class, handlerName.toString());
	}
	
	@WebMethod()
	public static String putCpUserSession(String json){
		return CaptivePortalWs.fetchWrapper(json,  HANDLER_NAME.putCpUserSession);
	}
	
	@WebMethod()
	public static String getCpUserSession(String json){
		return CaptivePortalWs.fetchWrapper(json,  HANDLER_NAME.getCpUserSession);
	}
	
} // end class
