package com.littlecloud.alert.object.data;

import javax.persistence.Column;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.peplink.api.db.DBObject;

public class AlertDevLevelObject extends DBObject implements
	java.io.Serializable {

    private int device_id;
    private int network_id;
    private int iana_id;
    private String sn;
    private String level;
    private int offlineMin;
    private String recipientType;
    private String info;

    public AlertDevLevelObject(int device_id, int network_id, int iana_id,
	    String sn, String level, int offlineMin, String recipientType,
	    String info) {
	super();
	this.device_id = device_id;
	this.network_id = network_id;
	this.iana_id = iana_id;
	this.sn = sn;
	this.level = level;
	this.offlineMin = offlineMin;
	this.recipientType = recipientType;
	this.info = info;
    }

    @Column(name = "device_id")
    public int getDevice_id() {
	return device_id;
    }

    public void setDevice_id(int device_id) {
	this.device_id = device_id;
    }

    @Column(name = "network_id")
    public int getNetwork_id() {
	return network_id;
    }

    public void setNetwork_id(int network_id) {
	this.network_id = network_id;
    }

    @Column(name = "iana_id")
    public int getIana_id() {
	return iana_id;
    }

    public void setIana_id(int iana_id) {
	this.iana_id = iana_id;
    }

    @Column(name = "sn")
    public String getSn() {
	return sn;
    }

    public void setSn(String sn) {
	this.sn = sn;
    }

    @Column(name = "level", nullable = false, length = 45)
    public String getLevel() {
	return this.level;
    }

    public void setLevel(String level) {
	this.level = level;
    }

    @Column(name = "offline_min", nullable = false)
    public int getOfflineMin() {
	return this.offlineMin;
    }

    public void setOfflineMin(int offlineMin) {
	this.offlineMin = offlineMin;
    }

    @Column(name = "recipient_type", nullable = false, length = 45)
    public String getRecipientType() {
	return this.recipientType;
    }

    public void setRecipientType(String recipientType) {
	this.recipientType = recipientType;
    }

    @Column(name = "info", length = 5000)
    public String getInfo() {
	return this.info;
    }

    public void setInfo(String info) {
	this.info = info;
    }

    @Override
    public String toString() {
    	return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
