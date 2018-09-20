package com.littlecloud.control.json.model.config;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.logging.Logger;

import com.google.gson.annotations.SerializedName;
import com.littlecloud.control.entity.ConfigurationPepvpnCertificates;
import com.littlecloud.control.entity.ConfigurationPepvpns;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.json.JsonExclude;
import com.littlecloud.control.json.model.config.util.PepvpnConfigUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.utils.Utils;

public class JsonConf_PepvpnProfilesNew extends JsonConf {

	private static final Logger log = Logger.getLogger(JsonConf_PepvpnProfilesNew.class);
	private static final Integer DEFAULT_NUM_OF_WAN = 6;
	
	public static enum ENCRYPTION {
		aes256, none
	};

	public static enum AUTHENTICATION {
		none, psk, x509
	};

	@ConfigValue(name = "MVPN", valueMap = "")
	private List<Profile> profileLst;

	public List<Profile> getProfileLst() {
		return profileLst;
	}

	public void setProfileLst(List<Profile> profileLst) {
		this.profileLst = profileLst;
	}

	public class Profile {
		private Integer id; // network_id
		@ConfigValue(name = "PRIORITY", valueMap = "")
		private String priority; // read wan_cnt later
		@ConfigValue(name = "NAME", valueMap = "")
		private String local_name;
		@ConfigValue(name = "CLIENT_SN", valueMap = "")
		private String remote_name;
		@ConfigValue(name = "ENABLE", valueMap = "true=yes|false=no")
		private Boolean enabled;
		@ConfigValue(name = "ENCRYPT_METHOD", valueMap = "none=none|aes256=aes256")
		private ENCRYPTION encryption;
		@ConfigValue(name = "SERVER_LIST", valueMap = "")
		private List<String> server_list;		
		private AUTHENTICATION authentication;
		@ConfigValue(name = "PSK", valueMap = "")
		private String psk;
		@SerializedName("data_port")
		private String data_port_type; // default, custom
		@ConfigValue(name = "DATA_PORT", valueMap = "")
		private Integer data_port_num;		
		@ConfigValue(name = "NAT_ENABLE", valueMap="true=yes|false=no")
		private Boolean nat_mode;
		@ConfigValue(name = "IC_MG", valueMap="true=1|false=${setnull}")
		private Boolean icMg = true;

		/*
		 * NOTE!:
		 * The list below is not persist to database in json format,
		 * the information is stored in pepvpn_configuration_certificates.
		 * So set empty before persist
		 */
		private List<Certificate> hub_cert_list;
		private List<Certificate> endpoint_cert_list; // if device(s) in the network is not hub, then endpoint

		public Boolean getIcMg() {
			return icMg;
		}

		public void setIcMg(Boolean icMg) {
			this.icMg = icMg;
		}

		@JsonConfList
		public List<String> getServer_list() {
			return server_list;
		}

		public void setServer_list(List<String> server_list) {
			this.server_list = server_list;
		}
		
		public Boolean getNat_mode() {
			return nat_mode;
		}

		public void setNat_mode(Boolean nat_mode) {
			this.nat_mode = nat_mode;
		}

		public AUTHENTICATION getAuthentication() {
			return authentication;
		}

		public void setAuthentication(AUTHENTICATION authentication) {
			this.authentication = authentication;
		}

		public String getPsk() {
			return psk;
		}

		public void setPsk(String psk) {
			this.psk = psk;
		}

		public String getData_port_type() {
			return data_port_type;
		}

		public void setData_port_type(String data_port_type) {
			this.data_port_type = data_port_type;
		}

		public Integer getData_port_num() {
			return data_port_num;
		}

		public void setData_port_num(Integer data_port_num) {
			this.data_port_num = data_port_num;
		}

