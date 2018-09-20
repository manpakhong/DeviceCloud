package com.littlecloud.ac.json.model;

public class Json_CaptivePortalUserSessionContent {
//	public final static String STATUS_LOGIN = "login";
//	public final static String STATUS_NOT_FOUND = "not_found";
//	public final static String STATUS_USAGE = "usage";
//	public final static String STATUS_LOGOUT = "logout";
	public final static Integer STATUS_SUCCESS = 1;
	public final static Integer STATUS_FAILURE = 0;
	
	private String client_mac;
	private String bssid;
	private String ssid;
	private Integer status;
	private Integer duration;
	private Integer bandwidth;
	private String status_msg;
	
	public String getClient_mac() {
		if (client_mac != null && !client_mac.isEmpty()){
			client_mac = client_mac.replace(":", "-");
		}
		return client_mac;
	}
	public void setClient_mac(String client_mac) {
		if (client_mac != null && !client_mac.isEmpty()){
			client_mac = client_mac.replace(":", "-");
		}
		this.client_mac = client_mac;
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}	
	public Integer getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(Integer bandwidth) {
		this.bandwidth = bandwidth;
	}
	
	public String getStatus_msg() {
		return status_msg;
	}
	public void setStatus_msg(String status_msg) {
		this.status_msg = status_msg;
	}
	@Override
	public String toString() {
		return "Json_CaptivePortalUserSessionContent [client_mac=" + client_mac
				+ ", bssid=" + bssid + ", ssid=" + ssid + ", status=" + status
				+ ", duration=" + duration + ", bandwidth=" + bandwidth
				+ ", status_msg=" + status_msg + "]";
	}


	
}
