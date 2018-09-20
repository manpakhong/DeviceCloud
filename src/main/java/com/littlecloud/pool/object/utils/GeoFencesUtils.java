package com.littlecloud.pool.object.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.GeoFencePointsDAO;
import com.littlecloud.control.dao.GeoFencesDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.GeoFencePoints;
import com.littlecloud.control.entity.GeoFences;
import com.littlecloud.control.entity.viewobject.DeviceTags;
import com.littlecloud.control.json.model.Json_Geo_Fence_Points;
import com.littlecloud.control.json.model.Json_Geo_Fences;
import com.littlecloud.control.json.request.JsonGeoFencesRequest;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.object.DevGeoFencesObject;

public class GeoFencesUtils {
	private static Logger log = Logger.getLogger(GeoFencesUtils.class);
	
	public static DevGeoFencesObject getDevGeoFencesObjectFromCache(DevGeoFencesObject devGeoFencesObject){
		try{
			if (devGeoFencesObject != null){

				if (devGeoFencesObject.getIana_id() != null && devGeoFencesObject.getSn() != null){
					devGeoFencesObject = ACUtil.getPoolObjectBySn(devGeoFencesObject, DevGeoFencesObject.class);			
				} // end if (devGeoFencesObject.getKey() != null)
			} // end if (devGeoFencesObject != null)
		
		} catch (Exception e){
			log.error("GEO20140204 - getDevGeoFencesFromCache", e);
		} // end try ... catch ...

		
		return devGeoFencesObject;
	} // end getDevGeoFences
	
	public static boolean setDevGeoFences2Cache(DevGeoFencesObject devGeoFencesObject){
		boolean isSet = false;
		try{
			if (devGeoFencesObject != null){
				ACUtil.<DevGeoFencesObject> cachePoolObjectBySn(devGeoFencesObject, DevGeoFencesObject.class);
			} // end if (devGeoFencesObject != null)
			isSet = true;
		} catch (Exception e){
			log.error("GEO20140204 - setDevGeoFences2Cache", e);
		} // end try ... catch ...
		return isSet;
	} // end getDevGeoFencesObjectFromCache

	
	public static boolean deleteGeoFences(List<Integer> geoIdList, Integer networkId, String organizationId){
		boolean result = false;
		try{
			GeoFencesDAO geoFencesDao = new GeoFencesDAO(organizationId);
			GeoFencePointsDAO geoFencePointsDao = new GeoFencePointsDAO(organizationId);
			if (geoIdList != null && geoIdList.size() > 0){
				List<GeoFences> geoFencesList = geoFencesDao.getGeoFencesList(geoIdList);
				if (geoFencesList != null && geoFencesList.size() > 0){
					GeoFences [] geoFencesArray = new GeoFences[geoFencesList.size()];
					for (int i = 0; i < geoFencesList.size(); i++){
						GeoFences geoFences = geoFencesList.get(i);
						List<GeoFencePoints> geoFencePointsList = geoFencePointsDao.getGeoFencePointsListByGeoId(geoFences.getId());
						if (geoFencePointsList != null && geoFencePointsList.size() > 0){
							GeoFencePoints [] geoFencePointsArray = new GeoFencePoints [geoFencePointsList.size()];
							for (int n = 0; n < geoFencePointsList.size(); n++){
								geoFencePointsArray[n] = geoFencePointsList.get(n);
							} // end for (int n = 0; n < geoFencePointsList.size(); n++)
							geoFencePointsDao.delete(geoFencePointsArray);
														
						} // end if (geoFencePointsList != null && geoFencePointsList.size() > 0)
						geoFencesArray[i] = geoFences;

						
						List<Devices> devicesList = NetUtils.getDeviceLstByNetId(organizationId, networkId);
						if (devicesList != null){
							if (log.isDebugEnabled()){
								log.debugf("GEO20140204 - deleteGeoFences-> no. of devicesList: %s, devId: %s,  networkId: %s, organizationId: %s",devicesList.size(), geoFences.getDeviceId(), geoFences.getNetworkId(), organizationId);		
							}
							for (Devices devices: devicesList){
								DevGeoFencesObject devGeoFencesObject = new DevGeoFencesObject();
								devGeoFencesObject.setIana_id(devices.getIanaId());
								devGeoFencesObject.setSn(devices.getSn());
								devGeoFencesObject = GeoFencesUtils.getDevGeoFencesObjectFromCache(devGeoFencesObject);
								if (devGeoFencesObject != null){
									int noOfDelete = devGeoFencesObject.removeGeoFencesObjectByGeoId(geoFences.getId());
									if (log.isDebugEnabled()){
										log.debugf("GEO20140204 - deleteGeoFences-> noOfDelete: %s, devId: %s,  networkId: %s, organizationId: %s",noOfDelete, geoFences.getDeviceId(), geoFences.getNetworkId(), organizationId);								
									}
									if (noOfDelete > 0){
										GeoFencesUtils.setDevGeoFences2Cache(devGeoFencesObject);
									}
								}
								
							}
						}

						
					} // end for (int i = 0; i < geoFencesList.size(); i++)
					geoFencesDao.delete(geoFencesArray);
					
					
					
				} // end if (geoFencesList != null && geoFencesList.size() > 0)
			} // end if (param_geo_id_list != null)		

			result = true;
		} catch (Exception e){
			log.error("GEO20140204 - deleteGeoFences Exception: geoFencesList: " + geoIdList , e);			
			return result;
		}
		return result;
	} // end deleteGeoFences
	
	
	
