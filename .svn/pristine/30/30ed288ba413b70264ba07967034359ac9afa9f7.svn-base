package com.littlecloud.control.webservices.handler;

import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.messagehandler.RedirectWtpMessageHandler;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.branch.SnsOrganizations;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.request.JsonCommandAcRedirectRequest;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.utils.BranchUtils;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.littlecloud.pool.object.utils.NetUtils;

public class ClientCommandsWsHandler {
	private static final Logger log = Logger.getLogger(ClientCommandsWsHandler.class);
	public static String commandACRedirect(JsonCommandAcRedirectRequest request, JsonResponse response){
		try{			
			if (request != null){
				if (request.getIana_id() != null && request.getSn() != null && !request.getSn().isEmpty()){
					String orgId = request.getOrganization_id();
					Integer ianaId = request.getIana_id();
					String sn = request.getSn();
					List<String> hostList = request.getHost_list();
					boolean commandIsPlaced = false;
					
					if (orgId == null){
						SnsOrganizations snsOrg = BranchUtils.getSnsOrganizationsByIanaIdSn(ianaId, sn);
						if (snsOrg != null){
							orgId = snsOrg.getOrganizationId();
						}
					}
					
					
					Devices devices = NetUtils.getDevices(orgId, ianaId, sn);
					if (devices != null){
						DevOnlineObject devOnline = DeviceUtils.getDevOnlineObject(devices);
						if (devOnline != null){
							Devices.ONLINE_STATUS status = devOnline.getStatus();
							if (log.isDebugEnabled()){
								log.debugf("REDIRECTCMD20140616 - ClientCommandsWsHandler.commandACRedirect - devOnline: %s ! orgId: %s, iana: %s, sn: %s",devOnline, request.getOrganization_id(), request.getIana_id(), request.getSn());		
							}
							if (status == Devices.ONLINE_STATUS.ONLINE){
								if (hostList != null && hostList.size() > 0){
									commandIsPlaced = RedirectWtpMessageHandler.doCheckAndRedirectWtpMessageHandler(devOnline, hostList);	
									if (log.isDebugEnabled()){
										log.debugf("REDIRECTCMD20140616 - ClientCommandsWsHandler.commandACRedirect - commandIsPlaced: %s ! orgId: %s, iana: %s, sn: %s",commandIsPlaced, request.getOrganization_id(), request.getIana_id(), request.getSn());			
									}
									if (commandIsPlaced){
										response.setResp_code(ResponseCode.SUCCESS);
										response.setMessage("redirect command is placed!!!");
									} else {
										response.setResp_code(ResponseCode.UNDEFINED);
										response.setMessage("redirect command is not placed with some problem!!!");
									}
								} else {
									log.warnf("REDIRECTCMD20140616 - ClientCommandsWsHandler.commandACRedirect, hostList is null or smaller than 1!!!, orgId: %s, iana: %s, sn: %s, request: %s", request.getOrganization_id(), request.getIana_id(), request.getSn(), request);
									response.setResp_code(ResponseCode.UNDEFINED);
									response.setMessage("redirect hostList is not specified!!!");
								}
							} else {
								log.warnf("REDIRECTCMD20140616 - ClientCommandsWsHandler.commandACRedirect, device is offline!!!, orgId: %s, iana: %s, sn: %s", request.getOrganization_id(), request.getIana_id(), request.getSn());
								response.setResp_code(ResponseCode.UNDEFINED);
								response.setMessage("device is not online, cannot get it from cache!!!");
							}
						} else {
							log.warnf("REDIRECTCMD20140616 - ClientCommandsWsHandler.commandACRedirect, devOnline is null!!!, orgId: %s, iana: %s, sn: %s", request.getOrganization_id(), request.getIana_id(), request.getSn());
							response.setResp_code(ResponseCode.UNDEFINED);
							response.setMessage("devOnline is null, device may not online/ cannot get it from cache!!!");
						}
					} else {
						log.warnf("REDIRECTCMD20140616 - ClientCommandsWsHandler.commandACRedirect, devices is null!!!, orgId: %s, iana: %s, sn: %s", request.getOrganization_id(), request.getIana_id(), request.getSn());
						response.setResp_code(ResponseCode.UNDEFINED);
						response.setMessage("device is null, cannot get it from cache/ database!!!");
					}

				} else { // end if (request != null)
					log.warnf("REDIRECTCMD20140616 - ClientCommandsWsHandler.commandACRedirect, request is null!!!");
					response.setResp_code(ResponseCode.UNDEFINED);
					response.setMessage("request is null!!!");
				}
			}
		} catch (Exception e){
			log.error("REDIRECTCMD20140616 - ClientCommandsWsHandler - " + e, e);
			response.setResp_code(ResponseCode.UNDEFINED);
			response.setMessage("Exception!!!!");
			return JsonUtils.toJson(response);
		} // end try ... catch ...
		return JsonUtils.toJson(response);
	}
	
}
