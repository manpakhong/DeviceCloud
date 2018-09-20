package com.littlecloud.control.deviceconfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

import com.littlecloud.ac.health.ThreadPoolAdapterInfo;
import com.littlecloud.ac.health.ThreadPoolManager;
import com.littlecloud.ac.health.ThreadPoolManager.ServiceType;
import com.littlecloud.ac.util.RootBranchRedirectManager;
import com.littlecloud.ac.util.RootBranchRedirectManager.SERVER_MODE;
import com.littlecloud.control.json.util.DateUtils;

public class DeviceConfigScheduler {
	public static Logger log = Logger.getLogger(DeviceConfigQueue.class);
	private static final int MAX_QUEUE_SIZE = 20000;
	private static final int PROCESSQ_INTERVAL_SECOND = 5;
	private static DeviceConfigQueue deviceConfigQueue;
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	public static ConcurrentHashMap<Long, String> threadPoolInfoMap = new ConcurrentHashMap<Long, String>();
	private static boolean isStarted = false;
	
	public static void startSchedule() {
		if (deviceConfigQueue == null){
			deviceConfigQueue = new DeviceConfigQueue(MAX_QUEUE_SIZE);		
		}
		
//        Thread thread = new Thread(deviceConfigQueue);
//        thread.start();
		log.debugf("DEVCONF20140424 - DeviceConfigQueue.startSchedule() with MAX_QUEUE_SIZE: %s", MAX_QUEUE_SIZE );
		isStarted = true;
		scheduler.scheduleAtFixedRate(deviceConfigQueue, PROCESSQ_INTERVAL_SECOND, PROCESSQ_INTERVAL_SECOND, TimeUnit.SECONDS);
	}
	
	public static DeviceConfigQueue getDeviceConfigQueue(){
		return deviceConfigQueue;
	}

	public static int getMaxQueueSize() {
		return MAX_QUEUE_SIZE;
	}
	
	public static ScheduledExecutorService getExecutor() {
		return scheduler;
	}

	public static void stopSchedule() {
		try {
			if (isStarted && !scheduler.isTerminated())
				scheduler.shutdown();//awaitTermination(1, TimeUnit.NANOSECONDS);
			isStarted = false;
		} catch (Exception e) {
			log.error("DeviceConfigScheduler.terminateSchedule by ThreadPoolManager %s",e);
			e.printStackTrace();
		}
	}
	public static String getMessageInfo() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(" PROCESSQ_INTERVAL_SECOND: ");
		sb.append(PROCESSQ_INTERVAL_SECOND);
		sb.append("<br>");
		sb.append(" MAX_QUEUE_SIZE: ");
		sb.append(MAX_QUEUE_SIZE);
		sb.append("<br>");
		sb.append(" starttime: ");
		sb.append(DateUtils.getUtcDate());
		sb.append("<br>");
		
		sb.append(" deviceConfigQueueSize: ");
		sb.append(DeviceConfigQueue.getQueueSize());
		sb.append("<br>");	
		/* Add more info if needed*/

		return sb.toString();
	}
	
	public static ThreadPoolAdapterInfo getThreadPoolAdapterInfo(){
		ThreadPoolAdapterInfo threadPoolAdapterInfo = new ThreadPoolAdapterInfo();
		threadPoolAdapterInfo.setType(ThreadPoolManager.ExecutorType.ScheduledExecutorService);
		threadPoolAdapterInfo.setName(ServiceType.DeviceConfigScheduler);
		threadPoolAdapterInfo.setExecutor_service(getExecutor());
		if (isStarted)
			threadPoolAdapterInfo.setStatus("Running");
		else
			threadPoolAdapterInfo.setStatus("Shutdown");
		
		threadPoolAdapterInfo.setThreadExecInfo(getMessageInfo());
		threadPoolAdapterInfo.setThreadPoolInfoMap(threadPoolInfoMap);
		return threadPoolAdapterInfo;
	}
}
