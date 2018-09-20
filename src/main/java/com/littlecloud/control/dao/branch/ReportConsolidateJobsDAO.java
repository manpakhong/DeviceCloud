package com.littlecloud.control.dao.branch;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.SQLException;

import org.jboss.logging.Logger;

import com.littlecloud.control.entity.branch.ReportConsolidateJobs;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.littlecloud.control.dao.jdbc.BaseDAO;


/**
 * Home object for domain model class Devices.
 * 
 * @see com.littlecloud.control.entity.Devices
 * @author Hibernate Tools
 */
public class ReportConsolidateJobsDAO extends BaseDAO<ReportConsolidateJobs, Integer> {

	protected static final Logger log = Logger.getLogger(ReportConsolidateJobsDAO.class);

	public ReportConsolidateJobsDAO() throws SQLException {
		super(ReportConsolidateJobs.class);
	}
	
	public ReportConsolidateJobsDAO(boolean readonly) throws SQLException {
		super(ReportConsolidateJobs.class, readonly);
	}
	
	/*public ReportConsolidateJobs findCurrentOrLaterTimeJob(int currentJobTime){
		log.debug("getting " + this.getClass().getSimpleName() + " instance with job_time "+currentJobTime);
		Query query  = session.createQuery("select rcj from ReportConsolidateJobs rcj where rcj.jobTime >= :jobTime");
		query.setParameter("jobTime", currentJobTime);
		return (ReportConsolidateJobs) query.uniqueResult();
	}
	
	public ReportConsolidateJobs findPreviousJobTime(int currentJobTime){
		log.debug("getting " + this.getClass().getSimpleName() + " instance with job_time "+currentJobTime);
		Query query  = session.createQuery("select rcj from ReportConsolidateJobs rcj where rcj.jobTime < :jobTime order by rcj.jobTime desc");
		query.setParameter("jobTime", currentJobTime);
		query.setMaxResults(1);
		return (ReportConsolidateJobs) query.uniqueResult();
	}*/
	
	public ReportConsolidateJobs findCurrentOrLaterTimeJob (int currentJobTime) throws SQLException{
		log.debug("getting " + this.getClass().getSimpleName() + " instance with job_time "+currentJobTime);
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(ReportConsolidateJobs.class);
			query.addCriteria("job_time", com.peplink.api.db.query.Criteria.GTE, currentJobTime);
			query.setLimit(1);
			return (ReportConsolidateJobs) query.executeQueryAsSingleObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
	public ReportConsolidateJobs findPreviousJobTime(int currentJobTime) throws SQLException{
		log.debug("getting " + this.getClass().getSimpleName() + " instance with job_time "+currentJobTime);
		DBConnection session = getSession();
		try {
			DBQuery query = session.createQuery();
			query.setQueryClass(ReportConsolidateJobs.class);
			query.addCriteria("job_time", com.peplink.api.db.query.Criteria.LT, currentJobTime);
			query.addOrderBy("job_time desc");		
			query.setLimit(1);
			return (ReportConsolidateJobs) query.executeQueryAsSingleObject();
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session!=null) session.close();
		}
	}
	
}
