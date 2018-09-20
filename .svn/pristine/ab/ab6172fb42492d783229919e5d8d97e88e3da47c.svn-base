package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.SQLException;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.ConfigurationRadios;
import com.littlecloud.control.entity.ConfigurationRadiosId;
import com.littlecloud.pool.utils.Utils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

/**
 * Home object for domain model class ConfigurationRadios.
 * 
 * @see com.littlecloud.control.entity.ConfigurationRadios
 * @author Hibernate Tools
 */
public class ConfigurationRadiosDAO extends BaseDAO<ConfigurationRadios, ConfigurationRadiosId> {

	private static final Logger log = Logger.getLogger(ConfigurationRadiosDAO.class);

	public ConfigurationRadiosDAO() throws SQLException {
		super(ConfigurationRadios.class);
	}

	public ConfigurationRadiosDAO(String orgId) throws SQLException {
		super(ConfigurationRadios.class, orgId);
	}
	
	public ConfigurationRadiosDAO(String orgId, boolean readonly) throws SQLException {
		super(ConfigurationRadios.class, orgId, readonly);
	}
	
	public Integer deleteByNetworkId( Integer network_id ) throws SQLException
	{
		DBConnection session = getSession();
		String dbPrefix = Utils.PARAM_ORGANIZATION_DB_PREFIX;
		String dbname = dbPrefix + session.getOrgId();
		try
		{
			DBQuery query = session.createQuery();
			String sql = "delete from " + dbname + ".configuration_radios where network_id = " + network_id;
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
