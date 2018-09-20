package com.littlecloud.control.webservices.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.control.dao.ConfigurationRadioChannelsDAO;
import com.littlecloud.control.dao.ConfigurationRadiosDAO;
import com.littlecloud.control.dao.ConfigurationSsidsDAO;
import com.littlecloud.control.dao.DeviceUpdatesDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.TagsDAO;
import com.littlecloud.control.dao.TagsxconfigurationSsidsDAO;
import com.littlecloud.control.entity.ConfigurationRadioChannels;
import com.littlecloud.control.entity.ConfigurationRadioChannelsId;
import com.littlecloud.control.entity.ConfigurationRadios;
import com.littlecloud.control.entity.ConfigurationRadiosId;
import com.littlecloud.control.entity.ConfigurationSettings;
import com.littlecloud.control.entity.ConfigurationSettingsId;
import com.littlecloud.control.entity.ConfigurationSsids;
import com.littlecloud.control.entity.ConfigurationSsidsId;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Tags;
import com.littlecloud.control.entity.TagsxconfigurationSsids;
import com.littlecloud.control.entity.TagsxconfigurationSsidsId;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.model.Json_Tags;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings.Modules;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettingsWeb;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfiles;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfiles.SECURITY;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfilesWeb;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfilesWeb.ACTION;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerDeviceTask;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerDeviceTask.CONFIG_UPDATE_REASON;
import com.littlecloud.control.json.model.config.util.ConfigurationSettingsUtils;
import com.littlecloud.control.json.model.config.util.PortalConfigUtils;
import com.littlecloud.control.json.model.config.util.PortalConfigUtils.PortalSsidConfigInfo;
import com.littlecloud.control.json.model.config.util.RadioConfigUtils;
import com.littlecloud.control.json.model.config.util.exception.FeatureNotFoundException;
import com.littlecloud.control.json.request.JsonRadioRequest;
import com.littlecloud.control.json.request.JsonRadioRequest_DataRadioSettings;
import com.littlecloud.control.json.request.JsonRadioRequest_DataSsidProfiles;
import com.littlecloud.control.json.request.JsonRadioRequest_DataSsidProfilesList;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.utils.ConfigSettingsUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.util.DBUtil;

public class RadioWsHandler {

	private static final Logger log = Logger.getLogger(RadioWsHandler.class);
	private static final String NETWORK_LEVEL_CFG = "WLAN";
	
	public static String getRequest(JsonRadioRequest request, JsonResponse<JsonConf_RadioSettingsWeb> response) {
		/* create request */
		request.setCaller_ref(JsonUtils.genServerRef());
		request.setVersion("0.1");
		request.setNetwork_id(1);
		request.setOrganization_id("test");

		return JsonUtils.toJson(request);
	}

