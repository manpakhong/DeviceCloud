package com.littlecloud.pool.object.utils;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.json.model.Json_DeviceNeighbor;
import com.littlecloud.ac.json.model.Json_DeviceNeighborList;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.json.model.Json_Device_Neighbor_List;
import com.littlecloud.pool.object.DevNeighborListObject;
import com.littlecloud.pool.object.DevNeighborListObject.Neighbor;


public class DeviceNeighborUtils {
	private static Logger log = Logger.getLogger(DeviceNeighborUtils.class);
	public static DevNeighborListObject getDevNeighborListObjectFromCache(DevNeighborListObject devNeighborListObject){
		try{
			if (devNeighborListObject != null){
				if (devNeighborListObject.getIana_id() != null && devNeighborListObject.getSn() != null){
					devNeighborListObject = ACUtil.getPoolObjectBySn(devNeighborListObject, DevNeighborListObject.class);			
				} // end if (devDdnsRecordsObject.getKey() != null)
			} // end if (devDdnsRecordsObject != null)
		} catch (Exception e){
			log.errorf(e, "INDOORPOS20140519 - DeviceNeighborUtils.getDevNeighborListObjectFromCache()");
		} // end try ... catch ...
		return devNeighborListObject;
	} // end getDevNeighborListObjectFromCache
	
	public static DevNeighborListObject getDevNeighborListObjectFromCache(Integer iana, String sn){
		DevNeighborListObject devNeighborListObject = new DevNeighborListObject(iana, sn);
		try{
			devNeighborListObject = getDevNeighborListObjectFromCache(devNeighborListObject);		
		} catch (Exception e){
			log.errorf(e, "INDOORPOS20140519 - DeviceNeighborUtils.getDevNeighborListObjectFromCache()");
		}
		return devNeighborListObject;
	}
	
	public static boolean setDevNeighborListObject2Cache(DevNeighborListObject devNeighborListObject){
		boolean isSet = false;
		try{
			if (devNeighborListObject != null){
				ACUtil.<DevNeighborListObject> cachePoolObjectBySn(devNeighborListObject, DevNeighborListObject.class);
			} // end if (devGeoFencesObject != null)
			isSet = true;
		} catch (Exception e){
			log.errorf(e, "INDOORPOS20140519 - DeviceNeighborUtils.setDevNeighborListObject2Cache()");
		} // end try ... catch ...
		return isSet;
	} // end setDevNeighborListObject2Cache
	
	private static boolean removeDevNeighborList(DevNeighborListObject devNeighborListObject){
		boolean isRemoved = false;
		
		if (devNeighborListObject != null){
			devNeighborListObject.resetNeighborList();
			isRemoved = true;
		}
		
		return isRemoved;
	}
	
	public static boolean removeDevNeighborListObjectFromCache(DevNeighborListObject devNeighborListObject){
		boolean isRemoved = false;
		try{
			if (devNeighborListObject != null){
				if (devNeighborListObject.getIana_id() != null && devNeighborListObject.getSn() != null){
					ACUtil.<DevNeighborListObject> removePoolObjectBySn(devNeighborListObject, DevNeighborListObject.class);	
					isRemoved = true;
				}
			}
		}catch (Exception e){
			log.errorf(e, "INDOORPOS20140519 - DeviceNeighborUtils.removeDevNeighborListObjectFromCache()");
		}
		return isRemoved;
	} // end removeDevNeighborListObjectFromCache()
	
	public static List<Neighbor> getDevNeighborList(Integer ianaId, String sn){
		List<Neighbor> neighborList = null;
		try{
			if (ianaId != null && sn != null){
				DevNeighborListObject devNeighborListObject = new DevNeighborListObject(ianaId, sn);
				devNeighborListObject = getDevNeighborListObjectFromCache(devNeighborListObject);
				if (devNeighborListObject != null){
					neighborList = devNeighborListObject.getNeighborList();
				}
			} else {
				log.warnf("INDOORPOS20140519 - DeviceNeighborUtils.getDevNeighborList(), ianaId or sn is null!");
			}
		} catch (Exception e){
			log.errorf(e, "INDOORPOS20140519 - DeviceNeighborUtils.getDevNeighborList()");
		}
		return neighborList;
	} // end getDevNeighborList()
	
