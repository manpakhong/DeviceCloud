package com.littlecloud.pool.control;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.littlecloud.ac.WtpMsgHandler;
import com.littlecloud.ac.handler.DeviceGpsReportHandler;
import com.littlecloud.ac.handler.MessageInfo;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.ac.messagehandler.EventLogMessageHandler;
import com.littlecloud.ac.util.ACScheduler;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.ClientMonthlyUsagesDAO;
import com.littlecloud.control.dao.ClientSsidUsagesDAO;
import com.littlecloud.control.dao.ClientUsagesDAO;
import com.littlecloud.control.dao.DailyClientUsagesDAO;
import com.littlecloud.control.dao.DailyDeviceUsagesDAO;
import com.littlecloud.control.dao.DeviceDpiUsagesDAO;
import com.littlecloud.control.dao.DeviceFeaturesDAO;
import com.littlecloud.control.dao.DeviceGpsLocationsDAO;
import com.littlecloud.control.dao.DeviceGpsLocationsDatesDAO;
import com.littlecloud.control.dao.DeviceMonthlyUsagesDAO;
import com.littlecloud.control.dao.DeviceOnlineHistoryDAO;
import com.littlecloud.control.dao.DeviceSsidUsagesDAO;
import com.littlecloud.control.dao.DeviceUsagesDAO;
import com.littlecloud.control.dao.DevicesChannelUtilizationsDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.EventLogDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.dao.branch.OuiInfosDAO;
import com.littlecloud.control.entity.DeviceFeatures;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.report.ClientMonthlyUsages;
import com.littlecloud.control.entity.report.ClientSsidUsages;
import com.littlecloud.control.entity.report.ClientUsages;
import com.littlecloud.control.entity.report.ClientUsagesId;
import com.littlecloud.control.entity.report.DailyClientUsages;
import com.littlecloud.control.entity.report.DailyDeviceUsages;
import com.littlecloud.control.entity.report.DeviceDpiUsages;
import com.littlecloud.control.entity.report.DeviceGpsLocations;
import com.littlecloud.control.entity.report.DeviceGpsLocationsDates;
import com.littlecloud.control.entity.report.DeviceGpsLocationsDatesId;
import com.littlecloud.control.entity.report.DeviceGpsLocationsId;
import com.littlecloud.control.entity.report.DeviceMonthlyUsages;
import com.littlecloud.control.entity.report.DeviceSsidUsages;
import com.littlecloud.control.entity.report.DeviceUsages;
import com.littlecloud.control.entity.report.DevicesChannelUtilizations;
import com.littlecloud.control.entity.report.EventLog;
import com.littlecloud.control.json.model.Json_Device_Feature;
import com.littlecloud.control.json.model.Json_Device_Feature.Json_Feature_List;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.util.NetworkUtils;
import com.littlecloud.criteria.SendImmediateAlertCriteria;
import com.littlecloud.dtos.json.EmailTemplateObjectDto;
import com.littlecloud.helpers.NetworkEmailNotificationsHelper;
import com.littlecloud.pool.object.DevChannelUtilObject;
import com.littlecloud.pool.object.DevLocationsConvertObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.DevSsidUsagesObject;
import com.littlecloud.pool.object.DevSsidUsagesObject.VapStatList;
import com.littlecloud.pool.object.DevTimelyUsageObject;
import com.littlecloud.pool.object.DevTimelyUsageObject.WanList;
import com.littlecloud.pool.object.DevUsageConsolidateObject;
import com.littlecloud.pool.object.DevUsageConsolidateObject.ConsolidateObject;
import com.littlecloud.pool.object.DevUsageConsolidateObject.ConsolidateUsages;
import com.littlecloud.pool.object.DevUsageConsolidateObject.Usages;
import com.littlecloud.pool.object.DevUsageObject;
import com.littlecloud.pool.object.DevUsageObject.UsageList;
import com.littlecloud.pool.object.DeviceLastLocObject;
import com.littlecloud.pool.object.EventLogListObject;
import com.littlecloud.pool.object.EventLogObject;
import com.littlecloud.pool.object.FeatureGetObject;
import com.littlecloud.pool.object.HourlyNdpiUsageObject;
import com.littlecloud.pool.object.HourlyNdpiUsageObject.Protocols;
import com.littlecloud.pool.object.LocationList;
import com.littlecloud.pool.object.SimpleDevLocationsObject;
import com.littlecloud.pool.object.SimpleLocationList;
import com.littlecloud.pool.object.StationList;
import com.littlecloud.pool.object.StationListObject;
import com.littlecloud.pool.object.StationUsageHistoryObject;
import com.littlecloud.pool.object.StationUsageHistoryObject.Timelist;
import com.littlecloud.pool.object.StationUsageHistoryObject.Timelist.ClientList;
import com.littlecloud.pool.object.utils.FeatureGetUtils;
import com.littlecloud.pool.utils.PropertyService;
import com.littlecloud.services.AlertMgr;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.util.DBUtil;
import com.peplink.api.db.util.TableMappingUtil;

/* Note: Too much schedlar thread will be created if the key is object type + organization id, thus organization id will be used as key for batch insert */
public class QueueControl {
	public static Logger log = Logger.getLogger(QueueControl.class);

	private static PropertyService<QueueControl> ps = new PropertyService<QueueControl>(QueueControl.class);
	private static final int MAX_QUEUE_SIZE = ps.getInteger("MAX_QUEUE_SIZE");
	private static final int MAX_BATCH_SIZE = ps.getInteger("MAX_BATCH_SIZE");
	private static final int HIGH_PRIORITY_QUEUE_SIZE = ps.getInteger("HIGH_PRIORITY_QUEUE_SIZE");
	private static final int EVENT_EXPIRED_TIME_IN_SEC = WtpMsgHandler.getEventExpiredTimeInSec() * 1000;
	private static final int OVETTIME_THRESHOLD = 15;	// 15 sec
	private static final int MAX_BATCH_ADD = 50;	// 50 batches
	private static final int MAX_TASK_TIME = 13; // 13 sec
	private static final int MAX_LEN_CLIENTNAME = 45;
	private static final int RECOMMIT_WAIT = 3000; // 3 sec
	private static final int MAX_RECOMMIT_RETRY = 2; // retry 2 times
	private static final int ABNORMAL_QUEUE_SIZE = 500; 
	private static final int UUID_LENGTH = 45; // UUID field length
	
	private static ConcurrentHashMap<String, BlockingQueue<String>> queueMap = new ConcurrentHashMap<String, BlockingQueue<String>>();
	private static ConcurrentHashMap<String, QueueTaskInfo> queueRunningTasks = new ConcurrentHashMap<String, QueueTaskInfo>();

	private static int counter = 0;
	private static Integer license = -1;
	private static AtomicInteger msg_process_counter = new AtomicInteger(0);
	
	public static ConcurrentHashMap<String, QueueTaskInfo> getQueueRunningTasks() {
		return queueRunningTasks;
	}

	public static int getMaxBatchSize(){
		return MAX_BATCH_SIZE;
	}
	
	public static int getMaxQueueSize() {
		return MAX_QUEUE_SIZE;
	}
	
	public static int getQueueSize(String orgId) {
		return queueMap.get(orgId).size();
	}
	
	public static Set<String> getAllQueueSet() {		
		return queueMap.keySet();
	}
	
	public static int getQueueMapSize() {
		return queueMap.size();
	}
	
	public static int clearQueue(String orgId) 
	{	
		BlockingQueue<String> queue = queueMap.get(orgId);
		if (queue==null)
			return -1;
		
		queue.clear();
		
		return queue.size();
	}
	
	public static int clearQueueMessageTypes(String orgId, MessageType targetMt)
	{
		String[] elemLst = null;
		
		BlockingQueue<String> queue = queueMap.get(orgId);
		if (queue==null)
			return -1;
		
		elemLst = (String[]) queue.toArray();
		queue.clear();
		
		for (String json: elemLst)
		{
			try {
				QueryInfo<Object> info = JsonUtils.fromJson(json, QueryInfo.class);
				String type = (info.getType() == null ? null : info.getType().toString());
				MessageType elemMt = MessageType.getMessageType(type);
				if (targetMt != elemMt)
				{
					queue.add(json);
				}
			} catch (Exception e)
			{
				log.warnf("Exception ", e);
			}
		}
		
		return queue.size();
	}
	
	public static int getMsg_process_counter() {
		return msg_process_counter.intValue();
	}

	public static void setMsg_process_counter(int msg_process_counter) {
		QueueControl.msg_process_counter.set(msg_process_counter);
	}
	
	public static void addMsgProcessCounter()
	{
		QueueControl.msg_process_counter.incrementAndGet();
	}
	
	public static List<String> getQueueContent(String orgId, int size) {
				
		int count = 0;
		
		BlockingQueue<String> queue = queueMap.get(orgId);
		
		List<String> result = new ArrayList<String>();
		
		if (orgId==null || queue==null || size==0)
			return result;
		
		try {
			Iterator<String> itr = queue.iterator();
			while (itr.hasNext())
			{
				count++;
				String content = (String) itr.next();				
				
				result.add(content);
				
				if (size!=-1 && count>=size)
				{
					break;
				}
			}
		} catch (Exception e) {
			log.error("getQueueContent iterator exception", e);
		}
		return result;
	}
	
	public synchronized static void setCounter(boolean bAdd)
	{
		if (bAdd)
		{
			counter++;
//			log.debugf("added counter=%d", counter);
		}
		else
		{
			counter--;
//			log.debugf("minus counter=%d", counter);
		}
	}

	public static boolean put(String orgId, String json)
	{
		String key = orgId;
		//log.debug("Queue " + key + " add json " + json);

		/* create or get corresponding queue */
		BlockingQueue<String> queue = (ArrayBlockingQueue<String>) queueMap.get(key);
		if (queue == null)
		{
			log.warnf("[QUEUE] CREATE QUEUE SCHEDULER FOR ORG=%s", key);
			queue = startScheduler(key);
		}
		return queue.add(json);
	}

	public static synchronized BlockingQueue<String> startScheduler(String key)
	{
		BlockingQueue<String> queue = (ArrayBlockingQueue<String>) queueMap.get(key);
		if (queue != null)
			return queue;

		/* create new queue */
		queue = new ArrayBlockingQueue<String>(MAX_QUEUE_SIZE);
		queueMap.put(key, queue);

		/* start scheduler to persist queue */
		ACScheduler acs = new ACScheduler();
		acs.startPersistQueueScheduler(key);
		//log.debugf("11810 getGpsLocation -QueueControl.startScheduler() key= %s", key);

//		log.info("[QUEUE] >>>> Queue for key " + key + " started.");

		return queue;
	}

	public static Object get(String orgId)
	{
		String key = orgId;
		log.debug("Queue " + key + " take");

		BlockingQueue<String> queue = (ArrayBlockingQueue<String>) queueMap.get(key);
		if (queue == null)
			return null;

		return queue.poll();
	}

	private static Thread adjustPriority(String orgId, int qSize) 
	{
		Thread t = Thread.currentThread();                  
        if (qSize > HIGH_PRIORITY_QUEUE_SIZE)
        {
        	log.infof("orgId %s adjust thread %s priority to HIGH", orgId, t.getName());
        	t.setPriority(Thread.MAX_PRIORITY);
        }
        
        return t;
	}
	
	private static void resetPriority(String orgId, Thread t)
	{
		log.infof("orgId %s reset thread %s priority from %s to NORMAL", orgId, t.getName(), t.getPriority());
		t.setPriority(Thread.NORM_PRIORITY);
	}
	
