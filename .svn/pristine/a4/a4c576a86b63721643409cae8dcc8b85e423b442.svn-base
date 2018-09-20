package com.littlecloud.control.json.model;

import java.io.Serializable;
import java.util.List;

import com.littlecloud.control.json.model.pepvpn.PepvpnConnection;
import com.littlecloud.control.json.model.pepvpn.PepvpnLinkInfo;
import com.littlecloud.pool.object.PepvpnEndpointObject;

public class Json_PepvpnStatus implements Serializable {

	protected String sn;
	protected PepvpnLinkInfo linkinfo;	
	protected List<Integer> mvpn_order_list;
	protected List<PepvpnConnection> mvpn_conn_list;

	public Json_PepvpnStatus parsePepvpnEndpointObject(PepvpnEndpointObject pepvpn)
	{
		sn = pepvpn.getSn();
		linkinfo = pepvpn.getLinkinfo();
		mvpn_order_list = pepvpn.getMvpn_order_list();
		mvpn_conn_list = pepvpn.getMvpn_conn_list();

		return this;
	}
	
	@Override
	public String toString() {
		return "Json_PepvpnStatus [sn=" + sn + ", linkinfo=" + linkinfo + ", mvpn_order_list=" + mvpn_order_list + ", mvpn_conn_list=" + mvpn_conn_list + "]";
	}

	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public PepvpnLinkInfo getLinkinfo() {
		return linkinfo;
	}
	public void setLinkinfo(PepvpnLinkInfo linkinfo) {
		this.linkinfo = linkinfo;
	}
	public List<Integer> getMvpn_order_list() {
		return mvpn_order_list;
	}
	public void setMvpn_order_list(List<Integer> mvpn_order_list) {
		this.mvpn_order_list = mvpn_order_list;
	}
	public List<PepvpnConnection> getMvpn_conn_list() {
		return mvpn_conn_list;
	}
	public void setMvpn_conn_list(List<PepvpnConnection> mvpn_conn_list) {
		this.mvpn_conn_list = mvpn_conn_list;
	}
}
