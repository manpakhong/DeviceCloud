package com.littlecloud.control.json.request;

import java.util.List;

import com.littlecloud.control.json.JsonRequest;

public class JsonGeoFencesRequest extends JsonRequest{
	private String organization_id;
	private Integer network_id;
	private List<Integer> geo_id_list;
	
	private Integer id;
	private Integer device_id;
	private String zone_name;
	private String type;
	private Integer speed_limit;
    private String device_tag;
    private Boolean email_notify;
    private Integer created_at;
    private Boolean enabled;
	
	private List<JsonGeoFencesRequest_GeoFencePoints> geo_fence_points_list;
	
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDevice_id() {
		return this.device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}

	public String getZone_name() {
		return this.zone_name;
	}

	public void setZone_name(String zone_name) {
		this.zone_name = zone_name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getSpeed_limit() {
		return this.speed_limit;
	}

	public void setSpeed_limit(Integer speed_limit) {
		this.speed_limit = speed_limit;
	}

	public List<JsonGeoFencesRequest_GeoFencePoints> getGeo_fence_points_list() {
		return this.geo_fence_points_list;
	}

	public void setGeo_fence_points_list(
			List<JsonGeoFencesRequest_GeoFencePoints> geo_fence_points_list) {
		this.geo_fence_points_list = geo_fence_points_list;
	}	
	
	public List<Integer> getGeo_id_list() {
		return this.geo_id_list;
	}

	public void setGeo_id_list(List<Integer> geo_id_list) {
		this.geo_id_list = geo_id_list;
	}

	public Integer getNetwork_id() {
		return this.network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}

	public String getOrganization_id() {
		return this.organization_id;
	}

	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}



	public String getDevice_tag() {
		return device_tag;
	}

	public void setDevice_tag(String device_tag) {
		this.device_tag = device_tag;
	}

	public Boolean getEmail_notify() {
		return email_notify;
	}

	public void setEmail_notify(Boolean email_notify) {
		this.email_notify = email_notify;
	}

	public Integer getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Integer created_at) {
		this.created_at = created_at;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isValidRequest() {
		if (this.organization_id == null || this.network_id == null)
		{
			return false;
		}
		return true;
	}

	public class JsonGeoFencesRequest_GeoFencePoints{
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
		public Integer getId() {
			return this.id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public Integer getGeo_id() {
			return this.geo_id;
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
		public Integer getPoint_seq() {
			return this.point_seq;
		}
		public void setPoint_seq(Integer point_seq) {
			this.point_seq = point_seq;
		}
		public Float getLongitude() {
			return this.longitude;
		}
		public void setLongitude(Float longitude) {
			this.longitude = longitude;
		}
		public Float getLatitude() {
			return this.latitude;
		}
		public void setLatitude(Float latitude) {
			this.latitude = latitude;
		}
		
	}// end class JsonGeoFencesRequest_GeoFencePoints
	
} // end class JsonGeoFencesRequest
