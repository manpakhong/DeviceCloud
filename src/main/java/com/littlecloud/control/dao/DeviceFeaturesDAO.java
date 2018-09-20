package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.jboss.logging.Logger;

import com.littlecloud.control.entity.DeviceFeatures;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Tags;
import com.littlecloud.control.entity.report.DeviceLocations;
import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

/**
 * Home object for domain model class Devices.
 * 
 * @see com.littlecloud.control.entity.Devices
 * @author Hibernate Tools
 */
public class DeviceFeaturesDAO extends BaseDAO<DeviceFeatures, Integer> {

	private static final Logger log = Logger.getLogger(DeviceFeaturesDAO.class);

	public DeviceFeaturesDAO() throws SQLException {
		super(DeviceFeatures.class);
	}

	public DeviceFeaturesDAO(String orgId) throws SQLException {
		super(DeviceFeatures.class, orgId);
	}

	public DeviceFeaturesDAO(String orgId, boolean readonly) throws SQLException {
		super(DeviceFeatures.class, orgId, readonly);
	}

	public HashMap<Integer, DeviceFeatures> getFromDevLst(List<Devices> devLst) throws SQLException
	{
		List<Integer> devIdLst = DeviceUtils.getDevIdLstFromDevLst(devLst);
		return getFromDevIdLst(devIdLst);
	}
	
	public HashMap<Integer, DeviceFeatures> getFromDevIdLst(List<Integer> devIdLst) throws SQLException
	{
		log.info("getting device feature list from device id list " + devIdLst.toString());

		HashMap<Integer, DeviceFeatures> dfMap = new HashMap<Integer, DeviceFeatures>();
		List<DeviceFeatures> dfList = new ArrayList<DeviceFeatures>();
		if (devIdLst==null || devIdLst.size()==0)
			return dfMap;
				
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			String strSQL = "select * from " + query.getDBName() + ".device_features where device_id in (" + intListToParam(devIdLst) + " ) ";			

			dfList = query.executeQueryAsObject(strSQL);
			log.debug("query successful, result size: " + dfList.size());
			
			for (DeviceFeatures f : dfList)
			{
				dfMap.put(f.getDeviceId(), f);
			}
			
			return dfMap;

		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public boolean getGpsSupportByDeviceId(Integer devId) throws SQLException
	{
		
			DBConnection session = getSession();
			try
			{
				DBQuery query = session.createQuery();
				query.setQueryClass(persistentClass);
				
				String strSQL = "select gps_support from " + query.getDBName() + ".device_features where device_id = "+devId;
				Boolean support = (Boolean)query.executeQueryAsSingleObject(strSQL);
				
				return support;
			} 
			catch (SQLException e)
			{
				throw e;
			} 
			finally
			{
				if (session!=null) session.close();
			}
	}
	
	/**
	 * @return
	 * @throws SQLException
	 */
	public HashMap<Integer,Boolean> getGpsFeatureMap() throws SQLException
	{
		HashMap<Integer, Boolean> map = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		
		try
		{
			DBQuery query = session.createQuery();
			String sql = "select device_id, gps_support from " + query.getDBName() + ".device_features";
			rs = query.executeQuery(sql);
			if(rs != null)
			{
				map = new HashMap<Integer,Boolean>();
				while(rs.next())
					map.put(rs.getInt("device_id"), rs.getBoolean("gps_support"));
			}
		}
		catch(Exception e)
		{
			log.error("Get device features map error -"+e,e);
		}
		finally
		{
			if(rs != null)
				rs.close();
			if(session != null)
				session.close();
		}
		
		return map;
	}
}
