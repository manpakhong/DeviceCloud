package com.littlecloud.control.json.model;

import java.util.ArrayList;
import java.util.Map;

import com.littlecloud.control.json.model.pepvpn.TunnelDetail;
import com.littlecloud.control.json.model.pepvpn.TunnelStatResponse;
import com.littlecloud.pool.object.PepvpnTunnelStatObject;

public class Json_PepvpnTunnelStat {
	protected String sid;
	protected long timestamp;
	protected String organization_id;
	protected Integer network_id;
	protected Integer iana_id;
	protected String sn;
	protected Integer status;
	protected ArrayList<String> tunnel_order;
	protected String stat;	
	protected Map<String,Map<String, TunnelDetail>> tunnel_stat_list;
	
	public Json_PepvpnTunnelStat parsePepvpnTunnelStat (PepvpnTunnelStatObject pepvpn)
	{
		if (pepvpn == null)
			return null;
		
		organization_id = pepvpn.getOrganization_id();
		network_id = pepvpn.getNetwork_id();
		iana_id = pepvpn.getIana_id();
		sn = pepvpn.getSn();
		status = pepvpn.getStatus();
		sid = pepvpn.getSid();
		timestamp = pepvpn.getTimestamp();
		tunnel_order = pepvpn.getTunnel_order();
		stat = pepvpn.getStat();
		TunnelStatResponse tunnel_response = new TunnelStatResponse();
		tunnel_response = pepvpn.getResponse();
		if (tunnel_response != null && !tunnel_response.getTunnel().isEmpty())
		tunnel_stat_list = tunnel_response.getTunnel();
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Json_PepvpnTunnelStat [sid=");
		builder.append(sid);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", status=");
		builder.append(status);
		builder.append(", tunnel_order=");
		builder.append(tunnel_order);
		builder.append(", stat=");
		builder.append(stat);
		builder.append(", tunnel_stat_list=");
		builder.append(tunnel_stat_list);
		builder.append("]");
		return builder.toString();
	}
	public Map<String, Map<String, TunnelDetail>> getTunnel_stat_list() {
		return tunnel_stat_list;
	}
	public void setTunnel_stat_list(
			Map<String, Map<String, TunnelDetail>> tunnel_stat_list) {
		this.tunnel_stat_list = tunnel_stat_list;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}
	public Integer getNetwork_id() {
		return network_id;
	}
	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
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
	public Integer getStatus() {
		return status;
	}
	public ArrayList<String> getTunnel_order() {
		return tunnel_order;
	}

	public void setTunnel_order(ArrayList<String> tunnel_order) {
		this.tunnel_order = tunnel_order;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	
	

}
