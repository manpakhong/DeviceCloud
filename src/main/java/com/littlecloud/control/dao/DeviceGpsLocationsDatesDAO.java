package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.entity.branch.DeviceFirmwareSchedules;
import com.littlecloud.control.entity.report.DeviceGpsLocationsDates;
import com.littlecloud.control.entity.report.DeviceGpsLocationsDatesId;
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
public class DeviceGpsLocationsDatesDAO extends BaseDAO<DeviceGpsLocationsDates, DeviceGpsLocationsDatesId> {

	private static final Logger log = Logger.getLogger(DeviceGpsLocationsDatesDAO.class);

	public DeviceGpsLocationsDatesDAO() throws SQLException {
		super(DeviceGpsLocationsDates.class);
	}

	public DeviceGpsLocationsDatesDAO(String orgId) throws SQLException {
		super(DeviceGpsLocationsDates.class, orgId);
	}

	public DeviceGpsLocationsDatesDAO(String orgId, boolean readonly) throws SQLException {
		super(DeviceGpsLocationsDates.class, orgId, readonly);
	}
	
	public List<DeviceGpsLocationsDates> getDeviceGpsLocationsDatesList(Integer network_id, Integer dev_id) throws SQLException{
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceGpsLocationsDates.class);
			query.addCriteria("network_id", Criteria.EQ, network_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("device_id", Criteria.EQ, dev_id);
			return (List<DeviceGpsLocationsDates>)query.executeQueryAsObject();
		}catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

	public List<DeviceGpsLocationsDates> getDeviceGpsLocationsDatesRange(Integer network_id, Integer dev_id, Integer from, Integer to) throws SQLException{
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceGpsLocationsDates.class);
			query.addCriteria("network_id", Criteria.EQ, network_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("device_id", Criteria.EQ, dev_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("unixtime", Criteria.GE, from);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("unixtime", Criteria.LE, to);
			return (List<DeviceGpsLocationsDates>)query.executeQueryAsObject();
		}catch (SQLException e)
		{
			throw e;
		} finally 
		{
			if (session!=null) session.close();
		}
	}

