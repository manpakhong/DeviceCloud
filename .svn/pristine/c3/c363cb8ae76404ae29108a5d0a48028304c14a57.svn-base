package com.littlecloud.control.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.entity.report.ClientUsages;
import com.littlecloud.control.entity.report.DailyDeviceUsages;
import com.littlecloud.pool.utils.Utils;
import com.littlecloud.utils.CalendarUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.util.DBUtil;
import com.littlecloud.control.dao.jdbc.BaseDAO;

public class ClientUsagesDAO extends BaseDAO<ClientUsages, String> {
	private static final Logger log = Logger.getLogger(ClientUsagesDAO.class);

	public ClientUsagesDAO() throws SQLException {
		super(ClientUsages.class);
	}

	public ClientUsagesDAO(String orgId) throws SQLException {
		super(ClientUsages.class, orgId);
	}

	public ClientUsagesDAO(String orgId, boolean readonly) throws SQLException {
		super(ClientUsages.class, orgId, readonly);
	}
	
	public List<ClientUsages> getRecordsByNetIdAndDeviceIdAndTime(Integer netId,Integer devId,String datetime) throws SQLException
	{
		List<ClientUsages> result = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		
		try
		{
			DBQuery query = session.createQuery();
			String dbname = session.getDBName();
			String sql = "select id, mac, ip, name, tx, rx, datetime, unixtime, type from "+dbname+".client_usages where datetime like '"+datetime+"%' and device_id = "+devId+" and network_id="+netId+" group by ip;";
			log.warnf("getRecordsByNetIdAndDeviceIdAndTime: query=%s", sql);
			rs = query.executeQuery(sql);
			
			result = new ArrayList<ClientUsages>();
			
			while (rs.next()) {
				ClientUsages cu = new ClientUsages(rs.getString(1));
				cu.setUnixtime(rs.getInt(8));
				cu.setNetworkId(netId);
				cu.setDeviceId(devId);
				cu.setMac(rs.getString(2));
				cu.setIp(rs.getString(3));
				cu.setName(rs.getString(4));
				cu.setTx(rs.getFloat(5));
				cu.setRx(rs.getFloat(6));
				cu.setDatetime(rs.getDate(7));
				cu.setType(rs.getString(9));
				result.add(cu);
			}
		}
		catch( Exception e )
		{
			throw e;
		}
		finally
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
		
		return result;
	}
	

	public List<ClientUsages> getClientDailyUsagesRecords(Calendar calFrom, Calendar calTo, Integer networkId) throws SQLException{
		List<ClientUsages> result = new ArrayList<ClientUsages>();
		DBConnection session = getSession();
		ResultSet rs = null;
		try{
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("select ");
			sbSql.append("network_id, ");
			sbSql.append("device_id, ");
			sbSql.append("mac, ");
			sbSql.append("ip, ");
			sbSql.append("name, ");
			sbSql.append("sum(tx) as tx, ");
			sbSql.append("sum(rx) as rx, ");
			sbSql.append("type, ");
			sbSql.append("datetime, ");
			sbSql.append("unixtime ");
			sbSql.append("from ");
			sbSql.append(dbname + ".client_usages ");
			sbSql.append("where ");
			sbSql.append("datetime ");
			sbSql.append("between ");
			sbSql.append("'" + CalendarUtils.convertCalendar2MySqlDateString(calFrom) + "' ");
			sbSql.append("and ");
			sbSql.append("'" + CalendarUtils.convertCalendar2MySqlDateString(calTo) + "' ");
			sbSql.append("group by ");
			sbSql.append("device_id, ");
			sbSql.append("mac, ");
			sbSql.append("ip ");
			sbSql.append("order by ");
			sbSql.append("datetime desc, ");
			sbSql.append("network_id, ");
			sbSql.append("device_id ");

			if (log.isInfoEnabled()){
				log.infof("ClientUsagesDAO.getClientDailyUsagesRecords - sql: %s" + sbSql.toString());
			}
			rs = query.executeQuery(sbSql.toString());
			while (rs.next()){
				ClientUsages usageObj = new ClientUsages();
				usageObj.setNetworkId(rs.getInt("network_id"));
				usageObj.setDeviceId(rs.getInt("device_id"));
				usageObj.setMac(rs.getString("mac"));
				usageObj.setIp(rs.getString("ip"));
				usageObj.setName(rs.getString("name"));
				usageObj.setTx(rs.getFloat("tx"));
				usageObj.setRx(rs.getFloat("rx"));
				usageObj.setType(rs.getString("type"));
				usageObj.setDatetime((Date) rs.getObject("datetime"));
				result.add(usageObj);
			}
		} catch (Exception e){
			log.error("ClientUsagesDAO.getClientDailyUsagesRecords() - Exception:", e);
		} finally {
			if(rs != null){
				rs.close();
				rs = null;				
			}
			if( session != null ){
				session.close();
				session = null;
			}
		}
		return result;
	}
	
