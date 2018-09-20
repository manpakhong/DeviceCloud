package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DevBandwidthObject extends PoolObject implements PoolObjectIf, Serializable{
	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference
	
	private CopyOnWriteArrayList<BandwidthList> bandwidth_list;
//	private StationZ stationz;
	private Lifetime lifetime;
	private boolean fromWTPOrNot;
	private long lastUpdateTime;
	private Integer interval;
	private Integer duration;
	
	public class BandwidthList implements Serializable{
		private String wan_name;
		private Float dtx;
		private Float drx;
		private Integer timestamp;
		
		public String getWan_name() {
			return wan_name;
		}
		public void setWan_name(String wan_name) {
			this.wan_name = wan_name;
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
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("BandwidthList [wan_name=");
			builder.append(wan_name);
			builder.append(", dtx=");
			builder.append(dtx);
			builder.append(", drx=");
			builder.append(drx);
			builder.append(", timestamp=");
			builder.append(timestamp);
			builder.append(", getClass()=");
			builder.append(getClass());
			builder.append(", hashCode()=");
			builder.append(hashCode());
			builder.append(", toString()=");
			builder.append(super.toString());
			builder.append("]");
			return builder.toString();
		}		
	}

//	public class StationZ implements Serializable{
//		private Integer total;
//		private Integer eth;
//		private Integer wlan;
//		private Integer mvpn;
//		private Integer stat_ro;
//		private Integer pptp;
//		private Integer others;
//		public Integer getTotal() {
//			return total;
//		}
//		public void setTotal(Integer total) {
//			this.total = total;
//		}
//		public Integer getEth() {
//			return eth;
//		}
//		public void setEth(Integer eth) {
//			this.eth = eth;
//		}
//		public Integer getWlan() {
//			return wlan;
//		}
//		public void setWlan(Integer wlan) {
//			this.wlan = wlan;
//		}
//		public Integer getMvpn() {
//			return mvpn;
//		}
//		public void setMvpn(Integer mvpn) {
//			this.mvpn = mvpn;
//		}
//		public Integer getStat_ro() {
//			return stat_ro;
//		}
//		public void setStat_ro(Integer stat_ro) {
//			this.stat_ro = stat_ro;
//		}
//		public Integer getPptp() {
//			return pptp;
//		}
//		public void setPptp(Integer pptp) {
//			this.pptp = pptp;
//		}
//		public Integer getOthers() {
//			return others;
//		}
//		public void setOthers(Integer others) {
//			this.others = others;
//		}
//		@Override
//		public String toString() {
//			return "StationZ [total=" + total + ", eth=" + eth + ", wlan="
//					+ wlan + ", mvpn=" + mvpn + ", stat_ro=" + stat_ro
//					+ ", pptp=" + pptp + ", others=" + others + "]";
//		}
//	}
	
	public class Lifetime implements Serializable
	{
		private Integer tx;
		private Integer rx;
		private long timestamp;
		private String unit;
		
		public Integer getTx() {
			return tx;
		}
		public void setTx(Integer tx) {
			this.tx = tx;
		}
		public Integer getRx() {
			return rx;
		}
		public void setRx(Integer rx) {
			this.rx = rx;
		}
		public long getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Lifetime [tx=");
			builder.append(tx);
			builder.append(", rx=");
			builder.append(rx);
			builder.append(", timestamp=");
			builder.append(timestamp);
			builder.append(", unit=");
			builder.append(unit);
			builder.append(", getClass()=");
			builder.append(getClass());
			builder.append(", hashCode()=");
			builder.append(hashCode());
			builder.append(", toString()=");
			builder.append(super.toString());
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

	public CopyOnWriteArrayList<BandwidthList> getBandwidth_list() {
		return bandwidth_list;
	}

	public void setBandwidth_list(CopyOnWriteArrayList<BandwidthList> bandwidth_list) {
		this.bandwidth_list = bandwidth_list;
	}

//	public StationZ getStationz() {
//		return stationz;
//	}
//
//	public void setStationz(StationZ stationz) {
//		this.stationz = stationz;
//	}

	public Lifetime getLifetime() {
		return lifetime;
	}

	public void setLifetime(Lifetime lifetime) {
		this.lifetime = lifetime;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public boolean isFromWTPOrNot() {
		return fromWTPOrNot;
	}

	public void setFromWTPOrNot(boolean fromWTPOrNot) {
		this.fromWTPOrNot = fromWTPOrNot;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevBandwidthObject [sn=");
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
		builder.append(", bandwidth_list=");
		builder.append(bandwidth_list);
		builder.append(", lifetime=");
		builder.append(lifetime);
		builder.append(", fromWTPOrNot=");
		builder.append(fromWTPOrNot);
		builder.append(", lastUpdateTime=");
		builder.append(lastUpdateTime);
		builder.append(", interval=");
		builder.append(interval);
		builder.append(", duration=");
		builder.append(duration);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

}
