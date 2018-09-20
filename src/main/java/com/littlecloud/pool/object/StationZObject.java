package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StationZObject extends PoolObject implements PoolObjectIf, Serializable{

	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference
	
	private CopyOnWriteArrayList<StationMacList> station_mac_list;
	
	private Integer lastUpdateTime;
	private Integer lastRequestUpdateTime;
	
	public class StationMacList implements Serializable{
		private String ip;
		private String mac;
		private String name;
		private String bssid;
		private String essid;
		private String encryption;
		private boolean active;
		private String status;
		private String type;
		private Integer timestamp;
		private Date firstAppear;
		
		//----not from WTP
		private Integer association_time;
		
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getMac() {
			return mac;
		}
		public void setMac(String mac) {
			this.mac = mac;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getBssid() {
			return bssid;
		}
		public void setBssid(String bssid) {
			this.bssid = bssid;
		}
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
		public boolean isActive() {
			return active;
		}
		public void setActive(boolean active) {
			this.active = active;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public Integer getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Integer timestamp) {
			this.timestamp = timestamp;
		}
		public Integer getAssociation_time() {
			return association_time;
		}
		public void setAssociation_time(Integer association_time) {
			this.association_time = association_time;
		}
		public Date getFirstAppear() {
			return firstAppear;
		}
		public void setFirstAppear(Date firstAppear) {
			this.firstAppear = firstAppear;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("StationMacList [ip=");
			builder.append(ip);
			builder.append(", mac=");
			builder.append(mac);
			builder.append(", name=");
			builder.append(name);
			builder.append(", bssid=");
			builder.append(bssid);
			builder.append(", essid=");
			builder.append(essid);
			builder.append(", encryption=");
			builder.append(encryption);
			builder.append(", active=");
			builder.append(active);
			builder.append(", status=");
			builder.append(status);
			builder.append(", type=");
			builder.append(type);
			builder.append(", timestamp=");
			builder.append(timestamp);
			builder.append(", firstAppear=");
			builder.append(firstAppear);
			builder.append(", association_time=");
			builder.append(association_time);
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
	
	public CopyOnWriteArrayList<StationMacList> getStation_mac_list() {
		return station_mac_list;
	}

	public void setStation_mac_list(
			CopyOnWriteArrayList<StationMacList> station_mac_list) {
		this.station_mac_list = station_mac_list;
	}

	public Integer getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Integer lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Integer getLastRequestUpdateTime() {
		return lastRequestUpdateTime;
	}

	public void setLastRequestUpdateTime(Integer lastRequestUpdateTime) {
		this.lastRequestUpdateTime = lastRequestUpdateTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StationZObject [sn=");
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
		builder.append(", station_mac_list=");
		builder.append(station_mac_list);
		builder.append(", lastUpdateTime=");
		builder.append(lastUpdateTime);
		builder.append(", lastRequestUpdateTime=");
		builder.append(lastRequestUpdateTime);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

}
