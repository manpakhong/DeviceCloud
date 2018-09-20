package com.littlecloud.ac.messagehandler;

import org.jboss.logging.Logger;

import com.littlecloud.ac.health.ThreadPoolAdapterInfo;
import com.littlecloud.ac.health.ThreadPoolManager;
import com.littlecloud.ac.json.model.Json_AcInfoDevice;
import com.littlecloud.ac.json.model.Json_AcInfoUpdate;
import com.littlecloud.ac.messagehandler.executor.MessageExecutorsController;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.utils.DeviceUtils;

public class AcInfoUpdateMessageHandler {
	private static final Logger log = Logger.getLogger(AcInfoUpdateMessageHandler.class);
	public static boolean doAcInfoUpdateMessage(Json_AcInfoUpdate acInfoUpdate){
		boolean isDone = true;
		try{
			if (acInfoUpdate != null && acInfoUpdate.getDev_list() != null && acInfoUpdate.getDev_list().size() > 0){	
				if (acInfoUpdate.getFull()){ // if full list, triage to thread, 
					MessageExecutorsController.startAcInfoDeviceMessageExecutor(acInfoUpdate.getDev_list());
					log.debug("MessageExecutorsController.getThreadPoolAdapterInfo()  add again");
					ThreadPoolAdapterInfo info = MessageExecutorsController.getThreadPoolAdapterInfo();
					if (info != null)
					{
						ThreadPoolManager.refreshExecutorMonitor(info);
					}
				} else {
					for (Json_AcInfoDevice jAcInfoDevice :acInfoUpdate.getDev_list()){
						isDone = doAcInfoUpdateMessage(jAcInfoDevice);
						if (!isDone){
							log.warnf("ACINFOUP20140630 - AcInfoUpdateMessageHandler.doAcInfoUpdateMessage(jAcInfoDevice), isDone: false, acInfoUpdate:%s", acInfoUpdate);							
							isDone = false;
						}
					}
				}
			} else {
				log.warnf("ACINFOUP20140630 - AcInfoUpdateMessageHandler.doAcInfoUpdateMessage(), data is null or empty: %s", acInfoUpdate);
			}
		} catch (Exception e){
			log.error("ACINFOUP20140630 - AcInfoUpdateMessageHandler.doAcInfoUpdateMessage()", e);
		}
		return isDone;
	} // end doAcInfoUpdateMessage
	
	public static boolean doAcInfoUpdateMessage(Json_AcInfoDevice jAcInfoDevice){
		boolean isUpdated = false;
		try{
			if (jAcInfoDevice != null && jAcInfoDevice.getIana_id() != null && jAcInfoDevice.getSn() != null){
				DevOnlineObject devOnlineObject = DeviceUtils.getDevOnlineObject(jAcInfoDevice.getIana_id(), jAcInfoDevice.getSn());
				if (devOnlineObject != null){
					if (devOnlineObject.getWtp_ip() != null && !devOnlineObject.getWtp_ip().equals(jAcInfoDevice.getWtp_ip())){
						devOnlineObject.setWtp_ip(jAcInfoDevice.getWtp_ip());
						if (log.isDebugEnabled()){
							log.debugf("ACINFOUP20140630 - AcInfoUpdateMessageHandler.doAcInfoUpdateMessage(jAcInfoDevice) - devOnlineObject.setWtp_ip(jAcInfoDevice.getWtp_ip()), jAcInfoDevice:%s", jAcInfoDevice);							
						}
						DeviceUtils.putOnlineObject(devOnlineObject);
					}
				}
			}
			isUpdated = true;
		} catch (Exception e){
			log.errorf(e, "ACINFOUP20140630 - AcInfoUpdateMessageHandler.updateDevOnlineObject(), jAcInfoDevice: %s", jAcInfoDevice);
		}
		return isUpdated;
	}
} // end class
