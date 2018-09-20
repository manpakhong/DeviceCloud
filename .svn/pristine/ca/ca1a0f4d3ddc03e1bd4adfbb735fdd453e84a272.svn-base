package com.littlecloud.control.entity.report;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.peplink.api.db.util.TableMappingUtil;

@Embeddable
public class EventLogId implements java.io.Serializable {
	private static final int UUID_LENGTH = 45; // UUID field length
	private String Id;
	private Integer unixtime;

	public EventLogId() {
		Id = TableMappingUtil.getInstance().genUUID(UUID_LENGTH);
	}

	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name = "id", unique = true, nullable = false, length = 45)
	public String getId() {
		return Id;
	}

	public void setId(String Id) {
		this.Id = Id;
	}

	@Column(name = "unixtime", nullable = false)
	public Integer getUnixtime() {
		return this.unixtime;
	}

	public void setUnixtime(Integer unixtime) {
		this.unixtime = unixtime;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof EventLogId))
			return false;
		EventLogId castOther = (EventLogId) other;

		return (Id.equals(castOther.getId())
				&& (this.unixtime == castOther.getUnixtime()));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EventLogId [Id=");
		builder.append(Id);
		builder.append(", unixtime=");
		builder.append(unixtime);
		builder.append("]");
		return builder.toString();
	}

}
