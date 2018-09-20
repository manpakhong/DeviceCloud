package com.littlecloud.pool.model;

public class DailyClientUsageResult {
	String mac;
	String name;
	String manufacturer;
	String ip;
	Double tx;
	Double rx;
	Integer network_id;
	Integer device_id;
	Long client_cnt;
	Integer unixtime;
	
	public DailyClientUsageResult(){
		super();
	}
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public double getTx() {
		return tx;
	}
	public void setTx(Double tx) {
		this.tx = tx;
	}
	public double getRx() {
		return rx;
	}
	public void setRx(Double rx) {
		this.rx = rx;
	}	
	public Long getClient_cnt() {
		return client_cnt;
	}
	public void setClient_cnt(Long client_cnt) {
		this.client_cnt = client_cnt;
	}

	public Integer getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}

	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}

	public Integer getUnixtime() {
		return unixtime;
	}
	public void setUnixtime(Integer unixtime) {
		this.unixtime = unixtime;
	}
	
	@Override
	public String toString() {
		return "DailyClientUsageResult [mac=" + mac + ", ip=" + ip + ", name=" + name
				+ ", manufacturer=" + manufacturer + ", tx=" + tx + ", rx="
				+ rx + ", network_id=" + network_id + ", device_id="
				+ device_id + ", client_cnt=" + client_cnt + ", unixtime=" + unixtime + "]";
	}
	
	
	
}
