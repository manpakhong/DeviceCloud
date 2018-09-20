package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.DeviceStatus;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

/**
 * Home object for domain model class DeviceStatus.
 * 
 * @see com.littlecloud.control.entity.DeviceStatus
 * @author Hibernate Tools
 */
public class DeviceStatusDAO extends BaseDAO<DeviceStatus, Integer> {

	private static final Logger log = Logger.getLogger(DeviceStatusDAO.class);

	public DeviceStatusDAO() throws SQLException {
		super(DeviceStatus.class);
	}

	public DeviceStatusDAO(String orgId) throws SQLException {
		super(DeviceStatus.class, orgId);
	}
	
	public DeviceStatusDAO(String orgId, boolean readonly) throws SQLException {
		super(DeviceStatus.class, orgId, readonly);
	}
	
	public List<DeviceStatus> getDeviceStatusList(List<Integer> deviceIdList) throws SQLException {
		List<DeviceStatus> deviceStatusList = null;
		// JDBC
		DBConnection session = getSession();
		try
		{
			if (deviceIdList != null && deviceIdList.size() > 0){
				DBQuery query = session.createQuery();
				query.setQueryClass(DeviceStatus.class);
				String strDataBase = query.getDBName();
				
				StringBuilder sql = new StringBuilder();
				sql.append("select * from ");
				sql.append(strDataBase);
				sql.append(".device_status ");
				sql.append("where ");
				sql.append("device_id in ( ");
				int count = 0;
				for (Integer devId: deviceIdList){
					if (count > 0){
						sql.append(",");
					}
					sql.append(devId);
					count++;
				}
				sql.append(")");
				deviceStatusList = (List<DeviceStatus>) query.executeQueryAsObject(sql.toString());
			}
			return deviceStatusList;
		} 
		catch (SQLException e){
			throw e;
		} 
		finally{
			if(session != null){
				session.close();
				session = null;
			}
		}
	}
	
}
