package com.littlecloud.control.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

@Entity
@Table(name="device_fw_upgrade")
public class DeviceFwUpgrade extends DBObject implements java.io.Serializable
{
	private Integer device_id;
	private String target_ver;
	private Integer retry;
	private Integer level;
	
	@Column(name="device_id")
	public Integer getDevice_id() {
		return device_id;
	}
	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}
	
	@Column(name="target_ver")
	public String getTarget_ver() {
		return target_ver;
	}
	public void setTarget_ver(String target_ver) {
		this.target_ver = target_ver;
	}
	
	@Column(name="retry")
	public Integer getRetry() {
		return retry;
	}
	public void setRetry(Integer retry) {
		this.retry = retry;
	}
	
	@Column(name="level")
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceFwUpgrade [device_id=");
		builder.append(device_id);
		builder.append(", target_ver=");
		builder.append(target_ver);
		builder.append(", retry=");
		builder.append(retry);
		builder.append(", level=");
		builder.append(level);
		builder.append("]");
		return builder.toString();
	}
}
