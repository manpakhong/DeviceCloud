package com.littlecloud.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.OrganizationsDAO;
import com.littlecloud.control.entity.DeviceStatus;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.NetworkEmailNotifications;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.Organizations;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.criteria.SendGeoFencesAlertCriteria;
import com.littlecloud.criteria.SendImmediateAlertCriteria;
import com.littlecloud.dtos.json.AlertEmailContactDto;
import com.littlecloud.dtos.json.AlertEmailContactListDto;
import com.littlecloud.dtos.json.EmailTemplateObjectDto;
import com.littlecloud.dtos.json.RequestGeoFencesTemplateDto;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;

public class AlertMgr {
	private static final Logger log = Logger.getLogger(AlertMgr.class);
	
	public AlertMgr(){
	}
	
	public void sendGeoFencesAlert(SendGeoFencesAlertCriteria criteria,
			RequestGeoFencesTemplateDto requestGeoFencesTemplate) {
		try {
//			RequestGeoFencesTemplateData
			if (criteria != null && requestGeoFencesTemplate != null) {
				Devices device = NetUtils.getDevicesWithoutNetId(criteria.getOrgId(), criteria.getDevId().intValue());
				if (device != null){
					NetworkSilencePeriodsMgr networkSilencePeriodMgr = new NetworkSilencePeriodsMgr(criteria.getOrgId());
					boolean isDeviceInSilencePeriod = networkSilencePeriodMgr.isDeviceWithinSilencePeriod(device);
					if (!isDeviceInSilencePeriod){
						NetworkEmailNotificationsMgr networkEmailNotificationsMgr = new NetworkEmailNotificationsMgr(criteria.getOrgId());
						List<NetworkEmailNotifications> networkEmailNotificationList = 
								networkEmailNotificationsMgr.getNetworkEmailNotificationList(device.getNetworkId(), criteria.getAlertType(), criteria.getLevel().toString());
						if (networkEmailNotificationList != null){
							for (NetworkEmailNotifications networkEmailNotification: networkEmailNotificationList){
								if (networkEmailNotification != null){
									String recipients = networkEmailNotificationsMgr.getAlertEmailContactDtoRecipientsString(device.getNetworkId(),networkEmailNotification);		
									if (recipients != null && !recipients.isEmpty()){
										RailsMailMgr railsMailMgr = new RailsMailMgr();
										requestGeoFencesTemplate.setRecipient(recipients);
										String template = railsMailMgr.getGeoFencesTemplateJson(requestGeoFencesTemplate);
										AlertClientMgr alertClientMgr = new AlertClientMgr();
										alertClientMgr.sendEmailAlert(template);								
									}
								}
							} // end for (NetworkEmailNotifications networkEmailNotification: networkEmailNotificationList)
						} // end if (networkEmailNotificationList != null)
					} // end if (!isDeviceInSilencePeriod)
				} // end if (device != null)
										
			} // end if (criteria != null)
			else {
				log.warnf("GEO20140204 - something null-> msgType, orgId, netId, devId, level");
			}
		} // end try ...
		catch (Exception e) {
			log.error("GEO20140204 - sendGeoFencesAlert", e);
		} // end try ... catch ...
	} // end sendGeoFencesAlert
	
	
	public boolean sendOnlineImmediateAlert(SendImmediateAlertCriteria siaCriteria){
		boolean isAllSent = true;
		try{
			if (isSendImmediateAlertCriteriaReady(siaCriteria)){
				if (siaCriteria.getAlertType() != null && siaCriteria.getAlertType().equals(NetworkEmailNotifications.ALERT_TYPE_DEVICE)){
					if (log.isDebugEnabled()){
						log.debugf("ALERT201408211034 - AlertMgr.sendOnlineImmediateAlert() - siaCriteria: %s", siaCriteria);
					}
					
					DeviceStatusMgr deviceStatusMgr = new DeviceStatusMgr(siaCriteria.getOrgId());
					int lastSentLevel = deviceStatusMgr.getLastSentAlertLevelDeviceStatus(siaCriteria.getDevId());
					// online message loops deviceStatus table to notify online recipients according to offline alert
					for (int i = lastSentLevel; i > 0; i--){
						siaCriteria.setLevel(i);
						Thread.sleep(1000);
						boolean isOneSent = sendImmediateAlert(siaCriteria);
						if (!isOneSent){
							isAllSent = false;
						}
					}
					log.debugf("ALERT201408211034 - AlertMgr.sendOnlineImmediateAlert(), isAllSent: %s, siaCriteria: %s", isAllSent, siaCriteria);
	
				} else {
					log.warnf("ALERT201408211034 - AlertMgr.sendOnlineImmediateAlert(), this method is for ALERT_TYPE_DEVICE !!!!, siaCriteria: %s", siaCriteria);
				}
			}
		} catch (Exception e){
			log.error("ALERT201408211034 - AlertMgr.sendOnlineImmediateAlert()", e);
		}
		return isAllSent;
	}
		
