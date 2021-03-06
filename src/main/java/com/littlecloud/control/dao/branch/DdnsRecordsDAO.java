package com.littlecloud.control.dao.branch;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.branch.criteria.DdnsRecordsCriteria;
import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.DdnsRecords;
import com.littlecloud.utils.CalendarUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

public class DdnsRecordsDAO extends BaseDAO<DdnsRecords, Integer>{
	private static final Logger log = Logger.getLogger(DdnsRecordsDAO.class);
	public DdnsRecordsDAO() throws SQLException {
		super(DdnsRecords.class);
	}

	public DdnsRecordsDAO(boolean readonly) throws SQLException {
		super(DdnsRecords.class, readonly);
	}
	
	public List<DdnsRecords> getAllDdnsRecordsList() throws SQLException{
		return getDdnsRecordsList(null);
	}
	
	public List<DdnsRecords> getDdnsRecordsList(DdnsRecordsCriteria criteria) throws SQLException
	{
		DBConnection session = getSession();
		List<DdnsRecords> results = null;
		try{
			DBQuery query = session.createQuery();
			query.setQueryClass(DdnsRecords.class);
			
			StringBuilder  sbSql= new StringBuilder();
			sbSql.append("select * from " + query.getDBName() + ".ddns_records ");
			
			if (criteria != null){
				boolean needWhereKeyword = true;
				if (criteria.getLastUpdate() != null){
					Calendar calLastUpdate = Calendar.getInstance();
					calLastUpdate.setTime(criteria.getLastUpdate());
					if (needWhereKeyword){
						sbSql.append("where ");
						needWhereKeyword = false;
					} else {
						sbSql.append("and ");
					}
					sbSql.append("last_updated >=" + "'" + CalendarUtils.convertCalendar2MySqlDateString(calLastUpdate) + "' ");
				}
				if (criteria.getDdnsName() != null){
					if (needWhereKeyword){
						sbSql.append("where ");
						needWhereKeyword = false;
					} else {
						sbSql.append("and ");
					}	
					sbSql.append("ddns_name =" + "'" + criteria.getDdnsName() + "' ");
				}
				if (criteria.getIanaId() != null){
					if (needWhereKeyword){
						sbSql.append("where ");
						needWhereKeyword = false;
					} else {
						sbSql.append("and ");
					}	
					sbSql.append("iana_id =" + "'" + criteria.getIanaId() + "' ");
				}
				if (criteria.getSn() != null){
					if (needWhereKeyword){
						sbSql.append("where ");
						needWhereKeyword = false;
					} else {
						sbSql.append("and ");
					}	
					sbSql.append("sn =" + "'" + criteria.getSn() + "' ");
				}
				
				results = (List<DdnsRecords>) query.executeQueryAsObject(sbSql.toString());
				log.debug("DDNS20140402 - query successful, result size: " + results.size());
			}
			return results;
		} catch (SQLException e){
			throw e;
		} finally{
			if (session!=null){
				session.close();
			}
		}
	} // end getAllDdnsRecordsList()

} // end class
