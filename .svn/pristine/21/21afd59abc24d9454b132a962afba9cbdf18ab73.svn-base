package com.littlecloud.control.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.DpiUsageObject;
import com.littlecloud.control.entity.report.DeviceDailyDpiUsages;
import com.littlecloud.control.entity.report.DeviceDpiUsages;
import com.littlecloud.pool.utils.Utils;
import com.littlecloud.utils.CalendarUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

public class DeviceDpiUsagesDAO extends BaseDAO<DeviceDpiUsages, Integer>
{
	private static final Logger logger = Logger.getLogger(DeviceDpiUsagesDAO.class);
	
	public DeviceDpiUsagesDAO() throws SQLException {
		super(DeviceDpiUsages.class);
	}

	public DeviceDpiUsagesDAO(String orgId) throws SQLException {
		super(DeviceDpiUsages.class, orgId);
	}
	
	public DeviceDpiUsagesDAO(String orgId, boolean readonly) throws SQLException {
		super(DeviceDpiUsages.class, orgId, readonly);
	}
	
	public DeviceDpiUsages findByNetworkIdDeviceIdUnixtimeAndProtocol(Integer networkId,Integer deviceId,Integer unixtime,String protocol) throws SQLException
	{
		DeviceDpiUsages result = null;
		DBConnection session = getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceDpiUsages.class);
			query.addCriteria("network_id", com.peplink.api.db.query.Criteria.EQ, networkId);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("device_id", com.peplink.api.db.query.Criteria.EQ, deviceId);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("unixtime", com.peplink.api.db.query.Criteria.EQ, unixtime);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("service", com.peplink.api.db.query.Criteria.EQ, protocol);
			result = (DeviceDpiUsages)query.executeQueryAsSingleObject();
		}
		catch (SQLException e)
		{
			throw e;
		}
		finally
		{
			if (session!=null) session.close();
		}
		
		return result;
	}
	
	public List<Integer> getDistinctNetworkIds() throws SQLException
	{
		List<Integer> result = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		try
		{
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select distinct network_id from " + dbname + ".device_dpi_usages";
			rs = query.executeQuery(sql);
			if( rs != null )
			{
				result = new ArrayList<Integer>();
				int network_id = 0;
				while(rs.next())
				{
					network_id = rs.getInt("network_id");
					result.add(network_id);
				}
			}
		}
		catch (Exception e)
		{
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
		
		return result;
	}
	
	public List<DeviceDailyDpiUsages> getDeviceDpiDailyUsagesRecords(int networkId, Calendar calFrom, Calendar calTo) throws SQLException {
		List<DeviceDailyDpiUsages> results = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		try {
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("select ");
			sbSql.append("network_id, ");
			sbSql.append("device_id, ");
			sbSql.append("service, ");
			sbSql.append("from_unixtime(unixtime) as datetime, ");
			sbSql.append("unixtime, ");
			sbSql.append("sum(size) as size ");
			sbSql.append("from ");
			sbSql.append(dbname + ".device_dpi_usages ");
			sbSql.append("where ");
			sbSql.append("network_id = " + networkId + " ");
			sbSql.append("and ");
			sbSql.append("date(from_unixtime(unixtime)) ");
			sbSql.append("between ");
			sbSql.append("'" + CalendarUtils.convertCalendar2MySqlDateString(calFrom) + "' ");
			sbSql.append("and ");
			sbSql.append("'" + CalendarUtils.convertCalendar2MySqlDateString(calTo) + "' ");
			sbSql.append("group by ");
			sbSql.append("device_id, ");
			sbSql.append("service ");
			sbSql.append("order by ");
			sbSql.append("datetime, ");
			sbSql.append("network_id, ");
			sbSql.append("device_id ");
			
			if (logger.isInfoEnabled()){
				logger.info("DeviceDpiUsagesDAO.getDeviceDpiDailyUsagesRecords() - sql: " + sbSql.toString());				
			}

			rs = query.executeQuery(sbSql.toString());
			results = new ArrayList<DeviceDailyDpiUsages>();
			while(rs.next()){
				DeviceDailyDpiUsages deviceDailyDpiUsages = new DeviceDailyDpiUsages();
				deviceDailyDpiUsages.setNetwork_id(rs.getInt("network_id"));
				deviceDailyDpiUsages.setDevice_id(rs.getInt("device_id"));
				deviceDailyDpiUsages.setService(rs.getString("service"));
				deviceDailyDpiUsages.setDatetime((Date) rs.getObject("datetime")); 
				deviceDailyDpiUsages.setSize(rs.getLong("size"));
				deviceDailyDpiUsages.setUnixtime(rs.getInt("unixtime"));
				results.add(deviceDailyDpiUsages);
			}

		}
		catch (Exception e){
			throw e;
		}
		finally{
			if(rs != null){
				rs.close();
				rs = null;
			}
			if(session != null){
				session.close();
				session = null;
			}
		}
		return results;
	}
	
	public List<DpiUsageObject> getDailyDpiRecords(int networkId, String tz) throws SQLException
	{
		List<DpiUsageObject> result = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		
		try
		{
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select network_id,device_id,service,sum(size) as size,date_format(concat(YEAR(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"')),'-',MONTH(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"')),'-',DAY(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"'))),'%Y-%m-%d') as datetime from " + dbname 
						+ ".device_dpi_usages where network_id = "+networkId+" group by device_id,service,date_format(concat(YEAR(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"')),'-',MONTH(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"')),'-',DAY(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"'))),'%Y-%m-%d')";
			logger.info("Get daily dpi sql : " + sql);
			rs = query.executeQuery(sql);
			DpiUsageObject dpiUsage = null;
			if( rs != null )
			{
				result = new ArrayList<DpiUsageObject>();
				while(rs.next())
				{
					dpiUsage = new DpiUsageObject();
					dpiUsage.setNetwork_id(rs.getInt("network_id"));
					dpiUsage.setDevice_id(rs.getInt("device_id"));
					dpiUsage.setService(rs.getString("service"));
					dpiUsage.setSize(rs.getLong("size"));
					dpiUsage.setDatetime(rs.getString("datetime"));
					result.add(dpiUsage);
				}
			}
		}
		catch (Exception e)
		{
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
		
		return result;
	}
	
	public List<DeviceDpiUsages> getHourlyRecords(int networkId, int device_id, String service, int start, int end) throws SQLException
	{
		List<DeviceDpiUsages> result = null;
		DBConnection session = getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceDpiUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select * from " + dbname + ".device_dpi_usages where network_id = " + networkId 
					+ " and device_id = " +device_id+ " and service = '" +service+"' and unixtime >= " +start+ " and unixtime < " +end+ " order by unixtime asc";
			logger.info("Get daily dpi sql : " + sql);
			result = (List<DeviceDpiUsages>)query.executeQueryAsObject(sql);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (session!=null) 
				session.close();
		}
		return result;
	}
	
	public List<DeviceDpiUsages> getHourlyServiceSize(int networkId, int device_id, int start, int end, int top) throws SQLException
	{
		List<DeviceDpiUsages> result = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceDpiUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select network_id, device_id, service, sum(size) as size from " + dbname + ".device_dpi_usages where network_id = " + networkId 
					+ " and device_id = " +device_id+ " and unixtime >= " +start+ " and unixtime < " +end+ " group by service order by size desc limit "+top;
			logger.info("Get daily dpi sql : " + sql);
			rs = query.executeQuery(sql);
			if( rs != null )
			{
				DeviceDpiUsages usage = null;
				result = new ArrayList<DeviceDpiUsages>();
				while( rs.next() )
				{
					usage = new DeviceDpiUsages();
					usage.setNetwork_id(rs.getInt("network_id"));
					usage.setDevice_id(rs.getInt("device_id"));
					usage.setService(rs.getString("service"));
					usage.setSize(rs.getLong("size"));
					result.add(usage);
				}
			}
		}
		catch (Exception e)
		{
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
		return result;
	}
	
	public DeviceDpiUsages getHourlyTotalServiceSize(int networkId, int device_id, int start, int end) throws SQLException
	{
		DeviceDpiUsages result = null;
		DBConnection session = getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceDpiUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select network_id, device_id, service, sum(size) as size from " + dbname + ".device_dpi_usages where network_id = " + networkId 
					+ " and device_id = " +device_id+ " and unixtime >= " +start+ " and unixtime < " +end+ " group by network_id,device_id";
			logger.info("device dpi usage : " + sql);
			result = (DeviceDpiUsages)query.executeQueryAsSingleObject(sql);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (session!=null) 
				session.close();
		}
		return result;
	}
	
}
