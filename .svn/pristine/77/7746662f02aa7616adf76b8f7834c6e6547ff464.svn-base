package com.littlecloud.control.dao;

import java.sql.SQLException;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.Devicesxtags;
import com.littlecloud.control.entity.TagsxconfigurationSsids;
import com.littlecloud.control.entity.TagsxconfigurationSsidsId;
import com.peplink.api.db.DBConnection;

public class TagsxconfigurationSsidsDAO extends BaseDAO<TagsxconfigurationSsids, TagsxconfigurationSsidsId>{
	private static final Logger log = Logger.getLogger(TagsxconfigurationSsidsDAO.class);
	
	public TagsxconfigurationSsidsDAO() throws SQLException{
		super(TagsxconfigurationSsids.class);
	}
	
	public TagsxconfigurationSsidsDAO(String orgId) throws SQLException{
		super(TagsxconfigurationSsids.class, orgId);
	}
	
	public TagsxconfigurationSsidsDAO(String orgId, boolean readonly) throws SQLException{
		super(TagsxconfigurationSsids.class, orgId, readonly);
	}
	
	public void saveOrUpdate(TagsxconfigurationSsids transientInstance) throws SQLException {
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
