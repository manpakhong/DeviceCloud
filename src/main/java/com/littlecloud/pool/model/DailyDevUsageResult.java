package com.littlecloud.pool.model;

import java.util.Date;

public class DailyDevUsageResult {
	Integer network_id;
	Integer deviceId;
	double tx;
	double rx;
	Integer unixtime;
	
	public DailyDevUsageResult(){
		
	}
	public DailyDevUsageResult(Integer deviceId, double tx, double rx){
		super();
		this.deviceId = deviceId;
		this.tx = tx;
		this.rx = rx;
	}
	
	public DailyDevUsageResult(Integer unixtime, float tx, float rx){
		super();
		this.unixtime = unixtime;
		this.tx = tx;
		this.rx = rx;
	}
	
	public Integer getNetwork_id() {
		return network_id;
	}
	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	public double getTx() {
		return tx;
	}
	public void setTx(double tx) {
		this.tx = tx;
	}
	public double getRx() {
		return rx;
	}
	public void setRx(double rx) {
		this.rx = rx;
	}
	public Integer getUnixtime() {
		return unixtime;
	}
	public void setUnixtime(Integer unixtime) {
		this.unixtime = unixtime;
	}

	@Override
	public String toString() {
		return "DailyDevUsageResult [deviceId=" + deviceId + ", tx=" + tx
				+ ", rx=" + rx + ", unixtime=" + unixtime + "]";
	}
	
	
	
}
