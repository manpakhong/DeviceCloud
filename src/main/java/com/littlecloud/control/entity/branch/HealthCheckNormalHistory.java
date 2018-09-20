package com.littlecloud.control.entity.branch;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import com.peplink.api.db.DBObject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="health_check_history",catalog="littlecloud_branch_production")
public class HealthCheckNormalHistory extends DBObject implements Serializable
{
	private Integer id;
	private String server_id;
	private String service;
	private String json;
	private Date lastupdate;
	private Integer status;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name="id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="server_id", length=45)
	public String getServer_id() {
		return server_id;
	}
	public void setServer_id(String server_id) {
		this.server_id = server_id;
	}
	
	@Column(name="service", length=45)
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	
	@Column(name="json", length=500)
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	
	@Column(name="lastupdate")
	public Date getTimestamp() {
		return lastupdate;
	}
	public void setTimestamp(Date timestamp) {
		this.lastupdate = timestamp;
	}

	@Column(name="status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HealthCheckNormalHistory [id=");
		builder.append(id);
		builder.append(", server_id=");
		builder.append(server_id);
		builder.append(", service=");
		builder.append(service);
		builder.append(", json=");
		builder.append(json);
		builder.append(", lastupdate=");
		builder.append(lastupdate);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}
	
}
