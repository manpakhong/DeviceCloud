package com.littlecloud.control.dao.branch;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.branch.HealthCheckNormalHistory;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

public class HealthCheckNormalHistoryDAO extends BaseDAO<HealthCheckNormalHistory,Integer>{

	private static Logger logger = Logger.getLogger(HealthCheckNormalHistoryDAO.class);
	
	public HealthCheckNormalHistoryDAO() throws SQLException 
	{
		super(HealthCheckNormalHistory.class);
	}

	public HealthCheckNormalHistoryDAO(boolean readonly) throws SQLException 
	{
		super(HealthCheckNormalHistory.class, readonly);
	}
	
	public HealthCheckNormalHistory getLatestHealthHistoryByService(String service) throws SQLException
	{
		DBConnection session = getSession();
		HealthCheckNormalHistory result = null;
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(HealthCheckNormalHistory.class);
			String sql = "select * from " + query.getDBName() + ".health_check_history where service = '" + service + "' order by lastupdate desc limit 1";
			result = (HealthCheckNormalHistory)query.executeQueryAsSingleObject(sql);
		}
		catch(Exception e)
		{
			logger.error("get latest health history by service error-"+e,e);
		}
		finally
		{
			if(session != null)
				session.close();
		}
		
		return result;
	}
}
