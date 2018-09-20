package com.littlecloud.ac.health;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

import com.littlecloud.ac.WtpMsgHandlerPool;
import com.littlecloud.ac.messagehandler.executor.MessageExecutorsController;
import com.littlecloud.ac.messagehandler.queue.executor.CaptivePortalMessageHandleExecutorController;
import com.littlecloud.ac.messagehandler.queue.executor.CaptivePortalMessageThreadExecutor;
import com.littlecloud.control.devicechange.DeviceChangeService;
import com.littlecloud.control.deviceconfig.DeviceConfigScheduler;
import com.littlecloud.control.firmware.FirmwareScheduler;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerDeviceTask;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;


public class ThreadPoolManager {

	private static final Logger log = Logger.getLogger(ThreadPoolManager.class);
	
	public static enum ExecutorType
	{	
		ExecutorService,
		ScheduledExecutorService,
		ThreadPoolExecutor
	}
	public static enum ServiceType
	{		
		WtpMsgHandlerPool, /* WtpMsgHandlerPool ==> ThreadPoolExecutor */
		ScheduledExecutorService,  /* HealthMonitorHandler==> ScheduledExecutorService ScheduledThreadPool */
		MessageExecutorsController, /* MessageExecutorsController==> MessageExecutor */
		CaptivePortalMessageHandleExecutorController, /* CaptivePortalMessageHandleExecutorController==> CaptivePortalMessageThreadExecutor */
		DeviceChangeService,/* BlockingQueueExecutor, DeviceChangeService ==>  */
		DeviceConfigScheduler,
		FirmwareScheduler,
		ConfigUpdatePerDeviceTask,
		ConfigUpdatePerNetworkTask,
		ReportConsolidateService, /* ReportConsolidateService==> ,  */		
	}
	private static ThreadPoolManager instance;
//	public static ConcurrentHashMap<Long, String> threadPoolInfoMap = new ConcurrentHashMap<Long, String>();	
//	private static List<ThreadPoolAdapterInfo> threadPoolInfoList = new ArrayList<ThreadPoolAdapterInfo>();
	
	private static List<ThreadPoolAdapterInfo> threadExecutorInfoList = new ArrayList<ThreadPoolAdapterInfo>(); 

	public static void addExecutorMonitor(ThreadPoolAdapterInfo execInfo)
	{		
		if (execInfo!=null) 
			threadExecutorInfoList.add(execInfo);
	}
	
	public static void refreshExecutorMonitor(ThreadPoolAdapterInfo execInfo)
	{
		boolean found = false;
		if (threadExecutorInfoList != null && execInfo != null){
			for (ThreadPoolAdapterInfo info : threadExecutorInfoList) {
				if (info.getType() == execInfo.getType() && info.getName()== execInfo.getName()) {
					threadExecutorInfoList.remove(info);
					threadExecutorInfoList.add(execInfo);
					found = true;
				}
			}
		}
		if(!found) addExecutorMonitor(execInfo);
	}
	
	public static void removeExecutorMonitor(ThreadPoolAdapterInfo execInfo)
	{
		if (threadExecutorInfoList != null && execInfo != null)
			for (ThreadPoolAdapterInfo info : threadExecutorInfoList) {
				if (info.getType() == execInfo.getType() && info.getName()== execInfo.getName()) {
					threadExecutorInfoList.remove(info);
				}
			}
	}
	
	private ThreadPoolManager(){
		/* Test to use singleton design pattern */
		getInstance();
	}
	
