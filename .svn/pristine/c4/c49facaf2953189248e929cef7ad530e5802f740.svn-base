package com.littlecloud.control.webservices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.DevicesxtagsDAO;
import com.littlecloud.control.dao.TagsDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Devicesxtags;
import com.littlecloud.control.entity.DevicesxtagsId;
import com.littlecloud.control.entity.Tags;
import com.littlecloud.control.json.JsonExclusionStrategy;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.model.Json_Client_Usage;
import com.littlecloud.control.json.model.Json_Clients;
import com.littlecloud.control.json.model.Json_Device_Usage;
import com.littlecloud.control.json.model.Json_Devices;
import com.littlecloud.control.json.model.Json_Event_Logs;
import com.littlecloud.control.json.model.Json_Manufacturer_Usage;
import com.littlecloud.control.json.model.Json_Network_Time;
import com.littlecloud.control.json.model.Json_Networks;
import com.littlecloud.control.json.model.Json_PepvpnHubs;
import com.littlecloud.control.json.model.Json_PepvpnPeerDetail;
import com.littlecloud.control.json.model.Json_PepvpnStatus;
import com.littlecloud.control.json.model.Json_PepvpnTunnelStat;
import com.littlecloud.control.json.model.Json_SSID_Usage;
import com.littlecloud.control.json.model.Json_Tags;
import com.littlecloud.control.json.model.Json_Usage_Count;
import com.littlecloud.control.json.model.config.JsonConf_PepvpnProfiles;
import com.littlecloud.control.json.request.JsonNetworkRequest;
import com.littlecloud.control.json.request.JsonNetworkRequest_PepvpnProfile;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.handler.NetworkWsHandler;
import com.littlecloud.pool.object.PepvpnEndpointObject;

@WebService()
public class NetworkWs extends BaseWs {

	private static final Logger log = Logger.getLogger(NetworkWs.class);

	private static double stubTx = 20;
	private static double stubRx = 20;

	private enum HANDLER_NAME {
		getPepvpnLinks, getPepvpnHubs, getPepvpnProfile, getPepvpnEndpoints, getPepvpnPeerDetail, getPepvpnTunnelStat, putPepvpnProfile, getPepvpnFeaturedHubs,
		getClients, getClientsV3, getGpsLocationV2, getUsage, getDeviceUsage, getDeviceUsageV2, getSSIDUsage, getClientCount, getTopClient, getManufacturerUsage,
		getEventLog, getDevices, addDevicesTags, getDevicesTags, removeDevicesTags,updateTags, moveDevicesNetwork, getNetworkTime, addDevices, 
		getNetworkFeatures, addNetworks, setMasterDevice, getMasterDeviceList, syncMasterDeviceConfigs, getNetworkProductTypes, getLastGpsLocation, updateNetworkInformation
	};

	private static <RESPONSE_ENTITY> String fetchWrapper(String json, HANDLER_NAME handlerName)
	{
		String result = null;
		
		int callStartTime = DateUtils.getUnixtime();
		
		result = BaseWs.<JsonNetworkRequest, RESPONSE_ENTITY>
		fetch(json, JsonNetworkRequest.class, JsonResponse.class, NetworkWsHandler.class, handlerName.toString());
		
		int callUseTime = DateUtils.getUnixtime() - callStartTime;
		
		if (callUseTime > 60)
			log.warnf("HANDLER_NAME %s NetworkWs %s has process for %d seconds", handlerName, json, callUseTime);
		return result;
	}

	/* for configuration data type in request */
	private static <REQUEST, RESPONSE_ENTITY> String fetchRequestWrapper(String json, HANDLER_NAME handlerName, Class<REQUEST> requestClazz)
	{
		String result = null;
		
		int callStartTime = DateUtils.getUnixtime();
		
		result = BaseWs.<REQUEST, RESPONSE_ENTITY>
		fetch(json, requestClazz, JsonResponse.class, NetworkWsHandler.class, handlerName.toString());
		
		int callUseTime = DateUtils.getUnixtime() - callStartTime;
		
		if (callUseTime > 60)
			log.warnf("HANDLER_NAME %s NetworkWs %s has process for %d seconds", handlerName, json, callUseTime);
		
		return result;
	}

