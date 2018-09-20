package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.json.model.config.util.RadioConfigUtils;
import com.littlecloud.control.json.model.pepvpn.Peer_Networks;
import com.littlecloud.pool.object.DevicesObject;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

/**
 * Home object for domain model class Devices.
 * 
 * @see com.littlecloud.control.entity.Devices
 * @author Hibernate Tools
 */
public class DevicesDAO extends BaseDAO<Devices, Integer> {

	private static final Logger log = Logger.getLogger(DevicesDAO.class);

//	public DevicesDAO() {
	public DevicesDAO() throws SQLException {
		super(Devices.class);
	}

//	public DevicesDAO(String orgId) {
	public DevicesDAO(String orgId) throws SQLException {
		super(Devices.class, orgId);
	}

//	public DevicesDAO(String orgId, boolean readonly) {
	public DevicesDAO(String orgId, boolean readonly) throws SQLException {
		super(Devices.class, orgId, readonly);
	}

	public List<Peer_Networks> getPeerNetworkList(List<Integer> hubNetIdList, List<Integer> hubDevIdList) throws SQLException
	{
		log.info("getting endpointz from network id " + hubNetIdList);

		// Hibernate
//		// Query query =
//		// session.createQuery("select count(*) as endpointz from Devices d where d.isHub is false and d.networkId=:networkId");
//		Query query = session.createQuery("select new com.littlecloud.control.json.model.pepvpn.Peer_Networks(d.networks.id, d.networks.name, count(d.id)) from Devices d inner join d.networks where d.active = true and d.id not in (:hubDevIdList) and d.networks.id in (:hubNetIdList) group by d.networks.id, d.networks.name");
//		query.setParameterList("hubNetIdList", hubNetIdList);
//		query.setParameterList("hubDevIdList", hubDevIdList);
//		return (List<Peer_Networks>) query.list();
		
		// JDBC
		DBConnection session = getSession();
		ResultSet rs = null;
		try
		{
			DBQuery query = session.createQuery();
//			query.setQueryClass(Peer_Networks.class);
			
//			String strNetworkList = "", strDeviceList = "";
//			
//			Iterator<Integer> iter = hubNetIdList.iterator();
//			while (iter.hasNext())
//			{
//				int intNetId = (Integer) iter.next();
//				if (!strNetworkList.equals(""))
//				{
//					strNetworkList += ", ";
//				}
//				strNetworkList += Integer.toString(intNetId);
//			}	
//		
//			iter = hubDevIdList.iterator();
//			while (iter.hasNext())
//			{
//				int intDevId = (Integer) iter.next();
//				if (!strDeviceList.equals(""))
//				{
//					strDeviceList += ", ";
//				}
//				strDeviceList += Integer.toString(intDevId);
//			}
			
			String strDataBase = query.getDBName();
//			String strSQL = "select d.network_id, n_2.name, count(d.id)"
//					+ "from "+ strDataBase + ".devices d "
//					+ "inner join " + strDataBase + ".networks n_1 "
//					+ "on d.network_id = n_1.id, " + strDataBase +".networks n_2 "
//					+ "where d.network_id = n_2.id and d.active = 1";
//			
//			if (!strDeviceList.equals(""))
//			{
//				strSQL += " and ( d.id not in  ( " + strDeviceList + " ) )";
//			}
//			if (!strNetworkList.equals(""))
//			{
//				strSQL += " and ( d.network_id in ( " + strNetworkList + " ) )";
//			}
//			strSQL += " group by d.network_id , n_2.name";
			
			StringBuilder sql = new StringBuilder();
			sql.append("select d.network_id, n.name, count(d.id) from ");
			sql.append(strDataBase);
			sql.append(".devices d inner join ");
			sql.append(strDataBase);
			sql.append(".networks n on d.network_id = n.id where d.active = true ");
			sql.append("and n.active = 1");
			sql.append(" and d.id not in  ( " + intListToParam(hubDevIdList) + " ) ");
			sql.append(" and d.network_id in  ( " + intListToParam(hubNetIdList) + " ) ");
			sql.append("group by d.network_id, n.name");
			
			//log.debugf("sql=%s",sql);
			
			List<Peer_Networks> returnList = new ArrayList<Peer_Networks>();
			
			rs = query.executeQuery(sql.toString());
			while (rs.next())
			{
				Peer_Networks p_net = new Peer_Networks(rs.getInt(1), rs.getString(2), rs.getLong(3));
				returnList.add(p_net);
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

	/* get device configured as hub or not */
	// public List<Devices> getDevicesHubList(Integer networkId, boolean ishub)
	// {
	// log.info("getting device hub configured list from network id " + networkId + " ishub=" +ishub);
	//
	// try {
	// Criteria cri = session.createCriteria(this.persistentClass);
	//
	// if (networkId!=null)
	// cri.add(Restrictions.eq("networks.id", networkId));
	//
	// cri.add(Restrictions.eq("ishub", ishub));
	// cri.add(Restrictions.eq("active", true));
	// List<Devices> results = cri.list();
	//
	// log.debug("query successful, result size: " + results.size());
	// return results;
	// } catch (RuntimeException re) {
	// log.error("query failed", re);
	// throw re;
	// }
	// }
	
	public List<Devices> getDevicesHubListFromHubNetwork(Integer networkId, boolean ishub) throws SQLException
	{
		log.info("getting device hub configured list from network id " + networkId);

		List<Devices> result = new ArrayList<Devices>();

		class queryOut {
			Boolean enabled;
			Integer hubNetworkId;
			Integer hubDeviceId;

			Boolean haEnabled;
			Integer hahubNetworkId;
			Integer hahubDeviceId;

			@Override
			public String toString() {
				return ToStringBuilder.reflectionToString(this);
			}
		}

//		System.out.println("*****************\ngetDevicesHubListFromHubNetwork");
		
		try {
			// Hibernate
//			//Query query = session.createQuery("select c.enabled, c.hubNetworkId, c.hubDeviceId, c.haHubEnabled, c.haHubNetworkId, c.haHubDeviceId From ConfigurationPepvpns c where c.networkId = :networkId");
//			Query query = session.createQuery("select c.enabled, c.hubNetworkId, c.hubDeviceId, c.haHubEnabled, c.haHubNetworkId, c.haHubDeviceId " +
//					" From ConfigurationPepvpns c where c.hubNetworkId = :networkId or c.haHubNetworkId = :networkId");
//			query.setParameter("networkId", networkId);
//			List<Object[]> colList = (List<Object[]>) query.list();

			// JDBC
			DBConnection session = getSession();
			ResultSet rs = null;
			try
			{
				DBQuery query = session.createQuery();
				
				String strDataBase = query.getDBName();
				String strSQL = "select c.enabled, c.hub_network_id, c.hub_device_id, c.ha_hub_enabled, c.ha_hub_network_id, c.ha_hub_device_id From " + strDataBase + ".configuration_pepvpns c "
						+ "where c.hub_network_id = " + Integer.toString(networkId) + " or c.ha_hub_network_id = " + Integer.toString(networkId);	
//				System.out.println(strSQL);
				
				List<Object[]> colList = new ArrayList<Object[]>();
				
				rs = query.executeQuery(strSQL);
				while(rs.next())
				{
					Object[] arrObject = {rs.getBoolean(1), rs.getInt(2), rs.getInt(3), rs.getBoolean(4), rs.getInt(5), rs.getInt(6)};
					colList.add(arrObject);
				}
				rs.close();
				
				if (colList==null || colList.size()==0)
					return null;
				
				queryOut qout = new queryOut();
				for (Object[] cols:colList)
				{
					int i = 0;
					qout.enabled = (Boolean) cols[i++];
					qout.hubNetworkId = (Integer) cols[i++];
					qout.hubDeviceId = (Integer) cols[i++];
					qout.haEnabled = (Boolean) cols[i++];
					qout.hahubNetworkId = (Integer) cols[i++];
					qout.hahubDeviceId = (Integer) cols[i++];
					
					if (qout.enabled)
					{
						Devices dev = findById(qout.hubDeviceId);
						if (dev != null && dev.isActive() && dev.getNetworkId() == qout.hubNetworkId)
							result.add(dev);
	
						if (qout.haEnabled)
						{
							dev = findById(qout.hahubDeviceId);
							if (dev != null && dev.isActive() && dev.getNetworkId() == qout.hahubNetworkId)
								result.add(dev);
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
				if(session != null)
				{
					session.close();
					session = null;
				}
			}
			
		} catch (RuntimeException re) {
			log.error("query failed", re);
		}
		
		if (ishub)
		{
			return result;
		}
		else
		{
			List<Integer> devIdLst = new ArrayList<Integer>();
			for (Devices dev:result)
				devIdLst.add(dev.getId());
			log.debugf("devIdLst=%s", devIdLst);
			
			if (devIdLst.size()!=0)
			{
				
				// Hibernate
//				Query query = session.createQuery("select d From Devices d where d.networks.id = :networkId and d.id not in (:devIdLst)");
//				query.setParameter("networkId", networkId);
//				query.setParameterList("devIdLst", devIdLst);
//				result = (List<Devices>) query.list();
				
				// JDBC
				DBConnection session = getSession();
				try
				{
					DBQuery query = session.createQuery();
					query.setQueryClass(Devices.class);
					
					String strDeviceList = "";
					Iterator<Integer> iter = devIdLst.iterator();
					while (iter.hasNext())
					{
						int intDeviceId = (Integer) iter.next();
						if (!strDeviceList.equals(""))
						{
							strDeviceList += ", ";
						}
						strDeviceList += Integer.toString(intDeviceId);
					}
					
					String strSQL = "select d.* From " + query.getDBName() + ".devices d"
					+" where d.network_id = " + Integer.toString(networkId) + " and d.id not in (" + strDeviceList + ") and d.active=1";
					result = (List<Devices>) query.executeQueryAsObject(strSQL);
				} catch (SQLException e)
				{
					throw e;
				} finally
				{
					if (session!=null) session.close();	
				}
			}
		}
		return result;
	}
	
	public List<Devices> getDevicesHubListFromPepvpnConfiguredNetwork(Integer networkId, boolean ishub) throws SQLException
	{
		log.info("getting device hub configured list from network id " + networkId);

		List<Devices> result = new ArrayList<Devices>();

		class queryOut {
			Boolean enabled;
			Integer hubNetworkId;
			Integer hubDeviceId;

			Boolean haEnabled;
			Integer hahubNetworkId;
			Integer hahubDeviceId;

			@Override
			public String toString() {
				return ToStringBuilder.reflectionToString(this);
			}
		}

		try {
			// Hibernate
//			Query query = session.createQuery("select c.enabled, c.hubNetworkId, c.hubDeviceId, c.haHubEnabled, c.haHubNetworkId, c.haHubDeviceId From ConfigurationPepvpns c where c.networkId = :networkId");
//			query.setParameter("networkId", networkId);
//
//			int i = 0;
//			List<Object[]> colList = (List<Object[]>) query.list();
			
			// JDBC
			DBConnection session = getSession();
			ResultSet rs = null;
			try
			{
				DBQuery query = session.createQuery();
				
				String strDataBase = query.getDBName();
				String strSQL = "select c.enabled, c.hub_network_id, c.hub_device_id, c.ha_hub_enabled, c.ha_hub_network_id, c.ha_hub_device_id From " + strDataBase + ".configuration_pepvpns c where c.network_id = " + Integer.toString(networkId);
				log.debugf("strSQL=%s",strSQL);
				
				List<Object[]> colList = new ArrayList<Object[]>();
				
				rs = query.executeQuery(strSQL);
				while(rs.next())
				{
					Object[] arrObject = {rs.getBoolean(1), rs.getInt(2), rs.getInt(3), rs.getBoolean(4), rs.getInt(5), rs.getInt(6)};
					colList.add(arrObject);
				}
				rs.close();
				
				if (colList==null || colList.size()==0)
					return null;
				
				queryOut qout = new queryOut();
				for (Object[] cols:colList)
				{
					int i = 0;
					qout.enabled = (Boolean) cols[i++];
					qout.hubNetworkId = (Integer) cols[i++];
					qout.hubDeviceId = (Integer) cols[i++];
					qout.haEnabled = (Boolean) cols[i++];
					qout.hahubNetworkId = (Integer) cols[i++];
					qout.hahubDeviceId = (Integer) cols[i++];
					
					log.debug("qout.enabled="+qout.enabled+";quot.haEnabled="+qout.haEnabled);
					
					if (qout.enabled)
					{
						Devices dev = findById(qout.hubDeviceId);
						if (dev != null && dev.isActive() && dev.getNetworkId() == qout.hubNetworkId)
						{
							result.add(dev);
							log.debug("hub "+dev.getSn()+" is added");
						}
	
						if (qout.haEnabled)
						{
							dev = findById(qout.hahubDeviceId);
							if (dev != null && dev.isActive() && dev.getNetworkId() == qout.hahubNetworkId)
							{
								log.debug("ha hub "+dev.getSn()+" is added");
								result.add(dev);
							}
						}
					}
				}
			} catch (SQLException e)
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
			
		} catch (RuntimeException re) {
			log.error("query failed", re);
		}

		if (ishub)
		{
			return result;
		}
		else
		{
			List<Integer> devIdLst = new ArrayList<Integer>();
			for (Devices dev:result)
				devIdLst.add(dev.getId());
			log.debugf("devIdLst=%s", devIdLst);
			
			if (devIdLst.size()!=0)
			{
				// Hibernate
//				Query query = session.createQuery("select d From Devices d where d.networks.id = :networkId and d.id not in (:devIdLst)");
//				query.setParameter("networkId", networkId);
//				query.setParameterList("devIdLst", devIdLst);
//				result = (List<Devices>) query.list();
				
				// JDBC
				DBConnection session = getSession();
				try
				{
					DBQuery query = session.createQuery();
					query.setQueryClass(Devices.class);
					
					String strDeviceList = "";
					Iterator<Integer> iter = devIdLst.iterator();
					while (iter.hasNext())
					{
						int intDeviceId = (Integer) iter.next();
						if (!strDeviceList.equals(""))
						{
							strDeviceList += ", ";
						}
						strDeviceList = Integer.toString(intDeviceId);
	
					}
					
					String strSQL = "select * From " + query.getDBName() + ".devices d" 
					+ " where d.network_id = " + Integer.toString(networkId) 
					+ " and d.id not in (" + strDeviceList + ") and d.active=1";
					result = (List<Devices>) query.executeQueryAsObject(strSQL);
				} catch (SQLException e)
				{
					throw e;
				} finally
				{
					if (session!=null) session.close();
				}
			}
		}

		return result;
	}
	
	public List<Devices> getDevicesHubSupportList(Integer networkId, List<Integer> productIdLst, boolean bhubsupport) throws SQLException
	{
		log.info("getting device hub feature list from network id " + networkId + " where hub_support product id list=" + productIdLst + " and bhubsupport = "+bhubsupport);
	
		String whereClause = "";
		
		if (bhubsupport)
			whereClause = "product_id in (" + intListToParam(productIdLst) + ")";
		else
			whereClause = "product_id not in (" + intListToParam(productIdLst) + ")";
		
		if (networkId != null)
			whereClause += " and network_id =" + networkId;
		
		whereClause += " and active = true";
		
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Devices.class);
			
			String strSQL = "select * from " + query.getDBName() + ".devices where " + whereClause;
			
			List<Devices> results = (List<Devices>) query.executeQueryAsObject(strSQL);
			
			log.debug("query successful, result size: " + results.size());
			
			return results;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}		
	}	
	

//	@Deprecated
//	/* get device configured as hub or not */
//	public List<Devices> getDevicesHubFeatureList(Integer networkId, boolean ishubfeature) throws SQLException
//	{
//		log.info("getting device hub feature list from network id " + networkId + " ishub=" + ishubfeature);
//		
//		String whereClause = "";
//
//		// Hibernate
////		if (ishubfeature)
////			whereClause = "where d.deviceFeatures.mvpnLicense>0";
////		else
////			whereClause = "where d.deviceFeatures.mvpnLicense=-1";
////	
////		if (networkId != null)
////			whereClause += " and d.networks.id =" + networkId;
////	
////		whereClause += " and d.active = true";
//		
//		// JDBC
//		if (ishubfeature)
//			whereClause = "and d_f.mvpn_license > 0";
//		else
//			whereClause = "and d_f.mvpn_license = -1";
//
//		if (networkId != null)
//			whereClause += " and d.network_id =" + networkId;
//
//		whereClause += " and d.active = true";
//
//		try {
//			// Criteria cri = session.createCriteria(this.persistentClass);
//			// cri.add(Restrictions.eq("networks.id", networkId));
//			// cri.add(Restrictions.gt("deviceFeatures.mvpnLicense", 0));
//			// List<Devices> results = cri.list();
//
//			// Hibernate
////			Query query = session.createQuery("Select d From Devices d " + whereClause);
////			List<Devices> results = (List<Devices>) query.list();
//
//			// JDBC
//			DBConnection session = getSession();
//			try
//			{
//				DBQuery query = session.createQuery();
//				query.setQueryClass(Devices.class);
//				
//				String strSQL = "select * from " + query.getDBName() + ".devices d cross join " + query.getDBName() + ".device_features d_f where d.id = d_f.device_id " + whereClause;
//				
//				List<Devices> results = (List<Devices>) query.executeQueryAsObject(strSQL);
//				
//				log.debug("query successful, result size: " + results.size());
//				
//				return results;
//			} catch (SQLException e)
//			{
//				throw e;
//			} finally
//			{
//				if (session!=null) session.close();
//			}
//			
//		} catch (RuntimeException re) {
//			log.error("query failed", re);
//			throw re;
//		}
//	}

	public List<Integer> getAllImmediateUpdatingDevicesIdPerNetwork(Integer netId, Integer limit) throws SQLException {
		log.debugf("getting all updating device for network %d", netId);
		
		DBConnection session = getSession();
		List<Integer> result = new ArrayList<Integer>();
		List<Devices> devLst = null;
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Devices.class);
			
//			String strSQL = "select d.* from " + query.getDBName() + ".devices d cross join " + query.getDBName() + ".device_updates du "
//					+ "where d.id=du.device_id and du.conf_update = 0 and du.retry < "+ConfigUtils.MAX_RETRY_CFG_APPLY
//					+ " and (du.lastput is not null and du.lastapply is not null and du.lastput > du.lastapply)"
//					+ " and ( (du.firstput is null and (TO_SECONDS(now()) - TO_SECONDS(firstsave))/60 < 1) )"
//					+ " and d.network_id = " + Integer.toString(netId) + " and d.first_appear is not null and d.active = 1 "
//					+ "order by du.timestamp desc";
			
			String strSQL = "select d.* from " + query.getDBName() + ".devices d cross join " + query.getDBName() + ".device_updates du "
					+ "where d.id=du.device_id "
					+ " and du.firstput is null and du.lastput is null and du.lastapply is null"
					+ " and (TO_SECONDS(now()) - TO_SECONDS(firstsave))/60 < 1"
					+ " and d.network_id = " + Integer.toString(netId) + " and d.first_appear is not null and d.active = 1 "
					+ "order by du.timestamp desc";
			
			if (limit != null)
			{
				strSQL += " limit " + Integer.toString(limit);
			}
			log.debugf("strSQL (getAllImmediateUpdatingDevicesPerNetwork)=%s",strSQL);
			
			devLst = (List<Devices>) query.executeQueryAsObject(strSQL);			
			for (Devices dev:devLst)
			{
				result.add(dev.getId());	
			}					
			return result;			
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}		
	}
	
	public List<Devices> getAllRetryUpdateDevicesPerNetwork(Integer netId, Integer limit) throws SQLException {
		log.debugf("getting all device updates for network %d", netId);
		
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Devices.class);
			
			String strSQL = "select d.* from " + query.getDBName() + ".devices d cross join " + query.getDBName() + ".device_updates du "
					+ "where d.id=du.device_id and du.conf_update > 0 and du.retry < "+RadioConfigUtils.MAX_RETRY_CFG_APPLY
					+ " and ( (du.firstput is not null) or (du.firstput is null and (TO_SECONDS(now()) - TO_SECONDS(firstsave))/60 > 1) )"
					+ " and d.network_id = " + Integer.toString(netId) + " and d.first_appear is not null and d.active = 1 "
					+ "order by du.timestamp desc";
			
			if (limit != null)
			{
				strSQL += " limit " + Integer.toString(limit);
			}
			log.debugf("strSQL (getAllRetryUpdateDevicesPerNetwork)=%s",strSQL);
			
			return (List<Devices>) query.executeQueryAsObject(strSQL);
			
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}			
	}

