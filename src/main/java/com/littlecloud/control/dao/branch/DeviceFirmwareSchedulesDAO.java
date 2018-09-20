package com.littlecloud.control.dao.branch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.branch.DeviceFirmwareSchedules;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Condition;
import com.peplink.api.db.query.Criteria;

public class DeviceFirmwareSchedulesDAO extends BaseDAO<DeviceFirmwareSchedules, Integer>
{
	public static final int maxRetry = 100;

	protected static final Logger log = Logger.getLogger(DeviceFirmwareSchedulesDAO.class);

	public DeviceFirmwareSchedulesDAO() throws SQLException
	{
		super(DeviceFirmwareSchedules.class);
	}

	public DeviceFirmwareSchedulesDAO(boolean readonly) throws SQLException
	{
		super(DeviceFirmwareSchedules.class, readonly);
	}

	public List<DeviceFirmwareSchedules> findbySnFiltered(Integer ianaId, String sn) throws SQLException
	{
		/* housekeep before check update */		
		log.debugf("findbySn is called for %d %s", ianaId, sn);
				
		DBConnection session = getSession();
		try {		
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			String dbName = query.getDBName();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * from ");
			sql.append(dbName);
			sql.append(".");
			sql.append("device_firmware_schedules where iana_id = "+ianaId);
			sql.append(" and sn = \""+sn+"\"");
			sql.append(" and fw_version!='disabled'");
			
			log.debugf("getScheduleIdFromDevice sql=%s",sql.toString());
			
			List<DeviceFirmwareSchedules> dfsLst = query.executeQueryAsObject(sql.toString());			
			if (dfsLst==null)
				return new ArrayList<DeviceFirmwareSchedules>();
			
			if (dfsLst.size()>1)
				log.errorf("More than 1 schedule for dev %s!!", sn);
			
			return dfsLst;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}	
	}
	
	public Integer getScheduleIdFromDevice(Integer ianaId, String sn) throws SQLException
	{
		/* housekeep before check update */		
		log.debugf("getScheduleIdFromDevice is called for %d %s", ianaId, sn);
				
		DBConnection session = getSession();
		ResultSet rs = null;
		try {		
			DBQuery query = session.createQuery();
			String dbName = query.getDBName();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT id from ");
			sql.append(dbName);
			sql.append(".");
			sql.append("device_firmware_schedules where iana_id = "+ianaId);
			sql.append(" and sn = \""+sn+"\"");
			sql.append(" and fw_version!='disabled'");
			
			log.debugf("getScheduleIdFromDevice sql=%s",sql.toString());
			
			rs = query.executeQuery(sql.toString());
			if (rs.next())
			{
				return rs.getInt("id"); 
			}
			
			return 0;
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
			if(session != null)
			{
				session.close();
				session = null;
			}
		}	
	}
	
