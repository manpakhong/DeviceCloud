package com.littlecloud.control.json.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.littlecloud.control.json.model.pepvpn.PeerDetail;
import com.littlecloud.control.json.model.pepvpn.PeerDetailResponse;
import com.littlecloud.control.json.model.pepvpn.VpnGroupInfo;
import com.littlecloud.pool.object.PepvpnPeerDetailObject;

public class Json_PepvpnPeerDetail implements Serializable {
	protected String sid;
	protected long timestamp;
	protected String organization_id;
	protected Integer network_id;
	protected String sn;
	protected Integer status;
	
	protected Boolean enabled;	
	protected ArrayList<VpnGroupInfo> vpn_grp;
	protected String stat;
	protected List<PeerDetail> peer_detail_list;
	
	public Json_PepvpnPeerDetail parsePepvpnPeerDetail(PepvpnPeerDetailObject pepvpn) 
	{
		if (pepvpn == null)
			return null;
		organization_id = pepvpn.getOrganization_id();
		network_id = pepvpn.getNetwork_id();
		sid = pepvpn.getSid();
		timestamp = pepvpn.getTimestamp();
		sn = pepvpn.getSn();
		status = pepvpn.getStatus();
		enabled = pepvpn.getEnabled();
		vpn_grp = pepvpn.getVpn_grp();
		stat = pepvpn.getStat();
		PeerDetailResponse response = pepvpn.getResponse();		
		peer_detail_list = response.getPeer();
		
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Json_PepvpnPeerDetail [sid=");
		builder.append(sid);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", status=");
		builder.append(status);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", vpn_grp=");
		builder.append(vpn_grp);
		builder.append(", stat=");
		builder.append(stat);
		builder.append(", peer_detail_list=");
		builder.append(peer_detail_list);
		builder.append("]");
		return builder.toString();
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

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public ArrayList<VpnGroupInfo> getVpn_grp() {
		return vpn_grp;
	}

	public void setVpn_grp(ArrayList<VpnGroupInfo> vpn_grp) {
		this.vpn_grp = vpn_grp;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public List<PeerDetail> getPeer_detail_list() {
		return peer_detail_list;
	}

	public void setPeer_detail_list(List<PeerDetail> peer_detail_list) {
		this.peer_detail_list = peer_detail_list;
	}

	
}