	public static String getRadioConfig(JsonRadioRequest request, JsonResponse<JsonConf_RadioSettingsWeb> response) throws Exception {
		
		//boolean bIsIndividualCfgEnabled = false;

		String orgId = request.getOrganization_id();
		int netId = request.getNetwork_id();
		int devId = 0;
		try {
			devId = request.getDevice_id();
//			if (devId != 0)
//				bIsIndividualCfgEnabled = true;
		} catch (NullPointerException e)
		{
			log.debug("requesting network settings");
		}		
		log.debugf("getRadioConfig (%s, %d, %d)", orgId, netId, devId);
				
		ConfigurationSettings devSet = null;
		ConfigurationSettings netSet = null;
		ConfigurationSettingsId netSettingsId = null;
		ConfigurationSettingsId settingsId = null;
		Devices dev = null;
		
		/* Conversion of old config to new config table */
		ConfigurationSettingsUtils settingUtils = new ConfigurationSettingsUtils(orgId, netId, devId);
		/* it will only convert if new settings does not exist */
//		if (!settingUtils.convertToNewSettingsAllDevices())		
//		{
//			/* fail to convert to new settings */
//			response.setResp_code(ResponseCode.INVALID_OPERATION);
//			response.setMessage("Fail to convert existing config settings to new table!");
//			return JsonUtils.toJson(response);
//		}
		
		try {
			// ConfigurationSsidsDAO ssidDAO = new ConfigurationSsidsDAO(param_orgId);
			JsonConf_RadioSettingsWeb radioJson = RadioConfigUtils.getDatabaseRadioFullConfig(orgId, netId, devId);
			List<Modules> modules = radioJson.getModulesLst();
			if( modules != null )
			{
				//ConfigurationSsidsDAO configurationSsidDAO = new ConfigurationSsidsDAO(orgId, true);
				netSet = settingUtils.getNetworkConfigSettings();
				if (netSet == null)
				{
					netSettingsId = new ConfigurationSettingsId(netId, 0);
					netSet = new ConfigurationSettings();
					netSet.setId(netSettingsId);
					netSet.fillDefaultValues();
				}

				Map<Integer, ConfigurationSettings> devSettingsMap = settingUtils.getDevConfigSettingsOfNetwork();
				for(Modules mod : modules)
				{
					//boolean isWifiManageable = configurationSsidDAO.isWifiManageable(netId, mod.getDevice_id());
					devSet = devSettingsMap.get(mod.getDevice_id());
					if (devSet == null)
					{						
						devSet = new ConfigurationSettings();
						settingsId = new ConfigurationSettingsId(netId, devId);
						devSet.setId(settingsId);
						devSet.fillDefaultValues();
					}
					
					//dev = NetUtils.getDevices(orgId, devId);
					//mod.setWifi_cfg(ConfigUtils.getWifiCfg(dev, orgId, settings.isWifi_enabled()));
					//mod.setWifi_cfg(ConfigUtils.getDevWifiManaged(dev, netSet, devSet));
				}
			}
			response.setTimestamp(radioJson.getTimestamp());
			response.setData(radioJson);
			response.setResp_code(ResponseCode.SUCCESS);
		} catch (FeatureNotFoundException e) {
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			response.setMessage("FeatureNotFoundException getRadioConfig() - probably product table is undefined."+e.getMessage());
		} catch (Exception e) {
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}

		return JsonUtils.toJson(response);
	}

