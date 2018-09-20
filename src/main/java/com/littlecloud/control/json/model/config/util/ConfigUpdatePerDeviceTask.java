package com.littlecloud.control.json.model.config.util;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jboss.logging.Logger;

import com.littlecloud.ac.DebugManager;
import com.littlecloud.ac.health.ThreadPoolAdapterInfo;
import com.littlecloud.ac.health.ThreadPoolManager;
import com.littlecloud.ac.health.ThreadPoolManager.ServiceType;
import com.littlecloud.ac.health.ThreadPoolManager.ThreadMapMessage;
import com.littlecloud.ac.health.ThreadPoolMonitor.PoolDump;
import com.littlecloud.control.dao.DeviceUpdatesDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.dao.branch.ConfigUpdatesDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.branch.ConfigUpdatesId;
import com.littlecloud.control.entity.branch.Products;
import com.littlecloud.control.json.model.config.JsonConf;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerNetworkTask.UPDATE_OPERATION;
import com.littlecloud.control.json.model.config.util.info.ConfigPutTaskInfo;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.FeatureGetObject;
import com.littlecloud.pool.object.PoolObjectDAO;
import com.littlecloud.pool.object.utils.FeatureGetUtils;
import com.littlecloud.pool.object.utils.ProductUtils;

public class ConfigUpdatePerDeviceTask {

	private static final Logger log = Logger.getLogger(ConfigUpdatePerDeviceTask.class);
	
	private static boolean PROD_MODE = DebugManager.isPROD_MODE();
	public static enum CONFIG_UPDATE_REASON {
		dev_online,
		add_dev_hub_change, dev_move_network, dev_move_network_hub, dev_remove, dev_remove_update_hub, update_dev_info, 
		sync_master_config, unset_master_device,
		pepvpn_hub_update, pepvpn_endpt_update,
		put_radio_config, put_webadmin_config, dev_lvl_ssid_update, grp_lvl_ssid_update, dev_lvl_mgm_update, grp_lvl_mgm_update, 
		net_remove, net_remove_update_hub, add_dev_tags, remove_dev_tags, overwrite_config_backup_event, config_md5_event,
		
		/* failure retry */
		config_put_failure,
		
		/* others */
		update_network_info
	}
	private static boolean isStarted = false;
	private static int COUNT = 0;
	public static ConcurrentHashMap<Long, String> threadPoolInfoMap = new ConcurrentHashMap<Long, String>();

	private static final ThreadPoolExecutor configPutImmediateExecutor = new ThreadPoolExecutor(10, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());//Executors.newFixedThreadPool(10);
	
	private String orgId;
	private Integer netId;
	
	public String getOrgId() {
		return orgId;
	}
	
	public Integer getNetId() {
		return netId;
	}

	public ConfigUpdatePerDeviceTask(String orgId, Integer netId) {
		super();
		this.orgId = orgId;
		this.netId = netId;
		isStarted = true;
		COUNT ++;
		ThreadMapMessage mapMessage = new ThreadMapMessage();
		mapMessage.setThreadId(Thread.currentThread().getId());
		Calendar cal = Calendar.getInstance();
		Date d = cal.getTime();
		mapMessage.setDatetime(d);
		mapMessage.setMessage("ConfigUpdatePerDeviceTask orgId="+orgId+"netId"+netId);
		threadPoolInfoMap.put(Thread.currentThread().getId(), mapMessage.toString());
	}
	public static ThreadPoolExecutor getExecutor() {
		return configPutImmediateExecutor;
	}

	public static void stopService() {
		try {
			if (isStarted && !configPutImmediateExecutor.isShutdown())
				configPutImmediateExecutor.shutdown();//awaitTermination(1, TimeUnit.NANOSECONDS);
			isStarted = false;
		} catch (Exception e) {
			log.error("ConfigUpdatePerDeviceTask.stopService by ThreadPoolManager %s",e);
			e.printStackTrace();
		}
	}
	public static String getMessageInfo() {
		StringBuilder sb = new StringBuilder();
		ThreadPoolExecutor executor=getExecutor();
		if (executor != null) {
			PoolDump dump = new PoolDump();
			dump.setActive(executor.getActiveCount());
			dump.setComplete(executor.getCompletedTaskCount());
			dump.setCorepoolsize(executor.getCorePoolSize());
			dump.setKeepalive_time(executor.getKeepAliveTime(TimeUnit.SECONDS));
			dump.setLargest_poolsize(executor.getLargestPoolSize());
			dump.setMax_poolsize(executor.getMaximumPoolSize());
			dump.setPoolsize(executor.getPoolSize());
			dump.setQueuesize(executor.getQueue().size());
			dump.setTaskcount(executor.getTaskCount());
			sb.append(dump.toString());
		}
		
		sb.append(" COUNT: ");
		sb.append(COUNT);
		sb.append("<br>");		
			
		sb.append(" starttime: ");
		sb.append(DateUtils.getUtcDate());
		sb.append("<br>");
		
		
		/* Add more info if needed*/

		return sb.toString();
	}
	
