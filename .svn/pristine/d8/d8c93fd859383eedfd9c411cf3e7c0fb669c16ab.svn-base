package com.littlecloud.control.json.model.config;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.time.StopWatch;
import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.littlecloud.control.entity.ConfigurationRadioChannels;
import com.littlecloud.control.json.JsonExclude;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfiles.MAC_FILTER;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfiles.SECURITY;
import com.littlecloud.control.json.util.JsonUtils;

/* per network settings */
public class JsonConf_RadioSettings extends JsonConf {
	private static final Logger log = Logger.getLogger(JsonConf_RadioSettings.class);

	public static final int GLOBAL_DEV_INDEX = -1;
	
	public static final String STRING_DUAL_BAND = "dual";	
	public static final String DEFAULT_BAND = "2400";
	
	public static enum CONFIG {
		NONE, PART_SSID_OVERVIEW, PART_SSID_ACCESS_CONTROL, PART_SSID_AVAL, PART_SSID_RADIO
	};
	
	public static enum AP_RADIO_POLICY {
		ng, na, ac
	};
	
	@SerializedName("id")
	protected Integer network_id; // network id
	@SerializedName("country")
	@ConfigValue(name = "AP_COUNTRY_CODE", valueMap = "")
	protected Integer country_code;
	@ConfigValue(name = "R", valueMap = "")
	@SerializedName("modules")
	protected List<Modules> modulesLst; // i.e. channels
	@ConfigValue(name = "WLAN", valueMap = "")
	@SerializedName("ssid_profiles_config")
	protected List<JsonConf_SsidProfiles> ssidProfilesLst;
	@SerializedName("ssid_profiles_reserved")
	protected List<JsonConf_SsidProfiles> ssidProfilesReservedLst;

	@JsonExclude
	protected Date timestamp;
	
	protected Boolean wifi_mgm_enabled;
	protected Boolean follow_network;
	protected Boolean wifi_managed_by_ic2;
	
	@JsonExclude
	protected boolean webadminShowIcmg;
	
	public boolean isWebadminShowIcmg() {
		return webadminShowIcmg;
	}

	public void setWebadminShowIcmg(boolean webadminShowIcmg) {
		this.webadminShowIcmg = webadminShowIcmg;
	}

	public Boolean getFollow_network() {
		return follow_network;
	}

	public void setFollow_network(Boolean follow_network) {
		this.follow_network = follow_network;
	}

	public Boolean getWifi_mgm_enabled() {
		return wifi_mgm_enabled;
	}

	public void setWifi_mgm_enabled(Boolean wifi_mgm_enabled) {
		this.wifi_mgm_enabled = wifi_mgm_enabled;
	}

	public Boolean getWifi_managed_by_ic2() {
		return wifi_managed_by_ic2;
	}

	public void setWifi_managed_by_ic2(Boolean wifi_managed_by_ic2) {
		this.wifi_managed_by_ic2 = wifi_managed_by_ic2;
	}

	public void mergeModulesLst(JsonConf_RadioSettings update)
	{
		if (modulesLst == null || update.getModulesLst() == null)
			return;

		Map<String, Modules> newMapProfiles = new HashMap<String, Modules>();

		/* create hashmap for new ssid items */
		for (int j = 0; j < update.getModulesLst().size(); j++)
		{
			Modules m = update.getModulesLst().get(j);
			newMapProfiles.put(m.getDevice_id() + "|" + m.getModule_id(), update.getModulesLst().get(j));
		}
		log.debug("newMapProfiles = " + newMapProfiles.keySet());

		/* create a new list from old list and new list */
		List<Modules> resultLst = update.getModulesLst();

		/* loop old list */
		for (int j = 0; j < modulesLst.size(); j++)
		{
			Modules m = modulesLst.get(j);
			String key = m.getDevice_id() + "|" + m.getModule_id();

			/* if new list contains old list item, use new list. if not, add old list item. */
			if (!newMapProfiles.containsKey(key))
			{
				resultLst.add(modulesLst.get(j));
			}
		}
		modulesLst = resultLst;
	}

