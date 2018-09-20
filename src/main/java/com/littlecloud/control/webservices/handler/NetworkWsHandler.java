package com.littlecloud.control.webservices.handler;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang.RandomStringUtils;
import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.DebugManager;
import com.littlecloud.ac.json.model.Json_StationBandwidthListRequest;
import com.littlecloud.ac.json.model.Json_StationBandwidthListRequest.SBLRequestMac;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.messagehandler.GPSMessageHandler;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.ConfigurationPepvpnCertificatesDAO;
import com.littlecloud.control.dao.ConfigurationPepvpnsDAO;
import com.littlecloud.control.dao.ConfigurationRadioChannelsDAO;
import com.littlecloud.control.dao.ConfigurationSsidsDAO;
import com.littlecloud.control.dao.DailyClientSsidUsagesDAO;
import com.littlecloud.control.dao.DailyClientUsagesDAO;
import com.littlecloud.control.dao.DailyDeviceSsidUsagesDAO;
import com.littlecloud.control.dao.DailyDeviceUsagesDAO;
import com.littlecloud.control.dao.DeviceConfigurationsDAO;
import com.littlecloud.control.dao.DeviceFeaturesDAO;
import com.littlecloud.control.dao.DeviceGpsLocationsDAO;
import com.littlecloud.control.dao.DeviceGpsRecordsDAO;
import com.littlecloud.control.dao.DeviceUpdatesDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.DevicesxtagsDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.dao.OrganizationsDAO;
import com.littlecloud.control.dao.TagsDAO;
import com.littlecloud.control.dao.branch.ConfigUpdatesDAO;
import com.littlecloud.control.dao.branch.DeviceFirmwareSchedulesDAO;
import com.littlecloud.control.dao.branch.OuiInfosDAO;
import com.littlecloud.control.dao.branch.ProductsDAO;
import com.littlecloud.control.entity.ConfigurationPepvpnCertificates;
import com.littlecloud.control.entity.ConfigurationPepvpnCertificatesId;
import com.littlecloud.control.entity.ConfigurationPepvpns;
import com.littlecloud.control.entity.ConfigurationSsids;
import com.littlecloud.control.entity.DeviceConfigurations;
import com.littlecloud.control.entity.DeviceFeatures;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Devices.ONLINE_STATUS;
import com.littlecloud.control.entity.Devicesxtags;
import com.littlecloud.control.entity.DevicesxtagsId;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.Organizations;
import com.littlecloud.control.entity.Tags;
import com.littlecloud.control.entity.branch.ConfigUpdates;
import com.littlecloud.control.entity.branch.ConfigUpdatesId;
import com.littlecloud.control.entity.branch.DeviceFirmwareSchedules;
import com.littlecloud.control.entity.branch.Products;
import com.littlecloud.control.entity.report.DeviceGpsLocations;
import com.littlecloud.control.entity.report.DeviceGpsLocationsId;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.model.Json_Client_Usage;
import com.littlecloud.control.json.model.Json_Clients;
import com.littlecloud.control.json.model.Json_Device_Gps_Info;
import com.littlecloud.control.json.model.Json_Device_Locations;
import com.littlecloud.control.json.model.Json_Device_Usage;
import com.littlecloud.control.json.model.Json_Devices;
import com.littlecloud.control.json.model.Json_Mac_List;
import com.littlecloud.control.json.model.Json_Manufacturer_Usage;
import com.littlecloud.control.json.model.Json_MasterDevice;
import com.littlecloud.control.json.model.Json_Network_Time;
import com.littlecloud.control.json.model.Json_Networks;
import com.littlecloud.control.json.model.Json_PepvpnHubs;
import com.littlecloud.control.json.model.Json_PepvpnInfo;
import com.littlecloud.control.json.model.Json_PepvpnPeerDetail;
import com.littlecloud.control.json.model.Json_PepvpnTunnelStat;
import com.littlecloud.control.json.model.Json_SSID_Usage;
import com.littlecloud.control.json.model.Json_Tags;
import com.littlecloud.control.json.model.Json_Usage_Count;
import com.littlecloud.control.json.model.config.JsonConf_PepvpnProfiles;
import com.littlecloud.control.json.model.config.JsonConf_PepvpnProfilesNew;
import com.littlecloud.control.json.model.config.util.CommonConfigUtils;
import com.littlecloud.control.json.model.config.util.ConfigBackupUtils;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerDeviceTask;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerDeviceTask.CONFIG_UPDATE_REASON;
import com.littlecloud.control.json.model.config.util.ConfigurationSettingsUtils;
import com.littlecloud.control.json.model.config.util.PepvpnConfigUtils;
import com.littlecloud.control.json.model.config.util.PepvpnStatusUtils;
import com.littlecloud.control.json.model.config.util.RadioConfigUtils;
import com.littlecloud.control.json.model.pepvpn.PeerDetail;
import com.littlecloud.control.json.model.pepvpn.PeerDetailResponse;
import com.littlecloud.control.json.model.pepvpn.Peer_Networks;
import com.littlecloud.control.json.model.pepvpn.PepvpnQueryCommand;
import com.littlecloud.control.json.model.pepvpn.VpnGroupInfo;
import com.littlecloud.control.json.request.JsonNetworkRequest;
import com.littlecloud.control.json.request.JsonNetworkRequest_PepvpnProfile;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonMatcherUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.util.ComparatorSsidUsageUtils;
import com.littlecloud.control.webservices.util.NetworkUtils;
import com.littlecloud.pool.model.DailyClientSsidUsageResult;
import com.littlecloud.pool.model.DailyClientUsageResult;
import com.littlecloud.pool.model.DailyDevSsidUsageResult;
import com.littlecloud.pool.model.DailyDevUsageResult;
import com.littlecloud.pool.object.ConfigGetObject;
import com.littlecloud.pool.object.DevBandwidthObject;
import com.littlecloud.pool.object.DevBandwidthObject.BandwidthList;
import com.littlecloud.pool.object.DevDetailObject;
import com.littlecloud.pool.object.DevLocationsObject;
import com.littlecloud.pool.object.DevLocationsReportObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.FeatureGetObject;
import com.littlecloud.pool.object.LocationList;
import com.littlecloud.pool.object.PepvpnPeerDetailObject;
import com.littlecloud.pool.object.PepvpnTunnelStatObject;
import com.littlecloud.pool.object.PoolObjectDAO;
import com.littlecloud.pool.object.StationBandwidthListObject;
import com.littlecloud.pool.object.StationBandwidthListObject.StationStatusList;
import com.littlecloud.pool.object.StationList;
import com.littlecloud.pool.object.StationListObject;
import com.littlecloud.pool.object.StationZObject;
import com.littlecloud.pool.object.StationZObject.StationMacList;
import com.littlecloud.pool.object.utils.BranchUtils;
import com.littlecloud.pool.object.utils.ConfigSettingsUtils;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.littlecloud.pool.object.utils.FeatureGetUtils;
import com.littlecloud.pool.object.utils.FeatureGetUtils.ATTRIBUTE;
import com.littlecloud.pool.object.utils.LocationUtils;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;
import com.littlecloud.pool.object.utils.ProductUtils;
import com.littlecloud.pool.utils.MD5Manager;
import com.littlecloud.pool.utils.Utils;
import com.littlecloud.services.NetworksMgr;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.util.DBUtil;

/* Note: Method must be static to avoid NullPointerException from invoke which calls static method */
public class NetworkWsHandler
{
	private static final int GROUP_TINY = 10;
	private static final int GROUP_SMALL = 30;
	private static final int GROUP_MEDIUM = 60;
	private static final int GROUP_LARGE = 150;
	private static final int GROUP_HUGE = 500;
	private static final int RESEND_TIME = 5000; // 5sec
	private static final int TOP_N_CLIENTS = 500; // top 500 clients
	private final static int ASK_DEVICE_GET_CONFIG_RETRY = 8;
	private final static int ASK_DEVICE_GET_CONFIG_WAIT_TIME = 3000;
	private static final int MAX_CACHED_TIME = 5; // 15 sec
	private static boolean PROD_MODE = DebugManager.isPROD_MODE();
	
	private static final Logger log = Logger.getLogger(NetworkWsHandler.class);

	public enum WIFICFG 
	{
		device, network, not_managed, none
	}
	// private static ? generateRadioFullConfig(int param_networkId, List<Integer> devIdList, List<Integer> modIdList)
	// {
	// JsonConf_RadioSettings radioJson = JsonConf_RadioSettings.generateDefaultInstance(param_networkId, devIdList,
	// modIdList);
	// return radioJson;
	// }

