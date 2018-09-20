package com.littlecloud.pool.object.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;

import net.sf.json.JSONObject;

import com.google.gson.Gson;
import com.littlecloud.ac.WtpMsgHandler;
import com.littlecloud.ac.WtpMsgHandlerUtils;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.DeviceGpsLocationsDatesDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.report.DeviceGpsLocations;
import com.littlecloud.control.entity.report.DeviceGpsLocationsDates;
import com.littlecloud.control.entity.report.DeviceGpsLocationsDatesId;
import com.littlecloud.control.entity.report.DeviceGpsLocationsId;
import com.littlecloud.control.entity.report.DeviceGpsRecords;
import com.littlecloud.control.entity.report.DeviceGpsRecordsId;
import com.littlecloud.control.entity.report.DeviceGpsRecordsPoints;
import com.littlecloud.control.entity.report.DeviceGpsRecordsPointsList;
import com.littlecloud.control.json.model.Json_Device_Gps_Info;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.DevLocationsObject;
import com.littlecloud.pool.object.DevLocationsReportObject;
import com.littlecloud.pool.object.LocationList;
import com.littlecloud.pool.object.SimpleDevLocationsObject;
import com.littlecloud.pool.object.SimpleLocationList;
import com.littlecloud.pool.utils.ComparatorDevGpsLocation;
import com.littlecloud.pool.utils.ComparatorDevGpsRecordsPoints;
import com.littlecloud.pool.utils.ComparatorLocationList;

public class LocationUtils 
{
	private static Logger log = Logger.getLogger(LocationUtils.class);
	
