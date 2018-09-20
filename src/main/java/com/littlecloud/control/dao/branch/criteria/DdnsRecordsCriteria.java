package com.littlecloud.control.dao.branch.criteria;

import java.util.Date;

public class DdnsRecordsCriteria {
	private Date lastUpdate; 
	private String ddnsName; 
	private Integer ianaId; 
	private String sn;
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public String getDdnsName() {
		return ddnsName;
	}
	public void setDdnsName(String ddnsName) {
		this.ddnsName = ddnsName;
	}
	public Integer getIanaId() {
		return ianaId;
	}
	public void setIanaId(Integer ianaId) {
		this.ianaId = ianaId;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	
	
}
