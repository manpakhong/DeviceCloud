package com.littlecloud.ac.messagehandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONException;

import org.apache.commons.lang.time.DateUtils;
import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.Json_CaptivePortalUserSession;
import com.littlecloud.ac.json.model.Json_CaptivePortalUserSessionContent;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.messagehandler.queue.executor.CaptivePortalMessageHandleExecutorController;
import com.littlecloud.ac.messagehandler.queue.messages.Message;
import com.littlecloud.ac.messagehandler.queue.messages.MessageContent;
import com.littlecloud.ac.messagehandler.queue.messages.impl.CaptivePortalAcMessageContentImpl;
import com.littlecloud.ac.messagehandler.queue.messages.impl.CaptivePortalMessageImpl;
import com.littlecloud.control.entity.CaptivePortalActivity;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.CpSessionInfoObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.utils.CaptivePortalUtils;
import com.littlecloud.pool.object.utils.criteria.CpSessionInfoObjectCriteria;
import com.littlecloud.pool.utils.PropertyService;
import com.littlecloud.utils.CommonUtils;

public class CaptivePortalMessageHandler {
	private static final Logger log = Logger.getLogger(CaptivePortalMessageHandler.class);
	private static final int SESSION_TIME_OUT_NO_LIMIT = -1;
	private static final int SESSION_BANDWIDTH_NO_LIMIT = -1;
	private static PropertyService<CaptivePortalMessageHandler> ps = new PropertyService<CaptivePortalMessageHandler>(CaptivePortalMessageHandler.class);
	private static final int IDLE_MINUTE_TO_DETERMINE_DISCONNECTED = ps.getInteger("IDLE_MINUTE_TO_DETERMINE_DISCONNECTED");	
		
