package com.littlecloud.control.entity.branch;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

@Entity
@Table(name="debug_log", catalog = "littlecloud_branch_production")
public class DebugLogs extends DBObject implements java.io.Serializable
{
	private Integer id;
	private String type;
	private String machine_id;
	private String msg;
	private Date timestamp;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "type", length = 45, nullable = false)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "machine_id", length = 45, nullable = false)
	public String getMachine_id() {
		return machine_id;
	}
	public void setMachine_id(String machine_id) {
		this.machine_id = machine_id;
	}
	
	@Column(name = "msg", length = 500)
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	@Column(name = "timestamp", nullable = false)
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DebugLogs [id=");
		builder.append(id);
		builder.append(", type=");
		builder.append(type);
		builder.append(", machine_id=");
		builder.append(machine_id);
		builder.append(", msg=");
		builder.append(msg);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append("]");
		return builder.toString();
	}
}