		public void mergeServer_list(Profile update)
		{
			/* server list has no need to merge, just overwrite! */
			if (server_list == null)
			{
				/* directly assign update list to local if ok */
				server_list = update.getServer_list();
			}
			else
			{
				server_list = update.getServer_list();
			}
			return;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getPriority() {
			return priority;
		}

		public void setPriority(String priority) {
			this.priority = priority;
		}

		public String getLocal_name() {
			return local_name;
		}

		public void setLocal_name(String local_name) {
			this.local_name = local_name;
		}

		public String getRemote_name() {
			return remote_name;
		}

		public void setRemote_name(String remote_name) {
			this.remote_name = remote_name;
		}

		public Boolean getEnabled() {
			return enabled;
		}

		public void setEnabled(Boolean enabled) {
			this.enabled = enabled;
		}

		public ENCRYPTION getEncryption() {
			return encryption;
		}

		public void setEncryption(ENCRYPTION encryption) {
			this.encryption = encryption;
		}

		public List<Certificate> getEndpoint_cert_list() {
			return endpoint_cert_list;
		}

		public void setEndpoint_cert_list(List<Certificate> endpoint_cert_list) {
			this.endpoint_cert_list = endpoint_cert_list;
		}

		public List<Certificate> getHub_cert_list() {
			return hub_cert_list;
		}

		public void setHub_cert_list(List<Certificate> hub_cert_list) {
			this.hub_cert_list = hub_cert_list;
		}

		public void clear_all_certs() {
			clear_cert_list(hub_cert_list);
			clear_cert_list(endpoint_cert_list);
		}

		private void clear_cert_list(List<Certificate> certList) {
			if (certList == null)
				return;

			for (Certificate cert : certList)
			{
				cert.clearCertificates();
			}
		}

		@Override
		public String toString() {
			return "Profile [id=" + id + ", priority=" + priority + ", local_name=" + local_name + ", remote_name=" + remote_name + ", enabled=" + enabled + ", encryption=" + encryption + ", server_list=" + server_list + ", authentication=" + authentication + ", data_port_type=" + data_port_type + ", data_port_num=" + data_port_num + ", hub_cert_list=" + hub_cert_list + ", endpoint_cert_list=" + endpoint_cert_list + "]";
		}
	}

	//@JsonExclude()
	@ConfigValue(name = "MVPN_SITE_ID", valueMap = "")
	private String local_id;
	// private String

	private Integer hub_net_id; // hub device network_id
	private Integer hub_id; // hub device id
	private Boolean ha_enabled;
	private Integer ha_hub_net_id; // ha hub device network_id
	private Integer ha_hub_id; // ha hub device id

	@ConfigValue(name = "MVPN_ROUTE", valueMap = "true=1|false=${setnull}")	// since there is only one profile in endpoint
	// need MVPN_ROUTE_DNS_CUSTOM_SERVERS...
	private Boolean all_traffic_to_hub;
	@ConfigValue(name = "MVPN_HC_MODE", valueMap = "")
	private Integer link_failure_mode; // 0-3	
	@ConfigValue(name = "MVPN_ROUTE_DNS_CUSTOM_SERVERS", valueMap = "")
	private List<String> dns_list;

	// MVPN_ORDER...
	// MVPN%d_PRORITY
	// MVPN%d_NAME
	// MVPN%d_CLIENT_SN
	// MVPN_SITE_ID

	@JsonExclude
	private int version = 1;

	private Date timestamp;

	public ConcurrentHashMap<Integer, String> generateHardwareConfig(Class cls, CONFIG_TYPE configType) throws Exception
	{
		ConcurrentHashMap<Integer, String> confJsonMap = new ConcurrentHashMap<Integer, String>();
		if (traverseConfig(confJsonMap, "", this, cls, GLOBAL_DEV_INDEX, configType))
		{
			/* Merge global config to per device config */
			// String globalConfig = confJsonMap.get(GLOBAL_DEV_INDEX);
			// for (Integer id : confJsonMap.keySet())
			// {
			// if (id != GLOBAL_DEV_INDEX)
			// {
			// // log.debug("id=" + id);
			// String devConfig = globalConfig + confJsonMap.get(id);
			// confJsonMap.put(id, devConfig);
			// }
			// }
			// //....////confJsonMap.remove(GLOBAL_DEV_INDEX);

			String endptConfig = confJsonMap.get(GLOBAL_DEV_INDEX);
			StringBuilder sbList = new StringBuilder();
			sbList.append("\"");
			if (getProfileLst() != null && getProfileLst().size()!=0)
			{
				for (int i = 1; i <= getProfileLst().size(); i++)
				{
					sbList.append(i);
					sbList.append(" ");
				}
				sbList.setLength(sbList.length() - 1);
			}
			else
			{
				if (log.isInfoEnabled())
					log.infof("No profile is found for the " + this.getClass().getSimpleName() + " configType " + configType);
			}
			sbList.append("\"");

			endptConfig += "MVPN_ORDER=" + sbList.toString() + "\n";
			confJsonMap.put(GLOBAL_DEV_INDEX, endptConfig);
			
			log.debug("endptConfig="+endptConfig);

			return confJsonMap;
		}
		return null;
	}