	public static void persistQueue(String orgId)
	{
		setCounter(true);
		addMsgProcessCounter();
		
		String taskName = orgId + "_" + DateUtils.getUtcDate().getTime();		
		
		int globalstarttime = 0;
		int endtime = 0;		
		String key = orgId;
		int curProcessSize = 0; 
		//Thread t = null;
				
		JSONObject object;
		JSONObject data;

		Devices devices;
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date utcTime = null;
		Date networkTime = null;
		Date curT = null;
		//String networkTimeStr = "";
		DevOnlineObject devOnlineO;
		EventLogListObject eventLogListObject = null;
		CopyOnWriteArrayList<EventLogObject> eventLogList = null;

		ClientSsidUsages clientSsidUsages;
		DBUtil dbUtil = null;
		DBConnection batchConnection = null;
		
		QueueTaskInfo qtaskInfo = null;	
		
		log.debugf("persistQueue - processing Queue %s counter %d", taskName, counter);
		/* get current queue size */
		BlockingQueue<String> queue = (ArrayBlockingQueue<String>) queueMap.get(key);
		if (queue == null)
		{
			log.debug("queue for key " + key + " is null");
			setCounter(false);
			return;
		}
		curProcessSize = (queue.size() > MAX_BATCH_SIZE? MAX_BATCH_SIZE:queue.size());
		//t = adjustPriority(orgId, queue.size());
		log.debugf("persistQueue - processing Queue %s counter %d curProcessSize=%d queueSize=%d", taskName, counter, curProcessSize, queue.size());
		
		
		try {
			// Noted: DBUtil mentioned startSession should be called before getConnection!
			dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
			// dbUtil.setDebugSQL(true);
			batchConnection = dbUtil.getConnection(false, key, false);
			// dbUtil.setDebug(true);
		} catch (SQLException e) {
			log.error("Queue " + taskName + " persist error with getting DBUtil/connection instance");
			try {
				if (dbUtil.isSessionStarted()) {
					dbUtil.endSession();
				}
			} catch (Exception e2) {
				log.error("Error in endSession", e2);
			} finally {
				setCounter(false);
			}
			return;
		}

		StringBuilder jsonBuffer = new StringBuilder();
		int taskTime = 0;
		
		try {
			DevicesDAO deviceDAO = null;
			ClientSsidUsagesDAO clientSsidUsagesDAO = null;
			EventLogDAO eventDAO = null;
			NetworksDAO networksDAO = null;
			DeviceUsagesDAO deviceUsagesDAO = null;
			DeviceGpsLocationsDatesDAO deviceGpsLocaDatesDAO = null;
			DeviceGpsLocationsDAO deviceGpsLocaDAO = null;
			DeviceSsidUsagesDAO devSsidUsageDAO = null;
			DevicesChannelUtilizationsDAO devicesChannelUtilizationsDAO = null;
			ClientUsagesDAO clientUsagesDAO = null;
			ClientMonthlyUsagesDAO clientMonthlyUsagesDAO = null;
			DailyClientUsagesDAO dailyClientUsagesDAO = null;
			DeviceFeaturesDAO deviceFeaturesDAO = null;
			DeviceOnlineHistoryDAO deviceOnlineHistoryDAO = null;
			DeviceMonthlyUsagesDAO devMonthlyUsagesDAO = null;
			DailyDeviceUsagesDAO dailyDeviceUsagesDAO = null;
			DeviceDpiUsagesDAO deviceDpiUsagesDAO = null;
			
			SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
			SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM");

			qtaskInfo = queueRunningTasks.get(orgId);
			if (qtaskInfo == null)
				qtaskInfo = new QueueTaskInfo();
			
			HashMap<DeviceUsages, Boolean> DeviceUsageExists = null;
			//log.info("queueSize =" + queue.size());
			//while (!queue.isEmpty())
			globalstarttime = DateUtils.getUnixtime();
			for (int i = 0; i < curProcessSize; i++)
			{
				String type = null;
				/* loop all objects into arrays */

				String json = queue.poll();
				// log.debug("queueSize =" + queue.size() + " json=" + json);
				log.debugf("queueSizeLoop %s queueSize=%d processing %d/%d", taskName, queue.size(), i, curProcessSize);

				MessageType mt = null;
				try {
					Gson gson = new Gson();
					QueryInfo<Object> info = gson.fromJson(json, QueryInfo.class);
					type = (info.getType() == null ? null : info.getType().toString());
					log.infof("taskName = %s, type = %s, sn = %s processing %d/%d", taskName, type, info.getSn(), i, curProcessSize);

					mt = MessageType.getMessageType(type);

					qtaskInfo.startTask(orgId, info.getIana_id(), info.getSn(), mt);
					log.debugf("11810  -QueueControl stratTask orgId = %s, mt = %s, sn = %s iana_Id = /%d", orgId, mt, info.getSn(), info.getIana_id());

					jsonBuffer.append(info.toString());
					jsonBuffer.append("\n");

					switch (mt)
					{
					case PIPE_INFO_TYPE_DEV_ONLINE:
						break;
					case PIPE_INFO_TYPE_DEV_OFFLINE:
						break;
					case PIPE_INFO_TYPE_EVENT_LOG:
					{
						if (eventDAO == null) {
							eventDAO = new EventLogDAO(orgId);
						}
						if (clientSsidUsagesDAO == null) {
							clientSsidUsagesDAO = new ClientSsidUsagesDAO(orgId);// to find client name
						}
						if (networksDAO == null) {
							networksDAO = new NetworksDAO(orgId); // get timezone from network
						}
						// get online object by cache
						devOnlineO = new DevOnlineObject();
						devOnlineO.setIana_id(info.getIana_id());
						devOnlineO.setSn(info.getSn());
						devOnlineO = ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
						log.debug("QueueControl PIPE_INFO_TYPE_EVENT_LOG devOnlineO = " + devOnlineO);
						// object = JSONObject.fromObject(info);
						if(devOnlineO == null)
							break;
						
						Networks networks = networksDAO.findById(devOnlineO.getNetwork_id());
						if(networks == null) {
							log.warn("EVENT_LOG processing networks = null, sn="+devOnlineO.getSn());
							continue;
						}
						EventLogObject eventLogObject;
						eventLogListObject = new EventLogListObject();
						eventLogListObject.setSn(devOnlineO.getSn());
						eventLogListObject.setIana_id(devOnlineO.getIana_id());

						String logMsg = info.getData().toString();
						if(info.getRetry()!=null && info.getRetry()) {	// ignore retry at this moment, will fix after firmware has sent unique event id
							log.infof("Ignore retried EVENT_LOG sn=%s, evtmsg=%s", info.getSn(), logMsg);
							break;
						}

						String[] logMsgSingleLines = logMsg.split("\n");
						Date curTime = new Date();
						EventLog duplicate_log = null;
						
//						boolean isDeviceEmailEnabled = false;
						boolean isWanEmailEnabled = false;
						boolean isSpeedfusionEmailEnabled = false;
						
						if (devOnlineO != null && networks != null) {
							if (networks.isEmailNotificationEnabled()) {
								NetworkEmailNotificationsHelper eventLogMessageHelper = new NetworkEmailNotificationsHelper(networks, devOnlineO.getOrganization_id());
//								isDeviceEmailEnabled = eventLogMessageHelper.isDeviceEmailAlertEnabled();
								isWanEmailEnabled = eventLogMessageHelper.isWanEmailAlertEnabled();
								isSpeedfusionEmailEnabled = eventLogMessageHelper.isSpeedfusionEmailAlertEnabled();

							}
						}
						
						// string message loop
						for (String line : logMsgSingleLines) {

							// json message handler
							EventLogMessageHandler.doJsonMsgEvent(devOnlineO, line, batchConnection);
							
							Pattern pattern = Pattern.compile("(^\\d+)\\s([^\\s]+)\\s\\Q[\\E([^\\s]+)\\Q]\\E\\s(.+)");
							Matcher matcher = pattern.matcher(line);

							if (matcher.find() && matcher.groupCount() >= 4) {
								eventLogObject = new EventLogObject();
								utcTime = new Date((long) Integer.valueOf(matcher.group(1)) * 1000);
								
								int utc_unixT = (int)( utcTime.getTime() / 1000 );
								int cur_unixT = (int)( curTime.getTime() / 1000 );
								if( utc_unixT > (cur_unixT + 300) )
								{
//									logJson.warnf("Future date record found, record ignored orgId %s dev %d %s %d", orgId, info.getIana_id(), info.getSn(), utc_unixT);
									log.infof("Future date record found, record ignored %s dev %d %s %d", orgId, info.getIana_id(), info.getSn(), utc_unixT);
									continue;
								}
								
								Long timeDiff = utcTime.getTime() - curTime.getTime();
								boolean unsyncRecord = false;
//								if (Math.abs(timeDiff) >= 3600000) 
								if (Math.abs(timeDiff) >= EVENT_EXPIRED_TIME_IN_SEC)
								{
									unsyncRecord = true;
									eventLogListObject = ACUtil.<EventLogListObject> getPoolObjectBySn(eventLogListObject, EventLogListObject.class);
//									log.debugf("outsync Expired event obj=<<%s>>, event=<<%s>>", eventLogListObject, line);
									
									if (eventLogListObject == null) {
										// not buffered list in cache yet
										eventLogListObject = new EventLogListObject();
										eventLogListObject.setSn(devOnlineO.getSn());
										eventLogListObject.setIana_id(devOnlineO.getIana_id());
										eventLogList = new CopyOnWriteArrayList<EventLogObject>();
										// ACUtil.<EventLogListObject>cachePoolObjectBySn(eventLogListObject,
										// EventLogListObject.class);
									} else {
										eventLogList = eventLogListObject.getEventObjectList();
										if( eventLogList!=null && eventLogList.size() >= 100 )
											continue;
									}
//									log.debug("eventLogList = " + eventLogList);
								}
								networkTime = DateUtils.offsetFromUtcTimeZoneId(utcTime, networks.getTimezone());
								eventLogObject.setTimestamp(Integer.valueOf(matcher.group(1)));
								eventLogObject.setDevice_id(devOnlineO.getDevice_id());

								// handle WLAN messages first
								Pattern innerPattern = Pattern.compile("WLAN:\\s(.+)");
								Matcher innerMatcher = innerPattern.matcher(matcher.group(4));
								if (innerMatcher.find()) {
									eventLogObject.setEvent_type("WLAN");
									eventLogObject.setDetail(innerMatcher.group(1));

									String bssid = "";
									String ssid = "";
									String clientName = "";
									String wlanMsg = "";

									// This message is confirmed to be depreciated. 
									Pattern finalPattern = Pattern.compile("SSID\\s([^\\[]+)\\s\\[(.+)\\]\\sSTA\\s([^\\s]+)\\sIEEE\\s802.11:\\s([^\\s]+)");
									Matcher finalMatcher = finalPattern.matcher(innerMatcher.group(1));

									if (finalMatcher.find() && finalMatcher.groupCount() >= 1) {
										ssid = finalMatcher.group(1);
										bssid = finalMatcher.group(2);
										eventLogObject.setSsid(ssid);
										clientSsidUsages = clientSsidUsagesDAO.findLatestRecordByMac(finalMatcher.group(3).toUpperCase());

										if (clientSsidUsages != null) {
											clientName = clientSsidUsages.getName();
										}
										if (clientName == null || clientName.isEmpty()) {
											clientName = finalMatcher.group(3).toUpperCase();
										}
										
										clientName = (clientName.length() >= MAX_LEN_CLIENTNAME? clientName.substring(MAX_LEN_CLIENTNAME): clientName);
										eventLogObject.setClient_name(clientName);
										eventLogObject.setClient_mac(finalMatcher.group(3).toUpperCase());

										if (finalMatcher.group(4).equals("disassociated")) {
											wlanMsg = "Client disconnected from " + ssid;
										} else {
											wlanMsg = "Client connected to " + ssid;
										}
										eventLogObject.setDetail(wlanMsg);
										EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
												eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
												eventLogObject.getEvent_type(), wlanMsg);
										// TODO:save batch
										// eventDAO.save(eventLog);
										eventLog.create();
										if (unsyncRecord) {
											eventLogList.add(eventLogObject);
											eventLogListObject.setEventObjectList(eventLogList);
											ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
										} else {
											duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
											if(duplicate_log == null)
												batchConnection.addBatch(true,eventLog);
										}
									}

									String regex = "Client\\s\\(([^\\)]+)\\)\\s([^\\s]+)\\s[^\\s]+\\s\"(.+)\"\\s\\(([^\\s]+)\\)\\s(?:(\\(.*\\)\\s)|)";
//									finalPattern = Pattern.compile("Client\\s\\(([^\\)]+)\\)\\s([^\\s]+)\\s[^\\s]+\\s\"(.+)\"\\s\\(([^\\s]+)\\)\\s");
									finalPattern = Pattern.compile(regex);

									finalMatcher = finalPattern.matcher(innerMatcher.group(1));
//									log.debug("WLAN event log message = " + innerMatcher.group(1));
//									log.debug("Pattern string = " + regex);
//									log.debug("Pattern string = Client\\s\\(([^\\)]+)\\)\\s([^\\s]+)\\s[^\\s]+\\s\"(.+)\"\\s\\(([^\\s]+)\\)\\s");
									if (finalMatcher.find() && finalMatcher.groupCount() >= 1) {
//										log.debug("event log pattern matched, count = " + finalMatcher.groupCount());
										ssid = finalMatcher.group(3);
										bssid = finalMatcher.group(4);
										eventLogObject.setSsid(ssid);
										clientSsidUsages = clientSsidUsagesDAO.findLatestRecordByMac(finalMatcher.group(1).toUpperCase());

										if (clientSsidUsages != null) {
											clientName = clientSsidUsages.getName();
										}
										if (clientName == null || clientName.isEmpty()) {
											clientName = finalMatcher.group(1).toUpperCase();
										}
										clientName = (clientName.length() >= MAX_LEN_CLIENTNAME? clientName.substring(MAX_LEN_CLIENTNAME): clientName);
										eventLogObject.setClient_name(clientName);
										eventLogObject.setClient_mac(finalMatcher.group(1).toUpperCase());

										if (finalMatcher.group(2).equals("disconnected")) {
											wlanMsg = "Client disconnected from " + ssid;
										} else {
											wlanMsg = "Client connected to " + ssid;
										}
										eventLogObject.setDetail(wlanMsg);
										EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
												eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
												eventLogObject.getEvent_type(), wlanMsg);
										// TODO:save batch
										// eventDAO.save(eventLog);
										eventLog.create();
										if (unsyncRecord) {
											eventLogList.add(eventLogObject);
											eventLogListObject.setEventObjectList(eventLogList);
											ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
										} else {
											duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
											if(duplicate_log == null)
												batchConnection.addBatch(true,eventLog);
										}
									}

									continue;
								}

								// WAN up/down also many
								innerPattern = Pattern.compile("WAN:\\s(.+)");
								innerMatcher = innerPattern.matcher(matcher.group(4));
//								log.info("WAN event log message = " + matcher.group(4));

								if (innerMatcher.find()) {
									eventLogObject.setEvent_type("WAN");
									eventLogObject.setDetail(innerMatcher.group(1));
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}

									if (networks.isEmailNotificationEnabled()) {
										Pattern finalPattern = Pattern.compile("(.+)\\sdisconnected");
										Matcher finalMatcher = finalPattern.matcher(innerMatcher.group(1));

										if (finalMatcher.find()) {
											log.debug("AL10001 - Wan Disconnected log matched");
											if (!unsyncRecord && isWanEmailEnabled) {
//												AlertUtils alertUtils = new AlertUtils();
												AlertMgr alertMgr = new AlertMgr();
												
												SendImmediateAlertCriteria siaCriteria = new SendImmediateAlertCriteria();
												
												siaCriteria.setMsgId(EmailTemplateObjectDto.EMAIL_MSG_ID_WAN_DOWN);
												siaCriteria.setMsgType(EmailTemplateObjectDto.EMAIL_MSG_TYPE_EMAIL);
												siaCriteria.setOrgId(orgId);
												siaCriteria.setDevId(eventLogObject.getDevice_id());
												siaCriteria.setLevel(1);
												siaCriteria.setDuration(0);
												siaCriteria.setWanName(finalMatcher.group(1));
												siaCriteria.setVpnName("");
												siaCriteria.setDatetime(eventLog.getDatetime());
												siaCriteria.setAlertType(SendImmediateAlertCriteria.ALERT_TYPE_WAN);
												
												alertMgr.sendImmediateAlert(siaCriteria);
//												alertUtils.sendImmediateAlert(siaCriteria);
												
//												alertUtils.SendImmediateAlert(EMAIL_MSG_ID.wan_down, EMAIL_MSG_TYPE.email, orgId, eventLogObject.getDevice_id(), 1, 0, finalMatcher.group(1), "", eventLog.getDatetime());
											}
											else{
												log.warnf("ALERT201408211034 - skip out sync alert for dev %d %s eventlog %s", devOnlineO.getIana_id(), devOnlineO.getSn(), eventLog);
											}
										}

										finalPattern = Pattern.compile("(.+)\\sconnected (to\\s)?(.+)");
										finalMatcher = finalPattern.matcher(innerMatcher.group(1));

										if (finalMatcher.find()) {
											log.debug("AL10001 - Wan Connected log matched");
											if (!unsyncRecord && isWanEmailEnabled) {
												AlertMgr alertMgr = new AlertMgr();
//												AlertUtils alertUtils = new AlertUtils();
												
												SendImmediateAlertCriteria siaCriteria = new SendImmediateAlertCriteria();
												siaCriteria.setMsgId(EmailTemplateObjectDto.EMAIL_MSG_ID_WAN_UP);
												siaCriteria.setMsgType(EmailTemplateObjectDto.EMAIL_MSG_TYPE_EMAIL);
												siaCriteria.setOrgId(orgId);
												siaCriteria.setDevId(eventLogObject.getDevice_id());
												siaCriteria.setLevel(1);
												siaCriteria.setDuration(0);
												siaCriteria.setWanName(finalMatcher.group(1));
												siaCriteria.setVpnName("");
												siaCriteria.setDatetime(eventLog.getDatetime());
												siaCriteria.setAlertType(SendImmediateAlertCriteria.ALERT_TYPE_WAN);
												alertMgr.sendImmediateAlert(siaCriteria);
//												alertUtils.sendImmediateAlert(siaCriteria);
												
//												alertUtils.SendImmediateAlert(EMAIL_MSG_ID.wan_up, EMAIL_MSG_TYPE.email, orgId, eventLogObject.getDevice_id(), 1, 0, finalMatcher.group(1), "", eventLog.getDatetime());
											}
											else
											{
												log.warnf("ALERT201408211034 - skip out sync alert for dev %d %s eventlog %s", devOnlineO.getIana_id(), devOnlineO.getSn(), eventLog);
											}											
										}
									}
									continue;
								}
								
								innerPattern = Pattern.compile("[Ss]ystem:\\sChanges\\sapplied");
								innerMatcher = innerPattern.matcher(matcher.group(4));

								if (innerMatcher.find()) 
								{
									eventLogObject.setEvent_type("System");
									eventLogObject.setDetail("Changes applied");
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}

									continue;
								}
								
								innerPattern = Pattern.compile("[Ss]ystem[:]? Started up \\((.+)\\)");
								innerMatcher = innerPattern.matcher(matcher.group(4));

								if (innerMatcher.find()) {
									eventLogObject.setEvent_type("System");
									eventLogObject.setDetail("Started up ("+innerMatcher.group(1)+")");
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}
									continue;
								}

								innerPattern = Pattern.compile("[Ss]ystem[:]? Started");
								innerMatcher = innerPattern.matcher(matcher.group(4));

								if (innerMatcher.find()) {
									eventLogObject.setEvent_type("System");
									eventLogObject.setDetail("Started up");
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}
									continue;
								}

								innerPattern = Pattern.compile("[Ss]ystem[:]? Firmware upgrade ([^\\s]+)");
								innerMatcher = innerPattern.matcher(matcher.group(4));

								if (innerMatcher.find()) {
									eventLogObject.setEvent_type("System");
									if (innerMatcher.group(1).equals("now")) {
										eventLogObject.setDetail("Firmware upgrade now");
									} else if (innerMatcher.group(1).equals("failed")) {
										eventLogObject.setDetail("Firmware upgrade failed");
									} else {
										eventLogObject.setDetail("Firmware upgrade " + innerMatcher.group(1));
									}
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}
									continue;
								}

								innerPattern = Pattern.compile("System:\\s(HA\\sstatus\\schanged\\sto\\s.+)");
								innerMatcher = innerPattern.matcher(matcher.group(4));

								if (innerMatcher.find()) {
									eventLogObject.setEvent_type("HA");
									eventLogObject.setDetail(innerMatcher.group(1));
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}
									continue;
								}
								
