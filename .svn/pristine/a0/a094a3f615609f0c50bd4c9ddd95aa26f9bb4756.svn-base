package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.criteria.ConfigurationSsidsCriteria;
import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.CaptivePortalSessions;
import com.littlecloud.control.entity.ConfigurationSsids;
import com.littlecloud.control.entity.ConfigurationSsidsId;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.pool.utils.Utils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Condition;
import com.peplink.api.db.query.Criteria;

/**
 * Home object for domain model class ConfigurationSsids.
 * 
 * @see com.littlecloud.control.entity.ConfigurationSsids
 * @author Hibernate Tools
 */
public class ConfigurationSsidsDAO extends BaseDAO<ConfigurationSsids, ConfigurationSsidsId> {

	private static final Logger log = Logger.getLogger(ConfigurationSsidsDAO.class);

	public ConfigurationSsidsDAO() throws SQLException {
		super(ConfigurationSsids.class);
	}

	public ConfigurationSsidsDAO(String orgId) throws SQLException {
		super(ConfigurationSsids.class, orgId);
	}

	public ConfigurationSsidsDAO(String orgId, boolean readonly) throws SQLException {
		super(ConfigurationSsids.class, orgId, readonly);
	}

	public List<ConfigurationSsids> getConfigurationSsidsByDeviceLevelThenNetworkLevel(ConfigurationSsidsCriteria criteria){
		if (criteria != null){
			if (log.isDebugEnabled()){
				log.debugf("CAPORT20140526 - ConfigurationSsidsDAO.getConfigurationSsidsByDeviceIdThenNetworkId(), criteria- deviceId:%s, networkId:%s, enabled:%s, ssid:%s ", criteria.getDeviceId(), criteria.getNetworkId(), criteria.getEnabled(), criteria.getSsid());
			}
		} else {
			if (log.isDebugEnabled()){
				log.debugf("CAPORT20140526 - ConfigurationSsidsDAO.getConfigurationSsidsByDeviceIdThenNetworkId(), criteria is null!!!");
			}
		}
		try{
			
			if (criteria.getDeviceId() != null && criteria.getDeviceId() != 0){
				ConfigurationSsidsCriteria deviceCriteria = new ConfigurationSsidsCriteria();
				deviceCriteria.setNetworkId(criteria.getNetworkId());
				deviceCriteria.setDeviceId(criteria.getDeviceId());
				deviceCriteria.setEnabled(criteria.getEnabled());
				deviceCriteria.setSsidEnabled(criteria.getSsidEnabled());
				deviceCriteria.setSsid(criteria.getSsid());
				
				List<ConfigurationSsids> device_ConfigurationSsidsList = getConfigurationSsids(deviceCriteria);
				if (device_ConfigurationSsidsList != null && device_ConfigurationSsidsList.size() > 0){
					if (log.isDebugEnabled()){
						log.debugf("CAPORT20140526 - ConfigurationSsidsDAO.getConfigurationSsidsByDeviceIdThenNetworkId() - deviceLevel - devId:%s, networkId:%s, ssid:%s", criteria.getDeviceId(), criteria.getNetworkId(), criteria.getSsid());
					}
					return device_ConfigurationSsidsList;
				}
			}
			
			if (criteria.getNetworkId() != null){
				ConfigurationSsidsCriteria networkCriteria = new ConfigurationSsidsCriteria();
				networkCriteria.setNetworkId(criteria.getNetworkId());
				networkCriteria.setEnabled(criteria.getEnabled());
				networkCriteria.setSsidEnabled(criteria.getSsidEnabled());
				networkCriteria.setSsid(criteria.getSsid());
				
				List<ConfigurationSsids> network_ConfigurationSsidsList = getConfigurationSsids(networkCriteria);
				if (network_ConfigurationSsidsList != null && network_ConfigurationSsidsList.size() > 0){
					if (log.isDebugEnabled()){
						log.debugf("CAPORT20140526 - ConfigurationSsidsDAO.getConfigurationSsidsByDeviceIdThenNetworkId() - networkLevel - devId:%s, networkId:%s, ssid:%s", criteria.getDeviceId(), criteria.getNetworkId(), criteria.getSsid());
					}
					return network_ConfigurationSsidsList;
				}
			}
			
		} catch (Exception e){
			log.errorf(e, "CAPORT20140526 - ConfigurationSsidsDAO.getConfigurationSsidsByDeviceIdThenNetworkId()");
		}
		return null;
	}
		
