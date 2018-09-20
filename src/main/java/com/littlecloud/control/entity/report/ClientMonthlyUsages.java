package com.littlecloud.control.entity.report;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.peplink.api.db.DBObject;

@Entity
@Table(name="client_monthly_usages")
public class ClientMonthlyUsages extends DBObject implements java.io.Serializable 
{
	private ClientMonthlyUsagesId id;
	private int networkId;
	private int deviceId;
	private Date datetime;
	private String ip;
	private String mac;
	private Float rx;
	private Float tx;
	private String type;
	
	public ClientMonthlyUsages() {
		id = new ClientMonthlyUsagesId();
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "id", column = @Column(name = "id", nullable = false)),
			@AttributeOverride(name = "unixtime", column = @Column(name = "unixtime", nullable = false)) })
	public ClientMonthlyUsagesId getId() {
		return id;
	}
	public void setId(ClientMonthlyUsagesId id) {
		this.id = id;
	}
	
	@Column(name="network_id", length=10)
	public int getNetworkId() {
		return networkId;
	}
	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}
	
	@Column(name="device_id", length=11)
	public int getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	
	@Column(name="datetime")
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	
	@Column(name="ip", length=32)
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	@Column(name="mac", length=32)
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	
	@Column(name="rx")
	public Float getRx() {
		return rx;
	}
	public void setRx(Float rx) {
		this.rx = rx;
	}
	
	@Column(name="tx")
	public Float getTx() {
		return tx;
	}
	public void setTx(Float tx) {
		this.tx = tx;
	}
	
	@Column(name="type", length=20)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
		builder.append("ClientMonthlyUsages [id=");
		builder.append(id);
		builder.append(", networkId=");
		builder.append(networkId);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", datetime=");
		builder.append(datetime);
		builder.append(", ip=");
		builder.append(ip);
		builder.append(", mac=");
		builder.append(mac);
		builder.append(", rx=");
		builder.append(rx);
		builder.append(", tx=");
		builder.append(tx);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}
	
}