	public List<ClientUsages> getDailyRecords(Integer networkId, String tz, Integer start, Integer end) throws SQLException
	{
		List<ClientUsages> result = new ArrayList<ClientUsages>();
		DBConnection session = getSession();
		ResultSet rs = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		try
		{
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select network_id, device_id, mac, ip, name, sum(tx) as tx, sum(rx) as rx, type, " +
					"date_format(concat(YEAR(convert_tz(from_unixtime(unixtime),@@global.time_zone,'" + tz + "')),'-'," + 
					"MONTH(convert_tz(from_unixtime(unixtime),@@global.time_zone,'"+ tz +"')),'-'," + 
					"DAY(convert_tz(from_unixtime(unixtime),@@global.time_zone,'" + tz + "'))),'%Y-%m-%d') as datetime " +
					"FROM "+dbname+".client_usages where network_id = "+ networkId +" and unixtime >= "+start
					+" and unixtime < "+end+" group by device_id, mac, ip;";
			log.info("get client daily records sql : " + sql);
			rs = query.executeQuery(sql);
			ClientUsages usageObj = null;
			while (rs.next())
			{
				usageObj = new ClientUsages();
				usageObj.setNetworkId(rs.getInt("network_id"));
				usageObj.setDeviceId(rs.getInt("device_id"));
				usageObj.setMac(rs.getString("mac"));
				usageObj.setIp(rs.getString("ip"));
				usageObj.setName(rs.getString("name"));
				usageObj.setTx(rs.getFloat("tx"));
				usageObj.setRx(rs.getFloat("rx"));
				usageObj.setType(rs.getString("type"));
				usageObj.setDatetime(format.parse(rs.getString("datetime")));
				result.add(usageObj);
			}
			
		} 
		catch (Exception e)
		{
			log.error("Get daily client records failed",e);
		}
		finally
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
		
		return result;
	}
	
	public ClientUsages getUsageOfNetworkIdAndTime(Integer network_id, Integer unixtime) throws SQLException
	{
		ClientUsages result = null;
		DBConnection session = getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(ClientUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			String sql = "select * from "+dbname+".client_usages where network_id = " + network_id + " and unixtime = " + unixtime +" limit 1";
			log.info(sql);
			result = (ClientUsages)query.executeQueryAsSingleObject(sql);
		}
		catch(Exception e)
		{
			log.error("Get usage by network_id and time error "+e,e);
		}
		finally
		{
			if(session != null)
				session.close();
		}
		
		return result;
	}
	public boolean saveClientUsagesList(List<ClientUsages> clientUsageList) throws SQLException{
		boolean isSave = false;
		try{
			DBUtil dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
			DBConnection batchConnection = dbUtil.getConnection(false, orgId, false);
			if (clientUsageList != null && clientUsageList.size() > 0){
				for (ClientUsages clientUsage: clientUsageList){
					batchConnection.addBatch(clientUsage);
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
			log.error("ClientUsagesDAO.saveClientUsagesList() - Exception: ", e);
		}
		finally{
			if(dbUtil.isSessionStarted()) {
				dbUtil.endSession();
			}
		}
		return isSave;
	}
	
}
