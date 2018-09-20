package com.littlecloud.control.devicechange;

import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.branch.DevicePendingChangesDAO;
import com.littlecloud.control.entity.branch.DevicePendingChanges;
import com.littlecloud.pool.Cluster;
import com.littlecloud.pool.object.DevicePendingChangesObject;
import com.littlecloud.pool.object.utils.DevicePengingChangesUtils;

public class DeviceChangeServiceUtils {
	private static Logger log = Logger.getLogger(DeviceChangeServiceUtils.class);
	private static DeviceChangeService fm = DeviceChangeService.getInstance();
	
	public static enum ACTION {
		all, retry
	}

	public static void updateStatus(Integer ianaId, String sn, String sid, boolean isSuccess)
	{
		try {
			int matchCount = 0;
			
			DevicePendingChangesDAO devChangeDAO = new DevicePendingChangesDAO();
			List<DevicePendingChanges> devChangeLst = devChangeDAO.findbySid(sid);
			
			for (DevicePendingChanges devChange:devChangeLst)
			{
				if (devChange.getIana_id().intValue()==ianaId.intValue() && devChange.getSn().equalsIgnoreCase(sn))
				{
					matchCount++;
					
					switch (devChange.getStatus()) {
						case "pending": 
							{
								if (isSuccess)
								{
									devChange.setStatus(DevicePendingChanges.STATUS.completed.toString());
								}
								else
								{
									devChange.setRetry(devChange.getRetry()+1);
								}								
							}
							break;
						case "completed":
						case "reject":
						case "cancel":
							log.warnf("%s status is found for update! %d", devChange.getStatus(), devChange.getId());
							break;
						default:
							log.warnf("unknown status is found for update! %d", devChange.getId());
							break;
					}					
					
					devChangeDAO.update(devChange);
				}
			}
			
			if (matchCount>1)
				log.warnf("More than 1 record has been updated for dev %d %s sid %s", ianaId, sn, sid);
			
		} catch (Exception e) {
			log.error("Exception ", e);
		}
	}
	
	/* init queue from db only if cache is empty */
	public static void loadToQueue(ACTION action) {
		log.warn("initDevicePendingChangeObjects start");

		DevicePendingChangesDAO devChangeDAO = null;
		DevicePendingChangesObject devChangeObject = null;		
		List<DevicePendingChanges> devChangeLst = null;
		List<Integer> qIdLst = null;

		try {
			Cluster.beginTransaction(Cluster.CACHENAME.LittleCloudCache);
			
			/* check if cache exists */
			devChangeObject = DevicePengingChangesUtils.getObject();
			if (devChangeObject == null) {
				devChangeObject = new DevicePendingChangesObject();
				try {				
					DevicePengingChangesUtils.putIfAbsentObject(devChangeObject);
					log.warn("INFO - Init new pending object and load all pending tasks!");
					action = ACTION.all;
				} catch (Exception e) {
					log.warn("INFO - Init new pending object failure!");
				}				
			} else {
				if (action == ACTION.all)
				{
					log.warn("INFO - pending object exists, no need to load all!");
					return;
				}
			}

			/* can only lock if key exists */
			Cluster.lock(Cluster.CACHENAME.LittleCloudCache, devChangeObject.getKey());
			devChangeObject = DevicePengingChangesUtils.getObject();
			if (devChangeObject == null) {
				log.error("Cache cleared??");
				return;
			}
			
			qIdLst = devChangeObject.getQueuedIdLst();
			devChangeDAO = new DevicePendingChangesDAO();
			
			/* load from db */
			switch (action)
			{
				case all:
				{					
					devChangeLst = devChangeDAO.getAllPendingChanges();					
				}break;
				case retry:
				{
					if (!ACUtil.isNeedRefresh(devChangeObject))
					{
						/* avoid concurrent retry */
						return;
					}
						
					devChangeLst = devChangeDAO.getRetryPendingChanges();
				}break;
				default:
				{
					log.errorf("unknown action %s!!!!",  action);
				}break;
			}
			
			if (devChangeLst == null || devChangeLst.size() == 0)
			{
				log.infof("No pending change!");
				return;
			}
			
			/* put to cache */
			for (DevicePendingChanges item : devChangeLst) {
				if (action==ACTION.retry || !qIdLst.contains(item.getId().intValue()))
				{
					/* no need to check duplicate for retry */
					if (!qIdLst.contains(item.getId().intValue()))
						qIdLst.add(item.getId());
					
					fm.addItem(item);
				}
				else
				{
					log.warnf("item %d already existed!", item);
				}
			}
			DevicePengingChangesUtils.replaceObject(devChangeObject);

		} catch (Exception e) {
			try {
				Cluster.rollbackTransaction(Cluster.CACHENAME.LittleCloudCache);
			} catch (Exception e1) {
				log.error("fail to rollback cache transaction");
			}
		} finally {
			try {
				Cluster.commitTransaction(Cluster.CACHENAME.LittleCloudCache);
			} catch (Exception e) {
				log.error("fail to commit cache transaction");
			}
		}

		log.warn("initDevicePendingChangeObjects ended.");
	}

}