	public static boolean cacheLocationList(String json,QueryInfo<Object> info)
	{
		try
		{
			Gson gson = new Gson();
			JSONObject object = JSONObject.fromObject(info);
			JSONObject data = object.getJSONObject(WtpMsgHandler.DATA_TAG);

			Date curTime = new Date();
			boolean listTooLong = false;
			
			DevLocationsObject devLoc = null;
			
			log.info("version of dev loc " + data.getInt("version") );
			
			if( data.getInt("version") >= 4 )//TODO
			{
				SimpleDevLocationsObject simpleDevLoc = gson.fromJson(data.toString(), SimpleDevLocationsObject.class);
				if(simpleDevLoc != null)
				{
					devLoc = new DevLocationsObject();
					ArrayList<LocationList> locationLst = new ArrayList<LocationList>();
					
					log.info("Version 4 is existing!");
					ArrayList<SimpleLocationList> locLst = simpleDevLoc.getLocation_list();
					for(SimpleLocationList location : locLst)
					{
						if( location.getTimestamp() == null )
							continue;
						
						LocationList item = new LocationList();
						item.setAltitude(location.getAt());
						item.setH_dop(location.getHd());
						item.setH_uncertain(location.getHu());
						item.setLatitude(location.getLa());
						item.setLongitude(location.getLo());
						item.setSpeed(location.getSp());
						item.setStatus(location.getSt());
						item.setTimestamp(location.getTimestamp());
						item.setV_uncertain(location.getVu());
						locationLst.add(item);
					}
					CopyOnWriteArrayList<LocationList> addLocations = new CopyOnWriteArrayList<LocationList>();
					addLocations.addAll(locationLst);
					devLoc.setLocation_list(addLocations);
					devLoc.setVersion(simpleDevLoc.getVersion());
					log.info("Version 4 converted to others!");
				}
			}
			else
			{
				devLoc = gson.fromJson(data.toString(), DevLocationsObject.class);
				CopyOnWriteArrayList<LocationList> locations = devLoc.getLocation_list();
				
				if(devLoc.getVersion() == 3)
				{
					if( locations != null && !locations.isEmpty() )
					{
						if( locations.get(0).getStatus() == null )
						{
							log.warn("Json format error");
							return true;
						}
					}
				}
				
				ArrayList<LocationList> newLocations = new ArrayList<LocationList>();
				for(LocationList loc : locations)
				{
					if( loc.getTimestamp() != null && (new Date().getTime() - loc.getTimestamp() * 1000) >= 0)
						newLocations.add(loc);
				}
				CopyOnWriteArrayList<LocationList> addLocations = new CopyOnWriteArrayList<LocationList>();
				addLocations.addAll(newLocations);
				devLoc.setLocation_list(addLocations);
			}
			
			if (devLoc == null) {
				log.warn("device location object is null for " + info.getSn() + " status: " + info.getStatus());
				return false;
			}

			if (devLoc.getVersion() == null || devLoc.getVersion() < 2) {
				log.warn("Device Location with older version is submitted: version = " + devLoc.getVersion());
				return false;
			}

			if (devLoc.getLocation_list() == null) {
				log.warn("device location list from Json is null for " + info.getSn() + " status: " + info.getStatus());
				return false;
			}

			devLoc.setSid(info.getSid());
			devLoc.setSn(info.getSn());
			devLoc.setIana_id(info.getIana_id());
			
			DevLocationsObject devLocFromCache = ACUtil.getPoolObjectBySn(devLoc, DevLocationsObject.class);
			if (devLocFromCache == null) {
				devLocFromCache = new DevLocationsObject();
				devLocFromCache.setSid(info.getSid());
				devLocFromCache.setSn(info.getSn());
				devLocFromCache.setIana_id(info.getIana_id());
				devLocFromCache.setLocation_list(new CopyOnWriteArrayList<LocationList>());
			}
			List<LocationList> locListFromCache = devLocFromCache.getLocation_list();
			List<LocationList> locList = devLoc.getLocation_list();
			if (locList != null && !locList.isEmpty()) {
				LocationList lastLocation = devLocFromCache.getLast_query_location();
				boolean bSkipToAdd = false;
				for (LocationList locItem : locList) {
					
					if(locItem.getTimestamp() == null)
						continue;
					
					if (locItem.getStatus() != 0) {
						if (lastLocation != null) {
							if (lastLocation.getLatitude() != null && lastLocation.getLongitude() != null) {
								locItem.setTimestamp(lastLocation.getTimestamp() + 1);
								locItem.setLatitude(null);
								locItem.setLongitude(null);
							} else {
								// previous point is also invalid, skip to add to cache
								bSkipToAdd = true;
								log.debug("LocationUtils.cache lastLocation is null");
							}
						} else {
							// never has a success point, skip this point
							continue;
						}
					}

					if (locListFromCache.contains(locItem)) {
						bSkipToAdd = true;
					}

					if (!bSkipToAdd) {
						if (lastLocation != null) {
							if (locItem.getTimestamp().intValue() - lastLocation.getTimestamp().intValue() > 10) {
								// last query data received is five seconds ago
								LocationList nullToInsert = new LocationList();
								nullToInsert.setTimestamp(lastLocation.getTimestamp().longValue() + 1);
								nullToInsert.setLatitude(null);
								nullToInsert.setLongitude(null);
								locListFromCache.add(nullToInsert);
								log.debug("nullToInsert = " + nullToInsert);
							}
						}
						locListFromCache.add(locItem);
						lastLocation = locItem; /* point */
					}
				}

				while (locListFromCache.size() >= 120) {
					// keep last 120 data, don't care the timestamp
					locListFromCache.remove(0);
				}

				ComparatorLocationList compareLocList = new ComparatorLocationList();
				Collections.sort(new ArrayList<LocationList>(locListFromCache), compareLocList);

				devLocFromCache.setLast_query_location(lastLocation);
				devLocFromCache.setLocation_list(new CopyOnWriteArrayList<LocationList>(locListFromCache));
			}

			ACUtil.cachePoolObjectBySn(devLocFromCache, DevLocationsObject.class);
		}
		catch(Exception e)
		{
			StringWriter sw = new StringWriter();
		    e.printStackTrace(new PrintWriter(sw));		
			log.errorf("Exception on handler DEV_LOCATIONS - %s\n%s", json, sw);
			return false;
		}
		
		return true;
	}

