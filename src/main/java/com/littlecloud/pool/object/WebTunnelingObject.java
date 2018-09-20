package com.littlecloud.pool.object;

import java.io.Serializable;

public class WebTunnelingObject extends PoolObject implements PoolObjectIf, Serializable
{
	//key
	private Integer iana_id;
	private String sn;
	
	//value
	private String json;
	private String command_id;
	
	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getCommand_id() {
		return command_id;
	}

	public void setCommand_id(String command_id) {
		this.command_id = command_id;
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

	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.iana_id = iana_id;
		this.sn = sn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WebTunnelingObject [iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", json=");
		builder.append(json);
		builder.append(", command_id=");
		builder.append(command_id);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
	
}
