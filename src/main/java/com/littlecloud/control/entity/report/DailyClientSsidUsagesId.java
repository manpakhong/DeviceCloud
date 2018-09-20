package com.littlecloud.control.entity.report;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Embeddable
public class DailyClientSsidUsagesId implements java.io.Serializable {

	private Long Id;
	private Integer unixtime;

	public DailyClientSsidUsagesId() {
	}

	public DailyClientSsidUsagesId(Long Id, Integer unixtime) {
		this.Id = Id;
		this.unixtime = unixtime;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return Id;
	}

	public void setId(Long Id) {
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
		if (!(other instanceof DailyClientSsidUsagesId))
			return false;
		DailyClientSsidUsagesId castOther = (DailyClientSsidUsagesId) other;

		return (Id == castOther.getId() 
				&& (unixtime == castOther.getUnixtime()));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DailyClientSsidUsagesId [Id=");
		builder.append(Id);
		builder.append(", unixtime=");
		builder.append(unixtime);
		builder.append("]");
		return builder.toString();
	}

}
