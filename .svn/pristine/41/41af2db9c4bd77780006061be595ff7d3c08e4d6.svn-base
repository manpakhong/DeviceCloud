package com.littlecloud.ac.util;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.pool.control.QueueCacheControl;
import com.littlecloud.pool.control.QueueControl;
import com.littlecloud.pool.utils.PropertyService;

public class ACScheduler {

	private static final Logger log = Logger.getLogger(ACScheduler.class);
	private static PropertyService<ACScheduler> ps = new PropertyService<ACScheduler>(ACScheduler.class);

	private static final int INTERVAL_SECOND = ps.getInteger("QUEUE_CONTROL_SCHEDULE_INTERVAL_SECOND");
	private static final int CACHE_INTERVAL_SECOND = ps.getInteger("QUEUECACHE_CONTROL_SCHEDULE_INTERVAL_SECOND");

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(ps.getInteger("QUEUE_CONTROL_THREADPOOL_SIZE"));
	private static final ScheduledExecutorService schedulerQueueCache = Executors.newScheduledThreadPool(ps.getInteger("QUEUECACHE_CONTROL_THREADPOOL_SIZE"));
	
	public void startPersistQueueScheduler(final String orgId)
	{
		final Runnable queueTask = new Runnable() {
			
		    private final ExecutorService executor = Executors.newSingleThreadExecutor();
		    private Future<?> lastExecution;
		    private int skipCount = 0;
		    			
		    @Override
			public void run() {
				log.info(Thread.currentThread().getId() + "¡@" + new Date() + " calling "+orgId);
				try {
					
					if (lastExecution != null && !lastExecution.isDone()) {
						skipCount++;
						log.warnf("persistQueue - orgId %s queue skips %d times", orgId, skipCount);
			            return;
			        }
					else
					{
						skipCount=0;
					}
					
			        lastExecution = executor.submit(new Runnable() {			        	
			        	@Override
			 			public void run() {
			        		QueueControl.persistQueue(orgId);
			        	}			        	
			        });
					
					
				} catch (Exception e)
				{
					log.error("persistQueue exception", e);
				}
			}
		};
				
		int rand = 1 + (int)(Math.random()*INTERVAL_SECOND);
		
		// final ScheduledFuture<?> queueHandle =		
		scheduler.scheduleAtFixedRate(queueTask, rand, INTERVAL_SECOND, TimeUnit.SECONDS);
		
		// scheduler.schedule(new Runnable() {
		// public void run() {
		// queueHandle.cancel(true);
		// }
		// }, 1 * 60, TimeUnit.SECONDS);
	}
	
	public void startPersistQueueCacheScheduler(final String orgId)
	{
		final Runnable queueTask = new Runnable() {
			
			private final ExecutorService executor = Executors.newSingleThreadExecutor();
		    private Future<?> lastExecution;
		    private int skipCount = 0;
		    			
		    @Override
			public void run() {
		    	log.info(Thread.currentThread().getId() + "¡@" + new Date() + " calling "+orgId);
				try {
					if (lastExecution != null && !lastExecution.isDone()) {
						skipCount++;
						log.infof("persistQueueCache - orgId %s queue skips %d times", orgId, skipCount);
			            return;
			        }
					else
					{
						skipCount=0;
					}
					
			        lastExecution = executor.submit(new Runnable() {			        	
			        	@Override
			 			public void run() {
			        		QueueCacheControl.persistQueueCahce(orgId);
			        	}			        	
			        });
					
					
				} catch (Exception e)
				{
					log.error("persistQueueCache exception", e);
				}
			}
		};
		
		/* add random queue execution time */
		int rand = 1 + (int)(Math.random()*CACHE_INTERVAL_SECOND);
		
		// final ScheduledFuture<?> queueHandle =
		schedulerQueueCache.scheduleAtFixedRate(queueTask, rand, CACHE_INTERVAL_SECOND, TimeUnit.SECONDS);

		// scheduler.schedule(new Runnable() {
		// public void run() {
		// queueHandle.cancel(true);
		// }
		// }, 1 * 60, TimeUnit.SECONDS);
	}
}