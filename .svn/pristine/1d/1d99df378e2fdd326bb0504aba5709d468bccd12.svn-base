package com.littlecloud.pool.object;

import java.io.Serializable;

public class ACLicenseObject extends PoolObject implements PoolObjectIf, Serializable {
	private Integer version = 1;
	
	private String enc;
	private Integer expiry_date;
	private Integer license_devices;
	private Integer active_devices;
	private Integer server_time;
	private Integer timestamp;

	/* key */
	private String server;
	private Integer dummy = 0;
	
	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getServer() + "|" + getDummy();
	}

	@Override
	public void setKey(Integer key, String server) {
		this.dummy = key;
		this.server = server;
	}
	
	public void copyLicenseInfo(ACLicenseObject acLic)
	{
		/* for servlet */
		this.expiry_date = acLic.expiry_date;
		this.license_devices = acLic.license_devices;
		this.active_devices = acLic.active_devices;
		this.server_time = acLic.server_time;
		this.timestamp = acLic.timestamp;
	}
	
	public String getEnc() {
		return enc;
	}

	public void setEnc(String enc) {
		this.enc = enc;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}
	
	public Integer getDummy() {
		return dummy;
	}

	public void setDummy(Integer dummy) {
		this.dummy = dummy;
	}

	public Integer getExpiry_date() {
		return expiry_date;
	}

	public void setExpiry_date(Integer expiry_date) {
		this.expiry_date = expiry_date;
	}

	public Integer getLicense_devices() {
		return license_devices;
	}

	public void setLicense_devices(Integer license_devices) {
		this.license_devices = license_devices;
	}

	public Integer getActive_devices() {
		return active_devices;
	}

	public void setActive_devices(Integer active_devices) {
		this.active_devices = active_devices;
	}

	public Integer getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}
	
	public Integer getServer_time() {
		return server_time;
	}

	public void setServer_time(Integer server_time) {
		this.server_time = server_time;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ACLicenseObject [version=");
		builder.append(version);
		builder.append(", enc=");
		builder.append(enc);
		builder.append(", expiry_date=");
		builder.append(expiry_date);
		builder.append(", license_devices=");
		builder.append(license_devices);
		builder.append(", active_devices=");
		builder.append(active_devices);
		builder.append(", server_time=");
		builder.append(server_time);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", server=");
		builder.append(server);
		builder.append(", dummy=");
		builder.append(dummy);
		builder.append("]");
		return builder.toString();
	}
}
