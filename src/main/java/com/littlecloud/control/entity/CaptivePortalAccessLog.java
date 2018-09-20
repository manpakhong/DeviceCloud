package com.littlecloud.control.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "captive_portal_access_logs", uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
public class CaptivePortalAccessLog extends DBObject{
	private Integer id;
	private Integer networkId;
	private Integer deviceId;
	private Integer ssidId;
	private Integer cpId;
	private Integer unixtime;
	private String reportType;
	private String ssid;
	private String accessMode;
	private String clientMac;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "network_id", nullable = true)
	public Integer getNetworkId() {
		return networkId;
	}
	public void setNetworkId(Integer networkId) {
		this.networkId = networkId;
	}
	@Column(name = "device_id", nullable = true)
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	@Column(name = "ssid_id", nullable = true)
	public Integer getSsidId() {
		return ssidId;
	}
	public void setSsidId(Integer ssidId) {
		this.ssidId = ssidId;
	}
	@Column(name = "unixtime", nullable = true)
	public Integer getUnixtime() {
		return unixtime;
	}
	public void setUnixtime(Integer unixtime) {
		this.unixtime = unixtime;
	}
	@Column(name = "report_type", nullable = true)
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	@Column(name = "ssid", nullable = true)
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	@Column(name = "cp_id", nullable = true)
	public Integer getCpId() {
		return cpId;
	}
	public void setCpId(Integer cpId) {
		this.cpId = cpId;
	}
	@Column(name = "access_mode", nullable = true)
	public String getAccessMode() {
		return accessMode;
	}
	public void setAccessMode(String accessMode) {
		this.accessMode = accessMode;
	}
	@Column(name = "client_mac", nullable = true)
	public String getClientMac() {
		return clientMac;
	}
	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CaptivePortalAccessLog [id=");
		builder.append(id);
		builder.append(", networkId=");
		builder.append(networkId);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", ssidId=");
		builder.append(ssidId);
		builder.append(", unixtime=");
		builder.append(unixtime);
		builder.append(", reportType=");
		builder.append(reportType);
		builder.append(", ssid=");
		builder.append(ssid);
		builder.append(", accessMode=");
		builder.append(accessMode);
		builder.append(", clientMac=");
		builder.append(clientMac);
		builder.append("]");
		return builder.toString();
	}
	
	
}