	public synchronized static ThreadPoolManager getInstance()
	{
		if (instance != null)
			return instance;

		try {
			instance = new ThreadPoolManager();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}
	
	public static ThreadPoolAdapterInfo getInfo(ServiceType service) {
		ThreadPoolAdapterInfo execinfo = new ThreadPoolAdapterInfo();
		switch (service) {
		case WtpMsgHandlerPool:
			WtpMsgHandlerPool wtppool = WtpMsgHandlerPool.getInstance();
			if (wtppool != null)
				execinfo =wtppool.getThreadPoolAdapterInfo();
			return execinfo;
		case CaptivePortalMessageHandleExecutorController:
			execinfo = CaptivePortalMessageHandleExecutorController.getThreadPoolAdapterInfo();
			return execinfo;
		case DeviceChangeService:
			execinfo = DeviceChangeService.getThreadPoolAdapterInfo();
			return execinfo;
		case MessageExecutorsController://AcInfoUpdateMessageHandler start
			execinfo = MessageExecutorsController.getThreadPoolAdapterInfo();
			return execinfo;
		case DeviceConfigScheduler:
			execinfo = DeviceConfigScheduler.getThreadPoolAdapterInfo();
			return execinfo;
		case FirmwareScheduler:
			execinfo = FirmwareScheduler.getThreadPoolAdapterInfo();
			return execinfo;
		case ConfigUpdatePerDeviceTask:
			execinfo = ConfigUpdatePerDeviceTask.getThreadPoolAdapterInfo();
			return execinfo;
		default:
			break;
		}
		return null;
	}

	public static List<ThreadPoolAdapterInfo> getAllInfo() {

		if (threadExecutorInfoList != null)
			threadExecutorInfoList.clear();
		ThreadPoolAdapterInfo wtppoolinfo = getInfo(ServiceType.WtpMsgHandlerPool);
		ThreadPoolAdapterInfo captivePortalinfo = getInfo(ServiceType.CaptivePortalMessageHandleExecutorController);
		ThreadPoolAdapterInfo deviceChangeinfo = getInfo(ServiceType.DeviceChangeService);
		ThreadPoolAdapterInfo deviceMessageinfo = getInfo(ServiceType.MessageExecutorsController);
		ThreadPoolAdapterInfo deviceConfigSchedulerinfo = getInfo(ServiceType.DeviceConfigScheduler);
		ThreadPoolAdapterInfo firmwareSchedulerinfo = getInfo(ServiceType.FirmwareScheduler);
		ThreadPoolAdapterInfo configUpdatePerDeviceTaskinfo = getInfo(ServiceType.ConfigUpdatePerDeviceTask);
		
		addExecutorMonitor(wtppoolinfo);
		addExecutorMonitor(firmwareSchedulerinfo);
		addExecutorMonitor(captivePortalinfo);
		addExecutorMonitor(deviceChangeinfo);
		addExecutorMonitor(deviceConfigSchedulerinfo);
		addExecutorMonitor(configUpdatePerDeviceTaskinfo);
		addExecutorMonitor(deviceMessageinfo);
		return threadExecutorInfoList;
	}
		
	public void terminateAll(){
		
		log.warn("ThreadPoolManager.terminateAll called ");
		try {
			
			for (ThreadPoolAdapterInfo info : threadExecutorInfoList)
			{
				ServiceType servicetype = info.getName();
				terminateService(servicetype, info);
				log.debugf("Terminate Executor Service! ThreadPoolAdapterInfo = %s", info.toString());
			}
		
		} catch (Exception e) {
			log.errorf("ThreadPoolManager.terminateAll pool %s", threadExecutorInfoList.toString(), e);
			e.printStackTrace();
		}
	}
	
	public void terminateService(ServiceType service, ThreadPoolAdapterInfo info) {
		if (info != null){
		switch (service) {
		case WtpMsgHandlerPool:
				try{
					ThreadPoolExecutor wtpE = info.getThread_pool_executor();
					if (wtpE != null)
					{
						if (!wtpE.isShutdown())
							wtpE.shutdown();
						//wtpE.awaitTermination(60, TimeUnit.SECONDS);
					}
				} catch (Exception e) {
					log.errorf("Terminated Executor Service! ServiceType = %s", service.toString(),e);
					e.printStackTrace();
				}
				log.warnf("Terminated Executor Service! ServiceType = %s", service.toString());
			break;
		case CaptivePortalMessageHandleExecutorController:
			CaptivePortalMessageHandleExecutorController.stop();
			log.warnf("Terminated Executor Service! ServiceType = %s", service.toString());
			break;
		case DeviceChangeService:
			DeviceChangeService.stopService();
			log.warnf("Terminate Executor Service! ServiceType = %s", service.toString());
			break;
		case MessageExecutorsController:
			/*AcInfoUpdateMessageHandler start*/
			MessageExecutorsController.stopAcInfoDeviceMessageExecutor();
			log.warnf("Terminated Executor Service! ServiceType = %s may throw interrupted exception", service.toString());
			break;
		case DeviceConfigScheduler:
			DeviceConfigScheduler.stopSchedule();
			log.warnf("Terminate Executor Service! ServiceType = %s", service.toString());
			break;
		default:
			break;
		}
		}
	}
	
	public static class ThreadMapMessage {
		private long threadId;
		private Date datetime;
		private boolean isAlive;
		private String message;
		
		public ThreadMapMessage start(long thread_id, boolean is_alive, String message) {
			this.threadId = thread_id;
			this.datetime = DateUtils.getUtcDate();
			this.isAlive = is_alive;
			this.message = message;
			return this;
		}
		public long getThreadId() {
			return threadId;
		}
		public void setThreadId(long threadId) {
			this.threadId = threadId;
		}
		public Date getDatetime() {
			return datetime;
		}
		public void setDatetime(Date datetime) {
			this.datetime = datetime;
		}
		public boolean isAlive() {
			return isAlive;
		}
		public void setAlive(boolean isAlive) {
			this.isAlive = isAlive;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("threadId=");
			builder.append(threadId);
			//builder.append("<br>");
			builder.append("datetime=");
			builder.append(datetime);
			//builder.append("<br>");
			builder.append("isAlive=");
			builder.append(isAlive);
			//builder.append("<br>");
			builder.append("message=");
			builder.append(message);
			//builder.append("<br>");
			return builder.toString();
		}
		
	}
	
	public static void main() {
		ThreadPoolManager manager = ThreadPoolManager.getInstance();
		//TODO test
		
	}
}
