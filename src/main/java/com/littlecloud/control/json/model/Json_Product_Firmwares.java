package com.littlecloud.control.json.model;

import java.util.List;


public class Json_Product_Firmwares 
{
	private Integer id;
	private Integer network_id;
	private String name;
	private String type;
	private String version;
	private List<Json_Devices> device;
	private List<VersionInfo> version_info;
	private List<SelectVersion> select_version;
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Json_Devices> getDevice() {
		return device;
	}

	public void setDevice(List<Json_Devices> device) {
		this.device = device;
	}
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<VersionInfo> getVersion_info() {
		return version_info;
	}

	public void setVersion_info(List<VersionInfo> version_info) {
		this.version_info = version_info;
	}

	public List<SelectVersion> getSelect_version() {
		return select_version;
	}

	public void setSelect_version(List<SelectVersion> select_version) {
		this.select_version = select_version;
	}



	public class SelectVersion {
		private Integer product_id;
		private Integer device_id;
		private Integer network_id;
		private String custom_version;
		private String version;
		private String url;
		private String release_note;
		private String release_type;
		
		
		public String getCustom_version() {
			return custom_version;
		}
		public void setCustom_version(String custom_version) {
			this.custom_version = custom_version;
		}
		public Integer getProduct_id() {
			return product_id;
		}
		public void setProduct_id(Integer product_id) {
			this.product_id = product_id;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public Integer getDevice_id() {
			return device_id;
		}
		public void setDevice_id(Integer device_id) {
			this.device_id = device_id;
		}
		public Integer getNetwork_id() {
			return network_id;
		}
		public void setNetwork_id(Integer network_id) {
			this.network_id = network_id;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getRelease_note() {
			return release_note;
		}
		public void setRelease_note(String release_note) {
			this.release_note = release_note;
		}
		public String getRelease_type() {
			return release_type;
		}
		public void setRelease_type(String release_type) {
			this.release_type = release_type;
		}
	} // end class VersionInfo
	
	public class VersionInfo
	{
		private String version;
		private String url;
		private String release_note;
		private String release_type;
//		private boolean current;
		
		
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getRelease_note() {
			return release_note;
		}
		public void setRelease_note(String release_note) {
			this.release_note = release_note;
		}
		public String getRelease_type() {
			return release_type;
		}
		public void setRelease_type(String release_type) {
			this.release_type = release_type;
		}
//		public boolean isCurrent() {
//			return current;
//		}
//		public void setCurrent(boolean current) {
//			this.current = current;
//		}
	} // end class VersionInfo
} // end class Json_Product_Firmwares
