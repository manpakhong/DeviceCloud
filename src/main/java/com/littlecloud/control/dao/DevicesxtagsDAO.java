package com.littlecloud.control.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.Devicesxtags;
import com.littlecloud.control.entity.DevicesxtagsId;
import com.littlecloud.control.entity.viewobject.DeviceTags;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

public class DevicesxtagsDAO extends BaseDAO<Devicesxtags, DevicesxtagsId>{
	private static final Logger log = Logger.getLogger(DevicesxtagsDAO.class);
	
	public DevicesxtagsDAO() throws SQLException{
		super(Devicesxtags.class);
	}
	
	public DevicesxtagsDAO(String orgId) throws SQLException{
		super(Devicesxtags.class, orgId);
	}
	
	public DevicesxtagsDAO(String orgId, boolean readonly) throws SQLException{
		super(Devicesxtags.class, orgId, readonly);
	}
	
	public List<DeviceTags> getDeviceTagsByDeviceId(Integer deviceId) throws SQLException{
		
		log.infof("getting DeviceTags list from deviceId: %s ", deviceId);

		try {		
			// JDBC
			DBConnection session = getSession();
			ResultSet resultSet = null;
			try{
				DBQuery query = session.createQuery();
				query.setQueryClass(persistentClass);
				
				StringBuilder sbSql = new StringBuilder();
				sbSql.append("select dt.device_id, t.id as tag_id, t.name as tag_name from ");
				sbSql.append(query.getDBName() + ".devicesxtags dt ");
				sbSql.append("inner join ");
				sbSql.append(query.getDBName() + ".tags t on ");
				sbSql.append("t.id = dt.tag_id ");
				sbSql.append("where dt.device_id = " + deviceId + " ");
				sbSql.append("order by dt.device_id, t.id");
				List<DeviceTags> results = new ArrayList<DeviceTags>();
				resultSet = query.executeQuery(sbSql.toString());
				while(resultSet.next()){
					DeviceTags deviceTags = new DeviceTags();
					deviceTags.setDeviceId(resultSet.getInt("device_id"));
					deviceTags.setTagId(resultSet.getInt("tag_id"));
					deviceTags.setTagName(resultSet.getString("tag_name"));
					results.add(deviceTags);
				}
			
				log.debug("query successful, result size: " + results.size());
				return results;
				
			} catch (SQLException e){
				throw e;
			} finally{
				if(resultSet != null)
				{
					resultSet.close();
					resultSet = null;
				}
				if(session != null)
				{
					session.close();
					session = null;
				}
			}
		} catch (RuntimeException re) {
			log.error("query failed", re);
			throw re;
		}		
	}
	
	public List<DeviceTags> getDeviceTagsByTagsId(Integer tagsId) throws SQLException{
		
		log.infof("getting DeviceTags list from tagsId: %s ", tagsId);

		try {		
			// JDBC
			DBConnection session = getSession();
			ResultSet resultSet = null;
			try{
				DBQuery query = session.createQuery();
				query.setQueryClass(persistentClass);
				
				StringBuilder sbSql = new StringBuilder();
				sbSql.append("select dt.device_id, t.id as tag_id, t.name as tag_name from ");
				sbSql.append(query.getDBName() + ".devicesxtags dt ");
				sbSql.append("inner join ");
				sbSql.append(query.getDBName() + ".tags t on ");
				sbSql.append("t.id = dt.tag_id ");
				sbSql.append("where t.id = " + tagsId + " ");
				sbSql.append("order by t.id, dt.device_id");
				List<DeviceTags> results = new ArrayList<DeviceTags>();
				resultSet = query.executeQuery(sbSql.toString());
				while(resultSet.next()){
					DeviceTags deviceTags = new DeviceTags();
					deviceTags.setDeviceId(resultSet.getInt("device_id"));
					deviceTags.setTagId(resultSet.getInt("tag_id"));
					deviceTags.setTagName(resultSet.getString("tag_name"));
					results.add(deviceTags);
				}
			
				log.debug("query successful, result size: " + results.size());
				return results;
				
			} catch (SQLException e){
				throw e;
			} finally{
				if(resultSet != null)
				{
					resultSet.close();
					resultSet = null;
				}
				if(session != null)
				{
					session.close();
					session = null;
				}
			}
		} catch (RuntimeException re) {
			log.error("query failed", re);
			throw re;
		}		
	}
	
	public void saveOrUpdate(Devicesxtags transientInstance) throws SQLException {
		log.debug("saveIfNotExist " + this.persistentClass.getSimpleName() + " instance");
		DBConnection session = getSession();
		try {			
			session.saveIfNotExist(transientInstance);		
			log.debug("saveOrUpdate successful");
		} catch (RuntimeException re) {
			log.error("saveOrUpdate failed", re);
			throw re;
		} finally {
			if (session!=null) session.close();
		}
	}
}
