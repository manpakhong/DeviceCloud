package com.littlecloud.control.dao.branch;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
//import com.littlecloud.control.dao.BaseDAO;
import com.littlecloud.control.entity.branch.OuiInfos;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Criteria;

/**
 * Home object for domain model class Devices.
 * 
 * @see com.littlecloud.control.entity.Devices
 * @author Hibernate Tools
 */
public class OuiInfosDAO extends BaseDAO<OuiInfos, Long> {

	protected static final Logger log = Logger.getLogger(OuiInfosDAO.class);

	public OuiInfosDAO() throws SQLException {
		super(OuiInfos.class);
	}

	public OuiInfosDAO(boolean readonly) throws SQLException {
		super(OuiInfos.class, readonly);
	}

	public String findManufacturerByMac(String mac) throws SQLException
	{
		if (mac==null || mac.length()<8)
			return null;
		
		String macHex = mac.substring(0, 8).replaceAll(":", "") + "000000";
		Long macLong = Long.parseLong(macHex, 16);
		OuiInfos ouiInfo = findById(macLong);
		if (ouiInfo != null)
			return ouiInfo.getOrganization();
		else
			return null;
	}
	
	public HashMap<Long, String> getOuiInfosMap() throws SQLException
	{
		HashMap<Long, String> map = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		try
		{
			DBQuery query = session.createQuery();
			String sql = "select * from " + query.getDBName() + ".oui_infos";
			rs = query.executeQuery(sql);
			if( rs != null )
			{
				map = new HashMap<Long,String>();
				while(rs.next())
				{
					map.put(rs.getLong("oui"), rs.getString("organization"));
				}
			}
		}
		catch(Exception e)
		{
			log.error("Get oui info map error - " + e , e);
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
		return map;
	}
	
	public static void main(String args[]) throws SQLException
	{
		//String mac = "10:56:CA:03:BB:0E";
		String mac = "";
		OuiInfosDAO dao = new OuiInfosDAO();
		System.out.println(dao.getOuiInfosMap().get("167772160"));
	}
}
