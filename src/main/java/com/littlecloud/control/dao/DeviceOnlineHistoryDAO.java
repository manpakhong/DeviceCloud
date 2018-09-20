package com.littlecloud.control.dao;

import java.sql.SQLException;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.report.DeviceOnlineHistories;
import com.littlecloud.control.json.util.DateUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Criteria;

public class DeviceOnlineHistoryDAO extends BaseDAO<DeviceOnlineHistories, String>
{
	private static final Logger log = Logger.getLogger(DeviceOnlineHistoryDAO.class);

	public DeviceOnlineHistoryDAO() throws SQLException 
	{
		super(DeviceOnlineHistories.class);
		// TODO Auto-generated constructor stub
	}
	
	public DeviceOnlineHistoryDAO(String orgId) throws SQLException {
		super(DeviceOnlineHistories.class, orgId);
	}

	public DeviceOnlineHistoryDAO(String orgId, boolean readonly) throws SQLException {
		super(DeviceOnlineHistories.class, orgId, readonly);
	}
	
	public List<DeviceOnlineHistories> getAllOnlineHistoryByDeviceId(Integer devId)
	{
		List<DeviceOnlineHistories> results = null;
		DBConnection session = null;
		int starttime = -1;
		try
		{
			session = getSession();
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceOnlineHistories.class);
			
//			starttime = (int) (DateUtils.getUtcDateStartOfDay().getTime()/1000);
//			String sql = "select * from "+query.getDBName()+".device_online_histories where device_id = "+ devId.intValue()+
//					" and (online_time > "+starttime+" or offline_time > "+starttime+ ") order by offline_time desc ";
			
			String sql = "select * from "+query.getDBName()+".device_online_histories where device_id = "+ devId.intValue()
					+" order by offline_time desc, online_time desc ";
			
			log.debugf("getOnlineHistoryByDeviceIdFromTime sql = %s", sql);
			results = (List<DeviceOnlineHistories>)query.executeQueryAsObject(sql);
			log.debug("find online history successfully, result size: " + results.size());
		}
		catch( Exception e )
		{
			log.error("getOnlineHistoryByDeviceIdFromTime failed!", e);
		}
		finally
		{
			if( session != null )
			{
				try 
				{
					session.close();
				} 
				catch (SQLException e) 
				{
					log.error("SQLException", e);
				}
			}
		}
		
		return results;
	}
	
	public List<DeviceOnlineHistories> getOnlineHistoryByDeviceId(Integer devId, Integer start, Integer limit)
	{
		List<DeviceOnlineHistories> results = null;
		DBConnection session = null;
		
		try
		{
			session = getSession();
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceOnlineHistories.class);
			String sql = "select * from "+query.getDBName()+".device_online_histories where device_id = "+
						devId.intValue()+" order by offline_time desc limit "+start.intValue()+","+limit.intValue();
			results = (List<DeviceOnlineHistories>)query.executeQueryAsObject(sql);
			log.debug("find online history successfully, result size: " + results.size());
		}
		catch( Exception e )
		{
			log.error("find online history failed!", e);
			e.printStackTrace();
		}
		finally
		{
			if( session != null )
			{
				try 
				{
					session.close();
				} 
				catch (SQLException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return results;
	}
	
	public DeviceOnlineHistories getLastOfflineHistoryByTime(Integer devId)
	{
		DeviceOnlineHistories result = null;
		DBConnection session = null;
		
		try
		{
			session = getSession();
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceOnlineHistories.class);
			query.addCriteria("device_id", Criteria.EQ, devId);
			query.addOrderBy("offline_time desc");
			result = (DeviceOnlineHistories)query.executeQueryAsSingleObject();
		}
		catch( Exception e )
		{
			log.error("find online history failed!", e);
			e.printStackTrace();
		}
		finally
		{
			if( session != null )
			{
				try 
				{
					session.close();
				} 
				catch (SQLException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}

}
