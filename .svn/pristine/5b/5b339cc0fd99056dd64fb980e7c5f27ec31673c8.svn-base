package com.littlecloud.control.json.model.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class JsonConf_AdminRadius extends JsonConf {

	//private static final Logger log = Logger.getLogger(JsonConf_AdminRadius.class);

	public static final List<String> FULL_CONFIG_KEY = initFULL_CONFIG_KEY();
	
	public static enum ADMIN_AUTH_TYPE {
		mschapv2, pap
	}
	
	public static enum ADMIN_AUTHEN {
		device_managed, random_passwd, by_radius
	}
	
	private boolean managed;
	private ADMIN_AUTHEN admin_auth;
	private transient boolean admin_pass_generate;
	
	private boolean user_pass_enable;
	private transient boolean user_pass_generate;
	
	@ConfigValue(name = "ADMIN_RADIUS_ENABLE", valueMap = "true=yes|false=${setnull}") 
	private boolean radius_enable;

	@ConfigValue(name = "ADMIN_RADIUS_AUTH_TYPE", valueMap = "")
	private String auth_type;
	
	@ConfigValue(name = "ADMIN_RADIUS_AUTH_HOST", valueMap = "")
	private String auth_host;
	
	@ConfigValue(name = "ADMIN_RADIUS_AUTH_PORT", valueMap = "")
	private Integer auth_port;
	
	@ConfigValue(name = "ADMIN_RADIUS_AUTH_SECRET", valueMap = "")
	private String auth_secret;
	
	@ConfigValue(name = "ADMIN_RADIUS_AUTH_TIMEOUT", valueMap = "")
	private Integer auth_timeout;
	
	@ConfigValue(name = "ADMIN_RADIUS_ACCT_HOST", valueMap = "")
	private String acc_host;
	
	@ConfigValue(name = "ADMIN_RADIUS_ACCT_PORT", valueMap = "")
	private Integer acc_port;
	
	@ConfigValue(name = "ADMIN_RADIUS_ACCT_SECRET", valueMap = "")
	private String acc_secret;

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
		
		for (Field f : JsonConf_AdminRadius.class.getDeclaredFields()) {
			ConfigValue value = f.getAnnotation(ConfigValue.class);
			if (value!=null)
				result.add(value.name());
		}
		return result;
	}

	
	/*** getters and setters ***/
	
	public boolean isRadius_enable() {
		return radius_enable;
	}

	public void setRadius_enable(boolean radius_enable) {
		this.radius_enable = radius_enable;
	}
	
	public String getAuth_type() {
		return auth_type;
	}

	public void setAuth_type(String auth_type) {
		this.auth_type = auth_type;
	}

	public String getAuth_host() {
		return auth_host;
	}

	public void setAuth_host(String auth_host) {
		this.auth_host = auth_host;
	}

	public Integer getAuth_port() {
		return auth_port;
	}

	public void setAuth_port(Integer auth_port) {
		this.auth_port = auth_port;
	}

	public String getAuth_secret() {
		return auth_secret;
	}

	public void setAuth_secret(String auth_secret) {
		this.auth_secret = auth_secret;
	}

	public Integer getAuth_timeout() {
		return auth_timeout;
	}

	public void setAuth_timeout(Integer auth_timeout) {
		this.auth_timeout = auth_timeout;
	}

	public String getAcc_host() {
		return acc_host;
	}

	public void setAcc_host(String acc_host) {
		this.acc_host = acc_host;
	}

	public Integer getAcc_port() {
		return acc_port;
	}

	public void setAcc_port(Integer acc_port) {
		this.acc_port = acc_port;
	}

	public String getAcc_secret() {
		return acc_secret;
	}

	public void setAcc_secret(String acc_secret) {
		this.acc_secret = acc_secret;
	}

	public boolean isManaged() {
		return managed;
	}

	public void setManaged(boolean managed) {
		this.managed = managed;
	}

	public ADMIN_AUTHEN getAdmin_auth() {
		return admin_auth;
	}

	public void setAdmin_auth(ADMIN_AUTHEN admin_auth) {
		this.admin_auth = admin_auth;
	}

	public boolean isAdmin_pass_generate() {
		return admin_pass_generate;
	}

	public void setAdmin_pass_generate(boolean admin_pass_generate) {
		this.admin_pass_generate = admin_pass_generate;
	}

	public boolean isUser_pass_enable() {
		return user_pass_enable;
	}

	public void setUser_pass_enable(boolean user_pass_enable) {
		this.user_pass_enable = user_pass_enable;
	}

	public boolean isUser_pass_generate() {
		return user_pass_generate;
	}

	public void setUser_pass_generate(boolean user_pass_generate) {
		this.user_pass_generate = user_pass_generate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonConf_AdminRadius [managed=");
		builder.append(managed);
		builder.append(", admin_auth=");
		builder.append(admin_auth);
		builder.append(", user_pass_enable=");
		builder.append(user_pass_enable);
		builder.append(", enable=");
		builder.append(radius_enable);
		builder.append(", auth_type=");
		builder.append(auth_type);
		builder.append(", auth_host=");
		builder.append(auth_host);
		builder.append(", auth_port=");
		builder.append(auth_port);
		builder.append(", auth_secret=");
		builder.append(auth_secret);
		builder.append(", auth_timeout=");
		builder.append(auth_timeout);
		builder.append(", acc_host=");
		builder.append(acc_host);
		builder.append(", acc_port=");
		builder.append(acc_port);
		builder.append(", acc_secret=");
		builder.append(acc_secret);
		builder.append("]");
		return builder.toString();
	}
}