	public void mergeSsidProfilesLst(JsonConf_RadioSettings update)
	{
		if (ssidProfilesLst == null || update.getSsidProfilesLst() == null)
			return;

		Map<String, JsonConf_SsidProfiles> newMapProfiles = new HashMap<String, JsonConf_SsidProfiles>();

		/* create hashmap for new ssid items */
		for (int j = 0; j < update.getSsidProfilesLst().size(); j++)
		{
			JsonConf_SsidProfiles m = update.getSsidProfilesLst().get(j);
			newMapProfiles.put(m.getSsid_id().toString(), update.getSsidProfilesLst().get(j));
		}
		log.debug("newMapProfiles = " + newMapProfiles.keySet());

		/* create a new list from old list and new list */
		List<JsonConf_SsidProfiles> resultLst = update.getSsidProfilesLst();

		/* loop old list */
		for (int j = 0; j < ssidProfilesLst.size(); j++)
		{
			JsonConf_SsidProfiles m = ssidProfilesLst.get(j);
			String key = m.getSsid_id().toString();

			/* if new list contains old list item, use new list. if not, add old list item. */
			log.debug(key);
			if (!newMapProfiles.containsKey(key))
			{
				log.debug(key + " is NOT contained.");
				resultLst.add(ssidProfilesLst.get(j));
			}
		}
		ssidProfilesLst = resultLst;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}

	public Integer getCountry_code() {
		return country_code;
	}

	public void setCountry_code(Integer country_code) {
		this.country_code = country_code;
	}

	@JsonConfList
	public List<Modules> getModulesLst() {
		return modulesLst;
	}

	public void setModulesLst(List<Modules> modulesLst) {
		this.modulesLst = modulesLst;
	}

	@JsonConfList
	public List<JsonConf_SsidProfiles> getSsidProfilesLst() {
		return ssidProfilesLst;
	}

	public void setSsidProfilesLst(List<JsonConf_SsidProfiles> ssidProfilesLst) {
		this.ssidProfilesLst = ssidProfilesLst;
	}
	
	public List<JsonConf_SsidProfiles> getSsidProfilesReservedLst() {
		return ssidProfilesReservedLst;
	}

	public void setSsidProfilesReservedLst(List<JsonConf_SsidProfiles> ssidProfilesReservedLst) {
		this.ssidProfilesReservedLst = ssidProfilesReservedLst;
	}

	/* per device settings */
	public class Modules implements Comparable<Modules> {

		@JsonExclude
		public static final int DEFAULT_MODULES = 1; // LATER THE MODULES_MAX should depends on device capability //...

		private Integer device_id;
		@SerializedName("id")
		private Integer module_id;		
		protected String device_name;
		protected Integer product_id;
		protected String product_name;
		protected String model_name;

		@ConfigValue(name = "PROFILE_BG_CHANNEL", valueMap = "auto=0|1=1|2=2|3=3|4=4|5=5|6=6|7=7|8=8|9=9|10=10|11=11|12=12|13=13|14=14|36=36|40=40|44=44|48=48|52=52|56=56|60=60|64=64|100=100|104=104|108=108|112=112|116=116|120=120|124=124|128=128|132=132|136=136|140=140|149=149|153=153|157=157|161=161|165=165")
		private String channel;
		@ConfigValue(name = "AP_RADIO_TXLEVEL", valueMap = "max|max=max|high=high|mid=medium|low=low")
		private String power;
		@ConfigValue(name = "AP_RADIO_TXBOOST", valueMap = "true=yes|false=no")
		private Boolean boost;		
		private String frequency_band; // 2400/5000 for 802.11ng/802.11na or 802.11ac 
		@ConfigValue(name = "AP_RADIO_POLICY", valueMap = "ng=ng|na=na|ac=ac")
		private String protocol;
		private String frequency_band_capability;
		private String protocol_5gz_capability;
		private String wifi_cfg;
		
		public void fillDefault(int device_id, int module_id)
		{
			/* device default */
			this.setDevice_id(device_id);
			this.setModule_id(module_id);
			this.setProtocol(JsonConf_RadioSettingsWeb.STRING_DEFAULT_PROTOCOL);
			this.setChannel("auto");
			this.setPower("max");
			this.setBoost(false);
			this.setFrequency_band(JsonConf_RadioSettingsWeb.STRING_DEFAULT_BAND);
		}

