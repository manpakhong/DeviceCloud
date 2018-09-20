package com.littlecloud.ac;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.littlecloud.ac.ACService.OP_MODE;
import com.littlecloud.ac.handler.OnlineStatusAndCustomEventHandler;
import com.littlecloud.ac.handler.RootServerRedirectionHandler;
import com.littlecloud.ac.health.HealthMonitorHandler;
import com.littlecloud.ac.health.info.ACInfo;
import com.littlecloud.ac.json.model.Json_AcInfoUpdate;
import com.littlecloud.ac.json.model.Json_DeviceNeighborList_Response;
import com.littlecloud.ac.json.model.Json_DiagReport;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.ac.json.model.util.JsonApiFormatMessageParser;
import com.littlecloud.ac.messagehandler.AcInfoUpdateMessageHandler;
import com.littlecloud.ac.messagehandler.CaptivePortalMessageHandler;
import com.littlecloud.ac.messagehandler.DeviceNeighborSsidDiscoveryMessageHandler;
import com.littlecloud.ac.messagehandler.DiagnosticReportMessageHandler;
import com.littlecloud.ac.messagehandler.FirmwareMessageHandler;
import com.littlecloud.ac.messagehandler.GPSMessageHandler;
import com.littlecloud.ac.messagehandler.RedirectWtpMessageHandler;
import com.littlecloud.ac.messagehandler.queue.executor.CaptivePortalMessageHandleExecutorController;
import com.littlecloud.ac.util.ACLicenseManager;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.DeviceMonthlyUsagesDAO;
import com.littlecloud.control.dao.DeviceStatusDAO;
import com.littlecloud.control.dao.DeviceUpdatesDAO;
import com.littlecloud.control.dao.DeviceUsagesDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.NetworkEmailNotificationsDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.dao.branch.ConfigUpdatesDAO;
import com.littlecloud.control.dao.branch.DeviceFirmwareSchedulesDAO;
import com.littlecloud.control.devicechange.DeviceChangeServiceUtils;
import com.littlecloud.control.deviceconfig.DeviceConfigObj;
import com.littlecloud.control.deviceconfig.DeviceConfigScheduler;
import com.littlecloud.control.entity.DeviceStatus;
import com.littlecloud.control.entity.DeviceUpdates;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Devices.ONLINE_STATUS;
import com.littlecloud.control.entity.NetworkEmailNotifications;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.branch.ConfigUpdates;
import com.littlecloud.control.entity.branch.ConfigUpdatesId;
import com.littlecloud.control.entity.branch.DeviceFirmwareSchedules;
import com.littlecloud.control.entity.branch.SnsOrganizations;
import com.littlecloud.control.entity.report.DeviceUsages;
import com.littlecloud.control.json.model.config.JsonConf;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings;
import com.littlecloud.control.json.model.config.util.CommonConfigUtils;
import com.littlecloud.control.json.model.config.util.ConfigBackupUtils;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerDeviceTask.CONFIG_UPDATE_REASON;
import com.littlecloud.control.json.model.config.util.PepvpnStatusUtils;
import com.littlecloud.control.json.model.config.util.RadioConfigUtils;
import com.littlecloud.control.json.model.config.util.exception.FeatureNotFoundException;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonMatcherUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.util.NetworkUtils;
import com.littlecloud.criteria.SendImmediateAlertCriteria;
import com.littlecloud.dtos.json.EmailTemplateObjectDto;
import com.littlecloud.fusionhub.FusionHubUtil;
import com.littlecloud.fusionhub.Json_FusionHubLicenseRequest;
import com.littlecloud.helpers.NetworkEmailNotificationsHelper;
import com.littlecloud.pool.control.QueueCacheControl;
import com.littlecloud.pool.control.QueueControl;
import com.littlecloud.pool.object.ACLicenseObject;
import com.littlecloud.pool.object.ACRegListObject;
import com.littlecloud.pool.object.BasicObject;
import com.littlecloud.pool.object.BranchInfoObject;
import com.littlecloud.pool.object.ConfigBackupTextObject;
import com.littlecloud.pool.object.ConfigChecksumObject;
import com.littlecloud.pool.object.ConfigGetObject;
import com.littlecloud.pool.object.ConfigPutObject;
import com.littlecloud.pool.object.DevBandwidthObject;
import com.littlecloud.pool.object.DevDetailJsonObject;
import com.littlecloud.pool.object.DevDetailObject;
import com.littlecloud.pool.object.DevInfoObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.FeatureGetObject;
import com.littlecloud.pool.object.FirmwarePutObject;
import com.littlecloud.pool.object.FirmwareScheduleObject;
import com.littlecloud.pool.object.IcmgPutObject;
import com.littlecloud.pool.object.PepvpnEndpointObject;
import com.littlecloud.pool.object.PepvpnPeerDetailObject;
import com.littlecloud.pool.object.PepvpnPeerListObject;
import com.littlecloud.pool.object.PepvpnTunnelStatObject;
import com.littlecloud.pool.object.PoolObjectDAO;
import com.littlecloud.pool.object.StationBandwidthListObject;
import com.littlecloud.pool.object.StationBandwidthListObject.StationStatusList;
import com.littlecloud.pool.object.StationListObject;
import com.littlecloud.pool.object.SystemInfoObject;
import com.littlecloud.pool.object.TunnelObject;
import com.littlecloud.pool.object.WebTunnelingObject;
import com.littlecloud.pool.object.utils.BranchUtils;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.littlecloud.pool.object.utils.FeatureGetUtils;
import com.littlecloud.pool.object.utils.LocationUtils;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.ProductUtils;
import com.littlecloud.pool.utils.PropertyService;
import com.littlecloud.services.AlertMgr;
import com.littlecloud.utils.HtmlUtils;
//import com.littlecloud.control.dao.util.HibernateUtil;
import com.peplink.api.db.util.DBUtil;

public class WtpMsgHandler implements Runnable {

	private static boolean PROD_MODE = DebugManager.isPROD_MODE();
	private static final int MAX_CALL_TIME = 15; // 15 sec
	private static final int MAX_DEV_ONLINE_CALL_TIME = 5;
	private static final int MAX_JSON_MSG = 10240;
	
	private static final Logger log = Logger.getLogger(WtpMsgHandler.class);	
	
	private static PropertyService<WtpMsgHandler> ps = new PropertyService<WtpMsgHandler>(WtpMsgHandler.class);
	public static BlockingQueue<String> jsonQueue = new ArrayBlockingQueue<String>(MAX_JSON_MSG);
	
	private static final int DEV_USAGE_REPORT_INTERVAL_SECOND = ps.getInteger("DEV_USAGE_REPORT_INTERVAL_SECOND");	
	private static final boolean IsBYPASS_EXPIRY = (ps.getString("mode").equalsIgnoreCase("peplink")?false:true);
	private static final int EVENT_EXPIRED_TIME_IN_SEC = ps.getInteger("EVENT_EXPIRED_TIME_IN_SEC");	//TODO 
	public static final int MAX_CONCURRENT_MESSAGE = ps.getInteger("MAX_CONCURRENT_MESSAGE");
	private static final int MAX_CURRENT_ONLINEEVENT_COUNTER = ps.getInteger("MAX_CURRENT_ONLINEEVENT_COUNTER");
	private static final WtpMsgHandlerPool wtppool = WtpMsgHandlerPool.getInstance();
	public static boolean UNHEALTHY_SKIP_MESSAGE = ps.getString("UNHEALTHY_SKIP_MESSAGE").equalsIgnoreCase("true")?true:false;
	
	public static final String DATA_TAG = "data";
	
	private String json;
	private Long threadid;

	private static AtomicInteger counter = new AtomicInteger(0);
	private static AtomicInteger counterMax = new AtomicInteger(0);
	
	private static AtomicInteger currentOnlineEventCounter = new AtomicInteger(0);
	
	private static AtomicInteger msg_process_counter = new AtomicInteger(0);

	
	
	public static int getMaxJsonPipeInfoTypeDevLocations() {
		return MAX_JSON_MSG;
	}

	public String getJson() {
		return json;
	}
	
	public String getMessageInfo() {
		StringBuilder sb = null;
		
		Gson gson = new Gson();
		QueryInfo<Object> info = null;
		try {
			info = gson.fromJson(json, QueryInfo.class);
			
			if (info!=null)
			{
				sb = new StringBuilder();
				MessageType mt = info.getType();
				if(mt != null){
					sb.append(mt);
					sb.append(" ");
				}
				
				sb.append(info.getSid());
				sb.append(" ");
				sb.append(info.getIana_id());
				sb.append(" ");
				sb.append(info.getSn());
				sb.append(" ");
				sb.append("starttime:");
				sb.append(DateUtils.getUtcDate());
				
				return sb.toString();
			}
		} catch (Exception e) {
			return json;
		}
		
		return json;
	}

	public static int getEventExpiredTimeInSec() {
		return EVENT_EXPIRED_TIME_IN_SEC;
	}

	public static int getCounter() {
		return counter.intValue();
	}

	public static int getCounterMax() {
		return counterMax.intValue();
	}

	public static AtomicInteger getCurrentOnlineEventCounter() {
		return currentOnlineEventCounter;
	}

	public static void setCounter(boolean bAdd) {
		if (bAdd)
		{
			counter.incrementAndGet();

			if (counter.intValue() > counterMax.intValue())
				counterMax.set(counter.intValue());
		}
		else
		{
			counter.decrementAndGet();
		}
	}
	
	public static int getMsg_process_counter() {
		return msg_process_counter.get();
	}

	public static void setMsg_process_counter(int msg_process_counter) {
		WtpMsgHandler.msg_process_counter.set(msg_process_counter);
	}
	
	public static void addMsgProcessCounter()
	{
		WtpMsgHandler.msg_process_counter.incrementAndGet();
	}

	public static void reQueue(String msgJson)
	{
		try {
			jsonQueue.add(msgJson);
			if (log.isDebugEnabled())
				log.debug("ReQueued json: " + msgJson);
		} catch (IllegalStateException ie) {
			log.error("Error in reQueueing json: " + msgJson + ", e=" + ie, ie);
		} catch (Exception e) {
			log.error("Unknown error in reQueueing json: " + msgJson + ", e=" + e, e);
		}
	}

	public WtpMsgHandler(String json)
	{
		this.json = json;
	}
	
