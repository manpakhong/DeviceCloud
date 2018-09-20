package com.littlecloud.pool.object;

import java.io.Serializable;

public class TunnelObject extends PoolObject implements PoolObjectIf, Serializable {

	/* 
	 * startWebAdminTunnel
	 * 	- check expire time
	 * 	- set unknown status
	 * 	- fetch start
	 * 
	 * pollWebAdminTunnel
	 * 	- update expire time
	 * 	- fetch continue
	 * 
	 * stopWebAdminTunnel
	 * 	- set expire time to zero
	 * 	- fetch stop
	 * 
	 */
	
	public static final String STATUS_TYPE_UNKNOWN = "unknown";
	public static final String EVENT_TYPE_READY = "ready";
	public static final String EVENT_TYPE_PENDING = "pending";
	public static final String EVENT_TYPE_STOP = "stop";
	
	private Integer iana_id;
	private String sn;
	private Integer version;

	private Boolean success;
	private String status;
	private String port;
	private String domain;
	private String http;
	private Integer timestamp;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getHttp() {
		return http;
	}

	public void setHttp(String http) {
		this.http = http;
	}

	public Integer getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getIana_id() {
		return iana_id;
	}

	public void setInna_id(Integer iana_id) {
		this.iana_id = iana_id;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TunnelObject [iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", version=");
		builder.append(version);
		builder.append(", success=");
		builder.append(success);
		builder.append(", status=");
		builder.append(status);
		builder.append(", port=");
		builder.append(port);
		builder.append(", domain=");
		builder.append(domain);
		builder.append(", http=");
		builder.append(http);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
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
}
