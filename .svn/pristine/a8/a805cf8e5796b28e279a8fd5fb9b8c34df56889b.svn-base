package com.littlecloud.pool.object.utils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.json.JSONObject;

import org.apache.commons.beanutils.PropertyUtils;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.WtpMsgHandler;
import com.littlecloud.ac.ACService.OP_MODE;
import com.littlecloud.ac.handler.OnlineStatusAndCustomEventHandler;
import com.littlecloud.ac.handler.OnlineStatusAndCustomEventHandler.UPDATE_REASON;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Devices.ONLINE_STATUS;
import com.littlecloud.control.entity.branch.SnsOrganizations;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.util.NetworkUtils;
import com.littlecloud.pool.object.DevDetailObject;
import com.littlecloud.pool.object.DevInfoObject;
import com.littlecloud.pool.object.DevOfflineObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.DevicesObject;
import com.littlecloud.pool.object.DevicesTrimObject;
import com.littlecloud.pool.object.NetInfoObject;

public class DeviceUtils {

	private static Logger log = Logger.getLogger(DeviceUtils.class);
	
	private static final String DEV_LEVEL_CFG_WIFI="WLAN";
	private static final String DEV_LEVEL_CFG_MVPN="MVPN";
	
	private static final String DATA_TAG = WtpMsgHandler.DATA_TAG;
	
	public static void OnlineStatusHandler(DevOnlineObject devO, ONLINE_STATUS originStatus, ONLINE_STATUS finalStatus)
	{
		if (devO == null)
			return;
		
		if (devO.getOrganization_id()!=null && !devO.getOrganization_id().isEmpty())
		{
			try {
				DevicesDAO devDAO = new DevicesDAO(devO.getOrganization_id());
				Devices dev = devDAO.findById(devO.getDevice_id());
				dev.setOnline_status(ONLINE_STATUS.OFFLINE.getDbValue());
				devDAO.update(dev);
			} catch (SQLException e) {
				log.error("SQLException ", e);					
			}				
		}
	}
	
