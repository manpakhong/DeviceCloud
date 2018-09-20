package com.littlecloud.control.dao.branch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.branch.HealthCheck;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

public class HealthCheckDAO extends BaseDAO<HealthCheck,Integer>
{
	private static Logger logger = Logger.getLogger(HealthCheckDAO.class);
	
	public HealthCheckDAO() throws SQLException {
		super(HealthCheck.class);
	}

	public HealthCheckDAO(boolean readonly) throws SQLException {
		super(HealthCheck.class, readonly);
		// TODO Auto-generated constructor stub
	}
		
	public String getServerTimezone() throws SQLException
	{
		DBConnection session = getSession();
		ResultSet rs = null;
		String result = null;
		try 
		{		
			DBQuery query = session.createQuery();
			
			rs = query.executeQuery("SELECT IF(@@session.time_zone = 'SYSTEM', @@system_time_zone, @@session.time_zone) as timezone;");
			if (rs.next())
			{
				result = rs.getString("timezone");
			}
						
			return result;
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
	
	public Date getServerTime() throws SQLException
	{
		DBConnection session = getSession();
		ResultSet rs = null;
		Date result = null;
		try 
		{		
			DBQuery query = session.createQuery();
			
			rs = query.executeQuery("select now() as curtime;");
			if (rs.next())
			{
				result = rs.getTimestamp("curtime");
			}
						
			return result;
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
	
	public void clearHealthInfoByServer(String server_name) throws SQLException
	{
		DBConnection session = getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			String sql = "delete from " + query.getDBName() + ".health_check where server_id = '" + server_name + "'";
			query.executeUpdate(sql);
		}
		catch(Exception e)
		{
			logger.error("Clear health info errro -" + e, e);
		}
		finally
		{
			if( session != null )
				session.close();
		}
	}
	
	public void clearHealthInfoByServerAndService(String server_name, String service) throws SQLException
	{
		DBConnection session = getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			String sql = "delete from " + query.getDBName() + ".health_check where server_id = '" + server_name + "' and service = '" + service + "'";
			query.executeUpdate(sql);
		}
		catch(Exception e)
		{
			logger.error("Clear health info errro -" + e, e);
		}
		finally
		{
			if( session != null )
				session.close();
		}
	}
	
	public HealthCheck getServerHealthInfo(String server_id) throws SQLException
	{
		DBConnection session = getSession();
		HealthCheck result = null;
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(HealthCheck.class);
			String sql = "select * from " + query.getDBName() + ".health_check where server_id = '" + server_id +"'";
			result = (HealthCheck)query.executeQueryAsSingleObject(sql);
		}
		catch(Exception e)
		{
			logger.error("get health info error -" + e, e);
		}
		finally
		{
			if( session != null )
				session.close();
		}
		
		return result;
	}
	
	public List<HealthCheck> getLatestServerHealthInfos() throws SQLException
	{
		DBConnection session = getSession();
		List<HealthCheck> result = null;
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(HealthCheck.class);
			String sql = "select * from " + query.getDBName() + ".health_check where timediff(now(), lastupdate) < 300;";
			result = (List<HealthCheck>)query.executeQueryAsObject(sql);
		}
		catch(Exception e)
		{
			logger.error("get health info error -" + e, e);
		}
		finally
		{
			if( session != null )
				session.close();
		}
		
		return result;
	}
}
