package com.littlecloud.control.entity.report;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "device_monthly_usages")
public class DeviceMonthlyUsages extends DBObject implements java.io.Serializable
{
	private DeviceMonthlyUsagesId id;
	private Integer network_id;
	private Integer device_id;
	private Date datetime;
	private Integer wan_id;
	private String wan_name;
	private Float tx;
	private Float rx;

	public DeviceMonthlyUsages() {
		id = new DeviceMonthlyUsagesId();
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "id", column = @Column(name = "id", nullable = false)),
			@AttributeOverride(name = "unixtime", column = @Column(name = "unixtime", nullable = false)) })
	public DeviceMonthlyUsagesId getId() {
		return this.id;
	}

	public void setId(DeviceMonthlyUsagesId id) {
		this.id = id;
	}

	@Column(name = "network_id", nullable = false)
	public Integer getNetwork_id() {
		return network_id;
	}
	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}
	
	@Column(name = "device_id", nullable = false)
	public Integer getDevice_id() {
		return device_id;
	}
	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}
	
	@Column(name = "datetime", length = 19, columnDefinition="DATETIME")
	public Date getDatetime() {
		return datetime;
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
		return tx;
	}
	public void setTx(Float tx) {
		this.tx = tx;
	}
	
	@Column(name = "rx", precision = 12, scale = 0)
	public Float getRx() {
		return rx;
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
		builder.append("DeviceMonthlyUsages [id=");
		builder.append(id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", device_id=");
		builder.append(device_id);
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
	
}
