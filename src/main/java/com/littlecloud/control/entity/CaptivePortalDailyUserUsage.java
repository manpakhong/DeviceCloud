package com.littlecloud.control.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "captive_portal_daily_user_usages"
		, uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
public class CaptivePortalDailyUserUsage extends DBObject implements java.io.Serializable{
	private Integer id;
	private Date reportDate;
	private Integer unixtime;
	private Integer cpId;
	private String username;
	private String userGroupId;
	private BigDecimal bandwidthUsed;
	private BigDecimal timeUsed;
	private Integer sessionCount;
	private Boolean status;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "unixtime", nullable = true)
	public Integer getUnixtime() {
		return unixtime;
	}
	public void setUnixtime(Integer unixtime) {
		this.unixtime = unixtime;
	}
	@Column(name = "cp_id", nullable = true)
	public Integer getCpId() {
		return cpId;
	}
	public void setCpId(Integer cpId) {
		this.cpId = cpId;
	}
	@Column(name = "username", nullable = true)
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Column(name = "user_group_id", nullable = true)
	public String getUserGroupId() {
		return userGroupId;
	}
	public void setUserGroupId(String userGroupId) {
		this.userGroupId = userGroupId;
	}
	@Column(name = "bandwidth_used", nullable = true)
	public BigDecimal getBandwidthUsed() {
		return bandwidthUsed;
	}
	public void setBandwidthUsed(BigDecimal bandwidthUsed) {
		this.bandwidthUsed = bandwidthUsed;
	}
	@Column(name = "time_used", nullable = true)
	public BigDecimal getTimeUsed() {
		return timeUsed;
	}
	public void setTimeUsed(BigDecimal timeUsed) {
		this.timeUsed = timeUsed;
	}
	@Column(name = "session_count", nullable = true)
	public Integer getSessionCount() {
		return sessionCount;
	}
	public void setSessionCount(Integer sessionCount) {
		this.sessionCount = sessionCount;
	}
	@Column(name = "report_date", nullable = true)
	public Date getReportDate() {
		return reportDate;
	}
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}
	@Column(name = "status", nullable = true)
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CaptivePortalDailyUserUsage [id=");
		builder.append(id);
		builder.append(", reportDate=");
		builder.append(reportDate);
		builder.append(", unixtime=");
		builder.append(unixtime);
		builder.append(", cpId=");
		builder.append(cpId);
		builder.append(", username=");
		builder.append(username);
		builder.append(", userGroupId=");
		builder.append(userGroupId);
		builder.append(", bandwidthUsed=");
		builder.append(bandwidthUsed);
		builder.append(", timeUsed=");
		builder.append(timeUsed);
		builder.append(", sessionCount=");
		builder.append(sessionCount);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}




}