	public static boolean WarrantyExpiredHandler(DevOnlineObject devO)
	{	
		String orgId = null;
		Integer devId = null;
		String logMsg = null;
		
		/* remove online object from cache and kick device */
		if (devO == null)
			return true;
		
		if (devO.getOrganization_id()==null || devO.getDevice_id()==null)
		{
			log.warnf("devO has incomplete info %s", devO);
			return false;
		}
		
		orgId = devO.getOrganization_id();
		devId = devO.getDevice_id();
		
		
		try {
			devO.setOnline(false);
			
			if (OnlineStatusAndCustomEventHandler.updateOnlineStatusAndHistoryEventLog(orgId, devId,
					devO.getStatus(), ONLINE_STATUS.OFFLINE, DateUtils.getUtcDate(), UPDATE_REASON.warrantyexpired)) {
				logMsg = String.format("LT10001 INFO - dev %d %s has warranty expired and set offline ", devO.getIana_id(), devO.getSn());							
				DeviceUtils.logDevInfoObject(devO.getIana_id(), devO.getSn(), log, Level.WARN, logMsg);
			} else {
				logMsg = String.format("LT10001 - dev %d %s fail to be marked offline", devO.getIana_id(), devO.getSn());
				DeviceUtils.logDevInfoObject(devO.getIana_id(), devO.getSn(), log, Level.WARN, logMsg);
			}
						
			//ACUtil.<DevOnlineObject> removePoolObjectBySn(devO, DevOnlineObject.class);			
			ACService.set_dev_op_mode(JsonUtils.genServerRef(), devO.getIana_id(), devO.getSn(), OP_MODE.OPMODE_NOREPORT);
		} catch (Exception e) {
			log.error("WarrantyExpiredHandler exception "+e, e);
			return false;
		}
		return ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_DISCONNECT, devO.getSid(), devO.getIana_id(), devO.getSn());
	}
	
	public static DevDetailObject getDevDetailObject(Devices dev)
	{
		/* seek cache, else seek device */
		DevDetailObject devDetailObjectExample = new DevDetailObject();
		devDetailObjectExample.setIana_id(dev.getIanaId());
		devDetailObjectExample.setSn(dev.getSn());

		try {
			return ACUtil.<DevDetailObject> getPoolObjectBySn(devDetailObjectExample, DevDetailObject.class);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("getPoolObjectBySn exception "+e, e);
			return null;
		}
	}
	
	public static boolean putOnlineObject(DevOnlineObject devO)
	{
		try {
			ACUtil.<DevOnlineObject> cachePoolObjectBySn(devO, DevOnlineObject.class);
			return true;
		} catch (Exception e) {
			log.errorf("putOnlineObject exception %s", e.getMessage());
			return false;
		}
	}
	
	public static DevOnlineObject getDevOnlineObject(Devices dev)
	{
		/* seek cache, else seek device */
		DevOnlineObject devOnlineObjectExample = new DevOnlineObject();
		devOnlineObjectExample.setIana_id(dev.getIanaId());
		devOnlineObjectExample.setSn(dev.getSn());

		try {
			return ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineObjectExample, DevOnlineObject.class);
		} catch (Exception e) {
			log.error("getDevOnlineObject getPoolObjectBySn exception "+e, e);
			return null;
		}
	}
	
	public static boolean putDevInfoObject(DevInfoObject devInfo)
	{
		if (devInfo==null)
		{
			log.warn("trying to put devInfo is null!");
			return false;	
		}			
		
		try {
			ACUtil.<DevInfoObject> cachePoolObjectBySn(devInfo, DevInfoObject.class);
			return true;
		} catch (Exception e) {
			log.errorf("putDevInfoObject exception %s", e.getMessage());
			return false;
		}
	}
	
	public static DevInfoObject getDevInfoObject(Integer ianaId, String sn)
	{
		/* seek cache, else seek device */
		DevInfoObject devInfoObjectExample = new DevInfoObject(sn, ianaId);

		try {
			return ACUtil.<DevInfoObject> getPoolObjectBySn(devInfoObjectExample, DevInfoObject.class);
		} catch (Exception e) {
			log.error("getDevInfoObject getPoolObjectBySn exception "+e, e);
			return null;
		}
	}
	
	public static DevInfoObject createDevInfoObjectIfNotExists(Integer ianaId, String sn)
	{
		DevInfoObject devInfo = new DevInfoObject(sn, ianaId);
		
		/* seek cache, else seek device */
		DevInfoObject devInfoObjectExample = new DevInfoObject(sn, ianaId);

		try {
			devInfo = ACUtil.<DevInfoObject> getPoolObjectBySn(devInfoObjectExample, DevInfoObject.class);
			if (devInfo==null)
			{
				devInfo = new DevInfoObject(sn, ianaId);
				devInfo.setMissingOnlineObjectCount(0);
			}
		} catch (Exception e) {
			log.error("getDevInfoObject getPoolObjectBySn exception "+e, e);
			return devInfo;
		}
		return devInfo;
	}
	
	public static void logDevInfoObject(Integer ianaId, String sn, Logger log, Level logLvl, String logMsg)
	{
		log.log(logLvl, logMsg);
		DevInfoObject devInfo = DeviceUtils.createDevInfoObjectIfNotExists(ianaId, sn);
		devInfo.addDebugInfo(logMsg);
		DeviceUtils.putDevInfoObject(devInfo);
	}
	
	public static void logDevInfoObject(DevInfoObject devInfo, Logger log, Level logLvl, String logMsg)
	{
		if (devInfo==null || devInfo.getIana_id() == null || devInfo.getSn()==null)
		{
			log.errorf("devInfo is invalid %s", devInfo);
			return;
		}
		
		log.log(logLvl, logMsg);		
		devInfo.addDebugInfo(logMsg);
		DeviceUtils.putDevInfoObject(devInfo);
	}
	
	public static DevOnlineObject getDevOnlineObject(Integer ianaId, String sn)
	{
		/* seek cache, else seek device */
		DevOnlineObject devOnlineObjectExample = new DevOnlineObject();
		devOnlineObjectExample.setIana_id(ianaId);
		devOnlineObjectExample.setSn(sn);

		try {
			return ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineObjectExample, DevOnlineObject.class);
		} catch (Exception e) {
			log.error("getPoolObjectBySn exception "+e, e);
			return null;
		}
	}
	
	public static DevOfflineObject getDevOfflineObjectFromQueryInfo(QueryInfo<Object> info)
	{
		JSONObject object = null;
		JSONObject data = null;
		
		object = JSONObject.fromObject(info);
		data = object.getJSONObject(DATA_TAG);
		DevOfflineObject devO = JsonUtils.<DevOfflineObject>fromJson(data.toString(), DevOfflineObject.class);
		return devO;
	}
	
	public static DevOnlineObject getDevOnlineObjectFromQueryInfo(QueryInfo<Object> info)
	{
		JSONObject object = null;
		JSONObject data = null;
		
		object = JSONObject.fromObject(info);
		data = object.getJSONObject(DATA_TAG);
		DevOnlineObject devO = JsonUtils.<DevOnlineObject>fromJson(data.toString(), DevOnlineObject.class);
		return devO;
	}
	
	/* set online object online status */
	public static boolean setDevOnlineObjectOnlineOrOfflineFromStatus(DevOnlineObject devO, ONLINE_STATUS newStatus)
	{
		devO.setStatus(newStatus);
		switch (newStatus)
		{
		case ONLINE:
			devO.setOnline(true);			
			break;
		case NEVER_ONLINE:
		case WARRANTY_EXPIRED:
		case OFFLINE:
			devO.setOnline(false);
			break;
		case UNKNOWN:
			log.error("Should not set unknown state!");
			break;
		default:
			log.error("Should not set default state!");
			break;
		}
				
		return true;		
	}
	
	public static ONLINE_STATUS getDevOnlineStatus(Devices dev) 
	{
		return ONLINE_STATUS.statusOf(dev.getOnline_status());
	}
	
	public static String getOrgId(Integer ianaId, String sn)  
	{
		SnsOrganizations snsOrg = null;
		
		try {
			snsOrg = BranchUtils.getSnsOrganizationsByIanaIdSn(ianaId, sn);
			log.debugf("AAA snsOrg=%s", snsOrg);
			if (snsOrg != null)
			{
				return snsOrg.getOrganizationId();
			}
			else
			{
				if (BranchUtils.isLoaded())
					log.debugf("dev %d %s cannot find orgId", ianaId, sn);
				return null;
			}
		} catch (Exception e) {
			log.error("Exception BranchUtils.getSnsOrganizationsByIanaIdSn " + e, e);
		}
		
		return null;
	}
	
	public static List<Devices> getDevLst(String orgId, int netId) {
		List<Devices> devLst = null; 
		
		NetInfoObject netInfo = NetUtils.getNetInfoObject(orgId, netId);
		if (netInfo!=null && netInfo.isLoaded())
		{
			devLst = netInfo.getDevicesLst(orgId);					
		}
		else
		{
			log.errorf("netInfo for %s %d is empty from cache!!", orgId, netId);
			devLst = new ArrayList<Devices>();
		}
		
		return devLst;
	}
	
	public static List<Integer> getDevIdLstFromDevLst(List<Devices> devLst)
	{
		List<Integer> devIdLst = new ArrayList<Integer>();
		
		for(Devices dev:devLst)
		{
			devIdLst.add(dev.getId());
		}
		
		return devIdLst;
	}

	@Deprecated
	public static boolean isDevLevelWifiEnabled(Devices dev)
	{
		String dev_level_cfg = dev.getDev_level_cfg();
		String[] cfg = dev_level_cfg.split("\\|");
		for (String c:cfg)
		{
			log.debugf("matching %s %s", c.trim(), DEV_LEVEL_CFG_WIFI);
			if (c.trim().compareToIgnoreCase(DEV_LEVEL_CFG_WIFI)==0)
				return true;
		}
		return false;
	}
	
	@Deprecated
	public static boolean isDevLevelMvpnEnabled(Devices dev)
	{
		String dev_level_cfg = dev.getDev_level_cfg();
		String[] cfg = dev_level_cfg.split("\\|");
		for (String c:cfg)
		{
			if (c.trim().compareToIgnoreCase(DEV_LEVEL_CFG_MVPN)==0)
				return true;
		}
		return false;
	}
	
	public static String getDevLevelCfg(boolean wifi, boolean mvpn)
	{
		if (wifi == false && mvpn == false)
			return "";
		
		StringBuilder sb = new StringBuilder();
		if (wifi)
			sb.append(DEV_LEVEL_CFG_WIFI+"|");
		if (mvpn)
			sb.append(DEV_LEVEL_CFG_MVPN+"|");
		
		return sb.toString().substring(0, sb.toString().length()-1);
	}
	
	public static List<Devices> getDevicesListByTagsIdList(List<Integer> tagsIdList,Integer networksId, String organizationId){
		List<Devices> devicesList = null;
		try{
			if (tagsIdList != null){
				Set<Integer> deviceIdSet = new HashSet<Integer>();
				List<Integer> deviceIdList = new ArrayList<Integer>();
				for (Integer tagsId: tagsIdList){
					List<Integer> deviceIdTmpList = DeviceTagsUtils.getDeviceIdListByTagsId(tagsId, organizationId);
					if (deviceIdTmpList != null){
						for (Integer deviceIdTmp: deviceIdTmpList){
							if (deviceIdSet.add(deviceIdTmp)){
								deviceIdList.add(deviceIdTmp);
							} // end f (deviceIdSet.add(deviceIdTmp))
						} // end for (Integer deviceIdTmp: deviceIdTmpList)
					} // end if (deviceIdTmpList != null)
				} // end for (Integer tagsId: tagsIdList)
				
				if (deviceIdList.size() > 0){
					devicesList = new ArrayList<Devices>();
					for (Integer deviceId: deviceIdList){
						Devices devices = NetUtils.getDevices(organizationId, networksId, deviceId, true);
						if (devices != null){
							devicesList.add(devices);
						} // end if (devices != null)
					} // end for (Integer deviceId: deviceIdList)
				} // end if (deviceIdList.size() > 0)
				
			} // end if (tagsIdList != null)
		} catch (Exception e){
			log.error("DEV20140226 - getDevicesListByTagsIdList", e);
		} // end try ... catch ...
		return devicesList;
	} // end getDevicesListByTagsIdList
	
	public static void main(String args[])
	{
		log.debugf(getDevLevelCfg(true, true));
		
		Devices dev = new Devices();
		dev.setDev_level_cfg("WLAN");
		log.debugf("isDevLevelWifiEnabled = %s\n", isDevLevelWifiEnabled(dev));
		
		dev.setDev_level_cfg("WLAN|MVPN");
		log.debugf("isDevLevelWifiEnabled = %s\n", isDevLevelWifiEnabled(dev));		
		
		dev.setDev_level_cfg("MVPN");
		log.debugf("isDevLevelWifiEnabled = %s\n", isDevLevelWifiEnabled(dev));
	}
}
