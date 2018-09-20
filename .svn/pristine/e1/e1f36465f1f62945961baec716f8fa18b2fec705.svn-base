package com.littlecloud.ac.messagehandler;

import org.jboss.logging.Logger;

import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.FirmwareVersions;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.services.DeviceFirmwareSchedulesMgr;
import com.littlecloud.services.FirmwareVersionsMgr;

public class FirmwareMessageHandler {
	private static final Logger log = Logger.getLogger(FirmwareMessageHandler.class);
	public static boolean doOnlineMessage(Devices device, Networks network, String orgId){
		boolean isDone = false;
		try{
			if (device != null && network != null && orgId != null && !orgId.isEmpty()){
				checkAndSyncDeviceFwWithIc2Fw(device, network, orgId);
				isDone = true;
			} else {
				log.warnf("OnlineMessageHandler.doOnlineMessage() device or network or orgId is/are null? device: %s, network: %s, orgId: %s", device, network, orgId);
			}
		} catch (Exception e){
			log.error("OnlineMessageHandler.doOnlineMessage() - Exception: ", e);
		}
		return isDone;
	}
	
	private static boolean checkAndSyncDeviceFwWithIc2Fw(Devices device, Networks network, String orgId){
		boolean isDone = false;
		try{
		FirmwareVersionsMgr fwVersionsMgr = new FirmwareVersionsMgr(orgId);
			if (device.getId() != null && network.getId() != null && device.getFw_ver() != null && device.getProductId() != null && !device.getFw_ver().isEmpty()){
				boolean isVersionTheSame = fwVersionsMgr.isVersionSameAsInControlVersion(device.getId(), network.getId(), device.getProductId(), device.getFw_ver());
				if (!isVersionTheSame){
					FirmwareVersions firmwareVersion = fwVersionsMgr.getLastFirmwareVersion(device.getId(), network.getId(), device.getProductId());
					if (firmwareVersion != null){
						DeviceFirmwareSchedulesMgr devFwScheMgr = new DeviceFirmwareSchedulesMgr();
						isDone = devFwScheMgr.genDeviceFirmwareScheduleAndSave(firmwareVersion, orgId, device);
					}
				}
			}
		} catch (Exception e){
			log.error("OnlineMessageHandler.checkAndSyncDeviceFwWithIc2Fw() - Exception: ", e);			
		}
		return isDone;
	}
}
