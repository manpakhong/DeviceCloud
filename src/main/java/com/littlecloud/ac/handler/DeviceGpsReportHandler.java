package com.littlecloud.ac.handler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import net.sf.json.JSONObject;

import org.jboss.logging.Logger;

import com.littlecloud.ac.WtpMsgHandler;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.DeviceGpsLocationsDatesDAO;
import com.littlecloud.control.dao.DeviceGpsRecordsDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.report.DeviceGpsLocationsDates;
import com.littlecloud.control.entity.report.DeviceGpsLocationsDatesId;
import com.littlecloud.control.entity.report.DeviceGpsRecords;
import com.littlecloud.control.entity.report.DeviceGpsRecordsId;
import com.littlecloud.control.entity.report.DeviceGpsRecordsPoints;
import com.littlecloud.control.entity.report.DeviceGpsRecordsPointsList;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.DevLocationsConvertObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.DeviceLastLocObject;
import com.littlecloud.pool.object.LocationList;
import com.littlecloud.pool.object.SimpleDevLocationsObject;
import com.littlecloud.pool.object.SimpleLocationList;
import com.littlecloud.pool.utils.ComparatorDevGpsRecordsPoints;
import com.littlecloud.services.DeviceGpsRecordsMgr;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.util.DBUtil;

public class DeviceGpsReportHandler {
	public static Logger log = Logger.getLogger(DeviceGpsReportHandler.class);

	private QueryInfo info; 
	private String orgId;
	private DevOnlineObject devO;
	private int qsize;
	private String json;
	private DBUtil dbUtil;
	private DBConnection batchConnection;

	private DeviceGpsRecordsDAO gpsDAO = null;
	private DeviceGpsLocationsDatesDAO gpsDateDAO = null;
	private NetworksDAO netDAO = null;
	
	public DeviceGpsReportHandler(MessageInfo gpsInfo) throws Exception
	{
		this.info = gpsInfo.getInfo(); 
		this.orgId = gpsInfo.getOrgId();
		this.qsize = gpsInfo.getQueueSize();
		this.json = gpsInfo.getJson();
		this.dbUtil = gpsInfo.getDbUtil();
		this.batchConnection = gpsInfo.getBatchConnection();
		
		this.gpsDAO = new DeviceGpsRecordsDAO(orgId);
		this.gpsDateDAO = new DeviceGpsLocationsDatesDAO(orgId);
		this.netDAO = new NetworksDAO(orgId);
	}
		
	public boolean persistQueue() {
		if (log.isDebugEnabled()) log.debugf("DeviceGpsReportHandler.persistQueue() info.getSn()=%s", info.getSn());
		
		JSONObject object;
		JSONObject data;
		Integer version = null;
		try
		{
			devO = new DevOnlineObject();
			devO.setIana_id(info.getIana_id());
			devO.setSn(info.getSn());
			devO = ACUtil.getPoolObjectBySn(devO, DevOnlineObject.class);
			if (devO == null){
				log.debugf("DevOnline = %s", devO);
				return false;
			}
				

			object = JSONObject.fromObject(info);
			data = object.getJSONObject(ACUtil.DATA_TAG);
			if (data == null){
				if (log.isDebugEnabled()) log.debugf("data is empty");
				return false;
			}
			
			DevLocationsConvertObject devLoc = getPointListFromData(data);
			log.debugf("DeviceGpsReportHandler.persistQueue() DevLocationsConvertObject=%s", devLoc.toString());
			if (!isValidLocationReport(devLoc))
			{
				log.debug("data location is invalid!");
				return false;
			}

			/* Make a location list copy to persist the locations */
			List<DeviceGpsRecordsPoints> ptLst = devLoc.getPoint_list();
			log.debugf("DeviceGpsReportHandler.persistQueue() ptLst1=%s", ptLst);
			List<DeviceGpsRecordsPoints> validPtLst = addNullPoint(ptLst);			
			log.debugf("DeviceGpsReportHandler.persistQueue() ptLst2=%s", validPtLst);
			if (data.has("version"))
				version = data.getInt("version");
			persistGpsRecords(validPtLst, version);
			
			

			/* *******close connection ****** */
			try {
				batchConnection.commit();
				batchConnection.close();
				if (dbUtil.isSessionStarted())
					dbUtil.endSession();
				dbUtil.startSession();
				batchConnection = dbUtil.getConnection(false, orgId, false);
				return false;//why false
			} catch (SQLException e) {
				if (e.toString().indexOf("Deadlock found") != -1) {
					log.warnf("Processing DEV_LOCATIONS deadlock detected and requeue, sn=%s", info.getSn());
					WtpMsgHandler.reQueue(json);
				} else {
					log.error("Processing DEV_LOCATIONS SQLException, sn=" + info.getSn() + ", e=" + e, e);
					return false;
				}
			}
		} catch (Exception e) {
			log.error("Exception on handle DEV_LOCATIONS (" + info.getSn() + ") " + e, e);
			return false;
		}
		
		return true;
	}

