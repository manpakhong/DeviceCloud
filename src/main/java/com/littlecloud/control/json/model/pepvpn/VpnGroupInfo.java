package com.littlecloud.control.json.model.pepvpn;

import java.io.Serializable;
import java.util.ArrayList;

public class VpnGroupInfo implements Serializable {
	
	private long id = 0;//Only one group now
	private String topology = "star";
	private Integer hub_id;
	private Integer hub_net_id;
	private ArrayList<String> ip_host_list;
	private ArrayList<Integer> endpoints;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTopology() {
		return topology;
	}
	public void setTopology(String topology) {
		this.topology = topology;
	}
	public Integer getHub_id() {
		return hub_id;
	}
	public void setHub_id(Integer hub_id) {
		this.hub_id = hub_id;
	}
	public Integer getHub_net_id() {
		return hub_net_id;
	}
	public void setHub_net_id(Integer hub_net_id) {
		this.hub_net_id = hub_net_id;
	}
	public ArrayList<String> getIp_host_list() {
		return ip_host_list;
	}
	public void setIp_host_list(ArrayList<String> ip_host_list) {
		this.ip_host_list = ip_host_list;
	}
	public ArrayList<Integer> getEndpoints() {
		return endpoints;
	}
	public void setEndpoints(ArrayList<Integer> endpoints) {
		this.endpoints = endpoints;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VpnGroupInfo [id=");
		builder.append(id);
		builder.append(", topology=");
		builder.append(topology);
		builder.append(", hub_id=");
		builder.append(hub_id);
		builder.append(", hub_net_id=");
		builder.append(hub_net_id);
		builder.append(", ip_host_list=");
		builder.append(ip_host_list);
		builder.append(", endpoints=");
		builder.append(endpoints);
		builder.append("]");
		return builder.toString();
	}
}
