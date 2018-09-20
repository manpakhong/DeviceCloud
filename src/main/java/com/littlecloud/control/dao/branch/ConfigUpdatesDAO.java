package com.littlecloud.control.dao.branch;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.branch.ConfigUpdates;
import com.littlecloud.control.entity.branch.ConfigUpdatesId;
import com.littlecloud.control.json.model.config.util.RadioConfigUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

/**
 * Home object for domain model class Devices.
 * 
 * @see com.littlecloud.control.entity.Devices
 * @author Hibernate Tools
 */
public class ConfigUpdatesDAO extends BaseDAO<ConfigUpdates, ConfigUpdatesId> {

	private static final Logger log = Logger.getLogger(ConfigUpdatesDAO.class);

	public ConfigUpdatesDAO(boolean readonly) throws SQLException {
		super(ConfigUpdates.class, readonly);
		//setDbName(orgId);
	}
	
	public int houseKeep() throws SQLException 
	{
		log.infof("houseKeep - orgId - %s", orgId);
		
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			String dbName = query.getDBName();

			StringBuilder sql = new StringBuilder();			
			sql.append("delete from ");
			sql.append(dbName);
			sql.append(".config_updates where lastput<=lastapply) and conf_update=0");
			
			//log.debugf("sql=%s", sql);
			
			int result = query.executeUpdate(sql.toString());			
			log.debugf("houseKeep %d records", result);
			
			return result;			
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
			log.debugf("housekeep ended");
		}
	}
	
	public int incrementConfClearForDevLst(ConfigUpdatesId confUpId, String sid, String reason, boolean isDevConfigTextSupport) throws SQLException
	{
		log.infof("incrementConfClearForDevLst - increment conf update for confUpId %s sid %s reason %s", confUpId, sid, reason);

		int update_method = 0;
		if (isDevConfigTextSupport)
			update_method = 1;
		
		DBConnection session = getSession();
		String sql = null;
		try {
			DBQuery query = session.createQuery();
			String dbName = query.getDBName();

			/* update config_updates devices list */
			sql = "INSERT into " + dbName + ".config_updates (iana_id, sn, conf_update, update_method, timestamp) " +
					"select "+confUpId.getIanaId()+",'"+confUpId.getSn()+"',"+1+", "+update_method+", now() from dual where not exists "
							+ "( select true from " + dbName + ".config_updates where iana_id = "+confUpId.getIanaId()+" and sn = '"+confUpId.getSn()+"')";
			log.debugf("incrementConfClearForDevLst sql1=%s",sql);
			query.executeUpdate(sql);
			
			sql = "UPDATE " + dbName + ".config_updates SET retry=0, conf_update=1, update_method="+update_method+", timestamp=now(), detail=SUBSTRING(concat('updating...',detail),1,3000), "
					+ "error=SUBSTRING(concat('updating...',error),1,500) WHERE iana_id = "+confUpId.getIanaId()+" and sn = '"+ confUpId.getSn()+"' and conf_update <2;";
			log.debugf("incrementConfClearForDevLst sql2=%s",sql);
			return query.executeUpdate(sql);
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public int decrementConfClearForDev(ConfigUpdatesId confUpId, String detail, String error, boolean bDoneAndStopFurtherApply) throws SQLException
	{
		log.infof("decrementConfClearForDev for confUpId %s with detail", confUpId);
		DBConnection session = getSession();
		String sql = null;
		try {
			DBQuery query = session.createQuery();
			String dbName = query.getDBName();

			sql = "UPDATE " + dbName + ".config_updates SET lastput=now(), timestamp=now()";			
			if (detail!=null)
				sql +=", detail='"+StringEscapeUtils.escapeSql(detail)+"'";			
			if (bDoneAndStopFurtherApply)
				sql +=", lastapply=now(), conf_update=0 ";
			if (error!=null && !error.isEmpty())
				sql +=", error='"+StringEscapeUtils.escapeSql(error)+"'";
			else
				sql +=", error=null";
			sql +=" WHERE iana_id = "+confUpId.getIanaId()+" and sn = '"+ confUpId.getSn()+"' and conf_update > 0;";
			log.debug("sql="+sql);
			return query.executeUpdate(sql);
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
}
