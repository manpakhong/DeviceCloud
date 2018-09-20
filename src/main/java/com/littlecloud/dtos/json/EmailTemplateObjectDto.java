package com.littlecloud.dtos.json;

import java.util.List;

public class EmailTemplateObjectDto {
	public final static String EMAIL_MSG_ID_DEV_ONLINE = "dev_online";
	public final static String EMAIL_MSG_ID_DEV_OFFLINE = "dev_offline";
	public final static String EMAIL_MSG_ID_SPEED_FUSION_UP = "sf_up";
	public final static String EMAIL_MSG_ID_SPEED_FUSION_DOWN = "sf_down";
	public final static String EMAIL_MSG_ID_WAN_UP = "wan_up";
	public final static String EMAIL_MSG_ID_WAN_DOWN = "wan_down";
	
	public final static String EMAIL_MSG_TYPE_EMAIL = "email";


	/* mandatory */
	private String msgId;
	private String msgType;
	private String orgId;
	private String organization_name;
	private String network_name;

	/* optional */
	private AlertEmailContactListDto contactList;
	private List<Integer> devList;
	private String eventTime;
	private Integer duration;
	private String wanName;
	private String vpnName;
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
	public String getOrganization_name() {
		return organization_name;
	}
	public void setOrganization_name(String organization_name) {
		this.organization_name = organization_name;
	}
	public String getNetwork_name() {
		return network_name;
	}
	public void setNetwork_name(String network_name) {
		this.network_name = network_name;
	}
	public AlertEmailContactListDto getContactList() {
		return contactList;
	}
	public void setContactList(AlertEmailContactListDto contactList) {
		this.contactList = contactList;
	}
	public List<Integer> getDevList() {
		return devList;
	}
	public void setDevList(List<Integer> devList) {
		this.devList = devList;
	}
	public String getEventTime() {
		return eventTime;
	}
	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
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
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmailTemplateObjectDto [msgId=");
		builder.append(msgId);
		builder.append(", msgType=");
		builder.append(msgType);
		builder.append(", orgId=");
		builder.append(orgId);
		builder.append(", organization_name=");
		builder.append(organization_name);
		builder.append(", network_name=");
		builder.append(network_name);
		builder.append(", contactList=");
		builder.append(contactList);
		builder.append(", devList=");
		builder.append(devList);
		builder.append(", eventTime=");
		builder.append(eventTime);
		builder.append(", duration=");
		builder.append(duration);
		builder.append(", wanName=");
		builder.append(wanName);
		builder.append(", vpnName=");
		builder.append(vpnName);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