	public static boolean saveGeoFences(GeoFences geoFences, String organizationId){
		boolean isSaved = true;
		try{
			if (geoFences != null && organizationId != null){
				GeoFencesDAO geoFencesDao = new GeoFencesDAO(organizationId);
				GeoFencePointsDAO geoFencePointsDao = new GeoFencePointsDAO(organizationId);
				if (geoFences.getId() == null || geoFences.getId() == 0){
					geoFencesDao.save(geoFences);
				} else{
					geoFencesDao.update(geoFences);
				}
					
				if (geoFences.getId() != null && geoFences.getId() > 0){
					if (geoFences.getGeoFencePointsList() != null){
						geoFencePointsDao.deleteByGeoId(geoFences.getId());
						for (GeoFencePoints geoFencePoints: geoFences.getGeoFencePointsList()){
							geoFencePoints.setGeoId(geoFences.getId());
							geoFencePointsDao.saveOrUpdate(geoFencePoints);
							
							if (geoFencePoints.getId() == null){
								if (log.isDebugEnabled()){
									log.debugf("GEO20140204 - saveGeoFences->geoFencePoints failure! devId: %s,  networkId: %s, organizationId: %s", geoFences.getDeviceId(), geoFences.getNetworkId(), organizationId);		
								}
								isSaved = false;
							} // if (geoFencePoints.getId() != null)
						} // end for (GeoFencePoints geoFencePoints: geoFences.getGeoFencePointsList())
					} // end if (geoFences.getGeoFencePointsList() != null)
				} // end if (geoFences.getId() != null)
				else{
					if (log.isDebugEnabled()){
						log.debugf("GEO20140204 - saveGeoFences->geoFences failure! devId: %s,  networkId: %s, organizationId: %s", geoFences.getDeviceId(), geoFences.getNetworkId(), organizationId);
					}
					isSaved = false;
				} // end if (geoFences.getId() != null) ... else ...
				
				if (isSaved){
					if (geoFences.getNetworkId() != null && geoFences.getDeviceTag() != null && !geoFences.getDeviceTag().isEmpty()){
						String [] tagsIdArray = geoFences.getDeviceTag().split(GeoFences.DEVICE_TAG_DELIMITED);
						List<Integer> tagsIdList = null;
						if (tagsIdArray.length > 0){
							tagsIdList = new ArrayList<Integer>();
							for (String tagsId: tagsIdArray){
								Integer intTagsId = Integer.parseInt(tagsId);
								tagsIdList.add(intTagsId);
							}
						}
						
						if (tagsIdList != null && tagsIdList.size() > 0){
							List<Devices> devicesList = DeviceUtils.getDevicesListByTagsIdList(tagsIdList, geoFences.getNetworkId(), organizationId);
							changeDevGeoFencesObject(devicesList, geoFences);
						}
					} // end if (geoFences.getNetworkId() != null && geoFences.getDeviceTag() != null && !geoFences.getDeviceTag().isEmpty())
					
					if (geoFences.getNetworkId() != null && (geoFences.getDeviceTag() == null || geoFences.getDeviceTag().equals(""))){
						List<Devices> devicesList = NetUtils.getDeviceLstByNetId(organizationId, geoFences.getNetworkId());
						changeDevGeoFencesObject(devicesList, geoFences);
					}
				} // end if (isSaved)
			} // end if (geoFences != null && organizationId != null)
			else {
				if (log.isDebugEnabled()){
					log.debugf("GEO20140204 - don't pass null value into saveGeoFences - saveGeoFences failure! devId: %s,  networkId: %s, organizationId: %s", geoFences.getDeviceId(), geoFences.getNetworkId(), organizationId);
				}
				isSaved = false;
			}
		} catch (Exception e){
			log.error("GEO20140204", e);
			isSaved = false;
			return isSaved;
		} // end try ... catch ...
		// try to get devGeoFencesObject from Pool
		return isSaved;
	} // end saveGeoFences
	
