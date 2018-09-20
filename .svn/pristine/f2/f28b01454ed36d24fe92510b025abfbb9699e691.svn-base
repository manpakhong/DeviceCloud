package com.littlecloud.ac.messagehandler.executor;

import java.util.concurrent.ThreadPoolExecutor;


public interface MessageExecutor <T> extends Runnable {
	public static final String STATUS_COMPLETED = "completed";
	public static final String STATUS_RUNNING = "running";
	
	public void run();
	public boolean getRunningEnabled();
	public void setRunningEnabled(boolean runningEnabled);
	public void shutdown();
	public String getStatus();
	public String getMessageInfo();
	public ThreadPoolExecutor getExecutor();
}