	/*
	 * (3) Date Format
	 * When front end request a date to server, it will send in yyyy-MM-dd format.
	 * e.g. network report
	 * When front end request a date+hour to server, it will send in yyyy-MM-dd hh:00:00 format.
	 * e.g. network map
	 * When front end request a date+time to server, it will send in yyyy-MM-dd hh:mm:ss format.
	 * e.g. event log filter
	 * Any date from server would be in Rails' format (Json UTC).
	 */

	/* Return NULL for invalid */
	@WebMethod()
	public String getRequest() {
		SimpleDateFormat smf = new java.text.SimpleDateFormat(JsonUtils.jsonRequestDateTimeFormat);

		Gson gson = new Gson();

		List<Json_Tags> tagsLst = new ArrayList<Json_Tags>();
		/* create request */
		JsonNetworkRequest request = new JsonNetworkRequest();
		request.setCaller_ref(genCallerRef());
		request.setVersion("0.1");
		request.setNetwork_id(1);
		request.setOrganization_id("test");
		request.setTag_names(Arrays.asList("ABCTAG"));
//		Arrays.asList("ABCTAG")
		request.setDevice_ids(Arrays.asList(1));

		/* for event log */
//		request.setLog_id(10);
		request.setDirection(0);
		request.setBefore(smf.format(new Date()));
		request.setDevice("Device Name");
		request.setClient("MacOrName");
		request.setEvents("EventType");
		request.setSearch_by_config(false);
		// ...

		String json = gson.toJson(request);
		return json;
	}

	@WebMethod()
	public String getPepvpnProfile(String json)
	{
		return NetworkWs.<JsonConf_PepvpnProfiles> fetchWrapper(json, HANDLER_NAME.getPepvpnProfile);
	}

	@WebMethod()
	public String putPepvpnProfile(String json)
	{
		return NetworkWs.<JsonNetworkRequest_PepvpnProfile, JsonConf_PepvpnProfiles> fetchRequestWrapper(json, HANDLER_NAME.putPepvpnProfile, JsonNetworkRequest_PepvpnProfile.class);
	}

	@WebMethod()
	public String getGpsLocation(String json)
	{
		return NetworkWs.<List<Json_Devices>> fetchWrapper(json, HANDLER_NAME.getGpsLocationV2);
	}

	@WebMethod()
	public String getUsage(String json)
	{
		return NetworkWs.<List<Json_Usage_Count>> fetchWrapper(json, HANDLER_NAME.getUsage);
		/*
		 * SimpleDateFormat smf = new java.text.SimpleDateFormat(JsonUtils.jsonUtcDateFormat);
		 * 
		 * String serverRef = genServerRef();
		 * 
		 * Gson gsonResp = JsonUtils.createGsonToBuilder(new JsonExclusionStrategy(Devices.class));
		 * JsonResponse<List<Json_Usage_Count>> response = new JsonResponse<List<Json_Usage_Count>>();
		 * response.setEllapse_time(100);
		 * 
		 * JsonNetworkRequest request = null;
		 * try {
		 * Gson gsonReq = new Gson();
		 * request = gsonReq.fromJson(json, JsonNetworkRequest.class);
		 * } catch (Exception e) {
		 * response.setResp_code(ResponseCode.INVALID_INPUT);
		 * response.setError(10001);
		 * return gsonResp.toJson(response);
		 * }
		 * 
		 * if (!request.isValidRequest()) {
		 * response.setResp_code(ResponseCode.MISSING_INPUT);
		 * response.setError(10002);
		 * return gsonResp.toJson(response);
		 * }
		 * 
		 * if (request.getServer_ref().compareTo("0") != 0) {
		 * 2nd request for full response
		 * serverRef = request.getServer_ref();
		 * 
		 * check if caller_ref and server_ref exists
		 * String server_ref = "0";
		 * try {
		 * server_ref = callerMap.get(request.getCaller_ref());
		 * } catch (NullPointerException e) {
		 * response.setResp_code(ResponseCode.INVALID_INPUT);
		 * response.setError(10003);
		 * return gsonResp.toJson(response);
		 * }
		 * 
		 * if (request.getServer_ref().compareTo(server_ref) != 0) {
		 * response.setResp_code(ResponseCode.INVALID_INPUT);
		 * response.setError(10004);
		 * return gsonResp.toJson(response);
		 * }
		 * 
		 * set old server_ref
		 * response.setCaller_ref(request.getCaller_ref());
		 * response.setServer_ref(request.getServer_ref());
		 * } else {
		 * 
		 * response.setResp_code(ResponseCode.SUCCESS);
		 * serverSuccessMap.put(request.getServer_ref().compareTo("0") == 0 ? serverRef : request.getServer_ref(), "");
		 * int param_networkId = request.getNetwork_id();
		 * String param_orgId = request.getOrganization_id();
		 * 
		 * create stub data
		 * List<Json_Usage_Count> result = new ArrayList<Json_Usage_Count>();
		 * for (int i = 11; i <= 19; i++)
		 * {
		 * Json_Usage_Count usageCount = new Json_Usage_Count();
		 * usageCount.setDay("2013-07-" + i + " 00:00:00");
		 * usageCount.setValue(1200000000l);
		 * result.add(usageCount);
		 * usageCount = new Json_Usage_Count();
		 * usageCount.setDay("2013-07-" + i + " 12:00:00");
		 * usageCount.setValue(1200000000l);
		 * result.add(usageCount);
		 * }
		 * 
		 * response.setData(result);
		 * 
		 * return gsonResp.toJson(result);
		 * }
		 * 
		 * return gsonResp.toJson(response);
		 */
	}

