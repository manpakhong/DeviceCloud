package com.littlecloud.pool.object.eventlog;

import java.io.Serializable;

import com.google.gson.JsonElement;

public class JsonEventLogObject implements Serializable{
	public static String TYPE_WAN = "wan";
	public static String TYPE_TIME_SYNC = "time_sync";
	
	private String type;
	private String version;
	private JsonElement evt_obj;
		
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public JsonElement getEvt_obj() {
		return evt_obj;
	}
	public void setEvt_obj(JsonElement evt_obj) {
		this.evt_obj = evt_obj;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
}