	private static void changeDevGeoFencesObject(List<Devices> devicesList, GeoFences geoFences){
		try{
			// TODO check
			if (devicesList != null){
				for (Devices devices: devicesList){
					if (devices != null){
						DevGeoFencesObject devGeoFencesObject = new DevGeoFencesObject();
						devGeoFencesObject.setSn(devices.getSn());
						devGeoFencesObject.setIana_id(devices.getIanaId());
						devGeoFencesObject = GeoFencesUtils.getDevGeoFencesObjectFromCache(devGeoFencesObject);
						if (devGeoFencesObject != null){
							devGeoFencesObject.deleteThenAddFromGeoFences(geoFences);
						}else {
							devGeoFencesObject = new DevGeoFencesObject();
							devGeoFencesObject.setIana_id(devices.getIanaId());
							devGeoFencesObject.setSn(devices.getSn());
							devGeoFencesObject.addFromGeoFences(geoFences);
						}
						GeoFencesUtils.setDevGeoFences2Cache(devGeoFencesObject);
					}
				}
			}
		} catch (Exception e){
			log.error("GEO20140204 - changeDevGeoFencesObject - " + e, e);
		}
	}
	
	// check and get from cache first and then get from db
	public static DevGeoFencesObject getDevGeoFencesObjectByComboNetworkIdOrDeviceTag(Devices devices, Integer networkId, String organizationId){
		DevGeoFencesObject returnDevGeoFencesObject = null;
		try{
			if (devices != null && devices.getIanaId() != null && devices.getSn() != null && networkId != null){
				DevGeoFencesObject devGeoFencesObject = new DevGeoFencesObject();
				devGeoFencesObject.setKey(devices.getIanaId(), devices.getSn());
				devGeoFencesObject.setDevice_id(devices.getId());
				
				// try to get devGeoFencesObject from Pool
				devGeoFencesObject = GeoFencesUtils.getDevGeoFencesObjectFromCache(devGeoFencesObject);
				
				if (devGeoFencesObject != null){ // get from cache
					if (devGeoFencesObject.getHasRetrievedOnceByTag() && devGeoFencesObject.getHasRetrievedOnceByNetwork()){
						returnDevGeoFencesObject = devGeoFencesObject;
					} else {
						returnDevGeoFencesObject = getDevGeoFencesObjectByComboNetworkIdOrDeviceTagFromDB(devices, networkId, organizationId);
					}
				} else { // get from db
					returnDevGeoFencesObject = getDevGeoFencesObjectByComboNetworkIdOrDeviceTagFromDB(devices, networkId, organizationId);
				}
			} // end if (devices != null && devices.getIanaId() != null && devices.getSn() != null)
		}catch (Exception e){
			log.error("GEO20140204 - getDevGeoFencesObjectByComboNetworkIdOrDeviceTag - " + e, e);
		} // end try ... catch ...
		return returnDevGeoFencesObject;
	} // end getGeoFencesByDeviceId()	

	private static DevGeoFencesObject getDevGeoFencesObjectByComboNetworkIdOrDeviceTagFromDB(Devices devices, Integer networkId, String organizationId){
		DevGeoFencesObject returnDevGeoFencesObject = null;
		
		try{
			if (devices.getId() != null){
				List<GeoFences> geoFencesList = GeoFencesUtils.getGeoFencesByComboNetworkIdOrDeviceTags(devices, organizationId);
				
				if (geoFencesList != null){
					DevGeoFencesObject devGeoFencesObject = new DevGeoFencesObject();
					devGeoFencesObject.setKey(devices.getIanaId(), devices.getSn());
					devGeoFencesObject.setDevice_id(devices.getId());
					for (GeoFences geoFences: geoFencesList){						
						devGeoFencesObject.addFromGeoFences(geoFences);
					} // end for (GeoFences geoFences: geoFencesList)
					returnDevGeoFencesObject = devGeoFencesObject;
					GeoFencesUtils.setDevGeoFences2Cache(devGeoFencesObject);
				} // end if (geoFencesList != null)
			} // end if (devices.getId() != null)
		}catch (Exception e){
			log.error("GEO20140204 - getDevGeoFencesObjectByComboNetworkIdOrDeviceTagFromDB - " + e, e);
		}
		return returnDevGeoFencesObject;
	} // end getDevGeoFencesObjectByComboNetworkIdOrDeviceTagFromDB
	
