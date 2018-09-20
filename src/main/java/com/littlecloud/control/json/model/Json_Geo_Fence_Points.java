package com.littlecloud.control.json.model;

public class Json_Geo_Fence_Points {
	private Integer id;
	private Integer geo_id;
	private Integer point_group_id;
	private Integer point_seq;
	private Float longitude;
	private Float latitude;
	private Integer radius;
	
	public Integer getRadius() {
		return radius;
	}
	public void setRadius(Integer radius) {
		this.radius = radius;
	}
	public Integer getGeo_id() {
		return geo_id;
	}
	public void setGeo_id(Integer geo_id) {
		this.geo_id = geo_id;
	}

	public Integer getPoint_group_id() {
		return point_group_id;
	}
	public void setPoint_group_id(Integer point_group_id) {
		this.point_group_id = point_group_id;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPoint_seq() {
		return point_seq;
	}
	public void setPoint_seq(Integer point_seq) {
		this.point_seq = point_seq;
	}
	public Float getLongitude() {
		return longitude;
	}
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
	public Float getLatitude() {
		return latitude;
	}
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	
	
} // end class
