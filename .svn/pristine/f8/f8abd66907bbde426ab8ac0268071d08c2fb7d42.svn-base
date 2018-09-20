package com.littlecloud.pool.object;

import java.io.Serializable;

import com.littlecloud.ac.json.model.command.MessageType;

public class ACCommandObject extends PoolObject implements PoolObjectIf, Serializable {
	
	public static enum ACO_TYPE { UNICAST, BROADCAST }
		
	private String sid;
	private int iana_id = 23695;
	private String sn;
	private MessageType type;
	private int status;
	private String target_machine_id;

	private String json;
	
	private ACO_TYPE aco_type = ACO_TYPE.UNICAST;
	
	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.iana_id = iana_id;
		this.sn = sn;
	}
	
	public ACO_TYPE getAco_type() {
		return aco_type;
	}

	public void setAco_type(ACO_TYPE aco_type) {
		this.aco_type = aco_type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public int getIana_id() {
		return iana_id;
	}

	public void setIana_id(int iana_id) {
		this.iana_id = iana_id;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}
	
	public String getTarget_machine_id() {
		return target_machine_id;
	}

	public void setTarget_machine_id(String target_machine_id) {
		this.target_machine_id = target_machine_id;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ACCommandObject [sid=");
		builder.append(sid);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", type=");
		builder.append(type);
		builder.append(", status=");
		builder.append(status);
		builder.append(", target_machine_id=");
		builder.append(target_machine_id);
		builder.append(", json=");
		builder.append(json);
		builder.append(", aco_type=");
		builder.append(aco_type);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
