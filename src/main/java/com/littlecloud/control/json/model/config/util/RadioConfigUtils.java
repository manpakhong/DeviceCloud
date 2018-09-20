package com.littlecloud.control.json.model.config.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.DebugManager;
import com.littlecloud.ac.json.model.Json_Config_Icmg;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.ConfigurationPepvpnsDAO;
import com.littlecloud.control.dao.ConfigurationSsidsDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.TagsDAO;
import com.littlecloud.control.dao.branch.ProductsDAO;
import com.littlecloud.control.dao.criteria.ConfigurationSsidsCriteria;
import com.littlecloud.control.entity.ConfigurationPepvpns;
import com.littlecloud.control.entity.ConfigurationSettings;
import com.littlecloud.control.entity.ConfigurationSettingsId;
import com.littlecloud.control.entity.ConfigurationSsids;
import com.littlecloud.control.entity.DeviceFeatures;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.Tags;
import com.littlecloud.control.entity.branch.Products;
import com.littlecloud.control.json.model.Json_Tags;
import com.littlecloud.control.json.model.config.JsonConf;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings.Modules;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettingsWeb;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfiles;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfilesWeb;
import com.littlecloud.control.json.model.config.util.PortalConfigUtils.PortalSsidConfigInfo;
import com.littlecloud.control.json.model.config.util.exception.FeatureNotFoundException;
import com.littlecloud.control.webservices.handler.NetworkWsHandler.WIFICFG;
import com.littlecloud.control.webservices.util.ProductWsUtils;
import com.littlecloud.pool.object.ConfigPutObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.FeatureGetObject;
import com.littlecloud.pool.object.IcmgPutObject;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.littlecloud.pool.object.utils.FeatureGetUtils;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;
import com.littlecloud.pool.object.utils.ProductUtils;
import com.littlecloud.pool.utils.PropertyService;
import com.peplink.api.db.util.DBUtil;

public class RadioConfigUtils {

	private static PropertyService<RadioConfigUtils> ps = new PropertyService<RadioConfigUtils>(RadioConfigUtils.class);

	private static boolean PROD_MODE = DebugManager.isPROD_MODE();

	private static final Logger log = Logger.getLogger(RadioConfigUtils.class);
	
	private static final String NON_PATTERN_SITE_ID = "(?i)[^a-z0-9!@#$%^&*()=`_+~\\[\\]\\{}|;:'\",./<>?-]";
	private static enum DEVICE_TYPE {balancemax, ap}

	public static final String DATA_TAG = "data";	
	public static final Integer MAX_RETRY_CFG_APPLY = Integer.valueOf(ps.getString("MAX_RETRY_CFG_APPLY"));
	public static final Integer MIN_RETRY_MIN_TIMEOUT = 1;	/* minimum per retry wait for 1 min */
	public static final Integer MAX_RETRY_WAIT_MIN = 30;	/* maximum retry wait for 30 mins */

	public static final String PROHIBIT_WIFI_TAG_FILTER = "^EXTAP_ENABLE$";
	public static final String MVPN_FILTER_ALL = "^MVPN[0-9]_.*$";
	public static final String MVPN_FILTER_ALL2 = "^MVPN_.*$";
	public static final String MVPN_FILTER_P1 = "^MVPN";
	public static final String MVPN_FILTER_P2 = ".*_.*$";
	// public static final String PEPVPN_ORDER_FILTER = "^MVPN_ORDER$";
	public static final String WLAN_FILTER_ALL = "^WLAN[0-9].*_.*$";
	public static final String WLAN_FILTER_ALL2 = "^WLAN.*_.*$";
	public static final String WLAN_FILTER_P1 = "^WLAN";
	public static final String WLAN_FILTER_P2 = ".*_.*$";
	public static final String ICMG_TAG = "_IC_MG";
	public static final String ICMG_MVPN = "MVPN";
	public static final String ICMG_WLAN = "WLAN";

	public static final String MVPN_ORDER_TAG = "MVPN_ORDER";
	public static final String WLAN_ORDER_TAG = "WLAN_ORDER";
	public static final Integer MVPN_INFINITE_LICENSE_NUM = 99999; // for assigning infinite number of pepvpn license
	public static final Integer MVPN_MIN_PROFILE_NAME_LENGTH = 15; // e.g. -1234-1234-1234

	public static final String WLC_FEATURE_TAG = "EXTAP_ENABLE";
	public static final String GROUP_MASTER_CFG_PATH = ps.getString("GROUP_MASTER_CFG_PATH");
	public static final String GROUP_MASTER_CFG_SUFFIX = "_extracted/config";
	public static final String FIRMWARE_BLACKLIST = ps.getString("FIRMWARE_BLACKLIST");
	public static final String PORTAL_URL = ps.getString("PORTAL_URL");

	public static enum WEBAMIN_CFG {
		WLC
	}

	public static class WebadminCfg
	{
		private String value;

		public String getValue() {
			return value;
		}

		public boolean contains(WEBAMIN_CFG f)
		{
			if (value == null || value.isEmpty())
				return false;

			String[] items = value.split("\\|");
			boolean isFound = false;

			if (items != null)
			{
				for (String item : items)
				{
					if (item.equals(f.name()))
					{
						isFound = true;
					}
				}
			}

			return isFound;
		}