								innerPattern = Pattern.compile("System:\\s(.+)");
								innerMatcher = innerPattern.matcher(matcher.group(4));
								if (innerMatcher.find()) {
									eventLogObject.setEvent_type("System");
									eventLogObject.setDetail(innerMatcher.group(1));
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}
									continue;
								}

								innerPattern = Pattern.compile("(\\w+):\\sDDNS\\sdomain\\s(.+)\\Q.\\E");
								innerMatcher = innerPattern.matcher(matcher.group(4));

								if (innerMatcher.find() && innerMatcher.groupCount() >= 2) {
									eventLogObject.setEvent_type("DDNS");
									eventLogObject.setDetail("Domain " + innerMatcher.group(2) + " for " + innerMatcher.group(1));
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}
									continue;
								}

								innerPattern = Pattern.compile("Cloud:\\sTime sync'd\\s\\[([-]?\\d+)\\]\\sto\\s(\\S+)");
								innerMatcher = innerPattern.matcher(matcher.group(4));
								if (innerMatcher.find() && innerMatcher.groupCount() >= 2) {
									String timeSyncString = innerMatcher.group(1);
									if (timeSyncString == null || timeSyncString.isEmpty()) {
										timeSyncString = "0";
									}
									Integer timeSyncValue = Integer.valueOf(timeSyncString);
									log.debug("timeSyncValue = " + timeSyncValue);

									// TODO: FIX eventLogListObject = null and add this event to eventLogList also! 
									try
									{
										EventLogListObject eventLogListObject2 = ACUtil.<EventLogListObject> getPoolObjectBySn(eventLogListObject, EventLogListObject.class);
										if (eventLogListObject2 != null) {
											eventLogList = eventLogListObject2.getEventObjectList();
											Date cur = null;
//											log.debug("eventLogList = " + eventLogList);
											for (EventLogObject evt : eventLogList) {
												cur = new Date();
												if( (evt.getTimestamp() + timeSyncValue) - ((int)(cur.getTime()/1000)) > 300)
												{
													// logJson.warn("Future date record found, record ignored, ts=" + (evt.getTimestamp() + timeSyncValue));
//													log.info("Future date record found, record ignored, ts=" + (evt.getTimestamp() + timeSyncValue));
													continue;
												}
												evt.setTimestamp(evt.getTimestamp() + timeSyncValue);
												Date newUtcTime = new Date((long) evt.getTimestamp() * 1000);
												Date newNetworkTime = DateUtils.offsetFromUtcTimeZoneId(newUtcTime, networks.getTimezone());
												EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), newNetworkTime, evt.getTimestamp(),
														evt.getDevice_id(), evt.getSsid(), evt.getClient_name(), evt.getClient_mac(),
														evt.getEvent_type(), evt.getDetail());
												eventLog.create();
//												log.debug("eventLog = " + eventLog);
												duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
												if(duplicate_log == null)
													batchConnection.addBatch(true,eventLog);
											}
	
											eventLogList.clear();
											eventLogListObject2.setEventObjectList(eventLogList);
											ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject2, EventLogListObject.class);
											eventLogListObject = eventLogListObject2;	// replace eventLogListObject
										}
									} catch (Exception e) {
										log.error("EventLog: Time Sync handling exception, eventLogListObject="+eventLogListObject, e);
									}
									/*eventLogObject.setEvent_type("System");
									eventLogObject.setDetail("Time synchronization successful with " + innerMatcher.group(2));
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									eventLog.create();
									log.debug("eventLog = " + eventLog);
									batchConnection.addBatch(eventLog);*/
									continue;       
								}
								
								innerPattern = Pattern.compile("SpeedFusion:\\s(.+)");
								innerMatcher = innerPattern.matcher(matcher.group(4));
								if (innerMatcher.find()) {
									eventLogObject.setEvent_type("PepVPN");
									eventLogObject.setDetail(innerMatcher.group(1));
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}

									if (networks.isEmailNotificationEnabled()) {
										Pattern finalPattern = Pattern.compile("disconnected");
										Matcher finalMatcher = finalPattern.matcher(innerMatcher.group(1));

										if (finalMatcher.find()) {
											if (!unsyncRecord && isSpeedfusionEmailEnabled) {
//												AlertUtils alertUtils = new AlertUtils();
												AlertMgr alertMgr = new AlertMgr();
												SendImmediateAlertCriteria siaCriteria = new SendImmediateAlertCriteria();
												siaCriteria.setMsgId(EmailTemplateObjectDto.EMAIL_MSG_ID_SPEED_FUSION_DOWN);
												siaCriteria.setMsgType(EmailTemplateObjectDto.EMAIL_MSG_TYPE_EMAIL);
												siaCriteria.setOrgId(orgId);
												siaCriteria.setDevId(eventLogObject.getDevice_id());
												siaCriteria.setLevel(1);
												siaCriteria.setDuration(0);
												siaCriteria.setWanName("");
												siaCriteria.setVpnName("");
												siaCriteria.setDatetime(eventLog.getDatetime());
												siaCriteria.setAlertType(SendImmediateAlertCriteria.ALERT_TYPE_SPEEDFUSION);
												alertMgr.sendImmediateAlert(siaCriteria);
//												alertUtils.sendImmediateAlert(siaCriteria);
												
//												alertUtils.SendImmediateAlert(EMAIL_MSG_ID.sf_down, EMAIL_MSG_TYPE.email, orgId, eventLogObject.getDevice_id(), 1, 0, "", "", eventLog.getDatetime());
											}
											else{
												log.warnf("ALERT201408211034 - skip out sync alert for dev: %d %s, eventlog: %s", devOnlineO.getIana_id(), devOnlineO.getSn(), eventLog);
											}
										}

										finalPattern = Pattern.compile("connected");
										finalMatcher = finalPattern.matcher(innerMatcher.group(1));

										if (finalMatcher.find()) {
											if (!unsyncRecord && isSpeedfusionEmailEnabled) {
//												AlertUtils alertUtils = new AlertUtils();
												AlertMgr alertMgr = new AlertMgr();
												SendImmediateAlertCriteria siaCriteria = new SendImmediateAlertCriteria();
												siaCriteria.setMsgId(EmailTemplateObjectDto.EMAIL_MSG_ID_SPEED_FUSION_UP);
												siaCriteria.setMsgType(EmailTemplateObjectDto.EMAIL_MSG_TYPE_EMAIL);
												siaCriteria.setOrgId(orgId);
												siaCriteria.setDevId(eventLogObject.getDevice_id());
												siaCriteria.setLevel(1);
												siaCriteria.setDuration(0);
												siaCriteria.setWanName("");
												siaCriteria.setVpnName("");
												siaCriteria.setDatetime(eventLog.getDatetime());
												siaCriteria.setAlertType(SendImmediateAlertCriteria.ALERT_TYPE_SPEEDFUSION);
												alertMgr.sendImmediateAlert(siaCriteria);
//												alertUtils.sendImmediateAlert(siaCriteria);
												
//												alertUtils.SendImmediateAlert(EMAIL_MSG_ID.sf_up, EMAIL_MSG_TYPE.email, orgId, eventLogObject.getDevice_id(), 1, 0, "", "", eventLog.getDatetime());
											}
											else
											{
												log.warnf("ALERT201408211034 - skip out sync alert for dev: %d %s, eventlog: %s", devOnlineO.getIana_id(), devOnlineO.getSn(), eventLog);
											}
										}
									}
									continue;
								}

								innerPattern = Pattern.compile("S2S\\sVPN:\\s(.+)");
								innerMatcher = innerPattern.matcher(matcher.group(4));
								if (innerMatcher.find()) {
									eventLogObject.setEvent_type("PepVPN");
									eventLogObject.setDetail(innerMatcher.group(1));
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}

									if (networks.isEmailNotificationEnabled()) {
										Pattern finalPattern = Pattern.compile("disconnected");
										Matcher finalMatcher = finalPattern.matcher(innerMatcher.group(1));

										if (finalMatcher.find()) {
											if (!unsyncRecord) {
//												AlertUtils alertUtils = new AlertUtils();
												AlertMgr alertMgr = new AlertMgr();
												
												SendImmediateAlertCriteria siaCriteria = new SendImmediateAlertCriteria();
												siaCriteria.setMsgId(EmailTemplateObjectDto.EMAIL_MSG_ID_SPEED_FUSION_DOWN);
												siaCriteria.setMsgType(EmailTemplateObjectDto.EMAIL_MSG_TYPE_EMAIL);
												siaCriteria.setOrgId(orgId);
												siaCriteria.setDevId(eventLogObject.getDevice_id());
												siaCriteria.setLevel(1);
												siaCriteria.setDuration(0);
												siaCriteria.setWanName("");
												siaCriteria.setVpnName("");
												siaCriteria.setDatetime(eventLog.getDatetime());
												siaCriteria.setAlertType(SendImmediateAlertCriteria.ALERT_TYPE_SPEEDFUSION);
												
												alertMgr.sendImmediateAlert(siaCriteria);
//												alertUtils.sendImmediateAlert(siaCriteria);
												
//												alertUtils.SendImmediateAlert(EMAIL_MSG_ID.sf_down, EMAIL_MSG_TYPE.email, orgId, eventLogObject.getDevice_id(), 1, 0, "", "", eventLog.getDatetime());
											}
											else
											{
												log.warnf("ALERT201408211034 - skip out sync alert for dev %d %s eventlog %s", devOnlineO.getIana_id(), devOnlineO.getSn(), eventLog);
											}
										}

										finalPattern = Pattern.compile("connected");
										finalMatcher = finalPattern.matcher(innerMatcher.group(1));

										if (finalMatcher.find()) {
											if (!unsyncRecord) {
//												AlertUtils alertUtils = new AlertUtils();
												AlertMgr alertMgr = new AlertMgr();
												SendImmediateAlertCriteria siaCriteria = new SendImmediateAlertCriteria();
												siaCriteria.setMsgId(EmailTemplateObjectDto.EMAIL_MSG_ID_SPEED_FUSION_UP);
												siaCriteria.setMsgType(EmailTemplateObjectDto.EMAIL_MSG_TYPE_EMAIL);
												siaCriteria.setOrgId(orgId);
												siaCriteria.setDevId(eventLogObject.getDevice_id());
												siaCriteria.setLevel(1);
												siaCriteria.setDuration(0);
												siaCriteria.setWanName("");
												siaCriteria.setVpnName("");
												siaCriteria.setDatetime(eventLog.getDatetime());
												siaCriteria.setAlertType(SendImmediateAlertCriteria.ALERT_TYPE_SPEEDFUSION);
												alertMgr.sendImmediateAlert(siaCriteria);
//												alertUtils.sendImmediateAlert(siaCriteria);
												
												
//												alertUtils.SendImmediateAlert(EMAIL_MSG_ID.sf_up, EMAIL_MSG_TYPE.email, orgId, eventLogObject.getDevice_id(), 1, 0, "", "", eventLog.getDatetime());
											}
											else
											{
												log.warnf("ALERT201408211034 - skip out sync alert for dev %d %s eventlog %s", devOnlineO.getIana_id(), devOnlineO.getSn(), eventLog);
											}
										}
									}
									continue;
								}

								innerPattern = Pattern.compile("PepVPN:\\s(.+)");
								innerMatcher = innerPattern.matcher(matcher.group(4));
								if (innerMatcher.find()) {
									eventLogObject.setEvent_type("PepVPN");
									eventLogObject.setDetail(innerMatcher.group(1));
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}

									if (networks.isEmailNotificationEnabled()) {
										Pattern finalPattern = Pattern.compile("disconnected");
										Matcher finalMatcher = finalPattern.matcher(innerMatcher.group(1));

										if (finalMatcher.find()) {
											if (!unsyncRecord) {
//												AlertUtils alertUtils = new AlertUtils();
												AlertMgr alertMgr = new AlertMgr();
												SendImmediateAlertCriteria siaCriteria = new SendImmediateAlertCriteria();
												siaCriteria.setMsgId(EmailTemplateObjectDto.EMAIL_MSG_ID_SPEED_FUSION_DOWN);
												siaCriteria.setMsgType(EmailTemplateObjectDto.EMAIL_MSG_TYPE_EMAIL);
												siaCriteria.setOrgId(orgId);
												siaCriteria.setDevId(eventLogObject.getDevice_id());
												siaCriteria.setLevel(1);
												siaCriteria.setDuration(0);
												siaCriteria.setWanName("");
												siaCriteria.setVpnName("");
												siaCriteria.setDatetime(eventLog.getDatetime());
												siaCriteria.setAlertType(SendImmediateAlertCriteria.ALERT_TYPE_SPEEDFUSION);
												alertMgr.sendImmediateAlert(siaCriteria);
//												alertUtils.sendImmediateAlert(siaCriteria);
												
//												alertUtils.SendImmediateAlert(EMAIL_MSG_ID.sf_down, EMAIL_MSG_TYPE.email, orgId, eventLogObject.getDevice_id(), 1, 0, "", "", eventLog.getDatetime());
											}
											else
											{
												log.warnf("ALERT201408211034 - skip out sync alert for dev %d %s eventlog %s", devOnlineO.getIana_id(), devOnlineO.getSn(), eventLog);
											}
										}

										finalPattern = Pattern.compile("connected");
										finalMatcher = finalPattern.matcher(innerMatcher.group(1));

										if (finalMatcher.find()) {
											if (!unsyncRecord) {
//												AlertUtils alertUtils = new AlertUtils();
												AlertMgr alertMgr = new AlertMgr();
												SendImmediateAlertCriteria siaCriteria = new SendImmediateAlertCriteria();
												siaCriteria.setMsgId(EmailTemplateObjectDto.EMAIL_MSG_ID_SPEED_FUSION_UP);
												siaCriteria.setMsgType(EmailTemplateObjectDto.EMAIL_MSG_TYPE_EMAIL);
												siaCriteria.setOrgId(orgId);
												siaCriteria.setDevId(eventLogObject.getDevice_id());
												siaCriteria.setLevel(1);
												siaCriteria.setDuration(0);
												siaCriteria.setWanName("");
												siaCriteria.setVpnName("");
												siaCriteria.setDatetime(eventLog.getDatetime());
												siaCriteria.setAlertType(SendImmediateAlertCriteria.ALERT_TYPE_SPEEDFUSION);
												alertMgr.sendImmediateAlert(siaCriteria);
//												alertUtils.sendImmediateAlert(siaCriteria);
												
//												alertUtils.SendImmediateAlert(EMAIL_MSG_ID.sf_up, EMAIL_MSG_TYPE.email, orgId, eventLogObject.getDevice_id(), 1, 0, "", "", eventLog.getDatetime());
											}
											else
											{
												log.warnf("ALERT201408211034 - skip out sync alert for dev %d %s eventlog %s", devOnlineO.getIana_id(), devOnlineO.getSn(), eventLog);
											}
										}
									}
									continue;
								}

								innerPattern = Pattern.compile("PPTP:\\s(.+)");
								innerMatcher = innerPattern.matcher(matcher.group(4));
								if (innerMatcher.find()) {
									eventLogObject.setEvent_type("PPTP");
									eventLogObject.setDetail(innerMatcher.group(1));
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}
									continue;
								}

								innerPattern = Pattern.compile("IP\\saddress\\sconflict:(.+)");
								innerMatcher = innerPattern.matcher(matcher.group(4));
								if (innerMatcher.find()) {
									log.debug("IP address conflict found");
									eventLogObject.setEvent_type("IP address conflict");
									eventLogObject.setDetail(innerMatcher.group(1));

									Pattern finalPattern = Pattern.compile("((\\w{2}:){5}\\w{2})");
									Matcher finalMatcher = finalPattern.matcher(innerMatcher.group(1));
									if (finalMatcher.find() && finalMatcher.groupCount() >= 1) {
										String clientName = finalMatcher.group(1);
										clientName = (clientName.length() >= MAX_LEN_CLIENTNAME? clientName.substring(MAX_LEN_CLIENTNAME): clientName);
										eventLogObject.setClient_name(clientName);
									}
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}
									continue;
								}

								innerPattern = Pattern.compile("MAC\\saddress\\sconflict:(.+)");
								innerMatcher = innerPattern.matcher(matcher.group(4));
								if (innerMatcher.find()) {
									eventLogObject.setEvent_type("MAC address conflict");
									eventLogObject.setDetail(innerMatcher.group(1));

									Pattern finalPattern = Pattern.compile("((\\w{2}:){5}\\w{2})");
									Matcher finalMatcher = finalPattern.matcher(innerMatcher.group(1));
									if (finalMatcher.find() && finalMatcher.groupCount() >= 1) {
										eventLogObject.setClient_name(finalMatcher.group(1));
									}
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}
									continue;
								}

								innerPattern = Pattern.compile("HA\\s[Cc]onfig\\sSync:\\s(.+)");
								innerMatcher = innerPattern.matcher(matcher.group(4));
								if (innerMatcher.find()) {
									eventLogObject.setEvent_type("HA");
									eventLogObject.setDetail(innerMatcher.group(1));
									EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, eventLogObject.getTimestamp(),
											eventLogObject.getDevice_id(), eventLogObject.getSsid(), eventLogObject.getClient_name(), eventLogObject.getClient_mac(),
											eventLogObject.getEvent_type(), eventLogObject.getDetail());
									// TODO:save batch
									// eventDAO.save(eventLog);
									eventLog.create();
									if (unsyncRecord) {
										eventLogList.add(eventLogObject);
										eventLogListObject.setEventObjectList(eventLogList);
										ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
									} else {
										duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
										if(duplicate_log == null)
											batchConnection.addBatch(true,eventLog);
									}
									continue;
								}
							}
						}
						
						try {								
							batchConnection.commit();
							batchConnection.close();
							if( dbUtil.isSessionStarted() )
								dbUtil.endSession();
							dbUtil.startSession();
							batchConnection = dbUtil.getConnection(false, key, false);
							break;
						} catch(SQLException e) {
							if(e.toString().indexOf("Deadlock found") != -1) {
								log.warn("Processing PIPE_INFO_TYPE_EVENT_LOG deadlock detected and requeue, sn="+info.getSn());
								WtpMsgHandler.reQueue(json);
							} else {
								log.error("Processing PIPE_INFO_TYPE_EVENT_LOG SQLException, sn="+info.getSn()+", e="+e, e);
								break;
							}
						}

						break;
					}
					case PIPE_INFO_TYPE_DEV_DETAIL:
						break;
					case PIPE_INFO_TYPE_DEV_BANDWIDTH:
						break;
					case PIPE_INFO_TYPE_DEV_USAGE:
					{
						if (deviceUsagesDAO == null) {
							deviceUsagesDAO = new DeviceUsagesDAO(orgId);
						}

						if (deviceOnlineHistoryDAO == null) {
							deviceOnlineHistoryDAO = new DeviceOnlineHistoryDAO(orgId);
						}
						
						if(DeviceUsageExists == null){
							DeviceUsageExists = new HashMap<DeviceUsages, Boolean>();
						}

						object = JSONObject.fromObject(info);
						data = object.getJSONObject(ACUtil.DATA_TAG);
						DevUsageObject devUsageObject = (DevUsageObject) gson.fromJson(data.toString(), DevUsageObject.class);
						List<UsageList> usage_list = devUsageObject.getUsage_list();
						if(usage_list == null)
							break;
						
						devOnlineO = new DevOnlineObject();
						devOnlineO.setIana_id(info.getIana_id());
						devOnlineO.setSn(info.getSn());
						devOnlineO = ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
						DeviceUsages deviceUsages;
						
						for (UsageList usageListItem : usage_list) {
							utcTime = new Date((long) usageListItem.getTimestamp() * 1000);
							curT = new Date();
							
							int utc_unixT = (int)( utcTime.getTime() / 1000 );
							int cur_unixT = (int)( curT.getTime() / 1000 );
							if( utc_unixT > (cur_unixT + 300) )
							{
//								logJson.warnf("Future date record found, record ignored %s %s UTC %d CUR %d",devOnlineO.getSn(),MessageType.PIPE_INFO_TYPE_DEV_USAGE,utc_unixT,cur_unixT);
//								log.infof("Future date record found, record ignored %s %s UTC %d CUR %d",devOnlineO.getSn(),MessageType.PIPE_INFO_TYPE_DEV_USAGE,utc_unixT,cur_unixT);
								continue;
							}
							
							if (usageListItem.getId() == null) {
								usageListItem.setId(0);
								usageListItem.setWan_name("all_wan");
							}
							deviceUsages = deviceUsagesDAO.findByDeviceIdTimeWanId(devOnlineO.getDevice_id(), utcTime, usageListItem.getId());
							if (deviceUsages != null) {
								deviceUsages.setRx(usageListItem.getRx());
								deviceUsages.setTx(usageListItem.getTx());
								deviceUsages.update();
							} else {
								deviceUsages = new DeviceUsages(devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), utcTime, usageListItem.getId(), usageListItem.getWan_name(), usageListItem.getTx(), usageListItem.getRx(), usageListItem.getTimestamp());
								deviceUsages.createIgnore();
							}
							if (DeviceUsageExists.get(deviceUsages) == null || DeviceUsageExists.get(deviceUsages) == false){
//								batchConnection.addBatch(true, deviceUsages);
								batchConnection.addBatch(deviceUsages);
								log.debug("deviceUsages not found in DeviceUsageExists "+DeviceUsageExists.get(deviceUsages)+" "+ deviceUsages+" "+DeviceUsageExists);
								//log.debug("deviceUsages hash = "+deviceUsages.hashCode());
								DeviceUsageExists.put(deviceUsages, true);
							}
						}
						
						try {
							batchConnection.commit();

							if(log.isInfoEnabled()) {
								StringBuffer sb = new StringBuffer();
								for (String stmt : batchConnection.getStatementList()) {
									sb.append(stmt);
									sb.append("\n");
								}
								log.info("DEV_USAGE batch Stmt List: ---------\n" + sb.toString()+"\n---------");
							}

							batchConnection.close();
							if( dbUtil.isSessionStarted() )
								dbUtil.endSession();
							dbUtil.startSession();
							batchConnection = dbUtil.getConnection(false, key, false);
							break;
						} catch(SQLException e) {
							if(e.toString().indexOf("Deadlock found") != -1) {
								log.warn("Processing PIPE_INFO_TYPE_DEV_USAGE deadlock detected and requeue, sn="+info.getSn());
								WtpMsgHandler.reQueue(json);
							} else {
								log.error("Processing PIPE_INFO_TYPE_DEV_USAGE SQLException, sn="+info.getSn()+", e="+e, e);
								break;
							}
						}

						break;
					}

					case PIPE_INFO_TYPE_DEV_LOCATIONS:
