package com.littlecloud.ac.health.info;

import java.util.Date;

public class WebServiceInfo {

	private String key;
	private String request; // Json format
	private long thread_id;
	private String orgId;
	private String network_id;
	private String device_id;
	private String method; // WS name
	private Date start_time;
	private Date end_time;
	private long duration;
	private String status; // "running / completed"
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(" [");
		
		builder.append("name=");
		builder.append(method);		
		builder.append(", org_net_dev=");
		builder.append(request);
//		builder.append(", network_id=");
//		builder.append(network_id);
//		builder.append(", device_id=");
//		builder.append(device_id);
		builder.append(", thread_id=");
		builder.append(thread_id);
		builder.append(", start_time=");
		builder.append(start_time);
		builder.append(", end_time=");
		builder.append(end_time);
		builder.append(", duration=");
		builder.append(duration);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public long getThread_id() {
		return thread_id;
	}
	public void setThread_id(long thread_id) {
		this.thread_id = thread_id;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getNetwork_id() {
		return network_id;
	}
	public void setNetwork_id(String network_id) {
		this.network_id = network_id;
	}
	public String getDevice_id() {
		return device_id;
	}
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Date getStart_time() {
		return start_time;
	}
	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}
	public Date getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
