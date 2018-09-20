package com.littlecloud.ac.json.model;

public class Json_GenicApiResponse {
	protected String stat;
	protected Integer timestamp;
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	public Integer getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		return "Json_GenicApiResponse [stat=" + stat + ", timestamp="
				+ timestamp + "]";
	}
	
}
