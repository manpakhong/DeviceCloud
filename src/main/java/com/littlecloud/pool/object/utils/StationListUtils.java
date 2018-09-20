package com.littlecloud.pool.object.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;

import com.littlecloud.ac.json.model.Json_StationList;
import com.littlecloud.ac.json.model.Json_StationList.IfiList;
import com.littlecloud.ac.json.model.Json_StationList.StationStatusList;
import com.littlecloud.ac.json.model.Json_StationList.StationWirelessList;
import com.littlecloud.pool.object.PoolObjectDAO;
import com.littlecloud.pool.object.StationList;
import com.littlecloud.pool.object.StationListObject;

public class StationListUtils {
	private static final int INITIAL_CAPACITY = 100;
	private static Logger log = Logger.getLogger(StationListUtils.class);
	
	public static void copyStationList(StationList fromObj, StationList toObj)
	{
		// skip info that always use new
		// toObj.setClient_id(fromObj.getClient_id());
		// toObj.setIp(fromObj.getIp());
		// toObj.setStatus(fromObj.getStatus());
		// toObj.setDevice_id(fromObj.getDevice_id());
		
		// always copy from old
		toObj.setFirst_appear_time(fromObj.getFirst_appear_time());
		
		// copy old if not present in new
		if(toObj.getMac() == null || toObj.getMac().length()==0)
			toObj.setMac(fromObj.getMac().toUpperCase());

		// copy old if not present in new
		if(toObj.getName() == null || toObj.getName().length()==0)
			toObj.setName(fromObj.getName());
		
		// copy old wireless info if new is not wireless
		if(!toObj.getType().equalsIgnoreCase("wireless") && fromObj.getType().equalsIgnoreCase("wireless")) {
			toObj.setType(fromObj.getType());
			toObj.setRadio_mode(fromObj.getRadio_mode());
			toObj.setConn_len(fromObj.getConn_len());
			toObj.setRssi(fromObj.getRssi());
			toObj.setIfi_id(fromObj.getIfi_id());
			toObj.setBssid(fromObj.getBssid());
			toObj.setEssid(fromObj.getEssid());
			toObj.setSecurity(fromObj.getSecurity());
			toObj.setChannel(fromObj.getChannel());
			toObj.setCh_width(fromObj.getCh_width());
		}
	}
	
	public static StationListObject convertStationLst(Json_StationList stationList)
	{
		if (stationList == null) {
			return null;
		}
		StationListObject slo = new StationListObject();
		slo.setSn(stationList.getSn());
		slo.setDevice_id(stationList.getDevice_id());
		slo.setNetwork_id(stationList.getNetwork_id());
		slo.setOrganization_id(stationList.getOrganization_id());
		slo.setSid(stationList.getSid());
	
		List<StationStatusList> JSSList = stationList.getStation_status_list();
		List<StationWirelessList> JSWList = stationList.getStation_wireless_list();
		if (JSSList == null && JSWList == null) {
			return null;
		}
		
		int ss_size = JSSList==null? 0: JSSList.size();
		int sw_size = JSWList==null? 0: JSWList.size();
		
		List<IfiList> JIfiList = stationList.getIfi_list();
	
//		slo.setTimestamp(stationList.getTimestamp());
		int curTimestamp = (int)(new Date().getTime()/1000);
		
		HashMap<String, StationList> stationMap = new HashMap<String, StationList>((int)((ss_size + sw_size) / 0.75 + 1));
		
		if(JSSList !=null) {
			for (int i = 0; i < JSSList.size(); i++) {
				StationStatusList JSS = JSSList.get(i);
				StationList sl = new StationList();
				if (JSS.getMac() != null)
					sl.setMac(JSS.getMac().toUpperCase());
				sl.setIp(JSS.getIp());
				sl.setName(JSS.getName());
				sl.setStatus(JSS.getStatus());
				sl.setType(JSS.getType());
				sl.setClient_id(PoolObjectDAO.convertToClientId(sl.getMac(), sl.getIp()));
				if (sl.getStatus().equalsIgnoreCase("active")){
					sl.setFirst_appear_time(curTimestamp);
				}			
				sl.setLastUpdateTime(curTimestamp);
				stationMap.put(sl.getClient_id(), sl);
				if(log.isDebugEnabled())
					log.infof("put StationList obj: mac=%s, ip=%s, status=%s, type=%s", sl.getMac(), sl.getIp(), sl.getStatus(), sl.getType());
			}
		}
		
		if (JSWList != null) {
			for (int i = 0; i < JSWList.size(); i++) {
				StationWirelessList JSW = JSWList.get(i);
				if(JSW.getMac() == null)
					continue;
				
				// check by client_id if the station is already in ethernet station list
				String client_id = PoolObjectDAO.convertToClientId(JSW.getMac(), JSW.getIp());
				StationList sl = stationMap.get(client_id);
				if (sl != null) {
					sl.setRadio_mode(JSW.getRadio_mode());
					sl.setConn_len(JSW.getConn_len());
					sl.setRssi(JSW.getRssi());
					sl.setIfi_id(JSW.getIfi_id());
					
					// get the wireless interface details
					if (JIfiList != null) {
						for (int k = 0; k < JIfiList.size(); k++) {							
							IfiList il = JIfiList.get(k);
							if (il.getIfi_id() == sl.getIfi_id()) {
								sl.setBssid(il.getBssid());
								sl.setEssid(il.getEssid());
								sl.setSecurity(il.getSecurity());
								sl.setChannel(il.getChannel());
								sl.setCh_width(il.getCh_width());
								break;
							}
						}
					}
				} else {
					if(log.isDebugEnabled())
						StationListObject.log.debug("Not match in ethernet list, mac=" + JSW.getMac());
					StationList new_sl = new StationList();
					new_sl.setClient_id(client_id);
					new_sl.setMac(JSW.getMac().toUpperCase());
					new_sl.setIp(JSW.getIp());
					new_sl.setStatus(JSW.getStatus());
					new_sl.setRadio_mode(JSW.getRadio_mode());
					new_sl.setConn_len(JSW.getConn_len());
					new_sl.setRssi(JSW.getRssi());
					new_sl.setIfi_id(JSW.getIfi_id());
					new_sl.setType("wireless");
					// get the wireless interface details
					if (JIfiList != null){
						for (int k = 0; k < JIfiList.size(); k++) {
							IfiList il = JIfiList.get(k);
							if (il.getIfi_id() == new_sl.getIfi_id()) {
								new_sl.setBssid(il.getBssid());
								new_sl.setEssid(il.getEssid());
								new_sl.setSecurity(il.getSecurity());
								new_sl.setChannel(il.getChannel());
								new_sl.setCh_width(il.getCh_width());
								break;
							}
						}
					}
					if (new_sl.getStatus().equalsIgnoreCase("active")){
						new_sl.setFirst_appear_time(curTimestamp);
					}
					new_sl.setLastUpdateTime(curTimestamp);
					stationMap.put(client_id, new_sl);
//					if(log.isDebugEnabled())
//						log.infof("put StationList wireless obj: mac=%s, ip=%s, status=%s, type=%s", sl.getMac(), sl.getIp(), sl.getStatus(), sl.getType());
				}
			}
		}
		if(log.isDebugEnabled())
			log.info("stationMap = " + stationMap.values());

		List<StationList> lan_list = new ArrayList<StationList>(stationMap.values());
		slo.setStation_list(new CopyOnWriteArrayList<StationList>(lan_list));
	
		return slo;
	}
	
	
}
