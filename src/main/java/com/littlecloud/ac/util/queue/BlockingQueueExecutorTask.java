package com.littlecloud.ac.util.queue;

public abstract class BlockingQueueExecutorTask <I> implements Runnable {

	public I item;

	public BlockingQueueExecutorTask(I item) {
		super();
		this.item = item;
	}
}