package com.littlecloud.ac.json.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Json_CaptivePortalUserSession {
//	public final static String TYPE_USER_SESSION = "user_session";
	public final static String TYPE_LOGIN = "login";
	public final static String TYPE_DISCONNECT = "disconnect";
	public final static String TYPE_CONNECT = "connect";
	public final static String TYPE_USAGE = "usage";
	public final static String TYPE_LOGOUT = "logout";
//	public final static String STATUS_LOGIN = "login";
//	public final static String STATUS_NOT_FOUND = "not_found";
//	public final static String STATUS_USAGE = "usage";
//	public final static String STATUS_LOGOUT = "logout";
	
	private String type;
	private Integer version;
//	private String status;
	private CopyOnWriteArrayList<Json_CaptivePortalUserSessionContent> content;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public List<Json_CaptivePortalUserSessionContent> getContent() {
		return content;
	}
	public void setContent(List<Json_CaptivePortalUserSessionContent> content) {
		if (content != null){
			this.content = new CopyOnWriteArrayList<Json_CaptivePortalUserSessionContent>(content);
		}
	}
	@Override
	public String toString() {
		return "Json_CaptivePortalUserSession [type=" + type + ", version="
				+ version + ", content=" + content + "]";
	}
	
//	public String getStatus() {
//		return status;
//	}
//	public void setStatus(String status) {
//		this.status = status;
//	}


}
