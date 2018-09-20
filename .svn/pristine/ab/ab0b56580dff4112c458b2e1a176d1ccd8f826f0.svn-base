package com.littlecloud.control.webservices.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.Json_CaptivePortalUserSession;
import com.littlecloud.ac.json.model.Json_CaptivePortalUserSessionContent;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.messagehandler.queue.executor.CaptivePortalMessageHandleExecutorController;
import com.littlecloud.ac.messagehandler.queue.messages.Message;
import com.littlecloud.ac.messagehandler.queue.messages.MessageContent;
import com.littlecloud.ac.messagehandler.queue.messages.impl.CaptivePortalMessageImpl;
import com.littlecloud.ac.messagehandler.queue.messages.impl.CaptivePortalWsMessageContentImpl;
import com.littlecloud.control.entity.CaptivePortalActivity;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.request.JsonCaptivePortalRequest;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.CpSessionInfoObject;
import com.littlecloud.pool.object.utils.CaptivePortalUtils;
import com.littlecloud.pool.object.utils.criteria.CpSessionInfoObjectCriteria;

public class CaptivePortalWsHandler {
	private static final Logger log = Logger.getLogger(CaptivePortalWsHandler.class);
	
	public static boolean putCpUserSessionInAction(JsonCaptivePortalRequest request){
		boolean isPut = false;
		try{
			isPut = putCpUserSessionInAction(request, null);
		} catch (Exception e){
			log.error("CAPORT20140526 - CaptivePortalWsHandler.pubCpUserSession() - ", e);
		}
		return isPut;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean putCpUserSessionInAction(JsonCaptivePortalRequest request,JsonResponse response){
		boolean isPut = false;
		try{
			if (request != null){
				if (request.getIana_id() != null && request.getClient_mac() != null && request.getSsid() != null && request.getOrganization_id() != null && request.getNetwork_id() != null){
					boolean isSaved = CaptivePortalUtils.saveCaptivePortalSessions(request);	

					if (isSaved){
						if (response != null){
							response.setResp_code(ResponseCode.SUCCESS);
						}
						
						Json_CaptivePortalUserSessionContent content = new Json_CaptivePortalUserSessionContent();
						content.setDuration(request.getRemain_time()); 
						content.setBandwidth(request.getRemain_bandwidth());
						content.setBssid(request.getBssid());
						content.setClient_mac(request.getClient_mac());
						content.setSsid(request.getSsid());

						Json_CaptivePortalUserSession session = new Json_CaptivePortalUserSession();		
						
						// save captivePortalActivity log
						CaptivePortalActivity captivePortalActivity = new CaptivePortalActivity();
						captivePortalActivity.setBandwidthUsed(request.getRemain_bandwidth());
						captivePortalActivity.setTimeUsed(request.getRemain_time());
						captivePortalActivity.setBssid(request.getBssid());
						captivePortalActivity.setClientMac(request.getClient_mac());
						captivePortalActivity.setCpId(request.getCp_id());
						captivePortalActivity.setCreatedAt(new Date());
						captivePortalActivity.setIanaId(request.getIana_id());
						captivePortalActivity.setSn(request.getSn());
						captivePortalActivity.setSsid(request.getSsid());
						captivePortalActivity.setUsername(request.getUsername());
						
						
						if (request.getStatus() != null){
							if (request.getStatus().equals(JsonCaptivePortalRequest.STATUS_ACTIVE)){
								session.setType(Json_CaptivePortalUserSession.TYPE_LOGIN);
								captivePortalActivity.setActivityType(CaptivePortalActivity.ACTIVITY_TYPE_USER_LOGIN);
							} else if (request.getStatus().equals(JsonCaptivePortalRequest.STATUS_INACTIVE)){
								session.setType(Json_CaptivePortalUserSession.TYPE_LOGOUT);
								captivePortalActivity.setActivityType(CaptivePortalActivity.ACTIVITY_TYPE_USER_LOGOUT);
							}
						}
						
						boolean activityLogIsSaved = CaptivePortalUtils.saveCaptivePortalActivity(captivePortalActivity, request.getOrganization_id());
						if (!activityLogIsSaved){
							log.warnf("CAPORT20140526 - CaptivePortalWsHandler - putCpUserSession - CaptivePortalActivity cannot not be saved !!!! iana: %s, sn: %s, session: %s.", request.getIana_id(), request.getSsid(), session);						
						}
						
						
						List<Json_CaptivePortalUserSessionContent> contentList = new ArrayList<Json_CaptivePortalUserSessionContent>();
						contentList.add(content);
						
						session.setContent(contentList);
						session.setVersion(1);
						
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_PORTAL, JsonUtils.genServerRef(), request.getIana_id(), request.getSn(), session);
						if (log.isDebugEnabled()){
							log.debugf("CAPORT20140526 - CaptivePortalWsHandler - putCpUserSession - ACService.fetchCommand(PIPE_INFO_TYPE_PORTAL) ! iana: %s, sn: %s, session: %s.", request.getIana_id(), request.getSsid(), session);		
						}
						isPut = true;
					} else {
						if (log.isDebugEnabled()){
							log.debugf("CAPORT20140526 - CaptivePortalWsHandler - putCpUserSession - isSaved = false ! iana: %s, sn: %s, session: %s.", request.getIana_id(), request.getSsid());		
						}
					}
				
				} else {
					log.warnf("CAPORT20140526 - CaptivePortalWsHandler - putCpUserSession must have client_mac, ssid, organization_id and network_id input! iana: %s, sn: %s.", request.getIana_id(), request.getSsid());
				}
			} else { // end if (request != null)
				log.warnf("CAPORT20140526 - CaptivePortalWsHandler - putCpUserSession, request is null!!!");
			}

		} catch (Exception e){
			log.error("CAPORT20140526 - CaptivePortalWsHandler.pubCpUserSession() - ", e);
		}
		return isPut;
	}
	


