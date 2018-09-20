package com.littlecloud.ac.health;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//import org.apache.log4j.Logger;


import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.WtpCounterService;
import com.littlecloud.ac.WtpMsgHandler;
import com.littlecloud.ac.health.info.ACInfo;
import com.littlecloud.ac.health.info.UnHealthInfo;
import com.littlecloud.control.dao.branch.HealthCheckDAO;
import com.littlecloud.control.dao.branch.HealthCheckNormalHistoryDAO;
import com.littlecloud.control.entity.branch.HealthCheck;
import com.littlecloud.control.entity.branch.HealthCheckNormalHistory;
import com.littlecloud.pool.Cluster;
import com.littlecloud.pool.control.QueueCacheControl;
import com.littlecloud.pool.control.QueueControl;
import com.littlecloud.pool.utils.PropertyService;

public class HealthMonitorHandler
{
	private static Logger logger = Logger.getLogger(HealthMonitorHandler.class);
	private static PropertyService<HealthMonitorHandler> ps = new PropertyService<HealthMonitorHandler>(HealthMonitorHandler.class);
	private static final Integer monitor_sys_interval_min = ps.getInteger("monitor_sys_interval_min");
	private static final Integer health_recovery_time_in_sec = ps.getInteger("health_recovery_time_in_sec");
	private static final String serverName = ACService.getServerName();
	private static final String recipients = ps.getString("recipients");
	private static String clusterStatusDetail;
	private static Date startUpTime;
	private static boolean print_flag = false;
	private static boolean isClusterHealthy = false;
	private static boolean isSystemHealthy = false;
//	private static boolean isSystemHealthyToProcessMessage = false;
	private static String lastunhealthyreason;
	
	public enum HealthInfo
	{
		ac_info, db_info, jvm_info, core_info, disk_info, sys_info
	}
	
	public static String getClusterStatusDetail() {
		return clusterStatusDetail;
	}

	public static Integer getMonitorSysIntervalMin()
	{
		return monitor_sys_interval_min;
	}
	
	public static String getReceipients() {
		return recipients;
	}

	public static Date getStartUpTime() {
		return startUpTime;
	}
	
	public static void setStartUpTime(Date startUpTime) {
		HealthMonitorHandler.startUpTime = startUpTime;
	}

	public static boolean isClusterHealthy() {
		return isClusterHealthy;
	}

	public static void setClusterHealthy(boolean isClusterHealthy) {
		HealthMonitorHandler.isClusterHealthy = isClusterHealthy;
	}

	public static String getLastunhealthyreason() {
		return lastunhealthyreason;
	}

	public static void setLastunhealthyreason(String lastunhealthyreason) {
		HealthMonitorHandler.lastunhealthyreason = lastunhealthyreason;
	}

	public static boolean isSystemHealthy() {
		return isSystemHealthy;
	}
	
//	public static boolean isSystemHealthyToProcessMessage() {
//		return isSystemHealthyToProcessMessage;
//	}

	public static void setSystemHealthy(boolean isSystemHealthy) {
		HealthMonitorHandler.isSystemHealthy = isSystemHealthy;
	}

