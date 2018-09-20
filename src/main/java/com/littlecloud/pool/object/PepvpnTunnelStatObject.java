package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.ArrayList;

import com.littlecloud.control.json.model.pepvpn.TunnelStatResponse;

public class PepvpnTunnelStatObject extends PoolObject implements PoolObjectIf, Serializable {
    
	private String sid;	
	private long timestamp;
	private String organization_id;
	private Integer network_id;
	private Integer iana_id;
	private String sn;
	private Integer status;	
	private ArrayList<String> tunnel_order;
	private String stat;		
	private TunnelStatResponse response;
	private int fetchStartTime;
	@Override
	 public void setKey(Integer iana_id, String sn) {
	 this.iana_id = iana_id;
	 this.sn = sn;
	 }
	 @Override
	 public String getKey() {
	 return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	 }
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PepvpnTunnelStatObject [sid=");
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
		builder.append(", response=");
		builder.append(response);
		builder.append(", fetchStartTime=");
		builder.append(fetchStartTime);
		builder.append("]");
		return builder.toString();
	}
	


//	public class TunnelList {		
//		private Map<String, TunnelDetail> tunnelDetail = new HashMap<String, TunnelDetail> ();
//		private ArrayList<Integer> order;
//		public ArrayList<Integer> getOrder() {
//			return order;
//		}
//		public void setOrder(ArrayList<Integer> order) {
//			this.order = order;
//		}
//		public Map<String, TunnelDetail> getTunnelDetail() {
//			return tunnelDetail;
//		}
//		public void setTunnelDetail(Map<String, TunnelDetail> tunnelDetail) {
//			this.tunnelDetail = tunnelDetail;
//		}
//
//		@Override
//		public String toString() {
//			StringBuilder builder = new StringBuilder();
//			builder.append("[");
//			builder.append(tunnelDetail);
//			builder.append(", order=");
//			builder.append(order);
//			builder.append("]");
//			return builder.toString();
//		}						
//	}
//	

	

	
	public int getFetchStartTime() {
		return fetchStartTime;
	}
	public void setFetchStartTime(int fetchStartTime) {
		this.fetchStartTime = fetchStartTime;
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

	public void setStatus(Integer status) {
		this.status = status;
	}

	public ArrayList<String> getTunnel_order() {
		return tunnel_order;
	}
	public void setTunnel_order(ArrayList<String> tunnel_order) {
		this.tunnel_order = tunnel_order;
	}
	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public TunnelStatResponse getResponse() {
		return response;
	}

	public void setResponse(TunnelStatResponse response) {
		this.response = response;
	}

}
