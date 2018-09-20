package com.littlecloud.control.entity.branch;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "config_update_history"
		, catalog = "littlecloud_branch_production"
		, uniqueConstraints = @UniqueConstraint(columnNames = { "iana_id", "sn" }))
public class ConfigUpdateHistory extends DBObject implements java.io.Serializable {

	private Integer id;
	private int ianaId;
	private String sn;
	private String orgId;
	private String type;
	private String ssid;
	private String config;
	private String reason;
	private String md5;
	private Date timestamp;

	public ConfigUpdateHistory() {
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "iana_id", nullable = false)
	public int getIanaId() {
		return this.ianaId;
	}

	public void setIanaId(int ianaId) {
		this.ianaId = ianaId;
	}

	@Column(name = "sn", nullable = false, length = 32)
	public String getSn() {
		return this.sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
	
	@Column(name = "reason", nullable = true, length = 200)
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	@Column(name = "reason", nullable = true, length = 45)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
		builder.append("ConfigUpdateHistory [id=");
		builder.append(id);
		builder.append(", ianaId=");
		builder.append(ianaId);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", orgId=");
		builder.append(orgId);
		builder.append(", type=");
		builder.append(type);
		builder.append(", ssid=");
		builder.append(ssid);
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
