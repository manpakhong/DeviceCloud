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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "client_usages")
public class ClientUsages extends DBObject implements java.io.Serializable {
	public final static String TYPE_WIRELESS = "wireless";
	public final static String TYPE_LAN = "lan";
	public final static String TYPE_ETHERNET = "ethernet";
	public final static String TYPE_PPTP  = "pptp";
	public final static String TYPE_STATIC_ROUTE = "static_rout";
	private ClientUsagesId id;
	private int networkId;
	private Integer deviceId;
	private String mac;
	private String ip;
	private String name;
	private Float tx;
	private Float rx;
	private Date datetime;
	private String type;

	public ClientUsages() {
		id = new ClientUsagesId();
	}

	public ClientUsages(String idstr) {
		id = new ClientUsagesId(idstr);
	}

	public ClientUsages(int networkId, Integer deviceId, String mac, String ip, String name, Float tx, Float rx, Date datetime, String type, int unixtime) {
		id = new ClientUsagesId();
		id.setUnixtime(unixtime);
		this.networkId = networkId;
		this.deviceId = deviceId;
		this.mac = mac;
		this.ip = ip;
		this.name = name;
		this.tx = tx;
		this.rx = rx;
		this.datetime = datetime;
		this.type = type;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "id", column = @Column(name = "id", nullable = false)),
			@AttributeOverride(name = "unixtime", column = @Column(name = "unixtime", nullable = false)) })
	public ClientUsagesId getId() {
		return this.id;
	}

	public void setId(ClientUsagesId id) {
		this.id = id;
	}

	@Column(name = "network_id", nullable = false)
	public int getNetworkId() {
		return this.networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}

	@Column(name = "device_id")
	public Integer getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	@Column(name = "mac", nullable = false, length = 45)
	public String getMac() {
		return this.mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	@Column(name = "ip", length = 45)
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "name", length = 45)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datetime", length = 19)
	public Date getDatetime() {
		return this.datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	@Column(name = "type", length=20)
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
	
	public String toCSV() {
		return id + "," + networkId
				+ "," + deviceId + "," + mac + "," + ip
				+ "," + name + "," + tx + "," + rx
				+ "," + datetime + "," + id.getUnixtime();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientUsages [id=");
		builder.append(id);
		builder.append(", networkId=");
		builder.append(networkId);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", mac=");
		builder.append(mac);
		builder.append(", ip=");
		builder.append(ip);
		builder.append(", name=");
		builder.append(name);
		builder.append(", tx=");
		builder.append(tx);
		builder.append(", rx=");
		builder.append(rx);
		builder.append(", datetime=");
		builder.append(datetime);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}

}
