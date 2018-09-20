package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DevDeviceTagsObject  extends PoolObject implements PoolObjectIf, Serializable{
	/* key */
	private String sn;
	private Integer iana_id;
	
	/* value */
	private CopyOnWriteArrayList<DevDeviceXTags> devDeviceXTagsList;
	
	public class DevDeviceXTags implements Serializable{
		private Integer device_id;
		private Integer tag_id;
		private String tag_name;
		public Integer getDevice_id() {
			return device_id;
		}
		public void setDevice_id(Integer device_id) {
			this.device_id = device_id;
		}
		public Integer getTag_id() {
			return tag_id;
		}
		public void setTag_id(Integer tag_id) {
			this.tag_id = tag_id;
		}
		public String getTag_name() {
			return tag_name;
		}
		public void setTag_name(String tag_name) {
			this.tag_name = tag_name;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DevDeviceXTags [device_id=");
			builder.append(device_id);
			builder.append(", tag_id=");
			builder.append(tag_id);
			builder.append(", tag_name=");
			builder.append(tag_name);
			builder.append("]");
			return builder.toString();
		}

	}

	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.iana_id = iana_id;
		this.sn = sn;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getIana_id() {
		return iana_id;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}

	public CopyOnWriteArrayList<DevDeviceXTags> getDevDeviceXTagsList() {
		return devDeviceXTagsList;
	}

	public void setDevDeviceXTagsList(
			CopyOnWriteArrayList<DevDeviceXTags> devDeviceXTagsList) {
		this.devDeviceXTagsList = devDeviceXTagsList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevDeviceTagsObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", devDeviceXTagsList=");
		builder.append(devDeviceXTagsList);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
} // end class
