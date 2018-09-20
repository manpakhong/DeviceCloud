package com.littlecloud.control.webservices;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.logging.Logger;

import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.request.JsonFirmwareRequest;
import com.littlecloud.control.webservices.handler.FirmwareWsHandler;

@WebService
public class FirmwareWs 
{
	private static final Logger log = Logger.getLogger(FirmwareWs.class);
	
	private enum HANDLER_NAME 
	{
		saveFirmwareUpgradeSchedule,getProductFirmwareVersions
	};	
	
	private static <RESPONSE_ENTITY> String fetchWrapper(String json, HANDLER_NAME handlerName)
	{	
		return BaseWs.<JsonFirmwareRequest, RESPONSE_ENTITY>
			fetch(json, JsonFirmwareRequest.class, JsonResponse.class, FirmwareWsHandler.class, handlerName.toString());
	}
	
	private static <REQUEST, RESPONSE_ENTITY> String fetchRequestWrapper(String json, HANDLER_NAME handlerName, Class<REQUEST> requestClazz)
	{		
		return BaseWs.<REQUEST, RESPONSE_ENTITY>
			fetch(json, requestClazz, JsonResponse.class, FirmwareWsHandler.class, handlerName.toString());
	}
	
	@WebMethod()
	public String saveFirmwareUpgradeSchedule(String json)
	{
		return FirmwareWs.fetchWrapper(json, HANDLER_NAME.saveFirmwareUpgradeSchedule);
	}
	
	@WebMethod()
	public String getProductFirmwareVersions(String json)
	{
		return FirmwareWs.fetchWrapper(json, HANDLER_NAME.getProductFirmwareVersions);
	}
	
}
