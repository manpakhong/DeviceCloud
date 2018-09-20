package com.littlecloud.ac.json.model;

import java.util.List;

public class Json_StationList {
	
	/* key */
	protected String sn;

	/* value */
	protected Integer device_id;
	protected Integer network_id;
	protected String organization_id;
	protected String sid; // caller and server reference
	
	protected List<StationStatusList> station_status_list;
	protected List<StationWirelessList> station_wireless_list;
	protected List<IfiList> ifi_list;
	protected Integer timestamp;
	protected Integer status;
	
	public class StationStatusList{
		private String mac;
		private String ip;
		private String name;
		private String status;
		private String type;
//		private Float dtx;
//		private Float drx;
		
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
//		public Float getDtx() {
//			return dtx;
//		}
//		public void setDtx(Float dtx) {
//			this.dtx = dtx;
//		}
//		public Float getDrx() {
//			return drx;
//		}
//		public void setDrx(Float drx) {
//			this.drx = drx;
//		}
		@Override
		public String toString() {
			return "StationStatusList [mac=" + mac + ", ip=" + ip + ", name="
					+ name + ", status=" + status + ", type=" + type + "]";
		}
		
	}
	
	public class StationWirelessList{
		private String mac;
		private String ip;
		private String status;
		private String radio_mode;
		private Integer conn_len;
		private Float rssi;
		private Float tx;
		private Float rx;
		private Integer ifi_id;
		
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
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getRadio_mode() {
			return radio_mode;
		}
		public void setRadio_mode(String radio_mode) {
			this.radio_mode = radio_mode;
		}
		public Integer getConn_len() {
			return conn_len;
		}
		public void setConn_len(Integer conn_len) {
			this.conn_len = conn_len;
		}
		public Float getRssi() {
			return rssi;
		}
		public void setRssi(Float rssi) {
			this.rssi = rssi;
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
		public Integer getIfi_id() {
			return ifi_id;
		}
		public void setIfi_id(Integer ifi_id) {
			this.ifi_id = ifi_id;
		}
		@Override
		public String toString() {
			return "StationWirelessList [mac=" + mac + ", ip=" + ip
					+ ", status=" + status + ", radio_mode=" + radio_mode
					+ ", conn_len=" + conn_len + ", rssi=" + rssi + ", tx="
					+ tx + ", rx=" + rx + ", ifi_id=" + ifi_id + "]";
		}	
		
		
	}
	
	public class IfiList{
		private int ifi_id;		
		private String bssid;
		private String essid;
		private String security;
		private Integer channel;		
		private Float ch_width;
		
		public int getIfi_id() {
			return ifi_id;
		}
		public void setIfi_id(int ifi_id) {
			this.ifi_id = ifi_id;
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
		public String getSecurity() {
			return security;
		}
		public void setSecurity(String security) {
			this.security = security;
		}
		public Integer getChannel() {
			return channel;
		}
		public void setChannel(Integer channel) {
			this.channel = channel;
		}
		public Float getCh_width() {
			return ch_width;
		}
		public void setCh_width(Float ch_width) {
			this.ch_width = ch_width;
		}
		@Override
		public String toString() {
			return "IfiList [ifi_id=" + ifi_id + ", bssid=" + bssid
					+ ", essid=" + essid + ", security=" + security
					+ ", channel=" + channel + ", ch_width=" + ch_width + "]";
		}	
		
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
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

	public List<StationStatusList> getStation_status_list() {
		return station_status_list;
	}

	public void setStation_status_list(List<StationStatusList> station_status_list) {
		this.station_status_list = station_status_list;
	}

	public List<StationWirelessList> getStation_wireless_list() {
		return station_wireless_list;
	}

	public void setStation_wireless_list(
			List<StationWirelessList> station_wireless_list) {
		this.station_wireless_list = station_wireless_list;
	}

	

	public List<IfiList> getIfi_list() {
		return ifi_list;
	}

	public void setIfi_list(List<IfiList> ifi_list) {
		this.ifi_list = ifi_list;
	}

	public Integer getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Json_StationList [sn=" + sn + ", device_id=" + device_id
				+ ", network_id=" + network_id + ", organization_id="
				+ organization_id + ", sid=" + sid + ", station_status_list="
				+ station_status_list + ", station_wireless_list="
				+ station_wireless_list + ", ifi_list=" + ifi_list
				+ ", timestamp=" + timestamp + ", status=" + status + "]";
	}
}
