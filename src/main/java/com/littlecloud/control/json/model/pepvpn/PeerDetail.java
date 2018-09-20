package com.littlecloud.control.json.model.pepvpn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PeerDetail implements Serializable  {
//	private long id;
	private String peer_id;
	private Integer device_id;
	private String serial;
	private String name;
	private String type;	
	private String status;
	private Integer device_network_id;
	private Integer remote_device_id;
	private String remote_serial;	
	
	private String username;//profile name
	private List<String> route;
	private String bridge;
	private String server;
	private String client;
	private boolean secure;
	private long pid;//profile id
	
//	private List<Integer> endpoints_id;
//	private List<String> endpoints_serial;
	
//	public List<String> getEndpoints_serial() {
//		return endpoints_serial;
//	}
//	public void setEndpoints_serial(List<String> endpoints_serial) {
//		this.endpoints_serial = endpoints_serial;
//	}
//	public List<Integer> getEndpoints_id() {
//		return endpoints_id;
//	}
//	public void setEndpoints_id(List<Integer> endpoints_id) {
//		this.endpoints_id = endpoints_id;
//	}
//	public long getId() {
//		return id;
//	}
//	public void setId(long id) {
//		this.id = id;
//	}
	public String getPeer_id() {
		return peer_id;
	}
	public void setPeer_id(String peer_id) {
		this.peer_id = peer_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getPid() {
		return pid;
	}
	public void setPid(long pid) {
		this.pid = pid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public Integer getDevice_id() {
		return device_id;
	}
	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}
	public Integer getDevice_network_id() {
		return device_network_id;
	}
	public void setDevice_network_id(Integer device_network_id) {
		this.device_network_id = device_network_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	

	public List<String> getRoute() {
		return route;
	}
	public void setRoute(List<String> route) {
		this.route = route;
	}
	public String getBridge() {
		return bridge;
	}
	public void setBridge(String bridge) {
		this.bridge = bridge;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public boolean isSecure() {
		return secure;
	}
	public void setSecure(boolean secure) {
		this.secure = secure;
	}
//	public Integer getHub_id() {
//		return hub_id;
//	}
//	public void setHub_id(Integer hub_id) {
//		this.hub_id = hub_id;
//	}
//	public String getHub_serial() {
//		return hub_serial;
//	}
//	public void setHub_serial(String hub_serial) {
//		this.hub_serial = hub_serial;
//	}
	public Integer getRemote_device_id() {
		return remote_device_id;
	}
	public void setRemote_device_id(Integer remote_device_id) {
		this.remote_device_id = remote_device_id;
	}
	public String getRemote_serial() {
		return remote_serial;
	}
	public void setRemote_serial(String remote_serial) {
		this.remote_serial = remote_serial;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PeerDetail [peer_id=");
		builder.append(peer_id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", type=");
		builder.append(type);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", serial=");
		builder.append(serial);
		builder.append(", device_network_id=");
		builder.append(device_network_id);
		builder.append(", status=");
		builder.append(status);
		builder.append(", username=");
		builder.append(username);
		builder.append(", route=");
		builder.append(route);
		builder.append(", bridge=");
		builder.append(bridge);
		builder.append(", server=");
		builder.append(server);
		builder.append(", client=");
		builder.append(client);
		builder.append(", secure=");
		builder.append(secure);
		builder.append(", pid=");
		builder.append(pid);
		builder.append(", remote_device_id=");
		builder.append(remote_device_id);
		builder.append(", remote_serial=");
		builder.append(remote_serial);
//		builder.append(", endpoints_id=");
//		builder.append(endpoints_id);
//		builder.append(", endpoints_serial=");
//		builder.append(endpoints_serial);
		builder.append("]");
		return builder.toString();
	}
	
	

}