//					new table
					{
						log.debug("11810 getGpsLocation -QueueControl case PIPE_INFO_TYPE_DEV_LOCATIONS");
						MessageInfo gpsInfo = new MessageInfo();
						gpsInfo.setJson(json);
						gpsInfo.setOrgId(orgId);
						gpsInfo.setBatchConnection(batchConnection);
						gpsInfo.setDbUtil(dbUtil);
						gpsInfo.setInfo(info);
						gpsInfo.setQueueSize(queue.size());
						
						DeviceGpsReportHandler gpsHandler = new DeviceGpsReportHandler(gpsInfo);
						gpsHandler.persistQueue();
						log.debugf("DeviceGpsReportHandler gpsInfo=%s", gpsInfo);
						break;
					}

//					old table
//					{
//						//Init DAO
//						if (deviceGpsLocaDAO == null) {
//							deviceGpsLocaDAO = new DeviceGpsLocationsDAO(orgId);
//						}
//						
//						if (deviceGpsLocaDatesDAO == null){
//							deviceGpsLocaDatesDAO = new DeviceGpsLocationsDatesDAO(orgId);
//						}
//						
//						if (networksDAO == null){
//							networksDAO = new NetworksDAO(orgId);
//						}
//						
//						try
//						{
//							devOnlineO = new DevOnlineObject();
//							devOnlineO.setIana_id(info.getIana_id());
//							devOnlineO.setSn(info.getSn());
//							devOnlineO = ACUtil.getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
//							if( devOnlineO == null )
//								break;
//							//Parse Json to Object
//							object = JSONObject.fromObject(info);
//							data = object.getJSONObject(ACUtil.DATA_TAG);
//							
//							if(data == null)
//								break;
//							
//							DevLocationsConvertObject devLoc = null;
//							
////							log.info("version of dev loc " + data.getInt("version") );
//							
//							if( data.getInt("version") >= 4 )
//							{
//								SimpleDevLocationsObject simpleDevLoc = gson.fromJson(data.toString(), SimpleDevLocationsObject.class);
//								if(simpleDevLoc != null)
//								{
//									devLoc = new DevLocationsConvertObject();
//									ArrayList<LocationList> locationLst = new ArrayList<LocationList>();
//
//									int n = 1;
//									int qsize=queue.size();
//
//									if(qsize > 3000) {
//										n = 6;
//										log.warnf("Org=%s,  qsize=%d, n=%d, v4+", orgId, qsize, n);
//									} else if(qsize > 2000) {
//										n = 5;
//										log.warnf("Org=%s,  qsize=%d, n=%d, v4+", orgId, qsize, n);
//									} else if(qsize > 1500) {
//										n = 4;
//										log.warnf("Org=%s,  qsize=%d, n=%d, v4+", orgId, qsize, n);
//									} else if(qsize > 1000) {
//										n = 3;
//										log.warnf("Org=%s,  qsize=%d, n=%d, v4+", orgId, qsize, n);
//									} else if(qsize > 500) {
//										n = 2;
//										log.warnf("Org=%s,  qsize=%d, n=%d, v4+", orgId, qsize, n);
//									}									
//									
//									int cnt = 0;
//
////									log.info("Version 4 is existing!");
//									ArrayList<SimpleLocationList> locLst = simpleDevLoc.getLocation_list();
//									for(SimpleLocationList location : locLst)
//									{
//										if( location.getTimestamp() == null )
//											continue;
//										
//										LocationList item = new LocationList();
//										item.setAltitude(location.getAt());
//										item.setH_dop(location.getHd());
//										item.setH_uncertain(location.getHu());
//										item.setLatitude(location.getLa());
//										item.setLongitude(location.getLo());
//										item.setSpeed(location.getSp());
//										item.setStatus(location.getSt());
//										item.setTimestamp(location.getTimestamp().longValue());
//										item.setV_uncertain(location.getVu());
//										item.setFlag(location.getFlag());
//
//										if((cnt % n) == 0)	// skip odd cnt points
//											locationLst.add(item);
//										cnt++;
//
////										locationLst.add(item);
//									}
//									devLoc.setLocation_list(locationLst);
//									devLoc.setVersion(simpleDevLoc.getVersion());
////									log.info("Version 4 converted to others!");
//								}
//							}
//							else
//							{
//								devLoc = gson.fromJson(data.toString(), DevLocationsConvertObject.class);
//								ArrayList<LocationList> locations = devLoc.getLocation_list();
//								
//								if(devLoc.getVersion() == 3)
//								{
//									if( locations != null && !locations.isEmpty() )
//									{
//										if( locations.get(0).getStatus() == null )
//											break;
//									}
//								}
//
//								int n = 2;
//								int qsize=queue.size();
//
//								if(qsize > 3000) {
//									n = 8;
//									log.warnf("Org=%s,  qsize=%d, n=%d, v2/3", orgId, qsize, n);
//								} else if(qsize > 2500) {
//									n = 7;
//									log.warnf("Org=%s,  qsize=%d, n=%d, v2/3", orgId, qsize, n);
//								} else if(qsize > 2000) {
//									n = 6;
//									log.warnf("Org=%s,  qsize=%d, n=%d, v2/3", orgId, qsize, n);
//								} else if(qsize > 1500) {
//									n = 5;
//									log.warnf("Org=%s,  qsize=%d, n=%d, v2/3", orgId, qsize, n);
//								} else if(qsize > 1000) {
//									n = 4;
//									log.warnf("Org=%s,  qsize=%d, n=%d, v2/3", orgId, qsize, n);
//								} else if(qsize > 500) {
//									n = 3;
//									log.warnf("Org=%s,  qsize=%d, n=%d v2/3", orgId, qsize, n);
//								}
//																
//								int cnt = 0;
//								ArrayList<LocationList> newLocations = new ArrayList<LocationList>();
//								for(LocationList loc : locations)
//								{
//									if( loc.getTimestamp() != null && (new Date().getTime() - loc.getTimestamp() * 1000) >= 0) {
//										if((cnt % n) == 0)	// skip odd cnt points, i.e. 1pt / ns
//											newLocations.add(loc);
//										cnt++;
//									}
//								}
//								devLoc.setLocation_list(newLocations);
//								devLoc.setVersion(devLoc.getVersion());
//							}
//							
//							if (devLoc == null) {
//								log.warn("device location object is null for " + info.getSn() + " status: " + info.getStatus());
//								break;
//							}
//
//							if (devLoc.getVersion() == null || devLoc.getVersion() < 2) {
//								log.warn("Device Location with older version is submitted: version = " + devLoc.getVersion());
//								break;
//							}
//
//							if (devLoc.getLocation_list() == null) {
//								log.warn("device location list from Json is null for " + info.getSn() + " status: " + info.getStatus());
//								break;
//							}
//							
//							
//							//Make a location list copy to persist the locations
//							ArrayList<LocationList> locationList = devLoc.getLocation_list();
//							
//							//Begin loop location list
//							HashMap<Integer, Boolean> timestampAlreadyAddedList = new HashMap<Integer, Boolean>();
//							LocationList lastItem = null;
//							boolean isInvalidAdded = false;
//							DeviceGpsLocations[] locationsArray = null;
//							List<DeviceGpsLocations> locationsArrayList = new ArrayList<DeviceGpsLocations>();
//							for( LocationList locItem : locationList )
//							{
//								if (timestampAlreadyAddedList.get(locItem.getTimestamp()) != null) {
//									// already added, skip
//									continue;
//								}
//								
//								// save to database
//								utcTime = new Date((long) locItem.getTimestamp() * 1000);
//								curT = new Date();
//								
//								int utc_unixT = (int)( utcTime.getTime() / 1000 );
//								int cur_unixT = (int)( curT.getTime() / 1000 );
//								if( utc_unixT > (cur_unixT + 300) )
//								{
////									log.infof("Future date record found, record ignored %s %s UTC %d CUR %d",devLoc.getSn(),MessageType.PIPE_INFO_TYPE_DEV_LOCATIONS,utc_unixT,cur_unixT);
//									continue;
//								}
//								
//								DeviceGpsLocationsId dDeviceGpsLocationsId = new DeviceGpsLocationsId(devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), locItem.getTimestamp().intValue());
//								DeviceGpsLocations deviceGpsLocations = null;
//								
//								if( locItem.getStatus() != null && locItem.getStatus() == 0 )
//								{
//									deviceGpsLocations = new DeviceGpsLocations(dDeviceGpsLocationsId, utcTime, locItem.getLatitude(), locItem.getLongitude(),
//											locItem.getAltitude(), locItem.getSpeed(), locItem.getH_uncertain(), locItem.getV_uncertain(), locItem.getH_dop(), locItem.getFlag());
//									
//									//if(isInvalidAdded)
//									isInvalidAdded = false;
//								}
//								else
//								{
//									if( !isInvalidAdded )
//									{
//										deviceGpsLocations = new DeviceGpsLocations(dDeviceGpsLocationsId, utcTime, null, null,
//												null, null, null, null, null, null);
//										isInvalidAdded = true;
//									}
//								}
//								
//								if( deviceGpsLocations == null  )
//								{
//									continue;
//								}
//								
//								if(locItem.getH_uncertain() != null && locItem.getH_uncertain() > 999999)
//									deviceGpsLocations.setHUncertain(999999f);
//								deviceGpsLocations.createIgnore();
//								locationsArrayList.add(deviceGpsLocations);
//								
//								timestampAlreadyAddedList.put(locItem.getTimestamp().intValue(), true);
//								lastItem = locItem;
//							}
//							
//							if(lastItem == null) {
//								log.warn("no valid gps data (" + info.getSn() + " )");
//								break;
//							}
//							
//							if (locationsArrayList!=null && locationsArrayList.size()!=0)
//							{
//								locationsArray = new DeviceGpsLocations[locationsArrayList.size()];
//								int idx = 0;
//								for (DeviceGpsLocations elem:locationsArrayList)
//								{
//									locationsArray[idx++]=elem;
//								}
//								
//								if( locationsArray != null )
//									batchConnection.insertBatch(locationsArray); // with IGNORE
//							}
//							
//							/*** create DeviceGpsLocationsDate ***/
//							Networks network = networksDAO.findById(devOnlineO.getNetwork_id());
//							String timezone = network.getTimezone();
//							String timezoneId = DateUtils.getTimezoneFromId(Integer.valueOf(timezone));
//							TimeZone tz = TimeZone.getTimeZone(timezoneId);
//							int netTime = lastItem.getTimestamp().intValue();
//							int offsetMillis = tz.getOffset((long)netTime*1000);
//							int adjustNetTime = netTime + offsetMillis/1000;
//							adjustNetTime = adjustNetTime/86400 * 86400; //round to day
//							
//							DeviceGpsLocationsDates locaDate = deviceGpsLocaDatesDAO.getDeviceGpsLocationsDates(devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), adjustNetTime);
//							if (locaDate == null){
//								DeviceGpsLocationsDatesId locaDatesId = new DeviceGpsLocationsDatesId();
//								locaDatesId.setDeviceId(devOnlineO.getDevice_id());
//								locaDatesId.setNetworkId(devOnlineO.getNetwork_id());
//								locaDatesId.setUnixtime(adjustNetTime);
//								locaDate = new DeviceGpsLocationsDates();
//								locaDate.setId(locaDatesId);								
//								locaDate.createIgnore();
//								batchConnection.addBatch(true,locaDate);
//								//cache last location
//							}
//
//							DeviceLastLocObject devLastLocation = new DeviceLastLocObject();
//							devLastLocation.setSn(info.getSn());
//							devLastLocation.setIana_id(info.getIana_id());
//							devLastLocation.setLastLocation(lastItem);
//							devLastLocation.setStatic(false);
//							ACUtil.cachePoolObjectBySn(devLastLocation, DeviceLastLocObject.class);
//
//							try {
//								batchConnection.commit();
//								batchConnection.close();
//								if( dbUtil.isSessionStarted() )
//									dbUtil.endSession();
//								dbUtil.startSession();
//								batchConnection = dbUtil.getConnection(false, key, false);
//								break;
//							} catch(SQLException e) {
//								if(e.toString().indexOf("Deadlock found") != -1) {
//									log.warn("Processing DEV_LOCATIONS deadlock detected and requeue, sn="+info.getSn());
//									WtpMsgHandler.reQueue(json);
//								} else {
//									log.error("Processing DEV_LOCATIONS SQLException, sn="+info.getSn()+", e="+e, e);
//									break;
//								}
//							}
//						} catch(Exception e) {
//							log.error("Exception on handle DEV_LOCATIONS ("+info.getSn()+") "+e,e);
//						}
//						break;
//					}
					case PIPE_INFO_TYPE_DEV_USAGE_HIST:

						if (deviceUsagesDAO == null)
						{
							deviceUsagesDAO = new DeviceUsagesDAO(orgId);
						}
						if (dailyDeviceUsagesDAO == null)
						{
							dailyDeviceUsagesDAO = new DailyDeviceUsagesDAO(orgId);
						}
						if (devMonthlyUsagesDAO == null)
						{
							devMonthlyUsagesDAO = new DeviceMonthlyUsagesDAO(orgId);
						}
						if( deviceFeaturesDAO == null )
						{
							deviceFeaturesDAO = new DeviceFeaturesDAO(orgId);
						}

						object = JSONObject.fromObject(info);
						data = object.getJSONObject(ACUtil.DATA_TAG);
						DevTimelyUsageObject devTimelyUsageObject = (DevTimelyUsageObject) gson.fromJson(data.toString(), DevTimelyUsageObject.class);
						CopyOnWriteArrayList<WanList> devUsages = null;
						if (devTimelyUsageObject != null)
						{
//							log.info("Fetch DEV_USAGES_HIST HIST Object : " + devTimelyUsageObject.toString());
							devUsages = devTimelyUsageObject.getWan();
						}

						devOnlineO = new DevOnlineObject();
						devOnlineO.setIana_id(info.getIana_id());
						devOnlineO.setSn(info.getSn());
						devOnlineO = ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineO, DevOnlineObject.class);

						DeviceUsages devUsage = null;
						DailyDeviceUsages dailyDevUsage = null;
						DeviceMonthlyUsages devMonthlyUsage = null;
						if (devUsages != null && devOnlineO  != null)
						{
							
							DeviceFeatures device_feature = deviceFeaturesDAO.findById(devOnlineO.getDevice_id());
							String tz = null;
							Date hDate = null;
							
							if( device_feature != null )
							{
								String feature = device_feature.getFeatureList();
								Pattern pattern = Pattern.compile("\"tz\":\"(\\w+/\\w+)\"");
								Matcher matcher = pattern.matcher(feature);
								if( matcher.find() )
								{
									tz = matcher.group(1);
								}
								else
								{
									pattern = Pattern.compile("\"tz\":\"(\\w+)\"");
									matcher = pattern.matcher(feature);
									if( matcher.find() )
									{
										tz = matcher.group(1);
									}
								}
							}
							
							for (WanList usage : devUsages)
							{
								List<DevTimelyUsageObject.WanList.UsageList> hourlyUsages = usage.getHourly();
								if (hourlyUsages != null)
								{
									for (DevTimelyUsageObject.WanList.UsageList hUsage : hourlyUsages)
									{
										if( tz != null )
											hDate = DateUtils.getUtcDate(sFormat.parse(hUsage.getTime()), tz);
										else
											hDate = sFormat.parse(hUsage.getTime());
										
										curT = new Date();
										
										int utc_unixT = (int)( hDate.getTime() / 1000 );
										int cur_unixT = (int)( curT.getTime() / 1000 );
										if( utc_unixT > (cur_unixT + 300) )
										{
//											logJson.warnf("Future date record found, record ignored %s %s UTC %d CUR %d",devOnlineO.getSn(),MessageType.PIPE_INFO_TYPE_DEV_USAGE_HIST,utc_unixT,cur_unixT);
//											log.infof("Future date record found, record ignored %s %s UTC %d CUR %d",devOnlineO.getSn(),MessageType.PIPE_INFO_TYPE_DEV_USAGE_HIST,utc_unixT,cur_unixT);
											continue;
										}
										
										devUsage = deviceUsagesDAO.findByDeviceIdTimeAndWanId(devOnlineO.getDevice_id(), hDate, usage.getId());
										if (devUsage != null)
										{
											devUsage.setRx(hUsage.getDown());
											devUsage.setTx(hUsage.getUp());
											devUsage.setWan_id(usage.getId());
											devUsage.setWan_name(usage.getName());
											devUsage.update();
											if(log.isDebugEnabled())
												log.debug("updated devUsage! sn=" + devOnlineO.getSn() +", " + sFormat.parse(hUsage.getTime()));
										}
										else
										{
											devUsage = new DeviceUsages();
											devUsage.setWan_id(usage.getId());
											devUsage.setWan_name(usage.getName());
											devUsage.setRx(hUsage.getDown());
											devUsage.setTx(hUsage.getUp());
											
											if( tz != null )
											{
												Date datetime = DateUtils.getUtcDate(sFormat.parse(hUsage.getTime()), tz);
												devUsage.setDatetime(datetime);
												devUsage.setUnixtime((int)(datetime.getTime() / 1000));
											}
											else
											{
												devUsage.setDatetime(sFormat.parse(hUsage.getTime()));
												devUsage.setUnixtime((int) (sFormat.parse(hUsage.getTime()).getTime() / 1000));
											}
											devUsage.setDeviceId(devOnlineO.getDevice_id());
											devUsage.setNetworkId(devOnlineO.getNetwork_id());
											devUsage.createIgnore();
											if(log.isDebugEnabled())
												log.debug("added new devUsage! sn=" + devOnlineO.getSn());
										}
										batchConnection.addBatch(devUsage);
									}
								}

								List<DevTimelyUsageObject.WanList.UsageList> dailyUsages = usage.getDaily();
								if (dailyUsages != null)
								{
									for (DevTimelyUsageObject.WanList.UsageList hUsage : dailyUsages)
									{
										Date datetime = dFormat.parse(hUsage.getTime());
										int unixtime = (int)(datetime.getTime()/1000);
										dailyDevUsage = dailyDeviceUsagesDAO.findByNetworkIdDeviceIdTimeAndWanId(devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), unixtime, usage.getId());
										if (dailyDevUsage != null)
										{
											dailyDevUsage.setRx(hUsage.getDown());
											dailyDevUsage.setTx(hUsage.getUp());
											dailyDevUsage.setWan_id(usage.getId());
											dailyDevUsage.setWan_name(usage.getName());
											dailyDevUsage.update();
											if(log.isDebugEnabled())
												log.debug("devUsage found! " + datetime);
										}
										else
										{
											dailyDevUsage = new DailyDeviceUsages();
											dailyDevUsage.setUnixtime(unixtime);
											dailyDevUsage.setWan_id(usage.getId());
											dailyDevUsage.setWan_name(usage.getName());
											dailyDevUsage.setRx(hUsage.getDown());
											dailyDevUsage.setTx(hUsage.getUp());
											dailyDevUsage.setDatetime(datetime);
											dailyDevUsage.setDeviceId(devOnlineO.getDevice_id());
											dailyDevUsage.setNetworkId(devOnlineO.getNetwork_id());
											dailyDevUsage.createIgnore();
											if(log.isDebugEnabled())
												log.debug("created new devUsage");
										}
										batchConnection.addBatch(dailyDevUsage);
									}
								}

								List<DevTimelyUsageObject.WanList.UsageList> monthlyUsages = usage.getMonthly();
								if (monthlyUsages != null)
								{
									for (DevTimelyUsageObject.WanList.UsageList hUsage : monthlyUsages)
									{
//										curT = new Date();
//										int utc_unixT = (int) (mFormat.parse(hUsage.getTime()).getTime() / 1000);
//										int cur_unixT = (int)( curT.getTime() / 1000 );
//										if( utc_unixT > (cur_unixT + 300) )
//										{
//											logJson.error("Future date record found, record ignored");
//											log.error("Future date record found, record ignored");
//											continue;
//										}
										
										devMonthlyUsage = devMonthlyUsagesDAO.findByNetIdDeviceIdTimeAndWanId(devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), mFormat.parse(hUsage.getTime()), usage.getId());
										if (devMonthlyUsage != null)
										{
											devMonthlyUsage.setRx(hUsage.getDown());
											devMonthlyUsage.setTx(hUsage.getUp());
											devMonthlyUsage.setWan_id(usage.getId());
											devMonthlyUsage.setWan_name(usage.getName());
											devMonthlyUsage.update();
											log.debug("devUsage found! " + mFormat.parse(hUsage.getTime()));
										}
										else
										{
											devMonthlyUsage = new DeviceMonthlyUsages();
											devMonthlyUsage.setUnixtime((int) (mFormat.parse(hUsage.getTime()).getTime() / 1000));
											devMonthlyUsage.setWan_id(usage.getId());
											devMonthlyUsage.setWan_name(usage.getName());
											devMonthlyUsage.setRx(hUsage.getDown());
											devMonthlyUsage.setTx(hUsage.getUp());
											devMonthlyUsage.setDatetime(mFormat.parse(hUsage.getTime()));
											devMonthlyUsage.setDevice_id(devOnlineO.getDevice_id());
											devMonthlyUsage.setNetwork_id(devOnlineO.getNetwork_id());
											devMonthlyUsage.createIgnore();
											log.debug("devUsage not found!");
										}
										batchConnection.addBatch(devMonthlyUsage);
									}
								}

							}
						}

						break;
					case PIPE_INFO_TYPE_STATION_USAGE_HIST:
						if (clientUsagesDAO == null)
						{
							clientUsagesDAO = new ClientUsagesDAO(orgId);
						}
						if (dailyClientUsagesDAO == null)
						{
							dailyClientUsagesDAO = new DailyClientUsagesDAO(orgId);
						}
						if (clientMonthlyUsagesDAO == null)
						{
							clientMonthlyUsagesDAO = new ClientMonthlyUsagesDAO(orgId);
						}
						if(deviceFeaturesDAO == null)
						{
							deviceFeaturesDAO = new DeviceFeaturesDAO(orgId);
						}
						
						object = JSONObject.fromObject(info);
						data = object.getJSONObject(ACUtil.DATA_TAG);
						StationUsageHistoryObject stationUsageHistoryObject = (StationUsageHistoryObject) gson.fromJson(data.toString(), StationUsageHistoryObject.class);

