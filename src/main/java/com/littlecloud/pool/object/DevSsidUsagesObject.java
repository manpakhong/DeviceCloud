package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DevSsidUsagesObject extends PoolObject implements PoolObjectIf, Serializable{

	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference
	
	private CopyOnWriteArrayList<VapStatList> vap_stat_list;
	
	public class VapStatList{
		private String essid;
		private String encryption;
		private Float tx;
		private Float rx;
		private Integer timestamp;
		public String getEssid() {
			return essid;
		}
		public void setEssid(String essid) {
			this.essid = essid;
		}
		public String getEncryption() {
			return encryption;
		}
		public void setEncryption(String encryption) {
			this.encryption = encryption;
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
		public Integer getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Integer timestamp) {
			this.timestamp = timestamp;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("VapStatList [essid=");
			builder.append(essid);
			builder.append(", encryption=");
			builder.append(encryption);
			builder.append(", tx=");
			builder.append(tx);
			builder.append(", rx=");
			builder.append(rx);
			builder.append(", timestamp=");
			builder.append(timestamp);
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
	
	public CopyOnWriteArrayList<VapStatList> getVap_stat_list() {
		return vap_stat_list;
	}
	public void setVap_stat_list(CopyOnWriteArrayList<VapStatList> vap_stat_list) {
		this.vap_stat_list = vap_stat_list;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevSsidUsagesObject [sn=");
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
		builder.append(", vap_stat_list=");
		builder.append(vap_stat_list);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}	
}
