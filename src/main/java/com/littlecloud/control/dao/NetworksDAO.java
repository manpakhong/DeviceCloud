package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.pool.object.NetInfoObject;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Criteria;

/**
 * Home object for domain model class Networks.
 * 
 * @see com.littlecloud.control.entity.Networks
 * @author Hibernate Tools
 */
public class NetworksDAO extends BaseDAO<Networks, Integer> {

	private static final Logger log = Logger.getLogger(NetworksDAO.class);

	public NetworksDAO() throws SQLException {
		super(Networks.class);
	}

	public NetworksDAO(String orgId) throws SQLException {
		super(Networks.class, orgId);
	}

	public NetworksDAO(String orgId, boolean readonly) throws SQLException {
		super(Networks.class, orgId, readonly);
	}

	public List<String> getDistinctTimezone() throws SQLException {
		log.debug("getting distinct Timexone List" + this.getClass().getSimpleName());
		DBConnection session = getSession();
		ResultSet rs = null;
		try 
		{
			//Criteria criteria = session.createCriteria(Networks.class).setProjection(Projections.distinct(Projections.property("timezone")));			
			DBQuery query = session.createQuery();
			
			StringBuilder sql = new StringBuilder();
			sql.append("select timezone from ");
			sql.append(query.getDBName());
			sql.append(".");
			sql.append("networks where active = 1");

			List<String> result = new ArrayList<String>();
			rs = query.executeQuery(sql.toString());
			sql = null;
			while(rs.next())
			{
				result.add(rs.getString("timezone"));
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

	public List<Networks> getAllNetworks() throws SQLException {
		log.debug("getting List of all networks " + this.getClass().getSimpleName());
		//Query query = session.createQuery("select n from Networks n");
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("active", Criteria.EQ, 1);
			return (List<Networks>) query.executeQueryAsObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		}		
	}

	public Networks getNetworksById(Integer id) throws SQLException{
		DBConnection session = getSession();
		try {
			Networks networks = new Networks();
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("id", Criteria.EQ, id);
			query.addCriteria("active", Criteria.EQ, 1);
			List<Networks> networksList = (List<Networks>) query.executeQueryAsObject();
			if (networksList != null && networksList.size() == 1){
				networks = networksList.get(0);
			}
			return networks;
			
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		}	
	}
	
	public List<Networks> getNetworksByOrgId(String param_orgId) throws SQLException
	{
		//String hql = "select n from Networks n where n.organizationId = '" + param_orgId + "'";
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("organization_id", Criteria.EQ, param_orgId);
			query.addCriteria("active", Criteria.EQ, 1);
			return (List<Networks>) query.executeQueryAsObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		}			
	}

	public List<Networks> getAllNetworksForHousekeep(String param_orgId) throws SQLException
	{
		//String hql = "select n from Networks n where n.organizationId = '" + param_orgId + "'";
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(Networks.class);
			query.addCriteria("organization_id", Criteria.EQ, param_orgId);
			return (List<Networks>) query.executeQueryAsObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		}			
	}
	
	public List<Networks> getNetworksByOrgIdNTimezone(String orgId, String timezone) throws SQLException
	{
		log.debug("getting List of all networks " + this.getClass().getSimpleName() + " with orgId: " + orgId + " timezone: " + timezone);
		//Query query = session.createQuery("select n from Networks n where n.organizationId = :orgId and n.timezone = :timezone");
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("organization_id", Criteria.EQ, orgId);
			query.addCriteria("timezone", Criteria.EQ, timezone);
			query.addCriteria("active", Criteria.EQ, 1);
			return (List<Networks>) query.executeQueryAsObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		}				
	}
	
	public Networks getNetworksByDevId(Integer devId) throws SQLException
	{
		log.debug("getting networks " + this.getClass().getSimpleName() + " with devId: " + devId);
		
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Networks.class);
			
			String strSQL = "select * from " + query.getDBName() + ".networks n where active = 1 and n.id = (select d.network_id from " + query.getDBName() + ".devices d where d.id = " + Integer.toString(devId) + ")";
//			System.out.println(strSQL);
			
			return (Networks) query.executeQueryAsSingleObject(strSQL);
			
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	
/* 
 * 2.0.10
 */
	public void delete(Networks net) throws SQLException {
		log.debug("deleting " + this.persistentClass.getSimpleName() + " instance");
		super.delete(net);
		
		OrgInfoUtils.deleteNetworks(orgId, net);
		NetInfoObject netInfo = NetUtils.getNetInfoObject(orgId, net.getId());
		NetUtils.removeNetInfoObject(netInfo);
	}
	
	public void save(Networks net) throws SQLException {
		log.debug("saving " + this.persistentClass.getSimpleName() + " instance");
		super.save(net);
		OrgInfoUtils.saveNetworks(orgId, net);
	}
	
	public void update(Networks net) throws SQLException 
	{
		log.debug("updating " + this.persistentClass.getSimpleName() + " instance");
		super.update(net);
		OrgInfoUtils.saveOrUpdateNetworks(orgId, net);
	}
	
	public void saveOrUpdate(Networks net) throws SQLException {
		log.debug("saveOrUpdate " + this.persistentClass.getSimpleName() + " instance");
		super.saveOrUpdate(net);
		
		OrgInfoUtils.saveOrUpdateNetworks(orgId, net);
	}
	
}
