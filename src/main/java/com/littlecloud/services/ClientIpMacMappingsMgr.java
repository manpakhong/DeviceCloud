package com.littlecloud.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.ClientIpMacMappingsDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.entity.ClientIpMacMappings;
import com.littlecloud.control.entity.ClientIpMacMappingsId;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.pool.object.StationList;
import com.littlecloud.pool.object.StationListObject;
import com.littlecloud.pool.object.utils.StationListObjectUtils;

public class ClientIpMacMappingsMgr {
	private final static int MAX_STATION_INACTIVE = 7200; // 2 hrs
	private static final Logger log = Logger.getLogger(ClientIpMacMappingsMgr.class);
	private String orgId;
	private ClientIpMacMappingsDAO clientIpMacMappingsDao;
	public ClientIpMacMappingsMgr(String orgId){
		this.orgId = orgId;
		init();
	}
	private void init(){
		try{
			clientIpMacMappingsDao = new ClientIpMacMappingsDAO(orgId);
		} catch (Exception e){
			log.error("ClientIpMacMappingsMgr.init() - Exception:", e);
		}
	}
	public List<ClientIpMacMappings> getAllIpMacMapByNetworkDevice(Integer networkId, Integer deviceId) {
		List<ClientIpMacMappings> clientIpMacMappingList = null;
		try{
			clientIpMacMappingList = clientIpMacMappingsDao.getAllIpMacMapByNetworkDevice(networkId, deviceId);
		} catch (Exception e){
			log.error("ClientIpMacMappingsMgr.getAllIpMacMapByNetworkDevice() - Exception:", e);
		}
		return clientIpMacMappingList;
	}
	
	private Map<String, ClientIpMacMappings> getClientIpMacMap(Integer networkId, Integer deviceId){
		Map<String, ClientIpMacMappings> clientIpMacMap = null;
		try{
			ClientIpMacMappingsMgr clientIpMacMappingsMgr = new ClientIpMacMappingsMgr(orgId);
			List<ClientIpMacMappings> clientIpMacList = clientIpMacMappingsMgr.getAllIpMacMapByNetworkDevice(networkId, deviceId);
			clientIpMacMap = new HashMap<String, ClientIpMacMappings>();
			if (log.isInfoEnabled()){
				log.infof("ClientIpMacMappingsMgr.getClientIpMacMap() - clientIpMacList: %s", clientIpMacList);
			}
			if (clientIpMacList != null && clientIpMacList.size() > 0){
				for(ClientIpMacMappings clientIpMacMappingItem: clientIpMacList) {
					clientIpMacMap.put(clientIpMacMappingItem.getId().getIp(), clientIpMacMappingItem);
				}
			}
		} catch (Exception e){
			log.error("ClientIpMacMappingsMgr.fillClientIpMac() - Exception:", e);
		}
		return clientIpMacMap;
	}
	public boolean saveClientIpMacMappingList(List<ClientIpMacMappings> clientIpMacMappingList) {
		boolean areSaved = false;
		try{
			areSaved = clientIpMacMappingsDao.saveClientIpMacMappingList(clientIpMacMappingList);
		} catch(Exception e){
			log.error("ClientIpMacMappingsMgr.saveClientIpMacMappingList() - Exception:", e);
		}
		return areSaved;
	}
	
	
	public boolean doClientIpMacMappingsConsolidation(){
		boolean isDone = false;
		try{
			isDone = doClientIpMacMappingsConsolidation(null);
		}catch (Exception e){
			log.error("ClientIpMacMappingsMgr.doClientIpMacMappingsConsolidation() - Exception:", e);
		}
		return isDone;
	}
	