	private boolean isValidLocationReport(DevLocationsConvertObject devLoc) {
		if (devLoc == null) {
			log.warn("device location object is null for " + info.getSn() + " status: " + info.getStatus());
			return false;
		}

		if (devLoc.getVersion() == null || devLoc.getVersion() < 2) {
			log.warn("Device Location with older version is submitted: version = " + devLoc.getVersion());
			return false;
		}

		if (devLoc.getLocation_list() == null && devLoc.getPoint_list() == null) {
			log.warn("device location list from Json is null for " + info.getSn() + " status: " + info.getStatus());
			return false;
		}
		
		return true;
	}


	public void cacheNullPoint(DeviceGpsRecordsPoints lastpoint) throws Exception {
		LocationList lastItem = convertToLocationList(lastpoint);
		
		DeviceLastLocObject devLastLocation = new DeviceLastLocObject();
		devLastLocation.setSn(info.getSn());
		devLastLocation.setIana_id(info.getIana_id());
		devLastLocation.setLastLocation(lastItem);
		devLastLocation.setStatic(false);
		ACUtil.cachePoolObjectBySn(devLastLocation, DeviceLastLocObject.class);
	}
	
	private void cacheLastLocation(DeviceGpsRecordsPoints lastpoint) throws Exception {
		LocationList lastItem = convertToLocationList(lastpoint);
		
		DeviceLastLocObject devLastLocation = new DeviceLastLocObject();
		devLastLocation.setSn(info.getSn());
		devLastLocation.setIana_id(info.getIana_id());
		devLastLocation.setLastLocation(lastItem);
		devLastLocation.setStatic(false);
		ACUtil.cachePoolObjectBySn(devLastLocation, DeviceLastLocObject.class);
	}

	private DeviceGpsRecordsPoints getLastLocation(List<DeviceGpsRecordsPoints> ptLst) {
		DeviceGpsRecordsPoints lastpoint = null;
				
		if (ptLst!=null && ptLst.size()>0)
		{
			lastpoint = ptLst.get(ptLst.size()-1); 
			/* In case the lastpoint is null point */
			if (lastpoint.getLa() == null || lastpoint.getLo() == null)
			{
				if (ptLst.size()>1)		
					lastpoint = ptLst.get(ptLst.size()-2);
			}
		}
				
		if (lastpoint == null) {
			log.warnf("no valid gps data (%s)", info.getSn());
		}
		
		return lastpoint;
	}

	private DeviceGpsRecordsPoints getFirstLocation(List<DeviceGpsRecordsPoints> ptLst) {
		DeviceGpsRecordsPoints firstpoint = null;
				
		if (ptLst!=null && ptLst.size()>0)
		{
			firstpoint = ptLst.get(0); 
			if (firstpoint.getLa() == null || firstpoint.getLo() == null)
			{/* In case the null point */
				if (ptLst.size()>1)
					firstpoint = ptLst.get(1); 
			}
		}
				
		if (firstpoint == null) {
			log.warnf("no valid gps data (%s)", info.getSn());
		}
		
		return firstpoint;
	}

