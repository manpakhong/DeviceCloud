package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.Date;

public class CpSessionInfoObject extends PoolObject implements PoolObjectIf, Serializable{

	public static final String STATUS_ACTIVE = "active";
	public static final String STATUS_INACTIVE = "inactive";
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
	
	/* key */
	private String client_mac;
	private String ssid;
	private String organization_id;
	private Integer network_id;
	private Integer iana_id;
	private String key;
	
	private String sn;
	private Integer ssid_id;
	private String bssid;
	private String client_ip;
	private Integer cp_id;
	private String user_group_id;
	private String username;
	private Integer remain_time;
	private Integer remain_bandwidth;
	private Date expiry_date;
	private Integer session_timeout;
	private Date connect_time;
	private Date disconnect_time;
	private Date created_at;
	private Date last_access_time;
	private String status;
	private Integer quota_type;
	private Boolean is_portal_enabled;	
	private Boolean device_reply_login;
	private Boolean client_logout;
	private Date last_house_keep_check_time;
	private String access_mode;
	
	public CpSessionInfoObject(Integer ianaId, String clientMac, String ssid, String organizationId, Integer networkId){
		setKey(ianaId, clientMac, ssid, organizationId, networkId);
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
	
	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		if (bssid != null && !bssid.isEmpty()){
			bssid = bssid.toUpperCase();
		}
		this.bssid = bssid;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getClient_mac() {
		return client_mac;
	}

	public void setClient_mac(String client_mac) {
		if (client_mac != null && !client_mac.isEmpty()){
			client_mac = client_mac.replace(":", "-");
		}
		this.client_mac = client_mac;
	}

	public String getClient_ip() {
		return client_ip;
	}

	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}

	public Integer getCp_id() {
		return cp_id;
	}

	public void setCp_id(Integer cp_id) {
		this.cp_id = cp_id;
	}

	public String getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}

	public Integer getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}

	public String getUser_group_id() {
		return user_group_id;
	}

	public void setUser_group_id(String user_group_id) {
		this.user_group_id = user_group_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getRemain_time() {
		return remain_time;
	}

	public void setRemain_time(Integer remain_time) {
		this.remain_time = remain_time;
	}

	public Date getDisconnect_time() {
		return disconnect_time;
	}

	public Integer getRemain_bandwidth() {
		return remain_bandwidth;
	}

	public void setRemain_bandwidth(Integer remain_bandwidth) {
		this.remain_bandwidth = remain_bandwidth;
	}

	public void setDisconnect_time(Date disconnect_time) {
		this.disconnect_time = disconnect_time;
	}

	public Date getExpiry_date() {
		return expiry_date;
	}

	public void setExpiry_date(Date expiry_date) {
		this.expiry_date = expiry_date;
	}

	public Integer getSession_timeout() {
		return session_timeout;
	}

	public void setSession_timeout(Integer session_timeout) {
		this.session_timeout = session_timeout;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getLast_access_time() {
		return last_access_time;
	}

	public void setLast_access_time(Date last_access_time) {
		this.last_access_time = last_access_time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getSsid_id() {
		return ssid_id;
	}

	public void setSsid_id(Integer ssid_id) {
		this.ssid_id = ssid_id;
	}
	
	public Date getConnect_time() {
		return connect_time;
	}

	public void setConnect_time(Date connect_time) {
		this.connect_time = connect_time;
	}

	public Integer getQuota_type() {
		return quota_type;
	}

	public void setQuota_type(Integer quota_type) {
		this.quota_type = quota_type;
	}

	public Boolean getDevice_reply_login() {
		return device_reply_login;
	}

	public void setDevice_reply_login(Boolean device_reply_login) {
		this.device_reply_login = device_reply_login;
	}

	public Boolean getIs_portal_enabled() {
		return is_portal_enabled;
	}

	public void setIs_portal_enabled(Boolean is_portal_enabled) {
		this.is_portal_enabled = is_portal_enabled;
	}

	
	public Boolean getClient_logout() {
		return client_logout;
	}

	public void setClient_logout(Boolean client_logout) {
		this.client_logout = client_logout;
	}

	
	
	public String getAccess_mode() {
		return access_mode;
	}

	public void setAccess_mode(String access_mode) {
		this.access_mode = access_mode;
	}

	public Date getLast_house_keep_check_time() {
		return last_house_keep_check_time;
	}

	public void setLast_house_keep_check_time(Date last_house_keep_check_time) {
		this.last_house_keep_check_time = last_house_keep_check_time;
	}

	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "key_pk" + this.key + "|" + getIana_id();
	}


	public void setKey(Integer ianaId, String clientMac, String ssid, String organizationId, Integer networkId) {
		this.iana_id = ianaId;
		this.client_mac = clientMac;
		this.ssid = ssid;
		this.organization_id = organizationId;
		this.network_id = networkId;
		
		StringBuilder sb = new StringBuilder();
		sb.append(this.client_mac);
		sb.append(this.ssid);
		sb.append(this.organization_id);
		sb.append(this.network_id);
		
		setKey(ianaId, sb.toString());
	}

	@Override
	public void setKey(Integer iana_id, String key) {		
		this.key = key;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CpSessionInfoObject [client_mac=");
		builder.append(client_mac);
		builder.append(", ssid=");
		builder.append(ssid);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", key=");
		builder.append(key);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", ssid_id=");
		builder.append(ssid_id);
		builder.append(", bssid=");
		builder.append(bssid);
		builder.append(", client_ip=");
		builder.append(client_ip);
		builder.append(", cp_id=");
		builder.append(cp_id);
		builder.append(", username=");
		builder.append(username);
		builder.append(", remain_time=");
		builder.append(remain_time);
		builder.append(", remain_bandwidth=");
		builder.append(remain_bandwidth);
		builder.append(", expiry_date=");
		builder.append(expiry_date);
		builder.append(", session_timeout=");
		builder.append(session_timeout);
		builder.append(", connect_time=");
		builder.append(connect_time);
		builder.append(", disconnect_time=");
		builder.append(disconnect_time);
		builder.append(", created_at=");
		builder.append(created_at);
		builder.append(", last_access_time=");
		builder.append(last_access_time);
		builder.append(", status=");
		builder.append(status);
		builder.append(", quota_type=");
		builder.append(quota_type);
		builder.append(", is_portal_enabled=");
		builder.append(is_portal_enabled);
		builder.append(", device_reply_login=");
		builder.append(device_reply_login);
		builder.append(", client_logout=");
		builder.append(client_logout);
		builder.append(", last_house_keep_check_time=");
		builder.append(last_house_keep_check_time);
		builder.append(", access_mode=");
		builder.append(access_mode);
		builder.append("]");
		return builder.toString();
	}


}
