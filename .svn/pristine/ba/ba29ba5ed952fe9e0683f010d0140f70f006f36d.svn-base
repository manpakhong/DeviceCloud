package com.littlecloud.control.json.request;

import java.util.Date;

import com.littlecloud.control.json.JsonRequest;
import com.littlecloud.control.json.model.config.JsonConf_Admin;

public class JsonConfigRequest_Admin extends JsonRequest {

	private String organization_id;
	private Integer network_id;
	private Integer device_id;
	private Date timestamp;
	private JsonConf_Admin data;

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
	
	public JsonConf_Admin getData() {
		return data;
	}

	public void setData(JsonConf_Admin data) {
		this.data = data;
	}

	@Override
	public boolean isValidRequest() {
		if (organization_id == null || network_id == null)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonConfigRequest_Admin [organization_id=");
		builder.append(organization_id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}
}
