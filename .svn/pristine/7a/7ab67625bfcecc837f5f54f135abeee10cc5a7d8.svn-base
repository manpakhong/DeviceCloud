package com.littlecloud.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.DebugManager;
import com.littlecloud.ac.handler.OnlineStatusAndCustomEventHandler;
import com.littlecloud.ac.handler.OnlineStatusAndCustomEventHandler.UPDATE_REASON;
import com.littlecloud.ac.health.HealthMonitorHandler;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.dao.OrganizationsDAO;
import com.littlecloud.control.dao.branch.SnsOrganizationsDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Devices.ONLINE_STATUS;
import com.littlecloud.control.entity.NetworkEmailNotifications;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.Organizations;
import com.littlecloud.control.entity.branch.SnsOrganizations;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.util.NetworkUtils;
import com.littlecloud.criteria.SendImmediateAlertCriteria;
import com.littlecloud.dtos.OfflineAlertNetworkEmailNotificationsLvDto;
import com.littlecloud.dtos.json.EmailTemplateObjectDto;
import com.littlecloud.helpers.NetworkEmailNotificationsHelper;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.utils.BranchUtils;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.utils.PropertyService;
import com.peplink.api.db.util.DBUtil;

public class OfflineAlertCheckingMgr {
	private static boolean PROD_MODE = DebugManager.isPROD_MODE();

	private static final Logger log = Logger.getLogger(OfflineAlertCheckingMgr.class);
	// TODO rename the properties with the package and class name
	private static final PropertyService<OfflineAlertCheckingMgr> ps = new PropertyService<OfflineAlertCheckingMgr>(
			OfflineAlertCheckingMgr.class);
	private static Integer normalIdleThreshold;
	private static Integer lbIdleThreshold;
	
	public static boolean isOfflineAlertCheckRunning = false;

	public static Integer getNormalIdleThreshold() {
		try{
			if (normalIdleThreshold == null){
				normalIdleThreshold = ps.getInteger("idleThresholdNormal");
			}
		} catch (Exception e){
			log.error("ALERT201408211034 - OfflineAlertCheckingMgr.getNormalIdleThreshold() - ", e);
		}
		return normalIdleThreshold;
	}

	public static Integer getLbIdleThreshold() {
		try{
			if (lbIdleThreshold == null){
				lbIdleThreshold = ps.getInteger("idleThresholdNormalLowBandwidth");
			}
		} catch (Exception e){
			log.error("ALERT201408211034 - OfflineAlertCheckingMgr.getLbIdleThreshold() - ", e);
		}
		return lbIdleThreshold;
	}
	
	private static boolean isDeviceCurrentlyOfflineNetworkEmailNotificationTimeUp2SendMail(Devices device, Integer offlineMin){
		boolean isOfflineAndNenTimeUp2SendMail = false;
		try{
			if (device != null && device.getOfflineAt() != null && offlineMin != null){
				Long offlineTimeThresholdInMilisecond = ((long) offlineMin * 60 * 1000);
				long devOfflineTime = device.getOfflineAt().getTime();
				long devLastOnlineTime = device.getLastOnline().getTime();
				if (log.isDebugEnabled()){
					log.debugf("ALERT201408211034 - (1) OfflineAlertCheckingMgr.isDeviceCurrentlyOfflineNetworkEmailNotificationTimeUp2SendMail(), device %s, deviceOfflineTime:%s > devLastOnlineTime: %s", device, devOfflineTime, devLastOnlineTime);
					log.debugf("ALERT201408211034 - (2) OfflineAlertCheckingMgr.isDeviceCurrentlyOfflineNetworkEmailNotificationTimeUp2SendMail(), device %s, (System.currentTimeMillis(): %s - devOfflineTime: %s) > offlineTimeThresholdInMilisecond: %s", device, System.currentTimeMillis(), devOfflineTime, offlineTimeThresholdInMilisecond);
				}
				if ((devOfflineTime > devLastOnlineTime) && 
						((System.currentTimeMillis() - devOfflineTime) > offlineTimeThresholdInMilisecond)) {
					isOfflineAndNenTimeUp2SendMail = true;
					if (log.isDebugEnabled()){
						log.debugf("ALERT201408211034 - OfflineAlertCheckingMgr.isDeviceCurrentlyOfflineNetworkEmailNotificationTimeUp2SendMail(): %s, device: %s, offlineMin: %s", isOfflineAndNenTimeUp2SendMail, device, offlineMin);
					}
				}
			}
			
		} catch (Exception e){
			log.error("ALERT201408211034 - OfflineAlertCheckingMgr.isDeviceCurrentlyOffline()!!!", e);
		}
		return isOfflineAndNenTimeUp2SendMail;
	}
	

