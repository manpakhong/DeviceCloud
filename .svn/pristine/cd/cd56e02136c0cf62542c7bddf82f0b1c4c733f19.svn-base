package com.littlecloud.control.entity.support;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.peplink.api.db.DBObject;
@Entity
@Table(name = "device_diag_reports", uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
public class DeviceDiagReport extends DBObject implements Serializable{
	private Integer id;
	private Integer ianaId;
	private String orgId;
	private String sn;
	private Integer unixtime;
	private Date createdDate;
	private byte [] reportContent;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "iana_id", unique = false, nullable = false)
	public Integer getIanaId() {
		return ianaId;
	}
	public void setIanaId(Integer ianaId) {
		this.ianaId = ianaId;
	}
	@Column(name = "sn", unique = false, nullable = false)
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	@Column(name = "unixtime", unique = false, nullable = false)
	public Integer getUnixtime() {
		return unixtime;
	}
	public void setUnixtime(Integer unixtime) {
		this.unixtime = unixtime;
	}
	@Column(name = "created_date", unique = false, nullable = false)
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	@Column(name = "report_content", unique = false, nullable = false)
	public byte[] getReportContent() {
		return reportContent;
	}
	public void setReportContent(byte[] reportContent) {
		this.reportContent = reportContent;
	}	
	@Column(name = "org_id", unique = false, nullable = false)
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceDiagReport [id=");
		builder.append(id);
		builder.append(", ianaId=");
		builder.append(ianaId);
		builder.append(", orgId=");
		builder.append(orgId);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", unixtime=");
		builder.append(unixtime);
		builder.append(", createdDate=");
		builder.append(createdDate);
		builder.append(", reportContent=");
		builder.append(Arrays.toString(reportContent));
		builder.append("]");
		return builder.toString();
	}

	
}