	public static List<GeoFences> getGeoFencesByComboNetworkIdOrDeviceTags(Devices devices, String organizationId){
		List<GeoFences> comboGeoFencesList = new ArrayList<GeoFences>();
		Set<Integer> geoFencesIdSet = new HashSet<Integer>(); 
		try{

			List<GeoFences> networkGeoFencesList = getGeoFencesByNetworkIdButNoDeviceTags(devices.getNetworkId(), organizationId);
								
			if (networkGeoFencesList != null){
				for (GeoFences networkGeoFences: networkGeoFencesList){
					if (geoFencesIdSet.add(networkGeoFences.getId())){
						comboGeoFencesList.add(networkGeoFences);
					}
				}
			}
			
			List<GeoFences> tagGeoFencesList = getGeoFencesByComparedWithDeviceTags(devices, organizationId);
			if (tagGeoFencesList != null){
				for (GeoFences tagGeoFences: tagGeoFencesList){
					if (geoFencesIdSet.add(tagGeoFences.getId())){
						comboGeoFencesList.add(tagGeoFences);
					}
				}
			}
			
		} catch (Exception e){
			log.error("GEO20140204 - getGeoFencesByComboNetworkIdOrDeviceTags - " + e, e);
		}
		
		DevGeoFencesObject devGeoFencesObject = new DevGeoFencesObject();
		devGeoFencesObject.setKey(devices.getIanaId(), devices.getSn());
		
		devGeoFencesObject = GeoFencesUtils.getDevGeoFencesObjectFromCache(devGeoFencesObject);
		
		return comboGeoFencesList;
	}
	
	private static List<GeoFences> getGeoFencesByComparedWithDeviceTags(Devices devices, String organizationId){
		List<GeoFences> geoFencesList = null;
		if (devices != null && organizationId != null && !organizationId.isEmpty()){
			// method will get cache object first, else from db and then cache it
			List<DeviceTags> deviceTagsList = DeviceTagsUtils.getDeviceTagsList(devices, organizationId);
			if (deviceTagsList != null){
				geoFencesList = GeoFencesUtils.getGeoFencesByDeviceTagsList(devices, deviceTagsList, organizationId);
			}
		}
		return geoFencesList;
	}
	
	private static List<GeoFences> getGeoFencesByNetworkIdButNoDeviceTags(Integer networkId, String organizationId){
		List<GeoFences> geoFencesByNetworkIdList = new ArrayList<GeoFences>();
		Set<Integer> geoFencesIdSet = new HashSet<Integer>(); 
		try{
			if (networkId != null && organizationId != null){
				List<Devices> devicesList = NetUtils.getDeviceLstByNetId(organizationId, networkId);
				if (devicesList != null){
					for (Devices devices: devicesList){
						DevGeoFencesObject devGeoFencesObject = new DevGeoFencesObject();
						devGeoFencesObject.setIana_id(devices.getIanaId());
						devGeoFencesObject.setSn(devices.getSn());
						devGeoFencesObject = GeoFencesUtils.getDevGeoFencesObjectFromCache(devGeoFencesObject);
						
						if (devGeoFencesObject != null){
							if (devGeoFencesObject.getHasRetrievedOnceByNetwork()){
								if (devGeoFencesObject.getGeoFencesList() != null){
									List<GeoFences> geoFencesTempList = devGeoFencesObject.getGeoFencesList();
									for (GeoFences geoFencesTemp : geoFencesTempList){
										if (geoFencesTemp.getDeviceTag() == null || geoFencesTemp.getDeviceTag().isEmpty()){
											if (geoFencesIdSet.add(geoFencesTemp.getId())){
												geoFencesByNetworkIdList.add(geoFencesTemp);
											}
										}
									}
								} // end if (devGeoFencesObject.getGeoFencesList() != null)
							} // end if (devGeoFencesObject.getHasRetrievedOnceByNetwork())
						} else {
							List<GeoFences> geoFencesByNetworkIdTempList = getGeoFencesByNetworkIdButNoDeviceTagsFromDb(networkId, organizationId);
							if (geoFencesByNetworkIdTempList != null){
								for (GeoFences geoFencesTemp: geoFencesByNetworkIdTempList){
									if (geoFencesIdSet.add(geoFencesTemp.getId())){
										geoFencesByNetworkIdList.add(geoFencesTemp);
									}							
								}
							}
						}
					} // end for (Devices devices: devicesList)
				} // end if (devicesList != null)
			} else {
				log.warnf("GEO20140204 - getGeoFencesByNetworkIdButNoDeviceTags - networkId/ organizationID is/ are null");
			} // end if (networkId != null && organizationId != null) ... else ...
		}catch (Exception e){
			log.error("GEO20140204 - getGeoFencesByNetworkIdButNoDeviceTags - " + e, e);
		}
		return geoFencesByNetworkIdList;
	}
	
