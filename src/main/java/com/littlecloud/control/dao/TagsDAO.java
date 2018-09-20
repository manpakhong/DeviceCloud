package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.ConfigurationSsidsId;
import com.littlecloud.control.entity.Tags;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBObject;
import com.peplink.api.db.DBQuery;

/**
 * Home object for domain model class Devices.
 * 
 * @see com.littlecloud.control.entity.Devices
 * @author Hibernate Tools
 */
public class TagsDAO extends BaseDAO<Tags, Long> {

	private static final Logger log = Logger.getLogger(TagsDAO.class);

	public TagsDAO() throws SQLException {
		super(Tags.class);
	}

	public TagsDAO(String orgId) throws SQLException {
		super(Tags.class, orgId);
	}

	public TagsDAO(String orgId, boolean readonly) throws SQLException {
		super(Tags.class, orgId, readonly);
	}

	public Tags findByName(String tagName) throws SQLException
	{
		log.debug("getting " + this.getClass().getSimpleName() + " instance with unique name: " + tagName);

		// Hibernate
//		Query query = session.createQuery("Select t From Tags t where t.name = :tagName");
//		query.setParameter("tagName", tagName);
//		return (Tags) query.uniqueResult();
		
		// JDBC
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Tags.class);
			
			String strSQL = "select t.* from " + query.getDBName() + ".tags t where t.name = '" + tagName + "'";
			
			log.debug(strSQL);
//			this.displayResult(null, (Tags) query.executeQueryAsSingleObject(strSQL));
			
			return (Tags) query.executeQueryAsSingleObject(strSQL);
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

	public List<Tags> getAllTagsWithSsids(Integer netId, Integer devId, Integer ssid_id) throws SQLException
	{
		log.debugf("getting " + this.getClass().getSimpleName() + " instance with netId %d devId %d ssid_id %d",
				netId, devId, ssid_id);
		
		StringBuilder sql = new StringBuilder();
		
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Tags.class);
			
			sql.append("select distinct t.* from ");
			sql.append(query.getDBName());
			sql.append(".tags t");
			sql.append(" inner join ");
			sql.append(query.getDBName());
			sql.append(".tagsxconfiguration_ssids txs on t.id=txs.tag_id ");
			sql.append(" inner join ");
			sql.append(query.getDBName());
			sql.append(".configuration_ssids conf on txs.network_id = conf.network_id");
			sql.append(" and txs.ssid_id=conf.ssid_id ");
			sql.append(" where conf.network_id = " + netId);
			sql.append(" and conf.device_id = " + devId);
			sql.append(" and conf.ssid_id= " + ssid_id);

			log.debugf("getAllTagsWithSsids sql = %s", sql);
			
			return (List<Tags>) query.executeQueryAsObject(sql.toString());
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public List<Tags> getAllTagsByDeviceId(Integer devId) throws SQLException{
		try{
			List<Integer> devIdList = new ArrayList<Integer>();
			devIdList.add(devId);
			
			List<Tags> tagList = getAllTagsWithDeviceIdList(devIdList);
			
			return tagList;
		}
		catch (SQLException e){
			throw e;
		}
	}
	
	public List<Tags> getAllTagsWithDeviceIdList(List<Integer> devIdLst) throws SQLException
	{
		log.debug("getting " + this.getClass().getSimpleName() + " instance with id: " + devIdLst.toString());

		// Hibernate
//		Query query = session.createQuery("Select distinct t From Tags t inner join t.deviceses d where d.id in (:idList)");
//		query.setParameterList("idList", devIdLst);
//		return (List<Tags>) query.list();
		
		// JDBC
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Tags.class);
			
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
			
			String strSQL = "select distinct t.* from " + query.getDBName() + ".tags t"
					+ " inner join " + query.getDBName() + ".devicesxtags dxt on t.id=dxt.tag_id"
					+ " inner join " + query.getDBName() + ".devices d on dxt.device_id=d.id";
					
			if (!strDeviceList.equals(""))
			{
				strSQL += " where d.id in ( " + strDeviceList + " )";
			}
//			System.out.println(strSQL);
//			this.displayResult((List<Tags>) query.executeQueryAsObject(strSQL), null);
			
			return (List<Tags>) query.executeQueryAsObject(strSQL);
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

