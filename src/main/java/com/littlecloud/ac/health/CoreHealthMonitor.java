package com.littlecloud.ac.health;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.infinispan.Cache;
import org.jboss.logging.Logger;

import com.littlecloud.ac.WtpCounterService;
import com.littlecloud.ac.WtpMsgHandler;
import com.littlecloud.ac.handler.BranchServerRedirectionHandler;
import com.littlecloud.ac.handler.RootServerRedirectionHandler;
import com.littlecloud.ac.health.info.CoreInfo;
import com.littlecloud.ac.util.RootBranchRedirectManager;
import com.littlecloud.control.dao.branch.HealthCheckDAO;
import com.littlecloud.control.deviceconfig.DeviceConfigQueue;
import com.littlecloud.control.deviceconfig.DeviceConfigScheduler;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.Cluster;
import com.littlecloud.pool.MonitorAction;
import com.littlecloud.pool.control.QueueCacheControl;
import com.littlecloud.pool.control.QueueControl;
import com.littlecloud.pool.object.ClusterMemberInfoObject;
import com.littlecloud.pool.object.EventObject;
import com.littlecloud.pool.utils.PropertyService;
import com.sun.management.OperatingSystemMXBean;

@SuppressWarnings("restriction")
public class CoreHealthMonitor implements HealthMonitor {
	private static Logger log = Logger.getLogger(CoreHealthMonitor.class);
	private static final int INIT_MAX_OBJECTS = 1000000; // 100k

	private static final PropertyService<CoreHealthMonitor> ps = new PropertyService<CoreHealthMonitor>(
			CoreHealthMonitor.class);
	private static final Integer health_core_wtpmsg_thread_count = ps.getInteger("health_core_wtpmsg_thread_count");
	private static final Integer health_core_db_queue_size = ps.getInteger("health_core_db_queue_size");
	private static final Integer health_core_cache_queue_size = ps.getInteger("health_core_cache_queue_size");
	private static final Integer health_core_info_update_time = ps.getInteger("health_core_info_update_time") * 60;
	private static final Integer health_db_queue_size = ps.getInteger("health_db_queue_size");
	private static final Integer health_monitor_core_interval_min = ps.getInteger("monitor_core_interval_min");
	private static final Integer health_core_startup_time_in_sec = ps.getInteger("health_core_startup_time_in_sec");
	private static CoreInfo info = new CoreInfo();

	public static Integer getHealthMonitorCoreIntervalMin() {
		return health_monitor_core_interval_min;
	}

	public Date getServerTime() {
		return new Date();
	}

	@Override
	public CoreInfo getInfo() {
		// TODO Auto-generated method stub
		return info;
	}