	@WebMethod()
	public String getDeviceUsage(String json)
	{
//		return NetworkWs.<List<Json_Device_Usage>> fetchWrapper(json, HANDLER_NAME.getDeviceUsageV2);
		return NetworkWs.<HashMap<String, List<Json_Device_Usage>>> fetchWrapper(json, HANDLER_NAME.getDeviceUsageV2);
	}

	@WebMethod()
	public String getAPUsage(String json)
	{
		SimpleDateFormat smf = new java.text.SimpleDateFormat(JsonUtils.jsonUtcDateFormat);

		String serverRef = genServerRef();

		Gson gsonResp = JsonUtils.createGsonToBuilder(new JsonExclusionStrategy(Devices.class));
		JsonResponse<List<Json_Event_Logs>> response = new JsonResponse<List<Json_Event_Logs>>();
		response.setEllapse_time(100);

		JsonNetworkRequest request = null;
		try {
			Gson gsonReq = new Gson();
			request = gsonReq.fromJson(json, JsonNetworkRequest.class);
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
			int param_networkId = request.getNetwork_id();
			String param_orgId = request.getOrganization_id();

			/* create stub data */
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			for (int i = 1; i < 10; i++)
			{
				Map<String, Object> myMap = new TreeMap<String, Object>();
				myMap.put("device_id", i);
				myMap.put("device_name", "AP" + i);
				myMap.put("model_name", "MAX AP1");
				myMap.put("usage", 10);
				myMap.put("clients_percent", 10);
				result.add(myMap);

			}

			return gsonResp.toJson(result);
		}
		return gsonResp.toJson(response);
	}

	@WebMethod()
	public String getManufacturerUsage(String json)
	{
		return NetworkWs.<HashMap<String,List<Json_Manufacturer_Usage>>> fetchWrapper(json, HANDLER_NAME.getManufacturerUsage);
	}

	@WebMethod()
	public String getTopClient(String json)
	{
		return NetworkWs.<List<Json_Client_Usage>> fetchWrapper(json, HANDLER_NAME.getTopClient);
	}

	@WebMethod()
	public String getClientCount(String json)
	{
		return NetworkWs.<List<Json_Usage_Count>> fetchWrapper(json, HANDLER_NAME.getClientCount);
	}

	@WebMethod()
	public String getSSIDUsage(String json)
	{
		return NetworkWs.<List<Json_SSID_Usage>> fetchWrapper(json, HANDLER_NAME.getSSIDUsage);
	}

