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
@Table(name="health_check",catalog="littlecloud_branch_production")
public class HealthCheck extends DBObject implements Serializable
{
	private Integer id;
	private String server_id;
	private Integer status;
	private String status_detail;
	private Date lasthealthy;
	private Date lastunhealthy;
	private String lastUnhealthyReason;
	private Date lastupdate;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name="id",nullable=false,unique=true)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="server_id",length=45)
	public String getServer_id() {
		return server_id;
	}
	public void setServer_id(String server_id) {
		this.server_id = server_id;
	}
	
	@Column(name="status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Column(name="status_detail")
	public String getStatus_detail() {
		return status_detail;
	}
	public void setStatus_detail(String status_detail) {
		this.status_detail = status_detail;
	}
	
	@Column(name="lasthealthy")
	public Date getLasthealthy() {
		return lasthealthy;
	}
	public void setLasthealthy(Date lasthealthy) {
		this.lasthealthy = lasthealthy;
	}
	
	@Column(name="lastunhealthy")
	public Date getLastunhealthy() {
		return lastunhealthy;
	}
	public void setLastunhealthy(Date lastunhealthy) {
		this.lastunhealthy = lastunhealthy;
	}
	
	@Column(name="lastunhealthyreason")
	public String getLastUnhealthyReason() {
		return lastUnhealthyReason;
	}
	public void setLastUnhealthyReason(String lastUnhealthyReason) {
		this.lastUnhealthyReason = lastUnhealthyReason;
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
		builder.append("HealthCheck [id=");
		builder.append(id);
		builder.append(", server_id=");
		builder.append(server_id);
		builder.append(", status=");
		builder.append(status);
		builder.append(", status_detail=");
		builder.append(status_detail);
		builder.append(", lasthealthy=");
		builder.append(lasthealthy);
		builder.append(", lastunhealthy=");
		builder.append(lastunhealthy);
		builder.append(", lastUnhealthyReason=");
		builder.append(lastUnhealthyReason);
		builder.append(", lastupdate=");
		builder.append(lastupdate);
		builder.append("]");
		return builder.toString();
	}
	
}
