package com.littlecloud.control.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

@Entity
@Table(name="columns",catalog="information_schema")
public class Columns extends DBObject implements java.io.Serializable
{
	private String table_name;
	private String column_name;
	
	@Column(name="table_name")
	public String getTable_name() 
	{
		return table_name;
	}
	
	public void setTable_name(String table_name) 
	{
		this.table_name = table_name;
	}
	
	@Column(name="column_name")
	public String getColumn_name() 
	{
		return column_name;
	}
	
	public void setColumn_name(String column_name) 
	{
		this.column_name = column_name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Columns [table_name=");
		builder.append(table_name);
		builder.append(", column_name=");
		builder.append(column_name);
		builder.append("]");
		return builder.toString();
	}
}
