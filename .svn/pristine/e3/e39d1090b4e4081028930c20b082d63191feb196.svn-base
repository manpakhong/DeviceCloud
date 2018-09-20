package com.littlecloud.pool.object;

import java.io.Serializable;
	
public class StationList implements Serializable {
	public final static String TYPE_WIRELESS = "wireless";
	public final static String TYPE_LAN = "lan";
	public final static String TYPE_ETHERNET = "ethernet";
	public final static String TYPE_PPTP  = "pptp";
	public final static String TYPE_STATIC_ROUTE = "static_rout";
	private String client_id;
//	private Integer device_id;	// no use
	private String mac;
	private String ip;
	private String name;
	private String status;
	private String type;

	private String radio_mode;
	private Integer conn_len;
	private Float rssi;
	private Integer ifi_id;

	private String bssid;
	private String essid;
	private String security;
	private Integer channel;
	private Float ch_width;
	
	private Integer lastUpdateTime;
	private Integer first_appear_time;

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

//	public Integer getDevice_id() {
//		return device_id;
//	}
//
//	public void setDevice_id(Integer device_id) {
//		this.device_id = device_id;
//	}
//
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRadio_mode() {
		return radio_mode;
	}

	public void setRadio_mode(String radio_mode) {
		this.radio_mode = radio_mode;
	}

	public Integer getConn_len() {
		return conn_len;
	}

	public void setConn_len(Integer conn_len) {
		this.conn_len = conn_len;
	}

	public Float getRssi() {
		return rssi;
	}

	public void setRssi(Float rssi) {
		this.rssi = rssi;
	}

	public Integer getIfi_id() {
		return ifi_id;
	}

	public void setIfi_id(Integer ifi_id) {
		this.ifi_id = ifi_id;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getEssid() {
		return essid;
	}

	public void setEssid(String essid) {
		this.essid = essid;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public Float getCh_width() {
		return ch_width;
	}

	public void setCh_width(Float ch_width) {
		this.ch_width = ch_width;
	}

	public Integer getFirst_appear_time() {
		return first_appear_time;
	}

	public void setFirst_appear_time(Integer first_appear_time) {
		this.first_appear_time = first_appear_time;
	}

	public Integer getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Integer lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StationList [client_id=");
		builder.append(client_id);
//		builder.append(", device_id=");
//		builder.append(device_id);
		builder.append(", mac=");
		builder.append(mac);
		builder.append(", ip=");
		builder.append(ip);
		builder.append(", name=");
		builder.append(name);
		builder.append(", status=");
		builder.append(status);
		builder.append(", type=");
		builder.append(type);
		builder.append(", radio_mode=");
		builder.append(radio_mode);
		builder.append(", conn_len=");
		builder.append(conn_len);
		builder.append(", rssi=");
		builder.append(rssi);
		builder.append(", ifi_id=");
		builder.append(ifi_id);
		builder.append(", bssid=");
		builder.append(bssid);
		builder.append(", essid=");
		builder.append(essid);
		builder.append(", security=");
		builder.append(security);
		builder.append(", channel=");
		builder.append(channel);
		builder.append(", ch_width=");
		builder.append(ch_width);
		builder.append(", first_appear_time=");
		builder.append(first_appear_time);
		builder.append(", lastUpdateTime=");
		builder.append(lastUpdateTime);
		builder.append("]");
		return builder.toString();
	}

	@Override
    public boolean equals(Object object)
    {
        boolean same = false;
        if (object != null && object instanceof StationList){
        	same = this.getClient_id().compareTo(((StationList)object).getClient_id()) == 0;
        }
        return same;
    }
	
	@Override
	public int hashCode(){
		return this.getClient_id().hashCode();
	}

}