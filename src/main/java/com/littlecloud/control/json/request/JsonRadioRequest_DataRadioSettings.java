package com.littlecloud.control.json.request;

import java.util.Date;

import com.littlecloud.control.json.JsonRequest;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettingsWeb;

public class JsonRadioRequest_DataRadioSettings extends JsonRequest {

	private String organization_id;
	private Integer network_id;
	private Integer device_id;
	private JsonConf_RadioSettingsWeb data;
	private Date timestamp;

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public JsonConf_RadioSettingsWeb getData() {
		return data;
	}

	public void setData(JsonConf_RadioSettingsWeb data) {
		this.data = data;
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

	@Override
	public boolean isValidRequest() {
		if (organization_id == null || network_id == null || data == null)
		{
			return false;
		}
		return true;
	}

}
