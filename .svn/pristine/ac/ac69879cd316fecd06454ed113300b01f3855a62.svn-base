package com.littlecloud.control.json.model.pepvpn;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PepvpnConnection implements Serializable {

	private Long id;
	private String sn;
	private String device_name;
	private Integer device_id;
	private Integer device_network_id;
	private String device_network_name;
	private String main_state;
	private String security_state;
	private String version;
	private String name;
	private List<String> remoteid_list;
	private Boolean enable;
	private List<Integer> conn_list;
	private List<String> network_list;
	private List<Integer> conn_in_use_list;
	private List<Stat> stat_list;
//	private String group_name;
//	private Integer group_id;
	
	public String getDevice_network_name() {
		return device_network_name;
	}

	public void setDevice_network_name(String device_network_name) {
		this.device_network_name = device_network_name;
	}
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
	
	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}
	
	public Integer getDevice_network_id() {
		return device_network_id;
	}

	public void setDevice_network_id(Integer device_network_id) {
		this.device_network_id = device_network_id;
	}

	public String getMain_state() {
		return main_state;
	}

	public void setMain_state(String main_state) {
		this.main_state = main_state;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSecurity_state() {
		return security_state;
	}

	public void setSecurity_state(String security_state) {
		this.security_state = security_state;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getRemoteid_list() {
		return remoteid_list;
	}

	public void setRemoteid_list(List<String> remoteid_list) {
		this.remoteid_list = remoteid_list;
	}

	public Boolean isEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public List<Integer> getConn_list() {
		return conn_list;
	}

	public void setConn_list(List<Integer> conn_list) {
		this.conn_list = conn_list;
	}

	public List<String> getNetwork_list() {
		return network_list;
	}

	public void setNetwork_list(List<String> network_list) {
		this.network_list = network_list;
	}

	public List<Integer> getConn_in_use_list() {
		return conn_in_use_list;
	}

	public void setConn_in_use_list(List<Integer> conn_in_use_list) {
		this.conn_in_use_list = conn_in_use_list;
	}

	public List<Stat> getStat() {
		return stat_list;
	}

	public void setStat(List<Stat> stat) {
		this.stat_list = stat;
	}

	public class Stat implements Serializable {
		private long id;
		private long timestamp;
		private long lost;
		private long rtt;
		private long tx;
		private long rx;
		private Date datetime;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		public long getLost() {
			return lost;
		}

		public void setLost(long lost) {
			this.lost = lost;
		}

		public long getRtt() {
			return rtt;
		}

		public void setRtt(long rtt) {
			this.rtt = rtt;
		}

		public long getTx() {
			return tx;
		}

		public void setTx(long tx) {
			this.tx = tx;
		}

		public long getRx() {
			return rx;
		}

		public void setRx(long rx) {
			this.rx = rx;
		}

		public Date getDatetime() {
			return datetime;
		}

		public void setDatetime(Date datetime) {
			this.datetime = datetime;
		}

		@Override
		public String toString() {
			return "Stat [id=" + id + ", timestamp=" + timestamp + ", lost=" + lost + ", rtt=" + rtt + ", tx=" + tx + ", rx=" + rx + "]";
		}
	}

	@Override
	public String toString() {
		return "PepvpnConnection [id=" + id + ", sn=" + sn + ", device_name=" + device_name + ", device_id=" + device_id + ", device_network_id=" + device_network_id + ", main_state=" + main_state + ", security_state=" + security_state + ", version=" + version + ", name=" + name + ", remoteid_list=" + remoteid_list + ", enable=" + enable + ", conn_list=" + conn_list + ", network_list=" + network_list + ", conn_in_use_list=" + conn_in_use_list + ", stat_list=" + stat_list + "]";
	}

//	public String getGroup_name() {
//		return group_name;
//	}
//
//	public void setGroup_name(String group_name) {
//		this.group_name = group_name;
//	}
//
//	public Integer getGroup_id() {
//		return group_id;
//	}
//
//	public void setGroup_id(Integer group_id) {
//		this.group_id = group_id;
//	}
}