	@Override
	public void collectInfo() {
		// TODO Auto-generated method stub
		info.setUnhealthy_skip_msg(WtpMsgHandler.UNHEALTHY_SKIP_MESSAGE);
		info.setWtpmsg_thread_count(WtpMsgHandler.getCounter());
		info.setWtpmsg_thread_max_count(WtpMsgHandler.getCounterMax());
		info.setCurrent_online_event_counter(WtpMsgHandler.getCurrentOnlineEventCounter().intValue());
		Set<String> cacheQNames = QueueCacheControl.getAllQueueSet();
		int max_cache_queue_size = 0;
		for (String name : cacheQNames) {
			int temp_max = QueueCacheControl.getQueueSize(name);
			if (temp_max > max_cache_queue_size)
				max_cache_queue_size = QueueCacheControl.getQueueSize(name);
		}
		info.setMax_cache_queue_size(max_cache_queue_size);
		/*
		 * Set<String> dbQNames = QueueControl.getAllQueueSet(); int
		 * max_db_queue_size = 0; for(String name : dbQNames) { int temp_max =
		 * QueueControl.getQueueSize(name); if( temp_max > max_db_queue_size )
		 * max_db_queue_size = QueueControl.getQueueSize(name); }
		 * info.setMax_db_queue_size(max_db_queue_size);
		 */

		Map<String, Integer> dbQMap = new HashMap<String, Integer>();
		Set<String> dbQNames = QueueControl.getAllQueueSet();
		int max_db_queue_size = 0;
		StringBuffer top_five_db_Q_org = new StringBuffer();
		if (dbQNames != null) {
			for (String name : dbQNames) {
				dbQMap.put(name, QueueControl.getQueueSize(name));
			}

			dbQNames = null;
			dbQMap = com.littlecloud.pool.utils.Utils.getSortedMap(dbQMap, false);
			dbQNames = dbQMap.keySet();
			if (dbQNames != null) {
				Object[] maxQNames = dbQNames.toArray();
				if (maxQNames.length >= 5) {
					for (int i = 0; i < 5; i++) {
						top_five_db_Q_org.append(maxQNames[i]);
						top_five_db_Q_org.append("|");
					}
					if (dbQMap.get(maxQNames[0]) != null)
						max_db_queue_size = dbQMap.get(maxQNames[0]);
				} else if (maxQNames.length > 0) {
					for (int i = 0; i < maxQNames.length; i++) {
						top_five_db_Q_org.append(maxQNames[i]);
						top_five_db_Q_org.append("|");
					}
					if (dbQMap.get(maxQNames[0]) != null)
						max_db_queue_size = dbQMap.get(maxQNames[0]);
				}
			}
		}
		info.setMax_db_queue_size(max_db_queue_size);
		info.setTop_five_db_Q_org(top_five_db_Q_org.toString());
		if (!WtpCounterService.isCount_task_start())
			WtpCounterService.snapshot();
		info.setWtpmsg_proc_msg_count(WtpCounterService.getAverage_msg_process_counter());// read
																							// every
																							// time
		// info.setWtpmsg_proc_msg_count(WtpMsgHandler.getMsg_process_counter());
		info.setQueue_ctrl_proc_msg_count(QueueControl.getMsg_process_counter());
		info.setQueue_cache_ctrl_proc_msg_count(QueueCacheControl.getMsg_process_counter());

		info.setQueueDevLoc_count(WtpMsgHandler.jsonQueue.size());
		/*
		 * int max_queue_size = 0; Set<String> orgIds =
		 * QueueCacheControl.getAllQueueSet(); for( String orgId : orgIds ) {
		 * int tmpSize = QueueCacheControl.getQueueSize(orgId); if( tmpSize >
		 * max_queue_size ) { max_queue_size = tmpSize; } }
		 * info.setDb_queue_size(max_queue_size);
		 */

		Map<String, Integer> cacheQMap = new HashMap<String, Integer>();
		Set<String> cacheQueueNames = QueueCacheControl.getAllQueueSet();
		int max_queue_size = 0;
		StringBuffer top_five_cache_Q_org = new StringBuffer();
		if (cacheQueueNames != null) {
			for (String name : cacheQueueNames) {
				cacheQMap.put(name, QueueCacheControl.getQueueSize(name));
			}

			cacheQueueNames = null;
			cacheQMap = com.littlecloud.pool.utils.Utils.getSortedMap(cacheQMap, false);
			cacheQueueNames = cacheQMap.keySet();
			if (cacheQueueNames != null) {
				Object[] maxQNames = cacheQueueNames.toArray();
				if (maxQNames.length >= 5) {
					for (int i = 0; i < 5; i++) {
						top_five_cache_Q_org.append(maxQNames[i]);
						top_five_cache_Q_org.append("|");
					}
					if (dbQMap.get(maxQNames[0]) != null)
						max_queue_size = dbQMap.get(maxQNames[0]);
				} else if (maxQNames.length > 0) {
					for (int i = 0; i < maxQNames.length; i++) {
						top_five_cache_Q_org.append(maxQNames[i]);
						top_five_cache_Q_org.append("|");
					}
					if (dbQMap.get(maxQNames[0]) != null)
						max_queue_size = dbQMap.get(maxQNames[0]);
				}
			}
		}
		info.setMax_cache_queue_size(max_queue_size);
		info.setTop_five_cache_Q_org(top_five_cache_Q_org.toString());

		info.setCluster_max_members(Cluster.getConfigClusterSize(Cluster.CACHENAME.LittleCloudCache));
		info.setCluster_members(Cluster.getClusterSize(Cluster.CACHENAME.LittleCloudCache));
		info.setGrid_object_count(Cluster.getCacheObjectSize(Cluster.CACHENAME.LittleCloudCache));
		info.setTimestamp(DateUtils.getUnixtime());
		
		try {
			OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
			info.setProc_cpu_time((int) (osBean.getProcessCpuTime() / 1000000));
			info.setProc_cpu_load((int) (osBean.getProcessCpuLoad() * 100));
			info.setSys_cpu_load((int) (osBean.getSystemCpuLoad() * 100));
		} catch (java.lang.NoClassDefFoundError e)
		{
			log.error("NoClassDefFoundError - fail to load OperatingSystemMXBean");
		}

		HashSet<String> total_cluster_objects = new HashSet<String>(INIT_MAX_OBJECTS);

		HashMap<String, Integer> cluster_objects = new HashMap<String, Integer>();
		for (String member : Cluster.CONFIG_MEMBERS) {
			try {
				if (log.isInfoEnabled())
					log.infof("collectInfo: getting member=%s info", member);

				ClusterMemberInfoObject clusterInfo = (ClusterMemberInfoObject) Cluster.get(
						Cluster.CACHENAME.LittleCloudCache, "CACHE_" + member);
				if (clusterInfo != null) {
					cluster_objects.put(member, clusterInfo.getTotalobj());
					total_cluster_objects.addAll(clusterInfo.getObjkeys());
					if (log.isInfoEnabled())
						log.infof("collectInfo: member=%s, objects size=%d", member, clusterInfo.getTotalobj());
				}
			} catch (Exception e) {
				log.error("collectInfo: e=" + e, e);
			}
		}
		info.setCluster_objects(cluster_objects);
		info.setTotal_cluster_objects(total_cluster_objects.size());
		if (log.isInfoEnabled())
			log.infof("collectInfo: cluster objects total size=%d", info.getTotal_cluster_objects());

//		EventObject evt = new EventObject();
//		Cache<String, Object> cacheRepl = Cluster.getCache(Cluster.CACHENAME.LittleCloudCacheRepl);
//		cacheRepl.put("EVENT_GETKEYS_" + evt.getId(), evt);
//		if (log.isInfoEnabled())
//			log.info("collectInfo: triggered EVENT_GETKEYS_" + evt.getId());
	}

