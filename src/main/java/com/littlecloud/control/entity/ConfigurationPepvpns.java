package com.littlecloud.control.entity;

// Generated Sep 5, 2013 6:54:27 PM by Hibernate Tools 4.0.0

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

import com.peplink.api.db.DBObject;

/**
 * ConfigurationPepvpns generated by hbm2java
 */
@Entity
@Table(name = "configuration_pepvpns"
		)
public class ConfigurationPepvpns extends DBObject implements java.io.Serializable {

	private int networkId;
	private Date timestamp;
	//private Networks networks;
	//private boolean enable;
	private int hubNetworkId;
	private int hubDeviceId;
	private String config;
	private int configVersion;
	private boolean enabled;
	private String psk;
	private Integer haHubNetworkId;
	private Integer haHubDeviceId;
	private boolean haHubEnabled;

	public ConfigurationPepvpns() {
	}

	public ConfigurationPepvpns(Networks networks, boolean enable, int hubNetworkId, int hubDeviceId, String config, int configVersion, boolean enabled, boolean haHubEnabled) {
		//this.networks = networks;
		this.networkId = networks.getId();
		//this.enable = enable;
		this.hubNetworkId = hubNetworkId;
		this.hubDeviceId = hubDeviceId;
		this.config = config;
		this.configVersion = configVersion;
		this.enabled = enabled;
		this.haHubEnabled = haHubEnabled;
	}

	public ConfigurationPepvpns(Networks networks, boolean enable, int hubNetworkId, int hubDeviceId, String config, int configVersion, boolean enabled, String psk, Integer haHubNetworkId, Integer haHubDeviceId, boolean haHubEnabled) {
		//this.networks = networks;
		this.networkId = networks.getId();
		//this.enable = enable;
		this.hubNetworkId = hubNetworkId;
		this.hubDeviceId = hubDeviceId;
		this.config = config;
		this.configVersion = configVersion;
		this.enabled = enabled;
		this.psk = psk;
		this.haHubNetworkId = haHubNetworkId;
		this.haHubDeviceId = haHubDeviceId;
		this.haHubEnabled = haHubEnabled;
	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "networks"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "network_id", unique = true, nullable = false)
	public int getNetworkId() {
		return this.networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
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

//	@OneToOne(fetch = FetchType.LAZY, optional = false)
//	@NotFound(action=NotFoundAction.IGNORE)
//	@PrimaryKeyJoinColumn
//	public Networks getNetworks() {
//		return this.networks;
//	}
//
//	public void setNetworks(Networks networks) {
//		this.networks = networks;
//	}

//	@Column(name = "enable", nullable = false)
//	public boolean isEnable() {
//		return this.enable;
//	}
//
//	public void setEnable(boolean enable) {
//		this.enable = enable;
//	}

	@Column(name = "hub_network_id", nullable = false)
	public int getHubNetworkId() {
		return this.hubNetworkId;
	}

	public void setHubNetworkId(int hubNetworkId) {
		this.hubNetworkId = hubNetworkId;
	}

	@Column(name = "hub_device_id", nullable = false)
	public int getHubDeviceId() {
		return this.hubDeviceId;
	}

	public void setHubDeviceId(int hubDeviceId) {
		this.hubDeviceId = hubDeviceId;
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

	@Column(name = "enabled", nullable = false)
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = "psk", length = 45)
	public String getPsk() {
		return this.psk;
	}

	public void setPsk(String psk) {
		this.psk = psk;
	}

	@Column(name = "ha_hub_network_id")
	public Integer getHaHubNetworkId() {
		return this.haHubNetworkId;
	}

	public void setHaHubNetworkId(Integer haHubNetworkId) {
		this.haHubNetworkId = haHubNetworkId;
	}

	@Column(name = "ha_hub_device_id")
	public Integer getHaHubDeviceId() {
		return this.haHubDeviceId;
	}

	public void setHaHubDeviceId(Integer haHubDeviceId) {
		this.haHubDeviceId = haHubDeviceId;
	}

	@Column(name = "ha_hub_enabled", nullable = false)
	public boolean isHaHubEnabled() {
		return this.haHubEnabled;
	}

	public void setHaHubEnabled(boolean haHubEnabled) {
		this.haHubEnabled = haHubEnabled;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigurationPepvpns [networkId=");
		builder.append(networkId);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", hubNetworkId=");
		builder.append(hubNetworkId);
		builder.append(", hubDeviceId=");
		builder.append(hubDeviceId);
		builder.append(", config=");
		builder.append(config);
		builder.append(", configVersion=");
		builder.append(configVersion);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", psk=");
		builder.append(psk);
		builder.append(", haHubNetworkId=");
		builder.append(haHubNetworkId);
		builder.append(", haHubDeviceId=");
		builder.append(haHubDeviceId);
		builder.append(", haHubEnabled=");
		builder.append(haHubEnabled);
		builder.append("]");
		return builder.toString();
	}

}
