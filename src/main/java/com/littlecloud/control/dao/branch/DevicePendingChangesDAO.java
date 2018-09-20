package com.littlecloud.control.dao.branch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.branch.DevicePendingChanges;
import com.littlecloud.control.entity.branch.DevicePendingChanges.STATUS;
import com.littlecloud.fusionhub.FusionHubLicenseInfo;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

public class DevicePendingChangesDAO extends BaseDAO<DevicePendingChanges, Integer>
{
	private static final Logger log = Logger.getLogger(DevicePendingChangesDAO.class);
	
	private static final String tableName = DevicePendingChanges.tableName;
	
	private static final int maxRetry = 10;

	public DevicePendingChangesDAO() throws SQLException
	{
		super(DevicePendingChanges.class);
	}

	public DevicePendingChangesDAO(boolean readonly) throws SQLException
	{
		super(DevicePendingChanges.class, readonly);
	}

	public List<DevicePendingChanges> findbySid(String sid) throws SQLException
	{
		if (log.isDebugEnabled())
			log.debugf("findbySid()");
				 
		DBConnection session = null;
		DBQuery query = null;
		
		try
		{
			session = getSession();
			query = session.createQuery();
			query.setQueryClass(this.persistentClass);
			
			String dbTblName = StringUtils.join(new String[] {query.getDBName(), tableName}, ".");
			
			String sql = StringUtils.join(new String[] {
					"SELECT * from",
					dbTblName,
					"where sid = \""+sid+"\"",					
					"order by last_attemp_time desc"
			}, " ");
			
			if (log.isDebugEnabled())
				log.debugf("findbySid sql = %s", sql);
	
			return query.executeQueryAsObject(sql);
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public List<DevicePendingChanges> getAllPendingChanges() throws SQLException
	{
		if (log.isDebugEnabled())
			log.debugf("getAllPendingChanges()");
				 
		DBConnection session = null;
		DBQuery query = null;
		
		try
		{
			session = getSession();
			query = session.createQuery();
			query.setQueryClass(this.persistentClass);
			
			String dbTblName = StringUtils.join(new String[] {query.getDBName(), tableName}, ".");
			
			String sql = StringUtils.join(new String[] {
					"SELECT * from",
					dbTblName,
					"where status = \""+STATUS.pending+"\"",
					"and retry < "+maxRetry,
					"order by last_attemp_time desc"
			}, " ");
			
			if (log.isDebugEnabled())
				log.debugf("getStatus sql = %s", sql);
	
			return query.executeQueryAsObject(sql);
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session != null)
				session.close();
		}
	}

	public List<DevicePendingChanges> getRetryPendingChanges() throws SQLException
	{
		if (log.isDebugEnabled())
			log.debugf("getAllPendingChanges()");
				 
		DBConnection session = null;
		DBQuery query = null;
		
		try
		{
			session = getSession();
			query = session.createQuery();
			query.setQueryClass(this.persistentClass);
			
			String dbTblName = StringUtils.join(new String[] {query.getDBName(), tableName}, ".");
			
			String sql = StringUtils.join(new String[] {
					"SELECT * from",
					dbTblName,
					"where status = \""+STATUS.pending+"\"",
					"and retry > 0",
					"and retry < "+maxRetry,
					"order by last_attemp_time desc"
			}, " ");
			
			if (log.isDebugEnabled())
				log.debugf("getStatus sql = %s", sql);
	
			return query.executeQueryAsObject(sql);
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session != null)
				session.close();
		}
	}
	

	public int updateDevicePendingInfo(DevicePendingChanges license_info) throws SQLException
	{
		DBConnection session = getSession();
		
		try{
			DBQuery query = session.createQuery();
			
			StringBuilder sql =new StringBuilder();
			
			/* update device_pending_changes license_info */
			sql.append("INSERT into littlecloud_branch_production.device_pending_changes (iana_id, sid, sn, message_type, content, created_at) values (");// last_attemp_time, retry,
			sql.append(license_info.getIana_id());sql.append(", '");
			sql.append(license_info.getSid());sql.append("', '");
			sql.append(license_info.getSn());sql.append("', '");
			sql.append(license_info.getMessage_type());sql.append("', '");
			sql.append(license_info.getContent());sql.append("', ");			
			sql.append("now()");sql.append(");");
			return query.executeUpdate(sql.toString());
		
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (session!=null) session.close();
		}
		
	}
}
