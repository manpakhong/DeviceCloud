package com.littlecloud.control.entity.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "device_online_histories")
public class DeviceOnlineHistories extends DBObject implements java.io.Serializable
{
	private String id;
	private Integer device_id;
	private Integer online_time;
	private Integer offline_time;
	
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name = "id", unique = true, nullable = false, length = 45)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name = "device_id", nullable = false) 
	public Integer getDevice_id() {
		return device_id;
	}
	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}
	
	@Column(name = "online_time")
	public Integer getOnline_time() {
		return online_time;
	}
	public void setOnline_time(Integer online_time) {
		this.online_time = online_time;
	}
	
	@Column(name = "offline_time")
	public Integer getOffline_time() {
		return offline_time;
	}
	public void setOffline_time(Integer offline_time) {
		this.offline_time = offline_time;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceOnlineHistories [id=");
		builder.append(id);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", online_time=");
		builder.append(online_time);
		builder.append(", offline_time=");
		builder.append(offline_time);
		builder.append("]");
		return builder.toString();
	}
}