	public static void checkOfflineAlert(String orgId) {
		if (log.isDebugEnabled()){
			log.debugf("ALERT201408211034 - OfflineAlertCheckingMgr.checkOfflineAlert(), orgId %s", orgId);
		}
		DBUtil dbUtil = null;
		OrganizationsDAO organizationsDao = null;
		NetworksDAO networksDao = null;
		Organizations organization = null;

		try {
			dbUtil = DBUtil.getInstance();
			if (!dbUtil.isSessionStarted()) {
				dbUtil.startSession();
			}
			
			organizationsDao = new OrganizationsDAO(orgId, true);
			organization = organizationsDao.findById(orgId);
			if (organization != null) {
				networksDao = new NetworksDAO(orgId);
				List<Networks> networkList = networksDao.getAllNetworks();
				
				if (networkList != null){
					for (Networks network: networkList){
						NetworkEmailNotificationsHelper networkEmailNotificationsHelper = new NetworkEmailNotificationsHelper(network, orgId);
						
						boolean isDeviceEmailEnabled = networkEmailNotificationsHelper.isDeviceEmailAlertEnabled();
//						boolean isWanEmailEnabled = networkEmailNotificationsHelper.isWanEmailAlertEnabled();
//						boolean isSpeedfusionEmailEnabled = networkEmailNotificationsHelper.isSpeedfusionEmailAlertEnabled();
						if (log.isDebugEnabled()){
							log.debugf("ALERT201408211034 - OfflineAlertCheckingMgr.checkOfflineAlert() - networkEmailNotificationsHelper: %s, networkId: %s, ",networkEmailNotificationsHelper, network.getId());
						}
						if (isDeviceEmailEnabled){
							List<Devices> deviceList = NetUtils.getDeviceLstByNetId(orgId, network.getId());
							// deviceStatusMgr store all DeviceStatus for network in memory
							DeviceStatusMgr deviceStatusMgr = new DeviceStatusMgr(orgId, deviceList);
							if (log.isDebugEnabled()){
								log.debugf("ALERT201408211034 - OfflineAlertCheckingMgr.checkOfflineAlert() - deviceList:%s, networkId: %s, networkEmailNotificationsHelper: %s",deviceList, network.getId(), networkEmailNotificationsHelper);
							}
							if (deviceList != null && deviceList.size() > 0){
								Map<Integer, OfflineAlertNetworkEmailNotificationsLvDto> offlineDeviceReady2SendMailMap = new HashMap<Integer, OfflineAlertNetworkEmailNotificationsLvDto>();
								for (Devices device: deviceList){
									if (device != null && device.getId() != null){											
											Integer level = deviceStatusMgr.getNextTurnAlertLevel(device.getId());
											if (log.isDebugEnabled()){
												log.debugf("ALERT201408211034 - OfflineAlertCheckingMgr.checkOfflineAlert() - nextLevel: %s, deviceList:%s, networkId: %s, networkEmailNotificationsHelper: %s",level, deviceList, network.getId(), networkEmailNotificationsHelper);
											}
											NetworkEmailNotifications nen = networkEmailNotificationsHelper.getNetworkEmailNotificationsByAlertType(SendImmediateAlertCriteria.ALERT_TYPE_DEVICE, level);
											if (nen != null){
												if (log.isDebugEnabled()){
													if (nen.getLevel() != null){
														log.debugf("ALERT201408211034 - OfflineAlertCheckingMgr.checkOfflineAlert() - get from networkEmailNotificationsHelper -> nen.getLevel: %s, orgId: %s, device: %s, nen: %s",nen.getLevel(), orgId, device, nen);
													} else {
														log.debugf("ALERT201408211034 - OfflineAlertCheckingMgr.checkOfflineAlert() - get from networkEmailNotificationsHelper -> next turn level: %s, orgId: %s, device: %s, nen: %s",level, orgId, device, nen);
													}
												}
												
												boolean isDeviceCurrentlyOfflineNetworkEmailNotificationTimeUp2SendMail = isDeviceCurrentlyOfflineNetworkEmailNotificationTimeUp2SendMail(device, nen.getOfflineMin());
												if(isDeviceCurrentlyOfflineNetworkEmailNotificationTimeUp2SendMail){
													OfflineAlertNetworkEmailNotificationsLvDto offlineAlertNetworkEmailNotificationsLvDto = offlineDeviceReady2SendMailMap.get(level);
													if (offlineAlertNetworkEmailNotificationsLvDto == null){
														offlineAlertNetworkEmailNotificationsLvDto = new OfflineAlertNetworkEmailNotificationsLvDto();
														offlineAlertNetworkEmailNotificationsLvDto.setNetworkEmailNotification(nen);
														List<Integer> deviceIdList = offlineAlertNetworkEmailNotificationsLvDto.getDeviceList();
														deviceIdList.add(device.getId());
														offlineDeviceReady2SendMailMap.put(level, offlineAlertNetworkEmailNotificationsLvDto);
													} else {
														List<Integer> deviceIdList = offlineAlertNetworkEmailNotificationsLvDto.getDeviceList();
														deviceIdList.add(device.getId());
													}
												} else {
													if (log.isDebugEnabled()){
														log.debugf("ALERT201408211034 - OfflineAlertCheckingMgr.checkOfflineAlert() - isDeviceCurrentlyOfflineNetworkEmailNotificationTimeUp2SendMail: false!!!, device: %s, orgId: %s", orgId, device);
													}
												}
											} else {
												if (log.isDebugEnabled()){
													log.debugf("ALERT201408211034 - OfflineAlertCheckingMgr.checkOfflineAlert() - nen is null!!!, orgId: %s, device:%s ", orgId, device);
												}
											}
									}
								} // end for (Devices device: deviceList)
								
								if (offlineDeviceReady2SendMailMap.size() > 0){
									sendEmailAlertInBatch(orgId, offlineDeviceReady2SendMailMap);
								}
							} // end if (deviceList != null && deviceList.size() > 0)
						} // end if (isDeviceEmailEnabled)
					} // end for (Networks network: networkList)
				} else {
					if (log.isDebugEnabled()){
						log.debugf("ALERT201408211034 - OfflineAlertCheckingMgr.checkOfflineAlert() - networkList is null!!!, orgId: %s", orgId);
					}
				}
			} else {
				log.warnf("ALERT201408211034 - OfflineAlertCheckingMgr.checkOfflineAlert(), orgId: %s doesnt exists!", orgId);				
			}
		} catch (Exception e) {
			log.errorf("ALERT201408211034 - OfflineAlertCheckingMgr.checkOfflineAlert(), orgId: %s, Exception: %s", orgId, e);
		} finally {
			try {
				if (dbUtil != null && dbUtil.isSessionStarted()) {
					dbUtil.endSession();
				}
			} catch (Exception e) {
				log.errorf("ALERT201408211034 - OfflineAlertCheckingMgr.checkOfflineAlert(), error in endSession: %s", e);
			}
		}
	}

