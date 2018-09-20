package com.littlecloud.control.json.request;

import com.littlecloud.control.json.JsonRequest;

public class JsonMasterDeviceRequest extends JsonRequest{

	private Integer master_dev_id;
	private Integer network_id;
	private String organization_id;


	public Integer getNetwork_id() {
		return network_id;
	}


	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
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


	@Override
	public boolean isValidRequest() {
		if (organization_id == null)
		{
			return false;
		}
		return true;
	}

}
