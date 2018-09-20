package com.littlecloud.control.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.DpiUsageObject;
import com.littlecloud.control.entity.report.DailyDeviceUsages;
import com.littlecloud.control.entity.report.DeviceDailyDpiUsages;
import com.littlecloud.control.entity.report.DeviceMonthlyDpiUsages;
import com.littlecloud.pool.utils.Utils;
import com.littlecloud.utils.CalendarUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.util.DBUtil;

public class DeviceDailyDpiUsagesDAO extends BaseDAO<DeviceDailyDpiUsages, Integer>
{
	private static final Logger logger = Logger.getLogger(DeviceDailyDpiUsagesDAO.class);
	public DeviceDailyDpiUsagesDAO() throws SQLException
	{
		super(DeviceDailyDpiUsages.class);
	}
	
	public DeviceDailyDpiUsagesDAO(String orgId) throws SQLException
	{
		super(DeviceDailyDpiUsages.class, orgId);
	}
	
	public DeviceDailyDpiUsagesDAO(String orgId, boolean readonly) throws SQLException 
	{
		super(DeviceDailyDpiUsages.class, orgId, readonly);
	}
	
	public List<DeviceDailyDpiUsages> findByNetworkIdDeviceIdProtocolAndCalendarRange(Integer networkId, Integer deviceId, String protocol, Calendar calFrom, Calendar calTo) throws SQLException{
		List<DeviceDailyDpiUsages> results = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		try{
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("select ");
			sbSql.append("id, ");
			sbSql.append("network_id, ");
			sbSql.append("device_id, ");
			sbSql.append("from_unixtime(unixtime) as datetime, ");
			sbSql.append("unixtime, ");
			sbSql.append("service, ");
			sbSql.append("size ");
			sbSql.append("from ");
			sbSql.append(dbname + ".device_daily_dpi_usages ");
			sbSql.append("where ");
			sbSql.append("date(from_unixtime(unixtime)) ");
			sbSql.append("between ");
			sbSql.append("'" + CalendarUtils.convertCalendar2MySqlDateString(calFrom) + "' ");
			sbSql.append("and ");
			sbSql.append("'" + CalendarUtils.convertCalendar2MySqlDateString(calTo) + "' ");
			sbSql.append("and ");
			sbSql.append("service = '" + protocol + "' ");
			sbSql.append("and ");
			sbSql.append("network_id = " + networkId + " ");
			sbSql.append("and ");
			sbSql.append("device_id = " + deviceId + " ");
			sbSql.append("order by ");
			sbSql.append("datetime ");
			if (logger.isInfoEnabled()){
				logger.info("DeviceDailyDpiUsagesDAO.findByNetworkIdDeviceIdUnixtimeAndProtocolAndCalendarRange() - sql : " + sbSql.toString());
			}
			rs = query.executeQuery(sbSql.toString());
			results = new ArrayList<DeviceDailyDpiUsages>();
			while (rs.next()){
				DeviceDailyDpiUsages deviceDailyDpiUsage = new DeviceDailyDpiUsages();
				deviceDailyDpiUsage.setId(rs.getInt("id"));
				deviceDailyDpiUsage.setNetwork_id(rs.getInt("network_id"));
				deviceDailyDpiUsage.setDevice_id(rs.getInt("device_id"));
				deviceDailyDpiUsage.setDatetime((Date) rs.getObject("datetime"));
				deviceDailyDpiUsage.setUnixtime(rs.getInt("unixtime"));
				deviceDailyDpiUsage.setService(rs.getString("service"));
				deviceDailyDpiUsage.setSize(rs.getLong("size"));
				results.add(deviceDailyDpiUsage);
			}
		} 
		catch( Exception e ){
			logger.error("DeviceDailyDpiUsagesDAO.findByNetworkIdDeviceIdUnixtimeAndProtocolAndCalendarRange() - Exception:",e);
		}
		finally{
			if(rs != null){
				rs.close();
				rs = null;				
			}
			if(session != null ){
				session.close();
				session = null;
			}
		}
		return results;
	}
	
	public DeviceDailyDpiUsages findByNetworkIdDeviceIdUnixtimeAndProtocol(Integer networkId,Integer deviceId,Integer unixtime,String protocol) throws SQLException
	{
		DeviceDailyDpiUsages result = null;
		DBConnection session = getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceDailyDpiUsages.class);
			query.addCriteria("network_id", com.peplink.api.db.query.Criteria.EQ, networkId);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("device_id", com.peplink.api.db.query.Criteria.EQ, deviceId);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("unixtime", com.peplink.api.db.query.Criteria.EQ, unixtime);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("service", com.peplink.api.db.query.Criteria.EQ, protocol);
			result = (DeviceDailyDpiUsages)query.executeQueryAsSingleObject();
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
			String sql = "select distinct network_id from " + dbname + ".device_daily_dpi_usages";
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
	