	// No reference
//	public List<Devices> getUpdateDevices(List<Integer> devIdLst) {
//		log.debugf("getting device updates for devIdLst %s", devIdLst);
//		Query query = session.createQuery("Select d From Devices d where d.deviceUpdates.confUpdate>0 and d.deviceUpdates.retry<=5 and d.id in (:devIdLst) and d.active = true order by d.deviceUpdates.timestamp desc");
//		query.setParameterList("devIdLst", devIdLst);
//		return (List<Devices>) query.list();
//	}

	
	// getDevicesListByNetworkId = alias of getDevicesList(Integer networkId), if possible rename getDevicesList to getDevicesListByNetworkId avoid messing up
	public List<Devices> getDevicesListByNetworkId(Integer networkId) throws SQLException{
		return getDevicesList(networkId);
	}
	public List<Devices> getDevicesList(Integer networkId) throws SQLException
	{
		log.info("getting device list from network id " + networkId);
		
		// Hibernate
//		Query query = session.createQuery("Select d From Devices d where d.networks.id = :networkId and d.active = true");
//		query.setParameter("networkId", networkId);
//		return (List<Devices>) query.list();
		
		// JDBC
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Devices.class);
			
			String strSQL = "select * from " + query.getDBName() + ".devices d where d.network_id = " + Integer.toString(networkId) + " and d.active = true";
			
