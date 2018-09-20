package com.littlecloud.control.json.model;

public class Json_SSID_Usage {

	private String ssid;
	private String encryption;
	private Float usage;
	private Integer clients_count;
	private Float usage_percent;
	private Float clients_percent;
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getEncryption() {
		return encryption;
	}
	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}
	public Float getUsage() {
		return usage;
	}
	public void setUsage(Float usage) {
		this.usage = usage;
	}
	public Integer getClients_count() {
		return clients_count;
	}
	public void setClients_count(Integer clients_count) {
		this.clients_count = clients_count;
	}
	public Float getUsage_percent() {
		return usage_percent;
	}
	public void setUsage_percent(Float usage_percent) {
		this.usage_percent = usage_percent;
	}
	public Float getClients_percent() {
		return clients_percent;
	}
	public void setClients_percent(Float clients_percent) {
		this.clients_percent = clients_percent;
	}
	
	@Override
	public String toString() {
		return "Json_SSID_Usage [ssid=" + ssid + ", encryption=" + encryption
				+ ", usage=" + usage + ", clients_count=" + clients_count
				+ ", usage_percent=" + usage_percent + ", clients_percent="
				+ clients_percent + "]";
	}
		
}