	@WebMethod()
	public String getEventLog(String json)
	{
		Date start = new Date();
		String s = NetworkWs.<List<Json_Event_Logs>> fetchWrapper(json, HANDLER_NAME.getEventLog);
		Date end = new Date();
		log.info("Network getEventLog costed " + (end.getTime() - start.getTime()) + "ms");
		return s;
	}

	@WebMethod()
	public String updateNetworkInformation(String json)
	{
		return NetworkWs.<JsonResponse<Json_Networks>> fetchWrapper(json, HANDLER_NAME.updateNetworkInformation);
	}

	@WebMethod()
	/* get list of endpoints detail from parameters sn */
	public String getPepvpnTunnelStat(String json)
	{
		return NetworkWs.<List<Json_PepvpnTunnelStat>> fetchWrapper(json, HANDLER_NAME.getPepvpnTunnelStat);
	}

	@WebMethod()
	public String getPepvpnPeerDetail(String json)
	{
		return NetworkWs.<Json_PepvpnPeerDetail> fetchWrapper(json, HANDLER_NAME.getPepvpnPeerDetail);
	}

	@WebMethod()
	/* Important assumption - If not configured as hub, then endpoints */
	public String getPepvpnFeaturedHubs(String json)
	{
		return NetworkWs.<List<Json_PepvpnHubs>> fetchWrapper(json, HANDLER_NAME.getPepvpnFeaturedHubs);
	}

	@WebMethod()
	/* Important assumption - If not configured as hub, then endpoints */
	public String getPepvpnHubs(String json)
	{
		return NetworkWs.<List<Json_PepvpnHubs>> fetchWrapper(json, HANDLER_NAME.getPepvpnHubs);
	}

	@WebMethod()
	public String getPepvpnLinks(String json)
	{
		return NetworkWs.<PepvpnEndpointObject> fetchWrapper(json, HANDLER_NAME.getPepvpnLinks);
	}

	@WebMethod()
	public String getDevicesTags(String json)
	{
		return NetworkWs.<List<Json_Tags>> fetchWrapper(json, HANDLER_NAME.getDevicesTags);
	}

	@WebMethod()
	public String addDevices(String json)
	{		
		return NetworkWs.fetchWrapper(json, HANDLER_NAME.addDevices);
	}
	
	@WebMethod()
	public String addDevicesTags(String json)
	{
		return NetworkWs.<List<Json_Tags>> fetchWrapper(json, HANDLER_NAME.addDevicesTags);
	}

	@WebMethod()
	public String removeDevicesTags(String json)
	{		
		return NetworkWs.<List<Json_Tags>> fetchWrapper(json, HANDLER_NAME.removeDevicesTags);
	}

	@WebMethod()
	public String updateTags(String json)
	{
		return NetworkWs.<List<Json_Tags>> fetchWrapper(json, HANDLER_NAME.updateTags);
	}
	
	@WebMethod()
	/* get list of endpoints detail from parameters sn */
	public String getPepvpnEndpoints(String json)
	{
		return NetworkWs.<List<Json_PepvpnStatus>> fetchWrapper(json, HANDLER_NAME.getPepvpnEndpoints);
	}
	
	@WebMethod()
	public String getDevices(String json)
	{
		return NetworkWs.<List<Json_Devices>> fetchWrapper(json, HANDLER_NAME.getDevices);
	}

	@WebMethod()
	public String getClients(String json)
	{
		return NetworkWs.<List<Json_Clients>> fetchWrapper(json, HANDLER_NAME.getClientsV3);
	}
	
	@WebMethod()
	public String getClientsVTest(String json)
	{
		return NetworkWs.<List<Json_Clients>> fetchWrapper(json, HANDLER_NAME.getClientsV3);
	}

	@WebMethod()
	public String getNetworkTime(String json)
	{
		return NetworkWs.<Json_Network_Time>fetchWrapper(json, HANDLER_NAME.getNetworkTime);
	}
	
	@WebMethod()
	public String getNetworkFeatures(String json)
	{
		return NetworkWs.<Json_Networks>fetchWrapper(json, HANDLER_NAME.getNetworkFeatures);
	}
	
//	@WebMethod()
//	public String removeDevices(String json)
//	{
//		return NetworkWs.fetchWrapper(json, HANDLER_NAME.removeDevices);
//	}

