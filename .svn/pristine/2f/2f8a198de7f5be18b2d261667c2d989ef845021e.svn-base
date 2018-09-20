package com.littlecloud.control.json.model.config;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.littlecloud.control.entity.ConfigurationSsids;
import com.littlecloud.control.json.JsonExclude;
import com.littlecloud.control.json.util.JsonUtils;

/* Table: configuration_ssids */
public class JsonConf_SsidProfiles extends JsonConf {

	//@JsonExclude
	//public static final int MAX_SSIDPROFILE = 4; // LATER THE MAX_SSIDPROFILE should depends on device capability //...

	public static final String WLAN_ORDER_TAG = "WLAN_ORDER";
	public static final String WLAN_TAG = "WLAN";

	public static enum SECURITY {
		open, wpa_wpa2_personal, wpa_wpa2_enterprise, wep
	};

	public static enum KEY_FORMAT {
		ascii, hex
	};

	public static enum MAC_FILTER {
		none, deny, accept
	};
	
	public static enum PORTAL_AUTH {
		no_auth, open_auth, radius_auth, ic2_auth, fbwifi_auth
	}

	public static JsonConf_SsidProfiles parseConfigurationSsids(ConfigurationSsids ssid)
	{
		return JsonUtils.<JsonConf_SsidProfiles> fromJson(ssid.getConfig(), JsonConf_SsidProfiles.class);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonConf_SsidProfiles [network_id=");
		builder.append(network_id);
		builder.append(", ssid_id=");
		builder.append(ssid_id);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", ssid=");
		builder.append(ssid);
		builder.append(", security=");
		builder.append(security);
		builder.append(", wep=");
		builder.append(wep);
		builder.append(", layer2_isolation=");
		builder.append(layer2_isolation);
		builder.append(", radius_servers_list=");
		builder.append(radius_servers_list);
		builder.append(", mac_filter=");
		builder.append(mac_filter);
		builder.append(", mac_list=");
		builder.append(mac_list);
		builder.append(", multicast_filter=");
		builder.append(multicast_filter);
		builder.append(", multicast_rate=");
		builder.append(multicast_rate);
		builder.append(", broadcast=");
		builder.append(broadcast);
		builder.append(", icMg=");
		builder.append(icMg);
		builder.append(", vlan_id=");
		builder.append(vlan_id);
		builder.append(", radio_band=");
		builder.append(radio_band);
		builder.append(", radio_select=");
		builder.append(radio_select);
		builder.append(", band_steering=");
		builder.append(band_steering);
		builder.append(", portal_enabled=");
		builder.append(portal_enabled);
		builder.append(", portal_url=");
		builder.append(portal_url);
		builder.append(", portal_auth=");
		builder.append(portal_auth);
		builder.append(", portal_cna_bypass=");
		builder.append(portal_cna_bypass);
		builder.append(", portal_domain_accept=");
		builder.append(portal_domain_accept);
		builder.append(", fbwifi_gwid=");
		builder.append(fbwifi_gwid);
		builder.append(", fbwifi_secret=");
		builder.append(fbwifi_secret);
		builder.append(", version=");
		builder.append(version);
		builder.append(", wpa_personal=");
		builder.append(wpa_personal);
		builder.append("]");
		return builder.toString();
	}

	protected Integer network_id;
	@SerializedName("id")
	protected Integer ssid_id;
	@ConfigValue(name = "ADMIN_STATUS", valueMap = "true=enable|false=disable")
	protected Boolean enabled;
	@ConfigValue(name = "SSID", valueMap = "")
	protected String ssid;
	@ConfigValue(name = "SECURITY", valueMap = "open=none|wpa_wpa2_personal=WPAMIX|wpa_wpa2_enterprise=WPAMIX|wpa2_personal=WPA2|wpa2_enterprise=WPA2|wep=WEP", isCapitalValue = false)
	protected SECURITY security;
	@ConfigValue(name = "", valueMap = "")
	protected Wep wep;
	@ConfigValue(name = "L2_ISO", valueMap = "true=yes|false=no")
	protected Boolean layer2_isolation;
	@SerializedName("radius_servers")
	@ConfigValue(name = "RADIUS", valueMap = "")
	protected List<RadiusServer> radius_servers_list;
	@ConfigValue(name = "ACL_MODE", valueMap = "none=0|deny=1|accept=2")
	protected MAC_FILTER mac_filter;
	@ConfigValue(name = "ACL", valueMap = "")
	protected List<String> mac_list;
	@ConfigValue(name = "MCAST_FILTER", valueMap = "true=enable|false=disable")
	protected Boolean multicast_filter;
	@ConfigValue(name = "MCAST_FILTER_RATE", valueMap = "", isCapitalValue = true)
	protected String multicast_rate;
	@ConfigValue(name = "BROADCAST", valueMap = "true=yes|false=no")
	protected Boolean broadcast;
	@ConfigValue(name = "IC_MG", valueMap="true=1|false=${setnull}")
	protected Boolean icMg = true;
	@ConfigValue(name = "VLANID", valueMap = "")
	protected Integer vlan_id;	
	protected String radio_band;
	@ConfigValue(name = "RADIO_SELECTION", valueMap = "")
	protected String radio_select;
	@ConfigValue(name = "BAND_STEER", valueMap = "disable=disable|preferred=preferred|forced=forced")
	protected String band_steering;
	@ConfigValue(name = "PORTAL", valueMap = "true=enable|false=disable")
	protected Boolean portal_enabled;
	@ConfigValue(name = "PORTAL_URL", valueMap = "")
	protected String portal_url;
	@ConfigValue(name = "PORTAL_AUTH", valueMap = "no_auth=no_auth|open_auth=open_auth|radius_auth=radius_auth|ic2_auth=ic2_auth|fbwifi_auth=fbwifi_auth")
	protected String portal_auth;
	@ConfigValue(name = "PORTAL_CNA_BYPASS", valueMap = "true=enable|false=disable")
	protected Boolean portal_cna_bypass;
	@ConfigValue(name = "PORTAL_DOMAIN_ACCEPT", valueMap = "")
	protected String portal_domain_accept;	
	@ConfigValue(name = "FBWIFI_GWID", valueMap = "")
	protected String fbwifi_gwid;
	@ConfigValue(name = "FBWIFI_SECRET", valueMap = "")
	protected String fbwifi_secret;
	@JsonExclude
	protected int version = 2;