	public static boolean isGpsLocationConsolidated(String orgId, Integer netId, Integer dev_id, Integer unixtime)
	{
		log.debug("getting isGpsLocationConsolidated  with orgId="+ orgId+" netId=" +netId +" dev_id="+dev_id+" unixtime="+unixtime);
		
		if (orgId != null && netId != null && dev_id != null && unixtime != null) 
		{
			final boolean bReadOnlyDb = true;
			boolean isConsolidated = false;
			try
			{
				DeviceGpsLocationsDatesDAO deviceGpsDatesDAO = new DeviceGpsLocationsDatesDAO(orgId, bReadOnlyDb);
				isConsolidated = deviceGpsDatesDAO.isGpsLocationDatesConsolidated(netId, dev_id, unixtime, true);
				
				if (isConsolidated)
					return true;
				else
					return false;
			
			} catch (Exception e) {
				log.errorf("Exception %s - Fail to get isGpsLocationConsolidated for orgId=%d netId=%d devId=%d unixtime=%d e=%s", orgId, netId, dev_id, unixtime, e);

			}
		}
		else
		{
			log.warnf("Fail to call isGpsLocationConsolidated with orgId %s netId %d ", orgId, netId);
		}
		return false;
	}
	
	public static Integer getGpsLocationConsolidated(String orgId, Integer netId, Integer dev_id)
	{
		log.debug("11811 getGpsLocationConsolidated  with orgId="+ orgId+" netId=" +netId +" dev_id="+dev_id);
		
		if (orgId != null && netId != null && dev_id != null) 
		{
			final boolean bReadOnlyDb = true;
			DeviceGpsLocationsDates deviceGpsLocationsDates = null;
			DeviceGpsLocationsDatesId id = null;
			Integer firstConsolidatedTime = null;
			try
			{
				DeviceGpsLocationsDatesDAO deviceGpsDatesDAO = new DeviceGpsLocationsDatesDAO(orgId, bReadOnlyDb);
				deviceGpsLocationsDates = deviceGpsDatesDAO.getGpsLocationDatesConsolidated(netId, dev_id, true);//TODO
				log.debugf("getGpsLocationConsolidated DeviceGpsLocationsDatesId=%s", deviceGpsLocationsDates);
				
				if (deviceGpsLocationsDates != null && deviceGpsLocationsDates.getId()!=null)
				{	
					id = deviceGpsLocationsDates.getId();
					firstConsolidatedTime = id.getUnixtime();
					log.infof("getGpsLocationConsolidated firstConsolidatedTime=%d DeviceGpsLocationsDatesId=%s", firstConsolidatedTime);
					return firstConsolidatedTime;
				}
			} catch (Exception e) {
				log.errorf("Exception %s - Fail to getGpsLocationConsolidated for orgId=%d netId=%d devId=%d unixtime=%d e=%s", orgId, netId, dev_id, e);
				
			}
		}
		else
		{
			log.warnf("Fail to call isGpsLocationConsolidated with orgId %s netId %d ", orgId, netId);
		}
		return null;
	}
	
