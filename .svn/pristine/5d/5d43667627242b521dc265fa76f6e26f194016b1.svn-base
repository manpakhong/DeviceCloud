package com.littlecloud.control.json.model.config;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.littlecloud.control.entity.CaptivePortal;
import com.littlecloud.control.entity.ConfigurationSettingsExtraSettings;
import com.littlecloud.control.entity.ConfigurationSsids;
import com.littlecloud.control.json.model.Json_Tags;
import com.littlecloud.control.json.util.JsonUtils;

/* use to extends JsonConf_SsidProfiles for settings on Webpage */
public class JsonConf_SsidProfilesWeb extends JsonConf_SsidProfiles {

	public enum ACTION { insert, update, delete }
	
	private boolean device_selection = false;

	private List<Json_Tags> tags;
	private List<Json_Tags> deleted_tags;
	private List<Json_Tags> added_tags;
	private ACTION action;
	private CaptivePortal captive_portal;

	public static JsonConf_SsidProfilesWeb parseConfigurationSsids(ConfigurationSsids ssid)
	{
		return JsonUtils.<JsonConf_SsidProfilesWeb> fromJson(ssid.getConfig(), JsonConf_SsidProfilesWeb.class);
	}
	
	public List<Json_Tags> getTags() {
		return tags;
	}

	public void setTags(List<Json_Tags> tags) {
		this.tags = tags;
	}

	public List<Json_Tags> getDeleted_tags() {
		return deleted_tags;
	}

	public void setDeleted_tags(List<Json_Tags> deleted_tags) {
		this.deleted_tags = deleted_tags;
	}

	public List<Json_Tags> getAdded_tags() {
		return added_tags;
	}

	public void setAdded_tags(List<Json_Tags> added_tags) {
		this.added_tags = added_tags;
	}

	public boolean isDevice_selection() {
		return device_selection;
	}

	public void setDevice_selection(boolean device_selection) {
		this.device_selection = device_selection;
	}
	
	public ACTION getAction() {
		return action;
	}

	public void setAction(ACTION action) {
		this.action = action;
	}

	public CaptivePortal getCaptive_portal() {
		return captive_portal;
	}

	public void setCaptive_portal(CaptivePortal captive_portal) {
		this.captive_portal = captive_portal;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
