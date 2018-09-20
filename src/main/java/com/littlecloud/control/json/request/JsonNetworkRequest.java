package com.littlecloud.control.json.request;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.littlecloud.control.json.JsonRequest;
import com.littlecloud.control.json.model.Json_Devices;
import com.littlecloud.control.json.model.Json_Mac_List;

public class JsonNetworkRequest extends JsonRequest {

	private String organization_id;
	private Integer network_id;
	private Integer device_id;
	private Integer target_network_id;
	private Integer master_device_id;
	private Integer iana_id;
	private List<Integer> device_ids;
	private List<String> device_sns;
	private List<String> tag_names;
	private List<String> client_id_list;
	private List<Integer> geo_id_list;
	private List<Json_Devices> network_device_list;
	private Integer trace;
	@SerializedName("is_ssid_profile")
	private Boolean isSsidProfile;

	
	/* for event log query */
	private String log_id;
	private String page_id;
	private Integer direction; /* 0: latest 1: newer 2: older */
	private String before;
	private String device;
	private String client;
	private String events;
	private String start;
	private String end;	
	private String keyword;
	private boolean search_by_config = true;
	private boolean has_status = false;
	private List<Json_Mac_List> mac_list;
	private Date start_time;
	private String sort;
	private String type;
	private Date log_date;
	private Date first_date;
	private Integer row_per_page;
	private Integer page_no;
	private Integer device_count;
	private List<Integer> visible_devices;
	private Boolean is_apply_timezone;
	private Boolean is_gps_tracking_disabled = false;

	/* network settings */
	
	
	public Boolean getIs_gps_tracking_disabled() {
		return is_gps_tracking_disabled;
	}

	public void setIs_gps_tracking_disabled(Boolean is_gps_tracking_disabled) {
		this.is_gps_tracking_disabled = is_gps_tracking_disabled;
	}

	public Boolean getIs_apply_timezone() {
		return is_apply_timezone;
	}

	public void setIs_apply_timezone(Boolean is_apply_timezone) {
		this.is_apply_timezone = is_apply_timezone;
	}

	public Integer getIana_id() {
		return iana_id;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}

	public List<Integer> getGeo_id_list() {
		return geo_id_list;
	}

	public void setGeo_id_list(List<Integer> geo_id_list) {
		this.geo_id_list = geo_id_list;
	}

	public Integer getMaster_device_id() {
		return master_device_id;
	}

	public void setMaster_device_id(Integer master_device_id) {
		this.master_device_id = master_device_id;
	}

	public boolean isHas_status() {
		return has_status;
	}

	public void setHas_status(boolean has_status) {
		this.has_status = has_status;
	}

	public boolean isSearch_by_config() {
		return search_by_config;
	}

	public void setSearch_by_config(boolean search_by_config) {
		this.search_by_config = search_by_config;
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

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getLog_id() {
		return log_id;
	}

	public void setLog_id(String log_id) {
		this.log_id = log_id;
	}

	public String getPage_id() {
		return page_id;
	}

	public void setPage_id(String page_id) {
		this.page_id = page_id;
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

	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}

	public Integer getTarget_network_id() {
		return target_network_id;
	}

	public void setTarget_network_id(Integer target_network_id) {
		this.target_network_id = target_network_id;
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

	public List<String> getClient_id_list() {
		return client_id_list;
	}

	public void setClient_id_list(List<String> client_id_list) {
		this.client_id_list = client_id_list;
	}

	@Override
	public boolean isValidRequest() {
		if (organization_id == null || network_id == null)
		{
			return false;
		}
		return true;
	}

	public List<Json_Mac_List> getMac_list() {
		return mac_list;
	}

	public void setMac_list(List<Json_Mac_List> mac_list) {
		this.mac_list = mac_list;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getLog_date() {
		return log_date;
	}

	public void setLog_date(Date log_date) {
		this.log_date = log_date;
	}

	public Date getFirst_date() {
		return first_date;
	}

	public void setFirst_date(Date first_date) {
		this.first_date = first_date;
	}

	public Integer getRow_per_page() {
		return row_per_page;
	}

	public void setRow_per_page(Integer row_per_page) {
		this.row_per_page = row_per_page;
	}

	public Integer getPage_no() {
		return page_no;
	}

	public void setPage_no(Integer page_no) {
		this.page_no = page_no;
	}

	public Integer getDevice_count() {
		return device_count;
	}

	public void setDevice_count(Integer device_count) {
		this.device_count = device_count;
	}

	public List<Json_Devices> getNetwork_device_list() {
		return network_device_list;
	}

	public void setNetwork_device_list(List<Json_Devices> network_device_list) {
		this.network_device_list = network_device_list;
	}

	public List<Integer> getVisible_devices() {
		return visible_devices;
	}

	public void setVisible_devices(List<Integer> visible_devices) {
		this.visible_devices = visible_devices;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonNetworkRequest [organization_id=");
		builder.append(organization_id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", target_network_id=");
		builder.append(target_network_id);
		builder.append(", master_device_id=");
		builder.append(master_device_id);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", device_ids=");
		builder.append(device_ids);
		builder.append(", device_sns=");
		builder.append(device_sns);
		builder.append(", tag_names=");
		builder.append(tag_names);
		builder.append(", client_id_list=");
		builder.append(client_id_list);
		builder.append(", geo_id_list=");
		builder.append(geo_id_list);
		builder.append(", network_device_list=");
		builder.append(network_device_list);
		builder.append(", trace=");
		builder.append(trace);
		builder.append(", isSsidProfile=");
		builder.append(isSsidProfile);
		builder.append(", log_id=");
		builder.append(log_id);
		builder.append(", page_id=");
		builder.append(page_id);
		builder.append(", direction=");
		builder.append(direction);
		builder.append(", before=");
		builder.append(before);
		builder.append(", device=");
		builder.append(device);
		builder.append(", client=");
		builder.append(client);
		builder.append(", events=");
		builder.append(events);
		builder.append(", start=");
		builder.append(start);
		builder.append(", end=");
		builder.append(end);
		builder.append(", keyword=");
		builder.append(keyword);
		builder.append(", search_by_config=");
		builder.append(search_by_config);
		builder.append(", has_status=");
		builder.append(has_status);
		builder.append(", mac_list=");
		builder.append(mac_list);
		builder.append(", start_time=");
		builder.append(start_time);
		builder.append(", sort=");
		builder.append(sort);
		builder.append(", type=");
		builder.append(type);
		builder.append(", log_date=");
		builder.append(log_date);
		builder.append(", first_date=");
		builder.append(first_date);
		builder.append(", row_per_page=");
		builder.append(row_per_page);
		builder.append(", page_no=");
		builder.append(page_no);
		builder.append(", device_count=");
		builder.append(device_count);
		builder.append(", visible_devices=");
		builder.append(visible_devices);
		builder.append(", isFollowTimezone=");
		builder.append(is_apply_timezone);
		builder.append("]");
		return builder.toString();
	}
}