	public static Json_Device_Gps_Info getGpsLocationForLatest(Devices dev, Networks net)
	{
		if (dev == null || net == null)
		{	
			log.warnf("getGpsLocationForLatest  param_dev= %s , param_net= %s", dev, net);
			return null;	
		}
		log.debugf("11811 DeviceWS.getGpsLocationForLatest() called with devId=%d netId=%d",dev.getId(),net.getId());
		 DeviceGpsLocations latestDevLoc = null;
		 boolean bLastLocIsNull = false; // for adding a null location at the end
		 /*if (devLocListFromDB != null && !devLocListFromDB.isEmpty()){
			 //is the list is not null and not empty
			 //the list should be only has one point which is the latest point 
			 devLocFromDB = devLocListFromDB.get(0);
			 if (devLocFromDB.getLatitude() == null || devLocFromDB.getLongitude() == null){
				 latestDevLoc = deviceLocationsDAO.getLatestNotNullLocation(dev.getId());
				 bLastLocIsNull = true;
			 }else {
				 latestDevLoc = devLocFromDB;
			 }
		 }*/
		 Json_Device_Gps_Info gpsObj = new Json_Device_Gps_Info();
		 List<DeviceGpsLocations> locLst = null;
		
		try
		{
			/* ***** 11811 ***** */
			DevLocationsReportObject devLocReportSample = new DevLocationsReportObject();
			devLocReportSample.setIana_id(dev.getIanaId());
			devLocReportSample.setSn(dev.getSn());
			DevLocationsReportObject devLocReportFromCache = ACUtil.getPoolObjectBySn(devLocReportSample, DevLocationsReportObject.class);
			
			if (devLocReportFromCache != null)
			{
				if( devLocReportFromCache.getLocation_list() != null && !devLocReportFromCache.getLocation_list().isEmpty())
				{
					List<LocationList> locationListFromCache = devLocReportFromCache.getLocation_list();
					List<LocationList> localList = new ArrayList<LocationList>();
					localList.addAll(locationListFromCache);
					/* *** Get the latest locationList, list is sorted in adding *** */
					LocationList latestFromReportCache = localList.get(localList.size()-1);
					if (latestFromReportCache.getLongitude() == null || latestFromReportCache.getLatitude() == null){
					/* list should not be empty and at least has 60 points and there should be no consecutive null location points in cache */ 
						latestFromReportCache = localList.get(localList.size()-2);
						bLastLocIsNull = true;
					}else {
						bLastLocIsNull = false;
					}
					
					latestDevLoc = new DeviceGpsLocations();
					DeviceGpsLocationsId locObjId = new DeviceGpsLocationsId();
					locObjId.setNetworkId(net.getId());
					locObjId.setDeviceId(dev.getId());
					locObjId.setUnixtime(latestFromReportCache.getTimestamp().intValue());
					latestDevLoc.setId(locObjId);
					latestDevLoc.setLatitude(latestFromReportCache.getLatitude());
					latestDevLoc.setLongitude(latestFromReportCache.getLongitude());
					latestDevLoc.setAltitude(latestFromReportCache.getAltitude());
					latestDevLoc.setSpeed(latestFromReportCache.getSpeed());
					latestDevLoc.setHUncertain(latestFromReportCache.getH_uncertain());
					latestDevLoc.setType(latestFromReportCache.getFlag());
					
					long network_time = 0;
					if( latestFromReportCache.getTimestamp() != null )
						network_time = (long)latestFromReportCache.getTimestamp()*1000;
					Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
					latestDevLoc.setDatetime(datetime);
				}
		}
		
			/* ***** 11811 ***** */
		DevLocationsObject devLocationsExample = new DevLocationsObject();
		devLocationsExample.setIana_id(dev.getIanaId());
		devLocationsExample.setSn(dev.getSn());		
		DevLocationsObject devLocationsObject = ACUtil.getPoolObjectBySn(devLocationsExample, DevLocationsObject.class);
		
		if (devLocationsObject != null)
		{
			if( devLocationsObject.getLocation_list() != null && !devLocationsObject.getLocation_list().isEmpty())
			{
				List<LocationList> locationListFromCache = devLocationsObject.getLocation_list();
				List<LocationList> localList = new ArrayList<LocationList>();
				localList.addAll(locationListFromCache);
				/* *** Get the latest locationList, list is sorted in adding*** */
				LocationList latestFromReportCache = localList.get(localList.size()-1);
				/* *** is real time query only has one point and is null, skip to check *** */
				boolean bSkipCheck = false;
				if (latestFromReportCache.getLatitude() == null || latestFromReportCache.getLongitude() == null)
				{
					if (localList.size() > 1){
						latestFromReportCache = localList.get(localList.size()-2);
					}else {
						bSkipCheck = true;
					}
					bLastLocIsNull = true;
				}
				else {
					bLastLocIsNull = false;
				}
				
				if (!bSkipCheck)
				{
					if (latestDevLoc != null)
					{
						if (latestFromReportCache.getTimestamp().intValue() > latestDevLoc.getId().getUnixtime()){
							//realtime cache is latest one
							latestDevLoc.getId().setUnixtime(latestFromReportCache.getTimestamp().intValue());
							latestDevLoc.setLatitude(latestFromReportCache.getLatitude());
							latestDevLoc.setLongitude(latestFromReportCache.getLongitude());
							latestDevLoc.setAltitude(latestFromReportCache.getAltitude());
							latestDevLoc.setSpeed(latestFromReportCache.getSpeed());
							latestDevLoc.setHUncertain(latestFromReportCache.getH_uncertain());
							latestDevLoc.setType(latestFromReportCache.getFlag());
							
							
							long network_time = 0;
							if( latestFromReportCache.getTimestamp() != null )
								network_time = (long)latestFromReportCache.getTimestamp()*1000;
							Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
							latestDevLoc.setDatetime(datetime);
						}
					}
					else
					{
						latestDevLoc = new DeviceGpsLocations();
						DeviceGpsLocationsId locObjId = new DeviceGpsLocationsId();
						locObjId.setNetworkId(net.getId());
						locObjId.setDeviceId(dev.getId());
						locObjId.setUnixtime(latestFromReportCache.getTimestamp().intValue());
						latestDevLoc.setId(locObjId);
						latestDevLoc.setLatitude(latestFromReportCache.getLatitude());
						latestDevLoc.setLongitude(latestFromReportCache.getLongitude());
						latestDevLoc.setAltitude(latestFromReportCache.getAltitude());
						latestDevLoc.setSpeed(latestFromReportCache.getSpeed());
						latestDevLoc.setHUncertain(latestFromReportCache.getH_uncertain());
						latestDevLoc.setType(latestFromReportCache.getFlag());
						
						long network_time = 0;
						if( latestFromReportCache.getTimestamp() != null )
							network_time = (long)latestFromReportCache.getTimestamp()*1000;
						Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
						latestDevLoc.setDatetime(datetime);
					}
				}
			}
		}
		
		locLst = new ArrayList<DeviceGpsLocations>();
		if (latestDevLoc != null)
		{
			locLst.add(latestDevLoc);
			if (bLastLocIsNull){
				DeviceGpsLocations nullDevLoc = new DeviceGpsLocations();
				DeviceGpsLocationsId locObjId = new DeviceGpsLocationsId();
				locObjId.setNetworkId(net.getId());
				locObjId.setDeviceId(dev.getId());
				locObjId.setUnixtime(latestDevLoc.getId().getUnixtime()+1);
				nullDevLoc.setId(locObjId);
				long network_time = 0;
				network_time = (long)(latestDevLoc.getId().getUnixtime()+1)*1000;
				Date datetime = DateUtils.offsetFromUtcTimeZoneId(new Date(network_time), net.getTimezone());
				nullDevLoc.setDatetime(datetime);
				locLst.add(nullDevLoc);
			}
		}
		else
		{
			//all are null, never have a gps location in DB or cache
			if (dev.getLatitude() != 0 || dev.getLongitude() != 0){
				latestDevLoc = new DeviceGpsLocations();
				DeviceGpsLocationsId locObjId = new DeviceGpsLocationsId();
				locObjId.setNetworkId(net.getId());
				locObjId.setDeviceId(dev.getId());
				Date curTime = DateUtils.getUtcDate();
				locObjId.setUnixtime((int)(curTime.getTime()/1000));
				latestDevLoc.setId(locObjId);
				Date datetime = DateUtils.offsetFromUtcTimeZoneId(curTime, net.getTimezone());
				latestDevLoc.setDatetime(datetime);
				latestDevLoc.setLatitude(dev.getLatitude());
				latestDevLoc.setLongitude(dev.getLongitude());
				locLst.add(latestDevLoc);
				//bIsStatic = true;
				gpsObj.setbIsStatic(true);
			}
		}
		
		gpsObj.setLocLst(locLst);
		} catch (Exception e) {
			log.error("getGpsLocationForLatest - gpsObj = " +gpsObj + e,e);			
			return gpsObj;
		}

		if (log.isDebugEnabled())
			log.debug("latestDevLoc = "+latestDevLoc+" bLastLocIsNull = "+bLastLocIsNull);
				
		return gpsObj;
	}
	