	private List<DeviceGpsRecordsPoints> addNullPoint(List<DeviceGpsRecordsPoints> ptLst) {
		Date utcTime = null;
		Date curT = null;
		
		/* Begin loop location list */
		HashMap<Integer, Boolean> addedTimestamp = new HashMap<Integer, Boolean>();			
		boolean isInvalidAdded = false;
		boolean isLastRecordValid = false;
		DeviceGpsRecordsPoints newpoint = null;
		//DeviceGpsLocations[] locationsArray = null;
		List<DeviceGpsRecordsPoints> ptLstNew = new ArrayList<DeviceGpsRecordsPoints>();
		for (DeviceGpsRecordsPoints point : ptLst) {
			if (addedTimestamp.get(point.getTimestamp()) != null) {
				/* already added, skip */
				continue;
			}

			/* save to database */
			utcTime = new Date((long) point.getTimestamp() * 1000);
			curT = new Date();

			int utc_unixT = (int) (utcTime.getTime() / 1000);
			int cur_unixT = (int) (curT.getTime() / 1000);
			if (utc_unixT > (cur_unixT + 300)) {
				// log.infof("Future date record found, record ignored %s %s UTC %d CUR %d",devLoc.getSn(),MessageType.PIPE_INFO_TYPE_DEV_LOCATIONS,utc_unixT,cur_unixT);
				continue;
			}
			
			if (point.getHu() != null && point.getHu() > 999999)
				point.setHu(999999f);
			
			if (point.getSt() != null && point.getSt() == 0) {				
				newpoint = new DeviceGpsRecordsPoints(utcTime.getTime()/1000, 
						point.getLa(), point.getLo(), point.getAt(), point.getSp(),
						point.getHu(), point.getVu(), point.getSt(), point.getHd(), point.getFlag());

				isInvalidAdded = false;
			} else {
				if (!isInvalidAdded) {					
					newpoint = new DeviceGpsRecordsPoints(utcTime.getTime()/1000, 
							null, null, null, null,
							null, null, null, null, null);
					
					isInvalidAdded = true;
				}
			}

			if (newpoint == null) {
				continue;
			}
			
			ptLstNew.add(newpoint);

			addedTimestamp.put(point.getTimestamp().intValue(), true);
		}
	
		ComparatorDevGpsRecordsPoints com = new ComparatorDevGpsRecordsPoints();
		Collections.sort(ptLstNew, com);//to ensure
		
		/* If The last UnixTime is more than 60s ago */
		Integer currentUnixTime = ptLstNew.get(0).getTimestamp().intValue();
		isLastRecordValid = isLastRecordValid(currentUnixTime);
		if (!isLastRecordValid)
		{
			DeviceGpsRecordsPoints firstpoint = ptLstNew.get(0);
			DeviceGpsRecordsPoints nullpoint = new DeviceGpsRecordsPoints(firstpoint.getTimestamp()-1 , 
					null, null, null, null,
					null, null, null, null, null);
			ptLstNew.add(0, nullpoint);
			log.debugf("DeviceGpsReportHandler.addNullPoint() nullpoint = %s", nullpoint);
		}
		return ptLstNew;
	}

	private boolean isLastRecordValid(Integer currentUnixTime)
	{
		Integer lastUnixTime = gpsDAO.getLastUnixTime(devO.getDevice_id());
		log.debugf("DeviceGpsReportHandler.isLastRecordValid() currentUnixTime = %d lastUnixtime = %d", currentUnixTime, lastUnixTime);
		if (lastUnixTime != null && currentUnixTime != null)
		{
			if (currentUnixTime - lastUnixTime < 61)
				return true;
		}
		return false;
	}
	
	private DevLocationsConvertObject getPointListFromData(JSONObject data) {
		DevLocationsConvertObject devLoc = null;
		int n = getLoadFactor(orgId, qsize);
		
		if (data.getInt("version") >= 4) {
			SimpleDevLocationsObject simpleDevLoc = JsonUtils.fromJson(data.toString(), SimpleDevLocationsObject.class);
			log.debugf("getPointListFromData SimpleDevLocationsObject=%s", simpleDevLoc);
			if (simpleDevLoc != null) {
				devLoc = new DevLocationsConvertObject();					
				ArrayList<DeviceGpsRecordsPoints> pointLst = new ArrayList<DeviceGpsRecordsPoints>();
				
				int cnt = 0;
				// log.info("Version 4 is existing!");
				ArrayList<SimpleLocationList> locLst = simpleDevLoc.getLocation_list();
				for (SimpleLocationList location : locLst) {
					if (location.getTimestamp() == null)
						continue;

					DeviceGpsRecordsPoints pt = convertToDeviceGpsRecordsPoints(location);

					if ((cnt % n) == 0) // skip odd cnt points
						pointLst.add(pt);
					cnt++;

					// locationLst.add(item);
				}
				//devLoc.setLocation_list(locationLst);
				devLoc.setPoint_list(pointLst);
				devLoc.setVersion(simpleDevLoc.getVersion());
			}
		} else {
			devLoc = JsonUtils.fromJson(data.toString(), DevLocationsConvertObject.class);
			ArrayList<LocationList> locations = devLoc.getLocation_list();

			if (devLoc.getVersion() == 3) {
				if (locations != null && !locations.isEmpty()) {
					if (locations.get(0).getStatus() == null)
						return null;
				}
			}

			int cnt = 0;
			ArrayList<DeviceGpsRecordsPoints> pointLst = new ArrayList<DeviceGpsRecordsPoints>();
			for (LocationList loc : locations) {
				if (loc.getTimestamp() != null && (new Date().getTime() - loc.getTimestamp() * 1000) >= 0) {
					if ((cnt % n) == 0) // skip odd cnt points, i.e. 1pt / ns
					{
						DeviceGpsRecordsPoints pt = convertToDeviceGpsRecordsPoints(loc);
						pointLst.add(pt);
					}
					cnt++;
				}
			}
			//devLoc.setLocation_list(newLocations);
			devLoc.setPoint_list(pointLst);
			devLoc.setVersion(devLoc.getVersion());
		}
		
		return devLoc;
	}
	
