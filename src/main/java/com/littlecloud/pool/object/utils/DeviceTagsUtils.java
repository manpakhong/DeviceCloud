package com.littlecloud.pool.object.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.DevicesxtagsDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.viewobject.DeviceTags;
import com.littlecloud.pool.object.DevDeviceTagsObject;
import com.littlecloud.pool.object.DevicesTagDeviceObject;

public class DeviceTagsUtils {
	private static Logger log = Logger.getLogger(DeviceTagsUtils.class);
	
	public static List<Devices> getDeviceListByTagsId(Integer tagsId, String organizationId, Integer networkId){
		List<Devices> deviceList = null;
		try{
			List<Integer> deviceIdList = getDeviceIdListByTagsId(tagsId, organizationId);
			if (deviceIdList != null){
				deviceList = NetUtils.getDeviceLstByDevId(organizationId, networkId, deviceIdList);
			}
			
		}catch (Exception e){
			log.error("DEVTAG20140225 - getDeviceListByTagsId ", e);
		}
		return deviceList;
		
	}
	
	public static List<Integer> getDeviceIdListByTagsId(Integer tagsId, String organizationId){
		CopyOnWriteArrayList<Integer> deviceIdList = null;
		try{
			if (tagsId != null){
				DevicesTagDeviceObject devicesTagDeviceObject = new DevicesTagDeviceObject();
				devicesTagDeviceObject.setTag_id(tagsId);
				devicesTagDeviceObject = ACUtil.<DevicesTagDeviceObject> getPoolObjectBySn(devicesTagDeviceObject, DevicesTagDeviceObject.class);
				
				if (devicesTagDeviceObject != null){
					deviceIdList = devicesTagDeviceObject.getDevice_id_list();					
				} else {
					Set<Integer> deviceIdSet = new HashSet<Integer>();
					DevicesxtagsDAO dao = new DevicesxtagsDAO(organizationId);
					List<DeviceTags> deviceTagsList = dao.getDeviceTagsByTagsId(tagsId);
					if (deviceTagsList != null){
						deviceIdList = new CopyOnWriteArrayList<Integer>();
						devicesTagDeviceObject = new DevicesTagDeviceObject();
						devicesTagDeviceObject.setTag_id(tagsId);
						for (DeviceTags deviceTags: deviceTagsList){
							if (deviceIdSet.add(deviceTags.getDeviceId())){ // check if duplicate deviceId is added
								deviceIdList.add(deviceTags.getDeviceId());
							} // if (deviceIdSet.add(deviceTags.getDeviceId()))
						} // end for (DeviceTags deviceTags: deviceTagsList)
						devicesTagDeviceObject.setDevice_id_list(deviceIdList);
						ACUtil.<DevicesTagDeviceObject> cachePoolObjectBySn(devicesTagDeviceObject, DevicesTagDeviceObject.class);
					} // end if (deviceTagsList != null)
					
				} // end if (devicesTagDeviceObject != null)... else ...
			} // end if (tagsId != null)
		}catch (Exception e){
			log.error("DEVTAG20140225 - getDeviceIdListByTagsId ", e);
		}
		return deviceIdList;
	} // end getDeviceIdListByTagsId
	
	public static List<DeviceTags> getDeviceTagsList(Devices devices, String organizationId){
		List<DeviceTags> deviceTagsList = null;
		try{
			if (devices != null && organizationId != null){
				DevDeviceTagsObject devDeviceTagsObject = new DevDeviceTagsObject();
				devDeviceTagsObject.setKey(devices.getIanaId(), devices.getSn());
	
				// try to get devGeoFencesObject from Pool
				devDeviceTagsObject = ACUtil.getPoolObjectBySn(devDeviceTagsObject, DevDeviceTagsObject.class);
				
				if (devDeviceTagsObject != null){
					deviceTagsList = convert2DeviceTags(devDeviceTagsObject);
				} else {
					DevicesxtagsDAO devicesxtagsDao = new DevicesxtagsDAO(organizationId);
					deviceTagsList = devicesxtagsDao.getDeviceTagsByDeviceId(devices.getId());
					
					devDeviceTagsObject = convert2DevDeviceTagsObject(devices, deviceTagsList);
					
					if (devDeviceTagsObject != null){
						ACUtil.<DevDeviceTagsObject> cachePoolObjectBySn(devDeviceTagsObject, DevDeviceTagsObject.class);
					}
				}
			}
		}catch (Exception e){
			log.error("DEVTAG20140225 - getDeviceTagsList ", e);
		}
		return deviceTagsList;
	} // end getDeviceTagsList
	
	public static List<DeviceTags> convert2DeviceTags(DevDeviceTagsObject devDeviceTagsObject){
		List<DeviceTags> deviceTagsList = null;
		
		if (devDeviceTagsObject != null){
			List<DevDeviceTagsObject.DevDeviceXTags> devDeviceXTagsList = devDeviceTagsObject.getDevDeviceXTagsList();
			if (devDeviceXTagsList != null){
				deviceTagsList = new ArrayList<DeviceTags>();
				for (DevDeviceTagsObject.DevDeviceXTags devDeviceXTags: devDeviceXTagsList){
					DeviceTags deviceTags = new DeviceTags();
					deviceTags.setDeviceId(devDeviceXTags.getDevice_id());
					deviceTags.setTagId(devDeviceXTags.getTag_id());
					deviceTags.setTagName(devDeviceXTags.getTag_name());
					deviceTagsList.add(deviceTags);
				}
			}
		}
		return deviceTagsList;
	}
	
	public static boolean deleteDevDeviceTagsObject(DevDeviceTagsObject devDeviceTagsObject){
		boolean result = false;
		try{
			if (devDeviceTagsObject != null){
				if (devDeviceTagsObject.getSn() != null && devDeviceTagsObject.getIana_id() != null){
					ACUtil.<DevDeviceTagsObject> removePoolObjectBySn(devDeviceTagsObject, DevDeviceTagsObject.class);
				}
			}
		} catch (Exception e){
			log.error("DEVTAG20140225 - deleteDevDeviceTagsObject ", e);
		}
		return result;
	}
	
	
	public static DevDeviceTagsObject convert2DevDeviceTagsObject(Devices devices, List<DeviceTags> deviceTagsList){
		DevDeviceTagsObject devDeviceTagsObject = null;
		if (deviceTagsList != null){
			devDeviceTagsObject = new DevDeviceTagsObject();
			devDeviceTagsObject.setIana_id(devices.getIanaId());
			devDeviceTagsObject.setSn(devices.getSn());
			
			CopyOnWriteArrayList<DevDeviceTagsObject.DevDeviceXTags> devDeviceXTagsList = new CopyOnWriteArrayList<DevDeviceTagsObject.DevDeviceXTags>();
			
			for (DeviceTags deviceTags: deviceTagsList){
				
				DevDeviceTagsObject.DevDeviceXTags devDeviceXTags= devDeviceTagsObject.new DevDeviceXTags();
				devDeviceXTags.setDevice_id(deviceTags.getDeviceId());
				devDeviceXTags.setTag_id(deviceTags.getTagId());
				devDeviceXTags.setTag_name(deviceTags.getTagName());
				devDeviceXTagsList.add(devDeviceXTags);
			}
			devDeviceTagsObject.setDevDeviceXTagsList(devDeviceXTagsList);
		} // end if (deviceTagsList != null)
		
		return devDeviceTagsObject;
	} // end convert2DevDeviceTagsObject
} // end class
