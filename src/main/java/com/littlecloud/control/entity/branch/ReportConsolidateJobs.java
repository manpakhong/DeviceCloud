package com.littlecloud.control.entity.branch;

// Generated Jun 26, 2013 5:02:31 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

/**
 * ReportConsolidateJobs generated by hbm2java
 */
@Entity
@Table(name = "report_consolidate_jobs", catalog = "littlecloud_branch_production")
public class ReportConsolidateJobs extends DBObject implements java.io.Serializable {

	private int jobTime;
	private Boolean allSuccess;
	private Integer startTime;
	private Integer endTime;

	public ReportConsolidateJobs() {
	}

	public ReportConsolidateJobs(int jobTime) {
		this.jobTime = jobTime;
	}

	public ReportConsolidateJobs(int jobTime, Boolean allSuccess,
			Integer startTime, Integer endTime) {
		this.jobTime = jobTime;
		this.allSuccess = allSuccess;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	@Id
	@Column(name = "job_time", unique = true, nullable = false)
	public int getJobTime() {
		return this.jobTime;
	}

	public void setJobTime(int jobTime) {
		this.jobTime = jobTime;
	}

	@Column(name = "all_success")
	public Boolean getAllSuccess() {
		return this.allSuccess;
	}

	public void setAllSuccess(Boolean allSuccess) {
		this.allSuccess = allSuccess;
	}

	@Column(name = "start_time")
	public Integer getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}

	@Column(name = "end_time")
	public Integer getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Integer endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportConsolidateJobs [jobTime=");
		builder.append(jobTime);
		builder.append(", allSuccess=");
		builder.append(allSuccess);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append("]");
		return builder.toString();
	}

}