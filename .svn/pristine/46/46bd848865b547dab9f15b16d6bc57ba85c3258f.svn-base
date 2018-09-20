package com.littlecloud.control.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "geo_fence_points"
		, uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
public class GeoFencePoints extends DBObject implements Serializable {
	private Integer id;
	private Integer geoId;
	private Integer pointGroupId;
	private Integer pointSeq;
	private Float longitude;
	private Float latitude;
	private Integer radius;

	@Id	
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", nullable = false)	
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "geo_id", nullable = false)
	public Integer getGeoId() {
		return this.geoId;
	}
	public void setGeoId(Integer geoId) {
		this.geoId = geoId;
	}

	@Column(name = "point_group_id", nullable = false)
	public Integer getPointGroupId() {
		return pointGroupId;
	}
	public void setPointGroupId(Integer pointGroupId) {
		this.pointGroupId = pointGroupId;
	}
	@Column(name = "point_seq", nullable = false)
	public Integer getPointSeq() {
		return this.pointSeq;
	}
	public void setPointSeq(Integer pointSeq) {
		this.pointSeq = pointSeq;
	}
	@Column(name = "longitude", nullable = false)
	public Float getLongitude() {
		return this.longitude;
	}
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
	@Column(name = "latitude", nullable = false)
	public Float getLatitude() {
		return this.latitude;
	}
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	
	@Column(name = "radius")
	public Integer getRadius() {
		return radius;
	}
	public void setRadius(Integer radius) {
		this.radius = radius;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeoFencePoints [id=");
		builder.append(id);
		builder.append(", geoId=");
		builder.append(geoId);
		builder.append(", pointGroupId=");
		builder.append(pointGroupId);
		builder.append(", pointSeq=");
		builder.append(pointSeq);
		builder.append(", longitude=");
		builder.append(longitude);
		builder.append(", latitude=");
		builder.append(latitude);
		builder.append(", radius=");
		builder.append(radius);
		builder.append("]");
		return builder.toString();
	}
	
} // end class
