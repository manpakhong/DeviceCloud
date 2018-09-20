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
@Table(name = "device_ssid_usages")
public class DeviceSsidUsages extends DBObject implements java.io.Serializable {
	private DeviceSsidUsagesId id;
	private int networkId;
	private int deviceId;
	private Date datetime;
	private String essid;
	private String encryption;
	private float tx;
	private float rx;

	public DeviceSsidUsages() {
		id = new DeviceSsidUsagesId();
	}

	public DeviceSsidUsages(int networkId, int deviceId, Date datetime, String essid, String encryption, float tx, float rx, int unixtime) {
		id = new DeviceSsidUsagesId();
		id.setUnixtime(unixtime);
		this.networkId = networkId;
		this.deviceId = deviceId;
		this.datetime = datetime;
		this.essid = essid;
		this.encryption = encryption;
		this.tx = tx;
		this.rx = rx;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "id", column = @Column(name = "id", nullable = false)),
			@AttributeOverride(name = "unixtime", column = @Column(name = "unixtime", nullable = false)) })
	public DeviceSsidUsagesId getId() {
		return this.id;
	}

	public void setId(DeviceSsidUsagesId id) {
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
	@Column(name = "datetime", nullable = false, length = 19)
	public Date getDatetime() {
		return this.datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	@Column(name = "essid", nullable = false, length = 45)
	public String getEssid() {
		return this.essid;
	}

	public void setEssid(String essid) {
		this.essid = essid;
	}

	@Column(name = "encryption", nullable = false, length = 45)
	public String getEncryption() {
		return this.encryption;
	}

	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}

	@Column(name = "tx", nullable = false, precision = 12, scale = 0)
	public float getTx() {
		return this.tx;
	}

	public void setTx(float tx) {
		this.tx = tx;
	}

	@Column(name = "rx", nullable = false, precision = 12, scale = 0)
	public float getRx() {
		return this.rx;
	}

	public void setRx(float rx) {
		this.rx = rx;
	}

	public Integer getUnixtime() {
		return id.getUnixtime();
	}

	public void setUnixtime(Integer unixtime) {
		id.setUnixtime(unixtime);
	}
	
	public String toCSV() {
		return id + "," + networkId
				+ "," + deviceId + "," + datetime
				+ "," + id.getUnixtime() + "," + essid
				+ "," + encryption + "," + tx + "," + rx;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceSsidUsages [id=");
		builder.append(id);
		builder.append(", networkId=");
		builder.append(networkId);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", datetime=");
		builder.append(datetime);
		builder.append(", essid=");
		builder.append(essid);
		builder.append(", encryption=");
		builder.append(encryption);
		builder.append(", tx=");
		builder.append(tx);
		builder.append(", rx=");
		builder.append(rx);
		builder.append("]");
		return builder.toString();
	}

}
