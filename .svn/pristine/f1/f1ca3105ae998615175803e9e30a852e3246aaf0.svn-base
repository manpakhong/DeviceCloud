package com.littlecloud.ac.handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.WtpMsgHandler;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.DeviceGpsLocationsDatesDAO;
import com.littlecloud.control.dao.DeviceOnlineHistoryDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.EventLogDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.dao.branch.SnsOrganizationsDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Devices.ONLINE_STATUS;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.branch.SnsOrganizations;
import com.littlecloud.control.entity.report.DeviceGpsLocations;
import com.littlecloud.control.entity.report.DeviceGpsLocationsDates;
import com.littlecloud.control.entity.report.DeviceGpsLocationsDatesId;
import com.littlecloud.control.entity.report.DeviceGpsLocationsId;
import com.littlecloud.control.entity.report.DeviceOnlineHistories;
import com.littlecloud.control.entity.report.EventLog;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerDeviceTask;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerDeviceTask.CONFIG_UPDATE_REASON;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerNetworkTask;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.control.QueueControl;
import com.littlecloud.pool.object.DevBandwidthObject;
import com.littlecloud.pool.object.DevDetailObject;
import com.littlecloud.pool.object.DevInfoObject;
import com.littlecloud.pool.object.DevLocationsObject;
import com.littlecloud.pool.object.DevLocationsReportObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.DeviceLastLocObject;
import com.littlecloud.pool.object.LocationList;
import com.littlecloud.pool.object.StationBandwidthListObject;
import com.littlecloud.pool.object.StationListObject;
import com.littlecloud.pool.object.utils.BranchUtils;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.littlecloud.pool.object.utils.NetUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.util.DBUtil;

/*
 * Responsibilities:
 * Handle message online/offline/unknown status
 * Rebuild online status if necessary
 * 
 */
public class OnlineStatusAndCustomEventHandler {

	private static final Logger log = Logger.getLogger(OnlineStatusAndCustomEventHandler.class);
	private static final int EVENT_EXPIRED_TIME_IN_SEC = WtpMsgHandler.getEventExpiredTimeInSec();
	private static final int MAX_BATCH_ADD = 50;
	
	//private static final int MAX_CURRENT_ONLINEEVENT_COUNTER = 50;	
	//private static AtomicInteger currentOnlineEventCounter = new AtomicInteger(0);
	
	private static final int MAX_CURRENT_REBUILD_COUNTER = 50;	
	private static AtomicInteger currentRebuildCounter = new AtomicInteger(0);
	public static enum UPDATE_REASON { onofflineevent, markoffline, warrantyexpired	}

	/* 
	 * return true if status and db is updated
	 * 
	 */
	public static boolean handle(QueryInfo<Object> info)
	{				
		boolean result = true;
		boolean isOwnSession = false;
//		boolean isOnlineEventCounted = false;
		
		DBUtil dbUtil = null;
		String devLog = null;
		String devOrgId = null;
		Integer devId = null;
		Date eventUTC = null;
		
		DevicesDAO devDAO = null;
		Devices dev = null;
		String logMsg = null;

		if (info.getTimestamp()==0)
		{
			log.errorf("Invalid queryInfo timestamp in %s", info);
			return false;
		}
		else if (info.getSn() == null || info.getSn().isEmpty())
		{
			log.debugf("info is not a device message!");
			return false;
		}
		
		try {
			eventUTC = DateUtils.getUtcDateFromUnixTime(info.getTimestamp());
			
			Integer devIanaId = info.getIana_id();
			String devSn = info.getSn();
			devLog = String.format("dev %d %s", devIanaId, devSn);
			
			MessageType mt = info.getType();
					
			devOrgId = DeviceUtils.getOrgId(devIanaId, devSn);
			if (devOrgId == null)
			{
				log.infof("mt %s no orgId for %s", mt, devLog);
				return false;
			}
					
			/* rebuild past online status with online object */
			DevOnlineObject devO = DeviceUtils.getDevOnlineObject(devIanaId, devSn);
			if (devO == null)
			{
				/* start session */
				dbUtil = DBUtil.getInstance();
				if (!dbUtil.isSessionStarted())
				{
					dbUtil.startSession();
					isOwnSession = true;
				}
				
				devO = rebuildLastDevOnlineObjectIfWarrantyValid(mt, info, devOrgId, devIanaId, devSn);
				if (devO == null)
				{
					logMsg = String.format("online status is not rebuilt for %s, probably not registered if not due to exception.",devLog);
					DeviceUtils.logDevInfoObject(devIanaId, devSn, log, Level.DEBUG, logMsg);
					return false;
				}
			}
			else
			{
				/* update network or other core informaton from db object */
				dev = NetUtils.getDevices(devOrgId, devIanaId, devSn);
				if (dev==null)
				{
					logMsg = String.format("dev %s %d %s is not found in NetUtils!!", devOrgId, devIanaId, devSn);
					DeviceUtils.logDevInfoObject(devIanaId, devSn, log, Level.DEBUG, logMsg);
					return false;
				}
				devO.updateCoreInfo(devOrgId, dev);
			}
			log.debugf("handle devO=%s", devO);
	
			/* Handle Live Requests - update online object and save custom event */
			switch (mt)
			{
				case PIPE_INFO_TYPE_DEV_ONLINE:
//					isOnlineEventCounted = true;
					//currentOnlineEventCounter.incrementAndGet();
					//log.warnf("DEBUG online event (%d)", currentOnlineEventCounter.intValue());

//					if (currentOnlineEventCounter.intValue()>MAX_CURRENT_ONLINEEVENT_COUNTER)
//					{
//						log.warnf("MAX_CURRENT_ONLINE_EVENTCOUNTER %d is reached, skip %s! (current=%d)", MAX_CURRENT_ONLINEEVENT_COUNTER, devLog, currentOnlineEventCounter.intValue());						
//						return false;
//					}
					
					/* Fix out sync online status */
					if (devO.getStatus()==ONLINE_STATUS.ONLINE && !devO.isOnline())
					{
						log.warnf("Fix isOnline out sync with onlineStatus %s", devLog);
						devO.setOnline(true);
						DeviceUtils.putOnlineObject(devO);
					}
					
					/* start session */
					dbUtil = DBUtil.getInstance();
					if (!dbUtil.isSessionStarted())
					{
						dbUtil.startSession();
						isOwnSession = true;
					}

					devId = devO.getDevice_id();		
					devDAO = new DevicesDAO(devOrgId);
					dev = devDAO.findById(devId);
					
					DevOnlineObject devONew = DeviceUtils.getDevOnlineObjectFromQueryInfo(info);
					if (devONew == null)
					{
						logMsg = String.format("fail to get online object structure from queryInfo for %s",devLog);
						DeviceUtils.logDevInfoObject(devIanaId, devSn, log, Level.WARN, logMsg);
						return false;
					}
					DeviceUtils.logDevInfoObject(devIanaId, devSn, log, Level.INFO, "Received ONLINE from "+devLog);					
					
					updateOnlineStatusAndHistoryEventLog(devONew, devOrgId, devId, devO.getStatus(), ONLINE_STATUS.ONLINE, eventUTC, UPDATE_REASON.onofflineevent);
					break;		
				case PIPE_INFO_TYPE_DEV_OFFLINE:
					log.info("offline event");
					
					/* start session */
					dbUtil = DBUtil.getInstance();
					if (!dbUtil.isSessionStarted())
					{
						dbUtil.startSession();
						isOwnSession = true;
					}
					
					devId = devO.getDevice_id();		
					devDAO = new DevicesDAO(devOrgId);
					dev = devDAO.findById(devId);
					
					String curMachineId = ACService.getServerName();
					if (!devO.getMachine_id().equals(curMachineId)) {
						/* e.g. Online object is from ac1, but this offline is from ac2 */
						logMsg = String.format("Ignore outdated DEV_OFFLINE from current AC. dev %d %s current registered AC server is %s", devIanaId, devSn, devO.getMachine_id());
						DeviceUtils.logDevInfoObject(devIanaId, devSn, log, Level.DEBUG, logMsg);
						break;
					}
					DeviceUtils.logDevInfoObject(devIanaId, devSn, log, Level.INFO, "Received OFFLINE from "+devLog);
					
					updateOnlineStatusAndHistoryEventLog(devOrgId, devId, devO.getStatus(), ONLINE_STATUS.OFFLINE, eventUTC, UPDATE_REASON.onofflineevent);			
					break;
				default:
					break;
			}
		} catch (Exception e)
		{
			log.error("OnlineStatusAndCustomEventHandler error in startSession", e);
			return false;
		} finally {
			
//			if (isOnlineEventCounted)
//				currentOnlineEventCounter.decrementAndGet();
			
			try {
				if (isOwnSession)
				{
					if (dbUtil!=null && dbUtil.isSessionStarted()) {
						dbUtil.endSession();
					}
				}
			} catch (Exception e2) {
				log.error("OnlineStatusAndCustomEventHandler - error in endSession", e2);
			}
		}
		
		return result;
	}
	
