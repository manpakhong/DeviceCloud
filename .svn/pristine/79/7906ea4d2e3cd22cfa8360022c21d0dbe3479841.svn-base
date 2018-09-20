package com.littlecloud.control.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.criteria.CaptivePortalDailyUsagesCriteria;
import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.CaptivePortalDailyUsage;
import com.littlecloud.control.entity.report.ClientMonthlyUsages;
import com.littlecloud.pool.utils.Utils;
import com.littlecloud.utils.CommonUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.util.DBUtil;

public class CaptivePortalDailyUsagesDAO extends BaseDAO<CaptivePortalDailyUsage, Integer>{
	private static final Logger logger = Logger.getLogger(CaptivePortalDailyUsagesDAO.class);
	public CaptivePortalDailyUsagesDAO(String orgId) throws SQLException {
		super(CaptivePortalDailyUsage.class, orgId);
	}
	public CaptivePortalDailyUsagesDAO(String orgId, boolean readonly) throws SQLException {
		super(CaptivePortalDailyUsage.class, orgId, readonly);
	}
	
	public List<CaptivePortalDailyUsage> getCaptivePortalDailyUsageReport(CaptivePortalDailyUsagesCriteria criteria) throws SQLException{

		List<CaptivePortalDailyUsage> result = new ArrayList<CaptivePortalDailyUsage>();
		DBConnection session = null;
		ResultSet rs = null;
		
		try{
			session = getSession();
			DBQuery query = session.createQuery();
			query.setQueryClass(ClientMonthlyUsages.class);
			String dbname = Utils.PARAM_ORGANIZATION_DB_PREFIX + session.getOrgId();
			
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("select * ");
			sbSql.append("from ");
			sbSql.append(dbname + ".captive_portal_daily_usages ");
			
			int criteriaCount = 0;
			
			if (criteria.getCpId() != null){
				if (criteriaCount > 0){
					sbSql.append("and ");
				} else {
					sbSql.append("where ");
				}
				
				sbSql.append("cp_id = " + criteria.getCpId() + " ");
				criteriaCount++;
			}
			
			if (criteria.getBandwidthUsedFrom() != null && criteria.getBandwidthUsedTo() != null){
				if (criteriaCount > 0){
					sbSql.append("and ");
				} else {
					sbSql.append("where ");
				}
				
				sbSql.append("(");
				sbSql.append("bandwidth_used between " + criteria.getBandwidthUsedFrom().intValue() + " "); 
				sbSql.append("and ");
				sbSql.append(criteria.getBandwidthUsedTo().intValue() + ") ");
				criteriaCount++;
			}
		
			if (criteria.getReportDateFrom() != null && criteria.getReportDateTo() != null){
				if (criteriaCount > 0){
					sbSql.append("and ");
				} else {
					sbSql.append("where ");
				}
				
				sbSql.append("(");
				sbSql.append("report_date between '" + CommonUtils.convertDate2MySqlDateString(criteria.getReportDateFrom()) + "' "); 
				sbSql.append("and ");
				sbSql.append("'" + CommonUtils.convertDate2MySqlDateString(criteria.getReportDateTo()) + "') ");
				criteriaCount++;
			}

			if (criteria.getSessionCountFrom() != null && criteria.getSessionCountTo() != null){
				if (criteriaCount > 0){
					sbSql.append("and ");
				} else {
					sbSql.append("where ");
				}
				
				sbSql.append("(");
				sbSql.append("session_count between '" + criteria.getSessionCountFrom() + " "); 
				sbSql.append("and ");
				sbSql.append(criteria.getSessionCountTo() + ") ");
				criteriaCount++;
			}
			
			if (criteria.getTimeUsedFrom() != null && criteria.getTimeUsedTo() != null){
				if (criteriaCount > 0){
					sbSql.append("and ");
				} else {
					sbSql.append("where ");
				}
				
				sbSql.append("(");
				sbSql.append("time_used between '" + criteria.getTimeUsedFrom().intValue() + " "); 
				sbSql.append("and ");
				sbSql.append(criteria.getTimeUsedTo().intValue() + ") ");
				criteriaCount++;
			}
			
			if (criteria.getUnixtimeFrom() != null && criteria.getUnixtimeTo() != null){
				if (criteriaCount > 0){
					sbSql.append("and ");
				} else {
					sbSql.append("where ");
				}
				
				sbSql.append("(");
				sbSql.append("unixtime between '" + criteria.getUnixtimeFrom().intValue() + " "); 
				sbSql.append("and ");
				sbSql.append(criteria.getUnixtimeTo().intValue() + ") ");
				criteriaCount++;
			}

			if (criteria.getStatus() != null){
				if (criteriaCount > 0){
					sbSql.append("and ");
				} else {
					sbSql.append("where ");
				}
				
				Integer status = 0;
				if (criteria.getStatus()){
					status = 1;
				}
				
				sbSql.append("status = " + status + " ");
				criteriaCount++;
			}
			
			sbSql.append(";");
			rs = query.executeQuery(sbSql.toString());
			while (rs.next()){
				CaptivePortalDailyUsage cpdReport = new CaptivePortalDailyUsage();
				cpdReport.setId(CommonUtils.number2Integer((Number) rs.getObject("id")));
				cpdReport.setBandwidthUsed(CommonUtils.number2BigDecimal((Number) rs.getObject("bandwidth_used")));
				cpdReport.setCpId(CommonUtils.number2Integer((Number) rs.getObject("cp_id")));
				cpdReport.setSessionCount(CommonUtils.number2Integer((Number) rs.getObject("session_count")));
				cpdReport.setTimeUsed(CommonUtils.number2BigDecimal((Number) rs.getObject("time_used")));
				cpdReport.setUnixtime(CommonUtils.number2Integer((Number) rs.getObject("unixtime")));
				cpdReport.setReportDate((Date) rs.getObject("report_date"));
				cpdReport.setStatus((Boolean) rs.getObject("status"));
				result.add(cpdReport);
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
	
	public boolean saveCaptivePortalDailyUsageList(List <CaptivePortalDailyUsage> captivePortalDailyUsageList){
		boolean isSave = false;
		try{
			DBUtil dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
			DBConnection batchConnection = dbUtil.getConnection(false, orgId, false);
			if (captivePortalDailyUsageList != null && captivePortalDailyUsageList.size() > 0){
				for (CaptivePortalDailyUsage captivePortalDailyUsage: captivePortalDailyUsageList){
					batchConnection.addBatch(captivePortalDailyUsage);
				}
			}
			batchConnection.commit();
			batchConnection.close();
			if(dbUtil.isSessionStarted()){
				dbUtil.endSession();
			}
			isSave = true;
		}
		catch(Exception e){
			logger.error("CaptivePortalDailyUsagesDAO.save - Exception: ", e);
		}
		finally{
			if(dbUtil.isSessionStarted()) {
				dbUtil.endSession();
			}
		}
		return isSave;
	}
}
