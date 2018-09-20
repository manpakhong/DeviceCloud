package com.littlecloud.control.entity.report;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "daily_device_usages")
public class DailyDeviceUsages extends DBObject implements java.io.Serializable {
	private DailyDeviceUsagesId id;
	private int networkId;
	private int deviceId;
	private Date datetime;
	private Integer wan_id;
	private String wan_name;
	private Float tx;
	private Float rx;

	public DailyDeviceUsages() {
		id = new DailyDeviceUsagesId();
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "id", column = @Column(name = "id", nullable = false)),
			@AttributeOverride(name = "unixtime", column = @Column(name = "unixtime", nullable = false)) })
	public DailyDeviceUsagesId getId() {
		return this.id;
	}

	public void setId(DailyDeviceUsagesId id) {
		this.id = id;
	}
	
	@Column(name = "network_id", nullable = false)
	public int getNetworkId() {
		return this.networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}

	@Column(name = "device_id", nullable = false)
	public int getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datetime", length = 19, columnDefinition="DATETIME")
	public Date getDatetime() {
		return (Date)this.datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	@Column(name = "wan_id")
	public Integer getWan_id() {
		return wan_id;
	}

	public void setWan_id(Integer wan_id) {
		this.wan_id = wan_id;
	}

	@Column(name = "wan_name", length = 40)
	public String getWan_name() {
		return wan_name;
	}

	public void setWan_name(String wan_name) {
		this.wan_name = wan_name;
	}

	@Column(name = "tx", precision = 12, scale = 0)
	public Float getTx() {
		return this.tx;
	}

	public void setTx(Float tx) {
		this.tx = tx;
	}

	@Column(name = "rx", precision = 12, scale = 0)
	public Float getRx() {
		return this.rx;
	}

	public void setRx(Float rx) {
		this.rx = rx;
	}

	public Integer getUnixtime() {
		return id.getUnixtime();
	}

	public void setUnixtime(Integer unixtime) {
		id.setUnixtime(unixtime);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DailyDeviceUsages [id=");
		builder.append(id);
		builder.append(", networkId=");
		builder.append(networkId);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", datetime=");
		builder.append(datetime);
		builder.append(", wan_id=");
		builder.append(wan_id);
		builder.append(", wan_name=");
		builder.append(wan_name);
		builder.append(", tx=");
		builder.append(tx);
		builder.append(", rx=");
		builder.append(rx);
		builder.append("]");
		return builder.toString();
	}

	public String toCSV(){
		return id + ", " + networkId
				+ ", " + deviceId + ", " + datetime
				+ ", " + id.getUnixtime() + ", " + tx + ", " + rx;
		
	}
}
