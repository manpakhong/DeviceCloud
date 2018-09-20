package com.littlecloud.control.dao;

import java.sql.SQLException;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.FirmwareVersions;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Condition;
import com.peplink.api.db.query.Criteria;

public class FirmwareVersionsDAO extends BaseDAO<FirmwareVersions, Integer>
{
	private static final Logger log = Logger.getLogger(FirmwareVersions.class);
	
	public FirmwareVersionsDAO(String orgId) throws SQLException 
	{
		super(FirmwareVersions.class, orgId);
	}
	
	public FirmwareVersionsDAO(String orgId, boolean readonly) throws SQLException 
	{
		super(FirmwareVersions.class, orgId, readonly);
	}


	
	public List<FirmwareVersions> getFwVerByProductId(Integer product_id) throws SQLException
	{
		List<FirmwareVersions> versionList = null;
		DBConnection session = super.getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(FirmwareVersions.class);

			query.addCriteria("product_id", Criteria.EQ, product_id);
			versionList = (List<FirmwareVersions>)query.executeQueryAsObject();
		}
		catch(Exception e)
		{
			throw e;
			
		}
		finally
		{
			if (session!=null) 
				session.close();
		}
		
		return versionList;
	}		
	
	public FirmwareVersions getFwVerByDeviceIdAndProductId(Integer device_id, Integer product_id) throws SQLException
	{
		FirmwareVersions version = null;
		DBConnection session = super.getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(FirmwareVersions.class);

			query.addCriteria("device_id", Criteria.EQ, device_id );
			query.addCondition(Condition.AND);

			query.addCriteria("product_id", Criteria.EQ, product_id);
			version = (FirmwareVersions)query.executeQueryAsSingleObject();
		}
		catch(Exception e)
		{
			throw e;
			
		}
		finally
		{
			if (session!=null) 
				session.close();
		}
		
		return version;
	}	
	
	public FirmwareVersions getFwVerByNetworkIdAndProductId(Integer network_id, Integer product_id) throws SQLException
	{
		FirmwareVersions version = null;
		DBConnection session = super.getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(FirmwareVersions.class);
			if (network_id != null){
				query.addCriteria("network_id", Criteria.EQ, network_id );
				query.addCondition(Condition.AND);
			} // end if (network_id != null)
			query.addCriteria("product_id", Criteria.EQ, product_id);
			version = (FirmwareVersions)query.executeQueryAsSingleObject();
		}
		catch(Exception e)
		{
			throw e;
			
		}
		finally
		{
			if (session!=null) 
				session.close();
		}
		
		return version;
	}

	public FirmwareVersions getFwVerByNetworkIdAndProductIdAndVersion(Integer network_id, Integer product_id, String fw_ver) throws SQLException
	{
		FirmwareVersions version = null;
		DBConnection session = super.getSession();
		
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(FirmwareVersions.class);
			query.addCriteria("network_id", Criteria.EQ, network_id );
			query.addCondition(Condition.AND);
			query.addCriteria("product_id", Criteria.EQ, product_id);
			query.addCondition(Condition.AND);
			query.addCriteria("version", Criteria.EQ, fw_ver);
			version = (FirmwareVersions)query.executeQueryAsSingleObject();
		}
		catch(Exception e)
		{
			throw e;
			
		}
		finally
		{
			if (session!=null) 
				session.close();
		}
		
		return version;
	} 
	
}
