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
@Table(name = "firmwares", catalog = "littlecloud_branch_production")
public class Firmwares extends DBObject implements java.io.Serializable
{
	private Integer id;
	private Integer product_id;
	private String version;
	private String url;
	private String release_note;
	private String release_type;
	private Boolean active;
	private Date created_at;
	private Date updated_at;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "product_id")
	public Integer getProduct_id() {
		return product_id;
	}
	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}
	@Column(name = "version", length = 255)
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	@Column(name = "url", length = 255)
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Column(name = "release_note", length = 255)
	public String getRelease_note() {
		return release_note;
	}
	public void setRelease_note(String release_note) {
		this.release_note = release_note;
	}
	@Column(name = "release_type", length = 4)
	public String getRelease_type() {
		return release_type;
	}
	public void setRelease_type(String release_type) {
		this.release_type = release_type;
	}
	@Column(name = "active")
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
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
	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Firmwares [id=");
		builder.append(id);
		builder.append(", product_id=");
		builder.append(product_id);
		builder.append(", version=");
		builder.append(version);
		builder.append(", url=");
		builder.append(url);
		builder.append(", release_note=");
		builder.append(release_note);
		builder.append(", release_type=");
		builder.append(release_type);
		builder.append(", active=");
		builder.append(active);
		builder.append(", created_at=");
		builder.append(created_at);
		builder.append(", updated_at=");
		builder.append(updated_at);
		builder.append("]");
		return builder.toString();
	}
}