	public static boolean updateOnlineStatusAndHistoryEventLog(String orgId, Integer devId, ONLINE_STATUS fromStatus, ONLINE_STATUS toStatus, Date eventUTC, UPDATE_REASON reason) {
		return updateOnlineStatusAndHistoryEventLog(null, orgId, devId, fromStatus, toStatus, eventUTC, reason);
	}
	
	private static void checkConfigUpdate(DevOnlineObject devO, DevInfoObject devInfo, String orgId, Devices dev) throws SQLException
	{
		String logMsg = null;
		NetworksDAO netDAO = null;
		Networks net = null;
		
		/*** trigger config update for first time online or checksum not match ***/
		log.debugf("LT10001 net %d dev %d %s dev.getConfigChecksum %s devO.getConf_checksum %s", 
				dev.getNetworkId(), dev.getIanaId(), dev.getSn(), 
				dev.getConfigChecksum(), devO.getConf_checksum());
		if (dev.getFirstAppear() == null || dev.getConfigChecksum()==null ||
				(devO.getConf_checksum()!=null && !dev.getConfigChecksum().equalsIgnoreCase(devO.getConf_checksum()))) {
			
			netDAO = new NetworksDAO(orgId);
			net = netDAO.findById(dev.getNetworkId());
									
			if (net!=null && net.getMasterDeviceId() != null && net.getMasterDeviceId()!= 0)
			{
				logMsg = String.format("LT10001 net %d dev %d %s is running Group master mode", dev.getNetworkId(), dev.getIanaId(), dev.getSn());
				log.debugf(logMsg);
				devInfo.addDebugInfo(logMsg);
			}
			else
			{
				/* not master mode, mark config update */
				logMsg = String.format("LT10001 net %d dev %d %s trigger apply config ", dev.getNetworkId(), dev.getIanaId(), dev.getSn());
				log.debugf(logMsg);
				devInfo.addDebugInfo(logMsg);
//				ConfigUpdatePerNetworkTask.performConfigUpdateDevice(JsonUtils.genServerRef(), 
//						orgId, devO.getNetwork_id(), devO.getDevice_id(), "dev online checksum change");
				new ConfigUpdatePerDeviceTask(orgId, dev.getNetworkId()).performConfigUpdateDeviceDelay(dev.getId(), JsonUtils.genServerRef(), CONFIG_UPDATE_REASON.dev_online.toString());
				//PERFORMANCE
				
			}						
		}
		else
		{
			logMsg = String.format("LT10001 net %d dev %d %s checksum match", dev.getNetworkId(), dev.getIanaId(), dev.getSn());
			log.debugf(logMsg);
			devInfo.addDebugInfo(logMsg);
		}
	}
	
