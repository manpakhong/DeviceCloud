package com.littlecloud.ac;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServlet;

import net.sf.json.JSONObject;

import org.jboss.logging.Logger;

import com.littlecloud.ac.health.ACHealthMonitor;
import com.littlecloud.ac.health.CoreHealthMonitor;
import com.littlecloud.ac.health.DBHealthMonitor;
import com.littlecloud.ac.health.DiskHealthMonitor;
import com.littlecloud.ac.health.HealthMonitorHandler;
import com.littlecloud.ac.health.HealthMonitorHandler.HealthInfo;
import com.littlecloud.ac.health.JVMHealthMonitor;
import com.littlecloud.ac.health.ReportHealthMonitor;
import com.littlecloud.ac.json.model.Json_OpMode;
import com.littlecloud.ac.json.model.ReportInterval;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.ac.json.model.util.MessageTypePropertyLoader;
import com.littlecloud.ac.messagehandler.queue.executor.CaptivePortalMessageHandleExecutorController;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.ac.util.RootBranchRedirectManager;
import com.littlecloud.alert.client.AlertClient;
import com.littlecloud.control.dao.branch.QzTriggersDAO;
import com.littlecloud.control.devicechange.DeviceChangeService;
import com.littlecloud.control.deviceconfig.DeviceConfigScheduler;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.branch.HealthCheck;
import com.littlecloud.control.entity.viewobject.QuartzTriggers;
import com.littlecloud.control.json.model.config.util.ConfigBackupUtils;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.fusionhub.FusionHubUtil;
import com.littlecloud.pool.object.ACCommandObject;
import com.littlecloud.pool.object.ConfigChecksumObject;
import com.littlecloud.pool.object.ConfigGetObject;
import com.littlecloud.pool.object.ConfigPutObject;
import com.littlecloud.pool.object.DevBandwidthObject;
import com.littlecloud.pool.object.DevChannelUtilObject;
import com.littlecloud.pool.object.DevDetailObject;
import com.littlecloud.pool.object.DevLocationsObject;
import com.littlecloud.pool.object.DevNeighborListObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.DevSsidUsagesObject;
import com.littlecloud.pool.object.DevUsageObject;
import com.littlecloud.pool.object.EventLogObject;
import com.littlecloud.pool.object.FeatureGetObject;
import com.littlecloud.pool.object.PepvpnEndpointObject;
import com.littlecloud.pool.object.PepvpnPeerDetailObject;
import com.littlecloud.pool.object.PepvpnPeerListObject;
import com.littlecloud.pool.object.PepvpnTunnelStatObject;
import com.littlecloud.pool.object.PoolObject;
import com.littlecloud.pool.object.StationBandwidthListObject;
import com.littlecloud.pool.object.StationListObject;
import com.littlecloud.pool.object.StationUsageObject;
import com.littlecloud.pool.object.StationZObject;
import com.littlecloud.pool.object.utils.OrgInfoUtils;
import com.littlecloud.pool.utils.PropertyService;
import com.littlecloud.utils.CommonUtils;
import com.peplink.api.db.util.DBUtil;

