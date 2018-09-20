package com.littlecloud.control.entity;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "client_infos")
public class ClientInfos extends DBObject implements java.io.Serializable
{
	private ClientInfosId id;
	private String client_name;
	private Integer last_device_id;

	public ClientInfos() {
	}

	public ClientInfos(ClientInfosId id) {
		this.id = id;
	}

	public ClientInfos(ClientInfosId id, String client_name, Integer last_device_id) {
		this.id = id;
		this.client_name = client_name;
		this.last_device_id = last_device_id;
	}
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "client_id", column = @Column(name = "client_id", nullable = false)),
			@AttributeOverride(name = "last_updated", column = @Column(name = "last_updated", nullable = false)) })
	public ClientInfosId getId() {
		return this.id;
	}

	public void setId(ClientInfosId id) {
		this.id = id;
	}

	@Column(name="client_name")
	public String getClient_name() {
		return client_name;
	}
	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}
	
	@Column(name="last_device_id")
	public Integer getLast_device_id() {
		return last_device_id;
	}
	public void setLast_device_id(Integer last_device_id) {
		this.last_device_id = last_device_id;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientInfos [id=");
		builder.append(id);
		builder.append(", client_name=");
		builder.append(client_name);
		builder.append(", last_device_id=");
		builder.append(last_device_id);
		builder.append("]");
		return builder.toString();
	}
}
