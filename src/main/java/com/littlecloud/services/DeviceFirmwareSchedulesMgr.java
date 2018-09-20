package com.littlecloud.services;

import java.util.Date;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.branch.DeviceFirmwareSchedulesDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.FirmwareVersions;
import com.littlecloud.control.entity.branch.DeviceFirmwareSchedules;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.helpers.FirmwareVersionsHelper;

public class DeviceFirmwareSchedulesMgr {
	private static final Logger log = Logger.getLogger(DeviceFirmwareSchedulesMgr.class);
	private DeviceFirmwareSchedulesDAO devFwSchedluesDao;
	public DeviceFirmwareSchedulesMgr(){
		init();
	}
	
	private void init(){
		try{
			devFwSchedluesDao = new DeviceFirmwareSchedulesDAO();
		} catch (Exception e){
			log.error("DeviceFirmwareSchedulesMgr.init() - ", e);
		}
	}

	public boolean genDeviceFirmwareScheduleAndSave(FirmwareVersions firmwareVersion, String orgId, Devices device){
		boolean isGenAndSaved = false;
		try{
			DeviceFirmwareSchedules deviceFirmwareSchedule = genDeviceFirmwareSchedule(firmwareVersion, orgId, device);
			Integer modeLevel = deviceFirmwareSchedule.getLevel();
			boolean isVersionExisted = false;
			
			String newSetVersion = FirmwareVersionsHelper.getVersion(firmwareVersion);
			
			if (deviceFirmwareSchedule.getProduct_id() != null && newSetVersion != null && !newSetVersion.isEmpty()){
				FirmwareMgr firmwareMgr = new FirmwareMgr();
				isVersionExisted = firmwareMgr.isVersionExisted(deviceFirmwareSchedule.getProduct_id(), newSetVersion);
			}
			
			// if custom version, no need to check firmware version table, just apply change
			if (firmwareVersion.getVersion() != null && firmwareVersion.getVersion().equals(FirmwareVersions.VERSION_CUSTOM)){
				isVersionExisted = true;
			}
			
			if (isVersionExisted){
				DeviceFirmwareSchedules existDeviceFirmwareSchedule = devFwSchedluesDao.getUniqueSchedule(device.getIanaId(), device.getSn());

				if( existDeviceFirmwareSchedule == null ){
					devFwSchedluesDao.save(deviceFirmwareSchedule);
				}
				else{
					boolean hasMovedNetwork = hasMovedNetwork(existDeviceFirmwareSchedule, device);
					if (hasMovedNetwork || (existDeviceFirmwareSchedule.getLevel() != null && existDeviceFirmwareSchedule.getLevel().intValue() <= modeLevel.intValue())){
						devFwSchedluesDao.delete(existDeviceFirmwareSchedule);
						devFwSchedluesDao.save(deviceFirmwareSchedule);
						if (log.isDebugEnabled()){
							log.debugf("DeviceFirmwareSchedulesMgr.genDeviceFirmwareScheduleAndSave() - isVersionExsited: true, existDeviceFirmwareSchedule: %s", existDeviceFirmwareSchedule);
						}
					} else {
						if (log.isDebugEnabled()){
							log.debugf("DeviceFirmwareSchedulesMgr.genDeviceFirmwareScheduleAndSave() - isVersionExsited: false", isVersionExisted);						
						}
					}
				} // end if if( exist_sche == null ) ... else ...
				isGenAndSaved = true;
			}
			
			
		} catch (Exception e){
			log.error("DeviceFirmwareSchedulesMgr.genDeviceFirmwareScheduleAndSave() - ", e);		
		}
		return isGenAndSaved;

	}
	private boolean hasMovedNetwork(DeviceFirmwareSchedules deviceFirmwareSchedule, Devices device){
		boolean hasMoved = false;
		if (deviceFirmwareSchedule != null && device != null){
			if (deviceFirmwareSchedule.getNetwork_id() == null){
				hasMoved = true;
			} else {
				if (deviceFirmwareSchedule.getNetwork_id().intValue() == device.getNetworkId()){
					hasMoved = true;
				}
			}
		}
		
		
		return hasMoved;
	}
	private DeviceFirmwareSchedules genDeviceFirmwareSchedule(FirmwareVersions firmwareVersion, String orgId, Devices device){
		DeviceFirmwareSchedules deviceFirmwareSchedule = new DeviceFirmwareSchedules();

		try{
			int currentLevel = 0;
			if (firmwareVersion.getDevice_id() != null){ // device level
				currentLevel = DeviceFirmwareSchedules.LEVEL_DEVICE;
			} else { // group level
				currentLevel = DeviceFirmwareSchedules.LEVEL_NETWORK;
			}
			Date utcDate = DateUtils.getUtcDate();
			int sec = 10000;
			int schedTime = (int)(utcDate.getTime()/1000) + sec; 
			
			Date now = new Date();
			deviceFirmwareSchedule.setCreated_at(now);
			deviceFirmwareSchedule.setFw_url(firmwareVersion.getUrl());
			deviceFirmwareSchedule.setFw_version(FirmwareVersionsHelper.getVersion(firmwareVersion));
			deviceFirmwareSchedule.setIana_id(device.getIanaId());
			deviceFirmwareSchedule.setNetwork_id(device.getNetworkId());
			deviceFirmwareSchedule.setLevel(currentLevel);
			deviceFirmwareSchedule.setOrganization_id(orgId);
			deviceFirmwareSchedule.setProduct_id(firmwareVersion.getProduction_id());
			deviceFirmwareSchedule.setRelease_type(firmwareVersion.getType());
			deviceFirmwareSchedule.setSchedule_time(schedTime);
			deviceFirmwareSchedule.setSn(device.getSn());
			deviceFirmwareSchedule.setStatus(0);
			deviceFirmwareSchedule.setTrial_round(0);
			
		} catch (Exception e){
			log.error("DeviceFirmwareSchedulesMgr.genDeviceFirmwareScheduleAndSave() - ", e);			
		}
		
		return deviceFirmwareSchedule;
	}
	
	public boolean saveDeviceFirmwareSchedule(DeviceFirmwareSchedules deviceFirmwareSchedule){
		boolean isSaved = false;
		try{
			devFwSchedluesDao.save(deviceFirmwareSchedule);
			isSaved = true;
		} catch (Exception e){
			log.error("DeviceFirmwareSchedulesMgr.saveDeviceFirmwareSchedule() - ", e);
		}
		return isSaved;
	}
	
}
