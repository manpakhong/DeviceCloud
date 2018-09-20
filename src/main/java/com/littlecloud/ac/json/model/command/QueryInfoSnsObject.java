package com.littlecloud.ac.json.model.command;

import java.util.List;

public class QueryInfoSnsObject {
	private List<SnListObj> sn_list;

	public List<SnListObj> getSn_list() {
		return sn_list;
	}

	public void setSn_list(List<SnListObj> sn_list) {
		this.sn_list = sn_list;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryInfoSnsObject [sn_list=");
		builder.append(sn_list);
		builder.append("]");
		return builder.toString();
	}
}