	private static boolean sendEmailAlertInBatch(String orgId, Map<Integer, OfflineAlertNetworkEmailNotificationsLvDto> offlineDeviceReady2SendMailMap){
		boolean isAllSent = true;
		try{
			Iterator it = offlineDeviceReady2SendMailMap.entrySet().iterator();
			
			while (it.hasNext()){
				
				Map.Entry<Integer, OfflineAlertNetworkEmailNotificationsLvDto> pairs = (Map.Entry<Integer, OfflineAlertNetworkEmailNotificationsLvDto>) it.next();
	
				Integer level = pairs.getKey();
				OfflineAlertNetworkEmailNotificationsLvDto offlineAlertNetworkEmailNotificationsLvDto = pairs.getValue();
				NetworkEmailNotifications nen = offlineAlertNetworkEmailNotificationsLvDto.getNetworkEmailNotification();
				List<Integer> deviceIdList = offlineAlertNetworkEmailNotificationsLvDto.getDeviceList();
				if (log.isDebugEnabled()){
					log.debugf("ALERT201408211034 - OfflineAlertCheckingMgr.sendEmailAlertInBatch() - level: %s, offlineAlertNetworkEmailNotificationsLvDto: %s", level, offlineAlertNetworkEmailNotificationsLvDto);
				}			
				
				AlertMgr alertMgr = new AlertMgr();
				SendImmediateAlertCriteria siaCriteria = new SendImmediateAlertCriteria();
				
				siaCriteria.setMsgId(EmailTemplateObjectDto.EMAIL_MSG_ID_DEV_OFFLINE);
				siaCriteria.setMsgType(EmailTemplateObjectDto.EMAIL_MSG_TYPE_EMAIL);
				siaCriteria.setOrgId(orgId);
				siaCriteria.setDevIdList(deviceIdList);
				siaCriteria.setLevel(level);
				siaCriteria.setDuration(0);
				siaCriteria.setWanName("");
				siaCriteria.setVpnName("");
				siaCriteria.setDatetime(DateUtils.getUtcDate());
				siaCriteria.setAlertType(SendImmediateAlertCriteria.ALERT_TYPE_DEVICE);
				siaCriteria.setNetworkEmailNotifications(nen);
				
				boolean isSent = alertMgr.sendImmediateAlert(siaCriteria);
				if (!isSent){
					isAllSent = false;
				}
				if (log.isDebugEnabled()){
					log.debugf("ALERT201408211034 - OfflineAlertCheckingMgr.sendEmailAlertInBatch() - sendImmediateAlert by OfflineAlertCheckingMgr!!!, orgId: %s, deviceIdList: %s", orgId, deviceIdList);
				}
	
			}
		} catch (Exception e){
			log.errorf("ALERT201408211034 - OfflineAlertCheckingMgr.sendEmailAlertInBatch(), Exception: %s", e);
		}
		return isAllSent;
	}
	
	
	public static void startOfflineAlertChecking() {

		if (isOfflineAlertCheckRunning){
			return;
		}
		else {
			isOfflineAlertCheckRunning = true;
		}
		
		log.warnf("ALERT201408211034 - OfflineAlertCheckingMgr.startCheckingOfflineAlert() - INFO StartCheckingOfflineAlert is running...");

		// get list of organization from branch DB
		List<String> snsOrganizationsList = null;
		try {
			 boolean isClusterHealthy = HealthMonitorHandler.isClusterHealthy();
			 if (PROD_MODE && !isClusterHealthy){
				 log.warnf("ALERT201408211034 - OfflineAlertCheckingMgr.startCheckingOfflineAlert(), Cluster system is currently in unhealthy state, skip alert check. isClusterHealthy: %s ", isClusterHealthy);
				 return;
			 }
	
			SnsOrganizationsDAO snsOrganizationsDAO = new SnsOrganizationsDAO(true);
			snsOrganizationsList = snsOrganizationsDAO.getDistinctOrgList();
			
			// for each organization, get list of networks
			for (String snsOrganization : snsOrganizationsList) {
				checkOfflineAlert(snsOrganization);
			}
			
		} catch (Exception e) {
			log.errorf("ALERT201408211034 - OfflineAlertCheckingMgr.startCheckingOfflineAlert(), Exception: %s", e);
			return;
		} finally {
			isOfflineAlertCheckRunning = false;
			if (log.isDebugEnabled()){
				log.debug("ALERT201408211034 - OfflineAlertCheckingMgr.startCheckingOfflineAlert() - INFO StartCheckingOfflineAlert is done");
			}
		}
	}
	public static void startCheckIdleDevice() {

		 //TODO temporary disable until health check is ready!!
		 if (PROD_MODE && !com.littlecloud.ac.health.HealthMonitorHandler.isClusterHealthy()){
			 log.warn("Cluster system is currently in unhealthy state, skip idle check.");
			 return;
		 }

		// HibernateUtil hBrunchutil = new HibernateUtil(true);
		List<String> snsOrganizationsList = null;
		try {
			// hBrunchutil.beginBranchTransaction();
			SnsOrganizationsDAO snsOrganizationsDAO = new SnsOrganizationsDAO(true);
			snsOrganizationsList = snsOrganizationsDAO.getDistinctOrgList();
			// hBrunchutil.commitBranchTransaction();
			log.debug("snsOrganizationsList" + snsOrganizationsList);
		} catch (Exception e) {
			// hBrunchutil.rollbackTransaction();
			log.error("transaction is rollback - " + e, e);
			log.error("Get organization list error, archive raw data aborted");
			return;
		}

		// for each organization, check all devices
		for (String snsOrganization : snsOrganizationsList) {
			checkIdleDevice(snsOrganization);
		}
	}

