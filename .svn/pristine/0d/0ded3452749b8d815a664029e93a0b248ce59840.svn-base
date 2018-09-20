package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StationBandwidthListObject extends PoolObject implements PoolObjectIf, Serializable{
	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference
	
	private CopyOnWriteArrayList<StationStatusList> station_status_list;
	private Integer lastUpdatedTime;
	
	public class StationStatusList implements Serializable{
		private String client_id;
		private String mac;
		private String ip;
		private Float rssi;
		private Float dtx;
		private Float drx;
		private Integer timestamp;
		
		public String getMac() {
			return mac;
		}
		public void setMac(String mac) {
			this.mac = mac;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public Float getRssi() {
			return rssi;
		}
		public void setRssi(Float rssi) {
			this.rssi = rssi;
		}
		public Float getDtx() {
			return dtx;
		}
		public void setDtx(Float dtx) {
			this.dtx = dtx;
		}
		public Float getDrx() {
			return drx;
		}
		public void setDrx(Float drx) {
			this.drx = drx;
		}
		public Integer getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Integer timestamp) {
			this.timestamp = timestamp;
		}
		public String getClient_id() {
			return client_id;
		}
		public void setClient_id(String client_id) {
			this.client_id = client_id;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("StationStatusList [client_id=");
			builder.append(client_id);
			builder.append(", mac=");
			builder.append(mac);
			builder.append(", ip=");
			builder.append(ip);
			builder.append(", rssi=");
			builder.append(rssi);
			builder.append(", dtx=");
			builder.append(dtx);
			builder.append(", drx=");
			builder.append(drx);
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

	public CopyOnWriteArrayList<StationStatusList> getStation_status_list() {
		return station_status_list;
	}

	public void setStation_status_list(
			CopyOnWriteArrayList<StationStatusList> station_status_list) {
		this.station_status_list = station_status_list;
	}

	public Integer getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(Integer lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StationBandwidthListObject [sn=");
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
		builder.append(", station_status_list=");
		builder.append(station_status_list);
		builder.append(", lastUpdatedTime=");
		builder.append(lastUpdatedTime);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

}