		public void parseModules(ConfigurationRadioChannels channels) {
			if (log.isDebugEnabled())
				log.debugf("parseModules channels %s", channels);
			
			module_id = channels.getId().getModuleId();
			device_id = channels.getId().getDeviceId();
			channel = channels.getChannel();
			power = channels.getPower();
			boost = channels.isBoost();
			frequency_band = channels.getFrequencyBand();			
		}

		public String getDevice_name() {
			return device_name;
		}

		public void setDevice_name(String device_name) {
			this.device_name = device_name;
		}

		public Integer getProduct_id() {
			return product_id;
		}

		public void setProduct_id(Integer product_id) {
			this.product_id = product_id;
		}
		
		public String getProduct_name() {
			return product_name;
		}

		public void setProduct_name(String product_name) {
			this.product_name = product_name;
		}

		public String getModel_name() {
			return model_name;
		}

		public void setModel_name(String model_name) {
			this.model_name = model_name;
		}

		public String getFrequency_band() {
			return frequency_band;
		}

		public void setFrequency_band(String frequency_band) {
			this.frequency_band = frequency_band;
		}

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}

		public String getFrequency_band_capability() {
			return frequency_band_capability;
		}

		public void setFrequency_band_capability(String frequency_band_capability) {
			this.frequency_band_capability = frequency_band_capability;
		}
		
		public String getProtocol_5gz_capability() {
			return protocol_5gz_capability;
		}

		public void setProtocol_5gz_capability(String protocol_5gz_capability) {
			this.protocol_5gz_capability = protocol_5gz_capability;
		}

		public String getWifi_cfg() {
			return wifi_cfg;
		}

		public void setWifi_cfg(String wifi_cfg) {
			this.wifi_cfg = wifi_cfg;
		}

		public Boolean getBoost() {
			return boost;
		}

		public Integer getModule_id() {
			return module_id;
		}

		public void setModule_id(Integer module_id) {
			this.module_id = module_id;
		}

		public Integer getDevice_id() {
			return device_id;
		}

		public void setDevice_id(Integer device_id) {
			this.device_id = device_id;
		}

		public String getChannel() {
			return channel;
		}

		public void setChannel(String channel) {
			this.channel = channel;
		}

		public String getPower() {
			return power;
		}

		public void setPower(String power) {
			this.power = power;
		}

		public void setBoost(Boolean boost) {
			this.boost = boost;
		}

		@Override
		public String toString() {
			return "Modules [module_id=" + module_id + ", device_id=" + device_id + ", channel=" + channel + ", power=" + power + ", boost=" + boost + ", frequency_band=" + frequency_band + "]";
		}

