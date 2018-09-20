package com.littlecloud.control.dao.jdbc;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDaoInstances.InstanceType;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBObject;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.util.DBUtil;

/**
 * Home object for domain model class BaseDAO.
 * 
 * @see com.littlecloud.control.dao.BaseDAO
 */
public class BaseDAO<T extends DBObject, PK extends Serializable> {

	private static final Logger log = Logger.getLogger(BaseDAO.class);
	protected InstanceType instanceType;
	
	// protected DBConnection session;
	protected Class<T> persistentClass;
	protected String orgId;
	protected boolean readonly = false;
	protected DBUtil dbUtil = null;
	protected String dbName = null;

	protected DBConnection getSession() throws SQLException {
		try {
			if (this.instanceType == InstanceType.IC2ROOT)
			{
				if(log.isDebugEnabled()) log.debugf("InstanceType %s -> readonly %s", this.instanceType.toString(), String.valueOf(this.readonly));
				return dbUtil.getConnection(true, false, null, false);
			}
			
			if (dbName != null && !dbName.trim().equals("")) {
				if(log.isDebugEnabled())
					log.debug("DBNAME: " + dbName);
				return dbUtil.getConnection(dbName);
			}
			
			if (this.orgId == null || this.orgId.isEmpty()) {
				if(log.isDebugEnabled())
					log.debug("ORGID BRANCH -> readonly=" + this.readonly);
				return dbUtil.getConnection(true, null, readonly);
			} else {
				if(log.isDebugEnabled())
					log.debug("ORGID ORGANIZATION: " + this.orgId);
				return dbUtil.getConnection(false, this.orgId, readonly);
			}
		} catch (SQLException e) {
			log.error("Fail to get connection", e);
		} catch (Exception e) {
			throw new SQLException("Fail to get connection", e); 
		}
		return null;
	}
	
	public BaseDAO(final Class<T> persistentClass, String propertyFile, String dbName) throws SQLException {
		try {
			this.dbUtil = DBUtil.getInstance(propertyFile);
			this.dbName = dbName;
			this.persistentClass = persistentClass;
			// this.session = createSession();
		} catch (Exception e) {
			throw new SQLException("Fail to get dbutil instance", e); 
		}
	}
	
	public BaseDAO(final Class<T> persistentClass) throws SQLException {
		try {
			this.dbUtil = DBUtil.getInstance();
			this.persistentClass = persistentClass;
			// this.session = createSession();
		} catch (Exception e) {
			throw new SQLException("Fail to get dbutil instance", e); 
		}
	}
	
	public BaseDAO(final Class<T> persistentClass, boolean readonly) throws SQLException {
		try {
			this.dbUtil = DBUtil.getInstance();
			this.persistentClass = persistentClass;
			this.readonly = readonly;
			// this.session = createSession();			
		} catch (Exception e) {
			throw new SQLException("Fail to get dbutil instance", e); 
		}
	}

	
	public BaseDAO(final Class<T> persistentClass, InstanceType instanceType) throws SQLException {
		try {
			this.instanceType = instanceType;
			
			if (instanceType != null){
				this.dbUtil = DBUtil.getInstance(instanceType.getValue());
			} else {
				this.dbUtil = DBUtil.getInstance();
			}
			this.persistentClass = persistentClass;
			// this.session = createSession();
		} catch (Exception e) {
			throw new SQLException("Fail to get dbutil instance", e); 
		}
	}
	
	public BaseDAO(final Class<T> persistentClass, InstanceType instanceType, boolean readonly) throws SQLException {
		try {
			this.instanceType = instanceType;
			
			if (instanceType != null){
				this.dbUtil = DBUtil.getInstance(instanceType.getValue());
			} else {
				this.dbUtil = DBUtil.getInstance();
			}
			this.persistentClass = persistentClass;
			this.readonly = readonly;
			// this.session = createSession();			
		} catch (Exception e) {
			throw new SQLException("Fail to get dbutil instance", e); 
		}
	}

	public BaseDAO(final Class<T> persistentClass, InstanceType instanceType, String orgId) throws SQLException {
		try {
			this.instanceType = instanceType;
			
			if (instanceType != null){
				this.dbUtil = DBUtil.getInstance(instanceType.getValue());
			} else {
				this.dbUtil = DBUtil.getInstance();
			}
			
			this.persistentClass = persistentClass;
			this.orgId = orgId;
			// this.session = createSession();
		} catch (Exception e) {
			throw new SQLException("Fail to get dbutil instance", e); 
		}
	}
	
	public BaseDAO(final Class<T> persistentClass, String orgId) throws SQLException {
		try {
			this.dbUtil = DBUtil.getInstance();
			this.persistentClass = persistentClass;
			this.orgId = orgId;
			// this.session = createSession();
		} catch (Exception e) {
			throw new SQLException("Fail to get dbutil instance", e); 
		}
	}

