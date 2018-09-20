package com.littlecloud.control.entity;

// Generated Oct 4, 2013 4:04:40 PM by Hibernate Tools 4.0.0

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.peplink.api.db.DBObject;

/**
 * TagsxconfigurationSsidsId generated by hbm2java
 */
@Embeddable
public class TagsxconfigurationSsidsId extends DBObject implements java.io.Serializable {

	private long tagId;
	private int networkId;
	private int ssidId;

	public TagsxconfigurationSsidsId() {
	}

	public TagsxconfigurationSsidsId(long tagId, int networkId, int ssidId) {
		this.tagId = tagId;
		this.networkId = networkId;
		this.ssidId = ssidId;
	}

	@Column(name = "tag_id", nullable = false)
	public long getTagId() {
		return this.tagId;
	}

	public void setTagId(long tagId) {
		this.tagId = tagId;
	}

	@Column(name = "network_id", nullable = false)
	public int getNetworkId() {
		return this.networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}

	@Column(name = "ssid_id", nullable = false)
	public int getSsidId() {
		return this.ssidId;
	}

	public void setSsidId(int ssidId) {
		this.ssidId = ssidId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TagsxconfigurationSsidsId))
			return false;
		TagsxconfigurationSsidsId castOther = (TagsxconfigurationSsidsId) other;

		return (this.getTagId() == castOther.getTagId())
				&& (this.getNetworkId() == castOther.getNetworkId())
				&& (this.getSsidId() == castOther.getSsidId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (int) this.getTagId();
		result = 37 * result + this.getNetworkId();
		result = 37 * result + this.getSsidId();
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TagsxconfigurationSsidsId [tagId=");
		builder.append(tagId);
		builder.append(", networkId=");
		builder.append(networkId);
		builder.append(", ssidId=");
		builder.append(ssidId);
		builder.append("]");
		return builder.toString();
	}
}