		public void removeCfg(WEBAMIN_CFG f)
		{
			String newValue = null;

			if (value != null)
			{
				String[] items = value.split("\\|");
				if (items != null)
				{
					for (String item : items)
					{
						if (!item.equalsIgnoreCase(f.name()))
						{
							if (items[0].isEmpty())
								newValue = f.name();
							else
								newValue += "|" + f.name();
						}
					}
				}
			}

			this.value = newValue;
		}

		public void addCfg(WEBAMIN_CFG f)
		{
			boolean isFound = false;
			if (value != null)
			{
				String[] items = value.split("\\|");
				if (items != null)
				{
					for (String item : items)
					{
						if (item.equals(f.name()))
						{
							isFound = true;
						}
					}

					if (isFound == false && items.length > 0)
					{
						if (items[0].isEmpty())
							this.value = f.name();
						else
							this.value += "|" + f.name();
					}

				}
			}
			else
			{
				this.value = f.name();
			}
		}

		public WebadminCfg(String value) {
			super();
			this.value = value;
		}
	}

	public static List<String> CONFIG_FH_SPEC_KEY_PATTERN_LIST = initCONFIG_FH_SPEC_KEY_PATTERN_LIST();

	private static List<String> initCONFIG_FH_SPEC_KEY_PATTERN_LIST() {
		List<String> result = new ArrayList<String>();
		result.add("MVPN_DHCP_SERVER");
		result.add("MVPN_DHCP_SERVER_POOL_START");
		result.add("MVPN_DHCP_SERVER_POOL_END");
		result.add("MVPN_DHCP_SERVER_NETMASK");
		result.add("MVPN_NAT_MODE_NAT_ENABLE");
		return result;
	}

	public static boolean isBlacklistFirmware(String version)
	{
		if (version==null || version.isEmpty())
		{
			log.info("version null is given");
			return true;
		}
		
		for (String s:FIRMWARE_BLACKLIST_ARRAY)
		{
			if (s.equalsIgnoreCase(version))
				return true;
		}
		return false;
	}
	
	public static List<String> FIRMWARE_BLACKLIST_ARRAY = initFIRMWARE_BLACKLIST_ARRAY();
	
	private static List<String> initFIRMWARE_BLACKLIST_ARRAY() {
		List<String> result = new ArrayList<String>();
		String arr[] = FIRMWARE_BLACKLIST.split(",");
		if (arr!=null && arr.length!=0)
		{
			for (String s:arr)
				result.add(s);
		}				
		return result;
	}

	public static String formatSiteId(String site_id)
	{
		/* suppose site_id is sn without minus sign */
		if (site_id == null || site_id.length() == 0)
		{
			log.warn("site_id is empty!");
			return "";
		}

		site_id = site_id.replaceAll("-", "");

		/* hardware pattern matching */
		String result = site_id.replaceAll(NON_PATTERN_SITE_ID, "");
		if (result.length() >= 3)
			result = result.substring(0, (result.length() < 13 ? result.length() : 13)); // endIndex - 1
		else
			result = result + "---";
		return result;
	}

	public static JsonConf_RadioSettingsWeb getDatabaseRadioFullConfig(String param_orgId, int param_networkId, int param_deviceId) throws Exception
	{
		return getDatabaseRadioFullConfig(param_orgId, param_networkId, param_deviceId, false);
	}
	
	public static JsonConf_RadioSettingsWeb getDatabaseRadioFullConfig(String param_orgId, int param_networkId, int param_deviceId, boolean isForApplyConfig) throws Exception
	{
		return getDatabaseRadioFullConfig(param_orgId, param_networkId, param_deviceId, isForApplyConfig, null);
	}
	