	public static void checkIdleDevice(String orgId) {
		boolean isOwnSession = false;
		DBUtil dbUtil = null;
		String logMsg = null;

		List<Devices> devList = null;
		//Long idleThreshold = 0L;
		try {
			/* session for rebuild online object!! */
			dbUtil = DBUtil.getInstance();
			if (!dbUtil.isSessionStarted()) {
				dbUtil.startSession();
				isOwnSession = true;
			}

			DevicesDAO devicesDAO = new DevicesDAO(orgId);
			NetworksDAO networksDAO = new NetworksDAO(orgId);
			devList = devicesDAO.getAllDevices();
			Date curTime = DateUtils.getUtcDate();
			if (devList != null) {
				for (Devices dev : devList) {
					log.debug("dev = " + dev);
					if (!dev.isActive()) {
						logMsg = String.format("device %d %s is not active.", dev.getIanaId(), dev.getSn());
						DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.DEBUG, logMsg);
						continue;
					}

					DevOnlineObject devO = DeviceUtils.getDevOnlineObject(dev);
					if (devO == null) {
						logMsg = String.format("WARN PERFORMANCE - dev online object for %d %s does not exist, rebuild", dev.getIanaId(), dev.getSn());
						DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.WARN, logMsg);
						devO = OnlineStatusAndCustomEventHandler.rebuildLastDevOnlineObjectIfWarrantyValid(
								MessageType.PIPE_INFO_TYPE_DUMMY, null, orgId, dev.getIanaId(), dev.getSn());
						if (devO == null) {
							logMsg = String.format("online status is not rebuilt for orgId %s dev %d %s!!", orgId, dev.getIanaId(),
									dev.getSn());
							DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.WARN, logMsg);
							continue;
						}
					}

					if (!devO.isOnline()) {
						/* note: keep this part until devO.ONLINE_STATUS without UNKNOWN status */
						log.debug("dev is offline");
						continue;
					}

					if (devO == null || devO.getStatus() != ONLINE_STATUS.ONLINE) {
						logMsg = String.format("dev %d %s is not in properly online status %s (skip unknown or others)", dev.getIanaId(), dev.getSn(), devO.getStatus());
						DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.DEBUG, logMsg);
						continue;
					}

