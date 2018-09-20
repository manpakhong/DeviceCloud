package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;

public class StationListObject extends PoolObject implements PoolObjectIf, Serializable {
	
	public static Logger log = Logger.getLogger(StationListObject.class);	
	
	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference

	private Integer totalClient;
	private Integer totalOnlineClient;
	
	private  CopyOnWriteArrayList<StationList> station_list;

	private Integer timestamp;

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

	public Integer getTotalClient() {
		return totalClient;
	}

	public void setTotalClient(Integer totalClient) {
		this.totalClient = totalClient;
	}

	public Integer getTotalOnlineClient() {
		return totalOnlineClient;
	}

	public void setTotalOnlineClient(Integer totalOnlineClient) {
		this.totalOnlineClient = totalOnlineClient;
	}

	public List<StationList> getStation_list() {
		return station_list;
	}

	public void setStation_list(List<StationList> station_list) {
		CopyOnWriteArrayList <StationList> cpStationList = new CopyOnWriteArrayList<StationList>(station_list);
		this.station_list = cpStationList;
	}

	public Integer getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StationListObject [sn=");
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
		builder.append(", totalClient=");
		builder.append(totalClient);
		builder.append(", totalOnlineClient=");
		builder.append(totalOnlineClient);
		builder.append(", station_list=");
		builder.append(station_list);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

}