	@Override
	public void run() {
		
		if(json.indexOf("PIPE_INFO_AC_REG_LIST") !=-1) {
			Iterator<Map.Entry<Long, String>> iter = WtpMsgHandlerPool.threadExecInfoMap.entrySet().iterator();
			while(iter.hasNext()) { 
				Map.Entry<Long, String> threadInfo = iter.next();
				if (threadInfo.getValue()==null)
					log.warnf("threadInfo.getValue() is null for key %s", threadInfo.getKey());
				
				if(threadInfo.getValue()!=null && threadInfo.getValue().startsWith("PIPE_INFO_AC_REG_LIST")) {
					log.warn("Skip PIPE_INFO_AC_REG_LIST as last msg is still processing in thread "+ threadInfo.getKey());
					return;
				}
			}
		}
		
		this.threadid = Thread.currentThread().getId();
		
		wtppool.startThread(threadid, this.getMessageInfo());

		int callStartTime = DateUtils.getUnixtime();
		int callMsgTime = 0;
		boolean isOnlineEventCounted = false;
		
		JSONObject object;
		JSONObject data;
		DevOnlineObject devOnlineO = null;
		boolean isOnlineStatusAndCustomEventHandlerSuccess = true;
		Devices devices = null;		
		String OnlineOrgId = null;
		String logMsg = null; 		
		
		if (log.isDebugEnabled())
			log.debugf("received wtp msg %s", json);
		
		if (json == null)
		{
			log.warn("received json is null!");
			wtppool.endThread(this.threadid);
			return;
		}

		/* parse info type from WTP before assigning to object */
		Gson gson = new Gson();
		QueryInfo<Object> info = null;
		try {
			info = gson.fromJson(json, QueryInfo.class);
		} catch (Exception e) {
			log.errorf("Json from WTP format exception (json=%s)", json, e);
			wtppool.endThread(this.threadid);
			return;
		}
		
		MessageType mt = info.getType();
		if(mt == null){
			if(log.isDebugEnabled())
				log.debugf("Invalid message received, info=%s", info);
			wtppool.endThread(this.threadid);
			return;
		}
		
		if (PROD_MODE)
		{
			if (UNHEALTHY_SKIP_MESSAGE)
			{
				if (HealthMonitorHandler.isClusterHealthy()==true)
				{					
					UNHEALTHY_SKIP_MESSAGE = false;
					log.warn("RESET UNHEALTHY_SKIP_MESSAGE = false");
				}
				else
				{
					if (log.isDebugEnabled())
						log.debugf("UNHEALTHY_SKIP_MESSAGE = true");
					
					if(json.indexOf("PIPE_INFO_TYPE_DEV_LOCATIONS") != -1)
						reQueue(json);
					
					/* critical response message cannot be skipped */
					if (mt != MessageType.PIPE_INFO_TYPE_FIRMWARE_PUT)
					{
						wtppool.endThread(this.threadid);
						return;
					}
				}
			}
		}

		setCounter(true);
		
		try {
			addMsgProcessCounter();
			WtpMsgHandlerUtils.logOnlineStatusMessage(info);
			
			int status = info.getStatus();

			/* handle command or response from WTP */
			if (mt == MessageType.PIPE_INFO_TYPE_DEV_ONLINE)
			{
				isOnlineEventCounted = true;
				currentOnlineEventCounter.incrementAndGet();
				
				if (currentOnlineEventCounter.intValue()>MAX_CURRENT_ONLINEEVENT_COUNTER)
				{
					log.warnf("PIPE_INFO_TYPE_DEV_ONLINE - MAX_CURRENT_ONLINE_EVENTCOUNTER %d is reached, skip %s! (current=%d)", MAX_CURRENT_ONLINEEVENT_COUNTER, info.getIana_id()+" "+info.getSn(), currentOnlineEventCounter.intValue());
					return;
				}
			}

			// isMessageRequireFilter - filter based on message type only
			// isMessageRequireSkip - skip based on > max msg count and msg interval only
			if (WtpMsgHandlerUtils.isMessageRequireFilter(info) || WtpMsgHandlerUtils.isMessageRequireSkip(info))
			{
				if (log.isInfoEnabled())
					log.infof("dev %d %s msg type %s with sid %s status %d is filtered/skipped", info.getIana_id(), info.getSn(), mt.toString(), info.getSid(), status);
				return;
			}

			if(FusionHubUtil.handleDefaultSn(info)) {	// check default FusionHub or not, if yes then handle it
				return;
			}
			
			// check online redirection, no online object if device is redirected
			boolean isRedirected = RedirectWtpMessageHandler.doCheckAndRedirectWtpMessageHandler(info);
			
			if (isRedirected){
				return;
			}
			
			// will rebuild online object if not exist
			isOnlineStatusAndCustomEventHandlerSuccess = OnlineStatusAndCustomEventHandler.handle(info);

			devOnlineO = new DevOnlineObject();
			devOnlineO.setIana_id(info.getIana_id());
			devOnlineO.setSn(info.getSn());
			devOnlineO = ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
			
			if (!WtpMsgHandlerUtils.checkOnlineObject(info, devOnlineO)) {
				if (log.isDebugEnabled())
					log.debug("AL10001 - DevOnlineObject checking failure and return");
				return;
			}
			
			if(devOnlineO != null)	// there are still some messages here that devOnlineO is null
				OnlineOrgId = devOnlineO.getOrganization_id();
			
			boolean isImmediateAlertNeeded = false;
			boolean bFectchUsageHistory = false;
			int alertedLevel = 0;
			callMsgTime = DateUtils.getUnixtime();

			switch (mt)
			{
			/* AC scheduler sends in fix interval e.g.30s */
			case PIPE_INFO_AC_REG_LIST:	
			{
				/* use batch insert */
				DBUtil dbUtil = null;
				DevInfoObject devInfoObject = null;
				StringBuilder logWarn = new StringBuilder();				
						
				try {
					 /* Noted: DBUtil mentioned startSession should be called before getConnection! */
					dbUtil = DBUtil.getInstance();
					dbUtil.startSession();
					
					object = JSONObject.fromObject(info);
					data = object.getJSONObject(DATA_TAG);
					ACRegListObject regO = gson.fromJson(data.toString(), ACRegListObject.class);
					List<DevInfoObject> devInfoLst = regO.getDev_list();
					if (log.isDebugEnabled())
						log.debugf("REG_LIST start - devInfoLst.size %d", devInfoLst!=null?devInfoLst.size():0);
					
					BranchInfoObject brO = BranchUtils.getInfoObject();
					
					if(brO != null) {
						if (log.isInfoEnabled())
							log.info("brO.size = "+brO.getSnsOrgMap().size());

						for (DevInfoObject devInfo: devInfoLst)
						{					
							SnsOrganizations snsOrg = brO.getSnsOrgObj(devInfo.getIana_id(),  devInfo.getSn());
							if (snsOrg==null)
							{
								logMsg = String.format("Root Server Redirection - %d %s is not found in sns_organizations", devInfo.getIana_id(), devInfo.getSn());
								DeviceUtils.logDevInfoObject(devInfo, log, Level.INFO, logMsg);
								RootServerRedirectionHandler.getInstance().redirectToRootIfNotRegistered(devInfo.getIana_id(), devInfo.getSn());
								continue;
							}

							devInfoObject = DeviceUtils.createDevInfoObjectIfNotExists(devInfo.getIana_id(), devInfo.getSn());
							devInfoObject.setTs(devInfo.getTs());
							
							String orgId =  snsOrg.getOrganizationId();
							devices = NetUtils.getDevicesBySn(orgId, devInfo.getIana_id(), devInfo.getSn(), true);
							DevOnlineObject devO = PoolObjectDAO.getDevOnlineObject(devInfo.getIana_id(), devInfo.getSn());					
							if (IsBYPASS_EXPIRY || (devices!=null && isDeviceWarrantValid(devices)))
							{
								if (log.isDebugEnabled())
									log.debugf("LT10001 - warranty valid for %s", devInfo.getSn());
								if (devO != null)
								{
									/* update last update time */
									//logMsg = String.format("LT10001 - update last update time %s", devInfo.getSn());
									devO.setLastUpdateTime(DateUtils.getUtcDate());
								
									/* recover machine id */
									if (!devO.getMachine_id().equalsIgnoreCase(ACService.getServerName()))
									{
										if(devInfo.getTs() > devO.getLastAcTime()) {
											logMsg = String.format("LT10001E - dev %d %s has migrated from %s(%d) to this server %s(%d)", devO.getIana_id(), devO.getSn(), devO.getMachine_id(), devO.getLastAcTime(), ACService.getServerName(), devInfo.getTs()); 
											logWarn.append(logMsg+"\n");
											
											devO.setMachine_id(ACService.getServerName());									
											devO.setLastAcTime(devInfo.getTs());
											devInfoObject.addDebugInfo(logMsg);
											ACUtil.<DevOnlineObject> cachePoolObjectBySn(devO, DevOnlineObject.class);
											if (log.isInfoEnabled())
												log.infof("updated devOnlineObject with %s(%d)", ACService.getServerName(), devInfo.getTs()); 
										}
										else // ignore and no need to update cache for this DevOnlineObject
											if (log.isInfoEnabled())
												log.infof("ignored device %d %s's update %s(%d) in REG_LIST vs DevO %s(%d)", devO.getIana_id(), devO.getSn(), ACService.getServerName(), devInfo.getTs(), devO.getMachine_id(), devO.getLastAcTime());
									}
									else {
										devO.setLastAcTime(devInfo.getTs());
										ACUtil.<DevOnlineObject> cachePoolObjectBySn(devO, DevOnlineObject.class);
									}
								}
								
								if (devO == null || !devO.isOnline())	// || devO.isRebuild()==true
								{
									String reason = "";
									
									if (devO==null)
										reason="devO is null";
									else if (!devO.isOnline())
										reason="devO is offline";
									else if (devO.isRebuild())
										reason="devO is rebuild";
									
									if (log.isDebugEnabled())
										log.debugf("LT10001 - missing online object or status not update devO=%s\n devInfo=%s", devO, devInfo);	
									
									/* avoid duplicate online event log */
									
									if (devInfoObject.getMissingOnlineObjectCount()==null)
									{
										devInfoObject.setMissingOnlineObjectCount(0);
									}
									
									if (devInfoObject.getMissingOnlineObjectCount() >= DevInfoObject.MAX_MISS_COUNT_FOR_ONLINE_EVENT_LOG)
									{
										logMsg = String.format("LT10001 ONOFF20140130 - resend online %s cnt=%d (%s)", devInfo.getSn(), devInfoObject.getMissingOnlineObjectCount(), reason);
									
//										if (devO!=null && devO.isRebuild())
//											logMsg = logMsg + " (for rebuild object!)";
									
										log.warnf(logMsg);
										//if (onlineFetchCount<=MAX_CURRENT_ONLINEFETCH_COUNTER)
										//{
											//onlineFetchCount++;
										ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_ONLINE, JsonUtils.genServerRef(), devInfo.getIana_id(), devInfo.getSn());
										//}
//										else
//										{
//											log.warnf("LT10001 ONOFF20140130 - resend online %s later!", devInfo.getSn());
//										}
										devInfoObject.setMissingOnlineObjectCount(0);
										
										devInfoObject.addDebugInfo(logMsg);
									}
									else
									{
										devInfoObject.setMissingOnlineObjectCount(devInfoObject.getMissingOnlineObjectCount()+1);								
									}								
								}
							}
							else
							{
								boolean isExpireOpSuccess = false;
										
								if (devices==null)
								{
									if (log.isDebugEnabled())
										log.debugf("LT10001 - INVALID warranty device %s is not found in cache", devInfo.getSn());
								}
								
								if (log.isDebugEnabled())
									log.debugf("LT10001 - INVALID warranty for %s", devInfo.getSn());
								devInfoObject.addDebugInfo("LT10001 - INVALID warranty for "+devInfo.getSn());
								
								/* no need to handle warranty expire if status is offline or warranty expired */ 
								if (devO!=null)
								{
									if (!devO.getMachine_id().equalsIgnoreCase(ACService.getServerName()))
									{
										if(devInfo.getTs() > devO.getLastAcTime()) {
											logMsg = String.format("LT10001E - INVALID warranty dev %d %s has migrated from %s(%d) to this server %s(%d)", devO.getIana_id(), devO.getSn(), devO.getMachine_id(), devO.getLastAcTime(), ACService.getServerName(), devInfo.getTs()); 
											logWarn.append(logMsg+"\n");
											
											devO.setMachine_id(ACService.getServerName());									
											devO.setLastAcTime(devInfo.getTs());											
											ACUtil.<DevOnlineObject> cachePoolObjectBySn(devO, DevOnlineObject.class);
											if (log.isInfoEnabled())
												log.infof("INVALID warranty updated devOnlineObject with %s(%d)", ACService.getServerName(), devInfo.getTs());
										}
									}
									
									if (devO.getStatus()!=ONLINE_STATUS.WARRANTY_EXPIRED && devO.getStatus()!=ONLINE_STATUS.OFFLINE)
									{
										isExpireOpSuccess = DeviceUtils.WarrantyExpiredHandler(devO); 
										if (!isExpireOpSuccess)
										{
											log.errorf("Fail to handle warranty expired device %s", devInfo.getSn());
											devInfoObject.addDebugInfo("Fail to handle warranty expired device "+devInfo.getSn());
										}										
									}
									
									if (isExpireOpSuccess && devO.getStatus()!=ONLINE_STATUS.WARRANTY_EXPIRED)
									{
										/* sync warranty expire status */
										log.warnf("INFO update device %s to warranty_expired state", devInfo.getSn());
										devO.setStatus(ONLINE_STATUS.WARRANTY_EXPIRED);
										DeviceUtils.putOnlineObject(devO);
									}
								}
							}
							//ACUtil.<DevInfoObject> cachePoolObjectBySn(devInfoObject, DevInfoObject.class);
							DeviceUtils.putDevInfoObject(devInfoObject);
							
							// do service redirection
							RedirectWtpMessageHandler.doCheckAndRedirectWtpMessageHandler(devO);	
						}
						
						/* cache for lookup */				 
						regO.setIana_id(9999);
						regO.setSn(ACService.getServerName());
						ACUtil.<ACRegListObject> cachePoolObjectBySn(regO, ACRegListObject.class);
					}
					else
						log.error("Can't find BranchInfoObject for processing REG_LIST");
					
				} catch (Exception e) {
					log.error("AC_REG_LIST error in startSession", e);
					return;
				} finally {
					
					if (logWarn.toString().length()!=0)
						log.warnf("AC_REG_LIST WARN: %s", logWarn.toString());
					
					try {
						if (dbUtil!=null && dbUtil.isSessionStarted()) {
							dbUtil.endSession();
						}
					} catch (Exception e2) {
						log.error("AC_REG_LIST - error in endSession", e2);
					}
				}				
				if (log.isDebugEnabled())
					log.debugf("REG_LIST end");				
				return;
			}
			case PIPE_INFO_TYPE_DEV_ONLINE:
			{
					/* normal device */
					OP_MODE opMode = OP_MODE.OPMODE_NORMAL;
					
					/* online object has already been constructured in CustomEventHandler */				
					if (!isOnlineStatusAndCustomEventHandlerSuccess)
					{
						if (log.isInfoEnabled()) log.infof("OnlineStatusAndCustomEventHandler failed for info %s", info);
					}								
					
					if (devOnlineO == null ) {
						ACService.set_dev_op_mode(JsonUtils.genServerRef(), info.getIana_id(), info.getSn(), OP_MODE.OPMODE_NOREPORT);
						log.warnf("NO ONLINE OBJ (Assume unregistered), suppressed (sid=%s, iana=%d, sn=%s, type=%s)", info.getSid(), info.getIana_id(), info.getSn(), info.getType());
						break;
					}		
					if (log.isDebugEnabled())
						log.debug("devOnlineO=" + devOnlineO);								
					
	
					/*** Handle normal devices ***/
					//boolean needUpdateConfig = false;					
					DeviceStatusDAO deviceStatusDAO = null;
					NetworksDAO networksDAO = null;
					DevicesDAO devicesDAO = null;
					Networks network = null;
					try {
						devicesDAO = new DevicesDAO(OnlineOrgId);
						devices = devicesDAO.findBySn(info.getIana_id(), info.getSn());
						if (devices == null)
						{
							log.warnf("no record for %s %s", info.getIana_id(), info.getSn());
							break;
						}
											
						deviceStatusDAO = new DeviceStatusDAO(OnlineOrgId);
						networksDAO = new NetworksDAO(OnlineOrgId);
						network = networksDAO.findById(devices.getNetworkId());
	
						/* low bandwidth mode */
						if (devices != null && network != null && network.isLowBandwidthMode())
						{
							opMode = OP_MODE.OPMODE_LOWBAND;
						}
	

						// to check and sync if Devices fw version is not sync with Ic2 fw version, action to fwScheduleTable to sync it.
						FirmwareMessageHandler.doOnlineMessage(devices, network, OnlineOrgId);
						
						
						/* check warranty */
						Date lastOnline = DateUtils.getUtcDate();
						if (devices.getExpiryDate() == null) {
							log.warn("Device warranty is not set s/n: " + info.getSn());
							opMode = OP_MODE.OPMODE_NOREPORT;
						} else {
							if (log.isInfoEnabled())
								log.info("AL10001 - bBypassExpiry " + IsBYPASS_EXPIRY + " last on " + lastOnline.getTime() + " exp " + devices.getExpiryDate());
							if (IsBYPASS_EXPIRY || isDeviceWarrantValid(devices))
							{
								opMode = OP_MODE.OPMODE_NORMAL;		
								if (log.isDebugEnabled())
									log.debugf("device is WarrantValid - iana:%s, sn:%s ",  devices.getIanaId(), devices.getSn());
								/* ddns */
								devOnlineO.setDdns_enabled(devices.getDdns_enabled());						
								devOnlineO.setLastAcTime(DateUtils.getUtcUnixtime());
								ACUtil.<DevOnlineObject> cachePoolObjectBySn(devOnlineO, DevOnlineObject.class);
								
								// check if usage is exists in DB
								try
								{
									DeviceUsagesDAO deviceUsagesDAO = new DeviceUsagesDAO(OnlineOrgId);
									DeviceMonthlyUsagesDAO deviceMonthlyUsageDAO = new DeviceMonthlyUsagesDAO(OnlineOrgId);
									// ClientUsagesDAO clientUsagesDAO = new ClientUsagesDAO(OnlineOrgId);
									DeviceUsages device_usage = deviceUsagesDAO.getLatestDeviceUsage(devices.getId());
									// ClientUsages client_usage =
									// clientUsagesDAO.getLatestClientUsage(devices.getId());
	
									if (device_usage == null)
									{
										bFectchUsageHistory = true;
									}
									else
									{
										// DeviceMonthlyUsages usage =
										// deviceMonthlyUsageDAO.getLatestDeviceUsage(devices.getId());
										List<Integer> times = deviceMonthlyUsageDAO.getDistinctDatetimeByDevId(devices.getNetworkId(), devices.getId());
										if (times == null || times.size() < 2)
											bFectchUsageHistory = true;
									}
									
								} catch (SQLException e)
								{
									log.error("device_usage get failed" + e, e);
								}
	
								ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_FEATURE_GET, JsonUtils.genServerRef(), devices.getIanaId(), devices.getSn());
							
							} else {	// else if (IsBYPASS_EXPIRY || isDeviceWarrantValid(devices))
								// bypass is false AND OnlineTime is larger than ExpiryTime
								if (log.isInfoEnabled())
									log.info("PIPE_INFO_TYPE_DEV_ONLINE message for warranty expired device sn: " + info.getSn());
								opMode = OP_MODE.OPMODE_NOREPORT;
							}
						}	// else devices.getExpiryDate() == null
					} catch (Exception e) {
						log.error("DEV_ONLINE exception - " + e, e);
					}
					if (log.isInfoEnabled())
						log.infof("OnlineOrgId=%s", OnlineOrgId);
					
					ACService.set_dev_op_mode(JsonUtils.genServerRef(), info.getIana_id(), info.getSn(), opMode);
					if (NetworkUtils.isGpsTrackingDisabled(OnlineOrgId, network.getId()))
						GPSMessageHandler.suppressDeviceGpsReport(info.getIana_id(), info.getSn());
					
					/* check black list firmware and apply icmg */
					if (RadioConfigUtils.getConfigTypeFromDevId(OnlineOrgId, devOnlineO.getDevice_id()) == JsonConf.CONFIG_TYPE.MAX)
					{
						if (log.isInfoEnabled())
							log.infof("fetch CONFIG_GET to dev %s for backup", devOnlineO.getSn());
						
						//ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_GET, info.getSid(), info.getIana_id(), info.getSn());
						if (!RadioConfigUtils.isBlacklistFirmware(devOnlineO.getFw_ver()))
						{		
							if (DeviceConfigScheduler.getDeviceConfigQueue() != null){
								
								if (devices.getConfigChecksum()!=null && !devices.getConfigChecksum().equalsIgnoreCase(devOnlineO.getConf_checksum()))
								{
									if (log.isDebugEnabled())
										log.debugf("DEVCONF20140424 - WtpMsgHandler - DeviceConfigScheduler.getDeviceConfigQueue().put(devConfigObj), check sum:%s,ianaId:%s,sn:%s",devOnlineO.getConf_checksum(), info.getIana_id(),info.getSn());
									DeviceConfigObj devConfigObj = new DeviceConfigObj();
									devConfigObj.setIana_id(info.getIana_id());
									devConfigObj.setSn(info.getSn());
								
									//PERFORMANCE
									DeviceConfigScheduler.getDeviceConfigQueue().put(devConfigObj);
								}
							}
							
//							ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_GET, JsonUtils.genServerRef(), info.getIana_id(), info.getSn());
						}
						else
						{
							log.warnf("dev %d %s fw_ver %s is blacklisted. skipped config get application (DEV_ONLINE).", devices.getIanaId(), devices.getSn(), devices.getFw_ver());
						}
						
						
						/* apply volatile webadmin icmg settings */
						// TODO ICMG
						if (log.isInfoEnabled())
							log.info("no config update is required, apply volatile webadmin icmg");
						
						if (devOnlineO.isOnline() && DateUtils.getUtcDate().getTime() - devices.getFirstAppear().getTime() > 60)
						{
							/* not apply icmg when first online (assume within 60s) */
							try {
								boolean bICMG_WLAN = false;
								boolean bICMG_MVPN = false;							
								
								//icmg_speed
								if (devices.getIcmg()!=null)
								{
									bICMG_WLAN = CommonConfigUtils.getDeviceIcmgWlan(devices.getIcmg());
									bICMG_MVPN = CommonConfigUtils.getDeviceIcmgMvpn(devices.getIcmg());
								}
								else
								{
									bICMG_MVPN = RadioConfigUtils.webadminShowIcmgMvpn(OnlineOrgId, devOnlineO.getNetwork_id(), devOnlineO.getDevice_id());
									
									try {
										JsonConf_RadioSettings radioConfig = RadioConfigUtils.getDatabaseRadioFullConfig(OnlineOrgId, 
												devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), true);
									
										bICMG_WLAN = radioConfig.isWebadminShowIcmg();
										
										RadioConfigUtils.sendWebadminIcmg(JsonUtils.genServerRef(), devOnlineO.getIana_id(),
												devOnlineO.getSn(), bICMG_MVPN, bICMG_WLAN, "dev online icmg apply for "+devOnlineO.getIana_id()+", "+devOnlineO.getSn(), false, null);
									
									} catch (FeatureNotFoundException e)
									{
										log.warnf("FeatureNotFoundException for dev %d %s", devOnlineO.getIana_id(), devOnlineO.getSn());
									}
								}							
							} catch (FeatureNotFoundException e) {
								log.warnf("Exception on DEV ONLINE ICMG apply FeatureNotFoundException - dev %s %d %s (%s)", devOnlineO.getOrganization_id(), devOnlineO.getIana_id(), devOnlineO.getSn(), e.getMessage());
							} catch (Exception e1) {
								log.error("Exception on DEV ONLINE ICMG apply", e1);
							}
						}	// endif (devOnlineO.isOnline() && DateUtils.getUtcDate().getTime() - devices.getFirstAppear().getTime() > 60)
					}	// endif (RadioConfigUtils.getConfigTypeFromDevId(OnlineOrgId, devOnlineO.getDevice_id()) == JsonConf.CONFIG_TYPE.MAX)
					
					//}
	

									
					NetworkEmailNotificationsHelper networkEmailNotificationsHelper = new NetworkEmailNotificationsHelper(network, devOnlineO.getOrganization_id());
					
					boolean isDeviceEmailEnabled = networkEmailNotificationsHelper.isDeviceEmailAlertEnabled();
//									boolean isWanEmailEnabled = networkEmailNotificationsHelper.isWanEmailAlertEnabled();
//									boolean isSpeedfusionEmailEnabled = networkEmailNotificationsHelper.isSpeedfusionEmailAlertEnabled();
					if (isDeviceEmailEnabled){
						AlertMgr alertMgr = new AlertMgr();
						SendImmediateAlertCriteria siaCriteria = new SendImmediateAlertCriteria();
						
						siaCriteria.setMsgId(EmailTemplateObjectDto.EMAIL_MSG_ID_DEV_ONLINE);
						siaCriteria.setMsgType(EmailTemplateObjectDto.EMAIL_MSG_TYPE_EMAIL);
						siaCriteria.setOrgId(devOnlineO.getOrganization_id());
						siaCriteria.setDevId(devOnlineO.getDevice_id());
//										siaCriteria.setLevel(alertedLevel); // online message should not set Level, it will loop deviceStatus table to notify online recipients according to offline alert
						siaCriteria.setDuration(0);
						siaCriteria.setWanName("");
						siaCriteria.setVpnName("");
						siaCriteria.setDatetime(DateUtils.getUtcDate());
						siaCriteria.setAlertType(SendImmediateAlertCriteria.ALERT_TYPE_DEVICE);
						
						alertMgr.sendOnlineImmediateAlert(siaCriteria);
						
//										alertUtils.sendImmediateAlert(siaCriteria);
						
//										alertUtils.SendImmediateAlert(EMAIL_MSG_ID.dev_online, EMAIL_MSG_TYPE.email, devOnlineO.getOrganization_id(), devOnlineO.getDevice_id(), alertedLevel, 0, "", "", false, true);
					}
	
					// if (!immediateUpdate && bFectchUsageHistory)
					if (bFectchUsageHistory)
					{
						if (log.isInfoEnabled())
							log.info("fetch client and device usage history");
						
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_USAGE_HIST, JsonUtils.genServerRef(), devices.getIanaId(), devices.getSn());
						// ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_USAGE_HIST,JsonUtils.genServerRef(),
						// devices.getIanaId(), devices.getSn());
					
					}					
					
