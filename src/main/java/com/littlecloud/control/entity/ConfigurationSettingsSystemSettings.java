package com.littlecloud.control.entity;

import com.google.gson.annotations.SerializedName;

public class ConfigurationSettingsSystemSettings {
	@SerializedName("admin_password")
	String adminPassword;

	public ConfigurationSettingsSystemSettings() {
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}
}