	public static void persistSystemHealthInfo()
	{
		HealthCheck health = null;
		HealthCheckDAO healthCheckDAO = null;
		
		try
		{
			logger.info("HC001:Persist system health infos by schedule " + serverName);
			healthCheckDAO = new HealthCheckDAO();
			health = healthCheckDAO.getServerHealthInfo(serverName);
			String status = null;
			UnHealthInfo info = getSystemHealthyDetail();
			if( health != null )
			{
				long timediff = 0;
				if(info.isHealthy())
				{
					timediff = new Date().getTime() - health.getLastunhealthy().getTime();
					if( health.getLastunhealthy() != null && timediff < (health_recovery_time_in_sec * 1000) )
					{
						health.setStatus(1);
						clusterStatusDetail = "last unhealthy time within health recovery period.";
					}
					else if( startUpTime != null && new Date().getTime() - startUpTime.getTime() < (health_recovery_time_in_sec * 1000) )
					{
						health.setStatus(1);
						clusterStatusDetail = "startup time within health recovery period.";
					}
					else
					{
						health.setStatus(2);
						clusterStatusDetail = "normal.";
					}
					status = info.getStatus();
					health.setLasthealthy(new Date());
					
//					if( timediff > 60000 )
//					{
//						isSystemHealthyToProcessMessage = true;
//					}
//					
//					logger.info("Process timediff is " + timediff + " isSystemHealthyToProcessMessage : " + isSystemHealthyToProcessMessage );
				}
				else
				{
//					if( timediff > 60000 && info.isStartup() )
//						isSystemHealthyToProcessMessage = true;
//					else
//						isSystemHealthyToProcessMessage = false;
					clusterStatusDetail = "standalone server is unhealthy.";
					health.setStatus(0);
					health.setLastunhealthy(new Date());
					status = info.getStatus();
					String reason = info.getUnhealthy_reason();
					if(reason!=null && reason.length() > 200)
						health.setLastUnhealthyReason(reason.substring(0,200));
					else
						health.setLastUnhealthyReason(reason);
				}
				health.setStatus_detail(status);
				health.setLastupdate(new Date());
				healthCheckDAO.update(health);
			}
			else
			{
				health = new HealthCheck();
				health.setServer_id(serverName);
				if(getSystemHealthStatus())
				{
					if( startUpTime != null && new Date().getTime() - startUpTime.getTime() < (health_recovery_time_in_sec * 1000) )
					{
						health.setStatus(1);
						clusterStatusDetail = "startup time within health recovery period (2).";
					}
					else
						health.setStatus(2);
					clusterStatusDetail = "normal (2).";
					health.setLasthealthy(new Date());
				}
				else
				{
					health.setStatus(0);
					clusterStatusDetail = "standalone server is unhealthy (2).";
					health.setLastunhealthy(new Date());
					status = info.getStatus();
					String reason = info.getUnhealthy_reason();
					if(reason.length() > 200)
						health.setLastUnhealthyReason(reason.substring(0,200));
					else
						health.setLastUnhealthyReason(reason);
				}
				health.setStatus_detail(info.getStatus());
				health.setLastupdate(new Date());
				healthCheckDAO.save(health);
//				if(health.getStatus() == 0)
//					isSystemHealthyToProcessMessage = false;
//				else
//					isSystemHealthyToProcessMessage = true;
			}
		}
		catch(Exception e)
		{
			logger.error("Persist system health error - " + e, e);
		}
	}
	
	public static void persistSystemHealthInfo(ACInfo info)
	{
		HealthCheck health = null;
		HealthCheckDAO healthCheckDAO = null;
		
		try
		{
			logger.info("HC001:Persist system health infos by msg type");
			healthCheckDAO = new HealthCheckDAO();
			health = healthCheckDAO.getServerHealthInfo(serverName);
			if( health != null )
			{
				if(isSystemHealthy(info))
				{
					if( health.getLastunhealthy() != null && new Date().getTime() - health.getLastunhealthy().getTime() < (health_recovery_time_in_sec * 1000) )
					{
						health.setStatus(1);
					}
					else
					{
						health.setStatus(2);
					}
					health.setLasthealthy(new Date());
				}
				else
				{
					health.setStatus(0);
					health.setLastunhealthy(new Date());
				}
				health.setStatus_detail(getSystemHealthyDetail(info));
				health.setLastupdate(new Date());
				
				if( health.getStatus() == 0 )
					healthCheckDAO.update(health);
			}
			else
			{
				health = new HealthCheck();
				health.setServer_id(serverName);
				if(isSystemHealthy(info))
				{
					health.setStatus(2);
					health.setLasthealthy(new Date());
				}
				else
				{
					health.setStatus(0);
					health.setLastunhealthy(new Date());
				}
				health.setStatus_detail(getSystemHealthyDetail(info));
				health.setLastupdate(new Date());
				
				if(health.getStatus() == 0)
					healthCheckDAO.save(health);
			}
		}
		catch(Exception e)
		{
			logger.error("Persist system health error - " + e, e);
		}
	}
	
	public static void persistACHealthInfo()
	{
		ACHealthMonitor acHealthMonitor = null;
//		HealthCheck health = null;
		HealthCheckNormalHistory normalHistory = null; 
//		HealthCheckDAO healthCheckDAO = null;
		HealthCheckNormalHistoryDAO healthCheckNormalHistoryDAO = null;
		
		try
		{
			logger.info("HC001:Persist ac health infos by schedule");
			healthCheckNormalHistoryDAO = new HealthCheckNormalHistoryDAO();
			
			acHealthMonitor = new ACHealthMonitor();
			acHealthMonitor.collectInfo();
			String acJson = acHealthMonitor.toJson();
					
			normalHistory = new HealthCheckNormalHistory();
			normalHistory.setServer_id(serverName);
			normalHistory.setService(HealthInfo.ac_info.name());
			normalHistory.setJson(acJson);
			normalHistory.setTimestamp(new Date());
			if(acHealthMonitor.isHealthy())
				normalHistory.setStatus(2);
			else
				normalHistory.setStatus(0);
			
			healthCheckNormalHistoryDAO.save(normalHistory);
		}
		catch( Exception e )
		{
			logger.error("Persist ac info err -", e);
		}
	}
	
