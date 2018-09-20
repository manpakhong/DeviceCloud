package com.littlecloud.control.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.Columns;
import com.littlecloud.pool.utils.Utils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.util.DBUtil;

public class InformationSchemaDAO extends BaseDAO<Columns,String>
{

	public InformationSchemaDAO() throws SQLException {
		super(Columns.class);
	}
	
	private static Logger logger = Logger.getLogger(InformationSchemaDAO.class);
	
	public List<Columns> getBranchTablesWithSn() throws SQLException
	{
		List<Columns> cols = null;
		Columns col = null;
		DBConnection session = null;
		ResultSet rs = null;
		try
		{
			session = getSession();
			DBQuery query = session.createQuery();
			String sql = "select table_name, column_name from information_schema.columns where table_schema = 'littlecloud_branch_production' and column_name = 'SN';";
			rs = query.executeQuery(sql);
			if( rs != null )
			{
				cols = new ArrayList<Columns>();
				while(rs.next())
				{
					col = new Columns();
					col.setTable_name(rs.getString("table_name"));
					col.setColumn_name(rs.getString("column_name"));
					cols.add(col);
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Get branch table with col sn err -"+e,e);
		}
		finally
		{
			if(session != null)
			{
				session.close();
				session = null;
			}
			
			if( rs != null )
			{
				rs.close();
				rs = null;
			}
		}
		
		return cols;
	}
	
	public List<Columns> getOrganizationTableWithDesignatedCols(String orgId) throws SQLException
	{
		List<Columns> cols = null;
		DBConnection session = null;
		ResultSet rs = null;
		Columns col = null;
		try
		{
			session = getSession();
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + orgId;
			String sql = "select table_name, column_name from information_schema.columns where table_schema = '"+ dbname +"' and (column_name = 'DEVICE_ID' or column_name = 'SN');";
			
			rs = query.executeQuery(sql);
			if( rs != null )
			{
				cols = new ArrayList<Columns>();
				while(rs.next())
				{
					col = new Columns();
					col.setTable_name(rs.getString("table_name"));
					col.setColumn_name(rs.getString("column_name"));
					cols.add(col);
				}
			}
		}
		catch(SQLException e)
		{
			logger.error("Get organization table with col sn and device_id err -"+e,e);
		}
		finally
		{
			if( session != null )
			{
				session.close();
				session = null;
			}
			
			if( rs != null )
			{
				rs.close();
				rs = null;
			}
		}
		return cols;
	}
	
	public void deleteData(String orgId, String table_name, String column_name, String value) throws SQLException
	{
		DBConnection session = null;
		try
		{
			session = getSession();
			DBQuery query = session.createQuery();
			String sql = null;
			if( orgId != null )
			{
				String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + orgId;
				sql = "delete from " + dbname + "." + table_name + " where " + column_name + " = '" + value + "'";
			}
			else
			{
				sql = "delete from littlecloud_branch_product."+table_name+" where sn = '" + value + "'";
			}
			query.executeUpdate(sql);
		}
		catch(SQLException e)
		{
			logger.error("Delete purge device data error - " + e , e);
		}
		finally
		{
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
	}
	
	public void deleteBranchData(String table, String sns) throws SQLException
	{
		DBConnection session = null;
		try
		{
			session = getSession();
			DBQuery query = session.createQuery();
			String sql = "delete from littlecloud_branch_product."+table+" where sn in ("+sns+")";
			logger.info("delete branch : " + sql);
			query.executeUpdate(sql);
		}
		catch(SQLException e)
		{
			logger.error("Delete purge device data error - " + e , e);
		}
		finally
		{
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
	}
	
	public void deleteOrganizationDataBySn(String organization, String table, String sns) throws SQLException
	{
		DBConnection session = null;
		try
		{
			session = getSession();
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + organization;
			String sql = "delete from "+dbname+"."+table+" where sn in ("+sns+")";
			logger.info("delete org sn:"+sql);
			query.executeUpdate(sql);
		}
		catch(SQLException e)
		{
			logger.error("Delete purge device data error - " + e , e);
		}
		finally
		{
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
	}
	
	public void deleteOrganizationDataByDevIds(String orgId, String table, String devIds) throws SQLException
	{
		DBConnection session = null;
		try
		{
			session = getSession();
			DBQuery query = session.createQuery();
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + orgId;
			String sql = "delete from "+dbname+"."+table+" where device_id in ("+devIds+")";
			logger.info("delete org dev_id:"+sql);
			query.executeUpdate(sql);
		}
		catch(SQLException e)
		{
			logger.error("Delete purge device data error - " + e , e);
		}
		finally
		{
			if( session != null )
			{
				session.close();
				session = null;
			}
		}
	}
	
	public List<String> getAllOrganizations() throws SQLException
	{
		List<String> result = null;
		DBConnection session = getSession();
		ResultSet rs = null;
		
		try
		{
			DBQuery query = session.createQuery();
			String sql = "select distinct schema_name from information_schema.schemata where schema_name like 'peplink_organization%';";
			rs = query.executeQuery(sql);
			if(rs != null)
			{
				result = new ArrayList<String>();
				while(rs.next())
				{
					String org = rs.getString("schema_name");
					result.add(org.replace("peplink_organization_", ""));
				}
			}
		}
		catch(Exception e)
		{
			logger.error("get all organization error " + e,e);
		}
		finally
		{
			if(rs != null)
				rs.close();
			if(session != null)
				session.close();
		}
		return result;
	}
	
	public static void main(String[] args) throws SQLException
	{
		List<Columns> a = new InformationSchemaDAO().getOrganizationTableWithDesignatedCols("riMA5x");
		for( Columns c : a )
		{
			System.out.println(c.getTable_name());
		}
		System.out.println("end");
	}
}
