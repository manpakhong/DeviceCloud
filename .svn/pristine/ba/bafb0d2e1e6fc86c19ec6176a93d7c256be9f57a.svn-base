package com.littlecloud.control.firmware;

import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.DebugManager;
import com.littlecloud.ac.json.model.Json_FirmwareUpgrade;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.branch.DeviceFirmwareSchedulesDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.branch.DeviceFirmwareSchedules;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.littlecloud.pool.object.utils.NetUtils;

public class FirmwareUpgradeUtils {
	
	private static final Logger log = Logger.getLogger(FirmwareUpgradeUtils.class);
	public static final int maxBatchSize = 100;	/* k request */
	private static boolean PROD_MODE = DebugManager.isPROD_MODE();
	
	private static final Level logINFO = Level.INFO;
	private static final Level logDEBUG = Level.DEBUG;
	
	/*
	 * Refer to CR
	 *  status: 0 - waiting, 1 - completed, 2 - cancelled (reserved), 3 - upgrade in progress, 8 - same firmware version, 9 - max retry has reached, 99 - data inconsistency
	 */

	/* Do firmware upgrade for list of schedules */
	public static boolean performFirmwareUpgradeScheduleLst(List<DeviceFirmwareSchedules> dfsLst)
	{
		final boolean bReadOnlyDb = true;
		final String callerRef = "performFirmwareUpgradeDevice_";

		int totalCount = 0;
		int upgradeCount = 0;
		int offlineCount = 0;
		int fwVerMatchCount = 0;		
		int productIdNotMatchCount = 0;
		int exceptionCount = 0;		
		
		try {
			DeviceFirmwareSchedulesDAO dfsDAO = new DeviceFirmwareSchedulesDAO();
			DevicesDAO devDAO = null;
			
			//int batchCount = 0;
			for (DeviceFirmwareSchedules dfs : dfsLst)
			{
//				batchCount++;
//				if (batchCount > maxBatchSize)
//					break;
				
				log.infof("do schedule " + dfs + " for schedule_time = " + new Date(dfs.getSchedule_time() * 1000L));
				totalCount++;

				Devices dev = null;
				try {
					dev = NetUtils.getDevicesBySn(dfs.getOrganization_id(), dfs.getIana_id(), dfs.getSn(), true);
//					/*** until netutils has been revamped, below code is used instead **/
//					devDAO = new DevicesDAO(dfs.getOrganization_id());
//					dev = devDAO.findBySn(dfs.getIana_id(), dfs.getSn());
					if (dev != null)
					{
						if (dev.getNetworkId()!=dfs.getNetwork_id())
						{
							/* the existing schedule probably created before device has moved network */
							dev=null;
							log.warnf("dev %d %s the existing schedule probably created before device has moved network", dfs.getIana_id(), dfs.getSn());							
						}
					}
					else
					{
						log.warnf("dev %d %s is not found in NetUtils", dfs.getIana_id(), dfs.getSn());
					}
					/*****/
				} catch (Exception e)
				{
					log.error("Exception for dev "+dfs.getSn(), e);
					exceptionCount++;
					continue;
				}
				
				if (dev == null)
				{
					offlineCount++;
					log.warnf("Dev %s for net %d firmware schedule is stopped.", dfs.getSn(), dfs.getNetwork_id());
					dfs.setStatus(99);
					dfsDAO.update(dfs);
					continue;
				}

				DevOnlineObject devO = DeviceUtils.getDevOnlineObject(dev);
				if (PROD_MODE && (devO == null || !devO.isOnline()))
				{
					offlineCount++;
					log.logf(logDEBUG, "Dev %s is offline", dfs.getSn());
					continue;
				}

				if (dev.getProductId() == dfs.getProduct_id())
				{
					if (dev.getFw_ver() != null && dev.getFw_ver().compareToIgnoreCase(dfs.getFw_version()) != 0)
					{
						Json_FirmwareUpgrade fwJson = new Json_FirmwareUpgrade();
						fwJson.setFw_url(dfs.getFw_url());
						fwJson.setFw_ver(dfs.getFw_version());

						if (PROD_MODE)
						{							
							if( dfs.getTrial_round().intValue() <= DeviceFirmwareSchedulesDAO.maxRetry )
							{
								log.logf(logINFO, " fetch dev %s (%s) for firmware upgrade.", dev.getSn(), devO.getMachine_id());
								ACService.<Json_FirmwareUpgrade> fetchCommand(MessageType.PIPE_INFO_TYPE_FIRMWARE_PUT, callerRef + JsonUtils.genServerRef(), dev.getIanaId(), dev.getSn(), fwJson);								
							}
							else
							{
								log.logf(logINFO, " fetch dev %s (%s) max retry has reached!.", dev.getSn(), devO.getMachine_id());
							}
														
							dfs.setTrial_round(dfs.getTrial_round() + 1); /* add trial_count after apply */
							dfs.setUpgrade_time(DateUtils.getUnixtime());
							
							if( dfs.getTrial_round().intValue() >= DeviceFirmwareSchedulesDAO.maxRetry )
								dfs.setStatus(9);
							dfsDAO.update(dfs);
							
							upgradeCount++;
						}
						else
						{
							log.logf(logDEBUG, "dev %s should have upgraded in production!", dev.getSn());
						}
					}
					else
					{
						/* version matched, no op */
						log.logf(logINFO, " dev %s has fw version matched and no need to upgrade.", dev.getSn());
						fwVerMatchCount++;
						
						dfs.setUpgrade_time(DateUtils.getUnixtime());
						dfs.setStatus(8);
						dfsDAO.update(dfs);
					}
				}
				else
				{
					log.errorf("Dev %s productId (dev %s != schedue %s) not match ", dev.getSn(), dev.getProductId(), dfs.getProduct_id());
					productIdNotMatchCount++;
					continue;
				}
			}
		} catch (Exception e)
		{
			log.error("Exception ", e);
			return false;
		}

		log.logf(logINFO, " summary (fetch device upgrade:%d + offline:%d + ver_matched:%d + productIdNotMatch:%d + exceptionCount:%d / %d)", upgradeCount, offlineCount, fwVerMatchCount, productIdNotMatchCount, exceptionCount, totalCount);

		return bReadOnlyDb;
	}
}