	public List<DeviceMonthlyDpiUsages> getDeviceDpiMonthlyUsagesRecords(Integer networkId, Calendar calFrom, Calendar calTo) throws SQLException{
		List<DeviceMonthlyDpiUsages> result = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		try{
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("select ");
			sbSql.append("network_id, ");
			sbSql.append("device_id, ");
			sbSql.append("service, ");
			sbSql.append("sum(size) as size ,");
			sbSql.append("unixtime, ");
			sbSql.append("date(from_unixtime(unixtime)) as datetime ");
			sbSql.append("from ");
			sbSql.append(dbname + ".device_daily_dpi_usages ");
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
			if (logger.isInfoEnabled()){
				logger.infof("DeviceDailyDpiUsagesDAO.getMonthlyDeviceDpiUsagesRecords(): sql:%s", sbSql.toString());
			}
			rs = query.executeQuery(sbSql.toString());
			
			if( rs != null ){
				result = new ArrayList<DeviceMonthlyDpiUsages>();
				while(rs.next()){
					DeviceMonthlyDpiUsages deviceMonthlyDpiUsage = new DeviceMonthlyDpiUsages();
					deviceMonthlyDpiUsage.setNetwork_id(rs.getInt("network_id"));
					deviceMonthlyDpiUsage.setDevice_id(rs.getInt("device_id"));
					deviceMonthlyDpiUsage.setService(rs.getString("service"));
					deviceMonthlyDpiUsage.setSize(rs.getLong("size"));
					deviceMonthlyDpiUsage.setUnixtime(rs.getInt("unixtime"));
					deviceMonthlyDpiUsage.setDatetime((Date) rs.getObject("datetime"));
					result.add(deviceMonthlyDpiUsage);
				}
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
		return result;
	}
	
	public List<DpiUsageObject> getMonthlyDpiRecords(int networkId, String tz) throws SQLException
	{
		List<DpiUsageObject> result = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		
		try
		{
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select network_id,device_id,service,sum(size) as size,date_format(concat(YEAR(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"')),'-',MONTH(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"')),'-',DAY(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"'))),'%Y-%m') as datetime from " + dbname 
						+ ".device_daily_dpi_usages where network_id = "+networkId+" group by device_id,service,date_format(concat(YEAR(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"')),'-',MONTH(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"')),'-',DAY(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"'))),'%Y-%m')";
			logger.info("Get monthly dpi sql : " + sql);
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
	
	public DeviceDailyDpiUsages getDailyTotalServiceSize(int network_id, int device_id, int start , int end) throws SQLException
	{
		DeviceDailyDpiUsages result = null;
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceDailyDpiUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select network_id, device_id, service, sum(size) as size from " + dbname + ".device_daily_dpi_usages where network_id = " + network_id 
					+ " and device_id = " +device_id+ " and unixtime >= " +start+ " and unixtime < " +end+ " group by network_id,device_id";
			logger.info("device dpi usage : " + sql);
			result = (DeviceDailyDpiUsages)query.executeQueryAsSingleObject(sql);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (session!=null) session.close();
		}
		return result;
	}
	
	public List<DeviceDailyDpiUsages> getDailyServiceSize(int networkId, int device_id, int start, int end, int top) throws SQLException
	{
		List<DeviceDailyDpiUsages> result = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceDailyDpiUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select network_id, device_id, service, sum(size) as size from " + dbname + ".device_daily_dpi_usages where network_id = " + networkId 
					+ " and device_id = " +device_id+ " and unixtime >= " +start+ " and unixtime < " +end+ " group by service order by size desc limit "+top;
			logger.info("Get daily dpi sql : " + sql);
			rs = query.executeQuery(sql);
			if( rs != null )
			{
				DeviceDailyDpiUsages usage = null;
				result = new ArrayList<DeviceDailyDpiUsages>();
				while( rs.next() )
				{
					usage = new DeviceDailyDpiUsages();
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
	
	public List<DeviceDailyDpiUsages> getDailyRecords(int networkId, int device_id, String service, int start, int end) throws SQLException
	{
		List<DeviceDailyDpiUsages> result = null;
		DBConnection session = getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceDailyDpiUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select * from " + dbname + ".device_daily_dpi_usages where network_id = " + networkId 
					+ " and device_id = " +device_id+ " and service = '" +service+"' and unixtime >= " +start+ " and unixtime < " +end+ " order by unixtime" ;
			logger.info("Get daily dpi sql : " + sql);
			result = (List<DeviceDailyDpiUsages>)query.executeQueryAsObject(sql);
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
	
	public boolean saveDeviceDpiDailyUsagesList(List<DeviceDailyDpiUsages> deviceDailyDpiUsageList) throws SQLException{
		boolean isSave = false;
		try{
			DBUtil dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
			DBConnection batchConnection = dbUtil.getConnection(false, orgId, false);
			if (deviceDailyDpiUsageList != null && deviceDailyDpiUsageList.size() > 0){
				for (DeviceDailyDpiUsages deviceDailyDpiUsages: deviceDailyDpiUsageList){
					batchConnection.addBatch(deviceDailyDpiUsages);
				}
			}
			batchConnection.commit();
			batchConnection.close();
			if(dbUtil.isSessionStarted()){
				dbUtil.endSession();
			}
			isSave = true;
		}
		catch(Exception e){
			logger.error("DeviceDailyDpiUsagesDAO.saveDeviceDpiDailyUsagesList() - Exception: ", e);
		}
		finally{
			if(dbUtil.isSessionStarted()) {
				dbUtil.endSession();
			}
		}
		return isSave;
	}
	
}
