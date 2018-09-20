package com.littlecloud.control.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.entity.report.DailyClientSsidUsages;
import com.littlecloud.pool.model.DailyClientSsidUsageResult;
import com.littlecloud.pool.model.DailyClientUsageResult;
import com.littlecloud.pool.utils.Utils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.littlecloud.control.dao.jdbc.BaseDAO;

public class DailyClientSsidUsagesDAO extends BaseDAO<DailyClientSsidUsages, Integer>{
	private static final Logger log = Logger.getLogger(DailyClientSsidUsagesDAO.class);
	
	public DailyClientSsidUsagesDAO() throws SQLException{
		super(DailyClientSsidUsages.class);
	}
	
	public DailyClientSsidUsagesDAO(String orgId) throws SQLException{
		super(DailyClientSsidUsages.class, orgId);
	}
	
	public DailyClientSsidUsagesDAO(String orgId, boolean readonly) throws SQLException{
		super(DailyClientSsidUsages.class, orgId, readonly);
	}
	
	public DailyClientSsidUsages findByDeviceIdEssidEncryptionTime(Integer network_id, Integer device_id, String Essid, String Encryption, Integer unixtime, String mac) throws SQLException {
		if(log.isInfoEnabled())
			log.info("DailyClientSsidUsages : getting " + this.getClass().getSimpleName() + " instance with network_id=" + network_id + ", device_id="+ device_id + ", Essid="+ Essid + ", Enc="+ Encryption + ", unixtime="+ unixtime + ", mac=" + mac);
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(DailyClientSsidUsages.class);
			query.addCriteria("network_id", com.peplink.api.db.query.Criteria.EQ, network_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("device_id", com.peplink.api.db.query.Criteria.EQ, device_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("essid", com.peplink.api.db.query.Criteria.EQ, Essid);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("encryption", com.peplink.api.db.query.Criteria.EQ, Encryption);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("unixtime", com.peplink.api.db.query.Criteria.EQ, unixtime);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("mac", com.peplink.api.db.query.Criteria.EQ, mac);
			query.setLimit(1);
			return (DailyClientSsidUsages)query.executeQueryAsSingleObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

	public List<DailyClientSsidUsageResult> getSsidTotalClientCountList(Integer network_id, Integer startTime, Integer endTime) throws SQLException{
		log.debug("getting ssid total client count" + this.getClass().getSimpleName() + "  with network_id: "+network_id+ " and start time: "+ startTime +" and end time: "+endTime);
		DBConnection session = getSession();
		ResultSet rs = null;
		try {
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			rs = query.executeQuery("SELECT device_id, bssid, essid, encryption, count(distinct(mac)) FROM "+dbname+".daily_client_ssid_usages where network_id ="+network_id+" and unixtime>= "+startTime+" and unixtime<="+endTime+" group by essid, encryption");
			
			List<DailyClientSsidUsageResult> resultList = new ArrayList<DailyClientSsidUsageResult>();
			DailyClientSsidUsageResult result = null;
			String col;
			while (rs.next()){
				int i=1;
				result = new DailyClientSsidUsageResult();
				col = rs.getString(i++);
				result.setDeviceId(Integer.valueOf(col));
				col = rs.getString(i++);
				result.setBssid(col);
				col = rs.getString(i++);
				result.setEssid(col);
				col = rs.getString(i++);
				result.setEncryption(col);
				col = rs.getString(i++);
				result.setClient_count(Long.valueOf(col));
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

	public List<DailyClientSsidUsageResult> getSsidTotalClientCountListWithDevId(Integer network_id, Integer device_id, Integer startTime, Integer endTime) throws SQLException{
		log.debug("getting ssid total client count" + this.getClass().getSimpleName() + "  with network_id: "+network_id+ " and start time: "+ startTime +" and end time: "+endTime);
		DBConnection session = getSession();
		ResultSet rs = null;
		try {
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			rs = query.executeQuery("SELECT device_id, bssid, essid, encryption, count(distinct(mac)) FROM "+dbname+".daily_client_ssid_usages where network_id = "+network_id+" and device_id = "+device_id+" and unixtime>= "+startTime+" and unixtime<="+endTime+" group by essid, encryption");
			
			List<DailyClientSsidUsageResult> resultList = new ArrayList<DailyClientSsidUsageResult>();
			DailyClientSsidUsageResult result = null;
			String col;
			while (rs.next()){
				int i=1;
				result = new DailyClientSsidUsageResult();
				col = rs.getString(i++);
				result.setDeviceId(Integer.valueOf(col));
				col = rs.getString(i++);
				result.setBssid(col);
				col = rs.getString(i++);
				result.setEssid(col);
				col = rs.getString(i++);
				result.setEncryption(col);
				col = rs.getString(i++);
				result.setClient_count(Long.valueOf(col));
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

	public static void main(String[] args)
	{
		try {
			DailyClientSsidUsagesDAO dao = new DailyClientSsidUsagesDAO("jtCC3O");
			DailyClientSsidUsages rtn = dao.findByDeviceIdEssidEncryptionTime(3, 15, "BowenWave", "WPA2-AES", 1414454400, "F0:25:B7:7E:C7:8B");
			System.out.println("rtn = " + rtn);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