	public static ThreadPoolAdapterInfo getThreadPoolAdapterInfo(){
		ThreadPoolAdapterInfo threadPoolAdapterInfo = new ThreadPoolAdapterInfo();
		threadPoolAdapterInfo.setType(ThreadPoolManager.ExecutorType.ThreadPoolExecutor);
		threadPoolAdapterInfo.setName(ServiceType.ConfigUpdatePerDeviceTask);
		threadPoolAdapterInfo.setThread_pool_executor(getExecutor());
		if (isStarted)
			threadPoolAdapterInfo.setStatus("Running");
		else
			threadPoolAdapterInfo.setStatus("Shutdown");
		
		threadPoolAdapterInfo.setThreadExecInfo(getMessageInfo());
		threadPoolAdapterInfo.setThreadPoolInfoMap(threadPoolInfoMap);
		return threadPoolAdapterInfo;
	}
	public boolean performConfigUpdateDeviceDelay(Integer devId, String sid, String reason) {
		/* mark update */
		log.debugf("performConfigUpdateDeviceDelay is called for (orgId, netId, devId, sid, reason) = (%s, %d, %d, %s, %s)", orgId, netId, devId, sid, reason);

		try {
			DeviceUpdatesDAO devUpDAO = new DeviceUpdatesDAO(orgId);
			devUpDAO.incrementConfUpdateForDevLstIfNoUpdate(Arrays.asList(devId), sid, reason);
		} catch (SQLException e) {
			log.error("SQLException - " + e, e);
			return false;
		}

		return true;
	}

	public void performConfigUpdateNowForNetwork(String sid, String reason) throws SQLException {
		DevicesDAO devDAO = new DevicesDAO(this.orgId);
		
		List<Integer> devIdLst = devDAO.getDevicesIdList(this.netId);
		performConfigUpdateNow(devIdLst, sid, reason);
	}
	
	public void performConfigUpdateNow(List<Integer> devIdLst, String sid, String reason) throws SQLException {
		DeviceUpdatesDAO duDAO = new DeviceUpdatesDAO(this.orgId);
		duDAO.incrementConfUpdateForDevLst(devIdLst, sid, reason);
		
		/* check if device has already updating */
		if (PROD_MODE)
		{
			for (Integer devId:devIdLst)
			{
				performConfigUpdateDevice(sid, reason, devId);
			}
		}
	}
	
	public void performConfigUpdateNowIfNoUpdate(List<Integer> devIdLst, String sid, String reason) throws SQLException {
		
		log.debugf("performConfigUpdateNowIfNoUpdate is called with devIdLst %s sid %s reason %s", devIdLst, sid, reason);
		
		DeviceUpdatesDAO duDAO = new DeviceUpdatesDAO(this.orgId);
		duDAO.incrementConfUpdateForDevLstIfNoUpdate(devIdLst, sid, reason);
		
		/* check if device has already updating */ 
		for (Integer devId:devIdLst)
		{
			performConfigUpdateDevice(sid, reason, devId);
		}			
	}

