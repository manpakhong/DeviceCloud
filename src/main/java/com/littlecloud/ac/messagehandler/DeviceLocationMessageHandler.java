package com.littlecloud.ac.messagehandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.jboss.logging.Logger;

import com.littlecloud.ac.messagehandler.util.GeoUtils;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.EventLogDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.GeoFences;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.report.EventLog;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.criteria.SendGeoFencesAlertCriteria;
import com.littlecloud.dtos.json.RequestGeoFencesTemplateDataDto;
import com.littlecloud.dtos.json.RequestGeoFencesTemplateDto;
import com.littlecloud.pool.object.DevGeoFencesObject;
import com.littlecloud.pool.object.DevGeoFencesObject.GeoFencePointsObject;
import com.littlecloud.pool.object.DevGeoFencesObject.GeoFencesObject;
import com.littlecloud.pool.object.DevLocationsObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.LocationList;
import com.littlecloud.pool.object.utils.GeoFencesUtils;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;
import com.littlecloud.services.AlertMgr;
import com.littlecloud.services.RailsMailMgr;

public class DeviceLocationMessageHandler {

	// GpsLocation in cache is the most latest location - update every 5 sec
	// DevLastLocation object - update every 10 mins
	private static final int SEND_EMAIL_ALERT_LEVEL = 1;
	private static final Logger log = Logger.getLogger(DeviceLocationMessageHandler.class);
	private static final boolean IS_STOP_EMAIL_ALERT = false; // for geo_fences to stop email alert
	
	
	public static boolean doDeviceLocationMessageHandler(DevOnlineObject devOnlineO){
		boolean isSuccess = false;
		try{
			if (devOnlineO != null){
				if (log.isDebugEnabled()){
					log.debugf("GEO20140204(0a) - doDeviceLocationMessageHandler(): devId: %s, orgId: %s, netId: %s, devOnlineO isNull?: %s", devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id(), "false");	
				}
				DevLocationsObject devLocationsObject = new DevLocationsObject();
				devLocationsObject.setIana_id(devOnlineO.getIana_id());
				devLocationsObject.setSn(devOnlineO.getSn());
	
				devLocationsObject = ACUtil.<DevLocationsObject> getPoolObjectBySn(devLocationsObject, DevLocationsObject.class);
				
				LocationList location = null; 
				if (devLocationsObject != null && devLocationsObject.getLocation_list() != null){
					List<LocationList >lastLocationList = devLocationsObject.getLocation_list();
					if (lastLocationList != null && lastLocationList.size() > 0 && lastLocationList.get(0) != null){
						location = lastLocationList.get(0);
					}
				} // end if (devLastLocObject != null)
				
				if (location != null && location.getLatitude() != null && location.getLongitude() != null){
					detectAndDoInZoneActions(devOnlineO, location);	

					if (location.getSpeed() != null){
						detectAndDoOverspeedActions(devOnlineO, location);
					} else {
						log.warnf("GEO20140204(Overspeed) - doDeviceLocationMessageHandler - devId: %s, orgId: %s location.getSpeed() is null ?:%s", devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), location.getSpeed() == null);	
					}

				}
			} // end if (devOnlineO != null)
			else {
				log.warnf("GEO20140204(0b) - doDeviceLocationMessageHandler(): devOnlineO isNull?: %s", "true");	
			}
		} catch (Exception e){
			log.error("GEO20140204(0b) - doDeviceLocationMessageHandler()",e);
		}
		return isSuccess;
	} // end doDeviceLocationMessageHandler
	
	private static void detectAndDoInZoneActions(DevOnlineObject devOnlineO, LocationList location){
		try{
			if (devOnlineO != null){
				if (location != null){
					if (devOnlineO.getOrganization_id() != null && devOnlineO.getDevice_id() != null){

						
						Devices devices = NetUtils.getDevices(devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), false); // don't trigger reload and just let it failed
						if (devices != null){
							List<GeoFences> geoFencesList = GeoFencesUtils.getGeoFencesByComboNetworkIdOrDeviceTags(devices, devOnlineO.getOrganization_id());
							Networks networks = OrgInfoUtils.getNetwork(devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());
							if (geoFencesList != null && geoFencesList.size() > 0){
								DevGeoFencesObject devGeoFencesObject = new DevGeoFencesObject();
								devGeoFencesObject.setIana_id(devOnlineO.getIana_id());
								devGeoFencesObject.setSn(devOnlineO.getSn());
								
								devGeoFencesObject = GeoFencesUtils.getDevGeoFencesObjectFromCache(devGeoFencesObject);
																
								if (devGeoFencesObject != null){
									if (log.isDebugEnabled()){
										log.debugf("GEO20140204(In1) - detectAndDoInZoneActions() - devGeoFencesObject not null : devId: %s, orgId: %s, netId: %s, devOnlineO isNull?: %s", devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id(), "false");	
									}
									List<GeoFencesObject> geoFencesObjectList = devGeoFencesObject.getGeoFencesObjectList();
									if (geoFencesObjectList != null){
										if (log.isDebugEnabled()){
											log.debugf("GEO20140204(In2) - detectAndDoInZoneActions() - geoFencesObjectList.size(): %s,devId: %s, orgId: %s, netId: %s",geoFencesObjectList.size(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());	
										}
										for (GeoFencesObject geoFencesObject: geoFencesObjectList){
											if (geoFencesObject.getEnabled() != null && geoFencesObject.getEnabled()){
												boolean isInZonesCircleType = false;
												boolean isInZonesPolygonType = false;
												if (geoFencesObject.getType().equalsIgnoreCase(GeoFences.GEO_FENCES_TYPE_CIRCLE)){
													if (log.isDebugEnabled()){
														log.debugf("GEO20140204(In3a) - detectAndDoInZoneActions() - geoFencesId: %s, zone name: %s, type: %s,devId: %s, orgId: %s, netId: %s",geoFencesObject.getId(), geoFencesObject.getZoneName(), geoFencesObject.getType(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());
													}
													List<GeoFencePointsObject> geoFencePointsObjectList = geoFencesObject.getGeoFencePointsObjectList();
													if (geoFencePointsObjectList != null){
														float [] latsArray = new float[geoFencePointsObjectList.size()];
														float [] lngsArray = new float[geoFencePointsObjectList.size()];
														int [] radiusArray = new int[geoFencePointsObjectList.size()];
														if (log.isDebugEnabled()){
															log.debugf("GEO20140204(In4a) - detectAndDoInZoneActions() - geoFencePointsObjectList.size(): %s, geoFencesId: %s, zone name: %s, type: %s,devId: %s, orgId: %s, netId: %s",geoFencePointsObjectList.size(), geoFencesObject.getId(), geoFencesObject.getZoneName(), geoFencesObject.getType(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());
														}
														for (int i = 0; i < geoFencePointsObjectList.size(); i++){													
															GeoFencePointsObject geoFencePointsObject = geoFencePointsObjectList.get(i);
		
															latsArray[i] =geoFencePointsObject.getLatitude();
															lngsArray[i] = geoFencePointsObject.getLongitude();
															radiusArray[i] = geoFencePointsObject.getRadius();
															if (log.isDebugEnabled()){
																log.debugf("GEO20140204 (In5a) - detectAndDoInZoneActions() - lat:%s, log:%s, radius:%s, lat:%s, log:%s, isInZoneCircleType: %s, isInZonesPolygonType: %s, geoFencesId: %s, zone name: %s, type: %s,devId: %s, orgId: %s, netId: %s",geoFencePointsObject.getLatitude(),geoFencePointsObject.getLongitude(),geoFencePointsObject.getRadius(), location.getLatitude(), location.getLongitude(), isInZonesCircleType, isInZonesPolygonType, geoFencesObject.getId(), geoFencesObject.getZoneName(), geoFencesObject.getType(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());	
															}
														}
		
														if (GeoUtils.isIntersectsCircle(latsArray, lngsArray, radiusArray, location.getLatitude(), location.getLongitude())){
															isInZonesCircleType = true;
														} // end if (isIntersectsCircle())
													} // end if (geoFencePointsList != null)
												} // end if (geoFences.getType().equalsIgnoreCase(GeoFences.GEO_FENCES_TYPE_CIRCLE))
												if (geoFencesObject.getType().equalsIgnoreCase(GeoFences.GEO_FENCES_TYPE_POLYGON)){
													if (log.isDebugEnabled()){
														log.debugf("GEO20140204(In3b) - detectAndDoInZoneActions() - geoFencesId: %s, zone name: %s, type: %s,devId: %s, orgId: %s, netId: %s",geoFencesObject.getId(), geoFencesObject.getZoneName(), geoFencesObject.getType(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());
													}
													List<GeoFencePointsObject> geoFencePointsObjectList =  geoFencesObject.getGeoFencePointsObjectList();
													if (geoFencePointsObjectList != null){
														float [] latsArray = new float[geoFencePointsObjectList.size()];
														float [] lngsArray = new float[geoFencePointsObjectList.size()];
														int [] radiusArray = new int[geoFencePointsObjectList.size()];
														if (log.isDebugEnabled()){
															log.debugf("GEO20140204(In4b) - detectAndDoInZoneActions() - geoFencePointsObjectList.size(): %s, geoFencesId: %s, zone name: %s, type: %s,devId: %s, orgId: %s, netId: %s",geoFencePointsObjectList.size(), geoFencesObject.getId(), geoFencesObject.getZoneName(), geoFencesObject.getType(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());				
														}
														
														for (int i = 0; i < geoFencePointsObjectList.size(); i++){													
															GeoFencePointsObject geoFencePointsObject = geoFencePointsObjectList.get(i);
		
															latsArray[i] =geoFencePointsObject.getLatitude();
															lngsArray[i] = geoFencePointsObject.getLongitude();
															radiusArray[i] = geoFencePointsObject.getRadius();
															if (log.isDebugEnabled()){
																log.debugf("GEO20140204 (In5b) - detectAndDoInZoneActions() - lat:%s, log:%s, radius:%s, lat:%s, log:%s, isInZoneCircleType: %s, isInZonesPolygonType: %s, geoFencesId: %s, zone name: %s, type: %s,devId: %s, orgId: %s, netId: %s",geoFencePointsObject.getLatitude(),geoFencePointsObject.getLongitude(),geoFencePointsObject.getRadius(), location.getLatitude(), location.getLongitude(), isInZonesCircleType, isInZonesPolygonType, geoFencesObject.getId(), geoFencesObject.getZoneName(), geoFencesObject.getType(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());
															}
		
														}
														
														if (GeoUtils.isIntersectsPolygon(latsArray, lngsArray, radiusArray, location.getLatitude(), location.getLongitude())){
															isInZonesPolygonType = true;
														} // end if (isIntersectsCircle())
														
													} // end if (geoFencePointsList != null)
												} // end if (geoFences.getType().equalsIgnoreCase(GeoFences.GEO_FENCES_TYPE_POLYGON))
												
												/* combination
												 * offZone -> inZone - write EventLog, send mail
												 * inZone -> inZone - do nothing
												 * inZone -> offZone - write EventLog, send mail
												 */
												if (log.isDebugEnabled()){
													log.debugf("GEO20140204 (In6) - detectAndDoInZoneActions() - isInZoneCircleType: %s, isInZonesPolygonType: %s, geoFencesId: %s, zone name: %s, type: %s,devId: %s, orgId: %s, netId: %s",isInZonesCircleType, isInZonesPolygonType, geoFencesObject.getId(), geoFencesObject.getZoneName(), geoFencesObject.getType(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());	
												}
												if (isInZonesCircleType || isInZonesPolygonType){ // inZone
													if (geoFencesObject.getIsInZone() == null || !geoFencesObject.getIsInZone()){ // from offZone -> inZone
														geoFencesObject.setIsInZone(true);
														Integer inZoneTime = DateUtils.getUnixtime();
														geoFencesObject.setInZoneTime(inZoneTime);
														geoFencesObject.setOffZoneTime(null);
														devGeoFencesObject.updateGeoFencesObject(geoFencesObject);
														
														writeGpsLocation2EventLog(EventLog.EVENT_TYPE_FLEET_MESSAGE_IN_ZONE, location, devOnlineO, geoFencesObject, null);
														if (!IS_STOP_EMAIL_ALERT && geoFencesObject.getEmailNotify() != null && geoFencesObject.getEmailNotify()){
															RequestGeoFencesTemplateDto requestGeoFencesTemplateDto = prepareReqTemplate(RailsMailMgr.ALERT_TYPE_GEO_FENCES_ENTERED_ZONE, location, devOnlineO, geoFencesObject, null);
															SendGeoFencesAlertCriteria criteria = new SendGeoFencesAlertCriteria();
															
															if (networks!= null){
																criteria.setNetworkId(networks.getId());
															}
															criteria.setMsgType(SendGeoFencesAlertCriteria.MSG_TYPE_EMAIL);
															criteria.setOrgId(devOnlineO.getOrganization_id());
															criteria.setDevId(devOnlineO.getDevice_id());
															criteria.setLevel(1);
															
															AlertMgr alertMgr = new AlertMgr();
															alertMgr.sendGeoFencesAlert(criteria, requestGeoFencesTemplateDto);
														}
	//													GeoFencesUtils.setDevGeoFences2Cache(devGeoFencesObject);
	
													}
												} else { // offZone
		
													if (geoFencesObject.getIsInZone() != null && geoFencesObject.getIsInZone()){ // from inZone -> offZone
														geoFencesObject.setIsInZone(false);
														Integer offZoneTime = DateUtils.getUnixtime();
														geoFencesObject.setOffZoneTime(offZoneTime);
														devGeoFencesObject.updateGeoFencesObject(geoFencesObject);
														writeGpsLocation2EventLog(EventLog.EVENT_TYPE_FLEET_MESSAGE_OUT_ZONE,location, devOnlineO, geoFencesObject, null);											
														if (!IS_STOP_EMAIL_ALERT && geoFencesObject.getEmailNotify() != null && geoFencesObject.getEmailNotify()){
															RequestGeoFencesTemplateDto requestGeoFencesTemplateDto = prepareReqTemplate(RailsMailMgr.ALERT_TYPE_GEO_FENCES_LEFT_ZONE,location, devOnlineO, geoFencesObject, null);
															SendGeoFencesAlertCriteria criteria = new SendGeoFencesAlertCriteria();
															
															if (networks!= null){
																criteria.setNetworkId(networks.getId());
															}
															criteria.setMsgType(SendGeoFencesAlertCriteria.MSG_TYPE_EMAIL);
															criteria.setOrgId(devOnlineO.getOrganization_id());
															criteria.setDevId(devOnlineO.getDevice_id());
															criteria.setLevel(1);
															
															AlertMgr alertMgr = new AlertMgr();
															alertMgr.sendGeoFencesAlert(criteria, requestGeoFencesTemplateDto);
														}
	//													GeoFencesUtils.setDevGeoFences2Cache(devGeoFencesObject);
													}
												} // end if (isInZonesCircleType || isInZonesPolygonType) ... else ...
											} // end if geoFencesObject.getEnabled() != null && geoFencesObject.getEnabled())								
										} // end for (GeoFences geoFences: geoFencesList)
									} // end if (geoFencesObjectList != null)
								} // end if (devGeoFencesObject != null)
								GeoFencesUtils.setDevGeoFences2Cache(devGeoFencesObject);
							} // end if (geoFencesList != null && geoFencesList.size() > 0){
							if (log.isDebugEnabled()){
								log.debugf("GEO20140204 (In7) - detectAndDoInZoneActions() - devId: %s, orgId: %s, netId: %s", devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());	
							}
						} // end if (devices != null)
					} // end if (devOnlineO.getDevice_id() != null)
				} // end if (gpsLocation() != null)
			} // end if (devOnlineO != null)
		} catch (Exception e){
			log.error("GEO20140204 (Ine1) - detectAndDoInZoneActions - isDeviceInTheZones:", e);
		} // end try ... catch ...
	} // end detectAndDoInZoneActions
		
	private static void detectAndDoOverspeedActions(DevOnlineObject devOnlineO, LocationList location){
		try{
			// mark DeviceLastLocObject.LocationList.is_enter_the_zone to true
			if (devOnlineO != null && location != null){
				Devices devices = NetUtils.getDevices(devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), false);	// don't trigger reload and let if failed here
				if (log.isDebugEnabled()){
					log.debugf("GEO20140204 (OverSpeed1) - detectAndDoOverspeedActions() - devId: %s, orgId: %s, netId: %s", devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());	
				}
					
				if (devices != null){
					
					DevGeoFencesObject devGeoFencesObj = new DevGeoFencesObject();											
					devGeoFencesObj = GeoFencesUtils.getDevGeoFencesObjectByComboNetworkIdOrDeviceTag(devices, devOnlineO.getNetwork_id(), devOnlineO.getOrganization_id());
					if (log.isDebugEnabled()){
						log.debugf("GEO20140204 (OverSpeed1-1) - detectAndDoOverspeedActions() - devGeoFencesObject is null?: %s, devId: %s, orgId: %s, netId: %s",(devGeoFencesObj == null),devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());	
					}
					Networks networks = OrgInfoUtils.getNetwork(devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());
					if (devGeoFencesObj != null && location != null){
						List<GeoFencesObject> geoFencesObjectList = devGeoFencesObj.getGeoFencesObjectList();
						if (geoFencesObjectList != null){
							/*
							 * combination
							 * inZone - notOverSpeedLimit -> overSpeedLimit : write event log, send mail * (1)
							 * inZone - notOverSpeedLimit -> notOverSpeedLimit : do nothing
							 * inZone - overSpeedLimit -> notOverSpeedLimit: write event log, send mail * (2)
							 * inZone - overSpeedLimit -> overSpeedLimit: do nothing
							 * offZone - overSpeedLimit -> overSpeedLimit: change overSpeedLimit to notOverSpeedLimit, write event log, send mail * (3)
							 * offZone - overSpeedLimit -> notOverSpeedLimit: change overSpeedLimit to notOverSpeedLimit, write event log, send mail * (4)
							 * offZone - notOverSpeedLimit -> notOverSpeedLimit: do nothing
							 * offZone - notOverSpeedLimit -> overSpeedLimit: do nothing
							 */ 
							Float currentSpeed = location.getSpeed();
							if (log.isDebugEnabled()){
								log.debugf("GEO20140204 (OverSpeed1-2) - detectAndDoOverspeedActions() - currentSpeed: %s, devId: %s, orgId: %s, netId: %s",currentSpeed,devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());	
							}
							for (GeoFencesObject geoFencesObject: geoFencesObjectList){
								if (geoFencesObject.getEnabled() != null && geoFencesObject.getEnabled()){

									boolean isOverSpeedLimit = (currentSpeed > geoFencesObject.getSpeedLimit());
									if (log.isDebugEnabled()){
										log.debugf("GEO20140204 (OverSpeed1-3) - detectAndDoOverspeedActions() - isOverSpeedLimit? %s, speedLimit: %s currentSpeed: %s, devId: %s, orgId: %s, netId: %s",(currentSpeed > geoFencesObject.getSpeedLimit()), geoFencesObject.getSpeedLimit(),currentSpeed,devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());	
									}
									if (geoFencesObject.getIsInZone() != null && geoFencesObject.getIsInZone()){ // inZone
										if (isOverSpeedLimit){ // change to overSpeedLimit
											if (geoFencesObject.getIsOverSpeedLimit() == null || !geoFencesObject.getIsOverSpeedLimit()){ // notOverSpeedLimit -> overSpeedLimit * (1)
												geoFencesObject.setIsOverSpeedLimit(true);
												Integer overSpeedTime = DateUtils.getUnixtime();												
												geoFencesObject.setOverSpeedTime(overSpeedTime);
												geoFencesObject.setOverSpeedFigure(currentSpeed);
												geoFencesObject.setMaxOverSpeedFigure(currentSpeed);
												
												devGeoFencesObj.updateGeoFencesObject(geoFencesObject);
												if (log.isDebugEnabled()){
													log.debugf("GEO20140204 (OverSpeed2a) - detectAndDoOverspeedActions() - change to overSpeed- maxSpeed: %s, currentSpeed: %s, speedLimit: %s, devId: %s, orgId: %s, netId: %s",currentSpeed,currentSpeed,geoFencesObject.getIsOverSpeedLimit(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());	
												}
	
												writeGpsLocation2EventLog(EventLog.EVENT_TYPE_FLEET_MESSAGE_OVER_SPEED_CAUGHT, location, devOnlineO, geoFencesObject, currentSpeed);
												if (!IS_STOP_EMAIL_ALERT && geoFencesObject.getEmailNotify() != null && geoFencesObject.getEmailNotify()){
													RequestGeoFencesTemplateDto requestGeoFencesTemplateDto = prepareReqTemplate(RailsMailMgr.ALERT_TYPE_GEO_FENCES_SPEED_LIMIT, location, devOnlineO, geoFencesObject, currentSpeed);
													SendGeoFencesAlertCriteria criteria = new SendGeoFencesAlertCriteria();
													
													if (networks!= null){
														criteria.setNetworkId(networks.getId());
													}
													criteria.setMsgType(SendGeoFencesAlertCriteria.MSG_TYPE_EMAIL);
													criteria.setOrgId(devOnlineO.getOrganization_id());
													criteria.setDevId(devOnlineO.getDevice_id());
													criteria.setLevel(1);
													
													AlertMgr alertMgr = new AlertMgr();
													alertMgr.sendGeoFencesAlert(criteria, requestGeoFencesTemplateDto);
												}
											} else { // if isOverSpeedLimit flag is true, check whether current maxOverSpeedFigure is smaller than currentSpeed
												if (geoFencesObject.getMaxOverSpeedFigure() != null){
													if (geoFencesObject.getMaxOverSpeedFigure() < currentSpeed){
														if (log.isDebugEnabled()){
															log.debugf("GEO20140204 (OverSpeed2b) - detectAndDoOverspeedActions() - change to overSpeed- maxSpeed: %s, currentSpeed: %s, speedLimit: %s, devId: %s, orgId: %s, netId: %s",geoFencesObject.getMaxOverSpeedFigure(),currentSpeed,geoFencesObject.getIsOverSpeedLimit(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());	
														}
														geoFencesObject.setMaxOverSpeedFigure(currentSpeed);
														devGeoFencesObj.updateGeoFencesObject(geoFencesObject);
													}
												}
											}
										} else { // change to notOverSpeedLimit
											if (geoFencesObject.getIsOverSpeedLimit() != null && geoFencesObject.getIsOverSpeedLimit()){ // overSpeedLimit -> notOverSpeedLimit * (2)
												geoFencesObject.setIsOverSpeedLimit(false);
												Integer normalSpeedTime = DateUtils.getUnixtime();		
												geoFencesObject.setNormalSpeedTime(normalSpeedTime);
												devGeoFencesObj.updateGeoFencesObject(geoFencesObject);
												if (log.isDebugEnabled()){
													log.debugf("GEO20140204 (OverSpeed2c) - detectAndDoOverspeedActions() - change to notOverSpeedLimit- maxSpeed: %s, currentSpeed: %s, speedLimit: %s, devId: %s, orgId: %s, netId: %s",geoFencesObject.getMaxOverSpeedFigure(),currentSpeed,geoFencesObject.getIsOverSpeedLimit(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());	
												}
	
												writeGpsLocation2EventLog(EventLog.EVENT_TYPE_FLEET_MESSAGE_OVER_SPEED_RESUME_NORMAL, location, devOnlineO, geoFencesObject, currentSpeed);
												if (!IS_STOP_EMAIL_ALERT && geoFencesObject.getEmailNotify() != null && geoFencesObject.getEmailNotify()){
													RequestGeoFencesTemplateDto requestGeoFencesTemplateDto = prepareReqTemplate(RailsMailMgr.ALERT_TYPE_GEO_FENCES_SPEED_NORMAL, location, devOnlineO, geoFencesObject, currentSpeed);
													SendGeoFencesAlertCriteria criteria = new SendGeoFencesAlertCriteria();
													
													if (networks!= null){
														criteria.setNetworkId(networks.getId());
													}
													criteria.setMsgType(SendGeoFencesAlertCriteria.MSG_TYPE_EMAIL);
													criteria.setOrgId(devOnlineO.getOrganization_id());
													criteria.setDevId(devOnlineO.getDevice_id());
													criteria.setLevel(1);
													
													AlertMgr alertMgr = new AlertMgr();
													alertMgr.sendGeoFencesAlert(criteria, requestGeoFencesTemplateDto);
												}
											}
										}
									} else { // offZone
										if (isOverSpeedLimit){ // current is overSpeed but offZone, need to set to normal
											if (geoFencesObject.getIsOverSpeedLimit() != null && geoFencesObject.getIsOverSpeedLimit()){ // overSpeedLimit -> overSpeedLimit * (3)
												geoFencesObject.setIsOverSpeedLimit(false);
												Integer normalSpeedTime = DateUtils.getUnixtime();		
												geoFencesObject.setNormalSpeedTime(normalSpeedTime);
												devGeoFencesObj.updateGeoFencesObject(geoFencesObject);
												if (log.isDebugEnabled()){
													log.debugf("GEO20140204 (OverSpeed2d) - detectAndDoOverspeedActions() - change to overSpeed but offZone- maxSpeed: %s, currentSpeed: %s, speedLimit: %s, devId: %s, orgId: %s, netId: %s",geoFencesObject.getMaxOverSpeedFigure(),currentSpeed,geoFencesObject.getIsOverSpeedLimit(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());	
												}
	
												writeGpsLocation2EventLog(EventLog.EVENT_TYPE_FLEET_MESSAGE_OVER_SPEED_RESUME_NORMAL, location, devOnlineO, geoFencesObject, currentSpeed);
												if (!IS_STOP_EMAIL_ALERT && geoFencesObject.getEmailNotify() != null && geoFencesObject.getEmailNotify()){
													RequestGeoFencesTemplateDto requestGeoFencesTemplateDto = prepareReqTemplate(RailsMailMgr.ALERT_TYPE_GEO_FENCES_LEFT_ZONE, location, devOnlineO, geoFencesObject, currentSpeed);	
													SendGeoFencesAlertCriteria criteria = new SendGeoFencesAlertCriteria();
													
													if (networks!= null){
														criteria.setNetworkId(networks.getId());
													}
													criteria.setMsgType(SendGeoFencesAlertCriteria.MSG_TYPE_EMAIL);
													criteria.setOrgId(devOnlineO.getOrganization_id());
													criteria.setDevId(devOnlineO.getDevice_id());
													criteria.setLevel(1);
													
													AlertMgr alertMgr = new AlertMgr();
													alertMgr.sendGeoFencesAlert(criteria, requestGeoFencesTemplateDto);
												}
											}
										} // end if (isOverSpeedLimit)
									} // end if (geoFencesObject.getIsInZone()) ... else ...
								} // end if (geoFencesObject.getEnabled() != null && geoFencesObject.getEnabled())
								else {
									if (log.isDebugEnabled()){
										log.debugf("GEO20140204 (OverSpeed) - detectAndDoOverspeedActions() geoFences(id): %s is disabled- change to overSpeed but offZone- maxSpeed: %s, currentSpeed: %s, speedLimit: %s, devId: %s, orgId: %s, netId: %s",geoFencesObject.getId(), geoFencesObject.getMaxOverSpeedFigure(),currentSpeed,geoFencesObject.getIsOverSpeedLimit(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());	
									}
								}
							} // end for (GeoFencesObject geoFencesObject: geoFencesObjectList)
						} // end if (geoFencesObjectList != null)
						
						GeoFencesUtils.setDevGeoFences2Cache(devGeoFencesObj);
					} // end if (devGeoFencesObj != null)
				} // end if (devices != null)

			} // end if (devOnlineO != null)
			else {
				if (log.isDebugEnabled()){
					log.debugf("GEO20140204 - detectAndDoOverspeedActions, either deviceOnline is null or location is null!!!");
				}
			}
		} catch (Exception e){
			log.error("GEO20140204 - isDevicesOverSpeed:", e);
		}
	} // end isDevicesOverSpeed	
	
	private static boolean writeGpsLocation2EventLog(String eventTypeTrunkMessage,LocationList location, DevOnlineObject devOnlineO, GeoFencesObject geoFencesObject, Float currentSpeed){
		boolean isWritten = false;
		try{
			if (devOnlineO != null && devOnlineO.getOrganization_id() != null && location != null){
				Float latitude = location.getLatitude();
				Float longtitude = location.getLongitude();
				if (devOnlineO.getNetwork_id() != null && devOnlineO.getOrganization_id() != null){
					Networks network = OrgInfoUtils.getNetwork(devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());
					
					Date curTime = DateUtils.getUtcDate();
					Date networkTime = DateUtils.offsetFromUtcTimeZoneId(curTime, network.getTimezone());
					EventLogDAO eventLogDao = new EventLogDAO(devOnlineO.getOrganization_id());
					EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), networkTime, (int)(curTime.getTime()/1000),
							devOnlineO.getDevice_id(), "", "", "", EventLog.EVENT_TYPE_FLEET, "");
//					eventLog.setLatitude(latitude);
//					eventLog.setLongtitude(longtitude);
					
					Devices devices = NetUtils.getDevices(devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), false);	// don't trigger reload and let it failed here
					Networks networks =  OrgInfoUtils.getNetwork(devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());			
					if (devices != null && networks != null){
						if (log.isDebugEnabled()){
							log.debugf("GEO20140204 (wlog1) - writeGpsLocation2EventLog(), type: %s -  geoFencesId: %s, zone name: %s, type: %s,devId: %s, orgId: %s, netId: %s",eventTypeTrunkMessage, geoFencesObject.getId(), geoFencesObject.getZoneName(), geoFencesObject.getType(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());
						}
						StringBuilder sb = new StringBuilder();
						
						if (eventTypeTrunkMessage.equals(EventLog.EVENT_TYPE_FLEET_MESSAGE_IN_ZONE)){
							sb.append(devices.getName() + " entered " + geoFencesObject.getZoneName());
							sb.append(" of " + networks.getName());
							sb.append(" (latitude:" + latitude + ", longitude:" + longtitude + ")");
							eventLog.setDetail(sb.toString());
						}  else if (eventTypeTrunkMessage.equals(EventLog.EVENT_TYPE_FLEET_MESSAGE_OUT_ZONE)){
							sb.append(devices.getName() + " left " + geoFencesObject.getZoneName());
							sb.append(" of " + networks.getName());
							sb.append(" (latitude:" + latitude + ", longitude:" + longtitude + ")");
							eventLog.setDetail(sb.toString());
						} else if (eventTypeTrunkMessage.equals(EventLog.EVENT_TYPE_FLEET_MESSAGE_OVER_SPEED_CAUGHT)){
							sb.append(devices.getName() + " exceed speed limit (");
							sb.append(geoFencesObject.getSpeedLimit() + ") on ");
							sb.append(geoFencesObject.getZoneName());
							sb.append(" of " + networks.getName());
							sb.append(" (latitude:" + latitude + ", longitude:" + longtitude + ")");
							eventLog.setDetail(sb.toString());
						} else if (eventTypeTrunkMessage.equals(EventLog.EVENT_TYPE_FLEET_MESSAGE_OVER_SPEED_RESUME_NORMAL)){
							int periodInSecond = 0;
							
							if (geoFencesObject.getOverSpeedTime() != null && geoFencesObject.getNormalSpeedTime() != null){
								periodInSecond = geoFencesObject.getNormalSpeedTime() - geoFencesObject.getOverSpeedTime();
							}
							sb.append(devices.getName() + " did not exceed speed limit (");
							sb.append(geoFencesObject.getSpeedLimit() + ") on ");
							sb.append(geoFencesObject.getZoneName() + " of " + networks.getName());
							sb.append(" of " + networks.getName());
							sb.append(" (latitude:" + latitude + ", longitude:" + longtitude + ")");
							sb.append(", last for " + periodInSecond + " secs");
							sb.append(" with max speed of " + geoFencesObject.getMaxOverSpeedFigure());
							eventLog.setDetail(sb.toString());
						}
					} // end if (devices != null)
					eventLogDao.save(eventLog);
					isWritten = true;
					if (log.isDebugEnabled()){
						log.debugf("GEO20140204 (wlog2) - writeGpsLocation2EventLog(), saved!, type: %s -  geoFencesId: %s, zone name: %s, type: %s,devId: %s, orgId: %s, netId: %s",eventTypeTrunkMessage, geoFencesObject.getId(), geoFencesObject.getZoneName(), geoFencesObject.getType(), devOnlineO.getDevice_id(), devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());
					}
				} // end if (devOnlineO.getNetwork_id() != null && devOnlineO.getOrganization_id() != null)
			}
		}catch (Exception e){
			log.error("GEO20140204 writeGpsLocation2EventLog (wlog7e2) - writeGpsLocation2EventLog:", e);
		} // end try ... catch (Exception e)
		return isWritten;
	} // end writeGpsLocation2EventLog
	

	
	private static RequestGeoFencesTemplateDto prepareReqTemplate(String alertGeoFencesType,LocationList location, DevOnlineObject devOnlineO, GeoFencesObject geoFencesObject, Float currentSpeed){
		RequestGeoFencesTemplateDto reqGeoFencesTemplate = null;
		try{
			if (devOnlineO != null){
				if (log.isDebugEnabled()){
					log.debugf("GEO20140204 - sendEmailNotification -location is null? %s, alertGeoFencesType:%s, orgId: %s, netId: %s, devId: %d",
						(location == null), alertGeoFencesType, devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id(), 
						devOnlineO.getDevice_id());	
				}
				
				Integer alertedLevel = null;
				if (devOnlineO.getNetwork_id() != null && devOnlineO.getOrganization_id() != null && location != null){
					Float latitude = location.getLatitude();
					Float longitude = location.getLongitude();
					// reserved function, temporary use alertedLevel = 1
					/*
					DeviceStatusDAO deviceStatusDAO = new DeviceStatusDAO(devOnlineO.getOrganization_id());
					DeviceStatus deviceStatus = deviceStatusDAO.findById(devOnlineO.getDevice_id());
					if (deviceStatus != null) {
					// DeviceStatus deviceStatus = devices.getDeviceStatus();
					log.debugf("deviceStatus: %s, with devId: %s", deviceStatus.getAlertedLevel(), devOnlineO.getDevice_id());
						alertedLevel = deviceStatus.getAlertedLevel();
						log.debug("alertedLevel = " + alertedLevel);
						deviceStatusDAO.delete(deviceStatus);
					}
					*/
					// fill in RequestGeoFencesTemplate

					reqGeoFencesTemplate = new RequestGeoFencesTemplateDto();
//					reqGeoFencesTemplate.setSender_email("noreply@peplink.com");
//					reqGeoFencesTemplate.setSender_name("InControl 2 (Dev)");


					// will be set below
//					reqGeoFencesTemplate.setMsg_id(criteria.getMsgId());
//					reqGeoFencesTemplate.setMsg_type(criteria.getMsgType());
					
					// will be set at AlertUtils.sendGeoFencesAlert(criteria, reqGeoFencesTemplate);
//					reqGeoFencesTemplate.setRecipients(networkEmailNotifications.getInfo());
//					reqGeoFencesTemplate.setPriority(criteria.getPriority());					
//					reqGeoFencesTemplate.setSender_email(criteria.getSenderEmail());
//					reqGeoFencesTemplate.setSender_name(criteria.getSenderName());
					
					// --- data					
					RequestGeoFencesTemplateDataDto reqGeoFencesTemplateData = new RequestGeoFencesTemplateDataDto();		
					reqGeoFencesTemplateData.setService_name("InControl 2");
					
					Devices devices = NetUtils.getDevices(devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id(), devOnlineO.getDevice_id(), true);	// reload here to prevent data outsync
					Networks networks = OrgInfoUtils.getNetwork(devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());
					
					if (devices != null && devices.getName() != null){
						reqGeoFencesTemplateData.setDevice_name(devices.getName());
					}
					if (geoFencesObject !=null && geoFencesObject.getZoneName() != null){
						reqGeoFencesTemplateData.setZone_name(geoFencesObject.getZoneName());
					}
					if (networks != null && networks.getName() != null){
						reqGeoFencesTemplateData.setNetwork_name(networks.getName());
					}
					String networkTimeStr = "";
					// call get template web service
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					Date networkTime = DateUtils.getUtcDate();
					sdf.setTimeZone(TimeZone.getTimeZone(DateUtils.getTimezoneFromId(Integer.valueOf(networks.getTimezone()))));
					networkTimeStr = sdf.format(networkTime);
					reqGeoFencesTemplateData.setDatetime(networkTimeStr);
					
					// will be set depend on message type
//					reqGeoFencesTemplateData.setSpeed_limit(criteria.getSpeedLimit());
//					reqGeoFencesTemplateData.setSpeed_unit(criteria.getSpeedUnit());
//					reqGeoFencesTemplateData.setCurrent_speed(criteria.getCurrentSpeed());
//					reqGeoFencesTemplateData.setAddress(criteria.getAddress());
//					reqGeoFencesTemplateData.setDistance(criteria.getDistance());
//					reqGeoFencesTemplateData.setDuration(criteria.getDuration());
					
					
					
					reqGeoFencesTemplate.setData(reqGeoFencesTemplateData);
					
					String address = GeoUtils.getAddress(latitude, longitude);
					
					if (address != null && address.length() > 0){
						reqGeoFencesTemplateData.setAddress(address);
					}
					
					reqGeoFencesTemplateData.setLatitude(latitude);
					reqGeoFencesTemplateData.setLongitude(longitude);
					reqGeoFencesTemplateData.setCurrent_speed(currentSpeed);
					
					alertedLevel = SEND_EMAIL_ALERT_LEVEL;

					if (log.isDebugEnabled()){
						log.debugf("GEO20140204 - sendEmailNotification -location is null? %s, alertGeoFencesType:%s, orgId: %s, netId: %s, devId: %d",
							(location == null), alertGeoFencesType, devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id(), 
							devOnlineO.getDevice_id());	
					}
					switch (alertGeoFencesType){
						case RailsMailMgr.ALERT_TYPE_GEO_FENCES_ENTERED_ZONE:
							reqGeoFencesTemplate.setMsg_id(RailsMailMgr.ALERT_TYPE_GEO_FENCES_ENTERED_ZONE);
							reqGeoFencesTemplate.setMsg_type(SendGeoFencesAlertCriteria.MSG_TYPE_EMAIL);									
						break;
						case RailsMailMgr.ALERT_TYPE_GEO_FENCES_LEFT_ZONE:
							reqGeoFencesTemplate.setMsg_id(RailsMailMgr.ALERT_TYPE_GEO_FENCES_LEFT_ZONE);
							reqGeoFencesTemplate.setMsg_type(SendGeoFencesAlertCriteria.MSG_TYPE_EMAIL);	
							break;
						case RailsMailMgr.ALERT_TYPE_GEO_FENCES_SPEED_LIMIT:


							reqGeoFencesTemplate.setMsg_id(RailsMailMgr.ALERT_TYPE_GEO_FENCES_SPEED_LIMIT);
							reqGeoFencesTemplate.setMsg_type(SendGeoFencesAlertCriteria.MSG_TYPE_EMAIL);	
							reqGeoFencesTemplateData.setSpeed_limit(geoFencesObject.getSpeedLimit());
							reqGeoFencesTemplateData.setSpeed_unit(RequestGeoFencesTemplateDataDto.SPEED_UNIT_KM);
//									reqGeoFencesTemplateData.setCurrent_speed(geoFencesObject.getCurrentSpeed());
//									reqGeoFencesTemplateData.setDistance(geoFencesObject.getDistance());
//									reqGeoFencesTemplateData.setDuration(geoFencesObject.getDuration());
							break;
					} // end switch
					Long start = new Date().getTime() / 1000;
					if (log.isDebugEnabled()){
						log.debugf("GEO20140204 - exec time %d = %d", 23, new Date().getTime() / 1000 - start);
					}
				} // end if (devOnlineO.getNetwork_id() != null && devOnlineO.getOrganization_id() != null)
			} // end if (devOnlineO != null)
		} catch (Exception e){
			log.error("GEO20140204 - sendEmailNotification:", e);
		} // end try ... catch (Exception e) ...
		return reqGeoFencesTemplate;
	} // end sendEmailNotification()
	
} // end class
