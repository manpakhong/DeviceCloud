package com.littlecloud.pool.model;

public class DailyDevSsidUsageResult {
	Integer network_id;
	Integer device_id;
	String essid;
	String encryption;
	Double tx;
	Double rx;
	
	public DailyDevSsidUsageResult(){
		
	}
	public DailyDevSsidUsageResult(Integer network_id, Integer device_id, String essid, String encryption, Double tx, Double rx){
		super();
		this.network_id = network_id;
		this.device_id = device_id;
		this.essid = essid;
		this.encryption = encryption;
		this.tx = tx;
		this.rx = rx;
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
	public String getEssid() {
		return essid;
	}
	public void setEssid(String essid) {
		this.essid = essid;
	}
	public String getEncryption() {
		return encryption;
	}
	public void setEncryption(String encryption) {
		this.encryption = encryption;
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

	@Override
	public String toString() {
		return "DailyDevSsidUsageResult [essid=" + essid + ", encryption="
				+ encryption + ", tx=" + tx + ", rx=" + rx + "]";
	}
	
	
	
}