	public static boolean doCaptivePortalMessageHandle(DevOnlineObject devOnlineO, String data){
		boolean isHandled = false;
		try{
			if (log.isDebugEnabled()){
				log.debugf("CAPORT20140526 - CaptivePortalMessageHandler.doCaptivePortalMessageHandle(), data: %s", data);
			}
			if (data != null && !data.isEmpty() && devOnlineO != null){
				
				if (!checkIsDataSuccessMessage(data)){
					Gson gson = new GsonBuilder().setDateFormat(JsonUtils.jsonRequestDateTimeFormat).create();
					Json_CaptivePortalUserSession jCpUserSession = (Json_CaptivePortalUserSession)gson.fromJson(data, Json_CaptivePortalUserSession.class);
					
	//				Json_CaptivePortalUserSession jCpUserSession = JsonUtils.<Json_CaptivePortalUserSession>fromJson(data.toString(), Json_CaptivePortalUserSession.class);
					if (log.isDebugEnabled()){
						log.debugf("CAPORT20140526 - CaptivePortalMessageHandler.doCaptivePortalMessageHandle(), jCpUserSession: %s",jCpUserSession);
					}
					if (jCpUserSession != null && jCpUserSession.getType() != null){
						boolean isSaved = saveCaptivePortalActivity(jCpUserSession, devOnlineO);
						if (!isSaved){
							log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.doCaptivePortalMessageHandle(), saveCaptivePortalActivity is %s!!!, ianaId:%s, sn:%s, data:%s, jCpUserSession: %s",isSaved,devOnlineO.getIana_id(), devOnlineO.getSn(), data, jCpUserSession);
						}
						
						switch (jCpUserSession.getType()){
							case Json_CaptivePortalUserSession.TYPE_CONNECT:
								handleConnectMessage(jCpUserSession, devOnlineO);
							break;
							case Json_CaptivePortalUserSession.TYPE_DISCONNECT:
								handleDisconnectMessage(jCpUserSession, devOnlineO);
							break;
							case Json_CaptivePortalUserSession.TYPE_LOGIN:
								handleLoginMessage(jCpUserSession, devOnlineO);
							break;
							case Json_CaptivePortalUserSession.TYPE_USAGE:
								handleUsageMessage(jCpUserSession, devOnlineO);
							break;
							case Json_CaptivePortalUserSession.TYPE_LOGOUT:
								handleLogoutMessage(jCpUserSession, devOnlineO);
							break;	
							
						}
					} else {
						log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.doCaptivePortalMessageHandle(), jCpUserSession.getType() is null!!!, ianaId:%s, sn:%s, data:%s, jCpUserSession: %s",devOnlineO.getIana_id(), devOnlineO.getSn(), data, jCpUserSession);
					}
				} else {
					if (log.isDebugEnabled()){
						log.debugf("CAPORT20140526 - CaptivePortalMessageHandler.doCaptivePortalMessageHandle(), dummy message: data: {\"success\": <true>/<false>}, ianaId:%s, sn:%s, data:%s",devOnlineO.getIana_id(), devOnlineO.getSn(), data);			
					}
				}
			} else {
				if (devOnlineO == null){
					log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.doCaptivePortalMessageHandle(), jsonObject is null or devOnlineO is null!!!");
				} else {
					log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.doCaptivePortalMessageHandle(), jsonObject is null!!!, ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());					
				}
			}
		} catch (Exception e){
			if (e instanceof JSONException){
				log.errorf(e, "CAPORT20140526 - CaptivePortalMessageHandler.doCaptivePortalMessageHandle(), jsonobject cannot be gsoned!, ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());
			} else {
				log.errorf(e, "CAPORT20140526 - CaptivePortalMessageHandler.doCaptivePortalMessageHandle(), ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());
			}
		}
		return isHandled;
	} // end doCaptivePortalMessageHandle
	
	private static boolean checkIsDataSuccessMessage(String data){
		boolean isDataSuccessMessage = false;
		if (data != null && !data.isEmpty()){
			String sourceString = data;
			String patternString = "\\\"data\\\"\\:\\{\\\"success\\\"\\:\\s{0,1}(true|false)\\}";
			List<String> patternMatchedList = CommonUtils.regMatch(sourceString, patternString);
			if (patternMatchedList != null && patternMatchedList.size() > 0){
				isDataSuccessMessage = true;
			}
		}
		return isDataSuccessMessage;
	}
	
	private static boolean saveCaptivePortalActivity(Json_CaptivePortalUserSession jCpUserSession, DevOnlineObject devOnlineO){
		boolean isSaved = false;
		try{
			List<Json_CaptivePortalUserSessionContent> contentList = jCpUserSession.getContent();
			String activityType = "";
			switch (jCpUserSession.getType()){
			case Json_CaptivePortalUserSession.TYPE_CONNECT:
				activityType = CaptivePortalActivity.ACTIVITY_TYPE_CONNECT;
				break;
			case Json_CaptivePortalUserSession.TYPE_DISCONNECT:
				activityType = CaptivePortalActivity.ACTIVITY_TYPE_DISCONNECT;
				break;
			case Json_CaptivePortalUserSession.TYPE_LOGIN:
				activityType = CaptivePortalActivity.ACTIVITY_TYPE_LOGIN;
				break;
			case Json_CaptivePortalUserSession.TYPE_LOGOUT:
				activityType = CaptivePortalActivity.ACTIVITY_TYPE_LOGOUT;
				break;
			case Json_CaptivePortalUserSession.TYPE_USAGE:
				activityType = CaptivePortalActivity.ACTIVITY_TYPE_USAGE;
				break;
			}
			

			
			if (contentList != null && contentList.size() > 0){
				for (Json_CaptivePortalUserSessionContent content: contentList){
					boolean isValidMessage = isValidMessage(content, devOnlineO, "saveCaptivePortalActivity");
					if (isValidMessage){
						
						CpSessionInfoObjectCriteria criteriaIn = new CpSessionInfoObjectCriteria();
						criteriaIn.setClientMac(content.getClient_mac());
						criteriaIn.setDevicesId(devOnlineO.getDevice_id());
						criteriaIn.setIanaId(devOnlineO.getIana_id());
						criteriaIn.setNetworkId(devOnlineO.getNetwork_id());
						criteriaIn.setOrganizationId(devOnlineO.getOrganization_id());
						criteriaIn.setSn(devOnlineO.getSn());
						criteriaIn.setSsid(content.getSsid());
						
						CpSessionInfoObject cpSessionInfoObject = CaptivePortalUtils.getCpSessionInfoObject(criteriaIn);
						
						if (jCpUserSession.getType().equals(Json_CaptivePortalUserSession.TYPE_USAGE)){
							if (log.isDebugEnabled()){
								log.debugf("CAPORT20140526 - CaptivePortalMessageHandler.handleUsageMessage() -------> check cpId(), ianaId:%s, sn:%s, cpSessionInfoObject:%s", devOnlineO.getIana_id(), devOnlineO.getSn(), cpSessionInfoObject);	
							}
						}
						
						CaptivePortalActivity captivePortalActivity = new CaptivePortalActivity();
						captivePortalActivity.setBandwidthUsed(content.getBandwidth());
						captivePortalActivity.setTimeUsed(content.getDuration());
						captivePortalActivity.setBssid(content.getBssid());
						if (content.getClient_mac() != null && !content.getClient_mac().isEmpty()){
							captivePortalActivity.setClientMac(content.getClient_mac());							
						}
						captivePortalActivity.setCpId(0);						
						if (cpSessionInfoObject != null && cpSessionInfoObject.getCp_id() != null){
							captivePortalActivity.setCpId(cpSessionInfoObject.getCp_id());
							if (cpSessionInfoObject.getUsername() != null){
								captivePortalActivity.setUsername(cpSessionInfoObject.getUsername());
							}
						} else {
							log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.saveCaptivePortalActivity(), captivePortalActivity.setCpId(0) - cpSessionInfoObject: %s!!!, ianaId:%s, sn:%s",cpSessionInfoObject, devOnlineO.getIana_id(), devOnlineO.getSn());					
						}
						
						captivePortalActivity.setDeviceId(devOnlineO.getDevice_id());
						captivePortalActivity.setNetworkId(devOnlineO.getNetwork_id());
						
						captivePortalActivity.setCreatedAt(new Date());
						
						captivePortalActivity.setSsid(content.getSsid());
						captivePortalActivity.setActivityType(activityType);
						captivePortalActivity.setStatus(content.getStatus());
						captivePortalActivity.setStatusMsg(content.getStatus_msg());
						if (devOnlineO != null){
							captivePortalActivity.setIanaId(devOnlineO.getIana_id());
							captivePortalActivity.setSn(devOnlineO.getSn());
						}
						isSaved = CaptivePortalUtils.saveCaptivePortalActivity(captivePortalActivity, devOnlineO.getOrganization_id());
					}	
				}
			}
		} catch (Exception e){
			log.errorf(e, "CAPORT20140526 - CaptivePortalMessageHandler.doCaptivePortalMessageHandle(), ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());
		}
		return isSaved;
	}
	
	private static boolean handleUsageMessage(Json_CaptivePortalUserSession jCpUserSession, DevOnlineObject devOnlineO){
		boolean isHandled = false;
		List<Json_CaptivePortalUserSessionContent> contentList = jCpUserSession.getContent();
		if (contentList != null && contentList.size() > 0){
			for (Json_CaptivePortalUserSessionContent content: contentList){
				boolean isValidMessage = isValidMessage(content, devOnlineO, "handleUsageMessage");
				if (isValidMessage){
					if (content.getStatus() != null){
						if (content.getStatus().intValue() == Json_CaptivePortalUserSessionContent.STATUS_FAILURE.intValue()){
							return isHandled;
						}
					} else {
						return isHandled;
					}
					
					CpSessionInfoObjectCriteria criteria = new CpSessionInfoObjectCriteria();
					criteria.setDevicesId(devOnlineO.getDevice_id());
					criteria.setClientMac(content.getClient_mac());
					criteria.setSsid(content.getSsid());
					criteria.setOrganizationId(devOnlineO.getOrganization_id());
					criteria.setNetworkId(devOnlineO.getNetwork_id());
					criteria.setIanaId(devOnlineO.getIana_id());
					criteria.setSn(devOnlineO.getSn());
					
					
					CpSessionInfoObject cpSessionInfoObject = CaptivePortalUtils.getCpSessionInfoObject(criteria);
					if (cpSessionInfoObject != null){

						if (cpSessionInfoObject.getRemain_bandwidth() != null && !cpSessionInfoObject.getRemain_bandwidth().equals(SESSION_BANDWIDTH_NO_LIMIT)){
							int remainBandwidth = cpSessionInfoObject.getRemain_bandwidth() - content.getBandwidth();
							cpSessionInfoObject.setRemain_bandwidth(remainBandwidth);
						}
						if (cpSessionInfoObject.getRemain_time() != null && !cpSessionInfoObject.getRemain_time().equals(SESSION_TIME_OUT_NO_LIMIT)){
							int remainTime = cpSessionInfoObject.getRemain_time() - content.getDuration();
							cpSessionInfoObject.setRemain_time(remainTime);
						}
						
						cpSessionInfoObject.setLast_house_keep_check_time(new Date());
						
						if (log.isDebugEnabled()){
							log.debugf("CAPORT20140526 - CaptivePortalMessageHandler.handleUsageMessage(), ianaId:%s, sn:%s, cpSessionInfoObject.getRemain_bandwidth(): %s, content.getBandwidth(): %s", devOnlineO.getIana_id(), devOnlineO.getSn(), cpSessionInfoObject.getRemain_bandwidth(), content.getBandwidth());	
						}
						cpSessionInfoObject.setStatus(CpSessionInfoObject.STATUS_ACTIVE);
						boolean isSaved = CaptivePortalUtils.saveCaptivePortalSessions(cpSessionInfoObject);
						if (!isSaved){
							log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.handleUsageMessage(), cpSessionInfoObject not saved with some problem!!!, ianaId:%s, sn:%s", devOnlineO.getIana_id(), devOnlineO.getSn());					
						}
					} else {
						log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.handleUsageMessage(), cpSessionInfoObject is null!!!, ianaId:%s, sn:%s", devOnlineO.getIana_id(), devOnlineO.getSn());					
					}	
				}
			}	
		} else {
			log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.handleDisconnectMessage(), contentList is null or size() == 0!!!, ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());					
		}
		return isHandled;
	}
		
	private static boolean handleConnectMessage(Json_CaptivePortalUserSession jCpUserSession, DevOnlineObject devOnlineO){
		boolean isHandled = false;
		List<Json_CaptivePortalUserSessionContent> contentList = jCpUserSession.getContent();
		if (log.isDebugEnabled()){
			log.debugf("CAPORT20140526 - CaptivePortalMessageHandler.handleConnectMessage(), contentList: %s",contentList);							
		}
		if (contentList != null && contentList.size() > 0){
			Json_CaptivePortalUserSession jsession = new Json_CaptivePortalUserSession();
			jsession.setType(Json_CaptivePortalUserSession.TYPE_LOGIN);
//			jsession.setContent(jcontentList); // send after for loop
//			jsession.setVersion(1);
			List<Json_CaptivePortalUserSessionContent> jcontentList = new ArrayList<Json_CaptivePortalUserSessionContent>();
//			jsession.setStatus(status); //set after for loop
			for (Json_CaptivePortalUserSessionContent content: contentList){
				boolean isValidMessage = isValidMessage(content, devOnlineO, "handleConnectMessage");
				if (isValidMessage){
					CpSessionInfoObjectCriteria criteriaNoApMove = new CpSessionInfoObjectCriteria();
					criteriaNoApMove.setDevicesId(devOnlineO.getDevice_id());
					criteriaNoApMove.setClientMac(content.getClient_mac());
					criteriaNoApMove.setSsid(content.getSsid());
					criteriaNoApMove.setOrganizationId(devOnlineO.getOrganization_id());
					criteriaNoApMove.setNetworkId(devOnlineO.getNetwork_id());
					criteriaNoApMove.setIanaId(devOnlineO.getIana_id());
					criteriaNoApMove.setSn(devOnlineO.getSn());
					
					CpSessionInfoObject cpSessionInfoObject = 
							CaptivePortalUtils.getCpSessionInfoObject(criteriaNoApMove);					
					
					if (cpSessionInfoObject != null){ // check of session is found
						Json_CaptivePortalUserSessionContent jcontent = new Json_CaptivePortalUserSessionContent();

//						jcontent.setDuration(cpSessionInfoObject.getRemain_time()); 
//						jcontent.setBandwidth(cpSessionInfoObject.getRemain_bandwidth());
						if (content.getBssid() != null && !content.getBssid().isEmpty()){
							cpSessionInfoObject.setBssid(content.getBssid());
							jcontent.setBssid(content.getBssid());
						}
						if (log.isDebugEnabled()){
							log.debugf("CAPORT20140526 - CaptivePortalMessageHandler.handleConnectMessage(), cpSessionInfoObject: %s",cpSessionInfoObject);			
						}
						jcontent.setClient_mac(cpSessionInfoObject.getClient_mac());
						jcontent.setSsid(cpSessionInfoObject.getSsid());

						reactiveSessionAndSaveIfWithinIdlePeroid(cpSessionInfoObject);
						
						if (cpSessionInfoObject.getStatus() != null && cpSessionInfoObject.getStatus().equals(CpSessionInfoObject.STATUS_ACTIVE)){
							if (log.isDebugEnabled()){
								log.debugf("CAPORT20140526 - CaptivePortalMessageHandler.handleConnectMessage(), CpSessionInfoObject.STATUS_ACTIVE - ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());							
							}
							// calculate remain time and update cache and db
							if (cpSessionInfoObject.getCreated_at() != null){
								
								if (cpSessionInfoObject.getRemain_bandwidth() != null){
									jcontent.setBandwidth(cpSessionInfoObject.getRemain_bandwidth());									
								} else {
									log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.handleConnectMessage(), cpSessionInfoObject.getRemain_bandwidth() is null!!!, ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());					
								}

								if (cpSessionInfoObject.getRemain_time() != null){
									jcontent.setDuration(cpSessionInfoObject.getRemain_time());									
								} else {
									log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.handleConnectMessage(), cpSessionInfoObject.getRemain_timeh() is null!!!, ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());					
								}
																
								// mark last access time
								cpSessionInfoObject.setLast_access_time(new Date());
								CaptivePortalUtils.saveCaptivePortalSessions(cpSessionInfoObject);
								
								// check session time out
								if (cpSessionInfoObject.getSession_timeout() != null && cpSessionInfoObject.getSession_timeout() > SESSION_TIME_OUT_NO_LIMIT){

									boolean isSessionTimeOut = isSessionTimeOut(cpSessionInfoObject.getCreated_at(), cpSessionInfoObject.getSession_timeout());
									isSessionTimeOut = false; // <----- suspend isSessionTimeOut checking
									if (!isSessionTimeOut){
										jsession.setType(Json_CaptivePortalUserSession.TYPE_LOGIN);
										jcontentList.add(jcontent);
										jsession.setContent(jcontentList);


									} else {
										jsession.setType(Json_CaptivePortalUserSession.TYPE_LOGOUT);
										jcontentList.add(jcontent);
										jsession.setContent(jcontentList);
										if (log.isDebugEnabled()){
											log.debugf("CAPORT20140526 - CaptivePortalMessageHandler.handleConnectMessage(), isSessionTimeOut logout!!! - ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());							
										}
									}
								} else { // unlimited, no session time out
									jsession.setType(Json_CaptivePortalUserSession.TYPE_LOGIN);
									jcontentList.add(jcontent);
									jsession.setContent(jcontentList);
								}

							} else { // cpSessionInfoObject.getCreated_at() should not be null
								log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.handleConnectMessage(), cpSessionInfoObject.getCreated_at() is null!!!, ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());					
//								jsession.setType(Json_CaptivePortalUserSession.TYPE_LOGOUT);
//								jcontentList.add(jcontent);
//								jsession.setContent(jcontentList);
							}
						} else { // not active status in current
							if (log.isDebugEnabled()){
								log.debugf("CAPORT20140526 - CaptivePortalMessageHandler.handleConnectMessage(), not active Logout!!! CpSessionInfoObject.STATUS_INACTIVE -ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());							
							}
							jsession.setType(Json_CaptivePortalUserSession.TYPE_LOGOUT);
							jcontentList.add(jcontent);
							jsession.setContent(jcontentList);
						}
						
						
						// check if serial no is different from previous ------> move from AP1 to AP2 - fetch command to mark usage
						if (!devOnlineO.getSn().equals(cpSessionInfoObject.getSn())){
							placeUsageAcCommand(content, cpSessionInfoObject);
						}
						
					} else { // if cache object is null
						if (log.isDebugEnabled()){
							log.debugf("CAPORT20140526 - CaptivePortalMessageHandler.handleConnectMessage(), ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());							
						}
						Json_CaptivePortalUserSessionContent jcontent = new Json_CaptivePortalUserSessionContent();
//						jcontent.setDuration(content.getDuration()); 
//						jcontent.setBandwidth(content.getBandwidth());
						jcontent.setBssid(content.getBssid());
						jcontent.setClient_mac(content.getClient_mac());
//						jcontent.setSsid(content.getSsid());
						jcontentList.add(jcontent);
						jsession.setContent(jcontentList);
						jsession.setType(Json_CaptivePortalUserSession.TYPE_LOGOUT);

					} // end if (cpSessionInfoObject != null) ... else ...
				} else { // not a valid message
					log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.handleConnectMessage(), not a valid message!!!, ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());					
				}
			} // end for
			ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_PORTAL, JsonUtils.genServerRef(), devOnlineO.getIana_id(), devOnlineO.getSn(), jsession);
			

		} else {
			log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.handleConnectMessage(), contentList is null or size() == 0!!!, ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());					
		}
		return isHandled;
	}
	
	private static void placeUsageAcCommand(Json_CaptivePortalUserSessionContent content, CpSessionInfoObject cpSessionInfoObject){
		Json_CaptivePortalUserSessionContent rcontent = new Json_CaptivePortalUserSessionContent();
//		rcontent.setDuration(cpSessionInfoObject.getRemain_time()); 
//		rcontent.setBandwidth(cpSessionInfoObject.getRemain_bandwidth());
		
		if (content.getBssid() != null && !content.getBssid().isEmpty()){
			cpSessionInfoObject.setBssid(content.getBssid());
			rcontent.setBssid(content.getBssid());
		}
		
		rcontent.setClient_mac(cpSessionInfoObject.getClient_mac());
		rcontent.setSsid(cpSessionInfoObject.getSsid());

		Json_CaptivePortalUserSession rsession = new Json_CaptivePortalUserSession();						
		
		rsession.setType(Json_CaptivePortalUserSession.TYPE_USAGE);
		
		List<Json_CaptivePortalUserSessionContent> rcontentList = new ArrayList<Json_CaptivePortalUserSessionContent>();
		rcontentList.add(rcontent);
		
		rsession.setContent(rcontentList);
		rsession.setVersion(1);
		
		if (log.isDebugEnabled()){
			log.debugf("CAPORT20140526 - CaptivePortalMessageHandler.placeUsageAcCommand(),fetchCommand(Usage) rsession: %s",rsession);			
		}
		
		ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_PORTAL, JsonUtils.genServerRef(), cpSessionInfoObject.getIana_id(), cpSessionInfoObject.getSn(), rsession);
	}
	
	private static void reactiveSessionAndSaveIfWithinIdlePeroid(CpSessionInfoObject cpSessionInfoObject){
		if (cpSessionInfoObject != null 
				&& cpSessionInfoObject.getDisconnect_time() != null 
				&& cpSessionInfoObject.getStatus() != null){
			if (cpSessionInfoObject.getClient_logout() == null || !cpSessionInfoObject.getClient_logout()){
				if (cpSessionInfoObject.getStatus().equals(CpSessionInfoObject.STATUS_INACTIVE)){
					if (isWithinIdlePeriod(cpSessionInfoObject)){
						cpSessionInfoObject.setStatus(CpSessionInfoObject.STATUS_ACTIVE);
						if (log.isDebugEnabled()){
							log.debugf("CAPORT20140526 - CaptivePortalMessageHandler.reactiveSessionAndSaveIfWithinIdlePeroid() isWithinIdlePeroid: true, cpSessionInfoObject:%s",cpSessionInfoObject);							
						}
						boolean isSaved = CaptivePortalUtils.saveCaptivePortalSessions(cpSessionInfoObject);
						if (!isSaved){
							log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.reactiveSessionAndSaveIfWithinIdlePeroid(), saved problem!!!, cpSessionInfoObject:%s",cpSessionInfoObject);					
						}
					} else {
						if (log.isDebugEnabled()){
							log.debugf("CAPORT20140526 - CaptivePortalMessageHandler.reactiveSessionAndSaveIfWithinIdlePeroid() isWithinIdlePeroid: false, cpSessionInfoObject:%s",cpSessionInfoObject);							
						}
					}
				}
			}
		}
	}
	
	private static boolean isWithinIdlePeriod(CpSessionInfoObject cpSessionInfoObject){
		boolean isWithinIdlePeriod = false;
		if (cpSessionInfoObject != null && cpSessionInfoObject.getDisconnect_time() != null){
			Calendar calDisconnectTime = Calendar.getInstance();
			calDisconnectTime.setTime(cpSessionInfoObject.getDisconnect_time());
			calDisconnectTime.add(Calendar.MINUTE, IDLE_MINUTE_TO_DETERMINE_DISCONNECTED);

			Calendar calNow = Calendar.getInstance();
			if (calNow.after(calDisconnectTime)){
				isWithinIdlePeriod = false;
				
			} else {
				isWithinIdlePeriod = true;

			}
		}
		return isWithinIdlePeriod;
	}
	
	private static boolean handleLogoutMessage(Json_CaptivePortalUserSession jCpUserSession, DevOnlineObject devOnlineO){
		boolean isHandled = false;
		List<Json_CaptivePortalUserSessionContent> contentList = jCpUserSession.getContent();
		if (contentList != null && contentList.size() > 0){
			for (Json_CaptivePortalUserSessionContent content: contentList){
				boolean isValidMessage = isValidMessage(content, devOnlineO, "handleLogoutMessage");
				if (isValidMessage){
					isHandled = handleLogoutAndDisconnectCommonUpdateProcedure(content, devOnlineO, "handleLogoutMessage");
				}
			}
		} else {
			log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.handleLogoutMessage(), contentList is null or size() == 0!!!, ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());					
		}
		return isHandled;
	}
	
	private static boolean handleDisconnectMessage(Json_CaptivePortalUserSession jCpUserSession, DevOnlineObject devOnlineO){
		boolean isHandled = false;
		List<Json_CaptivePortalUserSessionContent> contentList = jCpUserSession.getContent();
		if (contentList != null && contentList.size() > 0){
			for (Json_CaptivePortalUserSessionContent content: contentList){
				boolean isValidMessage = isValidMessage(content, devOnlineO, "handleDisconnectMessage");
				if (isValidMessage){
					isHandled = handleLogoutAndDisconnectCommonUpdateProcedure(content, devOnlineO, "handleDisconnectMessage");
				}
			}
		} else {
			log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.handleDisconnectMessage(), contentList is null or size() == 0!!!, ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());					
		}
		return isHandled;
	}
	
	private static boolean handleLogoutAndDisconnectCommonUpdateProcedure(Json_CaptivePortalUserSessionContent content, DevOnlineObject devOnlineO, String handleMessage){
		boolean isHandled = false;
		
		if (content.getStatus() == null || content.getStatus().intValue() != Json_CaptivePortalUserSessionContent.STATUS_FAILURE.intValue()){
			
			CpSessionInfoObjectCriteria criteria = new CpSessionInfoObjectCriteria();
			criteria.setDevicesId(devOnlineO.getDevice_id());
			criteria.setClientMac(content.getClient_mac());
			criteria.setSsid(content.getSsid());
			criteria.setOrganizationId(devOnlineO.getOrganization_id());
			criteria.setNetworkId(devOnlineO.getNetwork_id());
			criteria.setIanaId(devOnlineO.getIana_id());
			criteria.setSn(devOnlineO.getSn());
			
			CpSessionInfoObject cpSessionInfoObject = 
					CaptivePortalUtils.getCpSessionInfoObject(criteria);
			if (cpSessionInfoObject != null){
				if (devOnlineO.getSn().equals(cpSessionInfoObject.getSn())){
					cpSessionInfoObject.setStatus(CpSessionInfoObject.STATUS_INACTIVE);
					cpSessionInfoObject.setDisconnect_time(new Date());
//					cpSessionInfoObject.setRemain_bandwidth(content.getBandwidth());
//					cpSessionInfoObject.setRemain_time(content.getDuration());
					cpSessionInfoObject.setBssid(content.getBssid());
					cpSessionInfoObject.setDevice_reply_login(null);
					boolean isSaved = CaptivePortalUtils.saveCaptivePortalSessions(cpSessionInfoObject);
					if (!isSaved){
						log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.%s, not saved with some problem!!!, ianaId:%s, sn:%s",handleMessage, devOnlineO.getIana_id(), devOnlineO.getSn());					
					}
				}
			} else {
				log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.%s, cpSessionInfoObject is null !!!, ianaId:%s, sn:%s",handleMessage, devOnlineO.getIana_id(), devOnlineO.getSn());					
			}
		} else {
			log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.%s, logout failure !!!, ianaId:%s, sn:%s",handleMessage, devOnlineO.getIana_id(), devOnlineO.getSn());					
		}
		return isHandled;
	}
		
	private static boolean handleLoginMessage(Json_CaptivePortalUserSession jCpUserSession, DevOnlineObject devOnlineO){
		boolean isHandled = false;
		List<Json_CaptivePortalUserSessionContent> contentList = jCpUserSession.getContent();
		if (contentList != null && contentList.size() > 0){
			for (Json_CaptivePortalUserSessionContent content: contentList){
				boolean isValidMessage = isValidMessage(content, devOnlineO, "handleLoginMessage");
				if (isValidMessage){
					CpSessionInfoObjectCriteria criteria = new CpSessionInfoObjectCriteria();
					criteria.setDevicesId(devOnlineO.getDevice_id());
					criteria.setClientMac(content.getClient_mac());
					criteria.setSsid(content.getSsid());
					criteria.setOrganizationId(devOnlineO.getOrganization_id());
					criteria.setNetworkId(devOnlineO.getNetwork_id());
					criteria.setIanaId(devOnlineO.getIana_id());
					criteria.setSn(devOnlineO.getSn());
					
					CpSessionInfoObject cpSessionInfoObject = 
							CaptivePortalUtils.getCpSessionInfoObject(criteria);
					
					
					if (cpSessionInfoObject != null){
						cpSessionInfoObject.setSn(devOnlineO.getSn()); // for catering the case switching from AP1 to AP2
						if (content.getStatus() != null && content.getStatus().intValue() == Json_CaptivePortalUserSessionContent.STATUS_SUCCESS.intValue()){
							cpSessionInfoObject.setStatus(CpSessionInfoObject.STATUS_ACTIVE);
							cpSessionInfoObject.setDevice_reply_login(true);
						} else {
							cpSessionInfoObject.setStatus(CpSessionInfoObject.STATUS_INACTIVE);
							cpSessionInfoObject.setDevice_reply_login(false);
						}
						boolean isSaved = CaptivePortalUtils.saveCaptivePortalSessions(cpSessionInfoObject);
						if (!isSaved){
							log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.handleLoginMessage(), not saved with some problem!!!, ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());					
						}
					}

				}
			}
		} else {
			log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.handleLoginMessage(), contentList is null or size() == 0!!!, ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());					
		}
		return isHandled;
	}
	
	private static boolean isSessionTimeOut(Date createdAt, Integer sessionTimeOut){
		boolean isSessionTimeOut = false;
		Calendar sessionCal = DateUtils.toCalendar(createdAt);
		sessionCal.add(Calendar.SECOND, sessionTimeOut);	
		Calendar nowCal = Calendar.getInstance();
		if (sessionCal.after(nowCal)){
			isSessionTimeOut = true;
		}
		return isSessionTimeOut;
	}
	
	private static int calculateRemainTime(Date createdAt){
		int timeDiff = 0;
		long seconds = (new Date().getTime()- createdAt.getTime()) /1000;
		
		timeDiff = CommonUtils.safeLongToInt(seconds);
		
		return timeDiff;
	}
	
	private static boolean isValidMessage(Json_CaptivePortalUserSessionContent content, DevOnlineObject devOnlineO, String logString){
		boolean isValid = false;
		if (content != null){
			if (content.getClient_mac() != null && 
				content.getSsid() != null && 
				devOnlineO.getNetwork_id() != null && 
				devOnlineO.getOrganization_id() != null && 
				devOnlineO.getDevice_id() != null){
				isValid = true;
			} else {
				log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.%s, insufficient parameters to get CpSessionInfoObject from cache!!!, ianaId:%s, sn:%s",logString,devOnlineO.getIana_id(), devOnlineO.getSn());					
			}
		} else {
			log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.%s, content is null or size() == 0!!!, ianaId:%s, sn:%s",logString,devOnlineO.getIana_id(), devOnlineO.getSn());					
		}
		return isValid;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean enqueueCaptivePortalMessage(DevOnlineObject devOnlineO, String data){
		boolean isEnqueue = false;
		try{
			if (devOnlineO != null && data != null && !data.isEmpty()){
				if (CaptivePortalMessageHandleExecutorController.getRunningEnabled()){
					Message message = new CaptivePortalMessageImpl();
					MessageContent messageContent = new CaptivePortalAcMessageContentImpl();
					messageContent.setSn(devOnlineO.getSn());
					messageContent.setIanaId(devOnlineO.getIana_id());
					messageContent.setData(data);
					message.setMessage(messageContent);
					message.setMessageType(Message.MESSAGE_TYPE_AC);
					
					CaptivePortalMessageHandleExecutorController.enqueueMessage(message);
					
				} else {
					log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.enqueueCaptivePortalMessage(), MessageHandleExecutorController.getRunningEnabled() is false!!!, ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());					
				}
			} else {
				if (devOnlineO == null){
					log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.enqueueCaptivePortalMessage(), jsonObject is null or devOnlineO is null!!!");
				} else {
					log.warnf("CAPORT20140526 - CaptivePortalMessageHandler.enqueueCaptivePortalMessage(), jsonObject is null!!!, ianaId:%s, sn:%s",devOnlineO.getIana_id(), devOnlineO.getSn());					
				}
			}
		} catch (Exception e){
			log.errorf("CAPORT20140526 - CaptivePortalMessageHandler.enqueueCaptivePortalMessage() -", e);					
		}
		return isEnqueue;
	}
	
} // end class
