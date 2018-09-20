package com.littlecloud.ac.json.model;

import java.util.List;

public class Json_RedirectWtp {
	private String cn;
	private List<String> host;
	private Integer version = 1;
	private String client_key;
	private String root_cert;
	private String client_cert;
	private String client_key_pwd;
	private Integer permanent;
	
	public Json_RedirectWtp(){
		
	}
	
	public Json_RedirectWtp(String cn, List<String> host, Integer version,
			String client_key, String root_cert, String client_cert,
			String client_key_pwd) {
		super();
		this.cn = cn;
		this.host = host;
		this.version = version;
		this.client_key = client_key;
		this.root_cert = root_cert;
		this.client_cert = client_cert;
		this.client_key_pwd = client_key_pwd;
	}
	
	public List<String> getHost() {
		return host;
	}

	public void setHost(List<String> host) {
		this.host = host;
	}
	
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getClient_key_pwd() {
		return client_key_pwd;
	}

	public void setClient_key_pwd(String client_key_pwd) {
		this.client_key_pwd = client_key_pwd;
	}

	public String getClient_key() {
		return client_key;
	}

	public void setClient_key(String client_key) {
		this.client_key = client_key;
	}

	public String getClient_cert() {
		return client_cert;
	}

	public void setClient_cert(String client_cert) {
		this.client_cert = client_cert;
	}

	public String getRoot_cert() {
		return root_cert;
	}

	public void setRoot_cert(String root_cert) {
		this.root_cert = root_cert;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public Integer getPermanent() {
		return permanent;
	}

	public void setPermanent(Integer permanent) {
		this.permanent = permanent;
	}

	@Override
	public String toString() {
		return "Json_RedirectWtp [cn=" + cn + ", host=" + host + ", version="
				+ version + ", client_key=" + client_key + ", root_cert="
				+ root_cert + ", client_cert=" + client_cert
				+ ", client_key_pwd=" + client_key_pwd + ", permanent="
				+ permanent + "]";
	}
}
