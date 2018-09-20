package com.littlecloud.control.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.report.DeviceMonthlyUsages;
import com.littlecloud.control.entity.report.DeviceMonthlyUsagesId;
import com.littlecloud.pool.utils.Utils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.util.DBUtil;

public class DeviceMonthlyUsagesDAO extends BaseDAO<DeviceMonthlyUsages, Integer>
{
	private static final Logger log = Logger.getLogger(DeviceMonthlyUsagesDAO.class);

	public DeviceMonthlyUsagesDAO() throws SQLException 
	{
		super(DeviceMonthlyUsages.class);
	}
	
	public DeviceMonthlyUsagesDAO( String orgId ) throws SQLException 
	{
		super(DeviceMonthlyUsages.class, orgId);
	}
	
	public DeviceMonthlyUsagesDAO(String orgId, boolean readonly) throws SQLException 
	{
		super(DeviceMonthlyUsages.class,orgId, readonly);
	}

	public boolean saveDeviceMonthlyUsagesList(List<DeviceMonthlyUsages> deviceMonthlyUsageList) throws SQLException{
		boolean isSave = false;
		try{
			DBUtil dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
			DBConnection batchConnection = dbUtil.getConnection(false, orgId, false);
			if (deviceMonthlyUsageList != null && deviceMonthlyUsageList.size() > 0){
				for (DeviceMonthlyUsages monthlyDeviceUsage: deviceMonthlyUsageList){
					batchConnection.addBatch(monthlyDeviceUsage);
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
			log.error("MonthlyDeviceUsagesDAO.saveDeviceMonthlyUsagesList() - Exception: ", e);
		}
		finally{
			if(dbUtil.isSessionStarted()) {
				dbUtil.endSession();
			}
		}
		return isSave;
	}
	
	public DeviceMonthlyUsages findByNetIdDeviceIdTimeAndWanId(Integer network_id, Integer device_id, Date datetime, Integer wan_id ) throws SQLException{
		if(log.isDebugEnabled())
			log.debug("findByNetIdDeviceIdTimeAndWanId> wan_id:"+wan_id+" network_id:" + network_id + " device_id:" + device_id + " datetime:"+ datetime );
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceMonthlyUsages.class);
			query.addCriteria("wan_id", com.peplink.api.db.query.Criteria.EQ, wan_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("network_id", com.peplink.api.db.query.Criteria.EQ, network_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("device_id", com.peplink.api.db.query.Criteria.EQ, device_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("datetime", com.peplink.api.db.query.Criteria.EQ, datetime);
			
			return (DeviceMonthlyUsages)query.executeQueryAsSingleObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public DeviceMonthlyUsages findByNetworkIdDeviceIdTimeAndWanId(Integer network_id, Integer device_id, Date datetime, Integer wan_id ) throws SQLException{
		if(log.isInfoEnabled())
			log.info("findByNetworkIdDeviceIdTimeAndWanId> network_id:"+network_id+" wan_id:"+wan_id+" device_id:" + device_id + " datetime:"+ datetime );
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceMonthlyUsages.class);
			query.addCriteria("network_id", com.peplink.api.db.query.Criteria.EQ, network_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("device_id", com.peplink.api.db.query.Criteria.EQ, device_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("datetime", com.peplink.api.db.query.Criteria.EQ, datetime);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("wan_id", com.peplink.api.db.query.Criteria.EQ, wan_id);
			
			return (DeviceMonthlyUsages)query.executeQueryAsSingleObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public List<DeviceMonthlyUsages> getRecordsWithDeviceIdAndWanIdV2(Integer netId,Integer devId, Integer wanId,int now_unixtime ) throws SQLException{
		List<DeviceMonthlyUsages> results = new ArrayList<DeviceMonthlyUsages>();
		DBConnection session = getSession();
		if(log.isInfoEnabled())
			log.info("getDistinctWanIdsWithDeviceId> netId:" + netId + " device_id:"+devId);
		ResultSet rs = null;
		try {
			
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("select ");
			sbSql.append("id, ");
			sbSql.append("network_id, ");
			sbSql.append("device_id, ");
			sbSql.append("datetime, ");
			sbSql.append("unixtime, ");
			sbSql.append("sum(tx) as tx, ");
			sbSql.append("sum(rx) as rx, ");
			sbSql.append("wan_id, ");
			sbSql.append("wan_name ");
			sbSql.append("from ");
			sbSql.append(dbname + ".device_monthly_usages ");
			sbSql.append("where ");
			sbSql.append("device_id=" + devId + " ");
			sbSql.append("and ");
			sbSql.append("network_id=" + netId +" ");
			sbSql.append("and ");
			sbSql.append("wan_id =" + wanId + " ");
			sbSql.append("and ");
			sbSql.append("unixtime <= " + now_unixtime + " ");
			sbSql.append("group by ");
			sbSql.append("datetime ");
			sbSql.append("order by ");
			sbSql.append("unixtime desc ");
			
			rs = query.executeQuery(sbSql.toString());
			results = new ArrayList<DeviceMonthlyUsages>();
			
			while (rs.next()){
				DeviceMonthlyUsages deviceMonthlyUsage = new DeviceMonthlyUsages();
				DeviceMonthlyUsagesId devMonthlyUsagesId = new DeviceMonthlyUsagesId();
				devMonthlyUsagesId.setId(rs.getString("id"));
				devMonthlyUsagesId.setUnixtime(rs.getInt("unixtime"));
				
				deviceMonthlyUsage.setId(devMonthlyUsagesId);
				deviceMonthlyUsage.setNetwork_id(rs.getInt("network_id"));
				deviceMonthlyUsage.setDevice_id(rs.getInt("device_id"));
				deviceMonthlyUsage.setDatetime((Date) rs.getObject("datetime"));
				deviceMonthlyUsage.setUnixtime(rs.getInt("unixtime"));
				deviceMonthlyUsage.setTx(rs.getFloat("tx"));
				deviceMonthlyUsage.setRx(rs.getFloat("rx"));
				deviceMonthlyUsage.setWan_id(rs.getInt("wan_id"));
				deviceMonthlyUsage.setWan_name(rs.getString("wan_name"));
				
				results.add(deviceMonthlyUsage);
			}
			
		} 
		catch (SQLException e){
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
	

	public List<Integer> getDistinctWanIdsWithDeviceId(Integer netId,Integer devId) throws SQLException
	{
		List<Integer> result = new ArrayList<Integer>();
		DBConnection session = getSession();
		if(log.isInfoEnabled())
			log.info("getDistinctWanIdsWithDeviceId> netId:" + netId + " device_id:"+devId);
		ResultSet rs = null;
		try 
		{
			
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select * from "+dbname+".device_monthly_usages where device_id="+devId+" and network_id="+netId+" and wan_id is not null group by wan_id;";
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
			if(session != null)
			{
				session.close();
				session = null;
			}
		}
		
		return result;
	}
	

	public List<Integer> getDistinctDatetimeByDevId(Integer netId, Integer devId ) throws SQLException
	{
		List<Integer> result = new ArrayList<Integer>();
		ResultSet rs = null;
		DBConnection session = getSession();
		String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
		
		if(log.isInfoEnabled())
			log.info("getDistinctDatetimeByDevId> netId:"+netId+" devId:" + devId);
		try
		{
			DBQuery query = session.createQuery();
			String sql = "select distinct unixtime from " + dbname + ".device_monthly_usages where network_id = " + netId + " and device_id = " + devId;
			
			rs = query.executeQuery(sql);
			while( rs.next() )
			{
				Integer date = rs.getInt("unixtime");
				result.add(date);
			}
		}
		catch(Exception e)
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

	
	public static void main(String[] args)
	{
		try {
			DeviceMonthlyUsagesDAO devMonthlyUsagesDAO = new DeviceMonthlyUsagesDAO("Michael");
			DeviceMonthlyUsages devMonthlyUsage = devMonthlyUsagesDAO.findByNetIdDeviceIdTimeAndWanId(10, 30, new Date(1412121600000L - 8*3600000L), 0);
			log.info(devMonthlyUsage);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}
}
