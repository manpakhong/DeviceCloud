package com.littlecloud.control.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.google.gson.annotations.SerializedName;
import com.peplink.api.db.DBObject;

@Entity
@Table(name = "captive_portal_access_settings")
public class CaptivePortalAccessSettings extends DBObject {
	public static final int QUOTA_TYPE_NONE = 0;
	public static final int QUOTA_TYPE_BANDWIDTH = 1;
	public static final int QUOTA_TYPE_TIME = 2;
	
	transient CaptivePortalAccessSettingsId id;
	transient @SerializedName("access_mode") String accessMode;
	@SerializedName("quota_type") int quotaType;
	@SerializedName("bandwidth_quota") int bandwidthQuota;
	@SerializedName("time_quota") int timeQuota;
	@SerializedName("reset_quota") boolean resetQuota;
	@SerializedName("reset_day") int resetDay;
	@SerializedName("reset_hour") int resetHour;
	@SerializedName("reset_minute") int resetMinute;
	@SerializedName("reset_time") String resetTime;
	transient String extraSetting;
	@SerializedName("settings") CaptivePortalExtraSettings cpExtraSetting;
	
	public CaptivePortalAccessSettings(){}
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "cpId", column = @Column(name = "cp_id", nullable = false)),
			@AttributeOverride(name = "mode", column = @Column(name = "mode", nullable = false)) })
	public CaptivePortalAccessSettingsId getId() {
		return id;
	}
	
	public void setId(CaptivePortalAccessSettingsId id) {
		this.id = id;
		accessMode = id.getMode();
	}
	
	public String getAccessMode() {
		return accessMode;
	}

	public void setAccessMode(String accessMode) {
		this.accessMode = accessMode;
	}

	@Column(name = "quota_type")
	public int getQuotaType() {
		return quotaType;
	}

	public void setQuotaType(int quotaType) {
		this.quotaType = quotaType;
	}

	@Column(name = "bandwidth_quota")
	public int getBandwidthQuota() {
		return bandwidthQuota;
	}

	public void setBandwidthQuota(int bandwidthQuota) {
		this.bandwidthQuota = bandwidthQuota;
	}

	@Column(name = "time_quota")
	public int getTimeQuota() {
		return timeQuota;
	}

	public void setTimeQuota(int timeQuota) {
		this.timeQuota = timeQuota;
	}

	@Column(name = "reset_quota")
	public boolean isResetQuota() {
		return resetQuota;
	}

	public void setResetQuota(boolean resetQuota) {
		this.resetQuota = resetQuota;
	}

	@Column(name = "reset_day")
	public int getResetDay() {
		return resetDay;
	}

	public void setResetDay(int resetDay) {
		this.resetDay = resetDay;
	}

	@Column(name = "reset_hour")
	public int getResetHour() {
		return resetHour;
	}

	public void setResetHour(int resetHour) {
		if (resetHour < 0)
			resetHour = 0;
		else if (resetHour > 23)
			resetHour = 23;
		
		this.resetHour = resetHour;
	}

	@Column(name = "reset_minute")
	public int getResetMinute() {
		return resetMinute;
	}

	public void setResetMinute(int resetMinute) {
		if (resetMinute < 0)
			resetMinute = 0;
		else if (resetMinute > 59)
			resetMinute = 59;
		
		this.resetMinute = resetMinute;
	}
	
	@Column(name = "extra_settings")
	public String getExtraSetting() {
		return extraSetting;
	}

	public void setExtraSetting(String extraSetting) {
		this.extraSetting = extraSetting;
	}
	
	public CaptivePortalExtraSettings getCpExtraSetting() {
		return cpExtraSetting;
	}

	public void setCpExtraSetting(CaptivePortalExtraSettings cpExtraSetting) {
		this.cpExtraSetting = cpExtraSetting;
	}

	public String getResetTime() {
		return resetTime;
	}
	
	public void setResetTime(String resetTime) {
		this.resetTime = resetTime;
	}
	
	public void parseData() {
		if (resetTime != null && resetTime.indexOf(":") > 0) {
			try {
				int resetHr = Integer.parseInt(resetTime.substring(0, resetTime.indexOf(":")).trim());
				int resetMin = Integer.parseInt(resetTime.substring(resetTime.indexOf(":") + 1, resetTime.length()).trim());
				
				setResetHour(resetHr);
				setResetMinute(resetMin);
			} catch (Exception e) {
			}
		} else {
			if (resetHour < 10)
				resetTime = "0" + resetHour + ":";
			else
				resetTime = resetHour + ":";
			
			if (resetMinute < 10)
				resetTime += "0" + resetMinute;
			else
				resetTime += resetMinute;
		}
	}

	public String getSettingId() {
		if (cpExtraSetting != null)
			return cpExtraSetting.getId();
		
		return null;
	}

	public String getSettingMessage() {
		if (cpExtraSetting != null)
			return cpExtraSetting.getMessage();

		return null;
	}
}
