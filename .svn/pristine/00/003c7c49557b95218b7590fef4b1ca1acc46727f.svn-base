package com.littlecloud.control.dao.branch;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.Devices;
//import com.littlecloud.control.dao.BaseDAO;
import com.littlecloud.control.entity.branch.Products;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.query.Criteria;

/**
 * Home object for domain model class Devices.
 * 
 * @see com.littlecloud.control.entity.Devices
 * @author Hibernate Tools
 */
public class ProductsDAO extends BaseDAO<Products, Integer> {

	protected static final Logger log = Logger.getLogger(ProductsDAO.class);

	public ProductsDAO() throws SQLException {
		super(Products.class);
	}

	public ProductsDAO(boolean readonly) throws SQLException {
		super(Products.class, readonly);
	}

	public List<Integer> getProductHubSupportIdList(boolean bHubSupport) throws SQLException
	{
		log.debug("getting Products List" + this.getClass().getSimpleName() + "  with bHubSupport: " + bHubSupport);
		DBConnection session = getSession();
		ResultSet rs = null;
		try {		
			DBQuery query = session.createQuery();
			
			StringBuilder sql = new StringBuilder();
			sql.append("select distinct id from ");
			sql.append(query.getDBName());
			sql.append(".");
			sql.append("products ");
			sql.append("where hub_support = "+bHubSupport);
			log.debug("getProductHubSupportIdList sql="+sql);

			List<Integer> result = new ArrayList<Integer>();
			rs = query.executeQuery(sql.toString());
			while(rs.next())
			{
				result.add(rs.getInt("id"));
			}
						
			return result;
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
	
	public Products getProductsById(Integer id) throws SQLException {
		log.debug("getting Products by id: " + this.getClass().getSimpleName() + "  with id: " + id);
		// "From Products p where p.mv = :mv"
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("id", Criteria.EQ, id);
			query.setDistinct(true);
			return (Products) query.executeQueryAsSingleObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	} // end public Products getProductsById(Integer id)
	
	public Products findByMv(String mv) throws SQLException {
		log.debug("getting Products List" + this.getClass().getSimpleName() + "  with mv: " + mv);
		// "From Products p where p.mv = :mv"
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			query.addCriteria("mv", Criteria.EQ, mv);
			query.setDistinct(true);
			return (Products) query.executeQueryAsSingleObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}

	public List<Products> getAllProducts() throws SQLException {
		log.debug("getting all products list " + this.getClass().getSimpleName());
		// "select p from Products p"
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			return (List<Products>) query.executeQueryAsObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public HashMap<Integer, Products> getAllProductsMap() throws SQLException
	{
		log.info("getting all device map");
		
		HashMap<Integer, Products> prodMap = new HashMap<Integer, Products>();
		List<Products> prodList = getAllProducts();

		log.debug("query successful, result size: " + prodList.size());			
		if (prodList!=null)
		{
			for (Products p : prodList)
			{
				prodMap.put(p.getId(), p);
			}
		}		
		return prodMap;
	}
}
