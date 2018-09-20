package com.littlecloud.control.webservices.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.DeviceGpsLocationsDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.branch.OuiInfosDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.report.DeviceGpsLocations;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.model.Json_Clients;
import com.littlecloud.control.json.model.Json_Devices;
import com.littlecloud.control.json.request.JsonClientRequest;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.ClientInfoObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.PoolObjectDAO;
import com.littlecloud.pool.object.StationBandwidthListObject;
import com.littlecloud.pool.object.StationListObject;
import com.littlecloud.pool.object.StationList;
import com.littlecloud.pool.object.UpdatedClientInfoObject;
import com.littlecloud.pool.object.StationBandwidthListObject.StationStatusList;
import com.littlecloud.pool.object.utils.NetUtils;

/* Note: Method must be static to avoid NullPointerException from invoke which calls static method */
public class ClientWsHandler {

	private static final Logger log = Logger.getLogger(ClientWsHandler.class);

	public static String getDetail(JsonClientRequest request, JsonResponse<List<Json_Clients>> response)
	{
		
		final boolean bReadOnlyDb = true;
		String param_orgId = request.getOrganization_id();
		Integer param_devId = request.getDevice_id();
		Integer param_netId = request.getNetwork_id();
		String param_clientId = request.getClient_id();
		Integer earlyFirstAppear = 0;
		/* seek cache or fetch */
		Json_Clients clientJson = new Json_Clients();
		List<Json_Clients> clientJsonLst = new ArrayList<Json_Clients>();
		
		try {
			DevicesDAO devDAO = new DevicesDAO(param_orgId,bReadOnlyDb);
			DeviceGpsLocationsDAO devLocDAO = new DeviceGpsLocationsDAO(param_orgId,bReadOnlyDb);

			OuiInfosDAO ouiInfoDAO = new OuiInfosDAO(bReadOnlyDb);

			response.setResp_code(ResponseCode.SUCCESS);

			/* seek cache, else seek device */
			if( param_netId != null && param_clientId != null )
			{
				List<Devices> deviceLst = NetUtils.getDeviceLstByNetId(param_orgId, param_netId);
				if( deviceLst != null )
				{
					for( Devices dev : deviceLst ) // from cache loop through all devices in the network to find all occurrences of the client 
					{
						if (dev == null)
							throw new Exception("device with id " + param_devId + " not found");
						else
						{
							DevOnlineObject devOnlineObj = PoolObjectDAO.getDevOnlineObject(dev);
							if( devOnlineObj!=null && devOnlineObj.isOnline() )
							{
								/* seek mac from stationList of the device */
								StationListObject stationListExample = new StationListObject();
								stationListExample.setIana_id(dev.getIanaId());
								stationListExample.setSn(dev.getSn());
								
								StationListObject stationListObject = ACUtil.<StationListObject> getPoolObjectBySn(stationListExample, StationListObject.class);
								if(log.isDebugEnabled())
									log.debug("stationListExample.getKey() = " + stationListExample.getKey());

								if (stationListObject != null)
								{
									if(log.isDebugEnabled())
										log.debug("cache found for device station list " + dev.getSn());
									earlyFirstAppear = 0;
									Json_Devices devJson = new Json_Devices();
									DeviceGpsLocations devLoc = devLocDAO.getLastDeviceLocations(dev);
									if( devLoc != null )
										devJson.parseDevices(dev, devLoc, param_orgId);
									else
									{
										if(dev.getLatitude()!=0 && dev.getLongitude()!=0)
										{
											devLoc = new DeviceGpsLocations();
											devLoc.setLatitude(dev.getLatitude());
											devLoc.setLongitude(dev.getLongitude());
											devJson.parseDevices(dev, devLoc, param_orgId);
											devJson.setAddress(dev.getAddress());
										} else {
											devJson.parseDevices(dev, param_orgId);
										}
									}
									for (StationList station : stationListObject.getStation_list())
									{
										boolean isChange = false;
										if(station.getClient_id().compareToIgnoreCase(param_clientId) == 0)
										{										
											UpdatedClientInfoObject client_info = new UpdatedClientInfoObject();
											client_info.setSn(dev.getSn());
											client_info.setIana_id(dev.getIanaId());
											
											client_info = ACUtil.getPoolObjectBySn(client_info, ClientInfoObject.class);
											
											if( earlyFirstAppear == null || earlyFirstAppear == 0 )
											{
												clientJson = new Json_Clients();
												clientJson.parseStationList(devJson, station);
												if(log.isDebugEnabled())
													log.debugf("devJson clientId="+param_clientId+", sn="+devJson.getSn());
												
												if( client_info != null && client_info.getClientInfoMap() != null)
												{
													if( client_info.getClientInfoMap().contains(station.getClient_id()) )
													{
														clientJson.setName(client_info.getClientInfoMap().get(station.getClient_id()).getClient_name());
													}
												}

												isChange = true;
												if( station.getFirst_appear_time() != null )
												{
													earlyFirstAppear = station.getFirst_appear_time();
													clientJson.setFirstAppear(new Date(Long.parseLong(station.getFirst_appear_time()+"000")));
												}
												else
												{
													clientJson.setFirstAppear(new Date());
													earlyFirstAppear = (int)(new Date().getTime() / 1000);
												}
												
											}
											else if( station.getFirst_appear_time() != null && station.getFirst_appear_time() < earlyFirstAppear )
											{
												earlyFirstAppear = station.getFirst_appear_time();
												clientJson = new Json_Clients();
												clientJson.parseStationList(devJson, station);
												if(log.isDebugEnabled())
													log.debugf("devJson2 clientId="+param_clientId+", sn="+devJson.getSn());
												
												if( client_info != null && client_info.getClientInfoMap() != null)
												{
													if( client_info.getClientInfoMap().contains(station.getClient_id()) )
													{
														clientJson.setName(client_info.getClientInfoMap().get(station.getClient_id()).getClient_name());
													}
												}
												
												isChange = true;
												if( station.getFirst_appear_time() != null )
													clientJson.setFirstAppear(new Date(Long.parseLong(station.getFirst_appear_time()+"000")));
												else
													clientJson.setFirstAppear(new Date());
												
											}
											
											if(station.getMac()!=null)
											{
												clientJson.setMac(station.getMac());
												clientJson.setManufacturer(ouiInfoDAO.findManufacturerByMac(station.getMac()));
											}
											
											if( isChange ==false )
											{
												clientJson.setFirstAppear(new Date());
											}
											
											StationBandwidthListObject stationBandWidthListExample = new StationBandwidthListObject();
											stationBandWidthListExample.setIana_id(dev.getIanaId());
											stationBandWidthListExample.setSn(dev.getSn());
											StationBandwidthListObject stationBandWidthListObject = ACUtil.<StationBandwidthListObject> getPoolObjectBySn(stationBandWidthListExample, StationBandwidthListObject.class);
											
											if( stationBandWidthListObject != null )
											{
												List<StationStatusList> statusList = stationBandWidthListObject.getStation_status_list();
												if( statusList != null )
												{
													for( StationStatusList status : statusList )
													{
														if( status.getClient_id() != null && status.getClient_id().equals(param_clientId) )
														{
															clientJson.setDownload_rate(status.getDrx());
															clientJson.setUpload_rate(status.getDtx());
															break;
														}
														
													}
												}
											}
											clientJsonLst.add(clientJson);
										}
									}
								}
							}
						}
					}
				}
			} else if( param_devId != null ) // if network id is null
			{
				Devices dev = devDAO.findById(param_devId);
				if (dev == null)
					throw new Exception("device with id " + param_devId + " not found");
				else
				{
					DevOnlineObject devOnlineObj = PoolObjectDAO.getDevOnlineObject(dev);
					if( devOnlineObj!=null && devOnlineObj.isOnline() )
					{
						/* seek mac from stationList of the device */
						StationListObject stationListExample = new StationListObject();
						stationListExample.setIana_id(dev.getIanaId());
						stationListExample.setSn(dev.getSn());
						
						StationListObject stationListObject = ACUtil.<StationListObject> getPoolObjectBySn(stationListExample, StationListObject.class);
						if(log.isDebugEnabled())
							log.debug("stationListExample.getKey() = " + stationListExample.getKey());
	
						if (stationListObject == null)
						{
							response.setResp_code(ResponseCode.PENDING); // pending for incomplete client list
							if(log.isDebugEnabled())
								log.debug("fetching device station list " + dev.getSn());
							ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_LIST, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn());
						}
						else
						{
							if(log.isDebugEnabled())
								log.debug("cache found for device station list " + dev.getSn());
	
							Json_Devices devJson = new Json_Devices();
							DeviceGpsLocations devLoc = devLocDAO.getLastDeviceLocations(dev);
							if( devLoc != null )
								devJson.parseDevices(dev, devLoc, param_orgId);
							else
							{
								if(dev.getLatitude()!=0 && dev.getLongitude()!=0)
								{
									devLoc = new DeviceGpsLocations();
									devLoc.setLatitude(dev.getLatitude());
									devLoc.setLongitude(dev.getLongitude());
									devJson.parseDevices(dev, devLoc, param_orgId);
									devJson.setAddress(dev.getAddress());
								} else {
									devJson.parseDevices(dev, param_orgId);
								}
							}
	
							for (StationList station : stationListObject.getStation_list())
							{
								if (station.getClient_id().compareToIgnoreCase(param_clientId) == 0)
								{
									clientJson = new Json_Clients();
									clientJson.parseStationList(devJson, station);
									if(log.isDebugEnabled())
										log.debugf("devJson3 clientId="+param_clientId+", sn="+devJson.getSn());
									
									ClientInfoObject client_info = new ClientInfoObject();
									client_info.setSn(station.getClient_id());
									client_info.setIana_id(9999);
									
									client_info = ACUtil.getPoolObjectBySn(client_info, ClientInfoObject.class);
									if( client_info != null )
									{
										clientJson.setName(client_info.getClient_name());
									}
									
									if(station.getMac()!=null)
									{
										clientJson.setMac(station.getMac());
										clientJson.setManufacturer(ouiInfoDAO.findManufacturerByMac(station.getMac()));
									}
									
									clientJson.setStatus(station.getStatus());
									
									if( station.getFirst_appear_time() != null )
										clientJson.setFirstAppear(new Date((long)station.getFirst_appear_time() * 1000));
									else
										clientJson.setFirstAppear(new Date());
									
									StationBandwidthListObject stationBandWidthListExample = new StationBandwidthListObject();
									stationBandWidthListExample.setIana_id(dev.getIanaId());
									stationBandWidthListExample.setSn(dev.getSn());
									StationBandwidthListObject stationBandWidthListObject = ACUtil.<StationBandwidthListObject> getPoolObjectBySn(stationBandWidthListExample, StationBandwidthListObject.class);
									
									if( stationBandWidthListObject != null )
									{
										List<StationStatusList> statusList = stationBandWidthListObject.getStation_status_list();
										if( statusList != null )
										{
											for( StationStatusList status : statusList )
											{
												if( status.getClient_id() != null && status.getClient_id().equals(param_clientId) )
												{
													clientJson.setDownload_rate(status.getDrx());
													clientJson.setUpload_rate(status.getDtx());
													break;
												}
											}
										}
									}
									else
									{
										if(log.isDebugEnabled())
											log.debug("fetching bandwidth info" + dev.getSn());
										ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_BANDWIDTH_LIST, request.getCaller_ref() + request.getServer_ref(), dev.getIanaId(), dev.getSn());										
									}
									
									clientJsonLst.add(clientJson);
								}
							}
						}
					}
				}
			}
//-----------------------------------------------------------------------------------
//			previous location
//-----------------------------------------------------------------------------------
			if( clientJsonLst.size() == 0 )
			{
				clientJson = new Json_Clients();
				
				if( param_clientId.startsWith("01") && param_clientId.indexOf(".") < 0 )
				{
					String cId = param_clientId.replace("01", "");
					String cMac = "";
					if(cId.length() % 2 ==0) {
						for( int i = 0 ; i < cId.length() ; i=i+2 )
						{
							cMac = cMac + cId.charAt(i) + cId.charAt(i+1);
							if( i < cId.length()-2 )
							{
								cMac += ":";
							}
						}
					}
					clientJson.setStatus("inactive");
					clientJson.setMac(cMac);
					String oui = ouiInfoDAO.findManufacturerByMac(cMac);
					if( oui != null && !oui.isEmpty() )
						clientJson.setManufacturer(oui);
					else
						clientJson.setManufacturer("Unknown");
				}
				
				clientJsonLst.add(clientJson);
			}
			response.setData(clientJsonLst);

		} catch (Exception e) {
			log.error("transaction is rollback if any - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		return JsonUtils.toJson(response);
	}
	
	public static String getGPSLocation(JsonClientRequest request, JsonResponse<Json_Clients> response)
	{
		
		String param_orgId = request.getOrganization_id();
		Integer param_devId = request.getDevice_id();
		
		Float longtitude = null;
		Float latitude = null;
		
		Json_Clients clientJson = new Json_Clients();
		
		try
		{
			DevicesDAO devDAO = new DevicesDAO(param_orgId);
			DeviceGpsLocationsDAO devLocDAO = new DeviceGpsLocationsDAO(param_orgId);
			
			Devices dev = devDAO.findById(param_devId);
			//List<Json_Devices> devLst = new ArrayList<Json_Devices>();			
			
			if( dev != null )
			{
				
				List<DeviceGpsLocations> devLoc = devLocDAO.getLocationsByDeviceIdWithStarttime(dev.getId(), null);
				
				if(devLoc.size() > 0)
				{
//					System.out.println("Find the client gps");
					latitude = devLoc.get(0).getLatitude();
					longtitude = devLoc.get(0).getLongitude();
				}
				else
				{
//					System.out.println("Find no client gps:"+param_devId);
				}
				
			}
			
			clientJson.setLatitude(latitude);
			clientJson.setLongitude(longtitude);
			clientJson.setRadius(20f);
			
			response.setData(clientJson);
			response.setResp_code(ResponseCode.SUCCESS);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.error("transaction is rollback - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
			return JsonUtils.toJson(response);
		}

		return JsonUtils.toJson(response);
	}
	
	public static void main(String[] args)
	{
		List<Integer> a = new ArrayList<Integer>();
		a.add(1);
		a.add(2);
		a.add(3);
		List<Integer> c = new ArrayList<Integer>();
		for(Integer b:a)
		{
			b = 4;
			c.add(b);
		}
		
		for(Integer b:c)
		{
			System.out.println(b);
		}
	}
	
}