	public static void persistACHealthInfo(ACInfo info)
	{
		ACHealthMonitor acHealthMonitor = null;
//		HealthCheck health = null;
		HealthCheckNormalHistory normalHistory = null; 
//		HealthCheckDAO healthCheckDAO = null;
		HealthCheckNormalHistoryDAO healthCheckNormalHistoryDAO = null;
		
		try
		{
			logger.info("HC001:Persist ac health infos by msg type");
			healthCheckNormalHistoryDAO = new HealthCheckNormalHistoryDAO();
			
			acHealthMonitor = new ACHealthMonitor();
			String acJson = acHealthMonitor.toJson(info);
					
			normalHistory = new HealthCheckNormalHistory();
			normalHistory.setServer_id(serverName);
			normalHistory.setService(HealthInfo.ac_info.name());
			normalHistory.setJson(acJson);
			normalHistory.setTimestamp(new Date());
			if(acHealthMonitor.isHealthy(info))
				normalHistory.setStatus(2);
			else
			{
				normalHistory.setStatus(0);
				healthCheckNormalHistoryDAO.save(normalHistory);
			}
		}
		catch( Exception e )
		{
			logger.error("Persist ac info by msg type err -", e);
		}
	}
	
	public static void persistDBHealthInfo()
	{
		DBHealthMonitor dbHealthMonitor = null;
//		HealthCheck health = null;
		HealthCheckNormalHistory normalHistory = null; 
//		HealthCheckDAO healthCheckDAO = null;
		HealthCheckNormalHistoryDAO healthCheckNormalHistoryDAO = null;
		
		try
		{
			logger.info("HC001:Persist db health infos by schedule");
			healthCheckNormalHistoryDAO = new HealthCheckNormalHistoryDAO();
			
			dbHealthMonitor = new DBHealthMonitor();
			dbHealthMonitor.collectInfo();
			String dbJson = dbHealthMonitor.toJson();
			
			normalHistory = new HealthCheckNormalHistory();
			normalHistory.setServer_id(serverName);
			normalHistory.setService(HealthInfo.db_info.name());
			normalHistory.setJson(dbJson);
			normalHistory.setTimestamp(new Date());
			if(dbHealthMonitor.isHealthy())
				normalHistory.setStatus(2);
			else
				normalHistory.setStatus(0);
			
			healthCheckNormalHistoryDAO.save(normalHistory);
		}
		catch( Exception e )
		{
			logger.error("Persist ac info err -", e);
		}
	}
	
	public static void persistDiskHealthInfo()
	{
		DiskHealthMonitor diskHealthMonitor = null;
//		HealthCheck health = null;
		HealthCheckNormalHistory normalHistory = null; 
//		HealthCheckDAO healthCheckDAO = null;
		HealthCheckNormalHistoryDAO healthCheckNormalHistoryDAO = null;
		
		try
		{
			logger.info("HC001:Persist disk health infos by schedule");
			healthCheckNormalHistoryDAO = new HealthCheckNormalHistoryDAO();
			
			diskHealthMonitor = new DiskHealthMonitor();
			diskHealthMonitor.collectInfo();
			String diskJson = diskHealthMonitor.toJson();
			
			normalHistory = new HealthCheckNormalHistory();
			normalHistory.setServer_id(serverName);
			normalHistory.setService(HealthInfo.disk_info.name());
			normalHistory.setJson(diskJson);
			normalHistory.setTimestamp(new Date());
			if(diskHealthMonitor.isHealthy())
				normalHistory.setStatus(2);
			else
				normalHistory.setStatus(0);
			
			healthCheckNormalHistoryDAO.save(normalHistory);
		}
		catch( Exception e )
		{
			logger.error("Persist ac info err -", e);
		}
	}
	
