package com.littlecloud.control.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

@Entity
@Table(name="firmware_versions")
public class FirmwareVersions extends DBObject implements java.io.Serializable
{
	public static final String VERSION_DISABLED = "disabled";
	public static final String VERSION_GROUP = "group";
	public static final String VERSION_CUSTOM = "custom";
	
	private Integer id;
	private Integer device_id;
	private Integer network_id;
	private Integer production_id;
	private String custom_version;
	private String version;
	private String url;
	private String type;
	private Date created_at;
	private Date updated_at;
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	@Column(name="device_id")
	public Integer getDevice_id() {
		return device_id;
	}
	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}
	@Column(name="network_id")
	public Integer getNetwork_id() {
		return network_id;
	}
	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}
	
	@Column(name="product_id")
	public Integer getProduction_id() {
		return production_id;
	}
	public void setProduction_id(Integer production_id) {
		this.production_id = production_id;
	}
	@Column(name="custom_version")
	public String getCustom_Version() {
		return this.custom_version;
	}
	public void setCustom_Version(String custom_version) {
		this.custom_version = custom_version;
	}	
	
	@Column(name="version")
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	@Column(name="url")
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Column(name="type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name="created_at")
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
	@Column(name="updated_at")
	public Date getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FirmwareVersions [id=");
		builder.append(id);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", production_id=");
		builder.append(production_id);
		builder.append(", custom_version=");
		builder.append(custom_version);
		builder.append(", version=");
		builder.append(version);
		builder.append(", url=");
		builder.append(url);
		builder.append(", type=");
		builder.append(type);
		builder.append(", created_at=");
		builder.append(created_at);
		builder.append(", updated_at=");
		builder.append(updated_at);
		builder.append("]");
		return builder.toString();
	}
} // end class