	private static List<GeoFences> getGeoFencesByNetworkIdButNoDeviceTagsFromDb(Integer networkId, String organizationId){
		List<GeoFences> geoFencesByNetworkIdList = null;
		try{
			if (networkId != null && organizationId != null){
				List<Devices> devicesList = NetUtils.getDeviceLstByNetId(organizationId, networkId);
				GeoFencesDAO geoFencesDao = new GeoFencesDAO(organizationId);
				if (devicesList != null){
					for (Devices devices: devicesList){
						
						geoFencesByNetworkIdList = geoFencesDao.getGeoFencesListByNetworkIdButNoDeviceTag(networkId);
						if (geoFencesByNetworkIdList != null){
							geoFencesByNetworkIdList = fillGeoFencePointsList(geoFencesByNetworkIdList, organizationId);
							DevGeoFencesObject devGeoFencesObject = new DevGeoFencesObject();							
							devGeoFencesObject.setIana_id(devices.getIanaId());
							devGeoFencesObject.setSn(devices.getSn());
							devGeoFencesObject.setDevice_id(devices.getId());
							
							devGeoFencesObject = GeoFencesUtils.getDevGeoFencesObjectFromCache(devGeoFencesObject);
							
							if (devGeoFencesObject == null){
								devGeoFencesObject = new DevGeoFencesObject();
								devGeoFencesObject.setIana_id(devices.getIanaId());
								devGeoFencesObject.setSn(devices.getSn());
								devGeoFencesObject.setDevice_id(devices.getId());
							}
							
							// for caching the geofences object
							for (GeoFences geoFences: geoFencesByNetworkIdList){
								devGeoFencesObject.addFromGeoFences(geoFences);
							}
							devGeoFencesObject.setHasRetrievedOnceByNetwork(true);
							GeoFencesUtils.setDevGeoFences2Cache(devGeoFencesObject);
													
						}
					}
				}
			}
		}catch (Exception e){
			log.error("GEO20140204 - getGeoFencesByNetworkIdButNoDeviceTagsFromDb - " + e, e);
		}
		
		return geoFencesByNetworkIdList;
	}
	
	
	private static List<GeoFences> getGeoFencesByDeviceTagsList(Devices devices, List<DeviceTags> deviceTagsList, String organizationId){
		List<GeoFences> geoFencesList = null;
		Set<Integer> geoFencesIdSet = new HashSet<Integer>();
		try{

			if (devices != null && devices.getIanaId() != null && devices.getSn() != null){
				DevGeoFencesObject devGeoFencesObject = new DevGeoFencesObject();
				devGeoFencesObject.setIana_id(devices.getIanaId());
				devGeoFencesObject.setSn(devices.getSn());
				devGeoFencesObject.setDevice_id(devices.getId());
				
				// try to get devGeoFencesObject from Pool
				devGeoFencesObject = getDevGeoFencesObjectFromCache(devGeoFencesObject);
				
				if (devGeoFencesObject != null){ // get from cache
					if (devGeoFencesObject.getHasRetrievedOnceByTag()){
						if (devGeoFencesObject.getGeoFencesList() != null){
							List<GeoFences> geoFencesTempList = devGeoFencesObject.getGeoFencesList();
							geoFencesList = new ArrayList<GeoFences>();
							for (GeoFences geoFencesTemp : geoFencesTempList){
								if (geoFencesTemp.getDeviceTag() != null && !geoFencesTemp.getDeviceTag().isEmpty()){
									if (geoFencesIdSet.add(geoFencesTemp.getId())){
										geoFencesList.add(geoFencesTemp);
									}
								}
							}
						}
					} else { // not yet get from db, do it now.
						geoFencesList = getGeoFencesByDeviceTagsListFromDb(devices, deviceTagsList, organizationId);
						devGeoFencesObject.setIana_id(devices.getIanaId());
						devGeoFencesObject.setSn(devices.getSn());
						devGeoFencesObject.setDevice_id(devices.getId());
						// for caching the geofences object
						for (GeoFences geoFences: geoFencesList){
							if (geoFencesIdSet.add(geoFences.getId())){
								devGeoFencesObject.addFromGeoFences(geoFences);
							}
						}
						devGeoFencesObject.setHasRetrievedOnceByTag(true);
						GeoFencesUtils.setDevGeoFences2Cache(devGeoFencesObject);
						
					}

				} else { // get from db
					geoFencesList = getGeoFencesByDeviceTagsListFromDb(devices, deviceTagsList, organizationId);
					devGeoFencesObject = new DevGeoFencesObject();
					devGeoFencesObject.setKey(devices.getIanaId(), devices.getSn());
					devGeoFencesObject.setDevice_id(devices.getId());
					// for caching the geofences object
					for (GeoFences geoFences: geoFencesList){
						if (geoFencesIdSet.add(geoFences.getId())){
							devGeoFencesObject.addFromGeoFences(geoFences);
						}
					}
					devGeoFencesObject.setHasRetrievedOnceByTag(true);
					GeoFencesUtils.setDevGeoFences2Cache(devGeoFencesObject);
				}
			} // end if (devices != null && devices.getIanaId() != null && devices.getSn() != null)
		}catch (Exception e){
			log.error("GEO20140204 - getGeoFencesByTagIds - " + e, e);
		}
		return geoFencesList;
	}
	
