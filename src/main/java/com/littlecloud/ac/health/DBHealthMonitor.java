package com.littlecloud.ac.health;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import com.google.gson.reflect.TypeToken;
import com.littlecloud.ac.handler.BranchServerRedirectionHandler;
import com.littlecloud.ac.handler.RootServerRedirectionHandler;
import com.littlecloud.ac.health.info.DBInfo;
import com.littlecloud.ac.root.health.DBHealthMonitorRoot;
import com.littlecloud.ac.util.RootBranchRedirectManager;
import com.littlecloud.control.dao.branch.HealthCheckDAO;
import com.littlecloud.control.dao.jdbc.BaseDaoInstances.InstanceType;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.utils.PropertyService;
import com.peplink.api.db.util.DBUtil;

public class DBHealthMonitor implements HealthMonitor<CopyOnWriteArrayList<DBInfo>> {

	private static final Logger log = Logger.getLogger(DBHealthMonitor.class);

	private static final PropertyService<DBHealthMonitor> ps = new PropertyService<DBHealthMonitor>(DBHealthMonitor.class);
	private static final Integer monitor_db_interval_min = ps.getInteger("monitor_db_interval_min");
	private static final Integer health_db_select_max_retry = ps.getInteger("health_db_select_max_retry");
	private static final Integer health_db_inpool_min_size = ps.getInteger("health_db_inpool_min_size");
	private static final Integer health_db_info_update_time = ps.getInteger("health_db_info_update_time") * 60;
	private static final Integer health_db_info_max_connection = initDbMaxConnection();

	public Date getServerTime() {
		HealthCheckDAO healthCheckDAO = null;		
		try
		{
			healthCheckDAO = new HealthCheckDAO();
			return healthCheckDAO.getServerTime();
		}
		catch(Exception e)
		{
			log.error("getServerTime error - " + e, e);
			return null;
		}
	}
	
	private static Integer initDbMaxConnection() {
		if (RootBranchRedirectManager.isBranchServerMode())
			return DBHealthMonitor.getMaxDBConnection();
		else
			return DBHealthMonitorRoot.getMaxDBConnection();
	}

	public String getServerTimezone() {
		HealthCheckDAO healthCheckDAO = null;		
		try
		{
			healthCheckDAO = new HealthCheckDAO();
			return healthCheckDAO.getServerTimezone();
		}
		catch(Exception e)
		{
			log.error("getServerTimezone error - " + e, e);
			return null;
		}
	}
	
	public static Integer getMaxDBConnection()
	{
		Integer result = 150;
		Properties props = new Properties();
		File f = new File(System.getProperty("db.properties"));
		if( f.exists() )
		{
			try
			{
				FileInputStream fis = new FileInputStream(f);
				props.load(fis);
				fis.close();		
				result = Integer.valueOf((String)props.get("DB_MAX_CONNECTION"));
			}
			catch(Exception e)
			{
				log.error("Get max db connection error", e);
			}
		}
		return result;
	}
	
	public static Integer getMonitorDbIntervalMin() {
		return monitor_db_interval_min;
	}

	private static CopyOnWriteArrayList<DBInfo> infoLst = new CopyOnWriteArrayList<DBInfo>();

	@Override
	public CopyOnWriteArrayList<DBInfo> getInfo() {
		return infoLst;
	}

	@Override
	public void collectInfo() {
		collectDBInfo();
//		try {			
//			Type listType = new TypeToken<CopyOnWriteArrayList<DBInfo>>() {}.getType();
//			infoLst = JsonUtils.<CopyOnWriteArrayList<DBInfo>>fromJson(DBUtil.getInstance().dumpDBPoolJson(), listType);
//			log.debug("infoLst : " + infoLst + " , json : " + DBUtil.getInstance().dumpDBPoolJson());
//			if( infoLst != null )
//			{
//				for (DBInfo info:infoLst)
//				{
//					info.setTimestamp(DateUtils.getUnixtime());
//				}
//			}
//		} catch (SQLException e) {
//			log.error("SQLException DBUtil.getInstance().dumpDBPoolJson() - ",e);
//		}		
	}
	