					// check firmware shcedule
					try
					{						
						DeviceFirmwareSchedulesDAO devFwSchedulesDAO = new DeviceFirmwareSchedulesDAO();
						DeviceFirmwareSchedules fwSchedule = devFwSchedulesDAO.getUniqueSchedule(info.getIana_id(), info.getSn());
					
						if (fwSchedule != null)
						{
							if (devOnlineO.getFw_ver()==null)
							{
								log.warnf("Firmware version is null in online object for dev %d %s", devOnlineO.getIana_id(), devOnlineO.getSn());								
							}
							if (log.isDebugEnabled())
								log.debugf("FW00001 - fwSchedule for dev %d %s found! %s", info.getIana_id(), info.getSn(), fwSchedule);							
							if (fwSchedule.getFw_version() != null && devOnlineO.getFw_ver() !=null && fwSchedule.getFw_version().trim().equals(devOnlineO.getFw_ver().trim()))
							{
								if (log.isDebugEnabled())
									log.debugf("FW00001 - fwSchedule for dev %d %s found! %s", info.getIana_id(), info.getSn(), fwSchedule);
								fwSchedule.setStatus(1); /* completed */
								fwSchedule.setUpgrade_time(DateUtils.getUnixtime());
								devFwSchedulesDAO.saveOrUpdate(fwSchedule);
							}
							else
							{
								if (log.isDebugEnabled())
									log.debugf("FW00001 - fwSchedule fw_version not match for dev %d %s fwSchedule.getFw_version() [%s] devO.getFw_ver() [%s]", 
										info.getIana_id(), info.getSn(), fwSchedule.getFw_version(), devOnlineO.getFw_ver());								
//								fwSchedule.setUpgrade_time(DateUtils.getUnixtime());
//								devFwSchedulesDAO.saveOrUpdate(fwSchedule);
							}
	
							FirmwareScheduleObject fsO = new FirmwareScheduleObject();
							fsO = ACUtil.<FirmwareScheduleObject> getPoolObjectBySn(fsO, FirmwareScheduleObject.class);
							
							if (log.isDebugEnabled())
								log.debugf("FW00001 - fsO=%s", fsO);	
							if (fsO != null)
							{
								if (log.isDebugEnabled())
									log.debugf("FW00001 - remove schedule %d", fwSchedule.getId());	
								fsO.remove(fwSchedule.getId());
								ACUtil.<FirmwareScheduleObject> cachePoolObjectBySn(fsO, FirmwareScheduleObject.class);
								
							}
	
						}
						else
						{
							if (log.isDebugEnabled())
								log.debugf("FW00001 - fwSchedule for dev %d %s not found!", info.getIana_id(), info.getSn());
						}
	
					} catch (Exception e)
					{
						log.error("Exception DeviceFirmwareSchedules ", e);
					}
					
