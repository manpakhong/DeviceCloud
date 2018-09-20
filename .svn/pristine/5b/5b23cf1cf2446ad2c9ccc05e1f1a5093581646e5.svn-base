package com.littlecloud.ac;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MaxPriorityThreadFactory implements ThreadFactory{

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = Executors.defaultThreadFactory().newThread(r);
        thread.setPriority(8);
        return thread;
	}

}
