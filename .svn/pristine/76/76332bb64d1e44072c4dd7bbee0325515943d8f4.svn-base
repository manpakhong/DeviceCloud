package com.littlecloud.control.webservices.handler;

//import java.text.NumberFormat;
//import java.text.SimpleDateFormat;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.ConfigurationRadioChannelsDAO;
import com.littlecloud.control.dao.DeviceUpdatesDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.model.Json_Tunnel;
import com.littlecloud.control.json.model.Json_Web_Tunneling;
import com.littlecloud.control.json.request.JsonTunnelRequest;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.util.NetworkUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.TunnelObject;
import com.littlecloud.pool.object.WebTunnelingObject;
import com.littlecloud.pool.object.utils.NetUtils;

/* Note: Method must be static to avoid NullPointerException from invoke which calls static method */
public class TunnelWsHandler {

	private static final Logger log = Logger.getLogger(TunnelWsHandler.class);
	
	public static String startWebAdminTunnel(JsonTunnelRequest request, JsonResponse<Json_Tunnel> response) {
		
		/*
		 * - parameter: device_id
		 * - parameter: ra_domain
		 * - Check if device is online, if not, return message "Device is offline"
		 * - Check if device RA status is on, if not, switch on the device RA  (i.e. Send "s" action thru WTP)
		 * - Start a proxy tunnel on this device
		 * - Return the local web admin port number and hostname to API server
		 */
		
		String param_orgId = request.getOrganization_id();
		String param_devSn = request.getSn();
		Integer param_ianaId = request.getIana_id();
		String param_ra_domain = request.getRa_domain();
		String param_ssh_host_key = request.getSsh_host_key();
		Boolean isRetry = request.getRetry()==null?false:request.getRetry();
		Json_Tunnel tunnel = new Json_Tunnel();
		
		try 
		{
			DevicesDAO deviceDAO = new DevicesDAO(param_orgId);
			Devices devices = deviceDAO.findBySn(param_ianaId, param_devSn);
			if( devices != null )
			{
				DevOnlineObject devO = new DevOnlineObject();
				devO.setIana_id(devices.getIanaId());
				devO.setSn(devices.getSn());
				
				devO = ACUtil.getPoolObjectBySn(devO, DevOnlineObject.class);
				if( devO == null )
				{
					response.setMessage("Device is offline");
					response.setResp_code(ResponseCode.SUCCESS);
				}
				else
				{
					//check device is online or not
					if( !devO.isOnline() )
					{
						response.setMessage("Device is offline");
						response.setResp_code(ResponseCode.SUCCESS);
					}
					else
					{
						TunnelObject tunnelObject = new TunnelObject();
						tunnelObject.setIana_id(devices.getIanaId());
						tunnelObject.setSn(devices.getSn());
						tunnelObject = ACUtil.getPoolObjectBySn(tunnelObject, TunnelObject.class);
						
						response.setResp_code(ResponseCode.SUCCESS);
						
						if( !isRetry && tunnelObject != null )
						{
							Integer timestamp = tunnelObject.getTimestamp();
							if( timestamp != null )
							{
								int unixtime = DateUtils.getUnixtime();
//								check whether device RA_status is off
								if( !((unixtime > timestamp.intValue()) && (unixtime - timestamp.intValue() < 600)) )
								{
									Json_Tunnel jsonTunnel = new Json_Tunnel();
									jsonTunnel.setDomain(param_ra_domain);
									jsonTunnel.setAction("s");
									jsonTunnel.setVersion(1);
									jsonTunnel.setSsh_host_key(param_ssh_host_key);
									ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_WEB_ADMIN_TUNNEL, request.getCaller_ref()+request.getServer_ref(), devices.getIanaId(), devices.getSn(),jsonTunnel);
								}
							}
							tunnel.setStatus(tunnelObject.getStatus());
						}
						else
						{
							/* retry */
							Json_Tunnel jsonTunnel = new Json_Tunnel();
							jsonTunnel.setDomain(param_ra_domain);
							jsonTunnel.setAction("s");
							jsonTunnel.setVersion(1);
							jsonTunnel.setSsh_host_key(param_ssh_host_key);
							ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_WEB_ADMIN_TUNNEL, request.getCaller_ref()+request.getServer_ref(), devices.getIanaId(), devices.getSn(), jsonTunnel);
							response.setResp_code(ResponseCode.PENDING);
						}
					}
				}
			}
		} 
		catch (Exception e) 
		{
			log.error("transaction is rollback - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		
		response.setData(tunnel);
		return JsonUtils.toJson(response);	
	}
	
	public static String stopWebAdminTunnel(JsonTunnelRequest request, JsonResponse<Json_Tunnel> response) {
		/*
		 * - parameter: device_id
- Close the proxy tunnel of this device (i.e. Send "e" action thru WTP)

		 */
		String param_orgId = request.getOrganization_id();
		String param_devSn = request.getSn();
		Integer param_ianaId = request.getIana_id();
		DevicesDAO devicesDAO = null;
		
		try
		{
			response.setResp_code(ResponseCode.SUCCESS);
			devicesDAO = new DevicesDAO(param_orgId,true);
			Devices devices = devicesDAO.findBySn(param_ianaId, param_devSn);
			if( devices != null )
			{
				Json_Tunnel jsonTunnel = new Json_Tunnel();
				jsonTunnel.setAction("e");
				ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_WEB_ADMIN_TUNNEL, request.getCaller_ref()+request.getServer_ref(), devices.getIanaId(), devices.getSn(),jsonTunnel);
			}
		}
		catch (Exception e) 
		{
			log.error("transaction is rollback - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		
		return JsonUtils.toJson(response);
		
	}
	
	public static String pollWebAdminTunnel(JsonTunnelRequest request, JsonResponse<Json_Tunnel> response) {
		/*
		 * - parameter: device_id
		   - Keep the tunnel alive (i.e. Send "c" action thru WTP)

		 */
		
		String param_orgId = request.getOrganization_id();
		String param_devSn = request.getSn();
		Integer param_ianaId = request.getIana_id();
		DevicesDAO devicesDAO = null;
		
		try
		{
			response.setResp_code(ResponseCode.SUCCESS);
			devicesDAO = new DevicesDAO(param_orgId,true);
			Devices devices = devicesDAO.findBySn(param_ianaId, param_devSn);
			if( devices != null )
			{
				Json_Tunnel jsonTunnel = new Json_Tunnel();
				jsonTunnel.setAction("c");
				ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_WEB_ADMIN_TUNNEL, request.getCaller_ref()+request.getServer_ref(), devices.getIanaId(), devices.getSn(),jsonTunnel);
			}
		}
		catch (Exception e) 
		{
			log.error("transaction is rollback - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		
		return JsonUtils.toJson(response);
		
	}
	
	public static String executeCommand(JsonTunnelRequest request, JsonResponse<Json_Tunnel> response) {
		/*
		 * - parameter: device_id
		   - Keep the tunnel alive (i.e. Send "c" action thru WTP)
		 */
		
		String param_orgId = request.getOrganization_id();
		String param_devSn = request.getSn();
		Integer param_ianaId = request.getIana_id();
		String param_command_id = request.getCommand_id();
		String param_command = request.getCommand();
		String param_action = request.getAction();
		boolean isCalled = request.isPending();
		List<HashMap<String,String>> param_argc = request.getArgc();
		String param_ver = request.getVersion();
		Json_Tunnel result = new Json_Tunnel();
		
		try
		{
			Json_Web_Tunneling json_tunneling = new Json_Web_Tunneling();
			if( param_command_id != null && param_command_id.isEmpty()==false )
			{
				json_tunneling.setCommand_id(param_command_id);
				json_tunneling.setCommand(param_command);
				json_tunneling.setAction(param_action);
				if( param_argc != null )
					json_tunneling.setArgc(param_argc);
				else
					json_tunneling.setArgc(new ArrayList<HashMap<String,String>>());
				json_tunneling.setVersion(Integer.valueOf(param_ver));
			}
			else
			{
				String command_id = generateCommandId(param_ianaId,param_devSn,param_command);
				json_tunneling.setCommand_id(command_id);
				json_tunneling.setCommand(param_command);
				json_tunneling.setAction(param_action);
				if( param_argc != null )
					json_tunneling.setArgc(param_argc);
				else
					json_tunneling.setArgc(new ArrayList<HashMap<String,String>>());
				json_tunneling.setVersion(Integer.valueOf(param_ver));
			}
			ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_WEB_TUNNELING, request.getServer_ref(), param_ianaId, param_devSn, json_tunneling);
			WebTunnelingObject webTunnelObj = new WebTunnelingObject();
			webTunnelObj.setIana_id(param_ianaId);
			webTunnelObj.setSn(param_devSn);
			webTunnelObj = ACUtil.getPoolObjectBySn(webTunnelObj, WebTunnelingObject.class);
			response.setResp_code(ResponseCode.SUCCESS);
			
			if(webTunnelObj != null && webTunnelObj.getCommand_id()!=null && webTunnelObj.getCommand_id().equals(param_command_id))
			{
				result.setJson(webTunnelObj.getJson());
			}
			else
			{
				response.setResp_code(ResponseCode.PENDING);
			}
			response.setData(result);
		}
		catch (Exception e) 
		{
			log.error("execute command error - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		
		return JsonUtils.toJson(response);
	}
	
	public static String generateCommandId(Integer iana_id, String sn, String command)
	{
		return iana_id + "_" + sn + "_" + command + "_" + (new Date().getTime());
	}
}
