package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;

public class DevUsageObject extends PoolObject implements PoolObjectIf, Serializable{

	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference
	
	private List<UsageList> usage_list;
	
	public class UsageList implements Serializable{
		private String wan_name;
		private Integer id;
		private String ip;
		private Integer timestamp;
		private Float tx;
		private Float rx;
		
		public String getWan_name() {
			return wan_name;
		}
		public void setWan_name(String wan_name) {
			this.wan_name = wan_name;
		}
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public Integer getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Integer timestamp) {
			this.timestamp = timestamp;
		}
		public Float getTx() {
			return tx;
		}
		public void setTx(Float tx) {
			this.tx = tx;
		}
		public Float getRx() {
			return rx;
		}
		public void setRx(Float rx) {
			this.rx = rx;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("UsageList [wan_name=");
			builder.append(wan_name);
			builder.append(", id=");
			builder.append(id);
			builder.append(", ip=");
			builder.append(ip);
			builder.append(", timestamp=");
			builder.append(timestamp);
			builder.append(", tx=");
			builder.append(tx);
			builder.append(", rx=");
			builder.append(rx);
			builder.append("]");
			return builder.toString();
		}		
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

	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}

	public Integer getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}

	public String getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public List<UsageList> getUsage_list() {
		return usage_list;
	}

	public void setUsage_list(List<UsageList> usage_list) {
		this.usage_list = usage_list;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevUsageObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", sid=");
		builder.append(sid);
		builder.append(", usage_list=");
		builder.append(usage_list);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
