package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.SQLException;
import org.jboss.logging.Logger;

import com.littlecloud.control.entity.NetworkSilencePeriods;
import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.pool.utils.Utils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

/**
 * Home object for domain model class Networks.
 * 
 * @see com.littlecloud.control.entity.Networks
 * @author Hibernate Tools
 */
public class NetworkSliencePeriodsDAO extends BaseDAO<NetworkSilencePeriods, Integer> {

	private static final Logger log = Logger.getLogger(NetworkSliencePeriodsDAO.class);

	public NetworkSliencePeriodsDAO() throws SQLException {
		super(NetworkSilencePeriods.class);
	}

	public NetworkSliencePeriodsDAO(String orgId) throws SQLException {
		super(NetworkSilencePeriods.class, orgId);
	}
	
	public NetworkSliencePeriodsDAO(String orgId, boolean readonly) throws SQLException {
		super(NetworkSilencePeriods.class, orgId, readonly);
	}
	
	public Integer deleteByNetworkId( Integer network_id ) throws SQLException
	{
		DBConnection session = getSession();
		String dbPrefix = Utils.PARAM_ORGANIZATION_DB_PREFIX;
		String dbname = dbPrefix + session.getOrgId();
		try
		{
			DBQuery query = session.createQuery();
			String sql = "delete from " + dbname + ".network_silence_periods where network_id = " + network_id;
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