	@ConfigValue(name = "WPA", valueMap = "")
	protected WpaPersonal wpa_personal;

	public class WpaPersonal
	{
		@ConfigValue(name = "PSK", valueMap = "true=enable|false=disable")
		protected Boolean psk_enable;

		@ConfigValue(name = "PASSPHRASE", valueMap = "")
		protected String shared_key;

		public String getShared_key() {
			return shared_key;
		}

		public void setShared_key(String shared_key) {
			this.shared_key = shared_key;
		}

		public Boolean getPsk_enable() {
			return psk_enable;
		}

		public void setPsk_enable(Boolean psk_enable) {
			this.psk_enable = psk_enable;
		}

		@Override
		public String toString() {
			return "WpaPersonal [psk_not_enable=" + psk_enable + ", shared_key=" + shared_key + "]";
		}
	}

	public void setDefaultValues(int id) {
		this.setSsid_id(id);

		this.setSsid("Unconfigured SSID");
		this.setEnabled(false);
		// this.setSsid("Unconfigured SSID");
		this.setSecurity(SECURITY.open);
		this.setLayer2_isolation(false);
		this.setMac_filter(MAC_FILTER.none);
		this.setMulticast_filter(false);
		this.setMulticast_rate("mcs0");
		this.setBroadcast(true);
		this.setVlan_id(0);
		//this.setEnabled(false);
	}
	
	public String getRadio_band() {
		return radio_band;
	}

	public void setRadio_band(String radio_band) {
		this.radio_band = radio_band;
	}
	
	public String getRadio_select() {
		return radio_select;
	}

	public void setRadio_select(String radio_select) {
		this.radio_select = radio_select;
	}

	public String getBand_steering() {
		return band_steering;
	}

	public void setBand_steering(String band_steering) {
		this.band_steering = band_steering;
	}

	public Boolean getPortal_enabled() {
		return portal_enabled;
	}

	public void setPortal_enabled(Boolean portal_enabled) {
		this.portal_enabled = portal_enabled;
	}

	public String getPortal_url() {
		return portal_url;
	}

	public void setPortal_url(String portal_url) {
		this.portal_url = portal_url;
	}

	public String getPortal_auth() {
		return portal_auth;
	}

	public void setPortal_auth(String portal_auth) {
		this.portal_auth = portal_auth;
	}
	
	public Boolean getPortal_cna_bypass() {
		return portal_cna_bypass;
	}

	public void setPortal_cna_bypass(Boolean portal_cna_bypass) {
		this.portal_cna_bypass = portal_cna_bypass;
	}
	
	public String getPortal_domain_accept() {
		return portal_domain_accept;
	}

	public void setPortal_domain_accept(String portal_domain_accept) {
		this.portal_domain_accept = portal_domain_accept;
	}

	public String getFbwifi_gwid() {
		return fbwifi_gwid;
	}

	public void setFbwifi_gwid(String fbwifi_gwid) {
		this.fbwifi_gwid = fbwifi_gwid;
	}

	public String getFbwifi_secret() {
		return fbwifi_secret;
	}

	public void setFbwifi_secret(String fbwifi_secret) {
		this.fbwifi_secret = fbwifi_secret;
	}
	
	public static void setWlanOrderTag(String WLAN_ORDER_TAG) {
		
	}

