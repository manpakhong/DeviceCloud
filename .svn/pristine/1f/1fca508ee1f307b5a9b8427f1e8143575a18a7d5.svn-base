package com.littlecloud.control.json.request;

import java.util.Date;
import java.util.List;

import com.littlecloud.control.json.JsonRequest;
import com.littlecloud.control.json.model.Json_Devices;
import com.littlecloud.control.json.model.Json_Mac_List;
import com.littlecloud.control.json.model.config.JsonConf_Admin;

public class JsonDeviceRequest extends JsonRequest {

	private String organization_id;
	private Integer iana_id;
	private String sn;
	private int device_id;
	private Date start;
	private Date end;
	private String month;
	private int start_num;
	private int limit;
	private List<Json_Mac_List> mac_list;
	private String type;
	private String device_name;
	private int wan_id;
	private Date from_date;
	private List<String> client_id_list;
	private int id;
	private Integer top;
	private Float latitude;
	private Float longitude;
	private String address;
	private Date expiry_date;
	private Boolean ddns_enabled;
	private List<DeviceUpdateInfo> devInfos;
	JsonConf_Admin adminConf;

	// for Indoor Mapping
	private int network_id;
	private List<String> sns;
	private List<Json_Devices> devices;
	/* optional */
	private int count;
	
	@Override
	public boolean isValidRequest() {
		if (organization_id==null || device_id==0)
		{
			return false;
		}
		return true;
	}
	
	public class DeviceUpdateInfo
	{
		private String organization_id;
		private String sn;
		private Integer iana_id;
		private Date expiry_date;
		private Date sub_expiry_date;
		
		public String getOrganization_id() {
			return organization_id;
		}
		public void setOrganization_id(String organization_id) {
			this.organization_id = organization_id;
		}
		public String getSn() {
			return sn;
		}
		public void setSn(String sn) {
			this.sn = sn;
		}
		public Date getExpiry_date() {
			return expiry_date;
		}
		public void setExpiry_date(Date expiry_date) {
			this.expiry_date = expiry_date;
		}
		public Date getSub_expiry_date() {
			return sub_expiry_date;
		}
		public void setSub_expiry_date(Date sub_expiry_date) {
			this.sub_expiry_date = sub_expiry_date;
		}
		public Integer getIana_id() {
			return iana_id;
		}
		public void setIana_id(Integer iana_id) {
			this.iana_id = iana_id;
		}
	}
	
	public Integer getIana_id() {
		return iana_id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}

	public int getNetwork_id() {
		return network_id;
	}
	public void setNetwork_id(int network_id) {
		this.network_id = network_id;
	}

	public List<String> getSns() {
		return sns;
	}
	public List<Json_Devices> getDevices() {
		return devices;
	}
	public void setDevices(List<Json_Devices> devices) {
		this.devices = devices;
	}
	public void setSns(List<String> sns) {
		this.sns = sns;
	}
	public String getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}
	public int getDevice_id() {
		return device_id;
	}
	public void setDevice_id(int device_id) {
		this.device_id = device_id;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public List<Json_Mac_List> getMac_list() {
		return mac_list;
	}

	public void setMac_list(List<Json_Mac_List> mac_list) {
		this.mac_list = mac_list;
	}

	public int getStart_num() {
		return start_num;
	}

	public void setStart_num(int start_num) {
		this.start_num = start_num;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public int getWan_id() {
		return wan_id;
	}

	public void setWan_id(int wan_id) {
		this.wan_id = wan_id;
	}

	public Date getFrom_date() {
		return from_date;
	}

	public void setFrom_date(Date from_date) {
		this.from_date = from_date;
	}

	public List<String> getClient_id_list() {
		return client_id_list;
	}

	public void setClient_id_list(List<String> client_id_list) {
		this.client_id_list = client_id_list;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public Integer getTop() {
		return top;
	}

	public void setTop(Integer top) {
		this.top = top;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getExpiry_date() {
		return expiry_date;
	}

	public void setExpiry_date(Date expiry_date) {
		this.expiry_date = expiry_date;
	}
	public List<DeviceUpdateInfo> getDevInfos() {
		return devInfos;
	}
	public void setDevInfos(List<DeviceUpdateInfo> devInfos) {
		this.devInfos = devInfos;
	}
	public JsonConf_Admin getAdminConf() {
		return adminConf;
	}
	public void setAdminConf(JsonConf_Admin adminConf) {
		this.adminConf = adminConf;
	}
	public Boolean getDdns_enabled() {
		return ddns_enabled;
	}
	public void setDdns_enabled(Boolean ddns_enabled) {
		this.ddns_enabled = ddns_enabled;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonDeviceRequest [organization_id=");
		builder.append(organization_id);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", start=");
		builder.append(start);
		builder.append(", end=");
		builder.append(end);
		builder.append(", month=");
		builder.append(month);
		builder.append(", start_num=");
		builder.append(start_num);
		builder.append(", limit=");
		builder.append(limit);
		builder.append(", mac_list=");
		builder.append(mac_list);
		builder.append(", type=");
		builder.append(type);
		builder.append(", device_name=");
		builder.append(device_name);
		builder.append(", wan_id=");
		builder.append(wan_id);
		builder.append(", from_date=");
		builder.append(from_date);
		builder.append(", client_id_list=");
		builder.append(client_id_list);
		builder.append(", id=");
		builder.append(id);
		builder.append(", top=");
		builder.append(top);
		builder.append(", latitude=");
		builder.append(latitude);
		builder.append(", longitude=");
		builder.append(longitude);
		builder.append(", address=");
		builder.append(address);
		builder.append(", expiry_date=");
		builder.append(expiry_date);
		builder.append(", ddns_enabled=");
		builder.append(ddns_enabled);
		builder.append(", devInfos=");
		builder.append(devInfos);
		builder.append(", adminConf=");
		builder.append(adminConf);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", sns=");
		builder.append(sns);
		builder.append(", devices=");
		builder.append(devices);
		builder.append(", count=");
		builder.append(count);
		builder.append("]");
		return builder.toString();
	}
	
	
}