//						log.info("station usage hist object : " + stationUsageHistoryObject);
						
						devOnlineO = new DevOnlineObject();
						devOnlineO.setIana_id(info.getIana_id());
						devOnlineO.setSn(info.getSn());
						devOnlineO = ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineO, DevOnlineObject.class);

						ClientUsages clientUsage = null;
						DailyClientUsages dailyClientUsage = null;
						ClientMonthlyUsages clientMonthlyUsage = null;
						StationListObject stationListObject = null;
						HashMap<String,StationList> ip_mac_map = null;
						HashMap<Long, String> oui_map = null;
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						
						if( devOnlineO != null )
						{
							stationListObject = new StationListObject();
							stationListObject.setSn(devOnlineO.getSn());
							stationListObject.setIana_id(devOnlineO.getIana_id());
							
							stationListObject = ACUtil.getPoolObjectBySn(stationListObject, StationListObject.class);
							if( stationListObject != null )
							{
								List<StationList> stations = stationListObject.getStation_list();
								if( stations != null )
								{
									ip_mac_map = new HashMap<String, StationList>((int)(stations.size() / 0.75 + 1));
									for(StationList station : stations)
									{
										ip_mac_map.put(station.getIp(), station);
									}
								}
							}
							
							OuiInfosDAO ouiDAO = new OuiInfosDAO();
							oui_map = ouiDAO.getOuiInfosMap();
						}
						else
							break; // skip this record if no online object
						
						List<Timelist> hourly = stationUsageHistoryObject.getHourly();
						if (hourly != null)
						{
							log.info("station usage hist hour " + devOnlineO.getDevice_id());
							
							DeviceFeatures device_feature = deviceFeaturesDAO.findById(devOnlineO.getDevice_id());
							log.info("station usage hist get device features "  + device_feature);
							String tz = null;
							Date hDate = null;
							
							if( device_feature != null )
							{
								log.info("station usage hist tz" + device_feature.getTimestamp());
								String feature = device_feature.getFeatureList();
								Pattern pattern = Pattern.compile("\"tz\":\"(\\w+/\\w+)\"");
								Matcher matcher = pattern.matcher(feature);
								if( matcher.find() )
								{
									tz = matcher.group(1);
								}
								else
								{
									pattern = Pattern.compile("\"tz\":\"(\\w+)\"");
									matcher = pattern.matcher(feature);
									if( matcher.find() )
									{
										tz = matcher.group(1);
									}
								}
							}
							else
							{
								log.info("station usage hist df null");
							}
							
							for (Timelist hour : hourly)
							{
								
								if( tz != null )
									hDate = DateUtils.getUtcDate(sFormat.parse(hour.getTime()), tz);
								else
									hDate = sFormat.parse(hour.getTime());
								
								curT = new Date();
								
								int utc_unixT = (int)( hDate.getTime() / 1000 );
								int cur_unixT = (int)( curT.getTime() / 1000 );
								if( utc_unixT > (cur_unixT + 300) )
								{
//									logJson.infof("Future date record found, record ignored %s %s UTC %d CUR %d",devOnlineO.getSn(),MessageType.PIPE_INFO_TYPE_DEV_USAGE_HIST,utc_unixT,cur_unixT);
									log.warnf("Future date record found, record ignored %s %s UTC %d CUR %d",devOnlineO.getSn(),MessageType.PIPE_INFO_TYPE_DEV_USAGE_HIST,utc_unixT,cur_unixT);
									continue;
								}
								
								if( utc_unixT < 1398873600 )
								{
									continue;
								}
								
								List<ClientList> clients = hour.getClient_list();
								if (clients != null)
								{
									for (ClientList client : clients)
									{
										
										clientUsage = new ClientUsages();
										ClientUsagesId clientUsageId = new ClientUsagesId();
										clientUsageId.setId(TableMappingUtil.getInstance().genUUID(UUID_LENGTH));
										clientUsageId.setUnixtime((int) (sFormat.parse(hour.getTime()).getTime() / 1000));
										clientUsage.setId(clientUsageId);
										clientUsage.setDatetime(sFormat.parse(hour.getTime()));
										clientUsage.setDeviceId(devOnlineO.getDevice_id());
										clientUsage.setIp(client.getIp());
										clientUsage.setNetworkId(devOnlineO.getNetwork_id());
										clientUsage.setRx(client.getRx());
										clientUsage.setTx(client.getTx());
										if( ip_mac_map != null )
										{
											StationList station = ip_mac_map.get(client.getIp());
											if( station != null )
											{
												clientUsage.setMac(station.getMac());
												clientUsage.setName(station.getName());
												clientUsage.setType(station.getType());
											}
											else
											{
												clientUsage.setMac(client.getIp());
												clientUsage.setName("");
												clientUsage.setType("ethernet");
											}
										}
										else
										{
											clientUsage.setMac(client.getIp());
											clientUsage.setName("");
											clientUsage.setType("ethernet");
										}
										// clientUsage.setType(client.getType());
										clientUsage.replace();
										batchConnection.addBatch(clientUsage);
										
										if( batchConnection.getBatchSize() >= MAX_BATCH_ADD )
										{
											batchConnection.commit();
											batchConnection.close();
											if( dbUtil.isSessionStarted() )
												dbUtil.endSession();
											dbUtil.startSession();
											// dbUtil.setDebugSQL(true);
											batchConnection = dbUtil.getConnection(false, key, false);
										}
									}
								}
							}
						}

						List<Timelist> daily = stationUsageHistoryObject.getDaily();
						if (daily != null)
						{
							log.info("station usage hist daily");
							for (Timelist day : daily)
							{
								Date date = sdf.parse(day.getTime());
								Integer unixtime = (int)(date.getTime()/1000);
								if( unixtime < 1398873600 ) // 30 Apr 2014
								{
									continue;
								}
								
								List<ClientList> clients = day.getClient_list();
								if (clients != null)
								{
									for (ClientList client : clients)
									{
										log.info("station usage hist current client " + client.getIp());
										dailyClientUsage = new DailyClientUsages();
										dailyClientUsage.setUnixtime((int) (dFormat.parse(day.getTime()).getTime() / 1000));
										dailyClientUsage.setDatetime(dFormat.parse(day.getTime()));
										dailyClientUsage.setDeviceId(devOnlineO.getDevice_id());
										dailyClientUsage.setNetworkId(devOnlineO.getNetwork_id());
										dailyClientUsage.setRx(client.getRx());
										dailyClientUsage.setTx(client.getTx());
										// dailyClientUsage.setType(client.getType());
										dailyClientUsage.setIp(client.getIp());
										if( ip_mac_map != null )
										{
											StationList station = ip_mac_map.get(client.getIp());
											if( station != null )
											{
												dailyClientUsage.setMac(station.getMac());
												dailyClientUsage.setName(station.getName());
												dailyClientUsage.setType(station.getType());
												if( oui_map != null )
												{
													String ouiMac = station.getMac();
													if (ouiMac!=null && ouiMac.length()>8)
														ouiMac = ouiMac.substring(0, 8).replaceAll(":", "");
													ouiMac = ouiMac + "000000";
													Long macLong = Long.parseLong(ouiMac, 16);
													log.info("station usage hist get manufacture" + oui_map.get(macLong));
													dailyClientUsage.setManufacturer(oui_map.get(macLong));
												}
											}
											else
											{
												dailyClientUsage.setMac(client.getIp());
												dailyClientUsage.setName("");
												dailyClientUsage.setType("ethernet");
											}
										}
										else
										{
											dailyClientUsage.setMac(client.getIp());
											dailyClientUsage.setName("");
											dailyClientUsage.setType("ethernet");
										}
										dailyClientUsage.replace();
										batchConnection.addBatch(dailyClientUsage);
										
										if( batchConnection.getBatchSize() >= MAX_BATCH_ADD )
										{
											batchConnection.commit();
											batchConnection.close();
											if( dbUtil.isSessionStarted() )
												dbUtil.endSession();
											dbUtil.startSession();
											// dbUtil.setDebugSQL(true);
											batchConnection = dbUtil.getConnection(false, key, false);
										}
									}
								}
							}
						}

						List<Timelist> monthly = stationUsageHistoryObject.getMonthly();
						if (monthly != null)
						{
							log.info("station usage hist month");
							
							for (Timelist month : monthly)
							{
								Date date = sdf.parse(month.getTime());
								Integer unixtime = (int)(date.getTime()/1000);
								if( unixtime < 1398873600 )
								{
									continue;
								}
								
								List<ClientList> clients = month.getClient_list();
								if (clients != null)
								{
									for (ClientList client : clients)
									{
										clientMonthlyUsage = new ClientMonthlyUsages();
										clientMonthlyUsage.setUnixtime((int) (mFormat.parse(month.getTime()).getTime() / 1000));
										clientMonthlyUsage.setDeviceId(devOnlineO.getDevice_id());
										clientMonthlyUsage.setNetworkId(devOnlineO.getNetwork_id());
										clientMonthlyUsage.setIp(client.getIp());
										clientMonthlyUsage.setDatetime(mFormat.parse(month.getTime()));
										clientMonthlyUsage.setRx(client.getRx());
										clientMonthlyUsage.setTx(client.getTx());
										if( ip_mac_map != null )
										{
											StationList station = ip_mac_map.get(client.getIp());
											if( station != null )
											{
												clientMonthlyUsage.setMac(station.getMac());
												clientMonthlyUsage.setType(station.getType());
											}
											else
											{
												clientMonthlyUsage.setMac(client.getIp());
												clientMonthlyUsage.setType("ethernet");
											}
										}
										else
										{
											clientMonthlyUsage.setMac(client.getIp());
											clientMonthlyUsage.setType("ethernet");
										}
										// clientMonthlyUsage.setType(client.getType());
										clientMonthlyUsage.replace();
										batchConnection.addBatch(clientMonthlyUsage);
										
										if( batchConnection.getBatchSize() >= MAX_BATCH_ADD )
										{
											batchConnection.commit();
											batchConnection.close();
											if( dbUtil.isSessionStarted() )
												dbUtil.endSession();
											dbUtil.startSession();
											// dbUtil.setDebugSQL(true);
											batchConnection = dbUtil.getConnection(false, key, false);
										}
									}
								}
							}
						}

						break;
					case PIPE_INFO_TYPE_DEV_SSID_USAGES:
					{
						if (devSsidUsageDAO == null) {
							devSsidUsageDAO = new DeviceSsidUsagesDAO(orgId);
						}
						object = JSONObject.fromObject(info);
						data = object.getJSONObject(ACUtil.DATA_TAG);
						DevSsidUsagesObject devSsidUsagesObject = (DevSsidUsagesObject) gson.fromJson(data.toString(), DevSsidUsagesObject.class);
						List<VapStatList> vap_stat_list = devSsidUsagesObject.getVap_stat_list();

						devOnlineO = new DevOnlineObject();
						devOnlineO.setIana_id(info.getIana_id());
						devOnlineO.setSn(info.getSn());
						devOnlineO = ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineO, DevOnlineObject.class);

						DeviceSsidUsages devSsidUsages;
						if (vap_stat_list != null && devOnlineO!=null) {
							for (VapStatList vapStatListItem : vap_stat_list) {
								if (vapStatListItem != null) {
									utcTime = new Date((long) vapStatListItem.getTimestamp() * 1000);
									curT = new Date();
									
									int utc_unixT = (int)( utcTime.getTime() / 1000 );
									int cur_unixT = (int)( curT.getTime() / 1000 );
									if( utc_unixT > (cur_unixT + 300) )
									{
//										logJson.warnf("Future date record found, record ignored %s %s UTC %d CUR %d",devOnlineO.getSn(),MessageType.PIPE_INFO_TYPE_DEV_USAGE_HIST,utc_unixT,cur_unixT);
										log.infof("Future date record found, record ignored %s %s UTC %d CUR %d",devOnlineO.getSn(),MessageType.PIPE_INFO_TYPE_DEV_USAGE_HIST,utc_unixT,cur_unixT);
										continue;
									}
									devSsidUsages = devSsidUsageDAO.findByDeviceIdEssidEncryptionTime(devOnlineO.getDevice_id(), vapStatListItem.getEssid(), vapStatListItem.getEncryption(), vapStatListItem.getTimestamp());
									if (devSsidUsages != null) {
										devSsidUsages.setRx(vapStatListItem.getRx());
										devSsidUsages.setTx(vapStatListItem.getTx());
										devSsidUsages.update();
										if(log.isDebugEnabled())
											log.debug("devSsidUsages found,  sn="+devOnlineO.getSn());
									} else {
										devSsidUsages = new DeviceSsidUsages(devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), utcTime, vapStatListItem.getEssid(), vapStatListItem.getEncryption(), vapStatListItem.getTx(), vapStatListItem.getRx(), vapStatListItem.getTimestamp());
										devSsidUsages.createIgnore();
										if(log.isDebugEnabled())
											log.debug("added new devSsidUsages, sn="+devOnlineO.getSn());
									}
									// TODO:save batch
									// devSsidUsageDAO.save(devSsidUsages);

									batchConnection.addBatch(devSsidUsages);
								}
							}
							try {
								batchConnection.commit();
								batchConnection.close();
								if( dbUtil.isSessionStarted() )
									dbUtil.endSession();
								dbUtil.startSession();
								batchConnection = dbUtil.getConnection(false, key, false);
								break;
							} catch(SQLException e) {
								if(e.toString().indexOf("Deadlock found") != -1) {
									log.warn("Processing PIPE_INFO_TYPE_DEV_SSID_USAGES deadlock detected and requeue, sn="+info.getSn());
									WtpMsgHandler.reQueue(json);
								} else {
									log.error("Processing PIPE_INFO_TYPE_DEV_SSID_USAGES SQLException, sn="+info.getSn()+", e="+e, e);
									break;
								}
							}
						}
						break;
					}
					case PIPE_INFO_TYPE_DEV_CHANNEL_UTIL:
						if (devicesChannelUtilizationsDAO == null) {
							devicesChannelUtilizationsDAO = new DevicesChannelUtilizationsDAO(orgId);
						}
						object = JSONObject.fromObject(info);
						data = object.getJSONObject(ACUtil.DATA_TAG);
						DevChannelUtilObject devChannelObject = (DevChannelUtilObject) gson.fromJson(data.toString(), DevChannelUtilObject.class);
						devOnlineO = new DevOnlineObject();
						devOnlineO.setIana_id(info.getIana_id());
						devOnlineO.setSn(info.getSn());
						devOnlineO = ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineO, DevOnlineObject.class);

						utcTime = new Date((long) devChannelObject.getTimestamp() * 1000);
						DevicesChannelUtilizations devChannelUtil = new DevicesChannelUtilizations(devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), utcTime, devChannelObject.getTimestamp(), devChannelObject.getChannel_utilization());
						// TODO:save batch
						// devicesChannelUtilizationsDAO.save(devChannelUtil);
						devChannelUtil.create();
						batchConnection.addBatch(true,devChannelUtil);

						break;
					case PIPE_INFO_TYPE_DEV_WLAN:
						break;
					case PIPE_INFO_TYPE_DEV_TCPDUMP:
						break;
					case PIPE_INFO_TYPE_STATION_LIST:
						break;
					case PIPE_INFO_TYPE_STATION_BANDWIDTH_LIST:
						break;
					case PIPE_INFO_TYPE_PEPVPN_ENDPOINT:
						break;
					case PIPE_INFO_TYPE_PEPVPN_PEER_LIST://ADD for EndpointV2
						break;
					case PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL:
						break;
					case PIPE_INFO_TYPE_PEPVPN_PEER_UPDATE:
						break;
					case PIPE_INFO_TYPE_PEPVPN_TUNNEL_STAT:
						break;
					// case PIPE_INFO_TYPE_CONFIG_CHECKSUM:
					// break;
					case PIPE_INFO_TYPE_CONFIG_GET:
					case PIPE_INFO_TYPE_CONFIG_GET_TEXT:
						break;
					case PIPE_INFO_TYPE_CONFIG_PUT:
						break;
					case PIPE_INFO_TYPE_FIRMWARE_PUT:
						break;
					case PIPE_INFO_TYPE_FEATURE_GET:
						devOnlineO = new DevOnlineObject();
						devOnlineO.setIana_id(info.getIana_id());
						devOnlineO.setSn(info.getSn());
						devOnlineO = ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
						if (deviceFeaturesDAO == null) {
							deviceFeaturesDAO = new DeviceFeaturesDAO(orgId);
						}
						
						if (devOnlineO != null) {
							deviceDAO = new DevicesDAO(orgId);
							networksDAO = new NetworksDAO(orgId);
							devices = deviceDAO.findById(devOnlineO.getDevice_id());
							if (devices != null) {
								DeviceFeatures devicefeatures;
								devicefeatures = deviceFeaturesDAO.findById(devOnlineO.getDevice_id());
								if (devicefeatures == null) {
									devicefeatures = new DeviceFeatures(devices);
								}
								object = JSONObject.fromObject(info);
								String featureString = object.toString();
								log.debug("feature = " + featureString);
								Json_Device_Feature json_device_feature = null;
								Json_Feature_List json_feature_list = null;
								if (object.has(ACUtil.DATA_TAG)) {
									data = object.getJSONObject(ACUtil.DATA_TAG);
									json_device_feature = JsonUtils.fromJson(data.toString(),Json_Device_Feature.class);
								} else {
									log.error("QueueControl case PIPE_INFO_TYPE_FEATURE_GET: no DATA_TAG, info = "+ info);
									break;
								}
								if (json_device_feature != null)
									json_feature_list = json_device_feature.getFeature();									
								
								Networks net = networksDAO.findById(devOnlineO.getNetwork_id());
								NetworkUtils.NetFeature netFeature = new NetworkUtils.NetFeature(net.getFeatures());
															
								/* netFeature: portal_ic2 */ 
								if (json_feature_list != null && json_feature_list.getPortal_ic2() != null && json_feature_list.getPortal_ic2()) {
									log.debug("Portal_ic2 = true case: insert portal_ic2 to networks.feature "+json_feature_list.getPortal_ic2());
									netFeature.addFeature(NetworkUtils.FEATURE.portal_ic2);
								} else {
									log.debug("Portal_ic2 = false, case: nothing to do");
								}
								
								/* netFeature: map */
								/* deviceFeature: gps */
								Integer version = data.getInt("version");
								boolean isGpsSupport = FeatureGetUtils.isGPSFeatureSupport(featureString, version);
								if(isGpsSupport) {
									devicefeatures.setGps_support(true);
									netFeature.addFeature(NetworkUtils.FEATURE.map);									
								} else {
									devicefeatures.setGps_support(false);
								}															
																
								/* deviceFeature: MvpnLicense */
								if (json_feature_list != null && json_feature_list.getMvpn() != null && json_feature_list.getMvpn().getLicense() != null){
									devicefeatures.setMvpnLicense(json_feature_list.getMvpn().getLicense());
								} else {
									devicefeatures.setMvpnLicense(-1);
								}
								log.debugf("devicefeatures.setMvpnLicense= %d", devicefeatures.getMvpnLicense());
								

								/* deviceFeature: WanCount */	
								Pattern pattern = Pattern.compile("\"wifi_ssid\":(\\d+)");
								Matcher matcher = pattern.matcher(featureString);
								if (matcher.find()) {
									devicefeatures.setWifiSsid(Integer.valueOf(matcher.group(1)));
								}
								
								/* deviceFeature: WifiSsid */								
								pattern = Pattern.compile("\"wan_cnt\":(\\d+)");
								matcher = pattern.matcher(featureString);
								if (matcher.find()) {
									devicefeatures.setWanCount(Integer.valueOf(matcher.group(1)));
								}
							
								/* deviceFeature: Wan */			
								pattern = Pattern.compile("wan\\d\":\"(\\w+)\"");
								matcher = pattern.matcher(featureString);
								String wan = new String();
								while (matcher.find()) {
									log.debug("matcher.groupCount()" + matcher.groupCount());
									for (int index = 1; index <= matcher.groupCount(); index++) {
										wan = wan + matcher.group(index) + "|";
										log.debug("wan" + wan);
										log.debug("matcher.group(index)" + matcher.group(index));
									}
								}
								devicefeatures.setWan(wan);
								
								/* ************* update network feature ******* */
								net.setFeatures(netFeature.getValue());
								networksDAO.saveOrUpdate(net);
								
								/* ************* update device feature ******* */
								FeatureGetObject featureGetObjectWithFeatureStr = null;
								String radioSupport = new String();
								featureGetObjectWithFeatureStr = FeatureGetUtils.createFeatureGetObject(info);
								featureGetObjectWithFeatureStr.setFeatures(null);	// omit features string for db
								radioSupport = JsonUtils.toJsonCompact(featureGetObjectWithFeatureStr);
								devicefeatures.setRadioSupport(radioSupport);
								devicefeatures.setFeatureList(featureString);
								devicefeatures.setTimestamp(DateUtils.getUtcDate());
								log.debug("devicefeatures = " + devicefeatures);
								devicefeatures.saveOrUpdate();
								batchConnection.addBatch(true,devicefeatures);								
							}
						}
						break;
					case PIPE_INFO_TYPE_UPDATE_AUTO_CHANNEL:
						break;
					case PIPE_INFO_TYPE_DUMMY_TEST:
						break;
					case PIPE_INFO_TYPE_NDPI:
						if( deviceDpiUsagesDAO == null )
						{
							deviceDpiUsagesDAO = new DeviceDpiUsagesDAO(orgId);
						}
						
						devOnlineO = new DevOnlineObject();
						devOnlineO.setIana_id(info.getIana_id());
						devOnlineO.setSn(info.getSn());
						devOnlineO = ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
						
						object = JSONObject.fromObject(info);
						data = object.getJSONObject(ACUtil.DATA_TAG);
						data = data.getJSONObject(ACUtil.DATA_TAG);
						log.info("NDPI DATA JSON:"+data);
						HourlyNdpiUsageObject hourlyNdpiUsageObject = (HourlyNdpiUsageObject) gson.fromJson(data.toString(), HourlyNdpiUsageObject.class);
						List<Protocols> protocolLst = null;
						if( hourlyNdpiUsageObject != null )
						{
							DeviceDpiUsages hourlyDpiUsage = null;
							protocolLst = hourlyNdpiUsageObject.getProtocols();
							if( protocolLst != null )
							{
								for( Protocols protocol : protocolLst )
								{
									hourlyDpiUsage = deviceDpiUsagesDAO.findByNetworkIdDeviceIdUnixtimeAndProtocol(devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), hourlyNdpiUsageObject.getBegin_sec(), protocol.getProtocol());
									if( hourlyDpiUsage != null )
									{
										hourlyDpiUsage.setNetwork_id(devOnlineO.getNetwork_id());
										hourlyDpiUsage.setDevice_id(devOnlineO.getDevice_id());
										hourlyDpiUsage.setUnixtime(hourlyNdpiUsageObject.getBegin_sec());
										hourlyDpiUsage.setService(protocol.getProtocol());
										hourlyDpiUsage.setSize(protocol.getBytes());
										hourlyDpiUsage.update();
									}
									else
									{
										hourlyDpiUsage = new DeviceDpiUsages();
										hourlyDpiUsage.setNetwork_id(devOnlineO.getNetwork_id());
										hourlyDpiUsage.setDevice_id(devOnlineO.getDevice_id());
										hourlyDpiUsage.setUnixtime(hourlyNdpiUsageObject.getBegin_sec());
										hourlyDpiUsage.setService(protocol.getProtocol());
										hourlyDpiUsage.setSize(protocol.getBytes());
										hourlyDpiUsage.createIgnore();
									}
									batchConnection.addBatch(hourlyDpiUsage);
								}
							}
						}
						
						break;
					case PIPE_INFO_TYPE_DEV_USAGE_CONSOLIDATE:
						if( deviceUsagesDAO == null )
						{
							deviceUsagesDAO = new DeviceUsagesDAO(orgId);
						}
						if( dailyDeviceUsagesDAO == null )
						{
							dailyDeviceUsagesDAO = new DailyDeviceUsagesDAO(orgId);
						}
						if( devMonthlyUsagesDAO == null )
						{
							devMonthlyUsagesDAO = new DeviceMonthlyUsagesDAO(orgId);
						}
						
						devOnlineO = new DevOnlineObject();
						
						devOnlineO = new DevOnlineObject();
						devOnlineO.setIana_id(info.getIana_id());
						devOnlineO.setSn(info.getSn());
						devOnlineO = ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
						
						if( devOnlineO != null )
						{
							object = JSONObject.fromObject(info);
							data = object.getJSONObject(ACUtil.DATA_TAG);
							JSONObject response = data.getJSONObject("response");
							log.info("Dev usage consolidate json : " + data.toString());
							//Parse json of DEV_USAGE_CONSOLIDATE
							DevUsageConsolidateObject devUsageConsolidateO = DevUsageConsolidateObject.getInstance(response);
							if(devUsageConsolidateO != null)
							{
								SimpleDateFormat formatHour = new SimpleDateFormat("yyyy-MM-dd-HH");
								SimpleDateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
								SimpleDateFormat formatMth = new SimpleDateFormat("yyyy-MM");
								Integer offset = devUsageConsolidateO.getTz_offset();
								//Persist hourly device usage consolidate info
								ConsolidateObject hourlyO = devUsageConsolidateO.getHourly();
								List<ConsolidateUsages> hourlyUsages = hourlyO.getWanUsages();
								if(hourlyUsages != null)
								{
									for( ConsolidateUsages usage : hourlyUsages )
									{
										List<Usages> usages = usage.getUsage();
										Integer wanId = usage.getId();
										String name = usage.getName();
										if( usages != null )
										{
											for( Usages hourlyUsage : usages )
											{
												Date datetime = formatHour.parse(hourlyUsage.getDate());
												datetime = new Date(datetime.getTime() - offset*1000L); // usage in device's local timezone offset -> GMT
												Integer unixtime = (int)(datetime.getTime()/1000);
												DeviceUsages deviceHourlyUsage = deviceUsagesDAO.findDuplicateUsages(devOnlineO.getDevice_id(), unixtime, devOnlineO.getNetwork_id(),wanId);
												if( deviceHourlyUsage != null )
												{
													deviceHourlyUsage.setWan_name(name);
													deviceHourlyUsage.setRx(hourlyUsage.getDown());
													deviceHourlyUsage.setTx(hourlyUsage.getUp());
													deviceHourlyUsage.update();
													if(log.isInfoEnabled())
														log.infof("updated hourly: usage="+deviceHourlyUsage);
												}
												else
												{
													deviceHourlyUsage = new DeviceUsages();
													deviceHourlyUsage.setUnixtime(unixtime);
													deviceHourlyUsage.setDeviceId(devOnlineO.getDevice_id());
													deviceHourlyUsage.setNetworkId(devOnlineO.getNetwork_id());
													deviceHourlyUsage.setWan_id(wanId);
													deviceHourlyUsage.setWan_name(name);
													deviceHourlyUsage.setDatetime(datetime);
													deviceHourlyUsage.setRx(hourlyUsage.getDown());
													deviceHourlyUsage.setTx(hourlyUsage.getUp());
													deviceHourlyUsage.create();
													if(log.isInfoEnabled())
														log.infof("added new hourly: usage="+deviceHourlyUsage);
												}
												batchConnection.addBatch(deviceHourlyUsage);
											}
										}
									}
								}
								
								//Persist daily device usage consolidate info
								ConsolidateObject dailyO = devUsageConsolidateO.getDaily();
								if(dailyO != null)
								{
									List<ConsolidateUsages> dailyUsages = dailyO.getWanUsages();
									if(dailyUsages != null)
									{
										for(ConsolidateUsages usage : dailyUsages)
										{
											if(usage != null)
											{
												Integer wanId = usage.getId();
												String name = usage.getName();
												List<Usages> usages = usage.getUsage();
												if(usages != null)
												{
													for(Usages dailyUsage : usages)
													{
														Date datetime = formatDay.parse(dailyUsage.getDate());
														int unixtime = (int)(datetime.getTime()/1000);
														DailyDeviceUsages deviceDailyUsage = dailyDeviceUsagesDAO.findByNetworkIdDeviceIdTimeAndWanId(devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), unixtime, wanId);
														if( deviceDailyUsage != null )
														{
															deviceDailyUsage.setWan_name(name);
															deviceDailyUsage.setRx(dailyUsage.getDown());
															deviceDailyUsage.setTx(dailyUsage.getUp());
															deviceDailyUsage.update();
															if(log.isInfoEnabled())
																log.infof("updated daily: usage="+deviceDailyUsage);
														}
														else
														{
															deviceDailyUsage = new DailyDeviceUsages();
															deviceDailyUsage.setUnixtime(unixtime);
															deviceDailyUsage.setDeviceId(devOnlineO.getDevice_id());
															deviceDailyUsage.setNetworkId(devOnlineO.getNetwork_id());
															deviceDailyUsage.setWan_id(wanId);
															deviceDailyUsage.setWan_name(name);
															deviceDailyUsage.setRx(dailyUsage.getDown());
															deviceDailyUsage.setTx(dailyUsage.getUp());
															deviceDailyUsage.setDatetime(datetime);
															deviceDailyUsage.create();
															if(log.isInfoEnabled())
																log.infof("added daily: usage="+deviceDailyUsage);
														}
														batchConnection.addBatch(deviceDailyUsage);
													}
												}
											}
										}
									}
								}
								
								//Persist monthly device usage consolidate info
								ConsolidateObject monthlyO = devUsageConsolidateO.getMonthly();
								if( monthlyO != null )
								{
									List<ConsolidateUsages> monthlyUsages = monthlyO.getWanUsages();
									if( monthlyUsages != null )
									{
										for(ConsolidateUsages usage : monthlyUsages)
										{
											Integer wanId = usage.getId();
											String name = usage.getName();
											List<Usages> usages = usage.getUsage();
											if(usages != null)
											{
												for( Usages monthlyUsage : usages )
												{
													Date datetime = formatMth.parse(monthlyUsage.getDate());
													DeviceMonthlyUsages deviceMonthlyUsages = devMonthlyUsagesDAO.findByNetworkIdDeviceIdTimeAndWanId(devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), datetime, wanId);
													if( deviceMonthlyUsages != null )
													{
														deviceMonthlyUsages.setWan_name(name);
														deviceMonthlyUsages.setRx(monthlyUsage.getDown());
														deviceMonthlyUsages.setTx(monthlyUsage.getUp());
														deviceMonthlyUsages.update();
														if(log.isInfoEnabled())
															log.infof("updated monthly: usage="+deviceMonthlyUsages);
													}
													else
													{
														deviceMonthlyUsages = new DeviceMonthlyUsages();
														deviceMonthlyUsages.setUnixtime((int)(datetime.getTime()/1000));
														deviceMonthlyUsages.setDevice_id(devOnlineO.getDevice_id());
														deviceMonthlyUsages.setNetwork_id(devOnlineO.getNetwork_id());
														deviceMonthlyUsages.setWan_id(wanId);
														deviceMonthlyUsages.setWan_name(name);
														deviceMonthlyUsages.setRx(monthlyUsage.getDown());
														deviceMonthlyUsages.setTx(monthlyUsage.getUp());
														deviceMonthlyUsages.setDatetime(datetime);
														deviceMonthlyUsages.createIgnore();
														if(log.isInfoEnabled())
															log.infof("added monthly: usage="+deviceMonthlyUsages);
													}
													batchConnection.addBatch(deviceMonthlyUsages);
													batchConnection.getBatchList();
												}
											}
										}
									}
								}
								try {
									batchConnection.commit();
									
									if(log.isInfoEnabled()) {
										StringBuffer sb = new StringBuffer();
										for (String stmt : batchConnection.getStatementList()) {
											sb.append(stmt);
											sb.append("\n");
										}
										log.info("Batch Stmt List: ---------\n" + sb.toString()+"\n---------");
									}
									
									batchConnection.close();
									if( dbUtil.isSessionStarted() )
										dbUtil.endSession();
									dbUtil.startSession();
									batchConnection = dbUtil.getConnection(false, key, false);
									break;
								} catch(SQLException e) {
									if(e.toString().indexOf("Deadlock found") != -1) {
										log.warn("Processing PIPE_INFO_TYPE_DEV_USAGE_CONSOLIDATE deadlock detected and requeue, sn="+info.getSn());
										WtpMsgHandler.reQueue(json);
									} else {
										log.error("Processing PIPE_INFO_TYPE_DEV_USAGE_CONSOLIDATE SQLException, sn="+info.getSn()+", e="+e, e);
										break;
									}
								}
							}
						}
						
						break;
