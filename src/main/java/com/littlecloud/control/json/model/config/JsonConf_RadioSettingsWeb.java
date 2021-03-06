package com.littlecloud.control.json.model.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.google.gson.annotations.SerializedName;
import com.littlecloud.control.entity.ConfigurationRadios;
import com.littlecloud.control.entity.ConfigurationSettingsExtraSettings;

/* per network settings */
public class JsonConf_RadioSettingsWeb extends JsonConf_RadioSettings {

	private static final Logger log = Logger.getLogger(JsonConf_RadioSettingsWeb.class);
	
	public static final String STRING_DEFAULT_PROTOCOL = "default";
	public static final String STRING_80211AC_PROTOCOL = "AC80211";
	public static final String STRING_80211NA_PROTOCOL = "NA80211";
	
	public static final String STRING_DEFAULT_BAND = "default";
	public static final String STRING_BAND2400 = "2400";
	public static final String STRING_BAND5000 = "5000";
		
	private String default_band;	
	private String prefer_5gz_protocol;

	@SerializedName("ssid_profiles")
	private List<JsonConf_SsidProfilesWeb> ssidProfilesWebLst;
	private ConfigurationSettingsExtraSettings extra_settings;
	private boolean show_wechat_settings = false;
	
	public void parseRadio(ConfigurationRadios radio) {
		network_id = radio.getId().getNetworkId();
		country_code = radio.getCountry();
		default_band = radio.getDefaultBand();	// default 2400
		prefer_5gz_protocol = radio.getProtocol();	// default ac		
		log.infof("prefer_protocol %s radio.getProtocol() %s", prefer_5gz_protocol, radio.getProtocol());
		
		timestamp = radio.getTimestamp();
	}
	
	/* this default shall be loaded from config file finally 
	 * default settings capability will be checked afterwards!
	 * */
	public JsonConf_RadioSettingsWeb fillDefaultValues(int network_id, List<Integer> deviceIdList, List<Integer> moduleIdList, Boolean isForApplyConfig) {
		
		/* global default */
		this.setNetwork_id(network_id);
		this.setCountry_code(840);
		this.setDefault_band(STRING_BAND2400);
		this.setPrefer_5gz_protocol(STRING_80211AC_PROTOCOL);
		this.setTimestamp(new Date());

		List<Modules> modLst = new ArrayList<Modules>();
		for (int device_id : deviceIdList)
		{
			for (int module_id : moduleIdList)
			{
				Modules m = this.new Modules();
				m.fillDefault(device_id, module_id);
				modLst.add(m);
			}
		}
		this.setModulesLst(modLst);
		return this;
	}
	
	public String getDefault_band() {
		return default_band;
	}

	public void setDefault_band(String default_band) {
		this.default_band = default_band;
	}
		
	public String getPrefer_5gz_protocol() {
		return prefer_5gz_protocol;
	}

	public void setPrefer_5gz_protocol(String prefer_5gz_protocol) {
		this.prefer_5gz_protocol = prefer_5gz_protocol;
	}
	
	public List<JsonConf_SsidProfilesWeb> getSsidProfilesWebLst() {
		return ssidProfilesWebLst;
	}

	public void setSsidProfilesWebLst(List<JsonConf_SsidProfilesWeb> ssidProfilesWebLst) {
		this.ssidProfilesWebLst = ssidProfilesWebLst;
	}
	
	public void mergeSsidProfilesWebLst(JsonConf_RadioSettingsWeb update)
	{
		/* dummy */
	}

	public ConfigurationSettingsExtraSettings getExtra_settings() {
		return extra_settings;
	}

	public void setExtra_settings(ConfigurationSettingsExtraSettings extra_settings) {
		this.extra_settings = extra_settings;
	}

	public boolean isShow_wechat_settings() {
		return show_wechat_settings;
	}

	public void setShow_wechat_settings(boolean show_wechat_settings) {
		this.show_wechat_settings = show_wechat_settings;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonConf_RadioSettingsWeb [prefer_protocol=");
		builder.append(prefer_5gz_protocol);
		builder.append(", ssidProfilesWebLst=");
		builder.append(ssidProfilesWebLst);
		builder.append(", extra_settings=");
		builder.append(extra_settings);
		builder.append("]");
		return builder.toString();
	}
}