	public BaseDAO(final Class<T> persistentClass, InstanceType instanceType, String orgId, boolean readonly) throws SQLException {
		try {
			this.instanceType = instanceType;
			
			if (instanceType != null){
				this.dbUtil = DBUtil.getInstance(instanceType.getValue());
			} else {
				this.dbUtil = DBUtil.getInstance();
			}
			this.persistentClass = persistentClass;
			this.orgId = orgId;
			this.readonly = readonly;
			// this.session = createSession();
		} catch (Exception e) {
			throw new SQLException("Fail to get dbutil instance", e); 
		}
	}
	
	public BaseDAO(final Class<T> persistentClass, String orgId, boolean readonly) throws SQLException {
		try {
			this.dbUtil = DBUtil.getInstance();
			this.persistentClass = persistentClass;
			this.orgId = orgId;
			this.readonly = readonly;
			// this.session = createSession();
		} catch (Exception e) {
			throw new SQLException("Fail to get dbutil instance", e); 
		}
	}

	public void save(T transientInstance) throws SQLException {
		log.debug("saving " + this.persistentClass.getSimpleName() + " instance");
		DBConnection session = null;
		try {			
			session = getSession();
			session.save(transientInstance);
			
			// this.getSession().persist(transientInstance);
			if(log.isDebugEnabled())
				log.debug("save successful");

		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		} catch (Exception e) {
//			log.warn(dbUtil.dumpDBPool());
			throw new SQLException("save failed (others)", e);
		} finally {
			if (session!=null) session.close();			
		}
	}
	
	public void update(T transientInstance) throws SQLException {
		if(log.isDebugEnabled())
			log.debug("updating " + this.persistentClass.getSimpleName() + " instance");
		DBConnection session = null;
		try {			
			session = getSession();
			session.update(transientInstance);
			if(log.isDebugEnabled())
				log.debug("update successful");

		} catch (RuntimeException re) {
			log.error("update failed", re);
			throw re;
		} catch (Exception e) {
			throw new SQLException("update failed (others)", e);
		} finally {
			if (session!=null) session.close();			
		}
	}

	public void saveOrUpdate(T transientInstance) throws SQLException {
		if(log.isDebugEnabled())
			log.debug("saveOrUpdate " + this.persistentClass.getSimpleName() + " instance");
		DBConnection session = null;
		try {			
			session = getSession();
			session.saveOrUpdate(transientInstance);	
			if(log.isDebugEnabled())
				log.debug("saveOrUpdate successful");
		} catch (RuntimeException re) {
			log.error("saveOrUpdate failed", re);
			throw re;
		} catch (Exception e) {
			throw new SQLException("saveOrUpdate failed (others)", e);
		} finally {
			if (session!=null) session.close();
		}
	}

	public void delete(T persistentInstance) throws SQLException {
		if(log.isDebugEnabled())
			log.debug("deleting " + this.persistentClass.getSimpleName() + " instance");
		DBConnection session = null;
		try {			
			session = getSession();
			session.delete(persistentInstance);			
			// this.getSession().delete(persistentInstance);
			if(log.isDebugEnabled())
				log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		} catch (Exception e) {
			throw new SQLException("delete failed (others)", e);
		} finally {
			if (session!=null) session.close();
		}
	}

	public void delete(T[] persistentInstanceArray) throws SQLException {
		if(log.isDebugEnabled())
			log.debug("deleting Object Array" + this.persistentClass.getSimpleName() + " instance");
		DBConnection session = null;
		try {			
			session = getSession();
			session.delete(persistentInstanceArray);			
			if(log.isDebugEnabled())
				log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		} catch (Exception e) {
			throw new SQLException("delete failed (others)", e);
		} finally {
			if (session!=null) session.close();
		}
	}	
	
	public T findById(PK id) throws SQLException {
		if(log.isDebugEnabled())
			log.debug("getting " + this.persistentClass.getSimpleName() + " instance with id: " + id);
		DBConnection session = null;
		try {
			session = getSession();
			DBQuery query = session.createQuery();
			T instance = (T) query.findById(this.persistentClass, id);
			if (instance == null) {
				if(log.isDebugEnabled())
					log.debug("get successful, no instance found");
			} else {
				if(log.isDebugEnabled())
					log.debug("get successful, instance found");
			}
			return instance;
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new SQLException("findById failed (others)", e);
		} finally {
			if (session!=null) session.close();
		}
	}
	

	protected String intListToParam(List<Integer> IdLst)
	{
		if (IdLst==null || IdLst.size()==0)
			return "";
		
		StringBuilder sb = new StringBuilder();
		for (Object b : IdLst)
		{
			sb.append(b.toString());
			sb.append(",");
		}

		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
	
	protected String strListToParam(List<String> IdLst)
	{
		StringBuilder sb = new StringBuilder();
		for (Object b : IdLst)
		{
			sb.append(b.toString());
			sb.append(",");
		}

		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
}