	private void persistGpsRecords(List<DeviceGpsRecordsPoints> ptLst, Integer version) throws Exception
	{
		
		if (log.isDebugEnabled()) log.debugf("persistGpsRecords() sn =%s timestamp=%d", info.getSn(),info.getTimestamp());
		if (ptLst == null ||ptLst.size()==0|| ptLst.get(0) ==null)
			return;
		
		DeviceGpsRecords gpsRecord = new DeviceGpsRecords();			
		DeviceGpsRecordsId gpsId = new DeviceGpsRecordsId();
		DeviceGpsRecordsPoints firstPoint = null;
		DeviceGpsRecordsPoints lastPoint = null;
		gpsId.setDeviceId(devO.getDevice_id());
		gpsId.setNetworkId(devO.getNetwork_id());
		try
		{
			/* ****persist DeviceGpsLocationsDate**** */			
			firstPoint = getFirstLocation(ptLst);
			persistGpsLocationDate(firstPoint);
			
			/* *******cache last location ****** */
			lastPoint = getLastLocation(ptLst);
			cacheLastLocation(lastPoint);

			log.debug("-persistGpsRecords firstPoint = "+ firstPoint+ " lastPoint = "+lastPoint);

			gpsRecord.setId(gpsId);
			if (firstPoint != null)
			{
				gpsId.setUnixtime(firstPoint.getTimestamp().intValue());		
				gpsRecord.setFirst_latitude(firstPoint.getLa());
				gpsRecord.setFirst_longitude(firstPoint.getLo());
			}
			if (lastPoint != null)
			{
				gpsRecord.setLast_unixtime(lastPoint.getTimestamp().intValue());
				gpsRecord.setLast_latitude(lastPoint.getLa());
				gpsRecord.setLast_longitude(lastPoint.getLo());
			}
			DeviceGpsRecordsPointsList recordslst = new DeviceGpsRecordsPointsList();
			recordslst.setRecords(ptLst);
			String location_data = JsonUtils.toJson(recordslst);
			gpsRecord.setLocation_data(location_data);
			
			gpsRecord.setVersion(version);
//			DeviceGpsRecordsMgr gpsMgr = new DeviceGpsRecordsMgr(orgId);	
//			gpsMgr.saveOrUpdate(gpsRecord);
			gpsRecord.createIgnore();
			batchConnection.addBatch(true, gpsRecord);/* with ignore */
			log.debug("11810 getGpsLocation -DeviceGpsRecordsHandler.persistGpsRecords()  firstPoint = "+ firstPoint+ " lastPoint = "+lastPoint);
		}
		catch (NullPointerException ne){
			log.warn("NullPointerException persistGpsRecords firstPoint = "+ firstPoint+ " lastPoint = "+lastPoint , ne);
		}
		catch (Exception e){
			log.errorf("11810 persistGpsRecords ptLst= %s version=%d persistGpsRecords gpsRecord= %s", ptLst, version, gpsRecord, e);
		}
		
	}

