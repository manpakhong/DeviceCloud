package com.littlecloud.control.entity.report;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "client_ssid_usages")
public class ClientSsidUsages extends DBObject implements java.io.Serializable {
	private ClientSsidUsagesId id;
	private int networkId;
	private Integer deviceId;
	private String mac;
	private String name;
	private String bssid;
	private String essid;
	private String encryption;
	private Boolean active;
	private String type;
	private Date datetime;

	public ClientSsidUsages() {
		id = new ClientSsidUsagesId();
	}

	public ClientSsidUsages(int networkId, Integer deviceId, String mac, String name, String bssid, String essid, String encryption, Boolean active, String type, Date datetime, int unixtime) {
		id = new ClientSsidUsagesId();
		id.setUnixtime(unixtime);
		this.networkId = networkId;
		this.deviceId = deviceId;
		this.mac = mac;
		this.name = name;
		this.bssid = bssid;
		this.essid = essid;
		this.encryption = encryption;
		this.active = active;
		this.type = type;
		this.datetime = datetime;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "id", column = @Column(name = "id", nullable = false)),
			@AttributeOverride(name = "unixtime", column = @Column(name = "unixtime", nullable = false)) })
	public ClientSsidUsagesId getId() {
		return id;
	}

	public void setId(ClientSsidUsagesId id) {
		this.id = id;
	}

	@Column(name = "network_id", nullable = false)
	public int getNetworkId() {
		return networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}

	@Column(name = "device_id")
	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	@Column(name = "mac", nullable = false, length = 45)
	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	@Column(name = "name", length = 100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "bssid", length = 45)
	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	@Column(name = "essid", length = 45)
	public String getEssid() {
		return essid;
	}

	public void setEssid(String essid) {
		this.essid = essid;
	}

	@Column(name = "encryption", length = 45)
	public String getEncryption() {
		return encryption;
	}

	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}

	@Column(name = "active")
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Column(name = "type", length = 45)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datetime", length = 19)
	public Date getDatetime() {
		return this.datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	
	public Integer getUnixtime() {
		return id.getUnixtime();
	}

	public void setUnixtime(Integer unixtime) {
		id.setUnixtime(unixtime);
	}
	
	public String toCSV() {
		return id + "," + networkId
				+ "," + deviceId + "," + mac + "," + name
				+ "," + bssid + "," + essid + ","
				+ encryption + "," + active + "," + type
				+ "," + datetime + "," + id.getUnixtime();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientSsidUsages [id=");
		builder.append(id);
		builder.append(", networkId=");
		builder.append(networkId);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", mac=");
		builder.append(mac);
		builder.append(", name=");
		builder.append(name);
		builder.append(", bssid=");
		builder.append(bssid);
		builder.append(", essid=");
		builder.append(essid);
		builder.append(", encryption=");
		builder.append(encryption);
		builder.append(", active=");
		builder.append(active);
		builder.append(", type=");
		builder.append(type);
		builder.append(", datetime=");
		builder.append(datetime);
		builder.append("]");
		return builder.toString();
	}

}
