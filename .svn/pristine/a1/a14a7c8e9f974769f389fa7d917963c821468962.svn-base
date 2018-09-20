package com.littlecloud.control.entity;

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
@Table(name = "ddns_records"
		, uniqueConstraints = @UniqueConstraint(columnNames = { "iana_id", "sn" }))
public class DdnsRecords extends DBObject implements java.io.Serializable{
	private Integer id;
	private Integer ianaId;
	private String sn;
	private String organizationId;
	private String wanId;
	private String ddnsName;
	private String wanIp;
	private Date lastUpdated;
	
	public DdnsRecords(Integer id, Integer ianaId, String sn,
			String organizationId, String wanId, String ddnsName, String wanIp,
			Date lastUpdated) {
		super();
		this.id = id;
		this.ianaId = ianaId;
		this.sn = sn;
		this.organizationId = organizationId;
		this.wanId = wanId;
		this.ddnsName = ddnsName;
		this.wanIp = wanIp;
		this.lastUpdated = lastUpdated;
	}

	public DdnsRecords() {
	}

	public DdnsRecords(Integer id) {
		super();
		this.id = id;
	}
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "iana_id", nullable = false)
	public Integer getIanaId() {
		return ianaId;
	}

	public void setIanaId(Integer ianaId) {
		this.ianaId = ianaId;
	}
	@Column(name = "sn", nullable = false)
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
	@Column(name = "organization_id", nullable = false)
	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	@Column(name = "wan_id", nullable = false)
	public String getWanId() {
		return wanId;
	}

	public void setWanId(String wanId) {
		this.wanId = wanId;
	}	
	@Column(name = "ddns_name", nullable = false)
	public String getDdnsName() {
		return ddnsName;
	}

	public void setDdnsName(String ddnsName) {
		this.ddnsName = ddnsName;
	}
	@Column(name = "wan_ip",nullable = false)
	public String getWanIp() {
		return wanIp;
	}

	public void setWanIp(String wanIp) {
		this.wanIp = wanIp;
	}
	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_updated", length = 19)
	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DdnsRecords [id=");
		builder.append(id);
		builder.append(", ianaId=");
		builder.append(ianaId);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", organizationId=");
		builder.append(organizationId);
		builder.append(", wanId=");
		builder.append(wanId);
		builder.append(", ddnsName=");
		builder.append(ddnsName);
		builder.append(", wanIp=");
		builder.append(wanIp);
		builder.append(", lastUpdated=");
		builder.append(lastUpdated);
		builder.append("]");
		return builder.toString();
	}
	
	public String getKey(){
		String key = "";
		if (this.getIanaId() != null){
			key += this.getIanaId();
		}
		if (this.getSn() != null){
			key += this.getSn();
		}
		return key;
	}
	
} // end class
