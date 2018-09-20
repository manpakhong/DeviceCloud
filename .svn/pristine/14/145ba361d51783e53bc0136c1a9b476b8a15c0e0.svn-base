package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.littlecloud.control.json.model.pepvpn.PepvpnConnection;
import com.littlecloud.control.json.model.pepvpn.PepvpnLinkInfo;

public class PepvpnEndpointObject extends PoolObject implements PoolObjectIf, Serializable {

	/* key */
	private Integer iana_id;
	protected String sn;
	private String sid; // caller and server reference
	private long timestamp;
	
	protected PepvpnLinkInfo linkinfo;	
	protected CopyOnWriteArrayList<Integer> mvpn_order_list;
	protected CopyOnWriteArrayList<PepvpnConnection> mvpn_conn_list;	
	
	private Integer status;
	private int fetchStartTime;
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
		return this.getClass().getSimpleName() + "sn_pk" + getSn();// + "|" + getIana_id();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PepvpnEndpointObject [iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", sid=");
		builder.append(sid);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", linkinfo=");
		builder.append(linkinfo);
		builder.append(", mvpn_order_list=");
		builder.append(mvpn_order_list);
		builder.append(", mvpn_conn_list=");
		builder.append(mvpn_conn_list);
		builder.append(", status=");
		builder.append(status);
		builder.append(", fetchStartTime=");
		builder.append(fetchStartTime);
		builder.append("]");
		return builder.toString();
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

	public PepvpnLinkInfo getLinkinfo() {
		return linkinfo;
	}
	public void setLinkinfo(PepvpnLinkInfo linkinfo) {
		this.linkinfo = linkinfo;
	}

	public CopyOnWriteArrayList<Integer> getMvpn_order_list() {
		return mvpn_order_list;
	}

	public void setMvpn_order_list(CopyOnWriteArrayList<Integer> mvpn_order_list) {
		this.mvpn_order_list = mvpn_order_list;
	}

	public CopyOnWriteArrayList<PepvpnConnection> getMvpn_conn_list() {
		return mvpn_conn_list;
	}

	public void setMvpn_conn_list(
			CopyOnWriteArrayList<PepvpnConnection> mvpn_conn_list) {
		this.mvpn_conn_list = mvpn_conn_list;
	}
	
}