	/*
	 * To Do:
	 * Need front end to provide a modified device list so only those device will be updated.
	 */
	public static String putRadioConfig(JsonRadioRequest_DataRadioSettings request, JsonResponse<JsonConf_RadioSettingsWeb> response) {
		boolean bIsIndividualCfgEnabled = false;

		String orgId = request.getOrganization_id();
		int netId = request.getNetwork_id();
		int devId = 0;
		try {
			devId = request.getDevice_id();
			if (devId != 0)
				bIsIndividualCfgEnabled = true;
		} catch (NullPointerException e)
		{
			log.debug("requesting network settings");
		}

		JsonConf_RadioSettingsWeb newRadioJson = request.getData();
		if (newRadioJson == null)
		{
			response.setResp_code(ResponseCode.MISSING_INPUT);
			return JsonUtils.toJson(response);
		}
		log.debugf("newRadioJson=%s", newRadioJson);
		
		DBUtil dbUtil = null;		
		DBConnection tranConn = null;
		try {
			dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
			tranConn = dbUtil.getConnection(false, orgId, false);
			tranConn.setBatchMode();
			
			/* get existing config */
			JsonConf_RadioSettingsWeb fullRadioJson = RadioConfigUtils.getDatabaseRadioFullConfig(orgId, netId, devId);

			/* merge with partial new config */
			if (!fullRadioJson.updateWith(newRadioJson))
			{
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
				return JsonUtils.toJson(response);
			}

			/* persist to database */
			try {
				// NetworksDAO networkDAO = new NetworksDAO(param_orgId);
				ConfigurationRadiosDAO radioDAO = new ConfigurationRadiosDAO(orgId);
				DeviceUpdatesDAO deviceUpdateDAO = new DeviceUpdatesDAO(orgId);

				/* update radio */
				ConfigurationRadiosId radioId = new ConfigurationRadiosId();
				radioId.setNetworkId(netId);
				radioId.setDeviceId(devId);
				ConfigurationRadios radio = radioDAO.findById(radioId);
				if (radio == null)
				{
					radio = new ConfigurationRadios();
					radio.setId(radioId);
				}
				radio.setCountry(fullRadioJson.getCountry_code());
				radio.setDefaultBand(fullRadioJson.getDefault_band());
				radio.setProtocol(fullRadioJson.getPrefer_5gz_protocol().toString());
				radio.setTimestamp(new Date());
				radioDAO.saveOrUpdate(radio);

				/* update radio channel */
				for (Modules updateM : fullRadioJson.getModulesLst())
				{
					log.debugf("updateM=%s", updateM);					
					ConfigurationRadioChannelsId radioChannelId = new ConfigurationRadioChannelsId();
					radioChannelId.setDeviceId(updateM.getDevice_id());
					radioChannelId.setModuleId(updateM.getModule_id());
					ConfigurationRadioChannelsDAO radioChannelDAO = new ConfigurationRadioChannelsDAO(orgId);
					ConfigurationRadioChannels radioChannel = radioChannelDAO.findById(radioChannelId);
					if (radioChannel == null)
					{
						radioChannel = new ConfigurationRadioChannels();
						radioChannel.setId(radioChannelId);
					}
					radioChannel.setBoost(updateM.getBoost());
					radioChannel.setChannel(updateM.getChannel());
					radioChannel.setPower(updateM.getPower());
					radioChannel.setFrequencyBand(updateM.getFrequency_band());
					radioChannel.setTimestamp(new Date());
					log.debugf("radioChannel=%s", radioChannel);
					radioChannelDAO.saveOrUpdate(radioChannel);
				}

				/* mark device for conf update */
				// deviceUpdateDAO.incrementConfUpdateForDevLst(...);
				//TODO:update modified dev
				//deviceUpdateDAO.incrementConfUpdateForNetwork(netId, response.getServer_ref(), "radio config change");
				new ConfigUpdatePerDeviceTask(orgId, netId).performConfigUpdateNowForNetwork(response.getServer_ref(), 
						CONFIG_UPDATE_REASON.put_radio_config.toString());
				
				ConfigSettingsUtils.removePoolObjectByNetId(orgId, netId);
				
			} catch (NullPointerException e) {
				log.error("generally MISSING_INPUT - " + e, e);
				response.setResp_code(ResponseCode.MISSING_INPUT);
				return JsonUtils.toJson(response);
			} catch (Exception e) {
				log.error("transaction is rollback - " + e, e);
				throw e;
			}

			/* execute config update */
			log.debugf("orgId %s netId %d devId %d", orgId, netId, devId);
			fullRadioJson = RadioConfigUtils.getDatabaseRadioFullConfig(orgId, netId, devId);
			
			response.setTimestamp(new Date());
			response.setData(fullRadioJson);
			response.setResp_code(ResponseCode.SUCCESS);
			
			if (tranConn!=null && tranConn.isBatchMode())
			{
				tranConn.commit();
			}
		} catch (Exception e) {
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		} finally {
			
			if (dbUtil!=null && dbUtil.isSessionStarted()) {
				dbUtil.endSession();
			}
		}
		
		return JsonUtils.toJson(response);
	}

