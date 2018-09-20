package com.littlecloud.control.json.model;

import java.util.List;

public class Json_Device_Timely_Usage {
	private Integer id;
	private String type;
	private Integer network_id;
	private Integer device_id;
	private List<Json_Wan_Info> wans;
	private List<Json_Usage_Info> usages;
	private String sn;
	
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	
	public List<Json_Wan_Info> getWans() {
		return wans;
	}
	public void setWans(List<Json_Wan_Info> wans) {
		this.wans = wans;
	}
	public List<Json_Usage_Info> getUsages() {
		return usages;
	}
	public void setUsages(List<Json_Usage_Info> usages) {
		this.usages = usages;
	}
	
}
