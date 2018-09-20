package com.littlecloud.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.DeviceStatusDAO;
import com.littlecloud.control.entity.DeviceStatus;
import com.littlecloud.control.entity.Devices;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.util.DBUtil;

public class DeviceStatusMgr {
	private static final Logger log = Logger.getLogger(DeviceStatusMgr.class);
	private String orgId;
	private Map<Integer, DeviceStatus> deviceStatusMap;
	private DeviceStatusDAO deviceStatusDao;
	
	public DeviceStatusMgr(String orgId){
		init(orgId);
	}
	
	public DeviceStatusMgr(String orgId, List<Devices> deviceList){
		init(orgId, deviceList);
	}
	
	private void init(String orgId){
		init(orgId, null);
	}
	
	private void init(String orgId, List<Devices> deviceList){
		try{
			this.orgId = orgId;
			deviceStatusMap = new HashMap<Integer, DeviceStatus>();
			deviceStatusDao = new DeviceStatusDAO (this.orgId);
			List<DeviceStatus> deviceStatusList = getDeviceStatusList(deviceList);
			if (deviceStatusList != null && deviceStatusList.size() > 0){
				for (DeviceStatus deviceStatus: deviceStatusList){
					if (deviceStatus != null){
						deviceStatusMap.put(deviceStatus.getDeviceId(), deviceStatus);
					}
				}
			}
		} catch (Exception e){
			log.error("ALERT201408211034 - DeviceStatusMgr constructor -", e);
		}		
	}
	
	public boolean deleteDeviceStatusInBatch(List<DeviceStatus> deviceStatusList){
		boolean areDeleted = false;
		try{
			if (deviceStatusList != null && deviceStatusList.size() > 0){
				DeviceStatus [] deviceStatusArray = new DeviceStatus[deviceStatusList.size()];
				for (int i = 0; i < deviceStatusList.size(); i++){
					deviceStatusArray[i] = deviceStatusList.get(i);
					if (log.isDebugEnabled()){
						log.debugf("ALERT201408211034 - DeviceStatusMgr.deleteDeviceStatusInBatch, deviceStatusArray[%s] : %s",i, deviceStatusArray[i]);
					}
				}
				deviceStatusDao.delete(deviceStatusArray);
				areDeleted = true;
			}
		} catch (Exception e){
			log.error("ALERT201408211034 - DeviceStatusMgr.deleteDeviceStatusInBatch -", e);
		}
		return areDeleted;
	}
	
	public boolean updateOrSaveDeviceStatusInBatch(List<DeviceStatus> deviceStatusList){
		boolean areDeleted = false;
		DBUtil dbUtil = null;
		DBConnection batchConnection = null;
		try{
			dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
			batchConnection = dbUtil.getConnection(false, orgId, false);
			for (DeviceStatus deviceStatus: deviceStatusList){
				batchConnection.addBatch(deviceStatus);
			}
			batchConnection.getBatchSize();
			batchConnection.commit();
			areDeleted = true;
		} catch (Exception e){
			log.error("ALERT201408211034 - DeviceStatusMgr.updateOrSaveDeviceStatusInBatch() -", e);
		}finally {
			//logger.warn("StartPersistReportFromCache: ClientUsages finally...");
			try{
				if (batchConnection!=null){
					batchConnection.commit();
					batchConnection.close();
				}
				if (dbUtil != null){
					dbUtil.endSession();
				}
			}
			catch(Exception e){
				log.error("ALERT201408211034 - DeviceStatusMgr.updateOrSaveDeviceStatusInBatch() -",e);
				try {
					if (dbUtil.isSessionStarted()) {
						dbUtil.endSession();
					}
				} catch (Exception e2) {
					log.error("ALERT201408211034 - DeviceStatusMgr.updateOrSaveDeviceStatusInBatch() -", e2);
				}
			}
		}
		return areDeleted;
	}
	
	
	private List<DeviceStatus> getDeviceStatusList(List<Devices> deviceList){
		List<DeviceStatus> deviceStatusList = null;
		try{
			if (deviceList != null && deviceList.size() > 0){
				List<Integer> deviceIdList = retrieveDeviceIdList(deviceList);
				deviceStatusList = deviceStatusDao.getDeviceStatusList(deviceIdList);
			}
		} catch (Exception e){
			log.error("ALERT201408211034 - DeviceStatusMgr.getDeviceStatus()", e);
		}
		return deviceStatusList;
	}
	
	private List<Integer> retrieveDeviceIdList(List<Devices> deviceList){
		List<Integer> deviceIdList = null;
		if (deviceList != null && deviceList.size() > 0){
			deviceIdList = new ArrayList<Integer>();
			for (Devices device: deviceList){
				if (device != null && device.getId() != null){
					deviceIdList.add(device.getId());
				}
			}
		}
		return deviceIdList;
	}
	
	private DeviceStatus getDeviceStatus(Integer deviceId){
		DeviceStatus deviceStatus = null;
		try{
			if (deviceId != null){
				deviceStatus = deviceStatusDao.findById(deviceId);
			}
		} catch (Exception e){
			log.error("ALERT201408211034 - DeviceStatusMgr.getDeviceStatus()", e);
		}
		return deviceStatus;
	}
	
	private DeviceStatus getDeviceStatusFromHashMapThenDb(Integer deviceId){
		DeviceStatus deviceStatus = null;
		deviceStatus = deviceStatusMap.get(deviceId);
		if (deviceStatus == null){
			deviceStatus = getDeviceStatus(deviceId);
		}	
		
		return deviceStatus;
	}
	
	public int getLastSentAlertLevelDeviceStatus(Integer deviceId){
		int lastSentAlertLevel = 0;
		try{
			if (deviceId != null){
				DeviceStatus deviceStatus = getDeviceStatusFromHashMapThenDb(deviceId);
				
				if (deviceStatus != null){
					int dbSentAlertLevel = deviceStatus.getAlertedLevel();
					lastSentAlertLevel = dbSentAlertLevel;
				}
			}
		} catch (Exception e){
			log.error("ALERT201408211034 - DeviceStatusMgr.getLastSentAlertLevelDeviceStatus()", e);
		}
		
		return lastSentAlertLevel;
	}
	
	public int getNextTurnAlertLevel(Integer deviceId){
		int lastLevel = 1;
		try{
			DeviceStatus deviceStatus = getDeviceStatusFromHashMapThenDb(deviceId);
			if (deviceStatus != null && deviceStatus.getAlertedLevel() != null){
				int alertLevel = deviceStatus.getAlertedLevel();
				alertLevel++;
				lastLevel = alertLevel;
			}
		} catch (Exception e){
			log.error("ALERT201408211034 - DeviceStatusMgr.getDeviceStatus()", e);
		}
		
		return lastLevel;
	}
	
}