					try
					{
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_SYSINFO, JsonUtils.genServerRef(), info.getIana_id(), info.getSn());
					}
					catch( Exception e )
					{
						log.error("Fetch system info error -", e);
					}
					
					try
					{
						StationListObject stationListObj = new StationListObject();
						stationListObj.setIana_id(info.getIana_id());
						stationListObj.setSn(info.getSn());
						stationListObj = ACUtil.getPoolObjectBySn(stationListObj, StationListObject.class);
						
						if( stationListObj == null )
						{
							ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_LIST, JsonUtils.genServerRef(), info.getIana_id(), info.getSn());
						}
					}
					catch( Exception e )
					{
						log.error("Fetch station list error -", e);
					}
				
				}
				break;
			case PIPE_INFO_TYPE_DEV_OFFLINE:
				if (log.isDebugEnabled())
					log.debugf("AL10001 - DEV_OFFLINE event is received");
				
				if (!isOnlineStatusAndCustomEventHandlerSuccess)
				{
					if (log.isInfoEnabled()) log.infof("OnlineStatusAndCustomEventHandler failed for info %s", info);
				}
				
				if (devOnlineO==null)
				{
					log.warnf("INFO Received device offline without online object for info %s", info);
					break;
				}
				
				String curMachineId = ACService.getServerName();
				if (!devOnlineO.getMachine_id().equals(curMachineId)) {
					// Online object is from ac1, but this offline is from ac2
					if (log.isDebugEnabled())
						log.debug("Dev Offline event is from old AC, machineID now: " + curMachineId + " old: " + devOnlineO.getMachine_id());
					break;
				}
				Networks network = null;
				try {
//					DevicesDAO devicesDAO = new DevicesDAO(devOnlineO.getOrganization_id());
//					devices = devicesDAO.findBySn(info.getIana_id(), info.getSn());
					NetworksDAO networksDAO = new NetworksDAO(devOnlineO.getOrganization_id());
					network = networksDAO.findById(devOnlineO.getNetwork_id());

//					if (devOnlineO != null) {
//						log.debugf("LT10001 PIPE_INFO_TYPE_DEV_OFFLINE - Test Utc time dev: %s vs DateUtils.getUTCTime()", devOnlineO.getSn(), DateUtils.getUtcDate().getTime());
//					}

					OnlineStatusAndCustomEventHandler.purgeCacheToDbUponOffline(devOnlineO);

					DeviceStatusDAO deviceStatusDAO = new DeviceStatusDAO(devOnlineO.getOrganization_id());
					NetworkEmailNotificationsDAO networkEmailNotificationsDAO = new NetworkEmailNotificationsDAO(devOnlineO.getOrganization_id());
					DeviceStatus deviceStatus = deviceStatusDAO.findById(devOnlineO.getDevice_id());
					if (deviceStatus == null) {
						if (log.isDebugEnabled())
							log.debugf("AL10001 - deviceStatus is null");
						
						// if deviceStatus is not null, that is, the device at least alerted once,
						// no need to check if offline threshold is set to zero or not
						if (network.isEmailNotificationEnabled()) {
							NetworkEmailNotifications nen = networkEmailNotificationsDAO.getLevelContactForNetwork(devOnlineO.getNetwork_id(), "1");
							if (nen != null && nen.getOfflineMin() == 0) {
								if (log.isDebugEnabled())
									log.debugf("AL10001 - immediate alert with 0 min set.");
								
								// send alert immediately
								deviceStatus = new DeviceStatus();
								// deviceStatus.setDevices(devices);
//								deviceStatus.setDeviceId(devices.getId());
								deviceStatus.setDeviceId(devOnlineO.getDevice_id());
								deviceStatus.setAlertedLevel(1);
								deviceStatus.setTimestamp(new Date());
								deviceStatusDAO.save(deviceStatus);

								isImmediateAlertNeeded = true;
								if (log.isInfoEnabled())
									log.info("AL10001 - Add offline alert here 1 " + nen.getOfflineMin() + " isImmediateSend " + isImmediateAlertNeeded);
							}
							else
							{
								if (log.isDebugEnabled())
									log.debugf("AL10001 - no immediate alert is required.");
							}
						}
					}
					else
					{
						if (log.isDebugEnabled())
							log.debugf("AL10001 - deviceStatus = %s", deviceStatus);
					}
				} catch (Exception e) {
					log.error("AL10001 - Exception ", e);
				}

				//ACUtil.<DevOfflineObject>cacheDataBySn(info,DevOfflineObject.class);
				if (isImmediateAlertNeeded) {
//					AlertUtils alertUtils = new AlertUtils();
					AlertMgr alertMgr = new AlertMgr();
					if (network != null){
						NetworkEmailNotificationsHelper eventLogMessageHelper = new NetworkEmailNotificationsHelper(network, devOnlineO.getOrganization_id());
						
						boolean isDeviceEmailEnabled = eventLogMessageHelper.isDeviceEmailAlertEnabled();
//						boolean isWanEmailEnabled = eventLogMessageHelper.isWanEmailAlertEnabled();
//						boolean isSpeedfusionEmailEnabled = eventLogMessageHelper.isSpeedfusionEmailAlertEnabled();
						
						if (isDeviceEmailEnabled){
							/* not raise level to prevent concurrent update from CheckOfflineAlert */
							SendImmediateAlertCriteria siaCriteria = new SendImmediateAlertCriteria();
							
							siaCriteria.setMsgId(EmailTemplateObjectDto.EMAIL_MSG_ID_DEV_OFFLINE);
							siaCriteria.setMsgType(EmailTemplateObjectDto.EMAIL_MSG_TYPE_EMAIL);
							siaCriteria.setOrgId(devOnlineO.getOrganization_id());
							siaCriteria.setDevId(devOnlineO.getDevice_id());
							siaCriteria.setLevel(1);
							siaCriteria.setDuration(0);
							siaCriteria.setWanName("");
							siaCriteria.setVpnName("");
							siaCriteria.setDatetime(DateUtils.getUtcDate());
							siaCriteria.setAlertType(SendImmediateAlertCriteria.ALERT_TYPE_DEVICE);
							alertMgr.sendImmediateAlert(siaCriteria);
							
//							alertUtils.sendImmediateAlert(siaCriteria);
//							alertUtils.SendImmediateAlert(EMAIL_MSG_ID.dev_offline, EMAIL_MSG_TYPE.email, devOnlineO.getOrganization_id(), devOnlineO.getDevice_id(), 1, 0, "", "", false, false);
							if (log.isInfoEnabled())
								log.info("ALERT201408211034 - Add offline alert here 2 " + devOnlineO.getSn());
						}
					}
				}
				break;
			case PIPE_INFO_TYPE_EVENT_LOG:
				if (status == 200 || status == 0) {
					QueueControl.put(devOnlineO.getOrganization_id(), json);
				}
				break;
			case PIPE_INFO_TYPE_DIAG_REPORT_PLB:
				try {
					if (log.isDebugEnabled()){
						log.debugf("DIAGRPT20141104 - WtpMsgHandler - PIPE_INFO_TYPE_DIAG_REPORT_PLB, info:%s" , info);
					}
					object = JSONObject.fromObject(info);
					if (log.isDebugEnabled()){
						log.debugf("DIAGRPT20141104 - WtpMsgHandler - PIPE_INFO_TYPE_DIAG_REPORT_PLB, object:%s" , object);
					}
					if (status == 200 || status == 0) {
						data = object.getJSONObject(DATA_TAG);
						Json_DiagReport jsonDiagReport = gson.fromJson(data.toString(), Json_DiagReport.class);
						if (jsonDiagReport != null){
							if (log.isDebugEnabled()){
								log.debugf("DIAGRPT20141104 - WtpMsgHandler - PIPE_INFO_TYPE_DIAG_REPORT_PLB, jsonDiagReport:%s" , jsonDiagReport);	
							}
							boolean isDone = DiagnosticReportMessageHandler.doDiagnosticReportMessage(devOnlineO,jsonDiagReport);
							
							if (log.isDebugEnabled()){
								log.debugf("DIAGRPT20141104 - WtpMsgHandler - PIPE_INFO_TYPE_DIAG_REPORT_PLB, jsonDiagReport:%s, isDone:%s" , jsonDiagReport, isDone);
							}
						} else {
							log.warnf("DIAGRPT20141104 - WtpMsgHandler - PIPE_INFO_TYPE_DIAG_REPORT_PLB, jsonDiagReport is null !!!, jsonDiagReport:%s" , jsonDiagReport);	
						}
					}
				} catch (net.sf.json.JSONException e) {
					log.error("DIAGRPT20141104 - PIPE_INFO_TYPE_DIAG_REPORT_PLB cannot get data tag", e);
				}
				break;
			case PIPE_INFO_AC_LICENSE:
			{
				object = JSONObject.fromObject(info);
				data = object.getJSONObject(DATA_TAG);
				ACLicenseObject acLicObj = gson.fromJson(data.toString(), ACLicenseObject.class);
				if (log.isDebugEnabled())
					log.debugf("acLicObj = %s", acLicObj);
				if (acLicObj == null) {
					log.warn("ACLicenseObject is null "+info);
					break;
				}				
				ACLicenseManager aclm = ACLicenseManager.getInstance();
				acLicObj = aclm.decryptACLicenseObject(acLicObj);
				
				System.out.println("======acLicObj="+acLicObj);
				
				acLicObj.setServer(ACService.getServerName());
				ACUtil.cachePoolObjectBySn(acLicObj, ACLicenseObject.class);
				
				ACLicenseManager.setLicenseModeActivated(true);
				break;
			}
			case PIPE_INFO_TYPE_DEV_DETAIL:
				if (status!=200)
					break;
				
				String orgId1 = null;
				Integer networkId1 = null;
				if (devOnlineO != null){
					orgId1 = devOnlineO.getOrganization_id();
					networkId1 = devOnlineO.getNetwork_id();
					
				}
				data = null; // init data
				if (log.isDebugEnabled())
					log.debugf("WtpMsgHandler: PIPE_INFO_TYPE_DEV_DETAIL: sn: %s orgId: %s", devOnlineO.getSn(), devOnlineO.getOrganization_id());
				
				object = JSONObject.fromObject(info);
//				logger.debugf("WtpMsgHandler: PIPE_INFO_TYPE_DEV_DETAIL: object is null? %s sn: %s orgId: %s",(object == null), devOnlineO.getSn(), devOnlineO.getOrganization_id());
				try{
					data = object.getJSONObject(DATA_TAG);
				} catch (Exception e){
					log.warnf("WtpmsgHandler.run() - PIPE_INFO_TYPE_DEV_DETAIL, json data not in correct format! json: %s", json);
				}
				
				if (data != null){
				
					Devices devices1 = null;
					try {
	//					DevicesDAO devicesDAO1 = new DevicesDAO(orgId1);
						if (orgId1 != null && networkId1 != null){
							//devices1 = NetUtils.getDevices(orgId1, devOnlineO.getNetwork_id(), info.getIana_id(), info.getSn(), true);
							devices1 = NetUtils.getDevicesBySn(orgId1, info.getIana_id(), info.getSn(), true);
						}
	//					devices1 = devicesDAO1.findBySn(info.getIana_id(), info.getSn());
					} catch (Exception e) {
						log.error("DevicesDAO exception:", e);
					}
					boolean isOldXMLFormat = false;
	
					
					try {
						if (data.get("interfaces") != null){
							isOldXMLFormat = false;
							if (devices1 != null) {
//								logger.info("newJsonFormat - devDetailObject devId:" + devices1.getId() + ", orgId:" + orgId1);
							} else {
								if (log.isInfoEnabled())
									log.info("devices is null!");
							}
						} else {
							isOldXMLFormat = true;
							if (devices1 != null) {
								// logger.info("oldXmlFormat - devDetailObject devId:" + devices1.getId() + ", orgId:" +
								// orgId1);
							} else {
								if (log.isInfoEnabled())
									log.info("devices is null!");
							}
						}
					} catch (Exception e){
//						logger.debug("WtpMsgHandler - PIPE_INFO_TYPE_DEV_DETAIL - data.get(\"interfaces\") - no interface");
						isOldXMLFormat = true;
						if (devices1 != null) {
							// logger.info("oldXmlFormat - devDetailObject devId:" + devices1.getId() + ", orgId:" +
							// orgId1);
						} else {
							if (log.isInfoEnabled())
								log.info("devices is null!");
						}
					}
					
//					if (data != null && data.get("interfaces") != null) {
//						isOldXMLFormat = false;
//						if (devices1 != null) {
//							logger.info("newJsonFormat - devDetailObject devId:" + devices1.getId() + ", orgId:" + orgId1);
//						} else {
//							logger.info("devices is null!");
//						}
//					} else {
//						isOldXMLFormat = true;
//						if (devices1 != null) {
//							// logger.info("oldXmlFormat - devDetailObject devId:" + devices1.getId() + ", orgId:" +
//							// orgId1);
//						} else {
//							logger.info("devices is null!");
//						}
//					}
//	
					if (devOnlineO.isDevDetailJson() != !isOldXMLFormat) {
						devOnlineO.setDevDetailJson(!isOldXMLFormat);
						ACUtil.<DevOnlineObject> cachePoolObjectBySn(devOnlineO, DevOnlineObject.class);
					}
	
					if (isOldXMLFormat) { // old wan_xml format
						// logger.info("oldXmlFormat - devId:" + devices1.getId() + ", orgId:" + orgId1 +
						// ", data.toString(): " + data.toString());
	
						DevDetailObject devDetailObject = gson.fromJson(data.toString(), DevDetailObject.class);
						if (log.isDebugEnabled())
							log.debug("devDetailObject before = " + devDetailObject);
	
						if (devDetailObject == null) {
							log.warn("devDetailObject is null for " + info.getSn() + " status: " + info.getStatus());
							break;
						}
						if (devDetailObject.getXml_wan_list() == null || devDetailObject.getXml_wan_list().getXml() == null) {
							log.warn("Wan info (XML) is null for " + info.getSn() + " status: " + info.getStatus());
							break;
						} else {
							// logger.debugf("XML20140127 - oldXmlFormat - devId: %s, orgId: %s, xmlWanList.getXml(): %s",
							// devices1.getId(), orgId1, devDetailObject.getXml_wan_list().getXml());
						}
	
						if (devDetailObject.getXml_wan_list() != null && devDetailObject.getXml_wan_list().getXml() != null){
							String xml = devDetailObject.getXml_wan_list().getXml();
							if (xml != null && !xml.isEmpty()){
								xml = HtmlUtils.unescapeHtml(xml);
							}
							devDetailObject.fillWanListByXml(xml);
						}
						
						devDetailObject.setSn(info.getSn());
						devDetailObject.setIana_id(info.getIana_id());
						devDetailObject.setSid(info.getSid());
						// logger.debug("devDetailObject after = " + devDetailObject);
						ACUtil.<DevDetailObject> cachePoolObjectBySn(devDetailObject, DevDetailObject.class);
					} else { // interfaces for new json format
						if (log.isInfoEnabled())
							log.info("newJsonFormat - devId:" + devices1.getId() + ", orgId:" + orgId1 + "data.toString(): " + data.toString());
						DevDetailJsonObject devDetailJsonObject = null;
						try {
							devDetailJsonObject = gson.fromJson(data.toString(), DevDetailJsonObject.class);
							if (log.isDebugEnabled())
								log.debug("DevDetailJsonObject before = " + devDetailJsonObject);
						} catch (Exception e) {
							log.error("newJsonFormat - devId:" + devices1.getId() + ", orgId:" + orgId1 + "data.toString(): " + data.toString(), e);
						}
	
						if (devDetailJsonObject == null) {
							log.warn("devDetailJsonObject is null for " + info.getSn() + " status: " + info.getStatus());
							break;
						}
	
						devDetailJsonObject.setSn(info.getSn());
						devDetailJsonObject.setIana_id(info.getIana_id());
						devDetailJsonObject.setSid(info.getSid());
						// logger.debug("devDetailObject after = " + devDetailJsonObject);
						ACUtil.<DevDetailJsonObject> cachePoolObjectBySn(devDetailJsonObject, DevDetailJsonObject.class);
					}
				} // end if (data != null)
				break;
			case PIPE_INFO_TYPE_DEV_BANDWIDTH:				
				if (status!=200)
					break;
				
				object = JSONObject.fromObject(info);
				data = object.getJSONObject(DATA_TAG);
				DevBandwidthObject devBandwidthObject = gson.fromJson(data.toString(), DevBandwidthObject.class);
				if (devBandwidthObject == null) {
					log.warn("device bandwidth usage is null for " + info.getSn() + " status: " + info.getStatus());
					break;
				}
				
				devBandwidthObject.setSn(info.getSn());
				devBandwidthObject.setIana_id(info.getIana_id());
				devBandwidthObject.setSid(info.getSid());				
				devBandwidthObject.setFromWTPOrNot(true);
//				devBandwidthObject.setLastUpdateTime(new Date().getTime());
				ACUtil.<DevBandwidthObject> cachePoolObjectBySn(devBandwidthObject, DevBandwidthObject.class);
				// ACUtil.<DevBandwidthObject>cacheDataBySn(info,DevBandwidthObject.class);
				break;
			case PIPE_INFO_TYPE_DEV_USAGE:
				if (status == 200 || status == 0) {
					boolean isSkip = false;					
					if (devOnlineO.getLastUpdateTime_DevUsage()!=null)
					{
						/* check time range */
						long lastrefresh = devOnlineO.getLastUpdateTime_DevUsage().getTime()/1000;
						if (DateUtils.getUnixtime() - lastrefresh <= DEV_USAGE_REPORT_INTERVAL_SECOND)
						{
							if (log.isDebugEnabled())
								log.debugf("PIPE_INFO_TYPE_DEV_USAGE sn %s skip within last update time range %d , offset %d", 
									devOnlineO.getSn(),
									lastrefresh,
									DateUtils.getUnixtime() - lastrefresh);
							isSkip = true;	
						}
						else
						{
							/* update last update time if proceed */
							devOnlineO.setLastUpdateTime_DevUsage(DateUtils.getUtcDate());
							ACUtil.<DevOnlineObject> cachePoolObjectBySn(devOnlineO, DevOnlineObject.class);
						}
					}
					else
					{
						/* update last update time if proceed */
						devOnlineO.setLastUpdateTime_DevUsage(DateUtils.getUtcDate());
						ACUtil.<DevOnlineObject> cachePoolObjectBySn(devOnlineO, DevOnlineObject.class);
					}
					
					if (!isSkip)
						QueueControl.put(devOnlineO.getOrganization_id(), json);
				}
				break;
			case PIPE_INFO_TYPE_DEV_USAGE_HIST:
				if (status == 200 || status == 0)
				{
					if (log.isInfoEnabled())
						log.info("Fetch DEV_USAGES_HIST : " + devOnlineO.getOrganization_id() + " , " + json);
					QueueControl.put(devOnlineO.getOrganization_id(), json);
				}
				break;
			case PIPE_INFO_TYPE_STATION_USAGE_HIST:
				if (status == 200 || status == 0)
				{
					QueueControl.put(devOnlineO.getOrganization_id(), json);
				}
				break;
			case PIPE_INFO_TYPE_DEV_LOCATIONS:
				if (status == 200 || status == 0)
				{	
					if (GPSMessageHandler.dropDeviceGpsReport(info.getIana_id(), info.getSn(), OnlineOrgId))
					{	
						/* drop and suppress already */
						break;
					}
					else
					{
						String sid = info.getSid();
						if( sid != null && !sid.isEmpty() )
						{
							boolean suc = LocationUtils.cacheLocationList(json, info);
							if( !suc )
								log.warn("Cache realtime location of device " + info.getSn() + ", with json " + json + " " + suc);
						}
						else
						{
							QueueControl.put(devOnlineO.getOrganization_id(), json);
						}
					}
				}
				break;
			case PIPE_INFO_TYPE_DEV_SSID_USAGES:
				if (status == 200 || status == 0) {
					QueueControl.put(devOnlineO.getOrganization_id(), json);
				}
				break;
			case PIPE_INFO_TYPE_DEV_CHANNEL_UTIL:
				if (status == 200 || status == 0) {
					QueueControl.put(devOnlineO.getOrganization_id(), json);
				}
				break;
			case PIPE_INFO_TYPE_DEV_WLAN:
				break;
			case PIPE_INFO_TYPE_DEV_TCPDUMP:
				break;
			case PIPE_INFO_TYPE_STATION_LIST:
