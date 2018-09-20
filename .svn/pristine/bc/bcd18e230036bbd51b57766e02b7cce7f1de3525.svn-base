package com.littlecloud.control.entity.branch;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "device_firmware_schedules", catalog = "littlecloud_branch_production")
public class DeviceFirmwareSchedules extends DBObject implements java.io.Serializable
{
	public static final Integer LEVEL_ORGANIZATION = 10;
	public static final Integer LEVEL_NETWORK = 20;
	public static final Integer LEVEL_DEVICE = 30;
	public static final String SCHEDULE_TIME_IMMEDIATELY = "immediately";
	public static final String SCHEDULE_TIME_SCHEDULED = "scheduled";
	private Integer id;
	private String organization_id;
	private Integer network_id;
	private String sn;
	private Integer iana_id;
	private Integer product_id;
	private String fw_version;
	private String fw_url;
	private Integer schedule_time;
	private Integer upgrade_time;
	private String release_type;
	private Integer level;
	private Integer trial_round;
	private Date created_at;
	private Integer status;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "organization_id")
	public String getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}
	
	@Column(name = "network_id")
	public Integer getNetwork_id() {
		return network_id;
	}
	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}
	
	@Column(name = "sn")
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	
	@Column(name = "iana_id")
	public Integer getIana_id() {
		return iana_id;
	}
	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}
	
	@Column(name = "product_id")
	public Integer getProduct_id() {
		return product_id;
	}
	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}
	@Column(name = "fw_version")
	public String getFw_version() {
		return fw_version;
	}
	public void setFw_version(String fw_version) {
		this.fw_version = fw_version;
	}
	
	@Column(name = "fw_url")
	public String getFw_url() {
		return fw_url;
	}
	public void setFw_url(String fw_url) {
		this.fw_url = fw_url;
	}
	
	@Column(name = "schedule_time")
	public Integer getSchedule_time() {
		return schedule_time;
	}
	public void setSchedule_time(Integer schedule_time) {
		this.schedule_time = schedule_time;
	}
	
	@Column(name = "upgrade_time")
	public Integer getUpgrade_time() {
		return upgrade_time;
	}
	public void setUpgrade_time(Integer upgrade_time) {
		this.upgrade_time = upgrade_time;
	}
	
	@Column(name = "release_type")
	public String getRelease_type() {
		return release_type;
	}
	public void setRelease_type(String release_type) {
		this.release_type = release_type;
	}
	
	@Column(name = "level")
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	
	@Column(name = "trial_round")
	public Integer getTrial_round() {
		return trial_round;
	}
	public void setTrial_round(Integer trial_round) {
		this.trial_round = trial_round;
	}
	
	@Column(name = "created_at")
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceFirmwareSchedules [id=");
		builder.append(id);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", product_id=");
		builder.append(product_id);
		builder.append(", fw_version=");
		builder.append(fw_version);
		builder.append(", fw_url=");
		builder.append(fw_url);
		builder.append(", schedule_time=");
		builder.append(schedule_time);
		builder.append(", upgrade_time=");
		builder.append(upgrade_time);
		builder.append(", release_type=");
		builder.append(release_type);
		builder.append(", level=");
		builder.append(level);
		builder.append(", trial_round=");
		builder.append(trial_round);
		builder.append(", created_at=");
		builder.append(created_at);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceFirmwareSchedules other = (DeviceFirmwareSchedules) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
