package com.littlecloud.control.entity.branch;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "device_pending_changes", catalog = "littlecloud_branch_production")
public class DevicePendingChanges extends DBObject implements Serializable {
	
	public static final String tableName = "device_pending_changes";
	
	public static enum STATUS {
		pending, completed, cancel, reject
	}

	private Integer id;
	private String sid;
	private Integer iana_id;
	private String sn;	
	private String message_type;
	private String content;
	private Date created_at;
	private Date last_attemp_time;
	private int retry;
	//private Integer status;
	private String status;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "sn", length = 30)
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	@Column(name = "sid", length = 100)
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	@Column(name = "iana_id")
	public Integer getIana_id() {
		return iana_id;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}

	@Column(name = "message_type", length = 50)
	public String getMessage_type() {
		return message_type;
	}

	public void setMessage_type(String message_type) {
		this.message_type = message_type;
	}

	@Column(name = "content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", length = 19)
	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_attemp_time", length = 19)
	public Date getLast_attemp_time() {
		return last_attemp_time;
	}

	public void setLast_attemp_time(Date last_attemp_time) {
		this.last_attemp_time = last_attemp_time;
	}

	@Column(name = "retry")
	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	@Column(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DevicePendingChanges other = (DevicePendingChanges) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevicePendingChanges [id=");
		builder.append(id);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", message_type=");
		builder.append(message_type);
		builder.append(", content=");
		builder.append(content);
		builder.append(", created_at=");
		builder.append(created_at);
		builder.append(", last_attemp_time=");
		builder.append(last_attemp_time);
		builder.append(", retry=");
		builder.append(retry);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}
}