	// No reference
//	public List<Long> getAllTagsIdWithName(List<String> tagNameLst, Integer networkId)
//	{
//		log.debug("getting " + this.getClass().getSimpleName() + " instance with name: " + tagNameLst.toString());
//
//		Query query = session.createQuery("Select distinct t From Tags t left join t.deviceses d where d.networks.id = :networkId and name in (:tagNameLst)");
//		query.setParameter("networkId", networkId);
//		query.setParameterList("tagNameLst", tagNameLst);
//		List<Long> tagIdLst = new ArrayList<Long>();
//		for (Tags tag : (List<Tags>) query.list())
//		{
//			tagIdLst.add(tag.getId());
//		}
//		return tagIdLst;
//	}

	public List<Tags> getAllTagsFromNetwork(Integer networkId) throws SQLException
	{
		log.info("getting all tags from network id " + networkId);
		
		List<Tags> tagList = getTagsListFromNetworkDevices(networkId);		
		tagList.addAll(getTagsListFromNetworkConfigSsid(networkId));
		return tagList;
	}
	
	public List<Tags> getTagsListFromNetworkDevices(Integer networkId) throws SQLException
	{
		log.info("getting tags list from devices with network id " + networkId);

		// Hibernate
//		Query query = session.createQuery("Select distinct t From Tags t inner join t.deviceses d where d.networks.id = :networkId");
//		query.setParameter("networkId", networkId);		
//		return (List<Tags>) query.list();
		
		// JDBC
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Tags.class);
			
			String strSQL = "select t.* from " + query.getDBName() + ".tags t"
					+ " inner join " + query.getDBName() + ".devicesxtags dxt on t.id = dxt.tag_id"
					+ " inner join " + query.getDBName() + ".devices d on dxt.device_id = d.id"
					+ " where d.network_id = " + Integer.toString(networkId)
					+ " and (select active from "+query.getDBName()+".networks where id = d.network_id) <> 0";
			
//			System.out.println(strSQL);
//			this.displayResult((List<Tags>) query.executeQueryAsObject(strSQL), null);
			
			return (List<Tags>) query.executeQueryAsObject(strSQL);
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

