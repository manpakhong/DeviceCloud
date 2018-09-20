package com.littlecloud.ac.health;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import com.littlecloud.ac.health.ThreadPoolManager.ExecutorType;
import com.littlecloud.ac.health.ThreadPoolManager.ServiceType;

public class ThreadPoolAdapterInfo {
	
	private ExecutorType type;
	private ServiceType name;
	private String status;/* "isShutDown", "running" */
	private ExecutorService executor_service;
	private ScheduledExecutorService scheduled_executor_service;
	private ThreadPoolExecutor thread_pool_executor;
	
	
	private String threadExecInfo;
	private ConcurrentHashMap<Long, String> threadPoolInfoMap;
		
	public ExecutorType getType() {
		return type;
	}
	public void setType(ExecutorType type) {
		this.type = type;
	}
	
	public ServiceType getName() {
		return name;
	}
	public void setName(ServiceType name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ExecutorService getExecutor_service() {
		return executor_service;
	}
	public void setExecutor_service(ExecutorService executor_service) {
		this.executor_service = executor_service;
	}
	public ScheduledExecutorService getScheduled_executor_service() {
		return scheduled_executor_service;
	}
	public void setScheduled_executor_service(
			ScheduledExecutorService scheduled_executor_service) {
		this.scheduled_executor_service = scheduled_executor_service;
	}
	public ThreadPoolExecutor getThread_pool_executor() {
		return thread_pool_executor;
	}
	public void setThread_pool_executor(ThreadPoolExecutor thread_pool_executor) {
		this.thread_pool_executor = thread_pool_executor;
	}
	public ConcurrentHashMap<Long, String> getThreadPoolInfoMap() {
		return threadPoolInfoMap;
	}
	public void setThreadPoolInfoMap(
			ConcurrentHashMap<Long, String> threadPoolInfoMap) {
		this.threadPoolInfoMap = threadPoolInfoMap;
	}
	public String getThreadExecInfo() {
		return threadExecInfo;
	}
	public void setThreadExecInfo(String threadExecInfo) {
		this.threadExecInfo = threadExecInfo;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ThreadPoolAdapterInfo [type=");
		builder.append(type);
		builder.append(", status=");
		builder.append(status);
		
		builder.append(", threadPoolInfoMap=");
		builder.append(threadPoolInfoMap);
		builder.append(", threadExecInfo=");
		builder.append(threadExecInfo);
		builder.append("]");
		return builder.toString();
	}
	
}