//					case PIPE_INFO_TYPE_WAN_IP_INFO:
//						// get online object by cache
//						devOnlineO = new DevOnlineObject();
//						devOnlineO.setIana_id(info.getIana_id());
//						devOnlineO.setSn(info.getSn());
//						devOnlineO = ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
//						log.debug("QueueControl PIPE_INFO_TYPE_WAN_IP_INFO devOnlineO = " + devOnlineO);
//						
//						object = JSONObject.fromObject(info);
//						data = object.getJSONObject(ACUtil.DATA_TAG);
//						
//						if (devOnlineO != null){
//							DdnsMessageHandler.doDdnsWanIpInfoMessageHandler(devOnlineO, data.toString());
//						}
//						
//						break;
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
						log.warn("Unknown report type from queue - " + type + "(" + tmpType + ") " + json);
						break;

					default:
						log.warn("Shouldnt reach here " + type);
						break;
					}
					
					qtaskInfo.endTask(orgId, mt);
					
				} catch (Exception eJson)
				{
					log.error("error "+(json.length()<=100?json:json.substring(0, 100)), eJson);
					if (qtaskInfo.isStarted())
						qtaskInfo.endTask(orgId, mt);
					continue;
				}
				
				taskTime = DateUtils.getUnixtime() - globalstarttime;
				if(queue.size() > ABNORMAL_QUEUE_SIZE) {
					if( batchConnection.getBatchSize() >= MAX_BATCH_ADD ) {
						log.warnf("Abnormal queue size=%d, MAX_BATCH_ADD commit, orgId=%s", queue.size(), orgId);
						int retry_cnt = 0;
						while(retry_cnt < MAX_RECOMMIT_RETRY) {
							try {
								retry_cnt ++;
								batchConnection.commit();
								batchConnection.close();
								if( dbUtil.isSessionStarted() )
									dbUtil.endSession();
								dbUtil.startSession();
								batchConnection = dbUtil.getConnection(false, key, false);
								break;
							} catch(SQLException e) {
								if(e.toString().indexOf("Deadlock found") != -1) {
									log.warnf("MAX_BATCH_ADD commit deadlock detected, wait and re-commit(), cnt="+retry_cnt+", orgId="+orgId);
									try {
										java.lang.Thread.sleep(RECOMMIT_WAIT);
									} catch(Exception e2) {
									}
								} else {
									log.error("MAX_BATCH_ADD commit SQLException, orgId="+orgId+", e="+e, e);
									StringBuffer sb = new StringBuffer();
									for (String stmt : batchConnection.getStatementList()) {
										sb.append(stmt);
										sb.append("\n");
									}
									log.error("MAX_BATCH_ADD All rollback Json message logged", e, e);
									log.error("MAX_BATCH_ADD Batch Error : " + sb.toString());
									break;
								}
							}
						}
					}
				} else {
					if( taskTime > MAX_TASK_TIME) {
						log.warnf("[%s] Task handling overtime breakout! total task processing time takes %d seconds > MAX_TASK_TIME, last mt=%s", orgId, taskTime, mt);
						break; // if queue is normal, then breakout
					}
				}
			} // end for loop
		} catch (Exception e) {
			log.error("Exception, exit loop! orgId="+orgId+" "+e, e);
		} finally {
			int retry_cnt = 0;
			while(retry_cnt < MAX_RECOMMIT_RETRY) {
				try {
					retry_cnt ++;
					batchConnection.commit();
					batchConnection.close();
					break;
				} catch(SQLException e) {
					if(e.toString().indexOf("Deadlock found") != -1) {
						log.warnf("FINALLY commit deadlock detected, wait and re-commit(), cnt="+retry_cnt+", orgId="+orgId);
						try {
							java.lang.Thread.sleep(RECOMMIT_WAIT);
						} catch(Exception e2) {
						}
					} else {
						log.error("FINALLY commit SQLException, orgId="+orgId+", e="+e, e);
						StringBuffer sb = new StringBuffer();
						for (String stmt : batchConnection.getStatementList()) {
							sb.append(stmt);
							sb.append("\n");
						}
						log.error("All rollback Json message logged", e, e);
						log.error("Batch Error : " + sb.toString());
						break;
					}
				}
			}
			try {
				log.infof("taskName %s endSession", taskName);
				if( dbUtil.isSessionStarted() )
					dbUtil.endSession();
			} catch (Exception e) {
				log.error("Error in endSession", e);
			} finally {
				setCounter(false);
			}
			endtime = DateUtils.getUnixtime();
			int callUsedTime = endtime - globalstarttime;
			if (callUsedTime > OVETTIME_THRESHOLD)				
				log.warnf("[%s] persistQueue overtime, total=%d sec, task=%d sec, db=%d sec", orgId, callUsedTime, taskTime, callUsedTime-taskTime);
		}
		if(log.isInfoEnabled())
			log.infof("persistQueue is successful! %s (counter=%d)", taskName, counter);
	}
	
	public static void main(String[] args)
	{
		BufferedReader br = null;
		String orgId = "iODv99";
		StringBuffer sb = new StringBuffer();
		try {
			br = new BufferedReader(new FileReader("C://0663.txt"));
			String line;
			while ((line = br.readLine()) != null ) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(br!=null) {
				try {
					br.close();
				} catch(Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		String json = sb.toString();
		log.info("json="+json);
		Gson gson = new Gson();
		QueryInfo<Object> info = gson.fromJson(json, QueryInfo.class);
		JSONObject object = JSONObject.fromObject(info);
		JSONObject data = object.getJSONObject(ACUtil.DATA_TAG);
		JSONObject response = data.getJSONObject("response");
		
		int dev_id = 7;
		int net_id = 3;
		
		log.info("Dev usage consolidate response json : " + response.toString());

		DevUsageConsolidateObject devUsageConsolidateO = DevUsageConsolidateObject.getInstance(response);
		if(devUsageConsolidateO != null) {
			DBUtil dbUtil = null;
			DBConnection batchConnection = null;

			try {
				DeviceUsagesDAO deviceUsagesDAO = new DeviceUsagesDAO(orgId);
				DailyDeviceUsagesDAO dailyDeviceUsagesDAO = new DailyDeviceUsagesDAO(orgId);
				DeviceMonthlyUsagesDAO devMonthlyUsagesDAO = new DeviceMonthlyUsagesDAO(orgId);
				
				dbUtil = DBUtil.getInstance();
				dbUtil.startSession();
				batchConnection = dbUtil.getConnection(false, orgId, false);
	
				SimpleDateFormat formatHour = new SimpleDateFormat("yyyy-MM-dd-HH");
				SimpleDateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat formatMth = new SimpleDateFormat("yyyy-MM");
				Integer offset = devUsageConsolidateO.getTz_offset();
				//Persist hourly device usage consolidate info
				ConsolidateObject hourlyO = devUsageConsolidateO.getHourly();
				List<ConsolidateUsages> hourlyUsages = hourlyO.getWanUsages();
				if(hourlyUsages != null)
				{
					for( ConsolidateUsages usage : hourlyUsages )
					{
						List<Usages> usages = usage.getUsage();
						Integer wanId = usage.getId();
						String name = usage.getName();
						if( usages != null )
						{
							for( Usages hourlyUsage : usages )
							{
								Date datetime = formatHour.parse(hourlyUsage.getDate());
								datetime = new Date(datetime.getTime() - offset*1000L); // usage in device's local timezone offset -> GMT
								Integer unixtime = (int)(datetime.getTime()/1000);
								DeviceUsages deviceHourlyUsage = deviceUsagesDAO.findDuplicateUsages(dev_id, unixtime, net_id, wanId);
								if( deviceHourlyUsage != null )
								{
									deviceHourlyUsage.setWan_name(name);
									deviceHourlyUsage.setRx(hourlyUsage.getDown());
									deviceHourlyUsage.setTx(hourlyUsage.getUp());
									deviceHourlyUsage.update();
								}
								else
								{
									deviceHourlyUsage = new DeviceUsages();
									deviceHourlyUsage.setUnixtime(unixtime);
									deviceHourlyUsage.setDeviceId(dev_id);
									deviceHourlyUsage.setNetworkId(net_id);
									deviceHourlyUsage.setWan_id(wanId);
									deviceHourlyUsage.setWan_name(name);
									deviceHourlyUsage.setDatetime(datetime);
									deviceHourlyUsage.setRx(hourlyUsage.getDown());
									deviceHourlyUsage.setTx(hourlyUsage.getUp());
									deviceHourlyUsage.createIgnore();
								}
								log.info("hourly = " + deviceHourlyUsage);
								batchConnection.addBatch(deviceHourlyUsage);
							}
						}
					}
				}
				
				//Persist daily device usage consolidate info
				ConsolidateObject dailyO = devUsageConsolidateO.getDaily();
				if(dailyO != null)
				{
					List<ConsolidateUsages> dailyUsages = dailyO.getWanUsages();
					if(dailyUsages != null)
					{
						for(ConsolidateUsages usage : dailyUsages)
						{
							if(usage != null)
							{
								Integer wanId = usage.getId();
								String name = usage.getName();
								List<Usages> usages = usage.getUsage();
								if(usages != null)
								{
									for(Usages dailyUsage : usages)
									{
										Date datetime = formatDay.parse(dailyUsage.getDate());
										int unixtime = (int)(datetime.getTime()/1000);
										DailyDeviceUsages deviceDailyUsage = dailyDeviceUsagesDAO.findByNetworkIdDeviceIdTimeAndWanId(net_id, dev_id, unixtime, wanId);
										if( deviceDailyUsage != null )
										{
											deviceDailyUsage.setWan_name(name);
											deviceDailyUsage.setRx(dailyUsage.getDown());
											deviceDailyUsage.setTx(dailyUsage.getUp());
											deviceDailyUsage.update();
										}
										else
										{
											deviceDailyUsage = new DailyDeviceUsages();
											deviceDailyUsage.setUnixtime(unixtime);
											deviceDailyUsage.setDeviceId(dev_id);
											deviceDailyUsage.setNetworkId(net_id);
											deviceDailyUsage.setWan_id(wanId);
											deviceDailyUsage.setWan_name(name);
											deviceDailyUsage.setRx(dailyUsage.getDown());
											deviceDailyUsage.setTx(dailyUsage.getUp());
											deviceDailyUsage.setDatetime(datetime);
											deviceDailyUsage.createIgnore();
										}
										log.info("daily = " + deviceDailyUsage);
										batchConnection.addBatch(deviceDailyUsage);
									}
								}
							}
						}
					}
				}
				
				//Persist monthly device usage consolidate info
				ConsolidateObject monthlyO = devUsageConsolidateO.getMonthly();
				if( monthlyO != null )
				{
					List<ConsolidateUsages> monthlyUsages = monthlyO.getWanUsages();
					if( monthlyUsages != null )
					{
						for(ConsolidateUsages usage : monthlyUsages)
						{
							Integer wanId = usage.getId();
							String name = usage.getName();
							List<Usages> usages = usage.getUsage();
							if(usages != null)
							{
								for( Usages monthlyUsage : usages )
								{
									Date datetime = formatMth.parse(monthlyUsage.getDate());
									DeviceMonthlyUsages deviceMonthlyUsages = devMonthlyUsagesDAO.findByNetworkIdDeviceIdTimeAndWanId(net_id, dev_id, datetime, wanId);
									if( deviceMonthlyUsages != null )
									{
										deviceMonthlyUsages.setWan_name(name);
										deviceMonthlyUsages.setRx(monthlyUsage.getDown());
										deviceMonthlyUsages.setTx(monthlyUsage.getUp());
										deviceMonthlyUsages.update();
										if(log.isInfoEnabled())
											log.info("updated monthly = " + deviceMonthlyUsages);
									}
									else
									{
										deviceMonthlyUsages = new DeviceMonthlyUsages();
										
										deviceMonthlyUsages.setUnixtime((int)(datetime.getTime()/1000));
										deviceMonthlyUsages.setDevice_id(dev_id);
										deviceMonthlyUsages.setNetwork_id(net_id);
										deviceMonthlyUsages.setWan_id(wanId);
										deviceMonthlyUsages.setWan_name(name);
										deviceMonthlyUsages.setRx(monthlyUsage.getDown());
										deviceMonthlyUsages.setTx(monthlyUsage.getUp());
										deviceMonthlyUsages.setDatetime(datetime);
										deviceMonthlyUsages.createIgnore();
										if(log.isInfoEnabled())
											log.info("added monthly = " + deviceMonthlyUsages);
									}
									batchConnection.addBatch(deviceMonthlyUsages);
								}
							}
						}
					}
				}
//				batchConnection.commit();
				batchConnection.close();
			} catch (Exception e) {
				log.error("Exception e="+e, e);
				try {
					if (dbUtil !=null && dbUtil.isSessionStarted()) {
						dbUtil.endSession();
					}
				} catch (Exception e2) {
					log.error("Error in endSession", e2);
				}
			}
		}
	}
}