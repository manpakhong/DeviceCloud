package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UpdatedClientInfoObject extends PoolObject implements PoolObjectIf, Serializable
{
	/* key */
	private String sn;
	private Integer iana_id;
	
	/* value */
	private ConcurrentHashMap<String,ClientInfoObject> clientInfoMap;
	private Date lastUpdatedTime;
	
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

	public ConcurrentHashMap<String, ClientInfoObject> getClientInfoMap() {
		return clientInfoMap;
	}

	public synchronized void setClientInfoMap(
			ConcurrentHashMap<String, ClientInfoObject> clientInfoMap) {
		this.clientInfoMap = clientInfoMap;
	}

	public Date getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpdatedClientInfoObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", clientInfoMap=");
		builder.append(clientInfoMap);
		builder.append(", lastUpdatedTime=");
		builder.append(lastUpdatedTime);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

}
