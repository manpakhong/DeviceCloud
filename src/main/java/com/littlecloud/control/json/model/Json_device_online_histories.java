package com.littlecloud.control.json.model;

import java.util.Date;

public class Json_device_online_histories 
{
	private Date online_time;
	private Date offline_time;
	
	public Date getOnline_time() {
		return online_time;
	}
	public void setOnline_time(Date online_time) {
		this.online_time = online_time;
	}
	public Date getOffline_time() {
		return offline_time;
	}
	public void setOffline_time(Date offline_time) {
		this.offline_time = offline_time;
	}	
	
}
