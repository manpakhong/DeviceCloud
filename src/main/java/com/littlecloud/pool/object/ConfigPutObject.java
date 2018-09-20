package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.annotations.SerializedName;
import com.littlecloud.ac.json.model.Json_Config_Icmg.Icmg;

public class ConfigPutObject extends PoolObject implements PoolObjectIf, Serializable{
	private Integer version = 1;
	
	public static final String MVPN_LOG_MSG = "PepVPN configuration has been updated by InControl";
	public static final String WLAN_LOG_MSG = "WiFi Configuration has been updated by InControl";
	
	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference
	
	private Integer status;
		
	private String config;
	private Boolean success;
	private String conf_checksum;
	private String cert_checksum;
	private String filepath;
	
	@SerializedName("feature")
	List<Icmg> icmgLst;
	
	List<String> config_type;
	List<String> log_msg;
	
	
	public List<Icmg> getIcmgLst() {
		return icmgLst;
	}
	public void setIcmgLst(List<Icmg> icmgLst) {
		this.icmgLst = icmgLst;
	}
	public List<String> getConfig_type() {
		return config_type;
	}
	public void setConfig_type(List<String> config_type) {
		this.config_type = config_type;
	}
	public List<String> getLog_msg() {
		return log_msg;
	}
	public void setLog_msg(List<String> log_msg) {
		this.log_msg = log_msg;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getConfig() {
		return config;
	}	
	public void setConfig(String config) {
		this.config = config;
	}
	public Boolean isSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getConf_checksum() {
		return conf_checksum;
	}
	public void setConf_checksum(String conf_checksum) {
		this.conf_checksum = conf_checksum;
	}
	public String getCert_checksum() {
		return cert_checksum;
	}
	public void setCert_checksum(String cert_checksum) {
		this.cert_checksum = cert_checksum;
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
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}	
	public Integer getIana_id() {
		return iana_id;
	}
	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}
	public Integer getDevice_id() {
		return device_id;
	}
	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}
	public Integer getNetwork_id() {
		return network_id;
	}
	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}
	public String getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public Boolean getSuccess() {
		return success;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigPutObject [version=");
		builder.append(version);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", sid=");
		builder.append(sid);
		builder.append(", status=");
		builder.append(status);
		builder.append(", config=");
		builder.append(config);
		builder.append(", success=");
		builder.append(success);
		builder.append(", conf_checksum=");
		builder.append(conf_checksum);
		builder.append(", cert_checksum=");
		builder.append(cert_checksum);
		builder.append(", filepath=");
		builder.append(filepath);
		builder.append(", icmgLst=");
		builder.append(icmgLst);
		builder.append(", config_type=");
		builder.append(config_type);
		builder.append(", log_msg=");
		builder.append(log_msg);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
