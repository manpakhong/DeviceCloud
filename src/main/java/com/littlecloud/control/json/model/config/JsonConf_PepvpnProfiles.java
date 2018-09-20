package com.littlecloud.control.json.model.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.logging.Logger;

import com.google.gson.annotations.SerializedName;
import com.littlecloud.control.entity.ConfigurationPepvpnCertificates;
import com.littlecloud.control.entity.ConfigurationPepvpns;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.json.JsonExclude;
import com.littlecloud.control.json.model.config.JsonConf_PepvpnProfilesNew.AUTHENTICATION;
import com.littlecloud.control.json.model.config.JsonConf_PepvpnProfilesNew.ENCRYPTION;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.SessionKey;

public class JsonConf_PepvpnProfiles extends JsonConf {

	private static final Logger log = Logger.getLogger(JsonConf_PepvpnProfiles.class);

	private Boolean clear_settings = false;
	
	private Integer id; // network_id
	private Boolean enabled;
	private ENCRYPTION encryption;

	private Integer hub_net_id; // hub device network_id
	private Integer hub_id; // hub device id
	private Boolean ha_enabled;
	private Integer ha_hub_net_id; // ha hub device network_id
	private Integer ha_hub_id; // ha hub device id
	
	private List<String> server_list;
	private AUTHENTICATION authentication;
	private String psk;
	@SerializedName("data_port")
	private String data_port_type; // default, custom
	private Integer data_port_num;
	private Boolean nat_enabled;

	private Boolean all_traffic_to_hub;
	private Integer link_failure_mode; // 0-3
	private List<String> dns_list;

	private String hub_name;
	private String endpoint_name;
	private String hub_sn;
	
	/*
	 * NOTE!:
	 * The list below is not persist to database in json format,
	 * the information is stored in pepvpn_configuration_certificates.
	 * So set empty before persist
	 */
	private List<Endpoint> endptNameLst;
	
	public List<String> getDns_list() {
		return dns_list;
	}

	public void setDns_list(List<String> dns_list) {
		this.dns_list = dns_list;
	}

	public class Endpoint {
		private Integer id;
		private String name;
		private String sn;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getSn() {
			return sn;
		}
		public void setSn(String sn) {
			this.sn = sn;
		}
		@Override
		public String toString() {
			return "Endpoint [id=" + id + ", name=" + name + ", sn=" + sn + "]";
		}
	}
	
	public List<Endpoint> getEndptNameLst() {
		return endptNameLst;
	}

	public void setEndptNameLst(List<Endpoint> endptNameLst) {
		this.endptNameLst = endptNameLst;
	}

	private List<Certificate> hub_cert_list;
	private List<Certificate> endpoint_cert_list; // if device(s) in the network is not hub, then endpoint

	@JsonExclude
	private int version = 1;

	private Date timestamp;
	
	public Boolean getClear_settings() {
		return clear_settings;
	}

	public void setClear_settings(Boolean clear_settings) {
		this.clear_settings = clear_settings;
	}

	public void clear_all_certs() {
		clear_cert_list(hub_cert_list);
		clear_cert_list(endpoint_cert_list);
	}
	
	private void clear_cert_list(List<Certificate> certList) {
		if (certList==null)
				return;
		
		for (Certificate cert: certList)
		{
			cert.clearCertificates();
		}
	}
	
//	public ConcurrentHashMap<Integer, String> generateHardwareConfig(Class cls, CONFIG_TYPE configType) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
//	{
//		ConcurrentHashMap<Integer, String> confJsonMap = new ConcurrentHashMap<Integer, String>();
//		if (traverseConfig(confJsonMap, "", this, cls, GLOBAL_DEV_INDEX, configType))
//		{
//			/* Merge global config to per device config */
//			String globalConfig = confJsonMap.get(GLOBAL_DEV_INDEX);
//			for (Integer id : confJsonMap.keySet())
//			{
//				if (id != GLOBAL_DEV_INDEX)
//				{
//					// log.debug("id=" + id);
//					String devConfig = globalConfig + confJsonMap.get(id);
//					confJsonMap.put(id, devConfig);
//				}
//			}
//			confJsonMap.remove(GLOBAL_DEV_INDEX);
//
//			return confJsonMap;
//		}
//		return null;
//	}
	
	public static JsonConf_PepvpnProfiles parseConfigurationPepvpns(ConfigurationPepvpns pepvpn)
	{
		log.debug("pepvpn.getConfig()=" + pepvpn.getConfig());

		JsonConf_PepvpnProfiles pepvpnProfile = JsonUtils.<JsonConf_PepvpnProfiles> fromJson(pepvpn.getConfig(), JsonConf_PepvpnProfiles.class);

		/* need to handle change network id in future since id is in json */
		pepvpnProfile.setEnabled(pepvpn.isEnabled());
		pepvpnProfile.setHa_hub_net_id(pepvpn.getHaHubNetworkId());
		pepvpnProfile.setHa_hub_id(pepvpn.getHaHubDeviceId());
		pepvpnProfile.setHa_enabled(pepvpn.isHaHubEnabled());
		pepvpnProfile.setPsk(pepvpn.getPsk());

		return pepvpnProfile;
	}

