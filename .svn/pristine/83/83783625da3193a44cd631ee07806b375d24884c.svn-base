package com.littlecloud.control.webservices;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.logging.Logger;

import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.request.JsonDeviceRequest;
import com.littlecloud.control.json.request.JsonGeoFencesRequest;
import com.littlecloud.control.webservices.handler.GeoFencesWsHandler;
@WebService
public class GeoFencesWs extends BaseWs{
	private static final Logger log = Logger.getLogger(GeoFencesWs.class);
	
	private enum HANDLER_NAME {
		getGeoFences, saveGeoFences, deleteGeoFences
	};

	private static <RESPONSE_ENTITY> String fetchWrapper(String json, HANDLER_NAME handlerName)
	{
		return BaseWs.<JsonGeoFencesRequest, RESPONSE_ENTITY>
				fetch(json, JsonGeoFencesRequest.class, JsonResponse.class, GeoFencesWsHandler.class, handlerName.toString());
	}
	
	/* for configuration data type in request */
	private static <REQUEST, RESPONSE_ENTITY> String fetchRequestWrapper(String json, HANDLER_NAME handlerName, Class<REQUEST> requestClazz)
	{
		return BaseWs.<REQUEST, RESPONSE_ENTITY>
				fetch(json, requestClazz, JsonResponse.class, GeoFencesWsHandler.class, handlerName.toString());
	}
	
	@WebMethod()
	public String saveGeoFences(String json){
		return GeoFencesWs.fetchWrapper(json,  HANDLER_NAME.saveGeoFences);
	}	
	
	@WebMethod()
	public String getGeoFences(String json){
		return GeoFencesWs.fetchWrapper(json,  HANDLER_NAME.getGeoFences);
	}
	
	@WebMethod()
	public String deleteGeoFences(String json){
		return GeoFencesWs.fetchWrapper(json, HANDLER_NAME.deleteGeoFences);
	}
	
} // end class
