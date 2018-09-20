package com.littlecloud.pool.object;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.littlecloud.control.json.JsonExclude;

public class PoolObject implements Serializable {

	protected Long createTime;
		
	protected boolean Refreshing = false;
	
	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public boolean isRefreshing() {
		return Refreshing;
	}

	public void setRefreshing(boolean Refreshing) {
		this.Refreshing = Refreshing;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PoolObject [createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