//				log.info("case PIPE_INFO_TYPE_STATION_LIST");
				if (status == 200 || status == 0) {
					QueueCacheControl.put(devOnlineO.getOrganization_id(), json);
					if (log.isDebugEnabled())
						log.debugf("STAT20140317 - WtpMsgHandler.run() - PIPE_INFO_TYPE_STATION_LIST, json: %s",  json); 
					/*
					 * object = JSONObject.fromObject(info);
					 * data = object.getJSONObject(DATA_TAG);
					 * Json_StationList stationLstJson = gson.fromJson(data.toString(), Json_StationList.class);
					 * if (stationLstJson == null) {
					 * logger.warn("station list Json is null for " + info.getSn() + " status: " + info.getStatus());
					 * break;
					 * }
					 * 
					 * //not status for latest firmware
					 * if (stationLstJson.getStatus() != 200) {
					 * logger.warn("Station list inside status for " + info.getSn() + " status: " + info.getStatus());
					 * break;
					 * }
					 * 
					 * StationListObject stationLst = StationListObject.convertStationLst(stationLstJson);
					 * if (stationLst != null) {
					 * stationLst.setSn(info.getSn());
					 * stationLst.setIana_id(info.getIana_id());
					 * stationLst.setSid(info.getSid());
					 * logger.debug("stationLst = " + stationLst);
					 * // ACUtil.<StationListObject>cacheDataBySn(info,StationListObject.class);
					 * ACUtil.cachePoolObjectBySn(stationLst, StationListObject.class);
					 * } else {
					 * logger.warn("station list is null for " + info.getSn() + " status: " + info.getStatus());
					 * }
					 */
				} else {
					if (log.isInfoEnabled())
						log.infof("PIPE_INFO_TYPE_STATION_LIST for " + info.getSn() + " status: " + info.getStatus());
				}
				break;
			case PIPE_INFO_TYPE_STATION_LIST_DELTA:
				if (status == 200 || status == 0) {
					QueueCacheControl.put(devOnlineO.getOrganization_id(), json);
				}
				break;
			case PIPE_INFO_TYPE_STATION_BANDWIDTH_LIST:
				object = JSONObject.fromObject(info);
				data = object.getJSONObject(DATA_TAG);
				StationBandwidthListObject stationBandwidthListObject = gson.fromJson(data.toString(), StationBandwidthListObject.class);
				if (stationBandwidthListObject == null) {
					if( info.getStatus() != 418 )
						log.warn("station bandwidth list is null for " + info.getSn() + " status: " + info.getStatus());
					break;
				}
				stationBandwidthListObject.setSid(info.getSid());
				stationBandwidthListObject.setSn(info.getSn());
				stationBandwidthListObject.setIana_id(info.getIana_id());
				List<StationStatusList> statusLst = stationBandwidthListObject.getStation_status_list();
				if (statusLst != null)
				{
					CopyOnWriteArrayList<StationStatusList> newStatusLst = new CopyOnWriteArrayList<StationStatusList>();
					StationStatusList newStatus = null;
					for (StationStatusList statusItem : statusLst)
					{
						newStatus = stationBandwidthListObject.new StationStatusList();
						newStatus.setClient_id(PoolObjectDAO.convertToClientId(statusItem.getMac(), statusItem.getIp()));
						newStatus.setDrx(statusItem.getDrx());
						newStatus.setDtx(statusItem.getDtx());
						newStatus.setIp(statusItem.getIp());
						newStatus.setMac(statusItem.getMac());
						newStatus.setRssi(statusItem.getRssi());
						newStatus.setTimestamp(statusItem.getTimestamp());
						newStatusLst.add(newStatus);
						if(log.isInfoEnabled())
							log.info("added bw status: "+newStatus);
					}
					stationBandwidthListObject.setStation_status_list(newStatusLst);
				}
				ACUtil.<StationBandwidthListObject> cachePoolObjectBySn(stationBandwidthListObject, StationBandwidthListObject.class);
				break;
			case PIPE_INFO_TYPE_STATION_USAGE:
				if (status == 200 || status == 0) 
				{
					QueueCacheControl.put(devOnlineO.getOrganization_id(), json);
				}
				break;
			case PIPE_INFO_TYPE_PEPVPN_ENDPOINT:
				if (log.isDebugEnabled())
					log.debugf("pepvpn status %d is received for dev %d %s", info.getStatus(), info.getIana_id(), info.getSn());
				if (status == 200)
				{
					PepvpnEndpointObject pepvpnEndpointObject;
					object = JSONObject.fromObject(info);
					data = object.getJSONObject(DATA_TAG);
					try
					{
						pepvpnEndpointObject = gson.fromJson(data.toString(), PepvpnEndpointObject.class);
					} catch (Exception e ){//JsonSyntaxException | NumberFormat
						//can only catch gson exception, cann't catch JsonUtils
						log.infof("ENDPOINT INVALID INPUT --%s --%s", info, e);
						break;
					}
					pepvpnEndpointObject.setSid(info.getSid());
					pepvpnEndpointObject.setSn(info.getSn());
					pepvpnEndpointObject.setIana_id(info.getIana_id());
					pepvpnEndpointObject.setStatus(info.getStatus());
					pepvpnEndpointObject.setTimestamp(info.getTimestamp());
					pepvpnEndpointObject.setFetchStartTime(DateUtils.getUnixtime());
					if (log.isDebugEnabled())
						log.debugf("pepvpn status is cached", info.getStatus());
					ACUtil.cachePoolObjectBySn(pepvpnEndpointObject, PepvpnEndpointObject.class);
					log.debugf("case PIPE_INFO_TYPE_PEPVPN_ENDPOINT: pepvpnEndpointObject= %s", pepvpnEndpointObject);
				
					/* Convert ENDPOINT to New PEPVPN: PeerDetail, TunnelStat */
					String convertstatus = PepvpnStatusUtils.convertPepvpnEndpointV2(pepvpnEndpointObject);/* ***** Add for PepvpnEndpointV2 ***** */
					log.debugf("case PIPE_INFO_TYPE_PEPVPN_ENDPOINT: pepvpnPeerDetailObject convert= %s", convertstatus);
				}
				break;			
			case PIPE_INFO_TYPE_PEPVPN_PEER_LIST:/* *** Add for PepvpnEndpointV2: getPeerList *** */				
				log.debugf("case PIPE_INFO_TYPE_PEPVPN_PEER_LIST: pepvpn peer_list status %d is received for dev %d %s", info.getStatus(), info.getIana_id(), info.getSn());
				if (status == 200)
				{
					object = JSONObject.fromObject(info);
					data = object.getJSONObject(DATA_TAG);
					
					PepvpnPeerListObject pepvpnPeerListObject = JsonUtils.fromJson(data.toString(), PepvpnPeerListObject.class);
					pepvpnPeerListObject.setSid(info.getSid());
					pepvpnPeerListObject.setSn(info.getSn());
					pepvpnPeerListObject.setIana_id(info.getIana_id());
					pepvpnPeerListObject.setStatus(info.getStatus());
					pepvpnPeerListObject.setTimestamp(info.getTimestamp());
					ACUtil.cachePoolObjectBySn(pepvpnPeerListObject, PepvpnPeerListObject.class);
					log.debugf("case PIPE_INFO_TYPE_PEPVPN_PEER_LIST: cache pepvpnPeerListObject= %s", pepvpnPeerListObject);			
				}
				break;
			case PIPE_INFO_TYPE_PEPVPN_PEER_UPDATE:/* *** Add for PepvpnEndpointV2: updatePeerList *** */				
				log.debugf("case PIPE_INFO_TYPE_PEPVPN_PEER_UPDATE: pepvpn peer_list status %d is received for dev %d %s", info.getStatus(), info.getIana_id(), info.getSn());
				if (status == 200)
				{
					object = JSONObject.fromObject(info);
				 if (object.has(DATA_TAG))
				 {
					data = object.getJSONObject(DATA_TAG);
					
					PepvpnPeerListObject pepvpnPeerListObject = JsonUtils.fromJson(data.toString(), PepvpnPeerListObject.class);
					if (pepvpnPeerListObject != null && "ok".equalsIgnoreCase(pepvpnPeerListObject.getStat())) 
					{
						pepvpnPeerListObject.setSn(info.getSn());
						pepvpnPeerListObject.setIana_id(info.getIana_id());
						pepvpnPeerListObject.setStatus(info.getStatus());
						PepvpnPeerListObject newPepvpnPeerListObject = PepvpnStatusUtils.updatePepvpnPeerListInfo(pepvpnPeerListObject);
						if (newPepvpnPeerListObject != null)
						{
							ACUtil.cachePoolObjectBySn(newPepvpnPeerListObject, PepvpnPeerListObject.class);}
						log.debugf("case PIPE_INFO_TYPE_PEPVPN_PEER_UPDATE: to cache updatedPepvpnPeerListObject= %s", newPepvpnPeerListObject);			
					} else {
						if (log.isInfoEnabled())
							log.infof("PIPE_INFO_TYPE_PEPVPN_PEER_UPDATE %s failed for dev %d %s", info, info.getIana_id(), info.getSn());
					}
				 } else {
					log.errorf("info %s has no %s tag", info, DATA_TAG);
				}
				}
				break;
			case PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL:/* *** Add for PepvpnEndpointV2: getPeerDetail *** */								
				log.debugf("case PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL: pepvpn peer_detail status %d is received for dev %d %s", info.getStatus(), info.getIana_id(), info.getSn());
				if (status == 200)
				{
					object = JSONObject.fromObject(info);
					PepvpnPeerDetailObject pepvpnPeerDetailObject;
					if (object.has(DATA_TAG))
					{
						data = object.getJSONObject(DATA_TAG);
						try
						{
							pepvpnPeerDetailObject = gson.fromJson(data.toString(), PepvpnPeerDetailObject.class);
						} catch (Exception e ){
							log.infof("PEPVPN_PEER_DETAIL INVALID INPUT --%s --%s", info, e);
							break;
						}

						if (pepvpnPeerDetailObject != null && "ok".equalsIgnoreCase(pepvpnPeerDetailObject.getStat())) 
						{							
							pepvpnPeerDetailObject.setSid(info.getSid());
							pepvpnPeerDetailObject.setSn(info.getSn());
							pepvpnPeerDetailObject.setIana_id(info.getIana_id());
							pepvpnPeerDetailObject.setStatus(info.getStatus());				
							pepvpnPeerDetailObject.setTimestamp(info.getTimestamp());	
							pepvpnPeerDetailObject.setFetchStartTime(DateUtils.getUnixtime());
							ACUtil.cachePoolObjectBySn(pepvpnPeerDetailObject, PepvpnPeerDetailObject.class);	
							log.debugf("case PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL: cache pepvpnPeerDetailObject= %s", pepvpnPeerDetailObject);
						}
						else
						{
							if (log.isInfoEnabled())
								log.infof("PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL %s failed for dev %d %s", info, info.getIana_id(), info.getSn());
						}
					}
					else
					{
						log.errorf("info %s has no %s tag", info, DATA_TAG);
					}
				}
				break;
			case PIPE_INFO_TYPE_PEPVPN_TUNNEL_STAT:/* *** Add for PepvpnEndpointV2: getTunnelStat *** */				
				log.debugf("case PIPE_INFO_TYPE_PEPVPN_TUNNEL_STAT: pepvpn tunnel_stat status %d is received for dev %d %s", info.getStatus(), info.getIana_id(), info.getSn());
				if (status == 200)
				{
					object = JSONObject.fromObject(info);
				if (object.has(DATA_TAG))
				{
					data = object.getJSONObject(DATA_TAG);
					String formated_data = null;
					ArrayList<String> order = new ArrayList<String>();
					formated_data = JsonMatcherUtils.JsonMatcherRemove(data.toString(), ",\"order\":", "]"); /* remove order list from JsonObject */
					log.debugf("case PIPE_INFO_TYPE_PEPVPN_TUNNEL_STAT: JsonMatcherUtils sn = %s order = %s formated_data = %s", info.getSn(),order, formated_data);
					
					PepvpnTunnelStatObject pepvpnTunnelStatObject;
					try
					{
						pepvpnTunnelStatObject = JsonUtils.fromJson(formated_data, PepvpnTunnelStatObject.class);
					} catch (Exception e ){
						log.infof("PEPVPN_TUNNEL_STAT INVALID INPUT --%s --%s", info, e);
						break;
					}
					if (pepvpnTunnelStatObject != null && "ok".equalsIgnoreCase(pepvpnTunnelStatObject.getStat())) {
						pepvpnTunnelStatObject.setSid(info.getSid());
						pepvpnTunnelStatObject.setSn(info.getSn());
						pepvpnTunnelStatObject.setIana_id(info.getIana_id());
						pepvpnTunnelStatObject.setStatus(info.getStatus());
						pepvpnTunnelStatObject.setTimestamp(info.getTimestamp());
						pepvpnTunnelStatObject.setFetchStartTime(DateUtils.getUnixtime());
						if (pepvpnTunnelStatObject.getResponse()!=null)
							pepvpnTunnelStatObject.setTunnel_order(pepvpnTunnelStatObject.getResponse().getTunnel_order());
						ACUtil.cachePoolObjectBySn(pepvpnTunnelStatObject, PepvpnTunnelStatObject.class);
					}
					log.debugf("case PIPE_INFO_TYPE_PEPVPN_TUNNEL_STAT: cache pepvpnTunnelStatObject= %s", pepvpnTunnelStatObject);											
			     }
				}
				break;
			case PIPE_INFO_TYPE_CONFIG_BACKUP_TEXT:
			case PIPE_INFO_TYPE_CONFIG_BACKUP:
				object = JSONObject.fromObject(info);
				/*** OVERWRITE CONFIG FROM IC2 IF NOT GROUP MASTER MODE ***/
				data = new JSONObject();
				if (info.getSid() == null || info.getSid().isEmpty())
				{
					try {
						String jsonString = object.toString();
						if (jsonString != null && jsonString.length() > 0){
							if (jsonString.indexOf(DATA_TAG) != -1){
								data = object.getJSONObject(DATA_TAG);
								if (data == null){
									log.warnf("L20140122 - no data tag, data is null, sn: %s", info.getSn());
									break;
								}
							} else {
								log.warn("L20140122 - BACKUP_TEXT: TAG is not found in type, sn: " + info.getSn());
								break;
							}
						}
					} catch (Exception e) {
						log.warn("L20140122 - BACKUP_TEXT: TAG is not found in type, sn: " + info.getSn());
						break;
					}

					ConfigBackupTextObject backupObject = gson.fromJson(data.toString(), ConfigBackupTextObject.class);
					if (log.isDebugEnabled())
						log.debugf("L20140122 - backupObject: %s", backupObject);
				
					/*** FOR CONFIG BACKUP ***/
					if (info.getSn() != null){
						String orgId = devOnlineO.getOrganization_id();					
						devices = NetUtils.getDevices(orgId, info.getIana_id(), info.getSn());
						String productType = null;
						if (devices != null){
							productType = ProductUtils.getDeviceType(orgId, devices.getId());
						} else {
							log.warnf("L20140122 - devices is null, backupObject: %s", backupObject);
						}
						if (productType != null && !productType.equalsIgnoreCase("ap")){
							/* get archive for backup */
							//TODO:Text config backup is not supported yet
							if (backupObject.getContent() == null){
								ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_GET, info.getSid(), info.getIana_id(), info.getSn());
							} else {
								if (backupObject.getConf_checksum() != null && !backupObject.getConf_checksum().isEmpty()){
									String configGetObjectMd5 = backupObject.getConf_checksum();
									boolean backupResult = ConfigBackupUtils.backupDeviceConfigByContent(configGetObjectMd5, backupObject.getContent(), orgId, devices.getId());
									if (!backupResult) {
										if (log.isDebugEnabled())
											log.debug("L20140122 - file not backup. please search the details log from backupDeviceConfig()");
									}
								} else {
									log.warnf("L20140122 - message contain no conf_checksum, iana: %s, sn: %s", info.getIana_id(), info.getSn());
								}
							}
						} else {
							log.warnf("L20140122 - productType is null, backupObject: %s", backupObject);
						}
					}
					if (log.isDebugEnabled())
						log.debug("L20140122 - BACKUP_TEXT: - devices (id:" + devices.getId() + ") - json=" + data.toString());
					
					boolean mastermode = false;
					try {
						NetworksDAO netDAO = new NetworksDAO(devOnlineO.getOrganization_id());
						Networks networks = netDAO.findById(devOnlineO.getNetwork_id());
						if (networks == null)
							log.warnf("L20140122 - BACKUP_TEXT: network %d is not found for dev %s", devOnlineO.getNetwork_id(), devOnlineO.getSn());
	
						//if (networks.getMasterDeviceId() != null)
						if (NetworkUtils.isMasterGroupConfigEnabledNetwork(networks))
						{
							mastermode = true;
						}
	
						if (!mastermode)
						{
							boolean isTriggerUpdateRequired = false; 
							
							DevicesDAO devicesDAO = new DevicesDAO(devOnlineO.getOrganization_id());
							devices = devicesDAO.findBySn(info.getIana_id(), info.getSn());
							if (devices != null)
							{
								if (backupObject.getConfig()==null || backupObject.getConfig().isEmpty())
								{
									log.warnf("INFO L20140122 - BACKUP without TEXT.");
									isTriggerUpdateRequired = true;
								}								
								else
								{
									Properties fullConfig = CommonConfigUtils.parsePropertiesString(backupObject.getConfig());
									String confChecksum = CommonConfigUtils.getIcmgAndConfigTypeChecksum(fullConfig, CommonConfigUtils.CONFIG_TAG_TYPE.TYPE_ALL_MANAGED);
									if (devices.getConfigChecksum()!=null && devices.getConfigChecksum().compareToIgnoreCase(confChecksum) == 0)
									{
										log.warnf("INFO L20140122 - BACKUP_TEXT: dev %d %s checksum NO change dev %s backup %s", devices.getIanaId(), devices.getSn(), devices.getConfigChecksum(), confChecksum);
									}
									else
									{
										log.warnf("INFO L20140122 - BACKUP_TEXT: dev %d %s checksum change dev %s backup %s", devices.getIanaId(), devices.getSn(), devices.getConfigChecksum(), confChecksum);
										isTriggerUpdateRequired = true;
									}
								}
								
								/* Unless CONFIG_BACKUP_TEXT is supported in device, it will trigger config overwritten */								
								if (isTriggerUpdateRequired)
								{
									DeviceUpdatesDAO duDAO = new DeviceUpdatesDAO(devOnlineO.getOrganization_id());
									if (!duDAO.isUpdating(devices.getId()))
									{					
										log.warnf("INFO L20140122 - Trigger update.");
										duDAO.incrementConfUpdateForDevLstIfNoUpdate(Arrays.asList(devices.getId()), JsonUtils.genServerRef(), CONFIG_UPDATE_REASON.overwrite_config_backup_event.toString());										
									}
								}
							}
						}
						else
						{
							if (log.isDebugEnabled())
								log.debugf("L20140122 - BACKUP_TEXT: dev %s is running in master mode network %d", devOnlineO.getSn(), devOnlineO.getNetwork_id());
						}
					} catch (SQLException e) {
						log.error("SQLException ", e);
					}
				} 