			List<Devices> results = (List<Devices>) query.executeQueryAsObject(strSQL);
			
			log.debug("query successful, result size: " + results.size());
			return results;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public List<Devices> getInactiveDevicesList(Integer networkId) throws SQLException
	{
		log.info("getting device list from network id " + networkId);

		// JDBC
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Devices.class);
			
			String strSQL = "select * from " + query.getDBName() + ".devices d where d.network_id = " + Integer.toString(networkId) + " and d.active = false";
			
			List<Devices> results = (List<Devices>) query.executeQueryAsObject(strSQL);
			
			log.debug("query successful, result size: " + results.size());
			return results;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	/* 1. get removeddevices / devices with no group
	 * 2. verify their sn has no active entries
	 * 3. 
	 */
	
	public List<Devices> getRemovedDevicesSnLstForClearConfig() throws SQLException 
	{		
		log.info("getting device with no group");
		
		/* select distinct d.sn from devices d inner join device_updates df 
		on d.id = df.device_id
		where df.conf_update > 0 and d.active = false 
		and d.sn not in (select sn from devices where active = true); */		
		
		List<Devices> result = new ArrayList<Devices>();
		
		DBConnection session = getSession();
		ResultSet rs = null;
		try
		{
			DBQuery query = session.createQuery();
			
			String dbName = query.getDBName();
			String strSQL = "select distinct max(d.id), d.iana_id, d.sn, d.product_id from " + dbName + ".devices d inner join " + dbName + ".device_updates df " + 
					"on d.id = df.device_id " + 
					"where df.conf_update > 0 and df.retry < "+RadioConfigUtils.MAX_RETRY_CFG_APPLY+" and d.active = false and d.first_appear is not null and" + 
					//"		and d.sn not in (select sn from " + dbName + ".devices where active = true)";  convert(sn using utf8)
					" d.sn not in (select convert(sn using utf8) from littlecloud_branch_production.sns_organizations where organization_id is not null)" +
					" and d.sn not in (select d2.sn from "+ dbName + ".devices d2 where active = true)" +
					" group by iana_id, sn";
			log.debugf("getRemovedDevicesSnLstForClearConfig sql=%s",strSQL);
			
			rs = query.executeQuery(strSQL);
			while(rs.next())
			{
				Devices dev = new Devices();
				dev.setId(rs.getInt(1));
				dev.setIanaId(rs.getInt(2));
				dev.setSn(rs.getString(3));
				dev.setProductId(rs.getInt(4));
				//dev.setFirstAppear(rs.getDate(3));
				result.add(dev);
			}
			rs.close();
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
		return result;
	}
		
	public List<Devices> getPrioritizedEndptDevicesList(Integer networkId, int limit) throws SQLException
	{
		log.infof("getting prioritized device list from network id %d with limit %d", networkId, limit);
		
		// Hibernate
//		Query query = session.createQuery("Select d From Devices d where d.networks.id = :networkId and d.active = true");
//		query.setParameter("networkId", networkId);
//		return (List<Devices>) query.list();
		
		// JDBC
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Devices.class);
			
			StringBuilder sql = new StringBuilder();
			sql.append("select * from ");
			sql.append(query.getDBName());
			sql.append(".devices d where d.network_id = ");
			sql.append(Integer.toString(networkId));
			sql.append(" and d.active = true ");
			sql.append(" and d.id not in (");
			sql.append("select hub_device_id from "+query.getDBName()+".configuration_pepvpns where enabled = 1"); 
			sql.append(" union "); 
			sql.append("select ha_hub_device_id from "+query.getDBName()+".configuration_pepvpns where enabled = 1 and ha_hub_enabled = 1)");
			sql.append("order by last_online asc");	/* currrently random order */	
			if (limit != 0)
			{
				sql.append(" limit ");
				sql.append(limit);
			}	
			log.debugf("getPrioritizedDevicesList sql = %s", sql);
			List<Devices> results = (List<Devices>) query.executeQueryAsObject(sql.toString());
			
			log.debug("query successful, result size: " + results.size());
			return results;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public List<Devices> getEverOnlineSpeedFusionEndptDevicesList(Integer networkId, int limit) throws SQLException
	{
		log.infof("getting ever online device list from network id %d with limit %d", networkId, limit);
		
		// Hibernate
//		Query query = session.createQuery("Select d From Devices d where d.networks.id = :networkId and d.active = true");
//		query.setParameter("networkId", networkId);
//		return (List<Devices>) query.list();
		
		// JDBC
		DBConnection session = getSession();		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Devices.class);
			
			StringBuilder sql = new StringBuilder();
			sql.append("select * from ");
			sql.append(query.getDBName());
			sql.append(".devices d where d.network_id = ");
			sql.append(Integer.toString(networkId));
			sql.append(" and d.active = true and d.first_appear is not null");
			sql.append(" and d.id not in (");
			sql.append("select hub_device_id from "+query.getDBName()+".configuration_pepvpns where enabled = 1"); 
			sql.append(" union "); 
			sql.append("select ha_hub_device_id from "+query.getDBName()+".configuration_pepvpns where enabled = 1 and ha_hub_enabled = 1)");
			if (limit != 0)
			{
				sql.append(" limit ");
				sql.append(limit);
			}		
			log.debugf("getEverOnlineSpeedFusionDevicesList sql = %s", sql);
			
			List<Devices> results = (List<Devices>) query.executeQueryAsObject(sql.toString());
			
			log.debug("query successful, result size: " + results.size());
			return results;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

	// No reference
//	public List<Devices> getDevicesListFromNetworkIdList(List<Integer> netIdLst)
//	{
//		log.info("getting device list from network id list " + netIdLst);
//
//		Query query = session.createQuery("Select d From Devices d where d.networks.id in (:netIdLst) and d.active = true");
//		query.setParameterList("netIdLst", netIdLst);
//		return (List<Devices>) query.list();
//	}

	public List<Integer> getDevicesIdList(Integer networkId) throws SQLException
	{
		log.info("getting device id list from network id " + networkId);

		try {
			List<Integer> devIdList = new ArrayList<Integer>();
			for (Devices dev : getDevicesList(networkId))
			{
				devIdList.add(dev.getId());
			}

			log.debug("query successful, result size: " + devIdList.size());
			return devIdList;
		} catch (RuntimeException re) {
			log.error("query failed", re);
			throw re;
		}
	}

	public Devices getDevices(Integer devId) throws SQLException{
		List<Integer> devIdLst = new ArrayList<Integer>();
		devIdLst.add(devId);
		
		
		
		List<Devices> devicesLst = getDevicesList(devIdLst);
		
		if (devicesLst != null && devicesLst.size() == 1){
			return devicesLst.get(0);
		} else {
			return null;
		}
	}
	
	public List<Devices> getDevicesList(List<Integer> devIdLst) throws SQLException
	{
		log.info("getting device list from device id list " + devIdLst.toString());

		try {
			
			// Hibernate
//			Criteria cri = session.createCriteria(this.persistentClass);
//			cri.add(Restrictions.in("id", devIdLst));
//			cri.add(Restrictions.and(Restrictions.eq("active", true)));
//			List<Devices> results = cri.list();
			
			// JDBC
			DBConnection session = getSession();
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(persistentClass);
				
				String strDeviceList = "";
				Iterator<Integer> iter = devIdLst.iterator();
				while (iter.hasNext())
				{
					int intDevId = (Integer) iter.next();
					if (!strDeviceList.equals(""))
					{
						strDeviceList += ", ";
					}
					strDeviceList += Integer.toString(intDevId);
				}
				
				String strSQL = "select * from " + query.getDBName() + ".devices";
				if (!strDeviceList.equals(""))
				{
					 strSQL += " where id in (" + strDeviceList + ")";
				}
				
				List<Devices> results = query.executeQueryAsObject(strSQL);
			
				log.debug("query successful, result size: " + results.size());
				return results;
				
			} catch (SQLException e)
			{
				throw e;
			} finally
			{
				if (session!=null) session.close();
			}
		} catch (RuntimeException re) {
			log.error("query failed", re);
			throw re;
		}
	}

