package com.littlecloud.control.entity;

import javax.persistence.Column;

import com.peplink.api.db.DBObject;

public class ConfigurationSettingsId extends DBObject implements java.io.Serializable {
	private int networkId;
	private int deviceId;

	public ConfigurationSettingsId() {
	}
	
	public ConfigurationSettingsId(int networkId, int deviceId) {
		super();
		this.networkId = networkId;
		this.deviceId = deviceId;
	}

	@Column(name = "network_id", nullable = false)
	public int getNetworkId() {
		return networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}

	@Column(name = "device_id", nullable = false)
	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + deviceId;
		result = prime * result + networkId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigurationSettingsId other = (ConfigurationSettingsId) obj;
		
		if (deviceId != other.deviceId)
			return false;
		if (networkId != other.networkId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigurationSettingsId [networkId=");
		builder.append(networkId);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append("]");
		return builder.toString();
	}
}
