package com.littlecloud.control.entity.branch;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

@Entity
@Table(name="health_check_abnormal_history",catalog="littlecloud_branch_production")
public class HealthCheckAbnormalHistory extends DBObject implements Serializable
{
	private Integer id;
	private String server_id;
	private String service;
	private String json;
	private Date lastupdate;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="server_id",nullable=false)
	public String getServer_id() {
		return server_id;
	}
	public void setServer_id(String server_id) {
		this.server_id = server_id;
	}

	@Column(name="service",length=45)
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	
	@Column(name="json",length=500)
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	
	@Column(name="lastupdate")
	public Date getLastupdate() {
		return lastupdate;
	}
	public void setLastupdate(Date lastupdate) {
		this.lastupdate = lastupdate;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HealthCheckAbnormalHistory [id=");
		builder.append(id);
		builder.append(", server_id=");
		builder.append(server_id);
		builder.append(", service=");
		builder.append(service);
		builder.append(", json=");
		builder.append(json);
		builder.append(", lastupdate=");
		builder.append(lastupdate);
		builder.append("]");
		return builder.toString();
	}
	
}
