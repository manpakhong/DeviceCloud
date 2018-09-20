package com.littlecloud.control.dao.branch;

//import org.hibernate.Query;
import java.sql.SQLException;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.branch.Models;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Criteria;

//import com.littlecloud.control.dao.BaseDAO;

public class ModelsDAO extends BaseDAO<Models, Integer>
{

	protected static final Logger log = Logger.getLogger(ModelsDAO.class);

	public ModelsDAO() throws SQLException
	{
		super(Models.class);
		// TODO Auto-generated constructor stub
	}

	public ModelsDAO(boolean readonly) throws SQLException
	{
		super(Models.class, readonly);
	}

	public Models findByProductCode(String pt_code) throws SQLException
	{
		log.debug("getting models List" + this.getClass().getSimpleName() + "  with product_code: " + pt_code);
		// String hql = "From Models m where m.productCode = '" + pt_code + "'";
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("product_code", Criteria.EQ, pt_code);
			// query.setParameter("ptcode", pt_code);
			Models models = (Models) query.executeQueryAsSingleObject();
			return models;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

	// public Models findByModelId(String mod_id)
	// {
	// log.debug("getting models List" + this.getClass().getSimpleName() + "  with product_code: "+mod_id);
	// //String hql = "From Models m where m.id = '" + mod_id + "'";
	// //Query query = session.createQuery(hql);
	// DBQuery query = session.createQuery();
	// query.setQueryClass(persistentClass);
	// query.addCriteria("
	// Models models = (Models)query.uniqueResult();
	// return models;
	// }
}
