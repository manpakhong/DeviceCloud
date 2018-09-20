package com.littlecloud.ac.messagehandler.executor.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

import com.littlecloud.ac.DebugManager;
import com.littlecloud.ac.health.HealthMonitorHandler;
import com.littlecloud.ac.health.ThreadPoolAdapterInfo;
import com.littlecloud.ac.health.ThreadPoolManager;
import com.littlecloud.ac.health.ThreadPoolManager.ServiceType;
import com.littlecloud.ac.health.ThreadPoolMonitor.PoolDump;
import com.littlecloud.ac.json.model.Json_AcInfoDevice;
import com.littlecloud.ac.messagehandler.executor.MessageExecutor;
import com.littlecloud.ac.messagehandler.executor.MessageExecutorsController;
import com.littlecloud.ac.messagehandler.threads.MessageHandlerThread;
import com.littlecloud.ac.messagehandler.threads.impl.AcInfoDeviceMessageHandlerThreadImpl;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.utils.PropertyService;

public class AcInfoDeviceMessageExecutorImpl implements MessageExecutor <Json_AcInfoDevice>  {
	private static final Logger log = Logger.getLogger(AcInfoDeviceMessageExecutorImpl.class);
	private static ThreadPoolExecutor executor;/* Before: ExecutorService */
	private boolean runningEnabled = true;
	
	private static PropertyService<AcInfoDeviceMessageExecutorImpl> ps = new PropertyService<AcInfoDeviceMessageExecutorImpl>(AcInfoDeviceMessageExecutorImpl.class);
	private static final Integer maxThreadPool = ps.getInteger("MAX_THREAD_POOL");
	private static final Integer threadSleep = ps.getInteger("THREAD_SLEEP");
	private List<Json_AcInfoDevice> messageObjectList;
	private int currentRunIndex;
	public static ConcurrentHashMap<Long, String> threadPoolInfoMap = new ConcurrentHashMap<Long, String>();

	public AcInfoDeviceMessageExecutorImpl(List<Json_AcInfoDevice> msgObjList){
		this.messageObjectList = msgObjList;
		this.currentRunIndex = 0;
		
		if (executor == null){
			executor = new ThreadPoolExecutor(maxThreadPool, maxThreadPool,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());//Executors.newFixedThreadPool(maxThreadPool);
		}
	}
	@Override
	public ThreadPoolExecutor getExecutor() {
		return executor;
	}
	@Override
	public String getMessageInfo() {
		StringBuilder sb = new StringBuilder();
		if (executor != null) {
			PoolDump dump = new PoolDump();
			dump.setActive(executor.getActiveCount());
			dump.setComplete(executor.getCompletedTaskCount());
			dump.setCorepoolsize(executor.getCorePoolSize());
			dump.setKeepalive_time(executor.getKeepAliveTime(TimeUnit.SECONDS));
			dump.setLargest_poolsize(executor.getLargestPoolSize());
			dump.setMax_poolsize(executor.getMaximumPoolSize());
			dump.setPoolsize(executor.getPoolSize());
			dump.setQueuesize(executor.getQueue().size());
			dump.setTaskcount(executor.getTaskCount());
			sb.append(dump.toString());
		}
		
		sb.append(" maxThreadPool: ");
		sb.append(maxThreadPool);
		sb.append("<br>");		
		sb.append(" runningEnabled: ");
		sb.append(getRunningEnabled());
		sb.append("<br>");
		sb.append(" currentRunIndex: ");
		sb.append(getCurrentRunIndex());
		sb.append("<br>");
		sb.append(" starttime: ");
		sb.append(DateUtils.getUtcDate());
		sb.append("<br>");
		sb.append(" messageObjectList: ");
		sb.append(getMessageObjectList());
		sb.append("<br>");
		/* Add more info if needed*/

		return sb.toString();
	}	

