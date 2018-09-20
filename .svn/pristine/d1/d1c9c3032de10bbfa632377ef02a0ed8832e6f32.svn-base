package com.littlecloud.services;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.FirmwareVersionsDAO;
import com.littlecloud.control.entity.FirmwareVersions;
import com.littlecloud.helpers.FirmwareVersionsHelper;

public class FirmwareVersionsMgr {
	private static final Logger log = Logger.getLogger(FirmwareVersionsMgr.class);
	private FirmwareVersionsDAO fwVersionDao;
	private String orgId;
	
	public FirmwareVersionsMgr(String orgId){
		if (orgId != null && !orgId.isEmpty()){
			this.orgId = orgId;
			init();
		} else {
			log.warnf("FirmwareVersionsMgr - orgId is null or empty: %s", orgId);
		}
	}
	
	private void init(){
		try{
			fwVersionDao = new FirmwareVersionsDAO(this.orgId);
		} catch (Exception e){
			log.error("FirmwareVersionsMgr.init() - ", e);
		}
	}
	
	
	public String getLastSetVersion(Integer deviceId, Integer networkId, Integer productId){
		String version = "";
		FirmwareVersions firmwareVersion = getLastFirmwareVersion(deviceId, networkId, productId);
		if (firmwareVersion != null){
			version = FirmwareVersionsHelper.getVersion(firmwareVersion);
		}
		return version;
	}
	
	public FirmwareVersions getLastFirmwareVersion(Integer deviceId, Integer networkId, Integer productId){

		FirmwareVersions firmwareVersion = null;
		try{
			FirmwareVersions deviceLevelFwVersion = getDeviceLevelFwVer(deviceId, productId);
			if (deviceLevelFwVersion != null && deviceLevelFwVersion.getVersion() != null){
				if (deviceLevelFwVersion.getVersion().equalsIgnoreCase(FirmwareVersions.VERSION_GROUP)){ // group level
					FirmwareVersions groupLevelFwVersion = getNetworkLevelFwVer(networkId, productId);
					firmwareVersion = groupLevelFwVersion;
				} else { // device level
					firmwareVersion = deviceLevelFwVersion;
				}
			}
		} catch (Exception e){
			log.error("FirmwareVersionsMgr.getLastSetVersion() - ", e);				
		}
		return firmwareVersion;
	}
	
	public boolean isVersionSameAsInControlVersion(Integer deviceId, Integer networkId, Integer productId, String version2Challenge){
		boolean isTheSame = false;
		try{
			String version = getLastSetVersion(deviceId, networkId, productId);
			
			FirmwareVersions deviceLevelFwVersion = getDeviceLevelFwVer(deviceId, productId);
			if (deviceLevelFwVersion != null && deviceLevelFwVersion.getVersion() != null){
				if (deviceLevelFwVersion.getVersion().equalsIgnoreCase(FirmwareVersions.VERSION_GROUP)){ // group level
					FirmwareVersions groupLevelFwVersion = getNetworkLevelFwVer(networkId, productId);
					version = FirmwareVersionsHelper.getVersion(groupLevelFwVersion);
					if (version != null && !version.isEmpty() && version.equalsIgnoreCase(version2Challenge)){
						isTheSame = true;
					}
				} else { // device level
					version = FirmwareVersionsHelper.getVersion(deviceLevelFwVersion);
					if (version != null && !version.isEmpty() && version.equalsIgnoreCase(version2Challenge)){
						isTheSame = true;
					}
				}
			}
		} catch (Exception e){
			log.error("FirmwareVersionsMgr.isVersionSameAsInControlVersion() - ", e);			
		}
		return isTheSame;
	}
	
//	private String getVersion(FirmwareVersions firmwareVersion){
//		String version = "";
//		if (firmwareVersion != null && firmwareVersion.getVersion() != null){
//			if (firmwareVersion.getVersion().equalsIgnoreCase(FirmwareVersions.VERSION_CUSTOM)){
//				version = firmwareVersion.getCustom_Version();
//			} else {
//				version = firmwareVersion.getVersion();
//			}
//		}
//		return version;
//	}
	
	public FirmwareVersions getDeviceLevelFwVer(Integer deviceId, Integer productId){
		FirmwareVersions firmwareVersion = null;
		try{
			if (deviceId != null && productId != null){
				firmwareVersion = fwVersionDao.getFwVerByDeviceIdAndProductId(deviceId, productId);
			} else {
				log.warnf("FirmwareVersionsMgr.getDeviceLevelFwVer() - deviceId or productId is null! deviceId: %s, productId: %s", deviceId, productId);					
			}
		} catch (Exception e){
			log.error("FirmwareVersionsMgr.getFirmwareVersion() - ", e);			
		}
		return firmwareVersion;
	}
	
	public FirmwareVersions getNetworkLevelFwVer(Integer networkId, Integer productId){
		FirmwareVersions firmwareVersion = null;
		try{
			if (networkId != null && productId != null){
				firmwareVersion = fwVersionDao.getFwVerByNetworkIdAndProductId(networkId, productId);
			} else {
				log.warnf("FirmwareVersionsMgr.getDeviceLevelFwVer() - networkId or productId is null! networkId: %s, productId: %s", networkId, productId);					
			}
		} catch (Exception e){
			log.error("FirmwareVersionsMgr.getFirmwareVersion() - ", e);			
		}
		return firmwareVersion;
	}
	
}