	public static String getWlanOrderTag() {
		return WLAN_ORDER_TAG;
	}

	public static void setWlanTag(String WLAN_TAG) {
		
	}
	
	public static String getWlanTag() {
		return WLAN_TAG;
	}

	public Integer getVlan_id() {
		return vlan_id;
	}

	public void setVlan_id(Integer vlan_id) {
		this.vlan_id = vlan_id;
	}

	public Boolean getIcMg() {
		return icMg;
	}

	public void setIcMg(Boolean icMg) {
		this.icMg = icMg;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public WpaPersonal getWpa_personal() {
		return wpa_personal;
	}

	public void setWpa_personal(WpaPersonal wpa_personal) {
		this.wpa_personal = wpa_personal;
	}

	public Boolean getBroadcast() {
		return broadcast;
	}

	public void setBroadcast(Boolean broadcast) {
		this.broadcast = broadcast;
	}

	public Integer getSsid_id() {
		return ssid_id;
	}

	public void setSsid_id(Integer ssid_id) {
		this.ssid_id = ssid_id;
	}

	public Integer getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public SECURITY getSecurity() {
		return security;
	}

	public void setSecurity(SECURITY security) {
		this.security = security;
	}

	public Wep getWep() {
		return wep;
	}

	public void setWep(Wep wep) {
		this.wep = wep;
	}

	public Boolean getLayer2_isolation() {
		return layer2_isolation;
	}

	public void setLayer2_isolation(Boolean layer2_isolation) {
		this.layer2_isolation = layer2_isolation;
	}

	public List<RadiusServer> getRadius_servers_list() {
		return radius_servers_list;
	}

	public void setRadius_servers_list(List<RadiusServer> radius_servers_list) {
		this.radius_servers_list = radius_servers_list;
	}

	public MAC_FILTER getMac_filter() {
		return mac_filter;
	}

	public void setMac_filter(MAC_FILTER mac_filter) {
		this.mac_filter = mac_filter;
	}

	public List<String> getMac_list() {
		return mac_list;
	}

	public void setMac_list(List<String> mac_list) {
		this.mac_list = mac_list;
	}

	public Boolean getMulticast_filter() {
		return multicast_filter;
	}

	public void setMulticast_filter(Boolean multicast_filter) {
		this.multicast_filter = multicast_filter;
	}

	public String getMulticast_rate() {
		return multicast_rate;
	}

	public void setMulticast_rate(String multicast_rate) {
		this.multicast_rate = multicast_rate;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ssid_id == null) ? 0 : ssid_id.hashCode());
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
		JsonConf_SsidProfiles other = (JsonConf_SsidProfiles) obj;
		if (ssid_id == null) {
			if (other.ssid_id != null)
				return false;
		} else if (!ssid_id.equals(other.ssid_id))
			return false;
		return true;
	}

	public class Wep {
		protected KEY_FORMAT key_format;
		@ConfigValue(name = "KEY_SIZE", valueMap = "")
		protected Integer key_size;
		@ConfigValue(name = "ENCRYPT_KEY", valueMap = "")
		protected String encrypt_key;
		@ConfigValue(name = "WEP_SK_AUTH", valueMap = "true=enable|false=disable")
		protected Boolean shared_key_auth;

		public KEY_FORMAT getKey_format() {
			return key_format;
		}

		public void setKey_format(KEY_FORMAT key_format) {
			this.key_format = key_format;
		}

		public String getEncrypt_key() {
			return encrypt_key;
		}

		public Integer getKey_size() {
			return key_size;
		}

		public void setKey_size(Integer key_size) {
			this.key_size = key_size;
		}

		public void setEncrypt_key(String encrypt_key) {
			this.encrypt_key = encrypt_key;
		}

		public Boolean getShared_key_auth() {
			return shared_key_auth;
		}

		public void setShared_key_auth(Boolean shared_key_auth) {
			this.shared_key_auth = shared_key_auth;
		}

		@Override
		public String toString() {
			return "Wep [key_format=" + key_format + ", key_size=" + key_size + ", encrypt_key=" + encrypt_key + ", shared_key_auth=" + shared_key_auth + "]";
		}
	}

	public class RadiusServer {
		protected Integer id;
		@ConfigValue(name = "HOST", valueMap = "")
		protected String host;
		@ConfigValue(name = "SECRET", valueMap = "")
		protected String secret;
		@ConfigValue(name = "AUTHPORT", valueMap = "")
		protected Integer auth_port;
		@ConfigValue(name = "ACCTPORT", valueMap = "")
		protected int account_port;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public Integer getAuth_port() {
			return auth_port;
		}

		public void setAuth_port(Integer auth_port) {
			this.auth_port = auth_port;
		}

		public int getAccount_port() {
			return account_port;
		}

		public void setAccount_port(int account_port) {
			this.account_port = account_port;
		}

	}
}
