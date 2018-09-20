package com.littlecloud.control.json.model;

import java.util.Date;

public class Json_Bandwidth {

	private String wan_name;
	private Integer dtx;
	private Integer drx;
	private Date timestamp;

	public String getWan_name() {
		return wan_name;
	}

	public void setWan_name(String wan_name) {
		this.wan_name = wan_name;
	}

	public Integer getDtx() {
		return dtx;
	}

	public void setDtx(Integer dtx) {
		this.dtx = dtx;
	}

	public Integer getDrx() {
		return drx;
	}

	public void setDrx(Integer drx) {
		this.drx = drx;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