	public boolean doClientIpMacMappingsConsolidation(List<Devices> deviceList){
		boolean isDone = false;
		try{
			if (deviceList == null){
				DevicesDAO devicesDao = new DevicesDAO(orgId);
				deviceList = devicesDao.getAllDevices();
			}
			long tstart = System.currentTimeMillis();
			if (deviceList != null && deviceList.size() > 0){
				for (Devices device: deviceList){
					List<StationList> delList = new ArrayList<StationList>();
					List<ClientIpMacMappings> clientIpMacMappingList = new ArrayList<ClientIpMacMappings>();
					Map<String, ClientIpMacMappings> clientIpMacMap = getClientIpMacMap(device.getNetworkId(), device.getId());
					
					StationListObject stationListObject = StationListObjectUtils.getStationListObject(device.getIanaId(), device.getSn());
					if (stationListObject != null && stationListObject.getStation_list() != null){
						List<StationList> stationList = stationListObject.getStation_list();
						for (StationList stationMacListItem:stationList) {	
							if (stationMacListItem != null){
								if(log.isInfoEnabled()){
									log.infof("ClientIpMacMappingsMgr.doClientIpMacMappingsConsolidation(): working on org %s dev %d %s", orgId, device.getIanaId(), device.getSn());
								}
								if (stationMacListItem.getIp() == null) {
									continue;
								}
								boolean foundInTable = false;
								boolean sameRecord = false;
								
								ClientIpMacMappings cimFound = null;
								String ip = stationMacListItem.getIp();
								ClientIpMacMappings ipMacMap = clientIpMacMap.get(ip);
								if(ipMacMap!=null) {
									if (ipMacMap.getMac() != null && stationMacListItem.getMac() != null){
										if (ipMacMap.getMac().equals(stationMacListItem.getMac())) {
											sameRecord = true;												
										}
									}
									cimFound = ipMacMap;
									foundInTable = true;
								}
								if (!foundInTable) {
									ClientIpMacMappingsId cimMapId = new ClientIpMacMappingsId();
									cimMapId.setIp(stationMacListItem.getIp());
									cimMapId.setDeviceId(stationListObject.getDevice_id());
									cimMapId.setNetworkId(stationListObject.getNetwork_id());
									ClientIpMacMappings clientIpMacMapping = new ClientIpMacMappings();
									clientIpMacMapping.setId(cimMapId);
									clientIpMacMapping.setMac(stationMacListItem.getMac());
									clientIpMacMapping.setUnixtime(stationListObject.getTimestamp());
									clientIpMacMapping.replace();
									if(log.isInfoEnabled()){
										log.infof("ClientIpMacMappingsMgr.doClientIpMacMappingsConsolidation(): addBatch clientIpMacMappings org %s dev %d %s", orgId, device.getIanaId(), device.getSn());
									}
									clientIpMacMappingList.add(clientIpMacMapping);
								} else if (!sameRecord) {
									//update the mac
									if(cimFound != null && stationMacListItem != null && stationMacListItem.getMac() != null){
										cimFound.setMac(stationMacListItem.getMac());
										cimFound.setUnixtime(stationListObject.getTimestamp());
										cimFound.replace();
										if(log.isInfoEnabled()){
											log.infof("ClientIpMacMappingsMgr.doClientIpMacMappingsConsolidation(): addBatch cimFound org %s dev %d %s", orgId, device.getIanaId(), device.getSn());
										}
										clientIpMacMappingList.add(cimFound);
									}
								}
								
								if(stationMacListItem.getLastUpdateTime()!=null) {
									if((int)(tstart / 1000) - stationMacListItem.getLastUpdateTime() > MAX_STATION_INACTIVE) {
										delList.add(stationMacListItem);
									}
								}
							} // end if stationMacListItem != null
						}
						if (clientIpMacMappingList != null && clientIpMacMappingList.size() > 0){
							boolean areSaved = saveClientIpMacMappingList(clientIpMacMappingList);
							if (!areSaved){
								log.warnf("ClientIpMacMappingsMgr.doClientIpMacMappingsConsolidation(): - saveClientIpMacMappingList failed!orgId:%s, ianaId:%s, sn:%s",orgId, device.getIanaId(), device.getSn());
							}
						}
						if(delList.size() > 0) {
							if(log.isInfoEnabled()) {
								StringBuilder sb = new StringBuilder();
								for(StationList delItem: delList) {
									sb.append(delItem.getMac());
									sb.append("=");
									sb.append(delItem.getIp());
									sb.append(" ");
								}
								if (log.isInfoEnabled()){
									log.infof("ClientIpMacMappingsMgr.doClientIpMacMappingsConsolidation(): removed inactive clients: orgId=%s, sn=%s, clients=%s" , orgId, device.getSn(), sb);
								}
							}
							stationList.removeAll(delList);
						}
						stationListObject.setStation_list(stationList);
						stationListObject.setTotalClient(stationList.size());
						boolean isUpdated = StationListObjectUtils.updateStationListObject(stationListObject);
						if (!isUpdated){
							log.warnf("ClientIpMacMappingsMgr.doClientIpMacMappingsConsolidation(): cached StationListObject failed!!!, orgid %s dev %d %s", orgId, device.getIanaId(), device.getSn());
						}
					}
				}								
			}
		} catch (Exception e){
			log.error("ClientIpMacMappingsMgr.doClientIpMacMappingsConsolidation() - Exception:", e);
		}
		return isDone;
	}
}
