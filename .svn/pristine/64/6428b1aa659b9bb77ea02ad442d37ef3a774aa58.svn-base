/*
 * To Do
 * 
 * ./var/run/ilink/ic_mg
 * 
 * 
 * Handle config file cleaning
 * 
 * When apply new config, lookup ConfigCache for latest version
 * (1) If ConfigCache is about to expire, dont use, remove it and send delete file cmd. Then get a new config.
 *  
 * (2) Mark last-use config path to db
 * 		a) clean up the last-use file path before apply new config
 * 		b) schedule job to clean the last-use file path if no cache entry and conf_update count is zero.  
 * 
 */

package com.littlecloud.control.json.model.config.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jboss.logging.Logger;

import com.littlecloud.ac.DebugManager;
import com.littlecloud.ac.health.ThreadPoolAdapterInfo;
import com.littlecloud.ac.health.ThreadPoolManager;
import com.littlecloud.ac.health.ThreadPoolManager.ServiceType;
import com.littlecloud.control.dao.DeviceUpdatesDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.branch.Products;
import com.littlecloud.control.json.JsonResponse.ResponseCode;
import com.littlecloud.control.json.model.config.JsonConf;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings.CONFIG;
import com.littlecloud.control.json.model.config.util.info.ConfigPutTaskInfo;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.PoolObjectDAO;
import com.littlecloud.pool.object.utils.ProductUtils;
import com.peplink.api.db.util.DBUtil;

public class ConfigUpdatePerNetworkTask implements Callable<String> {

	private static final Logger log = Logger.getLogger(ConfigUpdatePerNetworkTask.class);

	//private static ConcurrentHashMap<String, String> execMap = new ConcurrentHashMap<String, String>();
	
	private static int configUpdatePerNetworkTaskRunCount = 0;

	private static boolean PROD_MODE = DebugManager.isPROD_MODE();	
	public static Integer configDevId = null;
	private static boolean isStarted = false;
	public static ConcurrentHashMap<Long, String> threadPoolInfoMap = new ConcurrentHashMap<Long, String>();

	public static enum UPDATE_OPERATION {
		APPLY_GROUP_CONFIG,
		APPLY_GROUP_MASTER_DEVICE_CONFIG,
		CLEAR_CONFIG
	}

	private ExecutorService configPutExecutor = null;
	private String sid;
	private String orgId;
	private Integer netId;
	private JsonConf_RadioSettings.CONFIG filter;

	public static int getConfigUpdatePerNetworkTaskRunCount() {
		return configUpdatePerNetworkTaskRunCount;
	}

	public synchronized static void setRunCount(boolean bAdd) {

		if (bAdd)
		{
			configUpdatePerNetworkTaskRunCount++;
			log.debugf("added counter=%d", configUpdatePerNetworkTaskRunCount);
		}
		else
		{
			configUpdatePerNetworkTaskRunCount--;
			log.debugf("minus counter=%d", configUpdatePerNetworkTaskRunCount);
		}
	}

	public ConfigUpdatePerNetworkTask(ExecutorService configPutExecutor, String sid, String orgId, Integer netId, CONFIG filter) {
		super();
		this.sid = sid;
		this.orgId = orgId;
		this.netId = netId;
		this.filter = filter;
		this.configPutExecutor = configPutExecutor;
	}

	public ConfigUpdatePerNetworkTask() {
		// for unit test
	}

//	public ExecutorService getExecutor() {
//		return configPutExecutor;
//	}
//
//	public void stopService() {
//		try {
//			if (isStarted && !configPutExecutor.isShutdown())
//				configPutExecutor.shutdown();//awaitTermination(1, TimeUnit.NANOSECONDS);
//			isStarted = false;
//		} catch (Exception e) {
//			log.error("ConfigUpdatePerDeviceTask.stopService by ThreadPoolManager %s",e);
//			e.printStackTrace();
//		}
//	}
//	public static String getMessageInfo() {
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(" configUpdatePerNetworkTaskRunCount: ");
//		sb.append(configUpdatePerNetworkTaskRunCount);
//		sb.append("<br>");		
//		
//		sb.append(" starttime: ");
//		sb.append(DateUtils.getUtcDate());
//		sb.append("<br>");		
//		/* Add more info if needed*/
//
//		return sb.toString();
//	}
//	
//	public ThreadPoolAdapterInfo getThreadPoolAdapterInfo(){
//		ThreadPoolAdapterInfo threadPoolAdapterInfo = new ThreadPoolAdapterInfo();
//		threadPoolAdapterInfo.setType(ThreadPoolManager.ExecutorType.ExecutorService);
//		threadPoolAdapterInfo.setName(ServiceType.ConfigUpdatePerDeviceTask);
//		threadPoolAdapterInfo.setExecutor_service(getExecutor());
//		if (isStarted)
//			threadPoolAdapterInfo.setStatus("Running");
//		else
//			threadPoolAdapterInfo.setStatus("Shutdown");
//		
//		threadPoolAdapterInfo.setThreadExecInfo(getMessageInfo());
//		threadPoolAdapterInfo.setThreadPoolInfoMap(threadPoolInfoMap);
//		return threadPoolAdapterInfo;
//	}
	
