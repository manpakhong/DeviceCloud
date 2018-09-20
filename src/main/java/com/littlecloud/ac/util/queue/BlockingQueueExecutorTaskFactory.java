package com.littlecloud.ac.util.queue;

import java.lang.reflect.Constructor;

public class BlockingQueueExecutorTaskFactory<T extends BlockingQueueExecutorTask<I>, I> {

	private Class<T> taskCls;
	private Class<I> itemCls;

	public T getBlockingQueueExecutorTask(I item) throws Exception {
		Constructor<T> con = taskCls.getConstructor(itemCls);
		T task = (T) con.newInstance(item);
		return task;
	}

	public BlockingQueueExecutorTaskFactory(Class<T> factoryCls, Class<I> itemCls) {
		super();
		this.taskCls = factoryCls;
		this.itemCls = itemCls;
	}

	public Class<T> getFactoryCls() {
		return taskCls;
	}

	public void setFactoryCls(Class<T> factoryCls) {
		this.taskCls = factoryCls;
	}

	public Class<I> getItemCls() {
		return itemCls;
	}

	public void setItemCls(Class<I> itemCls) {
		this.itemCls = itemCls;
	}

}