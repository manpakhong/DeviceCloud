package com.littlecloud.control.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.entity.report.ClientUsages;
import com.littlecloud.control.entity.report.DailyDeviceUsages;
import com.littlecloud.control.entity.report.DeviceMonthlyUsages;
import com.littlecloud.control.entity.report.DeviceUsages;
import com.littlecloud.pool.model.DailyDevUsageResult;
import com.littlecloud.pool.utils.Utils;
import com.littlecloud.utils.CalendarUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.littlecloud.control.dao.jdbc.BaseDAO;

public class DeviceUsagesDAO extends BaseDAO<DeviceUsages, String>{
	private static final Logger log = Logger.getLogger(DeviceUsagesDAO.class);
	
	public DeviceUsagesDAO() throws SQLException{
		super(DeviceUsages.class);
	}
	
	public DeviceUsagesDAO(String orgId) throws SQLException{
		super(DeviceUsages.class, orgId);
	}
	
	public DeviceUsagesDAO(String orgId, boolean readonly) throws SQLException{
		super(DeviceUsages.class, orgId, readonly);
	}
	
	public DeviceUsages findByDeviceIdTimeWanId(Integer device_id, Date datetime, Integer wan_id) throws SQLException{
		log.debug("getting " + this.getClass().getSimpleName() + " instance with device_id: " + device_id + " and time: "+ datetime + " and wan_id: "+ wan_id);
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceUsages.class);
			query.addCriteria("device_id", com.peplink.api.db.query.Criteria.EQ, device_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("datetime", com.peplink.api.db.query.Criteria.EQ, datetime);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("wan_id", com.peplink.api.db.query.Criteria.EQ, wan_id);
			return (DeviceUsages) query.executeQueryAsSingleObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public DeviceUsages findDuplicateUsages(Integer device_id, Integer unixtime, Integer network_id, Integer wan_id) throws SQLException{
		log.debug("getting " + this.getClass().getSimpleName() + " instance with device_id: " + device_id + " and time: "+ unixtime + " and wan_id: "+ wan_id);
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceUsages.class);
			query.addCriteria("device_id", com.peplink.api.db.query.Criteria.EQ, device_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("unixtime", com.peplink.api.db.query.Criteria.EQ, unixtime);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("network_id", com.peplink.api.db.query.Criteria.EQ, network_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("wan_id", com.peplink.api.db.query.Criteria.EQ, wan_id);
			return (DeviceUsages) query.executeQueryAsSingleObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public DeviceUsages getLatestDeviceUsage( Integer devId ) throws Exception
	{
		DeviceUsages usage = null;
		
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select * from "+ dbname + ".device_usages where device_id = " + devId + " and wan_id is not null order by unixtime desc limit 1"; 
			usage = (DeviceUsages)query.executeQueryAsSingleObject(sql);
		} 
		catch (SQLException e)
		{
			throw e;
		}
		finally
		{
			if (session!=null) 
				session.close();
		}
		
		return usage;
	}
	
	public DeviceUsages getLatestDeviceUsage( Integer networkId, Integer devId ) throws Exception
	{
		DeviceUsages usage = null;
		
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select * from "+ dbname + ".device_usages where network_id = "+networkId+" and device_id = " + devId + " and wan_id is not null order by unixtime desc limit 1"; 
			usage = (DeviceUsages)query.executeQueryAsSingleObject(sql);
		} 
		catch (SQLException e)
		{
			throw e;
		}
		finally
		{
			if (session!=null) 
				session.close();
		}
		
		return usage;
	}


	public List<DeviceUsages> getRecordsByDeviceIdAndWanIdAndTimeV2( Integer networkId, Integer devId, Integer wanId,int now_unixtime,int unixtime) throws SQLException
	{
		
		log.debug("getting records List" + this.getClass().getSimpleName() + " with device_id : "+ devId + " and wanId : " + wanId);
		DBConnection session = getSession();

		try 
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "SELECT * FROM "+ dbname+".device_usages where network_id = "+ networkId+ 
					" and device_id = "+devId+" and wan_id = "+ wanId+" and unixtime>= "+unixtime+" and unixtime <= "+ now_unixtime+ " group by unixtime order by unixtime desc limit 24;";
			log.info("dev_daily_usage_sql:"+sql);
			return (List<DeviceUsages>) query.executeQueryAsObject(sql);
		} 
		catch (SQLException e)
		{
			throw e;
		} 
		finally
		{
			if (session!=null) 
				session.close();
		}
	}
	
