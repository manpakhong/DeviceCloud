package com.littlecloud.control.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "configuration_settings"
		)
public class ConfigurationSettings extends DBObject implements java.io.Serializable {

	private ConfigurationSettingsId id;
	private boolean wifi_enabled;
	private boolean follow_network;
	private String extra_settings;
	private String webadmin;

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "networkId", column = @Column(name = "network_id", nullable = false)),
			@AttributeOverride(name = "deviceId", column = @Column(name = "device_id", nullable = false))
			})
	public ConfigurationSettingsId getId() {
		return id;
	}

	public void fillDefaultValues() {
		this.follow_network = true;
		this.wifi_enabled = false;
	}
	
	public void setId(ConfigurationSettingsId id) {
		this.id = id;
	}

	@Column(name = "wifi_enabled", nullable = false)
	public boolean isWifi_enabled() {
		return wifi_enabled;
	}

	public void setWifi_enabled(boolean wifi_enabled) {
		this.wifi_enabled = wifi_enabled;
	}

	@Column(name = "follow_network", nullable = false)
	public boolean isFollow_network() {
		return follow_network;
	}

	public void setFollow_network(boolean follow_network) {
		this.follow_network = follow_network;
	}

	@Column(name = "extra_settings")
	public String getExtra_settings() {
		return extra_settings;
	}

	public void setExtra_settings(String extra_settings) {
		this.extra_settings = extra_settings;
	}
	
	@Column(name = "webadmin")
	public String getWebadmin() {
		return webadmin;
	}

	public void setWebadmin(String webadmin) {
		this.webadmin = webadmin;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigurationSettings [id=");
		builder.append(id);
		builder.append(", wifi_enabled=");
		builder.append(wifi_enabled);
		builder.append(", follow_network=");
		builder.append(follow_network);
		builder.append(", extra_settings=");
		builder.append(extra_settings);
		builder.append(", webadmin=");
		builder.append(webadmin);
		builder.append("]");
		return builder.toString();
	}
}