	/* 
	 * Handle cache online_status and db online_status, first_appear, last_online, offline_at.
	 * If newDevOnlineObject is empty, update existing one.
	 * 
	 * */ 
	public static boolean updateOnlineStatusAndHistoryEventLog(DevOnlineObject newDevOnlineObject, String orgId, Integer devId, 
			ONLINE_STATUS fromStatus, ONLINE_STATUS toStatus, Date eventUTC, UPDATE_REASON reason) {
		
		log.infof("LT10001 updateOnlineStatusAndHistoryEventLog DevOnlineObject %s orgId %s devId %d fromStatus %s toStatus %s date %s reason %s", 
				newDevOnlineObject, orgId, devId, fromStatus, toStatus, eventUTC, reason);
		
		boolean result = true;
		
		DBUtil dbUtil = null;
		DBConnection batchConn = null;
		
		boolean isOwnSession = false;
		DevicesDAO devDAO = null;
		//NetworksDAO netDAO = null;
		//DeviceMessagesDAO devMsgDAO = null;		
		DeviceOnlineHistoryDAO devOnHistDAO = null;		
		
		DeviceOnlineHistories lastHistory = null;
		//DeviceMessages devMsg = null;
		DevOnlineObject devO = null;
		//Networks net = null;
		Devices dev = null;
		DevInfoObject devInfo = null;
		String logMsg = null;
		
		boolean isOnlineHistoryRequireUpdate = true;
		
		boolean isOnlineObjectUpdated = false;		
		boolean isOnlineHistoryUpdated = false;
		boolean isEventLogUpdated = false;
		boolean isPurgeCacheToDb = false;
		boolean isUpdateNetInfo = false;
		
		if (orgId == null || devId == null)
		{
			log.errorf("LT10001 updateOnlineStatusAndHistoryEventLog null param! orgId %s devId %d", orgId, devId);
			return false;
		}
		
		try {
			dbUtil = DBUtil.getInstance();			
			if (dbUtil == null)
			{
				log.errorf("Fail to DBUtil.getInstance() dev %d in org %s", devId, orgId);
				return false;
			}
			
			if (!dbUtil.isSessionStarted())
			{
				dbUtil.startSession();
				isOwnSession = true;
			}
			
			batchConn = dbUtil.getConnection(false, orgId, false);
			batchConn.setBatchMode();
			
			devDAO = new DevicesDAO(orgId);
			//devMsgDAO = new DeviceMessagesDAO(orgId);
			devOnHistDAO = new DeviceOnlineHistoryDAO(orgId);
			
			
//			devMsg = devMsgDAO.findById(devId);
//			if (devMsg==null)
//			{
//				devMsg = new DeviceMessages();
//				devMsg.setDevice_id(devId);
//			}
			
			dev = devDAO.findById(devId);	
			if (dev==null)
			{
				log.errorf("LT10001 cannot find dev %d in org %s", devId, orgId);
				return false;
			}
			
			devO = DeviceUtils.getDevOnlineObject(dev);
			if (devO == null)
			{
				log.warnf("LT10001 fail to get online object for dev %d in org %s", devId, orgId);
				return false;
			}
						
			devInfo = DeviceUtils.createDevInfoObjectIfNotExists(dev.getIanaId(), dev.getSn());
			
			isUpdateNetInfo = true;
			/* update DevOnlineObject status and online history */
			switch (toStatus)
			{
			case ONLINE:				
				/* if offline->offline or online->online, need to verify if online history is matched. */
				if (dev.getFirstAppear() == null) {
					dev.setFirstAppear(eventUTC);
					dev.setLastOnline(eventUTC);
				}
				
				if (fromStatus == toStatus) 
				{					
					if (!devO.getMachine_id().equalsIgnoreCase(ACService.getServerName()))
					{
						devInfo.setTs(DateUtils.getUtcUnixtime());
						logMsg = String.format("LT10001A - dev %d %s has migrated from %s(%d) to this server %s(%d)", devO.getIana_id(), devO.getSn(), devO.getMachine_id(), devO.getLastAcTime(), ACService.getServerName(), devInfo.getTs());
												
						devO.setMachine_id(ACService.getServerName());
						devO.setLastAcTime(DateUtils.getUtcUnixtime());
						isOnlineObjectUpdated = DeviceUtils.putOnlineObject(devO);
						
						log.warnf("%s", logMsg);
						devInfo.addDebugInfo(logMsg);
//						log.infof("LT10001 - DISCONNECT DUPLICATE_SN from AC for duplicate ONLINE origin:%s new:%s", devO.getMachine_id(), ACService.getServerName());
//						ACService.disconnect_duplicate_sn_from_ac(JsonUtils.genServerRef(), devO.getIana_id(), devO.getSn(), devO.getMachine_id());
					}
					
					log.infof("LT10001 fromStatus == toStatus dev %d %s", dev.getIanaId(), dev.getSn());
					lastHistory = devOnHistDAO.getLastOfflineHistoryByTime(devId);
					
					/* check if last history is online */
					if (!isValidLastOnlineHistory(lastHistory))
					{						
						log.warnf("LT10001 ONLINE - dev %d %s with last online history out sync with current status %s "
								+ "=> continue to update event log and history", dev.getIanaId(), dev.getSn(), fromStatus);
						
						logMsg = String.format("LT10001 ONLINE - dev %d %s with last online history out sync with current status %s "
								+ "=> continue to update event log and history", dev.getIanaId(), dev.getSn(), fromStatus);
						
						devInfo.addDebugInfo(logMsg);
					}
					else
					{
						log.infof("LT10001 same status, still need to update online object such as fw_ver for firmware_upgrade fast reboot, "
								+ "update firstappear, but no status and event log update is required (just last_ac_update) dev %d %s ",
								dev.getIanaId(), dev.getSn());
						
						if (newDevOnlineObject != null)
						{
							newDevOnlineObject.retainPropertiesFromExistingOnlineObject(devO);
							devO = newDevOnlineObject;
							devO.setLastUpdateTime(eventUTC);
							isOnlineObjectUpdated = DeviceUtils.putOnlineObject(devO);
						}
						
//						if (dev.getFirstAppear() == null) {
//							dev.setFirstAppear(eventUTC);
//							
//							logMsg = String.format("LT10001 Invalid record! Since last status is ONLINE, "
//									+ "first appear should not be null! But proceed to update first appear dev %d %s", dev.getIanaId(), dev.getSn());
//							log.warnf(logMsg);
//							devInfo.addDebugInfo(logMsg);
//						}
						
						try {
							checkConfigUpdate(devO, devInfo, orgId, dev);
						} catch (Exception e)
						{
							log.error("Exception checkConfigUpdate(2)", e);						
						}
						
						addBatchUpdateToFwConfForDevice(batchConn, dev, eventUTC, newDevOnlineObject);
						break;
					}					
				}
				
				if (WtpMsgHandler.isDeviceWarrantValid(dev))
				{
					log.infof("LT10001 valid warranty dev %d %s", dev.getIanaId(), dev.getSn());
					if (newDevOnlineObject != null)
					{
						newDevOnlineObject.retainPropertiesFromExistingOnlineObject(devO);
						devO = newDevOnlineObject;						
					}	
					DeviceUtils.setDevOnlineObjectOnlineOrOfflineFromStatus(devO, toStatus);
					devO.setLastUpdateTime(eventUTC);
					isOnlineObjectUpdated = DeviceUtils.putOnlineObject(devO);
					
					try {
						checkConfigUpdate(devO, devInfo, orgId, dev);
					} catch (Exception e)
					{
						log.error("Exception checkConfigUpdate(2)", e);						
					}
					
					/* GOTO LABEL_#1 */
				}
				else if (devO.getStatus()!=ONLINE_STATUS.WARRANTY_EXPIRED)
				{
					log.infof("LT10001 warranty expired dev %d %s", dev.getIanaId(), dev.getSn());
					
					/* handle warranty expired - mark expire and set offline */
					if (newDevOnlineObject != null)
					{
						newDevOnlineObject.retainPropertiesFromExistingOnlineObject(devO);
						devO = newDevOnlineObject;						
					}					
					DeviceUtils.setDevOnlineObjectOnlineOrOfflineFromStatus(devO, ONLINE_STATUS.WARRANTY_EXPIRED);			
					devO.setLastUpdateTime(eventUTC);
					isOnlineObjectUpdated = DeviceUtils.putOnlineObject(devO); 
					if (isOnlineObjectUpdated)
					{
						/* this is to update status to offline instead of online! */						
						log.debugf("LT10001 dev %d in org %s is updating offline status, offlineAt.", devId, orgId);
						dev.setOnline_status(ONLINE_STATUS.WARRANTY_EXPIRED.getDbValue());
						dev.setOfflineAt(eventUTC);
						addBatchUpdateToFwConfForDevice(batchConn, dev, eventUTC, newDevOnlineObject);
						
						isOnlineHistoryUpdated = addBatchUpdateOnlineHistory(ONLINE_STATUS.WARRANTY_EXPIRED, orgId, devId, dev, eventUTC, batchConn);
						
						/* only update event log when online history is updated for marking warranty expired */
						if (isOnlineHistoryUpdated)
							isEventLogUpdated = addBatchUpdateEventLog(ONLINE_STATUS.WARRANTY_EXPIRED, orgId, dev, eventUTC, batchConn);
					}
					break;
				}
				else
				{
					log.infof("LT10001 - device %d %s has already expired. ignore DEV_ONLINE.", dev.getIanaId(), dev.getSn());
				}
				
				/* LABEL_#1 */
				if (isOnlineObjectUpdated)
				{
					log.infof("LT10001 dev %d in org %s is updating online status, lastonline and firstappear.", devId, orgId);
					dev.setOnline_status(ONLINE_STATUS.ONLINE.getDbValue());
					dev.setLastOnline(eventUTC);
//					if (dev.getFirstAppear() == null) {
//						dev.setFirstAppear(eventUTC);
//					}					
					addBatchUpdateToFwConfForDevice(batchConn, dev, eventUTC, newDevOnlineObject);
					
					isOnlineHistoryUpdated = addBatchUpdateOnlineHistory(toStatus, orgId, devId, dev, eventUTC, batchConn);
					
					/* only update event log when online history is updated or first online */
					if (isOnlineHistoryUpdated || fromStatus==ONLINE_STATUS.NEVER_ONLINE)
						isEventLogUpdated = addBatchUpdateEventLog(toStatus, orgId, dev, eventUTC, batchConn);
					
					if (!isOnlineHistoryUpdated || !isEventLogUpdated)
					{						
						logMsg = String.format("LT10001 dev %d %s operation failed on (isOnlineHistoryUpdated %s, isEventLogUpdated %s)", 
								dev.getIanaId(), dev.getSn(), isOnlineHistoryUpdated, isEventLogUpdated);
						log.warnf(logMsg);
						devInfo.addDebugInfo(logMsg);
					}
				}
				else
				{
					logMsg = String.format("LT10001 status %s is not updated dev %d %s", toStatus, dev.getIanaId(), dev.getSn());
					log.warnf(logMsg);
					result = false;
										
					devInfo.addDebugInfo(logMsg);
				}
				break;				
				
			case OFFLINE:
			
				/* if offline->offline or online->online, need to verify if online history is matched. */
				
				if (fromStatus == toStatus) 
				{
					lastHistory = devOnHistDAO.getLastOfflineHistoryByTime(devId);
					/* check if last history is offline */
					if (!isValidLastOfflineHistory(lastHistory))
					{
						logMsg = String.format("LT10001 OFFLINE - dev %d %s with last online history out sync with current status %s => update event log and history", dev.getIanaId(), dev.getSn(), fromStatus);
						log.warnf(logMsg);						
						devInfo.addDebugInfo(logMsg);
					}
					else
					{
						log.debugf("LT10001 same status, no update is required (just last_ac_update)");
						devO.setLastUpdateTime(eventUTC);
						isOnlineObjectUpdated = DeviceUtils.putOnlineObject(devO);
						// dev.update();
						// batchConn.addBatch(dev);
						break;
					}
				}
				
				if (fromStatus == ONLINE_STATUS.WARRANTY_EXPIRED || fromStatus == ONLINE_STATUS.NEVER_ONLINE)
				{
					log.debugf("LT10001 WARRANTY_EXPIRED and NEVER_ONLINE are considered same status as OFFLINE, no update is required.");
					break;
				}
				
				DeviceUtils.setDevOnlineObjectOnlineOrOfflineFromStatus(devO, toStatus);	
				devO.setLastUpdateTime(eventUTC);
				isOnlineObjectUpdated = DeviceUtils.putOnlineObject(devO); 
				if (isOnlineObjectUpdated)
				{
					log.debugf("LT10001 dev %d in org %s is updating offline status, offlineAt.", devId, orgId);
					dev.setOnline_status(toStatus.getDbValue());
					dev.setOfflineAt(eventUTC);
					//devDAO.update(dev);
					dev.update();					
					//TODO:!!add NetUtils.devLst cache update!!!!
					batchConn.addBatch(true,dev);					
					
					isOnlineHistoryUpdated = addBatchUpdateOnlineHistory(toStatus, orgId, devId, dev, eventUTC, batchConn);
					/* only update event log when online history is updated for offline */
					if (isOnlineHistoryUpdated)
						isEventLogUpdated = addBatchUpdateEventLog(toStatus, orgId, dev, eventUTC, batchConn);
					
					isPurgeCacheToDb = OnlineStatusAndCustomEventHandler.purgeCacheToDbUponOffline(devO);
					
					if (!isOnlineHistoryUpdated || !isEventLogUpdated || !isPurgeCacheToDb)
					{
						logMsg = String.format("LT10001 dev %d %s operation failed on (isOnlineHistoryUpdated %s, isEventLogUpdated %s, isPurgeCacheToDb %s)", 
								dev.getIanaId(), dev.getSn(), isOnlineHistoryUpdated, isEventLogUpdated, isPurgeCacheToDb);
						log.warnf(logMsg);
						
						devInfo.addDebugInfo(logMsg);
					}
				}
				else
				{
					logMsg = String.format("LT10001 status %s is not updated dev %d %s", toStatus, dev.getIanaId(), dev.getSn());
					log.warnf(logMsg);
					result = false;					
					
					devInfo.addDebugInfo(logMsg);
				}				
				break;
				
			default:
				logMsg = String.format("LT10001 unhandled state %s!", toStatus);
				log.warnf(logMsg);
				result = false;
				isUpdateNetInfo = false;

				devInfo.addDebugInfo(logMsg);				
				break;
			}
			
//			if (isOnlineObjectUpdated)
//			{
//				log.infof("LT10001 update latest online object to db dev %d %s", dev.getIanaId(), dev.getSn());
//				devMsg.setOnline_msg(JsonUtils.toJson(devO));
//				devMsg.saveOrUpdate();
//				batchConn.addBatch(true,devMsg);
//			}			
			
		} catch (Exception e)
		{
			log.error("LT10001 Exception handleOnlineStatus orgId "+orgId+" devId "+devId+" fromStatus "+fromStatus+" toStatus "+toStatus+" eventUTC "+eventUTC, e);
			return result;
		} finally {
			try {
		
				if (batchConn!=null)
					batchConn.commit();
				
				if(isUpdateNetInfo) {
					/* Update NetUtils devices cache since batch update didnt update cache */
					log.infof("LT10001 updating NetUtils.devLst for org %s dev %d", orgId, devId);
					if (!NetUtils.saveOrUpdateDevices(orgId, dev.getNetworkId(), dev))
					{
						logMsg = String.format("LT10001 dev org %s id %d fail to update NetUtils.devLst!", orgId, devId);
						log.warnf(logMsg);	
						if (devInfo!=null)
							devInfo.addDebugInfo(logMsg);
					}									
				}
			} catch (Exception e) {
				log.error("LT10001 commit batch connection error and rollback online object status orgId "+orgId+" devId "+devId, e);
				
				if (isOnlineObjectUpdated)
				{
					/* rollback on exception if updated */
					DeviceUtils.setDevOnlineObjectOnlineOrOfflineFromStatus(devO, fromStatus);
					try {
						DeviceUtils.putOnlineObject(devO);
					} catch (Exception e1)
					{
						logMsg = String.format("LT10001 error in rollback online object status for dev %d %s from %s to %s", devO.getIana_id(), devO.getSn(), fromStatus, toStatus);
						log.errorf(logMsg);
						if (devInfo!=null)
							devInfo.addDebugInfo(logMsg);;
					}
				}
				
				if (batchConn!=null && batchConn.getStatementList()!=null && batchConn.getStatementList().size()!=0)
				{
					StringBuilder sb = new StringBuilder();
					for (String stmt : batchConn.getStatementList()) {
						sb.append("stmt: " + stmt + "\n");
					}
					logMsg = String.format("LT10001 commit batch error statement %s", sb);
					log.errorf(logMsg);
				}
				result = false;
				if (devInfo!=null)
					devInfo.addDebugInfo(logMsg);
			} finally {	
				try {
					if (isOwnSession)
					{
						if (dbUtil!=null && dbUtil.isSessionStarted()) {
							dbUtil.endSession();
						}
					}					
				} catch (Exception e2) {
					logMsg = String.format("LT10001 OnlineStatusAndCustomEventHandler - error in endSession", e2);
					log.error(logMsg);
					result = false;
					if (devInfo!=null)
						devInfo.addDebugInfo(logMsg);
				} finally {
					if (devInfo!=null)
						DeviceUtils.putDevInfoObject(devInfo);
				}
			}
		}
	
		return result;
	}
	