//				else {
//					log.warnf("L20140122 - PIPE_INFO_TYPE_CONFIG_BACKUP, sid != null && sid is empty, info: %s", info);
//				}
				break;
			case PIPE_INFO_TYPE_CONFIG_MD5:
			case PIPE_INFO_TYPE_CONFIG_CHECKSUM:
				/* _reapply */
				Devices devicesCheckSum = null;
				try {
					DevicesDAO devicesDAOCheckSum = new DevicesDAO(devOnlineO.getOrganization_id());
					devicesCheckSum = devicesDAOCheckSum.findBySn(info.getIana_id(), info.getSn());
				} catch (Exception e) {
					log.error("L20140122 - Exception ",e);
				}
				if (info.getSid() == null || info.getSid().isEmpty())
				{
//					if (devicesCheckSum != null) {
//						log.debug("L20140122 - MD5: - devices (id:" + devicesCheckSum.getId() + ") configChecksumObject != null.");
//					}
					/* WTP triggered event */
					object = JSONObject.fromObject(info);
					data = object.getJSONObject(DATA_TAG);

					ConfigChecksumObject configChecksumObject = gson.fromJson(data.toString(), ConfigChecksumObject.class);
					String configGetObjectMd5 = "";
					try {
						if (configChecksumObject != null) {
							if (log.isDebugEnabled())
								log.debug("L20140122 - MD5: - devices (id:" + devicesCheckSum.getId() + ") configChecksumObject != null.");

							// configChecksumObject.getConf_checksum() != devices.config_checksum then {
							configGetObjectMd5 = configChecksumObject.getConf_checksum();
							if (configChecksumObject.getConf_checksum() != null) {
								String orgId = devOnlineO.getOrganization_id();

								// DevicesDAO devicesDAO = new DevicesDAO(orgId);
								// devices = devicesDAO.findBySn(info.getIana_id(), info.getSn());
								if (log.isDebugEnabled())
									log.debug("L20140122 - MD5: - devices (id:" + devicesCheckSum.getId() + ",orgId:" + orgId + ") configChecksumObject.getConf_checksum() != null.");

								if (devicesCheckSum != null) {
									// if (devices.getConfigChecksum() != null){

									if (!ConfigBackupUtils.isLastConfigFileMd5TheSame(configGetObjectMd5, orgId, devicesCheckSum.getId())) {
										if (log.isDebugEnabled())
											log.debug("L20140122 - MD5: - devices (id:" + devicesCheckSum.getId() + ",orgId:" + orgId + ") configCheckSum is equal to configChecksumObject Conf_checksum.");
										
										Devices dev = NetUtils.getDevices(orgId, info.getIana_id(), info.getSn());
										if (RadioConfigUtils.getConfigTypeFromDevId(orgId, dev.getId()) == JsonConf.CONFIG_TYPE.MAX)
										{	
											ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_GET, info.getSid(), info.getIana_id(), info.getSn());
										}
									}
									// } else{ // devices configuration has never been backup before
									// logger.info("devices (id:" + devices.getId()+ ",orgId:" + orgId
									// +") devices configuration has never been backup before.");
									// ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_GET, info.getSid(),
									// info.getIana_id(), info.getSn());
									// }
								} // end if (devices != null)

							} else {
								log.error("L20140122 - configChecksumObject.getConf_checksum() is null");
							}
						}
					} catch (Exception e) {
						log.error("L20140122 - check sum exception", e);
					}

					/* overwrite config from ic2 if not group master mode */
					boolean mastermode = false;
					try {
						NetworksDAO netDAO = new NetworksDAO(devOnlineO.getOrganization_id());
						Networks networks = netDAO.findById(devOnlineO.getNetwork_id());
						if (networks == null)
							log.warnf("network %d is not found for dev %s", devOnlineO.getNetwork_id(), devOnlineO.getSn());

						if (networks.getMasterDeviceId() != null)
						{
							mastermode = true;
						}

						if (!mastermode)
						{
							DevicesDAO devicesDAO = new DevicesDAO(devOnlineO.getOrganization_id());
							devices = devicesDAO.findBySn(info.getIana_id(), info.getSn());
							if (devices != null)
							{
								if (devices.getConfigChecksum()!=null && devices.getConfigChecksum().compareToIgnoreCase(configChecksumObject.getConf_checksum()) == 0)
								{
									if (log.isInfoEnabled())
										log.info("no change");
								}
								else
								{
									/* isUpdating */
									DeviceUpdatesDAO duDAO = new DeviceUpdatesDAO(devOnlineO.getOrganization_id());
									if (!duDAO.isUpdating(devices.getId()))
									{
										duDAO.incrementConfUpdateForDevLstIfNoUpdate(Arrays.asList(devices.getId()), JsonUtils.genServerRef(), CONFIG_UPDATE_REASON.config_md5_event.toString());
										//Performance: not realtime
//										new ConfigUpdatePerDeviceTask(devOnlineO.getOrganization_id(), devices.getNetworkId()).performConfigUpdateNowIfNoUpdate(
//												Arrays.asList(devices.getId())
//												, JsonUtils.genServerRef(), 
//												CONFIG_UPDATE_REASON.config_md5_event.toString());
										
										//devices.setConfigChecksum(configChecksumObject.getConf_checksum());
										//devicesDAO.update(devices);

										//devOnlineO.setConf_checksum(configChecksumObject.getConf_checksum());
										//ACUtil.<DevOnlineObject> cachePoolObjectBySn(devOnlineO, DevOnlineObject.class);
										if (log.isDebugEnabled())
											log.debugf("LT10001 PIPE_INFO_TYPE_CONFIG_MD5 - Retain dev %s online to %s", devOnlineO.getSn(), devOnlineO.isOnline());
									}
								}
							}
						}
						else
						{
							if (log.isDebugEnabled())
								log.debugf("dev %s is running in master mode network %d", devOnlineO.getSn(), devOnlineO.getNetwork_id());
						}
					} catch (SQLException e) {
						log.error("SQLException ", e);
					}
					configChecksumObject.setIana_id(info.getIana_id());
					configChecksumObject.setSn(info.getSn());
					configChecksumObject.setSid(info.getSid());
					ACUtil.<ConfigChecksumObject> cachePoolObjectBySn(configChecksumObject, ConfigChecksumObject.class);
				}
				else if (status == 200)
				{
					object = JSONObject.fromObject(info);
					data = object.getJSONObject(DATA_TAG);
					ConfigChecksumObject configChecksumObject = gson.fromJson(data.toString(), ConfigChecksumObject.class);
					/* for version checking, no need to save _reapply */
					// try {
					// DevicesDAO devicesDAO = new DevicesDAO(devOnlineO.getOrganization_id());
					// devices = devicesDAO.findBySn(info.getIana_id(), info.getSn());
					// if (devices != null) {
					// devices.setConfigChecksum(configChecksumObject.getConf_checksum());
					// devicesDAO.update(devices);
					// }
					// } catch (SQLException e) {
					// logger.error("SQLException ", e);
					// }
					configChecksumObject.setIana_id(info.getIana_id());
					configChecksumObject.setSn(info.getSn());
					configChecksumObject.setSid(info.getSid());
					ACUtil.<ConfigChecksumObject> cachePoolObjectBySn(configChecksumObject, ConfigChecksumObject.class);
				}
				else
				{
					log.error("PIPE_INFO_TYPE_CONFIG_CHECKSUM - Exceptional case from WTP");
				}

				// ACUtil.<ConfigChecksumObject>cacheDataBySn(info,ConfigChecksumObject.class);
				break;
			case PIPE_INFO_TYPE_CONFIG_ICMG_PUT:
				/* handle retry */
				object = JSONObject.fromObject(info);
				data = object.getJSONObject(DATA_TAG);
				IcmgPutObject icmgPutONew = gson.fromJson(data.toString(), IcmgPutObject.class);
				if (icmgPutONew == null)
					icmgPutONew=new IcmgPutObject();
				// if (icmgPutONew == null)
				// {
				// logger.errorf("Invalid PIPE_INFO_TYPE_CONFIG_ICMG_PUT message received: %s", data.toString());
				// break;
				// }

				// IcmgPutObject icmgPutO = ACUtil.<IcmgPutObject> getPoolObjectBySn(icmgPutONew, IcmgPutObject.class);
				// if (icmgPutO == null)
				// {
				// logger.errorf("Existing IcmgPutObject not existed: %s", data.toString());
				// break;
				// }
				//
				// if (info.getStatus() != 200 || icmgPutONew.isSuccess() == null || !icmgPutONew.isSuccess())
				// {
				// logger.warnf("Fail to PIPE_INFO_TYPE_CONFIG_ICMG_PUT with status %d %s", info.getStatus(),
				// icmgPutONew.isSuccess());
				// icmgPutONew.setRetry(icmgPutO.getRetry() + 1);
				// }
				//
				//TODO ICMG		
				if (info.getStatus()!=418 && info.getStatus()!=404)
				{
					if (info.getStatus() != 200 || icmgPutONew.isSuccess() == null || !icmgPutONew.isSuccess())
					{
						log.warnf("PIPE_INFO_TYPE_CONFIG_ICMG_PUT with status %d received", info.getStatus());
						icmgPutONew.setIana_id(info.getIana_id());
						icmgPutONew.setSn(info.getSn());
						icmgPutONew.setSid(info.getSid());
						icmgPutONew.setStatus(info.getStatus());
						icmgPutONew.setDevice_id(devOnlineO.getDevice_id());
						ACUtil.cachePoolObjectBySn(icmgPutONew, ConfigPutObject.class);
						
						if (devOnlineO.isOnline())
						{
							try {
								boolean bICMG_MVPN = false;
								boolean bICMG_WLAN = false;
								
								try {
									bICMG_MVPN = RadioConfigUtils.webadminShowIcmgMvpn(devOnlineO.getOrganization_id(),
									devOnlineO.getNetwork_id(), devOnlineO.getDevice_id());
									
									JsonConf_RadioSettings radioConfig = RadioConfigUtils.getDatabaseRadioFullConfig(devOnlineO.getOrganization_id(),
											devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), true);
									bICMG_WLAN = radioConfig.isWebadminShowIcmg();
									
									RadioConfigUtils.sendWebadminIcmg(JsonUtils.genServerRef(), devOnlineO.getIana_id(),
											devOnlineO.getSn(), bICMG_MVPN, bICMG_WLAN, "retry icmg", true, info);
								} catch (FeatureNotFoundException e)
								{
									log.warnf("FeatureNotFoundException for dev %d %s", devOnlineO.getIana_id(), devOnlineO.getSn());
								}
		
							} catch (Exception e) {
								log.error("Exception on DEV ONLINE ICMG apply", e);
							}
						}
					}
				}
				break;
			case PIPE_INFO_TYPE_CONFIG_GET:
			case PIPE_INFO_TYPE_CONFIG_GET_TEXT:
				
				ConfigGetObject configGetObject = null;
				String orgId = null;
				Integer dev_id = null;				
				
				try {
					if (devOnlineO!=null && devOnlineO.isOnline()) {						
						if (info.getStatus() == 200) 
						{
							object = JSONObject.fromObject(info);
							if (!object.has(DATA_TAG))
							{
								log.warnf("Invalid CONFIG_GET json (no data object)! %d %s - %s", info.getIana_id(), info.getSn(), info);
								break;
							}
							
							try {
								data = object.getJSONObject(DATA_TAG);
								if (data==null || data.isEmpty())
								{
									log.warnf("Invalid CONFIG_GET json (data is empty)! %d %s - %s", info.getIana_id(), info.getSn(), info);
									break;
								}
								configGetObject = gson.fromJson(data.toString(), ConfigGetObject.class);
							} catch (net.sf.json.JSONException e)
							{
								log.warnf("Invalid CONFIG_GET json (data is not an object)! %d %s - %s", info.getIana_id(), info.getSn(), info);
								break;
							}
						}
						else
						{
							configGetObject = new ConfigGetObject();
						}						
						configGetObject.setSn(info.getSn());
						configGetObject.setIana_id(info.getIana_id());
						configGetObject.setSid(info.getSid());
						configGetObject.setStatus(info.getStatus());
						
						if (info.getStatus() == 200)
						{
							if (configGetObject.getConfig()==null || configGetObject.getConfig().isEmpty())
							{
								log.warnf("Invalid CONFIG_GET from dev %d %s with no Config content!", info.getIana_id(), info.getSn());
								break;
							}						
							
							if (log.isDebugEnabled())
								log.debug("L20140122 - before MD5");
							//String configGetObjectMd5 = MD5Manager.md5(configGetObject.getConfig());
							String configGetObjectMd5 = CommonConfigUtils.getConfChecksumFromHwConf(configGetObject.getConfig());
							orgId = devOnlineO.getOrganization_id();
							dev_id = devOnlineO.getDevice_id();
							DevicesDAO devicesDAO = new DevicesDAO(orgId);
							devices = devicesDAO.findBySn(info.getIana_id(), info.getSn());
							if (devices == null)
							{
								log.warnf("CONFIG_GET/TEXT dev %s %d is not found!", orgId, dev_id);
								break;
							}
							// logger.debug("DeviceConfig -- devId:" + dev_id.toString() + "config:" +
							// configGetObject.getConfig());
							// if (!configGetObjectMd5.equalsIgnoreCase(devices.getConfigChecksum())){
							// // /* backup old config file before apply */
	//						log.debug("L20140122 - not equal checksum: orgId:" + orgId + ", devId:" + dev_id.toString());
							
							if (configGetObject.getFilepath()!=null && !configGetObject.getFilepath().isEmpty())
							{
								boolean backupResult = ConfigBackupUtils.backupDeviceConfig(configGetObjectMd5, configGetObject.getFilepath(), orgId, dev_id);
								if (!backupResult) {
									if (log.isDebugEnabled())
										log.debug("L20140122 - file not backup. please search the details log from backupDeviceConfig()");
								}
							}
							else
							{
								if (log.isInfoEnabled())
									log.infof("dev %s with product id %d isnt support config backup yet", devices.getSn(), devices.getProductId());
							}
							// } else{
							// logger.info("devId: " + devices.getId() +
							// " has the same config checksum with the configGetObject MD5 checksum, no file backup!");
							// }
	
							/* update database - update device.webadmin_cfg field */
							RadioConfigUtils.WebadminCfg wc = new RadioConfigUtils.WebadminCfg(devices.getWebadmin_cfg());
							if (RadioConfigUtils.isWlcEnabled(configGetObject.getConfig()))
							{
								if (log.isDebugEnabled())
									log.debugf("dev %s wlc enabled", info.getSn());
								wc.addCfg(RadioConfigUtils.WEBAMIN_CFG.WLC);
							}
							else
							{
								if (log.isDebugEnabled())
									log.debugf("dev %s wlc disabled", info.getSn());
								wc.removeCfg(RadioConfigUtils.WEBAMIN_CFG.WLC);
							}
	
							devices.setWebadmin_cfg(wc.getValue());
							devicesDAO.update(devices);
						} else {
							if (log.isInfoEnabled())
								log.info("info.getStatus() != 200 ");
						}
						
						ACUtil.<ConfigGetObject> cachePoolObjectBySn(configGetObject, ConfigGetObject.class);
					}
				} catch (JSONException je) {
					log.errorf("CONFIG_GET/TEXT JSONException - orgId %s dev %d %s err %s", orgId, info.getIana_id(), info.getSn(), je);
				} catch (Exception e) {
					log.error(String.format("CONFIG_GET/TEXT exception - orgId %s dev %d %s err %s", orgId, info.getIana_id(), info.getSn(), e), e);
				}
				
				// ACUtil.<ConfigGetObject>cacheDataBySn(info,ConfigGetObject.class);
				break;
			case PIPE_INFO_TYPE_CONFIG_PUT_TEXT:
			case PIPE_INFO_TYPE_CONFIG_PUT:
				if (log.isInfoEnabled())
					log.infof("received CONFIG_PUT/TEXT %s", json);
				
				object = JSONObject.fromObject(info);
				data = object.getJSONObject(DATA_TAG);
				ConfigPutObject configPutObject = gson.fromJson(data.toString(), ConfigPutObject.class);
				SnsOrganizations snsOrg = null;

				// put success
				// HibernateUtil hutilConf = new HibernateUtil(devOnlineO.getOrganization_id());
				try {
					
					/* clear config */
					ConfigUpdatesDAO confUpDAO = new ConfigUpdatesDAO(false);
					ConfigUpdatesId confUpId = new ConfigUpdatesId(info.getIana_id(), info.getSn());
					ConfigUpdates confUp = confUpDAO.findById(confUpId);
					if (confUp!=null)
					{
						snsOrg = BranchUtils.getSnsOrganizationsByIanaIdSn(info.getIana_id(), info.getSn());
						if (snsOrg!=null)
						{
							log.warnf("Probably outsync clear config records for dev %d %s, remove it.", info.getIana_id(), info.getSn());
							confUpDAO.delete(confUp);
							/* continue to set successful config update */
						}
						else
						{
							if (log.isInfoEnabled())
								log.infof("CLEAR_CONFIG task dev %d %s", info.getIana_id(), info.getSn());							
							if (configPutObject != null && configPutObject.isSuccess() != null && configPutObject.isSuccess()) {
								confUpDAO.decrementConfClearForDev(confUpId, null, null, true);	
							}
							else
							{
								confUpDAO.decrementConfClearForDev(confUpId, null, "Err:"+CONFIG_UPDATE_REASON.config_put_failure.toString()+"|"+info.getStatus(), false);
							}
							
							break;
						}
					}

					log.infof("APPLY_CONFIG task dev %d %s", info.getIana_id(), info.getSn());
					DevicesDAO devicesDAO = new DevicesDAO(devOnlineO.getOrganization_id());
					devices = devicesDAO.findBySn(info.getIana_id(), info.getSn());
					DeviceUpdatesDAO deviceUpdateDAO = new DeviceUpdatesDAO(devOnlineO.getOrganization_id());
					
					if (configPutObject != null && configPutObject.isSuccess() != null && configPutObject.isSuccess()) {
						if (log.isInfoEnabled())
							log.infof("CONFIG_PUT/TEXT success for dev %d %s", info.getIana_id(), info.getSn());
						
						DeviceUpdates du = deviceUpdateDAO.findById(devOnlineO.getDevice_id());						
						if (du!=null)
						{
							du.setLastapply(DateUtils.getUtcDate());
							// below deleted by _reapply
							// if (devices != null) {
							// devices.setConfigChecksum(configPutObject.getConf_checksum());
							// devices.setCertChecksum(configPutObject.getCert_checksum());
							// devicesDAO.update(devices);
							// }
							deviceUpdateDAO.saveOrUpdate(du);
						}
						else
						{
							log.warnf("deviceUpdates record is not found for dev %d %s", info.getIana_id(), info.getSn());
						}

						if (log.isInfoEnabled())
							log.infof("dev %d %s updated last sync date to current time", info.getIana_id(), info.getSn());
						devices.setLast_sync_date(DateUtils.getUnixtime());
						devicesDAO.update(devices);
					}
					else
					{
						configPutObject = new ConfigPutObject();
						//Performance:not realtime
						deviceUpdateDAO.incrementConfUpdateForDevLstIfNoUpdate(Arrays.asList(devices.getId()), null, null, "Err:"+CONFIG_UPDATE_REASON.config_put_failure.toString()+"|"+info.getStatus(), true);
					}

				} catch (Exception e) {
					log.error("Exception CONFIG_PUT ", e);
				}

				configPutObject.setIana_id(info.getIana_id());
				configPutObject.setSn(info.getSn());
				configPutObject.setSid(info.getSid());
				configPutObject.setStatus(info.getStatus());

				// ACUtil.<ConfigPutObject>cacheDataBySn(info,ConfigPutObject.class);
				ACUtil.cachePoolObjectBySn(configPutObject, ConfigPutObject.class);
				break;
			case PIPE_INFO_TYPE_FIRMWARE_PUT:
				object = JSONObject.fromObject(info);
				data = object.getJSONObject(DATA_TAG);
				if (log.isDebugEnabled())
					log.debugf("data=%s", data);
				FirmwarePutObject firmwarePutObject = gson.fromJson(data.toString(), FirmwarePutObject.class);
				if (firmwarePutObject==null)
				{
					if (log.isDebugEnabled())
						log.debug("firmwarePutObject is null");
					firmwarePutObject = new FirmwarePutObject();
				}
				firmwarePutObject.setStatus(info.getStatus());
				firmwarePutObject.setSid(info.getSid());
				firmwarePutObject.setSn(info.getSn());
				firmwarePutObject.setIana_id(info.getIana_id());
				ACUtil.<FirmwarePutObject> cachePoolObjectBySn(firmwarePutObject, FirmwarePutObject.class);
				if (log.isDebugEnabled())
					log.debugf("info.getStatus=%s, firmwarePutObject=%s", info.getStatus(), firmwarePutObject);

				DeviceFirmwareSchedulesDAO dfsDAO = null;
				// if (info.getStatus() == 200 && firmwarePutObject.getSuccess() == true)
				// {
				// FirmwareScheduleObject fsO = new FirmwareScheduleObject();
				// try {
				// fsO = ACUtil.<FirmwareScheduleObject> getPoolObjectBySn(fsO, FirmwareScheduleObject.class);
				// logger.debugf("fsO=%s", fsO);
				// try {
				// dfsDAO = new DeviceFirmwareSchedulesDAO();
				// List<DeviceFirmwareSchedules> dfsLst = dfsDAO.findbySn(info.getIana_id(), info.getSn());
				// /* dfsLst size more than 1 is abnormal */
				// if(dfsLst != null)
				// {
				// for (DeviceFirmwareSchedules dfs : dfsLst)
				// {
				// logger.debugf("dfs=%s", dfs);
				// if (fsO!=null)
				// fsO.remove(dfs.getId());
				// dfs.setStatus(1); /* completed */
				// dfs.setUpgrade_time(DateUtils.getUnixtime());
				// dfsDAO.saveOrUpdate(dfs);
				// }
				// }
				// else
				// {
				// logger.error("dfsLst is null!");
				// }
				// } catch (SQLException e) {
				// logger.error("SQLException", e);
				// }
				//
				// try {
				// if (fsO!=null)
				// ACUtil.<FirmwareScheduleObject> cachePoolObjectBySn(fsO, FirmwareScheduleObject.class);
				// } catch (InstantiationException | IllegalAccessException e) {
				// logger.errorf("Cache put exception", e);
				// }
				// } catch (InstantiationException | IllegalAccessException e) {
				// logger.errorf("Cache get exception for FirmwareScheduleObject!!!!", e);
				// }
				// }
				if (firmwarePutObject.getStage()==null || !firmwarePutObject.getStage().equalsIgnoreCase("UPGRADE_IN_PROGRESS"))
				{
					try {
						dfsDAO = new DeviceFirmwareSchedulesDAO();
						List<DeviceFirmwareSchedules> dfsLst = dfsDAO.findbySnFiltered(info.getIana_id(), info.getSn());
						/* dfsLst size more than 1 is abnormal */
						for (DeviceFirmwareSchedules dfs : dfsLst)
						{
							if (dfs.getStatus()!=1)
							{							
								//dfs.setTrial_round(dfs.getTrial_round() + 1);
								if (info.getStatus() == 200 && firmwarePutObject.getSuccess() == true)
								{
									dfs.setUpgrade_time(DateUtils.getUnixtime());
									dfs.setStatus(3);	/* upgrade in progress */
								}
								else if (info.getStatus() == 404)
								{
									/* status unchanged */
								}							
								else
								{
									//dfs.setUpgrade_time(DateUtils.getUnixtime());
									dfs.setStatus(0);
								}
								dfsDAO.update(dfs);
							}
							else
							{
								log.warnf("Firmware upgrade for dev %d %s has already completed", dfs.getIana_id(), dfs.getSn());
							}
						}
					} catch (SQLException e) {
						log.error("SQLException", e);
					}					
				}
				// ACUtil.<FirmwarePutObject>cacheDataBySn(info,FirmwarePutObject.class);
				break;
			case PIPE_INFO_TYPE_FEATURE_GET:				
				/* put db */
				if (status == 200 || status == 0) {
					/* put cache */
					/* FEATURE_GET object should only be updated thru here!! DAO is not necessary at this moment and will out sync*/
					FeatureGetObject fgo = FeatureGetUtils.createFeatureGetObject(info);
					ACUtil.<FeatureGetObject>cachePoolObjectBySn(fgo, FeatureGetObject.class);					
					
					QueueControl.put(devOnlineO.getOrganization_id(), json);
				}
				break;
			case PIPE_INFO_TYPE_UPDATE_AUTO_CHANNEL:
				break;
			case PIPE_INFO_TYPE_EVENT_ACT_FH_LIC:
				object = JSONObject.fromObject(info);
				try {
					data = object.getJSONObject(DATA_TAG);
				} catch (net.sf.json.JSONException e) {
					log.error("PIPE_INFO_TYPE_EVENT_ACT_FH_LIC cannot get data tag", e);
					break;
				}
				
				if (info.getSid()==null || info.getSid().isEmpty())
				{					
					Json_FusionHubLicenseRequest licReq = JsonUtils.fromJson(data.toString(), Json_FusionHubLicenseRequest.class);
					FusionHubUtil.handleVerifyLicReq(info, licReq, devOnlineO);
				}
				else
				{
					log.infof("PIPE_INFO_TYPE_EVENT_ACT_FH_LIC sn %s with status %d is received", info.getSn(), info.getStatus());
					
					/* check if sid exists for device pending change */
										
					BasicObject bo = JsonUtils.fromJson(data.toString(), BasicObject.class);
					DeviceChangeServiceUtils.updateStatus(info.getIana_id(), info.getSn(), info.getSid(), bo.getSuccess());					
				}
				break;
			case PIPE_INFO_TYPE_NDPI:
				if (status == 200 || status == 0) {
					QueueControl.put(devOnlineO.getOrganization_id(), json);
				}
				break;
			case PIPE_INFO_TYPE_DEV_SYSINFO:
				object = JSONObject.fromObject(info);
				try {
					data = object.getJSONObject(DATA_TAG);
				} catch (net.sf.json.JSONException e) {
					log.warn("PIPE_INFO_TYPE_DEV_SYSINFO cannot get data tag (possibly oldfw)");
					break;
				}
				
				SystemInfoObject sysInfo = gson.fromJson(data.toString(), SystemInfoObject.class);
				
				if( sysInfo != null )
				{	
					if( devOnlineO != null )
					{
						devOnlineO.setUptime(sysInfo.getUptime());
						devOnlineO.setUpdateUptimeDate(DateUtils.getUtcDate());
						ACUtil.cachePoolObjectBySn(devOnlineO, DevOnlineObject.class);
					}
				}
				break;