	public static String getDevicesTags(JsonNetworkRequest request, JsonResponse<List<Json_Tags>> response)
	{
		boolean bReadOnly = true;

		/* get param */
		String param_orgId = request.getOrganization_id();
		int param_networkId = request.getNetwork_id();
		List<Integer> param_devIdList = request.getDevice_ids();
		Boolean isSsidProfile = request.getIsSsidProfile();
		
//		System.out.println("**** param_orgId : " + param_orgId + " & param_networkId : " + param_networkId + " & isSsidProfile : " + isSsidProfile);
//		java.util.Iterator iter = param_devIdList.iterator();
//		while (iter.hasNext()){
//			System.out.println("**** Device ID : " + iter.next());	
//		}

		/* save */
		
		try {
			TagsDAO tagsDAO = new TagsDAO(param_orgId, bReadOnly);		
			DevicesDAO deviceDAO = new DevicesDAO(param_orgId, bReadOnly);
			ConfigurationSsidsDAO confSsidDAO = new ConfigurationSsidsDAO(param_orgId, bReadOnly);

			List<Tags> tagsList = new ArrayList<Tags>();
			if (param_devIdList == null || param_devIdList.size() == 0)
			{
				log.warn("device di is null");
				tagsList = tagsDAO.getAllTagsFromNetwork(param_networkId);
			}
			else
			{
				/* get network devices Ids */
				param_devIdList = deviceDAO.getDevicesIdList(param_networkId);
				tagsList = tagsDAO.getAllTagsWithDeviceIdList(param_devIdList);
			}

			List<Json_Tags> tagsJsonList = new ArrayList<Json_Tags>();
			HashMap<Long,Json_Tags> tagsJsonMap = new HashMap<Long,Json_Tags>();
			for (Tags tags : tagsList) {
				Json_Tags tagsJson = new Json_Tags().parseTags(tags);

				/* get ssid availability for each tags */
				if (isSsidProfile != null && isSsidProfile)
				{
					log.debug("get ssidProfile for tags");
					List<ConfigurationSsids> confSsidLst = confSsidDAO.getAllObjectsWithTagName(tags.getName());
					
					java.util.Iterator iter2 = confSsidLst.iterator();
//					while (iter2.hasNext()){
//						System.out.println("**** ConfigurationSsids: " + ((ConfigurationSsids) iter2.next()).getSsid());
//					}
					
					
					if (confSsidLst != null)
					{
						List<Integer> ssidProfilesIdLst = new ArrayList<Integer>();
						for (ConfigurationSsids confSsid : confSsidLst)
						{
							ssidProfilesIdLst.add(confSsid.getId().getSsidId());
						}
						if (ssidProfilesIdLst.size() != 0)
							tagsJson.setSsidProfilesIdLst(ssidProfilesIdLst);
					}
				}

				tagsJsonMap.put(tagsJson.getId(),tagsJson);
			}
			for(Long id : tagsJsonMap.keySet())
			{
				tagsJsonList.add(tagsJsonMap.get(id));
			}
			response.setData(tagsJsonList);
			
//			java.util.Iterator iter3 = tagsJsonList.iterator();
//			while (iter3.hasNext()){
//				System.out.println("**** tagsJsonList: " + ((Json_Tags) iter3.next()).getTag_name());
//			}

			response.setResp_code(ResponseCode.SUCCESS);
		} catch (Exception e)
		{
			log.error("getDevicesTags - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		log.debugf("getDevicesTags response = %s", JsonUtils.toJson(response));
		return JsonUtils.toJson(response);
	}

	public static String addDevices(JsonNetworkRequest request, JsonResponse response) throws SQLException
	{
		if (log.isDebugEnabled())
			log.debugf("addDevices - request %s", request);
		
		/* api server added the records to database, backend to reload cache for this device */		
//		List<String> param_snList = request.getDevice_sns();
		Integer param_networkId = request.getNetwork_id();
		String param_orgId = request.getOrganization_id();
//		Integer param_iana_id = request.getIana_id();
//		List<JsonNetworkDevice> param_networkDeviceList = request.getNetwork_device_list();
		List<Json_Devices> param_networkDeviceList = request.getNetwork_device_list();
		
		DBUtil dbUtil = null;		
		DBConnection tranConn = null;
		ConfigurationSettingsUtils settingsUtils = null;
		try
		{
			
			dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
			tranConn = dbUtil.getConnection(false, param_orgId, false);
			tranConn.setBatchMode();
			
			ConfigUpdatesDAO confUpDAO = new ConfigUpdatesDAO(false);
			ConfigurationPepvpnsDAO configurationPepvpnsDAO = new ConfigurationPepvpnsDAO(param_orgId);
			DeviceUpdatesDAO deviceUpdateDAO = new DeviceUpdatesDAO(param_orgId);
			List<Integer>hub_list = new ArrayList<Integer>();
			hub_list = configurationPepvpnsDAO.getHubandHahubDeviceIdFromNetworkId(param_networkId, true);
			DevicesDAO devicesDAO = new DevicesDAO(param_orgId);

			if (param_networkDeviceList == null || param_networkDeviceList.size() == 0)
			{			
				response.setMessage("empty device list");
				response.setResp_code(ResponseCode.SUCCESS);
				JsonUtils.toJson(response);
			}
			
			NetworkUtils.updateNetworksFeature(param_orgId, param_networkId);
			
			if(hub_list !=null && hub_list.size() > 0)
			{
				log.warnf("AD10001 - INFO hubidlst:"+hub_list);
				//deviceUpdateDAO.incrementConfUpdateForDevLst(hub_list, response.getServer_ref(), "add device hub change");
				new ConfigUpdatePerDeviceTask(param_orgId, param_networkId).performConfigUpdateNow(hub_list, response.getServer_ref(), 
						CONFIG_UPDATE_REASON.add_dev_hub_change.toString());
			}
			
			Devices dev = null;
			for( Json_Devices networkDevice : param_networkDeviceList )
			{
				if (networkDevice.getIana_id() != null && networkDevice.getSn() != null && !networkDevice.getSn().isEmpty()){
					dev = devicesDAO.findBySn(networkDevice.getIana_id(), networkDevice.getSn());
	
					if(dev != null)
					{

						NetUtils.updateNetworkToCache(dev, param_orgId, null, param_networkId);
						settingsUtils = new ConfigurationSettingsUtils(param_orgId, param_networkId, dev.getId());
						settingsUtils.initDevConfigSettings();
						//RadioConfigUtils.initDevConfigSettings(dev, param_orgId);
					}
					else
					{
						log.warnf("addDevices - dev %d %s is not found in db!");
					}
				} else {
					log.warnf("addDevices - param_networkDeviceList's element(s) contain(s) null values(iana_id/ sn)!");
				}
			}
			
			for( Json_Devices networkDevice : param_networkDeviceList  )
			{
				
				if (networkDevice.getIana_id() != null && networkDevice.getSn() != null && !networkDevice.getSn().isEmpty()){
					dev = devicesDAO.findBySn(networkDevice.getIana_id(), networkDevice.getSn());
					if(dev != null)
					{
						NetUtils.saveOrUpdateDevices(param_orgId, param_networkId, dev);
						BranchUtils.saveOrUpdateSnsOrganizationsByIanaIdSn(dev.getIanaId(), dev.getSn(), param_orgId);
					}
					else
					{
						log.errorf("Given dev %d %s is not found!!", networkDevice.getIana_id(), networkDevice.getSn());
					}
				} 
			}
			
			response.setResp_code(ResponseCode.SUCCESS);
			
			if (tranConn!=null && tranConn.isBatchMode())
			{
				tranConn.commit();
			}
						
			for( Json_Devices networkDevice : param_networkDeviceList  )
			{
				
				if (networkDevice.getIana_id() != null && networkDevice.getSn() != null && !networkDevice.getSn().isEmpty()){
					/* remove clear config */
					ConfigUpdates confUp = confUpDAO.findById(new ConfigUpdatesId(networkDevice.getIana_id(), networkDevice.getSn()));
					if (confUp!=null)
						confUpDAO.delete(confUp);
					
					ACService.fetchBroadcast(MessageType.PIPE_INFO_TYPE_DEV_ONLINE, request.getCaller_ref() + request.getServer_ref(), networkDevice.getIana_id(), networkDevice.getSn());
				} 
			}			
		}
		catch (Exception e)
		{
			log.error("AD10001 - add devices exception - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		finally {
			/* clear to reload all */
			//BranchUtils.removeBranchInfoObject();
			
			if (dbUtil!=null && dbUtil.isSessionStarted()) {
				dbUtil.endSession();
			}
		}
		
		log.debug(param_networkDeviceList);

		return JsonUtils.toJson(response);
	}

	public static String addDevicesTags(JsonNetworkRequest request, JsonResponse<List<Json_Tags>> response)
	{
		response.setResp_code(ResponseCode.SUCCESS);

		//int param_networkId = request.getNetwork_id();
		String param_orgId = request.getOrganization_id();
		List<String> param_tagNamesList = request.getTag_names();
		List<Integer> param_devIdList = request.getDevice_ids();

		try {
			TagsDAO tagDAO = new TagsDAO(param_orgId);
			DevicesDAO deviceDAO = new DevicesDAO(param_orgId);
			NetworksDAO networkDAO = new NetworksDAO(param_orgId);
			DevicesxtagsDAO devicextagsDAO = new DevicesxtagsDAO(param_orgId);
			//DeviceUpdatesDAO deviceUpdateDAO = new DeviceUpdatesDAO(param_orgId);
			Networks net = null;
			
			//Networks network = networkDAO.findById(param_networkId);
			for (int i = 0; i < param_tagNamesList.size(); i++)
			{
				Tags newTags; // = new Tags();
				newTags = tagDAO.findByName(param_tagNamesList.get(i));

				if (newTags == null)
				{
					newTags = new Tags();
				}

				newTags.setName(param_tagNamesList.get(i));
				tagDAO.saveOrUpdate(newTags);
				// newTags.setNetworks(network);

				Set<Devices> devSet = new HashSet<Devices>();
				for (int id : param_devIdList)
				{
					log.info(">>> id = " + id);
					Devices dev = deviceDAO.findById(id);
					
					if (dev==null)
					{
						log.warnf("devId %d of orgId %s does not exist", id, param_orgId);
						continue;
					}
					
					
//					dev.getTagses().add(newTags);
//					// devSet.add(dev);
//					deviceDAO.saveOrUpdate(dev);
					
					DevicesxtagsId devicextagsId = new DevicesxtagsId(newTags.getId(), dev.getId());
					Devicesxtags devicextags = new Devicesxtags(devicextagsId);
					devicextagsDAO.saveOrUpdate(devicextags);
					
					net = networkDAO.findById(dev.getNetworkId());					
					if (net!=null && !NetworkUtils.isMasterGroupConfigEnabledNetwork(net))
					{
						//deviceUpdateDAO.incrementConfUpdateForDevLstIfNoUpdate(param_devIdList, response.getServer_ref(), "add device tag");
						new ConfigUpdatePerDeviceTask(param_orgId, net.getId()).performConfigUpdateNowIfNoUpdate(param_devIdList, response.getServer_ref(), 
								CONFIG_UPDATE_REASON.add_dev_tags.toString());
					}
					else
					{
						log.warnf("netId %d is not found or master config net %s for orgId %s", dev.getNetworkId(), net, param_orgId);
					}
				}
				// newTags.setDeviceses(devSet);
				// tagDAO.persist(newTags);
			}

//			if (network!=null && network.getMasterDeviceId()==null)
//			{
//				//deviceUpdateDAO.incrementConfUpdateForDevLstIfNoUpdate(param_devIdList, response.getServer_ref(), "add device tag");
//				new ConfigUpdatePerDeviceTask(param_orgId, param_networkId).performConfigUpdateNowIfNoUpdate(param_devIdList, response.getServer_ref(), 
//						CONFIG_UPDATE_REASON.add_dev_tags.toString());
//			}
			
		} catch (Exception e)
		{
			log.error("addDevicesTags - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}

		return JsonUtils.toJson(response);
	}

	public static String removeDevicesTags(JsonNetworkRequest request, JsonResponse<List<Json_Tags>> response)
	{
		/* get param */
		String param_orgId = request.getOrganization_id();
		int param_networkId = request.getNetwork_id();
		List<Integer> param_devIdList = request.getDevice_ids();
		List<String> param_tagNameList = request.getTag_names();

		/* remove */
		response.setResp_code(ResponseCode.SUCCESS);
		try {
			DevicesDAO devDAO = new DevicesDAO(param_orgId);
			TagsDAO tagsDAO = new TagsDAO(param_orgId);
			DevicesxtagsDAO devXtagDAO = new DevicesxtagsDAO(param_orgId); 
			DeviceUpdatesDAO deviceUpdateDAO = new DeviceUpdatesDAO(param_orgId);
			ConfigurationRadioChannelsDAO  confiRadioDAO= new ConfigurationRadioChannelsDAO(param_orgId);
			NetworksDAO netDAO = new NetworksDAO(param_orgId);
			Networks net = null;
			Devices dev = null;
						
			if (param_devIdList.size() == 0)
			{
				log.debug("device id list is empty, remove tags");
				net = netDAO.findById(param_networkId);
				
				tagsDAO.removeTagsWithNameList(param_tagNameList, param_networkId);
				if (net!=null && !NetworkUtils.isMasterGroupConfigEnabledNetwork(net))
				{
					/* when remove tag with device list, sync all network dev if not master mode */
					//deviceUpdateDAO.incrementConfUpdateForNetwork(param_networkId, response.getServer_ref(), "remove device tag");
					new ConfigUpdatePerDeviceTask(param_orgId, param_networkId).performConfigUpdateNowIfNoUpdate(param_devIdList, response.getServer_ref(), 
							CONFIG_UPDATE_REASON.remove_dev_tags.toString());
				}
			}
			else
			{
				if(log.isDebugEnabled())
					log.debug("remove tag association with the device Id list");
				for (int devId: param_devIdList)
				{	
					dev = devDAO.findById(devId);
					if (dev==null)
					{
						log.warnf("devId %d for orgId %s is not found!", devId, param_orgId);
						continue;
					}
					
//					Devices dev = devDAO.findById(devId);
//					for (String tagName: param_tagNameList)
//					{
//						Tags tag = tagsDAO.findByName(tagName);
//						dev.getTagses().remove(tag);
//					}
//					devDAO.saveOrUpdate(dev);
					if(log.isDebugEnabled())
						log.debug("devId="+devId);

					for (String tagName: param_tagNameList) {
						if(log.isDebugEnabled())
							log.debug("tagName="+tagName);
						Tags tag = tagsDAO.findByName(tagName);
						if(log.isDebugEnabled())
							log.debug("tag="+tag);
						DevicesxtagsId devicextagsId = new DevicesxtagsId(tag.getId(), devId);
						if(log.isDebugEnabled())
							log.debug("devicextagsId="+devicextagsId);
						Devicesxtags devicextags = new Devicesxtags(devicextagsId);
						if(log.isDebugEnabled())
							log.debug("devicextags="+devicextags);
						devXtagDAO.delete(devicextags);
					}
					//confiRadioDAO.removeRadioChannel(devId);
					
					net = netDAO.findById(dev.getNetworkId());
					if (net!=null && !NetworkUtils.isMasterGroupConfigEnabledNetwork(net))
					{
						//deviceUpdateDAO.incrementConfUpdateForDevLstIfNoUpdate(param_devIdList, response.getServer_ref(), "remove device tag");
						new ConfigUpdatePerDeviceTask(param_orgId, net.getId()).performConfigUpdateNowIfNoUpdate(param_devIdList, response.getServer_ref(), 
								CONFIG_UPDATE_REASON.remove_dev_tags.toString());
					}
					else
					{
						log.warnf("netId %d is not found or master config net %s for orgId %s", dev.getNetworkId(), net, param_orgId);
					}
				}				
			}

		} catch (Exception e)
		{
			log.error("removeDevicesTags - " + e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}

		return JsonUtils.toJson(response);
	}
		
	public static String getDevices(JsonNetworkRequest request, JsonResponse<List<Json_Devices>> response)
	{
		int successTotal = 0;
		int successCount = 0;

		long startTime = new Date().getTime();
		int duration = 0;
		int interval = 0;
		
		int param_networkId = request.getNetwork_id();
		String param_orgId = request.getOrganization_id();
		List<Integer> devIdLst = request.getDevice_ids();
		boolean param_bhasStatus = request.isHas_status();
		
		Date startGetProduct = new Date();
		Date endGetProduct = new Date();
		Date startGetNetworks = new Date();
		Date endGetNetworks = new Date();

		DBUtil dbutil = null;
		
		try
		{
			dbutil = DBUtil.getInstance();
			dbutil.startSession();
		}
		catch(Exception e)
		{
			log.error("Get devices start session error - " + e,e);
			if( dbutil != null && dbutil.isSessionStarted() )
				dbutil.endSession();
		}
		
		try {
			List<Devices> devList = null;
			List<Json_Devices> devJsonList = new ArrayList<Json_Devices>();
			DeviceFirmwareSchedulesDAO deviceFwScheDAO = new DeviceFirmwareSchedulesDAO(true);
			ConfigurationSsidsDAO configurationSsidsDAO = new ConfigurationSsidsDAO(param_orgId);
			
			startGetNetworks = new Date();
			if(log.isDebugEnabled())
				log.debugf("ellapse time 0 =%d", new Date().getTime() - startTime);
			if(param_networkId ==0)
			{
				devList = new ArrayList<Devices>();
				List<Networks> networks = OrgInfoUtils.getNetworkLst(param_orgId);
				for( Networks net : networks )
				{
					devList.addAll(NetUtils.getDeviceLstByNetId(param_orgId, net.getId()));
				}
				if(log.isDebugEnabled())
					log.debugf("ellapse time 0A =%d", new Date().getTime() - startTime);
			}
			else
			{
				if (devIdLst != null && devIdLst.size() != 0)
				{
					devList = NetUtils.getDeviceLstByDevId(param_orgId, param_networkId, devIdLst);
				}
				else
				{
					devList = NetUtils.getDeviceLstByNetId(param_orgId, param_networkId);
				}
				if(log.isDebugEnabled())
					log.debugf("ellapse time 0B =%d", new Date().getTime() - startTime);
			}
			
			//devList = new ArrayList<Devices>(devList);
			if(log.isDebugEnabled())
				log.debug("before sort, orgId="+param_orgId+", devList="+devList);
			
			if(devList !=null)
			{
				Collections.sort(devList, new Comparator<Devices>() {
			        public int compare(Devices d1, Devices d2) {
			        				        	
			        	if (d1.getOnline_status() !=1  && d2.getOnline_status() ==1)
			        	{
		        			return 1;
			        	}
			        	else if (d1.getOnline_status() == 1 && d2.getOnline_status() !=1)
			        	{
			        		return -1;
			        	}
			        	
			        	if (d1.getName() != null && d2.getName() != null)
	        				return d1.getName().compareTo(d2.getName());
			        	else if (d1.getName() != null)
			        		return -1;
			        	else if (d2.getName() != null)
			        		return 1;
			        	
						return 0;
			        }
			    });
			}
			if(log.isDebugEnabled())
				log.debug("after sort, orgId="+param_orgId+", devList="+devList);
			
			endGetNetworks = new Date();
			if(devList!=null)
				successTotal = devList.size();
			
			if(successTotal <= GROUP_TINY)
			{
				duration = 30;	// let it auto refresh for 30s
				interval = 3;
			}
			else if (successTotal <= GROUP_SMALL)
			{
				duration = 30;	// let it auto refresh for 30s
				interval = 6;
			}
			else if (successTotal <= GROUP_MEDIUM)
			{
				duration = 30;	// let it auto refresh for 30s
				interval = 15;
			}
			else if (successTotal <= GROUP_LARGE)
			{
				duration = 30;	// one shot only
				interval = 30;
			}
			else if (successTotal <= GROUP_HUGE)
			{
				duration = 60;	// one shot only
				interval = 60;
			}
			else // YTLC only?
			{
				duration = 300;	// one shot only
				interval = 300;
			}
				
			
			int counter = 0;
//			log.debug("successTotal="+successTotal);
//			log.infof("ellapse time devList =%d", new Date().getTime() - startTime);
			
			HashMap<String, DeviceFirmwareSchedules> schedules = deviceFwScheDAO.getAllSchedules(param_orgId, param_networkId);
			HashMap<Integer, String> wifiManagedMap = null;
			if(param_networkId != 0)
				wifiManagedMap = RadioConfigUtils.getDevicesWifiManaged(param_orgId, param_networkId);
			
			if( wifiManagedMap != null )
			{
				if(log.isInfoEnabled())
					log.info("The wifi manage map : " + wifiManagedMap);
			}
			
			for (Devices dev : devList)
			{				
				if (dev.isActive())
				{

					counter++;
					
//					Networks net = networkDAO.findById(dev.getNetworkId());

					Networks net = OrgInfoUtils.getNetwork(param_orgId, dev.getNetworkId());
					
					Json_Devices devJson = new Json_Devices();
					devJson.parseDevices(dev, param_orgId);
					devJson.setFw_ver(dev.getFw_ver());
//					devJson.setDev_level_cfg(dev.getDev_level_cfg());
					devJson.setNetwork_id(dev.getNetworkId());
					devJson.setNetwork_name(net.getName());
					
					devJson.setAddress(dev.getAddress());
					
					if (dev.getLast_sync_date() != null){
						
						//devJson.setLast_sync_date(DateUtils.convertUnixtime2Date(dev.getLast_sync_date()));
						devJson.setLast_sync_date(DateUtils.offsetFromUtcTimeZoneId(new Date(Long.parseLong(dev.getLast_sync_date()+"000")), net.getTimezone()));
					}
					
					if (schedules!=null)
					{
						DeviceFirmwareSchedules fwSche = schedules.get(dev.getIanaId()+"\\|"+dev.getSn());
						if( fwSche != null )
						{
							Date network_time = DateUtils.offsetFromUtcTimeZone(DateUtils.getUtcDate(), net.getTimezone());
							devJson.setFw_pending_ver(fwSche.getFw_version());
							devJson.setFw_pending_network_time(network_time);
							if( fwSche.getSchedule_time() != null )
							{
								Long schedule_time = Long.parseLong(fwSche.getSchedule_time()+"000");
								devJson.setFw_pending_schedule_time(DateUtils.offsetFromUtcTimeZoneId(new Date(schedule_time), net.getTimezone()));
							}
							
							if( fwSche.getUpgrade_time() != null )
							{
								Long upgrade_time = Long.parseLong(fwSche.getUpgrade_time()+"000");
								devJson.setFw_pending_upgrade_time(DateUtils.offsetFromUtcTimeZoneId(new Date(upgrade_time), net.getTimezone()));
							}
							devJson.setFw_pending_status(fwSche.getStatus());
							
							if (devJson.getFw_pending_status()==3)
								devJson.setFw_pending_status(0);	// until front end handle state 3
						}
					}
					
//					Integer masterDeviceId = networkDAO.findById(dev.getNetworkId()).getMasterDeviceId();
					Integer masterDeviceId = OrgInfoUtils.getNetwork(param_orgId, dev.getNetworkId()).getMasterDeviceId();
					if (masterDeviceId != null){
						if (dev.getId().equals(masterDeviceId) ){
							devJson.setIs_master_device(true);
						}else{
							devJson.setIs_master_device(false);
						}
							
					}
					
					if (dev.getProductId() != null)
					{
						Products product = ProductUtils.getProducts(dev.getProductId());
						if (product != null)
						{
							devJson.setProduct_name(product.getName());
							devJson.setNetwork_type(product.getNetworkType());
							devJson.setDevice_type(product.getDeviceType());
						}
						else
						{
							log.warn("ProductId " + dev.getProductId() + " from org " + param_orgId + " not found!");
						}
					}
					else
					{
						log.warn("Devices with sn " + dev.getSn() + " does not have product id");
					}
					
					/* lookup model_name from mv */
					/*
					 * Comment for test
					 */
					if(wifiManagedMap != null)
						devJson.setWifi_cfg(wifiManagedMap.get(dev.getId()));
					
					/* check online status */
					DevOnlineObject devOnline = PoolObjectDAO.getDevOnlineObject(dev);
					// log.debug("QQQ 6");
					if (devOnline != null)
					{
//						NetworksDAO netDAO = new NetworksDAO(param_orgId, bReadOnlyDb); // duplication declaration?!!
//						Networks net = netDAO.getNetworksByDevId(dev.getId());
						
						//devJson.setFw_ver(devOnline.getFw_ver());
						if(log.isDebugEnabled())
							log.debug("devOnline object found for sn " + dev.getSn());
						if (devOnline.isOnline())
							devJson.setStatus("online");
						else
							devJson.setStatus("offline");
					}
					else
					{
						if (devOnline==null && DeviceUtils.getDevOnlineStatus(dev)==ONLINE_STATUS.ONLINE)
							devJson.setStatus("online");
						
						// log.info("devOnline object is not found!");
						successCount++;
					}
					/* convert utc date to local time */
					if (dev.getOfflineAt() != null)
						devJson.setOffline_at(DateUtils.offsetFromUtcTimeZoneId(dev.getOfflineAt(), net.getTimezone()));
					if (dev.getLastOnline() != null)
						devJson.setLast_online(DateUtils.offsetFromUtcTimeZoneId(dev.getLastOnline(), net.getTimezone()));
					
					devJson.setExpired(false);
					
					Date expire_date = dev.getExpiryDate();
					devJson.setExpiry_date(expire_date);
					if( expire_date != null )
					{
						Calendar excal = Calendar.getInstance();
						excal.setTime(expire_date);
						Calendar cal = Calendar.getInstance();
						if( excal.get(Calendar.YEAR) < cal.get(Calendar.YEAR) )
						{
							devJson.setExpired(true);
						}
						else if( excal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) )
						{
							if( excal.get(Calendar.MONTH) < cal.get(Calendar.MONTH) )
							{
								devJson.setExpired(true);
							}
							else if( excal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) )
							{
								if( excal.get(Calendar.DAY_OF_MONTH) < cal.get(Calendar.DAY_OF_MONTH) )
								{
									devJson.setExpired(true);
								}
							}
						}
					}
					else
					{
						devJson.setExpired(true);
					}
					
					Date sub_expiry_date = dev.getSubExpiryDate();
					if(sub_expiry_date != null)
					{
						devJson.setSub_expiry_date(sub_expiry_date);
						Date currentDate = DateUtils.getUtcDate();
						if( currentDate.after(sub_expiry_date) )
							devJson.setExpiredSub(true);
						else
							devJson.setExpiredSub(false);
					}
					else
					{
						devJson.setExpiredSub(true);
					}
					
					StationListObject stationListObjectFromCache = new StationListObject();
					stationListObjectFromCache.setSn(dev.getSn());
					stationListObjectFromCache.setIana_id(dev.getIanaId());
					stationListObjectFromCache = ACUtil.getPoolObjectBySn(stationListObjectFromCache, StationListObject.class);
					if (stationListObjectFromCache != null){
						devJson.setClientz(stationListObjectFromCache.getTotalOnlineClient()==null?0:stationListObjectFromCache.getTotalOnlineClient());
					}
					else
					{
						devJson.setClientz(0);
					}
					
					if (param_bhasStatus)
					{
						
						DevBandwidthObject objExample = new DevBandwidthObject();
						objExample.setIana_id(dev.getIanaId());
						objExample.setSn(dev.getSn());
	
						DevBandwidthObject devBandwidthObject = ACUtil.<DevBandwidthObject> getPoolObjectBySn(objExample, DevBandwidthObject.class);
						if (devBandwidthObject != null)
						{
							int drx = 0;
							int dtx = 0;
							long tnow = System.currentTimeMillis();
	
							devBandwidthObject.setInterval(interval);	// set the getBandwidth interval
							devBandwidthObject.setDuration(duration);
							devBandwidthObject.setSid(request.getCaller_ref() + request.getServer_ref());
							
							if( devBandwidthObject.isFromWTPOrNot() == false )
							{
								if( devBandwidthObject.getCreateTime() == null || ( tnow - devBandwidthObject.getCreateTime() >= RESEND_TIME ))
								{
									ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_BANDWIDTH, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn(), duration, interval);
									ACUtil.cachePoolObjectBySn(devBandwidthObject, DevBandwidthObject.class);
								}
							}
							else
							{
								List<BandwidthList> bandWidthList = devBandwidthObject.getBandwidth_list();
								for (BandwidthList band : bandWidthList)
								{
									dtx += band.getDtx();
									drx += band.getDrx();
								}
								devJson.setUsage(dtx + drx);
								successCount++;

//								if( ACUtil.isNeedRefresh(devBandwidthObject) )
								if(devBandwidthObject.getDuration() == null)
									devBandwidthObject.setDuration(0);
								
								
								if(tnow - devBandwidthObject.getCreateTime() >= devBandwidthObject.getInterval()*1000) // need to refresh
								{
									ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_BANDWIDTH, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn(), duration, interval);
									ACUtil.cachePoolObjectBySn(devBandwidthObject, DevBandwidthObject.class);
								}
							}
						}
						else
						{
							DevBandwidthObject devBandO = new DevBandwidthObject();
							devBandO.setSid(request.getCaller_ref() + request.getServer_ref());
							devBandO.setIana_id(dev.getIanaId());
							devBandO.setSn(dev.getSn());
							devBandO.setFromWTPOrNot(false);
							devBandO.setInterval(interval);	// set the getBandwidth interval
							devBandO.setDuration(duration);	// set the getBandwidth duration
							//devBandO.setLastUpdateTime(new Date().getTime());
							ACUtil.cachePoolObjectBySn(devBandO, DevBandwidthObject.class);
							response.setResp_code(ResponseCode.PENDING); // pending for incomplete client list
							log.infof("fetching device bandwidth sn=%s, interval=%d, duration=%d", dev.getSn(), interval, duration);
							ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_BANDWIDTH, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn(), duration, interval);
						}
					}
					
					/* get tag */
					devJson.setTags(Json_Devices.getTagNameList(dev, param_orgId));
					
					devJsonList.add(devJson);
					if(log.isInfoEnabled())
						log.info("getDevices: devJson="+devJson);
					if (request.getDevice_count() != null && request.getDevice_count() > 0 && devJsonList.size() >= request.getDevice_count())
						break;
	
					if (counter > 0 && counter % 50 == 0)
					{
	//					 hutil.getOrgSession().flush();
					}
				} // end if (dev.isActive())
				if(log.isDebugEnabled())
					log.debugf("ellapse time sn %s = %d", dev.getSn(), new Date().getTime() - startTime);
			} // end for (Devices dev : devList)

			response.setData(devJsonList);

			if(log.isDebugEnabled())
				log.debugf("ellapse time 1 =%d", new Date().getTime() - startTime);
			if (param_networkId == 0){

				//log.debug("################ DateUtils.getUtcDate : " + DateUtils.getUtcDate());
				response.setNetwork_time(DateUtils.getUtcDate());
				log.infof("ellapse time 1A (OrgInfoUtils.getNetwork)=%d", new Date().getTime() - startTime);
				
			} else {
//				Networks network = networkDAO.findById(param_networkId);
				Networks network = OrgInfoUtils.getNetwork(param_orgId, param_networkId);
				log.infof("ellapse time 1B (OrgInfoUtils.getNetwork)=%d", new Date().getTime() - startTime);
				response.setNetwork_time(DateUtils.offsetFromUtcTimeZoneId(DateUtils.getUtcDate(), network.getTimezone()));
				log.infof("ellapse time 1C (DateUtils.offsetFromUtcTimeZoneId)=%d", new Date().getTime() - startTime);
			}

			if (!param_bhasStatus || successCount == successTotal)
				response.setResp_code(ResponseCode.SUCCESS);
			else
				response.setResp_code(ResponseCode.PENDING);
		} catch (Exception e) {
			log.error("getDevices - " + param_orgId+"_"+param_networkId+"_"+devIdLst, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			
			return JsonUtils.toJson(response);
		}
		finally
		{
			if( dbutil != null && dbutil.isSessionStarted() )
				dbutil.endSession();
		}

		long ellapse = new Date().getTime() - startTime;
		response.setEllapse_time(ellapse);
		log.infof("ellapse time=%d", ellapse);
		String resultStr = JsonUtils.toJson(response);
		log.infof("ellapse time 4 (JsonUtils.toJson)=%d", new Date().getTime() - startTime);
		log.info("Caller ref : "+ request.getCaller_ref() +" Get networks use " + (endGetNetworks.getTime() - startGetNetworks.getTime())/1000 + " s, Get products use " + (endGetProduct.getTime() - startGetProduct.getTime())/1000 +" s, Create json use " + (new Date().getTime() - startTime)/1000 + " s");
		return resultStr;
	}

	
	public static String getPepvpnTunnelStat (JsonNetworkRequest request, JsonResponse<List<Json_PepvpnTunnelStat>> response)
	{
		String param_orgId = request.getOrganization_id();
		Integer param_networkId = request.getNetwork_id();	
		List<Json_Devices> param_device_lst = request.getNetwork_device_list();
		List<Json_PepvpnTunnelStat> pepvpnstatusLst = new ArrayList<Json_PepvpnTunnelStat>();
		
		if (param_device_lst == null)
		{
			log.info("sn list is not provided!");
			response.setResp_code(ResponseCode.MISSING_INPUT);
			return JsonUtils.toJson(response);
		}

		/* search cache for result */
		int foundCache = 0;	
		
		for (Json_Devices json_Devices : param_device_lst)
		{
			Boolean bWillFetch = false;
			String snSearch = json_Devices.getSn();
			PepvpnTunnelStatObject pepvpnStatus = null;
			PepvpnTunnelStatObject searchExample = new PepvpnTunnelStatObject();
			searchExample.setSn(snSearch);			
			searchExample.setIana_id(23695);
			ArrayList<String> tunnel_order = new ArrayList<String>();
			tunnel_order = json_Devices.getPeer_ids();
			
			try {						
				pepvpnStatus = ACUtil.<PepvpnTunnelStatObject> getPoolObjectBySn(searchExample, PepvpnTunnelStatObject.class);
				log.info("getPoolObjectBySn - PepvpnTunnelStatObject"+ pepvpnStatus);
			} catch (Exception e) {						
				log.error("CacheException getPoolObjectBySn -PepvpnTunnelStatObject " + e.getMessage(), e);
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
				response.setError(40007);
				return JsonUtils.toJson(response);
			}

			if (pepvpnStatus == null || ACUtil.isNeedRefresh(pepvpnStatus))
			{
				bWillFetch = true;			
			}
			else
			{				
				Json_PepvpnTunnelStat pepvpnJson = new Json_PepvpnTunnelStat();
				pepvpnStatus.setOrganization_id(param_orgId);
				pepvpnStatus.setNetwork_id(param_networkId);	
				pepvpnJson.parsePepvpnTunnelStat(pepvpnStatus);
				pepvpnstatusLst.add(pepvpnJson);
				foundCache++;	
				
				int cached_time = DateUtils.getUnixtime() - pepvpnStatus.getFetchStartTime();
				if (cached_time > MAX_CACHED_TIME)
				{
					bWillFetch = true;
					
				}
				//log.info("pepvpnStatus.getFetchStartTime() -- bWillFetch = "+bWillFetch+"cached_time= "+cached_time +"--"+DateUtils.getUnixtime()+" - "+pepvpnStatus.getFetchStartTime()+ "MAX_CACHED_TIME = "+MAX_CACHED_TIME);
				ArrayList<String> cached_tunnel = new ArrayList<String>();
				cached_tunnel = pepvpnStatus.getTunnel_order();
				if (cached_tunnel != null){
					for (String tunnel_id : tunnel_order){
						if (!cached_tunnel.contains(tunnel_id)){
							bWillFetch = true;
						}
					}
				} else{
					bWillFetch = true;
				}
			}	
			
			if (bWillFetch)
			{
				Devices devOnlineObject = NetUtils.getDevicesBySn(param_orgId, 23695, snSearch, true);
				if (ONLINE_STATUS.statusOf(devOnlineObject.getOnline_status())==ONLINE_STATUS.ONLINE)
				{					
					FeatureGetObject fgo = FeatureGetUtils.getFeatureGetObject(23695, snSearch);	
					String mvpnStatus = "0";
					if (fgo != null && fgo.getFeatures()!=null) {
						mvpnStatus = FeatureGetUtils.getFeatureAttributeAsStr(fgo, ATTRIBUTE.mvpn_status);
					}
					log.debug("mvpnStatus2 = "+mvpnStatus + "fgo = "+fgo);
					if (mvpnStatus != null && !mvpnStatus.isEmpty() && mvpnStatus.equals("1")) //Firmware version "status": 1  <----- NEW!!
					{
							
							PepvpnQueryCommand qm = new PepvpnQueryCommand();
							qm.setVersion(1);					
							qm.setTunnel(tunnel_order);
							//JSONObject obj = JSONObject.fromObject(qm);
							if (ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_PEPVPN_TUNNEL_STAT, request.getCaller_ref() + request.getServer_ref(), 23695, snSearch, qm) == false)
							{
								log.error("Fail to fetchCommand: PIPE_INFO_TYPE_PEPVPN_TUNNEL_STAT, NewPepvpn snSearch " + snSearch);//JsonUtils.genServerRef()
								response.setResp_code(ResponseCode.INTERNAL_ERROR);
								response.setError(40003);
								return JsonUtils.toJson(response);
							}										
					}
					else
					{
						if (ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_PEPVPN_ENDPOINT, request.getCaller_ref() + request.getServer_ref(), 23695, snSearch) == false)
						{
							log.error("fail to fetchCommand: PIPE_INFO_TYPE_PEPVPN_ENDPOINT, snSearch " + snSearch);
							response.setResp_code(ResponseCode.INTERNAL_ERROR);
							response.setError(40001);
							return JsonUtils.toJson(response);
						}
					}
				}
			}
	}

		if (foundCache != param_device_lst.size())
		{
			String message = foundCache + "/" + param_device_lst.size() + " matched cache is found!";
			log.info(message);
			if (pepvpnstatusLst != null) response.setData(pepvpnstatusLst);//For Test
			response.setResp_code(ResponseCode.PENDING);
			response.setMessage(message);
		}
		else
		{
			response.setData(pepvpnstatusLst);
			response.setResp_code(ResponseCode.SUCCESS);
		}
		response.setNetwork_time(new Date());
		return JsonUtils.toJson(response);
	
	}
	
	
	/* ********** Add for PepvpnV2 ********** */
	public static String getPepvpnPeerDetail(JsonNetworkRequest request, JsonResponse<Json_PepvpnPeerDetail> response)
	{		
		final boolean bRdonly = true;
		int param_networkId = request.getNetwork_id();
		String param_orgId = request.getOrganization_id();		

		List<Devices> hubList = new ArrayList<Devices>();
		ArrayList<VpnGroupInfo> vpn_grp = new ArrayList<VpnGroupInfo>();
		
		Boolean bWillFetch = false;
		Devices selectedHub = null;
		PepvpnPeerDetailObject pepvpnStatus = null;
		int maxMvpnLicense = -1, foundcache =0;

		try
		{
			response.setResp_code(ResponseCode.SUCCESS);

			DevicesDAO deviceDAO = new DevicesDAO(param_orgId, bRdonly);
			ConfigurationPepvpnsDAO pepvpnConfDao = new ConfigurationPepvpnsDAO(param_orgId, bRdonly);
			ConfigurationPepvpns pepvpnConf = pepvpnConfDao.findById(param_networkId);
			
			if (pepvpnConf != null && pepvpnConf.isEnabled() == true)
			{
				log.debug("finding hub " + pepvpnConf.getHubDeviceId());
				Devices hub = deviceDAO.findById(pepvpnConf.getHubDeviceId());
				hubList.add(hub);
				if (pepvpnConf.isHaHubEnabled())
				{
					if (pepvpnConf.getHaHubDeviceId() != null)
					{
						log.debug("finding ha hub " + pepvpnConf.getHaHubDeviceId());
						Devices hahub = deviceDAO.findById(pepvpnConf.getHaHubDeviceId());
						hubList.add(hahub);
					}
				}				
			}
			log.warn(hubList.size() + " hub(s) is found: "+ hubList);
			
			if (hubList.size() == 0)
			{
				response.setMessage("PEPVPN_NOT_CONFIG");
				response.setResp_code(ResponseCode.SUCCESS);
				return JsonUtils.toJson(response);
			}
		
			/* Check if hub or ha hub record exists in cache. 
			 * If not, fetch command to both WTP. 
			 * Only the Active hub should have registered network and response.
			 */
						
			for (Devices dev : hubList)
			{
				String snSearch = dev.getSn();
				PepvpnPeerDetailObject pepvpnStatusExample = new PepvpnPeerDetailObject();
				pepvpnStatusExample.setSn(snSearch);
				pepvpnStatusExample.setIana_id(dev.getIanaId());
				log.info("seek cache for sn=" + snSearch+" iana_id"+ dev.getIanaId()+"seek cache for key=" + pepvpnStatusExample.getKey());
				
				if (pepvpnStatus == null)
				{
					/* seek cache: use hub info, otherwise ha hub info if hub is null */					
					pepvpnStatus = ACUtil.<PepvpnPeerDetailObject> getPoolObjectBySn(pepvpnStatusExample, PepvpnPeerDetailObject.class);
					if (pepvpnStatus != null) selectedHub = dev;
				}
				log.debug("seek cache: pepvpnStatus1= " + pepvpnStatus);
			}
			
			if (pepvpnStatus == null || ACUtil.isNeedRefresh(pepvpnStatus))
			{
				for (Devices dev : hubList)
				{
					if (PROD_MODE)
					{					
						if (ONLINE_STATUS.statusOf(dev.getOnline_status())==ONLINE_STATUS.ONLINE)
						{
							log.debug("fetch=true");
							bWillFetch = true;
							selectedHub = dev;
						}
					}
					else
					{
						log.debug("fetch=true (debug mode)");
						bWillFetch = true;
						selectedHub = hubList.get(0);
					}
				} // end for						
			}				
			log.debug("After seek cache bWillFetch= " + bWillFetch +" selectedHub = "+selectedHub);			
			
			if (pepvpnStatus == null)
			{
				log.debug("no matched cache is found!");
				
				if (bWillFetch)
				{
					/* load Pepvpn from db */
					DeviceFeaturesDAO dfDAO = new DeviceFeaturesDAO(param_orgId, bRdonly);
					DeviceFeatures df = null;  
					if (hubList!=null && hubList.size()!=0)			
					{
					/* assume master and ha hub license are the same */
						df = dfDAO.findById(hubList.get(0).getId());
					}
					if(df != null && df.getMvpnLicense() != null)
					{
						maxMvpnLicense = (df.getMvpnLicense()==0?RadioConfigUtils.MVPN_INFINITE_LICENSE_NUM:df.getMvpnLicense());
						if (maxMvpnLicense<0)	// 0 means infinite number of license
						{
							log.warn("hub has no pepvpn license");	
							response.setResp_code(ResponseCode.SUCCESS);
							response.setMessage("HUB_ZERO_LICENSE");
							return JsonUtils.toJson(response);
						}
					}
					
					pepvpnStatus = PepvpnStatusUtils.loadPepvpnPeerDetailObj(param_orgId, param_networkId, maxMvpnLicense,selectedHub);
					log.debugf("pepvpnStatus2 = %s", pepvpnStatus);
					
					/* put to cache	*/	
					if (pepvpnStatus != null)
					{
						pepvpnStatus.setSid(request.getCaller_ref()+ request.getServer_ref());
						pepvpnStatus.setSn(selectedHub.getSn());
						pepvpnStatus.setIana_id(selectedHub.getIanaId());
						pepvpnStatus.setStatus(0);
						pepvpnStatus.setOrganization_id(param_orgId);
						pepvpnStatus.setNetwork_id(param_networkId);
						pepvpnStatus.setFetchStartTime(DateUtils.getUnixtime());
						ACUtil.cachePoolObjectBySn(pepvpnStatus, PepvpnPeerDetailObject.class);

						Json_PepvpnPeerDetail pepvpnJson = new Json_PepvpnPeerDetail();
						pepvpnJson.parsePepvpnPeerDetail(pepvpnStatus);
						response.setData(pepvpnJson);
					}
					response.setResp_code(ResponseCode.PENDING);
					response.setMessage("GETTING_HUB_INFO");
					
				}
				else
				{
					response.setResp_code(ResponseCode.SUCCESS);
					response.setMessage("HUB_OFFLINE");
				}
			}
			else
			{	
				log.debugf("Hub peerdetail info found in cache pepvpnStatus=%d", pepvpnStatus.getStatus());
				PeerDetailResponse peerdetailresponse = null;
				ArrayList<PeerDetail> newpepconnLst = null; 
				ArrayList<Integer> endpoints = new ArrayList<Integer>();
				VpnGroupInfo vpnInfo = new VpnGroupInfo();
				List<Devices> endpoints_lst = null;
				if (pepvpnStatus.getStatus()==null || pepvpnStatus.getStatus()==200)
				{/* Response from WTP */
					if (pepvpnStatus.getResponse() != null)
					{
						peerdetailresponse = pepvpnStatus.getResponse();
						ArrayList<PeerDetail> pepconnLst = new ArrayList<PeerDetail>();
						pepconnLst = peerdetailresponse.getPeer();
						newpepconnLst = new ArrayList<PeerDetail>();
						endpoints_lst = new ArrayList<Devices>();				
						
						if( pepconnLst!=null )
						{
							for (PeerDetail peerde : pepconnLst)
							{	
								if (peerde.getUsername()!=null)// ProfileName
								{
									String sn = PepvpnConfigUtils.getSnFromProfileName(peerde.getUsername());
									Devices dev = new Devices();
									dev = deviceDAO.findBySnNet(23695, sn, param_networkId);//Devices dev = NetUtils.getDevices(param_orgId, 23695, sn);		
									if (dev != null)
									{
										if (dev.getSn().equalsIgnoreCase(selectedHub.getSn()) && (dev.getIanaId().intValue()==selectedHub.getIanaId().intValue())) 
										{/* endpoints => hub(dev) */
//											peerde.setDevice_id(dev.getId());
//											peerde.setSerial(dev.getSn());
//											peerde.setName(dev.getName());	
											peerde.setRemote_device_id(selectedHub.getId());
											peerde.setRemote_serial(selectedHub.getSn());	
										}
										else
										{/* hub => endpoints(dev)*/
											endpoints.add(dev.getId());
											peerde.setDevice_id(selectedHub.getId());
											peerde.setSerial(selectedHub.getSn());
											peerde.setName(selectedHub.getName());	
											peerde.setRemote_device_id(dev.getId());
											peerde.setRemote_serial(dev.getSn());										

											/* Add all Endpoints */	
											endpoints_lst.add(dev);
										
											/* **** Add Hub PeerDetail ***** */	
											newpepconnLst.add(peerde);	
										}
									}
								} else {
										log.warn("pepvpn connection profile name is null!");
								}						
													
							} // for (PeerDetail peerde : pepconnLst)
							log.debugf("Add hub new peerdetail %s -- %s", endpoints,newpepconnLst);
						}
						
					/* **** If endpoint_dev online, Get peerdetail from endpoint_sn *****/							
					Json_PepvpnInfo pepvpn_res = new Json_PepvpnInfo();	
					if (endpoints_lst != null)
					{
						Json_PepvpnInfo pepvpn_req = new Json_PepvpnInfo();
						pepvpn_req.setSid(request.getCaller_ref() + request.getServer_ref());
						pepvpn_req.setSelectedHub(selectedHub);
						pepvpn_req.setEndpoints_lst(endpoints_lst);
						pepvpn_res = getPeerDetailFromEndpoints(pepvpn_req); /* ***Pepvpn code revamp*** */
					}
					if (pepvpn_res != null)	
					{
						foundcache = pepvpn_res.getFoundcache();
						List<PeerDetail> addpepconnLst = pepvpn_res.getNewpepconnLst();
						if (addpepconnLst != null){
							/* **** Add Endpoints PeerDetail ***** */	
							for(PeerDetail add_pd : addpepconnLst)
								newpepconnLst.add(add_pd);
						}
						
					}
					peerdetailresponse.setPeer(newpepconnLst);
					pepvpnStatus.setResponse(peerdetailresponse);
					
				}//if (pepvpnStatus.getResponse() != null)
					vpnInfo.setEndpoints(endpoints);
					vpnInfo.setHub_id(selectedHub.getId());
					vpnInfo.setHub_net_id(selectedHub.getNetworkId());
					vpn_grp.add(vpnInfo);
					pepvpnStatus.setVpn_grp(vpn_grp);
				}// if(pepvpnStatus.getStatus()==null || pepvpnStatus.getStatus()==200)
							
				pepvpnStatus.setOrganization_id(param_orgId);
				pepvpnStatus.setNetwork_id(param_networkId);
				log.debugf("pepvpnStatus4 = %s", pepvpnStatus);
				Json_PepvpnPeerDetail pepvpnJson = new Json_PepvpnPeerDetail();
				pepvpnJson.parsePepvpnPeerDetail(pepvpnStatus);
				response.setData(pepvpnJson);
				
				try {					
					if (pepvpnStatus.getStatus()==200 && endpoints_lst != null && foundcache== endpoints_lst.size()) // foundcache++
					{
						log.debugf("Finally foundcache= %d endpoints_lst.size()= %d",foundcache,endpoints_lst.size());
						response.setResp_code(ResponseCode.SUCCESS);
					} else {
						response.setResp_code(ResponseCode.PENDING);
						}
				} catch (NullPointerException e) {
					log.debug("status not found");
					response.setResp_code(ResponseCode.PENDING);
				}
			}
			
			if (bWillFetch && selectedHub!=null)
			{
				/* *** Fetch for Hub PepvpnPeerDetail *** */
				log.debug("Fetch for Hub PepvpnPeerDetail selectedHub.getSn= " +selectedHub.getSn() +" selectedHub.getIanaId= "+ selectedHub.getIanaId());
				FeatureGetObject fgo = FeatureGetUtils.getFeatureGetObject(23695, selectedHub.getSn());	
				
				List<Devices> devLst = new ArrayList<Devices>();
				devLst = NetUtils.getDeviceLstByNetId(param_orgId, param_networkId);//TODO //devLst = PepvpnStatusUtils.getAssignedEndptLst(param_orgId, param_networkId, maxMvpnLicense);
				ArrayList<String> sns = new ArrayList<String>();
				if (devLst != null)
				{					
					for (Devices dev : devLst)					
						sns.add(dev.getSn());					
				}
				
				String mvpnStatus = null;
				if (fgo != null && fgo.getFeatures()!=null) {
					mvpnStatus = FeatureGetUtils.getFeatureAttributeAsStr(fgo, ATTRIBUTE.mvpn_status);
				}
				log.debug("mvpnStatus1 = "+mvpnStatus + "fgo = "+fgo+ " sns "+sns);
				if (mvpnStatus != null && mvpnStatus.equals("1")) //Firmware version "status": 1  <----- NEW!!
				{
					PepvpnQueryCommand qm = new PepvpnQueryCommand();
					qm.setVersion(1);
					qm.setSn(sns);

					if ((ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL, request.getCaller_ref() + request.getServer_ref(), selectedHub.getIanaId(), selectedHub.getSn(), qm))==false)
					{
						log.error("Fail to fetchCommand: PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL, NewPepvpn selectedHub " + selectedHub.getSn());
					}
				}
				else
				{
					if((ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_PEPVPN_ENDPOINT, request.getCaller_ref() + request.getServer_ref(), selectedHub.getIanaId(), selectedHub.getSn()))==false)
					{
						log.error("fail to fetchCommand: PIPE_INFO_TYPE_PEPVPN_ENDPOINT, selectedHub " + selectedHub.getSn());
					}
				}
			}
		} catch (Exception e) {
			log.error(" getPepvpnPeerDetail -"+ e + "selectedHub ="+selectedHub+"pepvpnStatus = "+pepvpnStatus, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
		return JsonUtils.toJson(response);
	}
	
	public static Json_PepvpnInfo getPeerDetailFromEndpoints(Json_PepvpnInfo pepvpn_req)
	{   
		String sid = pepvpn_req.getSid();
		List<Devices> endpoints_lst = pepvpn_req.getEndpoints_lst();
		Devices selectedHub = pepvpn_req.getSelectedHub();
		
		
		int foundcache = 0;
		List<PeerDetail> newpepconnLst = new ArrayList<PeerDetail>();
		Json_PepvpnInfo pepvpn_res = new Json_PepvpnInfo();
		
		log.debugf("endpoint_dev online, Get peerdetail from endpoints  endpoints_lst size= %d ",endpoints_lst.size());
		try {
		if (endpoints_lst != null)
		{
			for (Devices endpoint_dev: endpoints_lst)
			{	
			  if (ONLINE_STATUS.statusOf(endpoint_dev.getOnline_status())==ONLINE_STATUS.ONLINE)
			  {/* **** Online Endpoints ****  */	
				String endpoint_sn = endpoint_dev.getSn();
				PepvpnPeerDetailObject peerDetailExample = new PepvpnPeerDetailObject();
				peerDetailExample.setSn(endpoint_sn);
				peerDetailExample.setIana_id(23695);
				log.info("seek cache for sn=" + endpoint_dev.getSn()+" iana_id"+ 23695+"seek cache for key=" + peerDetailExample.getKey());
									
				PepvpnPeerDetailObject endpoint_peerdetail = ACUtil.<PepvpnPeerDetailObject> getPoolObjectBySn(peerDetailExample, PepvpnPeerDetailObject.class);
				log.debug("seek cache: endpoint_peerdetail= " + endpoint_peerdetail);
		
				if (endpoint_peerdetail == null || ACUtil.isNeedRefresh(endpoint_peerdetail))
				{
					/* **** Add endpoints peerdetail ***** */
					PeerDetail endpd_caching = new PeerDetail();
					endpd_caching.setDevice_id(endpoint_dev.getId());
					endpd_caching.setSerial(endpoint_dev.getSn());
					endpd_caching.setName(endpoint_dev.getName());	
					endpd_caching.setStatus("CACHING...");
					endpd_caching.setPeer_id("0_0");
					endpd_caching.setRemote_serial(selectedHub.getSn());
					endpd_caching.setRemote_device_id(selectedHub.getId());
					newpepconnLst.add(endpd_caching);
					
					/* *** Fetch peerdetail from Endpoints *** */
					FeatureGetObject fgo = FeatureGetUtils.getFeatureGetObject(23695, endpoint_sn);				
					String mvpnStatus = null;
					if (fgo != null && fgo.getFeatures()!=null) {
						mvpnStatus = FeatureGetUtils.getFeatureAttributeAsStr(fgo, ATTRIBUTE.mvpn_status);
					}
					log.debug("mvpnStatus1 = "+mvpnStatus + "fgo = "+fgo);
					if (mvpnStatus != null && mvpnStatus.equals("1"))
					{
						ArrayList<String> sns = new ArrayList<String>();
						sns.add(selectedHub.getSn());
						PepvpnQueryCommand qm = new PepvpnQueryCommand();
						qm.setVersion(1);
						qm.setSn(sns);//TODO return all peerdetail or hub_sn
						if ((ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL, sid, 23695, endpoint_sn, qm))==false)
						{
							log.error("Fail to fetchCommand: PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL, NewPepvpn endpoint_sn " + endpoint_sn);				
						}
					}
					else
					{
						if((ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_PEPVPN_ENDPOINT, sid, 23695, endpoint_sn))==false)
						{
							log.error("fail to fetchCommand: PIPE_INFO_TYPE_PEPVPN_ENDPOINT, endpoint_sn " + endpoint_sn);						
						}
					}
				}//if (endpoint_peerdetail == null || ACUtil.isNeedRefresh(endpoint_peerdetail))
				else 
				{
					foundcache ++;
	
					if (endpoint_peerdetail.getResponse() != null)
					{
						PeerDetailResponse end_pd_response = endpoint_peerdetail.getResponse();
						ArrayList<PeerDetail> endpepconnLst = new ArrayList<PeerDetail>();
						endpepconnLst = end_pd_response.getPeer();
						if (endpepconnLst != null)
						{/* *** Endpoint PeerDetail***** */
							for (PeerDetail endpd : endpepconnLst)
							{
								if (endpd.getUsername()!=null)// ProfileName
								{
									String sn = PepvpnConfigUtils.getSnFromProfileName(endpd.getUsername());								
									if (selectedHub.getSn().equalsIgnoreCase(sn)) 
									{/* endpoints => hub(dev) */
										endpd.setDevice_id(endpoint_dev.getId());	//TO DO remote_id	
										endpd.setSerial(endpoint_dev.getSn());
										endpd.setName(endpoint_dev.getName());	
										
										endpd.setRemote_serial(selectedHub.getSn());
										endpd.setRemote_device_id(selectedHub.getId());
										
										/* **** Add endpoints peerdetail ***** */
										newpepconnLst.add(endpd);
										log.debug("Add Endpoints peerdetail "+ endpd);
									}
									log.debug(" sn ="+ sn +"Endpoints peerdetail ProfileName "+ endpd.getUsername());
								}						
							}
						}
					}
				}	
		}// online
		else {
				foundcache++;
				PeerDetail endpd_off = new PeerDetail();
				endpd_off.setDevice_id(endpoint_dev.getId());	//TO DO remote_id	
				endpd_off.setSerial(endpoint_dev.getSn());
				endpd_off.setName(endpoint_dev.getName());	
				endpd_off.setStatus("OFFLINE");
				endpd_off.setPeer_id("0_0");
				endpd_off.setRemote_serial(selectedHub.getSn());
				endpd_off.setRemote_device_id(selectedHub.getId());
				/* **** Add endpoints peerdetail ***** */
				newpepconnLst.add(endpd_off);
			}
		}//  for
		log.debugf("Endpoints peerdetail foundcache= %d" , foundcache);
		pepvpn_res.setFoundcache(foundcache);
		pepvpn_res.setNewpepconnLst(newpepconnLst);
	
	} //End if()

	} catch (Exception e) {
			log.error("Json_PepvpnRevamp getPeerDetailFromEndpoints -"+ e + "selectedHub ="+selectedHub+"pepvpn_req = "+pepvpn_req+"pepvpn_res = "+pepvpn_res, e);
			e.printStackTrace();
	}							
						
	return pepvpn_res;
}
	

	public static String getPepvpnHubs(JsonNetworkRequest request, JsonResponse<List<Json_PepvpnHubs>> response)
	{
		/* get all hub featured device(s) (ignore online status) of the organization networks from db */
		String param_orgId = request.getOrganization_id();
		Integer param_networkId = request.getNetwork_id();
		boolean param_isConfigured = request.isSearch_by_config();

		log.debug("param_isConfigured = " + param_isConfigured);

		List<Integer> productIdLst;
		List<Devices> hubList;
		List<Json_PepvpnHubs> endptzLst = new ArrayList<Json_PepvpnHubs>();


		try {
			response.setResp_code(ResponseCode.SUCCESS);
			ProductsDAO productDAO = new ProductsDAO();
			DevicesDAO deviceDAO = new DevicesDAO(param_orgId);			

			if (param_isConfigured)
			{				
				// Devices devExample = new Devices();
				// devExample.setIshub(true);
				// hubList = deviceDAO.findByExample(devExample);
				hubList = deviceDAO.getDevicesHubListFromHubNetwork(param_networkId, true);
			}
			else
			{
				//hubList = deviceDAO.getDevicesHubFeatureList(param_networkId, true);				
				productIdLst = productDAO.getProductHubSupportIdList(true);
				hubList = deviceDAO.getDevicesHubSupportList(param_networkId, productIdLst, true);						
			}

			if (hubList == null || hubList.size() == 0)
			{
				Json_PepvpnHubs dummy = new Json_PepvpnHubs();
				endptzLst.add(dummy);

				response.setMessage("no device is configured as hub in the network");
				response.setResp_code(ResponseCode.SUCCESS);
				return JsonUtils.toJson(response);
			}
			else
			{
				log.info(hubList.size() + " hub(s) is found.");
			}

			/* trace all networks from each hub's endpoints */
			for (Devices dev : hubList)
			{
				/* add the device to result */
				Json_PepvpnHubs hubs = new Json_PepvpnHubs();
				hubs.setId(dev.getId());
				hubs.setSn(dev.getSn());
				hubs.setName(dev.getName());
				hubs.setFw_ver(dev.getFw_ver());
				
				/* get all peer networks of this hub */
				ConfigurationPepvpnsDAO configPepvpnsDAO = new ConfigurationPepvpnsDAO(param_orgId);
				List<Integer> networkIdLst = null;
				// networkIdLst = configPepvpnsDAO.getHubNetworkIds(dev.getId());
				networkIdLst = configPepvpnsDAO.getMasterHubSupportedNetworkIds(dev.getId());

				/* count the endpoints in each network */
				if (networkIdLst != null && networkIdLst.size() != 0)
				{
					List<Integer> hubDevIdLst = new ArrayList<Integer>();
					for (Devices hub : hubList)
						hubDevIdLst.add(hub.getId());
					log.debugf("getPeerNetworkList(networkIdLst, hubDevIdLst)=(%s, %s)", networkIdLst, hubDevIdLst);
					List<Peer_Networks> pnLst = deviceDAO.getPeerNetworkList(networkIdLst, hubDevIdLst);
					hubs.setPeer_networks(pnLst);
				}
				else
				{
					log.warnf("no networkId is found! org=%s, net=%s", param_orgId, param_networkId);
				}

				endptzLst.add(hubs);
			}

		} catch (Exception e) {
			log.error("getPepvpnHubs - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		response.setData(endptzLst);
		response.setResp_code(ResponseCode.SUCCESS);
		return JsonUtils.toJson(response);
	}

	public static String getClients(JsonNetworkRequest request, JsonResponse<List<Json_Clients>> response)
	{

		final boolean bReadOnly = true;
		String param_orgId = request.getOrganization_id();
		int param_networkId = request.getNetwork_id();
		List<Json_Mac_List> mac_list = request.getMac_list();
		
		String serverRef1 = request.getServer_ref();
		String serverRef2 = JsonUtils.relateServerRef(serverRef1);
		String sid1 = request.getCaller_ref() + serverRef1;
		String sid2 = request.getCaller_ref() + serverRef2;
		
		int flag = 0;
		int flag1 = 0;		

		List<Json_Clients> resultClientList = new ArrayList<Json_Clients>();
		try
		{
			
			response.setResp_code(ResponseCode.SUCCESS);
			DevicesDAO devDAO = new DevicesDAO(param_orgId, bReadOnly);
			OuiInfosDAO ouiInfoDAO = new OuiInfosDAO(bReadOnly);
			
			List<Devices> devLst = devDAO.getDevicesList(param_networkId);

			for (Devices dev : devLst)
			{
				StationZObject stationzExample = new StationZObject();
				stationzExample.setIana_id(dev.getIanaId());
				stationzExample.setSn(dev.getSn());

				StationZObject stationzObj = ACUtil.<StationZObject> getPoolObjectBySn(stationzExample, StationZObject.class);
				Json_Devices devJson = new Json_Devices();
				devJson.parseDevices(dev, param_orgId);

				if (stationzObj != null)
				{
					Integer current_time = Integer.valueOf("" + System.currentTimeMillis() / 1000);

					if ((stationzObj.getLastUpdateTime() != null) && (current_time - stationzObj.getLastUpdateTime() >= 30))
//					if ( stationzObj.getLastUpdateTime() != null )
					{
						flag = 1;
						if ((stationzObj.getLastRequestUpdateTime() == null))
						{// getLastRequestUpdateTime, that means never request before
							response.setResp_code(ResponseCode.PENDING); // pending for incomplete client list
//							log.info("fetching device station list stz is new " + dev.getSn());
							flag = 2;
							ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_Z, sid1, dev.getIanaId(), dev.getSn());
							stationzObj.setLastRequestUpdateTime((int) (new Date().getTime() / 1000));
							ACUtil.cachePoolObjectBySn(stationzObj, StationZObject.class);
						}
//						else if ((current_time - stationzObj.getLastRequestUpdateTime() > 30))
//						{// last request is more than 30s ago
//							response.setResp_code(ResponseCode.PENDING); // pending for incomplete client list
////							log.info("fetching device station list last request is too old " + dev.getSn());
//							flag = 3;
//							ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_Z, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn());
//							stationzObj.setLastRequestUpdateTime((int) (new Date().getTime() / 1000));
//							ACUtil.cachePoolObjectBySn(stationzObj, StationZObject.class);
//						}
						else
						{
							// stationzObj.setLastRequestUpdateTime((int)(new Date().getTime()/1000));
							
							List<StationMacList> stationMacList = stationzObj.getStation_mac_list();
//							log.info("fetching device station list suc " + dev.getSn());
							flag = 4;
							if (stationMacList != null && stationMacList.size() > 0)
							{
								flag = 5;
								for (StationMacList station : stationMacList)
								{
									Json_Clients client = new Json_Clients();
									Integer cur_time = Integer.valueOf("" + (System.currentTimeMillis() / 1000));

									client.setIp(station.getIp());
									client.setMac(station.getMac());
									client.setName(station.getName());
									client.setSsid(station.getEssid());
									client.setStatus(station.getStatus());
									client.setConnection_type(station.getType());
									client.setDevice(devJson);
									client.setManufacturer(ouiInfoDAO.findManufacturerByMac(station.getMac()));
									if (station.isActive() == true)
									{
										client.setStatus("active");
									}
									else
									{
										client.setStatus("inactive");
									}

									StationBandwidthListObject stationBandWidthListExample = new StationBandwidthListObject();
									stationBandWidthListExample.setIana_id(dev.getIanaId());
									stationBandWidthListExample.setSn(dev.getSn());
									stationBandWidthListExample.setDevice_id(dev.getId());
									stationBandWidthListExample.setNetwork_id(param_networkId);
									stationBandWidthListExample.setOrganization_id(param_orgId);

									StationBandwidthListObject stationBandWidthListObject = ACUtil.<StationBandwidthListObject> getPoolObjectBySn(stationBandWidthListExample, StationBandwidthListObject.class);
									if (stationBandWidthListObject != null)
									{
										flag = 6;
										if (mac_list != null && mac_list.size() > 0)
										{
											flag = 7;
											CopyOnWriteArrayList<StationStatusList> statusList = stationBandWidthListObject.getStation_status_list();
											if (statusList != null && statusList.size() > 0)
											{
												flag = 8;
												for (Json_Mac_List macObj : mac_list)
												{
													int j = 0;
													for (StationStatusList status : statusList)
													{
														if (macObj.getMac().equals(status.getMac()))
														{
															j = 1;
															status.setTimestamp(cur_time);
															break;
														}
														else
														{
															if (stationBandWidthListObject.getLastUpdatedTime() != null && cur_time - stationBandWidthListObject.getLastUpdatedTime() > 120)
															{
																statusList.remove(status);
															}
														}
													}
													if (j == 0)
													{
														flag = 9;
														if (Integer.valueOf(macObj.getDevice_id().trim()) == dev.getId())
														{
															StationStatusList status = stationBandWidthListExample.new StationStatusList();
															status.setMac(macObj.getMac());
															status.setDrx(0f);
															status.setDtx(0f);
															status.setRssi(0f);
															status.setTimestamp(cur_time);
															statusList.add(status);
															stationBandWidthListObject.setLastUpdatedTime(cur_time);
														}
													}
												}
												stationBandWidthListObject.setStation_status_list(statusList);
												// ACUtil.cachePoolObjectBySn(stationBandWidthListObject,
												// StationBandwidthListObject.class);
												for (StationStatusList status : statusList)
												{
													if (status.getMac().equals(client.getMac()))
													{
														client.setDownload_rate(status.getDrx());
														client.setUpload_rate(status.getDtx());
														client.setSignal(status.getRssi());
														break;
													}
												}
											}

										}
									}
									else
									{
//										log.info("fetching device station list stationbandwidthlist is null!");
										flag = 10;
										if (mac_list != null && mac_list.size() > 0)
										{
											flag = 11;
											CopyOnWriteArrayList<StationStatusList> sta_status_List = new CopyOnWriteArrayList<StationStatusList>();

											for (Json_Mac_List macObj : mac_list)
											{
												if (Integer.valueOf(macObj.getDevice_id().trim()) == dev.getId())
												{
													StationStatusList status = stationBandWidthListExample.new StationStatusList();
													status.setMac(macObj.getMac());
													status.setDrx(null);
													status.setDtx(null);
													status.setRssi(null);
													sta_status_List.add(status);
												}
											}

											stationBandWidthListExample.setStation_status_list(sta_status_List);
											stationBandWidthListExample.setLastUpdatedTime(cur_time);
											// ACUtil.cachePoolObjectBySn(stationBandWidthListExample,
											// StationBandwidthListObject.class);
										}

										ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_BANDWIDTH_LIST, sid2, dev.getIanaId(), dev.getSn());

									}

									int tag = 0;
									for (Json_Clients jsclient : resultClientList)
									{
										log.info("The jsclient status: "+jsclient.getStatus());
										if (jsclient.getMac().equals(client.getMac()))
										{
											if (jsclient.getStatus().equals("inactive"))
											{
												resultClientList.remove(jsclient);
											}
											else if (client.getStatus().equals("inactive"))
											{
												tag = 1;
											}
											break;
										}
									}
									if (tag != 1)
									{
										resultClientList.add(client);
									}
									else
										flag = 20;
									flag1 = tag;
								}
							}
							
							if( current_time - stationzObj.getLastRequestUpdateTime() >= 30 )
							{
								response.setResp_code(ResponseCode.PENDING); // pending for incomplete client list
//								log.info("fetching device station list last request is too old " + dev.getSn());
								flag = 22;
								ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_Z, sid1, dev.getIanaId(), dev.getSn());
								stationzObj.setLastRequestUpdateTime((int) (new Date().getTime() / 1000));
								ACUtil.cachePoolObjectBySn(stationzObj, StationZObject.class);
							}
							
						}

					}
					else
					{
//						log.info("fetching device station list the cache is too old " + dev.getSn());
						flag = 12;
						List<StationMacList> stationMacList = stationzObj.getStation_mac_list();
						if (stationMacList != null && stationMacList.size() > 0)
						{
							flag = 13;
							log.info("fetching device station list the cache is too old 1 " + dev.getSn());
							for (StationMacList station : stationMacList)
							{
								Json_Clients client = new Json_Clients();
								Integer cur_time = Integer.valueOf("" + (System.currentTimeMillis() / 1000));

								client.setIp(station.getIp());
								client.setMac(station.getMac());
								client.setName(station.getName());
								client.setSsid(station.getEssid());
								client.setStatus(station.getStatus());
								client.setConnection_type(station.getType());
								client.setDevice(devJson);
								client.setManufacturer(ouiInfoDAO.findManufacturerByMac(station.getMac()));
								if (station.isActive() == true)
								{
									client.setStatus("active");
								}
								else
								{
									client.setStatus("inactive");
								}

								StationBandwidthListObject stationBandWidthListExample = new StationBandwidthListObject();
								stationBandWidthListExample.setIana_id(dev.getIanaId());
								stationBandWidthListExample.setSn(dev.getSn());
								stationBandWidthListExample.setDevice_id(dev.getId());
								stationBandWidthListExample.setNetwork_id(param_networkId);
								stationBandWidthListExample.setOrganization_id(param_orgId);

								StationBandwidthListObject stationBandWidthListObject = ACUtil.<StationBandwidthListObject> getPoolObjectBySn(stationBandWidthListExample, StationBandwidthListObject.class);
								if (stationBandWidthListObject != null)
								{
									flag = 14;
									if (mac_list != null && mac_list.size() > 0)
									{
										flag = 15;
										CopyOnWriteArrayList<StationStatusList> statusList = stationBandWidthListObject.getStation_status_list();
										if (statusList != null && statusList.size() > 0)
										{
											flag = 16;
											for (Json_Mac_List macObj : mac_list)
											{
												int j = 0;
												for (StationStatusList status : statusList)
												{
													if (macObj.getMac().equals(status.getMac()))
													{
														j = 1;
														status.setTimestamp(cur_time);
														break;
													}
													else
													{
														if (stationBandWidthListObject.getLastUpdatedTime() != null && cur_time - stationBandWidthListObject.getLastUpdatedTime() > 120)
														{
															statusList.remove(status);
														}
													}
												}
												if (j == 0)
												{
													if (Integer.valueOf(macObj.getDevice_id().trim()) == dev.getId())
													{
														StationStatusList status = stationBandWidthListExample.new StationStatusList();
														status.setMac(macObj.getMac());
														status.setDrx(0f);
														status.setDtx(0f);
														status.setRssi(0f);
														status.setTimestamp(cur_time);
														statusList.add(status);
														stationBandWidthListObject.setLastUpdatedTime(cur_time);
													}
												}
											}
											stationBandWidthListObject.setStation_status_list(statusList);
											// ACUtil.cachePoolObjectBySn(stationBandWidthListObject,
											// StationBandwidthListObject.class);
											for (StationStatusList status : statusList)
											{
												if (status.getMac().equals(client.getMac()))
												{
													client.setDownload_rate(status.getDrx());
													client.setUpload_rate(status.getDtx());
													client.setSignal(status.getRssi());
													break;
												}
											}
										}

									}
								}
								else
								{
									flag = 17;
									if (mac_list != null && mac_list.size() > 0)
									{
										CopyOnWriteArrayList<StationStatusList> sta_status_List = new CopyOnWriteArrayList<StationStatusList>();
										flag = 18;
										for (Json_Mac_List macObj : mac_list)
										{
											if (Integer.valueOf(macObj.getDevice_id().trim()) == dev.getId())
											{
												StationStatusList status = stationBandWidthListExample.new StationStatusList();
												status.setMac(macObj.getMac());
												status.setDrx(null);
												status.setDtx(null);
												status.setRssi(null);
												sta_status_List.add(status);
											}
										}

										stationBandWidthListExample.setStation_status_list(sta_status_List);
										stationBandWidthListExample.setLastUpdatedTime(cur_time);
										// ACUtil.cachePoolObjectBySn(stationBandWidthListExample,
										// StationBandwidthListObject.class);
									}

									ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_BANDWIDTH_LIST, sid2, dev.getIanaId(), dev.getSn());

								}

								int tag = 0;
								for (Json_Clients jsclient : resultClientList)
								{
									log.info("The jsclient status: "+jsclient.getStatus());
									if (jsclient.getMac().equals(client.getMac()))
									{
										if (jsclient.getStatus().equals("inactive"))
										{
											resultClientList.remove(jsclient);
										}
										else if (client.getStatus().equals("inactive"))
										{
											tag = 1;
										}
										break;
									}
								}
								if (tag != 1)
									resultClientList.add(client);
								else
									flag = 21;
								flag1 = tag;
							}
						}

					}
					
					log.info("fetching device station list add end! " + dev.getSn());

				}
				else
				{
					flag = 19;
					stationzObj = new StationZObject();
					stationzObj.setIana_id(dev.getIanaId());
					stationzObj.setSn(dev.getSn());
					stationzObj.setDevice_id(dev.getId());
					stationzObj.setOrganization_id(param_orgId);
					stationzObj.setLastRequestUpdateTime((int) (new Date().getTime() / 1000));
					ACUtil.cachePoolObjectBySn(stationzObj, StationZObject.class);
					response.setResp_code(ResponseCode.PENDING); // pending for incomplete client list
					log.info("fetching device station list " + dev.getSn());
					ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_Z, sid1, dev.getIanaId(), dev.getSn());
				}

			}

			response.setData(resultClientList);
			log.info("Test client list print flags: flag = " + flag + " , flag1 = "+flag1+", resultsize = "+resultClientList.size());
		} catch (Exception e)
		{
			log.error("getClients - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		return JsonUtils.toJson(response);
	}


	public static String getPepvpnProfile(JsonNetworkRequest request, JsonResponse<JsonConf_PepvpnProfiles> response)
	{
		String param_orgId = request.getOrganization_id();
		int param_networkId = request.getNetwork_id();

		log.debug("getPepvpnProfile(" + param_orgId + "," + param_networkId + ")");

		try {
			/* load db config if exists, create default if not exists */
			JsonConf_PepvpnProfiles pepvpnJson = PepvpnConfigUtils.getDatabasePepvpnFullConfig(param_orgId, param_networkId);
			if (pepvpnJson != null)
			{
				response.setTimestamp(pepvpnJson.getTimestamp());
				response.setData(pepvpnJson);
			}
			else
			{
				pepvpnJson = new JsonConf_PepvpnProfiles();
			}
			response.setResp_code(ResponseCode.SUCCESS);
		} catch (Exception e) {
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}

		return JsonUtils.toJson(response);
	}

	public static String putPepvpnProfile(JsonNetworkRequest_PepvpnProfile request, JsonResponse<JsonConf_PepvpnProfiles> response)
	{
		String param_orgId = request.getOrganization_id();
		int param_networkId = request.getNetwork_id();
		JsonConf_PepvpnProfiles newDataJson = request.getData();
		log.debug("param_networkId=" + param_networkId + " newDataJson=" + newDataJson);
		
		ConfigUpdatePerDeviceTask confUpTask = null;

		if (newDataJson == null)
		{
			response.setResp_code(ResponseCode.MISSING_INPUT);
			return JsonUtils.toJson(response);
		}

		try {
			/* get existing config */
			JsonConf_PepvpnProfiles fullDataJson = PepvpnConfigUtils.getDatabasePepvpnFullConfig(param_orgId, param_networkId);

			/* merge with partial new config */
			if (!fullDataJson.updateWith(newDataJson))
			{
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
				return JsonUtils.toJson(response);
			}

			log.debug("fullDataJson=" + fullDataJson);

			/* persist to database */
			ConfigurationPepvpnsDAO pepvpnDAO = new ConfigurationPepvpnsDAO(param_orgId);
			ConfigurationPepvpnCertificatesDAO certDAO = new ConfigurationPepvpnCertificatesDAO(param_orgId);
			DevicesDAO devDAO = new DevicesDAO(param_orgId);
			//DeviceUpdatesDAO deviceUpdateDAO = new DeviceUpdatesDAO(param_orgId);
			Devices hub = null;
			Devices hahub = null;
			NetworksDAO networkDAO = new NetworksDAO(param_orgId);
			try {

				ConfigurationPepvpns pepvpn = pepvpnDAO.findById(param_networkId);
				log.debug("finding device <" + fullDataJson.getHub_id() + ">");
				if (pepvpn == null)
				{
					log.debug("pepvpn is null, create new config");
					pepvpn = new ConfigurationPepvpns();
					Networks networks = networkDAO.findById(param_networkId);
					//pepvpn.setNetworks(networks);
					pepvpn.setNetworkId(networks.getId());
				}
				pepvpn.setEnabled(fullDataJson.getEnabled());
				pepvpn.setHubNetworkId(fullDataJson.getHub_net_id());
				pepvpn.setHubDeviceId(fullDataJson.getHub_id());

				pepvpn.setHaHubEnabled(fullDataJson.getHa_enabled());
				pepvpn.setHaHubNetworkId(fullDataJson.getHa_hub_net_id());
				pepvpn.setHaHubDeviceId(fullDataJson.getHa_hub_id());

				hub = devDAO.findById(fullDataJson.getHub_id());				
				if (hub==null)
				{
					response.setResp_code(ResponseCode.INVALID_INPUT);
					response.setMessage("MISSING_HUB_INFO");
					return JsonUtils.toJson(response);
				}
				
				//hub.setIshub(fullDataJson.getEnabled());
				//devDAO.saveOrUpdate(hub);

//				if (fullDataJson.getHa_hub_id() != null)
//				{
//					Devices ha_hub = devDAO.findById(fullDataJson.getHa_hub_id());
//					//ha_hub.setIshub(fullDataJson.getHa_enabled());
//					//devDAO.saveOrUpdate(ha_hub);
//				}

				if (request.getData().getAuthentication() == JsonConf_PepvpnProfilesNew.AUTHENTICATION.none)
				{
					/* Remote ID Case */
					fullDataJson.setPsk(null);//TODO
					pepvpn.setPsk(null);//TODO
				}
				else if (request.getData().getAuthentication() == JsonConf_PepvpnProfilesNew.AUTHENTICATION.psk)
				{
					/* pre-shared key generation */
					// 16 alphanumeric characters will be generated
					fullDataJson.setPsk(RandomStringUtils.randomAlphanumeric(16));
					pepvpn.setPsk(JsonConf_PepvpnProfilesNew.AUTHENTICATION.psk.toString());
				}
				else if (request.getData().getAuthentication() == JsonConf_PepvpnProfilesNew.AUTHENTICATION.x509)
				{
					/* X509 certificate */
					if (fullDataJson.getHub_cert_list() != null)
					{
						JsonConf_PepvpnProfiles.Certificate certJson = fullDataJson.getHub_cert_list().get(0);

						/* calculate public cert checksum */
						String publicKeyChecksum = MD5Manager.md5(certJson.getPublic_key_cert_pem().trim());

						ConfigurationPepvpnCertificates cert = certDAO.findById(new ConfigurationPepvpnCertificatesId(param_networkId, fullDataJson.getHub_id()));
						if (cert == null)
						{
							log.debug("no existing cert is found!");
							cert = new ConfigurationPepvpnCertificates(new ConfigurationPepvpnCertificatesId(param_networkId, certJson.getId()), certJson.isAutogen_cert(), publicKeyChecksum, certJson.getPublic_key_cert_pem().trim(), certJson.getPrivate_key_pem().trim(), certJson.getPrivate_key_pem_passphrase());
						}
						cert.setAutogen(certJson.isAutogen_cert());
						cert.setPublicKeyChecksum(publicKeyChecksum);
						cert.setPublicKey(certJson.getPublic_key_cert_pem().trim());
						cert.setPrivateKey(certJson.getPrivate_key_pem().trim());
						cert.setPrivateKeyPass(certJson.getPrivate_key_pem_passphrase());
						cert.setTimestamp(new Date());
						certDAO.saveOrUpdate(cert);
					}
				}

				log.debug("fullDataJson b4 ConfigurationPepvpnCertificates=" + fullDataJson);

				/* remove certs before saving to config */
				fullDataJson.clear_all_certs();

				pepvpn.setConfig(JsonUtils.toJson(fullDataJson));
				pepvpn.setTimestamp(new Date());
				
				pepvpnDAO.saveOrUpdate(pepvpn);

				/* hub */
				List<Integer> hubIdLst = new ArrayList<Integer>();
				hubIdLst.add(fullDataJson.getHub_id());
				new ConfigUpdatePerDeviceTask(param_orgId, hub.getNetworkId()).performConfigUpdateNow(Arrays.asList(hub.getId()), response.getServer_ref(), CONFIG_UPDATE_REASON.pepvpn_hub_update.toString());
				
				/* ha hub */
				if (fullDataJson.getHa_hub_id() != null)
				{
					hahub = devDAO.findById(fullDataJson.getHa_hub_id());
					hubIdLst.add(fullDataJson.getHa_hub_id());
					new ConfigUpdatePerDeviceTask(param_orgId, hahub.getNetworkId()).performConfigUpdateNow(Arrays.asList(hahub.getId()), response.getServer_ref(), CONFIG_UPDATE_REASON.pepvpn_hub_update.toString());
				}

				//deviceUpdateDAO.incrementConfUpdateForDevLst(hubIdLst, response.getServer_ref(), "pepvpn hub");
				//deviceUpdateDAO.incrementConfUpdateForNetwork(param_networkId, response.getServer_ref(), "pepvpn endpoint");								
				
				new ConfigUpdatePerDeviceTask(param_orgId, param_networkId).performConfigUpdateNowForNetwork(response.getServer_ref(), CONFIG_UPDATE_REASON.pepvpn_endpt_update.toString());				
				ConfigSettingsUtils.removePoolObjectByDevIdLst(param_orgId, hubIdLst);
				ConfigSettingsUtils.removePoolObjectByNetId(param_orgId, param_networkId);

			} catch (NullPointerException e) {
				log.error("generally MISSING_INPUT - " + e, e);
				response.setResp_code(ResponseCode.MISSING_INPUT);
				return JsonUtils.toJson(response);
			} catch (Exception e) {
				log.error("putPepvpnProfile - " + e, e);
				throw e;
			}
			//response.setTimestamp(fullDataJson.getTimestamp());
			response.setTimestamp(new Date());
			response.setData(fullDataJson);
			response.setResp_code(ResponseCode.SUCCESS);

			// * prepare to update config
			// update hub
			// ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_PUT, request.getCaller_ref() +
			// request.getServer_ref(), dev.getIanaId(), dev.getSn());
			// update endpoints...
			// ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_PUT, request.getCaller_ref() +
			// request.getServer_ref(), dev.getIanaId(), dev.getSn());
		} catch (Exception e) {
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}

		return JsonUtils.toJson(response);
	}

	
	public static String getDeviceUsageV2(JsonNetworkRequest request, JsonResponse<HashMap<String,List<Json_Device_Usage>>> response) {

		String param_orgId = request.getOrganization_id();
		Integer param_networkId = request.getNetwork_id();
		
		String param_type = request.getType();
		boolean bWifiOnly = false; 
		if (param_type == null || param_type.equalsIgnoreCase("wireless")){
			bWifiOnly = true;
		}

		if(log.isInfoEnabled())
			log.info("getDeviceUsageV2(" + param_orgId + "," + param_networkId + ","+param_type+")");

		String start = request.getStart();
		String end = request.getEnd();

		SimpleDateFormat smf = new java.text.SimpleDateFormat(JsonUtils.jsonRequestDateFormat);
		smf.setTimeZone(TimeZone.getTimeZone("Utc"));
		Date startTime = null;
		Date endTime = null;
		try {
			startTime = smf.parse(start);
			endTime = smf.parse(end);
		} catch (Exception e) {
			log.error("Parse time period error " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
				
		boolean useReadonlyDB = true;

		HashMap<String,List<Json_Device_Usage>> resultMap = new HashMap<String,List<Json_Device_Usage>>();
		List<Json_Device_Usage> resultUsageList = new ArrayList<Json_Device_Usage>();
		List<Json_Device_Usage> resultClientList = new ArrayList<Json_Device_Usage>();
		if (startTime != null && endTime != null) 
		{
			try 
			{
				ProductsDAO pdtDAO = new ProductsDAO(useReadonlyDB);
				DevicesDAO devDAO = new DevicesDAO(param_orgId, useReadonlyDB);
				DailyDeviceUsagesDAO dailyDeviceUsagesDAO = new DailyDeviceUsagesDAO(param_orgId, useReadonlyDB);
				DailyClientUsagesDAO dailyClientUsagesDAO = new DailyClientUsagesDAO(param_orgId, useReadonlyDB);
				
				List<DailyDevUsageResult> topUsageList = null;
				topUsageList = dailyDeviceUsagesDAO.getTopDeviceUsage(param_networkId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000));
//				log.info("topUsageListV2 = " + topUsageList);

				List<DailyClientUsageResult> topClientList = null;
				topClientList = dailyClientUsagesDAO.getDevicesTotalClientCountByNetwork(param_networkId, (int)(startTime.getTime() / 1000), (int)(endTime.getTime() / 1000), bWifiOnly);
//				log.info("topClientListV2 = " + topClientList);
				
				if (topUsageList != null) {
					for (DailyDevUsageResult dailyDevResult : topUsageList) {
						//log.debugf("devId=%d", dailyDevResult.getDeviceId());
						Devices dev = devDAO.findById(dailyDevResult.getDeviceId());
						//log.debugf("dev=%s", dev);
						if (dev!=null  && dev.isActive())
						{
							Products pdt = pdtDAO.findById(dev.getProductId());
							Json_Device_Usage devUsageJson = new Json_Device_Usage();
							devUsageJson.setModel_name(pdt.getName());
							devUsageJson.setDevice_network_id(dev.getNetworkId());
							devUsageJson.setDevice_name(dev.getName());
							devUsageJson.setDevice_id(dailyDevResult.getDeviceId());
							devUsageJson.setSn(dev.getSn());
							double dTotal = dailyDevResult.getTx() + dailyDevResult.getRx();
							devUsageJson.setUsage((float) dTotal * Utils.dataUsageSize1k * Utils.dataUsageSize1k);// front end is expecting byte
							// DailyClientUsageResult clientCntResult = dailyClientUsagesDAO.getDeviceTotalClientCount(param_networkId, dailyDevResult.getDeviceId(), (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000), bWifiOnly);
//							if (clientCntResult != null) {
//								devUsageJson.setClients_count(clientCntResult.getClient_count().intValue());
//							}
							devUsageJson.setClients_count(dailyClientUsagesDAO.getNetworkTotalClientCountWithDevId(param_networkId, dailyDevResult.getDeviceId(), (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000), bWifiOnly));
//							log.debug("clientCntResult = " + clientCntResult);
							resultUsageList.add(devUsageJson);
						}
					}
				}
				
				if( topClientList != null )
				{
					for( DailyClientUsageResult dailyClientUsage : topClientList )
					{
						Devices dev = NetUtils.getDevicesWithoutNetId(param_orgId, dailyClientUsage.getDevice_id());
						if (dev!=null  && dev.isActive() )
						{
							Products product = ProductUtils.getProducts(dev.getProductId());
							Json_Device_Usage devUsageJson = new Json_Device_Usage();
							devUsageJson.setModel_name(product.getName());
							devUsageJson.setDevice_name(dev.getName());
							devUsageJson.setDevice_id(dailyClientUsage.getDevice_id());
							devUsageJson.setDevice_network_id(dev.getNetworkId());
							devUsageJson.setSn(dev.getSn());
							devUsageJson.setClients_count(dailyClientUsage.getClient_cnt());
							DailyDevUsageResult devUsageResult = dailyDeviceUsagesDAO.getDailyUsageOfDevice(dailyClientUsage.getNetwork_id(), dailyClientUsage.getDevice_id(), (int)(startTime.getTime() / 1000), (int)(endTime.getTime() / 1000));
							if( devUsageResult != null )
							{
								double dTotal = devUsageResult.getRx()+devUsageResult.getTx();
								devUsageJson.setUsage((float)dTotal*Utils.dataUsageSize1k*Utils.dataUsageSize1k);
							}
							resultClientList.add(devUsageJson);
						}
					}
				}
				resultMap.put("usage", resultUsageList);
				resultMap.put("client", resultClientList);
				if(log.isInfoEnabled())
					log.info("resultUsageListV2 =" + resultUsageList);
				response.setData(resultMap);
			} catch (Exception e) {
				log.error("getDeviceUsageV2: orgId=" + param_orgId + ", networkId=" + param_networkId + " - " + e, e);
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
				return JsonUtils.toJson(response);
			}
		} else {
			log.error("Parse time period error, result time is null");
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		response.setResp_code(ResponseCode.SUCCESS);
		return JsonUtils.toJson(response);
	}
	
	public static String getSSIDUsage(JsonNetworkRequest request, JsonResponse<List<Json_SSID_Usage>> response) 
	{
		String param_orgId = request.getOrganization_id();
		Integer param_networkId = request.getNetwork_id();
		Integer param_deviceId = request.getDevice_id();
		String param_sort = request.getSort();
		if (param_sort == null || param_sort.isEmpty()){
			param_sort = "usage";
		}
		String param_type = request.getType();
//		boolean bWifiOnly = false; 
		List<Json_SSID_Usage> resultList = new ArrayList<Json_SSID_Usage>();
		if (param_type == null || param_type.equalsIgnoreCase("wireless")){
//			bWifiOnly = true;
		} else {
			log.error("non-wireless is called! ");
		}
		if(log.isInfoEnabled())
			log.info("getSSIDUsage(" + param_orgId + "," + param_networkId + ","+param_type+","+param_deviceId+")");

		String start = request.getStart();
		String end = request.getEnd();

		SimpleDateFormat smf = new java.text.SimpleDateFormat(JsonUtils.jsonRequestDateFormat);
		smf.setTimeZone(TimeZone.getTimeZone("Utc"));
		Date startTime = null;
		Date endTime = null;
		try {
			startTime = smf.parse(start);
			endTime = smf.parse(end);
		} catch (ParseException e) {
			log.error("Parse time period error " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		double totalUsage = 0;
		Long totalUser = 0l;
		boolean useReadonlyDB = true;

		if (startTime != null && endTime != null) {
			List<DailyDevSsidUsageResult> ssidUsageList = null;
			List<DailyClientSsidUsageResult> ssidUserCountList = null;
			try {
				DailyDeviceSsidUsagesDAO dailyDeviceSsidUsagesDAO = new DailyDeviceSsidUsagesDAO(param_orgId, useReadonlyDB);
				DailyClientSsidUsagesDAO dailyClientSsidUsagesDAO = new DailyClientSsidUsagesDAO(param_orgId, useReadonlyDB);
				
				if(param_deviceId != null && param_deviceId != 0)
				{
					ssidUsageList = dailyDeviceSsidUsagesDAO.getUsageListByNetworkNTimeWithDevId(param_networkId, param_deviceId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000));
					ssidUserCountList = dailyClientSsidUsagesDAO.getSsidTotalClientCountListWithDevId(param_networkId, param_deviceId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000));
				}
				else
				{
					ssidUsageList = dailyDeviceSsidUsagesDAO.getUsageListByNetworkNTime(param_networkId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000));
					ssidUserCountList = dailyClientSsidUsagesDAO.getSsidTotalClientCountList(param_networkId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000));
				}
				if(log.isInfoEnabled()) {
					log.info("ssidUsageList = " + ssidUsageList);
					log.info("ssidUserCountList = " + ssidUserCountList);
				}
				
				if (ssidUsageList != null) {
					for (DailyDevSsidUsageResult ssidusage : ssidUsageList) {
						totalUsage = totalUsage + ssidusage.getTx() + ssidusage.getRx();
					}
				} else {
					// return empty success response
					response.setData(resultList);
					response.setResp_code(ResponseCode.SUCCESS);
					return JsonUtils.toJson(response);
				}

				if (ssidUserCountList != null) {
					for (DailyClientSsidUsageResult ssidUserCount : ssidUserCountList) {
						totalUser += ssidUserCount.getClient_count();
					}
				} else {
					// return empty success response
					response.setData(resultList);
					response.setResp_code(ResponseCode.SUCCESS);
					return JsonUtils.toJson(response);
				}
			
				Json_SSID_Usage ssidUsageJson = null;
				for (DailyDevSsidUsageResult ssidusage : ssidUsageList) {
					if (ssidusage.getEncryption() == null || ssidusage.getEssid() == null){
						continue;
					}
					ssidUsageJson = new Json_SSID_Usage();
					ssidUsageJson.setClients_count(0);
					ssidUsageJson.setSsid(ssidusage.getEssid());
					ssidUsageJson.setEncryption(JsonUtils.getConvertedSsidEncryption(ssidusage.getEncryption()));
					Float fTotalUsage = (float) (ssidusage.getTx() + ssidusage.getRx());
					ssidUsageJson.setUsage(fTotalUsage);
					if (totalUsage > 0)
						ssidUsageJson.setUsage_percent((fTotalUsage / ((float) totalUsage)) * 100);
					for (DailyClientSsidUsageResult ssidUserCount : ssidUserCountList) {
						if(log.isInfoEnabled())
							log.info("su ess = " + ssidusage.getEssid() + " su encry = " + ssidusage.getEncryption() + " suc ess = " + ssidUserCount.getEssid() + " suc encry = " + ssidUserCount.getEncryption());
						if (ssidUserCount.getEncryption() == null || ssidUserCount.getEssid() == null){
							continue;
						}
						// WPAMIX/WPA2/WPA are counted the same, note encryption mode at least 3 chars!
						
						if( ssidUserCount.getEncryption().equals("none") )
						{
							ssidUserCount.setEncryption("OPEN");
						}
						
						if (ssidUserCount.getEssid().compareTo(ssidusage.getEssid()) == 0)
						{
							if (ssidUserCount.getEncryption().equals(ssidusage.getEncryption()) || (ssidUserCount.getEncryption().startsWith("WPA") && ssidusage.getEncryption().startsWith("WPA")))
							{
								ssidUsageJson.setClients_count(ssidUserCount.getClient_count().intValue());
								if (totalUser > 0)
									ssidUsageJson.setClients_percent(((float) ssidUserCount.getClient_count() / (float) totalUser) * 100);
								break;
							}
						}
						
					}
					
					resultList.add(ssidUsageJson);
				
				}
				
				ComparatorSsidUsageUtils comparator = new ComparatorSsidUsageUtils(param_sort);
				Collections.sort(resultList, comparator);
				
			} catch (Exception e) {
				log.error("getSSIDUsage: orgId=" + param_orgId + ", networkId=" + param_networkId + ", deviceId=" + param_deviceId + " - " + e, e);
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
				return JsonUtils.toJson(response);
			}
		} else {
			log.error("Parse time period error, result time is null");
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		response.setData(resultList);
		response.setResp_code(ResponseCode.SUCCESS);
		return JsonUtils.toJson(response);
	}

	public static String getTopClient(JsonNetworkRequest request, JsonResponse<List<Json_Client_Usage>> response) 
	{
		String param_orgId = request.getOrganization_id();
		int param_networkId = request.getNetwork_id();
		Integer param_deviceId = request.getDevice_id();
		String param_type = request.getType();
		boolean bWifiOnly = false; 
		List<Json_Client_Usage> resultList = new ArrayList<Json_Client_Usage>();
		if (param_type == null || param_type.equalsIgnoreCase("wireless")){
			bWifiOnly = true;
		}
		if(log.isDebugEnabled())
			log.debug("getTopClient(" + param_orgId + "," + param_networkId + ","+param_type+","+param_deviceId+")");

		String start = request.getStart();
		String end = request.getEnd();

		SimpleDateFormat smf = new java.text.SimpleDateFormat(JsonUtils.jsonRequestDateFormat);
		smf.setTimeZone(TimeZone.getTimeZone("Utc"));
		Date startTime = null;
		Date endTime = null;
		try {
			startTime = smf.parse(start);
			endTime = smf.parse(end);
		} catch (ParseException e) {
			log.error("Parse time period error " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		double totalUsage = 0;
		boolean useReadonlyDB = true;

		List<DailyClientUsageResult> clientUsageList = null;
		if (startTime != null && endTime != null) 
		{
			try 
			{
				DailyClientUsagesDAO dailyClientUsagesDAO = new DailyClientUsagesDAO(param_orgId, useReadonlyDB);
				HashMap<String,String> clientStatusMap = null;
				if( param_deviceId != null && param_deviceId != 0 )
				{
					Devices dev = NetUtils.getDevicesWithoutNetId(param_orgId, param_deviceId);
//					Devices dev = devicesDAO.findById(param_deviceId);
					clientStatusMap = new HashMap<String,String>();
					clientUsageList = dailyClientUsagesDAO.getTopClientTotalUsageWithDevId(param_networkId, param_deviceId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000), bWifiOnly);
					if( dev != null )
					{
						StationListObject stationListO = new StationListObject();
						stationListO.setIana_id(dev.getIanaId());
						stationListO.setSn(dev.getSn());
						stationListO = ACUtil.<StationListObject>getPoolObjectBySn(stationListO, StationListObject.class);
						
						if( stationListO != null )
						{
							List<StationList> stationList = stationListO.getStation_list();
							for( StationList station : stationList )
							{
								if( station.getStatus()!=null && station.getStatus().equals("active") )
									clientStatusMap.put(station.getClient_id(), station.getStatus());
							}
						}
					}
				}
				else
				{
					clientStatusMap = new HashMap<String,String>();
					clientUsageList = dailyClientUsagesDAO.getTopClientTotalUsage(param_networkId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000), bWifiOnly);
					List<Devices> deviceLst = NetUtils.getDeviceLstByNetId(param_orgId, param_networkId);
//					List<Devices> deviceLst = devicesDAO.getDevicesList(param_networkId);
					if( deviceLst != null )
					{
						StationListObject stationListO = null;
						for(Devices device : deviceLst)
						{
							stationListO = new StationListObject();
							stationListO.setIana_id(device.getIanaId());
							stationListO.setSn(device.getSn());
							stationListO = ACUtil.getPoolObjectBySn(stationListO, StationListObject.class);
							if( stationListO != null )
							{
								List<StationList> stationList = stationListO.getStation_list();
								if(stationList != null)
								{
									for(StationList station : stationList)
									{
										if(station.getStatus()!=null && station.getStatus().equals("active"))
											clientStatusMap.put(station.getClient_id(), station.getStatus());
									}
								}
							}
						}
					}
				}
				if (clientUsageList != null) {
					for (DailyClientUsageResult clientUsage : clientUsageList) {
						Double total = clientUsage.getRx() + clientUsage.getTx();
						totalUsage += total;
					}
				} else {
					if(log.isDebugEnabled())
						log.debug("clientUsageList = NULL");

					// return empty success response
					response.setData(resultList);
					response.setResp_code(ResponseCode.SUCCESS);
					return JsonUtils.toJson(response);
				}
				if(log.isDebugEnabled())
					log.debug("clientUsageList = " + clientUsageList);
				
				Json_Client_Usage clientUsageJson = null;

				int count = 0;
				for (DailyClientUsageResult clientUsage : clientUsageList) {
					count++;
					if (count > TOP_N_CLIENTS) {
						break;
					}
					clientUsageJson = new Json_Client_Usage();
					String client_id = PoolObjectDAO.convertToClientId(clientUsage.getMac(), null);
					clientUsageJson.setClient_id(client_id);
					if (clientUsage.getName() != null && !clientUsage.getName().isEmpty()) {
						clientUsageJson.setClient_name(clientUsage.getName());
					} else {
						if(clientUsage.getMac().indexOf(':')!=-1) { // this is real MAC
							clientUsageJson.setClient_name(clientUsage.getMac().toUpperCase() + " (" +clientUsage.getIp()+")"); 
						} else {
							clientUsageJson.setClient_name(clientUsage.getIp());
						}
					}
					
					if (clientUsage.getManufacturer() != null) {
						clientUsageJson.setManufacturer(clientUsage.getManufacturer());
					} else {
						clientUsageJson.setManufacturer("Unknown");
					}
					clientUsageJson.setUsage((float) ((clientUsage.getTx()+clientUsage.getRx()) * Utils.dataUsageSize1k));// return is kb
					clientUsageJson.setTx((float)(clientUsage.getTx()*Utils.dataUsageSize1k));
					clientUsageJson.setRx((float)(clientUsage.getRx()*Utils.dataUsageSize1k));
					if (totalUsage > 0) {
						clientUsageJson.setUsage_percent((float) (((float) ((clientUsage.getTx()+clientUsage.getRx()))) / totalUsage) * 100);
					} else {
						clientUsageJson.setUsage_percent(0f);
					}
					if(client_id != null)
					{
						if( clientStatusMap.get(client_id) != null )
							clientUsageJson.setOnline(true);
						else
							clientUsageJson.setOnline(false);
					}
					else
						clientUsageJson.setOnline(false);
					clientUsageJson.setMac(clientUsage.getMac().toUpperCase());
					
					resultList.add(clientUsageJson);
				}
			} catch (Exception e) {
				log.error("getTopClient: orgId=" + param_orgId + ", networkId=" + param_networkId + ", deviceId=" + param_deviceId + " - " + e, e);
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
				return JsonUtils.toJson(response);
			}

		} 
		else 
		{
			log.error("Parse time period error, result time is null");
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		response.setData(resultList);
		response.setResp_code(ResponseCode.SUCCESS);
		return JsonUtils.toJson(response);
	}

	public static String getManufacturerUsage(JsonNetworkRequest request, JsonResponse<HashMap<String,List<Json_Manufacturer_Usage>>> response)
	{
		String param_orgId = request.getOrganization_id();
		Integer param_networkId = request.getNetwork_id();
		Integer param_deviceId = request.getDevice_id();
		String param_type = request.getType();
		boolean bWifiOnly = false; 
		List<Json_Manufacturer_Usage> usageResultList = new ArrayList<Json_Manufacturer_Usage>();
		List<Json_Manufacturer_Usage> clientResultList = new ArrayList<Json_Manufacturer_Usage>();
		HashMap<String,List<Json_Manufacturer_Usage>> resultMap = new HashMap<String,List<Json_Manufacturer_Usage>>();
		if (param_type == null || param_type.equalsIgnoreCase("wireless")){
			bWifiOnly = true;
		}
		if(log.isInfoEnabled())
			log.info("getManufacturerUsage(" + param_orgId + "," + param_networkId + ","+param_type+","+param_deviceId+")");

		String start = request.getStart();
		String end = request.getEnd();
		
		SimpleDateFormat smf = new java.text.SimpleDateFormat(JsonUtils.jsonRequestDateFormat);
		smf.setTimeZone(TimeZone.getTimeZone("Utc"));
		Date startTime = null;
		Date endTime = null;
		try {
			startTime = smf.parse(start);
			endTime = smf.parse(end);
		} catch (ParseException e) {
			log.error("Parse time period error " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		boolean useReadonlyDB = true;
		List<DailyClientUsageResult> clientUsageList = null;
		DailyClientUsageResult totalClientUsage = null;
		try {
			DailyClientUsagesDAO dailyClientUsagesDAO = new DailyClientUsagesDAO(param_orgId, useReadonlyDB);
			Long totalClientCount = null;
			if( param_deviceId != null && param_deviceId != 0 )
			{
				totalClientUsage = dailyClientUsagesDAO.getTotalManufacturerUsageWithDevId(param_networkId, param_deviceId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000), bWifiOnly);
				totalClientCount = dailyClientUsagesDAO.getNetworkTotalClientCountWithDevId(param_networkId, param_deviceId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000), bWifiOnly);
			}
			else
			{
				totalClientUsage = dailyClientUsagesDAO.getTotalManufacturerUsage(param_networkId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000), bWifiOnly);
				totalClientCount = dailyClientUsagesDAO.getNetworkTotalClientCount(param_networkId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000), bWifiOnly);				
			}
				
			if(log.isDebugEnabled())
				log.debug("totalClientUsage = " + totalClientUsage);
			if (totalClientUsage == null) {
				// return empty success response
				response.setResp_code(ResponseCode.SUCCESS);
				return JsonUtils.toJson(response);
			}
			
			if( param_deviceId != null && param_deviceId != 0 )
			{
				clientUsageList = dailyClientUsagesDAO.getTopManufacturerUsageWithDevId(param_networkId, param_deviceId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000), bWifiOnly);
			}
			else
				clientUsageList = dailyClientUsagesDAO.getTopManufacturerUsage(param_networkId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000), bWifiOnly);
			if (clientUsageList != null) {
				Json_Manufacturer_Usage manuUsageJson = null;
				for (DailyClientUsageResult clientUsage : clientUsageList){
					manuUsageJson = new Json_Manufacturer_Usage();
					
					String manu = clientUsage.getManufacturer();
					if (manu == null)
						manu = "Unknown";
					manuUsageJson.setManufacturer(manu);
					
					manuUsageJson.setClients_count(clientUsage.getClient_cnt());
					if (totalClientCount != null && clientUsage.getClient_cnt() != null)
						manuUsageJson.setClients_percent(((float) clientUsage.getClient_cnt() / (float) totalClientCount) * 100);
					
					manuUsageJson.setUsage((float) (clientUsage.getTx() * Utils.dataUsageSize1k)); // tx is actually tx + rx for getTopManufacturerUsage call				
					manuUsageJson.setUsage_percent(0f);
					if (totalClientUsage.getTx() != 0)
						manuUsageJson.setUsage_percent((float) (clientUsage.getTx() / totalClientUsage.getTx()) * 100);

					usageResultList.add(manuUsageJson);						
				}				
			}
			
			if( param_deviceId != null && param_deviceId != 0 )
				clientUsageList = dailyClientUsagesDAO.getTopManufacturerClientWithDevId(param_networkId, param_deviceId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000), bWifiOnly);
			else
				clientUsageList = dailyClientUsagesDAO.getTopManufacturerClient(param_networkId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000), bWifiOnly);
			if (clientUsageList != null) {
				Json_Manufacturer_Usage manuUsageJson = null;
				for (DailyClientUsageResult clientUsage : clientUsageList){
					manuUsageJson = new Json_Manufacturer_Usage();

					String manu = clientUsage.getManufacturer();
					if (manu == null)
						manu = "Unknown";
					manuUsageJson.setManufacturer(manu);
					
					manuUsageJson.setClients_count(clientUsage.getClient_cnt());
					if (totalClientCount != null && clientUsage.getClient_cnt() != null)
						manuUsageJson.setClients_percent(((float) clientUsage.getClient_cnt() / (float) totalClientCount) * 100);
					
					manuUsageJson.setUsage((float) (clientUsage.getTx() * Utils.dataUsageSize1k)); // tx is actually tx + rx for getTopManufacturerUsage call
					manuUsageJson.setUsage_percent(0f);
					if (totalClientUsage.getTx() != 0)
						manuUsageJson.setUsage_percent((float) (clientUsage.getTx() / totalClientUsage.getTx()) * 100);

					clientResultList.add(manuUsageJson);						
				}				
			}
			
			resultMap.put("usage", usageResultList);
			resultMap.put("client", clientResultList);
			if(log.isInfoEnabled())
				log.info("manufacture usage result : " + resultMap);
			response.setData(resultMap);
			response.setResp_code(ResponseCode.SUCCESS);
			return JsonUtils.toJson(response);
		} catch (Exception e) {
			log.error("getManufacturerUsage: orgId=" + param_orgId + ", networkId=" + param_networkId + ", deviceId=" + param_deviceId + " - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
	}
	
	
	public static String getUsage(JsonNetworkRequest request, JsonResponse<List<Json_Usage_Count>> response) {
		String param_orgId = request.getOrganization_id();
		Integer param_networkId = request.getNetwork_id();
		Integer param_deviceId = request.getDevice_id();

		if(log.isInfoEnabled())
			log.info("getUsage(" + param_orgId + "," + param_networkId + "," + param_deviceId +")");

		String start = request.getStart();
		String end = request.getEnd();

		SimpleDateFormat smf = new java.text.SimpleDateFormat(JsonUtils.jsonRequestDateFormat);
		smf.setTimeZone(TimeZone.getTimeZone("Utc"));
		Date startTime = null;
		Date endTime = null;
		try {
			startTime = smf.parse(start);
			endTime = smf.parse(end);
		} catch (Exception e) {
			log.error("Parse time period error: start="+start+", end="+end+" " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
		
		boolean useReadonlyDB = true;

		if (startTime != null && endTime != null) 
		{
			List<DailyDevUsageResult> devUsageList = null;
			List<Json_Usage_Count> resultList = null;
			try 
			{
				DailyDeviceUsagesDAO dailyDeviceUsagesDAO = new DailyDeviceUsagesDAO(param_orgId, useReadonlyDB);
				if( param_deviceId != null && param_deviceId != 0 )
				{
					devUsageList = dailyDeviceUsagesDAO.getDailyDeviceUsageWithDevId(param_networkId, param_deviceId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000));
				}
				else
				{
					devUsageList = dailyDeviceUsagesDAO.getDailyDeviceUsage(param_networkId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000));
				}
				
				resultList = new ArrayList<Json_Usage_Count>();
				int s = (int) (startTime.getTime() / 1000);
				int e = (int) (endTime.getTime() / 1000);
				int cursor = 0;
				Json_Usage_Count usage_count_json = null;
				DailyDevUsageResult usageResult = null;
				
				if( devUsageList != null && devUsageList.size() > 0 )
				{
					for( int i = s; i <= e; i = i + 86400 )
					{
						usageResult = devUsageList.get(cursor);
						usage_count_json = new Json_Usage_Count();
						usage_count_json.setDay(smf.format(new Date((long)i*1000)));
						if( (usageResult != null) && (i == usageResult.getUnixtime()) )
						{
							usage_count_json.setValue((long)((usageResult.getRx()+usageResult.getTx())*Utils.dataUsageSize1k));
							if( cursor < devUsageList.size() - 1 )
								cursor++;
						}
						resultList.add(usage_count_json);
					}
				}
				else
				{
					for( int i = s; i <= e; i = i + 86400 )
					{
						usage_count_json = new Json_Usage_Count();
						usage_count_json.setDay(smf.format(new Date((long)i*1000)));
						resultList.add(usage_count_json);
					}
				}
				
				response.setData(resultList);
				response.setResp_code(ResponseCode.SUCCESS);
				return JsonUtils.toJson(response);
			} 
			catch (Exception e) 
			{
				log.error("getUsage: orgId=" + param_orgId + ", networkId=" + param_networkId + ", deviceId=" + param_deviceId + " - " + e, e);
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
				return JsonUtils.toJson(response);
			}
		} 
		else 
		{
			log.error("Parse time period error, result time is null");
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
	}

	public static String getClientCount(JsonNetworkRequest request, JsonResponse<List<Json_Usage_Count>> response) {
		String param_orgId = request.getOrganization_id();
		Integer param_networkId = request.getNetwork_id();
		Integer param_deviceId = request.getDevice_id();
		String param_type = request.getType();
		boolean bWifiOnly = false; 
		if (param_type == null || param_type.equalsIgnoreCase("wireless")){
			bWifiOnly = true;
		}

		String start = request.getStart();
		String end = request.getEnd();
		
		if(log.isDebugEnabled())
			log.debugf("getClientCount(%s %d %s %s %s)", param_orgId , param_networkId , param_type, start, end);

		SimpleDateFormat smf = new java.text.SimpleDateFormat(JsonUtils.jsonRequestDateFormat);
		smf.setTimeZone(TimeZone.getTimeZone("Utc"));
		Date startTime = null;
		Date endTime = null;
		try {
			startTime = smf.parse(start);
			endTime = smf.parse(end);
		} catch (ParseException e) {
			log.error("Parse time period error " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		boolean useReadonlyDB = true;

		if (startTime != null && endTime != null) {
			List<DailyClientUsageResult> userCountList = null;
			List<Json_Usage_Count> resultList = null;
			try {
				DailyClientUsagesDAO dailyClientUsagesDAO = new DailyClientUsagesDAO(param_orgId,useReadonlyDB);							
				userCountList = dailyClientUsagesDAO.getDailyClientCount(param_networkId, param_deviceId, (int) (startTime.getTime() / 1000), (int) (endTime.getTime() / 1000), bWifiOnly);
				
				if (log.isDebugEnabled())
					log.debugf("userCountList=%s", userCountList);
				
				//start change
				resultList = new ArrayList<Json_Usage_Count>();
				int s = (int) (startTime.getTime() / 1000);
				int e = (int) (endTime.getTime() / 1000);
				int cursor = 0;
				Json_Usage_Count client_count_json = null;
				if( userCountList != null && userCountList.size() > 0 )
				{
					for( int i = s; i <= e; i = i + 86400 )
					{
						client_count_json = new Json_Usage_Count();
						client_count_json.setDay(smf.format(new Date((long)i*1000)));
						if (log.isDebugEnabled())
							log.debugf("i %d cursor time %d ", i, userCountList.get(cursor).getUnixtime());
						if( i == userCountList.get(cursor).getUnixtime() )
						{
							client_count_json.setValue(userCountList.get(cursor).getClient_cnt());
							if( cursor < userCountList.size() - 1 )
								cursor++;
						}
						resultList.add(client_count_json);
					}
				}
				else
				{
					for( int i = s; i <= e; i = i + 86400 )
					{
						client_count_json = new Json_Usage_Count();
						client_count_json.setDay(smf.format(new Date((long)i*1000)));
						resultList.add(client_count_json);
					}
				}
				
				response.setData(resultList);
				response.setResp_code(ResponseCode.SUCCESS);
				return JsonUtils.toJson(response);
				// return JsonUtils.toJson(resultList);
			} catch (Exception e) {
				log.error("getClientCount: orgId=" + param_orgId + ", networkId=" + param_networkId + ", deviceId=" + param_deviceId + " - " + e, e);
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
				return JsonUtils.toJson(response);
			}
		} else {
			log.error("Parse time period error, result time is null");
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
	}
	

	public static String moveDevicesNetwork(JsonNetworkRequest request, JsonResponse response)
	{
		String param_orgId = request.getOrganization_id();
		int param_networkId = request.getNetwork_id();
		List<Integer> devIdList = request.getDevice_ids();
		int param_targetNetworkId = request.getTarget_network_id();
		ConfigurationSettingsUtils settingsUtils = null;
		
		Networks target_network = null;
		Networks orign_network =null;
		List<Integer>hub_list = null;
		List<Integer>hub_list1 = null;
		log.warnf("INFO MD10001 moveDevicesNetwork - Device start moved orgId %s netId %d devIdList %s targetnetUd %d", param_orgId, param_networkId, devIdList, param_targetNetworkId);
				
		try {			
			
			/* data validation */			
			OrganizationsDAO organizationsDAO = new OrganizationsDAO(param_orgId);
			NetworksDAO networksDAO = new NetworksDAO(param_orgId);
			Organizations organizations = organizationsDAO.findById(param_orgId);
			DeviceUpdatesDAO deviceUpdateDAO = new DeviceUpdatesDAO(param_orgId);
			ConfigurationRadioChannelsDAO  confiRadioDAO= new ConfigurationRadioChannelsDAO(param_orgId);
			ConfigurationPepvpnsDAO configurationPepvpnsDAO = new ConfigurationPepvpnsDAO(param_orgId);
			ConfigurationSsidsDAO configurationSsidsDAO = new ConfigurationSsidsDAO(param_orgId);
			List<Integer> full_hub_list = new ArrayList<Integer>();//the hub_list of this network

			DevicesDAO devicesDAO = new DevicesDAO(param_orgId);
			List<Devices> devList = new ArrayList<Devices>();//the devices which act as hub in the network
			Devices dev = null;
			Devices hub = null;
			response.setResp_code(ResponseCode.SUCCESS);
						
			target_network = networksDAO.findById(param_targetNetworkId);
			if (param_networkId!=0)
			{
				orign_network = networksDAO.findById(param_networkId);
				
				hub_list = configurationPepvpnsDAO.getHubandHahubDeviceIdFromNetworkId(orign_network.getId(), true);
				if (hub_list!=null && hub_list.size()>0)
					full_hub_list.addAll(hub_list);
				hub_list1 = configurationPepvpnsDAO.getHubandHahubDeviceIdFromNetworkId(param_targetNetworkId, true);
				if(hub_list1 !=null && hub_list1.size()>0)
					full_hub_list.addAll(hub_list1);
			}
			
			boolean isValid = true;
			for (Integer devId : devIdList) 
			{	
				dev = devicesDAO.findById(devId);
				if (dev==null)
				{
					isValid = false;
					break;
				}

				if (param_networkId==0)
				{
					orign_network = networksDAO.findById(dev.getNetworkId());
					if (orign_network==null)
					{
						isValid = false;
						break;
					}
					
					hub_list = configurationPepvpnsDAO.getHubandHahubDeviceIdFromNetworkId(orign_network.getId(), true);
					if (hub_list!=null && hub_list.size()>0)
						full_hub_list.addAll(hub_list);
					hub_list1 = configurationPepvpnsDAO.getHubandHahubDeviceIdFromNetworkId(param_targetNetworkId, true);
					if(hub_list1 !=null && hub_list1.size()>0)
						full_hub_list.addAll(hub_list1);
				}
				
				if (target_network != null && orign_network != null){
					if (target_network.getNetworkType() != null && orign_network.getNetworkType() != null){
						if (!target_network.getNetworkType().equals(orign_network.getNetworkType())){
							log.warnf("INFO MD10001 - Device(s) cannot be moved due to different network type!!!", request);
							response.setResp_code(ResponseCode.INVALID_OPERATION);
							response.setMessage("Device cannot be moved due to different network type!!!");
							return JsonUtils.toJson(response);
						}
					}
				} else {
					response.setResp_code(ResponseCode.INVALID_INPUT);
					return JsonUtils.toJson(response);
				}
				
				
				//-----check whether it is a hub
				if(configurationPepvpnsDAO.isEnabledHubOrHaHubInAnyNetwork(devId)) 
				{
					dev = devicesDAO.findById(devId);
					log.warnf("INFO MD10001 - HUB_CANNOT_REMOVE");
					response.setMessage("HUB_CANNOT_REMOVE");
					response.setResp_code(ResponseCode.INVALID_OPERATION);
					devList.add(dev);
					isValid = false;
				}
				//-----check whether it is a master device
				if(devId == orign_network.getMasterDeviceId())
				{
					log.warnf("INFO MD10001 - MASTER_DEVICE_CANNOT_REMOVE:"+devId);
					response.setMessage("MASTER_DEVICE_CANNOT_REMOVE:"+devId);
					response.setResp_code(ResponseCode.INVALID_OPERATION);
					isValid = false;
				}
			}
			
			/* proceeed update */
			if (isValid) {
				log.warnf("INFO MD10001 - VALID");
				for (Integer devId : devIdList) {
					dev = devicesDAO.findById(devId);
					if (dev != null) {
						settingsUtils = new ConfigurationSettingsUtils(param_orgId, dev.getNetworkId(), devId);
						//RadioConfigUtils.removeDevConfigSettings(dev, param_orgId);	/* remove settings from old network */
						settingsUtils.removeDevConfigSettings();
						NetUtils.updateNetworkToCache(dev, param_orgId, dev.getNetworkId(), param_targetNetworkId);
						//RadioConfigUtils.initDevConfigSettings(dev, param_orgId);
						settingsUtils = new ConfigurationSettingsUtils(param_orgId, param_networkId, devId);
						settingsUtils.initDevConfigSettings();
						
						//dev.setNetworks(network);
						dev.setNetworkId(target_network.getId());
						if(target_network.getLatitude() != null &&target_network.getLongitude() != null)
						{
							dev.setLatitude(target_network.getLatitude());
							dev.setLongitude(target_network.getLongitude());
							if(target_network.getAddress() != null)
							{
							dev.setAddress(target_network.getAddress());
							}
						}
						else
						{
							if(organizations.getAddress() != null)
							{
								dev.setAddress(organizations.getAddress());
							}
							if(organizations.getLatitude() != null)
							{
								dev.setLatitude(organizations.getLatitude());
							}
							if(organizations.getLongitude() != null)
							{
								dev.setLongitude(organizations.getLongitude());
							}
						}

						dev.setDev_level_cfg("");
						ConfigurationSsids cfgObj = configurationSsidsDAO.getCfgByNetworkIdAndDeviceId(dev.getNetworkId(), dev.getId());
						if( cfgObj != null )
							configurationSsidsDAO.delete(cfgObj);
																		
						devicesDAO.update(dev);
						// update cache right the way
						DevOnlineObject devOnline = new DevOnlineObject();
						devOnline.setSn(dev.getSn());
						devOnline.setIana_id(dev.getIanaId());
						DevOnlineObject devOnlineCache = ACUtil.getPoolObjectBySn(devOnline, DevOnlineObject.class);
						if (devOnlineCache != null) {
							devOnlineCache.setNetwork_id(param_targetNetworkId);
							devOnlineCache.setFw_ver(null);
							devOnlineCache.setConf_checksum(null);
							devOnlineCache.setCert_checksum(null);
							devOnlineCache.setDdns_enabled(false);
							
							ACUtil.cachePoolObjectBySn(devOnlineCache, DevOnlineObject.class);
							log.warnf("INFO MD10001 - moveDevicesNetwork - Retain dev %s online to %s", devOnlineCache.getSn(), devOnlineCache.isOnline());
						}
												
						//RadioConfigUtils.initDevConfigSettings(dev, param_orgId);	/* create settings from old network */
						
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_ONLINE, JsonUtils.genServerRef(), dev.getIanaId(), dev.getSn());
						
						log.warnf("INFO MD10001 - Device moved from " + orign_network.getId() + " to " + param_targetNetworkId);
					}
					confiRadioDAO.removeRadioChannel(devId);	
				}
				
				if (devIdList!=null && !devIdList.isEmpty() && target_network!=null && !NetworkUtils.isMasterGroupConfigEnabledNetwork(target_network))
				{
					//deviceUpdateDAO.incrementConfUpdateForDevLst(devIdList, response.getServer_ref(), "devices move network");
					new ConfigUpdatePerDeviceTask(param_orgId, param_targetNetworkId).performConfigUpdateNow(devIdList, response.getServer_ref(), 
							CONFIG_UPDATE_REASON.dev_move_network.toString());
				}
				
				//update the hub and hahub info of this network
				if(full_hub_list !=null && full_hub_list.size() > 0)
				{
					log.warnf("INFO MD10001 - hubidlst:"+full_hub_list);
					//devIdList.addAll(hub_list);
					for (Integer hubId:full_hub_list)
					{
						hub = devicesDAO.findById(hubId);
						if (hub!=null)
						{
							new ConfigUpdatePerDeviceTask(param_orgId, hub.getNetworkId()).performConfigUpdateNow(Arrays.asList(hubId), response.getServer_ref(), 
									CONFIG_UPDATE_REASON.dev_move_network_hub.toString());
						}
						else
						{
							log.warnf("hubId %d is not found!", hubId);
						}
					}
				}
			}
			else
			{
				response.setResp_code(ResponseCode.INVALID_OPERATION);
				response.setData(devList);
				log.warnf("INFO MD10001 invalid - hub list or others");
			}
			
			NetworkUtils.updateNetworksFeature(param_orgId, param_networkId);
			NetworkUtils.updateNetworksFeature(param_orgId, param_targetNetworkId);
			//response.setResp_code(ResponseCode.SUCCESS);
			return JsonUtils.toJson(response);

		} catch (Exception e) {
			log.error("MD10001 moveDevicesNetwork - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

	}

	public static String getNetworkTime(JsonNetworkRequest request, JsonResponse<Json_Network_Time> response)
	{

		final boolean useReadonlyDB = true;
		String param_orgId = request.getOrganization_id();
		Integer param_networkId = request.getNetwork_id();
		
		String datetime = null;
		Json_Network_Time jsonTs = new Json_Network_Time();

		try
		{
			NetworksDAO networkDAO = new NetworksDAO(param_orgId, useReadonlyDB);
			Networks network = networkDAO.findById(param_networkId);
			String timezone = network.getTimezone();

			Date cal = new Date();
			// cal.setTimeZone(TimeZone.getTimeZone(DateUtils.getTimezoneFromId(Integer.parseInt(timezone))));
			//System.out.println("---------TimeZone is:" + timezone + "-----------");

			SimpleDateFormat time_fmat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			time_fmat.setTimeZone(TimeZone.getTimeZone(DateUtils.getTimezoneFromId(Integer.parseInt(timezone))));
			// datetime = time_fmat.format(DateUtils.offsetFromUtcTimeZone(cal.getTime(),
			// DateUtils.getTimezoneFromId(Integer.parseInt(timezone))));
			datetime = time_fmat.format(cal);
			jsonTs.setTimestamp(datetime);
			response.setData(jsonTs);
			response.setResp_code(ResponseCode.SUCCESS);
		} catch (Exception e)
		{
			log.error("getNetworkTime - " + param_orgId+"_"+param_networkId, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		return JsonUtils.toJson(response);
	}
	
	public static Json_Device_Gps_Info getGpsLocationForLatest(Json_Device_Gps_Info param_gpsObj)
	{
		if (param_gpsObj == null)
		{	
			log.warnf("getGpsLocationForStart  param_gpsObj= %s", param_gpsObj);
			return null;	
		}

		final boolean bReadOnlyDb = true;
		String param_orgId = param_gpsObj.getParam_orgId();
		Devices dev = param_gpsObj.getDev();
		Networks net = param_gpsObj.getNet();
		List<DeviceGpsLocations> devLocLst = null;
		Json_Device_Gps_Info gpsObj = new Json_Device_Gps_Info();
		Integer firstConsolidatedTime = null;
		Integer param_devId = dev.getId();
		/* **************get the last location point, get from cache and DB***********/
		try
		{
			List<DeviceGpsLocations> devLocListFromDB = null;
			DeviceGpsLocationsDAO deviceLocDAO = new DeviceGpsLocationsDAO(param_orgId, bReadOnlyDb);
			DeviceGpsRecordsDAO deviceRecordsDAO = new DeviceGpsRecordsDAO(param_orgId,bReadOnlyDb);
			DeviceGpsLocations devLocFromDB = null;
			DeviceGpsLocations latestDevLoc = null;
			boolean bLastLocIsNull = false; // for adding a null location at the end

			/* ************ BEGIN DB Part: load the gps data ***** */
			firstConsolidatedTime = LocationUtils.getGpsLocationConsolidated(param_orgId, net.getId(), param_devId);
			log.debug("The parameter orgId= "+param_orgId+" netId= "+net.getId()+" firstConsolidatedTime= "+ firstConsolidatedTime);	
		
			if (firstConsolidatedTime == null)
			{
				log.debug("case 1 deviceLocDAO.getLatestNotNullLocation() firstConsolidatedTime= "+firstConsolidatedTime+" NetId=" +dev.getNetworkId()+" devId="+param_devId);
				devLocListFromDB = deviceLocDAO.getLocationsByDeviceIdWithStarttime(param_devId, null);
				if (devLocListFromDB != null && !devLocListFromDB.isEmpty())
				{
					/* *** the list should be only has one point which is the latest point *** */
					devLocFromDB = devLocListFromDB.get(0);
					if (devLocFromDB.getLatitude() == null || devLocFromDB.getLongitude() == null){
						latestDevLoc = deviceLocDAO.getLatestNotNullLocation(dev.getId());
						bLastLocIsNull = true;
					} else {
						latestDevLoc = devLocFromDB;
					}
				}
			}
			else
			{
				log.debug("case 2 deviceRecordsDAO.getLatestNotNullLocation() firstConsolidatedTime= "+firstConsolidatedTime+" NetId=" +dev.getNetworkId()+" devId="+param_devId);
				devLocListFromDB = deviceRecordsDAO.getLocationsRecordsWithStarttime(param_devId, null);
				if (devLocListFromDB != null && !devLocListFromDB.isEmpty())
				{
					devLocFromDB = devLocListFromDB.get(0);
					if (devLocFromDB.getLatitude() == null || devLocFromDB.getLongitude() == null){
						latestDevLoc = deviceRecordsDAO.getLatestNotNullLocation(dev.getId());
						bLastLocIsNull = true;
					} else {
						latestDevLoc = devLocFromDB;
					}
				}
			}
			/* ************ END DB Part ********************************* */
			
			if(log.isInfoEnabled())
				log.info("getGpsLocationV2: sn="+dev.getSn()+", devLocListFromDB: " + devLocListFromDB +", latestDevLoc: " + latestDevLoc);
			 
			DevLocationsReportObject devLocReportSample = new DevLocationsReportObject();
			devLocReportSample.setIana_id(dev.getIanaId());
			devLocReportSample.setSn(dev.getSn());
			DevLocationsReportObject devLocReportFromCache = ACUtil.getPoolObjectBySn(devLocReportSample, DevLocationsReportObject.class);
			
			if ( devLocReportFromCache != null )
			{
				 if( devLocReportFromCache.getLocation_list() != null && !devLocReportFromCache.getLocation_list().isEmpty())
				 {
					if(log.isDebugEnabled())
						log.debug("getGpsLocationV2: sn="+dev.getSn()+", devLocReportFromCache: "+devLocReportFromCache);
					
					List<LocationList> locationListFromCache = devLocReportFromCache.getLocation_list();
					List<LocationList> localList = new ArrayList<LocationList>();
					localList.addAll(locationListFromCache);
					LocationList latestFromReportCache = localList.get(localList.size()-1);//list is sorted in adding
					if (latestFromReportCache.getLongitude() == null || latestFromReportCache.getLatitude() == null)
					{
						/* list should not be empty and at least has 60 points,and there should be no consecutive null location points in cache */
						latestFromReportCache = localList.get(localList.size()-2);
						bLastLocIsNull = true;
					}
					else
					{
						bLastLocIsNull = false;
					}
					latestDevLoc = new DeviceGpsLocations();
					DeviceGpsLocationsId locObjId = new DeviceGpsLocationsId();
					locObjId.setNetworkId(net.getId());
					locObjId.setDeviceId(dev.getId());
					locObjId.setUnixtime(latestFromReportCache.getTimestamp().intValue());
					latestDevLoc.setId(locObjId);
					latestDevLoc.setLatitude(latestFromReportCache.getLatitude());
					latestDevLoc.setLongitude(latestFromReportCache.getLongitude());
					latestDevLoc.setAltitude(latestFromReportCache.getAltitude());
					latestDevLoc.setSpeed(latestFromReportCache.getSpeed());
					long network_time = 0;
					if( latestFromReportCache.getTimestamp() != null )
						network_time = (long)latestFromReportCache.getTimestamp()*1000;
					Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
					latestDevLoc.setDatetime(datetime);
					if(log.isDebugEnabled())
						log.debug("getGpsLocationV2: sn="+dev.getSn()+", latestDevLoc: "+latestDevLoc);
				}
			}
		
			DevLocationsObject devLocationsExample = new DevLocationsObject();
			devLocationsExample.setIana_id(dev.getIanaId());
			devLocationsExample.setSn(dev.getSn());
			DevLocationsObject devLocationsObject = ACUtil.getPoolObjectBySn(devLocationsExample, DevLocationsObject.class);
			
			if (devLocationsObject != null)
			{
				if( devLocationsObject.getLocation_list() != null && !devLocationsObject.getLocation_list().isEmpty())
				{
					if(log.isDebugEnabled())
						log.debug("getGpsLocationV2: sn="+dev.getSn()+", devLocationsObject: " + devLocationsObject);
					List<LocationList> locationListFromCache = devLocationsObject.getLocation_list();
					List<LocationList> localList = new ArrayList<LocationList>();
					localList.addAll(locationListFromCache);
					LocationList latestFromReportCache = localList.get(localList.size()-1);//list is sorted in adding
					boolean bSkipCheck = false;//is real time query only has one point and is null, skip to check
					if (latestFromReportCache.getLatitude() == null || latestFromReportCache.getLongitude() == null)
					{
						if (localList.size() > 1)
						{
							latestFromReportCache = localList.get(localList.size()-2);
						}
						else
						{
							bSkipCheck = true;
						}
						bLastLocIsNull = true;
					}
					else
					{
						bLastLocIsNull = false;
					}
					
					if (!bSkipCheck)
					{
						if (latestDevLoc != null){
							if (latestFromReportCache.getTimestamp().intValue() > latestDevLoc.getId().getUnixtime())
							{
								//realtime cache is latest one
								latestDevLoc.getId().setUnixtime(latestFromReportCache.getTimestamp().intValue());
								latestDevLoc.setLatitude(latestFromReportCache.getLatitude());
								latestDevLoc.setLongitude(latestFromReportCache.getLongitude());
								latestDevLoc.setAltitude(latestFromReportCache.getAltitude());
								latestDevLoc.setSpeed(latestFromReportCache.getSpeed());
								long network_time = 0;
								if( latestFromReportCache.getTimestamp() != null )
									network_time = (long)latestFromReportCache.getTimestamp()*1000;
								Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
								latestDevLoc.setDatetime(datetime);
							}
					}
					else
					{
							latestDevLoc = new DeviceGpsLocations();
							DeviceGpsLocationsId locObjId = new DeviceGpsLocationsId();
							locObjId.setNetworkId(net.getId());
							locObjId.setDeviceId(dev.getId());
							locObjId.setUnixtime(latestFromReportCache.getTimestamp().intValue());
							latestDevLoc.setId(locObjId);
							latestDevLoc.setLatitude(latestFromReportCache.getLatitude());
							latestDevLoc.setLongitude(latestFromReportCache.getLongitude());
							latestDevLoc.setAltitude(latestFromReportCache.getAltitude());
							latestDevLoc.setSpeed(latestFromReportCache.getSpeed());
							long network_time = 0;
							if( latestFromReportCache.getTimestamp() != null )
								network_time = (long)latestFromReportCache.getTimestamp()*1000;
							Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
							latestDevLoc.setDatetime(datetime);
						}
					}
				}
			}
		
			devLocLst = new ArrayList<DeviceGpsLocations>();
			
			if (latestDevLoc != null)
			{
				if(log.isDebugEnabled())
					log.debug("getGpsLocationV2: sn="+dev.getSn()+", has a gps location in DB or cache: "+latestDevLoc);
				
				devLocLst.add(latestDevLoc);
				if (bLastLocIsNull)
				{
					DeviceGpsLocations nullDevLoc = new DeviceGpsLocations();
					DeviceGpsLocationsId locObjId = new DeviceGpsLocationsId();
					locObjId.setNetworkId(net.getId());
					locObjId.setDeviceId(dev.getId());
					locObjId.setUnixtime(latestDevLoc.getId().getUnixtime()+1);
					nullDevLoc.setId(locObjId);
					long network_time = 0;
					network_time = (long)(latestDevLoc.getId().getUnixtime()+1)*1000;
					Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
					nullDevLoc.setDatetime(datetime);
					devLocLst.add(nullDevLoc);
				}
			}
			else
			{
				if(log.isDebugEnabled())
					log.debug("NetworkWS.getGpsLocationForLatest sn="+dev.getSn()+", no gps data, lat="+dev.getLatitude()+", long="+dev.getLongitude());
				
				//all are null, never have a gps location in DB or cache
				if (dev.getLatitude() != 0 || dev.getLongitude() != 0){
					latestDevLoc = new DeviceGpsLocations();
					DeviceGpsLocationsId locObjId = new DeviceGpsLocationsId();
					locObjId.setNetworkId(net.getId());
					locObjId.setDeviceId(dev.getId());
					Date curTime = DateUtils.getUtcDate();
					locObjId.setUnixtime((int)(curTime.getTime()/1000));
					latestDevLoc.setId(locObjId);
					Date datetime = DateUtils.offsetFromUtcTimeZoneId(curTime, net.getTimezone());
					latestDevLoc.setDatetime(datetime);
					latestDevLoc.setLatitude(dev.getLatitude());
					latestDevLoc.setLongitude(dev.getLongitude());
					devLocLst.add(latestDevLoc);
					gpsObj.setbIsStatic(true);//bIsStatic = true;
				}
			}
		
			gpsObj.setLocLst(devLocLst);
		
		if(log.isDebugEnabled())
			log.debug("NetworkWS.getGpsLocationForLatest sn="+dev.getSn()+", latestDevLoc = "+latestDevLoc+" bLastLocIsNull = "+bLastLocIsNull);
		} catch (Exception e) {
			log.error("NetworkWS.getGpsLocationForLatest orgId=" + param_orgId + ", networkId=" + net.getId() + ", vdevice=" + dev + " - " + e, e);			
			return gpsObj;
		}
		
		return gpsObj;
	}
	
	public static String getGpsLocationV2(JsonNetworkRequest request, JsonResponse<List<Json_Devices>> response)
	{

		final boolean bReadOnlyDb = true;
		
		Integer param_networkId = request.getNetwork_id();
		String param_orgId = request.getOrganization_id();
		Date param_start = request.getStart_time();
		List<Integer> visible_devices = request.getVisible_devices();
		
		List<Json_Devices> result = new ArrayList<Json_Devices>();
		List<Devices> devList = new ArrayList<Devices>();
		log.debugf("11812 NetworkWS.getGpsLocationV2() called with param_orgId=%s param_devId=%d param_start=%s",param_orgId,param_networkId,param_start);

		try {
			
			DeviceFeaturesDAO deviceFeaturesDAO = new DeviceFeaturesDAO(param_orgId, bReadOnlyDb);
			DevicesDAO deviceDAO = new DevicesDAO(param_orgId, bReadOnlyDb);
			response.setResp_code(ResponseCode.SUCCESS);
			ProductsDAO productsDAO = new ProductsDAO(bReadOnlyDb);
			
			if(log.isInfoEnabled())
				log.info("NetworkWs.getGpsLocationV2: Get gps request:"+new Date()+", networkId="+ param_networkId+", start="+param_start +", devlst="+visible_devices);
			if (param_networkId == 0)
			{
				if( visible_devices != null && !visible_devices.isEmpty() )
					devList = deviceDAO.getDevicesList(visible_devices);
				else	
					devList = deviceDAO.getAllDevices();
			}
			else
			{
				if( visible_devices != null && !visible_devices.isEmpty() )
					devList = deviceDAO.getDevicesList(visible_devices);
				else
					devList = deviceDAO.getDevicesList(param_networkId);
			}
			List<Devices> newdevList = new ArrayList<Devices>();
			
			for (Devices dev : devList)
			{
				boolean gps_tracking_disabled = false;
				
				 if (NetworkUtils.isGpsTrackingDisabled(param_orgId, dev.getNetworkId()))//Attention: param_networkId could be 0
				 {
					 gps_tracking_disabled=true;
					 
				 }
				 if (!gps_tracking_disabled)
				 {
					 /* *** If Gps_tracking_disabled by group wide setting *** */
					 newdevList.add(dev);
				 }
				 log.debugf("NetworkWsHandler.getGpsLocationV2 gps_tracking_disabled=%s devId=%s netId=%d", gps_tracking_disabled,dev.getId(),dev.getNetworkId());
				 
			}
			HashMap<Integer, Boolean> featuresMap = deviceFeaturesDAO.getGpsFeatureMap();
			
			for (Devices dev : newdevList)
			{
				if (dev.isActive())
				{
					List<Json_Device_Locations> devLocJsonLst = new ArrayList<Json_Device_Locations>();
					NetworksDAO netDAO = new NetworksDAO(param_orgId, bReadOnlyDb);
					Networks net = netDAO.getNetworksByDevId(dev.getId());

					 List<DeviceGpsLocations> devLocLst = null;
					 boolean bIsStatic = false;
					
					 boolean is_low_bandwidth_mode = false;
					 if (NetworkUtils.isLowBandwidthMode(param_orgId, dev.getNetworkId()))
							is_low_bandwidth_mode = true;
					 					 
					 if( param_start != null )
					 {
						 /* ***** getGpsLocationForStart: getGpsLocation with param_start ***** */

							Json_Device_Gps_Info param_gpsObj = new Json_Device_Gps_Info();
								param_gpsObj.setParam_orgId(param_orgId);
								param_gpsObj.setNet(net);
								param_gpsObj.setParam_start(param_start);
								param_gpsObj.setDev(dev);

							Json_Device_Gps_Info gpsObj = getGpsLocationForStart(param_gpsObj);
							if (gpsObj != null)
							{
								devLocLst = gpsObj.getLocLst();
							}
					 }
					 else // param_start == null
					 {
						 /* ***** getGpsLocationForLatest: getGpsLocation without param_start then return latest ***** */
						 Json_Device_Gps_Info param_gpsObj = new Json_Device_Gps_Info();	
						 	param_gpsObj.setParam_orgId(param_orgId);
							param_gpsObj.setNet(net);
							param_gpsObj.setDev(dev);
						 Json_Device_Gps_Info gpsObj = getGpsLocationForLatest(param_gpsObj);
							if (gpsObj != null)
							{
								devLocLst = gpsObj.getLocLst();
								bIsStatic= gpsObj.isbIsStatic();
							}
					 }
					 
					 
					for (DeviceGpsLocations devLoc : devLocLst)
					{
						Json_Device_Locations devLocJson = new Json_Device_Locations();
						try 
						{
							if( devLoc.getLatitude() != null ){
								devLocJson.setLatitude(devLoc.getLatitude());
								devLocJson.setExist(true);
							}
							if( devLoc.getLongitude() != null ){
								devLocJson.setLongitude(devLoc.getLongitude());
								devLocJson.setExist(true);
							}
							if (bIsStatic){
								devLocJson.setIsStatic(true);								
							}
						} catch (NullPointerException e)
						{
							log.error("Exception To Be Investigated, "+e, e);
						}
						long time = (long)devLoc.getId().getUnixtime()*1000;
						Date timestamp = new Date(time);
						devLocJson.setTimestamp(DateUtils.offsetFromUtcTimeZoneId(timestamp, net.getTimezone()));
						devLocJsonLst.add(devLocJson);
						if(log.isDebugEnabled())
							log.debug("getGpsLocationV2: sn="+dev.getSn()+", devLocJsonLst.add: lat=" + devLocJson.getLatitude() + ", long=" + devLocJson.getLongitude());
					}
					if(log.isDebugEnabled())
						log.debug("getGpsLocationV2: sn="+dev.getSn()+", devLocJsonLst: " + devLocJsonLst);
					 
					Json_Devices devJson = new Json_Devices();
					devJson.setId(dev.getId());
					devJson.setNetwork_id(dev.getNetworkId());
					devJson.setLocations(devLocJsonLst);
					devJson.setName(dev.getName());
					devJson.setSn(dev.getSn());
				
					DevOnlineObject devOnlineObject = PoolObjectDAO.getDevOnlineObject(dev);
					if (devOnlineObject != null)
					{
						if (devOnlineObject.isOnline())
						{
							devJson.setStatus("online");
							//fetch command only for online device
							if(featuresMap.get(dev.getId()) != null && featuresMap.get(dev.getId()) == true && is_low_bandwidth_mode == false)
								ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_LOCATIONS, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn());
						}
						else
						{
							devJson.setStatus("offline");
						}
						DevDetailObject devDetailObject = PoolObjectDAO.getDevDetailObject(dev);
	
						if (devDetailObject != null)
						{
							if (devDetailObject.getDev_info().getModel() != null && devDetailObject.getDev_info().getVariant() != null)
							{
								devJson.setModel_name(devDetailObject.getDev_info().getModel() + "|" + devDetailObject.getDev_info().getVariant());
								Products product = productsDAO.findById(dev.getProductId());
								String product_name = product.getName();
								if( product_name != null && product_name.equals("") == false )
									devJson.setProduct_name(product_name);
								else
									devJson.setProduct_name("unknown");
							}
							else
							{
								Products product = productsDAO.findById(dev.getProductId());
								String mv = product.getMv();
								String product_name = product.getName();
								devJson.setModel_name(mv);
								if( product_name != null && product_name.equals("") == false )
									devJson.setProduct_name(product_name);
								else
									devJson.setProduct_name("unknown");
							}
						}
						else
						{
							Products product = productsDAO.findById(dev.getProductId());
							if(product!=null)
							{
								String mv = product.getMv();
								String product_name = product.getName();
								devJson.setModel_name(mv);
								if( product_name != null && product_name.equals("") == false )
									devJson.setProduct_name(product_name);
								else
									devJson.setProduct_name("unknown");
							}
							else
							{
								log.warnf("No product found, product=%s, productId=%s, dev=%s", product, dev.getProductId(), dev.getSn());
								devJson.setProduct_name("unknown");
								devJson.setModel_name("unknown");								
							}
							
						}
	
					}
					else
					{
						devJson.setStatus("offline");
						Products product = productsDAO.findById(dev.getProductId());
						if (product != null){
							if (product.getMv() != null){
								String mv = product.getMv();
								devJson.setModel_name(mv);
							}
							String product_name = product.getName();
							if( product_name != null && product_name.equals("") == false )
								devJson.setProduct_name(product_name);
							else
								devJson.setProduct_name("unknown");
						}
					}

					StationListObject stationListObjectFromCache = new StationListObject();
					stationListObjectFromCache.setSn(dev.getSn());
					stationListObjectFromCache.setIana_id(dev.getIanaId());
					stationListObjectFromCache = ACUtil.getPoolObjectBySn(stationListObjectFromCache, StationListObject.class);
					if (stationListObjectFromCache != null){
						devJson.setClientz(stationListObjectFromCache.getTotalOnlineClient()==null?0:stationListObjectFromCache.getTotalOnlineClient());
					}
					else
					{
						devJson.setClientz(0);
					}
					
					Date date = DateUtils.getUtcDate();
					date = DateUtils.offsetFromUtcTimeZoneId(date, net.getTimezone());
					SimpleDateFormat format = new SimpleDateFormat(JsonUtils.jsonUtcDateFormat);
					devJson.setNetwork_time(format.format(date));
					devJson.setAddress(dev.getAddress());
					result.add(devJson);
					if(log.isInfoEnabled())
						log.infof("getGpsLocationV2: sn=%s, devJson=%s", devJson.getSn(), devJson);
				
				}
			}
			response.setData(result);

			response.setResp_code(ResponseCode.SUCCESS);
		} catch (Exception e) {
			log.error("getGpsLocationV2: orgId=" + param_orgId + ", networkId=" + param_networkId + ", vdevices=" + visible_devices + " - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		return JsonUtils.toJson(response);
	}

	public static Json_Device_Gps_Info getGpsLocationForStart(Json_Device_Gps_Info param_gpsObj)
	{
		if (param_gpsObj == null)
		{	
			log.warnf("getGpsLocationForStart  param_gpsObj= %s", param_gpsObj);
			return null;	
		}
		log.debugf("11812 NetworkWS.getGpsLocationForStart() called with param_gpsObj=%s",param_gpsObj);

		final boolean bReadOnlyDb = true;
		String param_orgId = param_gpsObj.getParam_orgId();
		Devices dev = param_gpsObj.getDev();
		Networks net = param_gpsObj.getNet();
		Date param_start = param_gpsObj.getParam_start();
		Integer param_devId = dev.getId();
		Date start = null, end = null;
		List<DeviceGpsLocations> devLocLst = null;
		Json_Device_Gps_Info gpsObj = new Json_Device_Gps_Info();
		Integer firstConsolidatedTime = null;
		
		 /* get all location point after start time, should be track for whole day */
		try
		{
			DeviceGpsLocationsDAO deviceLocDAO = new DeviceGpsLocationsDAO(param_orgId, bReadOnlyDb);
			DeviceGpsRecordsDAO deviceRecordsDAO = new DeviceGpsRecordsDAO(param_orgId,bReadOnlyDb);
			
			 start = DateUtils.getUtcDate(param_start, DateUtils.getTimezoneFromId(Integer.valueOf(net.getTimezone())));
			 		
			 /* ************ BEGIN DB Part: load the gps data ***** */
				firstConsolidatedTime = LocationUtils.getGpsLocationConsolidated(param_orgId, net.getId(), param_devId);
				if (firstConsolidatedTime == null)
				{
					log.debug("case 1  parameter start= "+start.getTime()/1000 + " firstConsolidatedTime= "+firstConsolidatedTime+" NetId=" +dev.getNetworkId()+" devId="+param_devId);
					devLocLst = deviceLocDAO.getLocationsByDeviceIdWithStarttime(dev.getId(), start);
				}
				else
				{
					end = DateUtils.convertUnixtime2Date(firstConsolidatedTime);//"UTC"
					log.debug("The parameter time start= "+start + " end()= "+ end+ " firstConsolidatedTime= "+firstConsolidatedTime);	
					if (firstConsolidatedTime > start.getTime()/1000)
					{
						log.debug("case 2  parameter start= "+start.getTime()/1000 + " end()= "+ end.getTime()/1000+ " firstConsolidatedTime= "+firstConsolidatedTime+" NetId=" +dev.getNetworkId()+" devId="+param_devId);
						devLocLst = new ArrayList<DeviceGpsLocations>();
						List<DeviceGpsLocations> locLst_old = deviceLocDAO.getLocationsByDeviceIdWithStarttime(param_devId, start, end);	
						List<DeviceGpsLocations> locLst_new = deviceRecordsDAO.getLocationsRecordsWithStarttime(param_devId, end);
						devLocLst.addAll(locLst_old);
						devLocLst.addAll(locLst_new);
					}
					else
					{
						log.debug("case 3  parameter start= "+start.getTime()/1000 + " firstConsolidatedTime= "+firstConsolidatedTime+" NetId=" +dev.getNetworkId()+" devId="+param_devId);
						devLocLst = deviceRecordsDAO.getLocationsRecordsWithStarttime(param_devId, start);
					}
				}
			/* ************ END DB Part ********************************** */
			
				if(log.isDebugEnabled())
				 log.debug("getGpsLocationV2: sn="+dev.getSn()+", from "+start.getTime());
			
				 if (devLocLst == null)
					 devLocLst = new ArrayList<DeviceGpsLocations>();
				 
				 int offset = 0;
				 Integer ms_time = null;
				 Integer lastTime = 0;
				
				 if( devLocLst.size() > 0 )
				 {
					offset = devLocLst.size()-1;
					ms_time = devLocLst.get(offset).getId().getUnixtime();							
				 }
				if(log.isDebugEnabled())
					log.debug("getGpsLocationV2: sn="+dev.getSn()+", last timestamp from db = "+ms_time);
		
			/* ************ merge cache data to list ********************** */
			DevLocationsReportObject devLocReportSample = new DevLocationsReportObject();
			devLocReportSample.setIana_id(dev.getIanaId());
			devLocReportSample.setSn(dev.getSn());
			DevLocationsReportObject devLocReportFromCache = ACUtil.getPoolObjectBySn(devLocReportSample, DevLocationsReportObject.class);
		
			if (devLocReportFromCache != null)
			{
				if( devLocReportFromCache.getLocation_list() != null && !devLocReportFromCache.getLocation_list().isEmpty())
				{
					if(log.isDebugEnabled())
						log.debug("getGpsLocationV2: sn="+dev.getSn()+", devLocReportFromCache: "+devLocReportFromCache);
	
					List<LocationList> locationListFromCache = devLocReportFromCache.getLocation_list();
					List<LocationList> localList = new ArrayList<LocationList>();
					localList.addAll(locationListFromCache);
					for( LocationList location :  localList)
					{
						if( ms_time != null )
						{
							if( location.getTimestamp() > ms_time )
							{
								DeviceGpsLocations locObj = new DeviceGpsLocations();
								DeviceGpsLocationsId locObjId = new DeviceGpsLocationsId();
								locObjId.setNetworkId(net.getId());
								locObjId.setDeviceId(dev.getId());
								locObjId.setUnixtime(location.getTimestamp().intValue());
								locObj.setId(locObjId);
								locObj.setLatitude(location.getLatitude());
								locObj.setLongitude(location.getLongitude());
								locObj.setAltitude(location.getAltitude());
								locObj.setSpeed(location.getSpeed());
								
								long network_time = 0;
								if( location.getTimestamp() != null )
									network_time = (long)location.getTimestamp()*1000;
								Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
								locObj.setDatetime(datetime);
								devLocLst.add(locObj);
								lastTime = location.getTimestamp().intValue();
							}
						} else {
							if ((start.getTime()/1000) < location.getTimestamp()){
								//add if data from cache is later then request time
								DeviceGpsLocations locObj = new DeviceGpsLocations();
								DeviceGpsLocationsId locObjId = new DeviceGpsLocationsId();
								locObjId.setNetworkId(net.getId());
								locObjId.setDeviceId(dev.getId());
								locObjId.setUnixtime(location.getTimestamp().intValue());
								locObj.setId(locObjId);
								locObj.setLatitude(location.getLatitude());
								locObj.setLongitude(location.getLongitude());
								locObj.setAltitude(location.getAltitude());
								locObj.setSpeed(location.getSpeed());
								long network_time = 0;
								if( location.getTimestamp() != null )
									network_time = (long)location.getTimestamp()*1000;
								Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
								locObj.setDatetime(datetime);
								devLocLst.add(locObj);
								lastTime = location.getTimestamp().intValue();
							}
						}
					}
				}
			}
		
			DevLocationsObject devLocationsExample = new DevLocationsObject();
			devLocationsExample.setIana_id(dev.getIanaId());
			devLocationsExample.setSn(dev.getSn());
			DevLocationsObject devLocationsObject = ACUtil.getPoolObjectBySn(devLocationsExample, DevLocationsObject.class);
		
			if (devLocationsObject != null)
			{
				if( devLocationsObject.getLocation_list() != null && !devLocationsObject.getLocation_list().isEmpty())
				{
					if(log.isDebugEnabled())
						log.debug("getGpsLocationV2: sn="+dev.getSn()+", devLocationsObject: "+devLocationsObject);
	
					List<LocationList> locationListFromCache = devLocationsObject.getLocation_list();
					List<LocationList> localList = new ArrayList<LocationList>();
					localList.addAll(locationListFromCache);
					for( LocationList location :  localList)
					{
						if( lastTime != 0 )
						{
							if( location.getTimestamp() > lastTime )
							{
								DeviceGpsLocations locObj = new DeviceGpsLocations();
								DeviceGpsLocationsId locObjId = new DeviceGpsLocationsId();
								locObjId.setNetworkId(net.getId());
								locObjId.setDeviceId(dev.getId());
								locObjId.setUnixtime(location.getTimestamp().intValue());
								locObj.setId(locObjId);
								locObj.setLatitude(location.getLatitude());
								locObj.setLongitude(location.getLongitude());
								locObj.setAltitude(location.getAltitude());
								locObj.setSpeed(location.getSpeed());
								
								long network_time = 0;
								if( location.getTimestamp() != null )
									network_time = (long)location.getTimestamp()*1000;
								Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
								locObj.setDatetime(datetime);
								devLocLst.add(locObj);
							}
						}
						else if( ms_time != null )
						{
							if( location.getTimestamp() > ms_time )
							{
								DeviceGpsLocations locObj = new DeviceGpsLocations();
								DeviceGpsLocationsId locObjId = new DeviceGpsLocationsId();
								locObjId.setNetworkId(net.getId());
								locObjId.setDeviceId(dev.getId());
								locObjId.setUnixtime(location.getTimestamp().intValue());
								locObj.setId(locObjId);
								locObj.setLatitude(location.getLatitude());
								locObj.setLongitude(location.getLongitude());
								locObj.setAltitude(location.getAltitude());
								locObj.setSpeed(location.getSpeed());
								
								long network_time = 0;
								if( location.getTimestamp() != null )
									network_time = (long)location.getTimestamp()*1000;
								Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
								locObj.setDatetime(datetime);
								devLocLst.add(locObj);
							}
						}else
						{
							if ((start.getTime()/1000) < location.getTimestamp()){
								//add if data from cache is later then request time
								DeviceGpsLocations locObj = new DeviceGpsLocations();
								DeviceGpsLocationsId locObjId = new DeviceGpsLocationsId();
								locObjId.setNetworkId(net.getId());
								locObjId.setDeviceId(dev.getId());
								locObjId.setUnixtime(location.getTimestamp().intValue());
								locObj.setId(locObjId);
								locObj.setLatitude(location.getLatitude());
								locObj.setLongitude(location.getLongitude());
								locObj.setAltitude(location.getAltitude());
								locObj.setSpeed(location.getSpeed());
								long network_time = 0;
								if( location.getTimestamp() != null )
									network_time = (long)location.getTimestamp()*1000;
								Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
								locObj.setDatetime(datetime);
								devLocLst.add(locObj);
							}
						}
					}
				}
			}
	
			gpsObj.setLocLst(devLocLst);
		
		} catch (Exception e) {
			log.error("NetworkWS.getGpsLocationForStart: orgId=" + param_orgId + ", networkId=" + net.getId() + ", vdevice=" + dev + " - " + e, e);
			
			return gpsObj;
		}
		
		return gpsObj;
	}
	
	public static String getClientsV3(JsonNetworkRequest request, JsonResponse<List<Json_Clients>> response)
	{
		final boolean bReadOnly = true;
		String param_orgId = request.getOrganization_id();
		int param_networkId = request.getNetwork_id();
		List<String> client_id_list = request.getClient_id_list();
		
		String serverRef1 = request.getServer_ref();
		String serverRef2 = JsonUtils.relateServerRef(serverRef1);
		String sid1 = request.getCaller_ref() + serverRef1;
		String sid2 = request.getCaller_ref() + serverRef2;
		
		List<Json_Clients> resultClientList = new ArrayList<Json_Clients>();
		
		if (client_id_list != null && client_id_list.size() > 0){
			//has mac list in query, return only the client exists in the mac list 
			//with bandwidth usage
			//first select which devId in the list, then select the client
//			HashMap<Integer, Boolean> devIdInList = new HashMap<Integer, Boolean>();
//			List<Integer> distinctDevIdList = new ArrayList<Integer>();
//			for (String idItem:client_id_list){
//				if (devIdInList.get(Integer.valueOf(idItem.getDevice_id())) != null){
//					//already added
//					continue;
//				}
//				devIdInList.put(Integer.valueOf(macItem.getDevice_id()), true);
//				distinctDevIdList.add(Integer.valueOf(macItem.getDevice_id()));
//			}
			try
			{
//				DevicesDAO devDAO = new DevicesDAO(param_orgId, bReadOnly);
				List<Devices> deviceLst = NetUtils.getDeviceLstByNetId(param_orgId, param_networkId);
				OuiInfosDAO ouiInfoDAO = new OuiInfosDAO(bReadOnly);
				boolean bSomeOnlineIsPending = false;
				for (Devices dev:deviceLst){
//					Devices dev = devDAO.findById(devId);
					if (dev == null){
						log.error("Cannot find device info from database, device_id = "+dev.getId());
						//skip this device
						continue;
					}
					
					Json_Devices devJson = new Json_Devices();
					devJson.parseDevices(dev, param_orgId);
					
					DevOnlineObject onlineObjExample = new DevOnlineObject();
					onlineObjExample.setIana_id(dev.getIanaId());
					onlineObjExample.setSn(dev.getSn());
					DevOnlineObject onlineObj = ACUtil.<DevOnlineObject>getPoolObjectBySn(onlineObjExample, DevOnlineObject.class);
					
					StationListObject stationListExample = new StationListObject();
					stationListExample.setIana_id(dev.getIanaId());
					stationListExample.setSn(dev.getSn());
					StationListObject stationListObj = ACUtil.<StationListObject>getPoolObjectBySn(stationListExample, StationListObject.class);
					
					if (stationListObj == null){
						if (onlineObj != null && onlineObj.isOnline()){
							//only fetch for online device
							stationListObj = new StationListObject();
							stationListObj.setStation_list(new ArrayList<StationList>());
							stationListObj.setIana_id(dev.getIanaId());
							stationListObj.setSn(dev.getSn());
							stationListObj.setSid(sid2);
							ACUtil.cachePoolObjectBySn(stationListObj, StationListObject.class);
							//if one online device's cache is null, return pending
							response.setResp_code(ResponseCode.PENDING);
							bSomeOnlineIsPending = true;
							if(log.isDebugEnabled())
								log.debug("fetching device station list " + dev.getSn());
							ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_LIST, sid2, dev.getIanaId(), dev.getSn());					
						}else{
							//for offline device, or device never online
							//return empty list
							if (!bSomeOnlineIsPending)
								response.setResp_code(ResponseCode.SUCCESS);							
						}
						//continue to next device
						continue;
					}
					
					List<StationList> stationList = stationListObj.getStation_list();
					if (stationList == null || stationList.isEmpty()){
						//there is a cache object, but no client, return empty list
						if (!bSomeOnlineIsPending)
							response.setResp_code(ResponseCode.SUCCESS);
						//continue to next device
						continue;
					}
					
					List<StationList> stationListlocalCopy = new ArrayList<StationList>();
					stationListlocalCopy.addAll(stationList);
					
					StationBandwidthListObject stationBandWidthListExample = new StationBandwidthListObject();
					stationBandWidthListExample.setIana_id(dev.getIanaId());
					stationBandWidthListExample.setSn(dev.getSn());
					StationBandwidthListObject stationBandWidthListObject = ACUtil.<StationBandwidthListObject> getPoolObjectBySn(stationBandWidthListExample, StationBandwidthListObject.class);
					
					List<StationStatusList> localCopyBandWidthList = new ArrayList<StationStatusList>();
					if(stationBandWidthListObject != null && !stationBandWidthListObject.getStation_status_list().isEmpty()){
						localCopyBandWidthList.addAll(stationBandWidthListObject.getStation_status_list());
					}
					
					Json_StationBandwidthListRequest sblRequestJson = new Json_StationBandwidthListRequest();
					List<SBLRequestMac> requestMacList = new ArrayList<SBLRequestMac>();

					for (String idItem:client_id_list)
					{
						for (StationList stationListItem:stationListlocalCopy)
						{
							if(stationListItem.getStatus()!=null && !stationListItem.getStatus().equals("active"))
								continue;
							
							if (stationListItem.getClient_id() != null)
							{
								if (stationListItem.getClient_id().equals(idItem)){
									Json_Clients client = new Json_Clients();
									client.setClient_id(stationListItem.getClient_id());
									client.setIp(stationListItem.getIp());
									client.setMac(stationListItem.getMac());
									if (stationListItem.getName() != null){
										client.setName(stationListItem.getName());
									}else {
										client.setName("");
									}
									client.setSsid(stationListItem.getEssid());
									client.setStatus(stationListItem.getStatus());
									client.setConnection_type(stationListItem.getType());
									client.setDevice(devJson);
									if (stationListItem.getMac() != null)
										client.setManufacturer(ouiInfoDAO.findManufacturerByMac(stationListItem.getMac()));
									if (!localCopyBandWidthList.isEmpty()){//localCopyBandWidthList must not be null
										for (StationStatusList bandwidthItem:localCopyBandWidthList){
											if (bandwidthItem.getMac().equals(client.getMac())){
												//found in bandwidth list
												client.setDownload_rate(bandwidthItem.getDrx());
												client.setUpload_rate(bandwidthItem.getDtx());
												client.setSignal(bandwidthItem.getRssi());
												localCopyBandWidthList.remove(bandwidthItem);
												break;
											}
										}
									}
									if( client.getIp()==null || !client.getIp().equals("0.0.0.0"))
										resultClientList.add(client);
//									break;
								}
							}						
						}
						//create macRequestItem for fetch to device
						SBLRequestMac sblRequestMac = sblRequestJson.new SBLRequestMac();
						sblRequestMac.setMac(idItem);
						requestMacList.add(sblRequestMac);
					}				
					//fetch the mac list to device again
					if(log.isDebugEnabled())
						log.debug("requestMacList = "+requestMacList);
					sblRequestJson.setMac_filter_list(requestMacList);
					sblRequestJson.setMac_filter_timeout(30);
					ACService.<Json_StationBandwidthListRequest>fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_BANDWIDTH_LIST, sid1, dev.getIanaId(), dev.getSn(), sblRequestJson);
					if (!bSomeOnlineIsPending)
						response.setResp_code(ResponseCode.SUCCESS);
				}
			}catch( Exception e )
			{
				log.error("getClientsV3: orgId=" + param_orgId + ", networkId=" + param_networkId + ", clients=" + client_id_list + " - " + e, e);
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
				return JsonUtils.toJson(response);
			}
			
			response.setData(resultClientList);
			
		}else{
			//return all the client for this network
			//without bandwidth usage
			try
			{
//				DevicesDAO devDAO = new DevicesDAO(param_orgId, bReadOnly);
				OuiInfosDAO ouiInfoDAO = new OuiInfosDAO(bReadOnly);
				boolean bSomeOnlineIsPending = false;
				
//				List<Devices> devLst = devDAO.getDevicesList(param_networkId);
				List<Devices> devLst = NetUtils.getDeviceLstByNetId(param_orgId, param_networkId);
				
				if (devLst == null || devLst.isEmpty()){
					//no device in network
					response.setResp_code(ResponseCode.SUCCESS);
					response.setData(resultClientList);
					return JsonUtils.toJson(response);
				}
				
				for (Devices dev : devLst)
				{
					Json_Devices devJson = new Json_Devices();
					devJson.parseDevices(dev, param_orgId);
					
					DevOnlineObject onlineObjExample = new DevOnlineObject();
					onlineObjExample.setIana_id(dev.getIanaId());
					onlineObjExample.setSn(dev.getSn());
					DevOnlineObject onlineObj = ACUtil.<DevOnlineObject>getPoolObjectBySn(onlineObjExample, DevOnlineObject.class);
					
					StationListObject stationListExample = new StationListObject();
					stationListExample.setIana_id(dev.getIanaId());
					stationListExample.setSn(dev.getSn());
					StationListObject stationListObj = ACUtil.<StationListObject>getPoolObjectBySn(stationListExample, StationListObject.class);
					
					if (stationListObj == null){
						if (onlineObj != null && onlineObj.isOnline()){
							//only fetch for online device
							stationListObj = new StationListObject();
							stationListObj.setStation_list(new ArrayList<StationList>());
							stationListObj.setIana_id(dev.getIanaId());
							stationListObj.setSn(dev.getSn());
							stationListObj.setSid(sid2);
							ACUtil.cachePoolObjectBySn(stationListObj, StationListObject.class);
							//if one online device's cache is null, return pending
							response.setResp_code(ResponseCode.PENDING);
							bSomeOnlineIsPending = true;
							if(log.isDebugEnabled())
								log.debug("fetching device station list " + dev.getSn());
							ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_LIST, sid2, dev.getIanaId(), dev.getSn());					
						}else{
							//for offline device, or device never online
							//return empty list
							if (!bSomeOnlineIsPending)
								response.setResp_code(ResponseCode.SUCCESS);							
						}
						//continue to next device
						continue;
					}
					
					List<StationList> stationList = stationListObj.getStation_list();
					if (stationList == null || stationList.isEmpty()){
						//there is a cache object, but no client, return empty list
						if (!bSomeOnlineIsPending)
							response.setResp_code(ResponseCode.SUCCESS);
						//continue to next device
						continue;
					}
					
					List<StationList> stationListlocalCopy = new ArrayList<StationList>();
					stationListlocalCopy.addAll(stationList);
					
					for (StationList stationListItem:stationListlocalCopy){
						
						if(stationListItem.getStatus()!=null && !stationListItem.getStatus().equals("active"))
							continue;
						
						Json_Clients client = new Json_Clients();
						client.setClient_id(stationListItem.getClient_id());
						client.setIp(stationListItem.getIp());
						client.setMac(stationListItem.getMac());
						if (stationListItem.getName() != null){
							client.setName(stationListItem.getName());
						}else {
							client.setName("");
						}
						client.setSsid(stationListItem.getEssid());
						client.setStatus(stationListItem.getStatus());
						client.setConnection_type(stationListItem.getType());
						client.setDevice(devJson);
						if (stationListItem.getMac() != null)
							client.setManufacturer(ouiInfoDAO.findManufacturerByMac(stationListItem.getMac()));	
						if( client.getIp()==null || !client.getIp().equals("0.0.0.0"))
							resultClientList.add(client);
					}
					if (!bSomeOnlineIsPending)
						response.setResp_code(ResponseCode.SUCCESS);
				}
			}catch( Exception e )
			{
				log.error("getClientsV3: orgId=" + param_orgId + ", networkId=" + param_networkId + e, e);
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
				return JsonUtils.toJson(response);
			}
			
			response.setData(resultClientList);
		}
		
		return JsonUtils.toJson(response);
	}
	
	public static String getNetworkFeatures(JsonNetworkRequest request, JsonResponse<Json_Networks> response)
	{
		String param_orgId = request.getOrganization_id();
		int param_networkId = request.getNetwork_id();
		Json_Networks features = new Json_Networks();
		boolean isGpsSupport = false;
		boolean isWifiSupport = false;
		boolean isPepvpnSupport = false;
		boolean isLowbandSet = false;
		boolean isPortalSupport = false;
		boolean gps_tracking_disabled = false;
		
		try
		{
			NetworksDAO networksDAO = new NetworksDAO(param_orgId);
			DevicesDAO devicesDAO = new DevicesDAO(param_orgId, true);
			DeviceFeaturesDAO deviceFeaturesDAO = new DeviceFeaturesDAO(param_orgId,true);
			//ProductsDAO productsDAO = new ProductsDAO(true);
			Networks network = OrgInfoUtils.getNetwork(param_orgId, param_networkId);
			response.setResp_code(ResponseCode.SUCCESS);
			if( network != null )
			{
				boolean isMasterMode = false;
				
				Devices masterDevices = null;
				
				if( network.getMasterDeviceId() != null )
					masterDevices = devicesDAO.getDevices(network.getMasterDeviceId());

				if (masterDevices != null ){
					isMasterMode = true;
				}
				
				if (network.isLowBandwidthMode())
					isLowbandSet = true;
				
				/* for validating gps_tracking_disabled*/
				if (NetworkUtils.isGpsTrackingDisabled(param_orgId, param_networkId))
					gps_tracking_disabled = true;
				
				String net_feature = network.getFeatures();
				if( net_feature != null && net_feature.isEmpty() == false)
				{				
					if (isLowbandSet)
					{
						if (net_feature.isEmpty())
							net_feature += "lowbandmode";
						else
							net_feature += "|lowbandmode";
					}
					
					if (gps_tracking_disabled)
						features.setFeatures(JsonMatcherUtils.JsonMatcherRemoveMap(net_feature, "map"));//gps_tracking_disabled
					else
						features.setFeatures(net_feature);

				} // end if( net_feature != null && net_feature.isEmpty() == false) ...
				else
				{
					net_feature = "";
//					List<Devices> devices = devicesDAO.getDevicesList(param_networkId);
					List<Devices> devLst = NetUtils.getDeviceLstByNetId(param_orgId, param_networkId);					
					HashMap<Integer, DeviceFeatures> featureMap = deviceFeaturesDAO.getFromDevLst(devLst);								
					
					if( devLst != null )
					{
						DeviceFeatures dev_feature = null;
						Products product = null;
						
						for( Devices dev : devLst )
						{
							product = ProductUtils.getProducts(dev.getProductId()); 
							dev_feature = featureMap.get(dev.getId());							
							
							//wifi support
							if( product.getRadio1_24G()==true ||
								product.getRadio1_5G() ==true ||
								product.getRadio2_24G()==true ||
								product.getRadio2_5G() ==true )
							{
								isWifiSupport = true;
							}
							
							// portal support
							if(log.isDebugEnabled())
								log.debugf("dev %d %s product %d %s", dev.getIanaId(), dev.getSn(), dev.getProductId(), product.getPortal_ic2());
							if(product.getPortal_ic2())
							{
								isPortalSupport = true;
							}
							
							//gps support
							if( dev_feature != null )
							{
								if( dev_feature.isGps_support() != null && dev_feature.isGps_support() )
									isGpsSupport = true;
								else 
								{
									if( (dev.getLatitude() != 0f) || (dev.getLongitude() != 0f) )
										isGpsSupport = true;
								}
									
							}
							else if( (dev.getLatitude() != 0f) || (dev.getLongitude() != 0f) )
								isGpsSupport = true;
							
							//pepvpn support
							boolean vpn_support = FeatureGetUtils.isPepvpnFeatureSupport(dev_feature, product);
							if( vpn_support )
							{
								isPepvpnSupport = true;
							}
							
							if( isWifiSupport && isGpsSupport && isPepvpnSupport && isPortalSupport)
								break;
							
						}
					}
					
					if( isWifiSupport )
						net_feature = "wifi";
					
					if(isPortalSupport)
					{
						if (net_feature.isEmpty())
							net_feature += "portal_ic2";
						else
							net_feature += "|portal_ic2";
					}
					
					if (isGpsSupport) {
						if (net_feature.isEmpty())
							net_feature += "map";
						else
							net_feature += "|map";
					}

					if (isPepvpnSupport) {
						if (net_feature.isEmpty())
							net_feature += "pepvpn";
						else
							net_feature += "|pepvpn";
					}

					if (isLowbandSet) {
						if (net_feature.isEmpty())
							net_feature += "lowbandmode";
						else
							net_feature += "|lowbandmode";
					}

					if( !(net_feature != null && net_feature.isEmpty() == false) )
						net_feature = "none";
					
					network.setFeatures(net_feature);
					networksDAO.update(network);
				} // end if( net_feature != null && net_feature.isEmpty() == false) ... else ...
				
				// !!!! do not need to store into database, so put after update and save
				if (isMasterMode) {
					if (net_feature.isEmpty()) {
						net_feature += "mastermode";
					} else {
						net_feature += "|mastermode";
					}
				}			
				
				if (gps_tracking_disabled)
					features.setFeatures(JsonMatcherUtils.JsonMatcherRemoveMap(net_feature, "map"));//gps_tracking_disabled
				else
					features.setFeatures(net_feature);

			} // end if( network != null )
			response.setData(features);
		}
		catch( Exception e )
		{
			log.error("getNetworkFeatures - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
		
		return JsonUtils.toJson(response);
	}
	
	private static boolean isDeviceOnline(Devices dev) {

		boolean isOnline = false;
		try{

			/* check online status */
			DevOnlineObject devOnline = PoolObjectDAO.getDevOnlineObject(dev);
			// log.debug("QQQ 6");
			if (devOnline != null)
			{
				if(log.isDebugEnabled())
					log.debugf("SYNC20140122 (0a-1) - devOnline object found for sn: %s ", dev.getSn());
				if (devOnline.isOnline()){
					isOnline = true;
				}
				else{
					isOnline = false;
				}
	
			}
		} catch (Exception e){
			log.error("SYNC20140122 (0b) - isDevOnline Exception " + dev.getSn(), e);
		}
		if(log.isDebugEnabled())
			log.debugf("SYNC20140122 - (0a-2) isDeviceOnline - devid %s, isOnline: %s", dev.getId(), isOnline);	

		return isOnline;
	}
	
	private static boolean isDeviceConfigExisted(String orgId, Integer devId){
		boolean isExisted = false;
		
		try{
			DeviceConfigurations devConfs = null;	
			DeviceConfigurationsDAO devConfsDao = new DeviceConfigurationsDAO(orgId);
			devConfs = devConfsDao.getLatestDeviceConfigurations(devId);

			if (devConfs != null){
				isExisted = true;
			}
		} catch (Exception e){
			log.error("isDeviceConfigExisted - " + e, e);
		}
		if(log.isDebugEnabled())
			log.debugf("SYNC20140122 - isDeviceConfigExisted - devid id: %s, isExisted: %s", devId, isExisted);	
		return isExisted;
	}

	public static String updateNetworkInformation(JsonNetworkRequest request, JsonResponse<Json_Networks> response) 
	{
		String param_orgId = request.getOrganization_id();
		Integer param_netId = request.getNetwork_id();
		Boolean param_isApplyTimezone = request.getIs_apply_timezone();	/* new value for follow timezone */
		boolean param_isGps_tracking_disabled = false; /* gps_tracking_disabled */
		String logMsg = null;
		
		try{
			NetworksMgr netMgr = new NetworksMgr(param_orgId);
			Networks net = netMgr.findById(param_netId);
			if (net == null)
			{
				response.setResp_code(ResponseCode.INVALID_INPUT);
				logMsg = String.format("Network %s %s not found!", param_orgId, param_netId);
				response.setMessage(logMsg);
				log.warnf(logMsg);
				return JsonUtils.toJson(response);
			}
			if (request.getIs_gps_tracking_disabled()==true)
				param_isGps_tracking_disabled = true;
									
			if (param_isGps_tracking_disabled)
			{
				/* Remove all existing GPS location data when the check box was firstly checked.
				 *  1 first check (no need if only remove gps_date)
				 *  2 supress call
				 *  3 delete gps_date (final)  
				 *  */
				
				List<Devices> devLst = new ArrayList<Devices>();
				devLst = NetUtils.getDeviceLstByNetId(param_orgId, param_netId);
				GPSMessageHandler.suppressDeviceListGpsReport(devLst);//call supress first, then delete
				
				log.infof("Gps_tracking_disabled -Remove all existing GPS location data with orgId %s netId %d", param_orgId, param_netId);
				NetworkUtils.removeDisabledGpsLocationDataByGroup(param_orgId, param_netId);
			}
			
			NetworkUtils.updateNetworksFeature(param_orgId, param_netId);
			OrgInfoUtils.saveNetworks(param_orgId, net);
			
			if (param_isApplyTimezone!=null && param_isApplyTimezone.booleanValue()==true)
			{
				log.warnf("INFO Apply follow time zone %s", param_isApplyTimezone);
				new ConfigUpdatePerDeviceTask(param_orgId, param_netId).performConfigUpdateNowForNetwork(response.getServer_ref(), 
						CONFIG_UPDATE_REASON.update_network_info.toString());
			}
			else
				log.warnf("INFO NOT Apply follow time zone %s", param_isApplyTimezone);
			
			response.setResp_code(ResponseCode.SUCCESS);
			return JsonUtils.toJson(response);	
		} catch (Exception e)
		{
			log.error("updateNetworkInformation ", e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
	}
	
	public static String setMasterDevice(JsonNetworkRequest request, JsonResponse<Json_Networks> response){
		Integer param_master_dev_id = request.getMaster_device_id();
		Integer param_network_id = request.getNetwork_id();
		String param_organization_id = request.getOrganization_id();

		try{
			DevicesDAO devicesDao = new DevicesDAO(param_organization_id);
			NetworksDAO networksDao = new NetworksDAO(param_organization_id);
			Networks networks = networksDao.getNetworksById(param_network_id);			
			DeviceUpdatesDAO deviceUpdateDao = new DeviceUpdatesDAO(param_organization_id);
			
			Devices devices = null;
			
			if (param_master_dev_id != 0){
				devices = devicesDao.getDevices(param_master_dev_id);
			}
			
			if (networks != null){
				response.setMessage(String.format("given network_id %d is not found!", param_network_id));
				response.setResp_code(ResponseCode.INVALID_INPUT);						
				return JsonUtils.toJson(response);
			}
						
			boolean isMasterDeviceNeed2BeChanged = true;

			if(log.isDebugEnabled())
				log.debugf("SMD20140128 - Master Device with devId: %s, networkId: %s, orgId: %s", param_master_dev_id, param_network_id, param_organization_id); 
			
			if (param_master_dev_id > 0){
		
				if (devices != null){
					
					if (networks.getMasterDeviceId() != null && networks.getMasterDeviceId().equals(devices.getId())){
						isMasterDeviceNeed2BeChanged = false;
					} // end if (networks.getMasterDeviceId() != null && networks.equals(devices.getId()))
					
					if (!isMasterDeviceNeed2BeChanged){
						response.setResp_code(ResponseCode.SUCCESS);
						response.setMessage("Master device has been set!");
					} else{
						if (isDeviceConfigExisted(param_organization_id, param_master_dev_id)){ // master config exists
							List<Integer> devicesIdListNeed2ChangeConfUpdate = devicesDao.getDevicesIdList(param_network_id);
//							List<Devices> devicesListChangeConfUpdate = devicesDao.getDevicesList(devicesIdListNeed2ChangeConfUpdate);						
							int result = deviceUpdateDao.suppressConfUpdateForDevLst(devicesIdListNeed2ChangeConfUpdate, response.getServer_ref(), "change master device");
							
							if(log.isDebugEnabled())
								log.debugf("SMD20140128 - There are %s record(s) of deviceUpdate under network (networkId: %s) are set to zero.", result, param_network_id); 
							networks.setMasterDeviceId(param_master_dev_id);
							networksDao.saveOrUpdate(networks);

							
							//deviceUpdateDao.incrementConfUpdateForNetwork(networks.getId());
							response.setResp_code(ResponseCode.SUCCESS);
							
						} else{ 
							if(log.isDebugEnabled())
								log.debugf("SMD20140128 - Suppose online device must have device config!");
							response.setResp_code(ResponseCode.INVALID_OPERATION);
							response.setMessage("NO_MASTER_CONF_FOUND");
						} // end if (isDeviceConfigExisted(param_organization_id, param_master_dev_id)) ... else ...
					} // end if (!isMasterDeviceChanged) else ...
					
				} // end if (devices != null)
			} // end if (param_master_dev_id > 0)
			else { // if master_device is unset
//				List<Integer> devicesIdListNeed2ChangeConfUpdate = devicesDao.getDevicesIdList(param_network_id);
//				List<Devices> devicesListChangeConfUpdate = devicesDao.getDevicesList(devicesIdListNeed2ChangeConfUpdate);						
				//int result = deviceUpdateDao.setConfUpdate2GivenValue(devicesIdListNeed2ChangeConfUpdate, 1);
				
				//log.debugf("SMD20140128 - There are %s record(s) of deviceUpdate under network (networkId: %s) are set to one.", result, param_network_id); 
				if(log.isDebugEnabled())
					log.debugf("SMD20140128 - unset master mode");

				if (networks.getMasterDeviceId()!=null && networks.getMasterDeviceId()!=0)
					deviceUpdateDao.incrementConfUpdateForNetwork(networks.getId(), response.getServer_ref(), CONFIG_UPDATE_REASON.unset_master_device.toString());
				
				networks.setMasterDeviceId(null);
				networksDao.saveOrUpdate(networks);
				
				List<Devices> devList4ClearConfigCheckSum = devicesDao.getDevicesList(param_network_id);
				
				for (Devices dev: devList4ClearConfigCheckSum){
					dev.setConfigChecksum(null);
					devicesDao.update(dev);
				}
				
				//TODO:immediate clear config				
				response.setResp_code(ResponseCode.SUCCESS);
			}
		} // end try ...
		catch (Exception e){
			log.error("SMD20140128 - setMasterDevice - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
		
		return JsonUtils.toJson(response);
	} // end setMasterDevice

	private static String askDevice2GetConfig(Integer iana_id, String sn){
		ConfigGetObject configExample = new ConfigGetObject();
		configExample.setIana_id(iana_id);
		configExample.setSn(sn);
		ConfigGetObject oldConfigObject;

		String result = null;
		try {
			oldConfigObject = ACUtil.<ConfigGetObject> getPoolObjectBySn(configExample, ConfigGetObject.class);
			if (log.isDebugEnabled()){
				log.debugf("SYNC20140122 - (2a-1) askDevice2PushConfig - devid sn %s", sn);	
			}
			String sid = JsonUtils.genServerRef();
			if (oldConfigObject == null) {
				ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_GET, sid, iana_id, sn);

				int Retrying = ASK_DEVICE_GET_CONFIG_RETRY;
				while (Retrying > 0 && oldConfigObject == null) {
					if (log.isDebugEnabled()){
						log.debugf("SYNC20140122 - (2a-2) dev %s retrying %d (sid, sn) = (%s, %s) ", sn, ASK_DEVICE_GET_CONFIG_RETRY - Retrying, sid, sn);
					}
					Thread.sleep(ASK_DEVICE_GET_CONFIG_WAIT_TIME);

					oldConfigObject = ACUtil.<ConfigGetObject> getPoolObjectBySn(configExample, ConfigGetObject.class);

					Retrying--;
					
					if (oldConfigObject != null && oldConfigObject.getConfig()!=null){
						break;
					}
				}
//				sbSummary.append("\n* - Retried "+(1-Retrying)+" times");
			} 
			
			if (oldConfigObject != null && oldConfigObject.getConfig()!=null){
				result = CommonConfigUtils.getConfChecksumFromHwConf(oldConfigObject.getConfig());
			}
		}
		catch (Exception e){
			log.error("SYNC20140122 - (2b) askDevice2GetConfig - " + e, e);
		}
		if (log.isDebugEnabled()){
			log.debugf("SYNC20140122 - (3) askDevice2PushConfig - devid sn %s, return: %s", sn, result);	
		}
		return result;
	}
	
	public static String syncMasterDeviceConfigs(JsonNetworkRequest request, JsonResponse<Json_Networks> response){
		Integer param_network_id = request.getNetwork_id();
		String param_organization_id = request.getOrganization_id();
		try{
			NetworksDAO networksDao = new NetworksDAO(param_organization_id);
			Networks networks = networksDao.getNetworksById(param_network_id);
			if (networks != null && networks.getMasterDeviceId() != null){
				DevicesDAO devicesDao = new DevicesDAO(param_organization_id);
				Devices masterDevice = devicesDao.getDevices(networks.getMasterDeviceId());
				
				if (masterDevice == null)
				{
					log.errorf("SYNC20140122 - master device is not found for networks %d", param_network_id);
					response.setResp_code(ResponseCode.INVALID_INPUT);
					return JsonUtils.toJson(response);
				}
				
				/* clear outdated config */
				if (log.isDebugEnabled()){
					log.debugf("SYNC20140122 - clear outdated config");
				}
				ConfigGetObject configGetObject = new ConfigGetObject();
				configGetObject.setIana_id(masterDevice.getIanaId());
				configGetObject.setSn(masterDevice.getSn());
				ACUtil.<ConfigGetObject> removePoolObjectBySn(configGetObject, ConfigGetObject.class);
					
				// mark last_sync_date to null
				if (log.isDebugEnabled()){
					log.debugf("SYNC20140122 - mark last_sync_date to null");
				}
				masterDevice.setLast_sync_date(null);
				devicesDao.update(masterDevice);
				
				String configChecksum = null;
				boolean isNewDeviceConfigExisted = false;
				if (isDeviceOnline(masterDevice)){ 
					if (log.isDebugEnabled()){
						log.debugf("SYNC20140122 - (1-1) before askDevice2PushConfig - devid %s", masterDevice.getId().toString());		
					}
					configChecksum = askDevice2GetConfig(masterDevice.getIanaId(), masterDevice.getSn());
					if (configChecksum!=null)
					{
						if (ConfigBackupUtils.isLastConfigFileMd5TheSame(configChecksum, param_organization_id, masterDevice.getId()))
							isNewDeviceConfigExisted = true; 
					}
					if (log.isDebugEnabled()){
						log.debugf("SYNC20140122 - (1-2) configChecksum: %s, isNewDeviceConfigExisted: %s - devid %s", configChecksum, isNewDeviceConfigExisted, masterDevice.getId());
					}
					if (isNewDeviceConfigExisted){ // master config exists
						if (log.isDebugEnabled()){
							log.debugf("SYNC20140122 - (4a) isDevicePushConfigSucceed, isDeviceConfigExisted() - devid %s", masterDevice.getId().toString());
						}
						// mark last_sync_date
						masterDevice.setLast_sync_date(DateUtils.getUnixtime());
						devicesDao.update(masterDevice);
						
						new ConfigUpdatePerDeviceTask(param_organization_id, param_network_id).performConfigUpdateNowForNetwork(
								response.getServer_ref(), CONFIG_UPDATE_REASON.sync_master_config.toString());
						
						response.setResp_code(ResponseCode.SUCCESS);
						response.setMessage("SYNC_SUCCESSFULLY");
					} else{
						if (log.isDebugEnabled()){
							log.debugf("SYNC20140122 - (4b) not isDevicePushConfigSucceed, not isDeviceConfigExisted() - askDevice2PushConfig - devid %s", masterDevice.getId().toString());	
						}
						response.setResp_code(ResponseCode.SUCCESS);
						response.setMessage("CONF_NOT_SYNC");
					} // end if (isDevicePushConfigSucceed && isDeviceConfigExisted) ... else ...
				} else{ // masterDeviceOffline
					response.setResp_code(ResponseCode.PENDING);
					response.setMessage("DEV_OFFLINE");
					if (log.isDebugEnabled()){
						log.debugf("SYNC20140122 - (0b) isDeviceOnline: false - devid %s", masterDevice.getId());	
					}
				}
			} // end if networks != null && networks.getMasterDeviceId() != null
			else{
				response.setResp_code(ResponseCode.UNDEFINED);	
			}

		} // end try ...
		catch (Exception e){
			log.error("syncMasterDeviceConfigs - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
		return JsonUtils.toJson(response);
	} // end syncMasterDeviceConfigs
	
	public static String getMasterDeviceList(JsonNetworkRequest request, JsonResponse<Json_MasterDevice> response){
		Integer param_network_id = request.getNetwork_id();
		String param_organization_id = request.getOrganization_id();
		Json_MasterDevice masterDeviceResponse = new Json_MasterDevice();
		
		try{
			
			NetworksDAO networksDao = new NetworksDAO(param_organization_id);
			Networks networks = networksDao.getNetworksById(param_network_id);
			if (networks != null){
				DevicesDAO devicesDao = new DevicesDAO(param_organization_id);
				
				if (networks.getMasterDeviceId() == null){
					masterDeviceResponse.setMaster_device_id(0);
				} else{
					masterDeviceResponse.setMaster_device_id(networks.getMasterDeviceId());
				}
				
				List <Devices> devicesList = devicesDao.getDevicesList(networks.getId());
				
				for (Devices devices: devicesList){
					
					if (isDeviceConfigExisted(param_organization_id, devices.getId())){
						Json_Devices devicesJson = new Json_Devices();
						devicesJson.setId(devices.getId());
						devicesJson.setName(devices.getName());
						masterDeviceResponse.getDevice_list().add(devicesJson);
					}
				}
				response.setResp_code(ResponseCode.SUCCESS);
				response.setData(masterDeviceResponse);
			} // end if (networks != null)
			else{
				response.setResp_code(ResponseCode.UNDEFINED);
				return JsonUtils.toJson(response);
			}
		} catch (Exception e){
			log.error(" " + e, e);
			response.setResp_code(ResponseCode.UNDEFINED);
			return JsonUtils.toJson(response);
		}
		
		
		return JsonUtils.toJson(response);
	} // end getMasterDeviceList()
	

	public static String getNetworkProductTypes(JsonNetworkRequest request, JsonResponse<Json_Networks> response)
	{
		Integer param_networkId = request.getNetwork_id();
		String param_organizationId = request.getOrganization_id();
		Json_Networks networkType = new Json_Networks();
		
		try
		{
			DevicesDAO devicesDAO = new DevicesDAO(param_organizationId,true);
			List<Integer> prod_ids = devicesDAO.getDistinctProductsIdByNetwork(param_networkId);
			LinkedHashSet<String> typeSet = new LinkedHashSet<String>();
			Products product = null;
			String netType = "";
			for(Integer prodId : prod_ids )
			{
				product = ProductUtils.getProducts(prodId);
				if( product != null )
				{
					typeSet.add(product.getDeviceType());
				}
			}
			
			for( String type : typeSet )
			{
				netType += type + "|";
			}
			
			if( !netType.isEmpty() )
				netType = netType.substring(0, netType.length()-1);
	
			networkType.setProduct_types(netType);
			response.setResp_code(ResponseCode.SUCCESS);
			response.setData(networkType);
		}
		catch( Exception e )
		{
			log.error("getNetworkProductTypes -" + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
		
		return JsonUtils.toJson(response);
	}
	
	public static String getLastGpsLocation(JsonNetworkRequest request, JsonResponse<List<Json_Devices>> response)
	{
		final boolean bReadOnlyDb = true;
		
		Integer param_networkId = request.getNetwork_id();
		String param_orgId = request.getOrganization_id();
		
		List<Json_Devices> result = new ArrayList<Json_Devices>();
		List<Devices> devList = new ArrayList<Devices>();
		
		try
		{
			DevicesDAO deviceDAO = new DevicesDAO(param_orgId, bReadOnlyDb);
			response.setResp_code(ResponseCode.SUCCESS);
			
			if(log.isDebugEnabled())
				log.debug("Get gps request:"+new Date());
			if (param_networkId == 0)
				devList = deviceDAO.getAllDevices();
			else
				devList = deviceDAO.getDevicesList(param_networkId);
			
			List<Devices> newdevList = new ArrayList<Devices>();
			for (Devices dev : devList)
			{
				boolean gps_tracking_disabled = false;
				
				 if (NetworkUtils.isGpsTrackingDisabled(param_orgId, dev.getNetworkId()))//Attention: param_networkId could be 0
				 {
					 gps_tracking_disabled=true;
					 
				 }
				 if (!gps_tracking_disabled)
				 {
					 /* *** If Gps_tracking_disabled by group wide setting *** */
					 newdevList.add(dev);
				 }
				 log.debugf("NetworkWsHandler.getLastGpsLocationV2 gps_tracking_disabled=%s dev=%s netId=%d", gps_tracking_disabled,dev,dev.getNetworkId());
				 
			}
			Networks network = null;
			for( Devices dev : newdevList )
			{
				if(log.isInfoEnabled())
					log.info("getLastGpsLocation: sn=" + dev.getSn() + ", dev: " + dev);
				network = OrgInfoUtils.getNetwork(param_orgId, dev.getNetworkId());
				Json_Devices devJson = new Json_Devices();
				devJson.setId(dev.getId());
				devJson.setNetwork_id(dev.getNetworkId());
				devJson.setName(dev.getName());
				devJson.setSn(dev.getSn());
				
				DevOnlineObject devOnlineObject = new DevOnlineObject();
				devOnlineObject.setIana_id(dev.getIanaId());
				devOnlineObject.setSn(dev.getSn());
				
				devOnlineObject = ACUtil.getPoolObjectBySn(devOnlineObject, DevOnlineObject.class);
				if( devOnlineObject != null && devOnlineObject.isOnline() == true)
				{
					devJson.setStatus("online");
					
					StationListObject stationListObject = new StationListObject();
					stationListObject.setIana_id(dev.getIanaId());
					stationListObject.setSn(dev.getSn());
					
					stationListObject = ACUtil.getPoolObjectBySn(stationListObject, StationListObject.class);
					if( stationListObject != null )
					{
						//devJson.setClientz(stationListObject.getTotalClient());
						devJson.setClientz(stationListObject.getTotalOnlineClient()==null?0:stationListObject.getTotalOnlineClient());
					}
					else
					{
						devJson.setClientz(0);
					}
				}
				else
				{
					devJson.setClientz(0);
					devJson.setStatus("offline");
				}
				
				Products product = ProductUtils.getProducts(dev.getProductId());
				if( product != null )
				{
					if( product.getName() != null && !product.getName().isEmpty() )
						devJson.setProduct_name(product.getName());
					else
						devJson.setProduct_name("unknown");
					devJson.setMv(product.getMv());
				}
				
				List<Json_Device_Locations> locations = new ArrayList<Json_Device_Locations>();
				Json_Device_Locations devLocJson = new Json_Device_Locations();
				if(dev.getLast_gps_latitude() != null && dev.getLast_gps_longitude() != null)
				{
					devLocJson.setLatitude(dev.getLast_gps_latitude());
					devLocJson.setLongitude(dev.getLast_gps_longitude());
					devLocJson.setExist(true);
					Date last_gps_date;
					if( dev.getLast_gps_unixtime() == null ) {
						devLocJson.setIsStatic(true);
						last_gps_date = new Date();
					} else {
						last_gps_date = new Date(dev.getLast_gps_unixtime() * 1000L);
					}
					devLocJson.setTimestamp(DateUtils.offsetFromUtcTimeZoneId(last_gps_date, network.getTimezone()));
				}
				locations.add(devLocJson);
				devJson.setLocations(locations);
				result.add(devJson);
			}
			
			response.setData(result);
		}
		catch(Exception e)
		{
			log.error("getLastGpsLocation -"+e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
		
		return JsonUtils.toJson(response);
	}
	
	public static void main( String[] args )
	{
		JsonNetworkRequest request = new JsonNetworkRequest();
		request.setNetwork_id(10);
		request.setOrganization_id("Michael");
		request.setDevice_id(30);
		request.setStart("2014-06-16");
		request.setEnd("2014-07-16");
		request.setSort("");
		request.setType("wireless");
		JsonResponse<List<Json_Client_Usage>> response = new JsonResponse<List<Json_Client_Usage>>();
		
		System.out.println(NetworkWsHandler.getTopClient(request, response));
	}
}