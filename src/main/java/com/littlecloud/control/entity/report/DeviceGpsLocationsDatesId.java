package com.littlecloud.control.entity.report;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DeviceGpsLocationsDatesId implements java.io.Serializable {

	private int networkId;
	private int deviceId;
	private int unixtime;

	public DeviceGpsLocationsDatesId() {
	}

	public DeviceGpsLocationsDatesId(int networkId, int deviceId, int unixtime) {
		this.networkId = networkId;
		this.deviceId = deviceId;
		this.unixtime = unixtime;
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

	@Column(name = "unixtime", nullable = false)
	public int getUnixtime() {
		return this.unixtime;
	}

	public void setUnixtime(int unixtime) {
		this.unixtime = unixtime;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof DeviceGpsLocationsDatesId))
			return false;
		DeviceGpsLocationsDatesId castOther = (DeviceGpsLocationsDatesId) other;

		return (this.getNetworkId() == castOther.getNetworkId())
				&& (this.getDeviceId() == castOther.getDeviceId())
				&& (this.getUnixtime() == castOther.getUnixtime());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getNetworkId();
		result = 37 * result + this.getDeviceId();
		result = 37 * result + this.getUnixtime();
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceGpsLocationsDatesId [networkId=");
		builder.append(networkId);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", unixtime=");
		builder.append(unixtime);
		builder.append("]");
		return builder.toString();
	}

}
