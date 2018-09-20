package com.littlecloud.ac.json.model;

import java.util.List;

import com.littlecloud.pool.object.MacIpList;

public class Json_StationListDelta extends Json_StationList{
	
	/* key */
	//private String sn;

	/* value */
	//private Integer device_id;
	//private Integer network_id;
	//private String organization_id;
	//private String sid; // caller and server reference
	
	//private List<StationStatusList> station_status_list;
	//private List<StationWirelessList> station_wireless_list;
	//private List<IfiList> ifi_list;
	//private Integer timestamp;
	//private Integer status;
	
	private List<MacIpList> del_station_list;

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
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

	public String getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public List<StationStatusList> getStation_status_list() {
		return station_status_list;
	}

	public void setStation_status_list(List<StationStatusList> station_status_list) {
		this.station_status_list = station_status_list;
	}

	public List<StationWirelessList> getStation_wireless_list() {
		return station_wireless_list;
	}

	public void setStation_wireless_list(
			List<StationWirelessList> station_wireless_list) {
		this.station_wireless_list = station_wireless_list;
	}

	public List<IfiList> getIfi_list() {
		return ifi_list;
	}

	public void setIfi_list(List<IfiList> ifi_list) {
		this.ifi_list = ifi_list;
	}

	public Integer getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<MacIpList> getDel_station_list() {
		return del_station_list;
	}

	public void setDel_station_list(List<MacIpList> del_station_list) {
		this.del_station_list = del_station_list;
	}

	@Override
	public String toString() {
		return "Json_StationListDelta [sn=" + sn + ", device_id=" + device_id
				+ ", network_id=" + network_id + ", organization_id="
				+ organization_id + ", sid=" + sid + ", station_status_list="
				+ station_status_list + ", station_wireless_list="
				+ station_wireless_list + ", ifi_list=" + ifi_list
				+ ", timestamp=" + timestamp + ", status=" + status
				+ ", del_station_list=" + del_station_list + "]";
	}
}
