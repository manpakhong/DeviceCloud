package com.littlecloud.control.dao;

import java.sql.SQLException;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.ClientInfos;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

public class ClientInfosDAO extends BaseDAO<ClientInfos, String>
{
	private static final Logger log = Logger.getLogger(ClientInfosDAO.class);
	
	public ClientInfosDAO(String orgId) throws SQLException
	{
		super(ClientInfos.class,orgId);
	}
	
	public ClientInfosDAO(String orgId,boolean readonly) throws SQLException
	{
		super(ClientInfos.class,orgId,readonly);
	}

	public ClientInfos findByClientId(String clientId) throws SQLException
	{
		ClientInfos info  = null;
		DBConnection session = getSession();
		
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(ClientInfos.class);
			query.addCriteria("client_id", com.peplink.api.db.query.Criteria.EQ, clientId);
			info = (ClientInfos)query.executeQueryAsSingleObject();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		}
		return info;
	}
}
