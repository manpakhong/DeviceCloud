package com.littlecloud.control.json.request;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.littlecloud.control.json.JsonRequest;
import com.littlecloud.control.json.model.config.JsonConf_PepvpnProfiles;

public class JsonNetworkRequest_PepvpnProfile extends JsonRequest {

	private String organization_id;
	private Integer network_id;

	private JsonConf_PepvpnProfiles data;

	private List<Integer> device_ids;
	private List<String> device_sns;
	private List<String> tag_names;

	private Integer trace;
	@SerializedName("is_ssid_profile")
	private Boolean isSsidProfile;

	/* for event log query */
	private Integer log_id;
	private Integer direction; /* 0: latest 1: newer 2: older */
	private String before;
	private String device;
	private String client;
	private String events;
	private String start;
	private String end;

	public JsonConf_PepvpnProfiles getData() {
		return data;
	}

	public void setData(JsonConf_PepvpnProfiles data) {
		this.data = data;
	}

	public Boolean getIsSsidProfile() {
		return isSsidProfile;
	}

	public void setIsSsidProfile(Boolean isSsidProfile) {
		this.isSsidProfile = isSsidProfile;
	}

	public Integer getTrace() {
		return trace;
	}

	public void setTrace(Integer trace) {
		this.trace = trace;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public Integer getLog_id() {
		return log_id;
	}

	public void setLog_id(Integer log_id) {
		this.log_id = log_id;
	}

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getEvents() {
		return events;
	}

	public void setEvents(String events) {
		this.events = events;
	}

	public Integer getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}

	public String getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}

	public List<Integer> getDevice_ids() {
		return device_ids;
	}

	public void setDevice_ids(List<Integer> device_ids) {
		this.device_ids = device_ids;
	}

	public List<String> getDevice_sns() {
		return device_sns;
	}

	public void setDevice_sns(List<String> device_sns) {
		this.device_sns = device_sns;
	}

	public List<String> getTag_names() {
		return tag_names;
	}

	public void setTag_names(List<String> tag_names) {
		this.tag_names = tag_names;
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
