package com.littlecloud.criteria;

public class GetGeoFencesTemplateCriteria {
	
	protected String msgId; 
	protected String msgType; 
	protected String organizationId;
	protected String organizationName;
	protected Integer priority;
	protected String senderEmail;
	protected String senderName;
	protected String recipients;
	
	// -- data
	protected String serviceName;
	protected String deviceName; 
	protected String zoneName; 
	protected String networkName; 
	protected String datetime; 	
	protected Float speedLimit; 
	protected String speedUnit;	
	protected Float currentSpeed;
	protected String address;
	protected Float distance;
	protected String distanceUnit;
	protected String duration;
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
	public String getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public String getSenderEmail() {
		return senderEmail;
	}
	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getRecipients() {
		return recipients;
	}
	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public Float getSpeedLimit() {
		return speedLimit;
	}
	public void setSpeedLimit(Float speedLimit) {
		this.speedLimit = speedLimit;
	}
	public String getSpeedUnit() {
		return speedUnit;
	}
	public void setSpeedUnit(String speedUnit) {
		this.speedUnit = speedUnit;
	}
	public Float getCurrentSpeed() {
		return currentSpeed;
	}
	public void setCurrentSpeed(Float currentSpeed) {
		this.currentSpeed = currentSpeed;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Float getDistance() {
		return distance;
	}
	public void setDistance(Float distance) {
		this.distance = distance;
	}
	public String getDistanceUnit() {
		return distanceUnit;
	}
	public void setDistanceUnit(String distanceUnit) {
		this.distanceUnit = distanceUnit;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	@Override
	public String toString() {
		return "GetGeoFencesTemplateCriteria [msgId=" + msgId + ", msgType="
				+ msgType + ", organizationId=" + organizationId
				+ ", organizationName=" + organizationName + ", priority="
				+ priority + ", senderEmail=" + senderEmail + ", senderName="
				+ senderName + ", recipients=" + recipients + ", serviceName="
				+ serviceName + ", deviceName=" + deviceName + ", zoneName="
				+ zoneName + ", networkName=" + networkName + ", datetime="
				+ datetime + ", speedLimit=" + speedLimit + ", speedUnit="
				+ speedUnit + ", currentSpeed=" + currentSpeed + ", address="
				+ address + ", distance=" + distance + ", distanceUnit="
				+ distanceUnit + ", duration=" + duration + "]";
	}
}
