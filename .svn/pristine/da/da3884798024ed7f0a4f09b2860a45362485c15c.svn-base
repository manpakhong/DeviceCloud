package com.littlecloud.control.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.SerializedName;
import com.peplink.api.db.DBObject;

@Entity
@Table(name = "captive_portals")
public class CaptivePortal extends DBObject {
	public static final String DEFAULT_LANG = "en";
	
	public static final String OPEN = "open";
	public static final String GUEST = "guest";
	public static final String TOKEN = "token";
	public static final String FB_LIKE = "fb_like";
	public static final String FB_CHECKIN = "fb_checkin";
	public static final String FB_SHARE = "fb_share";
	public static final String FB_WIFI = "fb_wifi";
	public static final String WECHAT_FOLLOW = "wechat_follow";
	public static final String GOOGLE_AUTH = "google_auth";
	public static final String TWITTER_FOLLOW = "twitter_follow";
	public static final String LINKEDIN_FOLLOW = "linkedin_follow";
	public static final String WEIBO_FOLLOW = "weibo_follow";
	public static final String INSTAGRAM_FOLLOW = "instagram_follow";
	public static final String PINTEREST_FOLLOW = "pinterest_follow";
	
	public static final int QUOTA_TYPE_NONE = 0;
	public static final int QUOTA_TYPE_BANDWIDTH = 1;
	public static final int QUOTA_TYPE_TIME = 2;
	
	transient int id;
	transient String organizationId;
	transient int networkId;
	transient int deviceId;
	transient int ianaId;
	transient String sn;
	transient String ssid;
	transient int ssidId;
	@SerializedName("access_mode") String accessMode;
	@SerializedName("user_group_id") String userGroupId;
	@SerializedName("user_group_name") String userGroupName;
	transient @SerializedName("template_id") String templateId;
	@SerializedName("upload_filename") String uploadFilename; 
	transient byte[] logoData;
	@SerializedName("logo_filename") String logoFilename;
	@SerializedName("splash_page") String splashPage;
	@SerializedName("user_home_page") String userHomePage;
	@SerializedName("fb_gateway_id") String fbGatewayId;
	@SerializedName("content_md5sum") String contentMd5sum; 
	transient String fbGatewaySecret;
	transient boolean enabled;
	transient java.util.Date createdAt;
	transient java.util.Date updatedAt;
	transient String defaultLang;
	@SerializedName("mode_settings") HashMap <String, CaptivePortalAccessSettings> accessModeSettings = null;
	HashMap <String, HashMap<String, String>> contents = new HashMap <String, HashMap<String, String>>(); 
	transient HashSet <String> supportedLang = new HashSet<String>();
	
	public CaptivePortal(){
		
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

	@Column(name = "network_id", nullable = false)
	public int getNetworkId() {
		return networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}

	@Column(name = "mode", nullable = false)
	public String getAccessMode() {
		return accessMode;
	}

	public void setAccessMode(String accessMode) {
		this.accessMode = accessMode;
	}

	@Column(name = "template_id", nullable = false)
	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	@Column(name = "logo_data")
	public byte[] getLogoData() {
		return logoData;
	}

	public void setLogoData(byte[] logo) {
		this.logoData = logo;
	}
	
	@Column(name = "logo_filename")
	public String getLogoFilename() {
		return logoFilename;
	}

	public void setLogoFilename(String logoFilename) {
		this.logoFilename = logoFilename;
	}

	public String getUploadFilename() {
		return uploadFilename;
	}

	public void setUploadFilename(String uploadFilename) {
		this.uploadFilename = uploadFilename;
	}

	@Column(name = "landing_page")
	public String getSplashPage() {
		return splashPage;
	}

	public void setSplashPage(String splashPage) {
		this.splashPage = splashPage;
	}

	@Column(name = "user_home_page")
	public String getUserHomePage() {
		return userHomePage;
	}

	public void setUserHomePage(String userHomePage) {
		this.userHomePage = userHomePage;
	}

	@Column(name = "enabled")
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = "device_id")
	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	@Column(name = "ssid_id")
	public int getSsidId() {
		return ssidId;
	}

	public void setSsidId(int ssidId) {
		this.ssidId = ssidId;
	}

	public String getDefaultLang() {
		if (defaultLang == null)
			return DEFAULT_LANG;
		
		return defaultLang;
	}

	public void setDefaultLang(String defaultLang) {
		this.defaultLang = defaultLang;
	}

	@Column(name = "user_group_id")
	public String getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(String userGroupId) {
		this.userGroupId = userGroupId;
	}

	public String getUserGroupName() {
		return userGroupName;
	}

