package com.littlecloud.criteria;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.littlecloud.control.entity.NetworkEmailNotifications;

public class SendImmediateAlertCriteria {
	public final static String ALERT_TYPE_SPEEDFUSION = "speedfusion";
	public final static String ALERT_TYPE_WAN = "wan";
	public final static String ALERT_TYPE_DEVICE = "device";
	
	public final static String MSG_ID_DEV_ONLINE = "dev_online";
	public final static String MSG_ID_DEV_OFFLINE = "dev_offline";
	public final static String MSG_ID_SPEED_FUSION_UP = "sf_up";
	public final static String MSG_ID_SPEED_FUSION_DOWN = "sf_down";
	public final static String MSG_ID_WAN_UP = "wan_up";
	public final static String MSG_ID_WAN_DOWN = "wan_down";
	
	public final static String EMAIL_MSG_TYPE_EMAIL = "email";

	private String msgId; 
	private String msgType; 
	private String orgId; 
	private List<Integer> devIdListOnTheSameLevelSameNetwork;
	private Integer level; 
	private Integer duration; 
	private String wanName; 
	private String vpnName; 
	private Date datetime; 
	private String alertType;
	private NetworkEmailNotifications networkEmailNotifications; // optional to supply, if not given, the method will query it by itself

	public SendImmediateAlertCriteria(){
		devIdListOnTheSameLevelSameNetwork = new ArrayList<Integer>();
	}
	
	public NetworkEmailNotifications getNetworkEmailNotifications() {
		return networkEmailNotifications;
	}
	// optional to supply, if not given, the method will query it by itself
	public void setNetworkEmailNotifications(
			NetworkEmailNotifications networkEmailNotifications) {
		this.networkEmailNotifications = networkEmailNotifications;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
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
	// use as a multiple device ids
	public List<Integer> getDevIdList() {
		return this.devIdListOnTheSameLevelSameNetwork;
	}
	public void addDevId(Integer devId) {
		this.devIdListOnTheSameLevelSameNetwork.add(devId);
	}
	// use as a single device id
	public Integer getDevId(){
		if (this.devIdListOnTheSameLevelSameNetwork.size() == 1){
			return this.devIdListOnTheSameLevelSameNetwork.get(0);
		} else {
			return null;
		}
	}
	public void setDevId(Integer devId){
		if (this.devIdListOnTheSameLevelSameNetwork.size() == 1){
			this.devIdListOnTheSameLevelSameNetwork.set(0, devId);
		} else if (this.devIdListOnTheSameLevelSameNetwork.size() == 0){
			this.devIdListOnTheSameLevelSameNetwork.add(devId);
		}
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public String getWanName() {
		return wanName;
	}
	public void setWanName(String wanName) {
		this.wanName = wanName;
	}
	public String getVpnName() {
		return vpnName;
	}
	public void setVpnName(String vpnName) {
		this.vpnName = vpnName;
	}
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	
	public String getAlertType() {
		return alertType;
	}
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	public void setDevIdList(List<Integer> devIdListOnTheSameLevelSameNetwork) {
		this.devIdListOnTheSameLevelSameNetwork = devIdListOnTheSameLevelSameNetwork;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SendImmediateAlertCriteria [msgId=");
		builder.append(msgId);
		builder.append(", msgType=");
		builder.append(msgType);
		builder.append(", orgId=");
		builder.append(orgId);
		builder.append(", devIdListOnTheSameLevelSameNetwork=");
		builder.append(devIdListOnTheSameLevelSameNetwork);
		builder.append(", level=");
		builder.append(level);
		builder.append(", duration=");
		builder.append(duration);
		builder.append(", wanName=");
		builder.append(wanName);
		builder.append(", vpnName=");
		builder.append(vpnName);
		builder.append(", datetime=");
		builder.append(datetime);
		builder.append(", alertType=");
		builder.append(alertType);
		builder.append(", networkEmailNotifications=");
		builder.append(networkEmailNotifications);
		builder.append("]");
		return builder.toString();
	}


}
