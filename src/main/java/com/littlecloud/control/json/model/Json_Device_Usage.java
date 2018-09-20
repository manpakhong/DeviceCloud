package com.littlecloud.control.json.model;

public class Json_Device_Usage {

	private Integer device_id;
	private Integer device_network_id;
	private String device_name;
	private String model_name;
	private Float usage;
	private Long clients_count;
	private String sn;
	
	public Integer getDevice_id() {
		return device_id;
	}
	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}
	public String getDevice_name() {
		return device_name;
	}
	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	public Float getUsage() {
		return usage;
	}
	public void setUsage(Float usage) {
		this.usage = usage;
	}
	public Long getClients_count() {
		return clients_count;
	}
	public void setClients_count(Long clients_count) {
		this.clients_count = clients_count;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getDevice_network_id() {
		return device_network_id;
	}
	public void setDevice_network_id(Integer device_network_id) {
		this.device_network_id = device_network_id;
	}
	
}
