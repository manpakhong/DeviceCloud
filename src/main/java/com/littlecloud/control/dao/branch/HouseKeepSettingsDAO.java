package com.littlecloud.control.dao.branch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.jboss.logging.Logger;
import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.branch.HouseKeepSettings;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;


public class HouseKeepSettingsDAO extends BaseDAO<HouseKeepSettings, Integer>
{
	protected static final Logger log = Logger.getLogger(HouseKeepSettings.class);
	public HouseKeepSettingsDAO() throws SQLException
	{
		super(HouseKeepSettings.class);
		// TODO Auto-generated constructor stub
	}

	public HouseKeepSettingsDAO(boolean readonly) throws SQLException
	{
		super(HouseKeepSettings.class, readonly);
	}
	
	public Integer seleteHouseKeepDayByTableName(String table_name) throws SQLException
	{

		String dbPrefix = "littlecloud_branch_production";
		Integer day = null;
		log.debug("seleting house keep day by table name:"+ table_name);
		DBConnection session = getSession();
		ResultSet rs = null;
		try 
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(HouseKeepSettings.class);
			String dbname = dbPrefix;
			//String dbname = dbPrefix + session.getOrgId();
			String sql = "select housekeep_day from "+dbname+".housekeep_settings where table_name = '"+table_name+"';";
			
			rs = query.executeQuery(sql);
			while( rs.next() )
			{
				day = rs.getInt("housekeep_day");
			}
		} 
		catch (SQLException e)
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
			if(session != null)
			{
				session.close();
				session = null;
			}
		}
		return day;
	}
	
	public List<HouseKeepSettings> getAllDatas() throws SQLException {
		log.debug("getting List of all networks " + this.getClass().getSimpleName());
		//Query query = session.createQuery("select n from Networks n");
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			return (List<HouseKeepSettings>) query.executeQueryAsObject();

		} catch (SQLException e) {
			throw e;
		} finally {
			if (session!=null) session.close();
		}		
	}
}
