package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DevicePendingChangesObject extends PoolObject implements PoolObjectIf, Serializable {

	/* key */
	public String type;
	public Integer id;

	public static final String type_default = "dummy"; // dummy
	public static final Integer id_default = 9999; // dummy

	private List<Integer> queuedIdLst = new ArrayList<Integer>();

	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + this.type + "|" + this.id;
	}

	@Override
	public void setKey(Integer id, String type) {
		this.id = id_default;
		this.type = type_default;
	}	

	public List<Integer> getQueuedIdLst() {
		return queuedIdLst;
	}

	public void setQueuedIdLst(List<Integer> queuedIdLst) {
		this.queuedIdLst = queuedIdLst;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceUpdateScheduleObject [type=");
		builder.append(type);
		builder.append(", id=");
		builder.append(id);
		builder.append(", queuedIdLst=");
		builder.append(queuedIdLst);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

}
