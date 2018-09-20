package com.littlecloud.pool.object;

import java.io.Serializable;

public class ClientInfoObject extends PoolObject implements PoolObjectIf, Serializable
{
	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private String organization_id;
	private String client_id;
	private String client_name;
	private Integer last_device_id;
	private Long lastUpdated;
	private boolean flag;

	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.iana_id = iana_id;
		this.sn = sn;
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

	public String getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getClient_name() {
		return client_name;
	}

	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}

	public Integer getLast_device_id() {
		return last_device_id;
	}

	public void setLast_device_id(Integer last_device_id) {
		this.last_device_id = last_device_id;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientInfoObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", client_id=");
		builder.append(client_id);
		builder.append(", client_name=");
		builder.append(client_name);
		builder.append(", last_device_id=");
		builder.append(last_device_id);
		builder.append(", lastUpdated=");
		builder.append(lastUpdated);
		builder.append(", flag=");
		builder.append(flag);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

}
