package com.littlecloud.control.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.peplink.api.db.DBObject;
@Entity
@Table(name = "captive_portal_activities"
		, uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
public class CaptivePortalActivity extends DBObject implements java.io.Serializable{
	public final static String ACTIVITY_TYPE_USER_LOGIN = "user_login";
	public final static String ACTIVITY_TYPE_USER_LOGOUT = "user_logout";
	public final static String ACTIVITY_TYPE_LOGIN = "login";
	public final static String ACTIVITY_TYPE_LOGOUT = "logout";
	public final static String ACTIVITY_TYPE_CONNECT = "connect";
	public final static String ACTIVITY_TYPE_DISCONNECT = "disconnect";
	public final static String ACTIVITY_TYPE_USAGE = "usage";
	
	private Integer id;
	private Integer ianaId;
	private String sn;
	private Integer cpId;
	private Integer networkId;
	private Integer deviceId;
	private String ssid;
	private String username;
	private String clientMac;
	private String bssid;
	private Integer bandwidthUsed;
	private Integer timeUsed;
	private Integer unixtime;
	private Date createdAt;
	private String activityType;
	private Integer status;
	private String statusMsg;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "iana_id", nullable = true)
	public Integer getIanaId() {
		return ianaId;
	}
	public void setIanaId(Integer ianaId) {
		this.ianaId = ianaId;
	}
	@Column(name = "sn", nullable = true)
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	@Column(name = "cp_id", nullable = false)
	public Integer getCpId() {
		return cpId;
	}
	public void setCpId(Integer cpId) {
		this.cpId = cpId;
	}
	@Column(name = "network_id", nullable = false)
	public Integer getNetworkId() {
		return networkId;
	}
	public void setNetworkId(Integer networkId) {
		this.networkId = networkId;
	}
	@Column(name = "device_id", nullable = false)
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	@Column(name = "ssid", nullable = true)
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	@Column(name = "username", nullable = true)
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Column(name = "client_mac", nullable = true)
	public String getClientMac() {
		return clientMac;
	}
	public void setClientMac(String clientMac) {
		if (clientMac != null && !clientMac.isEmpty()){
			clientMac = clientMac.replace(":", "-");
		}
		this.clientMac = clientMac;
	}
	@Column(name = "bssid", nullable = true)
	public String getBssid() {
		return bssid;
	}
	public void setBssid(String bssid) {
		if (bssid != null && !bssid.isEmpty()){
			bssid = bssid.toUpperCase();
		}
		this.bssid = bssid;
	}
	@Column(name = "bandwidth_used", nullable = true)
	public Integer getBandwidthUsed() {
		return bandwidthUsed;
	}
	public void setBandwidthUsed(Integer bandwidthUsed) {
		this.bandwidthUsed = bandwidthUsed;
	}
	@Column(name = "time_used", nullable = true)
	public Integer getTimeUsed() {
		return timeUsed;
	}
	public void setTimeUsed(Integer timeUsed) {
		this.timeUsed = timeUsed;
	}
	@Column(name = "unixtime", nullable = true)
	public Integer getUnixtime() {
		return unixtime;
	}
	public void setUnixtime(Integer unixtime) {
		this.unixtime = unixtime;
	}
	@Column(name = "created_at", nullable = true)
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	@Column(name = "activity_type", nullable = true)
	public String getActivityType() {
		return activityType;
	}
	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}
	@Column(name = "status", nullable = true)
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name = "status_msg", nullable = true)
	public String getStatusMsg() {
		return statusMsg;
	}
	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CaptivePortalActivity [id=");
		builder.append(id);
		builder.append(", ianaId=");
		builder.append(ianaId);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", cpId=");
		builder.append(cpId);
		builder.append(", networkId=");
		builder.append(networkId);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", ssid=");
		builder.append(ssid);
		builder.append(", username=");
		builder.append(username);
		builder.append(", clientMac=");
		builder.append(clientMac);
		builder.append(", bssid=");
		builder.append(bssid);
		builder.append(", bandwidthUsed=");
		builder.append(bandwidthUsed);
		builder.append(", timeUsed=");
		builder.append(timeUsed);
		builder.append(", unixtime=");
		builder.append(unixtime);
		builder.append(", createdAt=");
		builder.append(createdAt);
		builder.append(", activityType=");
		builder.append(activityType);
		builder.append(", status=");
		builder.append(status);
		builder.append(", statusMsg=");
		builder.append(statusMsg);
		builder.append("]");
		return builder.toString();
	}


}
