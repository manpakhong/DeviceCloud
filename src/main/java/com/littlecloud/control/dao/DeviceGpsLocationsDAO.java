package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.CacheMode;
//import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.logging.Logger;

import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.report.DeviceGpsLocations;
import com.littlecloud.control.entity.report.DeviceGpsLocationsId;
import com.littlecloud.control.entity.report.DeviceGpsRecords;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.utils.Utils;
import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Criteria;

/**
 * Home object for domain model class Devices.
 * 
 * @see com.littlecloud.control.entity.Devices
 * @author Hibernate Tools
 */
public class DeviceGpsLocationsDAO extends BaseDAO<DeviceGpsLocations, DeviceGpsLocationsId> {

	private static final Logger log = Logger.getLogger(DeviceGpsLocationsDAO.class);

	public DeviceGpsLocationsDAO() throws SQLException {
		super(DeviceGpsLocations.class);
	}

	public DeviceGpsLocationsDAO(String orgId) throws SQLException {
		super(DeviceGpsLocations.class, orgId);
	}

	public DeviceGpsLocationsDAO(String orgId, boolean readonly) throws SQLException {
		super(DeviceGpsLocations.class, orgId, readonly);
	}

	public DeviceGpsLocations getLastDeviceLocations(Devices dev) throws SQLException {
		log.debug("finding last device gps location with id " + this.persistentClass.getSimpleName());

		try {

			// Hibernate
			// DeviceLocations devLoc = (DeviceLocations) session
			// .createCriteria(this.persistentClass)
			// .add(Restrictions.eq("deviceId", dev.getId()))
			// .addOrder(Order.desc("datetime")).setMaxResults(1).uniqueResult();

			// JDBC
			DeviceGpsLocations devLoc = this.getLastDeviceIdLocations(dev.getId());

			log.debug("find MAX successful");
			return devLoc;

		} catch (RuntimeException re) {
			log.error("find MAX failed 1", re);
			throw re;
		}
	}

	public DeviceGpsLocations getLastDeviceIdLocations(Integer devId) throws SQLException {
		log.debug("finding last device gps location with id " + this.persistentClass.getSimpleName());

		DBQuery query = null;
		DeviceGpsLocations devLoc = null;
		try {
			// Hibernate
			// DeviceLocations devLoc = (DeviceLocations) session
			// .createCriteria(this.persistentClass)
			// .add(Restrictions.eq("deviceId", devId))
			// .addOrder(Order.desc("id")).setMaxResults(1).uniqueResult();
			
			DBConnection session = getSession();
			try
			{
				if (devId != null){
					query = session.createQuery();
					query.setQueryClass(DeviceGpsLocations.class);

					String strSQL = "select * from " + query.getDBName() + ".device_gps_locations dl where dl.device_id = " + devId + " order by dl.unixtime desc limit 1";
					//System.out.println(strSQL);
	
					devLoc = (DeviceGpsLocations) query.executeQueryAsSingleObject(strSQL);
	
					log.debug("find MAX successful " + strSQL);
				} else{
					log.warn("getLastDeviceIdLocations - devId is null!");
				} 
				// TODO Bug report that the function will produce null pointer exception, add if(devId != null) logic, please check if return devLoc to null will have any other problem!!!!
				return devLoc;
			} catch (SQLException e)
			{
				throw e;
			} finally
			{
				if (session != null)
					session.close();
			}

		} catch (RuntimeException re) {
			log.error("find MAX failed 2 ", re);
			if (devId != null && query != null && query.getDBName() != null){
				log.error("find MAX failed 2 "+devId+" "+query.getDBName(),re);
			} else{
				log.error("find MAX failed 2 - devId is null?!!",re);
			}
				
			throw re;
		}
	} // end getLastDeviceIdLocations

	public List<DeviceGpsLocations> getByDevicesAndStarttime(Devices dev, Date start) throws SQLException
	{
		/* Return 24 hours data from start time, or latest device location if start time is not specified. */
		// DeviceLocations locExample = new DeviceLocations();
		// locExample.setDevices(dev);
		// DeviceLocations loc = findMaxByExample(locExample);
		//
		// HashSet<DeviceLocations> resultSet = new HashSet<DeviceLocations>();
		// resultSet.addAll(Arrays.asList(loc));
		// return resultSet;

		try {
			// Hibernate
			// Criteria cri = session.createCriteria(this.persistentClass);
			// cri.add(Restrictions.eq("deviceId", dev.getId()));
			//
			// if (start != null)
			// {
			// log.debug("start param=" + start);
			// cri.add(Restrictions.ge("datetime", start)).list();
			// }
			//
			// List<DeviceLocations> results = cri.list();

			// JDBC
			List<DeviceGpsLocations> results = this.getByDevicesIdAndStarttime(dev.getId(), start);

			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed 3", re);
			throw re;
		}
	}

