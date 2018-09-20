package com.littlecloud.control.entity;

// Generated Jul 26, 2013 2:53:06 PM by Hibernate Tools 4.0.0

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.peplink.api.db.DBObject;

/**
 * ConfigurationRadioChannelsId generated by hbm2java
 */
@Embeddable
public class ConfigurationRadiosId extends DBObject implements java.io.Serializable {

	private int networkId;
	private int deviceId;	

	public ConfigurationRadiosId() {
	}

	public ConfigurationRadiosId(int networkId, int deviceId) {
		super();
		this.networkId = networkId;
		this.deviceId = deviceId;
	}

	@Column(name = "device_id", nullable = false)
	public int getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	@Column(name = "network_id", nullable = false)
	public int getNetworkId() {
		return networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}	

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ConfigurationRadiosId))
			return false;
		ConfigurationRadiosId castOther = (ConfigurationRadiosId) other;

		return (this.getDeviceId() == castOther.getDeviceId())
				&& (this.getNetworkId() == castOther.getNetworkId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getDeviceId();
		result = 37 * result + this.getNetworkId();
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigurationRadiosId [networkId=");
		builder.append(networkId);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append("]");
		return builder.toString();
	}

}