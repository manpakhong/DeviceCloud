package com.littlecloud.control.dao.criteria;

public class CaptivePortalSessionsCriteria {
	private String clientMac;
	private String ssid;
	private Integer ssidId;
	private String organizationId;
	private Integer networkId;
	public String getClientMac() {
		return clientMac;
	}
	public void setClientMac(String clientMac) {
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

	public Integer getSsidId() {
		return ssidId;
	}
	public void setSsidId(Integer ssidId) {
		this.ssidId = ssidId;
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
