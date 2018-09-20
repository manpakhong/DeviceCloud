package com.littlecloud.ac.util.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

import com.littlecloud.ac.health.ThreadPoolMonitor.PoolDump;
import com.littlecloud.control.json.util.DateUtils;

public class BlockingQueueExecutor<C extends BlockingQueueExecutorTask<T>, T> {

	private static final Logger log = Logger.getLogger(BlockingQueueExecutor.class);

	private BlockingQueue<T> queue = null;
	private ThreadPoolExecutor service = null;/* Before: ExecutorService */
	private BlockingQueueExecutorTaskFactory<C, T> taskFactory = null;
	private Thread mainThread = null;

	public BlockingQueueExecutor(int queueSize, int poolSize, BlockingQueueExecutorTaskFactory<C, T> taskFactory) {
		super();
		this.service = new ThreadPoolExecutor(poolSize, poolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());//Executors.newFixedThreadPool(poolSize);
		this.queue = new LinkedBlockingQueue<T>(queueSize);
		this.taskFactory = taskFactory;
	}

	public void start() {
		log.warn("BlockingQueueExecutor start() ");

		Runnable queueJob = new Runnable() {
			public void run() {
				T item = null;
				try {
					log.info("mainThread start");
					while (!Thread.currentThread().isInterrupted()) {
						log.debugf("looping queue size %d", queue.size());
						
							item = queue.take();
							//item = queue.poll(2, TimeUnit.SECONDS);
							log.debugf("item take() %s", item);
	
							try {
								Runnable r;
								r = taskFactory.getBlockingQueueExecutorTask(item);
								// service.submit(r);
								service.execute(r);
							} catch (Exception e) {
								log.error("Exception on Task ", e);
							}
					}
					
				} catch (InterruptedException e) {
					log.warn("mainThread is interrupted");
				}
					
				log.warn("mainThread end");

			}
		};

		mainThread = new Thread(queueJob);
		mainThread.start();

		log.warn("BlockingQueueExecutor start() exit!!");
	}

	public void stop() {
		try {
			mainThread.interrupt();
			service.shutdownNow();
			while(!service.isTerminated())
			{
				log.infof("terminating...");				
				service.awaitTermination(3, TimeUnit.SECONDS);				
			}
		} catch (InterruptedException e) {
			log.error(e);
		}
	}

	public ThreadPoolExecutor getService() {
		return service;
	}

	public String getMessageInfo() {
		StringBuilder sb = new StringBuilder();
		ThreadPoolExecutor executor = getService();
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
			
		sb.append(" mainThreadId: ");
		sb.append(mainThread.getId());
		sb.append("<br>");
		sb.append(" isAlive: ");
		sb.append(mainThread.isAlive());
		sb.append("<br>");
		sb.append(" activeCount: ");
		sb.append(mainThread.activeCount());
		sb.append("<br>");
		sb.append(" starttime: ");
		sb.append(DateUtils.getUtcDate());
		sb.append("<br>");
		
		sb.append(" queue: ");
		sb.append(queue);
		sb.append("<br>");	
		/* Add more info if needed*/

		return sb.toString();
	}
	public void addItem(T item) throws Exception {
		queue.put(item);
	}
	
	public boolean contains(T item) {
		return queue.contains(item);
	}
}
