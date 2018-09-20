package com.littlecloud.control.dao;

import java.sql.SQLException;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.GeoFencePoints;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.pool.object.NetInfoObject;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Condition;
import com.peplink.api.db.query.Criteria;

public class GeoFencePointsDAO extends BaseDAO<GeoFencePoints, Integer> {
	private static final Logger log = Logger.getLogger(GeoFencePointsDAO.class);
	public GeoFencePointsDAO() throws SQLException {
		super(GeoFencePoints.class);
	} // end constructor
	public GeoFencePointsDAO(String orgId) throws SQLException {
		super(GeoFencePoints.class, orgId);
	} // end GeoFencePointsDAO(String orgId)

	public GeoFencePointsDAO(String orgId, boolean readonly) throws SQLException {
		super(GeoFencePoints.class, orgId, readonly);
	} // end GeoFencePointsDAO(String orgId, boolean readonly)
	
	public List<GeoFencePoints> getGeoFencePointsListByGeoId(Integer geoId) throws SQLException
	{
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("geo_id", Criteria.EQ, geoId);
			return (List<GeoFencePoints>) query.executeQueryAsObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		} // end try ... catch ... finally ...					
	} // end getGeoFencePointsListByGeoId
	
	public void deleteByGeoId(Integer geoId) throws SQLException {
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();

			StringBuilder sbSql = new StringBuilder();
			sbSql.append("delete from ");
			sbSql.append(query.getDBName() + ".geo_fence_points ");
			sbSql.append("where geo_id = " + geoId);
			
			query.executeUpdate(sbSql.toString());
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		} // end try ... catch ... finally ...	
	}
	
	public void delete(GeoFencePoints geoFencePoints) throws SQLException {
		log.debug("deleting " + this.persistentClass.getSimpleName() + " instance");
		super.delete(geoFencePoints);
		
//		OrgInfoUtils.del(orgId, geoFencePoints);
//		NetInfoObject netInfo = NetUtils.getNetInfoObject(orgId, net.getId());
//		NetUtils.removeNetInfoObject(netInfo);
	}
	
	public void delete (GeoFencePoints[] geoFencePointsArray) throws SQLException {
		log.debug("deleting " + this.persistentClass.getSimpleName() + " instance");
		super.delete(geoFencePointsArray);
	}
	
	public void save(GeoFencePoints geoFencePoints) throws SQLException {
		log.debug("saving " + this.persistentClass.getSimpleName() + " instance");
		super.save(geoFencePoints);
//		OrgInfoUtils.saveNetworks(orgId, geoFencePoints);
	}
	
	public void update(GeoFencePoints geoFencePoints) throws SQLException 
	{
		log.debug("updating " + this.persistentClass.getSimpleName() + " instance");
		super.update(geoFencePoints);
//		OrgInfoUtils.saveOrUpdateNetworks(orgId, net);
	}
	
	public void saveOrUpdate(GeoFencePoints geoFencePoints) throws SQLException {
		log.debug("saveOrUpdate " + this.persistentClass.getSimpleName() + " instance");
		super.saveOrUpdate(geoFencePoints);
		
//		OrgInfoUtils.saveOrUpdateNetworks(orgId, net);
	}
	
	
} // end class
