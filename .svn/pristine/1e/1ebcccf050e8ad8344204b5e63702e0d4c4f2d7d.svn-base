package com.littlecloud.control.json.model.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class JsonConf_Admin extends JsonConf {

	public static final String ADMIN_DEFAULT_USER_NAME = "admin";
	public static final String ADMIN_DEFAULT_READONLY_USER_NAME = "user";
	public static final String ADMIN_DEFAULT_HTTP_PORT = "80";
	public static final String ADMIN_DEFAULT_HTTPS_PORT = "443";
	
	public static final List<String> FULL_CONFIG_KEY = initFULL_CONFIG_KEY();
		
	/* user pass */
	public static enum ADMIN_AUTHEN {
		device_managed, random_passwd, disable
	}
	
	public static enum ADMIN_ACCESS_PROTOCOL {
		disable, http, https
	}
	
	@ConfigValue(name = "ADMIN_NAME", valueMap = "")
	private String admin_name;
	
	@ConfigValue(name = "ADMIN_PASSWORD", valueMap = "") 
	private String admin_password;
	
	@ConfigValue(name = "ADMIN_ROA_NAME", valueMap = "")
	private String admin_readonly_name;
	
	@ConfigValue(name = "ADMIN_ROA_PASSWORD", valueMap = "")
	private String admin_readonly_password;

	private Boolean managed;
	private ADMIN_AUTHEN admin_auth;
	private ADMIN_AUTHEN admin_user_auth;
	private ADMIN_ACCESS_PROTOCOL protocol;
	private Integer admin_auth_port;
	private transient boolean update_admin_pass;		
	private transient boolean update_user_pass;
	
	/* others */
	private Integer productId;
	
	/* protocol port */
	@ConfigValue(name = "ADMIN_HTTP_ENABLE", valueMap = "")
	private Boolean admin_http_enable;
	@ConfigValue(name = "ADMIN_IPLIST", valueMap = "")
	private String admin_iplist;
	@ConfigValue(name = "ADMIN_LANONLY", valueMap = "")
	private Boolean admin_lanonly;
	@ConfigValue(name = "ADMIN_PORT", valueMap = "")
	private String admin_port;

	@ConfigValue(name = "ADMIN_HTTPS_ENABLE", valueMap = "")
	private Boolean admin_https_enable;
	@ConfigValue(name = "ADMIN_HTTPS_IPLIST", valueMap = "")
	private String admin_https_iplist;
	@ConfigValue(name = "ADMIN_HTTPS_LANONLY", valueMap = "")
	private String admin_https_lanonly;
	@ConfigValue(name = "ADMIN_HTTPS_PORT", valueMap = "")
	private String admin_https_port;
	
	/* ap */
	@ConfigValue(name = "WEB_ADMINISTRATION", valueMap = "true=enable|false=disable")
	private Boolean web_administration;
	@ConfigValue(name = "WEB_ACCESS_PROTOCOL", valueMap = "")
	private String web_access_protocol;
	@ConfigValue(name = "WEB_MANAGEMENT_PORT", valueMap = "")
	private String web_management_port;
	@ConfigValue(name = "WEB_ADMIN_USERNAME", valueMap = "")
	private String web_admin_username;
	@ConfigValue(name = "WEB_ADMIN_PASSWORD", valueMap = "")
	private String web_admin_password;
	
	public String generateHardwareConfig(Class cls, CONFIG_TYPE configType) throws Exception
	{
		ConcurrentHashMap<Integer, String> confJsonMap = new ConcurrentHashMap<Integer, String>();
		if (traverseConfig(confJsonMap, "", this, cls, GLOBAL_DEV_INDEX, configType))
		{
			return confJsonMap.get(GLOBAL_DEV_INDEX);
		}
		return null;
	}

	private static List<String> initFULL_CONFIG_KEY() {
		List<String> result = new ArrayList<String>();
		
		for (Field f : JsonConf_Admin.class.getDeclaredFields()) {
			ConfigValue value = f.getAnnotation(ConfigValue.class);
			if (value!=null)
				result.add(value.name());
		}
		return result;
	}
	
	/*** getters and setters ***/
	public String getAdmin_name() {
		return admin_name;
	}

	public void setAdmin_name(String admin_name) {
		this.admin_name = admin_name;
	}

	public String getAdmin_password() {
		return admin_password;
	}

	public void setAdmin_password(String admin_password) {
		this.admin_password = admin_password;
	}

	public String getAdmin_readonly_name() {
		return admin_readonly_name;
	}

	public void setAdmin_readonly_name(String admin_readonly_name) {
		this.admin_readonly_name = admin_readonly_name;
	}

	public String getAdmin_readonly_password() {
		return admin_readonly_password;
	}

	public void setAdmin_readonly_password(String admin_readonly_password) {
		this.admin_readonly_password = admin_readonly_password;
	}

	public boolean isManaged() {
		if (managed==null || !managed)
			return false;
		else
			return true;
	}
	
	public Boolean getManaged() {
		return managed;
	}

	public void setManaged(Boolean managed) {
		this.managed = managed;
	}

	public ADMIN_AUTHEN getAdmin_auth() {
		return admin_auth;
	}

	public void setAdmin_auth(ADMIN_AUTHEN admin_auth) {
		this.admin_auth = admin_auth;
	}
	
	public ADMIN_AUTHEN getAdmin_user_auth() {
		return admin_user_auth;
	}

	public void setAdmin_user_auth(ADMIN_AUTHEN admin_user_auth) {
		this.admin_user_auth = admin_user_auth;
	}

	public boolean isUpdate_admin_pass() {
		return update_admin_pass;
	}

	public void setUpdate_admin_pass(boolean update_admin_pass) {
		this.update_admin_pass = update_admin_pass;
	}

	public boolean isUpdate_user_pass() {
		return update_user_pass;
	}

	public void setUpdate_user_pass(boolean update_user_pass) {
		this.update_user_pass = update_user_pass;
	}

	/* protocol */
	public Boolean getAdmin_http_enable() {
		return admin_http_enable;
	}

	public void setAdmin_http_enable(Boolean admin_http_enable) {
		this.admin_http_enable = admin_http_enable;
	}

	public String getAdmin_iplist() {
		return admin_iplist;
	}

	public void setAdmin_iplist(String admin_iplist) {
		this.admin_iplist = admin_iplist;
	}

	public ADMIN_ACCESS_PROTOCOL getProtocol() {
		return protocol;
	}

	public void setProtocol(ADMIN_ACCESS_PROTOCOL protocol) {
		this.protocol = protocol;
	}

	public Integer getAdmin_auth_port() {
		return admin_auth_port;
	}

	public void setAdmin_auth_port(Integer admin_auth_port) {
		this.admin_auth_port = admin_auth_port;
	}

	public Boolean getAdmin_lanonly() {
		return admin_lanonly;
	}

	public void setAdmin_lanonly(Boolean admin_lanonly) {
		this.admin_lanonly = admin_lanonly;
	}

	public String getAdmin_port() {
		return admin_port;
	}

	public void setAdmin_port(String admin_port) {
		this.admin_port = admin_port;
	}

	public Boolean getAdmin_https_enable() {
		return admin_https_enable;
	}

	public void setAdmin_https_enable(Boolean admin_https_enable) {
		this.admin_https_enable = admin_https_enable;
	}

	public String getAdmin_https_iplist() {
		return admin_https_iplist;
	}

	public void setAdmin_https_iplist(String admin_https_iplist) {
		this.admin_https_iplist = admin_https_iplist;
	}

	public String getAdmin_https_lanonly() {
		return admin_https_lanonly;
	}

	public void setAdmin_https_lanonly(String admin_https_lanonly) {
		this.admin_https_lanonly = admin_https_lanonly;
	}

	public String getAdmin_https_port() {
		return admin_https_port;
	}

	public void setAdmin_https_port(String admin_https_port) {
		this.admin_https_port = admin_https_port;
	}
	
	public Boolean getWeb_administration() {
		return web_administration;
	}

	public void setWeb_administration(Boolean web_administration) {
		this.web_administration = web_administration;
	}
	
	public String getWeb_access_protocol() {
		return web_access_protocol;
	}

	public void setWeb_access_protocol(String web_access_protocol) {
		this.web_access_protocol = web_access_protocol;
	}
	
	public String getWeb_management_port() {
		return web_management_port;
	}

	public void setWeb_management_port(String web_management_port) {
		this.web_management_port = web_management_port;
	}

	public String getWeb_admin_username() {
		return web_admin_username;
	}

	public void setWeb_admin_username(String web_admin_username) {
		this.web_admin_username = web_admin_username;
	}

	public String getWeb_admin_password() {
		return web_admin_password;
	}

	public void setWeb_admin_password(String web_admin_password) {
		this.web_admin_password = web_admin_password;
	}
	
	/* others */
	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonConf_Admin [admin_name=");
		builder.append(admin_name);
		builder.append(", admin_password=");
		builder.append(admin_password);
		builder.append(", admin_readonly_name=");
		builder.append(admin_readonly_name);
		builder.append(", admin_readonly_password=");
		builder.append(admin_readonly_password);
		builder.append(", managed=");
		builder.append(managed);
		builder.append(", admin_auth=");
		builder.append(admin_auth);
		builder.append(", admin_user_auth=");
		builder.append(admin_user_auth);
		builder.append(", protocol=");
		builder.append(protocol);
		builder.append(", admin_auth_port=");
		builder.append(admin_auth_port);
		builder.append(", update_admin_pass=");
		builder.append(update_admin_pass);
		builder.append(", update_user_pass=");
		builder.append(update_user_pass);
		builder.append(", productId=");
		builder.append(productId);
		builder.append(", admin_http_enable=");
		builder.append(admin_http_enable);
		builder.append(", admin_iplist=");
		builder.append(admin_iplist);
		builder.append(", admin_lanonly=");
		builder.append(admin_lanonly);
		builder.append(", admin_port=");
		builder.append(admin_port);
		builder.append(", admin_https_enable=");
		builder.append(admin_https_enable);
		builder.append(", admin_https_iplist=");
		builder.append(admin_https_iplist);
		builder.append(", admin_https_lanonly=");
		builder.append(admin_https_lanonly);
		builder.append(", admin_https_port=");
		builder.append(admin_https_port);
		builder.append(", web_administration=");
		builder.append(web_administration);
		builder.append(", web_access_protocol=");
		builder.append(web_access_protocol);
		builder.append(", web_management_port=");
		builder.append(web_management_port);
		builder.append(", web_admin_username=");
		builder.append(web_admin_username);
		builder.append(", web_admin_password=");
		builder.append(web_admin_password);
		builder.append("]");
		return builder.toString();
	}
}
