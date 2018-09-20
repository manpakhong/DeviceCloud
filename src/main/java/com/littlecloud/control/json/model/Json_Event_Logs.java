package com.littlecloud.control.json.model;

import java.util.Date;

public class Json_Event_Logs {

	public enum EVENT_TYPE { SYSTEM, SPEEDFUSION, WAN, PPTP, IP_ADDR_CONFLICT, MAC_ADDR_CONFLICT, HA_CONFIG_SYNC; };
		
	private String id;	
	private Date ts;
	private String device_name;
	private Integer device_id;
	private String ssid;
	private String client_name;
	private String client_id;
	private String client_mac;
	private String event_type;
	private String detail;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getTs() {
		return ts;
	}
	public void setTs(Date ts) {
		this.ts = ts;
	}
	public String getDevice_name() {
		return device_name;
	}
	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}
	public Integer getDevice_id() {
		return device_id;
	}
	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getClient_name() {
		return client_name;
	}
	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getClient_mac() {
		return client_mac;
	}
	public void setClient_mac(String client_mac) {
		this.client_mac = client_mac;
	}
	public String getEvent_type() {
		return event_type;
	}
	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
		
	
}