	@Override
	public boolean isHealthy() {
		boolean result;

		try {
			if (DateUtils.getUnixtime() - info.getTimestamp() > health_core_info_update_time
					|| info.getWtpmsg_thread_count() > health_core_wtpmsg_thread_count
					|| info.getCluster_max_members() != info.getCluster_members()
					|| info.getDb_queue_size() > health_db_queue_size
					|| info.getMax_cache_queue_size() > health_core_cache_queue_size
					|| info.getMax_db_queue_size() > health_core_db_queue_size
					|| HealthMonitorHandler.getStartUpTime() == null
					|| new Date().getTime() - HealthMonitorHandler.getStartUpTime().getTime() < (health_core_startup_time_in_sec * 1000)) {
				log.warn("Unhealthy! - " + "1 " + info.getTimestamp() + " " + health_core_info_update_time + "2 "
						+ info.getWtpmsg_thread_count() + " " + health_core_wtpmsg_thread_count + "3 "
						+ info.getCluster_max_members() != info.getCluster_members() + "4 " + info.getDb_queue_size()
						+ " " + health_db_queue_size + "5 " + info.getMax_cache_queue_size() + " "
						+ health_core_cache_queue_size + "6 " + info.getMax_db_queue_size() + " "
						+ health_core_db_queue_size + "7 " + HealthMonitorHandler.getStartUpTime());
				result = false;
			} else {
				result = true;
			}
		} catch (Exception e) {
			log.error(
					"Unhealthy! Exception - " + "1 " + info.getTimestamp() + " " + health_core_info_update_time + "2 "
							+ info.getWtpmsg_thread_count() + " " + health_core_wtpmsg_thread_count + "3 "
							+ info.getCluster_max_members() != info.getCluster_members() + "4 "
							+ info.getDb_queue_size() + " " + health_db_queue_size + "5 "
							+ info.getMax_cache_queue_size() + " " + health_core_cache_queue_size + "6 "
							+ info.getMax_db_queue_size() + " " + health_core_db_queue_size + "7 "
							+ HealthMonitorHandler.getStartUpTime(), e);
			return false;
		}

		return result;
	}

