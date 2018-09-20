package com.littlecloud.control.json.request;

import java.util.Date;
import java.util.List;

import com.littlecloud.control.json.JsonRequest;

public class JsonFirmwareRequest extends JsonRequest
{

	public static final String MODE_NETWORK = "network";
	public static final String MODE_DEVICE = "device";
	public static final String MODE_ORGANIZATION = "organization";
	private String organization_id;
	private Integer network_id;
	private List<DevFirmware> firmware_list;
	private String update_time;
	private Date start_time;
	private Integer minInterval;
	private Integer interval;
	private List<Integer> device_list;
	private String mode;
	
	public class DevFirmware
	{
		
		public static final String STAGE_CUSTOM = "custom";
		public static final String STAGE_DISABLED = "disabled";
		public static final String STAGE_GROUP = "group";
		
		private Integer product_id;
		private String type;	//custom/ga/beta
		private String version; //6.1.0
		private String custom_version;
		private String url;
		public Integer getProduct_id() {
			return product_id;
		}
		public void setProduct_id(Integer product_id) {
			this.product_id = product_id;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
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
		public String getCustom_version() {
			return custom_version;
		}
		public void setCustom_version(String custom_version) {
			this.custom_version = custom_version;
		}
		
	}
	
	public String getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}

	public Integer getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}

	public List<DevFirmware> getFirmware_list() {
		return firmware_list;
	}

	public void setFirmware_list(List<DevFirmware> firmware_list) {
		this.firmware_list = firmware_list;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public Integer getMinInterval() {
		return minInterval;
	}

	public void setMinInterval(Integer minInterval) {
		this.minInterval = minInterval;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer secInterval) {
		this.interval = secInterval;
	}

	public List<Integer> getDevice_list() {
		return device_list;
	}

	public void setDevice_list(List<Integer> device_list) {
		this.device_list = device_list;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
	

	@Override
	public boolean isValidRequest() 
	{
		if (organization_id == null )
		{
			return false;
		}
		return true;
	}

}
