package com.littlecloud.control.json.request;

import java.util.Date;
import java.util.List;

import com.littlecloud.control.json.JsonRequest;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfiles;

public class JsonRadioRequest_DataSsidProfilesList extends JsonRequest {

	private String organization_id;
	private Integer network_id;
	private Integer device_id;
	private List<JsonConf_SsidProfiles> data;
	private Date timestamp;	
	private Boolean wifi_mgm_enabled;
	private Boolean follow_network;
	
	public Boolean getFollow_network() {
		return follow_network;
	}

	public void setFollow_network(Boolean follow_network) {
		this.follow_network = follow_network;
	}

	public Boolean getWifi_mgm_enabled() {
		return wifi_mgm_enabled;
	}

	public void setWifi_mgm_enabled(Boolean wifi_mgm_enabled) {
		this.wifi_mgm_enabled = wifi_mgm_enabled;
	}

	public List<JsonConf_SsidProfiles> getData() {
		return data;
	}

	public void setData(List<JsonConf_SsidProfiles> data) {
		this.data = data;
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
		if (organization_id == null || network_id == null)	/*|| data == null*/
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "JsonRadioRequest_DataSsidProfilesList [organization_id=" + organization_id + ", network_id="
				+ network_id + ", device_id=" + device_id + ", data=" + data + ", timestamp=" + timestamp
				+ ", wifi_mgm_enabled=" + wifi_mgm_enabled + ", follow_network=" + follow_network + ", caller_ref="
				+ caller_ref + ", server_ref=" + server_ref + ", version=" + version + "]";
	}
}
