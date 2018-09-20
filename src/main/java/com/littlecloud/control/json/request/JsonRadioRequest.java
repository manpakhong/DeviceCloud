package com.littlecloud.control.json.request;

import java.util.Date;

import com.littlecloud.control.json.JsonRequest;

public class JsonRadioRequest extends JsonRequest {

	private String organization_id;
	private Integer network_id;
	private Integer device_id;
	private Boolean follow_network;
	private Date timestamp;

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}

	public Integer getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}
	
	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}

	public Boolean getFollow_network() {
		return follow_network;
	}

	public void setFollow_network(Boolean follow_network) {
		this.follow_network = follow_network;
	}

	@Override
	public boolean isValidRequest() {
		if (organization_id == null || network_id == null)
		{
			return false;
		}
		return true;
	}

}