	public List<Tags> getTagsListFromNetworkConfigSsid(Integer networkId) throws SQLException
	{
		log.info("getting tags list from configSsid with network id " + networkId);

		// Hibernate
//		Query query = session.createQuery("Select distinct t From Tags t inner join t.configurationSsidses c where c.id.networkId in :networkId");
//		query.setParameter("networkId", networkId);
//		return (List<Tags>) query.list();
		
		// JDBC
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(Tags.class);
			
			String strSQL = "select distinct t.* from " + query.getDBName() + ".tags t"
					+ " inner join " + query.getDBName() + ".tagsxconfiguration_ssids txc on t.id = txc.tag_id"
					+ " inner join " + query.getDBName() + ".configuration_ssids cs on txc.network_id = cs.network_id and txc.ssid_id = cs.ssid_id"
					+ " where cs.network_id = " + Integer.toString(networkId)
					+ " and (select active from "+query.getDBName()+".networks where id = " + networkId + ") <> 0";
			
//			System.out.println(strSQL);
//			this.displayResult((List<Tags>) query.executeQueryAsObject(strSQL), null);
			
			return (List<Tags>) query.executeQueryAsObject(strSQL);
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

	public void removeTagsWithNameList(List<String> tagNameLst, Integer networkId) throws SQLException
	{
		log.debug("remove " + this.getClass().getSimpleName() + " instance with tag names: " + tagNameLst.toString());
		
		// Hibernate
		// session.createQuery("Delete From Tags t where t.networks.id = :networkId and t.name in (:idList)");
//		Query query = session.createQuery("Delete From Tags t left join t.deviceses d where d.networks.id =:networkId and t.name in (:idList)");
//		query.setParameter("networkId", networkId);
//		query.setParameterList("idList", tagNameLst);
//		query.executeUpdate();
		
		// JDBC
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			
			String strListTag = "";
			Iterator<String> iter = tagNameLst.iterator();
			while (iter.hasNext())
			{
				String strTagName = (String) iter.next();
				if (!strListTag.equals(""))
				{
					strListTag += ", ";
				}
				strListTag += "'" + strTagName + "'";
			}
			
			String strSQL = "delete from " + query.getDBName() + ".tags t"
					+ " left join " + query.getDBName() + ".devicextags d"
					+ " on t.netowrk_id = d.network_id"
					+ " where d.network_id = " + networkId;
			
			if (!strListTag.equals("")){
				strSQL +=  " and t.name in (" + strListTag + ")";
			}
			
//			System.out.println(strSQL);
			
			query.executeUpdate(strSQL);
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
		
	}

	// public void removeTagsListFromDevicesIdList(List<String> tagNameLst, List<Integer> devIdLst)
	// {
	// log.debug("remove " + this.getClass().getSimpleName() + " instance with dev Id: " + devIdLst.toString());
	// List<Devices> devLst =
	//
	// Query query = session.createQuery("Delete From Tags t where t.name in (:idList)");
	// query.setParameterList("idList", tagNameLst);
	// query.executeUpdate();
	// }
	
	private void displayResult(List<Tags> list, Tags tags)
	{
		List<Tags> listTags = list;
		
		if (tags != null)
		{
			listTags =  new ArrayList<Tags>();
			listTags.add(tags);
		}
		
		int intCounter = 1;
		Iterator iter = listTags.iterator();
		while (iter.hasNext())
		{
			Tags temp = (Tags) iter.next();
//			System.out.println("*******************************");
//			System.out.println("[" + Integer.toString(intCounter) + "] " + temp.getId() + "\t" + temp.getName());
		}
	}
	
	public List<Tags> findByConfigurationSsidsId(ConfigurationSsidsId confSsidId) throws SQLException
	{
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			
			StringBuilder sb = new StringBuilder();
			sb.append("select t1.* from ");
			sb.append(query.getDBName());
			sb.append(".");
			sb.append("tags t1 ");
			sb.append("where t1.id in (select t2.tag_id from " + query.getDBName() + ".tagsxconfiguration_ssids t2 where t2.network_id = ");
			sb.append(confSsidId.getNetworkId());
			sb.append(" and (select active from "+query.getDBName()+".networks where id = "+confSsidId.getNetworkId()+") <> 0");
			sb.append(" and t2.ssid_id = ");
			sb.append(confSsidId.getSsidId());
			sb.append(")");
						
			query.setQueryClass(persistentClass);
			List<Tags> results = query.executeQueryAsObject(sb.toString());
			log.debug("SQL="+sb.toString());
			return results;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public Map<Integer, List<Tags>> findByNetIdSsidId(Integer netId, List<Integer> ssidIdLst) throws SQLException
	{
		Map<Integer, List<Tags>> results = new HashMap<Integer, List<Tags>>();
		
		if (ssidIdLst == null || ssidIdLst.size()==0)
			return results;
		
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			
			String sql = StringUtils.join(
					"select distinct t2.ssid_id, t1.* from ",query.getDBName(),".tags t1 inner join ",query.getDBName(),".tagsxconfiguration_ssids t2", 
					" on t1.id = t2.tag_id and t2.network_id = ",
					netId, 
					" where t2.ssid_id in (",
					intListToParam(ssidIdLst),
					") and (select active from ",query.getDBName(),".networks where id = ",
					netId,
					") <> 0;");			
			query.setQueryClass(TagsWithSsidTO.class);
			log.debug("SQL="+sql);
			
			List<TagsWithSsidTO> tagsWithSsidList = query.executeQueryAsObject(sql);
			if (tagsWithSsidList==null || tagsWithSsidList.size()==0)
				return results;
			
			for (TagsWithSsidTO tagSsid:tagsWithSsidList)
			{
				List<Tags> tagLst = results.get(tagSsid.getSsid_id());
				if (tagLst == null)
					tagLst = new ArrayList<Tags>();
				tagLst.add((Tags)tagSsid);
				results.put(tagSsid.getSsid_id(), tagLst);
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
	
	@Table(name = "dummy")
	public static class TagsWithSsidTO extends Tags {
		private Integer ssid_id;

		@Column(name = "ssid_id")
		public Integer getSsid_id() {
			return ssid_id;
		}

		public void setSsid_id(Integer ssid_id) {
			this.ssid_id = ssid_id;
		}
	}
}