	private static boolean addBatchUpdateOnlineHistory(ONLINE_STATUS newStatus, String orgId, Integer devId, Devices dev, Date eventUTC, DBConnection batchConn)
	{
		log.debugf("LT10001 addBatchUpdateOnlineHistory is called for newStatus %s, orgId %s, devId %d, eventUTC %s", 
				newStatus, orgId, devId, eventUTC);
		
		boolean isUpdate = false;
		boolean isInvalidEventLog = false;
		
		String logMsg = null;
		
		if (!isValidEventUtc(eventUTC, "orgId "+orgId+" devId "+devId))
		{
			isInvalidEventLog = true;
			logMsg = String.format("LT10001 orgId %s devId %s has provided an event with incorrect eventUTC %s, thus use server time to log instead!",
					orgId, devId, eventUTC);
			DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.WARN, logMsg);
			eventUTC = DateUtils.getUtcDate();
		}			
		
		try {
			DeviceOnlineHistoryDAO devOnHistDAO = new DeviceOnlineHistoryDAO(orgId);
			DeviceOnlineHistories lastHistory = null;
			
			switch(newStatus)
			{
			case ONLINE:
				lastHistory = devOnHistDAO.getLastOfflineHistoryByTime(devId);
				if (lastHistory != null) {
					if (isLastHistoryOffline(lastHistory)) {
						// first online after goes offline
						logMsg = String.format("LT10001 DeviceOnlineHistories - org %s dev %d online after goes offline", orgId, devId);
						DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.INFO, logMsg);
						lastHistory.setOnline_time((int) (eventUTC.getTime() / 1000));
						//devOnHistDAO.update(lastHistory);
						lastHistory.update();
						batchConn.addBatch(true,lastHistory);
						isUpdate = true;
					}
				}
				else
				{
					log.infof("LT10001 DeviceOnlineHistories - org %s dev %d first online", orgId, devId);
					isUpdate = false;
				}
				break;			
			case OFFLINE:
			case WARRANTY_EXPIRED:
				lastHistory = devOnHistDAO.getLastOfflineHistoryByTime(devId);					
				if (lastHistory != null){
					if (isLastHistoryOnline(lastHistory)){
						// first offline after goes online
						logMsg = String.format("LT10001 DeviceOnlineHistories - org %s dev %d first offline after goes online", orgId, devId);
						DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.INFO, logMsg);
						lastHistory = new DeviceOnlineHistories();
						lastHistory.setOffline_time((int)(eventUTC.getTime()/1000));
						lastHistory.setDevice_id(devId);
						//devOnHistDAO.save(lastHistory);
						lastHistory.saveOrUpdate();
						batchConn.addBatch(true,lastHistory);
						isUpdate = true;
					}
					else
					{
						logMsg = String.format("LT10001 DeviceOnlineHistories - receive DEV_OFFLINE with existing last history not online for org %s dev %d ", orgId, devId);
						DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.WARN, logMsg);
					}
				}else {
					// never offline before
					logMsg = String.format("LT10001 DeviceOnlineHistories - org %s dev %d never offline before", orgId, devId);
					DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.DEBUG, logMsg);
					lastHistory = new DeviceOnlineHistories();
					lastHistory.setOffline_time((int)(eventUTC.getTime()/1000));
					lastHistory.setDevice_id(devId);
					//devOnHistDAO.save(lastHistory);
					lastHistory.saveOrUpdate();
					batchConn.addBatch(true,lastHistory);
					isUpdate = true;
				}
				break;
				
			default:
				logMsg = String.format("LT10001 status %s is trying to update online history for org %s dev %d ", newStatus, orgId, devId);
				DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.WARN, logMsg);
				break;
			}
		} catch (Exception e)
		{
			logMsg = String.format("LT10001 Exception updateOnlineHistory status "+newStatus+" orgId "+orgId+" devId "+devId+" eventUTC "+eventUTC,e);
			DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.ERROR, logMsg);
			return isUpdate;
		}
		
		return isUpdate;
	}
	
	private static boolean isLastHistoryOffline(DeviceOnlineHistories devOnHist)
	{
		if (devOnHist==null)
			return false;
		
		if (devOnHist.getOffline_time() != null && devOnHist.getOnline_time() == null)
			return true;
		else
			return false;
	}
	
	private static boolean isLastHistoryOnline(DeviceOnlineHistories devOnHist)
	{
		if (devOnHist==null)
			return false;
		
		if (devOnHist.getOffline_time() != null && devOnHist.getOnline_time() != null)
			return true;
		else
			return false;
	}
	
	private static boolean isValidEventUtc(Date eventUTC, String logDevInfo)
	{
		/* validate eventUTC */
		if (Math.abs(DateUtils.getUtcDate().getTime() - eventUTC.getTime()) > EVENT_EXPIRED_TIME_IN_SEC * 1000)
		{
			log.warnf("Invalid event log time for dev %s curUTC %d - eventUTC %d = %d, EVENT_EXPIRED_TIME_IN_SEC = %d", logDevInfo, 
					DateUtils.getUtcDate().getTime(), eventUTC.getTime(), DateUtils.getUtcDate().getTime() - eventUTC.getTime(), EVENT_EXPIRED_TIME_IN_SEC);
			return false;
		}
		
		return true;
	}
	
	/* add update dev table to batch */
	public static void addBatchUpdateToFwConfForDevice(DBConnection batchConn, Devices dev, Date eventUTC, DevOnlineObject newDevOnlineObject)
	{
		// dev.setLast_ac_updatetime(eventUTC);
		
		if (newDevOnlineObject.getFw_ver() != null && !newDevOnlineObject.getFw_ver().isEmpty()) {
			dev.setFw_ver(newDevOnlineObject.getFw_ver());
		}

		if (dev.getConfigChecksum() == null || dev.getConfigChecksum().isEmpty()) {
			/* update db with the checksum if not exists */
			if (newDevOnlineObject.getConf_checksum() != null) {
				dev.setConfigChecksum(newDevOnlineObject.getConf_checksum());
			}
		}
		
		dev.update();
		batchConn.addBatch(true,dev);
	}
	
	/* handle event log */ 
	private static boolean addBatchUpdateEventLog(ONLINE_STATUS status, String orgId, Devices dev, Date eventUTC, DBConnection batchConn) {
		
		log.debugf("addBatchUpdateEventLog is called for status %s, orgId %s, dev %s, eventUTC %s", 
				status, orgId, dev, eventUTC);
		
		boolean isUpdatedEventLog = false;
		boolean isInvalidEventLog = false;
		
		StringBuilder extraEventLogInfo = new StringBuilder();
		
		EventLog eventLog = null;
		String logMsg = null;
		
		if (status == null || orgId == null || dev == null)
		{
			logMsg = String.format("updateEventLog null param! status %s orgId %s dev %s", status, orgId, dev);
			DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.ERROR, logMsg);
			return false;
		}
		
		try {
			EventLogDAO eventLogDAO = new EventLogDAO(orgId);
			NetworksDAO networkDAO = new NetworksDAO(orgId);			
			Networks network = networkDAO.findById(dev.getNetworkId());
			if (network == null)
			{
				logMsg=String.format("Cannot find network for orgId %s dev %s", orgId, dev);
				DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.ERROR, logMsg);
				return false;
			}
			
			if (!isValidEventUtc(eventUTC, dev.getIanaId()+" "+dev.getSn()))
			{
				extraEventLogInfo.append(" [*]");
				isInvalidEventLog = true;
				eventUTC = DateUtils.getUtcDate();
			}
			
			Date eventNetworkTime = DateUtils.offsetFromUtcTimeZoneId(eventUTC, network.getTimezone());			
			switch(status)
			{
			case ONLINE:							
				eventLog = new EventLog(dev.getNetworkId(), eventNetworkTime, (int) (eventUTC.getTime() / 1000), 
						dev.getId(), "", "", "", EventLog.EVENT_TYPE_SYSTEM, "Device is online"+extraEventLogInfo.toString());
				//eventLogDAO.save(eventLog);
				eventLog.saveOrUpdate();
				batchConn.addBatch(true,eventLog);
				isUpdatedEventLog = true;
				break;
			case OFFLINE:
			case WARRANTY_EXPIRED:
				eventLog = new EventLog(dev.getNetworkId(), eventNetworkTime, (int) (eventUTC.getTime() / 1000), 
						dev.getId(), "", "", "", EventLog.EVENT_TYPE_SYSTEM, "Device is offline"+extraEventLogInfo.toString());
				//eventLogDAO.save(eventLog);
				eventLog.saveOrUpdate();
				batchConn.addBatch(true,eventLog);
				isUpdatedEventLog = true;
				break;
			default:
				break;
			}
			logMsg=String.format("LT10001 updateEventLog status %s for orgId %s dev %d %s date %s",  status, orgId, dev.getIanaId(), dev.getSn(), eventUTC);
			DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.INFO, logMsg);
		} catch (Exception e)
		{
			log.error("Exception updateEventLog status "+status+" orgId "+orgId+" dev "+dev+" eventUTC "+eventUTC,e);
			DeviceUtils.logDevInfoObject(dev.getIanaId(), dev.getSn(), log, Level.ERROR, 
					"Exception updateEventLog status "+status+" orgId "+orgId+" dev "+dev+" eventUTC "+eventUTC);
			return isUpdatedEventLog;
		}
		
		return isUpdatedEventLog;
	}
	
