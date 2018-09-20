package com.littlecloud.pool.object;

import java.io.Serializable;

public class DeviceLastLocObject implements PoolObjectIf, Serializable
{
	/* key */
	private String sn;
	private Integer iana_id;
	/*
	 * value
	 */
	// List contains only one last Location 
	LocationList lastLocation;
	boolean isStatic;
	
	public LocationList getLastLocation() 
	{
		return lastLocation;
	}
	
	public void setLastLocation(LocationList lastLocation) 
	{
		this.lastLocation = lastLocation;
	}
	
	public boolean isStatic() 
	{
		return isStatic;
	}
	
	public void setStatic(boolean isStatic) 
	{
		this.isStatic = isStatic;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
	
	public Integer getIana_id() {
		return iana_id;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}
	
	@Override
	public void setCreateTime(Long time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Long getCreateTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRefreshing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRefreshing(boolean markedRefresh) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		// TODO Auto-generated method stub
		this.iana_id = iana_id;
		this.sn = sn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceLastLocObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", lastLocation=");
		builder.append(lastLocation);
		builder.append(", isStatic=");
		builder.append(isStatic);
		builder.append("]");
		return builder.toString();
	}
	
}
