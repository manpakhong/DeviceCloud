package com.littlecloud.control.json.request;

import java.util.Date;

import com.littlecloud.control.json.JsonRequest;

public class JsonCaptivePortalRequest extends JsonRequest {
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
	
	private String sn;
	private Integer iana_id;
	private Integer device_id;
	private String bssid;
	private String ssid;
	private Integer ssid_id;
	private String client_mac;
	private String client_ip;
	private Integer cp_id;
	private String organization_id;
	private Integer network_id;
	private String user_group_Id;
	private String username;
	private Integer remain_time;
	private Integer remain_bandwidth;
	private Date expiry_date;
	private Integer session_timeout;
	private Date connect_time;
	private Integer quota_type;
	private Date disconnect_time;
	private Date created_at;
	private Date last_access_time;
	private String status;
	private Date last_house_keep_check_time;
	private String access_mode;
	
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

	
	
	public Integer getSsid_id() {
		return ssid_id;
	}

	public void setSsid_id(Integer ssid_id) {
		this.ssid_id = ssid_id;
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

	public String getUser_group_Id() {
		return user_group_Id;
	}

	public void setUser_group_Id(String user_group_Id) {
		this.user_group_Id = user_group_Id;
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

	public Integer getRemain_bandwidth() {
		return remain_bandwidth;
	}

	public void setRemain_bandwidth(Integer remain_bandwidth) {
		this.remain_bandwidth = remain_bandwidth;
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

	public Date getDisconnect_time() {
		return disconnect_time;
	}

	public void setDisconnect_time(Date disconnect_time) {
		this.disconnect_time = disconnect_time;
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

	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}

	
	
	public Date getLast_house_keep_check_time() {
		return last_house_keep_check_time;
	}

	public void setLast_house_keep_check_time(Date last_house_keep_check_time) {
		this.last_house_keep_check_time = last_house_keep_check_time;
	}

	
	
	public String getAccess_mode() {
		return access_mode;
	}

	public void setAccess_mode(String access_mode) {
		this.access_mode = access_mode;
	}

	@Override
	public boolean isValidRequest() {
		if (network_id == null || organization_id == null || client_mac == null
				|| ssid == null) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonCaptivePortalRequest [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", bssid=");
		builder.append(bssid);
		builder.append(", ssid=");
		builder.append(ssid);
		builder.append(", ssid_id=");
		builder.append(ssid_id);
		builder.append(", client_mac=");
		builder.append(client_mac);
		builder.append(", client_ip=");
		builder.append(client_ip);
		builder.append(", cp_id=");
		builder.append(cp_id);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", network_id=");
		builder.append(network_id);
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
		builder.append(", quota_type=");
		builder.append(quota_type);
		builder.append(", disconnect_time=");
		builder.append(disconnect_time);
		builder.append(", created_at=");
		builder.append(created_at);
		builder.append(", last_access_time=");
		builder.append(last_access_time);
		builder.append(", status=");
		builder.append(status);
		builder.append(", last_house_keep_check_time=");
		builder.append(last_house_keep_check_time);
		builder.append(", access_mode=");
		builder.append(access_mode);
		builder.append("]");
		return builder.toString();
	}


}
