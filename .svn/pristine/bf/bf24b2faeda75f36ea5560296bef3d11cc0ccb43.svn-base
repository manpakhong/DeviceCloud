package com.littlecloud.pool.object;

import java.io.Serializable;

import com.littlecloud.control.json.util.JsonUtils;

public class OpModePutObject extends PoolObject implements PoolObjectIf, Serializable {

	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference

	private Integer version;
	private Integer rpt_enable;
	private Integer rpt_offset;
	private Integer rpt_period;
	private Integer evt_period;
	private Integer heartbeat;
	private Integer peer_dead;	
	private Boolean success;




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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getRpt_enable() {
		return rpt_enable;
	}

	public void setRpt_enable(Integer rpt_enable) {
		this.rpt_enable = rpt_enable;
	}

	public Integer getRpt_offset() {
		return rpt_offset;
	}

	public void setRpt_offset(Integer rpt_offset) {
		this.rpt_offset = rpt_offset;
	}

	public Integer getRpt_period() {
		return rpt_period;
	}

	public void setRpt_period(Integer rpt_period) {
		this.rpt_period = rpt_period;
	}

	public Integer getEvt_period() {
		return evt_period;
	}

	public void setEvt_period(Integer evt_period) {
		this.evt_period = evt_period;
	}

	public Integer getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(Integer heartbeat) {
		this.heartbeat = heartbeat;
	}

	public Integer getPeer_dead() {
		return peer_dead;
	}

	public void setPeer_dead(Integer peer_dead) {
		this.peer_dead = peer_dead;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OpModePutObject [sn=");
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
		builder.append(", success=");
		builder.append(success);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

	
}
