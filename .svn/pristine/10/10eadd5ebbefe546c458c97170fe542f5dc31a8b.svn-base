package com.littlecloud.control.dao;

import java.sql.SQLException;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.criteria.CaptivePortalSessionsCriteria;
import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.dao.jdbc.BaseDaoInstances.InstanceType;
import com.littlecloud.control.entity.CaptivePortalSessions;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Condition;
import com.peplink.api.db.query.Criteria;
import com.peplink.api.db.util.DBUtil;

public class CaptivePortalSessionsDAO extends BaseDAO<CaptivePortalSessions, Integer>{
	private static final Logger log = Logger.getLogger(CaptivePortalSessionsDAO.class);
	
	public CaptivePortalSessionsDAO() throws SQLException {
		super(CaptivePortalSessions.class, InstanceType.CAPTIVEPORTAL);
	} // end constructor
	public CaptivePortalSessionsDAO(String orgId) throws SQLException {
		super(CaptivePortalSessions.class, InstanceType.CAPTIVEPORTAL, orgId);
	}// end CaptivePortalSessionsDAO(String orgId)
	
	public List<CaptivePortalSessions> getCaptivePortalSessions(CaptivePortalSessionsCriteria criteria) throws SQLException{
		if (criteria == null){
			log.warnf("CaptivePortalSessionsDAO - getCaptivePortalSessions, criteria is null!");
			return null;
		} else {
			if (criteria.getClientMac() == null || criteria.getNetworkId() == null || (criteria.getSsid() == null && criteria.getSsidId() == null)){
				log.warnf("CaptivePortalSessionsDAO - getCaptivePortalSessions, criteria - clientMac, networkId or ssid is/ are null!");
				return null;
			}
		}
		
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			
			int criteriaCount = 0;
			
			if (criteria.getClientMac() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				
				query.addCriteria("client_mac", Criteria.EQ, criteria.getClientMac());
				criteriaCount++;
			}
			

			
			if (criteria.getSsidId() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				query.addCriteria("ssid_id", Criteria.EQ, criteria.getSsidId());
			}
			

			
			if (criteria.getNetworkId() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				query.addCriteria("network_id", Criteria.EQ, criteria.getNetworkId());
			}
			

			
			if (criteria.getSsid() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				query.addCriteria("ssid", Criteria.EQ, criteria.getSsid());
			}

			List<CaptivePortalSessions> resultList = (List<CaptivePortalSessions>) query.executeQueryAsObject();
			
			DBUtil.warning("---- getCaptivePortalSessions: " + query.getQuery());
			DBUtil.warning("---- getCaptivePortalSessions ResultList: " + (resultList == null?0:resultList.size()));
			
			return resultList;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		} // end try ... catch ... finally ...					
	} // end getCaptivePortalSessions
	
	public void delete(CaptivePortalSessions[] captivePortalSessionsArray) throws SQLException {
		log.debug("deleting " + this.persistentClass.getSimpleName() + " instance");
		super.delete(captivePortalSessionsArray);
		
	}
	public void delete(CaptivePortalSessions captivePortalSessions) throws SQLException {
		log.debug("deleting " + this.persistentClass.getSimpleName() + " instance");
		super.delete(captivePortalSessions);
	}
	
	public void save(CaptivePortalSessions captivePortalSessions) throws SQLException {
		log.debug("saving " + this.persistentClass.getSimpleName() + " instance");
		super.save(captivePortalSessions);

	}
	
	public void update(CaptivePortalSessions captivePortalSessions) throws SQLException 
	{
		log.debug("updating " + this.persistentClass.getSimpleName() + " instance");
		super.update(captivePortalSessions);

	}
	
	public void saveOrUpdate(CaptivePortalSessions captivePortalSessions) throws SQLException {
		log.debug("saveOrUpdate " + this.persistentClass.getSimpleName() + " instance");
		super.saveOrUpdate(captivePortalSessions);

	}	
	
}