	/***
	 *  Only used to update wifi mgm enable or follow network settings now 
	 *  ***/  
	public static String putSsidProfileConfigList(JsonRadioRequest_DataSsidProfilesList request, JsonResponse<JsonConf_RadioSettings> response) {
		
		boolean isUpdatingDevLvl = false;
		boolean isFollowGroupSettings = false;
//		boolean isNetworkCfgEnabled = false;

		String orgId = request.getOrganization_id();
		int netId = request.getNetwork_id();
		Boolean isWifiManaged = request.getWifi_mgm_enabled();
				
		try {
			isFollowGroupSettings = request.getFollow_network();
		} catch (NullPointerException e)
		{
			log.debug("network settings is given");
		}
		int devId = request.getDevice_id();
		
		if (devId != 0)
			isUpdatingDevLvl = true;
//		else
//		{
//			if( isWifiManaged != null && isWifiManaged.booleanValue() == true )
//			{
//				isNetworkCfgEnabled = true;
//			}
//		}
		
		log.debugf("putSsidProfileConfigList request=%s", request);
		try {
			//DeviceUpdatesDAO deviceUpdateDAO = new DeviceUpdatesDAO(orgId);
			ConfigurationSettingsUtils settingsUtils = new ConfigurationSettingsUtils(orgId, netId, devId);
			ConfigurationSettingsId settingsId = new ConfigurationSettingsId(netId, devId);
			ConfigurationSettings settings = new ConfigurationSettings();
			settings.setId(settingsId);
			settings.setFollow_network(isFollowGroupSettings);
			settings.setWifi_enabled(isWifiManaged);
			log.debugf("putSsidProfileConfigList settings=%s", settings);
			settingsUtils.saveOrUpdateConfigurationSettings(settings);
			response.setResp_code(ResponseCode.SUCCESS);
			
			if (isUpdatingDevLvl)
			{
				//deviceUpdateDAO.incrementConfUpdateForDevLst(Arrays.asList(devId), response.getServer_ref(), "update dev lvl config management");
				new ConfigUpdatePerDeviceTask(orgId, netId).performConfigUpdateNow(Arrays.asList(devId), response.getServer_ref(), 
						CONFIG_UPDATE_REASON.dev_lvl_mgm_update.toString());
				
				ConfigSettingsUtils.removePoolObjectByDevId(orgId, devId);
			}
			else
			{
				//deviceUpdateDAO.incrementConfUpdateForNetwork(netId, response.getServer_ref(), "update grp lvl config management");}
				new ConfigUpdatePerDeviceTask(orgId, netId).performConfigUpdateNowForNetwork(response.getServer_ref(), 
						CONFIG_UPDATE_REASON.grp_lvl_ssid_update.toString());
				
				ConfigSettingsUtils.removePoolObjectByNetId(orgId, netId);
			}
			
			
			/** return updated config **/
			JsonConf_RadioSettingsWeb radioJson = RadioConfigUtils.getDatabaseRadioFullConfig(orgId, netId, devId);
			response.setTimestamp(new Date());
			response.setData(radioJson);
			response.setResp_code(ResponseCode.SUCCESS);
			if (log.isDebugEnabled()) log.debugf("response = %s", response);
		} catch (Exception e) {
			log.error("putSsidProfileConfigList - ", e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}

		return JsonUtils.toJson(response);
	}
	
	public static String putSsidProfileConfig(JsonRadioRequest_DataSsidProfiles request, JsonResponse<JsonConf_RadioSettingsWeb> response)
	{
		/* 
		 * Tags in putSsidProfileConfig must be existing tags. 
		 * No tags creation is supported here to prevent garbage data of unused tags (no device association) created here.
		 * 
		 */		
		boolean isUpdatingDevLvl = false;

		String orgId = request.getOrganization_id();
		Integer netId = request.getNetwork_id();
		int devId = 0;
		boolean psk_enable = true;
		Devices devInd = null;
		List<Json_Tags> deleted_tags = null;
		List<Json_Tags> added_tags = null;
		ACTION action = null;
		DBUtil dbUtil = null;		
		DBConnection tranConn = null;
		
		try {
			devId = request.getDevice_id();
			if (devId != 0)
				isUpdatingDevLvl = true;
		} catch (NullPointerException e)
		{
			log.debug("requesting network settings");
		}
		JsonConf_SsidProfilesWeb newSsidProJsonWeb = request.getData();
		
		if (newSsidProJsonWeb==null)
		{
			response.setResp_code(ResponseCode.INVALID_INPUT);
			response.setMessage("data is not specified");
			return JsonUtils.toJson(response);
		}
		
		if(newSsidProJsonWeb!=null && newSsidProJsonWeb.getSsid()!=null && newSsidProJsonWeb.getSsid().length() > 32 )
		{
			response.setResp_code(ResponseCode.INVALID_INPUT);
			response.setMessage("Ssid length is too long to handle");
			return JsonUtils.toJson(response);
		}
		
		deleted_tags = newSsidProJsonWeb.getDeleted_tags();
		added_tags = newSsidProJsonWeb.getAdded_tags();	
		action = newSsidProJsonWeb.getAction();
		
		log.debugf("ssidJson = %s", newSsidProJsonWeb);
		
		if (newSsidProJsonWeb!=null && newSsidProJsonWeb.getAction() == null)
		{
			response.setResp_code(ResponseCode.INVALID_INPUT);
			response.setMessage("Ssid action is not specified");
			return JsonUtils.toJson(response);
		}

		try {
			dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
			tranConn = dbUtil.getConnection(false, orgId, false);
			tranConn.setBatchMode();
			
			/** persist to database **/
			ConfigurationSsidsDAO ssidDAO = new ConfigurationSsidsDAO(orgId);
			DeviceUpdatesDAO deviceUpdateDAO = new DeviceUpdatesDAO(orgId);
			DevicesDAO deviceDAO = new DevicesDAO(orgId);
			TagsxconfigurationSsidsDAO tagXssidDAO = new TagsxconfigurationSsidsDAO(orgId);
			TagsDAO tagDAO = new TagsDAO(orgId);

			if (isUpdatingDevLvl)
			{
				devInd = deviceDAO.findById(devId);
				if (devInd == null)
				{
					log.errorf("dev %d is not found!", devId);
					response.setResp_code(ResponseCode.INVALID_INPUT);
					return JsonUtils.toJson(response);
				}
			}
			
			ConfigurationSsidsId ssidid = new ConfigurationSsidsId();
			ssidid.setSsidId(newSsidProJsonWeb.getSsid_id());
			ssidid.setDeviceId(devId);
			ssidid.setNetworkId(netId);
			ConfigurationSsids ssid = ssidDAO.findById(ssidid);

			switch(action)
			{
			case insert:
				/* check duplicate - ssid_id is assigned by ws client */
				if (ssid!=null)
				{
					response.setResp_code(ResponseCode.INVALID_INPUT);
					response.setMessage("ssid_id "+ssid.getId()+" already existed for insert");
					return JsonUtils.toJson(response);
				}					
				break;
			case update:
			case delete:
				/* get existing config */
				if (ssid==null)
				{
					response.setResp_code(ResponseCode.INVALID_INPUT);
					response.setMessage("ssid_id "+ssidid+" is not found for update/delete!");
					return JsonUtils.toJson(response);
				}	
				break;			
			default:
				response.setResp_code(ResponseCode.INVALID_INPUT);
				response.setMessage("action "+action+"is invalid to default");
				return JsonUtils.toJson(response);
			}

			switch(action)
			{
				case insert:
				case update:
				{
					JsonConf_SsidProfiles ssidProJson = null;
					if (ssid == null) {
						ssid = new ConfigurationSsids();
						ssid.setId(ssidid);						
						ssidProJson = newSsidProJsonWeb;
					}
					else
					{
						ssidProJson = JsonUtils.fromJson(ssid.getConfig(), JsonConf_SsidProfiles.class);
						
						if (ssidProJson.getMulticast_rate()==null)
						{
							JsonConf_SsidProfiles ssidProJsonNew = new JsonConf_SsidProfiles();
							ssidProJsonNew.setDefaultValues(ssidid.getSsidId());
							ssidProJsonNew.updateWith(ssidProJson);
							ssidProJson = ssidProJsonNew;
						}
						
						if (!ssidProJson.updateWith(newSsidProJsonWeb))
						{
							response.setResp_code(ResponseCode.INTERNAL_ERROR);
							return JsonUtils.toJson(response);
						}
					}					
	
					if (ssidProJson.getSecurity() == SECURITY.wpa_wpa2_enterprise)
					{
						psk_enable = false;
					}
					else
					{
						psk_enable = true;
					}
	
					JsonConf_SsidProfiles.WpaPersonal wpa = ssidProJson.getWpa_personal();
					if (wpa == null)
						wpa = ssidProJson.new WpaPersonal();
					wpa.setPsk_enable(psk_enable);
					ssidProJson.setWpa_personal(wpa);
					
					/* dual band settings - default to "preferred" */
					if (ssidProJson.getRadio_band().equalsIgnoreCase(JsonConf_RadioSettings.STRING_DUAL_BAND))
					{
						if (ssidProJson.getBand_steering()==null || ssidProJson.getBand_steering().isEmpty())
							ssidProJson.setBand_steering("preferred");
					}
					else
					{
						ssidProJson.setBand_steering("disable");
					}
					
					/* captive portal */
					PortalSsidConfigInfo ssidCtr = new PortalSsidConfigInfo();
					ssidCtr.setSsidProfileJson(ssidProJson);
					ssidCtr.setIsPortalEnable(ssidProJson.getPortal_enabled());
					
					if (newSsidProJsonWeb.getCaptive_portal()!=null)
					{
						ssidCtr.setAccessMode(newSsidProJsonWeb.getCaptive_portal().getAccessMode());
						ssidCtr.setFbGwId(newSsidProJsonWeb.getCaptive_portal().getFbGatewayId());
						ssidCtr.setFbGwSecret(newSsidProJsonWeb.getCaptive_portal().getFbGatewaySecret());
					}
					
					if (!PortalConfigUtils.configSsidPortal(ssidCtr))
					{
						log.errorf("dev %s %d miss portal information (%s)!", orgId, devId, ssidCtr);
						response.setResp_code(ResponseCode.INVALID_INPUT);
						return JsonUtils.toJson(response);
					}
	
					/* other settings */
					ssid.setSsid_enabled(newSsidProJsonWeb.getEnabled());
					ssid.setSsid(newSsidProJsonWeb.getSsid());
					ssid.setConfig(JsonUtils.toJson(ssidProJson));
					ssid.setConfigVersion(newSsidProJsonWeb.getVersion());
					ssid.setTimestamp(new Date());
					log.debugf("saveOrUpdate ssid=%s", ssid);
					ssidDAO.saveOrUpdate(ssid);
				}
				break;
				case delete:
				{
					log.debugf("delete ssid=%s", ssid);
					ssidDAO.delete(ssid);
					
					/* create tag list for deletion */
					List<Tags> tagLst = tagDAO.findByConfigurationSsidsId(ssid.getId());
					if (tagLst!=null && !tagLst.isEmpty())
					{
						for (Tags t:tagLst)
						{
							Json_Tags jsonTag = new Json_Tags();
							jsonTag.parseTags(t);
							deleted_tags.add(jsonTag);
						}
					}						
				}
				break;
				default:
					log.error("putSsidProfileConfig - Shouldnt be here!!");
					break;
					
			}
			
			
			/* update tags */
			/* delete tag association */				
			if (deleted_tags != null)
			{
				for (Json_Tags tags : deleted_tags)
				{
					Tags tag = tagDAO.findById(tags.getId());

					if (tag == null)
					{
						response.setMessage("tags " + tags + " in deleted_tags does not exist!");
						response.setError(10001);
						throw new IllegalArgumentException("tags " + tags + " in deleted_tags does not exist!");
					}

//						List<Tags> tagLst = tagDAO.findByConfigurationSsidsId(ssid.getId());
//						for (Tags t : tagLst)
//						{
//							tagDAO.delete(t);
//						}
					
					TagsxconfigurationSsidsId tagXssidId = new TagsxconfigurationSsidsId();
					tagXssidId.setNetworkId(ssid.getId().getNetworkId());
					tagXssidId.setSsidId(ssid.getId().getSsidId());
					tagXssidId.setTagId(tag.getId());

					TagsxconfigurationSsids tagXssid = new TagsxconfigurationSsids();
					tagXssid.setId(tagXssidId);
					tagXssidDAO.delete(tagXssid);
				}
			}

			/* add tag association */
			if (added_tags != null)
			{
				for (Json_Tags tags : added_tags)
				{
					Tags tag = tagDAO.findById(tags.getId());

					if (tag == null)
					{
						response.setMessage("tags " + tags + " in added_tags does not exist!");
						response.setError(10001);
						throw new IllegalArgumentException("tags " + tags + " in added_tags does not exist!");
					}

					TagsxconfigurationSsidsId tagXssidId = new TagsxconfigurationSsidsId();
					tagXssidId.setNetworkId(ssid.getId().getNetworkId());
					tagXssidId.setSsidId(ssid.getId().getSsidId());
					tagXssidId.setTagId(tag.getId());

					TagsxconfigurationSsids tagXssid = new TagsxconfigurationSsids();
					tagXssid.setId(tagXssidId);
					tagXssidDAO.saveOrUpdate(tagXssid);
				}
			}
			
			if (isUpdatingDevLvl)
			{
				//deviceUpdateDAO.incrementConfUpdateForDevLst(Arrays.asList(devId), response.getServer_ref(), "update dev lvl ssid profile");
				new ConfigUpdatePerDeviceTask(orgId, netId).performConfigUpdateNow(Arrays.asList(devId), response.getServer_ref(), 
						CONFIG_UPDATE_REASON.dev_lvl_ssid_update.toString());
				
				ConfigSettingsUtils.removePoolObjectByDevId(orgId, devId);
			}
			else
			{
				//deviceUpdateDAO.incrementConfUpdateForNetwork(netId, response.getServer_ref(), "update grp lvl ssid profile");
				new ConfigUpdatePerDeviceTask(orgId, netId).performConfigUpdateNowForNetwork(response.getServer_ref(), 
						CONFIG_UPDATE_REASON.grp_lvl_ssid_update.toString());
				
				ConfigSettingsUtils.removePoolObjectByNetId(orgId, netId);
			}
			
			/** return updated config **/
			JsonConf_RadioSettingsWeb radioJson = RadioConfigUtils.getDatabaseRadioFullConfig(orgId, netId, devId);
			response.setTimestamp(new Date());
			response.setData(radioJson);
			response.setResp_code(ResponseCode.SUCCESS);
			
			if (tranConn!=null && tranConn.isBatchMode())
			{
				tranConn.commit();
			}
		} catch (Exception e) {
			log.error("Exception "+request.getCaller_ref(), e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		} finally {			
			if (dbUtil!=null && dbUtil.isSessionStarted()) {
				dbUtil.endSession();
			}
		}

		return JsonUtils.toJson(response);
	}

	public static String updateAutoChannel(JsonRadioRequest request, JsonResponse<JsonConf_RadioSettingsWeb> response) {
		String param_orgId = request.getOrganization_id();
		int param_netId = request.getNetwork_id();
		JsonConf_RadioSettingsWeb radioJson = null;
		Map<Integer, List<Integer>> deviceIdRadioMap = new HashMap<Integer, List<Integer>>();
		
		try {
			try {
				DevicesDAO deviceDAO = new DevicesDAO(param_orgId);

				/* get all devices with channel set to auto */
				radioJson = RadioConfigUtils.getDatabaseRadioFullConfig(param_orgId, param_netId, 0, false);
				if (radioJson!=null)
				{
					List<Modules> modLst = radioJson.getModulesLst();
					if (modLst!=null && modLst.size()!=0)
					{
						for (Modules m:modLst)
						{
							if (m.getChannel().equalsIgnoreCase("auto"))
							{
								List<Integer> radLst = deviceIdRadioMap.get(m.getDevice_id());
								if (radLst==null)
									radLst = new ArrayList<Integer>();							
								radLst.add(m.getModule_id());
								deviceIdRadioMap.put(m.getDevice_id(), radLst);
							}
						}
					}
				}
				
				List<Devices> devLst = deviceDAO.getDevicesList(param_netId);
				for (Devices dev : devLst)
				{
					List<Integer> modIdLst = deviceIdRadioMap.get(dev.getId());
					if (modIdLst!=null && modIdLst.size()!=0)
					{
						/* fetch update command for each device */
						ACService.<List<Integer>> fetchCommand(MessageType.PIPE_INFO_TYPE_UPDATE_AUTO_CHANNEL, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn(), modIdLst);
					}
				}
			} catch (Exception e) {
				log.error("transaction is rollback - " + e, e);
				throw e;
			}

			try {				
				response.setTimestamp(new Date());
				response.setData(radioJson);
				response.setResp_code(ResponseCode.SUCCESS);
			} catch (Exception e) {
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
			}
		} catch (Exception e) {
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}

		return JsonUtils.toJson(response);
	}
}
