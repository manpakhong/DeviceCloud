package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DevicesTagDeviceObject extends PoolObject implements PoolObjectIf, Serializable{
	/* key */
	public static final String KEY_PART = "key";
	private String key_part = KEY_PART;
	private Integer tag_id;
	private String tag_name;
	private CopyOnWriteArrayList<Integer> device_id_list;
	
	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "key_part" + getKey_part() + "|" + getTag_id();
	}

	@Override
	public void setKey(Integer tag_id, String key_part) {
		this.tag_id = tag_id;
		this.key_part = key_part;
	}

	public String getKey_part() {
		return key_part;
	}

	public void setKey_part(String key_part) {
		this.key_part = key_part;
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

	public CopyOnWriteArrayList<Integer> getDevice_id_list() {
		return device_id_list;
	}

	public void setDevice_id_list(CopyOnWriteArrayList<Integer> device_id_list) {
		this.device_id_list = device_id_list;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevicesTagDeviceObject [key_part=");
		builder.append(key_part);
		builder.append(", tag_id=");
		builder.append(tag_id);
		builder.append(", tag_name=");
		builder.append(tag_name);
		builder.append(", device_id_list=");
		builder.append(device_id_list);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
