package com.littlecloud.control.entity.branch;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

@Entity
@Table(name="schedule_tasks", catalog = "littlecloud_branch_production")
public class ScheduleTasks extends DBObject implements java.io.Serializable
{
	private Integer id;
	private String jobid;
	private String organization_id;
	private String host_name;
	private Date start_time;
	private Date update_time;
	private Integer retry;
	private Date end_time;
	private Integer status;
	private String error;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name="id",nullable=false,unique=true)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="jobid", length=100)
	public String getJobid() {
		return jobid;
	}
	public void setJobid(String jobid) {
		this.jobid = jobid;
	}
	
	@Column(name="organization_id", length=45)
	public String getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}
	
	@Column(name="host_name", length=45)
	public String getHost_name() {
		return host_name;
	}
	public void setHost_name(String host_name) {
		this.host_name = host_name;
	}
	
	@Column(name="start_time")
	public Date getStart_time() {
		return start_time;
	}
	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}
	
	@Column(name="update_time")
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	
	@Column(name="retry")
	public Integer getRetry() {
		return retry;
	}
	public void setRetry(Integer retry) {
		this.retry = retry;
	}
	
	@Column(name="end_time")
	public Date getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}
	
	@Column(name="status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Column(name="error", length=500)
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ScheduleTasks [id=");
		builder.append(id);
		builder.append(", jobid=");
		builder.append(jobid);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", host_name=");
		builder.append(host_name);
		builder.append(", start_time=");
		builder.append(start_time);
		builder.append(", update_time=");
		builder.append(update_time);
		builder.append(", retry=");
		builder.append(retry);
		builder.append(", end_time=");
		builder.append(end_time);
		builder.append(", status=");
		builder.append(status);
		builder.append(", error=");
		builder.append(error);
		builder.append("]");
		return builder.toString();
	}
	
}
