package com.littlecloud.control.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.health.ACHealthMonitor;
import com.littlecloud.ac.health.CoreHealthMonitor;
import com.littlecloud.ac.health.DBHealthMonitor;
import com.littlecloud.ac.health.DiskHealthMonitor;
import com.littlecloud.ac.health.HealthMonitorHandler;
import com.littlecloud.ac.health.JVMHealthMonitor;
import com.littlecloud.ac.health.ReportHealthMonitor;
import com.littlecloud.ac.health.info.ACInfo;
import com.littlecloud.ac.health.info.CoreInfo;
import com.littlecloud.ac.health.info.DBInfo;
import com.littlecloud.ac.health.info.DiskInfo;
import com.littlecloud.ac.health.info.JVMInfo;
import com.littlecloud.ac.root.health.DBHealthMonitorRoot;
import com.littlecloud.ac.util.RootBranchRedirectManager;
import com.littlecloud.control.deviceconfig.DeviceConfigQueue;
import com.littlecloud.control.deviceconfig.DeviceConfigScheduler;
import com.littlecloud.control.webservices.WsTasksInfo;
import com.littlecloud.pool.Cluster;

@WebServlet(name="systemMonitor",urlPatterns="/sysMonitor")
public class SystemMonitorServlet extends HttpServlet
{
	private static Logger logger = Logger.getLogger(SystemMonitorServlet.class);
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		logger.warn("INFO sysMonitor is called");
		
		ACHealthMonitor acHealthMonitor = null;
		DBHealthMonitor dbHealthMonitor = null;
		DiskHealthMonitor diskHealthMonitor = null;
		JVMHealthMonitor jvmHealthMonitor = null;
		CoreHealthMonitor coreHealthMonitor = null;
				
		acHealthMonitor = new ACHealthMonitor();
		diskHealthMonitor = new DiskHealthMonitor();
		jvmHealthMonitor = new JVMHealthMonitor();
		coreHealthMonitor = new CoreHealthMonitor();
		if (RootBranchRedirectManager.isBranchServerMode())
			dbHealthMonitor = new DBHealthMonitor();
		else
			dbHealthMonitor = new DBHealthMonitorRoot();		
		
		logger.debug("acHealthMonitor.collectInfo");
		acHealthMonitor.collectInfo();
		logger.debug("dbHealthMonitor.collectInfo");
		dbHealthMonitor.collectInfo();
		logger.debug("diskHealthMonitor.collectInfo");
		diskHealthMonitor.collectInfo();
		logger.debug("jvmHealthMonitor.collectInfo");
		jvmHealthMonitor.collectInfo();
		logger.debug("coreHealthMonitor.collectInfo");
		coreHealthMonitor.collectInfo();
		
		logger.debug("acHealthMonitor.getInfo");
		ACInfo acInfo = acHealthMonitor.getInfo();
		logger.debug("dbHealthMonitor.getInfo");
		List<DBInfo> dbInfo = dbHealthMonitor.getInfo();
		logger.debug("diskHealthMonitor.getInfo");
		List<DiskInfo> diskInfo = diskHealthMonitor.getInfo();
		logger.debug("jvmHealthMonitor.getInfo");
		JVMInfo jvmInfo = jvmHealthMonitor.getInfo();
		logger.debug("coreHealthMonitor.getInfo");
		CoreInfo coreInfo = coreHealthMonitor.getInfo();
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("<html>");

		if (RootBranchRedirectManager.isBranchServerMode() && !dbHealthMonitor.getServerTimezone().equalsIgnoreCase("UTC"))
		{
			/* *********CLUSTER HEALTH STATUS**********/
			sb.append("CLUSTER HEALTH STATUS : ");
			logger.debug("HealthMonitorHandler.isHealthy");
			if(HealthMonitorHandler.isClusterHealthy())
			{
				sb.append("<font color=\"green\">");
				sb.append("Healthy");
				sb.append("</font>");
			}
			else
			{
				sb.append("<font color=\"red\">");
				sb.append("Unhealthy");
				sb.append(" ("+HealthMonitorHandler.getClusterStatusDetail()+")");
				sb.append("</font>");

			}			
			sb.append(" (WARNING: MySQL timezone is not UTC!! ( "+dbHealthMonitor.getServerTimezone()+"))");
		}
		sb.append("<br/>");
		sb.append("<br/>");
		