	private static List<GeoFences> getGeoFencesByDeviceTagsListFromDb(Devices devices, List<DeviceTags> deviceTagsList, String organizationId){
		List<GeoFences> geoFencesList = null;
		Set<Integer> geoFencesIdSet = new HashSet<Integer>();
		try{
			geoFencesList = new ArrayList<GeoFences>();
			GeoFencesDAO geoFencesDao = new GeoFencesDAO(organizationId);
			if (deviceTagsList != null){
				for (DeviceTags deviceTags: deviceTagsList){
					List<GeoFences> loopGeoFencesList = geoFencesDao.getGeoFencesListByTagId(deviceTags.getTagId());
					if (loopGeoFencesList != null){

						for (GeoFences loopGeoFences: loopGeoFencesList){
							if(geoFencesIdSet.add(loopGeoFences.getId())){
								geoFencesList.add(loopGeoFences);
							}
						}
					} // end if (loopGeoFencesList != null)
				} // end for (DeviceTags deviceTags: deviceTagsList)
				
				if (geoFencesList != null){
					geoFencesList = fillGeoFencePointsList(geoFencesList, organizationId);							
				} // end if (geoFencesList != null)
				
			} // end if (deviceTagsList != null)
		} catch (Exception e){
			log.error("GEO20140204 - getGeoFencesByDeviceTagsListFromDb - " + e, e);
		}
		return geoFencesList;
	}
	
	// check and get from cache first and then get from db
	private static List<GeoFences> getGeoFences(Devices devices, String organizationId){
		List<GeoFences> geoFencesList = null;
		try{
			if (devices != null && devices.getIanaId() != null && devices.getSn() != null){
				DevGeoFencesObject devGeoFencesObject = new DevGeoFencesObject();
				devGeoFencesObject.setKey(devices.getIanaId(), devices.getSn());
				devGeoFencesObject.setDevice_id(devices.getId());
				
				// try to get devGeoFencesObject from Pool
				devGeoFencesObject = GeoFencesUtils.getDevGeoFencesObjectFromCache(devGeoFencesObject);
				
				if (devGeoFencesObject != null){ // get from cache
					if (!devGeoFencesObject.getHasRetrievedOnceByNetwork() || !devGeoFencesObject.getHasRetrievedOnceByTag()){
						geoFencesList = GeoFencesUtils.getGeoFencesByComboNetworkIdOrDeviceTags(devices, organizationId);
					} else {
						geoFencesList = devGeoFencesObject.getGeoFencesList();
					}
				} else { // get from db
//					devices = NetUtils.getDevices(organizationId, networkId, devices.getIanaId(), devices.getSn());
					if (devices.getId() != null){
						geoFencesList = GeoFencesUtils.getGeoFencesByComboNetworkIdOrDeviceTags(devices, organizationId);
							
					} // end if (devices.getId() != null)
				}// end if (devGeoFencesObject != null) ... else ...
			} // end if (devices != null && devices.getIanaId() != null && devices.getSn() != null)
		}catch (Exception e){
			log.error("GEO20140204 - getGeoFences - " + e, e);
		} // end try ... catch ...
		return geoFencesList;
	} // end getGeoFencesByDeviceId()
	
	private static List<GeoFences> fillGeoFencePointsList(List<GeoFences> geoFencesList, String organizationId){
		try{
			if (geoFencesList != null){
				GeoFencePointsDAO geoFencePointsDao = new GeoFencePointsDAO(organizationId);					
				
				for (GeoFences geoFences: geoFencesList){
					List<GeoFencePoints> geoFencePointsList = geoFencePointsDao.getGeoFencePointsListByGeoId(geoFences.getId());
					geoFences.setGeoFencePointsList(geoFencePointsList);				
				} // end for (GeoFences geoFences: geoFencesList)
			}
		} catch (Exception e){
			
		}
		return geoFencesList;
	}
	
