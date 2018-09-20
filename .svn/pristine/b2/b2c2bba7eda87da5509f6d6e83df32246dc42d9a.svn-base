package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.entity.ConfigurationPepvpns;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.pool.utils.Utils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

/**
 * Home object for domain model class Devices.
 * 
 * @see com.littlecloud.control.entity.Devices
 * @author Hibernate Tools
 */
public class ConfigurationPepvpnsDAO extends BaseDAO<ConfigurationPepvpns, Integer> {

	private static final Logger log = Logger.getLogger(ConfigurationPepvpnsDAO.class);

	public ConfigurationPepvpnsDAO() throws SQLException {
		super(ConfigurationPepvpns.class);
	}

	public ConfigurationPepvpnsDAO(String orgId) throws SQLException {
		super(ConfigurationPepvpns.class, orgId);
	}
	
	public ConfigurationPepvpnsDAO(String orgId, boolean readonly) throws SQLException {
		super(ConfigurationPepvpns.class, orgId, readonly);
	}
	
	/* get all network id of the device if it is a pepvpn hub or ha hub*/
	public List<Integer> getAllHubSupportedNetworkIds(Integer hubDeviceId) throws SQLException
	{
		log.info("getting all network id from hubDeviceId "+hubDeviceId);
//		
//		Query query = session.createQuery("select c.hubNetworkId from com.littlecloud.control.entity.ConfigurationPepvpns c where c.hubDeviceId = :hubDeviceId and c.enabled=true" +
//				" union " +
//				" select c.haHubNetworkId from com.littlecloud.control.entity.ConfigurationPepvpns c where c.haHubDeviceId = :hubDeviceId and c.haHubenabled=true");
//		query.setParameter("hubDeviceId", hubDeviceId);
//		return (List<Integer>)query.list();
		
		DBConnection session = getSession();
		ResultSet rs = null;
		try {		
			DBQuery query = session.createQuery();
			
			StringBuilder sql = new StringBuilder();
			sql.append("select hub_network_id as network_id from ");
			sql.append(query.getDBName());
			sql.append(".");
			sql.append("configuration_pepvpns ");
			sql.append("where hub_device_id = "+hubDeviceId+" and enabled = true ");
			sql.append("union ");
			sql.append("select ha_hub_network_id as network_id from ");
			sql.append(query.getDBName());
			sql.append(".");
			sql.append("configuration_pepvpns ");
			sql.append("where ha_hub_device_id = "+hubDeviceId+" and ha_hub_enabled = true");

			List<Integer> result = new ArrayList<Integer>();
			rs = query.executeQuery(sql.toString());
			
			sql = null;
			while(rs.next())
			{
				result.add(rs.getInt("network_id"));
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
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
	}
	
	/* get all network id supported by the pepvpn primary hub */
	public List<Integer> getMasterHubSupportedNetworkIds(Integer hubDeviceId) throws SQLException
	{
		log.info("getting all network id from hubDeviceId "+hubDeviceId);
		
//		Query query = session.createQuery("select c.networkId from com.littlecloud.control.entity.ConfigurationPepvpns c where c.hubDeviceId = :hubDeviceId and c.enabled=true");
//		query.setParameter("hubDeviceId", hubDeviceId);
//		return (List<Integer>)query.list();
		
		DBConnection session = getSession();
		ResultSet rs = null;
		try {		
			DBQuery query = session.createQuery();
			
			StringBuilder sql = new StringBuilder();
			sql.append("select network_id from ");
			sql.append(query.getDBName());
			sql.append(".");
			sql.append("configuration_pepvpns ");
			sql.append("where hub_device_id = "+hubDeviceId+" and enabled = true ");

			List<Integer> result = new ArrayList<Integer>();
			rs = query.executeQuery(sql.toString());
			
			sql = null;
			while(rs.next())
			{
				result.add(rs.getInt("network_id"));
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
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
	}
	
	/* get all network id supported by the pepvpn ha hub */
	public List<Integer> getHaHubNetworkIds(Integer hahubDeviceId) throws SQLException
	{
		log.info("getting all network id from hubDeviceId "+hahubDeviceId);
		
//		Query query = session.createQuery("select c.networkId from com.littlecloud.control.entity.ConfigurationPepvpns c where c.haHubDeviceId = :hahubDeviceId and c.enabled=true");
//		query.setParameter("hahubDeviceId", hahubDeviceId);
//		return (List<Integer>)query.list();
		
		DBConnection session = getSession();
		ResultSet rs = null;
		try {		
			DBQuery query = session.createQuery();
			
			StringBuilder sql = new StringBuilder();
			sql.append("select network_id from ");
			sql.append(query.getDBName());
			sql.append(".");
			sql.append("configuration_pepvpns ");
			sql.append("where ha_hub_device_id = "+hahubDeviceId+" and enabled=true and ha_hub_enabled = true");

			List<Integer> result = new ArrayList<Integer>();
			rs = query.executeQuery(sql.toString());
			
			sql = null;
			while(rs.next())
			{
				result.add(rs.getInt("network_id"));
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
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
	}

	/* get all network id supported by the pepvpn hub */
	public boolean isEnabledMasterHubInAnyNetwork(Integer hubDeviceId) throws SQLException
	{
		List<Integer> hubLst = getMasterHubSupportedNetworkIds(hubDeviceId);
		if (hubLst.size()!=0)
			return true;
		else
			return false;
	}
	
	/* get all network id supported by the pepvpn hub */
	public boolean isEnabledHaHubInAnyNetwork(Integer hahubDeviceId) throws SQLException
	{
		List<Integer> hubLst = getHaHubNetworkIds(hahubDeviceId);
		if (hubLst.size()!=0)
			return true;
		else
			return false;
	}
	
	public boolean isEnabledHubOrHaHubInAnyNetwork(Integer hubDeviceId) throws SQLException
	{
		boolean result = false;
		
		List<Integer> hubLst = getMasterHubSupportedNetworkIds(hubDeviceId);
		if (hubLst.size()!=0)
			result = true;
		
		hubLst = getHaHubNetworkIds(hubDeviceId);
		if (hubLst.size()!=0)
			result = true;
		
		return result;
	}
	
	public List<Integer> getActiveHubListFromDevIdLstInAnyNetwork( List<Integer> devIds ) throws SQLException
	{
		List<Integer> result = new ArrayList<Integer>();
		DBConnection session = getSession();
		ResultSet rs = null;
		try 
		{
			DBQuery query = session.createQuery();
			
			for( Integer devId : devIds )
			{
				String sql = "select enabled,hub_device_id,ha_hub_enabled,ha_hub_device_id from " + query.getDBName() + ".configuration_pepvpns where hub_device_id = " + devId;
				log.info("Delete Groups SQL : " + sql);
				rs = query.executeQuery(sql);
				while( rs.next() )
				{
					if(rs.getBoolean("enabled") == true || rs.getBoolean("ha_hub_enabled") == true)
					{
//						rs.getInt("enabled") == 1 && rs.getInt("ha_hub_enabled") == 1
						Object hub_dev_id = rs.getObject("hub_device_id");
						Object hahub_dev_id = rs.getObject("ha_hub_device_id");
						if( hub_dev_id != null )
							result.add(Integer.valueOf(""+hub_dev_id));
						if(hahub_dev_id != null)
							result.add(Integer.valueOf(""+hahub_dev_id));
//						else
//							result.add(Integer.valueOf(0));
					}
				}
			}
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
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
		return result;
	}
	
	public Integer deleteByNetworkId( Integer network_id ) throws SQLException
	{
		DBConnection session = getSession();
		String dbPrefix = Utils.PARAM_ORGANIZATION_DB_PREFIX;
		String dbname = dbPrefix + session.getOrgId();
		try
		{
			DBQuery query = session.createQuery();
			String sql = "delete from " + dbname + ".configuration_pepvpns where network_id = " + network_id;
			int result = query.executeUpdate(sql);
			return result;
		}
		catch (SQLException e)
		{
			throw e;
		} 
		finally
		{
			if (session!=null) 
				session.close();
		}
	}
	
	public static void main( String[] args )
	{
		System.out.println(""+Integer.valueOf(null));
	}
	
	public List<Integer> getHubandHahubDeviceIdFromNetworkId(Integer networkId, boolean active) throws SQLException
	{
		List<Integer> result = new ArrayList<Integer>();
		DBConnection session = getSession();
		ResultSet rs = null;
		
		try 
		{
			DBQuery query = session.createQuery();

			String sql = "select hub_device_id,ha_hub_device_id,enabled,ha_hub_enabled from " + query.getDBName() + ".configuration_pepvpns "
					+ "where network_id = " + networkId;
			log.info("select hubid SQL : " + sql);
			rs = query.executeQuery(sql);
			while( rs.next() )
			{

//				rs.getInt("enabled") == 1 && rs.getInt("ha_hub_enabled") == 1
				Object hub_dev_id = rs.getObject("hub_device_id");				
				Object hahub_dev_id = rs.getObject("ha_hub_device_id");
				Boolean hub_enabled = rs.getBoolean("enabled");
				Boolean ha_hub_enabled = rs.getBoolean("ha_hub_enabled");
				if( hub_dev_id != null && (!active || hub_enabled))
					result.add(Integer.valueOf(""+hub_dev_id));
				if(hahub_dev_id != null && (!active || ha_hub_enabled))
					result.add(Integer.valueOf(""+hahub_dev_id));
//				else
//					result.add(Integer.valueOf(0));
			
			}
		
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
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
		return result;
	}
}
