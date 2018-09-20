package com.littlecloud.control.devicechange;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import org.jboss.logging.Logger;

import com.littlecloud.ac.health.ThreadPoolAdapterInfo;
import com.littlecloud.ac.health.ThreadPoolManager;
import com.littlecloud.ac.health.ThreadPoolManager.ServiceType;
import com.littlecloud.ac.util.queue.BlockingQueueExecutor;
import com.littlecloud.ac.util.queue.BlockingQueueExecutorTaskFactory;
import com.littlecloud.control.entity.branch.DevicePendingChanges;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.utils.PropertyService;

public class DeviceChangeService {
	
	private static final Logger log = Logger.getLogger(DeviceChangeService.class);
	private static final PropertyService<DeviceChangeService> ps = new PropertyService<DeviceChangeService>(DeviceChangeService.class);
	private static final boolean isServiceEnable = ps.getBoolean("SERVICE_ENABLE");
	
	private static DeviceChangeService instance = null;
	public static ConcurrentHashMap<Long, String> threadPoolInfoMap = new ConcurrentHashMap<Long, String>();
	private static BlockingQueueExecutor<DeviceChangeTask, DevicePendingChanges> executor = null;
	private static final int queueSize = 1000;
	private static final int poolSize = 5;
	private static boolean isStarted = false;
	
	private DeviceChangeService(){
		BlockingQueueExecutorTaskFactory<DeviceChangeTask, DevicePendingChanges> factory = 
				new BlockingQueueExecutorTaskFactory<DeviceChangeTask, DevicePendingChanges>(DeviceChangeTask.class, DevicePendingChanges.class);

		executor = new BlockingQueueExecutor<DeviceChangeTask, DevicePendingChanges>(queueSize, poolSize, factory);		
	};
	
	public static ThreadPoolExecutor getExecutor() {
		if (executor!=null)
			return executor.getService();
		else
			return  null;
	}

	public static ThreadPoolAdapterInfo getThreadPoolAdapterInfo(){
		ThreadPoolAdapterInfo threadPoolAdapterInfo = new ThreadPoolAdapterInfo();
		threadPoolAdapterInfo.setType(ThreadPoolManager.ExecutorType.ThreadPoolExecutor);
		threadPoolAdapterInfo.setName(ServiceType.DeviceChangeService);
		threadPoolAdapterInfo.setThread_pool_executor(getExecutor());
		if (isStarted)
			threadPoolAdapterInfo.setStatus("Running");
		else
			threadPoolAdapterInfo.setStatus("Shutdown");
		if (executor!=null)
			threadPoolAdapterInfo.setThreadExecInfo(executor.getMessageInfo());
		threadPoolAdapterInfo.setThreadPoolInfoMap(threadPoolInfoMap);
		return threadPoolAdapterInfo;
	}
	
	public static synchronized DeviceChangeService getInstance() {
		if (instance == null)
			instance = new DeviceChangeService();
		
		return instance;
	}
	
	public synchronized static void startService() {
		DeviceChangeService.getInstance().start();
	}
	
	public synchronized static void stopService() {
		DeviceChangeService.getInstance().stop();
	}
	
	public synchronized static void loadRetryQueue() {
		log.warnf("INFO DeviceChangeService loadRetryQueue START");
		if (!isServiceEnable)
		{
			log.warnf("DeviceChangeService is not enabled!! Queue not load!");
			return;
		}
		
		DeviceChangeServiceUtils.loadToQueue(DeviceChangeServiceUtils.ACTION.retry);
		log.warnf("INFO DeviceChangeService loadRetryQueue END");
	}
		
	private void start() {		
		if (!isServiceEnable)
		{
			log.warnf("DeviceChangeService is not enabled!!");
			return;
		}
		
		log.warnf("DeviceChangeService starting...");
		
		/* initial load from db */
		if (!isStarted)
		{
			isStarted = true;
			
			executor.start();
			
			DeviceChangeServiceUtils.loadToQueue(DeviceChangeServiceUtils.ACTION.all);
		}
		
		log.warnf("DeviceChangeService started.");
	}
	
	private void stop() {
		log.warnf("DeviceChangeService stopping...");
		executor.stop();
		log.warnf("DeviceChangeService stopped.");
	}

	public boolean addItem(DevicePendingChanges item) throws Exception {
		log.debugf("DeviceChangeService addItem %s", item);
		
		if (!executor.contains(item))
		{
			executor.addItem(item);
			return true;
		}
		else
		{
			log.warnf("item %d already existed in queue!", item.getId());
		}
		return false;
	}
	
	public static void main(String args[])
	{
		DeviceChangeService fm = DeviceChangeService.getInstance();
		fm.start();

		for (int i=0;i<3;i++)
		{
			DevicePendingChanges item = new DevicePendingChanges();
			try {
				item.setId(i);
				item.setSn("ABC"+i);
				fm.addItem(item);
				Thread.sleep(500);				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		fm.stop();
		log.infof("main end");
	}
}