	public static List<Json_Geo_Fences> getJsonGeoFencesListFromCache(Integer networkId, String organizationId){
		List<Json_Geo_Fences> jsonGeoFencesList = new ArrayList<Json_Geo_Fences>();
		try{
			if (organizationId != null){				
				if (networkId != null){
					List<Devices> devicesList = NetUtils.getDeviceLstByNetId(organizationId, networkId);
					List<GeoFences> geoFencesList = null;
					if (devicesList != null){
						// each devices has its own list
						for (Devices devices: devicesList){
							geoFencesList = getGeoFences(devices, organizationId);
							if (geoFencesList != null){								
								jsonGeoFencesList.addAll(convert2JsonGeoFencesList(geoFencesList, organizationId));
							} // end if (geoFencesList != null)
						} // end for (Devices devices: devicesList)
					} // end if (devicesList != null)
				} // end if (param_network_id != null)
			} // end if (param_organization_id != null)
		} catch (Exception e){
			log.error("GEO20140204 - getJsonGeoFencesList - " + e, e);
			return null;
		} // end try ... catch ...
		return jsonGeoFencesList;
	} // end getJsonGeoFencesList
	
	public static GeoFences convert2GeoFences(JsonGeoFencesRequest jsonNetReqGeoFences){
		GeoFences geoFences = new GeoFences();
		try{			
			if (jsonNetReqGeoFences != null){
				geoFences.setDeviceId(jsonNetReqGeoFences.getDevice_id());
				geoFences.setNetworkId(jsonNetReqGeoFences.getNetwork_id());
				geoFences.setSpeedLimit(jsonNetReqGeoFences.getSpeed_limit());
				geoFences.setType(jsonNetReqGeoFences.getType());
				geoFences.setZoneName(jsonNetReqGeoFences.getZone_name());
				geoFences.setId(jsonNetReqGeoFences.getId());				
				geoFences.setDeviceTag(jsonNetReqGeoFences.getDevice_tag());
				geoFences.setEmailNotify(jsonNetReqGeoFences.getEmail_notify());
				geoFences.setCreatedAt((int) (DateUtils.getUtcDate().getTime() / 1000));
				geoFences.setEnabled(jsonNetReqGeoFences.getEnabled());
				if (jsonNetReqGeoFences.getGeo_fence_points_list() != null){
					List <GeoFencePoints> geoFencePointsList = new ArrayList<GeoFencePoints>();
					for (JsonGeoFencesRequest.JsonGeoFencesRequest_GeoFencePoints jsonReq_geoFencePoints: jsonNetReqGeoFences.getGeo_fence_points_list()){
						GeoFencePoints geoFencePoints = new GeoFencePoints();
						geoFencePoints.setGeoId(geoFences.getId());
						geoFencePoints.setPointGroupId(jsonReq_geoFencePoints.getPoint_group_id());
						geoFencePoints.setLatitude(jsonReq_geoFencePoints.getLatitude());
						geoFencePoints.setLongitude(jsonReq_geoFencePoints.getLongitude());
						geoFencePoints.setPointSeq(jsonReq_geoFencePoints.getPoint_seq());
						geoFencePoints.setId(jsonReq_geoFencePoints.getId());
						geoFencePoints.setRadius(jsonReq_geoFencePoints.getRadius());
						geoFencePointsList.add(geoFencePoints);
					} // end for (JsonNetworkRequest_GeoFences.JsonNetworkRequest_GeoFencePoints jsonReq_geoFencePoints: request.getGeo_fence_points_list())
					geoFences.setGeoFencePointsList(geoFencePointsList);
				} // end if (request.getGeo_fence_points_list() != null)
			} // end if if (jsonNetReqGeoFences != null)

		}catch (Exception e){
			log.error("GEO20140204 - convert2GeoFences: ", e);
			return null;
		} // end try ... catch ...
		
		return geoFences;
	}// end convert2GeoFences()
	private static List<Json_Geo_Fences>convert2JsonGeoFencesList(List<GeoFences> geoFencesList, String organizationId){
		List<Json_Geo_Fences> jsonGeoFencesList = new ArrayList<Json_Geo_Fences>();
		if (geoFencesList != null){
			for (GeoFences geoFences: geoFencesList){
				Json_Geo_Fences jsonGeoFences = new Json_Geo_Fences();
				jsonGeoFences.setDevice_id(geoFences.getDeviceId());
				jsonGeoFences.setId(geoFences.getId());
				jsonGeoFences.setNetwork_id(geoFences.getNetworkId());
				jsonGeoFences.setSpeed_limit(geoFences.getSpeedLimit());
				jsonGeoFences.setType(geoFences.getType());
				jsonGeoFences.setZone_name(geoFences.getZoneName());
				jsonGeoFences.setDevice_tag(geoFences.getDeviceTag());
				jsonGeoFences.setEmail_notify(geoFences.getEmailNotify());
				jsonGeoFences.setCreated_at(geoFences.getCreatedAt());
				jsonGeoFences.setEnabled(geoFences.getEnabled());
														
				if (geoFences.getId() != null){
					List<GeoFencePoints> geoFencePointsList = geoFences.getGeoFencePointsList();
					if (geoFencePointsList != null){
						List<Json_Geo_Fence_Points> jsonGeoFencePointsList = new ArrayList<Json_Geo_Fence_Points>();
						for (GeoFencePoints geoFencePoints: geoFencePointsList){
							Json_Geo_Fence_Points jsonGeoFencePoints = new Json_Geo_Fence_Points();
							jsonGeoFencePoints.setGeo_id(geoFencePoints.getGeoId());
							jsonGeoFencePoints.setPoint_group_id(geoFencePoints.getPointGroupId());
							jsonGeoFencePoints.setId(geoFencePoints.getId());
							jsonGeoFencePoints.setLatitude(geoFencePoints.getLatitude());
							jsonGeoFencePoints.setLongitude(geoFencePoints.getLongitude());
							jsonGeoFencePoints.setRadius(geoFencePoints.getRadius());
							jsonGeoFencePoints.setPoint_seq(geoFencePoints.getPointSeq());
							jsonGeoFencePointsList.add(jsonGeoFencePoints);
						} // end for (GeoFencePoints geoFencePoints: geoFencePointsList)
						jsonGeoFences.setGeo_fence_points_list(jsonGeoFencePointsList);								
					} // end if (geoFencePointsList != null)
				} // end if (geoFences.getId() != null)
				jsonGeoFencesList.add(jsonGeoFences);
			} // end for (GeoFences geofences: geoFencesList)
		} // end if (geoFencesList != null)
		return jsonGeoFencesList;
	} // end List<Json_Geo_Fences>convert2JsonGeoFencesList()
	
