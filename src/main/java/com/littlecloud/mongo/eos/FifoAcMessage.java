package com.littlecloud.mongo.eos;

public class FifoAcMessage {
	private String id;
	private Integer timestamp;
	private String dir;
	private String payload;
	private String type;
	private String sn;
	private Integer ianaId;
	private String sid;
	private Integer status;
	private Integer totalRecordCount;
	
	private String fullMessage;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	public String getFullMessage() {
		return fullMessage;
	}
	public void setFullMessage(String fullMessage) {
		this.fullMessage = fullMessage;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getIanaId() {
		return ianaId;
	}
	public void setIanaId(Integer ianaId) {
		this.ianaId = ianaId;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Integer getTotalRecordCount() {
		return totalRecordCount;
	}
	public void setTotalRecordCount(Integer totalRecordCount) {
		this.totalRecordCount = totalRecordCount;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FifoAcMessage [id=");
		builder.append(id);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", dir=");
		builder.append(dir);
		builder.append(", payload=");
		builder.append(payload);
		builder.append(", type=");
		builder.append(type);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", ianaId=");
		builder.append(ianaId);
		builder.append(", sid=");
		builder.append(sid);
		builder.append(", status=");
		builder.append(status);
		builder.append(", totalRecordCount=");
		builder.append(totalRecordCount);
		builder.append(", fullMessage=");
		builder.append(fullMessage);
		builder.append("]");
		return builder.toString();
	}

}
