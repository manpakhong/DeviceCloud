package com.littlecloud.control.json.model.pepvpn;

import java.io.Serializable;

public class Link implements Serializable {	
	private long id;
	private Boolean enable;
	private Boolean ip_forward;
	private String name;
	private String static_ip;
	private String static_maskn;
	private String conn_method;	
	private Port port;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Boolean isEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Boolean isIp_forward() {
		return ip_forward;
	}

	public void setIp_forward(Boolean ip_forward) {
		this.ip_forward = ip_forward;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatic_ip() {
		return static_ip;
	}

	public void setStatic_ip(String static_ip) {
		this.static_ip = static_ip;
	}

	public String getStatic_maskn() {
		return static_maskn;
	}

	public void setStatic_maskn(String static_maskn) {
		this.static_maskn = static_maskn;
	}

	public String getConn_method() {
		return conn_method;
	}

	public void setConn_method(String conn_method) {
		this.conn_method = conn_method;
	}

	public Port getPort() {
		return port;
	}

	public void setPort(Port port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "Link [id=" + id + ", enable=" + enable + ", ip_forward=" + ip_forward + ", name=" + name + ", static_ip=" + static_ip + ", static_maskn=" + static_maskn + ", conn_method=" + conn_method + ", port=" + port + "]";
	}
}