	public static List<DeviceGpsLocations> convertGpsRecordsToLocationsList(DeviceGpsRecordsPointsList records)
	{
		log.debug("11811 convertGpsRecordsToLocationsList  with recordslist"+ records);
		
		if (records == null||records.getRecords()==null)
			return null;
		List<DeviceGpsLocations> locationlst = new ArrayList<DeviceGpsLocations>();
		List<DeviceGpsRecordsPoints> recordslst = records.getRecords();
		for (DeviceGpsRecordsPoints points : recordslst)
		{
			DeviceGpsLocations loc = new DeviceGpsLocations();
			loc.setAltitude(points.getAt());
			loc.setSpeed(points.getSp());
			loc.setLatitude(points.getLa());
			loc.setLongitude(points.getLo());
			loc.setHDop(points.getHd());
			loc.setHUncertain(points.getHu());
			loc.setVUncertain(points.getVu());
			loc.setType(points.getFlag());
//			loc.setDatetime();
//			DeviceGpsLocationsId id = new DeviceGpsLocationsId();
//			id.setDeviceId(points.get);
//			loc.setId(id);
			locationlst.add(loc);
		}
		
		return locationlst;
	}

	public static DeviceGpsRecords convertLocationListToGpsRecords(List<LocationList> locationsList, Integer network_id, Integer device_id)
	{
		log.debugf("11810 getGpsLocation convertLocationListToGpsRecords with netId = %d, devId = %d, locationLst = %s", network_id, device_id, locationsList);
		
		if (network_id == null || device_id == null || locationsList == null)
			return null;
		
		DeviceGpsRecords deviceGpsRecords = new DeviceGpsRecords();
		String location_data = null;
		List<DeviceGpsRecordsPoints> pointsLst = new ArrayList<DeviceGpsRecordsPoints>();
		for (LocationList loc : locationsList)
		{
			DeviceGpsRecordsPoints item = new DeviceGpsRecordsPoints();
			item.setAt(loc.getAltitude());
			item.setHd(loc.getH_dop());
			item.setHu(loc.getH_uncertain());
			item.setLa(loc.getLatitude());
			item.setLo(loc.getLongitude());
			item.setSp(loc.getSpeed());
			item.setSt(loc.getStatus());
			item.setTimestamp(loc.getTimestamp().longValue());
			item.setVu(loc.getV_uncertain());
			item.setFlag(loc.getFlag());
			pointsLst.add(item);			
		}
		ComparatorDevGpsRecordsPoints com = new ComparatorDevGpsRecordsPoints();
		Collections.sort(pointsLst, com);//to ensure
		DeviceGpsRecordsPoints first_point = pointsLst.get(0);
		DeviceGpsRecordsPoints last_point = pointsLst.get(pointsLst.size()-1);
		
		deviceGpsRecords.setFirst_latitude(first_point.getLa());
		deviceGpsRecords.setFirst_longitude(first_point.getLo());
		deviceGpsRecords.setLast_latitude(last_point.getLa());
		deviceGpsRecords.setLast_longitude(last_point.getLo());
		deviceGpsRecords.setLast_unixtime(last_point.getTimestamp().intValue());
		
		DeviceGpsRecordsId deviceGpsRecordsId = new DeviceGpsRecordsId(network_id, device_id, first_point.getTimestamp().intValue());
		deviceGpsRecords.setId(deviceGpsRecordsId);
		
		DeviceGpsRecordsPointsList recordslst = new DeviceGpsRecordsPointsList();
		recordslst.setRecords(pointsLst);
		location_data = JsonUtils.toJson(recordslst);
		deviceGpsRecords.setLocation_data(location_data);
		
		return deviceGpsRecords;
	}
	
