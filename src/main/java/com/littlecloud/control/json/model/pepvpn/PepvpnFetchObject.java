package com.littlecloud.control.json.model.pepvpn;

import com.littlecloud.ac.json.model.command.MessageType;

public class PepvpnFetchObject {

	private String sid;
	private String org_id;
	private String sn;
	private Integer iana_id;
	private MessageType type;
	private PepvpnQueryCommand querycommand;
	
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getOrg_id() {
		return org_id;
	}
	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getIana_id() {
		return iana_id;
	}
	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}
	public MessageType getType() {
		return type;
	}
	public void setType(MessageType type) {
		this.type = type;
	}
	public PepvpnQueryCommand getQuerycommand() {
		return querycommand;
	}
	public void setQuerycommand(PepvpnQueryCommand querycommand) {
		this.querycommand = querycommand;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PepvpnFetchObject [sid=");
		builder.append(sid);
		builder.append(", org_id=");
		builder.append(org_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", type=");
		builder.append(type);
		builder.append(", querycommand=");
		builder.append(querycommand);
		builder.append("]");
		return builder.toString();
	}
	
}
