package com.littlecloud.control.json.model;

public class Json_Operation_Logs {

	private String timestamp;
	private String user_id;
	private Integer network_id;
	private String page;
	private String label;
	private String ssid;
	private String old_val;
	private String new_val;
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}
	public int getNetwork_id() {
		return network_id;
	}
	public void setNetwork_id(int network_id) {
		this.network_id = network_id;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getOld_val() {
		return old_val;
	}
	public void setOld_val(String old_val) {
		this.old_val = old_val;
	}
	public String getNew_val() {
		return new_val;
	}
	public void setNew_val(String new_val) {
		this.new_val = new_val;
	}
}
