package com.littlecloud.control.webservices.handler;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.WtpMsgHandler;
import com.littlecloud.ac.json.model.Json_StationBandwidthListRequest;
import com.littlecloud.ac.json.model.Json_StationBandwidthListRequest.SBLRequestMac;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.ClientMonthlyUsagesDAO;
import com.littlecloud.control.dao.ClientUsagesDAO;
import com.littlecloud.control.dao.ConfigurationPepvpnsDAO;
import com.littlecloud.control.dao.DailyClientUsagesDAO;
import com.littlecloud.control.dao.DeviceConfigurationsDAO;
import com.littlecloud.control.dao.DeviceDailyDpiUsagesDAO;
import com.littlecloud.control.dao.DeviceDpiUsagesDAO;
import com.littlecloud.control.dao.DeviceFeaturesDAO;
import com.littlecloud.control.dao.DeviceGpsLocationsDAO;
import com.littlecloud.control.dao.DeviceGpsLocationsDatesDAO;
import com.littlecloud.control.dao.DeviceGpsRecordsDAO;
import com.littlecloud.control.dao.DeviceMonthlyDpiUsagesDAO;
import com.littlecloud.control.dao.DeviceMonthlyUsagesDAO;
import com.littlecloud.control.dao.DeviceOnlineHistoryDAO;
import com.littlecloud.control.dao.DeviceUpdatesDAO;
import com.littlecloud.control.dao.DeviceUsagesDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.dao.branch.DeviceFirmwareSchedulesDAO;
import com.littlecloud.control.dao.branch.ModelsDAO;
import com.littlecloud.control.dao.branch.OuiInfosDAO;
import com.littlecloud.control.dao.branch.ProductsDAO;
import com.littlecloud.control.entity.DdnsRecords;
import com.littlecloud.control.entity.DeviceConfigurations;
import com.littlecloud.control.entity.DeviceFeatures;
import com.littlecloud.control.entity.DeviceUpdates;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Devices.ONLINE_STATUS;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.branch.DeviceFirmwareSchedules;
import com.littlecloud.control.entity.branch.Models;
import com.littlecloud.control.entity.branch.Products;
import com.littlecloud.control.entity.report.ClientMonthlyUsages;
import com.littlecloud.control.entity.report.ClientUsages;
import com.littlecloud.control.entity.report.DailyClientUsages;
import com.littlecloud.control.entity.report.DeviceDailyDpiUsages;
import com.littlecloud.control.entity.report.DeviceDpiUsages;
import com.littlecloud.control.entity.report.DeviceGpsLocations;
import com.littlecloud.control.entity.report.DeviceGpsLocationsDates;
import com.littlecloud.control.entity.report.DeviceGpsLocationsId;
import com.littlecloud.control.entity.report.DeviceMonthlyDpiUsages;
import com.littlecloud.control.entity.report.DeviceOnlineHistories;
import com.littlecloud.control.entity.report.DeviceUsages;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.model.Json_Client_Bandwidth_Usage;
import com.littlecloud.control.json.model.Json_Client_Bandwidth_Usage.Client_UsageInfo;
import com.littlecloud.control.json.model.Json_Clients;
import com.littlecloud.control.json.model.Json_Device_Bandwidths;
import com.littlecloud.control.json.model.Json_Device_Configuration;
import com.littlecloud.control.json.model.Json_Device_Configuration.file_Info;
import com.littlecloud.control.json.model.Json_Device_Gps_Info;
import com.littlecloud.control.json.model.Json_Device_Interfaces;
import com.littlecloud.control.json.model.Json_Device_Locations;
import com.littlecloud.control.json.model.Json_Device_Month;
import com.littlecloud.control.json.model.Json_Device_Timely_Usage;
import com.littlecloud.control.json.model.Json_Device_Vlan_Interfaces;
import com.littlecloud.control.json.model.Json_Devices;
import com.littlecloud.control.json.model.Json_Devices.RadioMode;
import com.littlecloud.control.json.model.Json_device_online_histories;
import com.littlecloud.control.json.model.Json_nDpi_Report;
import com.littlecloud.control.json.model.Json_nDpi_Report.Usage;
import com.littlecloud.control.json.model.config.JsonConf_Admin;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings.Modules;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfiles;
import com.littlecloud.control.json.model.config.util.AdminConfigDeviceLevel;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerDeviceTask;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerDeviceTask.CONFIG_UPDATE_REASON;
import com.littlecloud.control.json.model.config.util.ConfigurationSettingsUtils;
import com.littlecloud.control.json.model.config.util.RadioConfigUtils;
import com.littlecloud.control.json.model.config.util.exception.FeatureNotFoundException;
import com.littlecloud.control.json.model.config.util.info.AdminInfo;
import com.littlecloud.control.json.request.JsonDeviceRequest;
import com.littlecloud.control.json.request.JsonDeviceRequest.DeviceUpdateInfo;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonMatcherUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.util.NetworkUtils;
import com.littlecloud.control.webservices.util.ProductWsUtils;
import com.littlecloud.pool.object.ClientInfoObject;
import com.littlecloud.pool.object.ConfigSettingsObject;
import com.littlecloud.pool.object.ConfigSettingsObject.SsidProfiles;
import com.littlecloud.pool.object.DevBandwidthObject;
import com.littlecloud.pool.object.DevBandwidthObject.BandwidthList;
import com.littlecloud.pool.object.DevBandwidthObject.Lifetime;
import com.littlecloud.pool.object.DevDetailJsonObject;
import com.littlecloud.pool.object.DevDetailObject;
import com.littlecloud.pool.object.DevDetailObject.LanList;
import com.littlecloud.pool.object.DevDetailObject.WanList;
import com.littlecloud.pool.object.DevLocationsObject;
import com.littlecloud.pool.object.DevLocationsReportObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.DeviceLastLocObject;
import com.littlecloud.pool.object.JsonDeviceInterfaces;
import com.littlecloud.pool.object.LocationList;
import com.littlecloud.pool.object.PoolObjectDAO;
import com.littlecloud.pool.object.StationBandwidthListObject;
import com.littlecloud.pool.object.StationBandwidthListObject.StationStatusList;
import com.littlecloud.pool.object.StationList;
import com.littlecloud.pool.object.StationListObject;
import com.littlecloud.pool.object.UpdatedClientInfoObject;
import com.littlecloud.pool.object.utils.ConfigSettingsUtils;
import com.littlecloud.pool.object.utils.DdnsUtils;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.littlecloud.pool.object.utils.LocationUtils;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;
import com.littlecloud.pool.object.utils.ProductUtils;
import com.littlecloud.pool.utils.ComparatorJsonDevLocations;
import com.littlecloud.pool.utils.Utils;
import com.littlecloud.services.DeviceMgr;
import com.littlecloud.services.WanBandwidthMgr;
import com.littlecloud.utils.CalendarUtils;

