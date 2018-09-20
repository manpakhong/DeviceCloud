package com.littlecloud.control.dao.criteria;

import java.util.Date;

public class CaptivePortalActivitiesCriteria {
	protected Integer id;
	protected Integer ianaId;
	protected String sn;
	protected Integer cpId;
	protected String ssid;
	protected String username;
	protected String clientMac;
	protected String bssid;
	protected Integer bandwidthUsedOverOrEqual;
	protected Integer bandwidthUsedLessThanOrEqual;
	protected Integer timeUsedOverOrEqual;
	protected Integer timeUsedLessThanOrEqual;
	protected Date createAtFrom;
	protected Date createAtTo;
	protected String activityType;
	protected Integer status;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public Integer getCpId() {
		return cpId;
	}
	public void setCpId(Integer cpId) {
		this.cpId = cpId;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getClientMac() {
		return clientMac;
	}
	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}
	public String getBssid() {
		return bssid;
	}
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public Integer getBandwidthUsedOverOrEqual() {
		return bandwidthUsedOverOrEqual;
	}
	public void setBandwidthUsedOverOrEqual(Integer bandwidthUsedOverOrEqual) {
		this.bandwidthUsedOverOrEqual = bandwidthUsedOverOrEqual;
	}
	public Integer getBandwidthUsedLessThanOrEqual() {
		return bandwidthUsedLessThanOrEqual;
	}
	public void setBandwidthUsedLessThanOrEqual(Integer bandwidthUsedLessThanOrEqual) {
		this.bandwidthUsedLessThanOrEqual = bandwidthUsedLessThanOrEqual;
	}

	public Integer getTimeUsedOverOrEqual() {
		return timeUsedOverOrEqual;
	}
	public void setTimeUsedOverOrEqual(Integer timeUsedOverOrEqual) {
		this.timeUsedOverOrEqual = timeUsedOverOrEqual;
	}
	public Integer getTimeUsedLessThanOrEqual() {
		return timeUsedLessThanOrEqual;
	}
	public void setTimeUsedLessThanOrEqual(Integer timeUsedLessThanOrEqual) {
		this.timeUsedLessThanOrEqual = timeUsedLessThanOrEqual;
	}
	public Date getCreateAtFrom() {
		return createAtFrom;
	}
	public void setCreateAtFrom(Date createAtFrom) {
		this.createAtFrom = createAtFrom;
	}
	public Date getCreateAtTo() {
		return createAtTo;
	}
	public void setCreateAtTo(Date createAtTo) {
		this.createAtTo = createAtTo;
	}
	public String getActivityType() {
		return activityType;
	}
	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CaptivePortalActivitiesCriteria [id=");
		builder.append(id);
		builder.append(", ianaId=");
		builder.append(ianaId);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", cpId=");
		builder.append(cpId);
		builder.append(", ssid=");
		builder.append(ssid);
		builder.append(", username=");
		builder.append(username);
		builder.append(", clientMac=");
		builder.append(clientMac);
		builder.append(", bssid=");
		builder.append(bssid);
		builder.append(", bandwidthUsedOverOrEqual=");
		builder.append(bandwidthUsedOverOrEqual);
		builder.append(", bandwidthUsedLessThanOrEqual=");
		builder.append(bandwidthUsedLessThanOrEqual);
		builder.append(", timeUsedOverOrEqual=");
		builder.append(timeUsedOverOrEqual);
		builder.append(", timeUsedLessThanOrEqual=");
		builder.append(timeUsedLessThanOrEqual);
		builder.append(", createAtFrom=");
		builder.append(createAtFrom);
		builder.append(", createAtTo=");
		builder.append(createAtTo);
		builder.append(", activityType=");
		builder.append(activityType);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}
}
