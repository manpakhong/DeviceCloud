package com.littlecloud.control.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.littlecloud.pool.object.DevGeoFencesObject;
import com.peplink.api.db.DBObject;
@Entity
@Table(name = "geo_fences"
		, uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
public class GeoFences extends DBObject implements java.io.Serializable{
	public static final String GEO_FENCES_TYPE_CIRCLE = "circle";
	public static final String GEO_FENCES_TYPE_POLYGON = "polygon";
	public static final String GEO_FENCES_TYPE_ROUTE = "route";	
	public static final String DEVICE_TAG_DELIMITED = ",";
	
	private Integer id;
	private Integer networkId;
	private Integer deviceId;
	private String zoneName;
	private String type;
	private Integer speedLimit;
    private String deviceTag;
    private Boolean emailNotify;
    private Integer createdAt;
    private Boolean enabled;
	
	private List<GeoFencePoints> geoFencePointsList;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "network_id")
	public Integer getNetworkId() {
		return networkId;
	}

	public void setNetworkId(Integer networkId) {
		this.networkId = networkId;
	}

	@Column(name = "zone_name", nullable = false)
	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	
	@Column(name = "type", nullable = false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "speed_limit")
	public Integer getSpeedLimit() {
		return speedLimit;
	}

	public void setSpeedLimit(Integer speedLimit) {
		this.speedLimit = speedLimit;
	}
	
	@Column(name = "device_id")
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public List<GeoFencePoints> getGeoFencePointsList() {
		return geoFencePointsList;
	}

	public void setGeoFencePointsList(List<GeoFencePoints> geoFencePointsList) {
		this.geoFencePointsList = geoFencePointsList;
	}
	@Column(name = "device_tag")
	public String getDeviceTag() {
		return deviceTag;
	}

	public void setDeviceTag(String deviceTag) {
		this.deviceTag = deviceTag;
	}
	@Column(name = "email_notify")
	public Boolean getEmailNotify() {
		return emailNotify;
	}

	public void setEmailNotify(Boolean emailNotify) {
		this.emailNotify = emailNotify;
	}
	@Column(name = "created_at")
	public Integer getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Integer createdAt) {
		this.createdAt = createdAt;
	}
	@Column(name = "enabled")
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeoFences [id=");
		builder.append(id);
		builder.append(", networkId=");
		builder.append(networkId);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", zoneName=");
		builder.append(zoneName);
		builder.append(", type=");
		builder.append(type);
		builder.append(", speedLimit=");
		builder.append(speedLimit);
		builder.append(", deviceTag=");
		builder.append(deviceTag);
		builder.append(", emailNotify=");
		builder.append(emailNotify);
		builder.append(", createdAt=");
		builder.append(createdAt);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", geoFencePointsList=");
		builder.append(geoFencePointsList);
		builder.append("]");
		return builder.toString();
	}
	
} // end class