	public static Json_Device_Neighbor_List getJsonDeviceNeighborList(Integer ianaId, String sn){
		Json_Device_Neighbor_List jsonDeviceNeighborList = null;
		
		List<Neighbor> neighborList = getDevNeighborList(ianaId, sn);
		jsonDeviceNeighborList = convert2JsonDeviceNeighborList(neighborList);
		
		return jsonDeviceNeighborList;
	}
	
	public static Json_Device_Neighbor_List convert2JsonDeviceNeighborList(List<Neighbor> devNeighorList){
		Json_Device_Neighbor_List jsonDeviceNeighborList = new Json_Device_Neighbor_List();
		if (devNeighorList != null && devNeighorList.size() > 0){
			for (Neighbor neighbor: devNeighorList){
				if (neighbor != null){
					jsonDeviceNeighborList.addDevNeighbor(neighbor);
				}
			}
		}		
		return jsonDeviceNeighborList;
	}
	
	public static boolean updateDevNeighborListByJsonDeviceNeighborList(Integer ianaId, String sn, Json_DeviceNeighborList jsonDeviceNeighborList){
		boolean updated = false;
		try{
			if (ianaId != null && sn != null && jsonDeviceNeighborList != null){

				List<Neighbor> neighborList = convert2DevNeighborList(jsonDeviceNeighborList);
				if (log.isDebugEnabled()){
					log.debugf("INDOORPOS20140519 - DeviceNeighborUtils.updateDevNeighborListByJsonDeviceNeighborList() - iana: %s, sn: %s, no of neighborList: %s",ianaId, sn, neighborList.size());
				}
				updated = updateDevNeighborList(ianaId, sn, neighborList);
			}
		} catch (Exception e){
			log.errorf(e, "INDOORPOS20140519 - DeviceNeighborUtils.updateDevNeighborList()");
		}
		return updated;
	}

	private static List<Neighbor> convert2DevNeighborList(Json_DeviceNeighborList jsonDeviceNeighborList){
		List<Neighbor> neighborList = new ArrayList<Neighbor>();
		if (jsonDeviceNeighborList != null){
			if (jsonDeviceNeighborList.getNeighbor() != null){
				for (Json_DeviceNeighbor jsonDeviceNeighbor: jsonDeviceNeighborList.getNeighbor()){
					if (jsonDeviceNeighbor != null){
						DevNeighborListObject devNeighborListObject = new DevNeighborListObject();
						Neighbor neighbor = devNeighborListObject.new Neighbor();
						neighbor.setChannel(jsonDeviceNeighbor.getChannel());
						neighbor.setLast(jsonDeviceNeighbor.getLast());
						neighbor.setMac(jsonDeviceNeighbor.getMac());
						neighbor.setSecurity(jsonDeviceNeighbor.getSecurity());
						neighbor.setSignal(jsonDeviceNeighbor.getSignal());
						neighbor.setSsid_hex(jsonDeviceNeighbor.getSsid_hex());
						neighbor.setStatus(jsonDeviceNeighbor.getStatus());
						
						neighborList.add(neighbor);
						
					}
				}
			}
		}
		return neighborList;
	}
	
	public static boolean updateDevNeighborList(Integer ianaId, String sn, List<Neighbor> neighorList){
		boolean updated = false;
		try{
			DevNeighborListObject devNeighborListObject = new DevNeighborListObject(ianaId, sn);
			devNeighborListObject = getDevNeighborListObjectFromCache(devNeighborListObject);
			if (devNeighborListObject != null){
				removeDevNeighborList(devNeighborListObject);
				if (neighorList != null){
					devNeighborListObject.setNeighborList(neighorList);
				}
				setDevNeighborListObject2Cache(devNeighborListObject);
				if (log.isDebugEnabled()){
					log.debugf("INDOORPOS20140519 - DeviceNeighborUtils.updateDevNeighborList() - update List and cache, iana: %s, sn: %s",ianaId, sn);
				}
			} else {
				devNeighborListObject = new DevNeighborListObject(ianaId, sn);
	
				if (neighorList != null){
					devNeighborListObject.setNeighborList(neighorList);		
				}
				setDevNeighborListObject2Cache(devNeighborListObject);
				if (log.isDebugEnabled()){
					log.debugf("INDOORPOS20140519 - DeviceNeighborUtils.updateDevNeighborList() - new List and cache, iana: %s, sn: %s",ianaId, sn);
				}
			}
			
		} catch (Exception e){
			log.errorf(e, "INDOORPOS20140519 - DeviceNeighborUtils.updateDevNeighborList()");
		}
		return updated;
	}
	
	
} // end class