	public static List<Json_Geo_Fences> getJsonGeoFencesList(Integer networkId, String organizationId){
		List<Json_Geo_Fences> jsonGeoFencesList = new ArrayList<Json_Geo_Fences>();
		Set<Integer> geoFencesSet = new HashSet<Integer>();
		try{
			if (organizationId != null && networkId != null){
				List<Devices> devicesList = NetUtils.getDeviceLstByNetId(organizationId, networkId);
				
				if (devicesList != null){
					for (Devices devices: devicesList){
						List<GeoFences> geoFencesList = new ArrayList<GeoFences>();
						List<GeoFences> geoFencesTempList = GeoFencesUtils.getGeoFences(devices, organizationId);
						if (geoFencesTempList != null){
							DevGeoFencesObject devGeoFencesObject = new DevGeoFencesObject();
							devGeoFencesObject.setIana_id(devices.getIanaId());
							devGeoFencesObject.setSn(devices.getSn());
							devGeoFencesObject.setDevice_id(devices.getId());
							
							devGeoFencesObject = GeoFencesUtils.getDevGeoFencesObjectFromCache(devGeoFencesObject);
							
							if (devGeoFencesObject == null){
								devGeoFencesObject = new DevGeoFencesObject();
								devGeoFencesObject.setIana_id(devices.getIanaId());
								devGeoFencesObject.setSn(devices.getSn());
								devGeoFencesObject.setDevice_id(devices.getId());
							}
							
							for (GeoFences geoFencesTemp: geoFencesTempList){
								if (geoFencesSet.add(geoFencesTemp.getId())){
									devGeoFencesObject.addFromGeoFences(geoFencesTemp);
									geoFencesList.add(geoFencesTemp);
								}
							}
							GeoFencesUtils.setDevGeoFences2Cache(devGeoFencesObject);
							List<Json_Geo_Fences> jsonGeoFencesReturnList = convert2JsonGeoFencesList(geoFencesList, organizationId);
							jsonGeoFencesList.addAll(jsonGeoFencesReturnList);
						} // end if (geoFencesList != null)
					} // end for (Devices devices: devicesList)
				} // if (devicesList != null)
			} // end if (param_organization_id != null) 
			else {
				log.warn("GEO20140204 - getJsonGeoFencesList - networkId, organizationId - null value, cannot process the function!");
			}
		} catch (Exception e){
			log.error("GEO20140204 - getJsonGeoFencesList - " + e, e);
			return null;
		} // end try ... catch ...
		return jsonGeoFencesList;
	} // end getJsonGeoFencesList
	
	
	
} // end class
