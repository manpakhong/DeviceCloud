package com.littlecloud.control.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import com.peplink.api.db.DBObject;

@Embeddable
public class ClientInfosId extends DBObject implements java.io.Serializable {

	private String client_id;
	private Date last_updated;

	public ClientInfosId() {
	}

	public ClientInfosId(String client_id, Date last_updated) {
		this.client_id = client_id;
		this.last_updated = last_updated;
	}

	@Column(name = "client_id", nullable = false)
	public String getClient_id() {
		return this.client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	@Column(name="last_updated", nullable = false)
	public Date getLast_updated() {
		return last_updated;
	}

	public void setLast_updated(Date last_updated) {
		this.last_updated = last_updated;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ClientInfosId))
			return false;
		ClientInfosId castOther = (ClientInfosId) other;

		return (this.getClient_id().equals(castOther.getClient_id())
				&& (this.getLast_updated().getTime() == castOther.getLast_updated().getTime()));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getLast_updated() == null ? 0 : this.getLast_updated().hashCode());
		result = 37 * result + (getClient_id() == null ? 0 : this.getClient_id().hashCode());
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientInfosId [client_id=");
		builder.append(client_id);
		builder.append(", last_updated=");
		builder.append(last_updated);
		builder.append("]");
		return builder.toString();
	}
}
