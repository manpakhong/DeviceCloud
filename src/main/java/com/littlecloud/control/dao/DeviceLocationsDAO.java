package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

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
import com.littlecloud.control.entity.report.DeviceLocations;
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
public class DeviceLocationsDAO extends BaseDAO<DeviceLocations, String> {

	private static final Logger log = Logger.getLogger(DeviceLocationsDAO.class);

	public DeviceLocationsDAO() throws SQLException {
		super(DeviceLocations.class);
	}

	public DeviceLocationsDAO(String orgId) throws SQLException {
		super(DeviceLocations.class, orgId);
	}

	public DeviceLocationsDAO(String orgId, boolean readonly) throws SQLException {
		super(DeviceLocations.class, orgId, readonly);
	}

	public DeviceLocations getLastDeviceLocations(Devices dev) throws SQLException {
		log.debug("finding last device location with id " + this.persistentClass.getSimpleName());

		try {

			// Hibernate
			// DeviceLocations devLoc = (DeviceLocations) session
			// .createCriteria(this.persistentClass)
			// .add(Restrictions.eq("deviceId", dev.getId()))
			// .addOrder(Order.desc("datetime")).setMaxResults(1).uniqueResult();

			// JDBC
			DeviceLocations devLoc = this.getLastDeviceIdLocations(dev.getId());

			log.debug("find MAX successful");
			return devLoc;

		} catch (RuntimeException re) {
			log.error("find MAX failed", re);
			throw re;
		}
	}

	public DeviceLocations getLastDeviceIdLocations(Integer devId) throws SQLException {
		log.debug("finding last device location with id " + this.persistentClass.getSimpleName());

		try {
			// Hibernate
			// DeviceLocations devLoc = (DeviceLocations) session
			// .createCriteria(this.persistentClass)
			// .add(Restrictions.eq("deviceId", devId))
			// .addOrder(Order.desc("id")).setMaxResults(1).uniqueResult();

			DBConnection session = getSession();
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(DeviceLocations.class);

				String strSQL = "select * from " + query.getDBName() + ".device_locations dl where dl.device_id = " + Integer.toString(devId) + " order by dl.id desc limit 1";
				//System.out.println(strSQL);

				DeviceLocations devLoc = (DeviceLocations) query.executeQueryAsSingleObject(strSQL);

				log.debug("find MAX successful");
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
			log.error("find MAX failed", re);
			throw re;
		}
	}

	public List<DeviceLocations> getByDevicesAndStarttime(Devices dev, Date start) throws SQLException
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
			List<DeviceLocations> results = this.getByDevicesIdAndStarttime(dev.getId(), start);

			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List<DeviceLocations> getByDevicesIdAndStarttime(Integer devId, Date start) throws SQLException
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
				query.setQueryClass(DeviceLocations.class);

				// String strSQL = "select * from " + query.getDBName() + ".device_locations dl where dl.device_id = " +
				// Integer.toString(devId) + " order by dl.id desc limit 1";
				// System.out.println(strSQL);

				query.addCriteria("device_id", Criteria.EQ, devId);
				if (start != null) {
					query.addCriteria("datetime", Criteria.GTE, start);
				}

				List<DeviceLocations> results = (List<DeviceLocations>) query.executeQueryAsObject();

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
			log.error("find by example failed", re);
			throw re;
		}
	}

	public DeviceLocations getLatestNotNullLocation(Integer devId)
	{
		DeviceLocations loc = null;
		DBConnection session = null;
		
		try
		{
			session = getSession();
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceLocations.class);
			if( devId != null )
			{
//				String strSQL = "select * from " + query.getDBName() + ".device_locations dl where dl.device_id = " + Integer.toString(devId) + " order by dl.id desc limit 1";
				String sql = "select * from " + query.getDBName() + ".device_locations where " +
						"(latitude <> 'null' or longitude <> 'null') and device_id = "+ devId +" order by unixtime desc limit 1;";
//				System.out.println(sql);
				loc = (DeviceLocations)query.executeQueryAsSingleObject(sql);
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
	
	public List<DeviceLocations> getLocationsByDeviceIdWithStarttime(Integer devId, Date start) throws Exception
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
				query.setQueryClass(DeviceLocations.class);

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
					query.setLimit(5000);
					query.addOrderBy("unixtime asc");
					query.addOrderBy("id asc");

				}
				else
				{
					query.addOrderBy("unixtime desc");
					query.setLimit(1);
				}
//				System.out.println("LocationQuery:"+query.getQuery());
				List<DeviceLocations> results = query.executeQueryAsObject();

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

	public List<DeviceLocations> getLocationsByDeviceIdWithStarttime(Integer devId, Date start, int limit) throws Exception
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
				query.setQueryClass(DeviceLocations.class);

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
					query.addOrderBy("id asc");

				}
				else
				{
					query.addOrderBy("unixtime desc");
					query.setLimit(1);
				}
//				System.out.println("LocationQuery:"+query.getQuery());
				List<DeviceLocations> results = query.executeQueryAsObject();

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
	
	public List<DeviceLocations> getLocationsByDeviceIdWithMonth(Integer devId, Integer month) throws Exception
	{
		DBConnection session = getSession();
		List<DeviceLocations> results = new ArrayList<DeviceLocations>();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceLocations.class);
			if( month != null )
			{
//				String strSQL = "select * from " + query.getDBName() + ".device_locations dl where dl.device_id = " + Integer.toString(devId) + " order by dl.id desc limit 1";
				String sql = "SELECT * FROM " + query.getDBName() + ".device_locations where device_id = " + 
						Integer.toString(devId) + " and unixtime > '" + month.toString() + 
						"' group by CONCAT_WS('-',YEAR(datetime), MONTH(datetime), DAY(datetime)) order by unixtime asc";
				//System.out.println(sql);
				results = query.executeQueryAsObject(sql);
			}
			
			log.debug("find by month successful, result size: " + results.size());
			
		}
		catch( Exception e )
		{
			log.error("find locations failed!", e);
			e.printStackTrace();
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
	public List<DeviceLocations> getLocationsAllTheDay(Integer devId, int start, int end) throws SQLException
	{
		List<DeviceLocations> results = null;
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
			query.setQueryClass(DeviceLocations.class);
			query.addCriteria("device_id", Criteria.EQ, devId);
			if (start != 0 && end != 0)
			{
				log.debug("start param=" + start);

				query.addCriteria("unixtime", Criteria.LE, start);
				query.addCriteria("unixtime", Criteria.GE, end);
				query.setLimit(86400);
				query.addOrderBy("unixtime asc");
				query.addOrderBy("id asc");
//				System.out.println(query.toString());
				results = query.executeQueryAsObject();
			}
			else
			{
				query.addOrderBy("unixtime desc");
				results = new ArrayList<DeviceLocations>();
				if (query.executeQueryAsObject().size() > 0)
					results.add((DeviceLocations) query.executeQueryAsObject().get(0));
			}

			log.debug("find by example successful, result size: " + results.size());
		} catch (Exception e)
		{
			log.error("find locations failed!", e);
			e.printStackTrace();
		} finally
		{
			if (session != null)
				session.close();
		}
		return results;
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

		log.debug("deleting device location before time " + endTime + " Rows " + nRows);

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

			String strSQL = "delete from " + dbPrefix + orgId + ".device_locations where unixtime < " + endTime + " limit " + nRows;
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
