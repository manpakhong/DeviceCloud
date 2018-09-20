package com.littlecloud.control.entity;

import java.util.Date;

public class DpiUsageObject 
{
	private Integer network_id;
	private Integer device_id;
	private String service;
	private Long size;
	private String datetime;
	
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
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DpiUsageObject [network_id=");
		builder.append(network_id);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", service=");
		builder.append(service);
		builder.append(", size=");
		builder.append(size);
		builder.append(", datetime=");
		builder.append(datetime);
		builder.append("]");
		return builder.toString();
	}
}
