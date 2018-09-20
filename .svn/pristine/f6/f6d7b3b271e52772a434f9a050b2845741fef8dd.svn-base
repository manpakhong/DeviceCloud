package com.littlecloud.control.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.gson.annotations.SerializedName;
import com.peplink.api.db.DBObject;

@Entity
@Table(name = "captive_portal_users")
public class CaptivePortalUser extends DBObject {
	public static final int USER_TYPE_GUEST = 1;
	public static final int USER_TYPE_TOKEN = 2;
	public static final int USER_TYPE_GOOGLE = 3;
	
	public static final String STATUS_ACTIVE = "active";
	public static final String STATUS_INACTIVE = "inactive";
	
	int id;
	@SerializedName("batch_id") String batchId;
	@SerializedName("user_group_id") String userGroupId;
	String username;
	String password;
	@SerializedName("access_token") String accessToken;
	@SerializedName("concurrent_login") boolean concurrentLogin;
	@SerializedName("use_quota") boolean useQuota;
	@SerializedName("bandwidth_quota") int bandwidthQuota;
	@SerializedName("daily_quota") boolean dailyQuota;
	@SerializedName("expiry_in_days") int expiryInDays;
	@SerializedName("reset_quota") boolean resetQuota;
	@SerializedName("reset_day") int resetDay;
	@SerializedName("reset_hour") int resetHour;
	@SerializedName("reset_minute") int resetMinute;
	@SerializedName("expiry_date") Date expiryDate;
	@SerializedName("activation_date") Date activationDate;
	@SerializedName("quota_start_date") Date quotaStartDate;
	@SerializedName("last_login_time") Date lastLoginTime;
	transient int userType;
	@SerializedName("no_usage_expiry_day") int noUsageExpiryDay;
	@SerializedName("status") String status;
	transient Date createdAt;
	transient Date updatedAt;
	@SerializedName("max_concurrent_session") int maxConcurrentSession = 5;
	String action;
	@SerializedName("remain_bandwidth") int remainBandwidth = 0;
	boolean expired;

	public CaptivePortalUser() {

	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "batch_id")
	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	@Column(name = "username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "access_token")
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@Column(name = "user_type")
	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}
	
	public boolean isGuestUser() {
		return userType == USER_TYPE_GUEST;
	}
	
	public boolean isTokenUser() {
		return userType == USER_TYPE_TOKEN;
	}

	@Column(name = "use_quota")
	public boolean isUseQuota() {
		return useQuota;
	}

	public void setUseQuota(boolean useQuota) {
		this.useQuota = useQuota;
	}

	@Column(name = "bandwidth_quota")
	public int getBandwidthQuota() {
		return bandwidthQuota;
	}

	public void setBandwidthQuota(int bandwidthQuota) {
		this.bandwidthQuota = bandwidthQuota;
	}

	@Column(name = "daily_quota")
	public boolean isDailyQuota() {
		return dailyQuota;
	}

	public void setDailyQuota(boolean dailyQuota) {
		this.dailyQuota = dailyQuota;
	}

	@Column(name = "expiry_date")
	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Column(name = "activate_date")
	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	@Column(name = "quota_start_date")
	public Date getQuotaStartDate() {
		return quotaStartDate;
	}

	public void setQuotaStartDate(Date quotaStartDate) {
		this.quotaStartDate = quotaStartDate;
	}

	@Column(name = "user_group_id")
	public String getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(String userGroupId) {
		this.userGroupId = userGroupId;
	}

	@Column(name = "concurrent_login")
	public boolean isConcurrentLogin() {
		return concurrentLogin;
	}

	public void setConcurrentLogin(boolean concurrentLogin) {
		this.concurrentLogin = concurrentLogin;
	}

	@Column(name = "expiry_in_days")
	public int getExpiryInDays() {
		return expiryInDays;
	}

	public void setExpiryInDays(int expiryInDays) {
		this.expiryInDays = expiryInDays;
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
		this.resetHour = resetHour;
	}

	@Column(name = "reset_minute")
	public int getResetMinute() {
		return resetMinute;
	}

	public void setResetMinute(int resetMinute) {
		this.resetMinute = resetMinute;
	}

	@Column(name = "concurrent_session")
	public int getMaxConcurrentSession() {
		return maxConcurrentSession;
	}

	public void setMaxConcurrentSession(int maxConcurrentSession) {
		this.maxConcurrentSession = maxConcurrentSession;
	}

	@Column(name = "no_usage_expiry_day")
	public int getNoUsageExpiryDay() {
		return noUsageExpiryDay;
	}

	public void setNoUsageExpiryDay(int noUsageExpiryDay) {
		this.noUsageExpiryDay = noUsageExpiryDay;
	}

	@Column(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isActive() {
		return status != null && status.equals(STATUS_ACTIVE);
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_login_time")
	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable=false)
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at", nullable=false)
	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getRemainBandwidth() {
		return remainBandwidth;
	}

	public void setRemainBandwidth(int remainBandwidth) {
		this.remainBandwidth = remainBandwidth;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}
	
	public boolean checkExpired() {
		if (expiryDate != null) {
			if (System.currentTimeMillis() > expiryDate.getTime()) {
				expired = true;
			}
		} else {
			if (activationDate != null && expiryInDays > 0) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(activationDate);
				cal.add(Calendar.DAY_OF_MONTH, expiryInDays);
				if (cal.getTimeInMillis() > System.currentTimeMillis()) {
					expired = true;
				}
				
				if (expiryDate == null) {
					expiryDate = new java.util.Date();
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CaptivePortalUser [id=");
		builder.append(id);
		builder.append(", batchId=");
		builder.append(batchId);
		builder.append(", userGroupId=");
		builder.append(userGroupId);
		builder.append(", username=");
		builder.append(username);
		builder.append(", password=");
		builder.append(password);
		builder.append(", accessToken=");
		builder.append(accessToken);
		builder.append(", concurrentLogin=");
		builder.append(concurrentLogin);
		builder.append(", useQuota=");
		builder.append(useQuota);
		builder.append(", bandwidthQuota=");
		builder.append(bandwidthQuota);
		builder.append(", dailyQuota=");
		builder.append(dailyQuota);
		builder.append(", expiryInDays=");
		builder.append(expiryInDays);
		builder.append(", resetQuota=");
		builder.append(resetQuota);
		builder.append(", resetDay=");
		builder.append(resetDay);
		builder.append(", resetHour=");
		builder.append(resetHour);
		builder.append(", resetMinute=");
		builder.append(resetMinute);
		builder.append(", expiryDate=");
		builder.append(expiryDate);
		builder.append(", activationDate=");
		builder.append(activationDate);
		builder.append(", lastLoginTime=");
		builder.append(lastLoginTime);
		builder.append(", noUsageExpiryDay=");
		builder.append(noUsageExpiryDay);
		builder.append(", status=");
		builder.append(status);
		builder.append(", maxConncurrentSession=");
		builder.append(maxConcurrentSession);
		builder.append(", action=");
		builder.append(action);
		builder.append(", remainBandwidth=");
		builder.append(remainBandwidth);
		builder.append("]");
		return builder.toString();
	}
	
}
