package com.littlecloud.control.json.request;

import java.util.Date;

import com.littlecloud.control.json.JsonRequest;

public class JsonClientRequest extends JsonRequest {

	private String organization_id;
	private Integer device_id;
	private Integer network_id;
	private String mac;
	private String type;
	private Date from_date;
	private Integer wan_id;
	private String client_id;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getFrom_date() {
		return from_date;
	}

	public void setFrom_date(Date from_date) {
		this.from_date = from_date;
	}

	public Integer getWan_id() {
		return wan_id;
	}

	public void setWan_id(Integer wan_id) {
		this.wan_id = wan_id;
	}

	public String getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}
	
	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public Integer getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	@Override
	public boolean isValidRequest() {
		if (organization_id == null) {
			return false;
		} 
		return true;
	}
}
