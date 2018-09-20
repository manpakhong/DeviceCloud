package com.littlecloud.control.json.request;

public class JsonNetworkDevice {
	private Integer iana_id;
	private String sn;
	
	public JsonNetworkDevice(Integer iana_id, String sn) {
		super();
		this.iana_id = iana_id;
		this.sn = sn;
	}
	
	public JsonNetworkDevice(){
		
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
	
	
}
