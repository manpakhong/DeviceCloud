package com.littlecloud.control.entity;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.SerializedName;
import com.peplink.api.db.DBObject;

@Entity
@Table(name = "captive_portal_user_groups")
public class CaptivePortalUserGroups extends DBObject {
	public static final String STATUS_TEMP = "temp";
	public static final String STATUS_ACTIVE = "active";
	
	@SerializedName("user_group_id") String userGroupId;
	@SerializedName("user_group_name") String description;
	@SerializedName("status") String status;
	@SerializedName("created_at") java.util.Date createdAt;
	@SerializedName("updated_at") java.util.Date updatedAt;
	@SerializedName("guest_account_count") int noOfUsers;
	@SerializedName("access_token_count") int noOfTokens;
	@SerializedName("google_account_count") int noOfGoogleUsers;
	@SerializedName("ssids") HashMap<Integer, String> ssids = new HashMap<Integer, String>();

	public CaptivePortalUserGroups() {
	}

	@Id
	@Column(name = "user_group_id", unique = true, nullable = false)
	public String getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(String userGroupId) {
		this.userGroupId = userGroupId;
	}

	@Column(name = "description", nullable = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "status", nullable = false)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "created_at", nullable = false)
	public java.util.Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(java.util.Date createdAt) {
		this.createdAt = createdAt;
	}

	@Column(name = "updated_at", nullable = false)
	public java.util.Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(java.util.Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public HashMap<Integer, String> getSsids() {
		return ssids;
	}

	public void setSsids(HashMap<Integer, String> ssids) {
		this.ssids = ssids;
	}
	
	public void addSsid(int ssidId, String ssid) {
		ssids.put(ssidId, ssid);
	}

	public int getNoOfUsers() {
		return noOfUsers;
	}

	public void setNoOfUsers(int noOfUsers) {
		this.noOfUsers = noOfUsers;
	}

	public int getNoOfTokens() {
		return noOfTokens;
	}

	public void setNoOfTokens(int noOfTokens) {
		this.noOfTokens = noOfTokens;
	}

	public int getNoOfGoogleUsers() {
		return noOfGoogleUsers;
	}

	public void setNoOfGoogleUsers(int noOfGoogleUsers) {
		this.noOfGoogleUsers = noOfGoogleUsers;
	}
}
