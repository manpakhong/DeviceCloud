package com.littlecloud.ac.messagehandler;

import org.jboss.logging.Logger;

import com.littlecloud.ac.json.model.Json_DeviceNeighborList;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.utils.DeviceNeighborUtils;

public class DeviceNeighborSsidDiscoveryMessageHandler {
	private static final Logger log = Logger.getLogger(EventLogMessageHandler.class);
	public static boolean doDeviceNeighborSsidDiscoveryMessage(DevOnlineObject devOnlineO, Json_DeviceNeighborList jsonDeviceNeighborList){
		boolean result = false;
		if (devOnlineO != null && jsonDeviceNeighborList != null){
			if (log.isDebugEnabled()){
				log.debugf("INDOORPOS20140519 - DeviceNeighborSsidDiscoveryHandler - doDeviceNeighborSsidDiscoveryMessage - ianaId:%s, sn:%s, jsonDeviceNeighborList:%s", devOnlineO.getIana_id(), devOnlineO.getSn(), jsonDeviceNeighborList);
			}
			result = DeviceNeighborUtils.updateDevNeighborListByJsonDeviceNeighborList(devOnlineO.getIana_id(), devOnlineO.getSn(), jsonDeviceNeighborList);
		} else {
			if (log.isDebugEnabled()){
				log.debugf("INDOORPOS20140519 - DeviceNeighborSsidDiscoveryHandler - doDeviceNeighborSsidDiscoveryMessage - devOnlineObject is null or jsonDeviceNeighborList is null!");
			}
		}
		return result;
	}
}
