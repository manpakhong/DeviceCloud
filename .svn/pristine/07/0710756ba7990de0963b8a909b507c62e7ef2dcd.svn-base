package com.littlecloud.control.dao.branch;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import java.util.Collection;

//import com.littlecloud.control.dao.BaseDAO;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.branch.Products;
import com.littlecloud.control.entity.branch.SnsOrganizations;
import com.littlecloud.control.json.model.pepvpn.Peer_Networks;
import com.littlecloud.pool.object.DevOnlineObject;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Criteria;

/**
 * Home object for domain model class Devices.
 * 
 * @see com.littlecloud.control.entity.Devices
 * @author Hibernate Tools
 */
public class SnsOrganizationsDAO extends BaseDAO<SnsOrganizations, Integer> {

	protected static final Logger log = Logger.getLogger(SnsOrganizationsDAO.class);

	public SnsOrganizationsDAO(boolean readonly) throws SQLException {
		super(SnsOrganizations.class, readonly);
	}

	public SnsOrganizationsDAO() throws SQLException {
		super(SnsOrganizations.class);
	}

	public List<DevOnlineObject> getAllDevices() throws SQLException 
	{
		log.debug("getting " + this.getClass().getSimpleName() + " instance with all devices");
		
		DBConnection session = getSession();
		ResultSet rs = null;
		try
		{
			DBQuery query = session.createQuery();
			
			String strDataBase = query.getDBName();
			
			StringBuilder sql = new StringBuilder();
			sql.append("select s.organization_id, s.iana_id, s.sn from ");
			sql.append(strDataBase);
			sql.append(".sns_organizations s ");
			sql.append("order by s.organization_id, s.id desc");
			
			log.debugf("sql=%s",sql);
			
			List<DevOnlineObject> returnList = new ArrayList<DevOnlineObject>();
			
			rs = query.executeQuery(sql.toString());
			while (rs.next())
			{
				DevOnlineObject devO = new DevOnlineObject();
				devO.setOrganization_id(rs.getString(1));
				devO.setIana_id(rs.getInt(2));
				devO.setSn(rs.getString(3));
				returnList.add(devO);
			}
			
			return returnList;
			
		} 
		catch (SQLException e)
		{
			throw e;
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
	}
	
	public List<DevOnlineObject> getAllDevicesWithOrgId(String orgId) throws SQLException 
	{
		log.debug("getting " + this.getClass().getSimpleName() + " instance with all devices");
		
		DBConnection session = getSession();
		ResultSet rs = null;
		try
		{
			DBQuery query = session.createQuery();
			
			String strDataBase = query.getDBName();
			
			StringBuilder sql = new StringBuilder();
			sql.append("select s.organization_id, s.iana_id, s.sn from ");
			sql.append(strDataBase);
			sql.append(".sns_organizations s where s.organization_id = '");
			sql.append(orgId);
			sql.append("' order by s.organization_id, s.id desc");
			
			log.debugf("sql=%s",sql);
			
			List<DevOnlineObject> returnList = new ArrayList<DevOnlineObject>();
			
			rs = query.executeQuery(sql.toString());
			while (rs.next())
			{
				DevOnlineObject devO = new DevOnlineObject();
				devO.setOrganization_id(rs.getString(1));
				devO.setIana_id(rs.getInt(2));
				devO.setSn(rs.getString(3));
				returnList.add(devO);
			}
			
			return returnList;
			
		} 
		catch (SQLException e)
		{
			throw e;
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
	}
	
	public SnsOrganizations findByIanaIdSnOrgId(Integer IanaId, String Sn, String orgId) throws SQLException 
	{
		log.debug("getting " + this.getClass().getSimpleName() + " instance with IanaId: " + IanaId + " and Sn: " + Sn);
		// Query query =
		// session.createQuery("Select so From SnsOrganizations so where so.ianaId = :ianaId and so.sn = :sn");
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("iana_id", Criteria.EQ, IanaId);
			query.addCriteria("sn", Criteria.EQ, Sn);
			query.addCriteria("organization_id", Criteria.EQ, orgId);
			return (SnsOrganizations) query.executeQueryAsSingleObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		}			
	}
	
	public List<SnsOrganizations> getAllSnsOrganizations() throws SQLException 
	{
		log.debug("getting " + this.getClass().getSimpleName() + " all instance");
		// Query query =
		// session.createQuery("Select so From SnsOrganizations so where so.ianaId = :ianaId and so.sn = :sn");
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setDistinct(true);
			query.setQueryClass(persistentClass);
			return (List<SnsOrganizations>) query.executeQueryAsObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		}			
	}
	
