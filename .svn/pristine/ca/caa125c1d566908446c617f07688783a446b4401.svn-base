package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.littlecloud.control.json.util.DateUtils;

public class DevicesTrimObject extends PoolObject implements PoolObjectIf, Serializable {

	/* key */
	private Integer id;
	private String sn;
	private Integer ianaId;
	private Integer network_Id;

	public DevicesTrimObject(Integer id, Integer iana_id, String sn, Integer network_Id) {
		this.id = id;
		this.ianaId = iana_id;
		this.sn = sn;
		this.network_Id = network_Id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getIanaId() {
		return ianaId;
	}

	public void setIanaId(Integer ianaId) {
		this.ianaId = ianaId;
	}
	
	public Integer getNetwork_Id() {
		return network_Id;
	}

	public void setNetwork_Id(Integer network_Id) {
		this.network_Id = network_Id;
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.ianaId = iana_id;
		this.sn = sn;
	}

	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIanaId();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevicesTrimObject [id=");
		builder.append(id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", ianaId=");
		builder.append(ianaId);
		builder.append(", network_Id=");
		builder.append(network_Id);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ianaId == null) ? 0 : ianaId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((sn == null) ? 0 : sn.hashCode());
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
		DevicesTrimObject other = (DevicesTrimObject) obj;
		if (ianaId == null) {
			if (other.ianaId != null)
				return false;
		} else if (!ianaId.equals(other.ianaId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (sn == null) {
			if (other.sn != null)
				return false;
		} else if (!sn.equals(other.sn))
			return false;
		return true;
	}
}
