package com.littlecloud.pool;

import java.util.concurrent.Future;

import com.littlecloud.pool.Cluster.CACHE_ACTION;

public class ClusterOption {
	private Integer expireInSeconds;
	private Integer retry;
	private CACHE_ACTION action;

	private Future ret_futureAsync;

	public Integer getExpireInSeconds() {
		return expireInSeconds;
	}

	public void setExpireInSeconds(Integer expireInSeconds) {
		this.expireInSeconds = expireInSeconds;
	}

	public Integer getRetry() {
		return retry;
	}

	public void setRetry(Integer retry) {
		this.retry = retry;
	}

	public CACHE_ACTION getAction() {
		return action;
	}

	public void setAction(CACHE_ACTION action) {
		this.action = action;
	}

	public Future getRet_futureAsync() {
		return ret_futureAsync;
	}

	public void setRet_futureAsync(Future ret_futureAsync) {
		this.ret_futureAsync = ret_futureAsync;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClusterOption [expireInSeconds=");
		builder.append(expireInSeconds);
		builder.append(", retry=");
		builder.append(retry);
		builder.append(", action=");
		builder.append(action);
		builder.append(", ret_futureAsync=");
		builder.append(ret_futureAsync);
		builder.append("]");
		return builder.toString();
	}
}