	private void persistGpsLocationDate(DeviceGpsRecordsPoints firstpoint) throws Exception
	{
		/* *** create DeviceGpsLocationsDate *** */
		log.debugf("persistGpsLocationDate with sn= %s timestamp= %d firstpoint = %s", info.getSn(), info.getTimestamp(), firstpoint.toString());
//		Networks network = netDAO.findById(devO.getNetwork_id());
//		String timezone = network.getTimezone();
//		String timezoneId = DateUtils.getTimezoneFromId(Integer.valueOf(timezone));
//		TimeZone tz = TimeZone.getTimeZone(timezoneId);
//		int netTime = firstpoint.getTimestamp().intValue();
//		int offsetMillis = tz.getOffset((long) netTime * 1000);
//		int adjustNetTime0 = netTime + offsetMillis / 1000;
//		int adjustNetTime = adjustNetTime0 / 86400 * 86400; // round to day
//		log.debugf("persistGpsLocationDate with timezone=%s netTime=%d offsetMillis=%d adjustNetTime0=%d adjustNetTime=%d", timezone, netTime, offsetMillis,adjustNetTime0,adjustNetTime);
	
		int adjustNetTime0 = firstpoint.getTimestamp().intValue();
		int adjustNetTime = adjustNetTime0 / 86400 * 86400; // round to day
		log.debugf("persistGpsLocationDate with  adjustNetTime0=%d adjustNetTime=%d", adjustNetTime0,adjustNetTime);

		DeviceGpsLocationsDates locaDate = gpsDateDAO.getDeviceGpsLocationsDates(
				devO.getNetwork_id(), devO.getDevice_id(), adjustNetTime);
		log.debugf("persistGpsLocationDate with network_id = %d, device_id = %d, adjustNetTime = %d locaDate=%s", devO.getNetwork_id(), devO.getDevice_id(), adjustNetTime,locaDate);

		if (locaDate == null) {
			log.debugf("persistGpsLocationDate begin with network_id = %d, device_id = %d, adjustNetTime = %d", devO.getNetwork_id(), devO.getDevice_id(), adjustNetTime);
			DeviceGpsLocationsDatesId locaDatesId = new DeviceGpsLocationsDatesId();
			locaDatesId.setDeviceId(devO.getDevice_id());
			locaDatesId.setNetworkId(devO.getNetwork_id());
			locaDatesId.setUnixtime(adjustNetTime);//TODO
			locaDate = new DeviceGpsLocationsDates();
			locaDate.setId(locaDatesId);
			locaDate.createIgnore();
			locaDate.setConsolidated(true);
			batchConnection.addBatch(true, locaDate);
			log.debugf("persistGpsLocationDate end with network_id = %d, device_id = %d, adjustNetTime = %d", devO.getNetwork_id(), devO.getDevice_id(), adjustNetTime);

		}
	}
	
	private int getLoadFactor(String orgId, int qsize)
	{
		int n = 1;
		
		if (qsize > 3000) {
			n = 8;
			log.warnf("Org=%s,  qsize=%d, n=%d, v2/3", orgId, qsize, n);
		} else if (qsize > 2500) {
			n = 7;
			log.warnf("Org=%s,  qsize=%d, n=%d, v2/3", orgId, qsize, n);
		} else if (qsize > 2000) {
			n = 6;
			log.warnf("Org=%s,  qsize=%d, n=%d, v2/3", orgId, qsize, n);
		} else if (qsize > 1500) {
			n = 5;
			log.warnf("Org=%s,  qsize=%d, n=%d, v2/3", orgId, qsize, n);
		} else if (qsize > 1000) {
			n = 4;
			log.warnf("Org=%s,  qsize=%d, n=%d, v2/3", orgId, qsize, n);
		} else if (qsize > 500) {
			n = 3;
			log.warnf("Org=%s,  qsize=%d, n=%d v2/3", orgId, qsize, n);
		}
		
		return n;
	}
	
	private LocationList convertToLocationList(DeviceGpsRecordsPoints lastpoint) {
		LocationList item = new LocationList();
		item.setAltitude(lastpoint.getAt());
		item.setH_dop(lastpoint.getHd());
		item.setH_uncertain(lastpoint.getHu());
		item.setLatitude(lastpoint.getLa());
		item.setLongitude(lastpoint.getLo());
		item.setSpeed(lastpoint.getSp());
		item.setStatus(lastpoint.getSt());
		item.setTimestamp(lastpoint.getTimestamp().longValue());
		item.setV_uncertain(lastpoint.getVu());
		item.setFlag(lastpoint.getFlag());		
		return item;
	}
	
	private DeviceGpsRecordsPoints convertToDeviceGpsRecordsPoints(SimpleLocationList simpleLoc)
	{
		DeviceGpsRecordsPoints item = new DeviceGpsRecordsPoints();
		item.setAt(simpleLoc.getAt());
		item.setHd(simpleLoc.getHd());
		item.setHu(simpleLoc.getHu());
		item.setLa(simpleLoc.getLa());
		item.setLo(simpleLoc.getLo());
		item.setSp(simpleLoc.getSp());
		item.setSt(simpleLoc.getSt());
		item.setTimestamp(simpleLoc.getTimestamp().longValue());
		item.setVu(simpleLoc.getVu());
		item.setFlag(simpleLoc.getFlag());
		return item;
	}
	
	
	private DeviceGpsRecordsPoints convertToDeviceGpsRecordsPoints(LocationList loc) {
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
		return item;
	}
}