	public List<Integer> getDevicesIdByKeyword(String key) throws SQLException
	{
		List<Integer> result = new ArrayList<Integer>();

			// Hibernate
//			Criteria cri = session.createCriteria(this.persistentClass);
//			cri.add(Restrictions.or(Restrictions.like("name", "%" + key + "%"), Restrictions.like("sn", "%" + key + "%")));
//			cri.add(Restrictions.eq("active", true));
//			List<Devices> list = cri.list();
			
			// JDBC
			DBConnection session = getSession();
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(Devices.class);
				
				String strSQL = "select * from " + query.getDBName() + ".devices d where ( d.name like '%" + key + "%' or d.sn like '%" + key + "%' ) and d.active = true";
				List<Devices> list = query.executeQueryAsObject(strSQL);
				
				for (Devices dev : list)
				{
					result.add(dev.getId());
				}
				
			} catch (SQLException e)
			{
				throw e;
			} finally
			{
				if (session!=null) session.close();
			}


		return result;
	}
	
	public Devices findBySn(Integer iana_id, String sn) throws SQLException {
		log.info("getting device from with sn: " + sn + " and iana_id " + iana_id);
		
		// Hibernate
//		Query query = session.createQuery("Select d From Devices d where d.ianaId = :iana_id and d.sn = :sn and d.active = true");
//		query.setParameter("iana_id", iana_id);
//		query.setParameter("sn", sn);
//		return (Devices) query.uniqueResult();
		
		// JDBC
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Devices.class);
			
			String strSQL = "select * from " + query.getDBName() + ".devices d where d.iana_id = " + Integer.toString(iana_id) + " and d.sn = '" + sn + "' and d.active = 1";
