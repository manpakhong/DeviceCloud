package com.littlecloud.ac.json.model;

import java.util.List;

public class Json_AcInfoUpdate {
	private Boolean full;
	private List<Json_AcInfoDevice> dev_list;
	
	public Boolean getFull() {
		return full;
	}

	public void setFull(Boolean full) {
		this.full = full;
	}

	public List<Json_AcInfoDevice> getDev_list() {
		return dev_list;
	}

	public void setDev_list(List<Json_AcInfoDevice> dev_list) {
		this.dev_list = dev_list;
	}

	@Override
	public String toString() {
		return "Json_AcInfoUpdate [full=" + full + ", dev_list=" + dev_list
				+ "]";
	}

}
