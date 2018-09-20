package com.littlecloud.ac.messagehandler.executor;

import java.lang.Thread.State;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.logging.Logger;

import com.littlecloud.ac.health.ThreadPoolAdapterInfo;
import com.littlecloud.ac.health.ThreadPoolManager;
import com.littlecloud.ac.health.ThreadPoolManager.ServiceType;
import com.littlecloud.ac.health.ThreadPoolManager.ThreadMapMessage;
import com.littlecloud.ac.json.model.Json_AcInfoDevice;
import com.littlecloud.ac.messagehandler.executor.impl.AcInfoDeviceMessageExecutorImpl;
import com.littlecloud.control.json.util.JsonUtils;

public class MessageExecutorsController {
	private static final Logger log = Logger.getLogger(MessageExecutorsController.class);
	public final static String INSTANCE_TYPE_AC_INFO_DEVICE_MESSAGE_EXECUTOR = "INSTANCE_TYPE_AC_INFO_DEVICE_MESSAGE_EXECUTOR";
	public static ConcurrentHashMap<Long, String> threadPoolInfoMap = new ConcurrentHashMap<Long, String>();
	private static MessageExecutor<Json_AcInfoDevice> acInfoDeviceMessageExecutor;
	private static Thread msgExecutorThread;
	private static boolean isStarted = false;
	private static void createAcInfodeviceMessageExecutorInstance(List <Json_AcInfoDevice> acInfoDeviceList){
		if (acInfoDeviceMessageExecutor == null){
			acInfoDeviceMessageExecutor = new AcInfoDeviceMessageExecutorImpl(acInfoDeviceList);
		}
		isStarted = true;
	}
	private static MessageExecutor<Json_AcInfoDevice> getCurrentAcInfodeviceMessageExecutorInstance(){
		return acInfoDeviceMessageExecutor;
	}
	
	public static void startAcInfoDeviceMessageExecutor(List <Json_AcInfoDevice> acInfoDeviceList){
		if (getCurrentAcInfodeviceMessageExecutorInstance() == null){
			createAcInfodeviceMessageExecutorInstance(acInfoDeviceList);
		}
		msgExecutorThread = new Thread(acInfoDeviceMessageExecutor);
		msgExecutorThread.start();
		isStarted = true;
		ThreadMapMessage threadMapMessage = new ThreadMapMessage();
		ThreadMapMessage message = threadMapMessage.start(msgExecutorThread.getId(), true, "acInfoDeviceList ="+acInfoDeviceList);
		threadPoolInfoMap.put(msgExecutorThread.getId(), message.toString());
	}
	public static void stopAcInfoDeviceMessageExecutor(){
		try{
			if (msgExecutorThread != null){
				msgExecutorThread.join();
				msgExecutorThread = null;
			}
			isStarted = false;
		} catch (Exception e){
			log.error("ACINFOUP20140630 - MessageExecutorsController.stopAcInfoDeviceMessageExecutor", e);
		}
	}
	
	public static ThreadPoolAdapterInfo getThreadPoolAdapterInfo() {
		ThreadPoolAdapterInfo threadPoolAdapterInfo = new ThreadPoolAdapterInfo();
		threadPoolAdapterInfo.setType(ThreadPoolManager.ExecutorType.ThreadPoolExecutor);
		threadPoolAdapterInfo.setName(ServiceType.MessageExecutorsController);
		threadPoolAdapterInfo.setThreadPoolInfoMap(threadPoolInfoMap);
		if (isStarted)
			threadPoolAdapterInfo.setStatus("Running");
		else
			threadPoolAdapterInfo.setStatus("Shutdown");
		if (MessageExecutorsController.acInfoDeviceMessageExecutor != null)
		{
			threadPoolAdapterInfo.setStatus(acInfoDeviceMessageExecutor.getStatus());
			threadPoolAdapterInfo.setThreadExecInfo(acInfoDeviceMessageExecutor.getMessageInfo());
			threadPoolAdapterInfo.setThread_pool_executor(acInfoDeviceMessageExecutor.getExecutor());			
		}
		return threadPoolAdapterInfo;
	}
}
