package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import com.littlecloud.ac.DebugManager;
import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.DeviceUpdates;
import com.littlecloud.control.json.model.config.util.RadioConfigUtils;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

/**
 * Home object for domain model class Devices.
 * 
 * @see com.littlecloud.control.entity.Devices
 * @author Hibernate Tools
 */
public class DeviceUpdatesDAO extends BaseDAO<DeviceUpdates, Integer> {

	private static final Logger log = Logger.getLogger(DeviceUpdatesDAO.class);
	
	private static boolean PROD_MODE = DebugManager.isPROD_MODE();
	
	public DeviceUpdatesDAO(String orgId) throws SQLException {
		super(DeviceUpdates.class, orgId);
	}

	public DeviceUpdatesDAO(String orgId, boolean readonly) throws SQLException {
		super(DeviceUpdates.class, orgId, readonly);
	}

	public int houseKeep() throws SQLException 
	{
		if (log.isDebugEnabled()) log.debugf("houseKeep - orgId - %s", orgId);
		
		if (!PROD_MODE)
			return 0;
		
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			String dbName = query.getDBName();

			StringBuilder sql = new StringBuilder();			
			sql.append("delete from ");
			sql.append(dbName);
			sql.append(".device_updates where device_id not in (select id from ");
			sql.append(dbName);
			sql.append(".devices where active = true) and conf_update=0");
			
			if (log.isDebugEnabled()) log.debugf("sql=%s", sql);
			
			int result = query.executeUpdate(sql.toString());			
			if (log.isDebugEnabled()) log.debugf("houseKeep %d records", result);
			
			return result;			
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
			if (log.isDebugEnabled()) log.debugf("housekeep ended");
		}
	}
	
	public boolean isRequireConfigUpdate() throws SQLException
	{
		/* housekeep before check update */		
		int result = houseKeep();
		if (log.isDebugEnabled()) log.debugf("housekeeped records=%d", result);

		log.infof("isRequireConfigUpdate - orgId - %s", orgId);				
		DBConnection session = getSession();
		ResultSet rs = null;
		try {		
			DBQuery query = session.createQuery();
			String dbName = query.getDBName();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT count(*) as total FROM ");
			sql.append(dbName);
			sql.append(".");
			sql.append("device_updates where conf_update != 0 and device_id in (");
			sql.append("select id from ");
			sql.append(dbName);
			sql.append(".");
			sql.append("devices where active = 1)");					
			sql.append("and retry < "+RadioConfigUtils.MAX_RETRY_CFG_APPLY);
			
			rs = query.executeQuery(sql.toString());
			if (rs.next())
			{
				int total = rs.getInt("total"); 
				if (total>0)
				{
					//log.debugf("total = %d", total);
					return true;
				}
			}
			//log.debugf("total = 0");			
								
			return false;
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
	
	private String validateSid(String sid, String reason)
	{
		if (sid==null)
		{
			log.warnf("sid is not given for reason %s", reason);
			return JsonUtils.genServerRef();
		}
		return sid;
	}
	
	public int incrementConfUpdateForDevLst(List<Integer> devIdLst, String sid, String reason) throws SQLException
	{		
		log.infof("incrementConfUpdateForDevLst - increment conf update for devIdLst %s sid %s reason %s", devIdLst, sid, reason);
		
		if (!PROD_MODE)
			return 0;
		
		if (devIdLst==null || devIdLst.isEmpty())
		{
			log.warnf("incrementConfUpdateForDevLst - no action for empty devIdLst");
			return 0;
		}
		
		sid = validateSid(sid, reason);

		DBConnection session = getSession();
		StringBuilder sql = new StringBuilder(); 
		try {
			DBQuery query = session.createQuery();
			String dbName = query.getDBName();

			/* create or update device_updates devices list */
			sql.append("INSERT into ");
			sql.append(dbName);
			sql.append(".device_updates (device_id, conf_update, sid, reason, firstsave, firstput, timestamp) ");
			sql.append("SELECT id,0,'"+sid+"','"+reason+"',now(), null, now() FROM ");
			sql.append(dbName);
			sql.append(".devices where id in (");
			sql.append(intListToParam(devIdLst));
			sql.append(") and id not in ");
			sql.append("(select device_id FROM ");
			sql.append(dbName);
			sql.append(".device_updates);");
			if (log.isDebugEnabled()) log.debugf("sql1=%s", sql);
			query.executeUpdate(sql.toString());			
			
			sql.setLength(0);
			
			sql.append("UPDATE ");
			sql.append(dbName);
			sql.append(".device_updates SET retry=0, ");
			sql.append("firstsave=now(), firstput=null, lastput=null, lastapply=null, reason='");
			sql.append(reason);
			sql.append("', sid='");
			sql.append(sid);
			sql.append("', timestamp=now()");
			sql.append(" WHERE device_id in (");
			sql.append(intListToParam(devIdLst));
			sql.append(") and conf_update = 2;");
			if (log.isDebugEnabled()) log.debugf("sql2=%s", sql);
			query.executeUpdate(sql.toString());
			
			sql.setLength(0);
			
			sql.append("UPDATE ");
			sql.append(dbName);
			sql.append(".device_updates SET retry=0, conf_update=conf_update+1, ");
			sql.append("firstsave=now(), firstput=null, lastput=null, lastapply=null, reason='");
			sql.append(reason);
			sql.append("', sid='");
			sql.append(sid);
			sql.append("', timestamp=now()");
			sql.append(" WHERE device_id in (");
			sql.append(intListToParam(devIdLst));
			sql.append(") and conf_update <2;");
			if (log.isDebugEnabled()) log.debugf("sql3=%s", sql);
			return query.executeUpdate(sql.toString());
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

	public int suppressConfUpdateForDevLst(List<Integer> deviceIdList, String sid, String reason) throws SQLException{
		log.infof("suppressConfUpdateForDevLst - deviceId: %s %s %s", deviceIdList.size(), sid, reason);
		
		if (!PROD_MODE)
			return 0;
		
		sid = validateSid(sid, reason);
		
		DBConnection session = getSession();
		StringBuilder sql = new StringBuilder(); 
		try {
			DBQuery query = session.createQuery();
			String dbName = query.getDBName();

			String deviceIdListString = "";
			for (int i= 0; i < deviceIdList.size(); i ++){
				if (i > 0){
					deviceIdListString += "," + deviceIdList.get(i);
				} else{
					deviceIdListString += deviceIdList.get(i);
				}
				
			}
			/* update device_updates by deviceId */
			sql.append("UPDATE ");
			sql.append(dbName);
			sql.append(".device_updates SET conf_update=0, lastput=now(), lastapply=now(), reason='");
			sql.append(reason);
			sql.append("', sid='");
			sql.append(sid);
			sql.append("' WHERE device_id in (");
			sql.append(deviceIdListString);
			sql.append(");");
			if (log.isDebugEnabled()) log.debugf("SMD20140128 - suppressConfUpdateForDevLst - deviceId: %d, sql: %s", deviceIdList.size(), sql);
			return query.executeUpdate(sql.toString());

		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}		
	}
	
	public int incrementConfUpdateForDevLstIfNoUpdate(List<Integer> devIdLst, String sid, String reason) throws SQLException
	{
		if (!PROD_MODE)
			return 0;
		
		return incrementConfUpdateForDevLstIfNoUpdate(devIdLst, sid, reason, null, false);
	}
	
	/*** Handle immediate config update or retry ***/
	public int incrementConfUpdateForDevLstIfNoUpdate(List<Integer> devIdLst, String sid, String reason, String error, boolean isRetry) throws SQLException
	{
		log.infof("incrementConfUpdateForDevLstIfNoUpdate - increment conf update for devIdLst %s sid %s reason %s", devIdLst, sid, reason);
		
		if (!PROD_MODE)
			return 0;
		
		if (devIdLst==null || devIdLst.isEmpty())
		{
			log.warnf("incrementConfUpdateForDevLst - no action for empty devIdLst");
			return 0;
		}
		
		sid = validateSid(sid, reason);
		int updateCount = 0;
		String sqlError = error==null?null:StringUtils.join("SUBSTRING('", DateUtils.getCurrentDateTimeInUtcGeneralFormat()," ",StringEscapeUtils.escapeSql(error),"',1,500)");
		
		DBConnection session = getSession();
		StringBuilder sql = new StringBuilder();
		try {
			DBQuery query = session.createQuery();
			String dbName = query.getDBName();

			/* update device_updates devices list */
			sql.append("INSERT into ");
			sql.append(dbName);
			sql.append(".device_updates (device_id, conf_update, sid, reason, firstsave, firstput, timestamp) ");
			sql.append("SELECT id,0,'"+sid+"','"+reason+"',now(), null, now() FROM ");
			sql.append(dbName);
			sql.append(".devices where id in (");
			sql.append(intListToParam(devIdLst));
			sql.append(") and id not in ");
			sql.append("(select device_id FROM ");
			sql.append(dbName);
			sql.append(".device_updates);");
			updateCount = query.executeUpdate(sql.toString());
			if (log.isDebugEnabled()) log.debugf("incrementConfUpdateForDevLstIfNoUpdate updateCount(1) = %d sql %s", updateCount, sql);
			
			sql.setLength(0);
			
			if (!isRetry)
			{
				sql.append("UPDATE ");
				sql.append(dbName);
				sql.append(".device_updates SET retry=0, conf_update=conf_update+1, ");
				sql.append(" sid='"+sid+"', ");
				sql.append(" reason='"+reason+"', ");
				sql.append("firstsave=now(), ");
				sql.append(" firstput=null, lastput=null, lastapply=null, timestamp=now(), ");
				sql.append("error="+sqlError+" ");
				sql.append("WHERE device_id in (");
				sql.append(intListToParam(devIdLst));
				sql.append(") and conf_update = 0;");
				updateCount = query.executeUpdate(sql.toString());			
				if (log.isDebugEnabled()) log.debugf("incrementConfUpdateForDevLstIfNoUpdate updateCount(2) = %d sql %s", updateCount, sql);
			}
			else
			{
				sql.append("UPDATE ");
				sql.append(dbName);
				sql.append(".device_updates SET retry=retry+1, conf_update=conf_update+1, ");
				sql.append(" firstput=null, lastput=null, lastapply=null, timestamp=now(), ");
				sql.append("error="+sqlError+" ");
				sql.append("WHERE device_id in (");
				sql.append(intListToParam(devIdLst));
				sql.append(") and conf_update = 0;");
				updateCount = query.executeUpdate(sql.toString());			
				if (log.isDebugEnabled()) log.debugf("incrementConfUpdateForDevLstIfNoUpdate updateCount(retry) = %d sql %s", updateCount, sql);
			}
			
			return updateCount;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

	public int incrementConfUpdateForNetwork(Integer netId, String sid, String reason) throws SQLException
	{
		log.infof("increment conf update for network %d", netId);
		
		if (!PROD_MODE)
			return 0;
		
		sid = validateSid(sid, reason);
		
		DBConnection session = getSession();
		StringBuilder sql = new StringBuilder();
		try {
			DBQuery query = session.createQuery();
			String dbName = query.getDBName();

			/* update device_updates devices list */
			sql.append("INSERT into ");
			sql.append(dbName);
			sql.append(".device_updates (device_id, conf_update, sid, reason, firstsave, firstput, timestamp) ");
			sql.append("SELECT id,0,'"+sid+"','"+reason+"',now(), null, now() FROM ");
			sql.append(dbName);
			sql.append(".devices where network_id = ");
			sql.append(netId);
			sql.append(" and id not in ");
			sql.append("(select device_id FROM ");
			sql.append(dbName);
			sql.append(".device_updates);");
			query.executeUpdate(sql.toString());
			
			sql.setLength(0);
			
			sql.append("UPDATE ");
			sql.append(dbName);
			sql.append(".device_updates SET retry=0, conf_update=conf_update+1, ");
			sql.append(" sid='"+sid+"', ");
			sql.append(" reason='"+reason+"', ");
			sql.append(" firstsave=now(), firstput=null, lastput=null, lastapply=null, timestamp=now(), ");			
			sql.append("WHERE device_id in (select id FROM ");
			sql.append(dbName);
			sql.append(".devices where network_id = ");
			sql.append(netId);
			sql.append(") and conf_update = 2;");
			query.executeUpdate(sql.toString());
			
			sql.setLength(0);
			
			sql.append("UPDATE ");
			sql.append(dbName);
			sql.append(".device_updates SET retry=0, conf_update=conf_update+1, ");
			sql.append(" sid='"+sid+"', ");
			sql.append(" reason='"+reason+"', ");
			sql.append(" firstsave=now(), firstput=null, lastput=null, lastapply=null, timestamp=now(), ");
			sql.append("WHERE device_id in (select id FROM ");
			sql.append(dbName);
			sql.append(".devices where network_id = ");
			sql.append(netId);
			sql.append(") and conf_update <2;");;
			return query.executeUpdate(sql.toString());
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

//	public int decrementConfUpdateForDev(Integer devId) throws SQLException
//	{
//		return decrementConfUpdateForDev(devId, null);
//	}
	
	public int decrementConfUpdateForDev(Integer devId, String detail, String error, boolean bDoneAndStopFurtherApply) throws SQLException
	{
		return doDecrementConfUpdateForDev(devId, detail, error, null, bDoneAndStopFurtherApply);
	}
	
	public int decrementConfUpdateForDevWithConfig(Integer devId, String detail, String error, String config, boolean bDoneAndStopFurtherApply) throws SQLException
	{
		return doDecrementConfUpdateForDev(devId, detail, error, config, bDoneAndStopFurtherApply);
	}
	
	private int doDecrementConfUpdateForDev(Integer devId, String detail, String error, String config, boolean bDoneAndStopFurtherApply) throws SQLException
	{
		log.infof("decrementConfUpdateForDev for devId %d with detail", devId);
		
		if (!PROD_MODE)
			return 0;
				
		DBConnection session = getSession();
		StringBuilder sql = new StringBuilder();
		String sqlError = StringUtils.join("SUBSTRING('", DateUtils.getCurrentDateTimeInUtcGeneralFormat(), " ", StringEscapeUtils.escapeSql(error), "',1,500)");
		try {
			DBQuery query = session.createQuery();
			String dbName = query.getDBName();

			sql.append("UPDATE ");
			sql.append(dbName);
			sql.append(".device_updates SET conf_update=conf_update-1, timestamp=now(),");
			sql.append(" lastput = (case when firstput is not null then now() else lastput end),");	/* must precede firstput! */
			sql.append(" firstput = (case when firstput is null then now() else firstput end)");
			if (detail!=null)
			{
				sql.append(", detail='");
				sql.append(StringEscapeUtils.escapeSql(detail));
				sql.append("'");
			}
			if (config!=null)
			{
				sql.append(", config='");
				sql.append(StringUtils.join(DateUtils.getCurrentDateTimeInUtcGeneralFormat(), "\n", StringEscapeUtils.escapeSql(config)));
				sql.append("'");
			}
			if (bDoneAndStopFurtherApply)
			{
				sql.append(", lastapply=now() ");
			}
			if (sqlError!=null && !sqlError.isEmpty())
			{
				sql.append(", error=");
				sql.append(sqlError);				
			}
			else
			{
				sql.append(", error=null");
			}
			sql.append(" WHERE device_id = ");
			sql.append(devId);
			sql.append(" and conf_update > 0;");
			if (log.isDebugEnabled()) log.debugf("sql=%s", sql.toString());
			return query.executeUpdate(sql.toString());
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

//	public int decrementConfUpdateForNetwork(Integer netId) throws SQLException
//	{
//		return decrementConfUpdateForNetwork(netId, null);
//	}
//	
//	public int decrementConfUpdateForNetwork(Integer netId, String detail) throws SQLException
//	{
//		log.infof("increment conf update for network %d", netId);
//		DBConnection session = getSession();
//		String sql = null;
//		try {
//			DBQuery query = session.createQuery();
//			String dbName = query.getDBName();
//
//			sql = "UPDATE " + dbName + ".device_updates SET conf_update=conf_update-1, lastput=now(), timestamp=now()";
//			if (detail!=null)
//				sql +=", detail='"+StringEscapeUtils.escapeSql(detail)+"'";
//			sql +=" WHERE device_id in (select id FROM " + dbName + ".devices where network_id = "+netId+") and conf_update > 0;";
//			return query.executeUpdate(sql);
//		} catch (SQLException e)
//		{
//			throw e;
//		} finally
//		{
//			if (session!=null) session.close();
//		}
//	}

	public int incrementNoAckConfRetryForNetwork(Integer netId) throws SQLException
	{
		log.infof("increment conf retry for netId %d", netId);
		
		DBConnection session = getSession();
		StringBuilder sql = new StringBuilder();
		String sqlError = StringUtils.join("'", DateUtils.getCurrentDateTimeInUtcGeneralFormat()," No Ack'");
		try {
			DBQuery query = session.createQuery();
			String dbName = query.getDBName();

			sql.append("UPDATE ");
			sql.append(dbName);
			sql.append(".device_updates SET conf_update=1, retry=retry+1, timestamp=now(), ");
			sql.append("error = ");
			sql.append(sqlError);
			sql.append(" WHERE device_id in (select id FROM ");
			sql.append(dbName);
			sql.append(".devices where network_id = ");
			sql.append(netId); 
			sql.append(" and retry < ");
			sql.append(RadioConfigUtils.MAX_RETRY_CFG_APPLY);
			sql.append(" and conf_update = 0 ");
			sql.append(" and (firstput is not null || (firstput is null and (TO_SECONDS(now()) - TO_SECONDS(firstsave))/60 > 1))");
			sql.append(" and ((lastapply is null) or (lastapply < lastput))");
			sql.append(" and ((lastput is null) or (TO_SECONDS(now()) - TO_SECONDS(lastput))/60 > ");
			sql.append("( case WHEN ");
			sql.append(RadioConfigUtils.MIN_RETRY_MIN_TIMEOUT);
			sql.append(" * retry > ");
			sql.append(RadioConfigUtils.MAX_RETRY_WAIT_MIN);
			sql.append(" THEN ");
			sql.append(RadioConfigUtils.MIN_RETRY_MIN_TIMEOUT);
			sql.append(" * ");
			sql.append(RadioConfigUtils.MAX_RETRY_WAIT_MIN);
			sql.append(" WHEN retry = 0 THEN 1 ELSE retry END)) )");				
			
			if (log.isDebugEnabled()) log.debugf("sql=%s",sql);

			if (!PROD_MODE)
				return 0;

			return query.executeUpdate(sql.toString());
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public boolean isUpdating(Integer devId) throws SQLException 
	{
		log.infof("isUpdating for devId %d", devId);

		DBConnection session = getSession();
		ResultSet rs = null;
		try {		
			DBQuery query = session.createQuery();
			String dbName = query.getDBName();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT 1 as found FROM ");
			sql.append(dbName);
			sql.append(".");
			sql.append("device_updates where device_id = " + devId + " and conf_update != 0;");
			
			rs = query.executeQuery(sql.toString());
			if (rs.next())
			{
				return true;
			}	
								
			return false;
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
	
	public DeviceUpdates getUnappliedConfig(Integer devId) throws SQLException
	{
		DeviceUpdates deviceUpdates = null;
		DBConnection session = getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceUpdates.class);
			String dbName = query.getDBName();
			String sql = "select * from " + dbName + ".device_updates where device_id = " + devId + " and conf_update <> 0";
			if (log.isDebugEnabled()) log.debug("SQL get unapplied : " + sql);
			deviceUpdates = (DeviceUpdates)query.executeQueryAsSingleObject(sql);
		}
		catch(Exception e)
		{
			log.error("Get unapplied config err - " + e , e);
		}
		finally
		{
			if(session!=null)
				session.close();
		}
		
		return deviceUpdates;
	}
}
