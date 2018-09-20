package com.littlecloud.control.entity.branch;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "config_updates"
		, catalog = "littlecloud_branch_production")
public class ConfigUpdates extends DBObject implements java.io.Serializable {

	private ConfigUpdatesId id;
	private String orgId;		
	private int confUpdate;
	private int updateMethod;
	private String ssid;
	private Date firstsave;		/* first config save time */
	private Date firstput;		/* first config_put time */
	private Date lastput;		/* last config_put sent time */
	private Date lastapply;		/* last config_put response time */
	private int retry;
	private String detail;
	private String error;
	private String config;
	private String reason;
	private String md5;
	private Date timestamp;

	public ConfigUpdates() {
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "ianaId", column = @Column(name = "iana_id", nullable = false)),
			@AttributeOverride(name = "sn", column = @Column(name = "sn", nullable = false)) })
	public ConfigUpdatesId getId() {
		return id;
	}

	public void setId(ConfigUpdatesId id) {
		this.id = id;
	}

	@Column(name = "org_id", length = 8)
	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	@Column(name = "ssid", nullable = true, length = 100)
	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	
	@Column(name = "reason", nullable = true, length = 200)
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Column(name = "retry", nullable = false)
	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	@Column(name = "detail", nullable = true, length = 3000)
	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	@Column(name = "conf_update", nullable = false)
	public int getConfUpdate() {
		return this.confUpdate;
	}

	public void setConfUpdate(int confUpdate) {
		this.confUpdate = confUpdate;
	}
	
	@Column(name = "update_method", nullable = false)
	public int getUpdateMethod() {
		return updateMethod;
	}

	public void setUpdateMethod(int updateMethod) {
		this.updateMethod = updateMethod;
	}

	@Column(name = "firstsave", nullable = true, length = 19)
	public Date getFirstsave() {
		return firstsave;
	}

	public void setFirstsave(Date firstsave) {
		this.firstsave = firstsave;
	}

	@Column(name = "firstput", nullable = true, length = 19)
	public Date getFirstput() {
		return firstput;
	}

	public void setFirstput(Date firstput) {
		this.firstput = firstput;
	}

	@Column(name = "lastapply", nullable = true, length = 19)
	public Date getLastapply() {
		return lastapply;
	}

	public void setLastapply(Date lastapply) {
		this.lastapply = lastapply;
	}
	
	@Column(name = "lastput", nullable = true, length = 19)
	public Date getLastput() {
		return lastput;
	}

	public void setLastput(Date lastput) {
		this.lastput = lastput;
	}

	@Column(name = "error", nullable = true, length = 500)
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	@Column(name = "config", nullable = true)
	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}
	
	@Column(name = "md5", nullable = true, length = 100)
	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "timestamp", nullable = false, length = 19)
	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigUpdates [id=");
		builder.append(id);
		builder.append(", orgId=");
		builder.append(orgId);
		builder.append(", confUpdate=");
		builder.append(confUpdate);
		builder.append(", updateMethod=");
		builder.append(updateMethod);
		builder.append(", ssid=");
		builder.append(ssid);
		builder.append(", firstsave=");
		builder.append(firstsave);
		builder.append(", firstput=");
		builder.append(firstput);
		builder.append(", lastput=");
		builder.append(lastput);
		builder.append(", lastapply=");
		builder.append(lastapply);
		builder.append(", retry=");
		builder.append(retry);
		builder.append(", detail=");
		builder.append(detail);
		builder.append(", error=");
		builder.append(error);
		builder.append(", config=");
		builder.append(config);
		builder.append(", reason=");
		builder.append(reason);
		builder.append(", md5=");
		builder.append(md5);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append("]");
		return builder.toString();
	}
}