//			case PIPE_INFO_TYPE_SET_TIMER:
//				break;
			case PIPE_INFO_TYPE_WEB_ADMIN_TUNNEL:
				if(status == 200 || status == 0)
				{
					object = JSONObject.fromObject(info);
					try {
						data = object.getJSONObject(DATA_TAG);
					} catch (net.sf.json.JSONException e) {
						log.error("PIPE_INFO_TYPE_WEB_ADMIN_TUNNEL cannot get data tag", e);
						break;
					}
					TunnelObject tunnelObj = (TunnelObject)gson.fromJson(data.toString(), TunnelObject.class);
					tunnelObj.setIana_id(info.getIana_id());
					tunnelObj.setSn(info.getSn());
					ACUtil.cachePoolObjectBySn(tunnelObj, TunnelObject.class);
				}
				break;
			case PIPE_INFO_TYPE_WEB_TUNNELING:
				if (status == 200 || status == 0)
				{
					object = JSONObject.fromObject(info);
					try {
						data = object.getJSONObject(DATA_TAG);
					} catch (net.sf.json.JSONException e) {
						log.error("PIPE_INFO_TYPE_WEB_TUNNELING cannot get data tag", e);
						break;
					}
					WebTunnelingObject webTunlObj = new WebTunnelingObject();
					webTunlObj.setIana_id(info.getIana_id());
					webTunlObj.setSn(info.getSn());
					webTunlObj.setJson(data.toString());
					String cmd_id = null;
					try
					{
						cmd_id  = data.getString("command_id"); 
						if(cmd_id != null)
							webTunlObj.setCommand_id(cmd_id);
					}
					catch(JSONException e)
					{
						log.warn("WEB_TUNNELING response success false command_id is " + cmd_id);
					}
					ACUtil.cachePoolObjectBySn(webTunlObj, WebTunnelingObject.class);
				}
				break;
			case PIPE_INFO_TYPE_DUMMY_TEST:
				break;