	public static void persistJVMHealthInfo()
	{
		JVMHealthMonitor jvmHealthMonitor = null;
//		HealthCheck health = null;
		HealthCheckNormalHistory normalHistory = null; 
//		HealthCheckDAO healthCheckDAO = null;
		HealthCheckNormalHistoryDAO healthCheckNormalHistoryDAO = null;
		
		try
		{
			logger.info("HC001:Persist jvm health infos by schedule");
			healthCheckNormalHistoryDAO = new HealthCheckNormalHistoryDAO();
			
			jvmHealthMonitor = new JVMHealthMonitor();
			jvmHealthMonitor.collectInfo();
			String jvmJson = jvmHealthMonitor.toJson();
			
			normalHistory = new HealthCheckNormalHistory();
			normalHistory.setServer_id(serverName);
			normalHistory.setService(HealthInfo.jvm_info.name());
			normalHistory.setJson(jvmJson);
			normalHistory.setTimestamp(new Date());
			if(jvmHealthMonitor.isHealthy())
				normalHistory.setStatus(2);
			else
				normalHistory.setStatus(0);
			
			healthCheckNormalHistoryDAO.save(normalHistory);
		}
		catch( Exception e )
		{
			logger.error("Persist ac info err -", e);
		}
	}
	
	public static void persistCoreHealthInfo()
	{
		CoreHealthMonitor coreHealthMonitor = null;
//		HealthCheck health = null;
		HealthCheckNormalHistory normalHistory = null; 
//		HealthCheckDAO healthCheckDAO = null;
		HealthCheckNormalHistoryDAO healthCheckNormalHistoryDAO = null;
		
		try
		{
			logger.info("HC001:Persist core health infos by schedule");
			healthCheckNormalHistoryDAO = new HealthCheckNormalHistoryDAO();
			
			coreHealthMonitor = new CoreHealthMonitor();
			coreHealthMonitor.collectInfo();
			String coreJson = coreHealthMonitor.toJson();
			
			normalHistory = new HealthCheckNormalHistory();
			normalHistory.setServer_id(serverName);
			normalHistory.setService(HealthInfo.core_info.name());
			normalHistory.setJson(coreJson);
			normalHistory.setTimestamp(new Date());
			if(coreHealthMonitor.isHealthy())
				normalHistory.setStatus(2);
			else
				normalHistory.setStatus(0);
			
			healthCheckNormalHistoryDAO.save(normalHistory);
			logger.info("Persist core health info completed");
			WtpCounterService.CountTask(WtpMsgHandler.getMsg_process_counter()); //read wtp_msg_process_counter before set 0;
			WtpMsgHandler.setMsg_process_counter(0);
			QueueControl.setMsg_process_counter(0);
			QueueCacheControl.setMsg_process_counter(0);
		}
		catch( Exception e )
		{
			logger.error("Persist ac info err -", e);
		}
	}
	
	public static boolean getLatestClusterHealthStatus()
	{
		boolean result = false;
		HealthCheckDAO healthCheckDAO = null;
		int status = 0;
		
		try
		{
			healthCheckDAO = new HealthCheckDAO();
			List<HealthCheck> infos = healthCheckDAO.getLatestServerHealthInfos();
			if (infos.size()==Cluster.CONFIG_MEMBERS.size())
			{
				if( infos != null && !infos.isEmpty() )
				{
					status = infos.get(0).getStatus();
					setLastunhealthyreason( infos.get(0).getLastUnhealthyReason());
					for(HealthCheck info : infos)
					{
						if(info.getStatus() < status)
							status = info.getStatus();
						setLastunhealthyreason( infos.get(0).getLastUnhealthyReason());
					}
				}
			}
			
			if(status == 2)
				result = true;
		}
		catch(SQLException e)
		{
			logger.error("Get cluster health status error - " + e, e);
			return false;
		}
		
		return result;
	}
	
	public static List<HealthCheck> getLatestClusterHealthyInfos()
	{
		HealthCheckDAO healthCheckDAO = null;
		List<HealthCheck> infos = null;
		
		try
		{
			healthCheckDAO = new HealthCheckDAO();
			infos = healthCheckDAO.getLatestServerHealthInfos();
		}
		catch(SQLException e)
		{
			logger.error("Get cluster health status error - " + e, e);
		}
		
		return infos;
	}
	
