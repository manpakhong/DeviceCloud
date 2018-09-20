package com.littlecloud.pool.object;

import java.io.Serializable;

public class SetTimerObject extends PoolObject implements PoolObjectIf, Serializable
{
	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private String version;
	private boolean rpt_enable;
	private boolean rpt_offset;
	private boolean rpt_period;
	private boolean evt_period;
	private boolean heartbeat;
	private boolean peer_dead;
	private boolean gps_sample;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public boolean isEnable() {
		return rpt_enable;
	}
	public void setEnable(boolean rpt_enable) {
		this.rpt_enable = rpt_enable;
	}
	public boolean isRpt_enable() {
		return rpt_enable;
	}
	public void setRpt_enable(boolean rpt_enable) {
		this.rpt_enable = rpt_enable;
	}
	public boolean isRpt_offset() {
		return rpt_offset;
	}
	public void setRpt_offset(boolean rpt_offset) {
		this.rpt_offset = rpt_offset;
	}
	public boolean isRpt_period() {
		return rpt_period;
	}
	public void setRpt_period(boolean rpt_period) {
		this.rpt_period = rpt_period;
	}
	public boolean isEvt_period() {
		return evt_period;
	}
	public void setEvt_period(boolean evt_period) {
		this.evt_period = evt_period;
	}
	public boolean isHeartbeat() {
		return heartbeat;
	}
	public void setHeartbeat(boolean heartbeat) {
		this.heartbeat = heartbeat;
	}
	public boolean isPeer_dead() {
		return peer_dead;
	}
	public void setPeer_dead(boolean peer_dead) {
		this.peer_dead = peer_dead;
	}
	public boolean isGps_sample() {
		return gps_sample;
	}
	public void setGps_sample(boolean gps_sample) {
		this.gps_sample = gps_sample;
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
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SetTimerObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", version=");
		builder.append(version);
		builder.append(", rpt_enable=");
		builder.append(rpt_enable);
		builder.append(", rpt_offset=");
		builder.append(rpt_offset);
		builder.append(", rpt_period=");
		builder.append(rpt_period);
		builder.append(", evt_period=");
		builder.append(evt_period);
		builder.append(", heartbeat=");
		builder.append(heartbeat);
		builder.append(", peer_dead=");
		builder.append(peer_dead);
		builder.append(", gps_sample=");
		builder.append(gps_sample);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
