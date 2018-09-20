package com.littlecloud.control.json.request;

import java.util.List;

import com.littlecloud.control.json.JsonRequest;

public class JsonCommandAcRedirectRequest extends JsonRequest{
	private String organization_id;
	private Integer iana_id;
	private String sn;
	private List<String> host_list;
	
	public String getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}
	public Integer getIana_id() {
		return iana_id;
	}
	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public List<String> getHost_list() {
		return host_list;
	}
	public void setHost_list(List<String> host_list) {
		this.host_list = host_list;
	}
	
	
	@Override
	public String toString() {
		return "JsonCommandAcRedirectRequest [organization_id="
				+ organization_id + ", iana_id=" + iana_id + ", sn=" + sn
				+ ", host_list=" + host_list + "]";
	}
	@Override
	public boolean isValidRequest() {

		if (iana_id == null ){
			return false;
		}
		if (sn == null ){
			return false;
		}
		return true;
	}
	
}