	public boolean sendImmediateAlert(SendImmediateAlertCriteria siaCriteria){
		boolean isSent = false;
		try{
			if (isSendImmediateAlertCriteriaReady(siaCriteria)){
				EmailTemplateObjectDto emailTemplateObjectDto = null;
				boolean isNetworkEmailNotificationSkeletonGetReady = false;
				List<Integer>deviceIdListNotInSilencePeriod = new ArrayList<Integer>();
				
				for (Integer devId: siaCriteria.getDevIdList()){
					Devices device = NetUtils.getDevicesWithoutNetId(siaCriteria.getOrgId(), devId.intValue());
					if (device != null){
						NetworkSilencePeriodsMgr networkSilencePeriodMgr = new NetworkSilencePeriodsMgr(siaCriteria.getOrgId());
						boolean isDeviceInSilencePeriod = networkSilencePeriodMgr.isDeviceWithinSilencePeriod(device);
						if (!isDeviceInSilencePeriod){
							deviceIdListNotInSilencePeriod.add(device.getId());
							
							NetworkEmailNotificationsMgr networkEmailNotificationsMgr = new NetworkEmailNotificationsMgr(siaCriteria.getOrgId());
							
							// logic to check optional criteria networkEmailNotification, if not given, the method gets it by itself
							List<NetworkEmailNotifications> networkEmailNotificationList = null;
							if (siaCriteria.getNetworkEmailNotifications() == null){
								networkEmailNotificationList = networkEmailNotificationsMgr.getNetworkEmailNotificationList(device.getNetworkId(), siaCriteria.getAlertType(), siaCriteria.getLevel().toString());

							} else {
								networkEmailNotificationList = new ArrayList<NetworkEmailNotifications>();
								networkEmailNotificationList.add(siaCriteria.getNetworkEmailNotifications());
							}
							 
							if (networkEmailNotificationList != null && networkEmailNotificationList.size() > 0){
								for (NetworkEmailNotifications networkEmailNotification: networkEmailNotificationList){
									if (networkEmailNotification != null){
										if (!isNetworkEmailNotificationSkeletonGetReady){ // for multiple devices id, if EmailTemplateObject basic was not created, create once
											List<AlertEmailContactDto> alertEmailContactDtoList = networkEmailNotificationsMgr.getAlertEmailContactDtoList(device.getNetworkId(),networkEmailNotification);
											if (log.isDebugEnabled()){
												log.debugf("ALERT201408211034 - AlertMgr.sendImmediateAlert() - alertEmailContactDtoList: %s, device: %s", alertEmailContactDtoList, device);
											}
											if (alertEmailContactDtoList != null){
												if (log.isDebugEnabled()){
													log.debugf("ALERT201408211034 - AlertMgr.sendImmediateAlert(), alertEmailContactDtoList: %s", alertEmailContactDtoList);
												}
												AlertEmailContactListDto alertEmailContactListDto = new AlertEmailContactListDto();
												alertEmailContactListDto.setAdmins(alertEmailContactDtoList);
												
												emailTemplateObjectDto = new EmailTemplateObjectDto();
												emailTemplateObjectDto.setContactList(alertEmailContactListDto);
												emailTemplateObjectDto.setMsgId(siaCriteria.getMsgId());
												emailTemplateObjectDto.setMsgType(siaCriteria.getMsgType());
												emailTemplateObjectDto.setOrgId(siaCriteria.getOrgId());
	
												OrganizationsDAO organizationsDao = new OrganizationsDAO(siaCriteria.getOrgId());
												Organizations organization = organizationsDao.findById(siaCriteria.getOrgId());
												
												if (organization != null && organization.getName() != null){
													emailTemplateObjectDto.setOrganization_name(organization.getName());
												} else {
													if (log.isDebugEnabled()){
														log.debugf("ALERT201408211034 - AlertMgr.sendImmediateAlert(), organization or organization.getName() is null: %s", organization);
													}
												}
												
												Networks network = OrgInfoUtils.getNetwork(siaCriteria.getOrgId(), device.getNetworkId());
												if (network != null && network.getName() != null){
													emailTemplateObjectDto.setNetwork_name(network.getName());									
												} else {
													if (log.isDebugEnabled()){
														log.debugf("ALERT201408211034 - AlertMgr.sendImmediateAlert(), network or network.getName() is null: %s", network);
													}
												}
												Date datetime = siaCriteria.getDatetime(); 
												SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
												Date networkTime = datetime;
												String eventTime = sdf.format(networkTime);
												emailTemplateObjectDto.setEventTime(eventTime);
												emailTemplateObjectDto.setDuration(siaCriteria.getDuration());
												if (siaCriteria.getWanName() != null){
													emailTemplateObjectDto.setWanName(siaCriteria.getWanName());
												} else {
													emailTemplateObjectDto.setWanName("");
												}
												if (siaCriteria.getVpnName() != null){
													emailTemplateObjectDto.setVpnName(siaCriteria.getVpnName());
												} else {
													emailTemplateObjectDto.setVpnName("");
												}
												isNetworkEmailNotificationSkeletonGetReady = true;
												
											}
										} else {
											if (log.isDebugEnabled()){
												log.debugf("ALERT201408211034 - AlertMgr.sendImmediateAlert(), networkEmailNotification is null!! : devId: %s, orgId: %s", siaCriteria.getDevId(), siaCriteria.getOrgId());
											}
										}
									} // end if (!isNetworkEmailNotificationSkeletonGetReady)
								} // end for (NetworkEmailNotifications networkEmailNotification: networkEmailNotificationList)
								
							} else { // end if (networkEmailNotificationList != null)
								if (log.isDebugEnabled()){
									log.debugf("ALERT201408211034 - AlertMgr.sendImmediateAlert(), networkEmailNotificationList is null!! : devId: %s, orgId: %s", siaCriteria.getDevId(), siaCriteria.getOrgId());
								}
							}
						} else { // end if (!isDeviceInSilencePeriod)
							if (log.isDebugEnabled()){
								log.debugf("ALERT201408211034 - AlertMgr.sendImmediateAlert(), device is in SilencePeriod: devId: %s, orgId: %s", siaCriteria.getDevId(), siaCriteria.getOrgId());
							}
						}
					} else {
						if (log.isDebugEnabled()){
							log.debugf("ALERT201408211034 - AlertMgr.sendImmediateAlert(), device is null: devId: %s, orgId: %s", siaCriteria.getDevId(), siaCriteria.getOrgId());
						}
					}
				} // end for (Integer devId: siaCriteria.getDevIdList())
				if (emailTemplateObjectDto != null && deviceIdListNotInSilencePeriod.size() > 0 && isNetworkEmailNotificationSkeletonGetReady){
					isSent = placeSendMailCommand2BsAlertWs(siaCriteria, emailTemplateObjectDto, deviceIdListNotInSilencePeriod);
				}
			} else {
				log.warnf("ALERT201408211034 - AlertMgr.sendImmediateAlert(), criteria is null: %s", siaCriteria);
			}
		} catch (Exception e){
			log.error("ALERT201408211034 - AlertMgr.sendImmediateAlert()", e);
		}
		return isSent;
	}
	
