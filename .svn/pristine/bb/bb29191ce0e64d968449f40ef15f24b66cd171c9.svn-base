package com.littlecloud.control.entity.report;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "device_monthly_dpi_usages")
public class DeviceMonthlyDpiUsages extends DBObject implements java.io.Serializable
{
	private Integer id;
	private Integer network_id;
	private Integer device_id;
	private Integer unixtime;
	private Date datetime;
	private String service;
	private Long size;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "network_id", nullable = false)
	public Integer getNetwork_id() {
		return network_id;
	}
	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}
	
	@Column(name = "device_id", nullable = false)
	public Integer getDevice_id() {
		return device_id;
	}
	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}
	
	@Column(name = "unixtime", length=10)
	public Integer getUnixtime() {
		return unixtime;
	}
	public void setUnixtime(Integer unixtime) {
		this.unixtime = unixtime;
	}
	
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	@Column(name = "service", length = 45)
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	
	@Column(name = "size")
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceMonthlyDpiUsages [id=");
		builder.append(id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", unixtime=");
		builder.append(unixtime);
		builder.append(", datetime=");
		builder.append(datetime);
		builder.append(", service=");
		builder.append(service);
		builder.append(", size=");
		builder.append(size);
		builder.append("]");
		return builder.toString();
	}

}