//			System.out.println(strSQL);
		
			return (Devices) query.executeQueryAsSingleObject(strSQL);
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public Devices findBySnNet(Integer iana_id, String sn, Integer netId) throws SQLException {
		log.info("getting device from with sn: " + sn + " and iana_id " + iana_id);
		
		// Hibernate
//		Query query = session.createQuery("Select d From Devices d where d.ianaId = :iana_id and d.sn = :sn and d.networks.id = :netId and d.active = true");
//		query.setParameter("iana_id", iana_id);
//		query.setParameter("sn", sn);
//		query.setParameter("netId", netId);
//		return (Devices) query.uniqueResult();
		
		// JDBC
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Devices.class);
			
			String strSQL = "select * from " + query.getDBName() + ".devices d where d.iana_id = " + Integer.toString(iana_id) + " and d.sn = '" + sn + "' and d.network_id = " + Integer.toString(netId) + " and d.active = 1";
			log.debugf("findBySnNet strSQL=%s",strSQL);
			
			return (Devices) query.executeQueryAsSingleObject(strSQL);
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

	public List<Devices> getAllDevices() throws SQLException {
		log.debug("getting all devices");
		
		StringBuilder sb = new StringBuilder();
		
		// JDBC
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Devices.class);
			
			sb.append("select d.* from ");
			sb.append(query.getDBName());
			sb.append(".devices d inner join ");
			sb.append(query.getDBName());
			sb.append(".networks n ");
			sb.append(" where d.network_id = n.id and d.active = 1 and n.active = 1");
			
			return (List<Devices>) query.executeQueryAsObject(sb.toString());
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public HashMap<Integer, Devices> getAllDevicesMap() throws SQLException
	{
		log.info("getting all device map");
		
		HashMap<Integer, Devices> devMap = new HashMap<Integer, Devices>();
		List<Devices> devList = getAllDevices();

		log.debug("query successful, result size: " + devList.size());			
		if (devList!=null)
		{
			for (Devices f : devList)
			{
				devMap.put(f.getId(), f);
			}
		}
		
		return devMap;
	}
	
	public HashMap<Integer, Devices> devlistToMap(List<Devices> devLst)
	{
		HashMap<Integer, Devices> devMap = new HashMap<Integer, Devices>();
		if (devLst==null)
		{
			return devMap;
		}
		
		for (Devices f : devLst)
		{
			devMap.put(f.getId(), f);
		}
		return devMap;
	}

	// No reference
