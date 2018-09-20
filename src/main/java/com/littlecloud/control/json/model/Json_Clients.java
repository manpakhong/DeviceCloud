package com.littlecloud.control.json.model;

import java.util.Date;

import com.littlecloud.pool.object.StationList;

public class Json_Clients {
	// public static enum Status {
	// connected, disconnected
	// };

	// public enum ConnectionType {
	// ethernet(1),
	// wireless(2);
	//
	// private final Integer duration;
	//
	// ConnectionType(Integer duration) {
	// this.duration = duration;
	// }
	//
	// public Integer getDuration() {
	// return this.duration;
	// }
	// }
	private String client_id;
	private String mac;
	private String name;
	private Json_Devices device;

	private String last_seen;
	private Integer duration;

	// private ConnectionType connection_type;
	private String connection_type;
	// private Status status;
	private String status;
	private String ssid;
	private Integer channel;
	private String ip;
	private Float channel_width;
	private String radio_mode;
	private Float signal;
	private Float upload_rate;
	private Float download_rate;
	private String manufacturer;

	private Float latitude;
	private Float longitude;
	private Float radius;
	private Date firstAppear;

	public void parseStationList(Json_Devices device, StationList station)
	{
		this.client_id = station.getClient_id();
		this.mac = station.getMac();
		this.name = station.getName();
		this.device = device; // need its name, location, signal

		// this.last_seen =
		this.duration = station.getConn_len();

		this.connection_type = station.getType();
		this.status = station.getStatus();
		this.ssid = station.getEssid();
		this.channel = station.getChannel();
		this.ip = station.getIp();
		this.channel_width = station.getCh_width();
		this.radio_mode = station.getRadio_mode();		
		this.signal = station.getRssi();
		
		//---------------------------------
//		this.upload_rate = station.getDrx();
//		this.download_rate = station.getDtx();
		//-----------------------------------

		//this.manufacturer = ;
		
		this.latitude = device.getLatitude();
		this.longitude = device.getLongitude();

		this.radius = 100F; // finally guess from device model
	}

	public String getConnection_type() {
		return connection_type;
	}

	public void setConnection_type(String connection_type) {
		this.connection_type = connection_type;
	}

	public Json_Devices getDevice() {
		return device;
	}

	public void setDevice(Json_Devices device) {
		this.device = device;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public Float getChannel_width() {
		return channel_width;
	}

	public void setChannel_width(Float channel_width) {
		this.channel_width = channel_width;
	}

	public String getRadio_mode() {
		return radio_mode;
	}

	public void setRadio_mode(String radio_mode) {
		this.radio_mode = radio_mode;
	}

	public Float getSignal() {
		return signal;
	}

	public void setSignal(Float signal) {
		this.signal = signal;
	}

	public Float getUpload_rate() {
		return upload_rate;
	}

	public void setUpload_rate(Float upload_rate) {
		this.upload_rate = upload_rate;
	}

	public Float getDownload_rate() {
		return download_rate;
	}

	public void setDownload_rate(Float download_rate) {
		this.download_rate = download_rate;
	}

	public String getLast_seen() {
		return last_seen;
	}

	public void setLast_seen(String last_seen) {
		this.last_seen = last_seen;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
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

	public Float getRadius() {
		return radius;
	}

	public void setRadius(Float radius) {
		this.radius = radius;
	}

	public Date getFirstAppear() {
		return firstAppear;
	}

	public void setFirstAppear(Date firstAppear) {
		this.firstAppear = firstAppear;
	}

}
