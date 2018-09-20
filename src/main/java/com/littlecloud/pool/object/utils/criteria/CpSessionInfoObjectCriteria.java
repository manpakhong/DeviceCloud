package com.littlecloud.pool.object.utils.criteria;

public class CpSessionInfoObjectCriteria {
	private Integer ianaId; 
	private String sn; 
	private Integer devicesId; 
	private String clientMac; 
	private String ssid; 
	private String organizationId; 
	private Integer networkId;
	
	public Integer getIanaId() {
		return ianaId;
	}
	public void setIanaId(Integer ianaId) {
		this.ianaId = ianaId;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getDevicesId() {
		return devicesId;
	}
	public void setDevicesId(Integer devicesId) {
		this.devicesId = devicesId;
	}
	public String getClientMac() {
		return clientMac;
	}
	public void setClientMac(String clientMac) {
		if (clientMac != null){
			clientMac = clientMac.replace(':', '-');
		}
		this.clientMac = clientMac;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	public Integer getNetworkId() {
		return networkId;
	}
	public void setNetworkId(Integer networkId) {
		this.networkId = networkId;
	}
	
}