	public List<DeviceGpsLocations> getByDevicesIdAndStarttime(Integer devId, Date start) throws SQLException
	{
		/* Return 24 hours data from start time, or latest device location if start time is not specified. */
		// DeviceLocations locExample = new DeviceLocations();
		// locExample.setDevices(dev);
		// DeviceLocations loc = findMaxByExample(locExample);
		//
		// HashSet<DeviceLocations> resultSet = new HashSet<DeviceLocations>();
		// resultSet.addAll(Arrays.asList(loc));
		// return resultSet;

		try {
			// Hibernate
			// Criteria cri = session.createCriteria(this.persistentClass);
			// cri.add(Restrictions.eq("deviceId", devId));
			//
			// if (start != null)
			// {
			// log.debug("start param=" + start);
			// cri.add(Restrictions.ge("datetime", start)).list();
			// }
			//
			// List<DeviceLocations> results = cri.list();

			// JDBC
			DBConnection session = getSession();
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(DeviceGpsLocations.class);

				// String strSQL = "select * from " + query.getDBName() + ".device_locations dl where dl.device_id = " +
				// Integer.toString(devId) + " order by dl.id desc limit 1";
				// System.out.println(strSQL);

				query.addCriteria("device_id", Criteria.EQ, devId);
				if (start != null) {
					query.addCriteria("datetime", Criteria.GTE, start);
				}

				List<DeviceGpsLocations> results = (List<DeviceGpsLocations>) query.executeQueryAsObject();

				log.debug("find by example successful, result size: " + results.size());
				return results;
			} catch (SQLException e)
			{
				throw e;
			} finally
			{
				if (session != null)
					session.close();
			}
		} catch (RuntimeException re) {
			log.error("find by example failed 4", re);
			throw re;
		}
	}

	public DeviceGpsLocations getLatestNotNullLocation(Integer devId)
	{
		DeviceGpsLocations loc = null;
		DBConnection session = null;
		
		try
		{
			session = getSession();
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceGpsLocations.class);
			if( devId != null )
			{
//				String strSQL = "select * from " + query.getDBName() + ".device_locations dl where dl.device_id = " + Integer.toString(devId) + " order by dl.id desc limit 1";
				String sql = "select * from " + query.getDBName() + ".device_gps_locations where " +
						"(latitude <> 'null' or longitude <> 'null') and device_id = "+ devId +" order by unixtime desc limit 1;";
//				System.out.println(sql);
				loc = (DeviceGpsLocations)query.executeQueryAsSingleObject(sql);
			}
		}
		catch( Exception e )
		{
			log.error("find latest not null point failed", e);
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} 
		finally
		{
			if (session != null)
				try {
					session.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return loc;
	}
	
	public List<DeviceGpsLocations> getLocationsByDeviceIdWithStarttime(Integer devId, Date start) throws Exception
	{
		try
		{
			// Hibernate
			// Criteria cri = session.createCriteria(DeviceLocations.class);
			// cri.add(Restrictions.eq("deviceId", devId));
			// List<DeviceLocations> results = null;
			//
			// if (start != null)
			// {
			// log.debug("start param=" + start);
			//
			// Calendar start_time = Calendar.getInstance();
			// // start_time.setTime(start);
			//
			// Calendar end_cal = Calendar.getInstance();
			// end_cal.setTime(start);
			// // end_cal.add(Calendar.DAY_OF_MONTH, -1);
			//
			// System.out.println("--------"+end_cal.getTime()+"-----"+start_time.getTime()+"----");
			// // cri.add(Restrictions.le("unixtime",
			// Integer.parseInt((""+(start_time.getTimeInMillis()+TimeZone.getDefault().getRawOffset())).substring(0,
			// 10))));
			// // cri.add(Restrictions.ge("unixtime",
			// Integer.parseInt((""+(end_cal.getTimeInMillis()+TimeZone.getDefault().getRawOffset())).substring(0,
			// 10))));
			// cri.add(Restrictions.le("unixtime", Integer.parseInt((""+(start_time.getTimeInMillis())).substring(0,
			// 10))));
			// cri.add(Restrictions.ge("unixtime", Integer.parseInt((""+(end_cal.getTimeInMillis())).substring(0,
			// 10))));
			// cri.setMaxResults(5000);
			// cri.addOrder(Order.asc("unixtime"));
			// cri.addOrder(Order.asc("id"));
			// System.out.println(cri.toString());
			// results = cri.list();
			// }
			// else
			// {
			// cri.addOrder(Order.desc("unixtime"));
			// results = new ArrayList<DeviceLocations>();
			// if( cri.list().size() > 0 )
			// results.add((DeviceLocations)cri.list().get(0));
			// }

			log.debugf("11812 NetworkWS -getLocationsByDeviceIdWithStarttime() called with param_devId=%d param_start=%s ",devId,start);

			DBConnection session = getSession();
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(DeviceGpsLocations.class);

				query.addCriteria("device_id", Criteria.EQ, devId);
				if (start != null)
				{
					log.info("Location start time:" + start);

					Calendar start_time = Calendar.getInstance();

					Calendar end_cal = Calendar.getInstance();
					end_cal.setTime(start);

//					System.out.println("--------" + end_cal.getTime() + "-----" + start_time.getTime() + "----");

					query.addCriteria("unixtime", Criteria.LTE, Integer.parseInt(("" + (start_time.getTimeInMillis())).substring(0, 10)));
					query.addCriteria("unixtime", Criteria.GTE, Integer.parseInt(("" + (end_cal.getTimeInMillis())).substring(0, 10)));
					query.setLimit(86400);
					query.addOrderBy("unixtime asc");
					//query.addOrderBy("id asc");

				}
				else
				{
					query.addOrderBy("unixtime desc");
					query.setLimit(1);
				}

				List<DeviceGpsLocations> results = query.executeQueryAsObject();

				log.debug("find by example successful, result size: " + results.size());
				return results;

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
			e.printStackTrace();
			throw e;
		}
	}
	public List<DeviceGpsLocations> getLocationsByDeviceIdWithStarttime(Integer netId, Integer devId, Date start) throws Exception
	{
		/* ************************************************************
		 *   Get Gps data from Old table: device_gps_locations
		 *   with starttime
		 *   depends on 'consolidated' in 'device_gps_locations_dates'
		 * ************************************************************ */
		try
		{
			log.debugf("getGpsLocation ByDeviceIdWithStarttime netId=%d, devId=%d, start=%s", netId, devId, start.getTime()/1000);
			DBConnection session = getSession();
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(DeviceGpsLocations.class);

				query.addCriteria("network_id", Criteria.EQ, netId);
				query.addCriteria("device_id", Criteria.EQ, devId);
				if (start != null)
				{
					log.info("Location start time:" + start);

					Calendar start_time = Calendar.getInstance();

					Calendar end_cal = Calendar.getInstance();
					end_cal.setTime(start);

					
//					System.out.println("--------" + end_cal.getTime() + "-----" + start_time.getTime() + "----");

					query.addCriteria("unixtime", Criteria.LTE, Integer.parseInt(("" + (start_time.getTimeInMillis())).substring(0, 10)));
					query.addCriteria("unixtime", Criteria.GTE, Integer.parseInt(("" + (end_cal.getTimeInMillis())).substring(0, 10)));
					query.setLimit(86400);
					query.addOrderBy("unixtime asc");
					//query.addOrderBy("id asc");

				}
				else
				{
					query.addOrderBy("unixtime desc");
					query.setLimit(1);
				}

				List<DeviceGpsLocations> results = query.executeQueryAsObject();

				log.debug("find by example successful, result size: " + results.size());
				return results;

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
	
	public List<DeviceGpsLocations> getLocationsByDeviceIdWithStarttime(Integer netId, Integer devId, Date start, Date end) throws Exception
	{
		/* ************************************************************
		 *   Get Gps data from Old table: device_gps_locations
		 *   with starttime and endtime
		 *   depends on 'consolidated' in 'device_gps_locations_dates'
		 * ************************************************************ */
		try
		{
			log.debugf("getGpsLocation ByDeviceIdWithStarttime netId=%d, devId=%d, start=%s end=%s", netId, devId, start.getTime()/1000, end.getTime()/1000);

			DBConnection session = getSession();
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(DeviceGpsLocations.class);

				query.addCriteria("network_id", Criteria.EQ, netId);
				query.addCriteria("device_id", Criteria.EQ, devId);
				if (start != null)
				{
					log.info("Location start time:" + start);

					Calendar start_time = Calendar.getInstance();
					start_time.setTime(end);
					Calendar end_cal = Calendar.getInstance();
					end_cal.setTime(start);
					
//					System.out.println("--------" + end_cal.getTime() + "-----" + start_time.getTime() + "----");

					query.addCriteria("unixtime", Criteria.LTE, Integer.parseInt(("" + (start_time.getTimeInMillis())).substring(0, 10)));
					query.addCriteria("unixtime", Criteria.GTE, Integer.parseInt(("" + (end_cal.getTimeInMillis())).substring(0, 10)));
					query.setLimit(86400);
					query.addOrderBy("unixtime asc");
				}
				else
				{
					query.addOrderBy("unixtime desc");
					query.setLimit(1);
				}

				List<DeviceGpsLocations> results = query.executeQueryAsObject();

				log.debug("find by example successful, result size: " + results.size());
				return results;

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
	
	public List<DeviceGpsLocations> getLocationsByDeviceIdWithStarttime(Integer devId, Date start, Date end) throws Exception
	{
		/* ************************************************************
		 *   Get Gps data from Old table: device_gps_locations
		 *   with starttime and endtime
		 *   depends on 'consolidated' in 'device_gps_locations_dates'
		 * ************************************************************ */
		log.debugf("118112 NetworkWS -getLocationsByDeviceIdWithStarttime() called with param_devId=%d param_start=%s param_end=%s ",devId,start,end);
		try
		{
			DBConnection session = getSession();
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(DeviceGpsLocations.class);

				query.addCriteria("device_id", Criteria.EQ, devId);
				if (start != null)
				{
					log.info("Location start time:" + start);

					Calendar start_time = Calendar.getInstance();
					start_time.setTime(end);
					Calendar end_cal = Calendar.getInstance();
					end_cal.setTime(start);
					
//					System.out.println("--------" + end_cal.getTime() + "-----" + start_time.getTime() + "----");

					query.addCriteria("unixtime", Criteria.LTE, Integer.parseInt(("" + (start_time.getTimeInMillis())).substring(0, 10)));
					query.addCriteria("unixtime", Criteria.GTE, Integer.parseInt(("" + (end_cal.getTimeInMillis())).substring(0, 10)));
					query.setLimit(86400);
					query.addOrderBy("unixtime asc");
				}
				else
				{
					query.addOrderBy("unixtime desc");
					query.setLimit(1);
				}

				List<DeviceGpsLocations> results = query.executeQueryAsObject();

				log.debug("find by example successful, result size: " + results.size());
				return results;

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

	public List<DeviceGpsLocations> getLocationsByDeviceIdWithStarttime(Integer devId, Date start, int limit) throws Exception
	{
		try
		{
			// Hibernate
			// Criteria cri = session.createCriteria(DeviceLocations.class);
			// cri.add(Restrictions.eq("deviceId", devId));
			// List<DeviceLocations> results = null;
			//
			// if (start != null)
			// {
			// log.debug("start param=" + start);
			//
			// Calendar start_time = Calendar.getInstance();
			// // start_time.setTime(start);
			//
			// Calendar end_cal = Calendar.getInstance();
			// end_cal.setTime(start);
			// // end_cal.add(Calendar.DAY_OF_MONTH, -1);
			//
			// System.out.println("--------"+end_cal.getTime()+"-----"+start_time.getTime()+"----");
			// // cri.add(Restrictions.le("unixtime",
			// Integer.parseInt((""+(start_time.getTimeInMillis()+TimeZone.getDefault().getRawOffset())).substring(0,
			// 10))));
			// // cri.add(Restrictions.ge("unixtime",
			// Integer.parseInt((""+(end_cal.getTimeInMillis()+TimeZone.getDefault().getRawOffset())).substring(0,
			// 10))));
			// cri.add(Restrictions.le("unixtime", Integer.parseInt((""+(start_time.getTimeInMillis())).substring(0,
			// 10))));
			// cri.add(Restrictions.ge("unixtime", Integer.parseInt((""+(end_cal.getTimeInMillis())).substring(0,
			// 10))));
			// cri.setMaxResults(5000);
			// cri.addOrder(Order.asc("unixtime"));
			// cri.addOrder(Order.asc("id"));
			// System.out.println(cri.toString());
			// results = cri.list();
			// }
			// else
			// {
			// cri.addOrder(Order.desc("unixtime"));
			// results = new ArrayList<DeviceLocations>();
			// if( cri.list().size() > 0 )
			// results.add((DeviceLocations)cri.list().get(0));
			// }

			// JDBC
			DBConnection session = getSession();
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(DeviceGpsLocations.class);

				query.addCriteria("device_id", Criteria.EQ, devId);
				if (start != null)
				{
					log.info("Location start time:" + start);

					Calendar start_time = Calendar.getInstance();

					Calendar end_cal = Calendar.getInstance();
					end_cal.setTime(start);

//					System.out.println("--------" + end_cal.getTime() + "-----" + start_time.getTime() + "----");

					query.addCriteria("unixtime", Criteria.LTE, Integer.parseInt(("" + (start_time.getTimeInMillis())).substring(0, 10)));
					query.addCriteria("unixtime", Criteria.GTE, Integer.parseInt(("" + (end_cal.getTimeInMillis())).substring(0, 10)));
					query.setLimit(limit);
					query.addOrderBy("unixtime asc");
					//query.addOrderBy("id asc");

				}
				else
				{
					query.addOrderBy("unixtime desc");
					query.setLimit(1);
				}

				List<DeviceGpsLocations> results = query.executeQueryAsObject();

				log.debug("find by example successful, result size: " + results.size());
				return results;

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

	public List<DeviceGpsLocations> getLocationsByDeviceIdWithMonth(Integer devId, Integer month) throws Exception
	{
		DBConnection session = getSession();
		List<DeviceGpsLocations> results = new ArrayList<DeviceGpsLocations>();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceGpsLocations.class);
			if( month != null )
			{
//				String strSQL = "select * from " + query.getDBName() + ".device_locations dl where dl.device_id = " + Integer.toString(devId) + " order by dl.id desc limit 1";
				String sql = "SELECT * FROM " + query.getDBName() + ".device_gps_locations where device_id = " + 
						Integer.toString(devId) + " and unixtime > '" + month.toString() + 
						"' group by CONCAT_WS('-',YEAR(datetime), MONTH(datetime), DAY(datetime), HOUR(datetime)) order by unixtime asc";
				//System.out.println(sql);
				results = query.executeQueryAsObject(sql);
			}
			
			log.debug("find by month successful, result size: " + results.size());
			
		}
		catch( Exception e )
		{
			log.error("find locations failed!", e);
			
			throw e;
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return results;
	}
	
	// No reference
	// public List<DeviceLocations> getLocationsAllTheDay(Integer devId, Date start)
	// {
	// try
	// {
	// Criteria cri = session.createCriteria(DeviceLocations.class);
	// cri.add(Restrictions.eq("deviceId", devId));
	// List<DeviceLocations> results = null;
	//
	// if (start != null)
	// {
	// log.debug("start param=" + start);
	//
	// Calendar start_time = Calendar.getInstance();
	// start_time.setTime(start);
	// start_time.set(Calendar.HOUR_OF_DAY, 23);
	// start_time.set(Calendar.MINUTE, 59);
	// start_time.set(Calendar.SECOND, 59);
	//
	// Calendar end_cal = Calendar.getInstance();
	// end_cal.setTime(start);
	// end_cal.set(Calendar.HOUR_OF_DAY, 0);
	// end_cal.set(Calendar.MINUTE, 0);
	// end_cal.set(Calendar.SECOND, 0);
	//
	// System.out.println("--------"+end_cal.getTime()+"-----"+start_time.getTime()+"----");
	//
	// Date st = DateUtils.getUtcDate(start_time.getTime());
	// Date et = DateUtils.getUtcDate(end_cal.getTime());
	//
	// start_time.setTime(st);
	// end_cal.setTime(et);
	//
	// cri.add(Restrictions.le("unixtime", Integer.parseInt((""+(start_time.getTimeInMillis())).substring(0, 10))));
	// cri.add(Restrictions.ge("unixtime", Integer.parseInt((""+(end_cal.getTimeInMillis())).substring(0, 10))));
	// cri.setMaxResults(5000);
	// cri.addOrder(Order.asc("unixtime"));
	// cri.addOrder(Order.asc("id"));
	// System.out.println(cri.toString());
	// results = cri.list();
	// }
	// else
	// {
	// cri.addOrder(Order.desc("unixtime"));
	// results = new ArrayList<DeviceLocations>();
	// if( cri.list().size() > 0 )
	// results.add((DeviceLocations)cri.list().get(0));
	// }
	//
	// log.debug("find by example successful, result size: " + results.size());
	// return results;
	// }
	// catch(Exception e)
	// {
	// log.error("find locations failed!",e);
	// e.printStackTrace();
	// throw e;
	// }
	// }

	// No reference
	public List<DeviceGpsLocations> getLocationsAllTheDay(Integer devId, int start, int end) throws SQLException
	{
		List<DeviceGpsLocations> results = null;
		DBConnection session = getSession();
		try
		{
			// Hibernate
			// Criteria cri = session.createCriteria(DeviceLocations.class);
			// cri.add(Restrictions.eq("deviceId", devId));
			// List<DeviceLocations> results = null;
			//
			// if (start != 0 && end != 0)
			// {
			// log.debug("start param=" + start);
			//
			// cri.add(Restrictions.le("unixtime", start));
			// cri.add(Restrictions.ge("unixtime", end));
			// cri.setMaxResults(5000);
			// cri.addOrder(Order.asc("unixtime"));
			// cri.addOrder(Order.asc("id"));
			// System.out.println(cri.toString());
			// results = cri.list();
			// }
			// else
			// {
			// cri.addOrder(Order.desc("unixtime"));
			// results = new ArrayList<DeviceLocations>();
			// if( cri.list().size() > 0 )
			// results.add((DeviceLocations)cri.list().get(0));
			// }

			// JDBC
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceGpsLocations.class);
			query.addCriteria("device_id", Criteria.EQ, devId);
			if (start != 0 && end != 0)
			{
				log.debug("start param=" + start);
				results = new ArrayList<DeviceGpsLocations>();
				query.setTableName("device_gps_locations");
				query.addCriteria("unixtime", Criteria.LE, start);
				query.addCriteria("unixtime", Criteria.GE, end);
				//query.setLimit(5000);
				query.addOrderBy("unixtime asc");
				//query.addOrderBy("id asc");
//				System.out.println(query.toString());
				results = query.executeQueryAsObject();
				
//				Date startSQL = new Date();
//				java.sql.ResultSet rs = query.executeQuery();
//				Date endSQL = new Date();
//				StringBuilder sb = new StringBuilder();
//				
//				Date resultStart = new Date();
//				while (rs.next()) 
//				{
//					String re = rs.getFloat("latitude") + "," + rs.getFloat("longitude") + "," + rs.getFloat("altitude") + "," + rs.getFloat("speed") + "," +rs.getInt("unixtime");
//					sb.append("{"+re+"},");
//				}
//				Date resultEnd = new Date();
//				log.debug("--Get-GPS-locations-:"+"startSQL:" + startSQL + ", EndSQL:" + endSQL + ", startResult:"+resultStart+", endResult:" + resultEnd);
//				System.out.println("--Get-GPS-locations-:"+"startSQL:" + startSQL + ", EndSQL:" + endSQL + ", startResult:"+resultStart+", endResult:" + resultEnd);
			}
			else
			{
				query.addOrderBy("unixtime desc");
				results = new ArrayList<DeviceGpsLocations>();
				if (query.executeQueryAsObject().size() > 0)
					results.add((DeviceGpsLocations) query.executeQueryAsObject().get(0));
			}

			log.debug("find by example successful, result size: " + results.size());
		} catch (Exception e)
		{
			log.error("find locations failed!", e);
			
		} finally
		{
			if (session != null)
				session.close();
		}
		return results;
	}

	
	public List<DeviceGpsLocations> getConsolidatedGpsRecords(Integer netId, Integer devId, Date start) throws Exception
	{
		/* *** getLocationsByDeviceIdWithStarttime *** */
		try
		{
			DBConnection session = getSession();
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(DeviceGpsRecords.class);

				query.addCriteria("network_id", Criteria.EQ, netId);
				query.addCriteria("device_id", Criteria.EQ, devId);
				if (start != null)
				{
					log.info("Location start time:" + start);

					Calendar start_time = Calendar.getInstance();
					Calendar end_cal = Calendar.getInstance();
					end_cal.setTime(start);
					
					query.addCriteria("unixtime", Criteria.LTE, Integer.parseInt(("" + (start_time.getTimeInMillis())).substring(0, 10)));
					query.addCriteria("unixtime", Criteria.GTE, Integer.parseInt(("" + (end_cal.getTimeInMillis())).substring(0, 10)));
					query.setLimit(86400);
					query.addOrderBy("unixtime asc");
					//query.addOrderBy("id asc");

				}
				else
				{
					query.addOrderBy("unixtime desc");
					query.setLimit(1);
				}

				List<DeviceGpsLocations> results = query.executeQueryAsObject();

				log.debug("find by example successful, result size: " + results.size());
				return results;

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
			log.error("find locations failed! DeviceGpsLocationsDAO.getLocationRecordsByDeviceIdWithStarttime() netId="+netId+" devId ="+devId+" start="+ start+ e);
			
			throw e;
		}
	
	}
	public String getConsolidatedGpsRecords(Integer devId, int start, int end, String resp_code, String caller_ref, String server_ref, String timezone ) throws SQLException
	{
		/* *** getLocationRecordsAlltheDayToJson *** */
		StringBuilder resultsJson = null;
		Float la = null;
		Float lo = null;
		Float at = null;
		Float sp = null;
		Float hu = null;
		Boolean isNewTrack = null;
		ResultSet rs = null;

		DBConnection session = getSession();
				try
				{
					DBQuery query = session.createQuery();
					query.setQueryClass(DeviceGpsLocations.class);
					query.addCriteria("device_id", Criteria.EQ, devId);
					if (start != 0 && end != 0)
					{
						log.debug("start param=" + start);
						resultsJson = new StringBuilder();
						query.setTableName("device_gps_records");
						query.addCriteria("unixtime", Criteria.LE, start);
						query.addCriteria("unixtime", Criteria.GE, end);
//						query.setLimit(86400);
						query.addOrderBy("unixtime asc");
//						System.out.println(query.toString());
						
						resultsJson.append("{");
						resultsJson.append("\"resp_code\":"+"\""+resp_code+"\""+",");
						resultsJson.append("\"caller_ref\":"+"\""+caller_ref+"\""+",");
						resultsJson.append("\"server_ref\":"+"\""+server_ref+"\""+",");
						resultsJson.append("\"data\":[");
						
//						System.out.println("jsonjson:"+resultsJson);

						long beforeExecSQL = System.currentTimeMillis();
						rs = query.executeQuery();
						long afterExecSQL = System.currentTimeMillis();
						
						SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
						form.setTimeZone(TimeZone.getTimeZone(DateUtils.getTimezoneFromId(Integer.parseInt(timezone))));
						String ts = null;

						long beforeResultSet = System.currentTimeMillis();
						int count = 0;
						while (rs.next()) 
						{
							int unixtime = rs.getInt("unixtime");
							ts = null;
							la = null;
							lo = null;
							sp = null;
							at = null;
							hu = null;
							isNewTrack = null;
							
							StringBuilder elementSB = new StringBuilder();
							String element = "{";					
							elementSB.append(element);
							
							if( unixtime != 0 )
							{
								long time = (long)unixtime * 1000;						
								Date locDataUTCTime = new Date(time);						
								ts = form.format(locDataUTCTime);
							}
							
							if( rs.getObject("latitude") != null )
							{
								la = rs.getFloat("latitude");
								//element += "\"la\":" + la + ",";
								elementSB.append("\"la\":");
								elementSB.append(la);
								elementSB.append(",");
							}
							
							if( rs.getObject("longitude") != null )
							{
								lo = rs.getFloat("longitude");						
								//element += "\"lo\":" + lo + ",";
								elementSB.append("\"lo\":");
								elementSB.append(lo);
								elementSB.append(",");
							}
							
							if( rs.getObject("speed") != null )
							{
								sp = rs.getFloat("speed");
								//element += "\"sp\":" + sp + ",";
								elementSB.append("\"sp\":");
								elementSB.append(sp);
								elementSB.append(",");
							}
							
							if( rs.getObject("altitude") != null )
							{
								at = rs.getFloat("altitude");
								//element += "\"at\":" + at + ",";
								elementSB.append("\"at\":");
								elementSB.append(at);
								elementSB.append(",");
							}
							
							if( rs.getObject("h_uncertain") != null )
							{
								hu = rs.getFloat("h_uncertain");
								//element += "\"hu\":" + hu + ",";
								elementSB.append("\"hu\":");
								elementSB.append(hu);
								elementSB.append(",");
							}
							
							if( rs.getObject("type") != null )
							{
								Integer iType = rs.getInt("type");
//								//element += "\"isNewTrack\":" + bType + ",";
//								boolean bType = iType == 1? true:false;
//								elementSB.append("\"isNewTrack\":");
//								elementSB.append(bType);
								elementSB.append("\"cp\":");
								elementSB.append(iType);
								elementSB.append(",");
							}
							//element = "\"ts\":" + "\""+ ts + "\"" + "," + "\"expired\":" + isExpired + "},";
							element = "\"ts\":" + "\""+ ts + "\"" + "},";
							elementSB.append(element);
							//resultsJson.append(element);
							resultsJson.append(elementSB.toString());
							count++;
						}
						long afterResultSet = System.currentTimeMillis();
						if( count > 0 )
							resultsJson.deleteCharAt(resultsJson.length()-1);
						resultsJson.append("]");
						resultsJson.append("}");

//						System.out.println("-------- Result:::: Exec SQL" + beforeExecSQL + ", " + afterExecSQL + "=" + (afterExecSQL - beforeExecSQL));
//						System.out.println("-------- Result:::: Exec ResultSet" + beforeResultSet + ", " + afterResultSet + "=" + (afterResultSet - beforeResultSet));
//						System.out.println("-------- Result:::: Total Size:" + count + ", " + resultsJson.length());
					}

					log.debug("find by example successful, result size: " + resultsJson.length());
				} catch (Exception e)
				{
					log.error("find locations failed!", e);
					
				} finally
				{
					if(rs != null)
					{
						rs.close();
						rs = null;
					}
					if(session != null)
					{
						session.close();
						session = null;
					}
				}
				return resultsJson.toString();
			
	}
	
	public String getLocationsAllTheDayToJson(Integer devId, int start, int end, String resp_code, String caller_ref, String server_ref, String timezone ) throws SQLException
	{// boolean isExpired,
		StringBuilder resultsJson = null;
		Float la = null;
		Float lo = null;
		Float at = null;
		Float sp = null;
		Float hu = null;
		Boolean isNewTrack = null;
		ResultSet rs = null;
		
		DBConnection session = getSession();
		try
		{

			// JDBC
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceGpsLocations.class);
			query.addCriteria("device_id", Criteria.EQ, devId);
			if (start != 0 && end != 0)
			{
				log.debug("start param=" + start);
				resultsJson = new StringBuilder();
				query.setTableName("device_gps_locations");
				query.addCriteria("unixtime", Criteria.LE, start);
				query.addCriteria("unixtime", Criteria.GE, end);
//				query.setLimit(86400);
				query.addOrderBy("unixtime asc");
//				System.out.println(query.toString());
				
				resultsJson.append("{");
				resultsJson.append("\"resp_code\":"+"\""+resp_code+"\""+",");
				resultsJson.append("\"caller_ref\":"+"\""+caller_ref+"\""+",");
				resultsJson.append("\"server_ref\":"+"\""+server_ref+"\""+",");
				resultsJson.append("\"data\":[");
				
//				System.out.println("jsonjson:"+resultsJson);

				long beforeExecSQL = System.currentTimeMillis();
				rs = query.executeQuery();
				long afterExecSQL = System.currentTimeMillis();
				
				SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				form.setTimeZone(TimeZone.getTimeZone(DateUtils.getTimezoneFromId(Integer.parseInt(timezone))));
				String ts = null;

				long beforeResultSet = System.currentTimeMillis();
				int count = 0;
				while (rs.next()) 
				{
					int unixtime = rs.getInt("unixtime");
					ts = null;
					la = null;
					lo = null;
					sp = null;
					at = null;
					hu = null;
					isNewTrack = null;
					
					StringBuilder elementSB = new StringBuilder();
					String element = "{";					
					elementSB.append(element);
					
					if( unixtime != 0 )
					{
						long time = (long)unixtime * 1000;						
						Date locDataUTCTime = new Date(time);						
						ts = form.format(locDataUTCTime);
					}
					
					if( rs.getObject("latitude") != null )
					{
						la = rs.getFloat("latitude");
						//element += "\"la\":" + la + ",";
						elementSB.append("\"la\":");
						elementSB.append(la);
						elementSB.append(",");
					}
					
					if( rs.getObject("longitude") != null )
					{
						lo = rs.getFloat("longitude");						
						//element += "\"lo\":" + lo + ",";
						elementSB.append("\"lo\":");
						elementSB.append(lo);
						elementSB.append(",");
					}
					
					if( rs.getObject("speed") != null )
					{
						sp = rs.getFloat("speed");
						//element += "\"sp\":" + sp + ",";
						elementSB.append("\"sp\":");
						elementSB.append(sp);
						elementSB.append(",");
					}
					
					if( rs.getObject("altitude") != null )
					{
						at = rs.getFloat("altitude");
						//element += "\"at\":" + at + ",";
						elementSB.append("\"at\":");
						elementSB.append(at);
						elementSB.append(",");
					}
					
					if( rs.getObject("h_uncertain") != null )
					{
						hu = rs.getFloat("h_uncertain");
						//element += "\"hu\":" + hu + ",";
						elementSB.append("\"hu\":");
						elementSB.append(hu);
						elementSB.append(",");
					}
					
					if( rs.getObject("type") != null )
					{
						Integer iType = rs.getInt("type");
//						//element += "\"isNewTrack\":" + bType + ",";
//						boolean bType = iType == 1? true:false;
//						elementSB.append("\"isNewTrack\":");
//						elementSB.append(bType);
						elementSB.append("\"cp\":");
						elementSB.append(iType);
						elementSB.append(",");
					}
					//element = "\"ts\":" + "\""+ ts + "\"" + "," + "\"expired\":" + isExpired + "},";
					element = "\"ts\":" + "\""+ ts + "\"" + "},";
					elementSB.append(element);
					//resultsJson.append(element);
					resultsJson.append(elementSB.toString());
					count++;
				}
				long afterResultSet = System.currentTimeMillis();
				if( count > 0 )
					resultsJson.deleteCharAt(resultsJson.length()-1);
				resultsJson.append("]");
				resultsJson.append("}");

//				System.out.println("-------- Result:::: Exec SQL" + beforeExecSQL + ", " + afterExecSQL + "=" + (afterExecSQL - beforeExecSQL));
//				System.out.println("-------- Result:::: Exec ResultSet" + beforeResultSet + ", " + afterResultSet + "=" + (afterResultSet - beforeResultSet));
//				System.out.println("-------- Result:::: Total Size:" + count + ", " + resultsJson.length());
			}

			log.debug("find by example successful, result size: " + resultsJson.length());
		} catch (Exception e)
		{
			log.error("find locations failed!", e);
			
		} finally
		{
			if(rs != null)
			{
				rs.close();
				rs = null;
			}
			if(session != null)
			{
				session.close();
				session = null;
			}
		}
		return resultsJson.toString();
	}
	
	// No reference
	// public List<DeviceLocations> getLocationsByDeviceIdWithOffset(Integer devId, int offset)
	// {
	// try
	// {
	// Criteria cri = session.createCriteria(DeviceLocations.class);
	// cri.add(Restrictions.eq("deviceId", devId));
	// List<DeviceLocations> results = null;
	//
	// if (offset != 0)
	// {
	// log.debug("offset param=" + offset);
	//
	// Date start_time = Calendar.getInstance().getTime();
	//
	// Calendar end_cal = new GregorianCalendar();
	// end_cal.setTime(start_time);
	// end_cal.add(Calendar.HOUR_OF_DAY, -offset);
	//
	// System.out.println("---off------"+end_cal.getTime()+"------set---");
	//
	// Date end_time = end_cal.getTime();
	// System.out.println("SO---------"+Integer.parseInt((""+start_time.getTime()).substring(0, 10))+"---------");
	// System.out.println("EO---------"+end_time.getTime()+"--------");
	// cri.add(Restrictions.le("unixtime", Integer.parseInt((""+start_time.getTime()).substring(0, 10))));
	// cri.add(Restrictions.ge("unixtime", Integer.parseInt((""+end_time.getTime()).substring(0, 10))));
	// System.out.println(cri.toString());
	// results = cri.list();
	// }
	// else
	// {
	// cri.addOrder(Order.desc("unixtime"));
	// results = new ArrayList<DeviceLocations>();
	// if( cri.list().size() > 0 )
	// results.add((DeviceLocations)cri.list().get(0));
	// }
	//
	// log.debug("find by example successful, result size: " + results.size());
	// return results;
	// }
	// catch(Exception e)
	// {
	// log.error("find locations failed!",e);
	// e.printStackTrace();
	// throw e;
	// }
	// }

	public Integer deleteDevLocationBeforeTime(Integer endTime, Integer nRows) throws SQLException {

		String dbPrefix = Utils.PARAM_ORGANIZATION_DB_PREFIX;
		if (orgId.compareToIgnoreCase("test") == 0)
			dbPrefix = Utils.PARAM_ORGANIZATION_DB_TEST_PREFIX;

		log.debug("deleting device gps location before time " + endTime + " Rows " + nRows);

		// Hibernate
		// String sql = "delete from "+dbPrefix+orgId+".device_locations where unixtime < :endTime limit "+nRows;
		// Query query = session.createSQLQuery(sql);
		// query.setCacheable(false);
		// query.setCacheMode(CacheMode.IGNORE);
		// query.setParameter("endTime", endTime);
		// return query.executeUpdate();

		// JDBC
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();

			String strSQL = "delete from " + dbPrefix + orgId + ".device_gps_locations where unixtime < " + endTime + " limit " + nRows;
			//System.out.println("StrSql = "+strSQL);
			return query.executeUpdate(strSQL);

		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session != null)
				session.close();
		}

	}

	public List<Integer> getGpsSupportedDevicesByNetworkId(Integer network_id) throws Exception
	{
		DBConnection session = getSession();
		List<Integer> results = new ArrayList<Integer>();
		ResultSet rs = null;
		try
		{
			DBQuery query = session.createQuery();
			String sql = "SELECT distinct g.device_id  FROM " + query.getDBName() + ".device_gps_locations g " +
					"where g.device_id in (select d.id from "+query.getDBName()+".devices d where d.network_id = "+ network_id+" and d.active=1)";
			rs = query.executeQuery(sql);
			Integer col = null;
			while(rs.next())
			{
				col = rs.getInt("device_id");
				results.add(Integer.valueOf(col));
			}
			if (results.isEmpty()){
				return null;
			}
			
			log.debug("getGpsSupportedDevicesByNetworkId successful, result size: " + results.size());
			return results;
		}
		catch( Exception e )
		{
			log.error("find locations failed!", e);
			
			throw e;
		}
		finally
		{
			if(rs != null)
			{
				rs.close();
				rs = null;
			}
			if(session != null)
			{
				session.close();
				session = null;
			}
		}
		
	}
	
	public static void main(String[] args)
	{
		// SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		// String txtDate = "3-02-2012 12:55:30";

		try {
			// Date date = dateFormat.parse(txtDate);
			// date = DateUtils.getUtcDate(date, "78");
			// long time = 1375147910;
			// Date date1 = new Date();
			// date1.setTime(time);
			// Calendar cal = new GregorianCalendar();
			// cal.setTime(date1);
			// cal.add(Calendar.DAY_OF_MONTH, -1);
			// System.out.println(cal.getTime()); // 1330617600000
			// System.out.println(date.getTime()); // 03-02-2012

			// Date newDate = DateUtils.offsetTimeZone(new Date(), "GMT+0", "GMT-" + 8);

			// TimeZone.setDefault(TimeZone.getTimeZone("Asia/Hong_Kong"));
			// Calendar c = Calendar.getInstance();
			// c.setTimeZone(TimeZone.getTimeZone("UTC"));
			// c.setTime(new Date());
			// SimpleDateFormat time_fmat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			// String d = time_fmat.format(c.getTime());
			// System.out.println(""+d+" "+(c.getTimeInMillis()+TimeZone.getDefault().getRawOffset()));

			Calendar start_time = Calendar.getInstance();
			start_time.set(Calendar.YEAR, 2013);
			start_time.set(Calendar.MONTH, 8);
			start_time.set(Calendar.HOUR_OF_DAY, 0);
			start_time.set(Calendar.MINUTE, 0);
			start_time.set(Calendar.SECOND, 0);
			start_time.set(Calendar.DAY_OF_MONTH, 1);
			Calendar end_time = Calendar.getInstance();
			end_time.setTime(start_time.getTime());
			end_time.add(Calendar.SECOND, -1);
			// start_time.set(Calendar.SECOND, -1);
			// start_time.setTime(new Date());
			// start_time.set(Calendar.HOUR_OF_DAY, 23);
			// start_time.set(Calendar.MINUTE, 59);
			// start_time.set(Calendar.SECOND, 59);
			//
			// Calendar end_cal = Calendar.getInstance();
			// end_cal.set(Calendar.HOUR_OF_DAY, 0);
			// end_cal.set(Calendar.MINUTE, 0);
			// end_cal.set(Calendar.SECOND, 0);
			//
			// System.out.println(start_time.getTime()+" "+start_time.getTimeInMillis()+"|"+end_cal.getTime()+" "+end_cal.getTimeInMillis());
			//
			// long time = Long.parseLong("1379952005000");
			// System.out.println(time);
			// try
			// {
			// Date time1 = DateUtils.getUtcDate(new Date(time));
			// Date timestamp = DateUtils.offsetFromUtcTimeZone(time1, "Asia/Hong_Kong");
			// System.out.println("HK_TIME:"+timestamp);
			// }
			// catch(Exception e)
			// {
			// e.printStackTrace();
			// }
			//
//			System.out.println(end_time.getTime() + " " + end_time.getTimeInMillis());

			// TimeZone.setDefault(TimeZone.getTimeZone("Asia/Hong_Kong"));
			// Calendar c = Calendar.getInstance();
			// c.setTimeZone(TimeZone.getTimeZone("UTC"));
			// c.setTime(new Date());
			// SimpleDateFormat time_fmat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			// String d = time_fmat.format(c.getTime());
			// System.out.println(""+d+" "+(c.getTimeInMillis()+TimeZone.getDefault().getRawOffset()));

		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}

}