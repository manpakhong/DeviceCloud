package com.littlecloud.control.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "firmware_releases")
public class FirmwareReleases extends DBObject implements java.io.Serializable
{
	private Integer id;
	private Integer network_id;
	private Integer product_id;
	private String version;
	private String url;
	private String release_note;
	private String type;
	private Boolean active;
	private Date last_scan_date;
	private Date created_at;
	private Date updated_at;
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "network_id")
	public Integer getNetwork_id() {
		return network_id;
	}
	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}
	
	@Column(name = "product_id")
	public Integer getProduct_id() {
		return product_id;
	}
	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}
	
	@Column(name = "version", length=45)
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	@Column(name = "url", length = 45)
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Column(name = "release_note", length = 45)
	public String getRelease_note() {
		return release_note;
	}
	public void setRelease_note(String release_note) {
		this.release_note = release_note;
	}
	
	@Column(name = "type", length = 4)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "active")
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	@Column(name = "last_scan_date")
	public Date getLast_scan_date() {
		return last_scan_date;
	}
	public void setLast_scan_date(Date last_scan_date) {
		this.last_scan_date = last_scan_date;
	}
	
	@Column(name = "created_at")
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
	@Column(name = "updated_at")
	public Date getUpdated_at() {
		return updated_at;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FirmwareReleases [id=");
		builder.append(id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", product_id=");
		builder.append(product_id);
		builder.append(", version=");
		builder.append(version);
		builder.append(", url=");
		builder.append(url);
		builder.append(", release_note=");
		builder.append(release_note);
		builder.append(", type=");
		builder.append(type);
		builder.append(", active=");
		builder.append(active);
		builder.append(", last_scan_date=");
		builder.append(last_scan_date);
		builder.append(", created_at=");
		builder.append(created_at);
		builder.append(", updated_at=");
		builder.append(updated_at);
		builder.append("]");
		return builder.toString();
	}
	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
}