	@Override
	public String call() {
		setRunCount(true);
		
		log.infof("MemTrace - runCount = %d", getConfigUpdatePerNetworkTaskRunCount());		
		
		DBUtil dbUtil = null;
		try {
			dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
			
			if (performConfigUpdateNetwork(this.sid, this.orgId, this.netId, this.filter) == true)
			{
				setRunCount(false);
				return ResponseCode.SUCCESS.toString();
			}
			else
			{
				setRunCount(false);
				return ResponseCode.INTERNAL_ERROR.toString();
			}
		} catch (Exception e) {
			log.error("Exception on performConfigUpdateNetwork " + this.sid + "," + this.orgId + "," + this.netId + "," + this.filter, e);
			return ResponseCode.INTERNAL_ERROR.toString();
		}
		finally 
		{
			try{
				if (dbUtil != null && dbUtil.isSessionStarted()) {
					dbUtil.endSession();
				}
			} catch (Exception e2) {
				log.error("Exception in endSession", e2);
			}
		}
	}

	public boolean performConfigUpdateNetwork(String sid, String orgId, Integer netId, CONFIG filter)
	{
		log.infof("performConfigUpdateNetwork is called for (sid, orgId, netId, filter) = (%s, %s, %d, %s)", sid, orgId, netId, filter);
		
		final boolean rdonly = false;

		/* perform config update */
		List<Devices> fetchDevLst;
		List<Integer> updatingDevIdLst;
		//Properties newConfig;
		try {
			/* GROUP CONFIG APPLICATION */
			NetworksDAO networkDAO = new NetworksDAO(orgId, rdonly);
			DevicesDAO deviceDAO = new DevicesDAO(orgId, rdonly);
			DeviceUpdatesDAO devUpDAO = new DeviceUpdatesDAO(orgId, rdonly);
			
			log.debugf("resend config for CONFIG_PUT no ack for network %d", netId);
			devUpDAO.incrementNoAckConfRetryForNetwork(netId);
			
			log.debugf("getAllUpdateDevicesPerNetwork %d", netId);
			fetchDevLst = deviceDAO.getAllRetryUpdateDevicesPerNetwork(netId, null);			
			updatingDevIdLst = deviceDAO.getAllImmediateUpdatingDevicesIdPerNetwork(netId, null);
			if (updatingDevIdLst==null)
				updatingDevIdLst = new ArrayList<Integer>();

			/* no update is required */
			if (PROD_MODE)
			{
				if (fetchDevLst == null || fetchDevLst.size() == 0)
				{
					log.debugf("no fetch is required");
					return true;
				}
				else
				{
					log.debugf("fetchDevLst.size=%d", fetchDevLst);
				}
			}
			
			if (!PROD_MODE)
			{			
				if (configDevId == null)
					fetchDevLst = deviceDAO.getAllRetryUpdateDevicesPerNetwork(netId, null);
				else
				{
					log.debugf("get dev %d", configDevId);
					fetchDevLst = new ArrayList<Devices>();
					Devices dev = deviceDAO.findById(configDevId);
					fetchDevLst.add(dev);
				}
			}

			/* generate global network config */
			Networks net = networkDAO.findById(netId);
			if (net == null)
			{
				log.errorf("network %d does not exist", netId);
				return false;
			}

			String sidRelate = sid;
			int count = 0;
			for (Devices dev : fetchDevLst) {
				count++;
				log.debugf("fetchDevLst[x]=%s", dev.getSn(), fetchDevLst.size());
				
				if (updatingDevIdLst.contains(dev.getId().intValue()))
				{
					log.debugf("dev %d %s is updating, skip schedule update", dev.getIanaId(), dev.getSn());
					continue;
				}

				DevOnlineObject devOnline = PoolObjectDAO.getDevOnlineObject(dev);
				if ((!PROD_MODE && dev.getFirstAppear() != null) || devOnline != null && devOnline.isOnline())
				{					
					/* check if group master device is set */

					/* group master config */
					if (net.getMasterDeviceId() != null && net.getMasterDeviceId() != 0)
					{
						Future<Boolean> future = configPutExecutor.submit(new ConfigPutTask(new ConfigPutTaskInfo(orgId, netId, dev.getId(), sidRelate, dev.getIanaId(), dev.getSn(),
								UPDATE_OPERATION.APPLY_GROUP_MASTER_DEVICE_CONFIG)));
						try {
							if (future != null)
								future.get(25, TimeUnit.SECONDS);
						} catch (InterruptedException | ExecutionException | TimeoutException e) {
							log.error("MemTrace - exception ", e);
							future.cancel(true);
						}
					}
					else
					{
						/* skip blacklist */
						if (dev.getFirstAppear()==null)
						{
							log.debugf("orgId %s dev %d %s has never online (APPLY_GROUP_CONFIG)", orgId, dev.getIanaId(), dev.getSn());
							continue;
						}
						else if (dev.getFw_ver()==null)
						{
							log.warnf("orgId %s dev %d %s has fw_ver null (APPLY_GROUP_CONFIG)", orgId, dev.getIanaId(), dev.getSn());
							continue;
						}
						else if (RadioConfigUtils.isBlacklistFirmware(dev.getFw_ver()))
						{
							log.warnf("orgId %s dev %d %s fw_ver %s is blacklisted. skipped config application (APPLY_GROUP_CONFIG).", orgId, dev.getIanaId(), dev.getSn(), dev.getFw_ver());
							continue;
						}
						
						/* normal config */
						JsonConf_RadioSettings radioJson = null;
						ConcurrentHashMap<Integer, String> configMap = null;
						//radioJson = RadioConfigUtils.getDatabaseRadioFullConfig(orgId, netId, dev.getId(), true);
						if (log.isDebugEnabled())
							log.debugf("performConfigUpdateNetwork - radioJson=%s", JsonUtils.toJson(radioJson));

						log.debugf("memTrace - devOnline=%s new ConfigPutTask is called", devOnline);
						/* generate individual config (pepvpn...) and fetch */
						log.infof("sidRelate=%s", sidRelate);
						sidRelate = sidRelate + String.valueOf(count);
						Future<Boolean> future = configPutExecutor.submit(new ConfigPutTask(new ConfigPutTaskInfo(orgId, netId, dev.getId(), sidRelate, dev.getIanaId(), dev.getSn(),
								UPDATE_OPERATION.APPLY_GROUP_CONFIG)));
						try {
							if (future != null)
								future.get(25, TimeUnit.SECONDS);
						} catch (InterruptedException | ExecutionException | TimeoutException e) {
							log.error("MemTrace - exception orgId "+orgId+" "+netId+" "+dev.getId()+" "+dev.getIanaId()+" "+dev.getSn(), e);
							future.cancel(true);
						}
					}
				}
				else
				{
					log.debugf("memTrace - devOnline=%s dev is offline", devOnline);
				}
			}

			/* NO-GROUP CONFIG APPLICATION - CLEAR CONFIG */
			/*List<Devices> clearDevLst = deviceDAO.getRemovedDevicesSnLstForClearConfig();
			for (Devices dev : clearDevLst) {
				log.debugf("clearDevLst[x]=%s [%d]", dev.getSn(), clearDevLst.size());
				
				 skip blacklist 
				if (dev.getFirstAppear()==null)
				{
					String logMsg = String.format("orgId %s dev %d %s has never online (CLEAR_CONFIG)", orgId, dev.getIanaId(), dev.getSn());
					log.debugf(logMsg);
					confUpDAO.decrementConfUpdateForDev(dev.getId(), logMsg, null, true);
					continue;
				}
				else if (dev.getFw_ver()==null)
				{
					String logMsg = String.format("orgId %s dev %d %s has fw_ver null (CLEAR_CONFIG)", orgId, dev.getIanaId(), dev.getSn());
					log.warnf(logMsg);
					confUpDAO.decrementConfUpdateForDev(dev.getId(), logMsg, null, true);
					continue;
				}
				else if (ConfigUtils.isBlacklistFirmware(dev.getFw_ver()))
				{
					String logMsg = String.format("orgId %s dev %d %s fw_ver %s is blacklisted. skipped config application (CLEAR_CONFIG).", orgId, dev.getIanaId(), dev.getSn(), dev.getFw_ver());
					log.warn(logMsg);
					 only disable config update for CLEAR_CONFIG! 
					confUpDAO.decrementConfUpdateForDev(dev.getId(), logMsg, null, false);
					continue;
				}

				DevOnlineObject devOnline = PoolObjectDAO.getDevOnlineObject(dev.getIanaId(), dev.getSn());
				if ((!PROD_MODE) || devOnline != null && devOnline.isOnline()) {
					
					 get device config type 
					Products product = ProductUtils.getProducts(dev.getProductId());
					JsonConf.CONFIG_TYPE configType = ConfigUtils.getConfigTypeFromProducts(product);
					if (configType == JsonConf.CONFIG_TYPE.UNKNOWN)
					{
						log.errorf("dev %d %s find unknown configType %s", dev.getIanaId(), dev.getSn(), configType);
						continue;
					}

					log.debugf("memTrace - devOnline=%s new ConfigPutTask is called (2)", devOnline);
					sidRelate = JsonUtils.relateServerRef(sidRelate);
					Future<Boolean> future = configPutExecutor.submit(new ConfigPutTask(orgId, null, dev.getId(), sidRelate, dev.getIanaId(), dev.getSn(), null, configType, UPDATE_OPERATION.CLEAR_CONFIG));
					try {
						if (future != null)
							future.get(20, TimeUnit.SECONDS);
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						log.error("MemTrace - exception (2)", e);
						future.cancel(true);
					}
				}
				else
				{
					log.debugf("memTrace - devOnline=%s dev is offline (2)", devOnline);
				}
			}*/
		} catch (Exception e) {
			log.errorf("performConfigUpdateNetwork exception for sid %s org %s netId %d", sid, orgId, netId, e);
			log.error(e,e);
			return false;
		}
		return true;
	}
}