//	private static boolean handleAlert(ONLINE_STATUS status) {
//		/* check if alert is enabled */		
//		
//		/* check status of previous sent alert - online/offline */
//		
//		/* send if necessary and using offline_at time */
//		
//	}
	
	
	/* Rebuild DevOnlineObject with online status from database 
	 * public for unit test only */
	public static DevOnlineObject rebuildLastDevOnlineObjectIfWarrantyValid(MessageType mt, QueryInfo<Object> optional_OnlineInfo, String devOrgId, Integer devIanaId, String devSn)
	{
		log.debugf("rebuildLastDevOnlineObjectIfWarrantyValid is called with mt %s optional_OnlineInfo %s devOrgId %s devIanaId %d devSn %s", 
				mt, optional_OnlineInfo, devOrgId, devIanaId, devSn);
		
		currentRebuildCounter.incrementAndGet();		
		//log.warnf("DEBUG rebuildLastDevOnlineObjectIfWarrantyValid currentRebuildCounter=%d", currentRebuildCounter.intValue());
		
		if (currentRebuildCounter.intValue()>MAX_CURRENT_REBUILD_COUNTER)
		{
			log.warnf("MAX_CURRENT_REBUILD_COUNTER %d is reached, skip dev %d %s! (current=%d)", MAX_CURRENT_REBUILD_COUNTER, devIanaId, devSn, currentRebuildCounter.intValue());
			currentRebuildCounter.decrementAndGet();
			return null;
		}
		
		ONLINE_STATUS lastStatus = null;
		
		DevicesDAO devDAO = null;
		//DeviceMessagesDAO devMsgDAO = null;		
		Devices dev = null;
		//DeviceMessages devMsg = null;
		
		DevOnlineObject devORebuild = null;
		DevOnlineObject devONew = null;
		
		try {		
			devDAO = new DevicesDAO(devOrgId);
			//devMsgDAO = new DeviceMessagesDAO(devOrgId);
			dev = devDAO.findBySn(devIanaId, devSn);
			if (dev==null)
			{
				log.warnf("LT10001 - device %d %s is not registered to IC2", devIanaId, devSn);
				return null;
			}
				
			//devMsg = devMsgDAO.findById(dev.getId());	
									
			switch (mt)
			{
			case PIPE_INFO_TYPE_DEV_ONLINE:
				log.infof("LT10001 - use DEV_ONLINE and dev table to rebuild info for dev %d %s (complete info)", dev.getIanaId(), dev.getSn());				
				devONew = DeviceUtils.getDevOnlineObjectFromQueryInfo(optional_OnlineInfo);
				if (devONew == null) {
					log.warnf("LT10001 - Invalid DEV_ONLINE data message %s in ", optional_OnlineInfo);
					return null;
				}
				devORebuild = devONew;
				devORebuild.rebuildInfo(devOrgId, dev);
				//lastStatus = devORebuild.getStatus();
				break;
			default:				
//				if (devMsg != null)
//				{
//					log.infof("LT10001 - use last online msg to rebuild info for dev %d %s devMsg %s", dev.getIanaId(), dev.getSn(), devMsg);
//					devORebuild = JsonUtils.<DevOnlineObject>fromJson(devMsg.getOnline_msg(), DevOnlineObject.class);
//					if (devORebuild == null)
//					{				
//						log.infof("LT10001 - if backup restore failure, restore from dev %d %s", dev.getIanaId(), dev.getSn());
//						devORebuild = new DevOnlineObject();
//						devORebuild.rebuildInfo(devOrgId, dev);
//						//lastStatus = devORebuild.getStatus();
//					}
//					else
//					{
//						log.infof("LT10001 - use last online msg - devORebuild = %s for dev %d %s", devORebuild, dev.getIanaId(), dev.getSn());
//						//lastStatus = devORebuild.getStatus();
//					}
//					/* ensure update status is in cache */
//					ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_ONLINE, JsonUtils.genServerRef(), dev.getIanaId(), dev.getSn());
//				}
//				else
//				{
//					/* Act as a temporary resort to reduce server loading for initial launch (no devMsg in db) of this feature */
//					log.infof("use device table to rebuild info for dev %d %s (incomplete info)", dev.getIanaId(), dev.getSn());
//					devORebuild = new DevOnlineObject();
//					devORebuild.rebuildInfo(devOrgId, dev);
//					lastStatus = DeviceUtils.getDevOnlineStatus(dev);
					
					/* if no backup, fetch new */
//					log.infof("LT10001 - if no backup, fetch new - devMsg = %s for dev %d %s", devMsg, dev.getIanaId(), dev.getSn());
//					ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_ONLINE, JsonUtils.genServerRef(), dev.getIanaId(), dev.getSn());
					
				devORebuild = new DevOnlineObject();
				devORebuild.rebuildInfo(devOrgId, dev);
					//lastStatus = devORebuild.getStatus();
//				}
				break;
			}
			
			lastStatus = ONLINE_STATUS.UNKNOWN;
			//log.debugf("LT10001 - lastStatus is unknown for all rebuild task = %s for dev %d %s", lastStatus, dev.getIanaId(), dev.getSn());
			
			/* we made an important assumption that database is correct since health check should stop alert sending when db is unhealthy */
			if (lastStatus == ONLINE_STATUS.UNKNOWN)
			{
				if (!WtpMsgHandler.isDeviceWarrantValid(dev))
				{
					lastStatus = ONLINE_STATUS.WARRANTY_EXPIRED;
				}
				else if (dev.getFirstAppear()==null)
				{
					lastStatus = ONLINE_STATUS.NEVER_ONLINE;
				}
				else if (dev.getOfflineAt() != null && dev.getLastOnline() != null)
				{ 				
					if (dev.getOfflineAt().getTime() > dev.getLastOnline().getTime())
					{
						lastStatus = ONLINE_STATUS.OFFLINE;
					}
					else if (dev.getOfflineAt().getTime() < dev.getLastOnline().getTime())
					{
						lastStatus = ONLINE_STATUS.ONLINE;
					}
					else if (dev.getOfflineAt().getTime() == dev.getLastOnline().getTime())
					{
						log.warnf("LT10001 - online time = offline time for dev %d %s", dev.getIanaId(), dev.getSn());
					}
				}
				else if (dev.getOfflineAt() == null && dev.getLastOnline() != null)
				{
					lastStatus = ONLINE_STATUS.ONLINE;
				}
//				else if (dev.getLast_ac_updatetime()!=null && dev.getLast_ac_updatetime().getTime()<NetworkUtils.getNetworkIdleThreshold(net))
//				{
//					/* greater than threshold does not mean offline since the server may stop for more than threshold period */
//				}				
				
				if (lastStatus == ONLINE_STATUS.UNKNOWN)
				{
					log.warnf("LT10001 - !!Undetermined Status for dev %d %s", dev.getIanaId(), dev.getSn());
				}
			}		

			switch (lastStatus)
			{
			case ONLINE:
				log.debugf("LT10001 - last is online and status %s", lastStatus);
				devORebuild.setOnline(true);
				devORebuild.setStatus(lastStatus);
				DeviceUtils.putOnlineObject(devORebuild);
				break;
			case OFFLINE:
			case WARRANTY_EXPIRED:
				log.debugf("LT10001 - last is offline and status %s", lastStatus);
				devORebuild.setOnline(false);
				devORebuild.setStatus(lastStatus);
				DeviceUtils.putOnlineObject(devORebuild);
				break;
			default:
				log.debugf("LT10001 - last is others %s", lastStatus);
				devORebuild.setOnline(false);
				devORebuild.setStatus(lastStatus);
				DeviceUtils.putOnlineObject(devORebuild);
				break;
			}
			
		} catch (Exception e) {
			log.error("LT10001 - Exception rebuildLastDevOnlineObjectIfWarrantyValid mt "+mt+" info "+optional_OnlineInfo+" devOrgId "+devOrgId+" devIanaId "+devIanaId+" devSn "+devSn, e);
		} finally {
			currentRebuildCounter.decrementAndGet();
		}
	
		return devORebuild;
	}
	
	private static boolean isValidLastOnlineHistory(DeviceOnlineHistories lastHistory) 
	{
		/* check last is online history */
		if (lastHistory == null)
			return true;
			
		if (lastHistory.getOnline_time()!=null && lastHistory.getOffline_time()!=null)
			return true;			

		return false;
	}
	
	private static boolean isValidLastOfflineHistory(DeviceOnlineHistories lastHistory) 
	{
		/* check last is online history */
		if (lastHistory == null)
			return false;
			
		if (lastHistory.getOnline_time()==null && lastHistory.getOffline_time()!=null)
			return true;

		return false;
	}
	
	public static boolean purgeCacheToDbUponOffline(DevOnlineObject devO) 
	{
		if (devO==null)
		{
			log.warn("devO param is null");
			return false;
		}
		
		final int retryTime = 3;
		int retryCount = 1;

		while(retryCount<=retryTime && !purgeCacheToDb(devO))
		{
			retryCount++;
		}
		
		return true;
	}
	
	private static boolean purgeCacheToDb(DevOnlineObject devO)
	{
		log.debugf("purgeCacheToDb for dev %d %s", devO.getIana_id(), devO.getSn());
		
		Integer iana_id = devO.getIana_id();
		String sn = devO.getSn();
		
		try {
			// remove corresponding object from cache to avoid static data after device offline
			DevBandwidthObject devBandwitdthObject = new DevBandwidthObject();
			devBandwitdthObject.setIana_id(iana_id);
			devBandwitdthObject.setSn(sn);
			ACUtil.<DevBandwidthObject> removePoolObjectBySn(devBandwitdthObject, DevBandwidthObject.class);
			DevDetailObject devDetailObject = new DevDetailObject();
			devDetailObject.setIana_id(iana_id);
			devDetailObject.setSn(sn);
			ACUtil.<DevDetailObject> removePoolObjectBySn(devDetailObject, DevDetailObject.class);
			StationBandwidthListObject stationBandwidthListObject = new StationBandwidthListObject();
			stationBandwidthListObject.setIana_id(iana_id);
			stationBandwidthListObject.setSn(sn);
			ACUtil.<StationBandwidthListObject> removePoolObjectBySn(stationBandwidthListObject, StationBandwidthListObject.class);
			StationListObject stationLst = new StationListObject();
			stationLst.setIana_id(iana_id);
			stationLst.setSn(sn);
			ACUtil.<StationListObject> removePoolObjectBySn(stationLst, StationListObject.class);
	
			// flush the remaining gps data to database
			DevLocationsObject devLoc = new DevLocationsObject();
			devLoc.setSn(sn);
			devLoc.setIana_id(iana_id);
			DevLocationsObject devLocFromCache = ACUtil.getPoolObjectBySn(devLoc, DevLocationsObject.class);
			if (devLocFromCache == null || devLocFromCache.getLocation_list().isEmpty()) {
	
				log.debug("No device location found in cache to flush to db after device offline");
			} 
			else 
			{
				//Init DB connection
				DBUtil dbUtil = DBUtil.getInstance();
				DBConnection batchConnection = null;
				NetworksDAO networksDAO = null;
				DeviceGpsLocationsDatesDAO deviceGpsLocaDatesDAO = null;
				try
				{
					batchConnection = dbUtil.getConnection(false, devO.getOrganization_id(), false);
					networksDAO = new NetworksDAO(devO.getOrganization_id());
					deviceGpsLocaDatesDAO = new DeviceGpsLocationsDatesDAO(devO.getOrganization_id());
				}
				catch(SQLException e)
				{
					log.error("Purge gps data from cache to db get db connection error "+e,e);
					return false;
				}
				
				//Make a location list copy to persist the locations
				CopyOnWriteArrayList<LocationList> locationList = devLocFromCache.getLocation_list();
				ArrayList<LocationList> copyLocationList = new ArrayList<LocationList>();
				copyLocationList.addAll(locationList);
				
				//Begin loop location list
				HashMap<Integer, Boolean> timestampAlreadyAddedList = new HashMap<Integer, Boolean>();
				LocationList lastItem = null;
				boolean isInvalidAdded = false;
				Date utcTime = null;
				Date curT = null;
				for( LocationList locItem : copyLocationList )
				{
					if (timestampAlreadyAddedList.get(locItem.getTimestamp()) != null) {
						// already added, skip
						continue;
					}
					
					// save to database
					utcTime = new Date((long) locItem.getTimestamp() * 1000);
					curT = new Date();
					
					int utc_unixT = (int)( utcTime.getTime() / 1000 );
					int cur_unixT = (int)( curT.getTime() / 1000 );
					if( utc_unixT > (cur_unixT + 300) )
					{
						log.infof("Future date record found, record ignored %s %s UTC %d CUR %d",devLoc.getSn(),MessageType.PIPE_INFO_TYPE_DEV_LOCATIONS,utc_unixT,cur_unixT);
						continue;
					}
					
					DeviceGpsLocationsId dDeviceGpsLocationsId = new DeviceGpsLocationsId(devO.getNetwork_id(), devO.getDevice_id(), locItem.getTimestamp().intValue());
					DeviceGpsLocations deviceGpsLocations = null;
					
					if( locItem.getStatus() != null && locItem.getStatus() == 0 )
					{
						deviceGpsLocations = new DeviceGpsLocations(dDeviceGpsLocationsId, utcTime, locItem.getLatitude(), locItem.getLongitude(),
								locItem.getAltitude(), locItem.getSpeed(), locItem.getH_uncertain(), locItem.getV_uncertain(), locItem.getH_dop(), locItem.getFlag());
						
						if(isInvalidAdded)
							isInvalidAdded = false;
					}
					else
					{
						if( !isInvalidAdded )
						{
							deviceGpsLocations = new DeviceGpsLocations(dDeviceGpsLocationsId, utcTime, null, null,
									null, null, null, null, null, null);
							isInvalidAdded = true;
						}
					}
						
					if( deviceGpsLocations == null  )
					{
						continue;
					}
					
					if(locItem.getH_uncertain() != null && locItem.getH_uncertain() > 999999)
						deviceGpsLocations.setHUncertain(999999f);
					
					deviceGpsLocations.createReplace();
					batchConnection.addBatch(deviceGpsLocations);
					
					if( batchConnection.getBatchSize() >= MAX_BATCH_ADD )
					{
						batchConnection.commit();
						batchConnection.close();
						if( dbUtil.isSessionStarted() )
							dbUtil.endSession();
						dbUtil.startSession();
						// dbUtil.setDebugSQL(true);
						batchConnection = dbUtil.getConnection(false, devO.getOrganization_id(), false);
					}
					
					timestampAlreadyAddedList.put(locItem.getTimestamp().intValue(), true);
					lastItem = locItem;
				}
				
				DeviceLastLocObject devLastLocation = new DeviceLastLocObject();
				devLastLocation.setSn(sn);
				devLastLocation.setIana_id(iana_id);
				if (lastItem != null){
					Networks network = networksDAO.findById(devO.getNetwork_id());
					String timezone = network.getTimezone();
					String timezoneId = DateUtils.getTimezoneFromId(Integer.valueOf(timezone));
					TimeZone tz = TimeZone.getTimeZone(timezoneId);
					int netTime = lastItem.getTimestamp().intValue();
					int offsetMillis = tz.getOffset((long)netTime*1000);
					int adjustNetTime = netTime + offsetMillis/1000;
					adjustNetTime = adjustNetTime/86400 * 86400; //round to day
					
					DeviceGpsLocationsDates locaDate = deviceGpsLocaDatesDAO.getDeviceGpsLocationsDates(devO.getNetwork_id(), devO.getDevice_id(), adjustNetTime);
					if (locaDate == null){
						DeviceGpsLocationsDatesId locaDatesId = new DeviceGpsLocationsDatesId();
						locaDatesId.setDeviceId(devO.getDevice_id());
						locaDatesId.setNetworkId(devO.getNetwork_id());
						locaDatesId.setUnixtime(adjustNetTime);
						locaDate = new DeviceGpsLocationsDates();
						locaDate.setId(locaDatesId);								
						locaDate.create();
						batchConnection.addBatch(locaDate);
						//cache last location
						devLastLocation.setLastLocation(lastItem);
						devLastLocation.setStatic(false);
						ACUtil.cachePoolObjectBySn(devLastLocation, DeviceLastLocObject.class);
					}
				}
				
				ACUtil.removePoolObjectBySn(devLocFromCache, DevLocationsObject.class);
			}
		} catch (Exception e)
		{
			log.error("Exception ",e);
			return false;
		}
		
		return true;
	}


	public static void main( String[] args )
	{
		DBUtil dbUtil = null;
		try {
			dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
	
			SnsOrganizationsDAO snsDAO = new SnsOrganizationsDAO();
			List<SnsOrganizations> snsOrgLst = null;
	
			snsOrgLst = snsDAO.getAllSnsOrganizations();
			ConcurrentHashMap<String, SnsOrganizations> snsOrgMap = new ConcurrentHashMap<String, SnsOrganizations>();
					
			for (SnsOrganizations snsOrg : snsOrgLst) {
				snsOrgMap.put(BranchUtils.createKey(snsOrg.getIanaId(), snsOrg.getSn()), snsOrg);
			}
	
			int cnt = 0, cntp=0;
			BufferedReader br = new BufferedReader(new FileReader("C://sn.txt"));
			String line;
			
			while((line=br.readLine())!=null) {
				String iana;
				String sn;
				int pos;
				
				line = line.trim();
				pos = line.indexOf(' ');
				if(pos==-1)
					continue;
				
				iana = line.substring(0, pos);
				sn = line.substring(pos+1);
				
				log.infof("SN=%s", sn);
				SnsOrganizations snsO = snsOrgMap.get(iana + "_" + sn); 
				String orgId = snsO.getOrganizationId();
				
				DevicesDAO devDAO = new DevicesDAO(orgId);
				Devices dev = devDAO.findBySn(Integer.parseInt(iana), sn);
				if(dev!=null) {
					int network_id = dev.getNetworkId();
					if(dev.getOfflineAt().getTime() > dev.getLastOnline().getTime()) {
						EventLogDAO evtDAO = new EventLogDAO(orgId);	
						Date off = DateUtils.getUtcDate(dev.getOfflineAt(), "GMT-8");
						int ts = (int)(off.getTime()/1000);
						EventLog evt= evtDAO.getLastOnlineLaterThan(network_id+"", dev.getId(), ts);
						if(evt!=null) {
							int ts2 = evt.getUnixtime();
							Date on = new Date(ts2 *1000L);
							dev.setLastOnline(on);
							log.infof("org=%s, network=%s, device=%d, sn=%s, active=%s, offlineAt=%s, lastOnlineFromEvent=%s, ut=%d, patched", orgId, network_id, dev.getId(), dev.getSn(), dev.isActive(), dev.getOfflineAt(), on, ts2);
	//						devDAO.saveOrUpdateDB(dev);
	//						break;
							cntp ++;
						}
						else
							log.infof("org=%s, network=%s, device=%d, sn=%s, active=%s, offlineAt=%s, can't find lastOnline!!!", orgId, network_id, dev.getId(), dev.getSn(), dev.isActive(), dev.getOfflineAt());
					}
					else
						log.infof("org=%s, network=%s, device=%d, sn=%s, active=%s, offlineAt=%s, no mismatch", orgId, network_id, dev.getId(), dev.getSn(), dev.isActive(), dev.getOfflineAt());
				}
				else
					log.infof("org=%s, sn=%s, can't find the device", orgId, sn);
				cnt++;
			}
			br.close();
			log.infof("Done cnt=%d, cntp=%d", cnt, cntp);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try {
				dbUtil.endSession();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}