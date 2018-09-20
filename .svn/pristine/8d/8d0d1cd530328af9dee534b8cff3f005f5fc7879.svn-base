package com.littlecloud.control.entity.branch;



import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;


@Entity
@Table(name = "housekeep_settings",catalog = "littlecloud_branch_production")
public class HouseKeepSettings extends DBObject implements java.io.Serializable {

	private String table_name;
	private String column_name;
	private int housekeep_day;
	private String level;
	
	public HouseKeepSettings() {
	}

	public HouseKeepSettings(String table_name, String column_name,int housekeep_day,String level) {
		this.table_name = table_name;
		this.column_name = column_name;
		this.housekeep_day = housekeep_day;
		this.level = level;
	}
	
	
	
	@Column(name = "table_name",  nullable = false)
	public String get_TableName() {
		return table_name;
	}

	public void set_TableName(String table_name) {
		this.table_name = table_name;
	}
	
	@Column(name = "column_name")
	public String getColumnName() {
		return this.column_name;
	}
	
	public void setColumnName(String column_name) {
		this.column_name = column_name;
	}

	@Column(name = "housekeep_day")
	public int getHouseKeepDay() {
		return this.housekeep_day;
	}

	public void setHouseKeepDay(int housekeep_day) {
		this.housekeep_day = housekeep_day;
	}

	@Column(name = "level")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HouseKeepSettings [table_name=");
		builder.append(table_name);
		builder.append(", column_name=");
		builder.append(column_name);
		builder.append(", housekeep_day=");
		builder.append(housekeep_day);
		builder.append(", level=");
		builder.append(level);
		builder.append("]");
		return builder.toString();
	}
}
