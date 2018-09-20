package com.littlecloud.control.entity.report;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.peplink.api.db.util.TableMappingUtil;

@Embeddable
public class ClientUsagesId implements java.io.Serializable {
	private static final int UUID_LENGTH = 45; // UUID field length
	private String Id;
	private Integer unixtime;

	public ClientUsagesId() {
		Id = TableMappingUtil.getInstance().genUUID(UUID_LENGTH);
	}

	public ClientUsagesId(String Id) {
		this.Id = Id;
	}

	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name = "id", unique = true, nullable = false, length = 45)
	public String getId() {
		return this.Id;
	}

	public void setId(String Id) {
		this.Id = Id;
	}

	@Column(name = "unixtime", nullable = false)
	public Integer getUnixtime() {
		return unixtime;
	}

	public void setUnixtime(Integer unixtime) {
		this.unixtime = unixtime;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ClientUsagesId))
			return false;
		ClientUsagesId castOther = (ClientUsagesId) other;

		return (Id.equals(castOther.getId())
				&& (unixtime == castOther.getUnixtime()));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientUsagesId [Id=");
		builder.append(Id);
		builder.append(", unixtime=");
		builder.append(unixtime);
		builder.append("]");
		return builder.toString();
	}

}
