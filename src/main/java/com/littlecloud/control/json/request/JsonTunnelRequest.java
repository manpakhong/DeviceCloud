package com.littlecloud.control.json.request;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.littlecloud.control.json.JsonRequest;
import com.littlecloud.control.json.model.Json_Mac_List;

public class JsonTunnelRequest extends JsonRequest {

	/*
	 * Necessary
	 */
	private String organization_id;
	private int device_id;
	private int iana_id;
	private String sn;
	
	/* tunnel operation */
	private Date start;
	private Date end;
	private String month;
	private int start_num;
	private int limit;
	private List<Json_Mac_List> mac_list;
	private String type;
	private String device_name;
	private int wan_id;
	private Date from_date;
	private List<String> client_id_list;
	private int id;
	private Integer top;
	private Float latitude;
	private Float longitude;
	private String address;
	private String ra_domain;
	private Boolean retry;
	private String ssh_host_key;
	/* optional */
	private int count;
	
	/* execute command */
	private String command_id;
	private String command;
	private List<HashMap<String,String>> argc;
	private String action;
	private boolean pending;
	
	@Override
	public boolean isValidRequest() {
		if (organization_id==null || iana_id==0 || sn==null)
		{
			return false;
		}
		return true;
	}
		
	public String getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}
	public int getDevice_id() {
		return device_id;
	}
	public void setDevice_id(int device_id) {
		this.device_id = device_id;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public List<Json_Mac_List> getMac_list() {
		return mac_list;
	}

	public void setMac_list(List<Json_Mac_List> mac_list) {
		this.mac_list = mac_list;
	}

	public int getStart_num() {
		return start_num;
	}

	public void setStart_num(int start_num) {
		this.start_num = start_num;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public int getWan_id() {
		return wan_id;
	}

	public void setWan_id(int wan_id) {
		this.wan_id = wan_id;
	}

	public Date getFrom_date() {
		return from_date;
	}

	public void setFrom_date(Date from_date) {
		this.from_date = from_date;
	}

	public List<String> getClient_id_list() {
		return client_id_list;
	}

	public void setClient_id_list(List<String> client_id_list) {
		this.client_id_list = client_id_list;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public Integer getTop() {
		return top;
	}

	public void setTop(Integer top) {
		this.top = top;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRa_domain() {
		return ra_domain;
	}

	public void setRa_domain(String ra_domain) {
		this.ra_domain = ra_domain;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public int getIana_id() {
		return iana_id;
	}

	public void setIana_id(int iana_id) {
		this.iana_id = iana_id;
	}

	public Boolean getRetry() {
		return retry;
	}

	public void setRetry(Boolean retry) {
		this.retry = retry;
	}

	public String getSsh_host_key() {
		return ssh_host_key;
	}

	public void setSsh_host_key(String ssh_host_key) {
		this.ssh_host_key = ssh_host_key;
	}

	public String getCommand_id() {
		return command_id;
	}

	public void setCommand_id(String command_id) {
		this.command_id = command_id;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public List<HashMap<String,String>> getArgc() {
		return argc;
	}

	public void setArgc(List<HashMap<String, String>> argc) {
		this.argc = argc;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

}
