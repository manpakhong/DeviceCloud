package com.littlecloud.control.webservices.handler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.ConfigurationPepvpnsDAO;
import com.littlecloud.control.dao.DeviceUpdatesDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.DevicesxtagsDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.dao.OrganizationsDAO;
import com.littlecloud.control.dao.TagsDAO;
import com.littlecloud.control.dao.branch.DevicePendingChangesDAO;
import com.littlecloud.control.dao.branch.SnsOrganizationsDAO;
import com.littlecloud.control.devicechange.DeviceChangeService;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Devices.ONLINE_STATUS;
import com.littlecloud.control.entity.Devicesxtags;
import com.littlecloud.control.entity.DevicesxtagsId;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.Organizations;
import com.littlecloud.control.entity.Tags;
import com.littlecloud.control.entity.branch.DevicePendingChanges;
import com.littlecloud.control.entity.branch.SnsOrganizations;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.model.Json_Devices;
import com.littlecloud.control.json.model.Json_Networks;
import com.littlecloud.control.json.model.Json_Organization_Hub;
import com.littlecloud.control.json.model.Json_Organizations;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerDeviceTask;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerDeviceTask.CONFIG_UPDATE_REASON;
import com.littlecloud.control.json.model.config.util.ConfigurationSettingsUtils;
import com.littlecloud.control.json.model.config.util.RadioConfigUtils;
import com.littlecloud.control.json.request.JsonOrganizationRequest;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.util.HouseKeepUtils;
import com.littlecloud.control.webservices.util.HouseKeepUtils.HouseKeep;
import com.littlecloud.control.webservices.util.NetworkUtils;
import com.littlecloud.fusionhub.Json_FusionHubLicense;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.PoolObjectDAO;
import com.littlecloud.pool.object.StationListObject;
import com.littlecloud.pool.object.utils.BranchUtils;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;

/* Note: Method must be static to avoid NullPointerException from invoke which calls static method */
public class OrganizationWsHandler {

	private static final Logger log = Logger.getLogger(OrganizationWsHandler.class);
	
//	public static String addDevices(JsonOrganizationRequest request, JsonResponse<List<Json_Organizations>> response){
//		try{
//			
//		}catch (Exception e){
//			
//		}
//		return JsonUtils.toJson(response);
//	}
	
