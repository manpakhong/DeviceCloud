package com.littlecloud.control.entity.root;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "branches")
public class RootBranches extends DBObject {

	protected int id;
	protected String url;
	protected String api;
	protected String ac1;
	protected Integer ac1_port;
	protected String ac2;
	protected Integer ac2_port;
//	protected String ac3;
//	protected Integer ac3_port;
	protected String name;
	protected Integer status;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "api")
	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	@Column(name = "ac1")
	public String getAc1() {
		return ac1;
	}

	public void setAc1(String ac1) {
		this.ac1 = ac1;
	}

	@Column(name = "ac1_port")
	public Integer getAc1_port() {
		return ac1_port;
	}

	public void setAc1_port(Integer ac1_port) {
		this.ac1_port = ac1_port;
	}

	@Column(name = "ac2")
	public String getAc2() {
		return ac2;
	}

	public void setAc2(String ac2) {
		this.ac2 = ac2;
	}

	@Column(name = "ac2_port")
	public Integer getAc2_port() {
		return ac2_port;
	}

	public void setAc2_port(Integer ac2_port) {
		this.ac2_port = ac2_port;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
