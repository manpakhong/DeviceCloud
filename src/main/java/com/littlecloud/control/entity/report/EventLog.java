package com.littlecloud.control.entity.report;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.peplink.api.db.DBObject;
import com.peplink.api.db.util.TableMappingUtil;

@Entity
@Table(name = "event_log")
public class EventLog extends DBObject implements java.io.Serializable {
	public static final String EVENT_TYPE_SYSTEM = "System";
	public static final String EVENT_TYPE_PEPVPN = "PepVPN";
	public static final String EVENT_TYPE_WAN = "WAN";
	public static final String EVENT_TYPE_PPTP = "PPTP";
	public static final String EVENT_TYPE_IP_CONFLICT = "IP address conflict";
	public static final String EVENT_TYPE_MAC_CONFLICT = "MAC address conflict";
	public static final String EVENT_TYPE_HA = "HA";
	public static final String EVENT_TYPE_DDNS = "DDNS";
	public static final String EVENT_TYPE_WLAN = "WLAN";
	public static final String EVENT_TYPE_FLEET = "Fleet";
	
	public static final String EVENT_TYPE_FLEET_MESSAGE_IN_ZONE = "IN_ZONE";
	public static final String EVENT_TYPE_FLEET_MESSAGE_OUT_ZONE = "OUT_ZONE";
	public static final String EVENT_TYPE_FLEET_MESSAGE_OVER_SPEED_CAUGHT = "EVENT_TYPE_FLEET_MESSAGE_OVER_SPEED_CAUGHT";
	public static final String EVENT_TYPE_FLEET_MESSAGE_OVER_SPEED_RESUME_NORMAL = "EVENT_TYPE_FLEET_MESSAGE_OVER_SPEED_RESUME_NORMAL";	
	
	private EventLogId id;
	private Integer networkId;
	private Date datetime;
	private Integer deviceId;
	private String ssid;
	private String clientName;
	private String clientMac;
	private String eventType;
	private String detail;

	public EventLog() {
		id = new EventLogId();
	}

	public EventLog(int networkId, Date datetime, Integer unixtime, 
			Integer deviceId, String ssid, String clientName, String clientMac,
			String eventType, String detail) {
		id = new EventLogId();
		id.setUnixtime(unixtime);
		this.networkId = networkId;
		this.datetime = datetime;
		this.deviceId = deviceId;
		this.ssid = ssid;
		this.clientName = clientName;
		this.clientMac = clientMac;
		this.eventType = eventType;
		this.detail = detail;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "id", column = @Column(name = "id", nullable = false)),
			@AttributeOverride(name = "unixtime", column = @Column(name = "unixtime", nullable = false)) })
	public EventLogId getId() {
		return this.id;
	}

	public void setId(EventLogId id) {
		this.id = id;
	}

	@Column(name = "network_id", nullable = false)
	public int getNetworkId() {
		return this.networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datetime", nullable = false, length = 19)
	public Date getDatetime() {
		return this.datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public Integer getUnixtime() {
		return id.getUnixtime();
	}

	public void setUnixtime(Integer unixtime) {
		id.setUnixtime(unixtime);
	}

	@Column(name = "device_id")
	public Integer getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	@Column(name = "ssid", length = 45)
	public String getSsid() {
		return this.ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	@Column(name = "client_name", length = 45)
	public String getClientName() {
		return this.clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	@Column(name = "client_mac", length = 45)
	public String getClientMac() {
		return this.clientMac;
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}
	
	@Column(name = "event_type", length = 45)
	public String getEventType() {
		return this.eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	@Column(name = "detail", length = 500)
	public String getDetail() {
		return this.detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EventLog [id=");
		builder.append(id);
		builder.append(", networkId=");
		builder.append(networkId);
		builder.append(", datetime=");
		builder.append(datetime);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", ssid=");
		builder.append(ssid);
		builder.append(", clientName=");
		builder.append(clientName);
		builder.append(", clientMac=");
		builder.append(clientMac);
		builder.append(", eventType=");
		builder.append(eventType);
		builder.append(", detail=");
		builder.append(detail);
		builder.append("]");
		return builder.toString();
	}

}