	public void performConfigClearNow(Devices dev, String sid, String reason) throws SQLException {
		
		log.debugf("performConfigClearNow is called with dev %s sid %s reason %s", dev, sid, reason);
		
		boolean isDevConfigTextSupport=false;
		
		FeatureGetObject fgo = FeatureGetUtils.getFeatureGetObject(dev.getIanaId(), dev.getSn());								
		if (fgo==null || fgo.getFeatures()==null)
		{
			log.warnf("FeatureGetObject is not found for dev %d %s", dev.getIanaId() ,dev.getSn());
			//fgo = FeatureGetUtils.loadFeatureGetObject(orgId, true, dev);
			fgo = FeatureGetUtils.getFeatureGetObject(dev.getIanaId(), dev.getSn());
			if (fgo==null)
			{
				log.warnf("FeatureGetObject is not loaded for dev %d %s, default isDevConfigTextSupport not supported (CLEAR_CONFIG)!", dev.getIanaId() ,dev.getSn());
			}
			else
			{
				isDevConfigTextSupport=FeatureGetUtils.isConfigTextSupported(fgo);
			}
		}					
		
		ConfigUpdatesDAO confUpDAO = new ConfigUpdatesDAO(false);		
		confUpDAO.incrementConfClearForDevLst(new ConfigUpdatesId(dev.getIanaId(), dev.getSn()), sid, reason, isDevConfigTextSupport);
		performConfigClearDevice(sid, reason, dev);		
	}
	
	/* perform immediate config application for device */
	private boolean performConfigUpdateDevice(String sid, String reason, Integer devId)
	{
		log.infof("performConfigUpdateDevice is called for (sid, reason, orgId, netId, devId) = (%s, %s, %s, %d, %d)", sid, reason, orgId, netId, devId);
		
		final boolean rdonly = false;

		/* perform config update */
		Devices dev = null;
		try {
			/* GROUP CONFIG APPLICATION */
			NetworksDAO networkDAO = new NetworksDAO(orgId, rdonly);
			DevicesDAO deviceDAO = new DevicesDAO(orgId, rdonly);
			DeviceUpdatesDAO devUpDAO = new DeviceUpdatesDAO(orgId, rdonly);
			
			Networks net = networkDAO.findById(netId);
			if (net == null)
			{
				log.errorf("network %d does not exist", netId);
				return false;
			}

			
			dev = deviceDAO.findById(devId);
			if (dev==null)
			{
				log.errorf("dev %d does not exist for orgId %s", devId, orgId);
				return false;
			}
			else if (dev.getNetworkId()!=netId)
			{
				log.errorf("dev %d netId given is not persistent for orgId %s! (%d, %d)", devId, orgId, dev.getNetworkId(), netId);
				return false;
			}
									
			DevOnlineObject devOnline = PoolObjectDAO.getDevOnlineObject(dev);
			if ((!PROD_MODE && dev.getFirstAppear() != null) || (devOnline != null && devOnline.isOnline()))
			{
				/* get device config type */
				Products product = ProductUtils.getProducts(dev.getProductId());
				JsonConf.CONFIG_TYPE configType = RadioConfigUtils.getConfigTypeFromProducts(product);
				if (configType == JsonConf.CONFIG_TYPE.UNKNOWN)
				{
					log.errorf("dev %d %s find unknown configType %s", devOnline.getIana_id(), devOnline.getSn(), configType);
					return false;
				}
				
				/* check if group master device is set */

				/* group master config */
				if (net.getMasterDeviceId() != null && net.getMasterDeviceId() != 0)
				{
					log.debugf("memTrace - devOnline=%s new ConfigPutTask is called (APPLY_GROUP_MASTER_DEVICE_CONFIG)", devOnline);
					Future<Boolean> future = configPutImmediateExecutor.submit(new ConfigPutTask(new ConfigPutTaskInfo(orgId, netId, dev.getId(), sid, dev.getIanaId(), dev.getSn(), UPDATE_OPERATION.APPLY_GROUP_MASTER_DEVICE_CONFIG)));
					try {
						if (future != null)
							future.get(25, TimeUnit.SECONDS);
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						log.error("MemTrace - exception orgId "+orgId+" "+netId+" "+dev.getId()+" "+dev.getIanaId()+" "+dev.getSn(), e);
						future.cancel(true);
					}
				}
				else
				{
					/* skip blacklist */
					if (dev.getFirstAppear()==null)
					{
						log.debugf("orgId %s dev %d %s has never online (APPLY_GROUP_CONFIG)", orgId, dev.getIanaId(), dev.getSn());
						return false;
					}
					else if (dev.getFw_ver()==null)
					{
						log.warnf("orgId %s dev %d %s has fw_ver null (APPLY_GROUP_CONFIG)", orgId, dev.getIanaId(), dev.getSn());
						return false;
					}
					else if (RadioConfigUtils.isBlacklistFirmware(dev.getFw_ver()))
					{
						log.warnf("orgId %s dev %d %s fw_ver %s is blacklisted. skipped config application (APPLY_GROUP_CONFIG).", orgId, dev.getIanaId(), dev.getSn(), dev.getFw_ver());
						return false;
					}
					
					/* normal config */
					log.debugf("memTrace - devOnline=%s new ConfigPutTask is called (APPLY_GROUP_CONFIG)", devOnline);
					Future<Boolean> future = configPutImmediateExecutor.submit(new ConfigPutTask(new ConfigPutTaskInfo(orgId, netId, dev.getId(), sid, dev.getIanaId(), dev.getSn(), UPDATE_OPERATION.APPLY_GROUP_CONFIG)));
					/*try {
						if (future != null)
							future.get(20, TimeUnit.SECONDS);
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						log.error("MemTrace - exception (APPLY CONFIG) ", e);
						future.cancel(true);
					}*/
				}
			}
			else
			{
				String logMsg = String.format("memTrace - dev %d for orgId %s with no online object/offline", devId, orgId);
				log.debug(logMsg);
				devUpDAO.decrementConfUpdateForDev(dev.getId(), null, logMsg, false);	
			}

		} catch (Exception e) {
			log.errorf("performConfigUpdateDevice exception for sid %s org %s netId %d", sid, orgId, netId, e);
			log.error(e,e);
			return false;
		}
		return true;
	}
	