	public static JsonConf_PepvpnProfilesNew parseJsonConf_PepvpnProfilesAsHub(JsonConf_PepvpnProfiles pepOld, List<JsonConf_PepvpnProfilesNew> endptLst, Integer numOfWan, Boolean isSpeedFusionSupport, Boolean isSpeedFusionBondingSupport)
	{
		log.debug("parseJsonConf_PepvpnProfilesAsHub is called");
		
		JsonConf_PepvpnProfilesNew pepconf = new JsonConf_PepvpnProfilesNew();
		//pepconf.setLocal_id(ConfigUtils.formatSiteId(pepOld.getHub_name()));
		/* version 1 */
		//pepconf.setLocal_id(ConfigUtils.formatSiteId(pepOld.getHub_name()));	// use sn instead
		pepconf.setLocal_id(PepvpnConfigUtils.formatProfileNameFromDevnameSn(pepOld.getHub_name(), pepOld.getHub_sn()));
		//pepconf.setAll_traffic_to_hub(pepOld.getAll_traffic_to_hub());	// dont set value for hub
		pepconf.setAll_traffic_to_hub(null);	// this option is for endpoint only
		//pepconf.setLink_failure_mode(pepOld.getLink_failure_mode());
		pepconf.setLink_failure_mode(0);	/* set hub link failure detection time to recommended (9031) */
		pepconf.setHa_enabled(pepOld.getHa_enabled());
		pepconf.setHa_hub_id(pepOld.getHa_hub_id());
		pepconf.setHa_hub_net_id(pepOld.getHa_hub_net_id());
		pepconf.setTimestamp(pepOld.getTimestamp());

		List<JsonConf_PepvpnProfilesNew.Profile> profLst = new ArrayList<JsonConf_PepvpnProfilesNew.Profile>();
		int profileId = 0;
		log.debugf("!! wrong !! endptLst=%s", endptLst);
		for (JsonConf_PepvpnProfilesNew endpt : endptLst)
		{
			profileId++;
			JsonConf_PepvpnProfilesNew.Profile profile = pepconf.new Profile();
			JsonConf_PepvpnProfilesNew.Profile endptprof = endpt.getProfileLst().get(0);
			
			profile.setId(profileId);
			profile.setLocal_name(endpt.getLocal_id());
			profile.setRemote_name(endpt.getLocal_id());
			profile.setEnabled(endptprof.getEnabled());
			profile.setEncryption(endptprof.getEncryption());
			// profile.setServer_list(pepOld.getServer_list()); // no need
			profile.setData_port_num(pepOld.getData_port_num()); // apply to all hub profile => bugzilla/9947
			profile.setAuthentication(endptprof.getAuthentication());
			profile.setPsk(endptprof.getPsk());
			//profile.setNat_mode(true);	/* add NAT_ENABLE to hub side profiles */
			profile.setNat_mode(pepOld.getNat_enabled());	/* 2014-01-09 Set default disable NAT_MODE */	/* 2014-01-28 a user setting */
			profile.setPriority(generateEndptMvpnPriority(numOfWan, isSpeedFusionSupport, isSpeedFusionBondingSupport));	/* set priority according to hub itself, not endpoint */
			profLst.add(profile);
		}
		pepconf.setProfileLst(profLst);

		return pepconf;
	}