		/* *********SYSTEM HEALTH STATUS**********/
		sb.append("SYSTEM HEALTH STATUS : ");
		logger.debug("acHealthMonitor.isHealthy");
		if(acHealthMonitor.isHealthy() && dbHealthMonitor.isHealthy() && diskHealthMonitor.isHealthy() && coreHealthMonitor.isHealthy() && jvmHealthMonitor.isHealthy()	)
		{
			sb.append("<font color=\"green\">");
			sb.append("Healthy");
			sb.append("</font>");
		}
		else
		{
			sb.append("<font color=\"red\">");
			sb.append("Unhealthy\n");
			sb.append("</font>");
		}
		sb.append("<br/>");
		sb.append("<br/>");
		
	/* ******************** Top unhealthy item ******************** */
	ArrayList<String> APPEND_ORDER =  new ArrayList<String>();
	APPEND_ORDER.add("CORE_INFO");
	APPEND_ORDER.add("AC_INFO");
	APPEND_ORDER.add("DB_INFO");
	APPEND_ORDER.add("DISK_INFO");
	APPEND_ORDER.add("JVM_INFO");
	
	if (RootBranchRedirectManager.isBranchServerMode())
	{
		APPEND_ORDER.add("SCHEDULE_INFO");
		APPEND_ORDER.add("CONSOLIDATE_JOB_INFO");
		APPEND_ORDER.add("CONSOLIDATE_RESULT_INFO");
	}
	
	if(coreHealthMonitor.isHealthy()) {APPEND_ORDER.remove("CORE_INFO");APPEND_ORDER.add("CORE_INFO");}
	if(acHealthMonitor.isHealthy()) {APPEND_ORDER.remove("AC_INFO");APPEND_ORDER.add("AC_INFO");}
	if(dbHealthMonitor.isHealthy()) {APPEND_ORDER.remove("DB_INFO");APPEND_ORDER.add("DB_INFO");}
	if(diskHealthMonitor.isHealthy()) {APPEND_ORDER.remove("DISK_INFO");APPEND_ORDER.add("DISK_INFO");}
	if(jvmHealthMonitor.isHealthy()) {APPEND_ORDER.remove("JVM_INFO");APPEND_ORDER.add("JVM_INFO");}
	
	if (RootBranchRedirectManager.isBranchServerMode())
	{
		if(ACService.isSchedulerHealthy()) {APPEND_ORDER.remove("SCHEDULE_INFO");APPEND_ORDER.add("SCHEDULE_INFO");}
		if(ReportHealthMonitor.isConsolidateJobHealth()) {APPEND_ORDER.remove("CONSOLIDATE_JOB_INFO");APPEND_ORDER.add("CONSOLIDATE_JOB_INFO");}
		if(ReportHealthMonitor.isConsolidateResultCorrect()) {APPEND_ORDER.remove("CONSOLIDATE_RESULT_INFO");APPEND_ORDER.add("CONSOLIDATE_RESULT_INFO");}
	}
	
	for (int i=0; i<APPEND_ORDER.size(); i++) 
	{
		switch (APPEND_ORDER.get(i)) {
		case "CORE_INFO":		
			sb.append(coreHealthMonitor.toHtml(coreInfo));
			break;
		case "AC_INFO":
			sb.append(acHealthMonitor.toHtml(acInfo));
			break;
		case "DB_INFO":
			sb.append(dbHealthMonitor.toHtml(dbInfo));
			break;
		case "DISK_INFO":
			sb.append(diskHealthMonitor.toHtml(diskInfo));
			break;
		case "JVM_INFO":
			sb.append(jvmHealthMonitor.toHtml(jvmInfo));
			break;
		case "SCHEDULE_INFO":	
			sb.append(ACService.toHtml());
			break;
		case "CONSOLIDATE_JOB_INFO":
			sb.append(ReportHealthMonitor.toHtml_job());
			break;
		case "CONSOLIDATE_RESULT_INFO":
			sb.append(ReportHealthMonitor.toHtml_result());
			break;
		default:
			break;
		}
	}
//	String s = WsTasksInfo.getLastNWsInfos(100);
//	sb.append("------------------------------WsTasks INFO-----------------------------");
//	sb.append("<br/>");
//	sb.append(s);
 	sb.append("</html>");
	
 	PrintWriter pw = null;
	try
	{
		pw = response.getWriter();
		pw.write(sb.toString());
	} 
	catch (IOException e) 
	{
		e.printStackTrace();
	}
	finally
	{
		IOUtils.closeQuietly(pw);
	}
		
	}
	

}
