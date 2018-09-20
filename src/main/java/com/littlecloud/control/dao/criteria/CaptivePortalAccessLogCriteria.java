package com.littlecloud.control.dao.criteria;

import java.util.Date;

public class CaptivePortalAccessLogCriteria {
	private Integer id;
	private Integer networkId;
	private Integer deviceId;
	private Integer ssidId;
	private Date dateFrom;
	private Date dateTo;
	private String reportType;
	private String ssid;
	private String accessMode;
	private String clientMac;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getNetworkId() {
		return networkId;
	}
	public void setNetworkId(Integer networkId) {
		this.networkId = networkId;
	}
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	public Integer getSsidId() {
		return ssidId;
	}
	public void setSsidId(Integer ssidId) {
		this.ssidId = ssidId;
	}
	public Date getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	public Date getDateTo() {
		return dateTo;
	}
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getAccessMode() {
		return accessMode;
	}
	public void setAccessMode(String accessMode) {
		this.accessMode = accessMode;
	}
	public String getClientMac() {
		return clientMac;
	}
	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CaptivePortalAccessLogCriteria [id=");
		builder.append(id);
		builder.append(", networkId=");
		builder.append(networkId);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", ssidId=");
		builder.append(ssidId);
		builder.append(", dateFrom=");
		builder.append(dateFrom);
		builder.append(", dateTo=");
		builder.append(dateTo);
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
