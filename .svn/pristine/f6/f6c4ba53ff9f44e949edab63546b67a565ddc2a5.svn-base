package com.littlecloud.ac.messagehandler.queue.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.littlecloud.ac.health.ThreadPoolAdapterInfo;
import com.littlecloud.ac.health.ThreadPoolMonitor.PoolDump;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.ac.messagehandler.RedirectWtpMessageHandler;
import com.littlecloud.ac.messagehandler.queue.messages.Message;
import com.littlecloud.ac.messagehandler.queue.threads.CaptivePortalMessageConsumerThread;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.utils.PropertyService;

public class CaptivePortalMessageThreadExecutor implements Runnable {
	private static final Logger log = Logger.getLogger(CaptivePortalMessageThreadExecutor.class);
	private static ThreadPoolExecutor executor;/* Before is: ExecutorService */
	private boolean runningEnabled = true;
	private static BlockingQueue<Message> q;
	
	private static PropertyService<CaptivePortalMessageThreadExecutor> ps = new PropertyService<CaptivePortalMessageThreadExecutor>(CaptivePortalMessageThreadExecutor.class);
	private static final Integer maxThreadPool = ps.getInteger("MAX_THREAD_POOL");
	private static final Integer threadSleep = ps.getInteger("THREAD_SLEEP");
	
	
	public static ThreadPoolExecutor getExecutor() {
		return executor;
	}

	public static String getMessageInfo() {
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
		sb.append(" starttime: ");
		sb.append(DateUtils.getUtcDate());
		sb.append("<br>");
		sb.append(" BlockingQueue: ");
		sb.append(q);
		sb.append("<br>");
		/* Add more info if needed*/

		return sb.toString();
	}
	
	public CaptivePortalMessageThreadExecutor(BlockingQueue<Message> q){
		CaptivePortalMessageThreadExecutor.q = q;
		if (executor == null){
			executor = new ThreadPoolExecutor(maxThreadPool, maxThreadPool,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
					//Executors.newFixedThreadPool(maxThreadPool);
		}
	}
	
	public static BlockingQueue<Message> getQueue(){
		return CaptivePortalMessageThreadExecutor.q;
	}
	
	public static boolean enqueue(Message message){
		boolean isAdded= false;
		try{
			CaptivePortalMessageThreadExecutor.q.put(message);
		} catch (Exception e){
			log.error("MSGHANDLER20140617 - MessageThreadExecutor.isEnqueue() - ", e );
		}
		return isAdded;
	}
	
	private void execute(CaptivePortalMessageConsumerThread msgConsumerThread){
		try{
			executor.execute(msgConsumerThread);
		} catch (Exception e){
			log.error("MSGHANDLER20140617 - MessageThreadExecutor.execute() - ", e );
		}
	}
	@Override
	public void run() {
		try{
			while (runningEnabled){
				if (CaptivePortalMessageThreadExecutor.q != null && CaptivePortalMessageThreadExecutor.q.size() > 0){
					CaptivePortalMessageConsumerThread consumerThread = new CaptivePortalMessageConsumerThread(CaptivePortalMessageThreadExecutor.q);
					execute(consumerThread);
				}
				if (log.isDebugEnabled()){
					log.debugf("MSGHANDLER20140617 - MessageThreadExecutor.run(), MessageThreadExecutor.q: %s", CaptivePortalMessageThreadExecutor.q);					
				}
				Thread.sleep(threadSleep);
			}
		} catch (Exception e){
			log.error("MSGHANDLER20140617 - MessageThreadExecutor.run() - ", e );
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
								log.debugf("MSGHANDLER20140617 - MessageThreadExecutor.shutdown() - Some are still running after 1sec");
							}
					        executor.shutdownNow();
					        if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
					        	if (log.isDebugEnabled()){
					        		log.debugf("MSGHANDLER20140617 - MessageThreadExecutor.shutdown() - Some did not end and exit anyway");
					        	}
					        }
					    }					
					} catch(InterruptedException e){
						executor.shutdownNow();
						log.error("MSGHANDLER20140617 - MessageThreadExecutor.shutdown() - Interrupted and exit anyway", e );
					}
				}
				executor = null;
			}
		}
	}

}