	public static JsonConf_PepvpnProfilesNew parseJsonConf_PepvpnProfilesAsEndpoint(JsonConf_PepvpnProfiles pepOld, Integer devId, String sn, String localName, 
			Integer numOfWan, Boolean isSpeedFusionSupport, Boolean isSpeedFusionBondingSupport)
	{
		log.debug("parseJsonConf_PepvpnProfilesAsEndpoint is called");
		
		JsonConf_PepvpnProfilesNew pepconf = new JsonConf_PepvpnProfilesNew();
		//pepconf.setLocal_id(ConfigUtils.formatSiteId(localName));
		/* version 1 */
		//pepconf.setLocal_id(ConfigUtils.formatSiteId(sn));	// use sn instead
		pepconf.setLocal_id(PepvpnConfigUtils.formatProfileNameFromDevnameSn(localName, sn));
		pepconf.setAll_traffic_to_hub(pepOld.getAll_traffic_to_hub());
		if (pepconf.getAll_traffic_to_hub())
			pepconf.setDns_list(pepOld.getDns_list());
		pepconf.setLink_failure_mode(pepOld.getLink_failure_mode());
		pepconf.setHa_enabled(pepOld.getHa_enabled());
		pepconf.setHa_hub_id(pepOld.getHa_hub_id());
		pepconf.setHa_hub_net_id(pepOld.getHa_hub_net_id());
		pepconf.setTimestamp(pepOld.getTimestamp());

		JsonConf_PepvpnProfilesNew.Profile profile = pepconf.new Profile();
		profile.setId(1);	// only one profile for each endpoint
		/* version 1 */
		//profile.setLocal_name(ConfigUtils.formatSiteId(pepOld.getHub_name()));
		//profile.setRemote_name(ConfigUtils.formatSiteId(pepOld.getHub_name()));		
		profile.setLocal_name(PepvpnConfigUtils.formatProfileNameFromDevnameSn(pepOld.getHub_name(), pepOld.getHub_sn()));
		profile.setRemote_name(PepvpnConfigUtils.formatProfileNameFromDevnameSn(pepOld.getHub_name(), pepOld.getHub_sn()));		
		
		profile.setEnabled(pepOld.getEnabled());
		profile.setEncryption(pepOld.getEncryption());
		profile.setServer_list(pepOld.getServer_list());
		profile.setData_port_num(pepOld.getData_port_num());
		profile.setAuthentication(pepOld.getAuthentication());
		profile.setPsk(pepOld.getPsk());
		profile.setNat_mode(false);	/* disable NAT_ENABLE to endpoint side profiles */
		profile.setPriority(generateEndptMvpnPriority(numOfWan, isSpeedFusionSupport, isSpeedFusionBondingSupport));
		pepconf.setProfileLst(Arrays.asList(profile));

		return pepconf;
	}
		
	public static String generateEndptMvpnPriority(Integer numOfWan, Boolean isSpeedFusionSupport, Boolean isSpeedFusionBondingSupport)
	{
		if (isSpeedFusionSupport==null || numOfWan == null || isSpeedFusionBondingSupport == null)
			return null;
		
//		if (numOfWan==null)
//		{
//			log.warnf("numOfWan is null! use default of %d!", DEFAULT_NUM_OF_WAN);
//			numOfWan = DEFAULT_NUM_OF_WAN;
//		}
		
		StringBuilder sb = new StringBuilder();
		if (isSpeedFusionSupport && isSpeedFusionBondingSupport)
		{
			/* default all high priority */
			if (log.isDebugEnabled())
				log.debug("speedfusion and bonding support!");
			for (int i=1; i<=numOfWan; i++)
			{
				sb.append(i);
				sb.append(":");
				sb.append(1);
				sb.append(" ");
			}	
		}
		else
		{
			if (log.isDebugEnabled())
				log.debug("speedfusion/bonding NOT support!");
			
			/* default ordered priority */
			/* default all high priority */
			for (int i=1; i<=numOfWan; i++)
			{
				sb.append(i);
				sb.append(":");
				sb.append(i);
				sb.append(" ");
			}			
		}
		
		return sb.toString().trim();
	}

	public static JsonConf_PepvpnProfilesNew parseConfigurationPepvpns(ConfigurationPepvpns pepvpn)
	{
		log.debug("pepvpn.getConfig()=" + pepvpn.getConfig());

		JsonConf_PepvpnProfilesNew pepvpnProfile = JsonUtils.<JsonConf_PepvpnProfilesNew> fromJson(pepvpn.getConfig(), JsonConf_PepvpnProfilesNew.class);

		/* need to handle change network id in future since id is in json */
		JsonConf_PepvpnProfilesNew.Profile profile = pepvpnProfile.new Profile();
		// pepvpnProfile.setEnabled(pepvpn.isEnabled());
		profile.setEnabled(pepvpn.isEnabled());
		pepvpnProfile.setProfileLst(Arrays.asList(profile));

		pepvpnProfile.setHa_hub_net_id(pepvpn.getHaHubNetworkId());
		pepvpnProfile.setHa_hub_id(pepvpn.getHaHubDeviceId());
		pepvpnProfile.setHa_enabled(pepvpn.isHaHubEnabled());

		return pepvpnProfile;
	}

