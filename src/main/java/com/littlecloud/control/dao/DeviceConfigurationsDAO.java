package com.littlecloud.control.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.DeviceConfigurations;
import com.littlecloud.pool.utils.Utils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

public class DeviceConfigurationsDAO extends BaseDAO<DeviceConfigurations, Integer>{
	private static final Logger log = Logger.getLogger(DeviceConfigurationsDAO.class);
	
	public DeviceConfigurationsDAO() throws SQLException {
		super(DeviceConfigurations.class);
	}
	
	public DeviceConfigurationsDAO(String orgId) throws SQLException {
		super(DeviceConfigurations.class,orgId);
	}
	
	public DeviceConfigurationsDAO(String orgId,boolean readonly) throws SQLException {
		super(DeviceConfigurations.class,orgId,readonly);
	}
	
	public DeviceConfigurations getLatestDeviceConfigurations(Integer devId) throws SQLException{
		List<DeviceConfigurations> devConfsLst = getDeviceConfigurationsList(devId);
		if (devConfsLst.size() > 0){
			DeviceConfigurations devConfs = devConfsLst.get(devConfsLst.size() -1);
			return devConfs;
		}
		return null;
	}
	
	public DeviceConfigurations getDeviceConfigurations(Integer id) throws SQLException {
		DeviceConfigurations devConfs = new DeviceConfigurations();
		
		// JDBC
		DBConnection session = getSession();
		DBQuery query = null;
		try
		{
			query = session.createQuery();
						
			query.setQueryClass(DeviceConfigurations.class);
			query.addCriteria("id", com.peplink.api.db.query.Criteria.EQ, id);
			
			log.info("query:"+query.getQuery());
			
			devConfs = (DeviceConfigurations)query.executeQueryAsSingleObject();
			
		}catch (Exception e){
			if (e instanceof SQLException){
				log.error("query failed", e);
			} else{
				log.error("Exception", e);
			}
		} finally
		{
			if (session!=null) session.close();
		}
		
		return devConfs;
	}
	
	public List<DeviceConfigurations> getDeviceConfigurationsList(Integer devId) throws SQLException{
		List<Integer> devIdLst = new ArrayList<Integer>();
		devIdLst.add(devId);
		List<DeviceConfigurations> deviceConfigurationsList = getDeviceConfigurationsList(devIdLst);
		return deviceConfigurationsList;
	}
	
	 public List<DeviceConfigurations> getDeviceConfigurations(Integer devId, Date date) throws SQLException
	 {
	    List<DeviceConfigurations> deviceConfigurationsList = new ArrayList<DeviceConfigurations>();
	    if (date != null && devId != null)
	    {
	      deviceConfigurationsList = getDeviceConfigurationsList(devId, date, null);
	    }
	   return deviceConfigurationsList;
	 }
	 
	 public List<DeviceConfigurations> getDeviceConfigurationsBeforeDays(Integer devId, Integer daysBefore) throws SQLException{
	        List<DeviceConfigurations> deviceConfigurationsList = new ArrayList<DeviceConfigurations>();
	        if (devId != null && daysBefore != null){
	            deviceConfigurationsList = getDeviceConfigurationsList(devId, null, daysBefore);
	        }
	        return deviceConfigurationsList;
	    }
	
	public void save(DeviceConfigurations devConfs) throws SQLException {
		log.debug("saving " + this.persistentClass.getSimpleName() + " instance");

		super.save(devConfs);
	}
	
