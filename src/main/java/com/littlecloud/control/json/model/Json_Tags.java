package com.littlecloud.control.json.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.littlecloud.control.entity.Tags;
import com.littlecloud.control.json.JsonExclude;

public class Json_Tags {
	private Long id;
	@SerializedName("name")
	private String tag_name;
	@SerializedName("profile_ids")
	private List<Integer> ssidProfilesIdLst;

	public Json_Tags parseTags(Tags tag)
	{
		id = tag.getId();
		tag_name = tag.getName();
		// network_id = tag.getNetworkId();
		//network_id = tag.getNetworks().getId();

		return this;
	}

	public List<Integer> getSsidProfilesIdLst() {
		return ssidProfilesIdLst;
	}

	public void setSsidProfilesIdLst(List<Integer> ssidProfilesIdLst) {
		this.ssidProfilesIdLst = ssidProfilesIdLst;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTag_name() {
		return tag_name;
	}

	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
	}
}