	public static String putCpUserSession(JsonCaptivePortalRequest request, JsonResponse response){
		try{			
			if (request != null){
				if (request.getIana_id() != null && request.getClient_mac() != null && request.getSsid() != null && request.getOrganization_id() != null && request.getNetwork_id() != null){
					response.setResp_code(ResponseCode.SUCCESS);	
					boolean isQueueMode = false;
					
					if (CaptivePortalMessageHandleExecutorController.getRunningEnabled()){
						isQueueMode = true;
					}
					if (log.isDebugEnabled()){
						log.debugf("CAPORT20140526 - CaptivePortalWsHandler - putCpUserSession - iana: %s, sn: %s, MessageHandleExecutorController.getQueue():%s, isQueueMode:%s", request.getIana_id(), request.getSsid(), CaptivePortalMessageHandleExecutorController.getQueue(), isQueueMode);						
					}
					if (isQueueMode){
						Message<JsonCaptivePortalRequest> message = new CaptivePortalMessageImpl();
						message.setMessageType(Message.MESSAGE_TYPE_WEBSERVICE);
						MessageContent<JsonCaptivePortalRequest> messageContent = new CaptivePortalWsMessageContentImpl();
						messageContent.setData(request);
						message.setMessage(messageContent);
						CaptivePortalMessageHandleExecutorController.enqueueMessage(message);
						if (log.isDebugEnabled()){
							log.debugf("CAPORT20140526 - CaptivePortalWsHandler - putCpUserSession - iana: %s, sn: %s, MessageHandleExecutorController:%s", request.getIana_id(), request.getSsid(), CaptivePortalMessageHandleExecutorController.getQueue());						
						}
					} else {
						boolean isPut = putCpUserSessionInAction(request, response);
						if (!isPut){
							response.setResp_code(ResponseCode.UNDEFINED);	
							log.warnf("CAPORT20140526 - CaptivePortalWsHandler - putCpUserSession, isPut: false!!!");
						}
					}
						
				} else {
					log.warnf("CAPORT20140526 - CaptivePortalWsHandler - putCpUserSession must have client_mac, ssid, organization_id and network_id input! iana: %s, sn: %s.", request.getIana_id(), request.getSsid());
				}
			}else { // end if (request != null)
				log.warnf("CAPORT20140526 - CaptivePortalWsHandler - putCpUserSession, request is null!!!");
			}
			
		} catch (Exception e){
			log.error("CAPORT20140526 - CaptivePortalWsHandler.pubCpUserSession() - ", e);
			response.setResp_code(ResponseCode.UNDEFINED);
			return JsonUtils.toJson(response);
		} // end try ... catch ...
		
		return JsonUtils.toJson(response);
	} // end pubCpUserSession()
	
	
	@SuppressWarnings("rawtypes")
	public static String getCpUserSession(JsonCaptivePortalRequest request, JsonResponse response){
		try{			
			if (request != null && 
				request.getClient_mac() != null && 
				request.getSsid() != null && 
				request.getOrganization_id() != null &&
				request.getNetwork_id() != null && 
				request.getDevice_id() != null &&
				request.getIana_id() != null &&
				request.getSn() != null){
												
				CpSessionInfoObjectCriteria criteria = new CpSessionInfoObjectCriteria();
				criteria.setSn(request.getSn());
				criteria.setSsid(request.getSsid());
				criteria.setClientMac(request.getClient_mac());
				criteria.setIanaId(request.getIana_id());
				criteria.setNetworkId(request.getNetwork_id());
				criteria.setOrganizationId(request.getOrganization_id());
				criteria.setDevicesId(request.getDevice_id());
				
				CpSessionInfoObject cpSessionInfoObject = CaptivePortalUtils.getCpSessionInfoObject(criteria);
				
				if (cpSessionInfoObject != null){
					response.setResp_code(ResponseCode.SUCCESS);
					response.setData(cpSessionInfoObject);					
				} else {
					response.setResp_code(ResponseCode.PENDING);
					if (log.isDebugEnabled()){
						log.debugf("CAPORT20140526 - CaptivePortalWsHandler - getCpUserSession - cache have no UserSession, waiting for putCpUserSession! iana: %s, sn: %s.", request.getIana_id(), request.getSsid());
					}
				}
				
			} else { // end if (request != null)
				if (request != null){
					log.warnf("CAPORT20140526 - CaptivePortalWsHandler - getCpUserSession must have client_mac, ssid, organization_id and network_id input! iana: %s, sn: %s.", request.getIana_id(), request.getSsid());
				} else {
					log.warnf("CAPORT20140526 - CaptivePortalWsHandler - getCpUserSession, request is null");
				}
			}
		} catch (Exception e){
			log.errorf(e, "CAPORT20140526 - CaptivePortalWsHandler.getCpUserSession, request:%s", request);
			response.setResp_code(ResponseCode.UNDEFINED);
			return JsonUtils.toJson(response);
		} // end try ... catch ...
		
		return JsonUtils.toJson(response);
	} // end pubCpUserSession()
}