	public List<DeviceConfigurations> getDeviceConfigurationsList(List<Integer> devIdLst) throws SQLException
	{
		log.info("getting deviceConfigurations list from device id list " + devIdLst.toString());
		try {
			
			// Hibernate
//			Criteria cri = session.createCriteria(this.persistentClass);
//			cri.add(Restrictions.in("id", devIdLst));
//			cri.add(Restrictions.and(Restrictions.eq("active", true)));
//			List<Devices> results = cri.list();
			
			// JDBC
			DBConnection session = getSession();
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(persistentClass);
				
				String strDeviceList = "";
				Iterator<Integer> iter = devIdLst.iterator();
				while (iter.hasNext())
				{
					int intDevId = (Integer) iter.next();
					if (!strDeviceList.equals(""))
					{
						strDeviceList += ", ";
					}
					strDeviceList += Integer.toString(intDevId);
				}
				
				String strSQL = "select * from " + query.getDBName() + ".device_configurations";
				if (!strDeviceList.equals(""))
				{
					 strSQL += " where device_id in (" + strDeviceList + ") order by backup_time asc";
				}
				
				List<DeviceConfigurations> results = query.executeQueryAsObject(strSQL);
			
				log.debug("query successful, result size: " + results.size());
				return results;
				
			} catch (SQLException e)
			{
				throw e;
			} finally
			{
				if (session!=null) session.close();
			}
		} catch (RuntimeException re) {
			log.error("query failed", re);
			throw re;
		}
	}
	public DeviceConfigurations getDeviceConfigurationFile(int device_id,int id) throws SQLException
	{
		DBConnection session = getSession();
		try 
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceConfigurations.class);
			query.addCriteria("device_id", com.peplink.api.db.query.Criteria.EQ, device_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("id", com.peplink.api.db.query.Criteria.EQ, id);
			log.info("query:"+query.getQuery());
			return (DeviceConfigurations)query.executeQueryAsSingleObject();
		} 
		catch (SQLException e)
		{
			throw e;
		} 
		finally
		{
			if (session!=null) 
				session.close();
		}
	}	
			
	private List<DeviceConfigurations> getDeviceConfigurationsList(Integer devId, Date date, Integer daysBefore) throws SQLException
	{
		log.info("getting deviceConfigurations list from device id list " + devId.toString());

		try {
						
			// JDBC
			DBConnection session = getSession();
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(persistentClass);
				
//				String strDeviceList = "";
//				Iterator<Integer> iter = devIdLst.iterator();
//				while (iter.hasNext())
//				{
//					int intDevId = (Integer) iter.next();
//					if (!strDeviceList.equals(""))
//					{
//						strDeviceList += ", ";
//					}
//					strDeviceList += Integer.toString(intDevId);
//				}
				
				String strSQL = "select * from " + query.getDBName() + ".device_configurations";
//				if (!strDeviceList.equals("")){
					 strSQL += " where device_id = " + devId + "";
//				}
				
				if (date != null && daysBefore == null){
					int unixTime = (int) (date.getTime() /1000);	
					strSQL += " and backup_time >= " + unixTime;
					log.debug("L20140122 - isLastConfigFileMd5TheSame devId:" + devId + ",sql: " + strSQL);								
				}
				
				if (date == null && daysBefore != null){
					
//					int minusUnixTime = (60 * 24 * daysBefore * 60);
//					int unixTime = (int) (date.getTime() / 1000);
					
//					int dayBeforeUnixTime = unixTime - minusUnixTime;
					
					Integer dayBeforeUnixTime = findTenDaysRecordUnixDate(devId, daysBefore);

					if (dayBeforeUnixTime == null){
						return null;
					}
					
					strSQL += " and backup_time < " + dayBeforeUnixTime;
				}
				strSQL += " order by backup_time";
				log.infof("DeviceConfigurations Sql: ", strSQL);
				
				List<DeviceConfigurations> results =  query.executeQueryAsObject(strSQL);
			
				log.debug("query successful, result size: " + results.size());
				return results;
				
			} catch (SQLException e){
				throw e;
			} finally{
				if (session!=null) session.close();
			}
		} catch (RuntimeException re) {
			log.error("query failed", re);
			throw re;
		}
	}
	
	private Integer findTenDaysRecordUnixDate(Integer devId, Integer daysBefore) throws SQLException {
		Integer tenDaysRecordUnixDate = null;
		ResultSet result = null;
		
		DBConnection session = super.getSession();
		String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
		try{
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);

			StringBuilder sb = new StringBuilder();
			sb.append("select unix_timestamp(");
				sb.append("str_to_date(");
					sb.append("min(");
						sb.append("ten_dates.dates");
					sb.append(")"); // end min
					sb.append(",");
					sb.append("'%Y-%m-%d'");
				sb.append(")"); // end str_to_date
			sb.append(") as ten_date_unix_date"); // end unix_timestamp
			sb.append(" from");
			sb.append("(");
				sb.append("select distinct from_unixtime(");
					sb.append("backup_time,");
					sb.append("'%Y-%m-%d'");
				sb.append(") as dates");
				sb.append(" from");
				sb.append(" " + dbname + ".device_configurations"); 
				sb.append(" where device_id = ");
				sb.append(devId);
				sb.append(" order by backup_time desc limit ");
				sb.append(daysBefore);
			sb.append(") ten_dates"); // subquery
//			tenDaysRecordUnixDate 

			result = query.executeQuery(sb.toString());
			while (result.next()){
				tenDaysRecordUnixDate = result.getInt("ten_date_unix_date");
			}

		}
		catch(Exception e){
			throw e;
		}
		finally{
			if(result != null)
			{
				result.close();
				result = null;
			}
			if(session != null)
			{
				session.close();
				session = null;
			}
		}
		return tenDaysRecordUnixDate;
	}
	
	public void delete(DeviceConfigurations devConfs) throws SQLException 
	{
		log.debug("deleting " + this.persistentClass.getSimpleName() + " instance");
		super.delete(devConfs);

//		NetUtils.deleteDeviceConfigurations(orgId, dev.getNetworkId(), dev);
	}		
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public List<Integer> getDistinctbackup_time(Integer dev_id) throws SQLException
	{
		List<Integer> result = new ArrayList<Integer>();
		DBConnection session = getSession();
		ResultSet rs = null;
		
		try
		{
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			rs = query.executeQuery("select backup_time from "+dbname+".device_configurations where device_id ="+dev_id+" group by backup_time;");
			result = new ArrayList<Integer>();
			
			while (rs.next())
			{
				Integer backup_time = rs.getInt("backup_time"); 
				result.add(backup_time);
			}
			
		} 
		finally
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
		
		return result;
	}
	
	
	public List<DeviceConfigurations> getDeviceConfigurationbyBackupTime(Integer backup,Integer device_id) throws SQLException
	{
		
		DBConnection session = getSession();	
		List<DeviceConfigurations> results = new ArrayList<DeviceConfigurations>();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceConfigurations.class);
//			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			results = (List<DeviceConfigurations>) query.executeQueryAsObject("select * from "+query.getDBName()+".device_configurations where device_id ="+device_id+" and backup_time="+backup+";");
			log.info("dev_conf_results"+results);
			return results;
		} 
		 catch (SQLException e)
			{
				throw e;
			} finally
			{
				if (session!=null) session.close();
			}
	}	

}
