package com.littlecloud.control.entity;

// Generated Jul 26, 2013 2:53:06 PM by Hibernate Tools 4.0.0

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.peplink.api.db.DBObject;

/**
 * ConfigurationSsids generated by hbm2java
 */
@Entity
@Table(name = "configuration_ssids"
		)
public class ConfigurationSsids extends DBObject implements java.io.Serializable {

	private ConfigurationSsidsId id;
	private Date timestamp;
	private boolean enabled;
	private boolean ssid_enabled;
	private String ssid;
	private String config;
	private int configVersion;
	//private Set<Tags> tagses = new HashSet<Tags>(0);

	public ConfigurationSsids() {
	}

	public ConfigurationSsids(ConfigurationSsidsId id, String ssid, String config, int configVersion) {
		this.id = id;
		this.ssid = ssid;
		this.config = config;
		this.configVersion = configVersion;
	}

	public ConfigurationSsids(ConfigurationSsidsId id, String ssid, String config, int configVersion, Set<Tags> tagses) {
		this.id = id;
		this.ssid = ssid;
		this.config = config;
		this.configVersion = configVersion;
		//this.tagses = tagses;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "networkId", column = @Column(name = "network_id", nullable = false)),
			@AttributeOverride(name = "deviceId", column = @Column(name = "device_id", nullable = false)),
			@AttributeOverride(name = "ssidId", column = @Column(name = "ssid_id", nullable = false)) })
	public ConfigurationSsidsId getId() {
		return this.id;
	}

	public void setId(ConfigurationSsidsId id) {
		this.id = id;
	}

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "timestamp", nullable = false, length = 19)
	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Column(name = "enabled", nullable = false)
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = "ssid_enabled", nullable = false)
	public boolean isSsid_enabled() {
		return ssid_enabled;
	}

	public void setSsid_enabled(boolean ssid_enabled) {
		this.ssid_enabled = ssid_enabled;
	}
	
	@Column(name = "ssid", nullable = false, length = 100)
	public String getSsid() {
		return this.ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	@Column(name = "config", nullable = false, length = 8000)
	public String getConfig() {
		return this.config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	@Column(name = "config_version", nullable = false)
	public int getConfigVersion() {
		return this.configVersion;
	}

	public void setConfigVersion(int configVersion) {
		this.configVersion = configVersion;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigurationSsids [id=");
		builder.append(id);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", ssid_enabled=");
		builder.append(ssid_enabled);
		builder.append(", ssid=");
		builder.append(ssid);
		builder.append(", config=");
		builder.append(config);
		builder.append(", configVersion=");
		builder.append(configVersion);
		builder.append("]");
		return builder.toString();
	}

//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(name = "tagsxconfiguration_ssids", joinColumns = {
//			@JoinColumn(name = "network_id", nullable = false, updatable = false),
//			@JoinColumn(name = "ssid_id", nullable = false, updatable = false) }, inverseJoinColumns = {
//			@JoinColumn(name = "tag_id", nullable = false, updatable = false) })
//	public Set<Tags> getTagses() {
//		return this.tagses;
//	}
//
//	public void setTagses(Set<Tags> tagses) {
//		this.tagses = tagses;
//	}
}