package com.littlecloud.control.dao.branch;

import java.sql.SQLException;
import java.util.List;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.branch.Firmwares;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Criteria;

public class FirmwaresDAO extends BaseDAO<Firmwares,Integer>
{

	public FirmwaresDAO() throws SQLException 
	{
		super(Firmwares.class);
	}
	
	public FirmwaresDAO(boolean readonly) throws SQLException 
	{
		super(Firmwares.class, readonly);
	}

	public List<Firmwares> getFirmwaresListByProductId(Integer product_id) throws SQLException
	{
		List<Firmwares> result = null;
		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("product_id", Criteria.EQ, product_id);
			query.addCondition(com.peplink.api.db.query.Condition.AND);
			query.addCriteria("active", Criteria.EQ, 1);
			result = (List<Firmwares>)query.executeQueryAsObject();
		}
		catch (Exception e)
		{
			throw e;
		} 
		finally
		{
			if (session!=null) 
				session.close();
		}
		
		return result;
	}
	
}
