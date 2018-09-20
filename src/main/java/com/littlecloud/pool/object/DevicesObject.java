package com.littlecloud.pool.object;

import java.io.Serializable;

import com.littlecloud.control.entity.Devices;

public class DevicesObject extends Devices implements PoolObjectIf, Serializable {

	protected Long createTime;
	protected boolean Refreshing = false;

	public DevicesObject(String sn, Integer ianaId) {
		this.ianaId = ianaId;
		this.sn = sn;
	}

	public DevicesObject() {
		
	}

	@Override
	public Long getCreateTime() {
		return createTime;
	}

	@Override
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	@Override
	public boolean isRefreshing() {
		return Refreshing;
	}

	@Override
	public void setRefreshing(boolean Refreshing) {
		this.Refreshing = Refreshing;
	}

	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + sn + "|" + ianaId;
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.ianaId = iana_id;
		this.sn = sn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevicesObject [id=");
		builder.append(id);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", network_Id=");
		builder.append(network_Id);
		builder.append(", ianaId=");
		builder.append(ianaId);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", productId=");
		builder.append(productId);
		builder.append(", modelId=");
		builder.append(modelId);
		builder.append(", configChecksum=");
		builder.append(configChecksum);
		builder.append(", certChecksum=");
		builder.append(certChecksum);
		builder.append(", name=");
		builder.append(name);
		builder.append(", firstAppear=");
		builder.append(firstAppear);
		builder.append(", lastOnline=");
		builder.append(lastOnline);
		builder.append(", offlineAt=");
		builder.append(offlineAt);
		builder.append(", expiryDate=");
		builder.append(expiryDate);
		builder.append(", subExpiryDate=");
		builder.append(subExpiryDate);
		builder.append(", active=");
		builder.append(active);
		builder.append(", dev_level_cfg=");
		builder.append(dev_level_cfg);
		builder.append(", webadmin_cfg=");
		builder.append(webadmin_cfg);
		builder.append(", latitude=");
		builder.append(latitude);
		builder.append(", longitude=");
		builder.append(longitude);
		builder.append(", fw_ver=");
		builder.append(fw_ver);
		builder.append(", network_name=");
		builder.append(network_name);
		builder.append(", address=");
		builder.append(address);
		builder.append(", icmg=");
		builder.append(icmg);
		builder.append(", last_sync_date=");
		builder.append(last_sync_date);
		builder.append(", last_gps_latitude=");
		builder.append(last_gps_latitude);
		builder.append(", last_gps_longitude=");
		builder.append(last_gps_longitude);
		builder.append(", last_gps_unixtime=");
		builder.append(last_gps_unixtime);
		builder.append(", purge_data=");
		builder.append(purge_data);
		builder.append(", online_status=");
		builder.append(online_status);
		builder.append(", ddns_enabled=");
		builder.append(ddns_enabled);
		builder.append(", last_ac_updatetime=");
		builder.append(last_ac_updatetime);
		builder.append(", tableName=");
		builder.append(tableName);
		builder.append(", mode=");
		builder.append(mode);
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
		int result = super.hashCode();
		result = prime * result + (Refreshing ? 1231 : 1237);
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DevicesObject other = (DevicesObject) obj;
		if (Refreshing != other.Refreshing)
			return false;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		return true;
	}
}
