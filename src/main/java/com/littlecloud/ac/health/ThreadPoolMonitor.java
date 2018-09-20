package com.littlecloud.ac.health;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

import com.littlecloud.ac.WtpMsgHandlerPool;
import com.littlecloud.ac.health.ThreadPoolAdapterInfo;
import com.littlecloud.ac.messagehandler.executor.MessageExecutorsController;
import com.littlecloud.control.json.util.JsonUtils;



public class ThreadPoolMonitor {

	private static final Logger log = Logger.getLogger(ThreadPoolMonitor.class);
	
	private static ConcurrentHashMap<Long, ThreadPoolAdapterInfo> threadExecutorInfoMap = null;
	
	private static ThreadPoolMonitor instance;
	
	public static ThreadPoolMonitor getInstance() {
		if (instance != null)
			return instance;

		try {
			instance = new ThreadPoolMonitor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}
	
	public static String getAllInfo() {

		List<ThreadPoolAdapterInfo> threadInfoList = ThreadPoolManager.getAllInfo();
		if (threadInfoList != null) {
			StringBuilder sb = new StringBuilder();
			for (ThreadPoolAdapterInfo info: threadInfoList)
				sb.append(toJson(info));
			return sb.toString();
		} else {
			log.info("ThreadPoolManager.getAllInfo() is null");
			return "ThreadPoolManager.getAllInfo() is null";
		}
	}
	
	
	public static String toJson(ThreadPoolAdapterInfo info) {
		StringBuilder sb = new StringBuilder();
		sb.append("<br>");
	
		if (info != null)
		{	
			sb.append("<font color=\"blue\">");
			sb.append("ServiceName: ");
			sb.append(info.getName());
			sb.append("</font>");
			sb.append("<br>");
			sb.append("<font color=\"red\">");
			sb.append("ExecutorType: ");
			sb.append(info.getType());
			sb.append("<br>");
			sb.append("</font>");
			
			if("Running".equalsIgnoreCase(info.getStatus()))
			{
				sb.append("<font color=\"green\">");
				sb.append("Status: ");
				sb.append(info.getStatus());
				sb.append("</font>");
			} 
			else
			{
				sb.append("Status: ");
				sb.append(info.getStatus());
			}
			sb.append("<br>");
			sb.append("=====ThreadExecInfo===== ");
			sb.append("<br>");
//			sb.append("<p style=\"font-family:Arial, sans-serif;font-size:13px;\">");
			sb.append(info.getThreadExecInfo());
			
			sb.append("ThreadPoolMap: ");
			sb.append("<br>");
			
			ConcurrentHashMap<Long, String> threadmap = info.getThreadPoolInfoMap();
			if (threadmap != null) {
				Set<Long> keys = threadmap.keySet();
				for (Long key: keys)
				{
					sb.append(threadmap.get(key));
					sb.append("<br>");
				}
			}
			
			sb.append("<br>");
		}

		return sb.toString();
	}

	public String toJson(String info) {
		return JsonUtils.toJsonPretty(info);
	}
	
	public static class PoolDump {
		private int active;
		private long complete;
		private long corepoolsize;
		private long keepalive_time;
		private long largest_poolsize;
		private long max_poolsize;
		private long poolsize;
		private int queuesize;
		private long taskcount;
		private Map<Long, String> threadmap;

		public int getActive() {
			return active;
		}

		public void setActive(int active) {
			this.active = active;
		}

		public long getComplete() {
			return complete;
		}

		public void setComplete(long complete) {
			this.complete = complete;
		}

		public long getCorepoolsize() {
			return corepoolsize;
		}

		public void setCorepoolsize(long corepoolsize) {
			this.corepoolsize = corepoolsize;
		}

		public long getKeepalive_time() {
			return keepalive_time;
		}

		public void setKeepalive_time(long keepalive_time) {
			this.keepalive_time = keepalive_time;
		}

		public long getLargest_poolsize() {
			return largest_poolsize;
		}

		public void setLargest_poolsize(long largest_poolsize) {
			this.largest_poolsize = largest_poolsize;
		}

		public long getMax_poolsize() {
			return max_poolsize;
		}

		public void setMax_poolsize(long max_poolsize) {
			this.max_poolsize = max_poolsize;
		}

		public long getPoolsize() {
			return poolsize;
		}

		public void setPoolsize(long poolsize) {
			this.poolsize = poolsize;
		}

		public int getQueuesize() {
			return queuesize;
		}

		public void setQueuesize(int queuesize) {
			this.queuesize = queuesize;
		}

		public long getTaskcount() {
			return taskcount;
		}

		public void setTaskcount(long taskcount) {
			this.taskcount = taskcount;
		}

		public Map getThreadmap() {
			return threadmap;
		}

		public void setThreadmap(Map threadmap) {
			this.threadmap = threadmap;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(" active: ");
			builder.append(active);
			builder.append("<br>");
			builder.append(" complete: ");
			builder.append(complete);
			builder.append("<br>");
			builder.append(" corepoolsize: ");
			builder.append(corepoolsize);
			builder.append("<br>");
			builder.append(" keepalive_time: ");
			builder.append(keepalive_time);
			builder.append("<br>");
			builder.append(" largest_poolsize: ");
			builder.append(largest_poolsize);
			builder.append("<br>");
			builder.append(" max_poolsize: ");
			builder.append(max_poolsize);
			builder.append("<br>");
			builder.append(" poolsize: ");
			builder.append(poolsize);
			builder.append("<br>");
			builder.append(" queuesize: ");
			builder.append(queuesize);
			builder.append("<br>");
			builder.append(" taskcount: ");
			builder.append(taskcount);
			builder.append("<br>");
			builder.append(" threadmap: ");
			builder.append(threadmap);
			builder.append("<br>");
			return builder.toString();
		}
	}


}