	/* ConfigUtils is using this API, please dont make any change. Thanks. */
	public List<ConfigurationSsids> getConfigurationSsids(ConfigurationSsidsCriteria criteria) throws SQLException {
		if (log.isDebugEnabled()){
			log.debugf("CAPORT20140526 - ConfigurationSsidsDAO.getConfigurationSsids(), criteria- deviceId:%s, networkId:%s, enabled:%s, ssidEnabled:%s, ssid:%s ", criteria.getDeviceId(), criteria.getNetworkId(), criteria.getEnabled(), criteria.getSsidEnabled(), criteria.getSsid());
		}
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			
			int criteriaCount = 0;
			
			
			if (criteria.getDeviceId() != null){
				query.addCriteria("device_id", Criteria.EQ, criteria.getDeviceId());
				criteriaCount++;
			}

			// will be obsoleted
			if (criteria.getEnabled() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				
				int enabled = 0;
				if (criteria.getEnabled()){
					enabled = 1;
				}
				
				query.addCriteria("enabled", Criteria.EQ, enabled);
				criteriaCount++;
			}

			// use this one replace enabled field
			if (criteria.getSsidEnabled() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				
				int enabled = 0;
				if (criteria.getSsidEnabled()){
					enabled = 1;
				}
				
				query.addCriteria("ssid_enabled", Criteria.EQ, enabled);
				criteriaCount++;
			}
			
			if (criteria.getNetworkId() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				
				query.addCriteria("network_id", Criteria.EQ, criteria.getNetworkId());
				criteriaCount++;
			}

			if (criteria.getSsid() != null){
				if (criteriaCount > 0){
					query.addCondition(Condition.AND);
				}
				
				query.addCriteria("ssid", Criteria.EQ, criteria.getSsid());
				criteriaCount++;
			}
			
			if (criteria.getOrderBy() != null && !criteria.getOrderBy().isEmpty()){
				query.addOrderBy(criteria.getOrderBy());
			} else {
				query.addOrderBy("network_id asc, device_id asc, ssid_id asc");
			}
			return (List<ConfigurationSsids>) query.executeQueryAsObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		} // end try ... catch ... finally ...		
		
	} // end getConfigurationSsids
	
	public List<ConfigurationSsids> getAllObjectsWithTagNameList(List<Integer> TagNameLst) throws SQLException
	{
		if (log.isDebugEnabled()){
			log.debug("getting " + this.getClass().getSimpleName() + " instance with tag names: " + TagNameLst.toString());
		}
		
		if (TagNameLst==null || TagNameLst.size()==0)
			return null;
		
//		Query query = session.createQuery("Select c from ConfigurationSsids as c inner join c.tagses t where t.name in (:TagNameLst)");
//		query.setParameterList("TagNameLst", TagNameLst);
//		return (List<ConfigurationSsids>) query.list();
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			
			StringBuilder sb = new StringBuilder();
			sb.append("select c1.* from ");
			sb.append(query.getDBName());
			sb.append(".");
			sb.append("configuration_ssids c1 ");
			sb.append("inner join ");
			sb.append(query.getDBName());
			sb.append(".");
			sb.append("tagsxconfiguration_ssids t1 on c1.network_id = t1.network_id and c1.ssid_id = t1.ssid_id ");
			sb.append("inner join ");
			sb.append(query.getDBName());
			sb.append(".");
			sb.append("tags t2 on t1.tag_id=t2.id ");
			sb.append("where t2.id in ("+intListToParam(TagNameLst)+")");
			
			query.setQueryClass(persistentClass);
			List<ConfigurationSsids> results = query.executeQueryAsObject(sb.toString());
			if (log.isDebugEnabled()){
				log.debug("SQL="+sb.toString());
			}
			return results;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}		
	}

	public List<ConfigurationSsids> getAllObjectsWithTagName(String TagName) throws SQLException
	{
		if (log.isDebugEnabled()){
			log.debug("getting " + this.getClass().getSimpleName() + " instance with tag name: " + TagName);
		}
		if (TagName==null)
			return null;
		
//		Query query = session.createQuery("Select c from ConfigurationSsids as c inner join c.tagses t where t.name = :TagName");
//		query.setParameter("TagName", TagName);
//		return (List<ConfigurationSsids>) query.list();
		
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			
			StringBuilder sb = new StringBuilder();
			sb.append("select c1.* from ");
			sb.append(query.getDBName());
			sb.append(".");
			sb.append("configuration_ssids c1 ");
			sb.append("inner join ");
			sb.append(query.getDBName());
			sb.append(".");
			sb.append("tagsxconfiguration_ssids t1 on c1.network_id = t1.network_id and c1.ssid_id = t1.ssid_id ");
			sb.append("inner join ");
			sb.append(query.getDBName());
			sb.append(".");
			sb.append("tags t2 on t1.tag_id=t2.id ");
			sb.append("where t2.name = \'"+TagName+"\'");
			
			query.setQueryClass(persistentClass);
			List<ConfigurationSsids> results = query.executeQueryAsObject(sb.toString());
			return results;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public Boolean isWifiManageable(Integer netId, Integer devId) throws SQLException
	{
		if (log.isDebugEnabled()){
			log.debug("getting isManageable " + this.getClass().getSimpleName() + " instance with netId: " + netId);
		}
		if (netId==null)
		{
			log.warn("netId is null");
			return false;
		}
		
		DBConnection session = getSession();
		ResultSet rs = null;
		try {
			DBQuery query = session.createQuery();
			
			StringBuilder sql = new StringBuilder();
			sql.append("select count(*) as total from ");
			sql.append(query.getDBName());
			sql.append(".");
			sql.append("configuration_ssids where network_id = ");
			sql.append(netId);
			sql.append(" and device_id = ");
			sql.append(devId);
			sql.append(" and enabled = true");
						
			rs = query.executeQuery(sql.toString());
			if (rs.next())
			{
				Long total = rs.getLong("total"); 
				if (total!=null && total>0)
					return true;
			}

			return false;			
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
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
		
	}
	
	public int getMaxNetworkSsid(Integer netId, Integer devId) throws SQLException
	{
		if (log.isDebugEnabled()){
			log.debug("getting maxNetworkSsid " + this.getClass().getSimpleName() + " instance with netId: " + netId);
		}
		if (netId==null)
		{
			log.warn("netId is null");
			return 0;
		}
		
		DBConnection session = getSession();
		ResultSet rs = null;
		try {
			DBQuery query = session.createQuery();
			
			StringBuilder sql = new StringBuilder();
			sql.append("select count(*) as total from ");
			sql.append(query.getDBName());
			sql.append(".");
			sql.append("configuration_ssids where network_id = ");
			sql.append(netId);
			sql.append(" and device_id = ");
			sql.append(devId);
			sql.append(" and enabled = true");
						
			rs = query.executeQuery(sql.toString());
			int total = 0;
			if (rs.next())
			{
				 total = rs.getInt("total"); 
			}
			return total;			
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
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
		
	}
	
	public Integer deleteByNetworkId( Integer network_id ) throws SQLException
	{
		DBConnection session = getSession();
		String dbPrefix = Utils.PARAM_ORGANIZATION_DB_PREFIX;
		String dbname = dbPrefix + session.getOrgId();
		try
		{
			DBQuery query = session.createQuery();
			String sql = "delete from " + dbname + ".configuration_ssids where network_id = " + network_id;
			int result = query.executeUpdate(sql);
			return result;
		}
		catch( Exception e )
		{
			throw e;
		}
		finally
		{
			if( session != null )
				session.close();
		}
	}
	
	public Integer deleteByNetworkIdAndDeviceId( Integer network_id, Integer device_id ) throws SQLException
	{
		DBConnection session = getSession();
		String dbPrefix = Utils.PARAM_ORGANIZATION_DB_PREFIX;
		String dbname = dbPrefix + session.getOrgId();
		try
		{
			DBQuery query = session.createQuery();
			String sql = "delete from " + dbname + ".configuration_ssids where network_id = " + network_id + " and device_id = " + device_id;
			int result = query.executeUpdate(sql);
			return result;
		}
		catch( Exception e )
		{
			throw e;
		}
		finally
		{
			if( session != null )
				session.close();
		}
	}
	
	public ConfigurationSsids getCfgByNetworkIdAndDeviceId(Integer network_id, Integer device_id) throws SQLException
	{
		DBConnection session = getSession();
		ConfigurationSsids result = null;
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("network_id",Criteria.EQ,network_id);
			query.addCondition(Condition.AND);
			query.addCriteria("device_id",Criteria.EQ, device_id);
			result = (ConfigurationSsids)query.executeQueryAsSingleObject();
		}
		catch( Exception e )
		{
			throw e;
		}
		finally
		{
			if( session != null )
				session.close();
		}
		
		return result;
	}
	
	public Integer deleteByConfigurationSsidsId( Integer network_id, Integer device_id, String ssid_id ) throws SQLException
	{
		DBConnection session = getSession();
		String dbPrefix = Utils.PARAM_ORGANIZATION_DB_PREFIX;
		String dbname = dbPrefix + session.getOrgId();
		try
		{
			DBQuery query = session.createQuery();
			String sql = "delete from " + dbname + ".configuration_ssids where network_id = " + network_id + " and device_id = " + device_id + " and ssid_id not in (" + ssid_id + ")";
			if (log.isInfoEnabled()){
				log.info("deleteByConfigurationSsidsId : " + sql);
			}
			int result = query.executeUpdate(sql);
			return result;
		}
		catch( Exception e )
		{
			throw e;
		}
		finally
		{
			if( session != null )
				session.close();
		}
	}
	
}