	public static boolean getSystemHealthStatus()
	{
		boolean result = true;
		
		ACHealthMonitor acHealthMonitor = null;
		DBHealthMonitor dbHealthMonitor = null;
		DiskHealthMonitor diskHealthMonitor = null;
		JVMHealthMonitor jvmHealthMonitor = null;
		CoreHealthMonitor coreHealthMonitor = null;
		
		try
		{
			acHealthMonitor = new ACHealthMonitor();
			dbHealthMonitor = new DBHealthMonitor();
			diskHealthMonitor = new DiskHealthMonitor();
			jvmHealthMonitor = new JVMHealthMonitor();
			coreHealthMonitor = new CoreHealthMonitor();
			
			acHealthMonitor.collectInfo();
			String dbInfo = dbHealthMonitor.collectDBInfo();
			diskHealthMonitor.collectInfo();
			jvmHealthMonitor.collectInfo();
			coreHealthMonitor.collectInfo();
			
			boolean isAcHealthy = acHealthMonitor.isHealthy();
			boolean isDBHealthy = dbHealthMonitor.isHealthy();
			boolean isDiskHealthy = diskHealthMonitor.isHealthy();
			boolean isJvmHealthy = jvmHealthMonitor.isHealthy();
			boolean isCoreHealthy = coreHealthMonitor.isHealthy();
			
			if( !isAcHealthy ||
				!isDBHealthy ||
				!isDiskHealthy ||
				!isJvmHealthy ||
				!isCoreHealthy )
			{
				result = false;
			}
			
			if( !isDBHealthy && print_flag == false)
			{
				logger.warn("DB status is unhealthy " + dbInfo);
				print_flag = true;
			}
			else if(isDBHealthy && print_flag == true)
			{
				print_flag = false;
			}
		}
		catch(Exception e)
		{
			logger.error("Get system health status err -", e);
			result = false;
		}
		
		return result;
	}
	
	public static boolean isSystemHealthy(ACInfo info)
	{
		boolean result = true;
		
		ACHealthMonitor acHealthMonitor = null;
		DBHealthMonitor dbHealthMonitor = null;
		DiskHealthMonitor diskHealthMonitor = null;
		JVMHealthMonitor jvmHealthMonitor = null;
		CoreHealthMonitor coreHealthMonitor = null;
		
		try
		{
			acHealthMonitor = new ACHealthMonitor();
			dbHealthMonitor = new DBHealthMonitor();
			diskHealthMonitor = new DiskHealthMonitor();
			jvmHealthMonitor = new JVMHealthMonitor();
			coreHealthMonitor = new CoreHealthMonitor();
			
			dbHealthMonitor.collectInfo();
			diskHealthMonitor.collectInfo();
			jvmHealthMonitor.collectInfo();
			coreHealthMonitor.collectInfo();
			
			if( !acHealthMonitor.isHealthy(info) ||
				!dbHealthMonitor.isHealthy() ||
				!diskHealthMonitor.isHealthy() ||
				!jvmHealthMonitor.isHealthy() ||
				!coreHealthMonitor.isHealthy() )
			{
				result = false;
			}
		}
		catch(Exception e)
		{
			logger.error("Get system health status err -", e);
			result = false;
		}
		
		return result;
	}
	
	public static UnHealthInfo getSystemHealthyDetail()
	{
		UnHealthInfo result = new UnHealthInfo();
		StringBuffer status = new StringBuffer();
		StringBuffer reason = new StringBuffer();
		boolean isHealthy = true;
		boolean isStartup = false;
		
		ACHealthMonitor acHealthMonitor = null;
		DBHealthMonitor dbHealthMonitor = null;
		DiskHealthMonitor diskHealthMonitor = null;
		JVMHealthMonitor jvmHealthMonitor = null;
		CoreHealthMonitor coreHealthMonitor = null;
		
		try
		{
			acHealthMonitor = new ACHealthMonitor();
			dbHealthMonitor = new DBHealthMonitor();
			diskHealthMonitor = new DiskHealthMonitor();
			jvmHealthMonitor = new JVMHealthMonitor();
			coreHealthMonitor = new CoreHealthMonitor();
			
			acHealthMonitor.collectInfo();
			dbHealthMonitor.collectInfo();
			diskHealthMonitor.collectInfo();
			jvmHealthMonitor.collectInfo();
			coreHealthMonitor.collectInfo();
			
			if( acHealthMonitor.isHealthy() )
			{
				status.append("1");
			}
			else
			{
				status.append("0");
				isHealthy = false;
				reason.append(acHealthMonitor.getUnhealthReason());
				reason.append(",");
				if(isStartup)
					isStartup = false;
			}
			
			if( dbHealthMonitor.isHealthy() )
			{
				status.append("1");
			}
			else
			{
				status.append("0");
				isHealthy = false;
				reason.append("DB status is unhealthy");
				reason.append(",");
				if(isStartup)
					isStartup = false;
			}
			
			if( diskHealthMonitor.isHealthy() )
			{
				status.append("1");
			}
			else
			{
				status.append("0");
				isHealthy = false;
				reason.append("Disk status is unhealthy");
				reason.append(",");
				if(isStartup)
					isStartup = false;
			}
			
			if( jvmHealthMonitor.isHealthy() )
			{
				status.append("1");
			}
			else
			{
				status.append("0");
				isHealthy = false;
				reason.append("JVM status is unhealthy");
				reason.append(",");
				if(isStartup)
					isStartup = false;
			}
			
			if( coreHealthMonitor.isHealthy() )
			{
				status.append("1");
			}
			else
			{
				status.append("0");
				isHealthy = false;
				String core_reason = coreHealthMonitor.getUnhealthyReason();
				reason.append(core_reason);
				if(core_reason!= null && core_reason.equals("- System started up less than 5 mins"))
				{
					isStartup = true;
				}
				else
				{
					isStartup = false;
				}
			}
			
			result.setHealthy(isHealthy);
			result.setStatus(status.toString());
			result.setStartup(isStartup);
			result.setUnhealthy_reason(reason.toString());
		}
		catch(Exception e)
		{
			logger.error("Get system health status err -", e);
		}
		
		return result;
	}
	
