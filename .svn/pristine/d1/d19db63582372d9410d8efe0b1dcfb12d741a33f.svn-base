package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0


import java.sql.SQLException;

import org.jboss.logging.Logger;

import com.littlecloud.control.entity.Organizations;
import com.littlecloud.control.dao.jdbc.BaseDAO;





/**
 * Home object for domain model class DeviceStatus.
 * 
 * @see com.littlecloud.control.entity.DeviceStatus
 * @author Hibernate Tools
 */
public class OrganizationsDAO extends BaseDAO<Organizations, String> {

	private static final Logger log = Logger.getLogger(OrganizationsDAO.class);

	public OrganizationsDAO() throws SQLException {
		super(Organizations.class);
	}

	public OrganizationsDAO(String orgId) throws SQLException {
		super(Organizations.class, orgId);
	}
	
	public OrganizationsDAO(String orgId, boolean readonly) throws SQLException {
		super(Organizations.class, orgId, readonly);
	}

//	public Organizations getOrganizations(String orgId){
//		log.debug("getting Organizations with orgId = "+orgId);
//		Query query = session.createQuery("select org from Organizations org where org.id = :orgId");
//		query.setParameter("orgId", orgId);
//		query.setMaxResults(1);
//		return (Organizations)query.uniqueResult();
//	}
}
