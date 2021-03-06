package com.littlecloud.control.json.model.config.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.DebugManager;
import com.littlecloud.ac.json.model.Json_Config_Icmg;
import com.littlecloud.ac.json.model.Json_Config_Icmg.Icmg;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.ConfigurationPepvpnsDAO;
import com.littlecloud.control.dao.DeviceUpdatesDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.branch.ConfigUpdatesDAO;
import com.littlecloud.control.entity.ConfigurationPepvpns;
import com.littlecloud.control.entity.DeviceUpdates;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.branch.ConfigUpdates;
import com.littlecloud.control.entity.branch.ConfigUpdatesId;
import com.littlecloud.control.entity.branch.Products;
import com.littlecloud.control.json.model.config.JsonConf;
import com.littlecloud.control.json.model.config.JsonConf_PepvpnProfiles;
import com.littlecloud.control.json.model.config.JsonConf_PepvpnProfilesNew;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfiles;
import com.littlecloud.control.json.model.config.JsonConf_System;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerNetworkTask.UPDATE_OPERATION;
import com.littlecloud.control.json.model.config.util.SystemConfigUtils.SystemConfigInfo;
import com.littlecloud.control.json.model.config.util.exception.FeatureNotFoundException;
import com.littlecloud.control.json.model.config.util.info.AdminInfo;
import com.littlecloud.control.json.model.config.util.info.ConfigGetInfo;
import com.littlecloud.control.json.model.config.util.info.ConfigPutTaskInfo;
import com.littlecloud.control.json.model.config.util.info.SsidVlanConfigInfo;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.ConfigGetObject;
import com.littlecloud.pool.object.ConfigPutObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.FeatureGetObject;
import com.littlecloud.pool.object.InternalCmdObject;
import com.littlecloud.pool.object.InternalCmdObject.CMD;
import com.littlecloud.pool.object.PoolObjectDAO;
import com.littlecloud.pool.object.utils.ConfigGetUtils;
import com.littlecloud.pool.object.utils.FeatureGetUtils;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;
import com.littlecloud.pool.object.utils.ProductUtils;

/* command fetch: true */
public class ConfigPutTask implements Callable<Boolean> {

	private static boolean PROD_MODE = DebugManager.isPROD_MODE();
	public static boolean clearConfigTaskOff = false;
	public static Integer configDevId = null;	
	public static boolean force_pepoff = false;
	public static boolean force_wifioff = false;
	public static boolean force_pepon = false;
	public static boolean force_wifion = false;
	public static boolean force_apply = false;
	
	private static final Logger log = Logger.getLogger(ConfigPutTask.class);
	private final boolean rdonly = false;
	private static int configPutTaskRunCount = 0;
	private Timer timer = new Timer();
	private int timerCount = 0;		
	
	private JsonConf_RadioSettings.CONFIG filter;	
	private String orgId;
	private Integer netId;
	private Integer devId;
	private String sid;
	private Integer iana_id;
	private String sn;
	private UPDATE_OPERATION op;
	
	private DeviceUpdatesDAO devUpDAO = null;
	private DevicesDAO devDAO = null;
	private ConfigurationPepvpnsDAO pepDAO = null;
	
	JsonConf.CONFIG_TYPE configType = null;
	private Devices dev = null;
	private Products product = null;
	private Networks net = null;	
	private String errMsg = null;
	private StringBuilder sbSummary = new StringBuilder();

	public synchronized void setConfigPutTaskRunCount(int runCount) {
		configPutTaskRunCount = runCount;
	}
	
