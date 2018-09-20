/*
 * This object must be used as read-only when it is used in multi-thread environment!
 * 
 */
package com.littlecloud.pool.object;

import java.io.Serializable;

public class Session implements Serializable{

	private static final long serialVersionUID = -1867965938154106002L;
	
	private String custid = null;
	private String type = null;
	private String token = null;
	private String clientid = null;
	private String ip = null;	
	private String lastupdate = null;
	private int expireInSecond = -1;

	public Session(String custid, String type, String token, String clientid, String ip, String lastupdate, int expireInSecond) {
		super();
		this.custid = custid;
		this.type = type;
		this.token = token;
		this.clientid = clientid;
		this.ip = ip;
		this.lastupdate = lastupdate;
		this.expireInSecond = expireInSecond;
	}

	public String getCustid() {
		return custid;
	}

	public String getType() {
		return type;
	}

	public String getToken() {
		return token;
	}

	public String getClientid() {
		return clientid;
	}

	public String getIp() {
		return ip;
	}

	public String getLastupdate() {
		return lastupdate;
	}

	public int getExpireInSecond() {
		return expireInSecond;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Session [custid=");
		builder.append(custid);
		builder.append(", type=");
		builder.append(type);
		builder.append(", token=");
		builder.append(token);
		builder.append(", clientid=");
		builder.append(clientid);
		builder.append(", ip=");
		builder.append(ip);
		builder.append(", lastupdate=");
		builder.append(lastupdate);
		builder.append(", expireInSecond=");
		builder.append(expireInSecond);
		builder.append("]");
		return builder.toString();
	}
}