public class ACService extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(ACService.class);
	private static final int COMMAND_TIMEOUT_MILLISECONDS = 10000;

	private static PropertyService<ACService> ps = new PropertyService<ACService>(ACService.class);
	private static MessageTypePropertyLoader msgProp = 	MessageTypePropertyLoader.getInstance();
	private static ExecutorService executorAcWtpCmd = Executors.newFixedThreadPool(Integer.valueOf(ps.getString("USER_REQ_AC_TO_WTP_FIFO.THREAD_POOL_SIZE")));
	private static Thread fifoReaderThread = null;
	private static Thread fifoReplayThread = null;
	private static Thread msgRequeueThread = null;
	private static final int REPLAY_PERIOD = 5000; // 5s
	
	private static ACService acservice = null;
	private static final String serverName = ps.getString("SERVER_NAME");
	private static final String domainName = ps.getString("DOMAIN_NAME");
	private static final boolean SERVER_OP_MODE = ps.getString("OPMODE").equalsIgnoreCase("true")?true:false;
			
	// fetch command interval and duration
	private static final String devNeighborSsidDiscoveryCmdDurationInSec = ps.getString("PIPE_INFO_TYPE_SSID_DISCOVERY_CMD_DURATION");
	private static final String devNeighborSsidDiscoveryCmdInterval = ps.getString("PIPE_INFO_TYPE_SSID_DISCOVERY_CMD_INTERVAL");
	
	//for health check jobs
	private static final Integer monitor_sys_interval_min = HealthMonitorHandler.getMonitorSysIntervalMin() * 60;
	private static final Integer monitor_db_interval_min = DBHealthMonitor.getMonitorDbIntervalMin() * 60;
	private static final Integer monitor_ac_interval_min = ACHealthMonitor.getMonitorAcIntervalMin() * 60;
	private static final Integer monitor_core_interval_min = CoreHealthMonitor.getHealthMonitorCoreIntervalMin() * 60;
	private static final Integer monitor_disk_interval_min = DiskHealthMonitor.getMonitorDiskIntervalMin() * 60;
	private static final Integer monitor_jvm_interval_min = JVMHealthMonitor.getMonitorJvmIntervalMin() * 60;
	private static final String recipients = HealthMonitorHandler.getReceipients();
	
	//check db process scheduler
	private final ScheduledExecutorService dbScheduler = Executors.newScheduledThreadPool(1);
	private final static String sql = "SELECT * FROM information_schema.processlist where command !='Sleep' ORDER BY Time;";
	private static Connection conn = null;
	private static Statement stmt = null;
	private static ResultSet rs = null;
	
	// check scheduler status schedule
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	// properties to enable captive portal
	private static final boolean captive_portal_enabled = Boolean.valueOf(ps.getString("CAPTIVE_PORTAL_ENABLED"));
	
	public static enum OP_MODE {
		OPMODE_NORMAL, OPMODE_LOWBAND, OPMODE_NOREPORT
	};

	public static String getDevneighborssiddiscoverycmddurationinsec() {
		return devNeighborSsidDiscoveryCmdDurationInSec;
	}

	public static String getDevneighborssiddiscoverycmdinterval() {
		return devNeighborSsidDiscoveryCmdInterval;
	}

	public static ConcurrentHashMap<String, Json_OpMode> OP_MODE_MAP = new ConcurrentHashMap<String, Json_OpMode>();

	public static boolean isSchedulerHealthy()
	{
		boolean result = true;
		HashMap<String,QuartzTriggers> triggersMap = null;
		try
		{
			triggersMap = QzTriggersDAO.getTriggersInfo();
			if(triggersMap != null)
			{
				Set<String> triggerNames = triggersMap.keySet();
				if( triggerNames != null )
				{
					for(String key : triggerNames)
					{
						QuartzTriggers triggers = triggersMap.get(key);
						if( triggers.getNext_fire_time() < DateUtils.getUtcDate().getTime() || triggers.getNext_fire_time() < triggers.getPrev_fire_time() )
						{
							result = false;
						}
						else
						{
							if( triggers.getStatus().equalsIgnoreCase("paused")==true )
							{
								result = false;
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			log.error("Check scheduler healthy status error -"+e,e);
			result = false;
		}
		return result;
	}
	
	public void checkSchedulerStatus()
	{
		try 
		{
			final Runnable checkScheduleJob = new Runnable()
			{
				QzTriggersDAO triggerDAO = new QzTriggersDAO();
				HashMap<String,QuartzTriggers> triggersMap = null;
				public void run()
				{
					log.info("CheckSchedulerJob running...");
					try 
					{
						boolean isSend = false;
						triggersMap = QzTriggersDAO.getTriggersInfo();
						Set<String> triggerNames = triggersMap.keySet();
						String content = "The following job(s) is down :\\n";
						for(String key : triggerNames)
						{
							QuartzTriggers triggers = triggersMap.get(key);
							if( triggers.getNext_fire_time() < DateUtils.getUtcDate().getTime() || triggers.getNext_fire_time() < triggers.getPrev_fire_time() )
							{
								if(triggers.getStatus().equalsIgnoreCase("paused")==false)
								{
									content = content + "- " + key + "\\n";
									isSend = true;
								}
								else
								{
									content = content + "- " + key + " (paused)\\n";
									isSend = true;
								}
							}
							else
							{
								if( triggers.getStatus().equalsIgnoreCase("paused")==true )
								{
									content = content + "- " + key + " (paused)\\n";
									isSend = true;
								}
							}
						}
						
						if(!ReportHealthMonitor.isConsolidateJobHealth())
						{
							content = content + "- Standalone consolidate job is down\\n";
							isSend = true;
						}
						
						if(!ReportHealthMonitor.isConsolidateResultCorrect())
						{
							content = content + "- Standalone consolidate result is incorrect\\n";
							isSend = true;
						}
						
						List<HealthCheck> infos = HealthMonitorHandler.getLatestClusterHealthyInfos();
						StringBuffer sb = new StringBuffer();
						if( infos != null && !infos.isEmpty() )
						{
							for(HealthCheck info : infos)
							{
								if(info.getStatus() < 2)
								{
									sb.append(info.getServer_id());
									sb.append("- ");
									if( info.getStatus() == 0 )
										sb.append("unhealthy");
									else
										sb.append("calm down");
									sb.append(" ");
									sb.append(info.getLastUnhealthyReason());
									sb.append(" \\n");
									isSend = true;
								}
							}
						}
						
						content = content + sb.toString();
//						if(!HealthMonitorHandler.isClusterHealthy())
//						{
//							content = content + "- Cluster is unhealthy\\n";
//							isSend = true;
//						}
						
						log.info("Health check mail "+isSend);
						
						if( isSend == true )
						{
							//kennethkwan@peplink.com|tedc@peplink.com|
							AlertClient client = new AlertClient();
							String mail_json = "{\"msg_type\":\"email\",\"msg_content\":\""+content+"\","
									+ "\"msg_subject\":\"Schedule Error ("+domainName+")\","
											+ "\"sender_name\":null,\"sender_email\":null,\"recipient\":\""+recipients+"\",\"priority\":5}";
							log.info("schedule check mail json:" + mail_json + " --- " + content);
							client.sendCommand("127.0.0.1", 28300, "add -j " + mail_json);
						}
					} 
					catch (Exception e) 
					{
						if( e.getClass().equals(ConnectException.class) )
						{
							log.warn("Connection refused by localhost:28300");
						}
						log.error("check schedule status error - " + e, e);
					}
				}
			};
			
			final ScheduledFuture updateTimeHandler = scheduler.scheduleAtFixedRate(checkScheduleJob, 0, 3600, TimeUnit.SECONDS);			
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			log.error("Check scheduler status error -"+e, e);
		}
	}
	
	//Getting db connection for showing db process list
	public static Connection getStaticConnection()
	{
		if(conn == null)
		{
			Properties props = new Properties();
			try 
			{
				try
				{
					File f = new File(System.getProperty("db.properties"));
					if( f.exists() )
					{
						FileInputStream fis = new FileInputStream(f);
						props.load(fis);
						fis.close();				
					}
				}
				catch(Exception e)
				{
					log.fatal("Config is incorrect -",e);
				}
				
				Class.forName(props.getProperty("DB_JDBC_DRIVER"));
				conn = DriverManager.getConnection(props.getProperty("DB_URL"), props.getProperty("DB_USER"), props.getProperty("DB_PASSWORD"));
			} 
			catch (Exception e) 
			{
				log.error("Get DB Connection error "+e,e);
			}
		}
		
		return conn;
	}
	
	public void dbScheduler()
	{
		try 
		{
			final Runnable checkScheduleJob = new Runnable()
			{
				public void run()
				{
					//Query handle
					try 
					{
						stmt = getStaticConnection().createStatement();
						rs = stmt.executeQuery(sql);
						if(rs != null)
						{
							StringBuffer sb = null;
							while(rs.next())
							{
								sb = new StringBuffer();
								sb.append("[");
								sb.append("user:");
								sb.append(rs.getString("user")+", ");
								sb.append("host:");
								sb.append(rs.getString("host")+", ");
								sb.append("db:");
								sb.append(rs.getString("db")+", ");
								sb.append("command:");
								sb.append(rs.getString("command")+", ");
								sb.append("time:");
								sb.append(rs.getInt("time")+", ");
								sb.append("state:");
								sb.append(rs.getString("state")+", ");
								sb.append("info:");
								sb.append(rs.getString("info"));
								sb.append("]");
								log.warn("DEBUG:Get db process : " + sb.toString());
							}
						}
						else
						{
							log.warn("DEBUG:Get db process : no result");
						}
					} 
					catch (SQLException e) 
					{
						log.error("query error "+e,e);
					}
				}
			};
			
			final ScheduledFuture updateTimeHandler = dbScheduler.scheduleAtFixedRate(checkScheduleJob, 0, 60, TimeUnit.SECONDS);			
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			log.error("Check scheduler status error -"+e, e);
		}
	}
	
	// add opmode
	// read opmode

	private ACService() {
		log.info("ACService is initializing...");

		log.info("- Loading OP_MODE");
		for (OP_MODE op : OP_MODE.values()) {
			Json_OpMode opJson = new Json_OpMode(
					ps.getInteger(op.toString() + ".rpt_enable"),
					ps.getInteger(op.toString() + ".rpt_offset"),
					ps.getInteger(op.toString() + ".rpt_period"),
					ps.getInteger(op.toString() + ".evt_period"),
					ps.getInteger(op.toString() + ".gps_sample_intv"),
					ps.getInteger(op.toString() + ".gps_live_intv"),
					ps.getInteger(op.toString() + ".gps_live_pts"),
					ps.getInteger(op.toString() + ".heartbeat"),
					ps.getInteger(op.toString() + ".peer_dead"));

			log.info(op.toString() + " " + opJson);

			OP_MODE_MAP.put(op.toString(), opJson);
		}
		
		log.info("ACService started");
	}

	/* AC main */
	public void init()
	{
		Date coreStartUpTime = new Date();
		log.info("ACService init()");
		acservice = getInstance();
		
		try {
			fifoReaderThread = new Thread(new FifoReader(ps.getString("USER_REQ_WTP_TO_AC_FIFO")));
			fifoReaderThread.start();
			fifoReplayThread = new Thread(new FifoReader(ps.getString("REPLAY_REQ_WTP_TO_AC_FIFO"), REPLAY_PERIOD));
			fifoReplayThread.start();
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException - WTP to AC FIFO, e="+e, e);
			return;
		}
		log.warn("FifoReaderService started, USER_REQ_WTP_TO_AC_FIFO=" + ps.getString("USER_REQ_WTP_TO_AC_FIFO") + ", REPLAY_REQ_WTP_TO_AC_FIFO=" + ps.getString("REPLAY_REQ_WTP_TO_AC_FIFO"));
		
		try {
			msgRequeueThread = new Thread(new MsgRequeue());
			msgRequeueThread.start();
		} catch (Exception e) {
			log.error("Message Requeue Thread startup failed, e="+e, e);
			return;
		}
		
		HealthMonitorHandler healthHandler = new HealthMonitorHandler();
		HealthMonitorHandler.setStartUpTime(coreStartUpTime);
		
		if (!RootBranchRedirectManager.isBranchServerMode())
		{
			log.warnf("INFO Server is NOT running in BRANCH mode, below job will not be executed!");
			return;
		}
		else
		{
			log.warnf("INFO Server is running in BRANCH mode");
		}
		
		try
		{
			DBUtil.setLogger(log);			
			healthHandler.schedulePersistHealthInfo(monitor_sys_interval_min, HealthInfo.sys_info);
			healthHandler.schedulePersistHealthInfo(monitor_ac_interval_min, HealthInfo.ac_info);
			healthHandler.schedulePersistHealthInfo(monitor_db_interval_min, HealthInfo.db_info);
			healthHandler.schedulePersistHealthInfo(monitor_disk_interval_min, HealthInfo.disk_info);
			healthHandler.schedulePersistHealthInfo(monitor_jvm_interval_min, HealthInfo.jvm_info);
			healthHandler.schedulePersistHealthInfo(monitor_core_interval_min, HealthInfo.core_info);
		}
		catch(Exception e)
		{
			log.error("Init health check error -",e);
		}
		log.warn("HealthMonitorHandler started with ac_interval=" + monitor_ac_interval_min + ", jvm_interval=" + monitor_jvm_interval_min);
		
		if (RootBranchRedirectManager.isBranchServerMode())
		{
			try
			{
				checkSchedulerStatus();
			}
			catch(Exception e)
			{
				log.error("Schedule check err-"+e,e);
			}
			log.warn("AC Scheduler Check Job started");
					
			try
			{
				dbScheduler();
			}
			catch(Exception e)
			{
				log.error("Schedule check db process err-"+e,e);
			}
			log.warn("DB Health Check Scheduler Job started");
			
			try {
				DeviceChangeService.startService();
			} catch (Exception e){
				log.errorf(e, "DEVCONF20140424 - ACService.init() - DeviceConfigScheduler.startSchedule()");
			}
		}
		
		/* clear Products cache for reload upon startup */
		
		try{
			DeviceConfigScheduler.startSchedule();
			if (DeviceConfigScheduler.getDeviceConfigQueue() != null){
				log.debugf("DEVCONF20140424 - ACService.init() - DeviceConfigScheduler.startSchedule(), counter:%s", DeviceConfigScheduler.getDeviceConfigQueue().getQueueSize());
			}
		} catch (Exception e){
			log.errorf(e, "DEVCONF20140424 - ACService.init() - DeviceConfigScheduler.startSchedule()");
		}
		log.warn("Device Config Scheduler Job started");

		try{
			CaptivePortalMessageHandleExecutorController.setCaptivePortalEnabled(captive_portal_enabled);
			if (captive_portal_enabled){
				CaptivePortalMessageHandleExecutorController.run();
				log.infof("CAPORT20140526 - ACService.init() - captive portal MessageHandleExecutorController.run()");
			} else {
				log.infof("CAPORT20140526 - ACService.init() - captive portal is setted to disable CAPTIVE_PORTAL_ENABLED: false - MessageHandleExecutorController.run()");
			}

		} catch (Exception e){
			log.errorf(e, "CAPORT20140526 - ACService.init() - MessageHandleExecutorController.run()");
		}
		log.warn("Captive Portal Message Handler started");
		
		
		log.warn("ACService.init() - all started!");
	}


	public static String toHtml() {
		StringBuffer sb = new StringBuffer();
		sb.append("------------------Schedule INFO-----------------");
		sb.append("<br/>");
		log.debug("ACService.isSchedulerHealthy");
		if(ACService.isSchedulerHealthy())
		{
			sb.append("Status : ");
			sb.append("<font color=\"green\">");
			sb.append("Healthy");
			sb.append("</font>");
		}
		else
		{
			sb.append("Status : ");
			sb.append("<font color=\"red\">");
			sb.append("Unhealthy");
			sb.append("</font>");
		}
		sb.append("<br/>");
		sb.append("<br/>");
		return sb.toString();
	}
	
	public static String getServerName() {
		return serverName;
	}

	public static void shutdown()
	{
		if (fifoReaderThread!=null)
			fifoReaderThread.interrupt();

		if (fifoReplayThread!=null && fifoReplayThread.isAlive())
			fifoReplayThread.interrupt();
		
		executorAcWtpCmd.shutdown();

		if (executorAcWtpCmd.isTerminated())
		{
			log.info("executorAcWtpCmd is terminated.");
		}
		else
		{
			log.info("executorAcWtpCmd is NOT terminated!");
		}
	}

	public synchronized static ACService getInstance() {
		if (acservice == null) {
			try {
				acservice = new ACService();
			} catch (Exception e) {
				log.error("Failed to init ACService. " + e, e);
				return null;
			}
		}
		return acservice;
	}

	public static boolean set_dev_op_mode(DevOnlineObject obj, OP_MODE opMode)
	{
		return set_dev_op_mode(obj.getSid(), obj.getIana_id(), obj.getSn(), opMode);
	}
	
	public static boolean set_dev_op_mode(String sid, int iana_id, String sn, OP_MODE opMode)
	{
		if (SERVER_OP_MODE)
		{
			Json_OpMode opJson = OP_MODE_MAP.get(opMode.toString());
			log.debugf("dev %d %s sid %s setting op_mode %s", iana_id, sn, sid, opMode.toString());
			return ACService.<Json_OpMode> fetchCommand(MessageType.PIPE_INFO_TYPE_OP_MODE_PUT, sid, iana_id, sn, opJson);					
		}
		else
		{
			return true;
		}
	}
	
	public static boolean set_dev_rpt_mode(String sid, int iana_id, String sn, ReportInterval rptInt)
	{
		return ACService.<ReportInterval> putCommand(sid, iana_id, sn, MessageType.PIPE_INFO_TYPE_CONFIG_PUT, rptInt);
	}

	public static boolean config_put(String sid, int iana_id, String sn, String config)
	{
		return ACService.<String> putCommand(sid, iana_id, sn, MessageType.PIPE_INFO_TYPE_CONFIG_PUT, config);
	}

	public static <T> boolean putCommand(String sid, int iana_id, String sn, MessageType mt, T data)
	{
		return ACService.<T> fetchCommand(mt, sid, iana_id, sn, data);
	}
	
	public static boolean disconnect_duplicate_sn_from_ac(String sid, int iana_id, String sn, String target_machine_id)
	{
		return ACService.fetchBroadcastWithTargetMachine(MessageType.PIPE_INFO_TYPE_DEV_DISCONNECT, sid, iana_id, sn, target_machine_id);
	}

	public static boolean redirect_device_to_self(String sid, int iana_id, String sn)
	{
		return ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_DISCONNECT, sid, iana_id, sn);
	}
	
	public static boolean fetchCommand(MessageType rt, String sid, int iana_id, String sn)
	{
		return ACService.fetchCommand(rt, sid, iana_id, sn, null, null, false, null, null);
	}
		
	public static <T> boolean fetchCommand(MessageType rt, String sid, int iana_id, String sn, T data)
	{
		return fetchCommand(rt, sid, iana_id, sn, null, data, false, null, null);
	}

	public static <T> boolean fetchCommand(MessageType rt, String sid, int iana_id, String sn, Integer duration, Integer interval)
	{
		return ACService.fetchCommand(rt, sid, iana_id, sn, null, null, false, duration, interval);
	}

	public static boolean fetchBroadcast(MessageType rt, String sid, int iana_id, String sn)
	{
		return ACService.fetchCommand(rt, sid, iana_id, sn, null, null, true, null, null);
	}
	
	public static boolean fetchBroadcastWithTargetMachine(MessageType rt, String sid, int iana_id, String sn, String target_machine_id)
	{
		return ACService.fetchCommand(rt, sid, iana_id, sn, target_machine_id, null, true, null, null);
	}

	/* Try to fetch with same sid to avoid duplicate commands to AC/WTP */
	private static <T> boolean fetchCommand(MessageType rt, String sid, int iana_id, String sn, String target_machine_id, T data, boolean bBroadcast, Integer duration, Integer interval)
	{
		log.infof("fetchCommand - msg_type %s to sn %s iana_id %d with sid %s", rt.name(), sn, iana_id, sid);
		
		if (sid == null)
			log.warnf("fetchCommand - sid should not be null!! msg_type %s to sn %s iana_id %d with sid %s", rt.name(), sn, iana_id, sid);
				
		// --------------------------Add---------------------------
		DevDetailObject devDetailObject = null;
		DevBandwidthObject devBandwidthObject = null;
		StationListObject stationListObject = null;
		StationZObject stationzObject = null;
		EventLogObject eventLogObject = null;
		DevUsageObject devUsageObject = null;
		DevLocationsObject devLocationsObject = null;
		DevSsidUsagesObject devSsidUsagesObject = null;
		DevChannelUtilObject devChannelUtilObject = null;
		StationBandwidthListObject stationBandwidthListObject = null;
		StationUsageObject stationUsageObject=null;
		PepvpnEndpointObject pepvpnenEndpointObject=null;
		PepvpnPeerListObject pepvpnPeerListObject=null;
		PepvpnPeerDetailObject pepvpnPeerDetailObject=null;
		PepvpnTunnelStatObject pepvpnTunnelStatObject=null;
		ConfigChecksumObject configChecksumObject=null;
		ConfigGetObject configGetObject=null;
		FeatureGetObject featureGetObject=null;
		DevNeighborListObject devNeighborListObject=null;
		String orgin_sid = null; 
		
		QueryInfo<T> info = new QueryInfo<T>();		
		try
		{			
			switch (rt)
			{
			case PIPE_INFO_TYPE_DEV_DETAIL:
				devDetailObject = new DevDetailObject();
				devDetailObject.setIana_id(iana_id);
				devDetailObject.setSn(sn);
				devDetailObject = ACUtil.<DevDetailObject> getPoolObjectBySn(devDetailObject, DevDetailObject.class);
				if (devDetailObject!=null)
					orgin_sid = devDetailObject.getSid();
				break;
			case PIPE_INFO_TYPE_DEV_BANDWIDTH:
				devBandwidthObject = new DevBandwidthObject();
				devBandwidthObject.setIana_id(iana_id);
				devBandwidthObject.setSn(sn);
				devBandwidthObject = ACUtil.<DevBandwidthObject> getPoolObjectBySn(devBandwidthObject, DevBandwidthObject.class);
				if (devBandwidthObject!=null)
					orgin_sid = devBandwidthObject.getSid();
				break;
			case PIPE_INFO_TYPE_DEV_LOCATIONS:
				devLocationsObject = new DevLocationsObject();
				devLocationsObject.setIana_id(iana_id);
				devLocationsObject.setSn(sn);
				devLocationsObject = ACUtil.<DevLocationsObject> getPoolObjectBySn(devLocationsObject, DevLocationsObject.class);
				if (devLocationsObject!=null)
					orgin_sid = devLocationsObject.getSid();
				break;
			case PIPE_INFO_TYPE_DEV_USAGE:
				devUsageObject = new DevUsageObject();
				devUsageObject.setIana_id(iana_id);
				devUsageObject.setSn(sn);
				devUsageObject = ACUtil.<DevUsageObject> getPoolObjectBySn(devUsageObject, DevUsageObject.class);
				if (devUsageObject!=null)
					orgin_sid = devUsageObject.getSid();
				break;
			case PIPE_INFO_TYPE_DEV_SSID_USAGES:
				devSsidUsagesObject = new DevSsidUsagesObject();
				devSsidUsagesObject.setIana_id(iana_id);
				devSsidUsagesObject.setSn(sn);
				devSsidUsagesObject = ACUtil.<DevSsidUsagesObject> getPoolObjectBySn(devSsidUsagesObject, DevSsidUsagesObject.class);
				if (devSsidUsagesObject!=null)
					orgin_sid = devSsidUsagesObject.getSid();
				break;
			case PIPE_INFO_TYPE_DEV_CHANNEL_UTIL:
				devChannelUtilObject = new DevChannelUtilObject();
				devChannelUtilObject.setIana_id(iana_id);
				devChannelUtilObject.setSn(sn);
				devChannelUtilObject = ACUtil.<DevChannelUtilObject> getPoolObjectBySn(devChannelUtilObject, DevChannelUtilObject.class);
				if (devChannelUtilObject!=null)
					orgin_sid = devChannelUtilObject.getSid();
				break;
			case PIPE_INFO_TYPE_STATION_LIST:
				stationListObject = new StationListObject();
				stationListObject.setIana_id(iana_id);
				stationListObject.setSn(sn);
				stationListObject = ACUtil.<StationListObject> getPoolObjectBySn(stationListObject, StationListObject.class);
				if (stationListObject!=null)
					orgin_sid = stationListObject.getSid();
				break;
			case PIPE_INFO_TYPE_STATION_Z:
				log.warnf("fetchCommand - PIPE_INFO_TYPE_STATION_Z shouldn't be called!, sn %s iana_id %d with sid %s", sn, iana_id, sid);
//				stationzObject = new StationZObject();
//				stationzObject.setIana_id(iana_id);
//				stationzObject.setSn(sn);
//				stationzObject = ACUtil.<StationZObject>getPoolObjectBySn(stationzObject, StationZObject.class);
//				stationzObject.setLastRequestUpdateTime(Integer.valueOf((""+(System.currentTimeMillis()/1000))));
				break;
			case PIPE_INFO_TYPE_STATION_BANDWIDTH_LIST:
				stationBandwidthListObject = new StationBandwidthListObject();
				stationBandwidthListObject.setIana_id(iana_id);
				stationBandwidthListObject.setSn(sn);
				stationBandwidthListObject = ACUtil.<StationBandwidthListObject> getPoolObjectBySn(stationBandwidthListObject, StationBandwidthListObject.class);
				if (stationBandwidthListObject!=null)
					orgin_sid = stationBandwidthListObject.getSid();
				break;
			case PIPE_INFO_TYPE_STATION_USAGE:
				stationUsageObject = new StationUsageObject();
				stationUsageObject.setIana_id(iana_id);
				stationUsageObject.setSn(sn);
				stationUsageObject = ACUtil.<StationUsageObject> getPoolObjectBySn(stationUsageObject, StationUsageObject.class);
				if (stationUsageObject!=null)
					orgin_sid = stationUsageObject.getSid();
				break;
			case PIPE_INFO_TYPE_PEPVPN_ENDPOINT:
				pepvpnenEndpointObject = new PepvpnEndpointObject();
				pepvpnenEndpointObject.setIana_id(iana_id);
				pepvpnenEndpointObject.setSn(sn);
				pepvpnenEndpointObject = ACUtil.<PepvpnEndpointObject> getPoolObjectBySn(pepvpnenEndpointObject, PepvpnEndpointObject.class);
				if (pepvpnenEndpointObject!=null)
					orgin_sid = pepvpnenEndpointObject.getSid();
				break;
			case PIPE_INFO_TYPE_PEPVPN_PEER_LIST:/* *** add for PepvpnEndpointV2 *** */
				pepvpnPeerListObject = new PepvpnPeerListObject();
				pepvpnPeerListObject.setIana_id(iana_id);
				pepvpnPeerListObject.setSn(sn);
				pepvpnPeerListObject = ACUtil.<PepvpnPeerListObject> getPoolObjectBySn(pepvpnPeerListObject, PepvpnPeerListObject.class);
				if (pepvpnPeerListObject!=null)
					orgin_sid = pepvpnPeerListObject.getSid();
				break;
			case PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL:/* *** add for PepvpnEndpointV2 *** */
				pepvpnPeerDetailObject = new PepvpnPeerDetailObject();
				pepvpnPeerDetailObject.setIana_id(iana_id);
				pepvpnPeerDetailObject.setSn(sn);
				pepvpnPeerDetailObject = ACUtil.<PepvpnPeerDetailObject> getPoolObjectBySn(pepvpnPeerDetailObject, PepvpnPeerDetailObject.class);
				if (pepvpnPeerDetailObject!=null)
					orgin_sid = pepvpnPeerDetailObject.getSid();
				break;
			case PIPE_INFO_TYPE_PEPVPN_TUNNEL_STAT:/* *** add for PepvpnEndpointV2 *** */
				pepvpnTunnelStatObject = new PepvpnTunnelStatObject();
				pepvpnTunnelStatObject.setIana_id(iana_id);
				pepvpnTunnelStatObject.setSn(sn);
				pepvpnTunnelStatObject = ACUtil.<PepvpnTunnelStatObject> getPoolObjectBySn(pepvpnTunnelStatObject, PepvpnTunnelStatObject.class);
				if (pepvpnTunnelStatObject!=null)
					orgin_sid = pepvpnTunnelStatObject.getSid();
				break;
			case PIPE_INFO_TYPE_CONFIG_CHECKSUM:
				configChecksumObject = new ConfigChecksumObject();
				configChecksumObject.setIana_id(iana_id);
				configChecksumObject.setSn(sn);
				configChecksumObject = ACUtil.<ConfigChecksumObject> getPoolObjectBySn(configChecksumObject, ConfigChecksumObject.class);
				if (configChecksumObject!=null)
					orgin_sid = configChecksumObject.getSid();
				break;
			case PIPE_INFO_TYPE_CONFIG_GET:
				configGetObject = new ConfigGetObject();
				configGetObject.setIana_id(iana_id);
				configGetObject.setSn(sn);
				configGetObject = ACUtil.<ConfigGetObject> getPoolObjectBySn(configGetObject, ConfigGetObject.class);
				if (configGetObject!=null)
					orgin_sid = configGetObject.getSid();
				break;
			case PIPE_INFO_TYPE_FEATURE_GET:
				featureGetObject = new FeatureGetObject();
				featureGetObject.setIana_id(iana_id);
				featureGetObject.setSn(sn);
				featureGetObject = ACUtil.<FeatureGetObject> getPoolObjectBySn(featureGetObject, FeatureGetObject.class);
				if (featureGetObject!=null)
					orgin_sid = featureGetObject.getSid();
				break;
			case PIPE_INFO_TYPE_EVENT_LOG:
				eventLogObject = new EventLogObject();
				eventLogObject.setIana_id(iana_id);
				eventLogObject.setSn(sn);
				eventLogObject = ACUtil.<EventLogObject> getPoolObjectBySn(eventLogObject, EventLogObject.class);
				if (eventLogObject!=null)
					orgin_sid = eventLogObject.getSid();
				break;
			case PIPE_INFO_TYPE_SSID_DISCOVERY:
				devNeighborListObject = new DevNeighborListObject();
				devNeighborListObject.setIana_id(iana_id);
				devNeighborListObject.setSn(sn);
				devNeighborListObject = ACUtil.<DevNeighborListObject> getPoolObjectBySn(devNeighborListObject, DevNeighborListObject.class);
				log.debugf("INDOORPOS20140519 - ACService - PIPE_INFO_TYPE_SSID_DISCOVERY, devNeighborListObject: %s", devNeighborListObject);
				if (devNeighborListObject!=null)
				{
					orgin_sid = devNeighborListObject.getSid();
					
					String str_duration = getDevneighborssiddiscoverycmddurationinsec();	// override command
					String str_interval = getDevneighborssiddiscoverycmdinterval();		// override command
					
					if (str_duration != null && !str_duration.isEmpty() && CommonUtils.isInteger(str_duration)){
						info.setDuration(Integer.parseInt(str_duration));				
					}
					if (str_interval != null && ! str_interval.isEmpty() && CommonUtils.isInteger(str_interval)){
						info.setInterval(Integer.parseInt(str_interval));
					}
				}
				break;
				
			default:
				break;
			}
						
			if (orgin_sid != null)
				sid = orgin_sid;
			
			info.setSid(sid);
			
			switch (rt)
			{
			case PIPE_INFO_TYPE_DEV_DETAIL:
			case PIPE_INFO_TYPE_DEV_LOCATIONS:
			case PIPE_INFO_TYPE_DEV_BANDWIDTH:
			case PIPE_INFO_TYPE_STATION_BANDWIDTH_LIST:
			case PIPE_INFO_TYPE_PEPVPN_ENDPOINT:
			case PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL://ADD for EndpointV2
			case PIPE_INFO_TYPE_PEPVPN_TUNNEL_STAT:
				if (duration==null)
					duration = msgProp.getDuration(rt.name());				
				if (interval==null)
					interval = msgProp.getInterval(rt.name());
				break;
			default:
				break;
			}
		} catch (Exception e)
		{
			log.error("fetchCommand - fail to getPoolObjectBySn", e);
		}
		
		// --------------------------------------
		info.setType(rt);
		info.setIana_id(iana_id);
		info.setSn(sn);
		info.setData(data);
		
		if(duration!=null)
			info.setDuration(duration);

		if(interval!=null)
			info.setInterval(interval);

		
		DevOnlineObject devOnlineO = new DevOnlineObject();
		devOnlineO.setIana_id(iana_id);
		devOnlineO.setSn(sn);

		try {
			devOnlineO = ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
		} catch (InstantiationException | IllegalAccessException e1) {
			log.error("fetchCommand - fail to getPoolObjectBySn");
			return false;
		}

		if (bBroadcast)
		{
			String json = JsonUtils.toJsonCompact(info);
			
			ACCommandObject acObj = new ACCommandObject();
			acObj.setType(rt);
			acObj.setSid(sid);
			acObj.setIana_id(iana_id);
			acObj.setSn(genRandomPrefix(sn));
			acObj.setJson(json);
			acObj.setAco_type(ACCommandObject.ACO_TYPE.BROADCAST);
			acObj.setTarget_machine_id(target_machine_id);
			log.debug("fetchCommand - Fetch command JSon:"+json);			
			
			try 
			{
				ACUtil.<ACCommandObject>cachePoolObjectBySn(acObj,ACCommandObject.class);
			} 
			catch (InstantiationException | IllegalAccessException e) 
			{
				log.error("fetchCommand - fail to cachePoolObjectBySn (1)");
				return false;
			}
			return true;
		}
		
		/* refresh only online device status */
		String json = JsonUtils.toJsonCompact(info);
		if (devOnlineO != null)
		{
			if (devOnlineO.isOnline() || FusionHubUtil.isFHDefaultInfo(devOnlineO.getSn(), devOnlineO.getModel()))
			{
				if (data instanceof PoolObject)
				{
					log.debug(">>> fetchCommand - Refresh Cache!");
					((PoolObject) data).setRefreshing(true);
					((PoolObject) data).setCreateTime(DateUtils.getUtcDate().getTime());
				}
					
				/* if device is associated with local AC, fetch command; else put to cache */
//				if (devOnlineO.getMachine_id().compareTo(Cluster.getUniqueLocalId())==0)
				
				if (devOnlineO.getMachine_id().compareTo(serverName)==0)
				{
					Future<String> result = executorAcWtpCmd.submit(new FifoWriter(ps.getString("USER_REQ_AC_TO_WTP_FIFO"), json));
					
					try {
						result.get(COMMAND_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						log.errorf("fetchCommand - AC FIFO write timeout for %d second - Exception for ExecutorService %s", COMMAND_TIMEOUT_MILLISECONDS / 1000, e);
						return false;
					}
				}
				else
				{
					ACCommandObject acObj = new ACCommandObject();
					acObj.setType(rt);
					acObj.setSid(sid);
					acObj.setIana_id(iana_id);
					acObj.setSn(genRandomPrefix(sn));
					acObj.setJson(json);
					log.debug("fetchCommand - Fetch command JSon:"+json);					
					
					try 
					{
						ACUtil.<ACCommandObject>cachePoolObjectBySn(acObj,ACCommandObject.class);
					} 
					catch (InstantiationException | IllegalAccessException e) 
					{
						log.error("fetchCommand - fail to cachePoolObjectBySn (2)");
						return false;
					}
				}
			}
			else
			{
				log.info("fetchCommand - device is offline for sn " + sn);
				if (rt == MessageType.PIPE_INFO_TYPE_DEV_ONLINE || rt == MessageType.PIPE_INFO_TYPE_OP_MODE_PUT 
						|| rt == MessageType.PIPE_INFO_TYPE_REDIRECT_WTP)
				{
					Future<String> result = executorAcWtpCmd.submit(new FifoWriter(ps.getString("USER_REQ_AC_TO_WTP_FIFO"), json));
					try {
						result.get(COMMAND_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						log.errorf("fetchCommand - Resend Online - AC FIFO write timeout for %d second - Exception for ExecutorService %s", COMMAND_TIMEOUT_MILLISECONDS / 1000, e);
						return false;
					}
				}
			}
		}
		else
		{
			if (rt == MessageType.PIPE_INFO_TYPE_DEV_ONLINE || rt == MessageType.PIPE_INFO_TYPE_OP_MODE_PUT
					 || rt == MessageType.PIPE_INFO_TYPE_REDIRECT_WTP)
			{
				Future<String> result = executorAcWtpCmd.submit(new FifoWriter(ps.getString("USER_REQ_AC_TO_WTP_FIFO"), json));
				try {
					result.get(COMMAND_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					log.errorf("fetchCommand - Send OP_MODE_PUT for unregistered devices - AC FIFO write timeout for %d second - Exception for ExecutorService %s", COMMAND_TIMEOUT_MILLISECONDS / 1000, e);
					return false;
				}
			}
			else
			{
				log.info("fetchCommand - devOnlineObject not exist for dev sn " + sn);
			}
		}

		return true;
	}
	
	private static String genRandomPrefix(String sn)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(JsonUtils.genServerRef());
		sb.append("_");
		sb.append(sn);
		return sb.toString();
	}

	public static boolean fetchCommand(String cacheObjectKey)
	{
		log.info("fetchCommand from cache");

		Thread t = new Thread(new MyFetchCommand(cacheObjectKey));
		
		try {
			t.start();
		} catch(Exception e)
		{
			log.error("fetchCommand - "+cacheObjectKey, e);
		}
		
		return true;
	}
	
	private static class MyFetchCommand implements Runnable {

		private String cacheObjectKey;
		
		protected MyFetchCommand(String cacheObjectKey) {
			super();
			this.cacheObjectKey = cacheObjectKey;
		}

		@Override
		public void run() {

			boolean bFetch = false;
			DevOnlineObject devOnlineO = null;

			/* check if online object exist and associate with this server, yes fetch; no ignore */
			ACCommandObject aco = ACUtil.getPoolObjectByKey(cacheObjectKey);
			if (aco == null)
				return;

			if (aco.getAco_type() == ACCommandObject.ACO_TYPE.UNICAST) {
				String[] param_list = cacheObjectKey.split("\\|");
				devOnlineO = new DevOnlineObject();
				devOnlineO.setIana_id(Integer.valueOf(param_list[1]));
				devOnlineO.setSn(param_list[0].substring(param_list[0].length() - 14, param_list[0].length()));

				try {
					devOnlineO = ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
				} catch (InstantiationException | IllegalAccessException e) {
					log.error("fail to getPoolObjectBySn", e);
					return;
				}

				if (devOnlineO != null && devOnlineO.isOnline()
						&& devOnlineO.getMachine_id().compareTo(serverName) == 0)
				{
					bFetch = true;
				}
			} else if (aco.getAco_type() == ACCommandObject.ACO_TYPE.BROADCAST) {
				if (aco.getTarget_machine_id() == null) {
					bFetch = true;
				} else if (aco.getTarget_machine_id() != null
						&& aco.getTarget_machine_id().equalsIgnoreCase(ACService.getServerName())) {
					bFetch = true;
				} else {
					bFetch = false;
				}
			}

			if (bFetch) {
				if (aco.getType() == MessageType.PIPE_INFO_TYPE_CONFIG_PUT_MASTER) {
					QueryInfo<Object> info = JsonUtils.fromJson(aco.getJson(), QueryInfo.class);
					if (info != null) {
						JSONObject object = JSONObject.fromObject(info);
						if (object != null && object.getJSONObject(ACUtil.DATA_TAG) != null) {
							JSONObject data = object.getJSONObject(ACUtil.DATA_TAG);
							ConfigPutObject putObject = JsonUtils.fromJson(data.toString(), ConfigPutObject.class);
							if (putObject == null || putObject.getFilepath() == null) {
								log.errorf("Invalid PIPE_INFO_TYPE_CONFIG_PUT_MASTER message %s", aco.getJson());
								return;
							}

							Networks net = OrgInfoUtils.getNetwork(devOnlineO.getOrganization_id(),
									devOnlineO.getNetwork_id());
							if (net == null) {
								log.warnf("Fail to find network for devices %d %s net %d", devOnlineO.getIana_id(),
										devOnlineO.getSn(), devOnlineO.getNetwork_id());
								return;
							}

							if (net.getMasterDeviceId() == null) {
								log.warnf("Fail to find getMasterDeviceId for devices %d %s net %d",
										devOnlineO.getIana_id(), devOnlineO.getSn(), devOnlineO.getNetwork_id());
								return;
							}

							if (ConfigBackupUtils.getDeviceConfigAndSave2GivenPath(devOnlineO.getOrganization_id(),
									net.getMasterDeviceId(), putObject.getFilepath())) {
								log.infof("PIPE_INFO_TYPE_CONFIG_PUT_MASTER sid %s master config saved to path %s",
										putObject.getSid(), putObject.getFilepath());
							} else {
								log.errorf("getDeviceConfigAndSave2GivenPath %s failure", putObject.getFilepath());
								return;
							}
						}
					} else {
						log.errorf("info is null! %s", aco.getJson());
					}
				}

				log.infof("fetchCommand %s to sn %s with sid %s", aco.getType(), aco.getSn(), aco.getSid());
				Future<String> result = executorAcWtpCmd.submit(new FifoWriter(ps
						.getString("USER_REQ_AC_TO_WTP_FIFO"), aco.getJson()));

				try {
					result.get(COMMAND_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					log.error("Exception for ExecutorService (2)" + e);
					return;
				}

				log.info("fetchCommand from cache ok");
				return;
			}
			
			try {
				ACUtil.removePoolObjectBySn(aco, ACCommandObject.class);
			} catch (InstantiationException | IllegalAccessException e1) {
				log.error("removePoolObjectBySn Exception");
			}	
		}
		
	}
}