					Networks network = networksDAO.findById(dev.getNetworkId());
					if (network == null) {
						logMsg = String.format("dev %d %s has invalid network_id %d !! skip!", dev.getIanaId(), dev.getSn(),
								dev.getNetworkId());
						DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.WARN, logMsg);
						continue;
					}
					
					if (dev.getLastOnline() != null && dev.getOfflineAt() != null && dev.getLastOnline().getTime() < dev.getOfflineAt().getTime()) {
						logMsg = String.format("dev %d %s status in db is in conflict with cache status!! fetch online!! dev=%s devO=%s",
								dev.getIanaId(), dev.getSn(), dev, devO);
						DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.WARN, logMsg);
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_ONLINE, JsonUtils.genServerRef(), dev.getIanaId(), dev.getSn());
					}

					//PERFORMANCE - use to verify data integrity
					if (BranchUtils.isLoaded()){
						SnsOrganizations snsOrg = BranchUtils.getSnsOrganizationsByIanaIdSn(dev.getIanaId(), dev.getSn());
						if (snsOrg==null){
							logMsg = String.format("Mark offline device %d %s is not found in sns_organizations", dev.getIanaId(), dev.getSn());
							DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.WARN, logMsg);						
							continue;
						} else {
							if (!snsOrg.getOrganizationId().equalsIgnoreCase(orgId)){
								logMsg = String.format("Mark offline device %d %s orgId %s is outdated! sns_organizations snsOrg %s", 
										dev.getIanaId(), dev.getSn(), orgId, snsOrg);
								DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.ERROR, logMsg);						
								continue;
							}
						}
					}
					else{
						break;
					}
					
					
					// modify to use DevOnlineObject instead of Devices
					if ( dev.getFirstAppear() == null || dev.getLastOnline() == null 
							|| (devO.getLastUpdateTime()!=null && curTime.getTime() - devO.getLastUpdateTime().getTime() > NetworkUtils.getNetworkIdleThreshold(network)) ) {
						if (OnlineStatusAndCustomEventHandler.updateOnlineStatusAndHistoryEventLog(orgId, dev.getId(),
								devO.getStatus(), ONLINE_STATUS.OFFLINE, DateUtils.getUtcDate(), UPDATE_REASON.markoffline)) {
							logMsg = String.format("LT10001 - dev %d %s has been marked offline "
									+ "curTime %d - last_ac_update %d threashold %d", dev.getIanaId(), dev.getSn(),
									curTime.getTime(), devO.getLastUpdateTime().getTime(), NetworkUtils.getNetworkIdleThreshold(network));							
							DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.WARN, logMsg);
						} else {
							logMsg = String.format("LT10001 - dev %d %s fail to be marked offline", dev.getIanaId(), dev.getSn());
							DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.WARN, logMsg);
						}

					} else {
						log.debugf(
								"condition not match: curTime (%s) - dev.getLast_ac_updatetime() (%s) > idleThreshold (%d)",
								curTime, devO.getLastUpdateTime(), NetworkUtils.getNetworkIdleThreshold(network));
					}

				}
			}
		} catch (Exception e) {
			log.error("CheckIdleDevice Exception", e);
		} finally {
			try {
				if (isOwnSession) {
					if (dbUtil != null && dbUtil.isSessionStarted()) {
						dbUtil.endSession();
					}
				}
			} catch (Exception e2) {
				log.error("CheckIdleDevice - error in endSession", e2);
			}
		}
	}
	

}