	public static JsonConf_PepvpnProfiles generateDefaultInstance(int network_id, List<Devices> devList) {
		JsonConf_PepvpnProfiles instance = new JsonConf_PepvpnProfiles();
		instance.setId(network_id);

		instance.setEnabled(false);
		instance.setEncryption(ENCRYPTION.aes256);
		instance.setAuthentication(AUTHENTICATION.psk);
		instance.setData_port_type("default");
		instance.setLink_failure_mode(0);
		instance.setTimestamp(new Date());
		instance.setHa_enabled(false);
		instance.setNat_enabled(true);
		
		/* generate default endpoint cert list (cert list without cert) */
		List<Certificate> endpointCertLst = new ArrayList<Certificate>();
		Certificate certEndpointJson;

		if (devList!=null)
		{
			for (Devices dev : devList)
			{
				log.debug("getting endpoint " + dev.getId());
				certEndpointJson = instance.new Certificate();
				certEndpointJson.parseCertificates(null, dev);
				endpointCertLst.add(certEndpointJson);
			}
		}
		instance.setEndpoint_cert_list(endpointCertLst);

		return instance;
	}
		
	public void mergeDns_list(JsonConf_PepvpnProfiles update)
	{
		/* server list has no need to merge, just overwrite! */
		dns_list = update.getDns_list();		
		return;
	}
	
	public void mergeServer_list(JsonConf_PepvpnProfiles update)
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

		// Map<String, String> newMapProfiles = new HashMap<String, String>();
		//
		// /* create hashmap for new ssid items */
		// for (int j = 0; j < update.getServer_list().size(); j++)
		// {
		// String m = update.getServer_list().get(j);
		// newMapProfiles.put(m, update.getServer_list().get(j));
		// }
		// log.debug("newMapProfiles = " + newMapProfiles.keySet());
		//
		// /* create a new list from old list and new list */
		// List<String> resultLst = update.getServer_list();
		//
		// /* loop old list */
		// for (int j = 0; j < server_list.size(); j++)
		// {
		// String m = server_list.get(j);
		// String key = m;
		//
		// /* if new list contains old list item, use new list. if not, add old list item. */
		// log.debug(key);
		// if (!newMapProfiles.containsKey(key))
		// {
		// log.debug(key + " is NOT contained.");
		// resultLst.add(server_list.get(j));
		// }
		// }
		// server_list = resultLst;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getVersion() {
		return version;
	}

	public String getHub_name() {
		return hub_name;
	}

	public void setHub_name(String hub_name) {
		this.hub_name = hub_name;
	}
	
	public String getHub_sn() {
		return hub_sn;
	}

	public void setHub_sn(String hub_sn) {
		this.hub_sn = hub_sn;
	}

	public String getEndpoint_name() {
		return endpoint_name;
	}

	public void setEndpoint_name(String endpoint_name) {
		this.endpoint_name = endpoint_name;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	@JsonConfList
	public List<String> getServer_list() {
		return server_list;
	}

	public void setServer_list(List<String> server_list) {
		this.server_list = server_list;
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
	
	public Boolean getNat_enabled() {
		return nat_enabled;
	}

	public void setNat_enabled(Boolean nat_enabled) {
		this.nat_enabled = nat_enabled;
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
		
		public boolean equals(Object o) {
			if (o instanceof SessionKey) {		
				SessionKey sess = (SessionKey) o;
				if (this.hashCode()==sess.hashCode())
					return true;
			}
			return false;
		}
		
		public int hashCode()
		{
			return this.id;
		}
	}

//	@Override
//	public String toString() {
//		return "JsonConf_PepvpnProfiles [id=" + id + ", enabled=" + enabled + ", encryption=" + encryption + ", hub_net_id=" + hub_net_id + ", hub_id=" + hub_id + ", ha_enabled=" + ha_enabled + ", ha_hub_net_id=" + ha_hub_net_id + ", ha_hub_id=" + ha_hub_id + ", server_list=" + server_list + ", authentication=" + authentication + ", data_port_type=" + data_port_type + ", data_port_num=" + data_port_num + ", all_traffic_to_hub=" + all_traffic_to_hub + ", link_failure_mode=" + link_failure_mode
//				+ ", hub_cert_list=" + hub_cert_list + ", endpoint_cert_list=" + endpoint_cert_list + ", version=" + version + ", timestamp=" + timestamp + "]";
//	}
	
//	public static void main(String args[]) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException 
//	{
//		
//		JsonConf_PepvpnProfiles pepconf = new JsonConf_PepvpnProfiles();
//		pepconf.setData_port_num(10000);
//		System.out.println(pepconf.generateHardwareConfig(JsonConf_PepvpnProfiles.class, JsonConf.CONFIG_TYPE.MAX));
//	}
}