	private boolean placeSendMailCommand2BsAlertWs(SendImmediateAlertCriteria siaCriteria, EmailTemplateObjectDto emailTemplateObjectDto, List<Integer> deviceIdListNotInSilencePeriod){
		boolean isSent = false;
		try{
			emailTemplateObjectDto.setDevList(deviceIdListNotInSilencePeriod);
			
			AlertClientMgr alertClientMgr = new AlertClientMgr();
			isSent = alertClientMgr.sendEmailAlert(emailTemplateObjectDto);		
			
			if (log.isDebugEnabled()){
				log.debugf("ALERT201408211034 - AlertMgr.placeSendMailCommand2BsAlertWs(), isSent:%s, siaCriteria.getLevel: %s, deviceIdListNotInSilencePeroid: %s, emailTemplateObjectDto: %s", isSent,siaCriteria.getLevel(), deviceIdListNotInSilencePeriod, emailTemplateObjectDto);
			}
			if (siaCriteria.getAlertType() != null && siaCriteria.getAlertType().equals(NetworkEmailNotifications.ALERT_TYPE_DEVICE) && isSent){
				List<DeviceStatus> deviceStatusList = null;
				
				if (deviceIdListNotInSilencePeriod != null && deviceIdListNotInSilencePeriod.size() > 0){
					deviceStatusList = new ArrayList<DeviceStatus>();
					
					for (Integer devId: deviceIdListNotInSilencePeriod){		

						if (siaCriteria.getMsgId() != null && siaCriteria.getMsgId().equals(SendImmediateAlertCriteria.MSG_ID_DEV_ONLINE)){
							if (siaCriteria.getLevel() != null){
								Integer nenLevel = siaCriteria.getLevel();
								
								if (nenLevel != null && nenLevel.intValue() == 1){
									DeviceStatus deviceStatus = new DeviceStatus();
									deviceStatus.setDeviceId(devId);
									deviceStatus.delete();
									deviceStatusList.add(deviceStatus);

								}
		
							}
						} else if (siaCriteria.getMsgId() != null && siaCriteria.getMsgId().equals(SendImmediateAlertCriteria.MSG_ID_DEV_OFFLINE)){
							DeviceStatus deviceStatus = new DeviceStatus();
							deviceStatus.setDeviceId(devId);
							Integer deviceStatusLevel = siaCriteria.getLevel();
							deviceStatus.setAlertedLevel(deviceStatusLevel);
							deviceStatus.setTimestamp(DateUtils.getUtcDate());
							deviceStatus.createReplace();
							deviceStatusList.add(deviceStatus);
						}
						
					} // end for (Integer devId: siaCriteria.getDevIdList())
					
					
					boolean isBatchSaved = false;
					if (deviceStatusList != null && deviceStatusList.size() > 0){
						DeviceStatusMgr deviceStatusMgr = new DeviceStatusMgr(siaCriteria.getOrgId());
						// doing in a batch mode.
						if (siaCriteria.getMsgId() != null && siaCriteria.getMsgId().equals(SendImmediateAlertCriteria.MSG_ID_DEV_ONLINE)){
							isBatchSaved = deviceStatusMgr.deleteDeviceStatusInBatch(deviceStatusList);
						} else if (siaCriteria.getMsgId() != null && siaCriteria.getMsgId().equals(SendImmediateAlertCriteria.MSG_ID_DEV_OFFLINE)){
							isBatchSaved = deviceStatusMgr.updateOrSaveDeviceStatusInBatch(deviceStatusList);
						}
					}
					
					if (!isBatchSaved){
						if (log.isDebugEnabled()){
							log.debugf("ALERT201408211034 - AlertMgr.placeSendMailCommand2BsAlertWs(), isBatchSaved: %s, deviceIdListNotInSilencePeroid: %s, orgId: %s", isBatchSaved, deviceIdListNotInSilencePeriod, siaCriteria.getOrgId());
						}
					} else {
						if (log.isDebugEnabled()){
							log.debugf("ALERT201408211034 - AlertMgr.placeSendMailCommand2BsAlertWs(), isBatchSaved: %s, deviceIdListNotInSilencePeroid: %s, orgId: %s",isBatchSaved, deviceIdListNotInSilencePeriod, siaCriteria.getOrgId());
						}
					}
					
				} // end if (siaCriteria.getDevIdList() != null && siaCriteria.getDevIdList().size() > 0)
				

				
			}
		} catch (Exception e){
			log.error("ALERT201408211034 - AlertMgr.placeSendMailCommand2BsAlertWs()", e);
		}
		return isSent;
	}
	
