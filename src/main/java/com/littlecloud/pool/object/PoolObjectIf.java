package com.littlecloud.pool.object;

public interface PoolObjectIf {
	
	public abstract void setCreateTime(Long time);
	
	public abstract Long getCreateTime();
	
	public boolean isRefreshing();

	public void setRefreshing(boolean markedRefresh);
	
	public abstract String getKey();
	
	public abstract void setKey(Integer iana_id, String sn);
}