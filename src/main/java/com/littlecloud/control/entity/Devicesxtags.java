package com.littlecloud.control.entity;

// Generated Oct 4, 2013 5:33:28 PM by Hibernate Tools 4.0.0

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

/**
 * Devicesxtags generated by hbm2java
 */
@Entity
@Table(name = "devicesxtags")
public class Devicesxtags extends DBObject implements java.io.Serializable {

	private DevicesxtagsId id;

	public Devicesxtags() {
	}

	public Devicesxtags(DevicesxtagsId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "tagId", column = @Column(name = "tag_id", nullable = false)),
			@AttributeOverride(name = "deviceId", column = @Column(name = "device_id", nullable = false)) })
	public DevicesxtagsId getId() {
		return this.id;
	}

	public void setId(DevicesxtagsId id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Devicesxtags [id=");
		builder.append(id);
		builder.append("]");
		return builder.toString();
	}
}