	public static List<DeviceGpsLocations> convertGpsRecordsToLocationsList(DeviceGpsRecords records)
	{
		log.debug("11812 getGpsLocation - convertGpsRecordsToLocationsList  with records"+ records);
		
		if (records == null||records.getLocation_data()==null)
			return null;
		List<DeviceGpsLocations> locationlst = new ArrayList<DeviceGpsLocations>();
		ComparatorDevGpsLocation com = new ComparatorDevGpsLocation();
		DeviceGpsRecordsPointsList jsonlist = null;
		
		jsonlist = JsonUtils.fromJson(records.getLocation_data(), DeviceGpsRecordsPointsList.class);
		if (jsonlist != null)
		{
			List<DeviceGpsRecordsPoints> pointslst = jsonlist.getRecords();
			DeviceGpsRecordsId rid = records.getId();
			for (DeviceGpsRecordsPoints points : pointslst)
			{
				Integer points_unixtime = points.getTimestamp().intValue();
				
				DeviceGpsLocationsId id = new DeviceGpsLocationsId();
				id.setDeviceId(rid.getDeviceId());
				id.setNetworkId(rid.getNetworkId());
				id.setUnixtime(points_unixtime);/* **** Important, not to get rid.getUnixtime() **** */
									
				DeviceGpsLocations loc = new DeviceGpsLocations();
				loc.setId(id);
				loc.setAltitude(points.getAt());
				loc.setSpeed(points.getSp());
				loc.setLatitude(points.getLa());
				loc.setLongitude(points.getLo());
				loc.setHDop(points.getHd());
				loc.setHUncertain(points.getHu());
				loc.setVUncertain(points.getVu());
				loc.setType(points.getFlag());
				//loc.setDatetime();
				
				locationlst.add(loc);
			}
		}
		Collections.sort(locationlst, com);
		return locationlst;
	}
	
