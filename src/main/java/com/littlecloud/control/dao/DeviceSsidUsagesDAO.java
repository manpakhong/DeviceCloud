package com.littlecloud.control.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.report.DailyDeviceSsidUsages;
import com.littlecloud.control.entity.report.DeviceSsidUsages;
import com.littlecloud.pool.model.DailyDevSsidUsageResult;
import com.littlecloud.pool.utils.Utils;
import com.littlecloud.utils.CalendarUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

public class DeviceSsidUsagesDAO extends BaseDAO<DeviceSsidUsages, String>{
	private static final Logger log = Logger.getLogger(DeviceSsidUsagesDAO.class);
	
	public DeviceSsidUsagesDAO() throws SQLException{
		super(DeviceSsidUsages.class);
	}
	
	public DeviceSsidUsagesDAO(String orgId) throws SQLException{
		super(DeviceSsidUsages.class, orgId);
	}
	
	public DeviceSsidUsagesDAO(String orgId, boolean readonly) throws SQLException{
		super(DeviceSsidUsages.class, orgId, readonly);
	}
	public DeviceSsidUsages findByDeviceIdEssidEncryptionTime(Integer device_id, String Essid, String Encryption, Integer unixtime) throws SQLException{
		log.debug("getting " + this.getClass().getSimpleName() + " instance with device_id: " + device_id + " and Essid: "+ Essid + " and Encryption: "+ Encryption + " and unixtime: "+ unixtime );
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceSsidUsages.class);
			query.addCriteria("device_id", com.peplink.api.db.query.Criteria.EQ, device_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("essid", com.peplink.api.db.query.Criteria.EQ, Essid);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("encryption", com.peplink.api.db.query.Criteria.EQ, Encryption);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("unixtime", com.peplink.api.db.query.Criteria.EQ, unixtime);
	
			return (DeviceSsidUsages)query.executeQueryAsSingleObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	public DeviceSsidUsages findByDeviceIdEssidEncryptionCalendarRange(Integer deviceId, String essid, String encryption, Calendar calFrom, Calendar calTo) throws SQLException{
		if (log.isDebugEnabled()){
			log.debugf("DeviceSsidUsagesDAO.findByDeviceIdEssidEncryptionRange() - device_id: %s, Essid: %s, Encryption: %s, calFrom: %s, calTo: %s ", deviceId, essid, encryption, calFrom.getTime(), calTo.getTime());
		}
		DeviceSsidUsages result = null;
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceSsidUsages.class);
			query.addCriteria("device_id", com.peplink.api.db.query.Criteria.EQ, deviceId);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("essid", com.peplink.api.db.query.Criteria.EQ, essid);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("encryption", com.peplink.api.db.query.Criteria.EQ, encryption);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("datetime", com.peplink.api.db.query.Criteria.BETWEEN, CalendarUtils.convertCalendar2MySqlDateString(calFrom));
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("datetime", com.peplink.api.db.query.Criteria.BETWEEN, CalendarUtils.convertCalendar2MySqlDateString(calTo));
			result = (DeviceSsidUsages)query.executeQueryAsSingleObject(); 
		} catch (SQLException e){
			throw e;
		} finally{
			if (session!=null) {
				session.close();
			}
		}
		return result;
	}
	public List<DailyDeviceSsidUsages> getEssidEncryptionTotalTxRxWithTimePeriod(Integer networkId, Calendar calFrom, Calendar calTo) throws SQLException{
		if(log.isInfoEnabled()){
			log.infof("DeviceSsidUsagesDAO.getEssidEncryptionTotalTxRxWithTimePeriod() - networkId: %s, calFrom:%s, calTo:%s ", networkId, calFrom.getTime(), calTo.getTime());	
		}
		DBConnection session = getSession();
		ResultSet rs = null;
		List<DailyDeviceSsidUsages> resultList = null;
		try {
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("select ");
			sbSql.append("network_id, ");
			sbSql.append("device_id, ");
			sbSql.append("essid, ");
			sbSql.append("encryption, ");
			sbSql.append("sum(tx) as tx, ");
			sbSql.append("sum(rx) as rx ");
			sbSql.append("from ");
			sbSql.append(dbname + ".device_ssid_usages ");
			sbSql.append("where ");
			sbSql.append("network_id = " + networkId + " ");
			sbSql.append("and ");
			sbSql.append("datetime ");
			sbSql.append("between ");
			sbSql.append("'" + CalendarUtils.convertCalendar2MySqlDateString(calFrom) + "' ");
			sbSql.append("and ");
			sbSql.append("'" + CalendarUtils.convertCalendar2MySqlDateString(calTo) + "' ");
			sbSql.append("group by ");
			sbSql.append("device_id, ");
			sbSql.append("essid, ");
			sbSql.append("encryption ");
			
			rs = query.executeQuery(sbSql.toString());
			
			resultList = new ArrayList<DailyDeviceSsidUsages>();
			while (rs.next()){
				DailyDeviceSsidUsages result = new DailyDeviceSsidUsages();
				result.setNetworkId(rs.getInt("network_id"));
				result.setDeviceId(rs.getInt("device_id"));
				result.setEssid(rs.getString("essid"));
				result.setEncryption(rs.getString("encryption"));
				result.setRx(rs.getFloat("rx"));
				result.setTx(rs.getFloat("tx"));
				resultList.add(result);
			}

		} catch (SQLException e){
			throw e;
		} finally{
			if(rs != null){
				rs.close();
				rs = null;
			}
			if(session != null){
				session.close();
				session = null;
			}
		}
		return resultList;
	}

	public List<DailyDevSsidUsageResult> getEssidEncryptionTotalTxRxWithTimePeriod( Integer network_id, Integer startTime, Integer endTime) throws SQLException{
		if(log.isInfoEnabled())
			log.info("getting summerize device usage List " + this.getClass().getSimpleName()+" and start time: "+ startTime +" and end time: "+endTime);	
		DBConnection session = getSession();
		ResultSet rs = null;
		try {
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			rs = query.executeQuery("SELECT network_id, device_id, essid, encryption, sum(tx) as tx, sum(rx) as rx FROM "+dbname+".device_ssid_usages where network_id = " + network_id + " and unixtime>= "+startTime+" and unixtime<="+endTime+" group by device_id, essid, encryption;");
			
			List<DailyDevSsidUsageResult> resultList = new ArrayList<DailyDevSsidUsageResult>();
			DailyDevSsidUsageResult result = null;
			while (rs.next())
			{
				result = new DailyDevSsidUsageResult();
				result.setNetwork_id(rs.getInt("network_id"));
				result.setDevice_id(rs.getInt("device_id"));
				result.setEssid(rs.getString("essid"));
				result.setEncryption(rs.getString("encryption"));
				result.setRx(rs.getDouble("rx"));
				result.setTx(rs.getDouble("tx"));
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
			if(session != null)
			{
				session.close();
				session = null;
			}
		}
	}
}