	@WebMethod()
	public String moveDevicesNetwork(String json)
	{
		return NetworkWs.fetchWrapper(json, HANDLER_NAME.moveDevicesNetwork);
	}
	
	@WebMethod()
	public String setMasterDevice(String json){
		return NetworkWs.fetchWrapper(json, HANDLER_NAME.setMasterDevice);
	}
	
	@WebMethod()
	public String getMasterDeviceList(String json){
		return NetworkWs.fetchWrapper(json,  HANDLER_NAME.getMasterDeviceList);
	}
	
	@WebMethod()
	public String syncMasterDeviceConfigs (String json){
		return NetworkWs.fetchWrapper(json,  HANDLER_NAME.syncMasterDeviceConfigs);
	}
	
	@WebMethod()
	public String getNetworkProductTypes(String json)
	{
		return NetworkWs.<Json_Networks>fetchWrapper(json, HANDLER_NAME.getNetworkProductTypes);
	}
	
	@WebMethod()
	public String getLastGpsLocation(String json)
	{
		return NetworkWs.<List<Json_Devices>>fetchWrapper(json, HANDLER_NAME.getLastGpsLocation);
	}
	
/*2.0.10
	@WebMethod()
	public String addNetworks(String json)
	{
		return NetworkWs.fetchWrapper(json, HANDLER_NAME.addNetworks);
	}
*/	
	// @WebMethod()
	// public String getPepvpnEndpoints(String json)
	// {
	// stubTx += StubUtils.randomTxRx();
	// stubRx += StubUtils.randomTxRx();
	//
	// String serverRef = genServerRef();
	//
	// Gson gsonResp = JsonUtils.createGsonFromBuilder(new JsonExclusionStrategy(Devices.class));
	// JsonResponse<List<Json_Tags>> response = new JsonResponse<List<Json_Tags>>();
	// response.setEllapse_time(100);
	//
	// JsonNetworkRequest request = null;
	// try {
	// Gson gsonReq = new Gson();
	// request = gsonReq.fromJson(json, JsonNetworkRequest.class);
	// } catch (Exception e) {
	// response.setResp_code(ResponseCode.INVALID_INPUT);
	// response.setError(10001);
	// return gsonResp.toJson(response);
	// }
	//
	// if (!request.isValidRequest()) {
	// response.setResp_code(ResponseCode.MISSING_INPUT);
	// response.setError(10002);
	// return gsonResp.toJson(response);
	// }
	//
	// if (request.getServer_ref().compareTo("0") != 0) {
	// /* 2nd request for full response */
	// serverRef = request.getServer_ref();
	//
	// /* check if caller_ref and server_ref exists */
	// String server_ref = "0";
	// try {
	// server_ref = callerMap.get(request.getCaller_ref());
	// } catch (NullPointerException e) {
	// response.setResp_code(ResponseCode.INVALID_INPUT);
	// response.setError(10003);
	// return gsonResp.toJson(response);
	// }
	//
	// if (request.getServer_ref().compareTo(server_ref) != 0) {
	// response.setResp_code(ResponseCode.INVALID_INPUT);
	// response.setError(10004);
	// return gsonResp.toJson(response);
	// }
	//
	// /* set old server_ref */
	// response.setCaller_ref(request.getCaller_ref());
	// response.setServer_ref(request.getServer_ref());
	// } else {
	//
	// response.setResp_code(ResponseCode.SUCCESS);
	// serverSuccessMap.put(request.getServer_ref().compareTo("0") == 0 ? serverRef : request.getServer_ref(), "");
	// int param_networkId = request.getNetwork_id();
	// String param_orgId = request.getOrganization_id();
	//
	// // return
	// //
	// StubUtils.createStubJson("{'sn':'1000-XXXX-XXXX-XXXX','linkinfo':{'order_list':[1,2],'link_list':[{'id':1,'enable':true,'ip_forward':true,'name':'Core1','static_ip':'10.80.9.1','static_maskn':'16','conn_method':'static'},{'id':2,'enable':true,'ip_forward':true,'name':'Core2','static_ip':'10.80.9.2','static_maskn':'16','conn_method':'static'}]},'mvpn_order_list':[1,2],'mvpn_conn_list':[{'id':1,'sn':'9000-XXXX-XXXX-XXXX','main_state':'established','security_state':'aes256','version':'6','name':'Michael Home1','remoteid_list':['1231231231231','2342342342341'],'enable':true,'conn_list':[1],'network_list':['192.168.30.0/24'],'conn_in_use_list':[1],'stat_list':[{'id':1,'timestamp':1364989791,'lost':300,'rtt':4,'tx':27368800,'rx':12106816}]},{'id':2,'sn':'9000-XXXX-XXXX-XXXX','main_state':'established','security_state':'aes256','version':'6','name':'Michael Home2','remoteid_list':['1231231231232','2342342342342'],'enable':true,'conn_list':[1],'network_list':['192.168.30.0/24'],'conn_in_use_list':[1],'stat_list':[{'id':1,'timestamp':1364989791,'lost':600,'rtt':8,'tx':54737600,'rx':24213632}]}]}");
	// return StubUtils.createStubJson(StubUtils.genPepvpnEndpoints());
	// }
	//
	// return gsonResp.toJson(response);
	// }

