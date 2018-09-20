package com.littlecloud.control.webservices.handler;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.entity.GeoFences;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.model.Json_Geo_Fences;
import com.littlecloud.control.json.request.JsonGeoFencesRequest;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.DevGeoFencesObject;
import com.littlecloud.pool.object.utils.GeoFencesUtils;
// please be reminded that method must have static, otherwise cannot be called.
public class GeoFencesWsHandler {
	private static final Logger log = Logger.getLogger(GeoFencesWsHandler.class);
	
	public static String saveGeoFences(JsonGeoFencesRequest request, JsonResponse response){
		Integer param_network_id = request.getNetwork_id();
		String param_organization_id = request.getOrganization_id();
		try{			
			if (request != null){
				GeoFences geoFences = GeoFencesUtils.convert2GeoFences(request);
				boolean isSaved = GeoFencesUtils.saveGeoFences(geoFences, param_organization_id);
				if (isSaved){
					response.setResp_code(ResponseCode.SUCCESS);
					if (log.isDebugEnabled()){
						log.debugf("GEO20140204 - saveGeoFences - param_network_id: %s, param_organization_id: %s, jsonRequest String: %s", param_network_id, param_organization_id, JsonUtils.toJson(request));
					}
				} else {
					response.setResp_code(ResponseCode.INTERNAL_ERROR);
					response.setMessage("Save failure!");
					if (log.isDebugEnabled()){
						log.debugf("GEO20140204 - saveGeoFences - param_network_id: %s, param_organization_id: %s, jsonRequest String: %s", param_network_id, param_organization_id, JsonUtils.toJson(request));						
					}
				}
			} // end if (request != null)

		} catch (Exception e){
			log.error("GEO20140204 - saveGeoFences - " + e, e);
			response.setResp_code(ResponseCode.UNDEFINED);
			return JsonUtils.toJson(response);
		} // end try ... catch ...
		
		return JsonUtils.toJson(response);
	} // end saveGeoFences()
	
	public static String getGeoFences(JsonGeoFencesRequest request, JsonResponse<List<Json_Geo_Fences>> response){
		Integer param_network_id = request.getNetwork_id();
		String param_organization_id = request.getOrganization_id();
		List<Json_Geo_Fences> jsonGeoFencesList = new ArrayList<Json_Geo_Fences>();
		if (log.isDebugEnabled()){
			log.debugf("GEO20140204 - getGeoFences - param_network_id: %s, param_organization_id: %s", param_network_id, param_organization_id);	
		}
		try{
			if (param_network_id != null && param_organization_id != null){
				jsonGeoFencesList = GeoFencesUtils.getJsonGeoFencesList(param_network_id, param_organization_id);
			} // end if (param_network_id != null && param_organization_id != null)
			response.setResp_code(ResponseCode.SUCCESS);
			response.setData(jsonGeoFencesList);
		} catch (Exception e){
			log.error("GEO20140204 - getGeoFences - " + e, e);
			response.setResp_code(ResponseCode.UNDEFINED);
			return JsonUtils.toJson(response);
		} // end try ... catch (Exception e)
		if (log.isDebugEnabled()){
			log.debugf("GEO20140204 - getGeoFences - param_network_id: %s, param_organization_id: %s, response: %s", param_network_id, param_organization_id, JsonUtils.toJson(response));
		}	
		return JsonUtils.toJson(response);		 
	} // end getGeoFences()
	
	public static String deleteGeoFences(JsonGeoFencesRequest request, JsonResponse response){
		List<Integer> param_geo_id_list = request.getGeo_id_list();
		Integer param_network_id = request.getNetwork_id();
		String param_organization_id = request.getOrganization_id();
		try{
			
			if (param_geo_id_list != null && param_geo_id_list.size() > 0 && param_network_id != null && param_organization_id != null){
				boolean areDeleted = GeoFencesUtils.deleteGeoFences(param_geo_id_list,param_network_id, param_organization_id);
				if (areDeleted){
					response.setResp_code(ResponseCode.SUCCESS);
				} else {
					response.setResp_code(ResponseCode.INTERNAL_ERROR);
					response.setMessage("Something wrong! Cannot be deleted!");
				}
			} // end if (param_geo_id_list != null)
		}catch (Exception e){
			log.error("GEO20140204 - deleteGeoFences - " + e, e);
			response.setResp_code(ResponseCode.UNDEFINED);
			return JsonUtils.toJson(response);
		} // end try ... catch (Exception e)
		return JsonUtils.toJson(response);
	} // end deleteGeoFences(JsonNetworkRequest_GeoFences request, JsonResponse response)
} // end class