	public static List<DeviceGpsLocations> convertGpsRecordsToLocationsList(List<DeviceGpsRecords> recordslst)
	{
		if (recordslst == null)
			return null;
		log.debug("11812 getGpsLocation -convertGpsRecordsToLocationsList  with recordslist.size = "+ recordslst.size());
		
		List<DeviceGpsLocations> locationlst = new ArrayList<DeviceGpsLocations>();
		ComparatorDevGpsLocation com = new ComparatorDevGpsLocation();
		for (DeviceGpsRecords records : recordslst)
		{
			DeviceGpsRecordsPointsList jsonlist = null;
			if (records.getLocation_data() != null)
				jsonlist = JsonUtils.fromJson(records.getLocation_data(), DeviceGpsRecordsPointsList.class);
			if (jsonlist != null)
			{
				List<DeviceGpsRecordsPoints> pointslst = jsonlist.getRecords();
				DeviceGpsRecordsId rid = records.getId();
				for (DeviceGpsRecordsPoints points : pointslst)
				{
					Integer points_unixtime = points.getTimestamp().intValue();
					
					DeviceGpsLocationsId id = new DeviceGpsLocationsId();
					id.setDeviceId(rid.getDeviceId());
					id.setNetworkId(rid.getNetworkId());
					id.setUnixtime(points_unixtime);/* **** Important, not to get rid.getUnixtime() **** */
										
					DeviceGpsLocations loc = new DeviceGpsLocations();
					loc.setId(id);
					loc.setAltitude(points.getAt());
					loc.setSpeed(points.getSp());
					loc.setLatitude(points.getLa());
					loc.setLongitude(points.getLo());
					loc.setHDop(points.getHd());
					loc.setHUncertain(points.getHu());
					loc.setVUncertain(points.getVu());
					loc.setType(points.getFlag());
					//loc.setDatetime();
					
					locationlst.add(loc);
				}
			}
		}
		Collections.sort(locationlst, com);
		return locationlst;
	}

	public static List<DeviceGpsLocations> filterGpsRecordsToLocationsList(List<DeviceGpsRecords> recordslst, Integer start, Integer end)
	{		
		if (recordslst == null)
			return null;
				
		List<DeviceGpsLocations> locationlst = new ArrayList<DeviceGpsLocations>();
		ComparatorDevGpsLocation com = new ComparatorDevGpsLocation();
		for (DeviceGpsRecords records : recordslst)
		{
			DeviceGpsRecordsPointsList jsonlist = null;
			if (records.getLocation_data() != null)
				jsonlist = JsonUtils.fromJson(records.getLocation_data(), DeviceGpsRecordsPointsList.class);
			if (jsonlist != null)
			{
				List<DeviceGpsRecordsPoints> pointslst = jsonlist.getRecords();
				DeviceGpsRecordsId rid = records.getId();
				for (DeviceGpsRecordsPoints points : pointslst)
				{
					Integer points_unixtime = points.getTimestamp().intValue();
					if (points_unixtime == null || points_unixtime.compareTo(start)<0 || points_unixtime.compareTo(end)>0)
						continue;

					DeviceGpsLocationsId id = new DeviceGpsLocationsId();
					id.setDeviceId(rid.getDeviceId());
					id.setNetworkId(rid.getNetworkId());
					id.setUnixtime(points_unixtime);/* **** Important, not to get rid.getUnixtime() **** */
										
					DeviceGpsLocations loc = new DeviceGpsLocations();
					loc.setId(id);
					loc.setAltitude(points.getAt());
					loc.setSpeed(points.getSp());
					loc.setLatitude(points.getLa());
					loc.setLongitude(points.getLo());
					loc.setHDop(points.getHd());
					loc.setHUncertain(points.getHu());
					loc.setVUncertain(points.getVu());
					loc.setType(points.getFlag());
					//loc.setDatetime();
					
					locationlst.add(loc);
				}
			}
		}
		log.debug("11811 getGpsLocation - filterGpsRecordsToLocationsList  with recordslist.size = "+ recordslst.size()+" locationlst.size = "+locationlst.size());
		Collections.sort(locationlst, com);
		return locationlst;
	}

}
