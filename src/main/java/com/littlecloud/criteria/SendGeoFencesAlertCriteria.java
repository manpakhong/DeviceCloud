package com.littlecloud.criteria;

import com.littlecloud.control.entity.NetworkEmailNotifications;

public class SendGeoFencesAlertCriteria {
	public static final String MSG_TYPE_EMAIL = "email";
	protected String msgType; 
	protected String orgId; 
	protected Integer devId; 
	protected Integer networkId;
	protected Integer level; // for NetworkEmailNotificationsDAO
	protected String alertType;
	
	public String getAlertType() {
		return NetworkEmailNotifications.ALERT_TYPE_GEOFENCES;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	public Integer getNetworkId() {
		return networkId;
	}
	public void setNetworkId(Integer networkId) {
		this.networkId = networkId;
	}
	public Integer getDevId() {
		return devId;
	}
	public void setDevId(Integer devId) {
		this.devId = devId;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
} // end SendGeoFencesAlertCriteria
