package com.littlecloud.control.entity.branch;

// Generated Sep 17, 2013 5:34:16 PM by Hibernate Tools 4.0.0

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

/**
 * Products generated by hbm2java
 */
@Entity
@Table(name = "products", catalog = "littlecloud_branch_production")
public class Products extends DBObject implements java.io.Serializable {

	private int id;
	private String mv;
	private String name;
	private String networkType;
	private String deviceType;
	private Integer deviceDay;
	private Boolean hubSupport;
	private Boolean endpointSupport;
	private Boolean radio1_24G;
	private Boolean radio1_5G;
	private Boolean radio1_5G_na;
	private Boolean radio1_5G_ac;
	private Boolean radio2_24G;
	private Boolean radio2_5G;
	private Boolean radio2_5G_na;
	private Boolean radio2_5G_ac;
	private Integer ssidSupport;
	private Boolean portal_ic2;
	private Boolean ro_user_support;

	public Products() {
	}

	public Products(int id) {
		this.id = id;
	}

	public Products(int id, String mv, String name, String networkType, Integer deviceDay, Boolean hubSupport, Boolean endpointSupport) {
		this.id = id;
		this.mv = mv;
		this.name = name;
		this.networkType = networkType;
		this.deviceDay = deviceDay;
		this.hubSupport = hubSupport;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "mv", length = 100)
	public String getMv() {
		return this.mv;
	}

	public void setMv(String mv) {
		this.mv = mv;
	}

	@Column(name = "name", length = 100)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "network_type", length = 16)
	public String getNetworkType() {
		return this.networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	@Column(name = "device_type", length = 45)
	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	@Column(name = "device_day")
	public Integer getDeviceDay() {
		return this.deviceDay;
	}

	public void setDeviceDay(Integer deviceDay) {
		this.deviceDay = deviceDay;
	}

	@Column(name = "hub_support")
	public Boolean getHubSupport() {
		return this.hubSupport;
	}

	public void setHubSupport(Boolean hubSupport) {
		this.hubSupport = hubSupport;
	}
	
	@Column(name = "endpoint_support")
	public Boolean getEndpointSupport() {
		return endpointSupport;
	}

	public void setEndpointSupport(Boolean endpointSupport) {
		this.endpointSupport = endpointSupport;
	}

	@Column(name = "radio1_24G")
	public Boolean getRadio1_24G() {
		return radio1_24G;
	}

	public void setRadio1_24G(Boolean radio1_24g) {
		radio1_24G = radio1_24g;
	}

	@Column(name = "radio1_5G")
	public Boolean getRadio1_5G() {
		return radio1_5G;
	}

	public void setRadio1_5G(Boolean radio1_5g) {
		radio1_5G = radio1_5g;
	}

	@Column(name = "radio1_5G_na")
	public Boolean getRadio1_5G_na() {
		return radio1_5G_na;
	}

	public void setRadio1_5G_na(Boolean radio1_5g_na) {
		radio1_5G_na = radio1_5g_na;
	}

	@Column(name = "radio1_5G_ac")
	public Boolean getRadio1_5G_ac() {
		return radio1_5G_ac;
	}

	public void setRadio1_5G_ac(Boolean radio1_5g_ac) {
		radio1_5G_ac = radio1_5g_ac;
	}
	
	@Column(name = "radio2_24G")
	public Boolean getRadio2_24G() {
		return radio2_24G;
	}

	public void setRadio2_24G(Boolean radio2_24g) {
		radio2_24G = radio2_24g;
	}

	@Column(name = "radio2_5G")
	public Boolean getRadio2_5G() {
		return radio2_5G;
	}

	public void setRadio2_5G(Boolean radio2_5g) {
		radio2_5G = radio2_5g;
	}
	
	@Column(name = "radio2_5G_na")
	public Boolean getRadio2_5G_na() {
		return radio2_5G_na;
	}

	public void setRadio2_5G_na(Boolean radio2_5g_na) {
		radio2_5G_na = radio2_5g_na;
	}

	@Column(name = "radio2_5G_ac")
	public Boolean getRadio2_5G_ac() {
		return radio2_5G_ac;
	}

	public void setRadio2_5G_ac(Boolean radio2_5g_ac) {
		radio2_5G_ac = radio2_5g_ac;
	}

	@Column(name = "ssid_support")
	public Integer getSsidSupport() {
		return ssidSupport;
	}

	public void setSsidSupport(Integer ssidSupport) {
		this.ssidSupport = ssidSupport;
	}
	
	@Column(name = "portal_ic2")
	public Boolean getPortal_ic2() {
		return portal_ic2;
	}

	public void setPortal_ic2(Boolean portal_ic2) {
		this.portal_ic2 = portal_ic2;
	}
		
	@Column(name = "ro_user_support")
	public Boolean getRo_user_support() {
		return ro_user_support;
	}

	public void setRo_user_support(Boolean ro_user_support) {
		this.ro_user_support = ro_user_support;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Products [id=");
		builder.append(id);
		builder.append(", mv=");
		builder.append(mv);
		builder.append(", name=");
		builder.append(name);
		builder.append(", networkType=");
		builder.append(networkType);
		builder.append(", deviceType=");
		builder.append(deviceType);
		builder.append(", deviceDay=");
		builder.append(deviceDay);
		builder.append(", hubSupport=");
		builder.append(hubSupport);
		builder.append(", endpointSupport=");
		builder.append(endpointSupport);
		builder.append(", radio1_24G=");
		builder.append(radio1_24G);
		builder.append(", radio1_5G=");
		builder.append(radio1_5G);
		builder.append(", radio1_5G_na=");
		builder.append(radio1_5G_na);
		builder.append(", radio1_5G_ac=");
		builder.append(radio1_5G_ac);
		builder.append(", radio2_24G=");
		builder.append(radio2_24G);
		builder.append(", radio2_5G=");
		builder.append(radio2_5G);
		builder.append(", radio2_5G_na=");
		builder.append(radio2_5G_na);
		builder.append(", radio2_5G_ac=");
		builder.append(radio2_5G_ac);
		builder.append(", ssidSupport=");
		builder.append(ssidSupport);
		builder.append(", portal_ic2=");
		builder.append(portal_ic2);
		builder.append(", ro_user_support=");
		builder.append(ro_user_support);
		builder.append("]");
		return builder.toString();
	}
}