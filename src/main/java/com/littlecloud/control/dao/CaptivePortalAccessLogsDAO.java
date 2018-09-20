package com.littlecloud.control.dao;

import java.sql.SQLException;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.criteria.CaptivePortalAccessLogCriteria;
import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.CaptivePortalAccessLog;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Condition;
import com.peplink.api.db.query.Criteria;

public class CaptivePortalAccessLogsDAO extends BaseDAO<CaptivePortalAccessLog, Integer>{
	private static final Logger log = Logger.getLogger(CaptivePortalAccessLogsDAO.class);
	public CaptivePortalAccessLogsDAO(String orgId) throws SQLException {
		super(CaptivePortalAccessLog.class, orgId, false);
	}
	public CaptivePortalAccessLogsDAO(String orgId, boolean readonly) throws SQLException {
		super(CaptivePortalAccessLog.class, orgId, readonly);
	}
	
	public List<CaptivePortalAccessLog> getCaptivePortalAccessLogList(CaptivePortalAccessLogCriteria criteria) throws SQLException{
		List<CaptivePortalAccessLog> resultList = null;
		DBConnection session = getSession();
		try{
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			
			int criteriaCount = 0;
			
			if (criteria.getAccessMode() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				query.addCriteria("access_mode", Criteria.EQ, criteria.getAccessMode());
				criteriaCount++;
			}
			if (criteria.getClientMac() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				query.addCriteria("client_mac", Criteria.EQ, criteria.getClientMac());
				criteriaCount++;
			}
			if (criteria.getDateFrom() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				long dateFromLong = criteria.getDateFrom().getTime();
				int dateFromInt = (int) (dateFromLong / 1000);
				
				query.addCriteria("unixtime", Criteria.GTE, dateFromInt);
				criteriaCount++;
			}
			if (criteria.getDateTo() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				long dateToLong = criteria.getDateTo().getTime();
				int dateToInt = (int) (dateToLong / 1000);
				query.addCriteria("unixtime", Criteria.LTE, dateToInt);
				criteriaCount++;
			}
			if (criteria.getDeviceId() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				query.addCriteria("device_id", Criteria.EQ, criteria.getDeviceId());
				criteriaCount++;
			}
			if (criteria.getId() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				query.addCriteria("id", Criteria.EQ, criteria.getId());
				criteriaCount++;
			}
			if (criteria.getNetworkId() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				query.addCriteria("network_id", Criteria.EQ, criteria.getNetworkId());
				criteriaCount++;
			}
			if (criteria.getReportType() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				query.addCriteria("report_type", Criteria.EQ, criteria.getReportType());
				criteriaCount++;
			}
			if (criteria.getSsid() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				query.addCriteria("ssid", Criteria.EQ, criteria.getSsid());
				criteriaCount++;
			}
			if (criteria.getSsidId() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				query.addCriteria("ssid_id", Criteria.EQ, criteria.getSsidId());
				criteriaCount++;
			}
			
			query.addOrderBy("unixtime", false);
			query.addOrderBy("client_mac");

			resultList = (List<CaptivePortalAccessLog>) query.executeQueryAsObject();
			
		} catch (SQLException e){
			throw e;
		} finally{
			if (session!=null) {
				session.close();
			}
		}
		
		return resultList;
	} // end 
}
