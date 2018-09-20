package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.littlecloud.control.json.model.pepvpn.PeerDetail;
import com.littlecloud.control.json.model.pepvpn.PeerDetailResponse;
import com.littlecloud.control.json.model.pepvpn.VpnGroupInfo;



public class PepvpnPeerDetailObject extends PoolObject implements PoolObjectIf, Serializable {

	private String sid;	
	private long timestamp;
	private Integer iana_id;
	protected String organization_id;
	protected Integer network_id;
	private String sn;
	private Integer status;	
	protected Boolean enabled;	
	protected ArrayList<VpnGroupInfo> vpn_grp;
	private String stat;		
	private PeerDetailResponse response;
	private Boolean success; 
	private int fetchStartTime;
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PepvpnPeerDetailObject [sid=");
		builder.append(sid);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", iana_id=");
		builder.append(iana_id);
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
		builder.append(", response=");
		builder.append(response);
		builder.append(", success=");
		builder.append(success);
		builder.append(", fetchStartTime=");
		builder.append(fetchStartTime);
		builder.append("]");
		return builder.toString();
	}

	 public int getFetchStartTime() {
		return fetchStartTime;
	}

	public void setFetchStartTime(int fetchStartTime) {
		this.fetchStartTime = fetchStartTime;
	}

	@Override
	 public void setKey(Integer iana_id, String sn) {
	 this.iana_id = iana_id;
	 this.sn = sn;
	 }
	 @Override
	 public String getKey() {
	 return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
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

	public Integer getIana_id() {
		return iana_id;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
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

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public PeerDetailResponse getResponse() {
		return response;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public void setResponse(PeerDetailResponse response) {
		this.response = response;
	}

	
//	public class PeerDetailResponse {
//		private ArrayList<PeerDetail> peer;
//		
//		public ArrayList<PeerDetail> getPeer() {
//			return peer;
//		}
//		public void setPeer(ArrayList<PeerDetail> peer) {
//			this.peer = peer;
//		}
//
//		@Override
//		public String toString() {
//			StringBuilder builder = new StringBuilder();
//			builder.append("{peer=");
//			builder.append(peer);
//			builder.append("}");
//			return builder.toString();
//		}
//	}
	
	
}
