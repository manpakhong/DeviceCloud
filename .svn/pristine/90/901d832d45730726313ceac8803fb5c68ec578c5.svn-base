package com.littlecloud.pool.object;

import java.io.Serializable;
import com.littlecloud.ac.ACService;
import com.peplink.api.db.util.TableMappingUtil;

public class EventObject implements Serializable {	// for global consolidation event
	private static final int UUID_LENGTH = 45; // UUID field length
	private String sender;
	private String id;
	private Long time;
	
	public EventObject() {
		sender = ACService.getServerName() + java.lang.Thread.currentThread().getId();
		time = System.currentTimeMillis();
		id = TableMappingUtil.getInstance().genUUID(UUID_LENGTH);
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "EventObject [sender=" + sender + ", id=" + id + ", time=" + time + "]";
	}
}
