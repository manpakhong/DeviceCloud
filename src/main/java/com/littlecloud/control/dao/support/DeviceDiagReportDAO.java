package com.littlecloud.control.dao.support;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.dao.jdbc.BaseDaoInstances.InstanceType;
import com.littlecloud.control.dao.support.criteria.DeviceDiagReportCriteria;
import com.littlecloud.control.entity.support.DeviceDiagReport;
import com.littlecloud.utils.CalendarUtils;
import com.littlecloud.utils.CommonUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

public class DeviceDiagReportDAO extends BaseDAO<DeviceDiagReport, Integer>{
	private static final Logger log = Logger.getLogger(DeviceDiagReportDAO.class);
	
	public DeviceDiagReportDAO() throws SQLException {
		super(DeviceDiagReport.class, InstanceType.SUPPORTDB.getValue(), "diagreports");
	} // end constructor

	
	public List<DeviceDiagReport> getDeviceDiagReport(DeviceDiagReportCriteria criteria) throws SQLException{
		if (criteria == null){
			log.warnf("DeviceDiagReportDAO - getDeviceDiagReport, criteria is null!");
			return null;
		} else {
			if ((criteria.getCreateDateFrom() != null && criteria.getCreateDateTo() == null) || (criteria.getCreateDateTo() != null && criteria.getCreateDateFrom() == null)){
				log.warnf("DeviceDiagReportDAO - getDeviceDiagReport, criteria - createDateFrom and createDateTo must be in pair!");
				return null;
			}
		}
		
		List<DeviceDiagReport> result = new ArrayList<DeviceDiagReport>();
		DBConnection session = null;
		ResultSet rs = null;
		
		try{
			session = getSession();
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceDiagReport.class);
//			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			
			StringBuilder sbSql = new StringBuilder();
			if (criteria.isReportContentIncluded()){
				sbSql.append("select * ");
			} else {
				sbSql.append("select ");
				sbSql.append("id, ");
				sbSql.append("org_id, ");
				sbSql.append("iana_id, ");
				sbSql.append("sn, ");
				sbSql.append("unixtime, ");
				sbSql.append("created_date ");
			}
			
			sbSql.append("from ");
			sbSql.append(session.getDBName() + ".device_diag_reports ");
			
			int criteriaCount = 0;
			
			if (criteria.getId() != null){
				if (criteriaCount > 0){
					sbSql.append("and ");
				} else {
					sbSql.append("where ");
				}
				
				sbSql.append("id = " + criteria.getId() + " ");
				criteriaCount++;
			}
			if (criteria.getIanaId() != null){
				if (criteriaCount > 0){
					sbSql.append("and ");
				} else {
					sbSql.append("where ");
				}
				
				sbSql.append("iana_id = " + criteria.getIanaId() + " ");
				criteriaCount++;
			}
			if (criteria.getOrgId() != null){
				if (criteriaCount > 0){
					sbSql.append("and ");
				} else {
					sbSql.append("where ");
				}
				
				sbSql.append("org_id = '" + criteria.getOrgId() + "' ");
				criteriaCount++;
			}
			if (criteria.getSn() != null){
				if (criteriaCount > 0){
					sbSql.append("and ");
				} else {
					sbSql.append("where ");
				}
				
				sbSql.append("sn = '" + criteria.getSn() + "' ");
				criteriaCount++;
			}
			if (criteria.getUnixtimeFrom() != null && criteria.getUnixtimeTo() != null){
				if (criteriaCount > 0){
					sbSql.append("and ");
				} else {
					sbSql.append("where ");
				}
				
				sbSql.append("(");
				sbSql.append("unixtime between " + criteria.getUnixtimeFrom().intValue() + " "); 
				sbSql.append("and ");
				sbSql.append(criteria.getUnixtimeTo().intValue() + ") ");
				criteriaCount++;
			}
			
			
			if (criteria.getCreateDateFrom() != null && criteria.getCreateDateTo() != null){
				if (criteriaCount > 0){
					sbSql.append("and ");
				} else {
					sbSql.append("where ");
				}
				
				sbSql.append("(");
				sbSql.append("created_date between '" + CommonUtils.convertDate2MySqlDateString(criteria.getCreateDateFrom()) + "' "); 
				sbSql.append("and ");
				sbSql.append("'" + CommonUtils.convertDate2MySqlDateString(criteria.getCreateDateTo()) + "') ");
				criteriaCount++;
			}
			sbSql.append("order by ");
			sbSql.append("created_date asc ");
			sbSql.append(";");
			rs = query.executeQuery(sbSql.toString());
			
			while (rs.next()){
				DeviceDiagReport deviceDiagReport = new DeviceDiagReport();
				deviceDiagReport.setId(CommonUtils.number2Integer((Number) rs.getObject("id")));
				deviceDiagReport.setOrgId((String) rs.getObject("org_id")); 
				deviceDiagReport.setIanaId(CommonUtils.number2Integer((Number) rs.getObject("iana_id")));
				deviceDiagReport.setSn((String) rs.getObject("sn"));
				deviceDiagReport.setUnixtime(CommonUtils.number2Integer((Number) rs.getObject("unixtime")));
				deviceDiagReport.setCreatedDate((Date) rs.getObject("created_date"));
				if (criteria.isReportContentIncluded()){
					deviceDiagReport.setReportContent((byte[]) rs.getObject("report_content"));
				}
				result.add(deviceDiagReport);
			}			
		}
		catch( Exception e ){
			throw e;
		}
		finally{
			if(rs != null){
				rs.close();
				rs = null;				
			}
			if( session != null ){
				session.close();
				session = null;
			}
		}
		return result;				
	} // end getDeviceDiagReport
	
	public void delete(DeviceDiagReport[] DeviceDiagReportArray) throws SQLException {
		log.debug("deleting " + this.persistentClass.getSimpleName() + " instance");
		super.delete(DeviceDiagReportArray);
		
	}
	public void delete(DeviceDiagReport deviceDiagReport) throws SQLException {
		log.debug("deleting " + this.persistentClass.getSimpleName() + " instance");
		super.delete(deviceDiagReport);
	}
	
	public void save(DeviceDiagReport deviceDiagReport) throws SQLException {
		log.debug("saving " + this.persistentClass.getSimpleName() + " instance");
		super.save(deviceDiagReport);
	}
	
	public void update(DeviceDiagReport deviceDiagReport) throws SQLException {
		log.debug("updating " + this.persistentClass.getSimpleName() + " instance");
		super.update(deviceDiagReport);
	}
	
	public void saveOrUpdate(DeviceDiagReport deviceDiagReport) throws SQLException {
		log.debug("saveOrUpdate " + this.persistentClass.getSimpleName() + " instance");
		super.saveOrUpdate(deviceDiagReport);
	}	
	
	
	public List<Date> getNConsequentDateList(String orgId, Integer ianaId, String sn, Date date, Integer noOfConsequentDate)  throws SQLException{
		if (orgId == null || ianaId == null || sn == null || date == null || noOfConsequentDate == null){
			log.warnf("DeviceDiagReportDAO - getNConsequentDateList - missing parameters!!!!! orgId: %s, ianaId: %s, sn: %s, date: %s, noOfConsequentDate: %s ", orgId, ianaId, sn, date, noOfConsequentDate);
			return null;
		} 
		
		List<Date> result = new ArrayList<Date>();
		DBConnection session = null;
		ResultSet rs = null;
		
		try{
			Calendar cal = CalendarUtils.getMaxUtcCalendarToday();
			cal.setTime(date);
			
			session = getSession();
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceDiagReport.class);
			
			StringBuilder sbSql = new StringBuilder();

			sbSql.append("select ");
			sbSql.append("distinct ");
			sbSql.append("date(created_date) as date_list "); 			
			sbSql.append("from ");
			sbSql.append(session.getDBName() + ".device_diag_reports ");
			sbSql.append("where ");
			sbSql.append("date(created_date) <= date('" + CalendarUtils.convertCalendar2MySqlDateString(cal) +"') ");
			sbSql.append("order by ");
			sbSql.append("date(created_date) ");
			sbSql.append("desc ");
			sbSql.append("limit " + noOfConsequentDate);
			rs = query.executeQuery(sbSql.toString());
			
			while (rs.next()){
				Date dateElement = ((Date) rs.getObject("date_list"));
				result.add(dateElement);
			}			
		}
		catch( Exception e ){
			throw e;
		}
		finally{
			if(rs != null){
				rs.close();
				rs = null;				
			}
			if( session != null ){
				session.close();
				session = null;
			}
		}
		return result;			
	}

	public int deleteNConsequentDaysBeforeDate(String orgId, Integer ianaId, String sn, Calendar cal) throws SQLException{		
		if (orgId == null || ianaId == null || sn == null || cal == null){
			return 0;
		}
		DBConnection session = null;
		int numberOfDeleted = 0;
		try{
			session = getSession();
			DBQuery query = session.createQuery();
			query.setQueryClass(DeviceDiagReport.class);

			StringBuilder sbSql = new StringBuilder();
			sbSql.append("delete ");
			sbSql.append("from ");
			sbSql.append(session.getDBName() + ".device_diag_reports ");
			sbSql.append("where ");
			sbSql.append("org_id = '" + orgId + "' ");
			sbSql.append("and ");
			sbSql.append("iana_id = " + ianaId + " ");
			sbSql.append("and ");
			sbSql.append("sn = '" + sn + "' ");
			sbSql.append("and ");
			sbSql.append("date(created_date) < date('" + CalendarUtils.convertCalendar2MySqlDateString(cal) + "') ");
			sbSql.append(";");
			numberOfDeleted = query.executeUpdate(sbSql.toString());
		}
		catch( SQLException e ){
			throw e;
		}
		finally{
			try{
				if( session != null ){
					session.close();
					session = null;
				}
			} catch (Exception e){
				log.error("DeviceDiagReportDAO.deleteNRecordsWithinADay(), finally exception - ", e);
			}
		}
		return numberOfDeleted;
	}
	
}
