package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.littlecloud.ac.ACService;

public class PersistReportsObject  extends PoolObject implements PoolObjectIf, Serializable {

	/* key */
	public String sn = "PersistReportsObject"; // defaults
	public Integer iana_id = 9998; // defaults
	private Long thread_id;
	private String ac_id = ACService.getServerName();
	private Long lastUpdateTime;
	private Long endTime;
	private ArrayList<String> statusList = new ArrayList<String>();
	
	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.iana_id = iana_id;
		this.sn = sn;
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

	public String getAc_id() {
		return ac_id;
	}

	public void setAc_id(String ac_id) {
		this.ac_id = ac_id;
	}

	public Long getThread_id() {
		return thread_id;
	}

	public void setThread_id(Long thread_id) {
		this.thread_id = thread_id;
	}

	public ArrayList<String> getStatusList() {
		return statusList;
	}

	public void setStatusList(ArrayList<String> statusList) {
		this.statusList = statusList;
	}

	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PersistReportsObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", ac_id=");
		builder.append(ac_id);
		builder.append(", thread_id=");
		builder.append(thread_id);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", lastUpdateTime=");
		builder.append(lastUpdateTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", statusList=");
		builder.append(statusList);
		builder.append("]");
		return builder.toString();
	}
}
