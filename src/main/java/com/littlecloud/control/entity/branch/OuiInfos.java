package com.littlecloud.control.entity.branch;

// Generated Jun 27, 2013 8:32:19 PM by Hibernate Tools 4.0.0

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

/**
 * OuiInfos generated by hbm2java
 */
@Entity
@Table(name = "oui_infos"
		, catalog = "littlecloud_branch_production")
public class OuiInfos extends DBObject implements java.io.Serializable {

	private long oui;
	private String organization;

	public OuiInfos() {
	}

	public OuiInfos(long oui) {
		this.oui = oui;
	}

	public OuiInfos(long oui, String organization) {
		this.oui = oui;
		this.organization = organization;
	}

	@Id
	@Column(name = "oui", unique = true, nullable = false)
	public long getOui() {
		return this.oui;
	}

	public void setOui(long oui) {
		this.oui = oui;
	}

	@Column(name = "organization", length = 150)
	public String getOrganization() {
		return this.organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OuiInfos [oui=");
		builder.append(oui);
		builder.append(", organization=");
		builder.append(organization);
		builder.append("]");
		return builder.toString();
	}
}
