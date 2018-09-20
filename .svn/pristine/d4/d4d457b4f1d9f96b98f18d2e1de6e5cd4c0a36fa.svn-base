package com.littlecloud.control.dao.branch;

import java.sql.SQLException;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.branch.Firmwares;
import com.littlecloud.control.entity.branch.HouseKeepLogs;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Criteria;


public class HouseKeepLogsDAO extends BaseDAO<HouseKeepLogs, Integer>
{
	protected static final Logger log = Logger.getLogger(HouseKeepLogsDAO.class);
	public HouseKeepLogsDAO() throws SQLException
	{
		super(HouseKeepLogs.class);
		// TODO Auto-generated constructor stub
	}

	public HouseKeepLogsDAO(boolean readonly) throws SQLException
	{
		super(HouseKeepLogs.class, readonly);
	}
	
	public List<HouseKeepLogs> getHouseKeepLogs(String orgId, String tableName, Integer limit) throws SQLException
	{
		List<HouseKeepLogs> result = null;
		DBConnection session = getSession();
		try{
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("organization_id", Criteria.EQ, orgId);
			query.addCriteria("table_name", Criteria.EQ, tableName);
			query.addOrderBy("execute_time", false);
			query.setLimit(limit);
			result = (List<HouseKeepLogs>)query.executeQueryAsObject();
		}
		catch (Exception e){
			throw e;
		} 
		finally{
			if (session!=null) {
				session.close();
			}
		}
		
		return result;
	}
	
	public void deleteHouseKeepLogsBeforeTime(int clear_time) throws SQLException
	{
		log.debug("deleting house keep log before time "+ clear_time);
		DBConnection session = getSession();
		try 
		{
			DBQuery query = session.createQuery();
			String sql = "delete from littlecloud_branch_production.housekeep_logs where unixtime < '"+clear_time+"';";
			log.info(sql);
			query.executeUpdate(sql);
		} 
		catch (SQLException e)
		{
			throw e;
		} 
		finally
		{
			if (session!=null) session.close();
		}
	}
}
