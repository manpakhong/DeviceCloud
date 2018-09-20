package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.report.DeviceGpsLocations;
import com.littlecloud.control.entity.report.DeviceGpsRecordsId;
import com.littlecloud.control.entity.report.DeviceGpsRecords;
import com.littlecloud.control.entity.report.DeviceGpsRecordsPoints;
import com.littlecloud.control.entity.report.DeviceGpsRecordsPointsList;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.utils.LocationUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Criteria;

public class DeviceGpsRecordsDAO extends BaseDAO<DeviceGpsRecords, DeviceGpsRecordsId> {

	private static final Logger log = Logger.getLogger(DeviceGpsRecordsDAO.class);

	public DeviceGpsRecordsDAO() throws SQLException {
		super(DeviceGpsRecords.class);
	}

	public DeviceGpsRecordsDAO(String orgId) throws SQLException {
		super(DeviceGpsRecords.class, orgId);
	}

	public DeviceGpsRecordsDAO(String orgId, boolean readonly) throws SQLException {
		super(DeviceGpsRecords.class, orgId, readonly);
	}
	
	public List<DeviceGpsLocations> getLocationsRecordsWithStarttime(Integer netId, Integer devId, Date start) throws Exception
	{
		/* ************************************************************
		 *   Get Gps data from New table: device_gps_records
		 *   with starttime
		 *   depends on 'consolidated' in 'device_gps_locations_dates'
		 * ************************************************************ */
		try
		{
			log.debugf("11811 DeviceWs.getGpsLocation 1-getRecordsByDeviceIdWithStarttime netId=%d, devId=%d, start=%s", netId, devId, start.getTime()/1000);

			List<DeviceGpsLocations> deviceGpsLocationList = new ArrayList<DeviceGpsLocations>();
			DBConnection session = getSession();
			Integer range_start = null, range_end = null;
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(DeviceGpsRecords.class);

				query.addCriteria("network_id", Criteria.EQ, netId);
				query.addCriteria("device_id", Criteria.EQ, devId);
				if (start != null)
				{					
					Calendar start_time = Calendar.getInstance();
					Calendar end_cal = Calendar.getInstance();
					end_cal.setTime(start);

					range_start =  Integer.parseInt(("" + (start_time.getTimeInMillis())).substring(0, 10));
					range_end = Integer.parseInt(("" + (end_cal.getTimeInMillis())).substring(0, 10));
					log.debug("getLocationsRecords start time=" + start.getTime()/1000 + " range_start="+range_start+" range_end="+range_end);
					
					//String strSQL = "select * from " + query.getDBName() + ".device_gps_records dl where dl.network_id= "+ netId +" and dl.device_id = " + devId + " and unixtime >= "+range_end+ " and unixtime <= "+range_start+" order by dl.unixtime asc limit 86400";

					query.addCriteria("unixtime", Criteria.LTE, range_start);
					query.addCriteria("last_unixtime", Criteria.GTE, range_end);
					query.setLimit(86400);
					query.addOrderBy("unixtime asc");
				}//
				else
				{
					query.addOrderBy("unixtime desc");
					query.setLimit(1);
				}

				List<DeviceGpsRecords> rs = query.executeQueryAsObject();
				deviceGpsLocationList = LocationUtils.filterGpsRecordsToLocationsList(rs, range_end, range_start);
				log.debug("find by example successful, result size=" + rs.size()+" deviceGpsLocationList size="+deviceGpsLocationList.size());
			
				return deviceGpsLocationList;

			} catch (SQLException e)
			{
				throw e;
			} finally
			{
				if (session != null)
					session.close();
			}
		} catch (Exception e)
		{
			log.error("find locations failed!", e);
			
			throw e;
		}
	}
	
	public List<DeviceGpsLocations> getLocationsRecordsWithStarttime( Integer devId, Date start) throws Exception
	{
		/* ************************************************************
		 *   Get Gps data from New table: device_gps_records
		 *   with starttime
		 *   depends on 'consolidated' in 'device_gps_locations_dates'
		 * ************************************************************ */
		try
		{
			log.debugf("11812 NetworkWS.getGpsLocation 2-getLocationsRecordsWithStarttime() called with param_devId=%d param_start=%s ",devId,start);
			List<DeviceGpsRecords> rs;
			List<DeviceGpsLocations> deviceGpsLocationList = new ArrayList<DeviceGpsLocations>();
			DBConnection session = getSession();
			Integer range_start = null, range_end = null;
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(DeviceGpsRecords.class);

				query.addCriteria("device_id", Criteria.EQ, devId);
				if (start != null)
				{
					/* ***** ForStart() ***** */
					log.info("Location start time:" + start);

					Calendar start_time = Calendar.getInstance();
					Calendar end_cal = Calendar.getInstance();
					end_cal.setTime(start);

					range_start =  Integer.parseInt(("" + (start_time.getTimeInMillis())).substring(0, 10));
					range_end = Integer.parseInt(("" + (end_cal.getTimeInMillis())).substring(0, 10));
					
					query.addCriteria("unixtime", Criteria.LTE, range_start);
					query.addCriteria("last_unixtime", Criteria.GTE, range_end);
					query.setLimit(86400);
					query.addOrderBy("unixtime asc");
					rs = query.executeQueryAsObject();

					deviceGpsLocationList = LocationUtils.filterGpsRecordsToLocationsList(rs, range_end, range_start);
				}//String strSQL = "select * from " + query.getDBName() + ".device_gps_locations_dates dl where dl.network_id= "+ netId +" and dl.device_id = " + devId + " order by dl.unixtime asc limit 86400";
				else
				{
					/* ***** ForLatest() ***** */
					query.addOrderBy("unixtime desc");
					query.setLimit(1);
					rs = query.executeQueryAsObject();

					deviceGpsLocationList = LocationUtils.convertGpsRecordsToLocationsList(rs);
					Collections.reverse(deviceGpsLocationList);
//					List<DeviceGpsLocations> deviceGpsRecordsList = LocationUtils.convertGpsRecordsToLocationsList(rs);
//					
//					if (deviceGpsRecordsList != null && deviceGpsRecordsList.size()!=0)
//						deviceGpsLocationList.add(deviceGpsRecordsList.get(deviceGpsRecordsList.size()-1));
				}
				
				log.debug("find by example successful, result size=" + rs.size()+" deviceGpsLocationList ="+deviceGpsLocationList);
				return deviceGpsLocationList;

			} catch (SQLException e)
			{
				throw e;
			} finally
			{
				if (session != null)
					session.close();
			}
		} catch (Exception e)
		{
			log.error("find locations failed!", e);
		
			throw e;
		}
	}
	
	public DeviceGpsLocations getLatestNotNullLocation(Integer devId)
	{
		DeviceGpsLocations loc = null;
		DeviceGpsRecords rec = null;
		DBConnection session = null;
		
		try
		{
			session = getSession();
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceGpsRecords.class);
			if( devId != null )
			{
				String sql = "select * from " + query.getDBName() + ".device_gps_records where " +
						"(first_latitude <> 'null' or first_longitude <> 'null' or last_latitude <> 'null' or last_longitude <> 'null') and device_id = "+ devId +" order by unixtime desc limit 1;";
//				System.out.println(sql);
				rec = (DeviceGpsRecords)query.executeQueryAsSingleObject(sql);
				List<DeviceGpsLocations> loclist = LocationUtils.convertGpsRecordsToLocationsList(rec);
				if (loclist != null && loclist.size()!=0)
				{
					
					for (int n = loclist.size()-1; n>-1 ; n--)
					{
						loc = loclist.get(n);
						if (loc.getLatitude() != null && loc.getLongitude() != null)
							break;
					}
				}
			}
		}
		catch( Exception e )
		{
			log.error("find latest not null point failed", e);
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				log.error("find latest not null point failed throw exception ", e1);
			}
		} 
		finally
		{
			if (session != null)
				try {
					session.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					log.error("find latest not null point failed SQLException", e);
				}
		}
		
		return loc;
	}
	
	public Integer getLastUnixTime(Integer devId)
	{
		DeviceGpsRecordsPoints loc = null;
		DeviceGpsRecords rec = null;
		DBConnection session = null;
		Integer lastUnixTime = null;
		try
		{
			log.debug("DeviceGpsRecordsDAO.getLastUnixTime() with devId = "+devId);
			session = getSession();
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceGpsRecords.class);
			if( devId != null )
			{
				String sql = "select * from " + query.getDBName() + ".device_gps_records where " +
						"(first_latitude <> 'null' or first_longitude <> 'null' or last_latitude <> 'null' or last_longitude <> 'null') and device_id = "+ devId +" order by unixtime desc limit 1;";

				rec = (DeviceGpsRecords)query.executeQueryAsSingleObject(sql);
				if (rec != null && rec.getLast_unixtime() != 0)
				{
					lastUnixTime = rec.getLast_unixtime();
					return lastUnixTime;
				}
				String location_data = rec.getLocation_data();
				DeviceGpsRecordsPointsList pointslst = JsonUtils.fromJson(location_data, DeviceGpsRecordsPointsList.class);
				List<DeviceGpsRecordsPoints> loclist = pointslst.getRecords();
				if (loclist != null && loclist.size()!=0)
				{
					
					for (int n = loclist.size()-1; n>-1 ; n--)
					{
						loc = loclist.get(n);
						if (loc.getLa() != null && loc.getLo() != null)
							break;
					}
				}
			}
		}
		catch( Exception e )
		{
			log.error("get Last UnixTime failed devId = " + devId +" lastUnixTime = "+ lastUnixTime, e);
		} 
		finally
		{
			if (session != null)
				try {
					session.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					log.error("get Last UnixTime failed devId = " + devId +" lastUnixTime = "+ lastUnixTime, e);
				}
		}
		if (loc != null)
			lastUnixTime = loc.getTimestamp().intValue();
		return lastUnixTime;
	}
	
	public String getLocationsAllTheDayToJson(Integer devId, int start, int end, String resp_code, String caller_ref, String server_ref, String timezone ) throws SQLException
	{
		/* ************************************************************
		 *   Get Gps data from New table: device_gps_records
		 *   For All Day data
		 *   depends on 'consolidated' in 'device_gps_locations_dates'
		 * ************************************************************ */
		StringBuilder resultsJson = null;
		Float la = null;
		Float lo = null;
		Float at = null;
		Float sp = null;
		Float hu = null;
		Boolean isNewTrack = null;
		//ResultSet rs = null;
		List<DeviceGpsLocations> deviceGpsLocationList = new ArrayList<DeviceGpsLocations>();
		log.debug("getGpsLocation ForAllDay ");
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceGpsRecords.class);
			query.addCriteria("device_id", Criteria.EQ, devId);
			if (start != 0 && end != 0)
			{
				log.debug("start param=" + start);
				resultsJson = new StringBuilder();
				query.setTableName("device_gps_records");								
				query.addCriteria("last_unixtime", Criteria.GTE, start);
				query.addCriteria("unixtime", Criteria.LTE, end);
				query.addOrderBy("unixtime asc");
				//rs = query.executeQuery();
				List<DeviceGpsRecords> recordslst = query.executeQueryAsObject();
				log.debug("getGpsLocation ForAllDay find by example successful, recordslst size= "+recordslst.size());
				/* ****** Do convert to keep consistency with old gps data format****** */
				deviceGpsLocationList = LocationUtils.filterGpsRecordsToLocationsList(recordslst, start, end);
				
				resultsJson.append("{");
				resultsJson.append("\"resp_code\":"+"\""+resp_code+"\""+",");
				resultsJson.append("\"caller_ref\":"+"\""+caller_ref+"\""+",");
				resultsJson.append("\"server_ref\":"+"\""+server_ref+"\""+",");
				resultsJson.append("\"data\":[");

				
				
				SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				form.setTimeZone(TimeZone.getTimeZone(DateUtils.getTimezoneFromId(Integer.parseInt(timezone))));
				String ts = null;
				int count = 0;
				
				for (DeviceGpsLocations rs : deviceGpsLocationList) 
				{
					int unixtime = rs.getId().getUnixtime();
					ts = null;
					la = null;
					lo = null;
					sp = null;
					at = null;
					hu = null;
					isNewTrack = null;
					
					StringBuilder elementSB = new StringBuilder();
					String element = null;					
					elementSB.append("{");
					
					if( unixtime != 0 )
					{
						long time = (long)unixtime * 1000;						
						Date locDataUTCTime = new Date(time);						
						ts = form.format(locDataUTCTime);
					}
					
					if( rs.getLatitude() != null )
					{
						la = rs.getLatitude();
						elementSB.append("\"la\":");
						elementSB.append(la);
						elementSB.append(",");
					}
					
					if( rs.getLongitude() != null )
					{
						lo = rs.getLongitude();						
						elementSB.append("\"lo\":");
						elementSB.append(lo);
						elementSB.append(",");
					}
					
					if( rs.getSpeed() != null )
					{
						sp = rs.getSpeed();
						elementSB.append("\"sp\":");
						elementSB.append(sp);
						elementSB.append(",");
					}
					
					if( rs.getAltitude() != null )
					{
						at = rs.getAltitude();
						elementSB.append("\"at\":");
						elementSB.append(at);
						elementSB.append(",");
					}
					
					if( rs.getHUncertain() != null )
					{
						hu = rs.getHUncertain();
						elementSB.append("\"hu\":");
						elementSB.append(hu);
						elementSB.append(",");
					}
					
					if( rs.getType() != null )
					{
						Integer iType = rs.getType();

						elementSB.append("\"cp\":");
						elementSB.append(iType);
						elementSB.append(",");
					}
					
					elementSB.append("\"ts\":\"");
					elementSB.append(ts);
					elementSB.append("\"},");
					
					element = elementSB.toString();
					resultsJson.append(element);
					count++;
				}

				if( count > 0 )
					resultsJson.deleteCharAt(resultsJson.length()-1);
				resultsJson.append("]");
				resultsJson.append("}");
			}

			log.debug("getGpsLocation ForAllDay find by example successful, deviceGpsLocationList size= " + deviceGpsLocationList.size());
		} catch (Exception e)
		{
			log.error("find locations failed!", e);
		
		} finally
		{
			if(session != null)
			{
				session.close();
				session = null;
			}
		}
		return resultsJson.toString();
	}
}
