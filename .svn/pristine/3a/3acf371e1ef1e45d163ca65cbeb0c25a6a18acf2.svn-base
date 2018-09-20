package com.littlecloud.pool.object;

import java.io.Serializable;

import com.littlecloud.ac.json.model.Json_Config_Icmg;

public class IcmgPutObject extends PoolObject implements PoolObjectIf, Serializable{
	private Integer version = 1;
	
	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference
	
	private Json_Config_Icmg icmg;
	
	private Integer status;		
	private Boolean success;
	private Integer retry = 0;
	private String message;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Boolean isSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public Integer getRetry() {
		return retry;
	}
	public void setRetry(Integer retry) {
		this.retry = retry;
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
	public Boolean getSuccess() {
		return success;
	}
	public Json_Config_Icmg getIcmg() {
		return icmg;
	}
	public void setIcmg(Json_Config_Icmg icmg) {
		this.icmg = icmg;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IcmgPutObject [version=");
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
		builder.append(", icmg=");
		builder.append(icmg);
		builder.append(", status=");
		builder.append(status);
		builder.append(", success=");
		builder.append(success);
		builder.append(", retry=");
		builder.append(retry);
		builder.append(", message=");
		builder.append(message);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
