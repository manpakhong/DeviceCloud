package com.littlecloud.ac.json.model.command;

import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.ACScheduler;

import net.sf.json.JSONObject;

public class QueryInfo<T> {
	private static final Logger log = Logger.getLogger(QueryInfo.class);		
	
	private String sid;
	private int iana_id = 23695;
	private String sn;
	private List<SnListObj> sn_list;
	private MessageType type;
	private Boolean retry;
	// private MessageStatus status;
	private int status;
	private int timestamp;
	private Integer duration;
	private Integer interval;
	
	private T data;
	
	public List<SnListObj> getSn_list() {
		return sn_list;
	}

	public void setSn_list(List<SnListObj> sn_list) {
		this.sn_list = sn_list;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public int getIana_id() {
		return iana_id;
	}

	public void setIana_id(int iana_id) {
		this.iana_id = iana_id;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Boolean getRetry() {
		return retry;
	}

	public void setRetry(Boolean retry) {
		this.retry = retry;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}
	
	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryInfo [sid=");
		builder.append(sid);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", sn_list=");
		builder.append(sn_list);
		builder.append(", type=");
		builder.append(type);
		builder.append(", retry=");
		builder.append(retry);
		builder.append(", status=");
		builder.append(status);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", duration=");
		builder.append(duration);
		builder.append(", interval=");
		builder.append(interval);
		builder.append(", data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}
}
