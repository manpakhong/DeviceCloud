package com.littlecloud.control.entity.branch;

// Generated Jul 26, 2013 2:53:06 PM by Hibernate Tools 4.0.0

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.peplink.api.db.DBObject;

@Embeddable
public class ConfigUpdatesId extends DBObject implements java.io.Serializable {

	private int ianaId;
	private String sn;

	public ConfigUpdatesId() {
		super();
	}

	public ConfigUpdatesId(int ianaId, String sn) {
		super();
		this.ianaId = ianaId;
		this.sn = sn;
	}

	@Column(name = "iana_id", nullable = false)
	public int getIanaId() {
		return ianaId;
	}

	public void setIanaId(int ianaId) {
		this.ianaId = ianaId;
	}

	@Column(name = "sn", nullable = false, length = 32)
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigUpdatesId [ianaId=");
		builder.append(ianaId);
		builder.append(", sn=");
		builder.append(sn);
		builder.append("]");
		return builder.toString();
	}
}