	public static JsonConf_RadioSettingsWeb getDatabaseRadioFullConfig(String param_orgId, int param_networkId, int param_deviceId, boolean isForApplyConfig, StringBuilder sbSummary) throws Exception
	{
		/* 
		 * bForApplyConfig = true => ssidProfilesWeb;	// for config application
		 * bForApplyConfig = false => ssidProfiles;		// for web
		 */
		String infoLog = String.format("orgId %s netId %d devId %d isApply %s", param_orgId, param_networkId, param_deviceId, isForApplyConfig);
		boolean isOwnSession = false;
		
		boolean bReadOnly = true;
		boolean isDeviceFollowGroupSettings = false;
				
		DevicesDAO devDAO = null;
		TagsDAO tagDAO = null;		
		ProductsDAO productDAO = null;
		ConfigurationSsidsDAO ssidDAO = null;
		List<ConfigurationSsids> ssidLst = null;
		ConfigurationSsidsCriteria criteria = null;
		JsonConf_RadioSettingsWeb radioJson = null;
		List<Modules> moduleLst = null;
		
		Boolean isDeviceWifiManaged = false;
		Boolean isGroupWifiManaged = false;
		Boolean isWifiManagedByInControl = false;
		int MAX_SSIDPROFILE = -1;		
		Map<String, String> applyModFreqRadioMap = new HashMap<String, String>();
		String selectedRadio = null;
		
		/* Device settings parameters */
		DeviceFeatures feature = null;
		
		DBUtil dbUtil = null;
		ConfigurationSettingsUtils settingsUtils = null;
		ConfigurationSettings devSettings = null;
		ConfigurationSettings netSettings = null;
		int ssid_index = 1;
		
		List<Devices> devList = new ArrayList<Devices>();
		if (sbSummary==null)
			sbSummary = new StringBuilder();
		
		/* if no existing configuration from db, create default */				
		try {			
			dbUtil = DBUtil.getInstance();
			if (!dbUtil.isSessionStarted())
			{
				dbUtil.startSession();
				isOwnSession = true;
			}
			
			devDAO = new DevicesDAO(param_orgId, bReadOnly);
			tagDAO = new TagsDAO(param_orgId, bReadOnly);			
			productDAO = new ProductsDAO(bReadOnly);
			ssidDAO = new ConfigurationSsidsDAO(param_orgId, bReadOnly);		
			
			/* check setting level */
			boolean isDeviceLevelConfig = (param_deviceId != 0);
			log.debugf("getDatabaseRadioFullConfig for %s %d %d bForApplyConfig:%s bIsIndividualSettings:%s)", 
					param_orgId, param_networkId, param_deviceId, isForApplyConfig, isDeviceLevelConfig);
			
			settingsUtils = new ConfigurationSettingsUtils(param_orgId, param_networkId, param_deviceId);

			/* load group level config */
			netSettings = settingsUtils.getNetworkConfigSettings();
			if (netSettings==null)
			{
				log.infof("%s has no netSettings", infoLog);
				return new JsonConf_RadioSettingsWeb();
			}
			else
			{
				isGroupWifiManaged = netSettings.isWifi_enabled();
			}
			
			/* Get device list and check if wifi is managed by InControl2 */
			Devices devInd = null;
			if (isDeviceLevelConfig)
			{				
				devSettings = settingsUtils.getDevConfigSettings();
				if (devSettings==null)
				{
					log.warnf("%s has no devSettings, create now", infoLog);
					try {
						devDAO = new DevicesDAO(param_orgId, bReadOnly);
						devInd = devDAO.findById(param_deviceId);
						if (devInd!=null)
						{
							devSettings = settingsUtils.initDevConfigSettings();
						} else {
							log.warnf("dev %s %d is not found!", param_orgId, param_deviceId);
							return new JsonConf_RadioSettingsWeb();
						}
					} catch (Exception e)
					{
						log.errorf("fail to init dev config settings for %s %d (%s)", param_orgId, param_deviceId, e.getMessage());
						return new JsonConf_RadioSettingsWeb();
					}
				}
				
				devInd = NetUtils.getDevices(param_orgId, param_networkId, param_deviceId, false);
				if (devInd == null)
				{
					log.warnf("devId %d in org %s net %d does not exist!!", param_deviceId, param_orgId, param_networkId);
					return new JsonConf_RadioSettingsWeb();
				}
				
				isDeviceFollowGroupSettings = devSettings.isFollow_network();
				isDeviceWifiManaged = devSettings.isWifi_enabled();
				
				if (isDeviceFollowGroupSettings)
				{
					if (isGroupWifiManaged)
						isWifiManagedByInControl = true;
					else
						isWifiManagedByInControl = false;
				}				
				else
				{
					isWifiManagedByInControl = isDeviceWifiManaged;
				}
				
				devList.add(devInd);
				sbSummary.append(String.format("\n* %s has device level settings: \n - isDeviceWifiManaged %s \n - isDeviceFollowGroupSettings %s \n - isGroupWifiManaged %s", infoLog, isDeviceWifiManaged, isDeviceFollowGroupSettings, isGroupWifiManaged));
			} // end if (isDeviceLevelConfig)				
			else
			{
				isWifiManagedByInControl = isGroupWifiManaged;
				
				if (isForApplyConfig)
				{
					List<Devices> tmpDevList = DeviceUtils.getDevLst(param_orgId, param_networkId);
					for (Devices dev : tmpDevList)
					{
						if (PROD_MODE)
						{
							DevOnlineObject devO = DeviceUtils.getDevOnlineObject(dev);
							if (devO != null && devO.isOnline())
							{
								devList.add(dev);
							}
						}
						else
						{
							devList.add(dev);
						}
					}
				}
				else
				{
					devList = DeviceUtils.getDevLst(param_orgId, param_networkId);						
				}
				
				sbSummary.append(String.format("\n* %s has group level settings - isGroupWifiManaged %s", infoLog, isGroupWifiManaged));
			}			
			log.debugf("devList=%s", devList);

			/* parse module */
			if (log.isInfoEnabled()) log.info("parsing module start");
			RadioConfigHelper radioHelper = new RadioConfigHelper(param_orgId, param_networkId, devList, isForApplyConfig, sbSummary);
			radioJson = radioHelper.getRadioJson();
			moduleLst = radioHelper.getModuleLst();
			if (log.isDebugEnabled()) log.debugf("radioHelper.getModuleLst() = %s", moduleLst);
			
			HashMap<Integer, DeviceFeatures> featureMap = radioHelper.getFeatureMap();
			radioJson.setModulesLst(moduleLst);
			
			radioJson.setWifi_managed_by_ic2(isWifiManagedByInControl);
			sbSummary.append(String.format("\n = isWifiManagedByInControl %s", isWifiManagedByInControl));
			
			if (isDeviceLevelConfig)
				radioJson.setWifi_mgm_enabled(isDeviceWifiManaged);
			else
				radioJson.setWifi_mgm_enabled(isGroupWifiManaged);

			//TMP revamp dev lvl and net lvl
			if (isDeviceLevelConfig)
			{
				radioJson.setFollow_network(devSettings.isFollow_network());
				log.debugf("dev %s has dev level wifi config enable: %s", devInd.getSn(), !radioJson.getFollow_network());
				
				feature = featureMap.get(param_deviceId);
				if (feature != null)
				{
					MAX_SSIDPROFILE = feature.getWifiSsid();
				}
				else
				{
					Products product = productDAO.findById(devInd.getProductId());
					if (product==null)
						throw new FeatureNotFoundException("Product record is not found for devInd "+param_orgId+" "+devInd+" product "+devInd.getProductId());
					MAX_SSIDPROFILE = product.getSsidSupport();				
				}
				
				if (!isForApplyConfig)
				{
					/* not for apply config */
					criteria = new ConfigurationSsidsCriteria();
					criteria.setNetworkId(param_networkId);
					criteria.setDeviceId(param_deviceId);
					criteria.setOrderBy("network_id asc, device_id asc, ssid_id asc");
					ssidLst = ssidDAO.getConfigurationSsids(criteria);					
					if (ssidLst == null)
						ssidLst = new ArrayList<ConfigurationSsids>();
				}
				else
				{
					/* determine load dev or net level cfg */
					if (isDeviceFollowGroupSettings)
					{
						log.debug("/* get follow network ssid */");
						if (isGroupWifiManaged)
						{
							criteria = new ConfigurationSsidsCriteria();
							criteria.setNetworkId(param_networkId);
							criteria.setDeviceId(0);
							criteria.setOrderBy("network_id asc, device_id asc, ssid_id asc");
							ssidLst = ssidDAO.getConfigurationSsids(criteria);
							if (ssidLst == null)
								ssidLst = new ArrayList<ConfigurationSsids>();
						}
						else
						{
							/* wifi is not managed */
							ssidLst = new ArrayList<ConfigurationSsids>();
						}
					}
					else
					{
						log.debug("/* get dev level ssid */");
						if (isDeviceWifiManaged)
						{
							criteria = new ConfigurationSsidsCriteria();
							criteria.setNetworkId(param_networkId);
							criteria.setDeviceId(param_deviceId);
							criteria.setOrderBy("network_id asc, device_id asc, ssid_id asc");
							ssidLst = ssidDAO.getConfigurationSsids(criteria);
							if (ssidLst == null)
								ssidLst = new ArrayList<ConfigurationSsids>();
						}
						else
						{
							/* wifi is not managed */
							ssidLst = new ArrayList<ConfigurationSsids>();
						}
					}
				}
			} // end if (isDeviceLevelConfig)
			else
			{
				criteria = new ConfigurationSsidsCriteria();
				criteria.setNetworkId(param_networkId);
				criteria.setDeviceId(0);
				criteria.setOrderBy("network_id asc, device_id asc, ssid_id asc");
				ssidLst = ssidDAO.getConfigurationSsids(criteria);
				if (ssidLst!=null)
				{
					MAX_SSIDPROFILE = ssidLst.size();
				}				
			}
			if (log.isDebugEnabled()) log.debugf("MAX_SSIDPROFILE = %d", MAX_SSIDPROFILE);
			

			List<JsonConf_SsidProfiles> ssidProLst = new ArrayList<JsonConf_SsidProfiles>();
			List<JsonConf_SsidProfiles> ssidProReservedLst = new ArrayList<JsonConf_SsidProfiles>();
			List<JsonConf_SsidProfilesWeb> ssidProWebLst = new ArrayList<JsonConf_SsidProfilesWeb>();
			Map<Integer, List<Tags>> ssidMapTagLst = null;
			List<Integer> ssidIdLst = new ArrayList<Integer>();
			for (int i = 1; i <= ssidLst.size(); i++)
			{				
				ssidIdLst.add(ssidLst.get(i-1).getId().getSsidId());
			}			
			
			for (int i = 1; i <= ssidLst.size(); i++)
			{
				boolean isSsidAvalEnabled = false;
				boolean isSsidRequired = false;
				boolean isSsidReserved = false;

				/* get ssid profile */
				ConfigurationSsids ssidProfile = null;
				JsonConf_SsidProfiles ssidProfileJson = null;
				JsonConf_SsidProfilesWeb ssidProfileJsonWeb = null;
				
				ssidProfile = ssidLst.get(i-1);
				
				if (isForApplyConfig)
				{
					applyModFreqRadioMap = RadioConfigHelper.getModFreqRadioMap(moduleLst);
					//if (log.isDebugEnabled()) log.debugf("modFreqRadioMap = %s", applyModFreqRadioMap);
					
					/* apply config - ssidProLst */
					if (ssidProLst.size()>=MAX_SSIDPROFILE)
					{
						/* stop adding further ssid when limit is reached */
						isSsidReserved = true;
					}
						
					if (isDeviceLevelConfig)
					{
						if (isDeviceFollowGroupSettings)
						{
							//if (log.isDebugEnabled()) log.debug("/* apply follow network */");
							if (isGroupWifiManaged)
							{
								//if (log.isDebugEnabled()) log.debug("/* network managed */");
								/* load database data for the ssid */
								ssidProfileJson = JsonConf_SsidProfiles.parseConfigurationSsids(ssidProfile);

								/* load device availability for the ssid */
								//List<Tags> tagLst = tagDAO.findByConfigurationSsidsId(ssidProfile.getId());
								if (ssidMapTagLst == null)
								{									
									ssidMapTagLst = tagDAO.findByNetIdSsidId(param_networkId, ssidIdLst);
									//if (log.isDebugEnabled()) log.debugf("ssidMapTagLst(%s) = %s", ssidIdLst, ssidMapTagLst);
								}
								
								List<Tags> tagLst = ssidMapTagLst.get(ssidProfile.getId().getSsidId());
								if (ssidProfile != null && tagLst != null && tagLst.size() != 0) {
									//if (log.isDebugEnabled()) log.debug("- ssid_id " + ssidProfile.getId().getSsidId() + " " + ssidProfile.getSsid() + " has aval settings tagLst=" + tagLst);
									if (ssidProfileJson.getEnabled())
									{
										for (Tags t : tagLst) {
											List<Integer> devLst = devDAO.getDevicesIdByTagses(Arrays.asList(t.getName()), param_networkId);
											//if (log.isDebugEnabled()) log.debugf("devLst=%s (devId=%d)", devLst, devInd.getId());
											if (devLst.contains(devInd.getId().intValue()))
												isSsidAvalEnabled = true;
											
//											for (Integer d : devLst) {
//												if (d.intValue() == devInd.getId().intValue()) {
//													isSsidAvalEnabled = true;
//												}
//											}
										}

										/* set ssid radio selection */ 
										if (isSsidAvalEnabled) {
											if (ssidProfileJson.getRadio_band()!=null)
											{
												selectedRadio = applyModFreqRadioMap.get(ssidProfileJson.getRadio_band());
												if (selectedRadio!=null)
												{
													ssidProfileJson.setRadio_select(selectedRadio);
													
													if (log.isDebugEnabled()) log.debug("- ssid_id " + ssidProfile.getId().getSsidId() + " " + ssidProfile.getSsid() + " has aval settings - CONTAINS devId " + param_deviceId);
													if (ssidProfileJson.getEnabled())
														isSsidRequired = true;
														//ssidProLst.add(ssidProfileJson);
												}
											}
											else
											{
												if (ssidProfileJson.getEnabled())
													isSsidRequired = true;
													//ssidProLst.add(ssidProfileJson);
											}											
										} else {
											//if (log.isDebugEnabled()) log.debug("- ssid_id " + ssidProfile.getId().getSsidId() + " " + ssidProfile.getSsid() + " has aval settings - NOT CONTAINS devId " + param_deviceId);											
											sbSummary.append(String.format("\n* - SSID %d %s is not required - with aval settings", ssidProfileJson.getSsid_id(), ssidProfileJson.getSsid()));
										}
									}
									else
									{
										//if (log.isDebugEnabled()) log.debug("- ssid_id " + ssidProfile.getId().getSsidId() + " " + ssidProfile.getSsid() + " is disabled");
										sbSummary.append(String.format("\n* - SSID %d %s is not required - disabled", ssidProfileJson.getSsid_id(), ssidProfileJson.getSsid()));
									}
								} else {
									/* no ssid aval settings */
									if (log.isDebugEnabled()) log.debug("- ssid_id " + ssidProfile.getId().getSsidId() + " " + ssidProfile.getSsid() + " has NO aval settings");
									if (ssidProfileJson.getRadio_band()!=null)
									{
										//if (log.isDebugEnabled()) log.debugf("ssidProfileJson.getRadio_band()=%s modFreqRadioMap=%s", ssidProfileJson.getRadio_band(), applyModFreqRadioMap);
										
										selectedRadio = applyModFreqRadioMap.get(ssidProfileJson.getRadio_band());
										if (selectedRadio!=null)
										{
											ssidProfileJson.setRadio_select(selectedRadio);		
											if (ssidProfileJson.getEnabled())
												isSsidRequired = true;
										}
										else
										{
											//if (log.isDebugEnabled()) log.debugf("- ssid_id %d %s band %s is not allowed (%s)", ssidProfile.getId().getSsidId(), ssidProfile.getSsid(), ssidProfileJson.getRadio_band(), applyModFreqRadioMap);
											sbSummary.append(String.format("\n* - SSID %d %s is not required - band %s is not allowed", ssidProfileJson.getSsid_id(), ssidProfileJson.getSsid(), ssidProfileJson.getRadio_band()));
										}
									}
									else
									{
										if (ssidProfileJson.getEnabled())
											isSsidRequired = true;
									}
								}								

							} else {
								//if (log.isDebugEnabled()) log.debug("/* network not managed */");
							}
						}	// end if (isDeviceFollowGroupSettings)
						else
						{
							if (log.isDebugEnabled()) log.debug("/* apply individual */");
							if (isDeviceWifiManaged)
							{
								/* load database data for the ssid */
								ssidProfileJson = JsonConf_SsidProfiles.parseConfigurationSsids(ssidProfile);

								if (ssidProfileJson.getRadio_band()!=null)
								{
									selectedRadio = applyModFreqRadioMap.get(ssidProfileJson.getRadio_band());
									if (selectedRadio!=null)
									{
										ssidProfileJson.setRadio_select(selectedRadio);		
										if (ssidProfileJson.getEnabled())
											isSsidRequired = true;
									}
								}
								else
								{
									/* handle origin config without band specified! */	
									if (ssidProfileJson.getEnabled())
										isSsidRequired = true;
								}
							}
						}
						
						if (isSsidRequired)
						{
							sbSummary.append("\n>>> Portal config >>>");
							PortalSsidConfigInfo ctr = new PortalSsidConfigInfo();
							ctr.setSsidProfileJson(ssidProfileJson);							
							if (feature==null)
							{
								//log.warnf("FEATURE_NOT_FOUND - for device %d %s apply portal config. Default is PORTAL_NOT_SUPPORT (%s)!", devInd.getIanaId(), devInd.getSn(), ctr);
								sbSummary.append(String.format("\n* - FEATURE_NOT_FOUND - Default is PORTAL_NOT_SUPPORT!"));
								ctr.setIsPortalIc2Support(false);
								ctr.setIsPortalFbWifiSupport(false);
							}
							else
							{
								FeatureGetObject fgo = FeatureGetUtils.createFeatureGetObject(devInd.getIanaId(), devInd.getSn(), feature);
								ctr.setIsPortalIc2Support(FeatureGetUtils.isPortalIc2Support(fgo));
								ctr.setIsPortalFbWifiSupport(FeatureGetUtils.isPortalFbWifiSupport(fgo));								
							}							
							if (!PortalConfigUtils.disablePortalIfNotSupport(ctr))
							{
								//if (log.isDebugEnabled()) log.debugf("PORTAL_NOT_SUPPORT - for device %d %s apply portal config. Default is PORTAL_DISABLED (%s)!", devInd.getIanaId(), devInd.getSn(), ctr);
								sbSummary.append(String.format("\n* - PORTAL_NOT_SUPPORT - Default is PORTAL_DISABLED!"));
							}
							
													
							if (isSsidReserved)
							{
								ssidProReservedLst.add(ssidProfileJson);
								sbSummary.append(String.format("\n* - SSID %d %s is reserved", ssidProfileJson.getSsid_id(), ssidProfileJson.getSsid()));
							}
							else
							{
								ssidProLst.add(ssidProfileJson);
								sbSummary.append(String.format("\n* - SSID %d %s is selected", ssidProfileJson.getSsid_id(), ssidProfileJson.getSsid()));
							}
						}
						
						/* filter radio band not match capability - cant find wlan config to bind specific radio settings */
					}	// end if (isDeviceLevelConfig)					
					/* else no ssid settings return for network (non-individual) config */
				}	// end if (isForApplyConfig)
				else
				{
					/* load database data for the ssid */						
					ssidProfileJsonWeb = JsonConf_SsidProfilesWeb.parseConfigurationSsids(ssidProfile);
					
					if (!isDeviceLevelConfig)
					{
						List<Tags> tagLst = tagDAO.getAllTagsWithSsids(ssidProfile.getId().getNetworkId(), ssidProfile.getId().getDeviceId(), ssidProfile.getId().getSsidId());
						List<Json_Tags> jsonTagLst = new ArrayList<Json_Tags>();
						for (Tags t:tagLst)
						{
							Json_Tags jsonTag = new Json_Tags();
							jsonTag.parseTags(t);
							jsonTagLst.add(jsonTag);
						}					
						ssidProfileJsonWeb.setTags(jsonTagLst);	
					}
					
					ssidProWebLst.add(ssidProfileJsonWeb);
				}
			}	// end for (int i = 1; i <= ssidLst.size(); i++)
			
			if (isForApplyConfig)
			{				
				/* re-order ssid_id if apply config */
				if (!ssidProLst.isEmpty())
				{
					for (JsonConf_SsidProfiles proJson:ssidProLst)
					{
						proJson.setSsid_id(ssid_index++);
					}
				}
				
				/* check icmg */
				if (isWifiManagedByInControl)
				{
					Devices dev = devDAO.findById(param_deviceId);
					if (dev != null)
					{
						RadioConfigUtils.WebadminCfg wc = new RadioConfigUtils.WebadminCfg(dev.getWebadmin_cfg());
						if (!wc.contains(RadioConfigUtils.WEBAMIN_CFG.WLC))
							radioJson.setWebadminShowIcmg(true);
					}
				}
				else
				{
					radioJson.setWebadminShowIcmg(false);
				}
			}
			
			if (!ssidProLst.isEmpty())
				radioJson.setSsidProfilesLst(ssidProLst);
			
			if (!ssidProReservedLst.isEmpty())
				radioJson.setSsidProfilesReservedLst(ssidProReservedLst);
			
			if (!ssidProWebLst.isEmpty())
				radioJson.setSsidProfilesWebLst(ssidProWebLst);

		} catch (Exception e) {
			if (log.isInfoEnabled()) log.infof("Exception - " + e);
			throw e;
		} finally {
			try {
				if (isOwnSession)
				{
					if (dbUtil!=null && dbUtil.isSessionStarted()) {
						dbUtil.endSession();
					}
				}					
			} catch (Exception e2) {
				log.errorf("Exception getDatabaseRadioFullConfig endSession ", e2);
				throw e2;
			}
		}
		return radioJson;
	}

