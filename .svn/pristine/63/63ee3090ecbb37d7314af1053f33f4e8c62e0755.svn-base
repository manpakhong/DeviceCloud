package com.littlecloud.pool.object;

import java.io.Serializable;
//import java.util.List;
import java.util.ArrayList;
//import java.util.concurrent.CopyOnWriteArrayList;

public class StationUsageObject extends PoolObject implements PoolObjectIf, Serializable{

	/* key */
	private Integer iana_id;
	private String sn;
	/* value */
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference
	
//	private CopyOnWriteArrayList<StationUsageList> station_list;
	private ArrayList<StationUsageList> station_list;
	
	public class StationUsageList implements Serializable{
		public final static String TYPE_WIRELESS = "wireless";
		public final static String TYPE_LAN = "lan";
		public final static String TYPE_ETHERNET = "ethernet";
		public final static String TYPE_PPTP  = "pptp";
		public final static String TYPE_STATIC_ROUTE = "static_rout";
		private String mac;
		private String ip;
		private String name;
		private String type;
		private Float tx;
		private Float rx;
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
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
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
			builder.append("StationUsageList [mac=");
			builder.append(mac);
			builder.append(", ip=");
			builder.append(ip);
			builder.append(", name=");
			builder.append(name);
			builder.append(", type=");
			builder.append(type);
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

//	public CopyOnWriteArrayList<StationUsageList> getStation_list() {
	public ArrayList<StationUsageList> getStation_list() {
		return station_list;
	}

//	public void setStation_list(CopyOnWriteArrayList<StationUsageList> station_list) {
	public void setStation_list(ArrayList<StationUsageList> station_list) {
		this.station_list = station_list;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StationUsageObject [iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", sid=");
		builder.append(sid);
		builder.append(", station_list=");
		builder.append(station_list);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
