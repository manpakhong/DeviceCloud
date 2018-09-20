package com.littlecloud.control.firmware;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

import com.littlecloud.ac.DebugManager;
import com.littlecloud.ac.health.ThreadPoolAdapterInfo;
import com.littlecloud.ac.health.ThreadPoolManager;
import com.littlecloud.ac.health.ThreadPoolManager.ServiceType;
import com.littlecloud.ac.health.ThreadPoolManager.ThreadMapMessage;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;

public class FirmwareScheduler {

	private static boolean PROD_MODE = DebugManager.isPROD_MODE();
	
	private static final Logger log = Logger.getLogger(FirmwareScheduler.class);

	private static final int UPDATEQ_INITIAL_DELAY = 0;
	private static final int UPDATEQ_INTERVAL_SECOND = 60;
	private static final int PROCESSQ_INITIAL_DELAY = UPDATEQ_INITIAL_DELAY+10;
	private static final int PROCESSQ_INTERVAL_SECOND = 10;
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	public static ConcurrentHashMap<Long, String> threadPoolInfoMap = new ConcurrentHashMap<Long, String>();
	private static boolean isStarted = false;
	public static void startFirmwareScheduler(final long endtime)
	{	
		//final long TEST_TIME = 1387188750L;
		final long TEST_TIME = 1387188790L;
		final int TEST_UPDATEQ_INTERVAL_SECOND = 10;

		final Runnable updateQueueTask = new Runnable() 
		{			
			private int TEST_UPDATEQ_ACCUM_TIME = 0;
			
			public void run() 
			{
				/* process all schedules of before latter 1 mins */
				ThreadMapMessage mapMessage = new ThreadMapMessage();
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, 1);
				Date d = cal.getTime();
				log.debugf("FirmwareScheduler threadid=%d is called for date %s", Thread.currentThread().getId(), d);
				isStarted = true;
				String message = "FirmwareQueue.updateQueue("+d.getTime()/1000+"), process all schedules of before latter 1 mins";
				
				mapMessage.start(Thread.currentThread().getId(), Thread.currentThread().isAlive(), message);
				
				threadPoolInfoMap.put(Thread.currentThread().getId(), JsonUtils.toJsonPretty(mapMessage));
				
				if (PROD_MODE)					
					FirmwareQueue.updateQueue(d.getTime()/1000);
				else
				{
					FirmwareQueue.updateQueue(TEST_TIME+TEST_UPDATEQ_INTERVAL_SECOND+TEST_UPDATEQ_ACCUM_TIME);
					TEST_UPDATEQ_ACCUM_TIME+=TEST_UPDATEQ_INTERVAL_SECOND;	// simulate 1min batch every 3s
				}
				threadPoolInfoMap.remove(Thread.currentThread().getId());
			}
		};
		
		final Runnable processQueueTask = new Runnable() 
		{
			private int TEST_PROCESSQ_ACCUM_TIME = 0;
			
			public void run() 
			{			
				/* process all schedules of before latter 1 mins */
				ThreadMapMessage mapMessage = new ThreadMapMessage();
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, 1);
				Date d = cal.getTime();
				log.debugf("FirmwareScheduler threadid=%d is called for date %s (endtime=%d)", Thread.currentThread().getId(), d, endtime);
				isStarted = true;
				String message ="FirmwareQueue.processQueue("+d.getTime()/1000+"), process all schedules of before latter 1 mins";
				
				mapMessage.start(Thread.currentThread().getId(), Thread.currentThread().isAlive(), message);
				
				threadPoolInfoMap.put(Thread.currentThread().getId(), JsonUtils.toJsonPretty(mapMessage));
				
				if (PROD_MODE)					
					FirmwareQueue.processQueue(d.getTime()/1000);
				else
				{
					FirmwareQueue.processQueue(TEST_TIME+TEST_PROCESSQ_ACCUM_TIME);
					TEST_PROCESSQ_ACCUM_TIME+=PROCESSQ_INTERVAL_SECOND;	// simulate 1min batch every 3s
				}
				
				if (FirmwareQueue.getQueue()!=null && FirmwareQueue.getQueue().size()!=0)
				{
					if (d.getTime()<endtime);
						scheduler.schedule(this, PROCESSQ_INTERVAL_SECOND, TimeUnit.SECONDS);
				}
				threadPoolInfoMap.remove(Thread.currentThread().getId());
				log.infof("FirmwareQueue size=%d", FirmwareQueue.getQueue()==null?0:FirmwareQueue.getQueue().size());
			}
		};

		updateQueueTask.run();
		scheduler.schedule(processQueueTask, PROCESSQ_INTERVAL_SECOND, TimeUnit.SECONDS);
		
//		if (PROD_MODE)
//			scheduler.scheduleAtFixedRate(updateQueueTask, UPDATEQ_INITIAL_DELAY, UPDATEQ_INTERVAL_SECOND, TimeUnit.SECONDS);
//		else
//			scheduler.scheduleAtFixedRate(updateQueueTask, UPDATEQ_INITIAL_DELAY, TEST_UPDATEQ_INTERVAL_SECOND, TimeUnit.SECONDS);
		
//		if (PROD_MODE)
//			scheduler.scheduleAtFixedRate(processQueueTask, PROCESSQ_INITIAL_DELAY, PROCESSQ_INTERVAL_SECOND, TimeUnit.SECONDS);
//		else
//			scheduler.scheduleAtFixedRate(processQueueTask, 5, PROCESSQ_INTERVAL_SECOND, TimeUnit.SECONDS);

		// scheduler.schedule(new Runnable() {
		// public void run() {
		// queueHandle.cancel(true);
		// }
		// }, 1 * 60, TimeUnit.SECONDS);
	}
	
	public static ScheduledExecutorService getExecutor() {
		return scheduler;
	}

	public static void stopSchedule() {
		try {
			if (isStarted && !scheduler.isShutdown())
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
		sb.append(" PROCESSQ_INITIAL_DELAY: ");
		sb.append(PROCESSQ_INITIAL_DELAY);
		sb.append("<br>");	
		sb.append(" UPDATEQ_INITIAL_DELAY: ");
		sb.append(UPDATEQ_INITIAL_DELAY);
		sb.append("<br>");
		sb.append(" UPDATEQ_INTERVAL_SECOND: ");
		sb.append(UPDATEQ_INTERVAL_SECOND);
		sb.append("<br>");	
		sb.append(" starttime: ");
		sb.append(DateUtils.getUtcDate());
		sb.append("<br>");
		sb.append(" FirmwareQueue: ");
		sb.append("<br>");
		sb.append(JsonUtils.toJsonPretty(FirmwareQueue.getQueue()));
		sb.append("<br>");
		
		/* Add more info if needed*/

		return sb.toString();
	}
	
	public static ThreadPoolAdapterInfo getThreadPoolAdapterInfo(){
		ThreadPoolAdapterInfo threadPoolAdapterInfo = new ThreadPoolAdapterInfo();
		threadPoolAdapterInfo.setType(ThreadPoolManager.ExecutorType.ScheduledExecutorService);
		threadPoolAdapterInfo.setName(ServiceType.FirmwareScheduler);
		threadPoolAdapterInfo.setScheduled_executor_service(getExecutor());
		if (isStarted)
			threadPoolAdapterInfo.setStatus("Running");
		else
			threadPoolAdapterInfo.setStatus("Shutdown");
		
		threadPoolAdapterInfo.setThreadExecInfo(getMessageInfo());
		threadPoolAdapterInfo.setThreadPoolInfoMap(threadPoolInfoMap);
		return threadPoolAdapterInfo;
	}
}