	public static void setMatchedTagValue(Properties config, String regexpr, String value)
	{
		Enumeration enumeration = (Enumeration<String>) config.propertyNames();
		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			if (key.matches(regexpr)) {
				log.infof("key %s matches expr %s", key, regexpr);
				config.setProperty(key, value);
			}
		}
	}

	public static boolean webadminShowIcmgMvpn(String orgId, Integer netId, Integer devId) throws SQLException
	{
		boolean rdonly = true;
		boolean result = false;

		ConfigurationPepvpnsDAO pepDAO = new ConfigurationPepvpnsDAO(orgId, rdonly);

		boolean bNetworkPepvpnManaged = false;
		ConfigurationPepvpns confPep = pepDAO.findById(netId);
		if (confPep == null)
		{
			log.info("no pepvpn config, not enabled!");
			bNetworkPepvpnManaged = false;
		}
		else if (confPep.isEnabled())
		{
			bNetworkPepvpnManaged = true;
		}

		boolean isActiveHub = pepDAO.isEnabledMasterHubInAnyNetwork(devId);
		boolean isActiveHaHub = pepDAO.isEnabledHaHubInAnyNetwork(devId);

		result = bNetworkPepvpnManaged || isActiveHub || isActiveHaHub;

		return result;
	}

	public static void sendWebadminIcmg(String sid, Integer iana_id, String sn, boolean bICMG_MVPN, boolean bICMG_WLAN, String msg, boolean bRetry, QueryInfo<Object> info)
	{
		log.infof("sendWebadminIcmg (%s)", msg);
		boolean bAddRetryCount = false;
		IcmgPutObject icmgPutO = null;

		if (bRetry)
		{
			IcmgPutObject icmgPutSample = new IcmgPutObject();
			icmgPutSample.setIana_id(iana_id);
			icmgPutSample.setSn(sn);
			try {
				icmgPutO = ACUtil.<IcmgPutObject> getPoolObjectBySn(icmgPutSample, IcmgPutObject.class);

				if (icmgPutO == null)
				{
					log.errorf("Existing IcmgPutObject not existed for retry case.");
				}
				else
				{
					if (info.getStatus() != 200 || icmgPutO.isSuccess() == null || !icmgPutO.isSuccess())
					{
						log.warnf("%s Fail to PIPE_INFO_TYPE_CONFIG_ICMG_PUT with status %d %s", msg, info.getStatus(), icmgPutO.isSuccess());
						// icmgPutO.setRetry(icmgPutO.getRetry() + 1);
						bAddRetryCount = true;
					}

					if (icmgPutO.getRetry() > 2)
					{
						log.warnf("%s maximum icmg retry has reached %d", msg, icmgPutO.getRetry());
						return;
					}
				}
			} catch (InstantiationException | IllegalAccessException e) {
				log.errorf("Cache Exception %s %s", msg, e.getMessage());
			}
		}

		Json_Config_Icmg configIcmg = new Json_Config_Icmg();
		List<Json_Config_Icmg.Icmg> icmgLst = new ArrayList<Json_Config_Icmg.Icmg>();
		Json_Config_Icmg.Icmg icmgPepvpn = configIcmg.new Icmg(RadioConfigUtils.ICMG_MVPN, bICMG_MVPN ? "1" : "0");
		Json_Config_Icmg.Icmg icmgWifi = configIcmg.new Icmg(RadioConfigUtils.ICMG_WLAN, bICMG_WLAN ? "1" : "0");
		icmgLst.add(icmgPepvpn);
		icmgLst.add(icmgWifi);
		configIcmg.setIcmgLst(icmgLst);

		IcmgPutObject icmgPutObject = new IcmgPutObject();
		icmgPutObject.setIana_id(iana_id);
		icmgPutObject.setSn(sn);
		icmgPutObject.setSid(sid);
		icmgPutObject.setMessage(msg);
		icmgPutObject.setIcmg(configIcmg);
		if (bRetry && bAddRetryCount)
			icmgPutObject.setRetry(icmgPutO.getRetry() + 1);
		try {
			ACUtil.cachePoolObjectBySn(icmgPutObject, ConfigPutObject.class);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("cachePoolObjectBySn Exception", e);
		}

		ACService.<Json_Config_Icmg> fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_ICMG_PUT, sid, iana_id, sn, configIcmg);
	}

	public static boolean isWlcEnabled(String config)
	{
		Properties fullConfig = CommonConfigUtils.parsePropertiesString(config);

		return (isWlcEnabled(fullConfig));
	}
	
	public static boolean isWlcEnabled(Properties config)
	{
		if (config.getProperty(RadioConfigUtils.WLC_FEATURE_TAG) != null &&
				config.getProperty(RadioConfigUtils.WLC_FEATURE_TAG).equalsIgnoreCase("\"yes\""))
		{
			return true;
		}

		return false;
	}

	public static boolean isMasterDevice(Integer devicesId, Integer networkId, String organizationId) {
		boolean isMasterDev = false;
		if (devicesId != null && networkId != null && organizationId != null) {
			Devices devices = NetUtils.getDevices(organizationId, networkId, devicesId, false);
			Networks networks = OrgInfoUtils.getNetwork(organizationId, networkId);
			if (devices != null && networks != null) {
				if (devices.getId().equals(networks.getMasterDeviceId())) {
					isMasterDev = true;
				}
			}
		}
		return isMasterDev;
	}

	public static JsonConf.CONFIG_TYPE getConfigTypeFromDevId(String orgId, Integer devId) 
	{
		Devices dev = NetUtils.getDevicesWithoutNetId(orgId, devId, true);
		if (dev==null)
		{
			log.warnf("%s dev %d not found", orgId, devId);
			return null;
		}		
		Products product = ProductUtils.getProducts(dev.getProductId());

		if (product.getDeviceType()==null || product.getDeviceType().isEmpty())
		{
			log.warnf("orgId %s devId %d has unknown product type", orgId, devId);
			return JsonConf.CONFIG_TYPE.UNKNOWN;
		}
		
		if (product.getDeviceType().equalsIgnoreCase(DEVICE_TYPE.ap.toString()))
			return JsonConf.CONFIG_TYPE.AP;
		
		return JsonConf.CONFIG_TYPE.MAX;
	}
	
	public static JsonConf.CONFIG_TYPE getConfigTypeFromProducts(Products product) 
	{
		if (product == null)
		{
			log.warn("getConfigTypeFromProducts - product is null");
			return JsonConf.CONFIG_TYPE.UNKNOWN;
		}
		
		if (product.getDeviceType().equalsIgnoreCase(DEVICE_TYPE.ap.toString()))
			return JsonConf.CONFIG_TYPE.AP;
		
		return JsonConf.CONFIG_TYPE.MAX;
	}
	
	public boolean isDevWifiManagedByInControl(Devices dev, String orgId) throws Exception
	{
		String wifiStatus = getDevWifiManaged(dev, orgId);
		if (wifiStatus.equalsIgnoreCase(WIFICFG.network.name()) || wifiStatus.equalsIgnoreCase(WIFICFG.device.name()))
			return true;
		else
			return false;
	}
	
	public static HashMap<Integer, String> getDevicesWifiManaged( String orgId, Integer netId )
	{
		HashMap<Integer, String> map = null;
		
		try
		{
			ConfigurationSettingsUtils settingUtils = new ConfigurationSettingsUtils(orgId, netId, null);
			Map<Integer, ConfigurationSettings> devConfigurationsMap = settingUtils.getDevConfigSettingsOfNetwork();
			ConfigurationSettings networkConfigurations = settingUtils.getNetworkConfigSettings();
			
			if( devConfigurationsMap != null )
			{
				map = new HashMap<Integer, String>();
				for( Integer devId : devConfigurationsMap.keySet() )
				{
					if (devId != 0)
					{
						Devices dev = NetUtils.getDevices(orgId, netId, devId, false);
						String wifi_manage = getDevWifiManaged(dev, networkConfigurations, devConfigurationsMap.get(devId));
						map.put(devId, wifi_manage);
					}
				}
			}
		}
		catch(Exception e)
		{
			log.error("Get device wifi managed status map error - "+e,e);
			return null;
		}
		
		return map;
	}
		
	public static String getDevWifiManaged(Devices dev, String orgId) throws Exception
	{
		ConfigurationSettingsUtils settingUtils = new ConfigurationSettingsUtils(orgId, dev.getNetworkId(), dev.getId());
		String wifiManage = null;
		if( settingUtils != null )
		{
			try {
				log.debugf("getDevWifiManaged dev=%s, orgId=%s, netSet=%s, devSet=%s", dev, orgId, 
						settingUtils.getNetworkConfigSettings(), settingUtils.getDevConfigSettings());
				wifiManage = getDevWifiManaged(dev,settingUtils.getNetworkConfigSettings(),settingUtils.getDevConfigSettings());
				log.debugf("getDevWifiManaged dev=%s, orgId=%s, wifiManage=%s", dev, orgId, wifiManage);
			} catch (Exception e) {
				log.error("get dev wifi_managed with device error - "+e,e);
			}
		}
		
		return wifiManage;
	}
	
	public static String getDevWifiManaged(Devices dev, ConfigurationSettings networkSet, ConfigurationSettings devSet)
	{
		if (dev==null || networkSet==null || devSet==null)
		{
			log.infof("getWifiManaged null param dev %s networkSet %s devSet %s", dev, networkSet, devSet);
			return "";
		}
		
		return getDevWifiManaged(ProductWsUtils.isWifiSupport(dev.getProductId()), networkSet.isWifi_enabled(), 
				devSet.isWifi_enabled(), devSet.isFollow_network());				
	}
	
	private static String getDevWifiManaged(boolean isProductSupport, boolean isNetworkWifiManaged, 
			boolean isDeviceWifiManaged, boolean isDeviceFollowNetwork) 
	{		
		String result = null;
		
		if(isProductSupport)
		{
			if (isDeviceFollowNetwork)
			{
				if (isNetworkWifiManaged) {
					result = WIFICFG.network.name();
				} else {
					result = WIFICFG.not_managed.name();
				}
			} else {
				if (isDeviceWifiManaged) {
					result = WIFICFG.device.name();
				} else {
					result = WIFICFG.not_managed.name();
				}
			}
		}
		else
		{
			result = WIFICFG.none.name();
		}
		
		return result;
	}
}
