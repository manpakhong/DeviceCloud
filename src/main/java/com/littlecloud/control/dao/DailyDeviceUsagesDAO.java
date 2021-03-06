package com.littlecloud.control.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.report.DailyDeviceUsages;
import com.littlecloud.pool.model.DailyDevUsageResult;
import com.littlecloud.pool.utils.Utils;
import com.littlecloud.utils.CalendarUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.util.DBUtil;

public class DailyDeviceUsagesDAO extends BaseDAO<DailyDeviceUsages, Integer>{
	private static final Logger log = Logger.getLogger(DailyDeviceUsagesDAO.class);
	
	public DailyDeviceUsagesDAO() throws SQLException{
		super(DailyDeviceUsages.class);
	}
	
	public DailyDeviceUsagesDAO(String orgId) throws SQLException{
		super(DailyDeviceUsages.class, orgId);
	}
	
	public DailyDeviceUsagesDAO(String orgId, boolean readonly) throws SQLException{
		super(DailyDeviceUsages.class, orgId, readonly);
	}
	
	public DailyDeviceUsages findByNetworkIdDeviceIdTimeAndWanId(Integer networkId, Integer device_id, Integer unixtime, Integer wan_id ) throws SQLException{
		if(log.isDebugEnabled())
			log.debug("getting " + this.getClass().getSimpleName() + " instance with networkId "+networkId+"wan_id "+wan_id+" device_id: " + device_id + " and unixtime: "+ unixtime );
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(DailyDeviceUsages.class);
			query.addCriteria("network_id", com.peplink.api.db.query.Criteria.EQ, networkId);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("device_id", com.peplink.api.db.query.Criteria.EQ, device_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("unixtime", com.peplink.api.db.query.Criteria.EQ, unixtime);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("wan_id", com.peplink.api.db.query.Criteria.EQ, wan_id);
			if(log.isInfoEnabled())
				log.info("find sql : "+query.getQuery());
			return (DailyDeviceUsages) query.executeQueryAsSingleObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	
	public List<DailyDeviceUsages> getRecordsByDeviceIdAndWanIdV2(Integer netId,Integer devId, Integer wanId,int now_unixtime ) throws SQLException
	{
		log.debug("getting records List" + this.getClass().getSimpleName() + " with device_id : "+ devId + " and wanId : " + wanId);
		DBConnection session = getSession();

		try 
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DailyDeviceUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "SELECT * FROM "+ dbname+".daily_device_usages where network_id = "+ netId+ 
					" and device_id = "+devId+" and wan_id = "+ wanId+" and unixtime <= "+ now_unixtime+ " group by unixtime order by unixtime desc limit 31;";
			return (List<DailyDeviceUsages>) query.executeQueryAsObject(sql);
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
	
	public List<DailyDevUsageResult> getTopDeviceUsage(Integer network_id, Integer startTime, Integer endTime) throws SQLException{
		log.debug("getting top device usage List " + this.getClass().getSimpleName() + "  with network_id: "+network_id+ " and start time: "+ startTime +" and end time: "+endTime);
		DBConnection session = getSession();
		ResultSet rs = null;
		try {
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "SELECT device_id, sum(tx), sum(rx) FROM "+dbname+".daily_device_usages where network_id ="+network_id+" and unixtime>= "+startTime+" and unixtime<="+endTime+" and wan_id = 0 group by device_id order by sum(tx)+sum(rx) desc limit 100;";
			rs = query.executeQuery(sql);
			List<DailyDevUsageResult> resultList = new ArrayList<DailyDevUsageResult>();
			DailyDevUsageResult result = null;
			String col;
			while (rs.next()){
				int i=1;
				result  = new DailyDevUsageResult();
				col = rs.getString(i++);
				result.setDeviceId(Integer.valueOf(col));
				col = rs.getString(i++);
				result.setTx(Double.valueOf(col));
				col = rs.getString(i++);
				result.setRx(Double.valueOf(col));
				resultList.add(result);
			}
			if (resultList.isEmpty()){
				return null;
			}
			return resultList;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if(rs != null)
			{
				rs.close();
				rs = null;				
			}
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
	}
	
	
	public List<DailyDevUsageResult> getDailyDeviceUsage(Integer network_id, Integer startTime, Integer endTime) throws SQLException{
		log.debug("getting daily device usage List " + this.getClass().getSimpleName() + "  with network_id: "+network_id+ " and start time: "+ startTime +" and end time: "+endTime);
		DBConnection session = getSession();
		ResultSet rs = null;
		try {
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();

			rs = query.executeQuery("SELECT unixtime, sum(tx), sum(rx) FROM "+dbname+".daily_device_usages where network_id ="+network_id+" and unixtime>= "+startTime+" and unixtime<="+endTime+" and wan_id = 0 group by unixtime order by unixtime asc;");
			List<DailyDevUsageResult> resultList = new ArrayList<DailyDevUsageResult>();
			DailyDevUsageResult result = null;
			String col;
			while (rs.next()){
				int i=1;
				result  = new DailyDevUsageResult();
				col = rs.getString(i++);
				result.setUnixtime(Integer.valueOf(col));
				col = rs.getString(i++);
				result.setTx(Double.valueOf(col));
				col = rs.getString(i++);
				result.setRx(Double.valueOf(col));
				resultList.add(result);
			}
			if (resultList.isEmpty()){
				return null;
			}
			return resultList;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if(rs != null)
			{
				rs.close();
				rs = null;				
			}
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
	}
	
	public List<DailyDevUsageResult> getDailyDeviceUsageWithDevId(Integer network_id, Integer device_id, Integer startTime, Integer endTime) throws SQLException{
		log.debug("getting daily device usage List " + this.getClass().getSimpleName() + "  with network_id: "+network_id+ " and start time: "+ startTime +" and end time: "+endTime);
		DBConnection session = getSession();
		ResultSet rs = null;
		try {
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			rs = query.executeQuery("SELECT unixtime, sum(tx), sum(rx) FROM "+dbname+".daily_device_usages where network_id = "+network_id+" and device_id = "+device_id+" and unixtime >= "+startTime+" and unixtime<="+endTime+" and wan_id = 0 group by unixtime order by unixtime asc;");
			List<DailyDevUsageResult> resultList = new ArrayList<DailyDevUsageResult>();
			DailyDevUsageResult result = null;
			String col;
			while (rs.next()){
				int i=1;
				result  = new DailyDevUsageResult();
				col = rs.getString(i++);
				result.setUnixtime(Integer.valueOf(col));
				col = rs.getString(i++);
				result.setTx(Double.valueOf(col));
				col = rs.getString(i++);
				result.setRx(Double.valueOf(col));
				resultList.add(result);
			}
			if (resultList.isEmpty()){
				return null;
			}
			return resultList;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if(rs != null)
			{
				rs.close();
				rs = null;				
			}
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
	}
	
	public List<Integer> getDistinctWanIdsWithDeviceId(Integer netId,Integer devId) throws SQLException
	{
		List<Integer> result = new ArrayList<Integer>();
		DBConnection session = getSession();
		log.debug("getting daily device wan names " + this.getClass().getSimpleName() + "  with device_id: "+devId);
		ResultSet rs = null;
		try 
		{
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select * from "+dbname+".daily_device_usages where device_id="+devId+" and network_id="+netId+" and wan_id is not null group by wan_id;";
			rs = query.executeQuery(sql);
			result = new ArrayList<Integer>();
			
			while (rs.next())
			{
				int wan_id = rs.getInt("wan_id"); 
				result.add(wan_id);
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
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
		
		return result;
	}
	

	public List<DailyDeviceUsages> getMonthlyRecords(int start, int end) throws SQLException
	{
		List<DailyDeviceUsages> result = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM");
		
		try
		{
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select date_format(datetime,'%Y-%m') as datetime, network_id, device_id, sum(tx) as tx, sum(rx) as rx, wan_id, wan_name, unixtime from "+
					dbname+".daily_device_usages where unixtime >= '"+start+"' and unixtime < '"+end+"' group by network_id, device_id, wan_id";
			log.info("Get monthly records sql : " + sql);
			rs = query.executeQuery(sql);
			result = new ArrayList<DailyDeviceUsages>();
			DailyDeviceUsages daily_usage = null;
			while (rs.next())
			{
				daily_usage = new DailyDeviceUsages();
				daily_usage.setUnixtime(rs.getInt("unixtime"));
				daily_usage.setDeviceId(rs.getInt("device_id"));
				daily_usage.setNetworkId(rs.getInt("network_id"));
				daily_usage.setTx(rs.getFloat("tx"));
				daily_usage.setRx(rs.getFloat("rx"));
				daily_usage.setWan_id(rs.getInt("wan_id"));
				daily_usage.setWan_name(rs.getString("wan_name"));
				daily_usage.setDatetime(dformat.parse(rs.getString("datetime")));
				result.add(daily_usage);
			}
		} 
		catch( Exception e )
		{
			log.error("Get Monthly records failed",e);
		}
		finally
		{
			if(rs != null)
			{
				rs.close();
				rs = null;				
			}
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
		
		return result;
	}
	
	public List<DailyDeviceUsages> getDeviceMonthlyRecords(Calendar calFrom, Calendar calTo) throws SQLException{
		List<DailyDeviceUsages> result = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM");
		try{
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("select ");
			sbSql.append("date_format(datetime, '%Y-%m') as datetime, ");
			sbSql.append("network_id, ");
			sbSql.append("device_id, ");
			sbSql.append("sum(tx) as tx, ");
			sbSql.append("sum(rx) as rx, ");
			sbSql.append("wan_id, ");
			sbSql.append("wan_name, ");
			sbSql.append("unixtime ");
			sbSql.append("from ");
			sbSql.append(dbname + ".daily_device_usages ");
			sbSql.append("where ");
			sbSql.append("datetime ");
			sbSql.append("between ");
			sbSql.append("'" + CalendarUtils.convertCalendar2MySqlDateString(calFrom) + "' ");
			sbSql.append("and ");
			sbSql.append("'" + CalendarUtils.convertCalendar2MySqlDateString(calTo) + "' ");
			sbSql.append("group by ");
			sbSql.append("network_id, ");
			sbSql.append("device_id, ");
			sbSql.append("wan_id ");
			sbSql.append("order by ");
			sbSql.append("datetime ");
			if (log.isInfoEnabled()){
				log.info("Get monthly records sql : " + sbSql.toString());
			}
			rs = query.executeQuery(sbSql.toString());
			result = new ArrayList<DailyDeviceUsages>();
			while (rs.next()){
				DailyDeviceUsages daily_usage = new DailyDeviceUsages();
				daily_usage.setUnixtime(rs.getInt("unixtime"));
				daily_usage.setDeviceId(rs.getInt("device_id"));
				daily_usage.setNetworkId(rs.getInt("network_id"));
				daily_usage.setTx(rs.getFloat("tx"));
				daily_usage.setRx(rs.getFloat("rx"));
				daily_usage.setWan_id(rs.getInt("wan_id"));
				daily_usage.setWan_name(rs.getString("wan_name"));
				daily_usage.setDatetime(dformat.parse(rs.getString("datetime")));
				result.add(daily_usage);
			}
		} 
		catch( Exception e ){
			log.error("Get Monthly records failed",e);
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
		return result;
	}
	
	public DailyDevUsageResult getDailyUsageOfDevice(Integer networkId, Integer deviceId, Integer startTime, Integer endTime) throws SQLException
	{
		DBConnection session = getSession();
		DailyDevUsageResult result = null;
		ResultSet rs = null;
		try
		{
			String dbPrefix = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			DBQuery query = session.createQuery();
			String sql;
			sql = "select network_id, device_id, sum(tx) as tx, sum(rx) as rx from " + dbPrefix + ".daily_device_usages where network_id = " + networkId + 
					" and device_id = " + deviceId + " and wan_id = 0 and unixtime >= " + startTime + " and unixtime <= " + endTime;
			rs = query.executeQuery(sql);
			if( rs != null )
			{
				result = new DailyDevUsageResult();
				while( rs.next() )
				{
					result.setDeviceId(rs.getInt("device_id"));
					result.setNetwork_id(rs.getInt("network_id"));
					result.setRx(rs.getDouble("rx"));
					result.setTx(rs.getDouble("tx"));
				}
			}
		}
		catch(SQLException e)
		{
			log.error("Get daily usage of designated device error -" + e);
		}
		finally
		{
			if(rs != null)
			{
				rs.close();
				rs = null;				
			}
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
		return result;
	}
	
	public DailyDeviceUsages getLatestDate(Integer unixtime,Integer network_id) throws SQLException
	{
		DailyDeviceUsages result = null;
		DBConnection session = getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			String dbName = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			query.setQueryClass(DailyDeviceUsages.class);
			String sql = "select * from " + dbName + ".daily_device_usages where network_id = "+network_id +" and wan_id = 0 and unixtime >= " + unixtime + " limit 1";
			log.info(sql);
			result = (DailyDeviceUsages)query.executeQueryAsSingleObject(sql);
		}
		catch(Exception e)
		{
			log.error("Get latest date error - " + e,e);
		}
		finally
		{
			if(session != null)
				session.close();
		}
		
		return result;
	}
	
	public Integer getAnExistingNetworkId() throws SQLException
	{
		Integer result = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		
		try
		{
			DBQuery query = session.createQuery();
			String dbName = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select distinct network_id from " + dbName + ".daily_device_usages where network_id != 0 and network_id is not null limit 1";
			rs = query.executeQuery(sql);
			if(rs != null)
			{
				while(rs.next())
				{
					result = rs.getInt("network_id");
				}
			}
		}
		catch(Exception e)
		{
			log.error("Get network_id error -"+e,e);
		}
		finally
		{
			if(rs!=null)
				rs.close();
			if(session != null)
				session.close();
		}
		
		return result;
	}
	public boolean saveDeviceDailyUsagesList(List<DailyDeviceUsages> deviceDailyUsageList) throws SQLException{
		boolean isSave = false;
		try{
			DBUtil dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
			DBConnection batchConnection = dbUtil.getConnection(false, orgId, false);
			if (deviceDailyUsageList != null && deviceDailyUsageList.size() > 0){
				for (DailyDeviceUsages dailyDeviceUsage: deviceDailyUsageList){
					batchConnection.addBatch(dailyDeviceUsage);
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
			log.error("DailyDeviceUsagesDAO.saveDeviceDailyUsagesList() - Exception: ", e);
		}
		finally{
			if(dbUtil.isSessionStarted()) {
				dbUtil.endSession();
			}
		}
		return isSave;
	}
	
}