	public DeviceGpsLocationsDates getDeviceGpsLocationsDates(Integer network_id, Integer dev_id, Integer unixtime) throws SQLException{
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceGpsLocationsDates.class);
			query.addCriteria("network_id", Criteria.EQ, network_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("device_id", Criteria.EQ, dev_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("unixtime", Criteria.EQ, unixtime);
			return (DeviceGpsLocationsDates)query.executeQueryAsSingleObject();
		}catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

	public boolean isGpsLocationDatesConsolidated(Integer network_id, Integer dev_id, Integer unixtime, boolean isConsolidated) throws SQLException
	{
		DBConnection session = null;
		List<DeviceGpsLocationsDates> deviceGpsLocationDatesList = null;
		try {
			session = getSession();
			deviceGpsLocationDatesList = new ArrayList<DeviceGpsLocationsDates>();
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceGpsLocationsDates.class);
			
			query.addCriteria("network_id", Criteria.EQ, network_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("device_id", Criteria.EQ, dev_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("consolidated", Criteria.EQ, isConsolidated);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("unixtime", Criteria.GTE, unixtime);
			
			deviceGpsLocationDatesList = (List<DeviceGpsLocationsDates>)query.executeQueryAsSingleObject();
			
			if (deviceGpsLocationDatesList != null && deviceGpsLocationDatesList.size() != 0)
			{
				log.debug("find deviceGpsLocationDatesList successful, result size: " + deviceGpsLocationDatesList.size());
				return true;
			}
			else 
			{
				log.debug("find deviceGpsLocationDatesList successful, result : " + deviceGpsLocationDatesList);
				return false;/* *** if null or false, return false *** */
			}
		}catch (SQLException e)
		{
			log.warnf("DeviceGpsLocationDatesDAO.isGpsLocationDatesConsolidated network_id= %d, dev_id= %d unixtime= %d",  network_id, dev_id, unixtime);
			throw e;
		} finally
		{
			if (session!=null)
				session.close();
		}
	}
	
	public DeviceGpsLocationsDates getGpsLocationDatesConsolidated(Integer network_id, Integer dev_id, boolean isConsolidated) throws SQLException
	{
		DBConnection session = null;
		DeviceGpsLocationsDates deviceGpsLocationsDates = null;
		try {
			session = getSession();
			deviceGpsLocationsDates = new DeviceGpsLocationsDates();
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceGpsLocationsDates.class);
			
			/* ****** TestCase:****** */
			//select * from peplink_organization_riMA5x.device_gps_locations_dates dl where dl.network_id= 1 and dl.device_id = 1 and dl.consolidated = true order by dl.unixtime asc limit 1;

			String strSQL = "select * from " + query.getDBName() + ".device_gps_locations_dates dl where dl.network_id= "+ network_id +" and dl.device_id = " + dev_id + " and dl.consolidated = "+ isConsolidated+" order by dl.unixtime asc limit 1";

			deviceGpsLocationsDates = (DeviceGpsLocationsDates)query.executeQueryAsSingleObject(strSQL);
			
			
			log.debug("find deviceGpsLocationDatesList successful, result : " + deviceGpsLocationsDates);
			return deviceGpsLocationsDates;/* *** if null or false, return false *** */
		
		}catch (SQLException e)
		{
			log.warnf("DeviceGpsLocationDatesDAO.isGpsLocationDatesConsolidated network_id= %d, dev_id= %d unixtime= %d",  network_id, dev_id);
			throw e;
		} finally
		{
			if (session!=null)
				session.close();
		}
	}

	public List<DeviceGpsLocationsDates> getGpsLocationDatesConsolidated(Integer network_id, Integer dev_id, Integer unixtime, boolean isConsolidated) throws SQLException
	{
		DBConnection session = null;
		List<DeviceGpsLocationsDates> deviceGpsLocationDatesList = null;
		try {
			session = getSession();
			deviceGpsLocationDatesList = new ArrayList<DeviceGpsLocationsDates>();
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceGpsLocationsDates.class);
			query.addCriteria("network_id", Criteria.EQ, network_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("device_id", Criteria.EQ, dev_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("consolidated", Criteria.EQ, isConsolidated);
//			query.addCondition(com.peplink.api.db.query.Condition.AND);
//			query.addCriteria("unixtime", Criteria.GE, unixtime);
			
			deviceGpsLocationDatesList = (List<DeviceGpsLocationsDates>)query.executeQueryAsSingleObject();
			log.debug("find by example successful, result size: " + deviceGpsLocationDatesList.size());
			
			return deviceGpsLocationDatesList;/* *** return ( > = unixtime) *** */
		
		}catch (SQLException e)
		{
			log.warnf("DeviceGpsLocationDatesDAO.isGpsLocationDatesConsolidated network_id= %d, dev_id= %d unixtime= %d",  network_id, dev_id, unixtime);
			throw e;
		} finally
		{
			if (session!=null)
				session.close();
		}
	}
	
	public int deleteDeviceGpsLocationsDatesByNetId(Integer network_id) throws SQLException
	{
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceGpsLocationsDates.class);
			
			String sql = "delete from " + query.getDBName() + ".device_gps_locations_dates where network_id = " + network_id;
			int result = query.executeUpdate(sql);
			
			return result;
		}catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public void saveOrUpdate(DeviceGpsLocationsDates deviceGpsLocationsDates) throws SQLException{
		log.debug("saveIfNotExist " + this.persistentClass.getSimpleName() + " instance");
		DBConnection session = getSession();
		try {			
			session.saveIfNotExist(deviceGpsLocationsDates);		
			log.debug("saveOrUpdate successful");
		} catch (RuntimeException re) {
			log.error("saveOrUpdate failed", re);
			throw re;
		} finally {
			if (session!=null) session.close();
		}
	}
}