	public ConfigPutTask(ConfigPutTaskInfo taskInfo) throws Exception {
		super();
		
		if (log.isDebugEnabled())
			log.debugf("ConfigPutTask is called for taskInfo %s", taskInfo);
		
		this.orgId = taskInfo.orgId;
		this.netId = taskInfo.netId;
		this.devId = taskInfo.devId;
		this.sid = taskInfo.sid;
		this.iana_id = taskInfo.iana_id;
		this.sn = taskInfo.sn;
		this.op = taskInfo.op;
		
		devUpDAO = new DeviceUpdatesDAO(orgId, rdonly);
		devDAO = new DevicesDAO(orgId, rdonly);
		pepDAO = new ConfigurationPepvpnsDAO(orgId, rdonly);
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				timerCount++;
				log.warnf("memTrace - ConfigPutTask executed for more than %d mins", timerCount);
			}
		}, 1 * 60 * 1000);
	}
	
	@Override
	public Boolean call() {
		if (log.isDebugEnabled()) log.debugf("memTrace - ConfigPutTask is called for (String orgId, Integer netId, Integer devId, String sid, Integer iana_id, String sn) = "
				+ "(%s, %d, %d, %s, %d, %s)", orgId, netId, devId, sid, iana_id, sn);		
		if (log.isInfoEnabled()) log.infof("MemTrace - PutTaskRunCount = %d", configPutTaskRunCount);
		setConfigPutTaskRunCount(configPutTaskRunCount + 1);
		
		sbSummary.append("\n* date utc: "+DateUtils.getCurrentDateTimeInUtcGeneralFormat());
		sbSummary.append("\n* org:" + orgId + " netId:" + netId + " devId:" + devId);
		sbSummary.append("\n* iana_id:" + iana_id + " sn:" + sn);
		sbSummary.append("\n* threadId: "+Thread.currentThread().getName());
		sbSummary.append("\n* sid: " + sid);

		Boolean result = false;
		try {
			switch (op)
			{
			case APPLY_GROUP_CONFIG:
				if (log.isDebugEnabled()) log.debug("APPLY_GROUP_CONFIG");

				if (!isValidInfo())
					return false;

				result = applyGroupConfig();
				break;
			case APPLY_GROUP_MASTER_DEVICE_CONFIG:
				if (log.isDebugEnabled()) log.debug("APPLY_GROUP_MASTER_DEVICE_CONFIG");
				
				if (!isValidInfo())
					return false;

				result = applyGroupMasterDeviceConfig();
				break;
			case CLEAR_CONFIG:
				log.debug("CLEAR_CONFIG");
				if (!clearConfigTaskOff)
					result = clearConfigTask();
				else
					log.debugf("clearConfigTaskOff!");
				break;
			default:
				log.errorf("unknown operation for org %s dev %d %s", orgId, iana_id, sn);
				break;
			}
		} catch (Exception e)
		{
			log.error("applyGroupConfigTask exception", e);
		} finally {
			setConfigPutTaskRunCount(configPutTaskRunCount - 1);
			timer.cancel();
			
			log.infof("\n************* Apply Config Summary %d %s ***************** %s \n******************************************************", iana_id, sn, sbSummary.toString());
		}
		return result;
	}

	private Boolean applyGroupConfig() throws Exception {
		boolean bApplyNetworkCfg = false;		
		boolean bNetworkPepvpnManaged = false;
		boolean isWlcEnabled = false;
		
		
		boolean isActiveHub = false;
		boolean isActiveHaHub = false;
		boolean isDevConfigTextSupport = false;
		
		ConfigGetObject oldConfigObject = null;		
		ConfigurationPepvpns confPep = null;
		FeatureGetObject fgo = null;		
		
		List<Integer> supportedNetIdLst = null;		
		List<String> CONFIG_CLEAR_KEY_PATTERN_LIST = new ArrayList<String>();
		
		Properties systemHwProperties = null;
		Properties adminHwProperties = null;
		Properties pepHwProperties = null;
		Properties networkHwProperties = null;
		ConcurrentHashMap<Integer, String> networkHwConfigMap = null;
		ConfigurationSettingsUtils settingsUtils = null;
		
		log.infof("Apply InControl Config");
		sbSummary.append("\n* action: APPLY_GROUP_CONFIG");
		
		SystemConfigInfo sysInfo = new SystemConfigInfo();
		ConfigGetInfo cfgInfo = new ConfigGetInfo();
		AdminInfo adminInfo = new AdminInfo();
		
		/* load configuration settings */
		settingsUtils = new ConfigurationSettingsUtils(orgId, netId, dev.getId());
		if (settingsUtils.getDevConfigSettings()==null)
		{
			log.warnf("dev settings is not found for dev %s, create default now", dev);
			settingsUtils.initDevConfigSettings();
		}
		
		try {					
			cfgInfo.setIana_id(iana_id);
			cfgInfo.setSn(sn);
			cfgInfo.setSid(sid);
			cfgInfo.setRetry(3);
			cfgInfo.setSbSummary(sbSummary);
			ConfigGetUtils.removeObject(cfgInfo);	/* remove outdated object */
			oldConfigObject = ConfigGetUtils.getObject(cfgInfo);
			isDevConfigTextSupport = cfgInfo.getOut_isDevConfigTextSupport();
			
			fgo = cfgInfo.getOut_FeatureGetObject();
			
		} catch (Exception e) {
			errMsg = String.format("dev %s remove and get config object exception", sn);
			log.error(errMsg, e);
			sbSummary.append("\n* err - "+errMsg);
			if (PROD_MODE)
				devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
			return false;
		} 
		
		/**** admin config ****/
		try {
			adminInfo.setOrgId(orgId);
			adminInfo.setNetId(netId);
			adminInfo.setDevId(devId);
			adminInfo.setIana_id(dev.getIanaId());
			adminInfo.setSn(dev.getSn());
			adminInfo.setDev(dev);
			adminInfo.setConfigType(configType);
			adminInfo.setFgo(fgo);
			adminInfo.setSettingsUtils(settingsUtils);		
			adminInfo.setSbSummary(sbSummary);
			//AdminConfigApplication adminApp = new AdminConfigApplication(adminInfo);
			AdminConfigBase adminApp = AdminConfigApplicationFactory.getAdminConfigApplication(adminInfo);
			adminHwProperties = adminApp.getHardwareProperties();
		} catch (FeatureNotFoundException e)
		{
			errMsg = String.format("AdminConfigApplication - %s for dev %d %s", e.getMessage(), iana_id, sn);
			log.error(errMsg);
			sbSummary.append("\n* err - "+errMsg);
			devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
			return false;
		}
		
		/**** system config ****/
		sbSummary.append("\n>>> System config >>>");
		sysInfo.setDevice_name(dev.getName());
		sysInfo.setSn(dev.getSn());
		if (StringUtils.isNotEmpty(net.getTimezone()))
			sysInfo.setTzId(Integer.valueOf(net.getTimezone()));
		sysInfo.setFollowNetworkTimezone(net.isApplyTimezone());
		
		JsonConf_System sysJson = SystemConfigUtils.getDatabaseSystemFullConfig(sysInfo);
		systemHwProperties = CommonConfigUtils.parsePropertiesString(sysJson.generateHardwareConfig(JsonConf_System.class, configType).get(JsonConf_RadioSettings.GLOBAL_DEV_INDEX));
		sbSummary.append(sysInfo.getSb().toString());
		
		/**** radio config ****/
		sbSummary.append("\n>>> Wifi config >>>");
		JsonConf_RadioSettings radioJson = null;
		try {
			radioJson = RadioConfigUtils.getDatabaseRadioFullConfig(orgId, netId, dev.getId(), true, sbSummary);
			if(radioJson == null)
			{
				errMsg = String.format("no configuration is supplied for dev %d %s", iana_id, sn);
				log.error(errMsg);
				sbSummary.append("\n* err - "+errMsg);
				devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
				return false;
			}
		} catch (FeatureNotFoundException e)
		{
			errMsg = String.format("RadioConfigUtils device feature is not found for dev %d %s", iana_id, sn);
			log.error(errMsg);
			sbSummary.append("\n* err - "+errMsg);
			devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
			return false;
		}
		
		try {
			if (oldConfigObject != null)
			{
				if (oldConfigObject.getStatus() != 200)
				{
					errMsg = String.format("dev %s config get with status %d", sn, oldConfigObject.getStatus());
					log.warn(errMsg);
					if (PROD_MODE)
						devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
					
					ConfigGetUtils.removeObject(cfgInfo);			
					return false;
				}

				if (oldConfigObject.getConfig() != null) {						
					Properties fullConfig = null;
					Properties oldConfig = null;
					StringBuilder wlanOrder = null;
					String wlanOrderStr = null;
					String remainWlanOrderStr = "";
					String remainMvpnOrderStr = "";

					Enumeration<String> enumeration = null;
				
					try {
						/**** CREATE FINAL CONFIG FROM OLD CONFIG ****/
						fullConfig = CommonConfigUtils.parsePropertiesString(oldConfigObject.getConfig());
						
						/**** DETERMINE SSID APPLICATION IF VLAN EXISTS ****/
						SsidVlanConfigInfo vlanInfo = new SsidVlanConfigInfo();
						vlanInfo.setOrgId(orgId);
						vlanInfo.setNetId(netId);							
						vlanInfo.setDevId(devId);
						vlanInfo.setConfigType(configType);
						vlanInfo.setFullconfig(fullConfig);
						vlanInfo.setSsidProfileJsonLst(radioJson.getSsidProfilesLst());
						vlanInfo.setSsidProfileReservedJsonLst(radioJson.getSsidProfilesReservedLst());
						vlanInfo.setSbSummary(sbSummary);
						if (!VlanConfigUtils.configSsidVlan(vlanInfo))
						{
							errMsg = String.format("Fail to configSsidVlan %s", vlanInfo);
							log.error(errMsg);
							if (PROD_MODE)
								devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
							return false;
						}
						
						bApplyNetworkCfg = radioJson.getWifi_managed_by_ic2()==null?false:radioJson.getWifi_managed_by_ic2();
						
						networkHwConfigMap = radioJson.generateHardwareConfig(JsonConf_RadioSettings.class, filter, JsonConf_RadioSettings.GLOBAL_DEV_INDEX, configType);
						networkHwProperties = CommonConfigUtils.parsePropertiesString(networkHwConfigMap.get(devId));	
						networkHwProperties = ProductConfigConversionUtils.patchRadioHardwareConfigType(networkHwProperties, configType);
						
						if (!PROD_MODE)
							log.debugf("networkHwConfig = %s", CommonConfigUtils.getOrderedConfig(networkHwProperties));
						
						log.debugf("---VlanConfigUtils.configSsidVlan ssidProfileLst = %s", radioJson.getSsidProfilesLst());
						
						isWlcEnabled = RadioConfigUtils.isWlcEnabled(oldConfigObject.getConfig()); 
						if (isWlcEnabled)
						{
							sbSummary.append("\n* - wlc enabled: , wifi config not applied");
							bApplyNetworkCfg = false;
							
							/* update database - update device.webadmin_cfg field */
							RadioConfigUtils.WebadminCfg wc = new RadioConfigUtils.WebadminCfg(dev.getWebadmin_cfg());
							wc.addCfg(RadioConfigUtils.WEBAMIN_CFG.WLC);
							dev.setWebadmin_cfg(wc.getValue());
							devDAO.update(dev);
						}
						
						oldConfig = new Properties();
						oldConfig.putAll(fullConfig);								

						if (bApplyNetworkCfg)
						{
							/**** CHECK WIFI SUPPORT ****/
							Integer maxWifiSsid = FeatureGetUtils.getMaxSsidSupport(fgo);
							log.infof("dev %s device id %d %s with wifi ssid %d", sn, dev.getId(), dev.getSn(), maxWifiSsid);

							if (fgo != null && fgo.getFeatureRadioConfigLst() != null)
							{
								sbSummary.append("\n* - wifi is supported for " + maxWifiSsid + " ssid");
							}

							/**** CREATE WLAN_ORDER ****/
							wlanOrder = new StringBuilder();
							if (radioJson.getSsidProfilesLst()!=null && radioJson.getSsidProfilesLst().size()>0)
							{
								for (JsonConf_SsidProfiles ssidProfile : radioJson.getSsidProfilesLst())
								{
									wlanOrder.append(ssidProfile.getSsid_id());
									wlanOrder.append(" ");
								}
								
								if (wlanOrder.length() > 0)
								{
									wlanOrder.setLength(wlanOrder.length() - 1);
									wlanOrderStr = "\"" + wlanOrder.toString() + "\"";
								}
							}
							log.debugf("dev %s final wlanOrderStr = %s", sn, wlanOrderStr);
						}
					} catch (SQLException e) {
						log.error("Wifi - SQLException ", e);
						return false;
					} catch (Exception e) {
						log.error("Wifi - Other Exception ", e);
						return false;
					}

					/**** GENERATE PEPVPN CONFIG IF NECESSARY ****/
					try {
						sbSummary.append("\n>>> Pepvpn config >>>");															
						confPep = pepDAO.findById(netId);
						if (confPep != null)
						{
							if (confPep.isEnabled())
							{
								bNetworkPepvpnManaged = (force_pepoff ? false : true);
								log.debug("Managed Pepvpn Configuration Network");
							}
						}
						else
						{
							log.debug("UnManaged Pepvpn Configuration Network");
						}
						
						/* force settings */
						bNetworkPepvpnManaged = (force_pepon ? true : bNetworkPepvpnManaged);
						sbSummary.append("\n* - pepvpn network management: " + bNetworkPepvpnManaged);
						
						JsonConf_PepvpnProfilesNew pepEndptConfNew = null;
						isActiveHub = pepDAO.isEnabledMasterHubInAnyNetwork(devId);
						isActiveHaHub = pepDAO.isEnabledHaHubInAnyNetwork(devId);
						log.debugf("bNetworkPepvpnManaged %s isActiveHub %s isActiveHaHub %s", bNetworkPepvpnManaged, isActiveHub, isActiveHaHub);
						if (bNetworkPepvpnManaged || isActiveHub || isActiveHaHub)
						{	
							//Boolean isSpeedFusionSupport = FeatureGetUtils.isSpeedFusionSupport(fgo);
							Boolean isSpeedFusionSupport = FeatureGetUtils.isSpeedFusionBondingSupport(fgo);
							log.debugf("...isSpeedFusionBondingSupport=%s, \nfgo=%s", FeatureGetUtils.isSpeedFusionBondingSupport(fgo), JsonUtils.toJsonPretty(fgo)); 
	
							supportedNetIdLst = new ArrayList<Integer>();
							if (isActiveHub)
								supportedNetIdLst = pepDAO.getMasterHubSupportedNetworkIds(devId);
	
							if (isActiveHaHub)
								supportedNetIdLst.addAll(pepDAO.getHaHubNetworkIds(devId));
	
							log.debugf("dev %s pepDAO.isHub(%d)=%s, pepDAO.isHaHub(%d)=%s", sn, devId, isActiveHub, devId, isActiveHaHub);
	
							if (isActiveHub || isActiveHaHub)
							{
								log.debugf("Pepvpn Managed Device is also configured as hub");
								if (supportedNetIdLst.size() != 0) {
									// hub config is required
									List<JsonConf_PepvpnProfilesNew> endptProfLst = new ArrayList<JsonConf_PepvpnProfilesNew>();
									JsonConf_PepvpnProfiles pepvpnProfile = null;
									//int supportedLicense = feature.getMvpnLicense();
									int supportedLicense = FeatureGetUtils.getMaxMvpnSupport(fgo);
									if (supportedLicense == 0)
										supportedLicense = RadioConfigUtils.MVPN_INFINITE_LICENSE_NUM; // infinite number of
																									// license
									int maxLicenseUsed = 0;
									for (Integer nid : supportedNetIdLst) {
										List<JsonConf_PepvpnProfilesNew> profLst = PepvpnConfigUtils.collectEndptConfigLst(orgId, nid, supportedLicense - maxLicenseUsed);
										if (profLst != null)
										{
											endptProfLst.addAll(profLst);
											maxLicenseUsed += profLst.size();
										}
	
										if (pepvpnProfile == null)
											pepvpnProfile = PepvpnConfigUtils.getDatabasePepvpnFullConfig(orgId, nid);
									}
									
									sbSummary.append("\n* - Hub: " + isActiveHub + " HA Hub: " + isActiveHaHub + " (apply hub configuration with license " + supportedLicense + ")");
									pepEndptConfNew = JsonConf_PepvpnProfilesNew.parseJsonConf_PepvpnProfilesAsHub(pepvpnProfile, endptProfLst, 
											FeatureGetUtils.getNumberOfWanSupport(fgo), isSpeedFusionSupport, FeatureGetUtils.isSpeedFusionBondingSupport(fgo));
								}
							} else {
								// endpoint config is required
								if (product.getEndpointSupport())
								{
									JsonConf_PepvpnProfiles pepvpnProfile = PepvpnConfigUtils.getDatabasePepvpnFullConfig(orgId, netId);
									pepEndptConfNew = JsonConf_PepvpnProfilesNew.parseJsonConf_PepvpnProfilesAsEndpoint(pepvpnProfile, dev.getId(), dev.getSn(), dev.getName(), 
											FeatureGetUtils.getNumberOfWanSupport(fgo), isSpeedFusionSupport, FeatureGetUtils.isSpeedFusionBondingSupport(fgo));
									log.debugf("dev %d %s pepconf = \n%s", dev.getId(), dev.getSn(), pepEndptConfNew);
									sbSummary.append("\n* - Hub: " + isActiveHub + " HA Hub: " + isActiveHaHub + " (apply endpoint configuration)");
									log.debug("\n* - Hub: " + isActiveHub + " HA Hub: " + isActiveHaHub + " (apply endpoint configuration)");
								}
								else
								{
									sbSummary.append("\n* Product "+product.getName()+" isn't allowed to be endpoint.");
								}
							}
	
							log.debug("Generate Pepvpn Configuration");
							if (pepEndptConfNew != null) {
								pepHwProperties = CommonConfigUtils.parsePropertiesString(pepEndptConfNew.generateHardwareConfig(JsonConf_PepvpnProfilesNew.class, configType).get(JsonConf_RadioSettings.GLOBAL_DEV_INDEX));
								log.debugf("pepConfig=" + pepHwProperties);
							}
						}
						else
						{
							log.debugf("dev %s no pepvpn config is required.", sn);
						}
					} catch (SQLException e) {
						log.error("Pepvpn - SQLException ", e);
						return false;
					} catch (Exception e) {
						log.error("Pepvpn - Other Exception ", e);
						return false;
					}
		
					/**** CLEAR OLD CONFIG BEFORE MERGE ****/
					// if (bNetworkWifiManaged)
					if (bApplyNetworkCfg)
					{
						/* clear all */
						sbSummary.append("\n* - clear all wifi config");
//						if (bWifiSupport)
//							CONFIG_CLEAR_KEY_PATTERN_LIST.add(ConfigUtils.PROHIBIT_WIFI_TAG_FILTER);
						CONFIG_CLEAR_KEY_PATTERN_LIST.add(RadioConfigUtils.WLAN_FILTER_ALL);
						CONFIG_CLEAR_KEY_PATTERN_LIST.add(RadioConfigUtils.WLAN_FILTER_ALL2);
					}
					else if (!isWlcEnabled)
					{
						/* clear ic2 */
						sbSummary.append("\n* - clear icmg wifi managed config");
						String[] wlanLst = CommonConfigUtils.orderToLst(fullConfig.getProperty(RadioConfigUtils.WLAN_ORDER_TAG));
						String icmgFilter = null;
						if (wlanLst != null)
						{
							if (wlanLst != null && wlanLst.length != 0)
							{
								log.debugf("non-empty wlanLst=%s (size=%d)", wlanLst, wlanLst.length);
								for (int i = 0; i < wlanLst.length; i++)
								{
									String icmgKey = CommonConfigUtils.createICMGKey(Integer.valueOf(wlanLst[i]), CommonConfigUtils.CONFIG_TAG_TYPE.TYPE_WLAN);
									if (fullConfig.containsKey(icmgKey))
									{
										log.debugf("remove icmgKey=%s", icmgKey);
										icmgFilter = CommonConfigUtils.createICMGKeyFilter(Integer.valueOf(wlanLst[i]), CommonConfigUtils.CONFIG_TAG_TYPE.TYPE_WLAN);
										CONFIG_CLEAR_KEY_PATTERN_LIST.add(icmgFilter);
									}
									else
									{
										remainWlanOrderStr += Integer.valueOf(wlanLst[i]) + " ";
									}
								}

								if (remainWlanOrderStr != null && remainWlanOrderStr.length()!=0)
									remainWlanOrderStr = "\"" + remainWlanOrderStr.trim() + "\"";											
							}
						}
						
						CONFIG_CLEAR_KEY_PATTERN_LIST.add(RadioConfigUtils.WLAN_ORDER_TAG);
						
						log.debugf("remainWlanOrderStr=%s", remainWlanOrderStr);
					}

					if (bNetworkPepvpnManaged || isActiveHub || isActiveHaHub)
					{
						/* clear all */
						sbSummary.append("\n* - clear all pepvpn managed config");
						log.debugf("Prepare to apply pepvpn config");
						CONFIG_CLEAR_KEY_PATTERN_LIST.add(RadioConfigUtils.MVPN_FILTER_ALL);
						CONFIG_CLEAR_KEY_PATTERN_LIST.add(RadioConfigUtils.MVPN_FILTER_ALL2);
					}
					else
					{
						/* clear ic2 */
						sbSummary.append("\n* - clear icmg pepvpn managed config");
						String[] mvpnLst = CommonConfigUtils.orderToLst(fullConfig.getProperty(RadioConfigUtils.MVPN_ORDER_TAG));
						String icmgFilter = null;
						if (mvpnLst != null)
						{
							for (int i = 0; i < mvpnLst.length; i++)
							{
								String icmgKey = CommonConfigUtils.createICMGKey(Integer.valueOf(mvpnLst[i]), CommonConfigUtils.CONFIG_TAG_TYPE.TYPE_MVPN);
								if (fullConfig.containsKey(icmgKey))
								{
									log.debugf("remove icmgKey=%s", icmgKey);
									icmgFilter = CommonConfigUtils.createICMGKeyFilter(Integer.valueOf(mvpnLst[i]), CommonConfigUtils.CONFIG_TAG_TYPE.TYPE_MVPN);
									CONFIG_CLEAR_KEY_PATTERN_LIST.add(icmgFilter);
								}
								else
								{
									remainMvpnOrderStr += Integer.valueOf(mvpnLst[i]) + " ";
								}
							}									
							
							if (remainMvpnOrderStr != null && remainMvpnOrderStr.length() != 0)
								remainMvpnOrderStr = "\"" + remainMvpnOrderStr.trim() + "\"";	
						}
						
						CONFIG_CLEAR_KEY_PATTERN_LIST.add(RadioConfigUtils.MVPN_ORDER_TAG);
						
						log.debugf("remainMvpnOrderStr=%s", remainMvpnOrderStr);
					}

					enumeration = (Enumeration<String>) fullConfig.propertyNames();
					while (enumeration.hasMoreElements()) {
						boolean exempt = false;
						String key = (String) enumeration.nextElement();
						// for (String fhkey: )
													
						for (String filter : RadioConfigUtils.CONFIG_FH_SPEC_KEY_PATTERN_LIST) {
							if (key.matches(filter)) 
								exempt = true;
						}
						
						for (String filter : CONFIG_CLEAR_KEY_PATTERN_LIST) {									
							if (!exempt && key.matches(filter)) {
								fullConfig.remove(key);
							}
						}
					}

					/**** MERGE CONFIG WIFI AND PEPVPN CONFIG ****/
					if (adminHwProperties!=null)
						fullConfig.putAll(adminHwProperties);					
					fullConfig.putAll(systemHwProperties);					
					
					if (bApplyNetworkCfg)
					{								
						fullConfig.putAll(networkHwProperties);
						if (wlanOrderStr !=null && wlanOrderStr.length() !=0)
							fullConfig.put(RadioConfigUtils.WLAN_ORDER_TAG, wlanOrderStr);
					}
					else
					{					
						if (remainWlanOrderStr != null && remainWlanOrderStr.length() != 0)
							fullConfig.put(RadioConfigUtils.WLAN_ORDER_TAG, remainWlanOrderStr);
					}

					if (bNetworkPepvpnManaged || isActiveHub || isActiveHaHub)
					{
						if (pepHwProperties != null)
							fullConfig.putAll(pepHwProperties);
					}
					else
					{
						if (remainMvpnOrderStr !=null && remainMvpnOrderStr.length() !=0)
							fullConfig.put(RadioConfigUtils.MVPN_ORDER_TAG, remainMvpnOrderStr);
					}

					/**** REMOVE CACHE OBJECT AFTER USE ****/
					ACUtil.<ConfigGetObject> removePoolObjectBySn(oldConfigObject, ConfigGetObject.class);

					/**** PUT CONFIG ****/
					sbSummary.append("\n>>> Put config >>>");
					if (log.isDebugEnabled()) log.debugf("Fetching PIPE_INFO_TYPE_CONFIG_PUT for dev (%s)", sn);
					ConfigPutObject putObject = new ConfigPutObject();
					String orderedConfig = CommonConfigUtils.getOrderedConfig(fullConfig);

					try
					{
						if (oldConfigObject.getSid() != null && !oldConfigObject.getSid().isEmpty())
							putObject.setSid(oldConfigObject.getSid());
						sbSummary.append("\n* sid applied: " + sid);
					} catch (NullPointerException e)
					{
						log.warn("Nullpointerexception here", e);
					}
					putObject.setConfig(orderedConfig);
					putObject.setFilepath(oldConfigObject.getFilepath());

					/**** COMPARE CONFIG PART ****/
					
					String md5Old = CommonConfigUtils.getIcmgAndConfigTypeChecksum(oldConfig, CommonConfigUtils.CONFIG_TAG_TYPE.TYPE_ALL_MANAGED);
					String md5New = CommonConfigUtils.getIcmgAndConfigTypeChecksum(fullConfig, CommonConfigUtils.CONFIG_TAG_TYPE.TYPE_ALL_MANAGED);
					sbSummary.append("\n* - prevMd5 = "+md5Old);
					sbSummary.append("\n* - newMd5 = "+md5New);
					
					/**** CREATE ICMG ****/
					sbSummary.append("\n>>> Icmg config >>>");
					int icmg = 0;
					boolean bICMG_MVPN = false;
					boolean bICMG_WLAN = false;
					
					bICMG_MVPN = bNetworkPepvpnManaged || isActiveHub || isActiveHaHub;
					// bICMG_WLAN = bNetworkWifiManaged;
					bICMG_WLAN = radioJson.isWebadminShowIcmg();
					icmg = CommonConfigUtils.createDeviceIcmg(bICMG_WLAN, bICMG_MVPN);
					
					/* for re-apply */
					dev.setIcmg(icmg);
					dev.setConfigChecksum(md5New);
					devDAO.update(dev);
					
					/*** APPLY CONFIG ***/
					if (md5Old.compareToIgnoreCase(md5New) == 0)
					{
						if (log.isDebugEnabled()) log.debugf("\n oldConfig=%s, \n newConfig=%s", oldConfig, fullConfig);						
						String logMsg = "\n* - checksum matched. no need to reconfigure (old "+md5Old+" new "+md5New+").";
						DeviceUpdates du = devUpDAO.findById(devId);
						if (du!=null)
							logMsg += "prev..."+du.getError();
						sbSummary.append(logMsg);
						if (PROD_MODE)
							devUpDAO.decrementConfUpdateForDev(devId, null, logMsg, true);
					}
					else
					{
						if (log.isDebugEnabled()) log.debugf("\n(%s) ~CONFIG_PUT~ md5 = %s apply config = %s", sn, md5New, putObject.getConfig());

						try {
							if (PROD_MODE) {							
								sbSummary.append("\n* - ICMG_MVPN=" + (bICMG_MVPN ? "1" : "0"));
								sbSummary.append("\n* - ICMG_WLAN=" + (bICMG_WLAN ? "1" : "0"));
								sbSummary.append("\n* - CONFIG APPLIED");
								devUpDAO.decrementConfUpdateForDevWithConfig(devId, sbSummary.toString(), null, putObject.getConfig(), false);
								if (isDevConfigTextSupport)
								{
									List<Icmg> icmgLst = new ArrayList<Icmg>();
									List<String> config_type = new ArrayList<String>();
									List<String> log_msg = new ArrayList<String>();
									
									/* set feature */
									Json_Config_Icmg jsonIcmg = new Json_Config_Icmg();
									icmgLst.add(jsonIcmg.new Icmg(RadioConfigUtils.ICMG_MVPN, String.valueOf(bICMG_MVPN? "1" : "0")));
									icmgLst.add(jsonIcmg.new Icmg(RadioConfigUtils.ICMG_WLAN, String.valueOf(bICMG_WLAN? "1" : "0")));										
									putObject.setIcmgLst(icmgLst);
									
									/* set icmg and event log msg */
									if (bICMG_MVPN){
										config_type.add(RadioConfigUtils.ICMG_MVPN);
										log_msg.add(ConfigPutObject.MVPN_LOG_MSG);
									}
									if (bICMG_WLAN)
									{
										config_type.add(RadioConfigUtils.ICMG_WLAN);
										log_msg.add(ConfigPutObject.WLAN_LOG_MSG);
									}
									putObject.setConfig_type(config_type);
									putObject.setLog_msg(log_msg);		
									
									ACService.<ConfigPutObject> fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_PUT_TEXT, sid, iana_id, sn, putObject);
									sbSummary.append("\n* - applying config text... " + sid);
								}
								else
								{
									ACService.<ConfigPutObject> fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_PUT, sid, iana_id, sn, putObject);
									sbSummary.append("\n* - applying config... " + sid);

									RadioConfigUtils.sendWebadminIcmg(sid, iana_id, sn, bICMG_MVPN, bICMG_WLAN, "APPLY_CONFIG apply config_put for "+iana_id+", "+sn, false, null);
									sbSummary.append("\n* - applying icmg... " + sid);
								}
							}
							else
							{
								sbSummary.append("\n* - ICMG_MVPN=" + (bICMG_MVPN ? "1" : "0"));
								sbSummary.append("\n* - ICMG_WLAN=" + (bICMG_WLAN ? "1" : "0"));
								sbSummary.append("\n* - CONFIG APPLIED (sim)");
							}
						} catch (Exception e) {
							log.error("Exception - " + e, e);

							if (PROD_MODE) {
								if (log.isDebugEnabled()) log.debugf("delete config temp file " + oldConfigObject.getFilepath());
								InternalCmdObject cmdO = new InternalCmdObject();
								cmdO.setCommand(CMD.DEL_CFG_TMP_FILE);
								cmdO.setParam(oldConfigObject.getFilepath());
								ACService.<InternalCmdObject> fetchCommand(MessageType.PIPE_INFO_TYPE_INTERNAL_CMD, sid, iana_id, sn, cmdO);
							}

							return false;
						}
					}
				} else {
					errMsg = String.format("APPLY_GROUP_CONFIG - unable to get update config from online device %d %s (status=%d)", iana_id, sn, oldConfigObject.getStatus());
					log.warn(errMsg);
					sbSummary.append("\n* err - "+errMsg);
					if (PROD_MODE)
						devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);							
				}				
			} else {
				errMsg = String.format("APPLY_GROUP_CONFIG - unable to get update config from online device %d %s (Timeout)", iana_id, sn);
				log.warn(errMsg);
				sbSummary.append("\n* err - "+errMsg);
				if (PROD_MODE)
					devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);						
			}
		} catch (Exception e) {
			errMsg = String.format("APPLY_GROUP_CONFIG - dev %d %s with exception %s", iana_id, sn, e);
			log.error(errMsg, e);
			sbSummary.append("\n* err - "+errMsg);
			if (PROD_MODE)
				devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);					
		}
		
		return true;
	
	}

	private boolean applyGroupMasterDeviceConfig() throws Exception {
		log.infof("Apply Group Master Device Config");
		sbSummary.append("\n* action: APPLY_GROUP_MASTER_DEVICE_CONFIG");
		try {					
			sbSummary.append("\n* Apply Group Master Device Config\n");
			
			/* only apply if mv between master device and applied device are matched */
			sbSummary.append("\n* network "+net.getId()+" master device id = "+net.getMasterDeviceId());
			Devices devMaster = NetUtils.getDevices(orgId, netId, net.getMasterDeviceId(), false);
			if (devMaster==null)
			{
				errMsg = String.format("Cannot find master dev of orgId %s netid %d with id %d", orgId, netId, net.getMasterDeviceId());
				log.error(errMsg);
				sbSummary.append("\n* err - "+errMsg);
				if (PROD_MODE)
					devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
				return false;
			}
			if (devMaster.getId().intValue()==dev.getId().intValue())
			{
				errMsg = String.format("************* Apply Group Master Config Summary %s ***************** \n"
						+ "%s \n******************************************************", sn, "Config is not applied to master device");
				log.info(errMsg);
				sbSummary.append("\n* info - "+errMsg);
				if (PROD_MODE)
					devUpDAO.decrementConfUpdateForDev(devId, errMsg, null, true);
				
				/* clear icmg settings */
				sbSummary.append("\n* - ICMG_MVPN=0");
				sbSummary.append("\n* - ICMG_WLAN=0");
				RadioConfigUtils.sendWebadminIcmg(sid, iana_id, sn, false, false, "APPLY_CONFIG(master mode) clear icmg for master device "+iana_id+", "+sn, false, null);	
				return false;
			}
			if (devMaster.getProductId()!=dev.getProductId())
			{
				errMsg = String.format("************* Apply Group Master Config Summary %s ***************** \n"
						+ "Master device %d product id %d does not match applied dev %d product id %d \n"
						+ "******************************************************", sn, devMaster.getId(), devMaster.getProductId(), dev.getId(), dev.getProductId());
				log.info(errMsg);
				sbSummary.append("\n* err - "+errMsg);
				if (PROD_MODE)
					devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
				return false;
			}
			if (devMaster.getLast_sync_date()==null)
//							|| ( devMaster.getLast_sync_date()!=null && dev.getLast_sync_date()==null )
//							|| ( devMaster.getLast_sync_date()!=null && dev.getLast_sync_date()!=null && devMaster.getLast_sync_date()<dev.getLast_sync_date()))
			{							
				errMsg = String.format("************* Apply Group Master Config Summary %s ***************** \nLatest master device config is not retrieved " +
						"for dev %d %s %d (compared to dev %d %s %d)" +
						"\n******************************************************", sn, 
						devMaster.getId(), devMaster.getSn(), devMaster.getLast_sync_date()==null?0:devMaster.getLast_sync_date(), 
						dev.getId(), dev.getSn(), dev.getLast_sync_date()==null?0:dev.getLast_sync_date());
				log.info(errMsg);
				sbSummary.append("\n* err - "+errMsg);
				if (PROD_MODE)
					devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);							
				return false;
			}
			
			ConfigPutObject putObject = new ConfigPutObject();
			putObject.setSid(sid);
			sbSummary.append("\n* sid applied: " + sid);
			
			putObject.setFilepath(RadioConfigUtils.GROUP_MASTER_CFG_PATH+"/"+sn);
			
			/* save dev config to file (dave) */
			if (PROD_MODE) {								
				if (ConfigBackupUtils.getDeviceConfigAndSave2GivenPath(orgId, devMaster.getId(), putObject.getFilepath()))
				{		
					sbSummary.append("\n* dev "+devId+" "+sn+" save master_device_id "+devMaster.getId()+" config file path: " + putObject.getFilepath());
					/* do config update */								
					devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), null, false);
					
					ACService.<ConfigPutObject> fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_PUT_MASTER, sid, iana_id, sn, putObject);
					
					/* no need to do icmg things */
				}
				else
				{
					errMsg = "dev "+devId+" "+sn+" fail to save config file to path " + putObject.getFilepath();
					sbSummary.append("\n* "+errMsg);
													
					devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
				}								
			}
			sbSummary.append("\n* Apply Group Master Device " + devMaster.getId() +" " + devMaster.getSn() + " Config");
			sbSummary.append("\n* - ICMG_MVPN=0");
			sbSummary.append("\n* - ICMG_WLAN=0");
			sbSummary.append("\n* - CONFIG APPLIED");
			RadioConfigUtils.sendWebadminIcmg(sid, iana_id, sn, false, false, "APPLY_CONFIG(master mode) apply slave config icmg for "+iana_id+", "+sn, false, null);
		} catch (Exception e) {
			errMsg = String.format("dev %d %s APPLY_GROUP_MASTER_DEVICE_CONFIG exception %s", iana_id, sn, e);
			log.error(errMsg, e);
			sbSummary.append("\n* err - "+errMsg);
			if (PROD_MODE)
				devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
			
			return false;
		}
		return true;
	}

	private boolean isValidInfo() throws Exception {
		if (sn == null) {
			log.warnf("sn is null");
			return false;
		}
		
		dev = NetUtils.getDevices(orgId, netId, devId, false);
		if (dev == null)
		{
			errMsg = String.format("devices %s not found", sn);
			log.warn(errMsg);
			sbSummary.append("\n* err - "+errMsg);
			if (PROD_MODE)
				devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
			return false;
		}
		
		net = OrgInfoUtils.getNetwork(orgId, netId);
		log.debugf("net=%s", net);
		if (net==null)
		{
			errMsg = String.format("net %d is not found in cache object (dev %s %d)!", netId, orgId, devId);
			log.errorf(errMsg);
			sbSummary.append("\n* err - "+errMsg);
			if (PROD_MODE)
				devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
			return false;
		}

		product = ProductUtils.getProducts(dev.getProductId());
		if (product == null)
		{
			errMsg = String.format("devices %s product %d not found", sn, dev.getProductId());
			log.warn(errMsg);
			sbSummary.append("\n* err - "+errMsg);
			if (PROD_MODE)
				devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
			return false;
		}														
		sbSummary.append("\n* product: " + product.getName() + " (" + product.getId() + ")");
		
		
		configType = RadioConfigUtils.getConfigTypeFromProducts(product);
		if (configType == JsonConf.CONFIG_TYPE.UNKNOWN)
		{
			errMsg = String.format("dev %d %s find unknown configType %s", dev.getIanaId(), dev.getSn(), configType);
			log.error(errMsg);
			sbSummary.append("\n* err - "+errMsg);
			if (PROD_MODE)
				devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
			return false;
		}
		sbSummary.append("\n* configType: " + configType);
		
		
		DevOnlineObject devOnline = PoolObjectDAO.getDevOnlineObject(iana_id, sn);
		if (PROD_MODE) {
			if (devOnline == null || !devOnline.isOnline()) // CTD
			{
				errMsg = String.format("dev %d %s is offline", iana_id, sn);
				log.debug(errMsg);
				sbSummary.append("\n* err - "+errMsg);
				devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
				return false;
			}
		}
		
		return true;
	}

	private Boolean clearConfigTask() throws SQLException
	{
		if (log.isDebugEnabled())
			log.debugf("apply clear config for orgId %s netId %d sn %s", orgId, netId, sn);
		
		String errMsg = null; 
		final boolean rdonly = false;
		boolean isDevConfigTextSupport = false;
		boolean isWlcEnabled = false;
		
		ConfigGetObject oldConfigObject = null;
		ConfigGetInfo cfgInfo = new ConfigGetInfo();
		
		List<String> CONFIG_CLEAR_KEY_PATTERN_LIST = new ArrayList<String>();
		StringBuilder sbSummary = new StringBuilder();
		sbSummary.append("\n* action: clearConfigTask");
		sbSummary.append("\n* date: " + DateUtils.getUtcDate());
		sbSummary.append("\n* org: " + orgId + " netId (irrelevent): " + netId + " devId: " + devId);
		sbSummary.append("\n* iana_id: " + iana_id + " sn: " + sn);
		sbSummary.append("\n* threadId: "+Thread.currentThread().getName());		
		sbSummary.append("\n* sid: " + sid);

		log.debugf("performing clear config for sn %s ", sn);

		/* ... */
		if (iana_id == null || sn == null) {
			log.warnf("dev sn or iana_id (%d, %s) is null", iana_id, sn);
			return false;
		}

		//DeviceUpdatesDAO devUpDAO = null;
		ConfigUpdatesDAO confUpDAO = null;
		ConfigUpdatesId confUpId = null;
		ConfigUpdates confUp = null;
		try {
			//devUpDAO = new DeviceUpdatesDAO(orgId, rdonly);
			confUpDAO = new ConfigUpdatesDAO(false);
			confUpId = new ConfigUpdatesId(iana_id, sn);
			confUp = confUpDAO.findById(confUpId);
			if (confUp==null)
			{
				errMsg = String.format("dev %d %s is not found from ConfigUpdates!", iana_id, sn);
				log.error(errMsg);
				return false;
			}
		} catch (SQLException e) {
			log.error("SQLException ", e);
			return false;
		}
		
		DevOnlineObject devOnline = PoolObjectDAO.getDevOnlineObject(iana_id, sn);
		if (PROD_MODE) {
			if (devOnline == null || !devOnline.isOnline()) // CTD
			{
				errMsg = String.format("dev %s is offline", sn);
				log.debug(errMsg);
				sbSummary.append("\n* err - "+errMsg);
				//devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
				confUpDAO.decrementConfClearForDev(confUpId, sbSummary.toString(), errMsg, false);
				return false;
			}
		}
				
		cfgInfo.setIana_id(iana_id);
		cfgInfo.setSn(sn);
		cfgInfo.setSid(sid);
		cfgInfo.setRetry(1);
		cfgInfo.setSbSummary(sbSummary);
		
		try {
			oldConfigObject = ConfigGetUtils.getObject(cfgInfo);
		} catch (Exception e) {
			errMsg = String.format("dev %s getPoolObjectBySn exception", sn);
			log.error(errMsg, e);
			sbSummary.append("\n* err - "+errMsg);
			if (PROD_MODE)
				confUpDAO.decrementConfClearForDev(confUpId, sbSummary.toString(), errMsg, false);
			return false;
		} 
		
		try {
			if (oldConfigObject != null) {
				if (oldConfigObject.getStatus() != 200) {
					errMsg = String.format("dev %s config get with status %d", sn, oldConfigObject.getStatus());
					log.warn(errMsg);
					sbSummary.append("\n* err - "+errMsg);
					// devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
					if (PROD_MODE)
						confUpDAO.decrementConfClearForDev(confUpId, sbSummary.toString(), errMsg, false);

					ConfigGetUtils.removeObject(cfgInfo);
					return false;
				}

				if (oldConfigObject.getConfig() != null) {

					Properties fullConfig = null;
					String remainWlanOrderStr = "";
					String remainMvpnOrderStr = "";

					Enumeration<String> enumeration = null;

					/**** CREATE FINAL CONFIG FROM OLD CONFIG ****/
					fullConfig = CommonConfigUtils.parsePropertiesString(oldConfigObject.getConfig());

					/**** CLEAR OLD CONFIG BEFORE MERGE ****/
					isWlcEnabled = RadioConfigUtils.isWlcEnabled(oldConfigObject.getConfig());
					if (isWlcEnabled) {
						sbSummary.append("\n* - wlc enabled: , wifi config not clear");
					} else {
						sbSummary.append("\n* - wlc not enabled: , wifi config clear");

						/* clear ic2 */
						String[] wlanLst = CommonConfigUtils.orderToLst(fullConfig
								.getProperty(RadioConfigUtils.WLAN_ORDER_TAG));
						if (wlanLst != null) {
							for (int i = 0; i < wlanLst.length; i++) {
								String icmgKey = CommonConfigUtils.createICMGKey(Integer.valueOf(wlanLst[i]),
										CommonConfigUtils.CONFIG_TAG_TYPE.TYPE_WLAN);
								log.debugf("checking icmgKey %s", icmgKey);
								if (fullConfig.containsKey(icmgKey)) {
									log.debugf("remove icmgKey=%s", icmgKey);
									String icmgFilter = CommonConfigUtils.createICMGKeyFilter(
											Integer.valueOf(wlanLst[i]), CommonConfigUtils.CONFIG_TAG_TYPE.TYPE_WLAN);
									CONFIG_CLEAR_KEY_PATTERN_LIST.add(icmgFilter);
									sbSummary.append("\n* - clear " + icmgFilter);
								} else {
									remainWlanOrderStr += Integer.valueOf(wlanLst[i]) + " ";
								}
							}

							if (remainWlanOrderStr != null)
								remainWlanOrderStr = "\"" + remainWlanOrderStr.trim() + "\"";
						}

						CONFIG_CLEAR_KEY_PATTERN_LIST.add(RadioConfigUtils.WLAN_ORDER_TAG);

						log.debugf("remainWlanOrderStr=%s", remainWlanOrderStr);
					}

					/* clear ic2 */
					String[] mvpnLst = CommonConfigUtils.orderToLst(fullConfig
							.getProperty(RadioConfigUtils.MVPN_ORDER_TAG));
					if (mvpnLst != null) {
						for (int i = 0; i < mvpnLst.length; i++) {
							String icmgKey = CommonConfigUtils.createICMGKey(Integer.valueOf(mvpnLst[i]),
									CommonConfigUtils.CONFIG_TAG_TYPE.TYPE_MVPN);
							log.debugf("checking icmgKey %s", icmgKey);
							if (fullConfig.containsKey(icmgKey)) {
								log.debugf("remove icmgKey=%s", icmgKey);
								String icmgFilter = CommonConfigUtils.createICMGKeyFilter(Integer.valueOf(mvpnLst[i]),
										CommonConfigUtils.CONFIG_TAG_TYPE.TYPE_MVPN);
								CONFIG_CLEAR_KEY_PATTERN_LIST.add(icmgFilter);
								sbSummary.append("\n* - clear " + icmgFilter);
							} else {
								remainMvpnOrderStr += Integer.valueOf(mvpnLst[i]) + " ";
							}
						}

						if (remainMvpnOrderStr != null && remainMvpnOrderStr.length() != 0)
							remainMvpnOrderStr = "\"" + remainMvpnOrderStr.trim() + "\"";
					}

					CONFIG_CLEAR_KEY_PATTERN_LIST.add(RadioConfigUtils.MVPN_ORDER_TAG);

					log.debugf("remainMvpnOrderStr=%s", remainMvpnOrderStr);

					log.debugf("CONFIG_CLEAR_KEY_PATTERN_LIST=%s", CONFIG_CLEAR_KEY_PATTERN_LIST);
					enumeration = (Enumeration<String>) fullConfig.propertyNames();
					while (enumeration.hasMoreElements()) {
						boolean exempt = false;
						String key = (String) enumeration.nextElement();

						for (String filter : RadioConfigUtils.CONFIG_FH_SPEC_KEY_PATTERN_LIST) {
							if (key.matches(filter))
								exempt = true;
						}

						for (String filter : CONFIG_CLEAR_KEY_PATTERN_LIST) {
							if (!exempt && key.matches(filter)) {
								fullConfig.remove(key);
							}
						}
					}

					if (!isWlcEnabled) {
						if (remainWlanOrderStr != null && remainWlanOrderStr.length() != 0)
							fullConfig.put(RadioConfigUtils.WLAN_ORDER_TAG, remainWlanOrderStr);
						// else if (fullConfig.contains(RadioConfigUtils.WLAN_ORDER_TAG))
						// fullConfig.put(RadioConfigUtils.WLAN_ORDER_TAG, "");
					}

					if (remainMvpnOrderStr != null && remainMvpnOrderStr.length() != 0)
						fullConfig.put(RadioConfigUtils.MVPN_ORDER_TAG, remainMvpnOrderStr);
					// else if (fullConfig.contains(RadioConfigUtils.MVPN_ORDER_TAG))
					// fullConfig.put(RadioConfigUtils.MVPN_ORDER_TAG, "");

					/**** REMOVE CACHE OBJECT AFTER USE ****/
					try {
						ACUtil.<ConfigGetObject> removePoolObjectBySn(oldConfigObject, ConfigGetObject.class);
					} catch (InstantiationException | IllegalAccessException e1) {
						// log.error("fail to remove config object " + e1, e1);
						errMsg = String.format("fail to remove config object " + e1);
						log.error(errMsg);
						sbSummary.append("\n* err - "+errMsg);
						// devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
						if (PROD_MODE)
							confUpDAO.decrementConfClearForDev(confUpId, sbSummary.toString(), errMsg, false);
					}

					/**** PUT CONFIG ****/
					log.debugf("Fetching PIPE_INFO_TYPE_CONFIG_PUT for dev (%s)", sn);
					ConfigPutObject putObject = new ConfigPutObject();
					String orderedConfig = CommonConfigUtils.getOrderedConfig(fullConfig);
					try {
						if (oldConfigObject.getSid() != null && !oldConfigObject.getSid().isEmpty())
							putObject.setSid(oldConfigObject.getSid());
						sbSummary.append("\n* sid applied: " + sid);
					} catch (NullPointerException e) {
						log.warn("Nullpointerexception here", e);
					}
					putObject.setConfig(orderedConfig);
					putObject.setFilepath(oldConfigObject.getFilepath());

					log.debugf("(%s) clear config = %s", sn, putObject.getConfig());

					try {
						// DevicesDAO devDAO = new DevicesDAO(orgId, rdonly);

						sbSummary.append("\n* - ICMG_MVPN=0");
						sbSummary.append("\n* - ICMG_WLAN=0");
						sbSummary.append("\n* - CONFIG CLEARED APPLIED");
						if (PROD_MODE) {
							/* do config clear */
							// devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), null, false);
							confUpDAO.decrementConfClearForDev(confUpId, sbSummary.toString(), errMsg, false);
							if (isDevConfigTextSupport)
								ACService.<ConfigPutObject> fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_PUT_TEXT,
										sid, iana_id, sn, putObject);
							else
								ACService.<ConfigPutObject> fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_PUT, sid,
										iana_id, sn, putObject);
							RadioConfigUtils.sendWebadminIcmg(sid, iana_id, sn, false, false,
									"CLEAR_CONFIG clear icmg for " + iana_id + "," + sn, false, null);
						}

					} catch (SQLException e) {
						log.error("SQLException - " + e, e);

						log.debugf("delete config temp file " + oldConfigObject.getFilepath());
						InternalCmdObject cmdO = new InternalCmdObject();
						cmdO.setCommand(CMD.DEL_CFG_TMP_FILE);
						cmdO.setParam(oldConfigObject.getFilepath());
						log.debugf("cmdO = %s", cmdO);
						if (PROD_MODE) {
							ACService.<InternalCmdObject> fetchCommand(MessageType.PIPE_INFO_TYPE_INTERNAL_CMD, sid,
									iana_id, sn, cmdO);
						}

						return false;
					}
				} else {
					errMsg = String
							.format("CLEAR_CONFIG - Fail to get update config from online device %s (status=%d)",
									sn, oldConfigObject.getStatus());
					log.warn(errMsg);
					sbSummary.append("\n* err - "+errMsg);
					// devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
					if (PROD_MODE)
						confUpDAO.decrementConfClearForDev(confUpId, sbSummary.toString(), errMsg, false);
				}
			} else {
				errMsg = String.format(
						"CLEAR_CONFIG - Fail to get update config from online device %s (Timeout)",
						sn);
				log.warn(errMsg);
				sbSummary.append("\n* err - "+errMsg);
				// devUpDAO.decrementConfUpdateForDev(devId, sbSummary.toString(), errMsg, false);
				if (PROD_MODE)
					confUpDAO.decrementConfClearForDev(confUpId, sbSummary.toString(), errMsg, false);
			}
		} catch (Exception e) {
			errMsg = String.format("CLEAR_CONFIG - dev %d %s with exception %s", iana_id, sn, e);
			sbSummary.append("\n* err - "+errMsg);
			log.error(errMsg, e);					
			if (PROD_MODE)
				confUpDAO.decrementConfClearForDev(confUpId, sbSummary.toString(), errMsg, false);
		}

		log.debug("\n************* Clear Config Summary " + sn + " *****************" + sbSummary.toString() + "\n*******************************************************************");

		log.debugf("memTrace - ConfigPutTask is returned for sn %s", this.sn);
		return true;
	}

	/*** setters and getters ***/
	public StringBuilder getSbSummary() {
		return sbSummary;
	}
	
}
