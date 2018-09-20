package com.littlecloud.control.json.request;

import java.util.Date;
import java.util.List;

import com.littlecloud.control.json.JsonRequest;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfiles;

public class JsonRadioRequest_SsidAvailability extends JsonRequest {

	private String organization_id;
	private Integer network_id;
	private JsonConf_SsidProfiles data;
	private Date timestamp;

	private List<String> deleted_tag_names;
	private List<String> added_tag_names;

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

	public JsonConf_SsidProfiles getData() {
		return data;
	}

	public void setData(JsonConf_SsidProfiles data) {
		this.data = data;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public List<String> getDeleted_tag_names() {
		return deleted_tag_names;
	}

	public void setDeleted_tag_names(List<String> deleted_tag_names) {
		this.deleted_tag_names = deleted_tag_names;
	}

	public List<String> getAdded_tag_names() {
		return added_tag_names;
	}

	public void setAdded_tag_names(List<String> added_tag_names) {
		this.added_tag_names = added_tag_names;
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
