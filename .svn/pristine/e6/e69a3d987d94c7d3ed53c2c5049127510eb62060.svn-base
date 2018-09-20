package com.littlecloud.control.entity.branch;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;


@Entity
@Table(name = "housekeep_logs",catalog = "littlecloud_branch_production")
public class HouseKeepLogs extends DBObject implements java.io.Serializable {

	private Integer id;
	private String organization_id;
	private String table_name;
	private Integer unixtime;
	public final static String TABLE_NAME_CAPTIVE_PORTAL_SESSIONS = "captive_portal_sessions";
	public final static String TABLE_NAME_CAPTIVE_PORTAL_UPLOAD_CONTENTS = "captive_portal_upload_contents";
	public final static String TABLE_NAME_CLIENT_INFOS = "client_infos";
	
	public HouseKeepLogs() {
	}

	public HouseKeepLogs(String org_id, String table_Name, int unixtime) {
		this.organization_id = org_id;
		this.table_name = table_Name;
		this.unixtime = unixtime;
	}
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "organization_id")
	public String getOrganizationId() {
		return this.organization_id;
	}
	
	public void setOrganizationId(String org_id) {
		this.organization_id = org_id;
	}

	@Column(name = "table_name")
	public String getTable_Name() {
		return this.table_name ;
	}
	
	public void setTable_Name(String table_name) {
		this.table_name = table_name;
	}

	@Column(name = "unixtime")
	public Integer getTime() {
		return this.unixtime;
	}

	public void setTime(Integer unixtime) {
		this.unixtime = unixtime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HouseKeepLogs [id=");
		builder.append(id);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", table_name=");
		builder.append(table_name);
		builder.append(", unixtime=");
		builder.append(unixtime);
		builder.append("]");
		return builder.toString();
	}
}
