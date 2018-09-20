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
@Table(name = "captive_portal_sessions"
		, uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
public class CaptivePortalSessions extends DBObject implements java.io.Serializable{
	public final static String STATUS_ACTIVE = "active";
	public final static String STATUS_INACTIVE = "inactive";
	public static final int QUOTA_TYPE_NO_QUOTA = 0;
	public static final int QUOTA_TYPE_BANDWIDTH = 1;
	public static final int QUOTA_TIME = 2;
	public static final String ACCESS_MODE_OPEN = "open";
	public static final String ACCESS_MODE_TOKEN = "token";
	public static final String ACCESS_MODE_GUEST = "guest";
	public static final String ACCESS_MODE_FB_WIFI = "fb_wifi";
	public static final String ACCESS_MODE_FB_CHECKIN = "fb_checkin";
	public static final String ACCESS_MODE_FB_SHARE = "fb_share";
	public static final String ACCESS_MODE_FB_LIKE = "fb_like";
	public static final String ACCESS_MODE_WECHAT_FOLLOW = "wechat_follow";
	
	
	private Integer id;
	private Integer ianaId;
	private String sn;
	private Integer networkId;
	private Integer ssidId;
	private String clientMac;
	private String clientIp;
	private String bssid;
	private String ssid;
	private String accessMode;
	private String userGroupId;
	private String username;
	private Integer remainTime; // in second
	private Integer remainBandwidth; // in kb
	private Date expiryDate;	
	private Date connectTime;
	private Date disconnectTime;
	private Integer cpId;
	private Integer sessionTimeOut; // in second
	private Date lastAccessTime;
	private String status;
	private Integer quotaType;
	private Date createdAt;
	private Boolean deviceReplyLogin;
	private Boolean clientLogout;
	private Date lastHouseKeepCheckTime;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "iana_id", nullable = false)
	public Integer getIanaId() {
		return ianaId;
	}
	public void setIanaId(Integer ianaId) {
		this.ianaId = ianaId;
	}
	@Column(name = "sn", nullable = false)
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	@Column(name = "network_id")
	public Integer getNetworkId() {
		return networkId;
	}
	public void setNetworkId(Integer networkId) {
		this.networkId = networkId;
	}
	@Column(name = "ssid_id")
	public Integer getSsidId() {
		return ssidId;
	}
	public void setSsidId(Integer ssidId) {
		this.ssidId = ssidId;
	}
	@Column(name = "access_mode")
	public String getAccessMode() {
		return accessMode;
	}
	public void setAccessMode(String accessMode) {
		this.accessMode = accessMode;
	}
	@Column(name = "client_mac")
	public String getClientMac() {
		return clientMac;
	}
	public void setClientMac(String clientMac) {
		if (clientMac != null && !clientMac.isEmpty()){
			clientMac = clientMac.replace(":", "-");
		}
		this.clientMac = clientMac;
	}
	@Column(name = "client_ip")
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	@Column(name = "bssid")
	public String getBssid() {
		return bssid;
	}
	public void setBssid(String bssid) {
		if (bssid != null && !bssid.isEmpty()){
			bssid = bssid.toUpperCase();
		}
		this.bssid = bssid;
	}
	@Column(name = "ssid")
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	@Column(name = "user_group_id")
	public String getUserGroupId() {
		return userGroupId;
	}
	public void setUserGroupId(String userGroupId) {
		this.userGroupId = userGroupId;
	}
	@Column(name = "username")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Column(name = "remain_time")
	public Integer getRemainTime() {
		return remainTime;
	}
	public void setRemainTime(Integer remainTime) {
		this.remainTime = remainTime;
	}
	@Column(name = "remain_bandwidth")
	public Integer getRemainBandwidth() {
		return remainBandwidth;
	}
	public void setRemainBandwidth(Integer remainBandwidth) {
		this.remainBandwidth = remainBandwidth;
	}
	@Column(name = "expiry_date")
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	@Column(name = "connect_time")
	public Date getConnectTime() {
		return connectTime;
	}
	public void setConnectTime(Date connectTime) {
		this.connectTime = connectTime;
	}
	@Column(name = "disconnect_time")
	public Date getDisconnectTime() {
		return disconnectTime;
	}
	public void setDisconnectTime(Date disconnectTime) {
		this.disconnectTime = disconnectTime;
	}
	@Column(name = "created_at")
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	@Column(name = "cp_id")
	public Integer getCpId() {
		return cpId;
	}
	public void setCpId(Integer cpId) {
		this.cpId = cpId;
	}
	@Column(name = "session_timeout")
	public Integer getSessionTimeOut() {
		return sessionTimeOut;
	}
	public void setSessionTimeOut(Integer sessionTimeOut) {
		this.sessionTimeOut = sessionTimeOut;
	}
	@Column(name = "last_access_time")
	public Date getLastAccessTime() {
		return lastAccessTime;
	}
	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}
	@Column(name = "status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Column(name = "quota_type")
	public Integer getQuotaType() {
		return quotaType;
	}
	public void setQuotaType(Integer quotaType) {
		this.quotaType = quotaType;
	}
	
	@Column(name = "device_reply_login")
	public Boolean getDeviceReplyLogin() {
		return deviceReplyLogin;
	}
	public void setDeviceReplyLogin(Boolean deviceReplyLogin) {
		this.deviceReplyLogin = deviceReplyLogin;
	}
	
	@Column(name = "client_logout")
	public Boolean getClientLogout() {
		return clientLogout;
	}
	public void setClientLogout(Boolean clientLogout) {
		this.clientLogout = clientLogout;
	}
	
	@Column(name = "last_house_keep_check_time")
	public Date getLastHouseKeepCheckTime() {
		return lastHouseKeepCheckTime;
	}
	public void setLastHouseKeepCheckTime(Date lastHouseKeepCheckTime) {
		this.lastHouseKeepCheckTime = lastHouseKeepCheckTime;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CaptivePortalSessions [id=");
		builder.append(id);
		builder.append(", ianaId=");
		builder.append(ianaId);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", networkId=");
		builder.append(networkId);
		builder.append(", ssidId=");
		builder.append(ssidId);
		builder.append(", clientMac=");
		builder.append(clientMac);
		builder.append(", clientIp=");
		builder.append(clientIp);
		builder.append(", bssid=");
		builder.append(bssid);
		builder.append(", ssid=");
		builder.append(ssid);
		builder.append(", accessMode=");
		builder.append(accessMode);
		builder.append(", username=");
		builder.append(username);
		builder.append(", remainTime=");
		builder.append(remainTime);
		builder.append(", remainBandwidth=");
		builder.append(remainBandwidth);
		builder.append(", expiryDate=");
		builder.append(expiryDate);
		builder.append(", connectTime=");
		builder.append(connectTime);
		builder.append(", disconnectTime=");
		builder.append(disconnectTime);
		builder.append(", cpId=");
		builder.append(cpId);
		builder.append(", sessionTimeOut=");
		builder.append(sessionTimeOut);
		builder.append(", lastAccessTime=");
		builder.append(lastAccessTime);
		builder.append(", status=");
		builder.append(status);
		builder.append(", quotaType=");
		builder.append(quotaType);
		builder.append(", createdAt=");
		builder.append(createdAt);
		builder.append(", deviceReplyLogin=");
		builder.append(deviceReplyLogin);
		builder.append(", clientLogout=");
		builder.append(clientLogout);
		builder.append(", lastHouseKeepCheckTime=");
		builder.append(lastHouseKeepCheckTime);
		builder.append("]");
		return builder.toString();
	}


} // end class