	public static JsonConf_PepvpnProfilesNew generateDefaultInstance(int network_id, List<Devices> devList) {
		JsonConf_PepvpnProfilesNew instance = new JsonConf_PepvpnProfilesNew();

		instance.setLink_failure_mode(0);
		instance.setTimestamp(new Date());
		instance.setHa_enabled(false);

		/* generate default endpoint cert list (cert list without cert) */
		List<Certificate> endpointCertLst = new ArrayList<Certificate>();
		Certificate certEndpointJson;

		JsonConf_PepvpnProfilesNew.Profile profile = instance.new Profile();
		for (Devices dev : devList)
		{
			log.debug("getting endpoint " + dev.getId());
			certEndpointJson = instance.new Certificate();
			certEndpointJson.parseCertificates(null, dev);
			endpointCertLst.add(certEndpointJson);
		}
		profile.setEndpoint_cert_list(endpointCertLst);
		profile.setId(network_id);
		profile.setEnabled(false);
		profile.setEncryption(ENCRYPTION.aes256);
		profile.setAuthentication(AUTHENTICATION.psk);
		profile.setData_port_type("default");
		instance.setProfileLst(Arrays.asList(profile));

		return instance;
	}
	
	public void mergeDns_list(Profile update)
	{
		/* dns list has no need to merge, just overwrite! */
		dns_list = update.getServer_list();
		return;
	}
	
	public List<String> getDns_list() {
		return dns_list;
	}

	public void setDns_list(List<String> dns_list) {
		this.dns_list = dns_list;
	}

	public String getLocal_id() {
		return local_id;
	}

