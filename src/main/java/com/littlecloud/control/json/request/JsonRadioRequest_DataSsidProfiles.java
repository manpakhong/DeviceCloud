package com.littlecloud.control.json.request;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.littlecloud.control.json.JsonRequest;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfiles;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfilesWeb;

public class JsonRadioRequest_DataSsidProfiles extends JsonRequest {

	private String organization_id;
	private Integer network_id;
	private Integer device_id;
	private JsonConf_SsidProfilesWeb data;
	private Boolean follow_network;
	private Date timestamp;

	public JsonConf_SsidProfilesWeb getData() {
		return data;
	}

	public void setData(JsonConf_SsidProfilesWeb data) {
		this.data = data;
	}

	public Boolean getFollow_network() {
		return follow_network;
	}

	public void setFollow_network(Boolean follow_network) {
		this.follow_network = follow_network;
	}

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

	@Override
	public boolean isValidRequest() {
		if (organization_id == null || network_id == null || data == null)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