	public static String getPepvpHubCount(JsonOrganizationRequest request, JsonResponse<List<Json_Organizations>> response)
	{
		/* lookup isHub from device, at most 2 hubs in one network. */
		String param_orgId = request.getOrganization_id();

		List<Json_Organizations> result = new ArrayList<Json_Organizations>();

		final boolean bReadonly = true;
		//HibernateUtil hutil = new HibernateUtil(param_orgId, true);				
		try {
			//hutil.beginTransaction();
			DevicesDAO devDAO = new DevicesDAO(param_orgId, bReadonly);
			NetworksDAO networkDAO = new NetworksDAO(param_orgId, bReadonly);
			response.setResp_code(ResponseCode.SUCCESS);

			/* lookup all networks */
//			Networks networkExample = new Networks();
//			networkExample.setOrganizationId(param_orgId);
//			List<Networks> networkLst = networkDAO.findByExample(networkExample);
			List<Networks> networkLst = networkDAO.getNetworksByOrgId(param_orgId);

			log.debug("networkLst="+networkLst);
			/* lookup hub count */
			for (Networks network : networkLst)
			{
				Json_Organizations orgJson = new Json_Organizations();
				orgJson.setNetwork_id(network.getId());
				List<Devices> devLst = devDAO.getDevicesHubListFromHubNetwork(network.getId(), true);
				orgJson.setHub_count(devLst==null?0:devLst.size());

				result.add(orgJson);
			}

			response.setData(result);

			//hutil.commitTransaction();
		} catch (Exception e) {
			log.error("transaction is rollback - " + e, e);
			//hutil.rollbackTransaction();
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		return JsonUtils.toJson(response);
	}
	
	public static String getNetworks(JsonOrganizationRequest request, JsonResponse<Json_Organizations> response)
	{
		String param_orgId = request.getOrganization_id();
		Integer param_networkId = request.getNetwork_id();
		int online_count = 0;
		int offline_count = 0;
		int expiry_count = 0;
		int expiry_soon = 0;
		int client_count = 0;

		Date s_get = new Date();
		Date e_get = null;
		
		List<Json_Networks> networksJsonList = new ArrayList<Json_Networks>();
		Json_Organizations orgJson = new Json_Organizations();
		try
		{
			//hutil.beginTransaction();
			OrganizationsDAO organizationsDAO = new OrganizationsDAO(param_orgId);
			Organizations organizations = organizationsDAO.findById(param_orgId);
			Json_Networks jsonnet = null;
			List<Networks> networks = null;

			if (param_networkId != null && param_networkId > 0) {
				networks = new ArrayList<Networks>();
				Networks network = OrgInfoUtils.getNetwork(param_orgId, param_networkId);
				if (network != null) {
					networks.add(network);
				}
			}
			else
				networks = OrgInfoUtils.getNetworkLst(param_orgId);
			
			for( Networks net : networks )
			{
				if( net.isActive() == true )
				{
					jsonnet = new Json_Networks();
					List<Devices> devices = NetUtils.getDeviceLstByNetId(param_orgId, net.getId());
					// log.debugf("Network has device list size : %s, org_id: %s, net_id: %s" + devices.size(), param_orgId, net.getId());
					online_count = 0;
					offline_count = 0;
					client_count = 0;
					expiry_count = 0;
					boolean gps_tracking_disabled = false;
					Calendar cal = Calendar.getInstance();
					cal.setTimeZone(TimeZone.getTimeZone(net.getTimezone()));
					java.util.Date today = cal.getTime();
					
					cal.add(Calendar.DAY_OF_MONTH, -14);
					java.util.Date expirySoon = cal.getTime();
					if (NetworkUtils.isGpsTrackingDisabled(param_orgId, net.getId()))
						gps_tracking_disabled = true;
					
					for (Devices dev : devices)
					{
						if (dev.isActive())
						{
							DevOnlineObject devOnline = PoolObjectDAO.getDevOnlineObject(dev);
							if (devOnline != null && devOnline.isOnline() == true)
							{
								online_count++;
								StationListObject objExample = new StationListObject();
								objExample.setIana_id(dev.getIanaId());
								objExample.setSn(dev.getSn());

								StationListObject stationListObject = ACUtil.<StationListObject> getPoolObjectBySn(objExample, StationListObject.class);
								if (stationListObject != null)
								{
									//if( client_count == null )
										//client_count = 0;
									if(stationListObject.getTotalOnlineClient() != null)
										client_count += stationListObject.getTotalOnlineClient();
									else
										client_count += 0;
								}
								else
								{
									response.setResp_code(ResponseCode.PENDING); // pending for incomplete client list
//									log.debug("fetching device bandwidth " + dev.getSn());
									ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_LIST, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn());
								}
							}
							else
							{
								offline_count++;
							}
							
//							if (dev.getExpiryDate() != null && today.compareTo(dev.getExpiryDate()) > 0)
//							{
//								expiry_count++;
//							}
//							else if (dev.getExpiryDate() != null && expirySoon.compareTo(dev.getExpiryDate()) > 0)
//								expiry_soon++;
							
							boolean exp = false;
							boolean expSub = false;
							Date expiryDate = dev.getExpiryDate();
							Date subExpiryDate = dev.getSubExpiryDate();
							//Check warranty expired
							if( dev.getExpiryDate() == null || today.compareTo(expiryDate) > 0)
								exp = true;
							
							//Check IC2 licence expired
							if( dev.getSubExpiryDate() == null || today.compareTo(subExpiryDate) > 0 )
								expSub = true;
							
							if( exp && expSub )
							{
								expiry_count++;
							}
							else
							{
								if(!exp && !expSub)
								{
									if( expiryDate.compareTo(subExpiryDate) > 0 )
									{
										if( expirySoon.compareTo(expiryDate) > 0 )
										{
											expiry_soon++;
										}
									}
									else
									{
										if( expirySoon.compareTo(subExpiryDate) > 0 )
										{
											expiry_soon++;
										}
									}
								}
								else if( !exp )
								{
									if( expirySoon.compareTo(expiryDate) > 0 )
									{
										expiry_soon++;
									}
								}
								else if( !expSub )
								{
									if( expirySoon.compareTo(subExpiryDate) > 0 )
									{
										expiry_soon++;
									}
								}
							}
						}
					}
					
					jsonnet.setId(net.getId());
					jsonnet.setName(net.getName());
					jsonnet.setOffline_device_count(offline_count);
					jsonnet.setOnline_device_count(online_count);
					jsonnet.setClient_count(client_count);
					jsonnet.setExpiry_count(expiry_count);
					jsonnet.setExpiry_soon_count(expiry_soon);
//					jsonnet.setUsage(dtx + drx);
					jsonnet.setNetwork_type(net.getNetworkType());
					jsonnet.setMaster_device_id(net.getMasterDeviceId());
					jsonnet.setIncontrol_host1(net.getIncontrolHost1());
					jsonnet.setIncontrol_host2(net.getIncontrolHost2());
					jsonnet.setCustom_common_name(net.getCustomCommonName());
					jsonnet.setCustom_certificate(net.getCustomCertificate());
					jsonnet.setCustom_cert_perm(net.getCustomCertPerm());
//					jsonnet.setAddress(net.getAddress());
//					jsonnet.setLatitude(net.getLatitude());
//					jsonnet.setLongitude(net.getLongitude());
					if (!gps_tracking_disabled)
					{/* ***ignore for Gps_tracking_disabled*** */
						if(net.getLatitude() != null && net.getLongitude() != null)
						{
							jsonnet.setAddress(net.getAddress());
							jsonnet.setCountry(net.getCountry());
							jsonnet.setLatitude(net.getLatitude());
							jsonnet.setLongitude(net.getLongitude());
						}
						else
						{
					
							jsonnet.setAddress(organizations.getAddress());
							jsonnet.setCountry(organizations.getCountry());
	//						log.info("Country:"+organizations.getCountry());
							jsonnet.setLatitude(organizations.getLatitude());
							jsonnet.setLongitude(organizations.getLongitude());
							
						}
					}
					else {
						log.debugf("ignore for Gps_tracking_disabled netId=%d disabled=%s", net.getId(), net.isGps_tracking_disabled());
					}
					networksJsonList.add(jsonnet);
				}
			}
			
			orgJson.setOrganization_id(param_orgId);
			orgJson.setNetworks(networksJsonList);
			response.setData(orgJson); 
			//hutil.commitTransaction();
			response.setResp_code(ResponseCode.SUCCESS);
			e_get = new Date();
//			log.info("Organization has network list size : " + networks.size());
		} 
		catch (Exception e)
		{
			log.error("transaction is rollback - " + e, e);
			//hutil.rollbackTransaction();
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		Date s_set = new Date();
		String result = JsonUtils.toJson(response);
		Date e_set = new Date();
		
		log.info("Memo_test get network use " + (e_get.getTime()-s_get.getTime()) + " ms, set json use " + (e_set.getTime()-s_set.getTime()) + " ms");
		
		return result;
	}
	
	public static String updateNetworks(JsonOrganizationRequest request, JsonResponse<Json_Organizations> response)
	{
		int online_count = 0;
		int offline_count = 0;
		int client_count = 0;
		int expiry_count = 0;
		int expiry_soon = 0;

		String param_orgId = request.getOrganization_id();
		List<Integer> delNetIds = request.getDel_net_ids();
		List<Networks> update_networks = request.getUpdate_networks();

		//HibernateUtil hutil = new HibernateUtil(param_orgId);				
		List<Json_Networks> networksJsonList = new ArrayList<Json_Networks>();
		Json_Organizations orgJson = new Json_Organizations();

		try
		{
			//hutil.beginTransaction();
			DevicesDAO deviceDAO = new DevicesDAO(param_orgId);
			NetworksDAO networkDAO = new NetworksDAO(param_orgId);

			if (delNetIds != null && delNetIds.size() > 0)
			{
				for (Integer netId : delNetIds)
				{
					Networks del_net = networkDAO.findById(netId);
					networkDAO.delete(del_net);
					System.out.println("----The deleted networid:" + netId + "----");
				}
			}

			if (update_networks != null && update_networks.size() > 0)
			{
				for (Networks net : update_networks)
				{
					Networks network = networkDAO.findById(net.getId());
					System.out.println("---Networks updated--" + net.getName() + "------");
					network.setName(net.getName());
//					network.setCountry(net.getCountry());
//					network.setAddress(net.getAddress());
//					network.setLatitude(net.getLatitude());
//					network.setLongitude(net.getLongitude());
					networkDAO.saveOrUpdate(network);
				}
			}

			List<Networks> networks = networkDAO.getNetworksByOrgId(param_orgId);
			for (Networks net : networks)
			{
				Json_Networks jsonnet = new Json_Networks();
				List<Devices> devices = deviceDAO.getDevicesList(net.getId());
				online_count = 0;
				offline_count = 0;
				client_count = 0;
				expiry_count = 0;
				expiry_soon = 0;
//				int dtx = 0;
//				int drx = 0;

				Calendar cal = Calendar.getInstance();
				cal.setTimeZone(TimeZone.getTimeZone(net.getTimezone()));
				java.util.Date today = cal.getTime();
				
				cal.add(Calendar.DAY_OF_MONTH, -14);
				java.util.Date expirySoon = cal.getTime();
				

				for (Devices dev : devices)
				{
					if (dev.isActive())
					{
						client_count++;
						DevOnlineObject devOnline = PoolObjectDAO.getDevOnlineObject(dev);
						if (devOnline != null)
						{
							online_count++;
						}
						else
						{
							offline_count++;
						}

						if (dev.getExpiryDate() != null && today.compareTo(dev.getExpiryDate()) > 0)
							expiry_count++;
						else if (dev.getExpiryDate() != null && expirySoon.compareTo(dev.getExpiryDate()) > 0)
							expiry_soon++;
						
//						StationListObject stationLstObj = PoolObjectDAO.getStationListObject(dev);
//						if (stationLstObj != null)
//						{
//							log.debug("stationLstObj found!");
//							if (stationLstObj.getStation_list() != null)
//							{
//								for (StationList statLst : stationLstObj.getStation_list())
//								{
//									if( statLst != null && statLst.getDtx() != null )
//										dtx += statLst.getDtx();
//									if( statLst != null && statLst.getDrx() != null )
//										drx += statLst.getDrx();
//								}
//							}
//						}
//						else
//						{
//							response.setResp_code(ResponseCode.PENDING);
//							ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_LIST, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn());
//						}
					}
				}
				jsonnet.setId(net.getId());
				jsonnet.setName(net.getName());
				jsonnet.setOffline_device_count(offline_count);
				jsonnet.setOnline_device_count(online_count);
				jsonnet.setClient_count(client_count);
				jsonnet.setExpiry_count(expiry_count);
				jsonnet.setExpiry_soon_count(expiry_soon);
//				jsonnet.setUsage(dtx + drx);
				jsonnet.setNetwork_type(net.getNetworkType());
				
				jsonnet.setIncontrol_host1(net.getIncontrolHost1());
				jsonnet.setIncontrol_host2(net.getIncontrolHost2());
				jsonnet.setCustom_common_name(net.getCustomCommonName());
				jsonnet.setCustom_certificate(net.getCustomCertificate());
//				jsonnet.setAddress(net.getAddress());
//				jsonnet.setCountry(net.getCountry());
//				jsonnet.setLatitude(net.getLatitude());
//				jsonnet.setLongitude(net.getLongitude());
				networksJsonList.add(jsonnet);
			}
			orgJson.setOrganization_id(param_orgId);
			orgJson.setNetworks(networksJsonList);
			response.setData(orgJson);
			
			//hutil.commitTransaction();
			response.setResp_code(ResponseCode.SUCCESS);
		} catch (Exception e)
		{
			log.error("transaction is rollback - " + e, e);
			//hutil.rollbackTransaction();
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		return JsonUtils.toJson(response);
	}
	
	public static String getOrganizationFeatures(JsonOrganizationRequest request, JsonResponse<Json_Organizations> response)
	{
		String param_orgId = request.getOrganization_id();
		Json_Organizations orgJson = new Json_Organizations();
		
		try
		{
			boolean isGpsSupport = false;
			List<Networks> networks = OrgInfoUtils.getNetworkLst(param_orgId);
			for( Networks network : networks )
			{
				boolean gps_tracking_disabled = false;
				if (NetworkUtils.isGpsTrackingDisabled(param_orgId, network.getId()))//network.getGps_tracking_disabled() doesn't work
					gps_tracking_disabled = true;
				log.debugf("getOrganizationFeatures Gps_tracking_disabled network = %s", network);
				if (!gps_tracking_disabled) 
				{
				if( network.getFeatures() != null )
				{
					String[] features = network.getFeatures().split("\\|");
					for( int i = 0; i < features.length; i++ )
					{
						if(features[i].equalsIgnoreCase("map"))
						{
							isGpsSupport = true;
							break;
						}
					}
				}
				
				if(isGpsSupport==true)
					break;
				}
			}
			
			if( isGpsSupport == true )
				orgJson.setFeatures("map");
			
			response.setResp_code(ResponseCode.SUCCESS);
			response.setData(orgJson);
		}
		catch (Exception e)
		{
			log.error("getOrganizationFeatures - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		return JsonUtils.toJson(response);
	}
	
/*	
	public static String updateNetworks(JsonOrganizationRequest request, JsonResponse<Json_Organizations> response)
	{
		int online_count = 0;
		int offline_count = 0;
		int client_count = 0;

		String param_orgId = request.getOrganization_id();
		List<Integer> delNetIds = request.getDel_net_ids();
		List<Networks> update_networks = request.getUpdate_networks();

		//HibernateUtil hutil = new HibernateUtil(param_orgId);				
		List<Json_Networks> networksJsonList = new ArrayList<Json_Networks>();
		Json_Organizations orgJson = new Json_Organizations();

		try
		{
			//hutil.beginTransaction();
			DevicesDAO deviceDAO = new DevicesDAO(param_orgId);
			NetworksDAO networkDAO = new NetworksDAO(param_orgId);

			if (delNetIds != null && delNetIds.size() > 0)
			{
				Networks del_net = null;
				for(Integer netId : delNetIds)
				{
//					Networks del_net = networkDAO.findById(netId);
					List<Networks> networks = OrgInfoUtils.getNetworkLst(param_orgId);
					
					if( networks != null && networks.size() > 0)
					{
						int flag = (int)(networks.size()/2);
						int i = 0;
						if( networks.get(0).getId() != netId )
						{
							while( (flag-i) >= 0 && (flag+i) < networks.size() )
							{
								if( networks.get(flag-i).getId() == netId )
								{
									del_net = networks.get(flag-i);
									break;
								}
								else if( networks.get(flag+i).getId() == netId )
								{
									del_net = networks.get(flag+i);
									break;
								}
								i++;
							}
						}
						else
							del_net = networks.get(0);
					}
					
					if( del_net != null )
						networkDAO.delete(del_net);
					System.out.println("----The deleted networid:" + netId + "----");
				}
			}

			if (update_networks != null && update_networks.size() > 0)
			{
				for (Networks net : update_networks)
				{
					Networks network = networkDAO.findById(net.getId());
					System.out.println("---Networks updated--" + net.getName() + "------");
					network.setName(net.getName());
//					network.setCountry(net.getCountry());
					
					networkDAO.saveOrUpdate(network);
				}
			}

			List<Networks> networks = OrgInfoUtils.getNetworkLst(param_orgId);
			for (Networks net : networks)
			{
				Json_Networks jsonnet = new Json_Networks();
				List<Devices> devices = NetUtils.getDeviceLst(param_orgId, net.getId());
				online_count = 0;
				offline_count = 0;
				client_count = 0;
				int dtx = 0;
				int drx = 0;
				for (Devices dev : devices)
				{
					if (dev.isActive())
					{
						client_count++;
						DevOnlineObject devOnline = PoolObjectDAO.getDevOnlineObject(dev);
						if (devOnline != null)
						{
							online_count++;
						}
						else
						{
							offline_count++;
						}
						StationListObject stationLstObj = PoolObjectDAO.getStationListObject(dev);
						if (stationLstObj != null)
						{
							log.debug("stationLstObj found!");
							if (stationLstObj.getStation_list() != null)
							{
								for (StationList statLst : stationLstObj.getStation_list())
								{
									if( statLst != null && statLst.getDtx() != null )
										dtx += statLst.getDtx();
									if( statLst != null && statLst.getDrx() != null )
										drx += statLst.getDrx();
								}
							}
						}
						else
						{
							response.setResp_code(ResponseCode.PENDING);
							ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_LIST, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn());
						}
					}
				}
				jsonnet.setId(net.getId());
				jsonnet.setName(net.getName());
				jsonnet.setOffline_device_count(offline_count);
				jsonnet.setOnline_device_count(online_count);
				jsonnet.setClient_count(client_count);
				jsonnet.setUsage(dtx + drx);
				jsonnet.setNetwork_type(net.getNetworkType());
				
				networksJsonList.add(jsonnet);
			}
			orgJson.setOrganization_id(param_orgId);
			orgJson.setNetworks(networksJsonList);
			response.setData(orgJson);
			
			//hutil.commitTransaction();
			response.setResp_code(ResponseCode.SUCCESS);
		} catch (Exception e)
		{
			log.error("transaction is rollback - " + e, e);
			//hutil.rollbackTransaction();
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		return JsonUtils.toJson(response);
	}
*/	
	public static String removeDevices(JsonOrganizationRequest request, JsonResponse response)
	{
		final boolean bReadOnlyDb = true;
		String param_orgId = request.getOrganization_id();
		List<Integer> devIdList = request.getDevice_ids();
 		boolean param_purge_data = request.isPurge_data();
		//List<Networks> networks = request.getUpdate_networks();
		log.warn("Remove Device start for sn= " + devIdList + " purge_data : " + param_purge_data);
		
		try {
			DevicesDAO devicesDAO = new DevicesDAO(param_orgId);
			//DeviceUpdatesDAO deviceUpdateDAO = new DeviceUpdatesDAO(param_orgId);
			SnsOrganizationsDAO snsDAO = new SnsOrganizationsDAO();
			ConfigurationPepvpnsDAO configurationPepvpnsDAO = new ConfigurationPepvpnsDAO(param_orgId);
			//ConfigurationRadioChannelsDAO confiRadioDAO= new ConfigurationRadioChannelsDAO(param_orgId);
			//ConfigurationSsidsDAO configurationSsidsDAO = new ConfigurationSsidsDAO(param_orgId);
			DevicesDAO devDAO = new DevicesDAO(param_orgId,bReadOnlyDb);
			NetworksDAO networksDAO = new NetworksDAO(param_orgId);
			List<Devices> removingHubLst = new ArrayList<Devices>();
			List<Devices> devLst = null;
			//Devices dev = new Devices();
			Devices dev = null;
			Devices hub = null;
			//the hub_list of this network
			
			List<Integer> hub_list = new ArrayList<Integer>();
			List<Integer> network_hub_lists = new ArrayList<Integer>();
			int param_networkId;
			response.setResp_code(ResponseCode.SUCCESS);

			devLst = devicesDAO.getDevicesList(devIdList);
			boolean isValid = true;
			if(devIdList!=null && devIdList.size()>0)
			{
				//find out the hub and hahub list of the network
				Networks network = networksDAO.getNetworksByDevId(devIdList.get(0));
				param_networkId = network.getId();
				hub_list = configurationPepvpnsDAO.getHubandHahubDeviceIdFromNetworkId(param_networkId, true);
				if(hub_list !=null && hub_list.size()>0)
				{
					network_hub_lists = configurationPepvpnsDAO.getActiveHubListFromDevIdLstInAnyNetwork(hub_list);
					//network_hub_lists.addAll(hub_list);
				}
				//check whether the device is a hub
				for (Integer devId : devIdList) 
				{
					if(configurationPepvpnsDAO.isEnabledHubOrHaHubInAnyNetwork(devId))
					{
						response.setMessage("HUB_CANNOT_REMOVE");
						response.setResp_code(ResponseCode.INVALID_OPERATION);
						dev = devDAO.findById(devId);
						removingHubLst.add(dev);
						
						isValid = false;
						log.info("hub test");
					}
					if(devId == network.getMasterDeviceId())
					{
						response.setMessage("MASTER_DEVICE_CANNOT_REMOVE:"+devId);
						response.setResp_code(ResponseCode.INVALID_OPERATION);
						isValid = false;
					}
				}
			}
			log.info("isValid:" + isValid);
			if (isValid)
			{
				for (Integer devId : devIdList) {
					dev = devicesDAO.findById(devId);
					if (dev != null)
					{																	
						if( param_purge_data == true )
							dev.setPurge_data(true);
						dev.setActive(false);
						dev.setConfigChecksum(null);
						dev.setCertChecksum(null);
						dev.setFirstAppear(null);
						dev.setLastOnline(null);
						dev.setOfflineAt(null);
						dev.setExpiryDate(null);
						dev.setDev_level_cfg("");
						dev.setWebadmin_cfg("");
						dev.setLast_sync_date(null);
						dev.setLast_gps_latitude(null);
						dev.setLast_gps_longitude(null);
						dev.setLast_gps_unixtime(null);
						// dev.setLast_ac_updatetime(null);
						dev.setDdns_enabled(false);
						dev.setOnline_status(ONLINE_STATUS.UNKNOWN.getDbValue());
						NetUtils.deleteDevices(param_orgId, dev.getNetworkId(), dev);
						devicesDAO.update(dev);
											
						SnsOrganizations sns = snsDAO.findByIanaIdSnOrgId(dev.getIanaId(), dev.getSn(), param_orgId);
						if (sns!=null)
						{
							snsDAO.delete(sns);
							BranchUtils.deleteSnsOrganizations(sns);
							OrgInfoUtils.deleteDevices(param_orgId, dev.getId(), dev.getIanaId(), dev.getSn());
						}
						
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_REDIRECT_DEV_ROOT, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn());
						log.debug("Device set active to false for sn= " + dev.getSn());
						
						/* Remove online object */						
						/*
						 * change to HouseKeepUtils
						 
							confiRadioDAO.removeRadioChannel(devId);
							configurationSsidsDAO.deleteByNetworkIdAndDeviceId(dev.getNetworkId(), devId);
						*/
						HouseKeepUtils.doHouseKeep(param_orgId, devId, dev.getNetworkId(), HouseKeep.REMOVE_DEVICE);
						DevOnlineObject devO = DeviceUtils.getDevOnlineObject(dev);
						if (devO!=null)
						{
							devO.setFw_ver(null);
							devO.setConf_checksum(null);
							devO.setCert_checksum(null);
							// dev.setLast_ac_updatetime(null);
							devO.setDdns_enabled(false);
							
							/* delay for clear config or re-add */
							ACUtil.<DevOnlineObject>cachePoolObjectBySnWithLifetime(devO,DevOnlineObject.class, 600);
							//ACUtil.<DevOnlineObject>removePoolObjectBySn(devO, DevOnlineObject.class);
						}
					}
				}
				
			}
			else
			{
				response.setData(removingHubLst);
				log.info("hub list"+removingHubLst);
			}
			
			if(network_hub_lists !=null && network_hub_lists.size() > 0)
			{
				log.info("hubidlst:"+network_hub_lists);
				//devIdList.addAll(hub_list);
				/* update hub */
				for (Integer hubId:network_hub_lists)
				{
					hub = devicesDAO.findById(hubId);
					if (hub!=null)
					{
						new ConfigUpdatePerDeviceTask(param_orgId, dev.getNetworkId()).performConfigUpdateNow(network_hub_lists, response.getServer_ref(), CONFIG_UPDATE_REASON.dev_remove_update_hub.toString());
					}
					else
					{
						log.warnf("hubId %d is not found!", hubId);
					}
				}
			}
			
			if( devLst != null && devLst.size() > 0 )
			{
				Integer network_id = devLst.get(0).getNetworkId();
				NetworkUtils.updateNetworksFeature(param_orgId, network_id);
				
				for (Devices dev1:devLst)
				{
					log.debugf("removeDevices dev1=%s", dev1);
					//log.info("removedevidlst:"+devIdList);
					//deviceUpdateDAO.incrementConfUpdateForDevLst(devIdList, response.getServer_ref(), "remove devices");
					new ConfigUpdatePerDeviceTask(param_orgId, dev1.getNetworkId()).performConfigClearNow(dev1, response.getServer_ref(), CONFIG_UPDATE_REASON.dev_remove.toString());
				}				
			}			
		} catch (Exception e) {
			log.error("Exception removeDevices - ", e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		} finally {
			/* clear to reload all */
			//BranchUtils.removeBranchInfoObject();
		}
		
		log.infof("removeDevices - result = %s", JsonUtils.toJson(response));
		return JsonUtils.toJson(response);
	}
	
	@SuppressWarnings("null")
	public static String removeNetworks(JsonOrganizationRequest request, JsonResponse<List<Json_Organization_Hub>> response)
	{
		String param_orgId = request.getOrganization_id();
		List<Integer> param_del_net_ids = request.getDel_net_ids();
		boolean isUnavailableToRemove = false;
		
		try
		{
			response.setResp_code(ResponseCode.SUCCESS);
			ConfigurationPepvpnsDAO configurationPepvpnsDAO = new ConfigurationPepvpnsDAO(param_orgId);
			DevicesDAO devicesDAO = new DevicesDAO(param_orgId);
			NetworksDAO networksDAO = new NetworksDAO(param_orgId);
//			ConfigurationRadiosDAO configurationRadiosDAO = new ConfigurationRadiosDAO(param_orgId);
//			NetworkEmailNotificationsDAO networkEmailNotificationsDAO = new NetworkEmailNotificationsDAO(param_orgId);
//			NetworkSliencePeriodsDAO networkSilencePeriodDAO = new NetworkSliencePeriodsDAO(param_orgId);
//			ConfigurationSsidsDAO configurationSsidsDAO = new ConfigurationSsidsDAO(param_orgId);
			DevicesxtagsDAO devXtagDAO = new DevicesxtagsDAO(param_orgId);
			TagsDAO tagsDAO = new TagsDAO(param_orgId);
			SnsOrganizationsDAO snsDAO = new SnsOrganizationsDAO();
			DeviceUpdatesDAO deviceUpdateDAO = new DeviceUpdatesDAO(param_orgId);
			
			List<Integer> device_list = null;
			List<Integer> dev_as_hub_in_this_net_ids =null;
			List<Json_Organization_Hub> json_hub_list =null;
			List<Integer>hub_of_this_net_list = new ArrayList<Integer>();
			List<Integer>updateconf_hub_lists = new ArrayList<Integer>();
			List<Integer>network_dev_lists = new ArrayList<Integer>();
			
			if(param_del_net_ids != null && param_del_net_ids.size()>0)
			{
				for( Integer netId : param_del_net_ids )
				{
					//find out the hub and hahub list of the network
					hub_of_this_net_list = configurationPepvpnsDAO.getHubandHahubDeviceIdFromNetworkId(netId, true);
					if(hub_of_this_net_list !=null && hub_of_this_net_list.size()>0)
					{
						updateconf_hub_lists.addAll(hub_of_this_net_list);
					}
					//check whether there are hubs in the network
					device_list = devicesDAO.getDevicesIdList(netId);
					dev_as_hub_in_this_net_ids = configurationPepvpnsDAO.getActiveHubListFromDevIdLstInAnyNetwork(device_list);
					
					if( dev_as_hub_in_this_net_ids != null && dev_as_hub_in_this_net_ids.size() > 0 )
					{
						isUnavailableToRemove = true;
						break;
					}
				}
			}
			log.info("isUnavailableToRemove"+isUnavailableToRemove);
			
			if( isUnavailableToRemove )
			{
				json_hub_list = new ArrayList<Json_Organization_Hub>();
				log.info("hubList"+dev_as_hub_in_this_net_ids);
				for(Integer hub : dev_as_hub_in_this_net_ids)
				{
					Devices device = devicesDAO.findById(hub);
					log.info("deviceinfo"+device+"id:"+hub);
					if(device !=null)
					{
						Json_Organization_Hub json_Organization_Hub = new Json_Organization_Hub();
						json_Organization_Hub.setId(device.getId());
						json_Organization_Hub.setName(device.getName());
						json_Organization_Hub.setSn(device.getSn());
						json_Organization_Hub.setNetwork_id(device.getNetworkId());
						json_Organization_Hub.setNetwork_name(device.getNetwork_name());
						json_hub_list.add(json_Organization_Hub);
						
					}
				}
				log.info("hub_list:"+json_hub_list);
				response.setData(json_hub_list);
				response.setMessage("HUB_CANNOT_REMOVE");
				response.setResp_code(ResponseCode.INVALID_OPERATION);
			}
			else
			{
				response.setResp_code(ResponseCode.SUCCESS);
				for( Integer netId : param_del_net_ids )
				{
					Networks network = networksDAO.findById(netId);
					
					network.setActive(false);
					networksDAO.update(network);
					
					HouseKeepUtils.doHouseKeep(param_orgId, null, netId, HouseKeep.REMOVE_NETWORKS);
//					configurationPepvpnsDAO.deleteByNetworkId(netId);
//					configurationRadiosDAO.deleteByNetworkId(netId);
//					networkEmailNotificationsDAO.deleteByNetworkId(netId);
//					networkSilencePeriodDAO.deleteByNetworkId(netId);
//					configurationSsidsDAO.deleteByNetworkId(netId);
					
					List<Devices> deviceLst = devicesDAO.getDevicesList(netId);
					List<Tags> tags = tagsDAO.getAllTagsFromNetwork(netId);
					if(deviceLst != null && deviceLst.size()>0)
					{
						for( Devices dev : deviceLst )
						{
							//devicesDAO.delete(dev);
							network_dev_lists.add(dev.getId());
							dev.setActive(false);
							//devicesDAO.updateNetworkToCache(dev);
							NetUtils.deleteDevices(param_orgId, dev.getNetworkId(), dev);
							devicesDAO.update(dev);

							for( Tags tag : tags )
							{
								DevicesxtagsId devicextagsId = new DevicesxtagsId(tag.getId(), dev.getId());
								log.debug("devicextagsId="+devicextagsId);
								Devicesxtags devicextags = new Devicesxtags(devicextagsId);
								log.debug("devicextags="+devicextags);
								devXtagDAO.delete(devicextags);
							}
							
							SnsOrganizations sns = snsDAO.findByIanaIdSnOrgId(dev.getIanaId(), dev.getSn(), param_orgId);
							if (sns!=null)
								snsDAO.delete(sns);
									
							ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_REDIRECT_DEV_ROOT, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn());
							log.debug("Device set active to false for sn= " + dev.getSn());
							
							/* Remove online object */
							DevOnlineObject devO = DeviceUtils.getDevOnlineObject(dev);
							if (devO!=null)
							{
								ACUtil.<DevOnlineObject>cachePoolObjectBySnWithLifetime(devO,DevOnlineObject.class, 600);
								new ConfigUpdatePerDeviceTask(param_orgId, dev.getNetworkId()).performConfigClearNow(dev, response.getServer_ref(), CONFIG_UPDATE_REASON.net_remove.toString());
								//ACUtil.<DevOnlineObject>removePoolObjectBySn(devO,DevOnlineObject.class);
							}
						}
					}
					OrgInfoUtils.deleteNetworks(param_orgId, network);
				}
				
				if(updateconf_hub_lists !=null && updateconf_hub_lists.size() > 0)
				{
					network_dev_lists.addAll(updateconf_hub_lists);
					log.info("removehubidlst:"+updateconf_hub_lists);
					log.info("removedev_lists:"+network_dev_lists);
					//TODO need to implement immediate config clear
					deviceUpdateDAO.incrementConfUpdateForDevLst(network_dev_lists, response.getServer_ref(), CONFIG_UPDATE_REASON.net_remove_update_hub.toString()+param_del_net_ids);
				}	
			}
			
		}
		catch( Exception e )
		{
			log.error("transaction is rollback if any - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		
		return JsonUtils.toJson(response);
	}
	
	/*2.0.10*/
	public static String addNetwork(JsonOrganizationRequest request, JsonResponse response)
	{
		String param_orgId = request.getOrganization_id();
		Integer param_netId = request.getNetwork_id();
		NetworksDAO networksDAO = null;
		log.info("New call for addNetworks.");
		try
		{
			response.setResp_code(ResponseCode.SUCCESS);			
			networksDAO = new NetworksDAO(param_orgId,true);
			Networks network = networksDAO.findById(param_netId);
			ConfigurationSettingsUtils settingsUtils = new ConfigurationSettingsUtils(param_orgId, param_netId, 0); 
			settingsUtils.initNetConfigSettings();
			OrgInfoUtils.saveOrUpdateNetworks(param_orgId, network);
		}
		catch( Exception e )
		{
			log.error("transaction is rollback if any - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
		
		return JsonUtils.toJson(response);
	}
	
	public static String updateFusionHubLicense(JsonOrganizationRequest request, JsonResponse<Json_Devices> response)
	{
		/*
		 * - Recieve parameter: license_info
		 * - Return "status: pending" if device is online, or "status: offline" if device is offline
		 */
		
		String status = "pending";
		List<Json_Devices> devices= new ArrayList<Json_Devices>();
		devices = request.getDevices();
	
		if (devices == null)
		{
			response.setResp_code(ResponseCode.INVALID_INPUT);
			response.setMessage("NULL PARAM");
			return JsonUtils.toJson(response);
		}
		try
		{
		  for (Json_Devices dev : devices)
		  {
			  String param_sn = dev.getSn();		
			  Integer param_ianaId = dev.getIana_id();
			  String sid = request.getCaller_ref() + JsonUtils.genServerRef();
			  Json_FusionHubLicense param_licenseInfo = dev.getLicenseUpdateInfo();

			Gson gson = new Gson();
			String license_info = gson.toJson(param_licenseInfo);

			DevicePendingChanges devChange = new DevicePendingChanges();
			devChange.setIana_id(param_ianaId);// 23695
			devChange.setSn(param_sn);// param_licenseInfo.getSn()
			devChange.setSid(sid);
			devChange.setMessage_type("PIPE_INFO_TYPE_EVENT_ACT_FH_LIC");
			devChange.setContent(license_info);
			devChange.setCreated_at(DateUtils.getUtcDate());

			devChange.setStatus(status);
			DevicePendingChangesDAO devicePendingChangesDAO = new DevicePendingChangesDAO();
			devicePendingChangesDAO.save(devChange);
//			devicePendingChangesDAO.updateDevicePendingInfo(devChange);
			DeviceChangeService.getInstance().addItem(devChange);

		}
			response.setResp_code(ResponseCode.SUCCESS);
			response.setMessage(status);
		} catch (Exception e) {
			DeviceWsHandler.log.error("updateFusionHubLicense - " + e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}

		return JsonUtils.toJson(response);
	}
	
//	public static String loadTest(JsonOrganizationRequest request, JsonResponse<List<Json_Event_Logs>> response)
//	{
//		String version = request.getVersion();
//		String orgId = request.getOrganization_id();
//		Integer networkId = request.getNetwork_id();
//		List<Json_Event_Logs> resultList = null;
//		Date s_get = new Date();		
//		Date e_get = null;
//				
//		try
//		{
//			if( version.equals("1") )
//			{
//				e_get = new Date();
//				response.setMessage("time " + (e_get.getTime()-s_get.getTime()) + " ms");
//				log.info("Version 1 need : " + (e_get.getTime() - s_get.getTime()) + " ms");
//				return version;
//			}
//			else if( version.equals("2") )
//			{
//				EventLogDAO logDAO = new EventLogDAO(orgId);
//				logDAO.getPageRecords(networkId, 30, "1,2,3,4,5,6,7,8,9", null, null, null, null, null, null, 0);
//				e_get = new Date();
//				response.setMessage("time " + (e_get.getTime()-s_get.getTime()) + " ms");
//				log.info("Version 2 need : " + (e_get.getTime() - s_get.getTime()) + " ms");
//				return version;
//			}
//			else
//			{
//				EventLogDAO logDAO = new EventLogDAO(orgId);
//				DevicesDAO devicesDAO = new DevicesDAO(orgId);
//				Date first_date = null;
//				List<EventLog> eventLogList = logDAO.getPageRecords(networkId, 30, "1,2,3,4,5,6,7,8,9", null, null, null, null, null, null, 0);
//				if (eventLogList != null)
//				{
//
//					Devices dev = null;
//					resultList = new ArrayList<Json_Event_Logs>();
//					Json_Event_Logs eventLogJson = null;
//
//					if (eventLogList.size() > 0)
//						first_date = eventLogList.get(0).getDatetime();
//					else
//					{
//						Calendar scal = Calendar.getInstance();
//						first_date = scal.getTime();
//					}
//
//					for (EventLog eventLog : eventLogList) {
//						eventLogJson = new Json_Event_Logs();
//						eventLogJson.setId(eventLog.getId());
//						eventLogJson.setTs(eventLog.getDatetime());
//						dev = devicesDAO.findById(eventLog.getDeviceId());
//						if (dev != null) {
//							eventLogJson.setDevice_name(dev.getName());
//							eventLogJson.setDevice_id(dev.getId());
//						}
//						if (eventLog.getSsid() == null) {
//							eventLogJson.setSsid("");
//						} else {
//							eventLogJson.setSsid(eventLog.getSsid());
//						}
//						if (eventLog.getClientName() == null) {
//							eventLogJson.setClient_name("");
//						} else {
//							eventLogJson.setClient_name(eventLog.getClientName());
//						}
//						if (eventLog.getClientMac() == null) {
//							eventLogJson.setClient_id("");
//						} else {
//							eventLogJson.setClient_id(eventLog.getClientMac());
//						}
//
//						eventLogJson.setEvent_type(eventLog.getEventType());
//						eventLogJson.setDetail(eventLog.getDetail());
//
//						resultList.add(eventLogJson);
//					}
//
//					if (resultList.size() > 0)
//					{
//						response.setStart_time(resultList.get(0).getTs());
//						response.setEnd_time(resultList.get(resultList.size() - 1).getTs());
//					}
//					else
//					{
//						response.setStart_time(new Date());
//						response.setEnd_time(new Date());
//					}
//
////					response.setPage_id(log_id + "|" + stime + "|" + etime);
//
//					response.setData(resultList);
//				}
//				
//				e_get = new Date();
//				log.info("Version 3 need : " + (e_get.getTime() - s_get.getTime()) + " ms");
//			}
//		}
//		catch( Exception e )
//		{
//			log.error("Roll back ERROR:",e);
//		}
//				
//		response.setMessage("time " + (e_get.getTime()-s_get.getTime()) + " ms");
//		String result = JsonUtils.toJson(response);
//		return result;
//	}
	
	public static void main( String[] args )
	{
		int[] a = { 1,2,5,7,3,6,4,9 };
		int flag = (int)(a.length/2);
		int i = 0;
		if( a[0] != 9 )
		{
			while( (flag-i) >=0 && (flag+i) < a.length )
			{
				if( a[flag-i]==9 )
				{
					System.out.println("r:"+(flag-i));
					break;
				}
				
				if( a[flag+i]==9 )
				{
					System.out.println("r:"+(flag+i));
					break;
				}
				i++;
			}
		}
		else
		{
			System.out.println("rr:"+0);
		}
		Integer f = null;
		f=98;
		System.out.println(""+flag+" "+f);
	}

}