	public void setLocal_id(String local_id) {
		this.local_id = local_id;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getVersion() {
		return version;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getHub_net_id() {
		return hub_net_id;
	}

	public void setHub_net_id(Integer hub_net_id) {
		this.hub_net_id = hub_net_id;
	}

	public Integer getHub_id() {
		return hub_id;
	}

	public void setHub_id(Integer hub_id) {
		this.hub_id = hub_id;
	}

	public Boolean getHa_enabled() {
		return ha_enabled;
	}

	public void setHa_enabled(Boolean ha_enabled) {
		this.ha_enabled = ha_enabled;
	}

	public Integer getHa_hub_net_id() {
		return ha_hub_net_id;
	}

	public void setHa_hub_net_id(Integer ha_hub_net_id) {
		this.ha_hub_net_id = ha_hub_net_id;
	}

	public Integer getHa_hub_id() {
		return ha_hub_id;
	}

	public void setHa_hub_id(Integer ha_hub_id) {
		this.ha_hub_id = ha_hub_id;
	}

	public Boolean getAll_traffic_to_hub() {
		return all_traffic_to_hub;
	}

	public void setAll_traffic_to_hub(Boolean all_traffic_to_hub) {
		this.all_traffic_to_hub = all_traffic_to_hub;
	}

	public Integer getLink_failure_mode() {
		return link_failure_mode;
	}

	public void setLink_failure_mode(Integer link_failure_mode) {
		this.link_failure_mode = link_failure_mode;
	}

	public class Certificate {
		private int id;
		private String name;
		private Boolean existed;
		private Date timestamp;
		private boolean autogen_cert;
		private String private_key_pem;
		private Boolean private_key_pem_encrypted;
		private String private_key_pem_passphrase;
		private String public_key_cert_pem;

		public void clearCertificates() {
			public_key_cert_pem = null;
			private_key_pem_passphrase = null;
			private_key_pem_encrypted = null;
			private_key_pem = null;
		}

		public void parseCertificates(ConfigurationPepvpnCertificates cert, Devices dev)
		{
			if (dev == null)
			{
				throw new IllegalArgumentException("Devices object cannot be null");
			}

			if (cert == null)
			{
				/* create default value */
				id = dev.getId();
				name = dev.getName();
				autogen_cert = false;
				existed = false;
				private_key_pem_encrypted = false;
			}
			else
			{
				/* use db value */
				id = cert.getId().getDeviceId();
				name = dev.getName();
				autogen_cert = cert.isAutogen();

				if (cert.getPrivateKey() == null || cert.getPublicKey() == null)
					existed = false;
				else
					existed = true;

				if (cert.getPrivateKeyPass() != null && cert.getPrivateKeyPass().length() != 0)
					private_key_pem_encrypted = true;
				else
					private_key_pem_encrypted = false;

				timestamp = cert.getTimestamp();
			}
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Boolean getExisted() {
			return existed;
		}

		public void setExisted(Boolean existed) {
			this.existed = existed;
		}

		public Date getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Date timestamp) {
			this.timestamp = timestamp;
		}

		public boolean isAutogen_cert() {
			return autogen_cert;
		}

		public void setAutogen_cert(boolean autogen_cert) {
			this.autogen_cert = autogen_cert;
		}

		public String getPrivate_key_pem() {
			return private_key_pem;
		}

		public void setPrivate_key_pem(String private_key_pem) {
			this.private_key_pem = private_key_pem;
		}

		public Boolean getPrivate_key_pem_encrypted() {
			return private_key_pem_encrypted;
		}

		public void setPrivate_key_pem_encrypted(Boolean private_key_pem_encrypted) {
			this.private_key_pem_encrypted = private_key_pem_encrypted;
		}

		public String getPrivate_key_pem_passphrase() {
			return private_key_pem_passphrase;
		}

		public void setPrivate_key_pem_passphrase(String private_key_pem_passphrase) {
			this.private_key_pem_passphrase = private_key_pem_passphrase;
		}

		public String getPublic_key_cert_pem() {
			return public_key_cert_pem;
		}

		public void setPublic_key_cert_pem(String public_key_cert_pem) {
			this.public_key_cert_pem = public_key_cert_pem;
		}
	}

	@Override
	public String toString() {
		return "PepCfgNew [profileLst=" + profileLst + ", local_id=" + local_id + ", hub_net_id=" + hub_net_id + ", hub_id=" + hub_id + ", ha_enabled=" + ha_enabled + ", ha_hub_net_id=" + ha_hub_net_id + ", ha_hub_id=" + ha_hub_id + ", all_traffic_to_hub=" + all_traffic_to_hub + ", link_failure_mode=" + link_failure_mode + ", version=" + version + ", timestamp=" + timestamp + "]";
	}

	public static void main(String args[]) throws Exception
	{
		JsonConf_PepvpnProfilesNew pepconf = new JsonConf_PepvpnProfilesNew();
		pepconf.setLocal_id("EP101");
		pepconf.setAll_traffic_to_hub(false);
		pepconf.setLink_failure_mode(1);

		JsonConf_PepvpnProfilesNew.Profile profile = pepconf.new Profile();
		profile.setId(1);
		profile.setLocal_name("HUB202");
		profile.setRemote_name("HUB202");
		profile.setEnabled(true);
		profile.setEncryption(ENCRYPTION.aes256);
		profile.setServer_list(Arrays.asList("1.1.1.1", "2.2.2.2", "3.3.3.3"));
		profile.setData_port_num(10000);

		profile.setAuthentication(AUTHENTICATION.psk);
		pepconf.setProfileLst(Arrays.asList(profile));

		System.out.println("endpoint = " + JsonUtils.toJson(pepconf));
		System.out.println(pepconf.generateHardwareConfig(JsonConf_PepvpnProfilesNew.class, JsonConf.CONFIG_TYPE.MAX));

		try {
			JsonConf_PepvpnProfiles pep = PepvpnConfigUtils.getDatabasePepvpnFullConfig("xoxcXd", 4);
			JsonConf_PepvpnProfilesNew ep = JsonConf_PepvpnProfilesNew.parseJsonConf_PepvpnProfilesAsEndpoint(pep, 1, "1111-2222-3333", "EP101", 6, false, false);
			System.out.println(ep.generateHardwareConfig(JsonConf_PepvpnProfilesNew.class, JsonConf.CONFIG_TYPE.MAX));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			JsonConf_PepvpnProfiles pep = PepvpnConfigUtils.getDatabasePepvpnFullConfig("xoxcXd", 4);
			System.out.println("pep = " + JsonUtils.toJson(pep));
			
			JsonConf_PepvpnProfilesNew hub = JsonConf_PepvpnProfilesNew.parseJsonConf_PepvpnProfilesAsHub(pep, Arrays.asList(pepconf, pepconf, pepconf), 6, true, true);
			System.out.println("hub = " + hub);
			System.out.println("hub = " + JsonUtils.toJson(hub));
			System.out.println(hub.generateHardwareConfig(JsonConf_PepvpnProfilesNew.class, JsonConf.CONFIG_TYPE.MAX));
		} catch (Exception e) {
			e.printStackTrace();
		}				
	}
}
