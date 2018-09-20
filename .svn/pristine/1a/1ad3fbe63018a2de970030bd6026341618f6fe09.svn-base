package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DevLocationsObject extends PoolObject implements PoolObjectIf, Serializable{

	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference
	private Integer version;
	
	private CopyOnWriteArrayList<LocationList> location_list; // realtime location data
	private LocationList last_report_location; 
	private LocationList last_query_location;
	
	private CopyOnWriteArrayList<LocationList> location_list_to_db; //not using, refer to DevLocationReportObject
	
	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.iana_id = iana_id;
		this.sn = sn;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
	
	public Integer getIana_id() {
		return iana_id;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
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
	
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public LocationList getLast_report_location() {
		return last_report_location;
	}

	public void setLast_report_location(LocationList last_report_location) {
		this.last_report_location = last_report_location;
	}

	public LocationList getLast_query_location() {
		return last_query_location;
	}

	public void setLast_query_location(LocationList last_query_location) {
		this.last_query_location = last_query_location;
	}

	public CopyOnWriteArrayList<LocationList> getLocation_list() {
		return location_list;
	}

	public void setLocation_list(CopyOnWriteArrayList<LocationList> location_list) {
		this.location_list = location_list;
	}

	public CopyOnWriteArrayList<LocationList> getLocation_list_to_db() {
		return location_list_to_db;
	}

	public void setLocation_list_to_db(
			CopyOnWriteArrayList<LocationList> location_list_to_db) {
		this.location_list_to_db = location_list_to_db;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevLocationsObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", sid=");
		builder.append(sid);
		builder.append(", version=");
		builder.append(version);
		builder.append(", location_list=");
		builder.append(location_list);
		builder.append(", last_report_location=");
		builder.append(last_report_location);
		builder.append(", last_query_location=");
		builder.append(last_query_location);
		builder.append(", location_list_to_db=");
		builder.append(location_list_to_db);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