	public static String getSystemHealthyDetail(ACInfo info)
	{
		StringBuffer result = new StringBuffer();
		
		ACHealthMonitor acHealthMonitor = null;
		DBHealthMonitor dbHealthMonitor = null;
		DiskHealthMonitor diskHealthMonitor = null;
		JVMHealthMonitor jvmHealthMonitor = null;
		CoreHealthMonitor coreHealthMonitor = null;
		
		try
		{
			acHealthMonitor = new ACHealthMonitor();
			dbHealthMonitor = new DBHealthMonitor();
			diskHealthMonitor = new DiskHealthMonitor();
			jvmHealthMonitor = new JVMHealthMonitor();
			coreHealthMonitor = new CoreHealthMonitor();
	
			if( acHealthMonitor.isHealthy(info) )
			{
				result.append("1");
			}
			else
			{
				result.append("0");
			}
			
			if( dbHealthMonitor.isHealthy() )
			{
				result.append("1");
			}
			else
			{
				result.append("0");
			}
			
			if( diskHealthMonitor.isHealthy() )
			{
				result.append("1");
			}
			else
			{
				result.append("0");
			}
			
			if( jvmHealthMonitor.isHealthy() )
			{
				result.append("1");
			}
			else
			{
				result.append("0");
			}
			
			if( coreHealthMonitor.isHealthy() )
			{
				result.append("1");
			}
			else
			{
				result.append("0");
			}
		}
		catch(Exception e)
		{
			logger.error("Get system health status err -", e);
			result = new StringBuffer();
		}
		
		return result.toString();
	}
	
	public void schedulePersistHealthInfo(int interval, HealthInfo info)
	{
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		Runnable persistJob = null;
		
		switch(info)
		{
			case ac_info:
				persistJob = new Runnable()
				{
					public void run() 
					{
						try 
						{
							logger.info("ac info check start");
							persistACHealthInfo();
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				};
			break;
			case db_info:
				persistJob = new Runnable()
				{
					public void run() 
					{
						try 
						{
							logger.info("db info check start");
							persistDBHealthInfo();
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				};
			break;
			case disk_info:
				persistJob = new Runnable()
				{
					public void run() 
					{
						try 
						{
							logger.info("disk info check start");
							persistDiskHealthInfo();
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				};
			break;
			case jvm_info:
				persistJob = new Runnable()
				{
					public void run() 
					{
						try 
						{
							logger.info("jvm info check start");
							persistJVMHealthInfo();
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				};
			break;
			case core_info:
				persistJob = new Runnable()
				{
					public void run() 
					{
						try 
						{
							logger.info("core info check start");
							persistCoreHealthInfo();
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				};
			break;
			case sys_info:
				persistJob = new Runnable()
				{
					public void run() 
					{
						try 
						{
							logger.info("sys info check start");
							persistSystemHealthInfo();
							setClusterHealthy(getLatestClusterHealthStatus());
							setSystemHealthy(getSystemHealthStatus());
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}
						
					}
				};
			break;
		}
		
		ScheduledFuture updateTimeHandler = scheduler.scheduleAtFixedRate(persistJob, 5, interval, TimeUnit.SECONDS);
	}
}