//	public List<Devices> getDevicesByTagses(List<String> tagNameList, Integer networkId) {
//		log.debug("getting device list with networkId: " + networkId + "tags list: " + tagNameList);
//		Query query = session.createQuery("select d from Devices d inner join d.tagses t where d.networks.id = :networkId and t.name in (:tagNameList) and d.active = true");
//		query.setParameter("networkId", networkId);
//		query.setParameterList("tagNameList", tagNameList);
//		return (List<Devices>) query.list();
//	}

	public List<Integer> getDevicesIdByTagses(List<String> tagNameList, Integer networkId) throws SQLException {
		log.debug("getting device list with networkId: " + networkId + " tags list: " + tagNameList);
		
		// Hibernate
//		Query query = session.createQuery("select d.id from Devices d inner join d.tagses t where d.networks.id = :networkId and t.name in (:tagNameList) and d.active = true");
//		query.setParameter("networkId", networkId);
//		query.setParameterList("tagNameList", tagNameList);
//		return (List<Integer>) query.list();
		
		// JDBC
		DBConnection session = getSession();
		ResultSet rs = null;
		try
		{
			DBQuery query = session.createQuery();
			
			String strListTags = "";
			Iterator iter = tagNameList.iterator();
			while (iter.hasNext())
			{
				String strTags = (String) iter.next(); 
				if (!strListTags.equals(""))
				{
					strListTags = ", ";
				}
				strListTags = "'" + strTags + "'";
			}
			
			String strSQL = "select d.id from " + query.getDBName() + ".devices d "
					+ "inner join " + query.getDBName() + ".devicesxtags dxt on d.id=dxt.device_id "
					+ "inner join " + query.getDBName() + ".tags t on dxt.tag_id = t.id "
					+ "where d.network_id = " + Integer.toString(networkId) + " and d.active = 1";
			
			if (!strListTags.equals("")){
				strSQL += " and ( t.name in (" + strListTags + ") )";
			}
			
			//log.debug("strSQL="+strSQL);
			
			List<Integer> returnList = new ArrayList<Integer>();
			rs = query.executeQuery(strSQL);
			while (rs.next()){
				returnList.add(rs.getInt(1));
			}
			rs.close();
			
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
	
	private void displayResult(List<Devices> list, Devices dev)
	{
		List<Devices> listDev = list;
		
		if (dev != null)
		{
			listDev =  new ArrayList<Devices>();
			listDev.add(dev);
		}
		
		int intCounter = 1;
		Iterator iter = listDev.iterator();
		while (iter.hasNext())
		{
			Devices temp = (Devices) iter.next();
//			System.out.println("*******************************");
//			System.out.println("[" + Integer.toString(intCounter) + "] " + temp.getId() + "\t" + temp.getName() + "\t" + temp.getProductId() + "\t" + temp.getModelId());
		}
	}
	
	public List<Integer> getDistinctProductsIdByNetwork(int networkId) throws SQLException {
		log.info("getting all products id of network " + networkId);
		// JDBC
		DBConnection session = getSession();
		ResultSet rs = null;
		List<Integer> result = null;
		try
		{
			DBQuery query = session.createQuery();
			
			String strSQL = "select distinct product_id from " + query.getDBName() + ".devices where network_id = " + networkId + " and active = 1";
			log.info("Get distinct product id sql : " + strSQL);
			rs = query.executeQuery(strSQL);
			if( rs != null )
			{
				result = new ArrayList<Integer>();
				while(rs.next())
				{
					result.add(rs.getInt("product_id"));
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
			if(session != null)
			{
				session.close();
				session = null;
			}
		}
		return result;
	}
	
	public List<Devices> getPurgeDeviceLst(Integer networkId) throws SQLException
	{
		DBConnection session = null;
		List<Devices> purge_device = null;
		try
		{
			session = getSession();
			DBQuery query = session.createQuery();
			query.setQueryClass(Devices.class);
			String sql = "select * from " + query.getDBName() + ".devices where network_id = "+ networkId +" and active=0 and purge_data = 1";
			purge_device = (List<Devices>)query.executeQueryAsObject(sql);
		}
		catch(Exception e)
		{
			log.error("Get purge device error -"+e,e);
		}
		finally
		{
			if(session != null)
			{
				session.close();
				session = null;
			}
		}
		return purge_device;
	}
	
	public void delete(Devices dev) throws SQLException 
	{
		log.debug("deleting " + this.persistentClass.getSimpleName() + " instance");
		super.delete(dev);
		NetUtils.deleteDevices(orgId, dev.getNetworkId(), dev);
	}
	
	public void save(Devices dev) throws SQLException {
		log.debug("saving " + this.persistentClass.getSimpleName() + " instance");
		super.save(dev);
		NetUtils.saveDevices(orgId, dev.getNetworkId(), dev);		
	}	
			
	public void saveOrUpdate(Devices dev) throws SQLException {
		log.debug("saveOrUpdate " + this.persistentClass.getSimpleName() + " instance");
		super.saveOrUpdate(dev);
		NetUtils.saveOrUpdateDevices(orgId, dev.getNetworkId(), dev);
	}
	
	public void update(Devices dev) throws SQLException {
		log.debug("update " + this.persistentClass.getSimpleName() + " instance");
		super.update(dev);
		NetUtils.saveOrUpdateDevices(orgId, dev.getNetworkId(), dev);
	}
	
	public void updateDb(Devices dev) throws SQLException {
		log.debug("updateDb " + this.persistentClass.getSimpleName() + " instance");
		super.update(dev);
	}
	
	public Devices checkMismatchOnlineOffline(String orgId, String sn) throws SQLException
	{
		log.infof("getting mismatch Online/Offline device list from orgid=%s, sn=%s" + orgId, sn);

		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Devices.class);
			
			String strSQL = "select * from peplink_organization_" + orgId + ".devices where sn = '" + sn + "' and active=1 and online_status=1 and offline_at > last_online";
			Devices dev = (Devices) query.executeQueryAsSingleObject(strSQL);
			log.debugf("query result=%s" + dev);
			return dev;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
}