		@Override
		public int compareTo(Modules o) {
			if (this.getDevice_id() - o.getDevice_id()!=0)
				return this.getDevice_id() - o.getDevice_id();
			else
				return this.getModule_id() - o.getModule_id();
		}
	}

	public ConcurrentHashMap<Integer, String> generateHardwareConfig(Class cls, CONFIG filter, Integer devId, CONFIG_TYPE configType) throws Exception
	{
		log.debugf("generateHardwareConfig filter %s devId %d configType %s", filter, devId, configType);
		ConcurrentHashMap<Integer, String> confJsonMap = new ConcurrentHashMap<Integer, String>();
		if (traverseConfig(confJsonMap, "", this, cls, devId, configType))
		{
			/* Merge global config to per device config */
			String globalConfig = confJsonMap.get(GLOBAL_DEV_INDEX);
			for (Integer id : confJsonMap.keySet())
			{
				if (id != GLOBAL_DEV_INDEX)
				{
					log.debug("id=" + id);
					String devConfig = globalConfig + confJsonMap.get(id);
					confJsonMap.put(id, devConfig);
				}
			}
			confJsonMap.remove(GLOBAL_DEV_INDEX);

			/*
			 * do filter to config if filter is not null
			 */
			if (filter != null && filter!=CONFIG.NONE)
			{
				for (Integer id : confJsonMap.keySet())
				{
					StringBuilder sb = new StringBuilder();
					String config = confJsonMap.get(id);
					String[] lines = null;
					switch (filter)
					{
					case PART_SSID_OVERVIEW:
						log.debug("filter by PART_SSID_OVERVIEW");
						lines = config.split("\n");
						for (String line : lines)
						{
							if (line.contains("WLAN"))
							{
								if (line.contains("MCAST_FILTER") || line.contains("ADMIN_STATUS"))
								{
									sb.append(line);
									sb.append("\n");
								}
							}
						}
						confJsonMap.put(id, sb.toString());
						break;
					case PART_SSID_ACCESS_CONTROL:
						log.debug("filter by PART_SSID_ACCESS_CONTROL");
						lines = config.split("\n");
						for (String line : lines)
						{
							if (line.contains("WLAN"))
							{
								if (line.contains("SECURITY") || line.contains("L2_ISO") || line.contains("_ACL_"))
								{
									sb.append(line);
									sb.append("\n");
								}
							}
						}
						confJsonMap.put(id, sb.toString());
						break;
					case PART_SSID_AVAL:
						log.debug("filter by PART_SSID_AVAL");
						lines = config.split("\n");
						for (String line : lines)
						{
							if (line.contains("WLAN"))
							{
								if (line.contains("BROADCAST"))
								{
									sb.append(line);
									sb.append("\n");
								}
							}
						}
						confJsonMap.put(id, sb.toString());
						break;
					case PART_SSID_RADIO:
						log.debug("filter by PART_SSID_RADIO");
						lines = config.split("\n");
						for (String line : lines)
						{
							if (line.contains("PROFILE_BG_CHANNEL") || line.contains("AP_RADIO_TXLEVEL") || line.contains("AP_RADIO_TXBOOST"))
							{
								sb.append(line);
								sb.append("\n");
							}
						}
						confJsonMap.put(id, sb.toString());
						break;
					case NONE:
						break;
					default:
						break;
					}
				}
			}

			return confJsonMap;
		}
		return null;
	}

	public static void main(String[] args) throws Exception
	{
		/* object merge */
		JsonConf_RadioSettingsWeb r1 = new JsonConf_RadioSettingsWeb();
		r1.fillDefaultValues(1, Arrays.asList(1, 2), Arrays.asList(11, 12), false);
		List<JsonConf_SsidProfiles> ssidProLst = new ArrayList<JsonConf_SsidProfiles>();
		for (int i = 1; i <= 4; i++)
		{
			JsonConf_SsidProfiles ssidPro = new JsonConf_SsidProfiles();
			ssidPro.setDefaultValues(i);
			ssidPro.setVlan_id(100);
			ssidProLst.add(ssidPro);
		}
		r1.setSsidProfilesLst(ssidProLst);

		System.out.println("start");
		StopWatch sw = new StopWatch();
		sw.start();
		for (int i = 1; i < 100000; i++)
			r1.toString();
		sw.stop();
		System.out.printf("ToString1=%s (%d)\n", r1.toString(), sw.getTime());

		
		Gson gson = new GsonBuilder().create();
		System.out.println("result1=" + gson.toJson(r1));

		JsonConf_RadioSettingsWeb r2 = new JsonConf_RadioSettingsWeb();
		r2.fillDefaultValues(2, Arrays.asList(2, 3), Arrays.asList(13, 14), false);
		r2.setSsidProfilesLst(ssidProLst);

		List<JsonConf_SsidProfiles> profLst = r2.getSsidProfilesLst();
		JsonConf_SsidProfiles prof = profLst.get(0);
		JsonConf_SsidProfiles.Wep wep = prof.new Wep();
		wep.setEncrypt_key("KEY123456");
		prof.setWep(wep);
		prof.setLayer2_isolation(true);

		JsonConf_SsidProfiles.RadiusServer rad = prof.new RadiusServer();
		rad.setId(1);
		rad.setAccount_port(8181);
		rad.setAuth_port(8282);
		rad.setHost("abc.host.com");
		rad.setSecret("secrettt");

		JsonConf_SsidProfiles.RadiusServer rad2 = prof.new RadiusServer();
		rad2.setId(2);
		rad2.setAccount_port(9191);
		rad2.setAuth_port(9292);
		rad2.setHost("kkk.host.com");
		rad2.setSecret("passwd");

		prof.setMac_filter(MAC_FILTER.accept);
		prof.setMac_list(Arrays.asList("11:22:11:22:11:22", "22:33:22:33:22:33"));
		prof.setRadius_servers_list(Arrays.asList(rad, rad2));

		JsonConf_SsidProfiles.WpaPersonal wpa = prof.new WpaPersonal();
		wpa.setShared_key("share123456");
		prof.setWpa_personal(wpa);
		prof.setSecurity(SECURITY.wpa_wpa2_enterprise);
		profLst.set(0, prof);

		prof = profLst.get(1);
		profLst.set(1, prof);
		r2.setSsidProfilesLst(profLst);
		List<Modules> modLst = r2.getModulesLst();
		for (Modules m: modLst)
		{
			m.setDevice_name("NEWNEW"+m.getDevice_id());
			m.setFrequency_band("5000");
		}		

		System.out.println("result2=" + gson.toJson(r2));

		if (r1.updateWith(r2))
		{
			System.out.println("result3=" + gson.toJson(r1));
		}
		else
			System.out.println("update failure");

		System.out.println("result4=" + gson.toJson(r1));
		
		try {
			System.out.println("CONFIG=\n" + r1.generateHardwareConfig(r1.getClass(), CONFIG.PART_SSID_ACCESS_CONTROL, GLOBAL_DEV_INDEX, JsonConf_RadioSettings.CONFIG_TYPE.MAX).get(2));
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			System.out.println(e.getLocalizedMessage() + "\n");
			e.printStackTrace();
		}
		
		try {
			System.out.println("CONFIG=\n" + r2.generateHardwareConfig(r2.getClass(), CONFIG.NONE, GLOBAL_DEV_INDEX, JsonConf_RadioSettings.CONFIG_TYPE.MAX).get(2));
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			System.out.println(e.getLocalizedMessage() + "\n");
			e.printStackTrace();
		}
		
		String json = "{'id':2,'country':840,'default_band':'2400','modules':[{'id':13,'device_id':2,'channel':'auto','power':'max','boost':false,'frequency_band':'2400'},{'id':14,'device_id':2,'channel':'auto','power':'max','boost':false,'frequency_band':'2400'},{'id':13,'device_id':3,'channel':'auto','power':'max','boost':false,'frequency_band':'2400'},{'id':14,'device_id':3,'channel':'auto','power':'max','boost':false,'frequency_band':'2400'}],'ssid_profiles':[{'id':1,'enabled':false,'ssid':'Unconfigured SSID','security':'wpa_wpa2_enterprise','wep':{'encrypt_key':'KEY123456'},'layer2_isolation':true,'radius_servers':[{'id':1,'host':'abc.host.com','secret':'secrettt','auth_port':8282,'account_port':8181},{'id':2,'host':'kkk.host.com','secret':'passwd','auth_port':9292,'account_port':9191}],'mac_filter':'accept','mac_list':['11:22:11:22:11:22','22:33:22:33:22:33'],'multicast_filter':false,'multicast_rate':'mcs0','broadcast':true,'version':1,'wpa_personal':{'shared_key':'share123456'}},{'id':2,'enabled':false,'ssid':'Unconfigured SSID','security':'open','layer2_isolation':false,'mac_filter':'none','multicast_filter':false,'multicast_rate':'mcs0','broadcast':true,'version':1},{'id':3,'enabled':false,'ssid':'Unconfigured SSID','security':'open','layer2_isolation':false,'mac_filter':'none','multicast_filter':false,'multicast_rate':'mcs0','broadcast':true,'version':1},{'id':4,'enabled':false,'ssid':'Unconfigured SSID','security':'open','layer2_isolation':false,'mac_filter':'none','multicast_filter':false,'multicast_rate':'mcs0','broadcast':true,'version':1}]}";
		JsonConf_RadioSettings r3 = JsonUtils.<JsonConf_RadioSettings>fromJson(json, JsonConf_RadioSettings.class);
		System.out.println("r3="+r3);
	}
}