	// @WebMethod()
	// public String getPepvpnHubs(String json)
	// {
	// String serverRef = genServerRef();
	//
	// Gson gsonResp = JsonUtils.createGsonFromBuilder(new JsonExclusionStrategy(Devices.class));
	// JsonResponse<List<Json_Tags>> response = new JsonResponse<List<Json_Tags>>();
	// response.setEllapse_time(100);
	//
	// JsonNetworkRequest request = null;
	// try {
	// Gson gsonReq = new Gson();
	// request = gsonReq.fromJson(json, JsonNetworkRequest.class);
	// } catch (Exception e) {
	// response.setResp_code(ResponseCode.INVALID_INPUT);
	// response.setError(10001);
	// return gsonResp.toJson(response);
	// }
	//
	// if (!request.isValidRequest()) {
	// response.setResp_code(ResponseCode.MISSING_INPUT);
	// response.setError(10002);
	// return gsonResp.toJson(response);
	// }
	//
	// if (request.getServer_ref().compareTo("0") != 0) {
	// /* 2nd request for full response */
	// serverRef = request.getServer_ref();
	//
	// /* check if caller_ref and server_ref exists */
	// String server_ref = "0";
	// try {
	// server_ref = callerMap.get(request.getCaller_ref());
	// } catch (NullPointerException e) {
	// response.setResp_code(ResponseCode.INVALID_INPUT);
	// response.setError(10003);
	// return gsonResp.toJson(response);
	// }
	//
	// if (request.getServer_ref().compareTo(server_ref) != 0) {
	// response.setResp_code(ResponseCode.INVALID_INPUT);
	// response.setError(10004);
	// return gsonResp.toJson(response);
	// }
	//
	// /* set old server_ref */
	// response.setCaller_ref(request.getCaller_ref());
	// response.setServer_ref(request.getServer_ref());
	// } else {
	//
	// response.setResp_code(ResponseCode.SUCCESS);
	// serverSuccessMap.put(request.getServer_ref().compareTo("0") == 0 ? serverRef : request.getServer_ref(), "");
	// int param_networkId = request.getNetwork_id();
	// String param_orgId = request.getOrganization_id();
	//
	// return
	// StubUtils.createStubJson("[{'id': 1,'name': 'Peplink Balance 1350','peer_networks': [{  'id': 1,  'name': 'Network1',  'endpointz': 3  }  {  'id': 2,  'name': 'Network2',  'endpointz': 7  }]}{'id': 2,'name': 'Peplink Balance 2350','peer_networks': [{  'id': 1,  'name': 'Network1',  'endpointz': 3  }  {  'id': 3,  'name': 'Network3',  'endpointz': 9  }]}]");
	// }
	//
	// return gsonResp.toJson(response);
	// return "testing";
	// }

}
