package com.littlecloud.control.json.request;

import java.util.List;

import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.json.JsonRequest;
import com.littlecloud.control.json.model.Json_Devices;

public class JsonOrganizationRequest extends JsonRequest {

	private Integer master_dev_id;
	private String organization_id;
	private List<Integer> deleted_network_ids;
	private List<Integer> device_ids;
	private List<Networks> networks;
	private Integer network_id;
	private boolean purge_data;
	private List<Json_Devices> devices;
	
	public List<Integer> getDeleted_network_ids() {
		return deleted_network_ids;
	}

	public void setDeleted_network_ids(List<Integer> deleted_network_ids) {
		this.deleted_network_ids = deleted_network_ids;
	}

	public List<Json_Devices> getDevices() {
		return devices;
	}

	public void setDevices(List<Json_Devices> devices) {
		this.devices = devices;
	}

	public List<Networks> getNetworks() {
		return networks;
	}

	public void setNetworks(List<Networks> networks) {
		this.networks = networks;
	}

	public boolean isPurge_data() {
		return purge_data;
	}

	public void setPurge_data(boolean purge_data) {
		this.purge_data = purge_data;
	}

	public Integer getMaster_dev_id() {
		return master_dev_id;
	}

	public void setMaster_dev_id(Integer master_dev_id) {
		this.master_dev_id = master_dev_id;
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

	public List<Integer> getDel_net_ids() {
		return deleted_network_ids;
	}

	public void setDel_net_ids(List<Integer> del_net_ids) {
		this.deleted_network_ids = del_net_ids;
	}

	public List<Networks> getUpdate_networks() {
		return networks;
	}

	public void setUpdate_networks(List<Networks> update_networks) {
		this.networks = update_networks;
	}

	public Integer getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}

	@Override
	public boolean isValidRequest() {		
		if (organization_id==null)
		{
			return false;
		}
		return true;
	}

}
