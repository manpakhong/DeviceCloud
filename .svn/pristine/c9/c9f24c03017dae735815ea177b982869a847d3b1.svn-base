package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.alert.object.data.AlertDevLevelObject;
import com.littlecloud.control.dao.criteria.NetworkEmailNotificationsCriteria;
import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.NetworkEmailNotifications;
import com.littlecloud.control.entity.NetworkEmailNotificationsId;
import com.littlecloud.pool.utils.Utils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Condition;
import com.peplink.api.db.query.Criteria;

/**
 * Home object for domain model class Networks.
 * 
 * @author Hibernate Tools
 */
/*
 * public class NetworkEmailNotificationsDAO extends BaseDAO<NetworkEmailNotifications, NetworkEmailNotificationsId> {
 * 
 * private static final Logger log = Logger.getLogger(NetworkEmailNotificationsDAO.class);
 * 
 * public NetworkEmailNotificationsDAO() {
 * super(NetworkEmailNotifications.class);
 * }
 * 
 * public NetworkEmailNotificationsDAO(String orgId) {
 * super(NetworkEmailNotifications.class, orgId);
 * }
 * 
 * public NetworkEmailNotificationsDAO(String orgId, boolean readonly) {
 * super(NetworkEmailNotifications.class, orgId, readonly);
 * }
 * 
 * public List<NetworkEmailNotifications> getAllLevelContactForNetwork(Integer networkId){
 * log.debug("getting all level contact for network" + this.getClass().getSimpleName() + " with networkId: "+
 * networkId);
 * Query query = session.createQuery(
 * "select nen from NetworkEmailNotifications nen where nen.id.networkId = :networkId order by nen.id.level asc");
 * query.setParameter("networkId", networkId);
 * return (List<NetworkEmailNotifications>) query.list();
 * 
 * }
 * 
 * public NetworkEmailNotifications getLevelContactForNetwork(Integer networkId, String level){
 * log.debug("getting all level contact for network" + this.getClass().getSimpleName() + " with networkId: "+
 * networkId+" and level: "+level);
 * Query query = session.createQuery(
 * "select nen from NetworkEmailNotifications nen where nen.id.networkId = :networkId and nen.id.level=:level order by nen.id.level asc"
 * );
 * query.setParameter("networkId", networkId);
 * query.setParameter("level", level);
 * query.setMaxResults(1);
 * return (NetworkEmailNotifications) query.uniqueResult();
 * }
 * 
 * }
 */

public class NetworkEmailNotificationsDAO extends BaseDAO<NetworkEmailNotifications, NetworkEmailNotificationsId> {

	private static final Logger log = Logger.getLogger(NetworkEmailNotificationsDAO.class);

	public NetworkEmailNotificationsDAO() throws SQLException {
		super(NetworkEmailNotifications.class);
	}

	public NetworkEmailNotificationsDAO(String orgId) throws SQLException {
		super(NetworkEmailNotifications.class, orgId);
	}

	public NetworkEmailNotificationsDAO(String orgId, boolean readonly) throws SQLException {
		super(NetworkEmailNotifications.class, orgId, readonly);
	}

	public List<AlertDevLevelObject> getAlertDevLstWithOrderedLevel(boolean isOnlyUnAlerted) throws SQLException {

		List<AlertDevLevelObject> result = new ArrayList<AlertDevLevelObject>();
		StringBuilder sql = new StringBuilder();

		DBConnection session = null;
		DBQuery query = null;
		ResultSet rs = null;
		try {
			session = getSession();
			query = session.createQuery();
			String dbName = query.getDBName();

			sql.append("SELECT d.id as device_id, d.network_id, d.iana_id, d.sn, e.level, e.offline_min, e.recipient_type, e.info ");
			sql.append("FROM " + dbName + ".network_email_notifications e ");
			sql.append("inner join " + dbName + ".devices d on e.network_id = d.network_id ");
			sql.append("where d.active = 1 ");
			
			if (isOnlyUnAlerted)
			{
				sql.append("and not exists ( ");
				sql.append("SELECT * FROM " + dbName + ".device_status ds ");
				sql.append("where d.id = ds.device_id ");
				sql.append("and e.level <= ds.alerted_level ");
				sql.append(") ");
			}
			
			sql.append("and d.network_id not in ");
			sql.append("( SELECT n.id FROM "+ dbName + ".networks n where n.email_notification_enabled = 0 and n.active=1)");
			
			sql.append("order by d.network_id asc, d.id asc, e.level asc");
			
			rs = query.executeQuery(sql.toString());
			while (rs.next()) {
				// UnAlertDevLevelObject(int device_id, int network_id, int iana_id, String sn,
				// String level, int offlineMin, String recipientType, String info)

				AlertDevLevelObject ao = new AlertDevLevelObject(rs.getInt("device_id"), rs.getInt("network_id"),
						rs.getInt("iana_id"), rs.getString("sn"), rs.getString("level"), rs.getInt("offline_min"),
						rs.getString("recipient_type"), rs.getString("info"));
				result.add(ao);
			}
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (session != null) {
				session.close();
				session = null;
			}
		}
	}
	