	public String getUnhealthyReason() {
		String result = "";

		if (DateUtils.getUnixtime() - info.getTimestamp() > health_core_info_update_time)
			result += "- unhealthy core info update time";

		if (info.getWtpmsg_thread_count() > health_core_wtpmsg_thread_count)
			result += "- unhealthy core wtpmsg thread count";

		if (info.getCluster_max_members() != info.getCluster_members())
			result += "- unhealthy cluster max members";

		if (info.getDb_queue_size() > health_db_queue_size)
			result += "- unhealthy db queue size";

		if (info.getMax_cache_queue_size() > health_core_cache_queue_size.intValue())
			result += "- unhealthy cache queue size";

		if (HealthMonitorHandler.getStartUpTime() == null
				|| new Date().getTime() - HealthMonitorHandler.getStartUpTime().getTime() < (health_core_startup_time_in_sec * 1000))
			result += "- System started up less than 5 mins (" + HealthMonitorHandler.getStartUpTime() + ")";

		return result;
	}

	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		return JsonUtils.toJson(getInfo());
	}

	public String toHtml(CoreInfo coreInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("------------------Core INFO-----------------");
		sb.append("<br/>");
		
		if (log.isDebugEnabled()) log.debug("coreHealthMonitor.isHealthy");
		if (this.isHealthy()) {
			sb.append("Status:");
			sb.append("<font color=\"green\">");
			sb.append("Healthy");
			sb.append("</font>");
		} else {
			sb.append("Status:");
			sb.append("<font color=\"red\">");
			sb.append("Unhealthy");
			sb.append("</font>");
			String reason = this.getUnhealthyReason();
			if (reason != null) {
				sb.append("<br>");
				sb.append("Unhealthy reason : ");
				sb.append(reason);
			}
		}
		sb.append("<br/>");
		sb.append("<br/>");
		
		if (log.isDebugEnabled()) log.debug("coreHealthMonitor config integrity check");
		sb.append("Server running mode:");
		sb.append(RootBranchRedirectManager.getServerMode());
		sb.append("<br/>");
		sb.append("- isBranchMode:");
		sb.append(RootBranchRedirectManager.isBranchServerMode());
		sb.append("<br/>");
		sb.append("Root server redirection:");
		sb.append(RootServerRedirectionHandler.getInstance().isRootRedirectionEnabled());
		sb.append("<br/>");
		sb.append("Branch server redirection:");
		sb.append(BranchServerRedirectionHandler.getInstance().isBranchredirectionenabled());
		sb.append("<br/>");
		if (RootServerRedirectionHandler.getInstance().isRootRedirectionEnabled() && BranchServerRedirectionHandler.getInstance().isBranchredirectionenabled())
		{
			sb.append("<font color=\"red\">");
			sb.append("Incorrect configuration! Branch and Root server redirection ON at the same time!");
			sb.append("</font>");
			sb.append("<br/>");
		}
		sb.append("<br/>");
		
		if (log.isDebugEnabled()) log.debug("coreInfo.isHealthy");
		if (RootBranchRedirectManager.isBranchServerMode())
		{
			if (coreInfo.isUnhealthy_skip_msg()) {
				sb.append("<font color=\"red\">");
				sb.append("UNHEALTHY SKIP MSG : ");
				sb.append(coreInfo.isUnhealthy_skip_msg());
				sb.append("<br/>");
				sb.append("</font>");
			} else {
				sb.append("UNHEALTHY SKIP MSG : ");
				sb.append(coreInfo.isUnhealthy_skip_msg());
			}
		}
		sb.append("<br/>");
		sb.append("Core uptime : ");
		sb.append(HealthMonitorHandler.getStartUpTime() == null ? 0 : (new Date().getTime() - HealthMonitorHandler
				.getStartUpTime().getTime()) / 1000);
		sb.append(" s");
		sb.append("<br/>");
		sb.append("Server clock: ");
		sb.append(this.getServerTime());
		sb.append("<br/>");
		sb.append("WTP Thread count : ");
		sb.append(coreInfo.getWtpmsg_thread_count());
		sb.append("<br/>");
		if (log.isDebugEnabled()) log.debug("DeviceConfigQueue.getQueueSize");
		if (DeviceConfigQueue.getQueueSize() >= DeviceConfigScheduler.getMaxQueueSize()) {
			sb.append("<font color=\"red\">");
			sb.append("Config backup Queue size : ");
			sb.append(DeviceConfigQueue.getQueueSize());
			sb.append("<br/>");
			sb.append("</font>");
		} else {
			sb.append("Config backup Queue size : ");
			sb.append(DeviceConfigQueue.getQueueSize());
			sb.append("<br/>");
		}
		sb.append("WTP MAX Thread count : ");
		sb.append(coreInfo.getWtpmsg_thread_max_count());
		sb.append("<br/>");
		sb.append("WTP CURRENT ONLINE EVENT COUNTER : ");
		sb.append(coreInfo.getCurrent_online_event_counter());
		sb.append("<br/>");
		sb.append("WTP PIPE_INFO_TYPE_DEV_LOCATIONS requeue size : ");
		sb.append(coreInfo.getQueueDevLoc_count());
		sb.append("<br/>");
		sb.append("MAX cache queue size : ");
		sb.append(coreInfo.getMax_cache_queue_size());
		sb.append("<br/>");
		sb.append("MAX db queue size : ");
		sb.append(coreInfo.getMax_db_queue_size());
		sb.append("<br/>");
		sb.append("WTP process msg : ");
		sb.append((int) (coreInfo.getWtpmsg_proc_msg_count() / 60));// already
																	// changed
																	// to
																	// average
		sb.append("/s  (");
		sb.append(coreInfo.getWtpmsg_proc_msg_count());
		sb.append("/min) ");
		// sb.append("interval ");
		// sb.append(WtpCounterService.getInterval());
		// sb.append("s ");
		sb.append("<br/>");
		sb.append("Queue control process msg : ");
		sb.append((int) (coreInfo.getQueue_ctrl_proc_msg_count() / 60));
		sb.append("/s");
		sb.append("<br/>");
		sb.append("Queue cache control process msg : ");
		sb.append((int) (coreInfo.getQueue_cache_ctrl_proc_msg_count() / 60));
		sb.append("/s");
		sb.append("<br/>");
		sb.append("Process cpu time : ");
		sb.append(coreInfo.getProc_cpu_time());
		sb.append(" sec");
		sb.append("<br/>");
		sb.append("Process cpu load : ");
		sb.append(coreInfo.getProc_cpu_load());
		sb.append("%");
		sb.append("<br/>");
		sb.append("System cpu load : ");
		sb.append(coreInfo.getSys_cpu_load());
		sb.append("%");
		sb.append("<br/>");
		sb.append("Cluster configured members: ");
		sb.append(Cluster.CONFIG_MEMBERS);
		sb.append("<br/>");
		sb.append("Cluster max members: ");
		sb.append(coreInfo.getCluster_max_members());
		sb.append("<br/>");
		sb.append("Cluster members: ");
		sb.append(coreInfo.getCluster_members());
		sb.append("<br/>");
		sb.append("Cluster objects: total=");
		sb.append(coreInfo.getTotal_cluster_objects());
		sb.append(", members=");
		sb.append(coreInfo.getCluster_objects());
		sb.append("<br/>");
		sb.append("timestamp : ");
		sb.append(coreInfo.getTimestamp());
		sb.append("<br/>");
		sb.append("<br/>");
		return sb.toString();
	}
}
