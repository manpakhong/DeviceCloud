package com.littlecloud.ac;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.littlecloud.ac.health.ThreadPoolAdapterInfo;
import com.littlecloud.ac.health.ThreadPoolManager;
import com.littlecloud.ac.health.ThreadPoolMonitor.PoolDump;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.ac.root.WtpMsgHandlerRoot;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.utils.PropertyService;

public class WtpMsgHandlerPool {
	private static final Logger log = Logger.getLogger(WtpMsgHandlerPool.class);

	private static final PropertyService<WtpMsgHandlerPool> ps = new PropertyService<WtpMsgHandlerPool>(
			WtpMsgHandlerPool.class);
	private static final int POOL_GENERAL_MESSAGE_PER_SECOND = ps.getInteger("POOL_GENERAL_MESSAGE_PER_SECOND");
	private static final int POOL_MAX_MESSAGE_PER_SECOND = ps.getInteger("POOL_GENERAL_MESSAGE_PER_SECOND");
	private static final int POOL_MESSAGE_WAIT_TIMEOUT = ps.getInteger("POOL_MESSAGE_WAIT_TIMEOUT");
	private static final int POOL_MESSAGE_QUEUE = ps.getInteger("POOL_MESSAGE_QUEUE");
	public static ConcurrentHashMap<Long, String> threadExecInfoMap = new ConcurrentHashMap<Long, String>();	

	//private static WtpThreadFactory wtpFactory = null;
	private static WtpMsgHandlerPool instance = null;
	private static BlockingQueue<Runnable> wtpQueue = null;
	private static ThreadPoolExecutor wtpE = null;//

	public static WtpMsgHandlerPool getInstance() {
		if (instance != null)
			return instance;

		try {
			instance = new WtpMsgHandlerPool();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}

	private WtpMsgHandlerPool() throws Exception {
		log.infof("WtpMsgHandlerPool() is initializing");

		if (POOL_MAX_MESSAGE_PER_SECOND == 0 || POOL_MAX_MESSAGE_PER_SECOND == 0 || POOL_MESSAGE_WAIT_TIMEOUT == 0
				|| POOL_MESSAGE_QUEUE == 0) {
			String err = "WtpMsgHandlerPool properties are not found!!";
			log.error(err);
			throw new Exception(err);
		}

		//wtpFactory = new WtpThreadFactory();
		wtpQueue = new ArrayBlockingQueue<Runnable>(POOL_MESSAGE_QUEUE);
		wtpE = new ThreadPoolExecutor(POOL_GENERAL_MESSAGE_PER_SECOND, POOL_MAX_MESSAGE_PER_SECOND,
				(long) POOL_MESSAGE_WAIT_TIMEOUT, TimeUnit.SECONDS, wtpQueue);	//, wtpFactory
		wtpE.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				String msgJson = ((WtpMsgHandler) r).getJson();
				Gson gson = new Gson();
				QueryInfo<Object> info = null;
				try {
					info = gson.fromJson(msgJson, QueryInfo.class);
					if (info!=null) {
						MessageType mt = info.getType();
						if(mt == MessageType.PIPE_INFO_TYPE_DEV_LOCATIONS) {
							WtpMsgHandler.reQueue(msgJson);
							if(log.isInfoEnabled())
								log.infof("WtpMsgHandlerPool exhausted - message %s is requeued.", ((WtpMsgHandler) r).getMessageInfo());
						} else 
							log.warnf("WtpMsgHandlerPool exhausted - message %s is discarded.",	((WtpMsgHandler) r).getMessageInfo());
					}
				} catch (Exception e) {
					log.error("WtpMsgHandlerPool exhausted, e="+e, e);
				}

			}
		});
		
//		long tid = Thread.currentThread().getId();
//		ExecutorAdapterInfo info = new ExecutorAdapterInfo();
//		info.setThread_pool_executor(wtpE);
//		info.setStatus("running");
//		info.setType(ExecutorType.ThreadPoolExecutor);
//		info.setThreadExecInfo(dumpPool());
//		ThreadPoolManager.put(tid, info);
		
		log.infof("WtpMsgHandlerPool() init completed. ThreadPoolManager.put() completed.");
	}

	public void execute(WtpMsgHandler r) {
		wtpE.execute(r);
	}
	
	public void execute(WtpMsgHandlerRoot r) {
		wtpE.execute(r);
	}
	
	public void startThread(Long tid, String info)
	{
		if(log.isDebugEnabled())
			log.debugf("tid=%d info=%s", tid, info);
		threadExecInfoMap.put(tid, info);
	}
	
	public void endThread(Long tid)
	{
		threadExecInfoMap.remove(tid);
	}

	public String dumpPool() {
		PoolDump dump = new PoolDump();
		dump.setActive(wtpE.getActiveCount());
		dump.setComplete(wtpE.getCompletedTaskCount());
		dump.setCorepoolsize(wtpE.getCorePoolSize());
		dump.setKeepalive_time(wtpE.getKeepAliveTime(TimeUnit.SECONDS));
		dump.setLargest_poolsize(wtpE.getLargestPoolSize());
		dump.setMax_poolsize(wtpE.getMaximumPoolSize());
		dump.setPoolsize(wtpE.getPoolSize());
		dump.setQueuesize(wtpE.getQueue().size());
		dump.setTaskcount(wtpE.getTaskCount());
		dump.setThreadmap(threadExecInfoMap);

		return JsonUtils.toJsonPretty(dump);
	}

	public ThreadPoolAdapterInfo getThreadPoolAdapterInfo() {
		ThreadPoolAdapterInfo execinfo = new ThreadPoolAdapterInfo();

		execinfo.setType(ThreadPoolManager.ExecutorType.ThreadPoolExecutor);
		execinfo.setName(ThreadPoolManager.ServiceType.WtpMsgHandlerPool);
		execinfo.setThreadPoolInfoMap(threadExecInfoMap);
		if (wtpE != null) {
			execinfo.setThread_pool_executor(wtpE);
			PoolDump dump = new PoolDump();
			dump.setActive(wtpE.getActiveCount());
			dump.setComplete(wtpE.getCompletedTaskCount());
			dump.setCorepoolsize(wtpE.getCorePoolSize());
			dump.setKeepalive_time(wtpE.getKeepAliveTime(TimeUnit.SECONDS));
			dump.setLargest_poolsize(wtpE.getLargestPoolSize());
			dump.setMax_poolsize(wtpE.getMaximumPoolSize());
			dump.setPoolsize(wtpE.getPoolSize());
			dump.setQueuesize(wtpE.getQueue().size());
			dump.setTaskcount(wtpE.getTaskCount());
			execinfo.setThreadExecInfo(dump.toString());

			if (wtpE.isShutdown())
				execinfo.setStatus("Shutdown");
			else if (wtpE.isTerminated())
				execinfo.setStatus("Terminated");
			else if (wtpE.isTerminating())
				execinfo.setStatus("Terminating");
			else
				execinfo.setStatus("Running");
		}
		return execinfo;
	}
	
//	private class WtpThreadFactory implements ThreadFactory {
//		public Thread newThread(Runnable r) {
//			Thread t = new Thread(r);
//			((WtpMsgHandler) r).setThreadid(t.getId());
//			threadExecInfoMap.put(t.getId(), ((WtpMsgHandler) r).getMessageInfo());
//			return t;
//		}
//	}

}
