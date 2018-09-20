package com.littlecloud.control.dao;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.GeoFences;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Condition;
import com.peplink.api.db.query.Criteria;

public class GeoFencesDAO extends BaseDAO<GeoFences, Integer>{
	private static final Logger log = Logger.getLogger(GeoFencesDAO.class);
	
	public GeoFencesDAO() throws SQLException {
		super(GeoFences.class);
	} // end constructor
	public GeoFencesDAO(String orgId) throws SQLException {
		super(GeoFences.class, orgId);
	}// end GeoFencesDAO(String orgId)

	public GeoFencesDAO(String orgId, boolean readonly) throws SQLException {
		super(GeoFences.class, orgId, readonly);
	}// end GeoFencesDAO(String orgId, boolean readonly)
	
	public List<GeoFences> getGeoFencesListByNetworkIdButNoDeviceTag(Integer networkId) throws SQLException{
		// JDBC
		DBConnection session = getSession();
		try{
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("select * from ");
			sbSql.append(query.getDBName() + ".geo_fences ");
			sbSql.append("where network_id = " + networkId + " ");
			sbSql.append("and (device_tag is null or device_tag = '') ");
			
			List<GeoFences> results = (List<GeoFences>) query.executeQueryAsObject(sbSql.toString());
		
			log.debug("query successful, result size: " + results.size());
			return results;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		} // end try ... catch ... finally ...	
	}
	
	public List<GeoFences> getGeoFencesListByTagId(Integer tagId) throws SQLException{
		log.info("getting GeoFences list from id list " + tagId.toString());

		try {		
			// JDBC
			DBConnection session = getSession();
			try{
				DBQuery query = session.createQuery();
				query.setQueryClass(persistentClass);
				
				StringBuilder sbSql = new StringBuilder();
				
				sbSql.append("select * from ");
				sbSql.append(query.getDBName() + ".geo_fences ");
				sbSql.append("where find_in_set('" + tagId + "', ");
				sbSql.append("device_tag) > 0 ");
				
				List<GeoFences> results = (List<GeoFences>) query.executeQueryAsObject(sbSql.toString());
			
				log.debug("query successful, result size: " + results.size());
				return results;
				
			} catch (SQLException e){
				throw e;
			} finally{
				if (session!=null) session.close();
			}
		} catch (RuntimeException re) {
			log.error("query failed", re);
			throw re;
		}
	}
	
	public List<GeoFences> getGeoFencesListByDeviceId(Integer deviceId) throws SQLException{
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("device_id", Criteria.EQ, deviceId);
			query.addCondition(Condition.AND);
			query.addCriteria("enabled", Criteria.EQ, 1);
			return (List<GeoFences>) query.executeQueryAsObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		} // end try ... catch ... finally ...	
	} // end getGeoFencesListByDeviceId
	
	public List<GeoFences> getGeoFencesListByNetworkId(Integer networkId) throws SQLException{
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("network_id", Criteria.EQ, networkId);
			return (List<GeoFences>) query.executeQueryAsObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		} // end try ... catch ... finally ...					
	} // end getGeoFencesListByNetworkId
	
	public List<GeoFences> getGeoFencesList(List<Integer> idLst) throws SQLException
	{
		log.info("getting GeoFences list from id list " + idLst.toString());

		try {		
			// JDBC
			DBConnection session = getSession();
			try{
				DBQuery query = session.createQuery();
				query.setQueryClass(persistentClass);
				
				String strIdList = "";
				Iterator<Integer> iter = idLst.iterator();
				while (iter.hasNext()){
					int intId = (Integer) iter.next();
					if (!strIdList.equals("")){
						strIdList += ", ";
					}
					strIdList += Integer.toString(intId);
				}
				String strSQL = "select * from " + query.getDBName() + ".geo_fences";
				if (!strIdList.equals("")){
					 strSQL += " where id in (" + strIdList + ")";
				}
				List<GeoFences> results = (List<GeoFences>) query.executeQueryAsObject(strSQL);
			
				log.debug("query successful, result size: " + results.size());
				return results;
				
			} catch (SQLException e){
				throw e;
			} finally{
				if (session!=null) session.close();
			}
		} catch (RuntimeException re) {
			log.error("query failed", re);
			throw re;
		}
	}
	
	public GeoFences getGeoFencesById(Integer id) throws SQLException{
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("id", Criteria.EQ, id);
			List<GeoFences> geoFencesList = (List<GeoFences>) query.executeQueryAsObject();
			if (geoFencesList != null && geoFencesList.size() > 0){
				return geoFencesList.get(0);				
			} else{
				return null;
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		} // end try ... catch ... finally ...					
	} // end getGeoFencesListByNetworkId
	
	public void delete(GeoFences[] geoFencesArray) throws SQLException {
		log.debug("deleting " + this.persistentClass.getSimpleName() + " instance");
		super.delete(geoFencesArray);
		
//		OrgInfoUtils.del(orgId, geoFencePoints);
//		NetInfoObject netInfo = NetUtils.getNetInfoObject(orgId, net.getId());
//		NetUtils.removeNetInfoObject(netInfo);
	}
	public void delete(GeoFences geoFences) throws SQLException {
		log.debug("deleting " + this.persistentClass.getSimpleName() + " instance");
		super.delete(geoFences);
		
//		OrgInfoUtils.del(orgId, geoFencePoints);
//		NetInfoObject netInfo = NetUtils.getNetInfoObject(orgId, net.getId());
//		NetUtils.removeNetInfoObject(netInfo);
	}
	
	public void save(GeoFences geoFences) throws SQLException {
		log.debug("saving " + this.persistentClass.getSimpleName() + " instance");
		super.save(geoFences);
//		OrgInfoUtils.saveNetworks(orgId, geoFencePoints);
	}
	
	public void update(GeoFences geoFences) throws SQLException 
	{
		log.debug("updating " + this.persistentClass.getSimpleName() + " instance");
		super.update(geoFences);
//		OrgInfoUtils.saveOrUpdateNetworks(orgId, net);
	}
	
	public void saveOrUpdate(GeoFences geoFences) throws SQLException {
		log.debug("saveOrUpdate " + this.persistentClass.getSimpleName() + " instance");
		super.saveOrUpdate(geoFences);
//		OrgInfoUtils.saveOrUpdateNetworks(orgId, net);
	}	
	
} // end class GeoFencesDAO
