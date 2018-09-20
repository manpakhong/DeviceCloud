package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DevNeighborListObject extends PoolObject implements PoolObjectIf, Serializable {
	/* key */
	private String sn;
	private Integer iana_id;
	private String sid; // caller and server reference
	private Integer duration;
	private Integer interval;
	
	private CopyOnWriteArrayList<Neighbor> neighborList;
	
	public DevNeighborListObject(Integer iana_id, String sn){
		super();
		neighborList = new CopyOnWriteArrayList<Neighbor>();
		setKey(iana_id, sn);
	}
	
	public DevNeighborListObject(){
		super();
		neighborList = new CopyOnWriteArrayList<Neighbor>();
	}
	
	public void resetNeighborList(){
		if (neighborList != null && neighborList.size() > 0){
			neighborList.clear();
		}
	}
	
	
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.iana_id = iana_id;
		this.sn = sn;
	}
	
	@Override
	public String getKey()
	{
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
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
	
	public CopyOnWriteArrayList<Neighbor> getNeighborList() {
		return neighborList;
	}

	public void setNeighborList(CopyOnWriteArrayList<Neighbor> neighborList) {
		this.neighborList = neighborList;
	}
	public void setNeighborList(List<Neighbor> neighborList) {
		if (neighborList != null && neighborList.size() > 0){
			CopyOnWriteArrayList<Neighbor> cpNeighborList = new CopyOnWriteArrayList<Neighbor>(neighborList);
			this.neighborList = cpNeighborList;
		} else {
			resetNeighborList();
		}
	}
	
	public class Neighbor implements Serializable{
		private Integer last;
		private String mac;
		private String status;
		private Integer signal;
		private String security;
		private String ssid_hex;
		private Integer channel;
		public Integer getLast() {
			return last;
		}
		public void setLast(Integer last) {
			this.last = last;
		}
		public String getMac() {
			return mac;
		}
		public void setMac(String mac) {
			this.mac = mac;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public Integer getSignal() {
			return signal;
		}
		public void setSignal(Integer signal) {
			this.signal = signal;
		}
		public String getSecurity() {
			return security;
		}
		public void setSecurity(String security) {
			this.security = security;
		}
		public String getSsid_hex() {
			return ssid_hex;
		}
		public void setSsid_hex(String ssid_hex) {
			this.ssid_hex = ssid_hex;
		}
		public Integer getChannel() {
			return channel;
		}
		public void setChannel(Integer channel) {
			this.channel = channel;
		}
		
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Neighbor [last=");
			builder.append(last);
			builder.append(", mac=");
			builder.append(mac);
			builder.append(", status=");
			builder.append(status);
			builder.append(", signal=");
			builder.append(signal);
			builder.append(", security=");
			builder.append(security);
			builder.append(", ssid_hex=");
			builder.append(ssid_hex);
			builder.append(", channel=");
			builder.append(channel);
			builder.append("]");
			return builder.toString();
		}
	} // end Neighbor

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevNeighborListObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", sid=");
		builder.append(sid);
		builder.append(", duration=");
		builder.append(duration);
		builder.append(", interval=");
		builder.append(interval);
		builder.append(", neighborList=");
		builder.append(neighborList);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
