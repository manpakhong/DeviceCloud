package com.littlecloud.control.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.report.ClientSsidUsages;
import com.littlecloud.control.entity.report.ClientUsages;
import com.littlecloud.pool.model.DailyClientSsidUsageResult;
import com.littlecloud.pool.utils.Utils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.util.DBUtil;

public class ClientSsidUsagesDAO extends BaseDAO<ClientSsidUsages, String>{
	private static final Logger log = Logger.getLogger(ClientSsidUsagesDAO.class);
	
	public ClientSsidUsagesDAO() throws SQLException{
		super(ClientSsidUsages.class);
	}
	
	public ClientSsidUsagesDAO(String orgId) throws SQLException{
		super(ClientSsidUsages.class, orgId);
	}
	
	public ClientSsidUsagesDAO(String orgId, boolean readonly) throws SQLException{
		super(ClientSsidUsages.class, orgId, readonly);
	}
	

	public ClientSsidUsages findLatestRecordByMac(String mac) throws SQLException{
		log.debug("getting " + this.getClass().getSimpleName() + "max instance with mac: " + mac );
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(ClientSsidUsages.class);
			query.addCriteria("mac", com.peplink.api.db.query.Criteria.EQ, mac);
			query.addOrderBy("unixtime desc");
					
			return (ClientSsidUsages) query.executeQueryAsSingleObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	

	public List<DailyClientSsidUsageResult> getSsidEncryptionByNetworkTimePeriod(Integer network_id, Integer startTime, Integer endTime) throws SQLException{
		if(log.isDebugEnabled())
			log.debug("getSsidEncryptionByNetworkTimePeriod: getting distinct client List " + this.getClass().getSimpleName()+" and start time: "+ startTime +" and end time: "+endTime);
		DBConnection session = getSession();
		ResultSet rs = null;
		try {
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "SELECT network_id, device_id, bssid, essid, encryption, mac, type FROM "+dbname+".client_ssid_usages where network_id = " + network_id + " and active = 1 and unixtime >= "+startTime+" and unixtime<="+endTime+" and type=\'wireless\' group by device_id, mac, essid, encryption";
			rs = query.executeQuery(sql);
			if(log.isInfoEnabled())
				log.info("getSsidEncryptionByNetworkTimePeriod sql : " + "SELECT device_id, bssid, essid, encryption, mac, type FROM "+dbname+".client_ssid_usages where active = 1 and unixtime>= "+startTime+" and unixtime<="+endTime+" group by mac, essid, encryption, device_id;");
			List<DailyClientSsidUsageResult> resultList = new ArrayList<DailyClientSsidUsageResult>();
			DailyClientSsidUsageResult result = null;
			while (rs.next())
			{
				result = new DailyClientSsidUsageResult();
				result.setNetworkId(Integer.valueOf(rs.getInt("network_id")));
				result.setDeviceId(Integer.valueOf(rs.getInt("device_id")));
				result.setBssid(rs.getString("bssid"));
				result.setEssid(rs.getString("essid"));
				result.setEncryption(rs.getString("encryption"));
				result.setMac(rs.getString("mac"));
				result.setType(rs.getString("type"));
				resultList.add(result);
			}
			if (resultList.isEmpty()){
				return null;
			}
			return resultList;
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
	
	public boolean saveClientSsidUsagesList(List<ClientSsidUsages> clientSsidUsageList) throws SQLException{
		boolean isSave = false;
		try{
			DBUtil dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
			DBConnection batchConnection = dbUtil.getConnection(false, orgId, false);
			if (clientSsidUsageList != null && clientSsidUsageList.size() > 0){
				for (ClientSsidUsages clientSsidUsage: clientSsidUsageList){
					batchConnection.addBatch(clientSsidUsage);
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
			log.error("ClientSsidUsagesDAO.saveClientSsidUsagesList() - Exception: ", e);
		}
		finally{
			if(dbUtil.isSessionStarted()) {
				dbUtil.endSession();
			}
		}
		return isSave;
	}
	

}