	/* perform immediate config application for device */
	private boolean performConfigClearDevice(String sid, String reason, Devices dev)
	{
		log.infof("performConfigClearDevice is called for (sid, reason, orgId, netId, dev) = (%s, %s, %s, %d, %s)", sid, reason, orgId, netId, dev);
		
		final boolean rdonly = false;

		/* perform config clear */
		try {
			/* GROUP CONFIG APPLICATION */
			NetworksDAO networkDAO = new NetworksDAO(orgId, rdonly);
			ConfigUpdatesDAO confUpDAO = new ConfigUpdatesDAO(false);
			ConfigUpdatesId confUpId = new ConfigUpdatesId(dev.getIanaId(), dev.getSn());

			Networks net = networkDAO.findById(netId);
			if (net == null)
			{
				log.errorf("network %d does not exist", netId);
				return false;
			}
			
			/* skip blacklist */
			if (dev.getFw_ver()==null)
			{
				String logMsg = String.format("orgId %s dev %d %s has fw_ver null (CLEAR_CONFIG)", orgId, dev.getIanaId(), dev.getSn());
				log.warnf(logMsg);
				confUpDAO.decrementConfClearForDev(confUpId, logMsg, null, true);
				return false;
			}
			else if (RadioConfigUtils.isBlacklistFirmware(dev.getFw_ver()))
			{
				String logMsg = String.format("orgId %s dev %d %s fw_ver %s is blacklisted. skipped config application (CLEAR_CONFIG).", orgId, dev.getIanaId(), dev.getSn(), dev.getFw_ver());
				log.warn(logMsg);
				/* only disable config update for CLEAR_CONFIG! */
				confUpId.setIanaId(dev.getIanaId());
				confUpId.setSn(dev.getSn());
				confUpDAO.decrementConfClearForDev(confUpId, logMsg, null, true);
				return false;
			}
				
			/* get device config type */
			Products product = ProductUtils.getProducts(dev.getProductId());
			JsonConf.CONFIG_TYPE configType = RadioConfigUtils.getConfigTypeFromProducts(product);
			if (configType == JsonConf.CONFIG_TYPE.UNKNOWN)
			{
				log.errorf("dev %d %s find unknown configType %s", dev.getIanaId(), dev.getSn(), configType);
				return false;
			}

			Future<Boolean> future = configPutImmediateExecutor.submit(new ConfigPutTask(new ConfigPutTaskInfo(orgId, null, dev.getId(), sid, dev.getIanaId(), dev.getSn(), UPDATE_OPERATION.CLEAR_CONFIG)));
			/*try {
				if (future != null)
					future.get(20, TimeUnit.SECONDS);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				log.error("MemTrace - exception (CLEAR_CONFIG)", e);
				future.cancel(true);
			}*/

		} catch (Exception e) {
			log.errorf("performConfigUpdateDevice exception for sid %s org %s netId %d", sid, orgId, netId, e);
			log.error(e,e);
			return false;
		}
		return true;
	}
}