	private boolean isSendImmediateAlertCriteriaReady(SendImmediateAlertCriteria siaCriteria){
		boolean isReady = true;
		StringBuilder sb = new StringBuilder();		
		if (siaCriteria != null){
			int count = 0;
			
			if (siaCriteria.getMsgId() == null){
				if (count > 0){
					sb.append(",");
				}
				sb.append("msgId null!!!");
				isReady = false;
			}
			if (siaCriteria.getMsgType() == null){
				if (count > 0){
					sb.append(",");
				}
				sb.append("msgType null!!!");
				isReady = false;
			}
			if (siaCriteria.getOrgId() == null){
				if (count > 0){
					sb.append(",");
				}
				sb.append("orgId null!!!");
				isReady = false;
			}
			if (siaCriteria.getDevId() == null && siaCriteria.getDevIdList() == null && siaCriteria.getDevIdList().size() == 0){
				if (count > 0){
					sb.append(",");
				}
				sb.append("devId null!!! or devIdList null!!!");
				isReady = false;
			}
//			if (siaCriteria.getLevel() == null){
//				if (count > 0){
//					sb.append(",");
//				}
//				sb.append("level null!!!");
//				isReady = false;
//			}
			if (siaCriteria.getDatetime() == null){
				if (count > 0){
					sb.append(",");
				}
				sb.append("dateTime null!!!");
				isReady = false;
			}
			if (siaCriteria.getAlertType() == null){
				if (count > 0){
					sb.append(",");
				}
				sb.append("alertType null!!!");
				isReady = false;
			}
			
//			if (siaCriteria.getDuration() == null){
//				
//			}
//			if (siaCriteria.getWanName() == null){
//				
//			}
//			if (siaCriteria.getVpnName() == null){
//				
//			}

		} else {
			isReady = false;
			sb.append("siaCriteria null!!!");
		}
		
		if (!isReady){
			log.warnf("ALERT201408211034 - AlertMgr.isSendImmediateAlertCriteriaReady() not ready !!! siaCriteria: %s, message: %s", siaCriteria, sb.toString());
		}
		
		return isReady;
	}	
}
