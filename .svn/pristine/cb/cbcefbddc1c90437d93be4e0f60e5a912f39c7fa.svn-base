package com.littlecloud.control.dao;

import java.sql.SQLException;
import java.util.List;


/*
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
*/
import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.report.EventLog;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Condition;
import com.peplink.api.db.query.Criteria;
import com.peplink.api.db.query.CriteriaGroup;

public class EventLogDAO extends BaseDAO<EventLog, String>{

	private static final Logger log = Logger.getLogger(EventLogDAO.class);

	public EventLogDAO() throws SQLException {
		super(EventLog.class);
	}

	public EventLogDAO(String orgId) throws SQLException {
		super(EventLog.class, orgId);
	}
	
	public EventLogDAO(String orgId, boolean readonly) throws SQLException {
		super(EventLog.class, orgId, readonly);
	}
	
	/* API SERVER CALL */
	public List<EventLog> getPerPageRecords(Integer networkId, int page_no, int count, String event, List<Integer> id_list, String keyword, Integer from_date, Integer to_date, String client_mac, int queryTimeout) throws SQLException
	{
		List<EventLog> results = null;
		
		DBConnection session = getSession();
		try 
		{
			DBQuery query = session.createQuery();
			
			if (queryTimeout > 0)
				query.setTimeout(queryTimeout);
			
			query.setQueryClass(EventLog.class);
			query.addCriteria("network_id", Criteria.EQ, networkId);
			
			if (id_list.size() > 0)
			{
				query.addCriteria("device_id", Criteria.IN, id_list);
			}
			
			if (client_mac != null && !client_mac.equals(""))
			{
				query.addCriteria("client_mac", Criteria.EQ, client_mac);
			}
			
			if (keyword!=null && !keyword.equals("") )
			{
				CriteriaGroup group = new CriteriaGroup(Condition.OR);
				String key = "%" + keyword + "%";
				group.addCriteria("ssid", Criteria.LIKE, key);
				group.addCriteria("client_name", Criteria.LIKE, key);
				group.addCriteria("detail", Criteria.LIKE, key);
				group.addCriteria("client_mac", Criteria.LIKE, key);
				query.addCriteria(group);
			}

			if (from_date != null)
			{
				query.addCriteria("unixtime", Criteria.LT, from_date);
			}
			if (to_date != null)
			{
				query.addCriteria("unixtime", Criteria.GE, to_date);
			}
			
			if(event!=null && !event.equals(""))
			{
				String[] events = event.split(",");
				
				CriteriaGroup group = new CriteriaGroup(Condition.OR);
				for( String evt : events)
				{
					group.addCriteria("event_type", Criteria.EQ, evt.trim());
				}
				query.addCriteria(group);
			}
			
			if( count > 0 && page_no >= 0 )
			{
				query.setLimit(page_no * count, count);
			}
			
			query.addOrderBy("unixtime desc");
			
			results = (List<EventLog>)query.executeQueryAsObject();
		} 
		catch (Exception re) 
		{
			log.error("query event log failed", re);
			re.printStackTrace();
			try {
				throw re;
			} catch (Exception e) {
				log.error("getPerPageRecords error ",e);
			}
		} 
		finally 
		{
			if (session!=null) session.close();
		}
		return results;
	}

	public EventLog getDuplicateEventLog(int network_id, int device_id, int unixtime, String event_type) throws SQLException
	{
		EventLog result = null;
		DBConnection session = getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(EventLog.class);
			query.addCriteria("network_id",Criteria.EQ,network_id);
			query.addCondition(Condition.AND);
			query.addCriteria("device_id", Criteria.EQ, device_id);
			query.addCondition(Condition.AND);
			query.addCriteria("unixtime", Criteria.EQ, unixtime);
			query.addCondition(Condition.AND);
			query.addCriteria("event_type", Criteria.EQ, event_type);
			result = (EventLog)query.executeQueryAsSingleObject();
		}
		catch (Exception e) 
		{
			log.error("Get duplicated eventlog error ",e);
		} 
		finally 
		{
			if (session!=null) 
				session.close();
		}
		
		return result;
	}
	
//	select * from peplink_organization_stitLW.event_log where device_id = 185 and network_id = 3 and unixtime > 1404950400 and detail like 'Device is online' order by unixtime asc;
	public EventLog getLastOnlineLaterThan(String network_id, Integer device_id, int timestamp) throws SQLException
	{
		EventLog event = null;
		DBConnection session = getSession();
		
		log.infof("netid=%s, did=%d, ts=%d", network_id, device_id, timestamp);
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(EventLog.class);
			query.addCriteria("device_id", Criteria.EQ, device_id);
			query.addCondition(Condition.AND);
			query.addCriteria("network_id", Criteria.EQ, network_id);
			query.addCondition(Condition.AND);
			query.addCriteria("unixtime", Criteria.GT, timestamp);
			query.addCondition(Condition.AND);
			query.addCriteria("detail", Criteria.LIKE, "Device is online");
			query.addOrderBy("unixtime asc");
			event = (EventLog)query.executeQueryAsSingleObject();
		}
		catch (Exception e) 
		{
			log.error("getTodayEventLogOfDevice error ",e);
		} 
		finally 
		{
			if (session!=null) 
				session.close();
		}
		return event;
	}
}