	public DeviceUsages findByDeviceIdTimeAndWanId(Integer device_id, Date datetime, Integer wan_id) throws SQLException{
		log.debug("getting " + this.getClass().getSimpleName() + " instance with device_id: " + device_id + " and time: "+ datetime +" and" +
				" wan_id: "+wan_id);
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceUsages.class);
			query.addCriteria("device_id", com.peplink.api.db.query.Criteria.EQ, device_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("datetime", com.peplink.api.db.query.Criteria.EQ, datetime);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("wan_id", com.peplink.api.db.query.Criteria.EQ, wan_id);
			return (DeviceUsages) query.executeQueryAsSingleObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	

	public List<Integer> getDistinctWanIds(Integer devId, Integer networkId) throws SQLException
	{
		List<Integer> result = new ArrayList<Integer>();
		DBConnection session = getSession();
		log.debug("getting device hourly usage List " + this.getClass().getSimpleName() + "  with device_id: "+devId+" and networkId: "+networkId);
		ResultSet rs = null;
		try 
		{
			
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select * from "+dbname+".device_usages where device_id = "+devId+" and network_id = "+networkId
					+" and wan_id is not null group by wan_id;";
			log.info("get distinct wan_id sql : " + sql);
			rs = query.executeQuery(sql);
			result = new ArrayList<Integer>();
			
			while (rs.next())
			{
				if( rs.getObject("wan_id") != null )
				{
					result.add(Integer.valueOf(""+rs.getObject("wan_id")));
				}
			}

		} 
		catch (SQLException e)
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
	

	public List<DeviceUsages> getDailyUsagesRecords(Integer networkId, String tz, Integer start, Integer end) throws SQLException
	{
		List<DeviceUsages> result = new ArrayList<DeviceUsages>();
		DBConnection session = getSession();
		ResultSet rs = null;
		SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select "+
					"date_format(concat(YEAR(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"')),'-',"+
					"MONTH(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"')),'-',"+
					"DAY(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+tz+"'))),'%Y-%m-%d') as datetime,"+
					"network_id, device_id, sum(tx) as tx, sum(rx) as rx, wan_id, wan_name from " + 
					dbname + ".device_usages where network_id = " + networkId + 
					" and unixtime >= "+start+" and unixtime < "+end
					+" group by device_id,wan_id;";
			log.info("get daily usages records sql : " + sql);
			rs = query.executeQuery(sql);
			
			DeviceUsages usageObj = null;
			while (rs.next())
			{
				usageObj = new DeviceUsages();
				usageObj.setNetworkId(rs.getInt("network_id"));
				usageObj.setDeviceId(rs.getInt("device_id"));
				usageObj.setTx(rs.getFloat("tx"));
				usageObj.setRx(rs.getFloat("rx"));
				usageObj.setWan_id(rs.getInt("wan_id"));
				usageObj.setWan_name(rs.getString("wan_name"));
				usageObj.setDatetime(dformat.parse(rs.getString("datetime")));
				result.add(usageObj);
			}
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Get daily usage records error",e);
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
	
	public DeviceUsages getUsageOfNetworkIdAndTime(Integer network_id, Integer unixtime) throws SQLException
	{
		DeviceUsages result = null;
		DBConnection session = getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select * from "+dbname+".device_usages where network_id = " + network_id + " and unixtime = " + unixtime +" limit 1";
			log.info(sql);
			result = (DeviceUsages)query.executeQueryAsSingleObject(sql);
		}
		catch(Exception e)
		{
			log.error("Get usage by network_id and time error "+e,e);
		}
		finally
		{
			if(session != null)
				session.close();
		}
		return result;
	}
	
	public List<DeviceUsages> getDailyUsagesRecords(Integer networkId, Calendar calFrom, Calendar calTo) throws SQLException{
		List<DeviceUsages> result = new ArrayList<DeviceUsages>();
		DBConnection session = getSession();
		ResultSet rs = null;		
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("select ");
			sbSql.append("date(datetime) as datetime ");
			sbSql.append(",unixtime ");
			sbSql.append(",device_id ");
			sbSql.append(",network_id ");
			sbSql.append(",sum(tx) as tx ");
			sbSql.append(",sum(rx) as rx ");
			sbSql.append(",wan_id ");
			sbSql.append(",wan_name ");
			sbSql.append("from ");
			sbSql.append(dbname + ".device_usages ");
			sbSql.append("where ");
			sbSql.append("datetime ");
			sbSql.append("between ");
			sbSql.append("'" + CalendarUtils.convertCalendar2MySqlDateString(calFrom) + "' ");
			sbSql.append("and " );
			sbSql.append("'" + CalendarUtils.convertCalendar2MySqlDateString(calTo) + "' ");
			sbSql.append("and ");
			sbSql.append("network_id = " + networkId + " ");
			sbSql.append("group by ");
			sbSql.append("device_id, ");
			sbSql.append("wan_id ");
			sbSql.append("order by ");
			sbSql.append("date(datetime) ");
			
			log.info("get daily usages records sql : " + sbSql.toString());
			rs = query.executeQuery(sbSql.toString());
			
			DeviceUsages usageObj = null;
			while (rs.next()){
				usageObj = new DeviceUsages();
				usageObj.setNetworkId(rs.getInt("network_id"));
				usageObj.setDeviceId(rs.getInt("device_id"));
				usageObj.setTx(rs.getFloat("tx"));
				usageObj.setRx(rs.getFloat("rx"));
				usageObj.setWan_id(rs.getInt("wan_id"));
				usageObj.setWan_name(rs.getString("wan_name"));
				usageObj.setDatetime(rs.getDate("datetime"));
				result.add(usageObj);
			}
		} 
		catch (Exception e) {
			log.error("DeviceUsagesDAO.getDailyUsagesRecords() - Exception: ",e);
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
	
}
