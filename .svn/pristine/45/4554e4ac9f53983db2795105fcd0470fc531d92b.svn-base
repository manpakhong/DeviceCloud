package com.littlecloud.control.dao;

import java.sql.SQLException;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.ClientIpMacMappings;
import com.littlecloud.control.entity.ClientIpMacMappingsId;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.util.DBUtil;

public class ClientIpMacMappingsDAO extends BaseDAO<ClientIpMacMappings, ClientIpMacMappingsId>{
	private static final Logger log = Logger.getLogger(ClientIpMacMappingsDAO.class);

	public ClientIpMacMappingsDAO() throws SQLException{
		super(ClientIpMacMappings.class);
	}
	
	public ClientIpMacMappingsDAO(String orgId) throws SQLException{
		super(ClientIpMacMappings.class, orgId);
	}
	
	/*public List<ClientIpMacMappings> getAllIpMacMapByNetworkDevice(Integer networkId, Integer deviceId){
		log.debug("getting all ip mac mapping with networkId: "+networkId+" deviceId: "+deviceId);
		Query query = session.createQuery("select cim  from ClientIpMacMappings cim where cim.id.networkId = :networkId and cim.id.deviceId = :deviceId");
		query.setParameter("networkId", networkId);
		query.setParameter("deviceId", deviceId);
		return (List<ClientIpMacMappings>) query.list();
	}*/
	public List<ClientIpMacMappings> getAllIpMacMapByNetworkDevice(Integer networkId, Integer deviceId) throws SQLException{
		log.debug("getting all ip mac mapping with networkId: "+networkId+" deviceId: "+deviceId);
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(ClientIpMacMappings.class);
			query.addCriteria("network_id", com.peplink.api.db.query.Criteria.EQ, networkId);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("device_id", com.peplink.api.db.query.Criteria.EQ, deviceId);
	
			return (List<ClientIpMacMappings>) query.executeQueryAsObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public ClientIpMacMappings findById(ClientIpMacMappingsId id) throws SQLException{
		log.debug("find by custom id " + this.getClass().getSimpleName()+" id= "+id);
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(ClientIpMacMappings.class);
			query.addCriteria("device_id", com.peplink.api.db.query.Criteria.EQ, id.getDeviceId());
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("ip", com.peplink.api.db.query.Criteria.EQ, id.getIp());
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("network_id", com.peplink.api.db.query.Criteria.EQ, id.getNetworkId());
			return (ClientIpMacMappings)query.executeQueryAsSingleObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public boolean saveClientIpMacMappingList(List<ClientIpMacMappings> clientIpMacMappingList) throws SQLException{
		boolean isSave = false;
		try{
			DBUtil dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
			DBConnection batchConnection = dbUtil.getConnection(false, orgId, false);
			if (clientIpMacMappingList != null && clientIpMacMappingList.size() > 0){
				for (ClientIpMacMappings clientIpMacMappings: clientIpMacMappingList){
					batchConnection.addBatch(clientIpMacMappings);
				}
			}
			batchConnection.commit();
			batchConnection.close();
			if(dbUtil.isSessionStarted()){
				dbUtil.endSession();
			}
			isSave = true;
		}
		catch(Exception e){
			log.error("ClientIpMacMappingsDAO.saveClientIpMacMappingList() - Exception: ", e);
		}
		finally{
			if(dbUtil.isSessionStarted()) {
				dbUtil.endSession();
			}
		}
		return isSave;
	}
	
}
