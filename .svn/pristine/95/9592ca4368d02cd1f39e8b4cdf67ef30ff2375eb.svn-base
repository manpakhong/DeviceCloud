package com.littlecloud.control.dao.branch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.jboss.logging.Logger;
import com.littlecloud.control.entity.viewobject.QuartzTriggers;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.util.DBUtil;

public class QzTriggersDAO
{
	private static final Logger log = Logger.getLogger(QzTriggersDAO.class);
	
	public static HashMap<String, QuartzTriggers> getTriggersInfo() throws SQLException
	{
		HashMap<String, QuartzTriggers> map = null;
		DBConnection session = null;
		ResultSet rs = null;
		
		try
		{
			DBUtil dbUtil = DBUtil.getInstance();
			session = dbUtil.getConnection(true, null, true);
			DBQuery query = session.createQuery();
			String sql = "SELECT TRIGGER_GROUP,NEXT_FIRE_TIME,PREV_FIRE_TIME,DESCRIPTION,TRIGGER_STATE FROM littlecloud_branch_production.QRTZ_TRIGGERS GROUP BY TRIGGER_GROUP;";
			rs = query.executeQuery(sql);
			if( rs != null )
			{
				QuartzTriggers trigger = null;
				map = new HashMap<String, QuartzTriggers>();
				while( rs.next() )
				{
					trigger = new QuartzTriggers();
					String key = rs.getString("TRIGGER_GROUP");
					String description = rs.getString("DESCRIPTION");
					String status = rs.getString("TRIGGER_STATE");
					long next_fire_time = rs.getLong("NEXT_FIRE_TIME");
					long prev_fire_time = rs.getLong("PREV_FIRE_TIME");
					trigger.setStatus(status);
					trigger.setDescription(description);
					trigger.setNext_fire_time(next_fire_time);
					trigger.setPrev_fire_time(prev_fire_time);
					map.put(key, trigger);
				}
			}
		}
		catch(Exception e)
		{
			log.error("Get trigger info error -", e);
			return null;
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
		
		return map;
	}
}