	public List<NetworkEmailNotifications> getAllLevelContactForNetwork(Integer networkId) throws SQLException {

		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(NetworkEmailNotifications.class);
			String sql = "select e.* from " + query.getDBName() + ".network_email_notifications e, "
					+ query.getDBName() + ".networks n where e.network_id = " + networkId
					+ " and e.network_id = n.id and n.active = 1";

			return (List<NetworkEmailNotifications>) query.executeQueryAsObject(sql);
			// query.addCriteria("network_id", com.peplink.api.db.query.Criteria.EQ, networkId);
			// query.addOrderBy("network_id");
			// return (List<NetworkEmailNotifications>) query.executeQueryAsObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

	public NetworkEmailNotifications getLevelContactForNetwork(Integer networkId, String level) throws SQLException {
		log.debug("getting all level contact for network" + this.getClass().getSimpleName() + " with networkId: "
				+ networkId + " and level: " + level);
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(NetworkEmailNotifications.class);

			String sql = "select e.* from " + query.getDBName() + ".network_email_notifications e, "
					+ query.getDBName() + ".networks n where e.network_id = " + networkId + " and e.level = " + level
					+ " and e.network_id = n.id and n.active = 1";
			return (NetworkEmailNotifications) query.executeQueryAsSingleObject(sql);

			// query.addCriteria("network_id", com.peplink.api.db.query.Criteria.EQ, networkId);
			// query.addCondition(com.peplink.api.db.query.Condition.AND);
			// query.addCriteria("level", com.peplink.api.db.query.Criteria.EQ, level);
			// return (NetworkEmailNotifications)query.executeQueryAsSingleObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

	public NetworkEmailNotifications findById(NetworkEmailNotificationsId id) throws SQLException {
		log.debug("find by custom id " + this.getClass().getSimpleName() + " id= " + id);
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(NetworkEmailNotifications.class);

			String sql = "select e.* from " + query.getDBName() + ".network_email_notifications e, "
					+ query.getDBName() + ".networks n where e.network_id = " + id.getNetworkId() + " and e.level = "
					+ id.getLevel() + " and e.network_id = n.id and n.active = 1";
			return (NetworkEmailNotifications) query.executeQueryAsSingleObject(sql);

			// query.addCriteria("network_id", com.peplink.api.db.query.Criteria.EQ, id.getNetworkId());
			// query.addCondition(com.peplink.api.db.query.Condition.AND);
			// query.addCriteria("level", com.peplink.api.db.query.Criteria.EQ, id.getLevel());
			//
			// return (NetworkEmailNotifications)query.executeQueryAsSingleObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

	public Integer deleteByNetworkId(Integer network_id) throws SQLException {
		DBConnection session = getSession();
		String dbPrefix = Utils.PARAM_ORGANIZATION_DB_PREFIX;
		String dbname = dbPrefix + session.getOrgId();
		try {
			DBQuery query = session.createQuery();
			String sql = "delete from " + dbname + ".network_email_notifications where network_id = " + network_id;
			int result = query.executeUpdate(sql);
			return result;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session != null)
				session.close();
		}

	}

	public List<NetworkEmailNotifications> getNetworkEmailNotificationList(NetworkEmailNotificationsCriteria networkEmailNotificationsCriteria) throws SQLException {
		List<NetworkEmailNotifications> result = null;
		DBConnection session = getSession();
		
		try{
			if (networkEmailNotificationsCriteria != null){
			
				DBQuery query = session.createQuery();
				query.setQueryClass(NetworkEmailNotifications.class);
			
				int count = 0;
				
				if (networkEmailNotificationsCriteria.getEnabled() != null){
					if (count > 0){
						query.addCondition(Condition.AND);
					}
					query.addCriteria("enabled", Criteria.EQ, networkEmailNotificationsCriteria.getEnabled().booleanValue());
					count++;
				}
				
				if (networkEmailNotificationsCriteria.getAlertType() != null){
					if (count > 0){
						query.addCondition(Condition.AND);
					}
					
					query.addCriteria("alert_type", Criteria.EQ, networkEmailNotificationsCriteria.getAlertType());
					count++;
				}
				
				if (networkEmailNotificationsCriteria.getLevel() != null){
					if (count > 0){
						query.addCondition(Condition.AND);
					}
					
					query.addCriteria("level", Criteria.EQ, networkEmailNotificationsCriteria.getLevel());
					count++;
				}
				
				if (networkEmailNotificationsCriteria.getNetworkId() != null){
					if (count > 0){
						query.addCondition(Condition.AND);
					}
					
					query.addCriteria("network_id", Criteria.EQ, networkEmailNotificationsCriteria.getNetworkId());
					count++;
				}
				
				if (networkEmailNotificationsCriteria.getRecipientType() != null){
					if (count > 0){
						query.addCondition(Condition.AND);
					}
					
					query.addCriteria("recipient_type", Criteria.EQ, networkEmailNotificationsCriteria.getRecipientType());
					count++;
				}
				
				query.addOrderBy("network_id asc");
				query.addOrderBy("level asc");
				
				result = (List<NetworkEmailNotifications>)query.executeQueryAsObject();
			}
		}
		catch(Exception e){
			throw e;
		} 
		finally{
			if(session!=null){ 
				session.close();
			}
		}
		return result;
	}
	
}
