package com.littlecloud.control.webservices.util;

import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.Json_FirmwareUpgrade;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.PoolObjectDAO;

public class FirmwareUpgrade {

	private static final Logger log = Logger.getLogger(FirmwareUpgrade.class);

	/* schedule network upgrade */
	public static boolean performFirmwareUpgrade(Integer product_id, String org_id, Integer net_id, String fw_url, String fw_version)
	{
		final boolean bReadOnlyDb = true;
		final String callerRef = "performFirmwareUpgrade_";

		int totalCount = 0;
		int upgradeCount = 0;
		int offlineCount = 0;
		int verMatchCount = 0;

		Json_FirmwareUpgrade fwJson = new Json_FirmwareUpgrade();
		fwJson.setFw_url(fw_url);
		fwJson.setFw_ver(fw_version);

		/* loop all online device with status */
		//HibernateUtil hutil = new HibernateUtil(org_id, bReadOnlyDb);		

		try {
			//hutil.beginBranchTransaction();
			DevicesDAO deviceDAO = new DevicesDAO(org_id, bReadOnlyDb);

			List<Devices> devList = deviceDAO.getDevicesList(net_id);
			for (Devices dev : devList)
			{
				log.info("Upgrade device:"+dev.getName());
				if (dev.getProductId() == product_id)
				{
					totalCount++;
					if (dev.getFw_ver()!=null && dev.getFw_ver().compareToIgnoreCase(fw_version) != 0) 
					{	
						DevOnlineObject devOnline = PoolObjectDAO.getDevOnlineObject(dev);
						if (devOnline != null)
						{
							ACService.<Json_FirmwareUpgrade> fetchCommand(MessageType.PIPE_INFO_TYPE_FIRMWARE_PUT, callerRef + JsonUtils.genServerRef(), dev.getIanaId(), dev.getSn(), fwJson);
							upgradeCount++;
						}
						else
						{
							offlineCount++;
						}
					}
					else
						verMatchCount++;
				}
			}

			log.infof("summary (fetch upgrade:%d + offline:%d + ver_matched:%d / %d)", upgradeCount, offlineCount, verMatchCount, totalCount);
			//hutil.commitBranchTransaction();
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			//hutil.rollbackBranchTransaction();
			return false;
		}
	}
	
	/* immediate firmware upgrade */
	public static boolean performFirmwareUpgradeDevice(Integer product_id, String org_id, Integer dev_id, String fw_url, String fw_version)
	{
		final boolean bReadOnlyDb = true;
		final String callerRef = "performFirmwareUpgradeDevice_";

		int totalCount = 0;
		int upgradeCount = 0;
		int offlineCount = 0;
		int verMatchCount = 0;

		Json_FirmwareUpgrade fwJson = new Json_FirmwareUpgrade();
		fwJson.setFw_url(fw_url);
		fwJson.setFw_ver(fw_version);

		/* loop all online device with status */
		//HibernateUtil hutil = new HibernateUtil(org_id, bReadOnlyDb);		

		try {
			//hutil.beginBranchTransaction();
			DevicesDAO deviceDAO = new DevicesDAO(org_id, bReadOnlyDb);

			Devices dev = deviceDAO.findById(dev_id);
			
			if (dev!=null && dev.isActive() && dev.getProductId() == product_id)
			{
				totalCount++;
				if (dev.getFw_ver()!=null && dev.getFw_ver().compareToIgnoreCase(fw_version) != 0) 
				{
					log.debugf("dev fw ver %s | target fw ver %s ", dev.getFw_ver(), fw_version);
	
					DevOnlineObject devOnline = PoolObjectDAO.getDevOnlineObject(dev);
					if (devOnline != null)
					{
						ACService.<Json_FirmwareUpgrade> fetchCommand(MessageType.PIPE_INFO_TYPE_FIRMWARE_PUT, callerRef + JsonUtils.genServerRef(), dev.getIanaId(), dev.getSn(), fwJson);
						upgradeCount++;
					}
					else
					{
						offlineCount++;
					}
				}
				else
				{
					verMatchCount++;
				}
			}

			log.infof("summary (fetch device upgrade:%d + offline:%d + ver_matched:%d / %d)", upgradeCount, offlineCount, verMatchCount, totalCount);
			//hutil.commitBranchTransaction();
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			//hutil.rollbackBranchTransaction();
			return false;
		}
	}
	
	public static boolean versionMatch(String sn, Integer iana_id, String org_id, Integer net_id, String fw_version)
	{
		boolean isMatched = false;
		
		try
		{
			DevicesDAO devicesDAO = new DevicesDAO(org_id, true);
			Devices dev = devicesDAO.findBySnNet(iana_id,sn,net_id);
			if( dev.getFw_ver().equals(fw_version) )
				isMatched = true;
		}
		catch(Exception e)
		{
			log.error(e.getMessage(), e);
			//hutil.rollbackBranchTransaction();
			return false;
		}
		
		return isMatched;
	}
	
}
