package com.littlecloud.pool.object;

import java.io.Serializable;

public class DistributedQueueElementObject implements Serializable {

	protected Long createTime;
	protected String next; // key to next element
	protected Object obj; // actual object held by this queue element
		
	public DistributedQueueElementObject(Object obj) {
		createTime = System.currentTimeMillis();
		this.obj = obj;
		next = null;
	}

	public DistributedQueueElementObject(Object obj, String next) {
		createTime = System.currentTimeMillis();
		this.obj = obj;
		this.next = next;
	}
	
	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Object getObject() {
		return obj;
	}

	public void setObject(Object obj) {
		this.obj = obj;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	@Override
	public String toString() {
		return "DistributedQueueElementObject [createTime=" + createTime + ", next=" + next + ", obj=" + obj
				+ "]";
	}
}
