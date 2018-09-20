package com.littlecloud.ac.health.info;

import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;

public class ACInfo {
	private long queue_size;
	private int uptime_sec;
	private int active_wtp;
	private int timestamp;
		
	public long getQueue_size() {
		return queue_size;
	}

	public void setQueue_size(long queue_size) {
		this.queue_size = queue_size;
	}

	public int getUptime_sec() {
		return uptime_sec;
	}

	public void setUptime_sec(int uptime_sec) {
		this.uptime_sec = uptime_sec;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public static void main(String args[])
	{
		DateUtils.loadTimeZones();
		
		ACInfo acmon = new ACInfo();
		acmon.setQueue_size(100);
		acmon.setUptime_sec(337896);
		acmon.setTimestamp(DateUtils.getUnixtime());
		
		System.out.println("acmon="+JsonUtils.toJson(acmon));
	}

	public int getActive_wtp() {
		return active_wtp;
	}

	public void setActive_wtp(int active_wtp) {
		this.active_wtp = active_wtp;
	}
}
