package com.littlecloud.ac.json.model;

import java.util.Date;

import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.control.json.JsonExclude;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;

public class Json_AcLicenseObject {

	@JsonExclude
	private String pK = null;

	private String enc;
	private Integer license_devices;
	private Integer register_devices;
	private Integer expiry_date;
	private Integer timestamp;

	public Integer getLicense_devices() {
		return license_devices;
	}

	public void setLicense_devices(Integer license_devices) {
		this.license_devices = license_devices;
	}

	public Integer getRegister_devices() {
		return register_devices;
	}

	public void setRegister_devices(Integer register_devices) {
		this.register_devices = register_devices;
	}

	public Integer getExpiry_date() {
		return expiry_date;
	}

	public void setExpiry_date(Integer expiry_date) {
		this.expiry_date = expiry_date;
	}

	public Integer getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}

	public String getpK() {
		return pK;
	}

	public void setpK(String pK) {
		this.pK = pK;
	}

	public String getEnc() {
		return enc;
	}

	public void setEnc(String enc) {
		this.enc = enc;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Json_AcLicenseObject [pK=");
		builder.append(pK);
		builder.append(", enc=");
		builder.append(enc);
		builder.append(", license_devices=");
		builder.append(license_devices);
		builder.append(", register_devices=");
		builder.append(register_devices);
		builder.append(", expiry_date=");
		builder.append(expiry_date);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append("]");
		return builder.toString();
	}

	public static void main(String args[]) {
		
		QueryInfo<Json_AcLicenseObject> info = new QueryInfo<Json_AcLicenseObject>();
		Json_AcLicenseObject lic = new Json_AcLicenseObject();
		lic.setExpiry_date(DateUtils.getUnixtime()+86400);
		lic.setTimestamp(DateUtils.getUnixtime());
		lic.setLicense_devices(100);
		lic.setRegister_devices(1000);
		lic.setpK("");
		lic.setEnc("sCJd6gku/lejToc1SM9svRT21puQX8lf8eMgYB0qb6P0jQsKCVK3mjEAgSVfRgIx+ClxsJXKHmcQHhcPh0DAvRhWE8Oz31sYq5kpGP0lqDWRaVfg9XM9Ekj3dG6Cecn4hkusY0nB9wOZx8fRIWHZfiIsaDuTUZ1uR2CuNZY9kzCGEXHhy6GGZLcrGk/etMN7KR7bnuaohCMCzDUNT+ahtE3SPsNvf0ebFNxR/Yam3X8dJ8Og/wdnhtCm91zZx7YsXj2M0Y0wFKq/wQszNO7dtkIQ1ljvA6v4oXZjUSWO0ZiXbCX5ciHSQpJHKWNdJSqz3Jd2Vr4zV8Mky73O6AqOFw==");
		info.setType(MessageType.PIPE_INFO_AC_LICENSE);
		info.setData(lic);
		System.out.println(JsonUtils.toJson(info));
		
		String json = " {\r\n" + 
				"    \"enc\": \"sCJd6gku/lejToc1SM9svRT21puQX8lf8eMgYB0qb6P0jQsKCVK3mjEAgSVfRgIx+ClxsJXKHmcQHhcPh0DAvRhWE8Oz31sYq5kpGP0lqDWRaVfg9XM9Ekj3dG6Cecn4hkusY0nB9wOZx8fRIWHZfiIsaDuTUZ1uR2CuNZY9kzCGEXHhy6GGZLcrGk/etMN7KR7bnuaohCMCzDUNT+ahtE3SPsNvf0ebFNxR/Yam3X8dJ8Og/wdnhtCm91zZx7YsXj2M0Y0wFKq/wQszNO7dtkIQ1ljvA6v4oXZjUSWO0ZiXbCX5ciHSQpJHKWNdJSqz3Jd2Vr4zV8Mky73O6AqOFw==\",\r\n" + 
				"    \"license_devices\": 100,\r\n" + 
				"    \"register_devices\": 1000,\r\n" + 
				"    \"expiry_date\": 1405672890,\r\n" + 
				"    \"timestamp\": 1405586490\r\n" + 
				"  }";
		System.out.println("json="+json);
		
		Json_AcLicenseObject licobj = JsonUtils.fromJson(json, Json_AcLicenseObject.class);
		System.out.println("licobj="+JsonUtils.toJson(licobj));
	}
}
