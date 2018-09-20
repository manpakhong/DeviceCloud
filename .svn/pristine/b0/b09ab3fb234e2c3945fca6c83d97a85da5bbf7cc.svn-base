package com.littlecloud.ac.health;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.ClientUsagesDAO;
import com.littlecloud.control.dao.DailyClientUsagesDAO;
import com.littlecloud.control.dao.DailyDeviceUsagesDAO;
import com.littlecloud.control.dao.DeviceUsagesDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.dao.branch.SnsOrganizationsDAO;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.report.ClientUsages;
import com.littlecloud.control.entity.report.DailyClientUsages;
import com.littlecloud.control.entity.report.DailyDeviceUsages;
import com.littlecloud.control.entity.report.DeviceUsages;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.utils.HttpCall;
import com.littlecloud.pool.utils.PropertyService;
import com.peplink.api.db.util.DBUtil;

public class ReportHealthMonitor 
{
	private static PropertyService<ReportHealthMonitor> ps = new PropertyService<ReportHealthMonitor>(ReportHealthMonitor.class);
	private static final String consolidate_job_url = ps.getString("CONSOLIDATE_JOB_URL");
	private static final Logger log = Logger.getLogger(ReportHealthMonitor.class);
	
	public static boolean isConsolidateJobHealth()
	{
		boolean result = false;
		String content = null;
		
		
		HttpCall httpcall = new HttpCall(consolidate_job_url, null);
		content = httpcall.getText();
		if (content != null && content.startsWith("Normal Poo"))
		{
			result = true;
		}

//		URL path = null;
//		StringBuffer sb = null;		
//		try 
//		{
//			path = new URL(consolidate_job_url);
//			InputStream is = path.openStream();
//			java.io.BufferedInputStream bis = new java.io.BufferedInputStream(is);
//			int c = bis.read();
//			sb = new StringBuffer();
//			while(c != -1)
//			{
//				sb.append((char)c);
//				c = bis.read();
//			}			
//		} catch (Exception e) {
//			log.error("get consolidate status error - " + e);
//			return false;
//		}
//		if(!sb.toString().startsWith("Normal Poo"))
//		{
//			result = false;
//		}
		return result;
	}
	
	public static boolean isConsolidateResultCorrect()
	{
		boolean result = false;
		boolean devHealth = false;
		boolean clientHealth = false;
		DBUtil dbutil = null;
		try
		{
			dbutil = DBUtil.getInstance();
			dbutil.startSession();
			SnsOrganizationsDAO snsOrganizationsDAO = new SnsOrganizationsDAO();
			DailyDeviceUsagesDAO dailyDeviceDAO = null;
			DailyClientUsagesDAO dailyClientDAO = null;
			DeviceUsagesDAO deviceUsagesDAO = null;
			ClientUsagesDAO clientUsagesDAO = null;
			NetworksDAO networkDAO = null;
			List<String> orgIds = snsOrganizationsDAO.getTopTenOrgList();
			if(orgIds != null)
			{
				for(String orgId : orgIds)
				{
					networkDAO = new NetworksDAO(orgId);
					dailyDeviceDAO = new DailyDeviceUsagesDAO(orgId);
					dailyClientDAO = new DailyClientUsagesDAO(orgId);
					Integer network_id = dailyDeviceDAO.getAnExistingNetworkId();
					if( network_id != null )
					{
						Networks networks = networkDAO.findById(network_id);
						Date time = DateUtils.getUtcDate();
						Date nettime = DateUtils.offsetFromUtcTimeZoneId(time, networks.getTimezone());
						Integer unixtime = (int)(nettime.getTime()/1000) / 86400 * 86400;
						
						DailyDeviceUsages dailyDeviceUsages = dailyDeviceDAO.getLatestDate(unixtime,network_id);
						if( dailyDeviceUsages != null )
						{
							devHealth = true;
						}
						else
						{
							deviceUsagesDAO = new DeviceUsagesDAO(orgId);
							Integer usagetime = (((int)(time.getTime()/1000)) / 3600 * 3600) - 3600 * 2;
							time = new Date((long)usagetime*1000);
							time = DateUtils.offsetFromUtcTimeZoneId(time, networks.getTimezone());
							DeviceUsages devUsage = deviceUsagesDAO.getUsageOfNetworkIdAndTime(network_id, usagetime);
							if( devUsage != null && (int)(time.getTime() / 1000) >= unixtime)
							{
								devHealth = false;
							}
							else
							{
								devHealth = true;
							}
						}
						
						DailyClientUsages dailyClientUsages = dailyClientDAO.getLatestUnixtime(unixtime, network_id);
						if( dailyClientUsages != null )
						{
							clientHealth = true;
						}
						else
						{
							clientUsagesDAO = new ClientUsagesDAO(orgId);
							Integer usagetime = (((int)(time.getTime()/1000)) / 3600 * 3600) - 3600 * 2;
							time = new Date((long)usagetime*1000);
							time = DateUtils.offsetFromUtcTimeZoneId(time, networks.getTimezone());
							ClientUsages clientUsage = clientUsagesDAO.getUsageOfNetworkIdAndTime(network_id, usagetime);
							if( clientUsage != null && (int)(time.getTime() / 1000) >= unixtime )
							{
								clientHealth = false;
							}
							else
							{
								clientHealth = true;
							}
						}
					}
				}
				
				if( devHealth == true && clientHealth == true )
					result = true;
				else
					result = false;
			}
		}
		catch(Exception e)
		{
			log.error("check consolidate job error -"+e,e);
			return false;
		}
		finally
		{
			if(dbutil.isSessionStarted())
				dbutil.endSession();
		}
		
		return result;
	}
	
	public static String toHtml_job() {
		StringBuffer sb = new StringBuffer();
		sb.append("------------------Consolidate job INFO-----------------");
		sb.append("<br/>");
		log.debug("ReportHealthMonitor.isConsolidateJobHealth");
		if(ReportHealthMonitor.isConsolidateJobHealth())
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
			sb.append("Unhealthy");
			sb.append("</font>");
		}
		sb.append("<br/>");
		sb.append("<br/>");
		return sb.toString();
	}
	
	public static String toHtml_result() {
		StringBuffer sb = new StringBuffer();
		sb.append("------------------Consolidate result INFO-----------------");
		sb.append("<br/>");
		log.debug("ReportHealthMonitor.isConsolidateResultCorrect");
		if(ReportHealthMonitor.isConsolidateResultCorrect())
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
			sb.append("Unhealthy");
			sb.append("</font>");
		}
		sb.append("<br/>");
		sb.append("<br/>");
		return sb.toString();
	}
	
	public static void main(String[] args)
	{
		DateUtils.loadTimeZones();
		System.out.println(""+ReportHealthMonitor.isConsolidateResultCorrect());
	}
}
