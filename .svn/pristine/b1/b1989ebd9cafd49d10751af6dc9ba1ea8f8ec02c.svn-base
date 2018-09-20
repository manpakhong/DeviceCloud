package com.littlecloud.control.dao;

import java.sql.SQLException;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.ConfigurationSettings;
import com.littlecloud.control.entity.ConfigurationSettingsId;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Condition;
import com.peplink.api.db.query.Criteria;

public class ConfigurationSettingsDAO extends BaseDAO<ConfigurationSettings, ConfigurationSettingsId> {

	private static final Logger log = Logger.getLogger(ConfigurationSettingsDAO.class);

	public ConfigurationSettingsDAO() throws SQLException {
		super(ConfigurationSettings.class);
	}

	public ConfigurationSettingsDAO(String orgId) throws SQLException {
		super(ConfigurationSettings.class, orgId);
	}

	public ConfigurationSettingsDAO(String orgId, boolean readonly) throws SQLException {
		super(ConfigurationSettings.class, orgId, readonly);
	}
	
	public ConfigurationSettings getConfigSettingsLstByNetworkIdNDeviceId(Integer netId, Integer devId) throws SQLException{
		log.infof("getConfigSettingsLstFromNetwork from network id: %s and device id: %s ", netId, devId);
		DBConnection session = null;
		try{
			session = getSession();
			DBQuery query = session.createQuery();
			query.setQueryClass(ConfigurationSettings.class);
			query.addCriteria("network_id", Criteria.EQ, netId);
			query.addCondition(Condition.AND);
			query.addCriteria("device_id", Criteria.EQ, devId);
			return (ConfigurationSettings) query.executeQueryAsSingleObject();
		} catch (SQLException e){
			throw e;
		} 
		finally{
			if(session != null){
				session.close();
				session = null;
			}
		}
	}
	
	public List<ConfigurationSettings> getConfigSettingsLstFromNetwork(Integer netId) throws SQLException
	{
		log.info("getConfigSettingsLstFromNetwork from network id " + netId);

		DBConnection session = null;
		DBQuery query = null;
		String dbName = null;
		StringBuilder sql = null;
		
		try
		{
			session = getSession();
			query = session.createQuery();
			query.setQueryClass(ConfigurationSettings.class);
			dbName = query.getDBName();
			
			sql = new StringBuilder();
			sql.append("select * from ");
			sql.append(dbName);
			sql.append(".configuration_settings ");
			sql.append("where network_id = "+netId);
			sql.append(" order by device_id asc");
			
			return (List<ConfigurationSettings>) query.executeQueryAsObject(sql.toString());
		} 
		catch (SQLException e)
		{
			throw e;
		} 
		finally
		{
			if(session != null)
			{
				session.close();
				session = null;
			}
		}
	}
}