	public String collectDBInfo() {
		String dbPoolJson = null;
		DBUtil dbUtil = null;
		try {			
			Type listType = new TypeToken<CopyOnWriteArrayList<DBInfo>>() {}.getType();			
			//if(log.isDebugEnabled()) log.debug("infoLst : " + infoLst + " , json : " + DBUtil.getInstance().dumpDBPoolJson());
			
			dbUtil = RootBranchRedirectManager.getDbUtilInstance();
			dbPoolJson = dbUtil.dumpDBPoolJson();
			
			infoLst = JsonUtils.<CopyOnWriteArrayList<DBInfo>>fromJson(dbPoolJson, listType);
			if( infoLst != null )
			{
				for (DBInfo info:infoLst)
				{
					info.setName(StringUtils.join(info.getName()," (",dbUtil.getPropertyName(),")"));
					info.setTimestamp(DateUtils.getUnixtime());
				}
			}
			else
			{
				log.errorf("dbPoolJson infoLst is null! (%s)", dbPoolJson);
			}
		} catch (Exception e) {
			log.error("Exception DBUtil.getInstance().dumpDBPoolJson() - ",e);
		}		
		return dbPoolJson;
	}

	@Override
	public boolean isHealthy() {

		boolean health = false;
		int i = 0;
		if (infoLst!=null)
		{
			for( DBInfo info : infoLst )
			{
				if( DateUtils.getUnixtime() - info.getTimestamp() > health_db_info_update_time || 
						(info.getInpool() < health_db_inpool_min_size && info.getMaxconn() == health_db_info_max_connection) )
				{
					log.warnf("Unhealthy! DateUtils.getUnixtime() %d - info.getTimestamp() %d > health_db_info_update_time %d "
							+ "|| info.getInpool() %d < health_db_inpool_min_size %d && info.getMaxconn() %d == health_db_info_max_connection %d" , 
							DateUtils.getUnixtime(), info.getTimestamp(), health_db_info_update_time, 
							info.getInpool(), health_db_inpool_min_size, info.getMaxconn(), health_db_info_max_connection);
					health = false;
					break;
				}
				i++;
			}
			if( i == infoLst.size() )
				health = true;
		}
		else
		{
			log.warnf("Unhealthy! infoLst is %s", infoLst);
		}
		return health;
	}
	
	public static void main(String args[])
	{
		try {
			System.out.printf("origin=%s",DBUtil.getInstance().dumpDBPoolJson());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		DBHealthMonitor dbmon = new DBHealthMonitor();
		dbmon.collectInfo();
		System.out.printf("dbmon.getInfo()=%s\n", dbmon.getInfo());
		
		for (DBInfo info:dbmon.getInfo())
		{
			System.out.printf("info=%s\n", info);
		}	
		
		System.out.printf("dbmon date =%s\n", dbmon.getServerTime());
	}
	
	@Override
	public String toJson() {
		return JsonUtils.toJson(getInfo());
	}	
	
	public String toHtml(List<DBInfo> dbInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("------------------DB INFO-----------------");
		sb.append("<br/>");
		log.debug("dbHealthMonitor.isHealthy");
		if(this.isHealthy())
		{
			sb.append("Status : ");
			sb.append("<font color=\"green\">");
			sb.append("Healthy");
			sb.append("</font>");
		}
		else
		{
			sb.append("Status : ");
			sb.append("<font color=\"red\">");
			sb.append("Unhealthy\n");
			sb.append("</font>");
		}		
		sb.append("<br/>");
		sb.append("Server clock: ");
		sb.append(this.getServerTime());
		sb.append("<br/>");
		sb.append("Server timezone: ");
		sb.append(this.getServerTimezone());
		sb.append("<br/>");
		sb.append("<br/>");
		if( dbInfo != null )
		{
			for( DBInfo info : dbInfo )
			{
				sb.append("db name : ");
				sb.append(info.getName());
				sb.append("<br/>");
				sb.append("max connection : ");
				sb.append(info.getMaxconn());
				sb.append("<br/>");
				sb.append("init connection : ");
				sb.append(info.getInit());
				sb.append("<br/>");
				sb.append("inpool name : ");
				sb.append(info.getInpool());
				sb.append("<br/>");
				sb.append("current connection : ");
				sb.append(info.getCurrent());
				sb.append("<br/>");
				sb.append("top connection : ");
				sb.append(info.getTop());
				sb.append("<br/>");
				sb.append("timestamp : ");
				sb.append(info.getTimestamp());
				sb.append("<br/>");
				sb.append("<br/>");
			}
		}
		return sb.toString();
	}
}
