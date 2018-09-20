package com.littlecloud.control.json.model.config.util.info;

import com.littlecloud.control.json.model.config.util.ConfigUpdatePerNetworkTask;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerNetworkTask.UPDATE_OPERATION;

public class ConfigPutTaskInfo {
	public String orgId;
	public Integer netId;
	public Integer devId;
	public String sid;
	public Integer iana_id;
	public String sn;
	public UPDATE_OPERATION op;
	
	public ConfigPutTaskInfo() {};

	public ConfigPutTaskInfo(String orgId, Integer netId, Integer devId, String sid, Integer iana_id, String sn,
			UPDATE_OPERATION op) {
		this.orgId = orgId;
		this.netId = netId;
		this.devId = devId;
		this.sid = sid;
		this.iana_id = iana_id;
		this.sn = sn;
		this.op = op;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public Integer getNetId() {
		return netId;
	}

	public void setNetId(Integer netId) {
		this.netId = netId;
	}

	public Integer getDevId() {
		return devId;
	}

	public void setDevId(Integer devId) {
		this.devId = devId;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public Integer getIana_id() {
		return iana_id;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public UPDATE_OPERATION getOp() {
		return op;
	}

	public void setOp(UPDATE_OPERATION op) {
		this.op = op;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigPutTaskInfo [orgId=");
		builder.append(orgId);
		builder.append(", netId=");
		builder.append(netId);
		builder.append(", devId=");
		builder.append(devId);
		builder.append(", sid=");
		builder.append(sid);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", op=");
		builder.append(op);
		builder.append("]");
		return builder.toString();
	}
}