//			case PIPE_INFO_TYPE_WAN_IP_INFO:
//				if (status == 200 || status == 0) {
//					QueueControl.put(devOnlineO.getOrganization_id(), json);
//				}
//				break;
			case PIPE_INFO_TYPE_SSID_DISCOVERY:
				if (status == 200 || status == 0) {
					if (devOnlineO != null && devOnlineO.getOrganization_id() != null){
//						QueueCacheControl.put(devOnlineO.getOrganization_id(), json);
						
						object = JSONObject.fromObject(info);
						data = object.getJSONObject(RadioConfigUtils.DATA_TAG);
						JsonApiFormatMessageParser<Json_DeviceNeighborList_Response> jApiParser = new JsonApiFormatMessageParser<Json_DeviceNeighborList_Response>(Json_DeviceNeighborList_Response.class);
						Json_DeviceNeighborList_Response t = jApiParser.parseResponse(data.toString());
			
						jApiParser.parseResponse(json.toString());
						
						//TODO
//						Json_DeviceNeighborList_Response t = JsonUtils.<Json_DeviceNeighborList_Response>fromJson(data.toString(), Json_DeviceNeighborList_Response.class);
						
						DeviceNeighborSsidDiscoveryMessageHandler.doDeviceNeighborSsidDiscoveryMessage(devOnlineO, t.getResponse());
						
						if (log.isDebugEnabled())
							log.debugf("INDOORPOS20140519 - WtpMsgHandler - PIPE_INFO_TYPE_SSID_DISCOVERY, iana: %s, sn: %s", devOnlineO.getIana_id(), devOnlineO.getSn());

					} else {
						log.warnf("INDOORPOS20140519 - WtpMsgHandler - PIPE_INFO_TYPE_SSID_DISCOVERY devOnlineO or devOnlineO.getOrganization_id() is null!!!");
					}
				}
				break;
			case PIPE_INFO_TYPE_AC_STATUS:
				object = JSONObject.fromObject(info);
				try {
					data = object.getJSONObject(DATA_TAG);
				} catch (net.sf.json.JSONException e) {
					log.error("PIPE_INFO_TYPE_AC_STATUS cannot get data tag", e);
					break;
				}
				ACInfo acInfo = (ACInfo)gson.fromJson(data.toString(), ACInfo.class);
				if(acInfo != null)
				{
					acInfo.setQueue_size(0);
					acInfo.setTimestamp((int)(new Date().getTime() / 1000));
					HealthMonitorHandler.persistACHealthInfo(acInfo);
					HealthMonitorHandler.persistSystemHealthInfo(acInfo);
				}
				break;
			case PIPE_INFO_TYPE_DEV_USAGE_CONSOLIDATE:
				if (status == 200 || status == 0) {
					QueueControl.put(devOnlineO.getOrganization_id(), json);
				}
				break;
			case PIPE_INFO_TYPE_PORTAL:
				if(CaptivePortalMessageHandleExecutorController.isCaptivePortalEnabled()){
					boolean queueMode = false;
					if (CaptivePortalMessageHandleExecutorController.getRunningEnabled()){
						queueMode = true;
					}
					if (status == 200 || status == 0) {
						data = null;
						object = JSONObject.fromObject(info);
						try {
							if (object != null){
								data = object.getJSONObject(DATA_TAG);
							} else {
								log.warnf("CAPORT20140526 - WtpMsgHandler - PIPE_INFO_TYPE_PORTAL, JSONObject.fromObject(info) is null! info:%s", info);		
							}
						} catch (net.sf.json.JSONException e) {
							log.error("CAPORT20140526 - WtpMsgHandler - PIPE_INFO_TYPE_PORTAL cannot get data tag", e);
							break;
						}
						if (log.isDebugEnabled())
							log.debugf("CAPORT20140526 - WtpMsgHandler - PIPE_INFO_TYPE_PORTAL, data:%s", data);		
	
						if (devOnlineO != null && data != null){
							if (!queueMode){
								CaptivePortalMessageHandler.doCaptivePortalMessageHandle(devOnlineO, data.toString());
							} else { // queueMode
								CaptivePortalMessageHandler.enqueueCaptivePortalMessage(devOnlineO, data.toString());
							}
						} else {
							if (devOnlineO != null){
								log.warnf("CAPORT20140526 - WtpMsgHandler - PIPE_INFO_TYPE_PORTAL data is null!!! iana:%s, sn:%s", devOnlineO.getIana_id(), devOnlineO.getSn());
							}
						}
					}
				} else {
					if (log.isDebugEnabled())
						log.debugf("CAPORT20140526 - WtpMsgHandler - PIPE_INFO_TYPE_PORTAL, MessageHandleExecutorController.getRunningEnabled():%s, isCaptivePortalEnabled:%s", CaptivePortalMessageHandleExecutorController.getRunningEnabled(),CaptivePortalMessageHandleExecutorController.isCaptivePortalEnabled());	
				}
				break;
			case PIPE_INFO_AC_INFO_UPDATE:
				try {
					object = JSONObject.fromObject(info);
					if (log.isDebugEnabled())
						log.debugf("ACINFOUP20140630 - WtpMsgHandler - PIPE_INFO_AC_INFO_UPDATE, info:%s" , info);	
					if (status == 200 || status == 0) {
						data = object.getJSONObject(DATA_TAG);
						Json_AcInfoUpdate jsonAcInfoUpdate = gson.fromJson(data.toString(), Json_AcInfoUpdate.class);
						if (jsonAcInfoUpdate != null){
							if (log.isDebugEnabled())
								log.debugf("ACINFOUP20140630 - WtpMsgHandler - PIPE_INFO_AC_INFO_UPDATE, jsonAcInfoUpdate:%s" , jsonAcInfoUpdate);	
							AcInfoUpdateMessageHandler.doAcInfoUpdateMessage(jsonAcInfoUpdate);
						} else {
							log.warnf("ACINFOUP20140630 - WtpMsgHandler - PIPE_INFO_AC_INFO_UPDATE, jsonAcInfoUpdate is null !!!, jsonAcInfoUpdate:%s" , jsonAcInfoUpdate);	
	
						}
					}
				} catch (net.sf.json.JSONException e) {
					log.error("ACINFOUP20140630 - PIPE_INFO_AC_INFO_UPDATE cannot get data tag", e);
				}

				break;
			case UNDEFINED:
				/* temp parse developing incorrect type from WTP by key name */
				String tmpType = null;
				try {

					JsonParser parser = new JsonParser();
					JsonObject obj = parser.parse(json).getAsJsonObject();

					tmpType = obj.get("type").toString().replace("\"", "");
				} catch (NullPointerException e)
				{
					log.warn("json type is empty or incorrect json");
				}
				log.warn("Unknown report type from WTP - " + mt + "(" + tmpType + ") " + json);

				// ACUtil.printAllStatusCaches();
				break;
			default:
				log.warn("Shouldnt reach here " + mt);
				break;
			}
		} catch (net.sf.json.JSONException je) {
			log.error("JSONException " + json, je);
		} catch (com.google.gson.JsonSyntaxException e) {
			log.warn("incorrect json format data is received from AC: " + e.getMessage() + " - " + json ,e);
		} catch (InstantiationException | IllegalAccessException e) {
			log.warn("Exception from persistDataBySn " + e.getMessage() + " - " + json, e);
		} catch (NullPointerException e) {
			log.warn("Exception to be investigated ... (" + e.getMessage() + ") (json=" + json + ")", e);
		} catch (Exception e) {
			log.error("Other exceptions", e);
		} finally
		{
			setCounter(false);
			
			if (isOnlineEventCounted)
				currentOnlineEventCounter.decrementAndGet();
			
			int callEndTime = DateUtils.getUnixtime();
			int callUseTime = callEndTime - callStartTime;
			int callHandleTime = callEndTime - callMsgTime;
			/* log message with processing time more than 60 */
			if (callUseTime > MAX_CALL_TIME)
				log.warnf("message type %s from dev %d %s has processed for %d,%d seconds", info.getType(), info.getIana_id(), info.getSn(), callUseTime, callHandleTime);
			
			if (mt==MessageType.PIPE_INFO_TYPE_DEV_ONLINE && callUseTime > MAX_DEV_ONLINE_CALL_TIME)
				log.warnf("PIPE_INFO_TYPE_DEV_ONLINE from dev %d %s has processed for %d,%d seconds", info.getIana_id(), info.getSn(), callUseTime, callHandleTime);			
			
			wtppool.endThread(Thread.currentThread().getId());
		}
				
		log.infof("WtpMsgHandler counter end = %d", counter);
		return;
	}
	
	public static boolean isDeviceWarrantValid(Devices dev) {
		
		final long one_day = 60 * 60 * 24 * 1000;
		
		boolean isHwValid = true;
		boolean isSubValid = true;
		
		if (dev == null) {
			log.warnf("isDeviceWarrantValid - dev %s is given", dev);
			return false;
		}
		
		if (dev.getExpiryDate()==null && dev.getSubExpiryDate()==null)
		{
			log.warnf("dev %d %s has no expirydate!! hw %s sub %s" , dev.getIanaId(), dev.getSn(), dev.getExpiryDate(), dev.getSubExpiryDate());
			return false;
		}
		
		Date curTime = DateUtils.getUtcDate();
		if (dev.getExpiryDate()==null || dev.getExpiryDate().getTime() + one_day < curTime.getTime())
			isHwValid = false;
		
		if (dev.getSubExpiryDate()==null || dev.getSubExpiryDate().getTime() + one_day < curTime.getTime())
			isSubValid = false;
		 
		return isHwValid || isSubValid;
	}
}