	public void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}

	@Column(name = "created_at")
	public java.util.Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(java.util.Date createdAt) {
		this.createdAt = createdAt;
	}

	@Column(name = "updated_at")
	public java.util.Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(java.util.Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getIanaId() {
		return ianaId;
	}

	public void setIanaId(int ianaId) {
		this.ianaId = ianaId;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	@Column(name = "fb_gateway_id")
	public String getFbGatewayId() {
		return fbGatewayId;
	}

	public void setFbGatewayId(String fbGatewayId) {
		this.fbGatewayId = fbGatewayId;
	}

	@Column(name = "fb_gateway_secret")
	public String getFbGatewaySecret() {
		return fbGatewaySecret;
	}

	public void setFbGatewaySecret(String fbGatewaySecret) {
		this.fbGatewaySecret = fbGatewaySecret;
	}

	public void addContent(CaptivePortalContent content) {
		HashMap<String, String> langContent = contents.get(content.getLang());
		
		if (langContent == null)
			langContent = new HashMap<String, String>();
		
		langContent.put(content.getContentType(), content.getContent());
		contents.put(content.getLang(), langContent);
		supportedLang.add(content.getLang());
	}
	
	public String getContent(String contentType, String lang) {
		if (contents != null && contentType != null && lang != null) {
			HashMap<String, String> langContent = contents.get(lang);
			if (langContent != null) {
				return langContent.get(contentType);
			}
		}
		
		return null;
	}
	
	public HashMap<String, HashMap<String, String>> getContents() {
		return contents;
	}

	public void setContents(HashMap<String, HashMap<String, String>> contents) {
		this.contents = contents;
	}

	public HashMap<String, CaptivePortalAccessSettings> getAccessModeSettings() {
		return accessModeSettings;
	}

	public void setAccessModeSettings(
			HashMap<String, CaptivePortalAccessSettings> accessModeSettings) {
		this.accessModeSettings = accessModeSettings;
	}

	public CaptivePortalAccessSettings getModeSettings(String accessMode) {
		if (accessModeSettings != null) {
			return accessModeSettings.get(accessMode);
		}
		
		return null;
	}

	public void setModeSettings(CaptivePortalAccessSettings modeSetting) {
		if (accessModeSettings == null)
			accessModeSettings = new HashMap<String, CaptivePortalAccessSettings>();

		accessModeSettings.put(modeSetting.getAccessMode(), modeSetting);
	}
	
	public void removeModeSettings(String accessMode) {
		if (accessModeSettings != null) {
			accessModeSettings.remove(accessMode);
		}
	}

	public HashSet<String> getSupportedLang() {
		return supportedLang;
	}

	public void setSupportedLang(HashSet<String> supportedLang) {
		this.supportedLang = supportedLang;
	}

	public boolean isSupportedLanguage(String lang) {
		return supportedLang.contains(lang);
	}
	
	public boolean isGuest() {
		return accessMode != null && accessMode.contains(GUEST);
	}
	
	public boolean isOpen() {
		return accessMode == null || accessMode.contains(OPEN);
	}
	
	public boolean isFBLike() {
		return accessMode == null || accessMode.contains(FB_LIKE);
	}
	
	public boolean isFBCheckin() {
		return accessMode == null || accessMode.contains(FB_CHECKIN);
	}
	
	public boolean isFBShare() {
		return accessMode == null || accessMode.contains(FB_SHARE);
	}
	
	public boolean isFBWifi() {
		return accessMode == null || accessMode.contains(FB_WIFI);
	}
	
	public boolean isToken() {
		return accessMode != null && accessMode.contains(TOKEN);
	}
	
	public boolean isLinkedinFollow() {
		return accessMode == null || accessMode.contains(LINKEDIN_FOLLOW);
	}
	
	public boolean isTwitterFollow() {
		return accessMode == null || accessMode.contains(TWITTER_FOLLOW);
	}
	
	public boolean isWeiboFollow() {
		return accessMode == null || accessMode.contains(WEIBO_FOLLOW);
	}
	
	public boolean isInstagramFollow() {
		return accessMode == null || accessMode.contains(INSTAGRAM_FOLLOW);
	}
	
	public boolean isPinterestFollow() {
		return accessMode == null || accessMode.contains(PINTEREST_FOLLOW);
	}
	
	public boolean isGoogleAuth() {
		return accessMode == null || accessMode.contains(GOOGLE_AUTH);
	}
	
	public boolean isWechatFollow() {
		return accessMode != null && accessMode.contains(WECHAT_FOLLOW);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CaptivePortal [accessMode=");
		builder.append(accessMode);
		builder.append(", userGroupId=");
		builder.append(userGroupId);
		builder.append(", templateId=");
		builder.append(templateId);
		builder.append(", uploadFilename=");
		builder.append(uploadFilename);
		builder.append(", logoFilename=");
		builder.append(logoFilename);
		builder.append(", splashPage=");
		builder.append(splashPage);
		builder.append(", contents=");
		builder.append(contents);
		builder.append("]");
		return builder.toString();
	}
}
