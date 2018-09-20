package com.littlecloud.services;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.NetworkEmailNotificationsDAO;
import com.littlecloud.control.dao.criteria.NetworkEmailNotificationsCriteria;
import com.littlecloud.control.entity.NetworkEmailNotifications;
import com.littlecloud.dtos.json.AlertEmailContactDto;

public class NetworkEmailNotificationsMgr {
	private static final Logger log = Logger.getLogger(NetworkEmailNotificationsMgr.class);
	private String orgId;
	
	public NetworkEmailNotificationsMgr(String orgId){
		this.orgId = orgId;
	}
	
	public String getAlertEmailContactDtoRecipientsString(Integer networkId, NetworkEmailNotifications networkEmailNotification){
		StringBuilder rtnString = new StringBuilder();
		try{
			List<AlertEmailContactDto> alertEmailContactDtoList = getAlertEmailContactDtoList(networkId, networkEmailNotification);
			if (alertEmailContactDtoList != null){
				for (int i = 0; i < alertEmailContactDtoList.size(); i++){
					AlertEmailContactDto alertEmailContactDto = alertEmailContactDtoList.get(i);
					if (i > 0){
						rtnString.append(" ");
					}
					if (alertEmailContactDto.getEmail() != null && !alertEmailContactDto.getEmail().isEmpty()){
						rtnString.append(alertEmailContactDto.getEmail());
					}
				}
			}
			
		} catch (Exception e){
			log.error("ALERT201408211034 -  NetworkEmailNotificationsMgr.getAlertEmailContactDtoRecipientsString()", e);
		}
		return rtnString.toString();
	}
	
	public List<AlertEmailContactDto> getAlertEmailContactDtoList(Integer networkId, NetworkEmailNotifications networkEmailNotification){
		List<AlertEmailContactDto> alertEmailContactDtoList = null;
		try{
			if (networkId != null && 
					networkEmailNotification.getRecipientType() != null && !networkEmailNotification.getRecipientType().isEmpty()
				){
				if (log.isDebugEnabled()){
					log.debugf("ALERT201408211034 - NetworkEmailNotificationsMgr.getAlertEmailContactDtoList: networkId: %s, networkEmailNotification: %s", networkId, networkEmailNotification);
				}
				switch (networkEmailNotification.getRecipientType()) {
				
					case NetworkEmailNotifications.RECIPIENT_TYPE_ONE_ADMIN:
					case NetworkEmailNotifications.RECIPIENT_TYPE_ALL_ADMIN:
					case NetworkEmailNotifications.RECIPIENT_TYPE_ALL_GROUP_ADMIN:
						RailsMailMgr railsMailMgr = new RailsMailMgr();
						alertEmailContactDtoList = railsMailMgr.getAlertEmailContactList(this.orgId, networkId, networkEmailNotification.getInfo(), networkEmailNotification.getRecipientType());
						break;
					case NetworkEmailNotifications.RECIPIENT_TYPE_EMAILS:
						alertEmailContactDtoList = getAlertEmailContactDtoFromNetworkEmailNotification(networkEmailNotification);
						break;
				}
					
				if (alertEmailContactDtoList == null){
					log.warnf("ALERT201408211034 - NetworkEmailNotificationsMgr.getAlertEmailContactDtoList() - mail contact list is empty from rails!! orgId %s netId %d nen %s", orgId, networkId, networkEmailNotification.getInfo());
				}
			} else {
				log.warnf("ALERT201408211034 - NetworkEmailNotificationsMgr.getNetworkEmailNotificationDtoList(), "
						+ "networkId or networkEmailNotification or networkEmailNotification.getRecipientType() or networkEmailNotification.getInfo() is/ are null!!!!!!, %s, %s", 
						networkId, networkEmailNotification);
			}
		} catch (Exception e){
			log.error("ALERT201408211034 -  NetworkEmailNotificationsMgr.getAlertEmailContactDtoList()", e);
		}
		return alertEmailContactDtoList;
	}
	
	private List<AlertEmailContactDto> getAlertEmailContactDtoFromNetworkEmailNotification(NetworkEmailNotifications networkEmailNotification){
		List<AlertEmailContactDto> alertEmailContactDtoList = null;
		try{
			if (networkEmailNotification.getInfo() != null){
				String emailList[] = networkEmailNotification.getInfo().split(" ");
				alertEmailContactDtoList = new ArrayList<AlertEmailContactDto>();
				for (String email : emailList) {
					AlertEmailContactDto emailContact = new AlertEmailContactDto();
					emailContact.setEmail(email);
					emailContact.setFull_name("");;
					alertEmailContactDtoList.add(emailContact);
				}
			} else {
				log.warnf("ALERT201408211034 - NetworkEmailNotificationsMgr.getAlertEmailContactDtoFromNetworkEmailNotification(), networkEmailNotification.getInfo() is null: %s", networkEmailNotification);
			}
		} catch (Exception e){
			log.error("ALERT201408211034 - NetworkEmailNotificationsMgr.getAlertEmailContactDtoFromNetworkEmailNotification()", e);
		}
		return alertEmailContactDtoList;
	}
	
	
	public List<NetworkEmailNotifications> getNetworkEmailNotificationList(Integer networkId, String alertType, String level){
		List<NetworkEmailNotifications> networkEmailNotificationsList = null;
		try{
			NetworkEmailNotificationsDAO networkEmailNotificationsDao = new NetworkEmailNotificationsDAO(orgId);
			
			NetworkEmailNotificationsCriteria networkEmailNotificationsCriteria = new NetworkEmailNotificationsCriteria();
			networkEmailNotificationsCriteria.setAlertType(alertType);
			networkEmailNotificationsCriteria.setLevel(level);
			networkEmailNotificationsCriteria.setNetworkId(networkId);
			
			networkEmailNotificationsList = networkEmailNotificationsDao.getNetworkEmailNotificationList(networkEmailNotificationsCriteria);
		} catch (Exception e){
			log.error("ALERT201408211034 -  NetworkEmailNotificationsMgr.getNetworkEmailNotificationList()", e);
		}
		return networkEmailNotificationsList;
	}
}
