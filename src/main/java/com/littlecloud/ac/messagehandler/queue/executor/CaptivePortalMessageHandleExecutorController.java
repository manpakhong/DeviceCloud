package com.littlecloud.ac.messagehandler.queue.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import org.jboss.logging.Logger;

import com.littlecloud.ac.health.ThreadPoolAdapterInfo;
import com.littlecloud.ac.health.ThreadPoolManager;
import com.littlecloud.ac.health.ThreadPoolManager.ExecutorType;
import com.littlecloud.ac.health.ThreadPoolManager.ServiceType;
import com.littlecloud.ac.messagehandler.queue.MessageQueueFactory;
import com.littlecloud.ac.messagehandler.queue.messages.Message;
import com.littlecloud.ac.messagehandler.queue.threads.CaptivePortalMessageConsumerThread;

public class CaptivePortalMessageHandleExecutorController {
	private static final Logger log = Logger.getLogger(CaptivePortalMessageHandleExecutorController.class);
	private static CaptivePortalMessageThreadExecutor msgThreadExecutor;
	public static ConcurrentHashMap<Long, String> threadPoolInfoMap = new ConcurrentHashMap<Long, String>();
	private static boolean isRunning = false;
	private static boolean captivePortalEnabled = false; // the whole portal service will be stopped including WTP handler and ac queue
	
	private static boolean ensureInstanceExisted(){
		try{
			if (msgThreadExecutor == null){
				msgThreadExecutor = new CaptivePortalMessageThreadExecutor(MessageQueueFactory.getInstance());
			}
		} catch (Exception e){
			log.error("MSGHANDLER20140617 - MessageHandleExecutorController.ensureInstanceExisted() - ", e );
			return false;
		}
		return true;
	}
	
	
	
	public static CaptivePortalMessageThreadExecutor getMsgThreadExecutor() {
		return msgThreadExecutor;
	}

	public static ThreadPoolAdapterInfo getThreadPoolAdapterInfo(){
		ThreadPoolAdapterInfo threadPoolAdapterInfo = new ThreadPoolAdapterInfo();
		if (ensureInstanceExisted())
		{
			ThreadPoolExecutor executor = CaptivePortalMessageThreadExecutor.getExecutor();
			threadPoolAdapterInfo.setThread_pool_executor(executor);
			threadPoolAdapterInfo.setThreadExecInfo(CaptivePortalMessageThreadExecutor.getMessageInfo());
			if (executor != null) {
				if (executor.isShutdown())
					threadPoolAdapterInfo.setStatus("Shutdown");
				else if (executor.isTerminated())
					threadPoolAdapterInfo.setStatus("Terminated");
				else if (executor.isTerminating())
					threadPoolAdapterInfo.setStatus("Terminating");
				else
					threadPoolAdapterInfo.setStatus("Running");
			}
			else
				threadPoolAdapterInfo.setStatus("Shutdown");
		}
		threadPoolAdapterInfo.setType(ExecutorType.ThreadPoolExecutor);
		threadPoolAdapterInfo.setName(ServiceType.CaptivePortalMessageHandleExecutorController);
		threadPoolAdapterInfo.setThreadPoolInfoMap(threadPoolInfoMap);
		
		return threadPoolAdapterInfo;
	}

	public static boolean getRunningEnabled(){
		return CaptivePortalMessageHandleExecutorController.isRunning;
	}
	
	@SuppressWarnings("rawtypes")
	public static BlockingQueue<Message> getQueue(){
		return CaptivePortalMessageThreadExecutor.getQueue();
	}
	
	public static boolean isCaptivePortalEnabled() {
		return CaptivePortalMessageHandleExecutorController.captivePortalEnabled;
	}

	public static void setCaptivePortalEnabled(boolean captivePortalEnabled) {
		CaptivePortalMessageHandleExecutorController.captivePortalEnabled = captivePortalEnabled;
	}

	@SuppressWarnings("rawtypes")
	public static boolean enqueueMessage(Message message){
		boolean isEnqueue = false;
		try{
			if (ensureInstanceExisted()){
				if (message != null && message.getMessageContent() != null){
					CaptivePortalMessageThreadExecutor.enqueue(message);
				}
			}
		} catch (Exception e){
			log.error("MSGHANDLER20140617 - MessageHandleExecutorController.enqueueMessage() - ", e );
		}
		return isEnqueue;
	}
	
	public static void run(){
		try{
			if (ensureInstanceExisted()){
				Thread msgExecutorThread = new Thread(msgThreadExecutor);
				CaptivePortalMessageHandleExecutorController.isRunning = true;
				msgExecutorThread.start();
				threadPoolInfoMap.put(msgExecutorThread.getId(), msgExecutorThread.getName());
			} else {
				log.error("MSGHANDLER20140617 - MessageHandleExecutorController.run() - cannot run!" );
			}
		} catch (Exception e){
			log.error("MSGHANDLER20140617 - MessageHandleExecutorController.run() - ", e );
		}
	}
		
	public static boolean stop(){
		boolean isStop = false;
		if (ensureInstanceExisted()){
			msgThreadExecutor.setRunningEnabled(false);
			isStop = true;
			CaptivePortalMessageHandleExecutorController.isRunning = false;
		}
		return isStop;
	}
	
	public static boolean resume(){
		boolean isResume = false;
		if (ensureInstanceExisted()){
			msgThreadExecutor.setRunningEnabled(true);
			isResume = true;
			CaptivePortalMessageHandleExecutorController.isRunning = true;
		}
		return isResume;
	}
	
}
