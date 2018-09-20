package com.littlecloud.control.webservices.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.FirmwareVersionsDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.dao.branch.DeviceFirmwareSchedulesDAO;
import com.littlecloud.control.dao.branch.FirmwaresDAO;
import com.littlecloud.control.dao.branch.ProductsDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.FirmwareVersions;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.branch.DeviceFirmwareSchedules;
import com.littlecloud.control.entity.branch.Firmwares;
import com.littlecloud.control.entity.branch.Products;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.model.Json_Devices;
import com.littlecloud.control.json.model.Json_Product_Firmwares;
import com.littlecloud.control.json.request.JsonFirmwareRequest;
import com.littlecloud.control.json.request.JsonFirmwareRequest.DevFirmware;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.utils.DeviceUtils;

public class FirmwareWsHandler 
{
	private static final Logger log = Logger.getLogger(FirmwareWsHandler.class);

	public static String saveFirmwareUpgradeSchedule(JsonFirmwareRequest request, JsonResponse response){
		String param_orgId = request.getOrganization_id();
		List<DevFirmware> firmware_list = request.getFirmware_list();
		String update_time = request.getUpdate_time();
		Date start_time = request.getStart_time();
		Integer sec = request.getInterval();
		List<Integer> deviceId_list = request.getDevice_list();
		String mode = request.getMode();
		if (deviceId_list != null && firmware_list !=null){
			if (log.isDebugEnabled()){
				log.debugf("FWSAV20140130 (0) - Save firmware upgrade schedule - orgId: %s, firmware_list: %s, update_time: %s, start_time: %s, sec: %s, deviceId_list: %s", param_orgId, firmware_list.toString(), update_time, start_time, sec, deviceId_list.toString());	
			}
		} // end if (deviceId_list != null && firmware_list !=null)
		else {
			if (log.isDebugEnabled()){
				log.warnf("FWSAV20140130 (0) - Save firmware upgrade schedule - orgId: %s, either no deviceList or no firmware list", param_orgId);		
			}
		}
		try
		{

			DevicesDAO devicesDAO = new DevicesDAO(param_orgId, true);
			NetworksDAO networksDAO = new NetworksDAO(param_orgId, true);
			DeviceFirmwareSchedulesDAO devFirmwareSchedulesDAO = new DeviceFirmwareSchedulesDAO();
			FirmwareVersionsDAO fvDAO = new FirmwareVersionsDAO(param_orgId);
			Map<Integer,DevFirmware> firmware_map = new HashMap<Integer,DevFirmware>();
			Date utcDate = null;
			
			int i = 0;
			
			for( DevFirmware firmware : firmware_list ){
				firmware_map.put(firmware.getProduct_id(), firmware);
			}
			
			List<Devices> devLst = devicesDAO.getDevicesList(deviceId_list);
			DeviceFirmwareSchedules schedule = null;
			FirmwareVersions fwVersion = null;
			
			if(devLst != null){
				Networks networks = null;
				for( Devices dev : devLst ){
					networks = networksDAO.findById(dev.getNetworkId());
					if( update_time.equalsIgnoreCase(DeviceFirmwareSchedules.SCHEDULE_TIME_SCHEDULED)){
						utcDate = DateUtils.getUtcDate(start_time, DateUtils.getTimezoneFromId(Integer.valueOf(networks.getTimezone())));
						if (log.isDebugEnabled()){
							log.debugf("FWSAV20140130 (1a) - Save firmware upgrade schedule isScheduled - orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s", param_orgId, dev.getId(), update_time, start_time, sec);		
						}
					}
					else{
//						utcDate = DateUtils.getUtcDate(new Date(), DateUtils.getTimezoneFromId(Integer.valueOf(networks.getTimezone())));
						utcDate = DateUtils.getUtcDate();
						if (log.isDebugEnabled()){
							log.debugf("FWSAV20140130 (1a) - Save firmware upgrade schedule notScheduled- orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s", param_orgId, dev.getId(), update_time, start_time, sec);		
						}
					}
					schedule = new DeviceFirmwareSchedules();
					fwVersion = new FirmwareVersions();
					schedule.setIana_id(dev.getIanaId());
					schedule.setSn(dev.getSn());
					schedule.setProduct_id(dev.getProductId());
					schedule.setNetwork_id(dev.getNetworkId());
					schedule.setOrganization_id(param_orgId);
					
					fwVersion.setProduction_id(dev.getProductId());
					DevFirmware devFirmware = null;
					boolean isVersionDisabled = false;
					boolean isFirmwareFound = false;


					Integer modeLevel = null;						
					if( firmware_map.containsKey(dev.getProductId())){
						devFirmware = firmware_map.get(dev.getProductId()); 
											
						if (devFirmware != null){
							// set mode
							if( mode != null ){
								if (mode.equalsIgnoreCase(JsonFirmwareRequest.MODE_NETWORK)){
									fwVersion.setNetwork_id(dev.getNetworkId());
									modeLevel = DeviceFirmwareSchedules.LEVEL_NETWORK;
								} // end if (mode.equalsIgnoreCase("network"))
								if (mode.equalsIgnoreCase(JsonFirmwareRequest.MODE_DEVICE)){
									modeLevel = DeviceFirmwareSchedules.LEVEL_DEVICE;
									fwVersion.setDevice_id(dev.getId());
								} // end if (mode.equalsIgnoreCase(JsonFirmwareRequest.MODE_DEVICE))
								if (mode.equalsIgnoreCase(JsonFirmwareRequest.MODE_ORGANIZATION)){
									modeLevel = DeviceFirmwareSchedules.LEVEL_ORGANIZATION;
								} // end if (mode.equalsIgnoreCase(JsonFirmwareRequest.MODE_DEVICE))
							}

					 		schedule.setFw_url(devFirmware.getUrl());
							schedule.setRelease_type(devFirmware.getType());
							if (devFirmware.getVersion().equalsIgnoreCase(DevFirmware.STAGE_CUSTOM)){
								schedule.setFw_version(devFirmware.getCustom_version());								
							} else {
								schedule.setFw_version(devFirmware.getVersion());				
							}

							
							schedule.setTrial_round(0);
							schedule.setStatus(0);
							schedule.setLevel(modeLevel); // <---- will be overwritten if follow group
							
							fwVersion.setVersion(devFirmware.getVersion());
							if (devFirmware.getVersion().equalsIgnoreCase(DevFirmware.STAGE_CUSTOM)){
								fwVersion.setCustom_Version(devFirmware.getCustom_version());
							}
							fwVersion.setUrl(devFirmware.getUrl());
							fwVersion.setType(devFirmware.getType());
							
							if (devFirmware.getVersion() != null){
								isVersionDisabled = devFirmware.getVersion().equalsIgnoreCase(DevFirmware.STAGE_DISABLED);
							} // end if (devFirmware.getVersion() != null)
							
							isFirmwareFound = true;
							
							FirmwareVersions existFirmwareVersionNetwork = null;	
							if (mode.equalsIgnoreCase(JsonFirmwareRequest.MODE_DEVICE)){
								if (devFirmware.getVersion().equalsIgnoreCase(DevFirmware.STAGE_GROUP)){
									if (log.isDebugEnabled()){
										log.debugf("FWSAV20140130 (2) - Save firmware upgrade schedule, device mode, is follow group: orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",param_orgId, dev.getId(), update_time, start_time, sec);			
									}
									existFirmwareVersionNetwork = fvDAO.getFwVerByNetworkIdAndProductId(dev.getNetworkId(), dev.getProductId());
									// --- follow group logic 
									if( existFirmwareVersionNetwork != null ){
										// lv 2 - check follow group, find the network/group check the network existing record is not DISABLE
										if (existFirmwareVersionNetwork.getVersion().equalsIgnoreCase(DevFirmware.STAGE_DISABLED)){	
											isVersionDisabled = true;
										} // end if (!exist_ver.getVersion().equalsIgnoreCase(DevFirmware.STAGE_DISABLED))
										else {
											isVersionDisabled = false;
										}
										
										// !!!!!!!!!!!!!!!!!!! because of follow group, so that schedule will retrieve follow group version
										schedule.setFw_url(existFirmwareVersionNetwork.getUrl());
										schedule.setRelease_type(existFirmwareVersionNetwork.getType());
										schedule.setLevel(DeviceFirmwareSchedules.LEVEL_NETWORK); // // <---- overwritten, follow group is found.
										
										if (existFirmwareVersionNetwork.getVersion().equalsIgnoreCase(DevFirmware.STAGE_CUSTOM)){
	//										fwVersion.setVersion(existFirmwareVersionNetwork.getCustom_Version());
											schedule.setFw_version(existFirmwareVersionNetwork.getCustom_Version());
										} else{
	//										fwVersion.setVersion(existFirmwareVersionNetwork.getVersion());
											schedule.setFw_version(existFirmwareVersionNetwork.getVersion());
										} // end if ... else ...
										if (log.isDebugEnabled()){
											log.debugf("FWSAV20140130 (3a) - Save firmware upgrade schedule, device mode - saved!, is follow group, isVersionDisabled: %s, found group version disabled: orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",isVersionDisabled, param_orgId, dev.getId(), update_time, start_time, sec);	
										}
									} else { // end if( exist_ver != null )
										isFirmwareFound = false;
										log.warnf("FWSAV20140130 (3a) - Save firmware upgrade schedule, device mode!, is follow group, but no group setting record!!!, isVersionDisabled: %s, found group version disabled: orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",isVersionDisabled, param_orgId, dev.getId(), update_time, start_time, sec);																	
									}
								}
							}
						} // end if (devFirmware != null)

						if (log.isDebugEnabled()){
							log.debugf("FWSAV20140130 (3b) - Save firmware upgrade schedule isVersionDisabled: %s, isFirmwareFound: %s - orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",isVersionDisabled, isFirmwareFound, param_orgId, dev.getId(), update_time, start_time, sec);		
						}
					} // end if( firmware_map.containsKey(dev.getProductId()))
	
					
					int sched_time = (int)(utcDate.getTime()/1000) + sec * i; 
					schedule.setSchedule_time(sched_time);
					schedule.setCreated_at(new Date());
					fwVersion.setCreated_at(new Date());
//					fwVersion.setUpdated_at(new Date(Long.parseLong(sched_time+"000")));
					fwVersion.setUpdated_at(new Date());
					if (log.isDebugEnabled()){
						log.debugf("FWSAV20140130 (4) - Save firmware upgrade schedule, modeLevel is: %s - orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",modeLevel, param_orgId, dev.getId(), update_time, start_time, sec);		
					}
					FirmwareVersions existFirmwareVersionNetwork = null;	
					FirmwareVersions existFirmwareVersionDevice = null;
					// lv 1 - check individual devFirmware.getVersion check is not DISABLED

					if (mode.equalsIgnoreCase(JsonFirmwareRequest.MODE_NETWORK)){
						existFirmwareVersionNetwork = fvDAO.getFwVerByNetworkIdAndProductId(dev.getNetworkId(), dev.getProductId());
						if( existFirmwareVersionNetwork != null ){
							fvDAO.delete(existFirmwareVersionNetwork);
						} // end if( exist_ver != null )
						fvDAO.save(fwVersion);	
						if (log.isDebugEnabled()){
							log.debugf("FWSAV20140130 (5a) - Save firmware upgrade schedule, network mode device saved!: orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",param_orgId, dev.getId(), update_time, start_time, sec);	
						}
					}// end if (mode.equalsIgnoreCase(JsonFirmwareRequest.MODE_NETWORK))
					else if (mode.equalsIgnoreCase(JsonFirmwareRequest.MODE_DEVICE)) { // device mode 
						//--- check is follow group
						if (devFirmware.getVersion().equalsIgnoreCase(DevFirmware.STAGE_GROUP)){
							if (log.isDebugEnabled()){
								log.debugf("FWSAV20140130 (5b) - Save firmware upgrade schedule, device mode, is follow group: orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",param_orgId, dev.getId(), update_time, start_time, sec);			
							}
							existFirmwareVersionNetwork = fvDAO.getFwVerByNetworkIdAndProductId(dev.getNetworkId(), dev.getProductId());
							
							// --- follow group logic 
							if( existFirmwareVersionNetwork != null ){
								// lv 2 - check follow group, find the network/group check the network existing record is not DISABLE
//								existFirmwareVersionNetwork.setCreated_at(DateUtils.getUtcDate());
								existFirmwareVersionNetwork.setUpdated_at(DateUtils.getUtcDate());
								fvDAO.saveOrUpdate(existFirmwareVersionNetwork); // delete network version
							} // end if( exist_ver != null ) 
							
							existFirmwareVersionDevice = fvDAO.getFwVerByDeviceIdAndProductId(dev.getId(), dev.getProductId());
							if( existFirmwareVersionDevice != null ){
								fvDAO.delete(existFirmwareVersionDevice);
							} // end if( exist_ver != null )
							
							fvDAO.save(fwVersion);	
							if (log.isDebugEnabled()){
								log.debugf("FWSAV20140130 (6b) - Save firmware upgrade schedule, device mode - saved!, is follow group, isVersionDisabled: %s, found group version disabled: orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",isVersionDisabled, param_orgId, dev.getId(), update_time, start_time, sec);			
							}
						}  // end if (devFirmware.getVersion().equalsIgnoreCase(DevFirmware.STAGE_GROUP))
						else {	// --- device mode, not follow group
							if (log.isDebugEnabled()){
								log.debugf("FWSAV20140130 (5c) - Save firmware upgrade schedule, device mode, is not follow group: orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",param_orgId, dev.getId(), update_time, start_time, sec);			
							}
							existFirmwareVersionDevice = fvDAO.getFwVerByDeviceIdAndProductId(dev.getId(), dev.getProductId());
							if( existFirmwareVersionDevice != null ){
								fvDAO.delete(existFirmwareVersionDevice);
							} // end if( exist_ver != null )
							fvDAO.save(fwVersion);	
							if (log.isDebugEnabled()){
								log.debugf("FWSAV20140130 (6c) - Save firmware upgrade schedule, device mode, is not follow group, isVersionDisabled: %s, found group version disabled: orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",isVersionDisabled, param_orgId, dev.getId(), update_time, start_time, sec);		
							}
						} // end if (devFirmware.getVersion().equalsIgnoreCase(DevFirmware.STAGE_CUSTOM))	
					} // end else if (mode.equalsIgnoreCase(JsonFirmwareRequest.MODE_DEVICE)) 
						
					if (log.isDebugEnabled()){
						log.debugf("FWSAV20140130 (7) - Save firmware upgrade schedule, putting into schedule, isVersionDisabled: %s, found group version: orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",isVersionDisabled, param_orgId, dev.getId(), update_time, start_time, sec);		
					}

					if (isFirmwareFound){
						if(isVersionDisabled){
							schedule.setFw_version(DevFirmware.STAGE_DISABLED);
							schedule.setFw_url("");
							schedule.setRelease_type("");
						} // end if(!isVersionDisabled)
						if (schedule.getFw_version() != null){
							if (modeLevel != null){
								DeviceFirmwareSchedules exist_sche =  devFirmwareSchedulesDAO.getUniqueSchedule(dev.getIanaId(), dev.getSn());
								if (log.isDebugEnabled()){
									log.debugf("FWSAV20140130 (8) - Save firmware upgrade schedule, putting into schedule, exist_sche ==null %s, isVersionDisabled: %s, found group version: orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",( exist_sche == null ),isVersionDisabled, param_orgId, dev.getId(), update_time, start_time, sec);	
								}
								if( exist_sche == null ){
									devFirmwareSchedulesDAO.save(schedule);
									if (log.isDebugEnabled()){
										log.debugf("FWSAV20140130 (9a) - Save firmware upgrade schedule, without schedule putting into schedule, isVersionDisabled: %s, found group version disabled: orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",isVersionDisabled, param_orgId, dev.getId(), update_time, start_time, sec);				
									}
								}
								else{
									if (exist_sche.getLevel() != null && exist_sche.getLevel().intValue() <= modeLevel.intValue()){
										devFirmwareSchedulesDAO.delete(exist_sche);
										devFirmwareSchedulesDAO.save(schedule);
										if (log.isDebugEnabled()){
											log.debugf("FWSAV20140130 (9b) - Save firmware upgrade schedule, saved! exist_sche.level: %s <= modeLevel: %s with schedule putting into schedule, isVersionDisabled: %s, found group version disabled: orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",exist_sche.getLevel(), modeLevel,isVersionDisabled, param_orgId, dev.getId(), update_time, start_time, sec);	
										}
									} else {
										if (log.isDebugEnabled()){
											log.debugf("FWSAV20140130 (9c) - Save firmware upgrade schedule, not saved!! exist_sche.level: %s <= modeLevel: %s with schedule putting into schedule, isVersionDisabled: %s, found group version disabled: orgId: %s, devId: %s, update_time: %s, start_time: %s, sec: %s",exist_sche.getLevel(), modeLevel,isVersionDisabled, param_orgId, dev.getId(), update_time, start_time, sec);	
										}
									}
								} // end if if( exist_sche == null ) ... else ...
								i++;
							} // end if (modeLevel != null)
						} // end if (schedule.getFw_version() != null)

					} // end if (isFirmwareFound)
				} // end for( Devices dev : devLst )
			} // end if(devLst != null)
			response.setResp_code(ResponseCode.SUCCESS);
		} // end try ...
		catch( Exception e ){		
			if (log.isDebugEnabled()){
				log.debugf("FWSAV20140130 - transaction is rollback - orgId: %s, firmware_list: %s, update_time: %s, start_time: %s, sec: %s, deviceId_list: %s", param_orgId, firmware_list, update_time, start_time, sec, deviceId_list);
			}
			log.error("FWSAV20140130 - transaction is rollback - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		} // end try ... catch ...
		
		return JsonUtils.toJson(response);
	} // end saveFirmwareUpgradeSchedule
	
	public static String getProductFirmwareVersions(JsonFirmwareRequest request, JsonResponse<List<Json_Product_Firmwares>> response)
	{
		String param_orgId = request.getOrganization_id();
		Integer param_networkId = request.getNetwork_id();
		List<Integer> param_deviceIds = request.getDevice_list();
		List<Json_Product_Firmwares> jsonProductFirmwaresList = new ArrayList<Json_Product_Firmwares>();
		if (log.isDebugEnabled()){
			log.debugf("FW20140128 - getProductFirmwareVersions devIds: %s, orgId: %s, networkId: %s", param_deviceIds, param_orgId, param_networkId);
		}
		try
		{
			DevicesDAO devicesDao = new DevicesDAO(param_orgId,true);

			// either by param_deviceIds or param_networkId
			if (param_deviceIds != null) {
				if (log.isDebugEnabled()){
					log.debugf("FW20140128 - param_deviceIds !=null - devIds: %s, orgId: %s, networkId: %s", param_deviceIds, param_orgId, param_networkId);
				}
				List<Devices> devicesList = devicesDao.getDevicesList(param_deviceIds);
				jsonProductFirmwaresList = getJsonProductFirmwaresList(devicesList, param_orgId);
			} // end if (param_deviceIds != null) 
			else { // param_networkId
				if (param_networkId != null){
					if (log.isDebugEnabled()){
						log.debugf("FW20140128 - param_networkId !=null - devIds: %s, orgId: %s, networkId: %s", param_deviceIds, param_orgId, param_networkId);
					}
					List<Devices> devicesList = devicesDao.getDevicesListByNetworkId(param_networkId);
					jsonProductFirmwaresList = getJsonProductFirmwaresList(devicesList, param_orgId);
				} else {
					log.warnf("FW20140128 - neither param_deviceIds nor param_networkId is passed!");
				}
			} // end if (param_deviceIds != null) ... else ...
			
			response.setResp_code(ResponseCode.SUCCESS);
			response.setData(jsonProductFirmwaresList);
		} // end try ...
		catch( Exception e )
		{
			log.error("transaction is rollback - " + e, e);
			response.setResp_code(ResponseCode.INTERNAL_ERROR);
		} // end try ... catch ...
		if (log.isDebugEnabled()){
			log.debugf("FW20140128 - devIds: %s, orgId: %s, networkId: %s - JsonUtils.toJson(response): %s", param_deviceIds, param_orgId, param_networkId, JsonUtils.toJson(response));
		}
		return JsonUtils.toJson(response);
	} // end getProductFirmwareVersions()

	private static List<Json_Product_Firmwares> getJsonProductFirmwaresList(List<Devices> devicesList, String orgId){
		List<Json_Product_Firmwares> jsonProductFirmwaresList = new ArrayList<Json_Product_Firmwares>();		
		try{
			ProductsDAO productsDao = new ProductsDAO(true);
			FirmwaresDAO firmwaresDao = new FirmwaresDAO(true);
	
			Set<Integer> productIdSet = new HashSet<Integer>();			

			FirmwareVersionsDAO firmwareVersionsDao = new FirmwareVersionsDAO(orgId, true);
			if (devicesList != null){
				for (Devices devices: devicesList){
					Products products = null;
					boolean isProductIdAdded = false; // init value from loop						
					
					if (devices.getProductId() != null){
						products = productsDao.getProductsById(devices.getProductId());
						isProductIdAdded = productIdSet.add(products.getId());
						
						if (isProductIdAdded){
							Json_Product_Firmwares jsonProductFirmwares = null;
							if (products != null){
								jsonProductFirmwares = new Json_Product_Firmwares();
								jsonProductFirmwares.setId(products.getId());
								jsonProductFirmwares.setNetwork_id(null); // assume passing param_deviceIds will not pass param_networkId
								jsonProductFirmwares.setName(products.getName());
								jsonProductFirmwares.setType(products.getDeviceType());
								jsonProductFirmwares.setVersion(devices.getFw_ver());
								
								// add first devices to  a new device List
								List<Json_Devices> jsonDeviceList = new ArrayList<Json_Devices>();
								
								Json_Devices jsonDevices = new Json_Devices();
								jsonDevices.setId(devices.getId());
								jsonDevices.setNetwork_id(devices.getNetworkId());
								jsonDevices.setName(devices.getName());
								jsonDevices.setVersion(devices.getFw_ver());
								jsonDeviceList.add(jsonDevices);
								jsonProductFirmwares.setDevice(jsonDeviceList);
																
								// add version_info List
								List<Firmwares> firmwaresList = firmwaresDao.getFirmwaresListByProductId(products.getId());		
								List<Json_Product_Firmwares.VersionInfo> versionInfoList = new ArrayList<Json_Product_Firmwares.VersionInfo>();
								if (firmwaresList != null){
									for (Firmwares firmwares: firmwaresList){
										if (firmwares.getActive() != null && firmwares.getActive()){
											Json_Product_Firmwares.VersionInfo version_info = jsonProductFirmwares.new VersionInfo();
											
											version_info.setVersion(firmwares.getVersion());
											version_info.setUrl(firmwares.getUrl());
											version_info.setRelease_note(firmwares.getRelease_note());
											version_info.setRelease_type(firmwares.getRelease_type());
											
											versionInfoList.add(version_info);
										} // end if (firmwares.getActive())
									} // end for (Firmwares firmwares: firmwaresList)
								} // end if (firmwaresList != null)
								
								jsonProductFirmwares.setVersion_info(versionInfoList);
								
								// add first firmwareVersions to a new select_version List
								List<Json_Product_Firmwares.SelectVersion> selectVersionList = new ArrayList<Json_Product_Firmwares.SelectVersion>();
								if (log.isDebugEnabled()){
									log.debugf("FW20140128 - getJsonProductFirmwaresList devIds: %s, orgId: %s, networkId: %s", devices.getId(), orgId, devices.getNetworkId());
								}
								List<FirmwareVersions> firmwareVersionsList = firmwareVersionsDao.getFwVerByProductId(products.getId());
								
								if (firmwareVersionsList != null){
									for (FirmwareVersions firmwareVersions: firmwareVersionsList){
										if (firmwareVersions != null){
											Json_Product_Firmwares.SelectVersion jsonSelectVersion = jsonProductFirmwares.new SelectVersion();
											jsonSelectVersion.setDevice_id(firmwareVersions.getDevice_id());
											jsonSelectVersion.setNetwork_id(firmwareVersions.getNetwork_id());

											jsonSelectVersion.setProduct_id(products.getId());
											if (firmwareVersions.getVersion() != null && firmwareVersions.getVersion().equalsIgnoreCase("custom")){
												jsonSelectVersion.setCustom_version(firmwareVersions.getCustom_Version());
											} // end if (firmwareVersions.getVersion() != null && firmwareVersions.getVersion().equalsIgnoreCase("custom"))

											jsonSelectVersion.setVersion(firmwareVersions.getVersion());
											jsonSelectVersion.setUrl(firmwareVersions.getUrl());
											jsonSelectVersion.setRelease_type(firmwareVersions.getType());

											selectVersionList.add(jsonSelectVersion);
										} // end if (firmwareVersions != null)
									}
								}
								jsonProductFirmwares.setSelect_version(selectVersionList);
								jsonProductFirmwaresList.add(jsonProductFirmwares);
								
							} // end if (products != null)
						} // end if (isProductIdAdded) ...
						else {
							
							for (Json_Product_Firmwares jsonProductFirmwares: jsonProductFirmwaresList){
								
								// add product firmware existing jsonProductfirmwares Device List
								if (jsonProductFirmwares.getId().equals(products.getId())){
									Json_Devices jsonDevices = new Json_Devices();
									jsonDevices.setId(devices.getId());
									jsonDevices.setNetwork_id(devices.getNetworkId());
									jsonDevices.setName(devices.getName());
									jsonDevices.setVersion(devices.getFw_ver());
									
									/* bug11180 - process "reboot to" */
									DevOnlineObject devO = DeviceUtils.getDevOnlineObject(devices);
									if (devO!=null && devO.isOnline() && devO.getBootflash()!=null)
									{
										jsonDevices.setBootflash_active(devO.getBootflash().getActive());
										jsonDevices.setBootflashs(Arrays.asList(devO.getBootflash().getFw0(), devO.getBootflash().getFw1()));
									}
									
									jsonProductFirmwares.getDevice().add(jsonDevices);

//									// add firmwareVersion to existing jsonProductFirmwares firmwareVersion List
//									log.debugf("FW20140128 - getJsonProductFirmwaresList devIds: %s, orgId: %s, networkId: %s", devices.getId(), orgId, devices.getNetworkId());
//									List<FirmwareVersions> firmwareVersionsList = firmwareVersionsDao.getFwVerByProductId(products.getId());
//									if (firmwareVersionsList != null){
//										for (FirmwareVersions firmwareVersions: firmwareVersionsList){
//											if (firmwareVersions != null){
//												Json_Product_Firmwares.SelectVersion jsonSelectVersion = jsonProductFirmwares.new SelectVersion();
//												jsonSelectVersion.setDevice_id(firmwareVersions.getDevice_id());
//												jsonSelectVersion.setNetwork_id(firmwareVersions.getNetwork_id());
//												jsonSelectVersion.setProduct_id(products.getId());
//		//										if (firmwareVersions.getVersion() != null && !firmwareVersions.getVersion().equalsIgnoreCase("disabled")){
//													jsonSelectVersion.setUrl(firmwareVersions.getUrl());
//													jsonSelectVersion.setRelease_type(firmwareVersions.getType());
//		//										} // end if (firmwareVersions.getVersion() != null && !firmwareVersions.getVersion().equalsIgnoreCase("disabled"))
//												
//												jsonProductFirmwares.getSelect_version().add(jsonSelectVersion);
//											} // end if (firmwareVersions != null)	
//										}
//									}
								}
								

							} // end for (Json_Product_Firmwares jsonProductFirmwares: jsonProductFirmwaresList)
						} // end end if (!isProductIdAdded) ... else ...
					} // end if (devices.getProductId() != null)
				} // end for (Devices devices: devicesList) 
			} // end if (devicesList != null)
			StringBuilder sb = new StringBuilder();
	        for(Integer intProductIdSet : productIdSet){
	        	sb.append(intProductIdSet);
	        }
	        if (log.isDebugEnabled()){
	        	log.debugf("FW20140128 - getJsonProductFirmwaresList orgId: %s", sb.toString());
	        }
		} // end try ...
		catch (Exception e){
			log.error("FW20140128 - Exception", e);
		} // end try ... catch ...
		return jsonProductFirmwaresList;
	} // end List<Json_Product_Firmwares> genJsonProductFirmwaresList(List<Devices> deviceList)
	
} // end class