	public List<DeviceFirmwareSchedules> getTimeoutSchedulesBeforeTime(Long unixtime, Long timeoutInSec) throws SQLException
	{
		log.info("getTimeoutSchedulesBeforeTime getting " + unixtime);
		
		List<Integer> results = new ArrayList<Integer>();
		
		Long timeout_unixtime = unixtime - timeoutInSec;
		
		if (unixtime == null || unixtime == 0)
			return new ArrayList<DeviceFirmwareSchedules>();
		
		StringBuilder sb = new StringBuilder();
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(this.persistentClass);
			
			if (query==null)
				log.error("query is null!!");
			
			sb.append("SELECT * FROM ");
			sb.append(query.getDBName());
			sb.append(".device_firmware_schedules s ");
			sb.append("where schedule_time < ");
			sb.append(timeout_unixtime);
			sb.append(" and trial_round < "); 
			sb.append(maxRetry);
			sb.append(" and (");
			/* case 1 - not fetch */
			sb.append(" (status = 0 and upgrade_time is null)");
			/* case 2 - no response */
			sb.append(" or (status = 0 and upgrade_time < "+timeout_unixtime+")");
			/* case 3 - with response but not success, retry after 15mins */
			sb.append(" or (status = 3 and upgrade_time < "+ (unixtime - 600L) +")");
			sb.append(")");
			sb.append(" and fw_version != 'disabled' order by schedule_time asc");
			
			log.debugf("getTimeoutSchedulesBeforeTime sql = %s", sb.toString());
			return (List<DeviceFirmwareSchedules>) query.executeQueryAsObject(sb.toString());			
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public List<DeviceFirmwareSchedules> getSchedulesBeforeTime(Long unixtime, List<Integer> exemptIdLst) throws SQLException
	{
		log.info("getSchedulesBeforeTime getting " + unixtime + " exemptIdLst=" + exemptIdLst);

		if (unixtime == null || unixtime == 0)
			return new ArrayList<DeviceFirmwareSchedules>();

		StringBuilder sb = new StringBuilder();
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(this.persistentClass);
			
			if (query==null)
				log.error("query is null!!");
			
			sb.append("SELECT * FROM ");
			sb.append(query.getDBName());
			sb.append(".device_firmware_schedules s ");
			sb.append("where schedule_time < ");
			sb.append(unixtime);
			sb.append(" and trial_round < "); 
			sb.append(maxRetry);
			//sb.append(" and (case when upgrade_time is not null then "+unixtime+" > upgrade_time + 60 else true end)");
			sb.append(" and (status=0 or status=3)");
			sb.append(" and fw_version!='disabled'");

			if (exemptIdLst != null && exemptIdLst.size() > 0)
				sb.append(" and id not in (" + intListToParam(exemptIdLst) + ")");
			
			sb.append(" order by schedule_time asc");

			log.debugf("getScheduleBeforeTime sql = %s", sb.toString());
			List<DeviceFirmwareSchedules> results = (List<DeviceFirmwareSchedules>) query.executeQueryAsObject(sb.toString());
			log.debug("query successful, result size: " + results.size());

			return results;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public DeviceFirmwareSchedules getUniqueSchedule(Integer iana_id, String sn) throws SQLException
	{
		DeviceFirmwareSchedules result = null;
		DBConnection session = getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceFirmwareSchedules.class);
			query.addCriteria("iana_id", Criteria.EQ, iana_id);
			query.addCondition(Condition.AND);
			query.addCriteria("sn", Criteria.EQ, sn);
			result = (DeviceFirmwareSchedules)query.executeQueryAsSingleObject();
		}
		catch (Exception e)
		{
			throw e;
		} finally
		{
			if (session != null)
				session.close();
		}
		
		return result; 
	}
	
	public HashMap<String,DeviceFirmwareSchedules> getAllSchedules(String orgId, Integer networkId) throws SQLException
	{
		HashMap<String,DeviceFirmwareSchedules> result = null;
		DBConnection session = getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceFirmwareSchedules.class);
			String sql = "select * from " + query.getDBName() + ".device_firmware_schedules where organization_id = '" + orgId + "' and network_id = " + networkId;
			List<DeviceFirmwareSchedules> schedules = (List<DeviceFirmwareSchedules>)query.executeQueryAsObject(sql);
			if( schedules != null )
			{
				result = new HashMap<String,DeviceFirmwareSchedules>();
				for(DeviceFirmwareSchedules schedule : schedules)
				{
					result.put(schedule.getIana_id()+"\\|"+schedule.getSn(), schedule);
				}
			}
		}
		catch (Exception e)
		{
			throw e;
		} finally
		{
			if (session != null)
				session.close();
		}
		
		return result; 
	}
	
	public int deleteByNetworkId(String orgId, Integer netId) throws SQLException
	{
		DBConnection session = getSession();
		int result = 0;
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceFirmwareSchedules.class);
			String sql = "delete from " + query.getDBName() + ".device_firmware_schedules where organization_id = '" + orgId + "' and network_id = " + netId;
			result = query.executeUpdate(sql);
		}
		catch (Exception e)
		{
			throw e;
		} finally
		{
			if (session != null)
				session.close();
		}
		return result;
	}
	
}
