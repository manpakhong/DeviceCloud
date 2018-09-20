package com.littlecloud.control.json.model.pepvpn;

import java.io.Serializable;
import java.util.List;

import com.littlecloud.pool.object.PoolObjectIf;

public class PepvpnLinkInfo implements Serializable {
	private List<Integer> order_list;
	private List<Link> link_list;
	
	public List<Integer> getOrder_list() {
		return order_list;
	}
	public void setOrder_list(List<Integer> order_list) {
		this.order_list = order_list;
	}
	public List<Link> getLink_list() {
		return link_list;
	}
	public void setLink_list(List<Link> link_list) {
		this.link_list = link_list;
	}
	@Override
	public String toString() {
		return "PepvpnLinkInfo [order_list=" + order_list + ", link_list=" + link_list + "]";
	}	
}