/* Note: Method must be static to avoid NullPointerException from invoke which calls static method */
public class DeviceWsHandler {
	private static final int STATION_LIST_REFRESH_SEC = 1800; // half hour
	static final Logger log = Logger.getLogger(DeviceWsHandler.class);
	public static final String BANDWIDTH_TYPE_HOURLY = "hourly";
	public static final String BANDWIDTH_TYPE_DAILY = "daily";
	public static final String BANDWIDTH_TYPE_MONTHLY = "monthly";
	public static final long MIN_DEV_LOC_FETCH_DUATION = 5;
	private enum WIFICFG 
	{
		device, network, not_managed, none
	}
	
	
	public static String updateDeviceInformation(JsonDeviceRequest request, JsonResponse<Json_Devices> response) 
	{
		String param_orgId = request.getOrganization_id();
		int param_deviceId = request.getDevice_id();
		Float param_lat = request.getLatitude();
		Float param_lon = request.getLongitude();
		String param_addr = request.getAddress();
		Date param_expiry_date = request.getExpiry_date();		
		Boolean param_ddnsEnabled = request.getDdns_enabled();
		List<Integer> devIdList = new ArrayList<Integer>();
		List<Integer> networkIdList = new ArrayList<Integer>();
		DeviceMgr devMgr = null;
		Devices dev = null;
		JsonConf_Admin adminConf = request.getAdminConf();
		
		boolean bUpdateName = false;
		try 
		{			
			ConfigurationPepvpnsDAO configurationPepvpnsDAO = new ConfigurationPepvpnsDAO(param_orgId);
			devMgr = new DeviceMgr(param_orgId);			
			dev = devMgr.findById(param_deviceId);
			if (dev==null)
			{
				response.setMessage("DEV_NOT_FOUND");
				response.setResp_code(ResponseCode.INVALID_INPUT);
				return JsonUtils.toJson(response);	
			}			
			
			if(log.isDebugEnabled())
				log.debugf("UPDEV20140806 - updateDeviceInformation (1) - dev: %s, request: %s", dev, request);
			
			if (dev.getName() == null || request.getDevice_name() == null ){
				log.warnf("UPDEV20140806 - updateDeviceInformation (2) - dev.getName(), request.getDevice_name() == null, dev: %s, request: %s", dev, request);
			}
			
			if (StringUtils.isEmpty(adminConf.getAdmin_password()) || StringUtils.isEmpty(adminConf.getAdmin_readonly_password()))
			{
				response.setMessage("PASSWORD_EMPTY");
				response.setResp_code(ResponseCode.INVALID_INPUT);
				return JsonUtils.toJson(response);	
			}
			
			dev.setWebadmin_password(adminConf.getAdmin_password());
			dev.setWebadmin_user_password(adminConf.getAdmin_readonly_password());
			
			if(dev.getName() != null && request.getDevice_name() != null && !dev.getName().equals(request.getDevice_name()))
			{
				bUpdateName = true;
				devIdList = configurationPepvpnsDAO.getHubandHahubDeviceIdFromNetworkId(dev.getNetworkId(), true);
				devIdList.add(param_deviceId);
				if(configurationPepvpnsDAO.isEnabledHubOrHaHubInAnyNetwork(param_deviceId)) 
				{
					networkIdList = configurationPepvpnsDAO.getMasterHubSupportedNetworkIds(param_deviceId);
					for(Integer networkId : networkIdList)
					{
						if(log.isInfoEnabled())
							log.info("UPDEV20140806 - update_hub_network:"+networkId);
						devIdList.addAll(devMgr.getDevicesIdList(networkId));
					}
				}
			}
			if( request.getDevice_name() != null && request.getDevice_name().length() > 45 )
			{
				log.warn("UPDEV20140806 - Too long device name : " + request.getDevice_name());
				dev.setName(request.getDevice_name().substring(0,45));
			}
			else
			{
				dev.setName(request.getDevice_name());
			}
			if( param_lat == null )
			{
				dev.setLatitude(0f);
				dev.setLast_gps_latitude(null);
			}
			else
			{
				dev.setLatitude(param_lat);
				dev.setLast_gps_latitude(null);
			}
			if( param_lon == null )
			{
				dev.setLongitude(0f);
				dev.setLast_gps_longitude(null);
			}
			else
			{
				dev.setLongitude(param_lon);
				dev.setLast_gps_longitude(null);
			}
			dev.setAddress(param_addr);
			if(param_expiry_date != null)
			{
				Networks net = OrgInfoUtils.getNetwork(param_orgId, dev.getNetworkId());
				if (net == null || net.getTimezone() == null ){
					log.warnf("UPDEV20140806 - updateDeviceInformation (3) - net: %s", net);
				}
				Date expired = DateUtils.getUtcDate(param_expiry_date, DateUtils.getTimezoneFromId(Integer.parseInt(net.getTimezone())));
				dev.setExpiryDate(expired);
			}
						
			if (param_ddnsEnabled != null){
				dev.setDdns_enabled(param_ddnsEnabled);
				DevOnlineObject devOnlineObject = DeviceUtils.getDevOnlineObject(dev);
				if (devOnlineObject != null){
					devOnlineObject.setDdns_enabled(param_ddnsEnabled);
					if (devOnlineObject.getSn() !=null && !devOnlineObject.getSn().isEmpty())
					ACUtil.<DevOnlineObject> cachePoolObjectBySn(devOnlineObject, DevOnlineObject.class);
				}

			}
			if(log.isDebugEnabled())
				log.debugf("UPDEV20140806 - updateDeviceInformation (3) - dev: %s, request: %s", dev, request);
			devMgr.update(dev);
			
			NetworkUtils.updateNetworksFeature(param_orgId, dev.getNetworkId());
			log.info("UPDEV20140806 - update_devIdList:"+devIdList);
			if(devIdList != null && bUpdateName)
			{
				if(log.isInfoEnabled())
					log.info("UPDEV20140806 - update_dev_idList:"+devIdList);
				//deviceUpdateDAO.incrementConfUpdateForDevLst(devIdList, response.getServer_ref(), "Update device info");
				new ConfigUpdatePerDeviceTask(param_orgId, dev.getNetworkId()).performConfigUpdateNow(devIdList, response.getServer_ref(), 
						CONFIG_UPDATE_REASON.update_dev_info.toString());
			}
			response.setResp_code(ResponseCode.SUCCESS);
		} catch (Exception e) {
			log.error("UPDEV20140806 - updateDeviceInformation - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		return JsonUtils.toJson(response);	
	}

	public static String getBandwidth(JsonDeviceRequest request, JsonResponse<Json_Device_Bandwidths> response)
	{
		String param_orgId = request.getOrganization_id();
		int param_deviceId = request.getDevice_id();
		
		Json_Device_Bandwidths devBandJson = new Json_Device_Bandwidths();
		
		try {
			response.setResp_code(ResponseCode.SUCCESS);
			DevicesDAO deviceDAO = new DevicesDAO(param_orgId);

			Devices dev = deviceDAO.findById(param_deviceId);

			/* get info from cache */
			/* seek cache, else seek device */
			DevBandwidthObject objExample = new DevBandwidthObject();
			DevBandwidthObject devBandwidthObject = null;
			if (dev != null)
			{
				objExample.setIana_id(dev.getIanaId());
				objExample.setSn(dev.getSn());
	
				devBandwidthObject = ACUtil.<DevBandwidthObject> getPoolObjectBySn(objExample, DevBandwidthObject.class);
			}
			if (devBandwidthObject == null)
			{
				response.setResp_code(ResponseCode.PENDING); // pending for incomplete client list
				if(log.isDebugEnabled())
					log.debug("fetching device bandwidth " + dev.getSn());
				ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_BANDWIDTH, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn());
			}
			else
			{
				if(log.isDebugEnabled())
					log.debug("cache found for device station list " + dev.getSn());
				devBandJson.parseDevBandwidthObject(devBandwidthObject);
				devBandJson.setTimestamp(DateUtils.getUtcDate());
				Lifetime lifetime = devBandwidthObject.getLifetime();
				if( lifetime != null )
				{
					devBandJson.setLt_rx(lifetime.getRx());
					devBandJson.setLt_tx(lifetime.getTx());
					devBandJson.setLt_unit(lifetime.getUnit());
					devBandJson.setLt_timestamp(new Date(Long.parseLong(lifetime.getTimestamp()+"000")));
				}
				/* ... !!!! still fetch for getting latest info from device until Lewis periodical status upload API is stable */
				ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_BANDWIDTH, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn());
			}

			response.setData(devBandJson);
		} catch (Exception e) {
			log.error("getBandwidth - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		return JsonUtils.toJson(response);
	}
	
	
	public static String getWanBandwidth(JsonDeviceRequest request, JsonResponse<Json_Device_Timely_Usage> response)
	{
		final boolean bReadOnlyDb = true;
		
		Integer param_devId = request.getDevice_id();
		String param_orgId = request.getOrganization_id();
		
		String type=request.getType();
		Integer wanid = request.getWan_id();
		Integer net_id = null;
		Devices dev = null;
		DevicesDAO devDAO = null;
		DeviceUsagesDAO deviceUsagesDAO = null;
		DeviceMonthlyUsagesDAO deviceMonthlyUsageDAO=null; 
		DeviceUsages usage1 = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		
		Json_Device_Timely_Usage devUsaList = new Json_Device_Timely_Usage();
		Calendar st = Calendar.getInstance();
		Networks net = null;
		
		Date date = new Date();	
		Date date_utc = DateUtils.getUtcDate(date);
		Calendar cal = CalendarUtils.getUtcCalendarToday();
		if(log.isInfoEnabled()) {
			log.infof("getWanBandwidth: devId=%d, orgId=%s, netId=%d, type=%s, wan_id=%d, date_time=%s, utcTime=%s", param_devId, param_orgId, net_id, type, wanid, date, date_utc);
		}
		long now_timemillis =date_utc.getTime();
		int now_unixtime = (int)(now_timemillis/1000);
		
		if(wanid == null)
		{
			wanid = 0;
		}
		try
		{
			devDAO = new DevicesDAO(param_orgId,bReadOnlyDb);
			deviceMonthlyUsageDAO=new DeviceMonthlyUsagesDAO(param_orgId,bReadOnlyDb);
			dev = devDAO.findById(param_devId);
			net_id = dev.getNetworkId();
			deviceUsagesDAO=new DeviceUsagesDAO(param_orgId,bReadOnlyDb);
			NetworksDAO netDAO = new NetworksDAO(param_orgId, bReadOnlyDb);
			net = netDAO.getNetworksByDevId(dev.getId());
			usage1 = deviceUsagesDAO.getLatestDeviceUsage(net.getId(), param_devId);
			
			if( usage1 != null )
				st.setTime(usage1.getDatetime());
		}
		catch( Exception e )
		{
			log.error("getWanBandwidth1 - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		


		if(usage1 != null) {
			switch(type) {
				case BANDWIDTH_TYPE_HOURLY:
					try{
						WanBandwidthMgr wanBandwidthMgr = new WanBandwidthMgr(param_orgId, wanid, dev, net);
						boolean isDone = wanBandwidthMgr.doHourlyJsonDeviceTimelyUsageResponse(cal, response);
						if (!isDone){
							log.warnf("DeviceWsHandler.getWanBandwidth() - BANDWIDTH_TYPE_HOURLY - request: %s", request);
						}
					} catch (Exception e){
						log.error("DeviceWsHandler.getWanBandwidth() - BANDWIDTH_TYPE_HOURLY", e );
					}
					break;
					
				case BANDWIDTH_TYPE_DAILY:
					try{
						WanBandwidthMgr wanBandwidthMgr = new WanBandwidthMgr(param_orgId, wanid, dev, net);
						boolean isDone = wanBandwidthMgr.doDailyJsonDeviceTimelyUsageResponse(date_utc, response);
						if (!isDone){
							log.warnf("DeviceWsHandler.getWanBandwidth() - BANDWIDTH_TYPE_HOURLY - request: %s", request);
						}
					} catch (Exception e){
						log.error("DeviceWsHandler.getWanBandwidth() - BANDWIDTH_TYPE_DAILY", e );
					}
					break;
					
				case BANDWIDTH_TYPE_MONTHLY:
					try{
						WanBandwidthMgr wanBandwidthMgr = new WanBandwidthMgr(param_orgId, wanid, dev, net);
						boolean isDone = wanBandwidthMgr.doMonthlyJsonDeviceTimelyUsageResponse(date_utc, response);
						if(!isDone){
							log.warnf("DeviceWsHandler.getWanBandwidth() - BANDWIDTH_TYPE_MONTHLY - request: %s", request);
						}
					} catch (Exception e){
						log.error("DeviceWsHandler.getWanBandwidth() - BANDWIDTH_TYPE_MONTHLY", e );
					}
					break;
				default:
						log.warn("Illegality input type: " + type);
						response.setResp_code(ResponseCode.INVALID_INPUT);
					break;
			}	
		} else {
			response.setMessage("UNAVAILABLE");
			if(log.isInfoEnabled()){
				log.info("getWanBandwidth: no latest usage, UNAVAILABLE");
			}
			response.setResp_code(ResponseCode.SUCCESS);
//					ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_USAGE_HIST, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn());
//					ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_USAGE_HIST, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn());
		}
		if (log.isDebugEnabled()){
			log.debugf("DeviceWsHandler.getWanBandwidth() - response: %s", response);
		}
		return JsonUtils.toJson(response);
	}

	public static String getWanClientUsage(JsonDeviceRequest request, JsonResponse<Json_Client_Bandwidth_Usage> response) {
		final boolean bReadOnlyDb = true;

		Integer param_devId = request.getDevice_id();
		String param_orgId = request.getOrganization_id();
		String type = request.getType();

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM");
		String month = dateformat.format(request.getFrom_date());
		SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd");
		String day = dateformat1.format(request.getFrom_date());
		SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyy-MM-dd HH");

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		String hour = null;

		Devices dev = null;
		DevicesDAO devDAO = null;
		ClientUsagesDAO clientUsagesDAO = null;
		Networks net = null;

		Json_Client_Bandwidth_Usage devUsaList = new Json_Client_Bandwidth_Usage();

		try {
			devDAO = new DevicesDAO(param_orgId, bReadOnlyDb);
			dev = devDAO.findById(param_devId);
			clientUsagesDAO = new ClientUsagesDAO(param_orgId, bReadOnlyDb);
			NetworksDAO netDAO = new NetworksDAO(param_orgId, bReadOnlyDb);
			net = netDAO.getNetworksByDevId(dev.getId());
			hour = dateformat2.format(DateUtils.getUtcDate(request.getFrom_date(),
					DateUtils.getTimezoneFromId(Integer.valueOf(net.getTimezone()))));
			if(log.isInfoEnabled())
				log.info("getWanClientUsage: hourly_usage_hour:" + hour);

		} catch (Exception e) {
			log.error("getWanClientUsage1, orgId = "+ param_orgId + ", devId = " + param_devId +" " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}

		switch (type) {

		case "hourly":
			try {
				List<ClientUsages> uList = clientUsagesDAO.getRecordsByNetIdAndDeviceIdAndTime(net.getId(), param_devId, hour);
				List<Client_UsageInfo> clients = new ArrayList<Client_UsageInfo>();
				if(log.isInfoEnabled())
					log.info("uList" + uList);

				if (uList != null && uList.size() > 0) {
					Date from_date = uList.get(0).getDatetime();
					Date network_fdate = DateUtils.offsetFromUtcTimeZone(from_date, DateUtils.getTimezoneFromId(Integer.valueOf(net.getTimezone())));
					String f_date = format.format(network_fdate);
					devUsaList.setFrom_date(f_date);
					Calendar cal = Calendar.getInstance();
					cal.setTime(network_fdate);
					cal.set(Calendar.MINUTE, 59);
					cal.set(Calendar.SECOND, 59);
					String t_date = format.format(cal.getTime());
					devUsaList.setTo_date(t_date);

					for (ClientUsages usage : uList) {
						Client_UsageInfo client = devUsaList.new Client_UsageInfo();

						if (usage.getRx() != null) {
							client.setDownload(usage.getRx() / 1024);
						}
						if (usage.getTx() != null) {
							client.setUpload(usage.getTx() / 1024);
						}
						if (usage.getIp() != null) {
							client.setIp(usage.getIp());
						}
						if (usage.getType() != null) {
							client.setType(usage.getType());
						}
						if (usage.getMac() != null) {
							client.setMac(usage.getMac());
						}
						
						client.setClient_id(PoolObjectDAO.convertToClientId(usage.getMac(), usage.getIp()));
						clients.add(client);
					}
					devUsaList.setClient_usages(clients);
					devUsaList.setId(request.getWan_id());
					devUsaList.setType(request.getType());
					devUsaList.setDevice_id(param_devId);
					response.setData(devUsaList);
					response.setResp_code(ResponseCode.SUCCESS);
				} else {
					response.setMessage("UNAVAILABLE");
					response.setResp_code(ResponseCode.SUCCESS);
				}
			} catch (Exception e) {
				log.error("getWanClientUsage hourly, orgId = "+ param_orgId + ", sn = " + dev.getSn() +" " + e, e);
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
			}

			break;

		case "daily":

			try {
				DailyClientUsagesDAO dailyClientUsagesDAO = new DailyClientUsagesDAO(param_orgId, bReadOnlyDb);
				List<Client_UsageInfo> clients = new ArrayList<Client_UsageInfo>();

				List<String> ipList = dailyClientUsagesDAO.getDistinctIpsByDeviceIdAndStarttime(net.getId(),
						param_devId, day);

				if (ipList != null && ipList.size() > 0) {
					for (String client_ip : ipList) {
						List<DailyClientUsages> uList = dailyClientUsagesDAO.getRecordsByDeviceIdAndIpAndTime(
								net.getId(), param_devId, client_ip, day);
						Client_UsageInfo client = devUsaList.new Client_UsageInfo();
						// if(uList.size()>=29)
						if (uList != null && uList.size() > 0) {
							if (uList.get(0).getDatetime() != null) {
								// st.setTime();
								devUsaList.setFrom_date(format.format(uList.get(0).getDatetime()));
								Calendar cal = Calendar.getInstance();
								cal.setTime(uList.get(0).getDatetime());
								cal.set(Calendar.HOUR_OF_DAY, 23);
								cal.set(Calendar.MINUTE, 59);
								cal.set(Calendar.SECOND, 59);
								devUsaList.setTo_date(format.format(cal.getTime()));
							}

							for (DailyClientUsages usage : uList) {
								if (usage.getRx() != null) {
									client.setDownload(usage.getRx() / Utils.dataUsageSize1k);
								}
								if (usage.getTx() != null) {
									client.setUpload(usage.getTx() / Utils.dataUsageSize1k);
								}
								if (usage.getIp() != null) {
									client.setIp(usage.getIp());
								}
								if (usage.getType() != null) {
									client.setType(usage.getType());
								}
								if (usage.getMac() != null) {
									client.setMac(usage.getMac());
								}
								
								client.setClient_id(PoolObjectDAO.convertToClientId(usage.getMac(), usage.getIp()));
								
								if (clients.contains(client) == false) {
									clients.add(client);
								}
							}

							// if( uList.size() < 29 )
							// {
							// ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_USAGE_HIST,
							// request.getCaller_ref() +
							// request.getServer_ref(), dev.getIanaId(),
							// dev.getSn());
							// }
						} else {
							response.setMessage("UNAVAILABLE");
							response.setResp_code(ResponseCode.SUCCESS);
							// ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_USAGE_HIST,
							// request.getCaller_ref() +
							// request.getServer_ref(), dev.getIanaId(),
							// dev.getSn());
						}
					}
					devUsaList.setClient_usages(clients);
					devUsaList.setId(request.getWan_id());
					// devUsaList.setName();
					devUsaList.setType(request.getType());
					devUsaList.setDevice_id(param_devId);
					response.setData(devUsaList);
					response.setResp_code(ResponseCode.SUCCESS);
				} else {
					response.setMessage("UNAVAILABLE");
					response.setResp_code(ResponseCode.SUCCESS);
					// ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_USAGE_HIST,
					// request.getCaller_ref() + request.getServer_ref(),
					// dev.getIanaId(), dev.getSn());
				}
			} catch (Exception e) {
				log.error("getWanClientUsage3 - " + e, e);
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
				// return JsonUtils.toJson(response);
			}

			break;

		case "monthly":
			try {
				ClientMonthlyUsagesDAO clientMonthlyUsagesDAO = new ClientMonthlyUsagesDAO(param_orgId, bReadOnlyDb);
				List<Client_UsageInfo> clients = new ArrayList<Client_UsageInfo>();
				if(log.isInfoEnabled())
					log.info("getWanClientUsage: net = " + net.getId());

				Calendar cal = Calendar.getInstance();
				cal.setTime(request.getFrom_date());
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				
				List<ClientMonthlyUsages> usages = clientMonthlyUsagesDAO.getRecordsByNetworkIdAndDeviceIdAndStarttime(
						net.getId(), param_devId, (int)(cal.getTimeInMillis()/1000));

				cal.add(Calendar.MONTH, 1);
				cal.add(Calendar.SECOND, -1);
				devUsaList.setTo_date(format.format(cal.getTime()));				
				
				if (usages != null && usages.size() > 0) {
					for (ClientMonthlyUsages usage : usages) {

						Client_UsageInfo client = devUsaList.new Client_UsageInfo();

						client.setDownload(usage.getRx() / Utils.dataUsageSize1k);
						client.setUpload(usage.getTx() / Utils.dataUsageSize1k);
						client.setIp(usage.getIp());
						client.setType(usage.getType());
						client.setMac(usage.getMac());
						client.setClient_id(PoolObjectDAO.convertToClientId(usage.getMac(), usage.getIp()));
						clients.add(client);

						if(log.isInfoEnabled())
							log.info("getWanClientUsage: mac=" + client.getMac() + ", ip=" + client.getIp() + ", type=" + client.getType()
								+ ", tx=" + client.getUpload() + ", rx=" + client.getDownload()+", net="+usage.getNetworkId()+", devid="+usage.getDeviceId());
					}
					devUsaList.setClient_usages(clients);
					devUsaList.setId(request.getWan_id());
					// devUsaList.setName();
					devUsaList.setType(request.getType());
					devUsaList.setDevice_id(param_devId);
					response.setData(devUsaList);
					response.setResp_code(ResponseCode.SUCCESS);
				} else {
					response.setMessage("UNAVAILABLE");
					response.setResp_code(ResponseCode.SUCCESS);
				}
			} catch (Exception e) {
				log.error("getWanClientUsage4 - " + e, e);
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
			}

			break;

		default:
			response.setResp_code(ResponseCode.INVALID_INPUT);
			return JsonUtils.toJson(response);

		}
		return JsonUtils.toJson(response);
	}
	
	public static String getGPSLocationV2(JsonDeviceRequest request, JsonResponse<List<Json_Device_Locations>> response)
	{
		final boolean bReadOnlyDb = true;
		String param_orgId = request.getOrganization_id();
		Integer param_devId = request.getDevice_id();
		Date param_start = request.getStart();
		/* ********** Parameter For Test ********** */
		//param_start = DateUtils.convertUnixtime2Date(1382401750);//test: ForStart() - 1 - case2
		//param_start = DateUtils.convertUnixtime2Date(1417392000);//test: ForStart() - 2 - calendar 0:0:0
		//param_start = null;//test: ForLatest() - without starttime then get latest
		/* ********** Parameter For Test ********** */
				
		Devices devExample = new Devices();
		devExample.setId(param_devId);

		try 
		{
			DeviceGpsLocationsDAO deviceLocationsDAO = new DeviceGpsLocationsDAO(param_orgId,bReadOnlyDb);
			DevicesDAO devDAO = new DevicesDAO(param_orgId,bReadOnlyDb);
			
			List<Json_Device_Locations> locJsonLst = new ArrayList<Json_Device_Locations>();
			ComparatorJsonDevLocations com = new ComparatorJsonDevLocations();
			TreeSet<Json_Device_Locations> locset = new TreeSet<Json_Device_Locations>(com);
			Devices dev = devDAO.findById(param_devId);
			List<DeviceGpsLocations> locLst = null;
			boolean gps_tracking_disabled = false;
			boolean is_low_bandwidth_mode = false;
			
			if (dev != null)
			{				
				if (NetworkUtils.isGpsTrackingDisabled(param_orgId, dev.getNetworkId()))
					gps_tracking_disabled = true;
			
				if (NetworkUtils.isLowBandwidthMode(param_orgId, dev.getNetworkId()))
					is_low_bandwidth_mode = true;
				
				if(log.isInfoEnabled())
					log.info("DeviceWs.getGPSLocationV2 gps_tracking_disabled="+gps_tracking_disabled+" is_low_bandwidth_mode= "+is_low_bandwidth_mode+" devId= " +dev.getId());				
			}
			
			NetworksDAO netDAO = new NetworksDAO(param_orgId, bReadOnlyDb);
			Networks net = netDAO.getNetworksByDevId(dev.getId());
			
			if(log.isInfoEnabled())
				log.info("devhand_device_id:"+dev.getId()+" dev_handler_network:"+net);
			if (log.isDebugEnabled())
				log.debug("Timezone = "+DateUtils.getTimezoneFromId(Integer.valueOf(net.getTimezone())));
			
			if (!gps_tracking_disabled)
			{
				Date expire_date = dev.getExpiryDate();
	
				if( expire_date != null )
				{
					Calendar excal = Calendar.getInstance();
					excal.setTime(expire_date);
					Calendar cal = Calendar.getInstance();
					if (log.isDebugEnabled())
						log.debug("expire day = "+excal+" current = "+cal);
				}
				
				boolean bIsStatic = false;
				if( param_start != null )//1418256000
				{
					/* ***** getGpsLocationForStart: getGpsLocation with param_start ***** */
					log.debugf("11811 DeviceWS.getGpsLocationV2() called with param_orgId=%s param_devId=%d param_start=%s",param_orgId,param_devId,param_start.getTime()/1000);
					
					Json_Device_Gps_Info param_gpsObj = new Json_Device_Gps_Info();
						param_gpsObj.setParam_orgId(param_orgId);
						param_gpsObj.setParam_devId(param_devId);
						param_gpsObj.setNet(net);
						param_gpsObj.setParam_start(param_start);
						param_gpsObj.setDev(dev);
						param_gpsObj.setIs_low_bandwidth_mode(is_low_bandwidth_mode);
						param_gpsObj.setCaller_ref(request.getCaller_ref());
						param_gpsObj.setServer_ref(request.getServer_ref());
					Json_Device_Gps_Info gpsObj = getGpsLocationForStart(param_gpsObj);//TODO
					if (gpsObj != null)
					{
						if (gpsObj.isTo_return())
							return gpsObj.getResult();
						locLst = gpsObj.getLocLst();
					}
				}
				else
				{
					/* ***** getGpsLocationForLatest: getGpsLocation without param_start then return latest ***** */
					log.debugf("11811 DeviceWS.getGpsLocationV2() called with param_orgId=%s param_devId=%d param_start=null",param_orgId,param_devId,param_start);
					
					Json_Device_Gps_Info gpsObj = getGpsLocationForLatest(dev, net);//TODO
					if (gpsObj != null)
					{
						locLst = gpsObj.getLocLst();
						bIsStatic= gpsObj.isbIsStatic();
					}
				}
				
				if (locLst.size() > 0)
				{
					for (DeviceGpsLocations loc : locLst)
					{
						/* add the device to result */
						Json_Device_Locations locJson = new Json_Device_Locations();
						if( loc.getLatitude() != null && loc.getLongitude() != null )
						{
							locJson.setLatitude(loc.getLatitude());//la
							locJson.setLongitude(loc.getLongitude());//lo
							locJson.setAltitude(loc.getAltitude());//at
							locJson.setSpeed(loc.getSpeed());//sp
							locJson.setHu(loc.getHUncertain());//he
							locJson.setType(loc.getType());//st
							if (bIsStatic){
								locJson.setIsStatic(true);
								locJson.setExist(true);
							}
							if (loc.getType() != null && loc.getType() == 1){
								locJson.setIsNewTrack(true);
							}
						}
						if( loc.getId().getUnixtime() != 0 )
						{
							long time = (long)(loc.getId().getUnixtime())*1000;
							Date timestamp = DateUtils.offsetFromUtcTimeZoneId(new Date(time), net.getTimezone());
							locJson.setTimestamp(timestamp);
						}
						locset.add(locJson);
					}
				}
				else
				{
					/* ***** locLst.size()==0, then no result to return ***** */
					Json_Device_Locations locJson = new Json_Device_Locations();
					DeviceGpsLocations loc = deviceLocationsDAO.getLatestNotNullLocation(param_devId);
					if( loc != null )
					{
						if( loc.getLatitude() != null && loc.getLongitude() != null )
						{
							locJson.setLatitude(loc.getLatitude());
							locJson.setLongitude(loc.getLongitude());
							locJson.setAltitude(loc.getAltitude());
							locJson.setSpeed(loc.getSpeed());
							locJson.setHu(loc.getHUncertain());
							locJson.setType(loc.getType());
						}
						if( loc.getId().getUnixtime() != 0 )
						{
							long time = (long)(loc.getId().getUnixtime())*1000;
							Date timestamp = DateUtils.offsetFromUtcTimeZoneId(new Date(time), net.getTimezone());
							locJson.setTimestamp(timestamp);
						}
	
						locset.add(locJson);
					}
					else
					{
						if (log.isDebugEnabled())
							log.debug("No Location data can be returned for request "+request.getCaller_ref());
					}
				}
				
				for(Json_Device_Locations loc : locset)
				{
					if( param_start != null && loc.getTimestamp().getTime() >= param_start.getTime() )
						locJsonLst.add(loc);
					else if(param_start == null)
						locJsonLst.add(loc);
				}
				
				Collections.sort(locJsonLst, com); // Collections.reverse(locJsonLst);
			}
		
			response.setData(locJsonLst); // log.debug(locJsonLst);
			
		} catch (Exception e) {
			log.error("getGpsLocationV2() transaction is rollback - " + e,e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
		
		response.setResp_code(ResponseCode.SUCCESS);
		return JsonUtils.toJson(response);			
	}
	
	public static Json_Device_Gps_Info getGpsLocationForStart(Json_Device_Gps_Info param_gpsObj)
	{
		if (param_gpsObj == null)
		{	
			log.warnf("getGpsLocationForStart  param_gpsObj= %s", param_gpsObj);
			return null;	
		}
		log.debugf("11811 DeviceWS.getGpsLocationForStart() called with param_gpsObj=%s",param_gpsObj);
		final boolean bReadOnlyDb = true;
		String param_orgId = param_gpsObj.getParam_orgId();
		Devices dev = param_gpsObj.getDev();
		Networks net = param_gpsObj.getNet();
		Integer param_devId = param_gpsObj.getParam_devId();
		Date param_start = param_gpsObj.getParam_start();
		boolean is_low_bandwidth_mode = param_gpsObj.isIs_low_bandwidth_mode();
		String caller_ref = param_gpsObj.getCaller_ref();
		String server_ref = param_gpsObj.getServer_ref();
		
		DevLocationsObject devLocationsObject = null;
		boolean isFetchNeeded = true;
		Json_Device_Gps_Info gpsObj = new Json_Device_Gps_Info();
		List<DeviceGpsLocations> locLst = null;
		Date start = null, end = null;
		Integer firstConsolidatedTime = null;
		
		try
		{
			DeviceFeaturesDAO deviceFeaturesDAO = new DeviceFeaturesDAO(param_orgId,bReadOnlyDb);
			HashMap<Integer, Boolean> featuresMap = deviceFeaturesDAO.getGpsFeatureMap();
			DeviceGpsLocationsDAO deviceLocationsDAO = new DeviceGpsLocationsDAO(param_orgId,bReadOnlyDb);
			DeviceGpsRecordsDAO deviceRecordsDAO = new DeviceGpsRecordsDAO(param_orgId,bReadOnlyDb);
			Calendar cal = Calendar.getInstance();
			cal.setTime(param_start);	
			
			if( cal.get(Calendar.HOUR_OF_DAY) != 0 || cal.get(Calendar.MINUTE) != 0 || cal.get(Calendar.SECOND) != 0)
			{
				Integer ms_time = null;
				Integer lastTime = 0;
				start = DateUtils.getUtcDate(param_start, DateUtils.getTimezoneFromId(Integer.valueOf(net.getTimezone())));
				
				/* ************ BEGIN DB Part: load the gps data ***** */
				firstConsolidatedTime = LocationUtils.getGpsLocationConsolidated(param_orgId, net.getId(), param_devId);
				if (log.isDebugEnabled())
					log.debug("The parameter time= "+param_start.getTime() + " firstConsolidatedTime="+ firstConsolidatedTime);					
				
				if (firstConsolidatedTime == null)
				{
					log.debug("case 1 The parameter start= "+start.getTime()/1000 + " firstConsolidatedTime= "+firstConsolidatedTime+" NetId=" +dev.getNetworkId()+" devId="+param_devId);
					locLst = deviceLocationsDAO.getLocationsByDeviceIdWithStarttime(dev.getNetworkId(),param_devId, start);	
				}
				else
				{
					end = DateUtils.convertUnixtime2Date(firstConsolidatedTime);//"UTC"
					
					if (firstConsolidatedTime > start.getTime()/1000)
					{
						log.debug("case 2 The parameter start= "+start.getTime()/1000 + " end()= "+ end.getTime()/1000+ " firstConsolidatedTime= "+firstConsolidatedTime+" NetId=" +dev.getNetworkId()+" devId="+param_devId);
						locLst = new ArrayList<DeviceGpsLocations>();
						List<DeviceGpsLocations> locLst_old = deviceLocationsDAO.getLocationsByDeviceIdWithStarttime(dev.getNetworkId(),param_devId, start, end);	
						List<DeviceGpsLocations> locLst_new = deviceRecordsDAO.getLocationsRecordsWithStarttime(dev.getNetworkId(),param_devId, end);
						locLst.addAll(locLst_old);
						locLst.addAll(locLst_new);
					}
					else
					{
						log.debug("case 3 The parameter start= "+start.getTime()/1000 + " firstConsolidatedTime= "+firstConsolidatedTime+" NetId=" +dev.getNetworkId()+" devId="+param_devId);
						locLst = deviceRecordsDAO.getLocationsRecordsWithStarttime(dev.getNetworkId(),param_devId, start);
					}
				}
				/* ************ END DB Part ********************************** */
				
				if (log.isDebugEnabled())
					log.debugf("locLst.size=%d", locLst.size());
				
				int offset = 0;
				
				if( locLst.size() > 0 )
				{
					offset = locLst.size()-1;
					ms_time = locLst.get(offset).getId().getUnixtime();
				}
				
				if (log.isDebugEnabled())
					log.debug("last timestamp from db = "+ms_time);
				
				/* ***** 11811 Cache Part: get gps data from cache ***** */

				DevLocationsReportObject devLocReportSample = new DevLocationsReportObject();
				devLocReportSample.setIana_id(dev.getIanaId());
				devLocReportSample.setSn(dev.getSn());
				DevLocationsReportObject devLocReportFromCache = ACUtil.getPoolObjectBySn(devLocReportSample, DevLocationsReportObject.class);
			
				if (devLocReportFromCache != null)
				{
					if( devLocReportFromCache.getLocation_list() != null && !devLocReportFromCache.getLocation_list().isEmpty())
					{
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
									locObj.setHUncertain(location.getH_uncertain());
									locObj.setType(location.getFlag());
									long network_time = 0;
									if( location.getTimestamp() != null )
										network_time = (long)location.getTimestamp()*1000;
									Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
									locObj.setDatetime(datetime);
									locLst.add(locObj);
									lastTime = location.getTimestamp().intValue();
									//log.debug("location cache added "+location.getTimestamp());
								}
							} else
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
									locObj.setHUncertain(location.getH_uncertain());
									locObj.setType(location.getFlag());
									long network_time = 0;
									if( location.getTimestamp() != null )
										network_time = (long)location.getTimestamp()*1000;
									Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
									locObj.setDatetime(datetime);
									locLst.add(locObj);
									lastTime = location.getTimestamp().intValue();
								}
							}
						}
					}
				}
				//ms_time = lastTime;
			
				DevLocationsObject devLocationsExample = new DevLocationsObject();
				devLocationsExample.setIana_id(dev.getIanaId());
				devLocationsExample.setSn(dev.getSn());
				/* ***** 11811 Cache Part: get gps data from cache ***** */
				devLocationsObject = ACUtil.getPoolObjectBySn(devLocationsExample, DevLocationsObject.class);
				
				if (devLocationsObject != null)
				{
					if( devLocationsObject.getLocation_list() != null && !devLocationsObject.getLocation_list().isEmpty())
					{
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
									locObj.setHUncertain(location.getH_uncertain());
									locObj.setType(location.getFlag());
									
									long network_time = 0;
									if( location.getTimestamp() != null )
										network_time = (long)location.getTimestamp()*1000;
									Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
									locObj.setDatetime(datetime);
									locLst.add(locObj);
									//lastTime = location.getTimestamp();
									//log.debug("location cache added "+location.getTimestamp());
									
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
									locObj.setHUncertain(location.getH_uncertain());
									locObj.setType(location.getFlag());
									
									long network_time = 0;
									if( location.getTimestamp() != null )
										network_time = (long)location.getTimestamp()*1000;
									Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
									locObj.setDatetime(datetime);
									locLst.add(locObj);
									//lastTime = location.getTimestamp();
									//log.debug("location cache added "+location.getTimestamp());
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
									locObj.setHUncertain(location.getH_uncertain());
									locObj.setType(location.getFlag());
									long network_time = 0;
									if( location.getTimestamp() != null )
										network_time = (long)location.getTimestamp()*1000;
									Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
									locObj.setDatetime(datetime);
									locLst.add(locObj);
									//lastTime = location.getTimestamp();
								}
							}
							long timed = DateUtils.getUnixtime() - location.getTimestamp();
							log.debugf("DeviceWs.getGpsLocation isFetchNeeded = %d - %d = %d", DateUtils.getUnixtime(), location.getTimestamp(), timed);
							if (timed < MIN_DEV_LOC_FETCH_DUATION)
							{
								isFetchNeeded = false;
								log.debugf("DeviceWs.getGpsLocation isFetchNeeded = false, timed= %d", timed);
							}
						}
					}
				}
				
			} 
			else
			{ // if( cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0 && cal.get(Calendar.SECOND) == 0)
				log.debug("getGpsLocation start_time with all hour, min, sec == 0 in request");
				Calendar utcTime = Calendar.getInstance();
				utcTime.setTimeZone(TimeZone.getTimeZone("Utc"));
				utcTime.setTime(param_start);
				Calendar st = Calendar.getInstance();
				st.setTimeZone(TimeZone.getTimeZone(DateUtils.getTimezoneFromId(Integer.valueOf(net.getTimezone()))));
				st.set(Calendar.YEAR, utcTime.get(Calendar.YEAR));
				st.set(Calendar.MONTH, utcTime.get(Calendar.MONTH));
				st.set(Calendar.DAY_OF_MONTH, utcTime.get(Calendar.DAY_OF_MONTH));
				st.set(Calendar.HOUR_OF_DAY, 23);
				st.set(Calendar.MINUTE, 59);
				st.set(Calendar.SECOND, 59);
				
				Calendar et = Calendar.getInstance();
				et.setTimeZone(TimeZone.getTimeZone(DateUtils.getTimezoneFromId(Integer.valueOf(net.getTimezone()))));
				et.set(Calendar.YEAR, utcTime.get(Calendar.YEAR));
				et.set(Calendar.MONTH, utcTime.get(Calendar.MONTH));
				et.set(Calendar.DAY_OF_MONTH, utcTime.get(Calendar.DAY_OF_MONTH));					
				et.set(Calendar.HOUR_OF_DAY, 0);
				et.set(Calendar.MINUTE, 0);
				et.set(Calendar.SECOND, 0);
				
				int tr_begin = (int)(st.getTimeInMillis()/1000);
				int tr_end = (int)(et.getTimeInMillis()/1000);
				
				/* ************ BEGIN DB Part: load the gps data ***** */
				String result = null;
				firstConsolidatedTime = LocationUtils.getGpsLocationConsolidated(param_orgId, net.getId(), param_devId);
				if (log.isDebugEnabled())
					log.debug("getGpsLocationForStart() The parameter firstConsolidatedTime="+firstConsolidatedTime+"time="+param_start +" tr_begin="+tr_begin+" tr_end="+tr_end);

				if (firstConsolidatedTime == null)
				{
					log.debug("case 1 The parameter tr_begin= "+tr_begin + " tr_end= "+tr_end + " firstConsolidatedTime= "+firstConsolidatedTime+" NetId=" +dev.getNetworkId()+" devId="+param_devId);
					result = deviceLocationsDAO.getLocationsAllTheDayToJson(param_devId, tr_begin, tr_end, ""+ResponseCode.SUCCESS, caller_ref, server_ref, net.getTimezone());
				}
				else
				{
					end = DateUtils.convertUnixtime2Date(firstConsolidatedTime);
//					Calendar  cons= Calendar.getInstance();
//					cons.setTime(end);
//					Calendar  conset= Calendar.getInstance();
//					//conset.setTimeZone(TimeZone.getTimeZone(DateUtils.getTimezoneFromId(Integer.valueOf(net.getTimezone()))));
//					conset.set(Calendar.YEAR, cons.get(Calendar.YEAR));
//					conset.set(Calendar.MONTH, cons.get(Calendar.MONTH));
//					conset.set(Calendar.DAY_OF_MONTH, cons.get(Calendar.DAY_OF_MONTH));					
//					conset.set(Calendar.HOUR_OF_DAY, 0);
//					conset.set(Calendar.MINUTE, 0);
//					conset.set(Calendar.SECOND, 0);
//					log.debug("The parameter time start= "+et.getTimeInMillis() + " end()= "+ conset.getTimeInMillis()+ " firstConsolidatedTime= "+firstConsolidatedTime);	
					//if (conset != null && conset.after(et))
					if(firstConsolidatedTime > tr_begin)//TODO
					{
						log.debug("case 2 The parameter tr_begin= "+tr_begin + " tr_end= "+tr_end + " firstConsolidatedTime= "+firstConsolidatedTime+" NetId=" +dev.getNetworkId()+" devId="+param_devId);
						result = deviceLocationsDAO.getLocationsAllTheDayToJson(param_devId, tr_begin, tr_end, ""+ResponseCode.SUCCESS, caller_ref, server_ref, net.getTimezone());
					}
					else
					{
						log.debug("case 3 The parameter tr_begin= "+tr_begin + " tr_end= "+tr_end + " firstConsolidatedTime= "+firstConsolidatedTime+" NetId=" +dev.getNetworkId()+" devId="+param_devId);
						result = deviceRecordsDAO.getLocationsAllTheDayToJson(param_devId, tr_end, tr_begin, ""+ResponseCode.SUCCESS, caller_ref, server_ref, net.getTimezone());
					}
				}
				/* ************ END DB Part ********************************** */
				log.debug("getGpsLocation start_time with all hour, min, sec == 0 return result = "+ result);	
				gpsObj.setResult(result);
				gpsObj.setTo_return(true);
				return gpsObj;
			}
			
			/* ****** In realtime case, It fetched command every second
			 * ****** however the device sends commands every 'interval = 20's
			 * */
			if (devLocationsObject == null || isFetchNeeded )
			{
				if(featuresMap.get(dev.getId()) != null && featuresMap.get(dev.getId()) == true && is_low_bandwidth_mode == false)
				{
					ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_LOCATIONS, caller_ref + server_ref, dev.getIanaId(), dev.getSn());
					log.infof("getGpsLocationV2 run after fetchCommand caller_ref=%s, server_ref=%s, sid=%s, Iana_Id=%d, sn=%s",caller_ref, server_ref,caller_ref + server_ref, dev.getIanaId(), dev.getSn());

				}
			}
		}
		catch (Exception e)
		{
			log.error("getGpsLocationForStart() - gpsObj = " +gpsObj + e,e);
		}
		gpsObj.setLocLst(locLst);
		return gpsObj;
	}
	
	public static Json_Device_Gps_Info getGpsLocationForLatest(Devices dev, Networks net)
	{
		if (dev == null || net == null)
		{	
			log.warnf("getGpsLocationForLatest  param_dev= %s , param_net= %s", dev, net);
			return null;	
		}
		log.debugf("11811 DeviceWS.getGpsLocationForLatest() called with devId=%d netId=%d",dev.getId(),net.getId());
		 DeviceGpsLocations latestDevLoc = null;
		 boolean bLastLocIsNull = false; // for adding a null location at the end
		 /*if (devLocListFromDB != null && !devLocListFromDB.isEmpty()){
			 //is the list is not null and not empty
			 //the list should be only has one point which is the latest point 
			 devLocFromDB = devLocListFromDB.get(0);
			 if (devLocFromDB.getLatitude() == null || devLocFromDB.getLongitude() == null){
				 latestDevLoc = deviceLocationsDAO.getLatestNotNullLocation(dev.getId());
				 bLastLocIsNull = true;
			 }else {
				 latestDevLoc = devLocFromDB;
			 }
		 }*/
		 Json_Device_Gps_Info gpsObj = new Json_Device_Gps_Info();
		 List<DeviceGpsLocations> locLst = null;
		
		try
		{
			/* ***** 11811 ***** */
			DevLocationsReportObject devLocReportSample = new DevLocationsReportObject();
			devLocReportSample.setIana_id(dev.getIanaId());
			devLocReportSample.setSn(dev.getSn());
			DevLocationsReportObject devLocReportFromCache = ACUtil.getPoolObjectBySn(devLocReportSample, DevLocationsReportObject.class);
			
			if (devLocReportFromCache != null)
			{
				if( devLocReportFromCache.getLocation_list() != null && !devLocReportFromCache.getLocation_list().isEmpty())
				{
					List<LocationList> locationListFromCache = devLocReportFromCache.getLocation_list();
					List<LocationList> localList = new ArrayList<LocationList>();
					localList.addAll(locationListFromCache);
					/* *** Get the latest locationList, list is sorted in adding *** */
					LocationList latestFromReportCache = localList.get(localList.size()-1);
					if (latestFromReportCache.getLongitude() == null || latestFromReportCache.getLatitude() == null){
					/* list should not be empty and at least has 60 points and there should be no consecutive null location points in cache */ 
						latestFromReportCache = localList.get(localList.size()-2);
						bLastLocIsNull = true;
					}else {
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
					latestDevLoc.setHUncertain(latestFromReportCache.getH_uncertain());
					latestDevLoc.setType(latestFromReportCache.getFlag());
					
					long network_time = 0;
					if( latestFromReportCache.getTimestamp() != null )
						network_time = (long)latestFromReportCache.getTimestamp()*1000;
					Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
					latestDevLoc.setDatetime(datetime);
				}
		}
		
			/* ***** 11811 ***** */
		DevLocationsObject devLocationsExample = new DevLocationsObject();
		devLocationsExample.setIana_id(dev.getIanaId());
		devLocationsExample.setSn(dev.getSn());		
		DevLocationsObject devLocationsObject = ACUtil.getPoolObjectBySn(devLocationsExample, DevLocationsObject.class);
		
		if (devLocationsObject != null)
		{
			if( devLocationsObject.getLocation_list() != null && !devLocationsObject.getLocation_list().isEmpty())
			{
				List<LocationList> locationListFromCache = devLocationsObject.getLocation_list();
				List<LocationList> localList = new ArrayList<LocationList>();
				localList.addAll(locationListFromCache);
				/* *** Get the latest locationList, list is sorted in adding*** */
				LocationList latestFromReportCache = localList.get(localList.size()-1);
				/* *** is real time query only has one point and is null, skip to check *** */
				boolean bSkipCheck = false;
				if (latestFromReportCache.getLatitude() == null || latestFromReportCache.getLongitude() == null)
				{
					if (localList.size() > 1){
						latestFromReportCache = localList.get(localList.size()-2);
					}else {
						bSkipCheck = true;
					}
					bLastLocIsNull = true;
				}
				else {
					bLastLocIsNull = false;
				}
				
				if (!bSkipCheck)
				{
					if (latestDevLoc != null)
					{
						if (latestFromReportCache.getTimestamp().intValue() > latestDevLoc.getId().getUnixtime()){
							//realtime cache is latest one
							latestDevLoc.getId().setUnixtime(latestFromReportCache.getTimestamp().intValue());
							latestDevLoc.setLatitude(latestFromReportCache.getLatitude());
							latestDevLoc.setLongitude(latestFromReportCache.getLongitude());
							latestDevLoc.setAltitude(latestFromReportCache.getAltitude());
							latestDevLoc.setSpeed(latestFromReportCache.getSpeed());
							latestDevLoc.setHUncertain(latestFromReportCache.getH_uncertain());
							latestDevLoc.setType(latestFromReportCache.getFlag());
							
							
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
						latestDevLoc.setHUncertain(latestFromReportCache.getH_uncertain());
						latestDevLoc.setType(latestFromReportCache.getFlag());
						
						long network_time = 0;
						if( latestFromReportCache.getTimestamp() != null )
							network_time = (long)latestFromReportCache.getTimestamp()*1000;
						Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
						latestDevLoc.setDatetime(datetime);
					}
				}
			}
		}
		
		locLst = new ArrayList<DeviceGpsLocations>();
		if (latestDevLoc != null)
		{
			locLst.add(latestDevLoc);
			if (!bLastLocIsNull)
			{ /* ****** To Change the previous 'if (bLastLocIsNull)' to 'if (!bLastLocIsNull)' *******/
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
				locLst.add(nullDevLoc);
			}
		}
		else
		{
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
				locLst.add(latestDevLoc);
				//bIsStatic = true;
				gpsObj.setbIsStatic(true);
			}
		}
		
		gpsObj.setLocLst(locLst);
		} catch (Exception e) {
			log.error("getGpsLocationForLatest - gpsObj = " +gpsObj + e,e);			
			return gpsObj;
		}

		if (log.isDebugEnabled())
			log.debug("latestDevLoc = "+latestDevLoc+" bLastLocIsNull = "+bLastLocIsNull);
				
		return gpsObj;
	}
	
	public static String getClientListV3(JsonDeviceRequest request, JsonResponse<List<Json_Clients>> response)
	{

		final boolean bReadOnly = true;
		String param_orgId = request.getOrganization_id();
		Integer param_devId = request.getDevice_id();
//		List<Json_Mac_List> mac_list = request.getMac_list();
		List<String> clientId_list = request.getClient_id_list();
		
		if(log.isInfoEnabled())
			log.info("client v3 params: orgId : " + param_orgId + " client_id_list : " + clientId_list + " param_devId : " + param_devId);
		
		String serverRef1 = request.getServer_ref();
		String serverRef2 = JsonUtils.relateServerRef(serverRef1);
		String sid1 = request.getCaller_ref() + serverRef1;
		String sid2 = request.getCaller_ref() + serverRef2;
		
		List<Json_Clients> resultClientList = new ArrayList<Json_Clients>();
		try
		{
			DevicesDAO devDAO = new DevicesDAO(param_orgId, bReadOnly);
			OuiInfosDAO ouiInfoDAO = new OuiInfosDAO(bReadOnly);
			
			Devices dev = devDAO.findById(param_devId);
			
			if (dev == null){
				log.error("Cannot find device info from database, device_id = "+param_devId);
				response.setResp_code(ResponseCode.INTERNAL_ERROR);
				return JsonUtils.toJson(response);
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
			//log.debug("stationListObj = "+stationListObj);
			if (stationListObj == null){
				if (onlineObj != null && onlineObj.isOnline()){
					//only fetch for online device
					stationListObj = new StationListObject();
					stationListObj.setStation_list(new ArrayList<StationList>());
					stationListObj.setIana_id(dev.getIanaId());
					stationListObj.setSn(dev.getSn());
					stationListObj.setSid(sid2);
					ACUtil.cachePoolObjectBySn(stationListObj, StationListObject.class);
					response.setResp_code(ResponseCode.PENDING);
					if (log.isDebugEnabled())
						log.debug("fetching device station list " + dev.getSn());
					ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_LIST, sid2, dev.getIanaId(), dev.getSn());					
				}else{
					//for offline device, or device never online
					//return empty list
					response.setResp_code(ResponseCode.SUCCESS);
					response.setData(resultClientList);
					
				}
				return JsonUtils.toJson(response);
			}
			if (log.isDebugEnabled())
				log.debugf("getClientListV3 stationListObj=%s", stationListObj);
			
			if (DateUtils.getUtcDate().getTime() - stationListObj.getCreateTime() > STATION_LIST_REFRESH_SEC * 1000)
				ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_LIST, sid2, dev.getIanaId(), dev.getSn());
			
			List<StationList> stationList = stationListObj.getStation_list();
			if (stationList == null || stationList.isEmpty()){
				//there is a cache object, but no client, return empty list
				response.setResp_code(ResponseCode.SUCCESS);
				response.setData(resultClientList);
				return JsonUtils.toJson(response);
			}
			List<StationList> stationListlocalCopy = new ArrayList<StationList>();
			stationListlocalCopy.addAll(stationList);
			
			if (clientId_list != null && clientId_list.size() > 0){		
				//has mac list in query, return only the client exists in the mac list 
				//with bandwidth usage
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
				String mac = null;
				for (String idItem:clientId_list){
					mac = null;
					for (StationList stationListItem:stationListlocalCopy)
					{
						if(stationListItem.getStatus()!=null && !stationListItem.getStatus().equals("active"))
							continue;
						
						if (stationListItem.getClient_id().equals(idItem)){
							Json_Clients client = new Json_Clients();
							client.setClient_id(stationListItem.getClient_id());
							client.setIp(stationListItem.getIp());
							client.setMac(stationListItem.getMac());
							mac = stationListItem.getMac();
							if (stationListItem.getName() != null){
								client.setName(stationListItem.getName());
							}else {
								client.setName("");
							}
							
							UpdatedClientInfoObject client_info = new UpdatedClientInfoObject();
							client_info.setSn(dev.getSn());
							client_info.setIana_id(dev.getIanaId());
							
							client_info = ACUtil.getPoolObjectBySn(client_info, ClientInfoObject.class);
							if( client_info != null && client_info.getClientInfoMap() != null)
							{
								if( client_info.getClientInfoMap().contains(stationListItem.getClient_id()) )
								{
									client.setName(client_info.getClientInfoMap().get(stationListItem.getClient_id()).getClient_name());
								}
							}
							
							client.setSsid(stationListItem.getEssid());
							client.setStatus(stationListItem.getStatus());
							client.setConnection_type(stationListItem.getType());
							client.setDevice(devJson);
							if (stationListItem.getMac() != null)
								client.setManufacturer(ouiInfoDAO.findManufacturerByMac(stationListItem.getMac()));
							if (!localCopyBandWidthList.isEmpty()){//localCopyBandWidthList must not be null
								for (StationStatusList bandwidthItem:localCopyBandWidthList){
//									if (bandwidthItem.getMac().equals(client.getMac()))
									if( bandwidthItem.getClient_id().equals(idItem) )
									{
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
//							break;
						}						
					}
					//create macRequestItem for fetch to device
					SBLRequestMac sblRequestMac = sblRequestJson.new SBLRequestMac();
					sblRequestMac.setMac(mac);
					requestMacList.add(sblRequestMac);					
				}				
				//fetch the mac list to device again
				log.debug("requestMacList = "+requestMacList);
				sblRequestJson.setMac_filter_list(requestMacList);
				sblRequestJson.setMac_filter_timeout(30);
				ACService.<Json_StationBandwidthListRequest>fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_BANDWIDTH_LIST, sid1, dev.getIanaId(), dev.getSn(), sblRequestJson);
			}else {
				//return all the client for this device
				//without bandwidth usage
				for (StationList stationListItem:stationListlocalCopy)
				{
					if(stationListItem.getStatus() != null && !stationListItem.getStatus().equals("active"))
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
					
					UpdatedClientInfoObject client_info = new UpdatedClientInfoObject();
					client_info.setSn(dev.getSn());
					client_info.setIana_id(dev.getIanaId());
					
					client_info = ACUtil.getPoolObjectBySn(client_info, ClientInfoObject.class);
					if( client_info != null && client_info.getClientInfoMap() != null)
					{
						if( client_info.getClientInfoMap().contains(stationListItem.getClient_id()) )
						{
							client.setName(client_info.getClientInfoMap().get(stationListItem.getClient_id()).getClient_name());
						}
					}
					
					client.setSsid(stationListItem.getEssid());
					client.setStatus(stationListItem.getStatus());
					client.setConnection_type(stationListItem.getType());
					client.setDevice(devJson);
					if (stationListItem.getMac() != null)
						client.setManufacturer(ouiInfoDAO.findManufacturerByMac(stationListItem.getMac()));	
					if( client.getIp()==null || !client.getIp().equals("0.0.0.0"))
					{
						resultClientList.add(client);
					}
				}
			}
			response.setData(resultClientList);
			response.setResp_code(ResponseCode.SUCCESS);
			
		}catch( Exception e )
		{
			log.error("getClientListV3 - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
		return JsonUtils.toJson(response);
	}
	
	public static String getDetail(JsonDeviceRequest request, JsonResponse<Json_Devices> response) 
	{		
		final boolean bReadOnlyDb = true;
		
		/* success */
		String param_orgId = request.getOrganization_id();
		Integer param_deviceId = request.getDevice_id();

		String serverRef1 = JsonUtils.genServerRef();
		String serverRef2 = JsonUtils.relateServerRef(serverRef1);
		String sid1 = request.getCaller_ref() + serverRef1;
		String sid2 = request.getCaller_ref() + serverRef2;
		
		AdminInfo adminInfo = new AdminInfo();
		JsonConf_Admin adminConf = null;
		List<Json_Device_Interfaces> ifcList = new ArrayList<Json_Device_Interfaces>();
		try 
		{
			DeviceUpdatesDAO deviceUpdatesDAO = new DeviceUpdatesDAO(param_orgId,bReadOnlyDb);
			ModelsDAO modDAO = new ModelsDAO(bReadOnlyDb);
			ProductsDAO productDAO = new ProductsDAO(bReadOnlyDb);
			DeviceFeaturesDAO deviceFeaturesDAO = new DeviceFeaturesDAO(param_orgId,bReadOnlyDb);
			DeviceFirmwareSchedulesDAO deviceFwScheDAO = new DeviceFirmwareSchedulesDAO(true);
			
			//Devices dev = deviceDAO.findById(param_deviceId);
			Devices dev = NetUtils.getDevicesWithoutNetId(param_orgId, param_deviceId);
			if (dev==null)
			{
				response.setMessage("DEV_NOT_FOUND");
				response.setResp_code(ResponseCode.INVALID_INPUT);
				response.setMessage("dev "+param_deviceId+" is not found in org "+param_orgId);
				return JsonUtils.toJson(response);
			}
			
			Json_Devices devJson = new Json_Devices();
			devJson.parseDevices(dev, param_orgId);
			
			adminInfo.setOrgId(param_orgId);			
			adminInfo.setNetId(dev.getNetworkId());
			adminInfo.setDevId(dev.getId());		
			adminInfo.setIana_id(dev.getIanaId());
			adminInfo.setSn(dev.getSn());
			adminInfo.setDev(dev);
			adminInfo.setSettingsUtils(new ConfigurationSettingsUtils(param_orgId, dev.getNetworkId(), dev.getId()));
			AdminConfigDeviceLevel adminDL = new AdminConfigDeviceLevel(adminInfo);
			adminConf = adminDL.getDatabaseConfig();			
			devJson.setAdminConf(adminConf);
			
			NetworksDAO netDAO = new NetworksDAO(param_orgId, bReadOnlyDb);
			Networks net = netDAO.getNetworksByDevId(dev.getId());
			
			DeviceUpdates devUpdates = deviceUpdatesDAO.getUnappliedConfig(param_deviceId);
			if( devUpdates != null )
			{
				if (devUpdates.getLastput()!=null)
					devJson.setConfig_lastput(DateUtils.offsetFromUtcTimeZoneId(devUpdates.getLastput(),net.getTimezone()));
				else if (devUpdates.getFirstput()!=null)
					devJson.setConfig_lastput(DateUtils.offsetFromUtcTimeZoneId(devUpdates.getFirstput(),net.getTimezone()));
				else if (devUpdates.getFirstsave()!=null)
					devJson.setConfig_lastput(DateUtils.offsetFromUtcTimeZoneId(devUpdates.getFirstsave(),net.getTimezone()));
			}
			else
			{
				if( dev.getLast_sync_date() != null && net.getTimezone() != null )
					devJson.setConfig_updated_at(DateUtils.offsetFromUtcTimeZoneId(new Date(Long.parseLong(dev.getLast_sync_date()+"000")), net.getTimezone()));
			}
			
			if (dev.getOfflineAt()!=null)
				devJson.setOffline_at(DateUtils.offsetFromUtcTimeZoneId(dev.getOfflineAt(), net.getTimezone()));
			Products pdt = productDAO.findById(dev.getProductId());
			log.debug("getDetail: Product from DB = "+pdt);
			if (pdt != null){
				devJson.setProduct_name(pdt.getName());
				devJson.setMv(pdt.getMv());
				devJson.setNetwork_type(pdt.getNetworkType());
				devJson.setDevice_type(pdt.getDeviceType());
				
				try {
					devJson.setFw_ver(dev.getFw_ver());
				} catch (NullPointerException e)
				{
					log.warn("fw_ver does not exist");
				}
				
				if( ProductWsUtils.isWifiSupport(pdt.getId()) )
				{
					devJson.setWifi_cfg(RadioConfigUtils.getDevWifiManaged(dev, param_orgId));
				}
				else
				{
					devJson.setWifi_cfg(WIFICFG.none.name());
				}
				
			}
			
			
			DeviceFirmwareSchedules fwSche = deviceFwScheDAO.getUniqueSchedule(dev.getIanaId(), dev.getSn());
			if( fwSche != null )
			{
				Date network_time = DateUtils.offsetFromUtcTimeZone(DateUtils.getUtcDate(), net.getTimezone());
				devJson.setFw_pending_trial_round(fwSche.getTrial_round());
				devJson.setFw_pending_max_no_of_trial(DeviceFirmwareSchedulesDAO.maxRetry);
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
			
			DeviceFeatures deviceFeatures = deviceFeaturesDAO.findById(dev.getId());
			List<RadioMode> radioModeLst = new ArrayList<RadioMode>();
			//HashMap<Integer, String> radioMap = new HashMap<Integer, String>();
			if( deviceFeatures != null )
			{
				devJson.setSupport_ssid_count(deviceFeatures.getWifiSsid());
				
				String feature_json = deviceFeatures.getFeatureList();
				
				Pattern pattern = Pattern.compile("\"v6_license\":\"\\w+\"");
				Matcher matcher = pattern.matcher(feature_json);
				if( matcher.find() )
				{
					String v6Licence = matcher.group(0).replace("\"", "");
					if(log.isInfoEnabled())
						log.info("v6_licence : " + v6Licence);
					devJson.setV6_license(v6Licence.split(":")[1]);
				}
			}
			
			/*-- begin to get device radio mode --*/

			try {
				JsonConf_RadioSettings radio_settings = null;
				try {
					radio_settings = RadioConfigUtils.getDatabaseRadioFullConfig(param_orgId, dev.getNetworkId(), param_deviceId, true);
				} catch (FeatureNotFoundException e)
				{
					if (log.isInfoEnabled()) log.infof("Feature not found for device %d %s", dev.getIanaId(), dev.getSn());					
					radio_settings = null;
				}
				
				if(radio_settings != null && radio_settings.getModulesLst()!=null)
				{
					/* create radio map */
					List<Modules> modLst = radio_settings.getModulesLst();
					for (Modules m:modLst)
					{
						RadioMode radioMode = devJson.new RadioMode();
						radioMode.setModule_id(m.getModule_id());
						//radioMode.setFrequency_band(m.getFrequency_band());
						radioMode.setFrequency_band(m.getFrequency_band_capability());						
						
						List<JsonConf_SsidProfiles> ssidProfiles = radio_settings.getSsidProfilesLst();
						if( ssidProfiles != null )
						{
							for(JsonConf_SsidProfiles profile : ssidProfiles)
							{
								String radio = profile.getRadio_select();
								//String band = profile.getRadio_band();
								if( radio != null )
								{
									if (radio.equalsIgnoreCase("3"))
									{
										radioMode.setActive_frequency_band(m.getFrequency_band());
									}
									else
									{
										if (radio.equalsIgnoreCase(String.valueOf(radioMode.getModule_id())))
											radioMode.setActive_frequency_band(m.getFrequency_band());
									}									
								}
							}
						}
						
						radioModeLst.add(radioMode);
						//radioMap.put(m.getModule_id(), m.getFrequency_band());
					}
				}
				devJson.setRadio_modules(radioModeLst);
				/* end to get radio modules */
			
				ConfigSettingsObject configSettingsO = ConfigSettingsUtils.getPoolObject(dev.getIanaId(), dev.getSn());
				List<SsidProfiles> ssid_profiles = new ArrayList<SsidProfiles>();
				if(configSettingsO != null)
				{
					ssid_profiles = configSettingsO.getSsidProfilesLst();
				}
				else
				{
					//JsonConf_RadioSettings radio_sets = RadioConfigUtils.getDatabaseRadioFullConfig(param_orgId, dev.getNetworkId(), param_deviceId, true);
					if( radio_settings != null )
					{
						List<JsonConf_SsidProfiles> ssidProfilesLst = radio_settings.getSsidProfilesLst();
						if( ssidProfilesLst != null )
						{
							SsidProfiles ssid_profile = null;
							configSettingsO = new ConfigSettingsObject();
							for( JsonConf_SsidProfiles profile : ssidProfilesLst )
							{
								if (profile != null){
									ssid_profile = configSettingsO.new SsidProfiles();
									ssid_profile.setId(profile.getSsid_id());
									ssid_profile.setSsid(profile.getSsid());
									ssid_profiles.add(ssid_profile);
								}
							}
							configSettingsO.setIana_id(dev.getIanaId());
							configSettingsO.setSn(dev.getSn());
							configSettingsO.setSsidProfilesLst(ssid_profiles);
							//assert BUG is fixed
							ConfigSettingsUtils.putPoolObject(configSettingsO);
						}
					}					
				}
				
				devJson.setSsid_profiles(ssid_profiles);
			} catch (FeatureNotFoundException e) {
				log.warnf("FeatureNotFoundException getDetail - dev %s %d %s %s", param_orgId, dev.getIanaId(), dev.getSn(), e.getMessage());				
			}
			
			/* get info from cache */
			DevOnlineObject devO = PoolObjectDAO.getDevOnlineObject(dev);			
			
			if (devO != null && devO.isOnline() )
			{				
				/* get dev status cache info, fetch request if cache not exist */
				
//				if( devOnlineObject.isOnline() )
//				{
//					devJson.setStatus("online");
//				}
//				else
//				{
//					devJson.setStatus("offline");
//				}
				
				if (devO.isRebuild())
				{
					try
					{
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_ONLINE, JsonUtils.genServerRef(), dev.getIanaId(), dev.getSn());
					}
					catch( Exception e )
					{
						log.error("getDetail - Fetch dev online error -", e);
					}
				}
				
				if (devO.getUptime() == null)
				{
					try
					{
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_SYSINFO, JsonUtils.genServerRef(), dev.getIanaId(), dev.getSn());
					}
					catch( Exception e )
					{
						log.error("getDetail - Fetch system info error -", e);
					}
				}
				
				devJson.setUptime(devO.getUptime());
				devJson.setUptime_appear(devO.getUpdateUptimeDate());
				devJson.setStatus("online");
				devJson.setLan_mac(devO.getLan_mac());//
				//devJson.setWtp_ip(devO.getWan_ip());
				devJson.setWtp_ip(devO.getWtp_ip());
				DevDetailObject devDetailObject = PoolObjectDAO.getDevDetailObject(dev);
				DevDetailJsonObject devDetailJsonObject = PoolObjectDAO.getDevDetailJsonObject(dev);
				
				Boolean isOldXmlStyle = !devO.isDevDetailJson();
				if(log.isInfoEnabled())
					log.info("isOldXmlStyle: " + isOldXmlStyle.toString());
								

				
				// =============  begin devDetail Object ======================		
				if (isOldXmlStyle){
					if( devDetailObject != null && !ACUtil.isNeedRefresh(devDetailObject))
					{	
	//					log.debug("-----------get detail-----------"+devDetailObject.getKey());
						devJson.setFw_ver(devDetailObject.getDev_info().getFw_ver());
	//					System.out.println("------------run here----------");
						
						devJson.setId(devDetailObject.getDevice_id());
						devJson.setProduct_code(devDetailObject.getDev_info().getProduct_code());
						
						if( devDetailObject.getDev_info().getProduct_code() != null &&
						    devDetailObject.getDev_info().getProduct_code().equals("") == false )
						{
							String pt_code = devDetailObject.getDev_info().getProduct_code();
							if(log.isDebugEnabled())
								log.debug("pt_code:"+devDetailObject.getDev_info().getProduct_code());
					
							Models model = modDAO.findByProductCode(pt_code);
	//						String model_name = modDAO.findNameByProductCode(pt_code);
	//						devJson.setModel_name(model_name);
							if( model != null )
							{
	//							System.out.println("The model name is:"+model.getModelName());
								devJson.setModel(model.getModelName());
							}
							else
							{
								if( dev.getModelId() != null )
								{
									model = modDAO.findById(dev.getModelId());
									
									if( model != null )
									{
	//									System.out.println("Product Model:"+model.getModelName());
										devJson.setModel(model.getModelName());
									}
								}
							}
						}
						else
						{
							if( dev.getModelId() != null )
							{
								Models model = modDAO.findById(dev.getModelId());
								if( model != null )
								{
	//								System.out.println("Product Model:"+model.getModelName());
									devJson.setModel(model.getModelName());
									devJson.setProduct_code(model.getProductCode());
								}
							}
						} // end if( devDetailObject.getDev_info().getProduct_code() != null && devDetailObject.getDev_info().getProduct_code().equals("") == false 
						
						devJson.setNetwork_id(devDetailObject.getNetwork_id());
	//					devJson.setProduct_name(devDetailObject.getDev_info().getProduct_name());
						devJson.setSn(devDetailObject.getSn());
						devJson.setFw_ver(devDetailObject.getDev_info().getFw_ver());
						
						if( devDetailObject.getDev_info().getModel()!=null && devDetailObject.getDev_info().getVariant()!=null )
							devJson.setModel_name(devDetailObject.getDev_info().getModel()+"|"+devDetailObject.getDev_info().getVariant());
						
	//					System.out.println("==========get wan list==========");
						
	
	
						// @@@@@@@@@@@@@@@@@@@@@@@@ Wan List
						
						List <DdnsRecords> ddnsRecordsList = null;
						devJson.setDdns_enabled(dev.getDdns_enabled());
						if (dev.getDdns_enabled() != null && dev.getDdns_enabled()){
							ddnsRecordsList = DdnsUtils.getDdnsRecordsList(dev.getIanaId(), dev.getSn());
							List<String> ddnsNameList = null;
							if (ddnsRecordsList != null && ddnsRecordsList.size() > 0){
								ddnsNameList = new ArrayList<String>();
								for (DdnsRecords ddnsRecords: ddnsRecordsList){
									ddnsNameList.add(ddnsRecords.getDdnsName());
								}
							}
							
							if (ddnsNameList != null){
								devJson.setDdns_names(ddnsNameList);
							}
						}
						
	
						// ---- for old XML style
						List<WanList> wlist = devDetailObject.getWan_list();
						if(log.isInfoEnabled())
							log.info("old format XMLStyle - devDetailObject devId:" + param_deviceId + ",orgId:" + param_orgId);
						if( wlist != null )
						{
	//						log.debug("---------------------wlist > 0------------------------");
							for( WanList wan : wlist )
							{
								Json_Device_Interfaces devIfc = new Json_Device_Interfaces();
								
	//							log.debug("0000000:"+wan.toString());
								if (wan != null){
									devIfc.setId(wan.getId());
									
									if (ddnsRecordsList != null && ddnsRecordsList.size() > 0){
										for (DdnsRecords ddnsRecords: ddnsRecordsList){
											if (ddnsRecords.getWanId() != null && !ddnsRecords.getWanId().isEmpty()){
												Integer wanId = Integer.parseInt(ddnsRecords.getWanId());
												if (wanId != null && wanId == (wan.getId())){
													if (dev.getDdns_enabled() != null && dev.getDdns_enabled()){
														devIfc.setDdns_enabled(dev.getDdns_enabled());
													}
													devIfc.setDdns_name(ddnsRecords.getDdnsName());
													break;
												}
											}
										}
									}
									
									
									devIfc.setCarrier_name(wan.getCarrier_name());
									devIfc.setMultiple_ip(wan.getMultiple_ip());
									devIfc.setConn_config_method(wan.getConn_config_method());
									devIfc.setConn_mode(wan.getConn_mode());
									devIfc.setDdns_host(wan.getDdns_host());
									if(wan.getDns_server() != null)
									{
										String[] servers = wan.getDns_server().split(" ");
										if( servers.length > 0 )
										{
											List<String> dns_servers = new ArrayList<String>();
											for(String server : servers)
												dns_servers.add(server);
											devIfc.setDns_servers(dns_servers);
										}
									}
									devIfc.setEsn(wan.getEsn());
									devIfc.setGateway(wan.getGateway());
									devIfc.setHealthcheck(wan.getHealthcheck());
									devIfc.setImei(wan.getImei());
									devIfc.setIp(wan.getIp());
									devIfc.setMeid_dec(wan.getMeid_dec());
									devIfc.setMeid_hex(wan.getMeid_hex());
									devIfc.setMtu(wan.getMtu());																	
									devIfc.setName(wan.getName());
									devIfc.setSignal(wan.getSignal());
									devIfc.setStandby_mode(wan.getStandby_mode());
									devIfc.setStatus(wan.getStatus_message());
									devIfc.setSims(wan.getSims());
									devIfc.setType(wan.getType());
									devIfc.setConn_method(wan.getConn_method());
									devIfc.setConn_mode(wan.getConn_mode());
									devIfc.setConnection_state(wan.getConnection_state());
									devIfc.setHealthy_state(wan.getHealthy_state());
									devIfc.setIs_backup(wan.getIs_backup());
									devIfc.setIs_enable(wan.getIs_enable());
									devIfc.setIs_manual_disconnect(wan.getIs_manual_disconnect());
									devIfc.setIs_overall_up(wan.getIs_overall_up());
									devIfc.setIs_quota_exceed(wan.getIs_quota_exceed());
									devIfc.setMtu_config(wan.getMtu_config());
									devIfc.setMtu_state(wan.getMtu_state());
									devIfc.setPhysical_state(wan.getPhysical_state());
									devIfc.setPort_type(wan.getPort_type());
									devIfc.setStandby_state(wan.getStandby_state());
									devIfc.setStatus_led(wan.getStatus_led());
									devIfc.setAdaptor_type(wan.getAdaptor_type());
									devIfc.setVendor_id(wan.getVendor_id());
									devIfc.setProduct_id(wan.getProduct_id());
									devIfc.setModem_name(wan.getModem_name());
									devIfc.setModem_manufacturer(wan.getModem_manufacturer());
									devIfc.setApn_auto(wan.getApn_auto());
									devIfc.setUsername_auto(wan.getUsername_auto());
									devIfc.setPassword_auto(wan.getPassword_auto());
									devIfc.setDialnum_auto(wan.getDialnum_auto());
									devIfc.setApn_custom(wan.getApn_custom());
									devIfc.setUsername_custom(wan.getUsername_custom());
									devIfc.setPassword_custom(wan.getPassword_custom());
									devIfc.setDialnum_custom(wan.getDialnum_custom());
									devIfc.setS2g3glte(wan.getS2g3glte());
									devIfc.setGroup(wan.getGroup());
									ifcList.add(devIfc);
								}
							}
	
							devJson.setInterfaces(ifcList);
						}
	
							
						
						// @@@@@@@@@@@@@@@@@@@@@@@@ Lan List
						
	
						List<LanList> lanlist = devDetailObject.getLan_list();
						List<Json_Device_Vlan_Interfaces> vlan_list = new ArrayList<Json_Device_Vlan_Interfaces>();
						
						if( lanlist != null )
						{
							for( LanList lan : lanlist )
							{
								Json_Device_Vlan_Interfaces vlan = new Json_Device_Vlan_Interfaces();
								if (lan != null){
									vlan.setVlan_id(lan.getVlan_id());
									vlan.setVlan_ip(lan.getVlan_ip());
									vlan.setNetmask(lan.getNetmask());
									vlan_list.add(vlan);
								}
							}
						}
						
						devJson.setVlan_interfaces(vlan_list);
						if(log.isInfoEnabled())
							log.info("loop old format xmlStyle - devDetailObject devId:" + param_deviceId + ",orgId:" + param_orgId + ", response:");						
						response.setResp_code(ResponseCode.SUCCESS);
					} // end if( devDetailObject != null )
					else
					{
						response.setResp_code(ResponseCode.PENDING);
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_DETAIL, sid1, dev.getIanaId(), dev.getSn());
						if(log.isDebugEnabled())
							log.debug("cache found for device station list " + dev.getSn() + ", SID : " + sid1);
					} // end if( devDetailObject != null ) ... else ...
				} // end if isOldXmlStyle()
				
				// =============  end devDetail Object ======================
				// =============  beginning of devDetailJson Object ======================				
				else {
					Boolean forDebug = (devDetailJsonObject != null);
					if(log.isInfoEnabled())
						log.info("devDetailJsonObject is null?, " + forDebug.toString() + ", devDetailObject devId:" + param_deviceId + ",orgId:" + param_orgId);
					if( devDetailJsonObject != null && !ACUtil.isNeedRefresh(devDetailJsonObject))
					{	
	//					log.debug("-----------get detail-----------"+devDetailJsonObject.getKey());
						devJson.setFw_ver(devDetailJsonObject.getDev_info().getFw_ver());
	//					System.out.println("------------run here----------");
						
						devJson.setId(devDetailJsonObject.getDevice_id());
						devJson.setProduct_code(devDetailJsonObject.getDev_info().getProduct_code());
						
						if( devDetailJsonObject.getDev_info().getProduct_code() != null &&
						    devDetailJsonObject.getDev_info().getProduct_code().equals("") == false )
						{
							String pt_code = devDetailJsonObject.getDev_info().getProduct_code();
							if(log.isDebugEnabled())
								log.debug("pt_code:"+devDetailJsonObject.getDev_info().getProduct_code());
					
							Models model = modDAO.findByProductCode(pt_code);
	//						String model_name = modDAO.findNameByProductCode(pt_code);
	//						devJson.setModel_name(model_name);
							if( model != null )
							{
	//							System.out.println("The model name is:"+model.getModelName());
								devJson.setModel(model.getModelName());
							}
							else
							{
								if( dev.getModelId() != null )
								{
									model = modDAO.findById(dev.getModelId());
									
									if( model != null )
									{
	//									System.out.println("Product Model:"+model.getModelName());
										devJson.setModel(model.getModelName());
									}
								}
							}
						}
						else
						{
							if( dev.getModelId() != null )
							{
								Models model = modDAO.findById(dev.getModelId());
								if( model != null )
								{
	//								System.out.println("Product Model:"+model.getModelName());
									devJson.setModel(model.getModelName());
									devJson.setProduct_code(model.getProductCode());
								}
							}
						} // end if( devDetailJsonObject.getDev_info().getProduct_code() != null && devDetailJsonObject.getDev_info().getProduct_code().equals("") == false 
						
						devJson.setNetwork_id(devDetailJsonObject.getNetwork_id());
	//					devJson.setProduct_name(devDetailJsonObject.getDev_info().getProduct_name());
						devJson.setSn(devDetailJsonObject.getSn());
						devJson.setFw_ver(devDetailJsonObject.getDev_info().getFw_ver());
						
						if( devDetailJsonObject.getDev_info().getModel()!=null && devDetailJsonObject.getDev_info().getVariant()!=null )
							devJson.setModel_name(devDetailJsonObject.getDev_info().getModel()+"|"+devDetailJsonObject.getDev_info().getVariant());
						
	//					System.out.println("==========get wan list==========");
						
	
	
						// @@@@@@@@@@@@@@@@@@@@@@@@ Wan List
						
	
						// ---- for new Json style
						List<JsonDeviceInterfaces> jsonDeviceInterfacesList = devDetailJsonObject.getInterfaces();
						if(log.isInfoEnabled())
							log.info("new format JsonStyle - devDetailJsonObject devId:" + param_deviceId + ",orgId:" + param_orgId);
						
						List <DdnsRecords> ddnsRecordsList = null;
						devJson.setDdns_enabled(dev.getDdns_enabled());
						if (dev.getDdns_enabled() != null && dev.getDdns_enabled()){
							ddnsRecordsList = DdnsUtils.getDdnsRecordsList(dev.getIanaId(), dev.getSn());
							List<String> ddnsNameList = null;
							if (ddnsRecordsList != null && ddnsRecordsList.size() > 0){
								ddnsNameList = new ArrayList<String>();
								for (DdnsRecords ddnsRecords: ddnsRecordsList){
									ddnsNameList.add(ddnsRecords.getDdnsName());
								}
							}
							
							if (ddnsNameList != null){
								devJson.setDdns_names(ddnsNameList);
							}
						}

						
						if( jsonDeviceInterfacesList != null )
						{
	//						log.debug("---------------------wlist > 0------------------------");
							for( JsonDeviceInterfaces jsonDevIfcs : jsonDeviceInterfacesList ){
								Json_Device_Interfaces devIfc = new Json_Device_Interfaces();
								
	//							log.debug("0000000:"+wan.toString());
								if (jsonDevIfcs != null){
									devIfc.setId(jsonDevIfcs.getId());
									
									if (ddnsRecordsList != null && ddnsRecordsList.size() > 0){
										for (DdnsRecords ddnsRecords: ddnsRecordsList){
											if (ddnsRecords.getWanId() != null && !ddnsRecords.getWanId().isEmpty()){
												Integer wanId = Integer.parseInt(ddnsRecords.getWanId());
												if (wanId != null && wanId == jsonDevIfcs.getId()){
													if (dev.getDdns_enabled() != null && dev.getDdns_enabled()){
														devIfc.setDdns_enabled(dev.getDdns_enabled());
													}
													devIfc.setDdns_name(ddnsRecords.getDdnsName());
													break;
												}
											}
										}
									}
									
									devIfc.setCarrier_name(jsonDevIfcs.getCarrier_name());
									devIfc.setMultiple_ip(jsonDevIfcs.getMultiple_ip());
									devIfc.setConn_config_method(jsonDevIfcs.getConn_config_method());
									devIfc.setConn_mode(jsonDevIfcs.getConn_mode());
									devIfc.setDdns_host(jsonDevIfcs.getDdns_host());
									
									List<String> dnsServerList =jsonDevIfcs.getDns_servers();
									if(dnsServerList != null && dnsServerList.size() > 0){
										devIfc.setDns_servers(dnsServerList);
									}
	
									devIfc.setEsn(jsonDevIfcs.getEsn());
									devIfc.setGateway(jsonDevIfcs.getGateway());
									devIfc.setHealthcheck(jsonDevIfcs.getHealthcheck());
									devIfc.setImei(jsonDevIfcs.getImei());
									devIfc.setIp(jsonDevIfcs.getIp());
									devIfc.setMeid_dec(jsonDevIfcs.getMeid_dec());
									devIfc.setMeid_hex(jsonDevIfcs.getMeid_hex());
									devIfc.setMtu(jsonDevIfcs.getMtu());																	
									devIfc.setName(jsonDevIfcs.getName());
									devIfc.setSignal(jsonDevIfcs.getSignal());
									devIfc.setStandby_mode(jsonDevIfcs.getStandby_mode());
									devIfc.setStatus(jsonDevIfcs.getStatus());
									devIfc.setSims(jsonDevIfcs.getSims());
									devIfc.setType(jsonDevIfcs.getType());
									devIfc.setConn_method(jsonDevIfcs.getConn_method());
									devIfc.setConn_mode(jsonDevIfcs.getConn_mode());
									devIfc.setConnection_state(jsonDevIfcs.getConnection_state());
									devIfc.setHealthy_state(jsonDevIfcs.getHealthy_state());
									devIfc.setIs_backup(jsonDevIfcs.getIs_backup());
									devIfc.setIs_enable(jsonDevIfcs.getIs_enable());
									devIfc.setIs_manual_disconnect(jsonDevIfcs.getIs_manual_disconnect());
									devIfc.setIs_overall_up(jsonDevIfcs.getIs_overall_up());
									devIfc.setIs_quota_exceed(jsonDevIfcs.getIs_quota_exceed());
									devIfc.setMtu_config(jsonDevIfcs.getMtu_config());
									devIfc.setMtu_state(jsonDevIfcs.getMtu_state());
									devIfc.setPhysical_state(jsonDevIfcs.getPhysical_state());
									devIfc.setPort_type(jsonDevIfcs.getPort_type());
									devIfc.setStandby_state(jsonDevIfcs.getStandby_state());
									devIfc.setStatus_led(jsonDevIfcs.getStatus_led());
									devIfc.setAdaptor_type(jsonDevIfcs.getAdaptor_type());
									devIfc.setVendor_id(jsonDevIfcs.getVendor_id());
									devIfc.setProduct_id(jsonDevIfcs.getProduct_id());
									devIfc.setModem_name(jsonDevIfcs.getModem_name());
									devIfc.setModem_manufacturer(jsonDevIfcs.getModem_manufacturer());
									devIfc.setApn_auto(jsonDevIfcs.getApn_auto());
									devIfc.setUsername_auto(jsonDevIfcs.getUsername_auto());
									devIfc.setPassword_auto(jsonDevIfcs.getPassword_auto());
									devIfc.setDialnum_auto(jsonDevIfcs.getDialnum_auto());
									devIfc.setApn_custom(jsonDevIfcs.getApn_custom());
									devIfc.setUsername_custom(jsonDevIfcs.getUsername_custom());
									devIfc.setPassword_custom(jsonDevIfcs.getPassword_custom());
									devIfc.setDialnum_custom(jsonDevIfcs.getDialnum_custom());
									devIfc.setS2g3glte(jsonDevIfcs.getS2g3glte());
									devIfc.setGroup(jsonDevIfcs.getGroup());
									if (jsonDevIfcs.getIp() != null){
										if(log.isInfoEnabled())
											log.info("newJsonFormat-loop Interfaces" + jsonDevIfcs.getIp() + "new format JsonStyle - devDetailJsonObject devId:" + param_deviceId + ",orgId:" + param_orgId);
									}
									
									ifcList.add(devIfc);
								}
							} // end for( JsonDeviceInterfaces jsonDevIfcs : jsonDeviceInterfacesList )						

							devJson.setInterfaces(ifcList);
						} // end if( jsonDeviceInterfacesList != null )

	
							
						
						// @@@@@@@@@@@@@@@@@@@@@@@@ Lan List
						
						
						List<com.littlecloud.pool.object.DevDetailJsonObject.LanList> lanlist = devDetailJsonObject.getLan_list();
						List<Json_Device_Vlan_Interfaces> vlan_list = new ArrayList<Json_Device_Vlan_Interfaces>();
						
						if( lanlist != null )
						{
							for( com.littlecloud.pool.object.DevDetailJsonObject.LanList lan : lanlist )
							{
								Json_Device_Vlan_Interfaces vlan = new Json_Device_Vlan_Interfaces();
								if (lan != null){
									vlan.setVlan_id(lan.getVlan_id());
									vlan.setVlan_ip(lan.getVlan_ip());
									vlan.setNetmask(lan.getNetmask());
									vlan_list.add(vlan);
								}
							}
						}
						
						devJson.setVlan_interfaces(vlan_list);
						if(log.isInfoEnabled())
							log.info("loop - new format JsonStyle - devDetailJsonObject devId:" + param_deviceId + ",orgId:" + param_orgId + ", response:");
						
						response.setResp_code(ResponseCode.SUCCESS);	
					} // end if( devDetailJsonObject != null )
					else
					{
						response.setResp_code(ResponseCode.PENDING);
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_DETAIL, sid1, dev.getIanaId(), dev.getSn());
						if(log.isDebugEnabled())
							log.debug("cache found for device station list " + dev.getSn() + ", SID : " + sid1);
					} // end if( devDetailJsonObject != null ) ... else ...
					
					
					
				} // end if isOldXmlStyle() ... else ...
				// =============  end of devDetailJson Object ======================					
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
				
				DevBandwidthObject objExample = new DevBandwidthObject();
				objExample.setIana_id(dev.getIanaId());
				objExample.setSn(dev.getSn());

				DevBandwidthObject devBandwidthObject = ACUtil.<DevBandwidthObject> getPoolObjectBySn(objExample, DevBandwidthObject.class);
				if( devBandwidthObject != null )
				{
					//int clientz = 0;
					int dtx = 0;
					int drx = 0;
					//if( devBandwidthObject.getStationz() != null )
					//	clientz = devBandwidthObject.getStationz().getTotal() - devBandwidthObject.getStationz().getMvpn();
					List<BandwidthList> bandWidthList = devBandwidthObject.getBandwidth_list();
					if (bandWidthList!=null)
					{
						for( BandwidthList band : bandWidthList )
						{
							dtx += band.getDtx();
							drx += band.getDrx();
						}
						devJson.setUsage(dtx + drx);
						//devJson.setClientz(clientz);
					}
					else
						devJson.setUsage(0);
				}
				else
				{
					response.setResp_code(ResponseCode.PENDING); // pending for incomplete client list
					if(log.isDebugEnabled())
						log.debug("fetching device bandwidth " + dev.getSn() + ", SID : " + sid2);
					ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_BANDWIDTH, sid2, dev.getIanaId(), dev.getSn());
				}
				
			}
			else
			{
				if ((devO==null && DeviceUtils.getDevOnlineStatus(dev)==ONLINE_STATUS.ONLINE))
				{
					/* online without online object */
					response.setResp_code(ResponseCode.PENDING);
					devJson.setStatus("online");					
				}
				else
				{				
					// log exception
					response.setResp_code(ResponseCode.SUCCESS);
					if(log.isDebugEnabled())
							log.debug("The device is offline!");
					devJson.setStatus("offline");
				}
			}
			
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
			
			devJson.setTags(Json_Devices.getTagNameList(dev, param_orgId));
			if(log.isInfoEnabled())
				log.info("The Interface length:"+ifcList.size());
			
			if( dev != null )
			{
				if( dev.getLast_gps_unixtime() != null && dev.getLast_gps_latitude() != null && dev.getLast_gps_longitude() != null )
				{
					devJson.setLatitude(dev.getLast_gps_latitude());
					devJson.setLongitude(dev.getLast_gps_longitude());
					devJson.setLocation_timestamp(DateUtils.offsetFromUtcTimeZoneId(new Date(Long.parseLong(dev.getLast_gps_unixtime()+"000")), net.getTimezone()));	
					devJson.setIsStatic(false);
				}
				else if( dev.getLatitude() != 0 || dev.getLongitude() != 0 )
				{
					devJson.setLatitude(dev.getLatitude());
					devJson.setLongitude(dev.getLongitude());
					devJson.setIsStatic(true);
				}
				devJson.setAddress(dev.getAddress());
				
				boolean isMasterDevice = RadioConfigUtils.isMasterDevice(dev.getId(), dev.getNetworkId(), param_orgId);
				devJson.setIs_master_device(isMasterDevice);
			
				Date subExpiryDate = dev.getSubExpiryDate();
				if(subExpiryDate != null)
				{
					devJson.setSub_expiry_date(subExpiryDate);
					
					Date currentDate = DateUtils.getUtcDate();
					if(currentDate.after(subExpiryDate))
						devJson.setExpiredSub(true);
					else
						devJson.setExpiredSub(false);
				}
				else
				{
					devJson.setExpiredSub(true);
				}
			}
			
			if(log.isInfoEnabled())
				log.info("response devJson: " + devJson.toString());
			response.setData(devJson);
			response.setNetwork_time(DateUtils.offsetFromUtcTimeZoneId(DateUtils.getUtcDate(), net.getTimezone()));
			
//			hutil.commitBranchTransaction();
			
		} catch (Exception e) {
			log.error("getDetail - "+param_orgId+"_"+param_deviceId+"_"+serverRef1 +"_"+ serverRef2 +"_"+ sid1 +"_"+ sid2 +" - "+ e, e);
//			hutil.rollbackBranchTransaction();
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		
//		log.debug("Model Name debug: "+JsonUtils.toJson(response));
//		log.debugf("XML20140127 - getDetail(), devId: %s, orgId: %s, json: %s", param_deviceId, param_orgId, JsonUtils.toJson(response));
		//log.debugf("UPDEV20140806 - getDetail - response: %s", response);
		return JsonUtils.toJson(response);
	} // end getDetail()
	
	public static String getDeviceMonth(JsonDeviceRequest request, JsonResponse<List<Json_Device_Month>> response)
	{
		String param_orgId = request.getOrganization_id();
		Integer param_devId = request.getDevice_id();
		String param_month = request.getMonth();
		final boolean bReadOnlyDb = true;
		List<Json_Device_Month> month_date = new ArrayList<Json_Device_Month>();
		
		try 
		{
			NetworksDAO netDAO = new NetworksDAO(param_orgId, bReadOnlyDb);
			Networks net = netDAO.getNetworksByDevId(param_devId);
			DeviceGpsLocationsDatesDAO deviceGpsLocaDatesDAO = new DeviceGpsLocationsDatesDAO(param_orgId);
			
			if( param_month != null && param_month.isEmpty() == false)
			{
				String[] temp = param_month.split("-");
				Calendar start_time = Calendar.getInstance();
				start_time.set(Calendar.YEAR, Integer.parseInt(temp[0]));
				start_time.set(Calendar.MONTH, Integer.parseInt(temp[1])-1);
				start_time.set(Calendar.HOUR_OF_DAY, 0);
				start_time.set(Calendar.MINUTE, 0);
				start_time.set(Calendar.SECOND, 0);
				start_time.set(Calendar.DAY_OF_MONTH, 1);
				Calendar end_time = Calendar.getInstance();
				end_time.setTime(start_time.getTime());
				end_time.add(Calendar.SECOND, -1);

				int from = (int) (start_time.getTimeInMillis() / 1000);
				int to  = (int) (end_time.getTimeInMillis() / 1000);
				
				List<DeviceGpsLocationsDates> datesList = deviceGpsLocaDatesDAO.getDeviceGpsLocationsDatesRange(net.getId(), param_devId, from, to);
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				if (datesList != null && !datesList.isEmpty()){
					for (DeviceGpsLocationsDates dates:datesList){
						Date dateObj = new Date((long)dates.getId().getUnixtime()*1000);
						String dateStr = sdf.format(dateObj);
						Json_Device_Month date_day = new Json_Device_Month();
						date_day.setDay(dateStr);
						month_date.add(date_day);
					}
				} else {
					Calendar today = Calendar.getInstance();
					today.set(Calendar.HOUR_OF_DAY, 0);
					today.set(Calendar.MINUTE, 0);
					today.set(Calendar.SECOND, 0);
					Date dateObj = today.getTime();
					String dateStr = sdf.format(dateObj);
					Json_Device_Month date_day = new Json_Device_Month();
					date_day.setDay(dateStr);
					month_date.add(date_day);
				}
			}
			else
			{
				List<DeviceGpsLocationsDates> datesList = deviceGpsLocaDatesDAO.getDeviceGpsLocationsDatesList(net.getId(), param_devId);
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				if (datesList != null && !datesList.isEmpty()){
					for (DeviceGpsLocationsDates dates:datesList){
						Date dateObj = new Date((long)dates.getId().getUnixtime()*1000);
						String dateStr = sdf.format(dateObj);
						Json_Device_Month date_day = new Json_Device_Month();
						date_day.setDay(dateStr);
						month_date.add(date_day);
						if(log.isInfoEnabled())
							log.info("getDeviceMonth OrgId="+param_orgId+", devId="+param_devId+", found GPS dates:" + date_day);
					}
				} else {
//					Calendar today = Calendar.getInstance();
//					today.set(Calendar.HOUR_OF_DAY, 0);
//					today.set(Calendar.MINUTE, 0);
//					today.set(Calendar.SECOND, 0);
//					Date dateObj = today.getTime();
//					String dateStr = sdf.format(dateObj);
//					Json_Device_Month date_day = new Json_Device_Month();
//					date_day.setDay(dateStr);
//					month_date.add(date_day);
					if(log.isInfoEnabled())
						log.info("getDeviceMonth OrgId="+param_orgId+", devId="+param_devId+", no GPS dates available");
				}
			}
			response.setData(month_date);
			response.setResp_code(ResponseCode.SUCCESS);
			
		}
		catch( Exception e )
		{
			log.error("getDeviceMonth OrgId="+param_orgId+", devId="+param_devId+", month="+param_month, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		
		return JsonUtils.toJson(response);
	}
	
	public static String isEverGPSAvailable(JsonDeviceRequest request,JsonResponse<Json_Device_Locations> response)
	{
	
		final boolean bReadOnlyDb = true;
		boolean isExist = false;
		boolean isStatic = false;
		String param_orgId = request.getOrganization_id();
		Integer param_devId = request.getDevice_id();
		Json_Device_Locations locjson = new Json_Device_Locations();
		
		try
		{
			DeviceGpsLocationsDAO deviceLocationsDAO = new DeviceGpsLocationsDAO(param_orgId,bReadOnlyDb);
			DevicesDAO devDAO = new DevicesDAO(param_orgId,bReadOnlyDb);
			Devices dev = devDAO.findById(param_devId);
			
			DeviceLastLocObject lastLocation = new DeviceLastLocObject();
			lastLocation.setSn(dev.getSn());
			lastLocation.setIana_id(dev.getIanaId());
			
			lastLocation = ACUtil.getPoolObjectBySn(lastLocation, DeviceLastLocObject.class);
			if( lastLocation != null )
			{
				isExist = true;
				isStatic = lastLocation.isStatic();
			}
			else
			{
				DeviceGpsLocations devLoc = deviceLocationsDAO.getLastDeviceIdLocations(dev.getId());
				/*
				 * Generate pool object
				 */
				DeviceLastLocObject devLastLocation = new DeviceLastLocObject();
				devLastLocation.setSn(dev.getSn());
				devLastLocation.setIana_id(dev.getIanaId());
				LocationList loc = new LocationList();
				
				if( devLoc != null ){
					isExist = true;
				
					loc.setAltitude(devLoc.getAltitude());
					loc.setH_dop(devLoc.getHDop());
					loc.setH_uncertain(devLoc.getHUncertain());
					loc.setLatitude(devLoc.getLatitude());
					loc.setLongitude(devLoc.getLongitude());
					loc.setSpeed(devLoc.getSpeed());
					loc.setTimestamp(devLoc.getDatetime().getTime() / 1000);
					loc.setV_uncertain(devLoc.getVUncertain());
					devLastLocation.setLastLocation(loc);
					devLastLocation.setStatic(false);
					ACUtil.cachePoolObjectBySn(devLastLocation, DeviceLastLocObject.class);
				}else {
					if (dev.getLatitude() != 0 || dev.getLongitude() != 0){
						isStatic = true;
						isExist = true;
						loc.setLatitude(dev.getLatitude());
						loc.setLongitude(dev.getLongitude());
						devLastLocation.setLastLocation(loc);
						devLastLocation.setStatic(true);
						ACUtil.cachePoolObjectBySn(devLastLocation, DeviceLastLocObject.class);
					}
				}
			}
		}
		catch( Exception e )
		{
			log.error("isEverGPSAvailable OrgId="+param_orgId+", devId="+param_devId, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		
		if (!isExist){
			//database and static don't have any record, no need to set isStatic
			locjson.setExist(false);
		}else {
			locjson.setExist(true);
			locjson.setIsStatic(isStatic);
		}
		
		response.setData(locjson);
		response.setResp_code(ResponseCode.SUCCESS);
		return JsonUtils.toJson(response);
	}
	
	public static String getStatusTime(JsonDeviceRequest request,JsonResponse<List<Json_device_online_histories>> response)
	{
		final boolean bReadOnlyDb = true;
		
		Integer param_devId = request.getDevice_id();
		String param_orgId = request.getOrganization_id();
		Integer param_begin = request.getStart_num();
		Integer param_limit = request.getLimit();
		
		List<Json_device_online_histories> devHistList = new ArrayList<Json_device_online_histories>();
		
		try
		{
			DeviceOnlineHistoryDAO devOnlineHistoryDAO = new DeviceOnlineHistoryDAO(param_orgId, bReadOnlyDb);
			NetworksDAO netDAO = new NetworksDAO(param_orgId, bReadOnlyDb);
			Networks net = netDAO.getNetworksByDevId(param_devId);
			
			List<DeviceOnlineHistories> hList = devOnlineHistoryDAO.getOnlineHistoryByDeviceId(param_devId, param_begin, param_limit);
			
			if( hList != null && hList.size() > 0)
			{
				for( DeviceOnlineHistories history : hList )
				{
					Json_device_online_histories devOnlineHistory = new Json_device_online_histories();
					if(log.isInfoEnabled())
						log.info("device online net : "+net + param_devId);
					if(history.getOnline_time() != null)
						devOnlineHistory.setOnline_time(DateUtils.offsetFromUtcTimeZoneId(new Date(Long.parseLong(history.getOnline_time().intValue()+"000")), net.getTimezone()));
					if(history.getOffline_time() != null)
						devOnlineHistory.setOffline_time(DateUtils.offsetFromUtcTimeZoneId(new Date(Long.parseLong(history.getOffline_time().intValue()+"000")), net.getTimezone()));
					devHistList.add(devOnlineHistory);
				}
			}
			
			response.setData(devHistList);
			response.setResp_code(ResponseCode.SUCCESS);
			
		}
		catch( Exception e )
		{
			log.error("getStatusTime - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		
		return JsonUtils.toJson(response);
	}
	
	public static String getDeviceFeatures(JsonDeviceRequest request, JsonResponse<Json_Devices> response)
	{
		String param_orgId = request.getOrganization_id();
		Integer param_devId = request.getDevice_id();
		
		Json_Devices result = new Json_Devices();
		boolean isGpsSupport = false;
		boolean isWifiSupport = false;
		boolean isDpiSupport = false;
		boolean isWebAdminTunnelSupport = false;
		boolean isWebProxySupport = false;
		boolean isWebTunnelingSupport = false;
		boolean isPepvpnSupport = false;
		boolean isPortalSupport = false;
		boolean isRoUserSupport = false;
		StringBuffer feature = new StringBuffer();
		boolean gps_tracking_disabled = false;
		try
		{
			DevicesDAO devicesDAO = new DevicesDAO(param_orgId,true);
			DeviceFeaturesDAO deviceFeaturesDAO = new DeviceFeaturesDAO(param_orgId,true);
			ProductsDAO productsDAO = new ProductsDAO(true);
			
			Devices device = devicesDAO.findById(param_devId);
			Products products = null;
			DeviceFeatures device_feature = null;
			
			response.setResp_code(ResponseCode.SUCCESS);
			
			if(device != null)
			{
				products = productsDAO.findById(device.getProductId());
				device_feature = deviceFeaturesDAO.findById(param_devId);
				
				if( products != null )
				{
					if( products.getRadio1_24G()==true || products.getRadio1_5G() ==true ||
						products.getRadio2_24G()==true || products.getRadio2_5G() ==true )
					{
						isWifiSupport = true;
					}
										
					if(products.getPortal_ic2()!=null && products.getPortal_ic2())
					{
						isPortalSupport = true;
					}
					
					if (products.getRo_user_support()!=null && products.getRo_user_support())
					{
						isRoUserSupport = true;
					}
				}
				
				if( device_feature != null )
				{
					
					if( device_feature.isGps_support() != null && device_feature.isGps_support() )
						isGpsSupport = true;
					else 
					{
						if( (device.getLatitude() != 0f) || (device.getLongitude() != 0f) )
							isGpsSupport = true;
					}
					
					if( device_feature.getFeatureList() != null )
					{
						String features = device_feature.getFeatureList();
						Pattern pattern = Pattern.compile("\"report_dpi\":\"\\w+\"");
						Matcher matcher = pattern.matcher(features);
						if( matcher.find() )
						{
							/* Lewis: If key exists, means support */
							// String ndpi = matcher.group(0).replace("\"", "");
							// if( ndpi.split(":")[1].equals("support") || ndpi.split(":")[1].equals("1"))
							isDpiSupport = true;
						}
						
						pattern = Pattern.compile("\"web_admin_tunnel\":\"1\"");
						matcher = pattern.matcher(features);
						if( matcher.find() )
						{
							isWebAdminTunnelSupport = true;
						}
						
						pattern = Pattern.compile("\"web_tunneling\":\"1\"");
						matcher = pattern.matcher(features);
						if( matcher.find() )
						{
							isWebTunnelingSupport = true;
						}
						
						pattern = Pattern.compile("\"webproxy\":true");
						matcher = pattern.matcher(features);
						if( matcher.find() )
						{
							isWebProxySupport = true;
						}
						
						pattern = Pattern.compile("\"speedfusion\":");
						matcher = pattern.matcher(features);
						if( matcher.find() )
						{
							isPepvpnSupport = true;
						}
					}
					else
					{
						if(products!=null && products.getEndpointSupport() && products.getHubSupport() )
							isPepvpnSupport = true;
					}
				}
				else 
				{
					if( (device.getLatitude() != 0f) || (device.getLongitude() != 0f) )
						isGpsSupport = true;
					
					if(products!=null && products.getEndpointSupport() && products.getHubSupport() )
						isPepvpnSupport = true;
				}
				
				/* for gps_tracking_disabled*/
				if (NetworkUtils.isGpsTrackingDisabled(param_orgId, device.getNetworkId()))
					gps_tracking_disabled = true;
			}
			
			if( isWifiSupport==false && isGpsSupport==false && isDpiSupport==false && isWebAdminTunnelSupport==false 
					&& isPepvpnSupport==false && isPortalSupport==false && isRoUserSupport==false)
				feature.append("none");
			else
			{
				if( isWifiSupport )
					feature.append("wifi");
				
				if(isPortalSupport)
				{
					if (feature.toString().isEmpty())
						feature.append("portal_ic2");
					else
						feature.append("|portal_ic2");
				}
				
				if(isRoUserSupport)
				{
					if (feature.toString().isEmpty())
						feature.append("ro_user_support");
					else
						feature.append("|ro_user_support");
				}
				
				if( isGpsSupport )
				{
					if( feature.toString().isEmpty() )
						feature.append("map");
					else
						feature.append("|map");
				}
				
				if(isDpiSupport)
				{
					if( feature.toString().isEmpty() )
						feature.append("dpi");
					else
						feature.append("|dpi");
				}
				
				if( isWebAdminTunnelSupport )
				{
					if( feature.toString().isEmpty() )
						feature.append("web_admin_tunnel");
					else
						feature.append("|web_admin_tunnel");
				}
				
				if( isWebTunnelingSupport )
				{
					if( feature.toString().isEmpty() )
						feature.append("web_tunneling");
					else
						feature.append("|web_tunneling");
				}
				
				if( isWebProxySupport )
				{
					if( feature.toString().isEmpty() )
						feature.append("webproxy");
					else
						feature.append("|webproxy");
				}
				
				if( isPepvpnSupport )
				{
					if( feature.toString().isEmpty() )
						feature.append("pepvpn");
					else
						feature.append("|pepvpn");
				}							
			}

			if (gps_tracking_disabled)
				result.setFeatures(JsonMatcherUtils.JsonMatcherRemoveMap(feature.toString(), "map"));//gps_tracking_disabled
			else
				result.setFeatures(feature.toString());

			result.setId(param_devId);
			response.setData(result);
			
		}
		catch( Exception e )
		{
			log.error("getDeviceFeatures - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		return JsonUtils.toJson(response);
	}
	
	public static String getDeviceProductTypes(JsonDeviceRequest request, JsonResponse<Json_Devices> response)
	{
		String param_orgId = request.getOrganization_id();
		Integer param_devId = request.getDevice_id();
		Json_Devices result = new Json_Devices();
		
		try
		{
			Devices dev = NetUtils.getDevicesWithoutNetId(param_orgId, param_devId);
			if (dev==null)
			{
				log.warnf("%s dev %d not found", param_orgId, param_devId);
				response.setResp_code(ResponseCode.SUCCESS);
				return JsonUtils.toJson(response);
			}
			
			Products product = ProductUtils.getProducts(dev.getProductId());
			String devType = product.getDeviceType();
	
			result.setProduct_type(devType);
			response.setResp_code(ResponseCode.SUCCESS);
			response.setData(result);
		}
		catch( Exception e )
		{
			log.error("Get dev product type error, orgId = " + param_orgId + ", devId = " + param_devId + ", " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}
		
		return JsonUtils.toJson(response);
	}
	
	public static String getDeviceConfigurations(JsonDeviceRequest request, JsonResponse<List<Json_Device_Configuration>> response)
	{
		final boolean bReadOnlyDb = true;
		String param_orgId = request.getOrganization_id();
		int param_deviceId = request.getDevice_id();
		Networks net = null;
		Devices dev = null;
		List<Json_Device_Configuration> json_Device_Configuration_List = new ArrayList<Json_Device_Configuration>();
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");	
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm:ss");
//		HashMap 
		try {
			response.setResp_code(ResponseCode.SUCCESS);
			
			DevicesDAO devDAO = new DevicesDAO(param_orgId,bReadOnlyDb);
			DeviceConfigurationsDAO deviceConfigurationsDAO = new DeviceConfigurationsDAO(param_orgId);
			NetworksDAO netDAO = new NetworksDAO(param_orgId, bReadOnlyDb);
			
			dev = devDAO.findById(param_deviceId);
			net = netDAO.getNetworksByDevId(dev.getId());
			
//			List<DeviceConfigurations> device_Configurations_by_backups = new ArrayList<DeviceConfigurations>();
//			List<Integer> backup_times = new ArrayList<Integer>();
			List<DeviceConfigurations> deivceConfigurationsList = new ArrayList<DeviceConfigurations>();
			
			deivceConfigurationsList = deviceConfigurationsDAO.getDeviceConfigurationsList(param_deviceId);
			//log.debug("deivceConfigurationsList"+deivceConfigurationsList.toString());
			LinkedHashMap<String, ArrayList<DeviceConfigurations>> mapConfig = new LinkedHashMap<String,  ArrayList<DeviceConfigurations>>();
			for (DeviceConfigurations configProcess: deivceConfigurationsList){
				
				Date dateBackUp = new Date(configProcess.getBackupTime() * (long) 1000);
				dateBackUp = DateUtils.offsetFromUtcTimeZone(DateUtils.getUtcDate(dateBackUp), DateUtils.getTimezoneFromId(Integer.valueOf(net.getTimezone())));
				if(log.isInfoEnabled())
					log.info("dateBackUp"+dateBackUp);
				String strKey = dateFormater.format(dateBackUp);
				if(log.isInfoEnabled())
					log.info("strKey"+strKey+"dev_id:"+configProcess.getDeviceId());
				ArrayList<DeviceConfigurations> configArray = mapConfig.get(strKey);
				if (configArray == null){
					configArray = new ArrayList<DeviceConfigurations>();
				}
				configArray.add(configProcess);
				mapConfig.put(strKey, configArray);
			}
			
			
			for (String strKey : mapConfig.keySet()) {
				if(log.isInfoEnabled())
					log.info("Last_strKey"+strKey);
				Json_Device_Configuration json_Device_Configuration = new Json_Device_Configuration();
				ArrayList<DeviceConfigurations> configArray = mapConfig.get(strKey);
				
				json_Device_Configuration.setDate(strKey);
				
				List<file_Info> fileList = new ArrayList<file_Info>();
				for (DeviceConfigurations configProcess: configArray){
					
					file_Info fileTemp = json_Device_Configuration.new file_Info();
					
					Date dateBackUp = new Date(configProcess.getBackupTime() * (long) 1000);
					dateBackUp = DateUtils.offsetFromUtcTimeZone(DateUtils.getUtcDate(dateBackUp), DateUtils.getTimezoneFromId(Integer.valueOf(net.getTimezone())));
					
					float size = (float) configProcess.getFileSize() / 1024;
					NumberFormat numFormat = NumberFormat.getNumberInstance();
				    numFormat.setMaximumFractionDigits(2);
					
					fileTemp.setTime(timeFormater.format(dateBackUp));
					fileTemp.setFileSize(numFormat.format(size).toString() + " KB");
					fileTemp.setId(configProcess.getId());
					fileList.add(fileTemp);
				}
					
				json_Device_Configuration.setFile_list(fileList);
				json_Device_Configuration_List.add(json_Device_Configuration);
			}
			if(log.isInfoEnabled())
				log.info("json_Device_Configuration_List"+json_Device_Configuration_List.toString());
			response.setData(json_Device_Configuration_List);

		} catch (Exception e) {
			log.error("getDeviceProductTypes - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		return JsonUtils.toJson(response);
	}
	
	public static String getDeviceConfigurationFile(JsonDeviceRequest request, JsonResponse<file_Info> response)
	{
		final boolean bReadOnlyDb = true;
		String param_orgId = request.getOrganization_id();
		int param_deviceId = request.getDevice_id();
		int param_id = request.getId();
		file_Info file_If = null;
		Json_Device_Configuration json_Device_Configuration = new Json_Device_Configuration();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Networks net = null;
		Devices dev = null;
		try {			
			response.setResp_code(ResponseCode.SUCCESS);
			DeviceConfigurationsDAO deviceConfigurationsDAO = new DeviceConfigurationsDAO(param_orgId);
			DeviceConfigurations device_Configuration = new DeviceConfigurations();
			device_Configuration = deviceConfigurationsDAO.getDeviceConfigurationFile(param_deviceId,param_id );
			DevicesDAO devDAO = new DevicesDAO(param_orgId,bReadOnlyDb);
			NetworksDAO netDAO = new NetworksDAO(param_orgId, bReadOnlyDb);
			dev = devDAO.findById(param_deviceId);
			net = netDAO.getNetworksByDevId(dev.getId());	
			String sn =  dev.getSn();
			if(device_Configuration != null)
			{			
				Date date = new Date(device_Configuration.getCreatedAt()*(long)1000);
				date = DateUtils.getUtcDate(date);
				Date create_date = DateUtils.offsetFromUtcTimeZone(date, DateUtils.getTimezoneFromId(Integer.valueOf(net.getTimezone())));
				String c_date = format.format(create_date);
				//json_Device_Configuration.setDate(c_date);
				file_If = json_Device_Configuration.new file_Info();
				
				file_If.setTime(sn+"_"+c_date +".conf");
//				json_Device_Configuration.setDeviceId(device_Configuration.getDeviceId()); devUsaList.new Client_UsageInfo();
//				json_Device_Configuration.setFileSize(device_Configuration.getFileSize());
				file_If.setFileContent(new String(device_Configuration.getFileContent()));
				
			}
			response.setData(file_If);
		} 
		catch (Exception e)
		{
			log.error("getDeviceConfigurationFile - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		return JsonUtils.toJson(response);
	}
	
	
	
	public static String getDpiUsageReport(JsonDeviceRequest request, JsonResponse<List<Json_nDpi_Report>> response)
	{
		String param_orgId = request.getOrganization_id();
		Integer param_devId = request.getDevice_id();
		Date param_start = request.getStart();
		Date param_end = request.getEnd();
		Integer param_top = request.getTop();
		String param_type = request.getType();
		List<Json_nDpi_Report> dpiLst = new ArrayList<Json_nDpi_Report>();
		SimpleDateFormat hourly_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat daily_format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat monthly_format = new SimpleDateFormat("yyyy-MM");
		
		try
		{
			DevicesDAO deviceDAO = new DevicesDAO(param_orgId, true);
			NetworksDAO networksDAO = new NetworksDAO(param_orgId,true);
			DeviceDpiUsagesDAO deviceDpiUsagesDAO = new DeviceDpiUsagesDAO(param_orgId, true);
			DeviceDailyDpiUsagesDAO deviceDailyDpiUsagesDAO = new DeviceDailyDpiUsagesDAO(param_orgId, true);
			DeviceMonthlyDpiUsagesDAO deviceMonthlyDpiUsagesDAO = new DeviceMonthlyDpiUsagesDAO(param_orgId,true);
			Devices device = deviceDAO.findById(param_devId);
			Networks networks = networksDAO.findById(device.getNetworkId());
			Json_nDpi_Report dpi = null;
			long time = 0;
			int start = 0;
			int end = 0;
			Calendar cal = null;
			
			response.setResp_code(ResponseCode.SUCCESS);
			switch(param_type)
			{
				case "hourly":
					start = (int)(DateUtils.getUtcDate(param_start, DateUtils.getTimezoneFromId(Integer.parseInt(networks.getTimezone()))).getTime()/1000);
					cal = Calendar.getInstance();
					cal.setTime(param_end);
					cal.add(Calendar.DAY_OF_MONTH, 1);
					Date cur = new Date();
					if( cal.getTime().compareTo(cur) <= 0 )
						end = (int)(DateUtils.getUtcDate(cal.getTime(), DateUtils.getTimezoneFromId(Integer.parseInt(networks.getTimezone()))).getTime()/1000);
					else
					{
						cal = Calendar.getInstance();
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						end = (int)(cal.getTime().getTime()/1000);
//						end = (int)(DateUtils.getUtcDate(cal.getTime(), DateUtils.getTimezoneFromId(Integer.parseInt(networks.getTimezone()))).getTime()/1000);
//						end = (int)(DateUtils.getUtcDate(cur, DateUtils.getTimezoneFromId(Integer.parseInt(networks.getTimezone()))).getTime()/1000);
					}
					if(log.isInfoEnabled())
						log.info("The start and end time : " + start + " " + end);
					DeviceDpiUsages totalUsage = deviceDpiUsagesDAO.getHourlyTotalServiceSize(device.getNetworkId(), device.getId(), start, end);
					List<DeviceDpiUsages> hourlyProtocols = deviceDpiUsagesDAO.getHourlyServiceSize(device.getNetworkId(), device.getId(), start, end, param_top);
					if( hourlyProtocols != null )
					{
						long totalSize = 0;
						if( totalUsage != null )
							totalSize = totalUsage.getSize();
						for(DeviceDpiUsages deviceDpi : hourlyProtocols )
						{
							dpi = new Json_nDpi_Report();
							dpi.setName(deviceDpi.getService());
							if( totalSize > 0 )
								dpi.setPercentage(((float)deviceDpi.getSize()/totalSize)*100);
							Usage usage = null;
							List<Usage> usages = new ArrayList<Usage>();
							
							if(log.isInfoEnabled())
								log.info("total dpi size : " + totalSize +" "+hourlyProtocols.size() + " " + deviceDpi.getDevice_id() + " " + deviceDpi.getNetwork_id() + " " + deviceDpi.getService() + " " + deviceDpi.getSize() );
							List<DeviceDpiUsages> serviceDpis = deviceDpiUsagesDAO.getHourlyRecords(deviceDpi.getNetwork_id(), deviceDpi.getDevice_id(), deviceDpi.getService(), start, end);
							if( serviceDpis != null )
							{
								int cursor = 0;
								DeviceDpiUsages hourlyDpi = null;
								for( int i = start; i < end; i = i + 3600 )
								{
									hourlyDpi = serviceDpis.get(cursor);
									usage = dpi.new Usage();
									usage.setTimestamp(hourly_format.format(DateUtils.offsetFromUtcTimeZoneId(new Date((long)i*1000), networks.getTimezone())));
									if( hourlyDpi != null && (hourlyDpi.getUnixtime().intValue()>=i && hourlyDpi.getUnixtime().intValue() < i+3600 ) )
									{
										usage.setValue(hourlyDpi.getSize());
										if( cursor < serviceDpis.size() - 1 )
											cursor++;
									}
									usages.add(usage);
								}
							}
							else
							{
								for( int i = start; i < end; i = i + 3600 )
								{
									usage = dpi.new Usage();
									usage.setTimestamp(hourly_format.format(DateUtils.offsetFromUtcTimeZoneId(new Date((long)i*1000), networks.getTimezone())));
									usages.add(usage);
								}
							}
							/*
							for(DeviceDpiUsages serviceDpi : serviceDpis )
							{
								time = (long)serviceDpi.getUnixtime()*1000;
								usage = dpi.new Usage();
								usage.setValue(serviceDpi.getSize());
								usage.setTimestamp(hourly_format.format(DateUtils.offsetFromUtcTimeZoneId(new Date(time), networks.getTimezone())));
								usages.add(usage);
							}
							*/
							dpi.setUsage(usages);
							dpiLst.add(dpi);
						}
					}
					break;
				case "daily":
					start = (int)(DateUtils.getUtcDate(param_start, DateUtils.getTimezoneFromId(Integer.parseInt(networks.getTimezone()))).getTime()/1000);
					cal = Calendar.getInstance();
					cal.setTime(param_end);
					cal.add(Calendar.DAY_OF_MONTH, 1);
					end = (int)(DateUtils.getUtcDate(cal.getTime(), DateUtils.getTimezoneFromId(Integer.parseInt(networks.getTimezone()))).getTime()/1000);
					DeviceDailyDpiUsages totalDailyUsage = deviceDailyDpiUsagesDAO.getDailyTotalServiceSize(device.getNetworkId(), device.getId(), start, end);
					List<DeviceDailyDpiUsages> dailyProtocols = deviceDailyDpiUsagesDAO.getDailyServiceSize(device.getNetworkId(), device.getId(), start, end, param_top);
					if( dailyProtocols != null )
					{
						long totalDailySize = 0;
						if(totalDailyUsage != null)
							totalDailySize = totalDailyUsage.getSize();
						for(DeviceDailyDpiUsages deviceDpi : dailyProtocols )
						{
							dpi = new Json_nDpi_Report();
							dpi.setName(deviceDpi.getService());
							if( totalDailySize > 0 )
								dpi.setPercentage(((float)deviceDpi.getSize()/totalDailySize)*100);
							if(log.isInfoEnabled())
								log.info("total dpi size : " + totalDailySize +" "+dailyProtocols.size() + " " + dpi.getPercentage());
							Usage usage = null;
							List<Usage> usages = new ArrayList<Usage>();
							List<DeviceDailyDpiUsages> serviceDpis = deviceDailyDpiUsagesDAO.getDailyRecords(deviceDpi.getNetwork_id(), deviceDpi.getDevice_id(), deviceDpi.getService(), start, end);
							if( serviceDpis != null )
							{
								int cursor = 0;
								DeviceDailyDpiUsages dailyDpi = null;
								for( int i = start; i < end; i = i + 86400 )
								{
									dailyDpi = serviceDpis.get(cursor);
									usage = dpi.new Usage();
									usage.setTimestamp(daily_format.format(DateUtils.offsetFromUtcTimeZoneId(new Date((long)i*1000), networks.getTimezone())));
									if( dailyDpi != null && dailyDpi.getUnixtime().intValue()==i )
									{
										usage.setValue(dailyDpi.getSize());
										if( cursor < serviceDpis.size() - 1 )
											cursor++;
									}
									usages.add(usage);
								}
							}
							else
							{
								for( int i = start; i < end; i = i + 86400 )
								{
									usage = dpi.new Usage();
									usage.setTimestamp(daily_format.format(DateUtils.offsetFromUtcTimeZoneId(new Date((long)i*1000), networks.getTimezone())));
									usages.add(usage);
								}
							}
							/*
							for(DeviceDailyDpiUsages serviceDpi : serviceDpis )
							{
								time = (long)serviceDpi.getUnixtime()*1000;
								usage = dpi.new Usage();
								usage.setValue(serviceDpi.getSize());
								usage.setTimestamp(daily_format.format(DateUtils.offsetFromUtcTimeZoneId(new Date(time), networks.getTimezone())));
								usages.add(usage);
							}
							*/
							dpi.setUsage(usages);
							dpiLst.add(dpi);
						}
					}
					break;
				case "monthly":
					start = (int)(DateUtils.getUtcDate(param_start, DateUtils.getTimezoneFromId(Integer.parseInt(networks.getTimezone()))).getTime()/1000);
					cal = Calendar.getInstance();
					cal.setTime(param_end);
					cal.add(Calendar.DAY_OF_MONTH, 1);
					end = (int)(DateUtils.getUtcDate(cal.getTime(), DateUtils.getTimezoneFromId(Integer.parseInt(networks.getTimezone()))).getTime()/1000);
					DeviceMonthlyDpiUsages totalMonthlyUsage = deviceMonthlyDpiUsagesDAO.getMonthlyTotalServiceSize(device.getNetworkId(), device.getId(), start, end);
					List<DeviceMonthlyDpiUsages> monthlyProtocols = deviceMonthlyDpiUsagesDAO.getMonthlyServiceSize(device.getNetworkId(), device.getId(), start, end, param_top);
					if( monthlyProtocols != null )
					{
						long totalMonthlySize = 0;
						if(totalMonthlyUsage != null)
							totalMonthlySize = totalMonthlyUsage.getSize();
						for(DeviceMonthlyDpiUsages deviceDpi : monthlyProtocols )
						{
							dpi = new Json_nDpi_Report();
							dpi.setName(deviceDpi.getService());
							if(totalMonthlySize > 0)
								dpi.setPercentage(((float)deviceDpi.getSize()/totalMonthlySize)*100);
							if(log.isInfoEnabled())
								log.info("total dpi size : " + totalMonthlySize +" "+monthlyProtocols.size() + " " + dpi.getPercentage());
							Usage usage = null;
							List<Usage> usages = new ArrayList<Usage>();
							List<DeviceMonthlyDpiUsages> serviceDpis = deviceMonthlyDpiUsagesDAO.getMonthlyRecords(deviceDpi.getNetwork_id(), deviceDpi.getDevice_id(), deviceDpi.getService(), start, end);
							for(DeviceMonthlyDpiUsages serviceDpi : serviceDpis )
							{
								time = (long)serviceDpi.getUnixtime()*1000;
								usage = dpi.new Usage();
								usage.setValue(serviceDpi.getSize());
								usage.setTimestamp(monthly_format.format(DateUtils.offsetFromUtcTimeZoneId(new Date(time), networks.getTimezone())));
								usages.add(usage);
							}
							dpi.setUsage(usages);
							dpiLst.add(dpi);
						}
					}
					break;
			}
			
			response.setData(dpiLst);
		}
		catch(Exception e)
		{
			log.error("getDpiUsageReport - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		
		return JsonUtils.toJson(response);
	}
	
	public static String updateDevicesWarrantyDate(JsonDeviceRequest request, JsonResponse<Json_Devices> response)
	{
		List<DeviceUpdateInfo> devInfos = request.getDevInfos();
		DevicesDAO devicesDAO = null;
		Devices devices = null;
		try
		{
			if(log.isInfoEnabled())
				log.info("update device warranty date - list size " + devInfos.size());
			
			for(DeviceUpdateInfo devInfo : devInfos)
			{
				Devices dev = NetUtils.getDevices(devInfo.getOrganization_id(), devInfo.getIana_id(), devInfo.getSn());
				
				if(dev != null)
				{
					devicesDAO = new DevicesDAO(devInfo.getOrganization_id());
					devices = devicesDAO.findById(dev.getId());
					devices.setExpiryDate(devInfo.getExpiry_date());
					devices.setSubExpiryDate(devInfo.getSub_expiry_date());
					devicesDAO.update(devices);
					
					DevOnlineObject devO = DeviceUtils.getDevOnlineObject(dev);
					if (devO!=null && devO.getStatus()==ONLINE_STATUS.WARRANTY_EXPIRED && WtpMsgHandler.isDeviceWarrantValid(dev))
					{
						/* clear previous status in order to revive */
						devO.setStatus(ONLINE_STATUS.OFFLINE);
						DeviceUtils.putOnlineObject(devO);
					}
				}
			}
			response.setResp_code(ResponseCode.SUCCESS);
		}
		catch(Exception e)
		{
			log.error("updateDevicesWarrantyDate - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		}
		
		return JsonUtils.toJson(response);
	}
	
	public static void main(String[] args)
	{
		JsonDeviceRequest request = new JsonDeviceRequest();
		request.setNetwork_id(3);
		request.setOrganization_id("ovGjKb");
		request.setWan_id(0);
		request.setDevice_id(116);
		request.setType("monthly");
		JsonResponse<Json_Device_Timely_Usage> response = new JsonResponse<Json_Device_Timely_Usage>();
		
		System.out.println(DeviceWsHandler.getWanBandwidth(request, response));
	}

		
		//		Gson gson = new Gson();
//		String json = "{\"data\":{\"location_list\":[{\"altitude\":\"331.0\",\"latitude\":\"38.796790999999999\",\"status\":\"0\",\"h_uncertain\":\"5.1929999999999996\",\"timestamp\":\"1390443556\",\"v_uncertain\":3.0,\"longitude\":\"-94.921875999999997\",\"speed\":0.0,\"heading\":176.835938,\"h_dop\":0.59999999999999998,\"v_dop\":0.59999999999999998}],\"version\":2},\"sid\":\"20140113044501633132014011304445549375370\",\"iana_id\":23695,\"type\":\"PIPE_INFO_TYPE_DEV_LOCATIONS\",\"status\":200,\"sn\":\"2830-E3DA-9F1A\"}";
//		String n_json = "{\"data\":{\"location_list\":[{\"altitude\":331.0,\"latitude\":38.796790999999999,\"status\":0,\"h_uncertain\":5.1929999999999996,\"timestamp\":\"1390443556\",\"v_uncertain\":3.0,\"longitude\":-94.921875999999997,\"speed\":0.0,\"heading\":176.835938,\"h_dop\":0.59999999999999998,\"v_dop\":0.59999999999999998}],\"version\":2},\"sid\":\"20140113044501633132014011304445549375370\",\"iana_id\":23695,\"type\":\"PIPE_INFO_TYPE_DEV_LOCATIONS\",\"status\":200,\"sn\":\"2830-E3DA-9F1A\"}";
//
//		QueryInfo<Object> info = null;
//		QueryInfo<Object> n_info = null;
//		info = gson.fromJson(json, QueryInfo.class);
//		n_info = gson.fromJson(n_json, QueryInfo.class);
//		String DATA_TAG = "data";
//		JSONObject object;
//		JSONObject data;
//		
//		object = JSONObject.fromObject(info);
//		data = object.getJSONObject(DATA_TAG);
//		DevLocationsObject devLoc = gson.fromJson(data.toString(), DevLocationsObject.class);
//		
//		String n_DATA_TAG = "data";
//		JSONObject n_object;
//		JSONObject n_data;
//		
//		n_object = JSONObject.fromObject(n_info);
//		n_data = n_object.getJSONObject(n_DATA_TAG);
//		DevLocationsObject n_devLoc = gson.fromJson(n_data.toString(), DevLocationsObject.class);
//		System.out.println("s_devLoc:"+devLoc);
//		LinkedHashMap<String, Integer> mapConfig = new LinkedHashMap<String,  Integer>();
//		mapConfig.put("2014-01-21", 21);
//		mapConfig.put("2014-01-22", 22);
//		mapConfig.put("2014-01-23", 23);
//		for (String strKey : mapConfig.keySet()) 
//		{
//			System.out.println(strKey);
//		}
//		List<String> s = new ArrayList<String>();
//		s.add("s");
//		s.add("t");
//		s.add("r");
//		for(String a : s)
//		{
//			System.out.print(a);
//		}
//		Collections.reverse(s);
//		for(String a : s)
//		{
//			System.out.print(a);
//		}
//	}
}