	public SnsOrganizations getOrgIdByIanaSn(Integer IanaId, String Sn) throws SQLException {
		log.debug("getting " + this.getClass().getSimpleName() + " instance with IanaId: " + IanaId + " and Sn: " + Sn);
		// Query query =
		// session.createQuery("Select so From SnsOrganizations so where so.ianaId = :ianaId and so.sn = :sn");
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("iana_id", Criteria.EQ, IanaId);
			query.addCriteria("sn", Criteria.EQ, Sn);
			return (SnsOrganizations) query.executeQueryAsSingleObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		}
	}

	public List<String> getDistinctOrgList() throws SQLException {
		log.debug("getting distinct org list" + this.getClass().getSimpleName());
		DBConnection session = getSession();
		ResultSet rs = null;
		try
		{
			DBQuery query = session.createQuery();
			
			StringBuilder sql = new StringBuilder();
			sql.append("select distinct organization_id from ");
			sql.append(query.getDBName()+".sns_organizations");

			rs = query.executeQuery(sql.toString());
			List<String> result = new ArrayList<String>();
			while (rs.next())
			{
				result.add(rs.getString("organization_id"));
			}
			sql = null;
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

	public List<String> getDistinctOrgListUndo(String orgId) throws SQLException {
		log.debug("getting distinct org list" + this.getClass().getSimpleName());
		DBConnection session = getSession();
		ResultSet rs = null;
		try
		{
			DBQuery query = session.createQuery();
			
			StringBuilder sql = new StringBuilder();
			sql.append("select distinct organization_id from ");
			sql.append(query.getDBName()+".sns_organizations where organization_id >= '");
			sql.append(orgId);
			sql.append("' order by organization_id");

			rs = query.executeQuery(sql.toString());
			List<String> result = new ArrayList<String>();
			while (rs.next())
			{
				result.add(rs.getString("organization_id"));
			}
			sql = null;
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
	
	/* for monitor purpose */
	public Long getCount() throws SQLException {
		// return (Long) session.createQuery("select count(*) from SnsOrganizations").uniqueResult();
		DBConnection session = getSession();
		ResultSet rs = null;
		try {
			DBQuery query = session.createQuery();
			
			StringBuilder sql = new StringBuilder();
			sql.append("select count(*) as total from ");
			sql.append(query.getDBName());
			sql.append(".");
			sql.append("sns_organizations");
			
			log.debug("sql="+sql);
			
			rs = query.executeQuery(sql.toString());
			if (rs.next())
				return rs.getLong("total");
			else
				return 0L;
			
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
	
	public List<String> getTopTenOrgList() throws SQLException {
		log.debug("getting top 10 org list" + this.getClass().getSimpleName());
		DBConnection session = getSession();
		ResultSet rs = null;
		try
		{
			DBQuery query = session.createQuery();
			
			StringBuilder sql = new StringBuilder();
			sql.append("select organization_id, count(sn) as count from ");
			sql.append(query.getDBName());
			sql.append(".sns_organizations group by organization_id order by count desc limit 5");

			rs = query.executeQuery(sql.toString());
			List<String> result = new ArrayList<String>();
			while (rs.next())
			{
				result.add(rs.getString("organization_id"));
			}
			sql = null;
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
}