	public ThreadPoolAdapterInfo getThreadPoolAdapterInfo() {
		ThreadPoolAdapterInfo threadPoolAdapterInfo = new ThreadPoolAdapterInfo();
		threadPoolAdapterInfo.setType(ThreadPoolManager.ExecutorType.ThreadPoolExecutor);
		threadPoolAdapterInfo.setName(ServiceType.MessageExecutorsController);
		threadPoolAdapterInfo.setStatus(getStatus());
		threadPoolAdapterInfo.setThreadExecInfo(getMessageInfo());
		threadPoolAdapterInfo.setThread_pool_executor(getExecutor());
		threadPoolAdapterInfo.setThreadPoolInfoMap(threadPoolInfoMap);
		
		return threadPoolAdapterInfo;
	}
	private void execute(MessageHandlerThread<Json_AcInfoDevice> msgThread){
		if (log.isDebugEnabled()) log.debug("ACINFOUP20140630 - MessageThreadExecutor.execute()");
		try{
			executor.execute(msgThread);
		} catch (Exception e){
			log.error("ACINFOUP20140630 - MessageThreadExecutor.execute() - ", e );
		}
	}
	@Override
	public void run() {
		try{
			while (runningEnabled){
				if (HealthMonitorHandler.isClusterHealthy()){
					log.debug("ACINFOUP20140630 - AcInfoDeviceMessageExecutorImpl.run() - HealthMonitorHandler.isClusterHealthy() - true");
					if (messageObjectList != null && currentRunIndex < messageObjectList.size()){
						Json_AcInfoDevice msgObj = messageObjectList.get(currentRunIndex);
						if (msgObj != null){
							MessageHandlerThread<Json_AcInfoDevice> acInfoDevMsgThread = new AcInfoDeviceMessageHandlerThreadImpl(msgObj);
							execute(acInfoDevMsgThread);
						} else {
							log.warnf("ACINFOUP20140630 - AcInfoDeviceMessageExecutorImpl.run(),currentRunIndex:%s, msgObj is null!!!:%s ",currentRunIndex, msgObj);
						}
						currentRunIndex++;
					} else {
						runningEnabled = false;
						shutdown();
						MessageExecutorsController.stopAcInfoDeviceMessageExecutor();
						log.debug("ACINFOUP20140630 - AcInfoDeviceMessageExecutorImpl.run() - MessageExecutorsController.stopAcInfoDeviceMessageExecutor()");
					}
				}
				else
				{
					if (log.isDebugEnabled()) log.debug("ACINFOUP20140630 - AcInfoDeviceMessageExecutorImpl.run() - HealthMonitorHandler.isClusterHealthy() - false");
				}
				Thread.sleep(threadSleep);
			}
		} catch (Exception e){
			log.error("ACINFOUP20140630 - MessageThreadExecutor.run() - ", e );
		}
	}
	
	public boolean getRunningEnabled() {
		return runningEnabled;
	}

	public void setRunningEnabled(boolean runningEnabled) {
		this.runningEnabled = runningEnabled;
	}
	public void shutdown(){
		if (executor != null){
			if (executor != null){
				if (!executor.isShutdown()) {
					executor.shutdown();
					try{
						if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
							if (log.isDebugEnabled()){
								log.debugf("ACINFOUP20140630 - MessageThreadExecutor.shutdown() - Some are still running after 1sec");
							}
					        executor.shutdownNow();
					        if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
								if (log.isDebugEnabled()){
									log.debugf("ACINFOUP20140630 - MessageThreadExecutor.shutdown() - Some did not end and exit anyway");
								}
					        }
					    }					
					} catch(InterruptedException e){
						executor.shutdownNow();
						log.error("ACINFOUP20140630 - MessageThreadExecutor.shutdown() - Interrupted and exit anyway", e );
					}
				}
				executor = null;
			}
		}
	}



	@Override
	public String getStatus() {
		String status = MessageExecutor.STATUS_COMPLETED;
		if (this.messageObjectList != null){
			if (this.currentRunIndex < this.messageObjectList.size()){
				status = MessageExecutor.STATUS_RUNNING;
			}
		}
		return status;
	}
	public List<Json_AcInfoDevice> getMessageObjectList() {
		return messageObjectList;
	}
	public void setMessageObjectList(List<Json_AcInfoDevice> messageObjectList) {
		this.messageObjectList = messageObjectList;
	}
	public int getCurrentRunIndex() {
		return currentRunIndex;
	}
	public void setCurrentRunIndex(int currentRunIndex) {
		this.currentRunIndex = currentRunIndex;
	}	
	
	

}
