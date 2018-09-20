package com.littlecloud.pool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.WtpMsgHandlerPool;
import com.littlecloud.ac.handler.BranchServerRedirectionHandler;
import com.littlecloud.ac.health.ThreadPoolMonitor;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.ac.util.RootBranchRedirectManager;
import com.littlecloud.control.entity.branch.SnsOrganizations;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.Cluster.CACHE_ACTION;
import com.littlecloud.pool.Cluster.CacheException;
import com.littlecloud.pool.control.QueueCacheControl;
import com.littlecloud.pool.control.QueueControl;
import com.littlecloud.pool.control.QueueTaskInfo;
import com.littlecloud.pool.object.ACCommandObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.DevicesObject;
import com.littlecloud.pool.object.DistributedQueueObject;
import com.littlecloud.pool.object.utils.BranchUtils;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.utils.DBUtilManager;
import com.opensymphony.xwork2.ActionSupport;
import com.peplink.api.db.util.DBUtil;

public class MonitorAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {
	private static final WtpMsgHandlerPool wtpPool = WtpMsgHandlerPool.getInstance();
	private static final long serialVersionUID = 424115174489128522L;
	private static Logger log = Logger.getLogger(MonitorAction.class);
	private static final String logPrefix = "[CUSTOM ACTION] WARN ";
	private static final String CMD_WTPINFO = "capwap_cli WTP info";
	private static final ThreadPoolMonitor threadPoolMonitor = ThreadPoolMonitor.getInstance();
	protected HttpServletRequest request;
	protected HttpServletResponse response;

	protected static final String RESULT_TRUE = "true";
	protected static final String RESULT_FALSE = "false";
		
	private static enum REDIRECT_ACTION 
	{
		update, remove, monitor;
		
		public static REDIRECT_ACTION getEnumFromStr(String str)
		{
			for (REDIRECT_ACTION action:REDIRECT_ACTION.values())
			{
				if (str.equalsIgnoreCase(action.toString()))
					return action;
			}
			
			return monitor;
		}
	}
	
	// protected static QPService qpService = new QPServiceImpl();
	// protected ProxyPropertyBean proxyPropertyBean = ProxyPropertyBean.getInstance();

	public HttpServletRequest getServletRequest() {
		return request;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getServletResponse() {
		return response;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String monitorWtpInfo() throws IOException
	{
		response.setContentType("text/html");
		try {
			StringBuilder sb = new StringBuilder();
			Runtime aRT = Runtime.getRuntime();
			Process aProc = aRT.exec(CMD_WTPINFO);
			BufferedReader br = new BufferedReader(new InputStreamReader(aProc.getInputStream()));
			String line = "";
			
			int count = 0;
			sb.append("<table>");
			while ((line=br.readLine())!=null) {
				sb.append("<tr><td nowrap>");
				line = line.replaceAll("\\|", "</td><td nowrap>");
				sb.append(line);
				sb.append("</td></tr>\n");
				count++;
			}
			br.close();
			String str_count ="<font color=\"red\">  ****************** WTP COUNT  "+ count+ " *********************</font>";
			sb.append("</table>");
			sb.insert(0, str_count);
			response.getWriter().write(sb.toString());
			IOUtils.closeQuietly(response.getWriter());
		} catch (Exception e) {
			log.error("Exception " + e ,e);
		}
		return SUCCESS;
	}
	
	public String monitorConsolidate() throws IOException
	{
		response.setContentType("text/html");
		try {
			StringBuilder sb = new StringBuilder();
			DistributedQueueObject dqueue = DistributedQueueObject.getInstance();
			response.getWriter().write(dqueue.toString());
			IOUtils.closeQuietly(response.getWriter());
		} catch (Exception e) {
			log.error("Exception " + e ,e);
		}
		return SUCCESS;
	}

	@SkipValidation
	public String monitorThreadPool() throws IOException, SQLException
	{
		response.setContentType("text/html");
		
		StringBuilder sb = new StringBuilder();
		sb.append("<p style=\"font-family:Arial, sans-serif;font-size:14px;\">");
		sb.append(ThreadPoolMonitor.getAllInfo());
		sb.append("</p>");
		response.getWriter().write(sb.toString());//TODO ThreadPoolMonitor.toJson()
		IOUtils.closeQuietly(response.getWriter());
		
		return SUCCESS;
	}
	
	@SkipValidation
	public String monitorWtpPool() throws IOException, SQLException
	{
		response.getWriter().write(wtpPool.dumpPool());
		IOUtils.closeQuietly(response.getWriter());
		
		return SUCCESS;
	}
	
	@SkipValidation
	public String monitorBranchRedirection() throws IOException, SQLException
	{
		String s = Utils.getContentStr(request);
		String action = Utils.getParameter(request, s, "action");
		action = (action==null? REDIRECT_ACTION.monitor.toString():action);		
		REDIRECT_ACTION redirectAction = REDIRECT_ACTION.getEnumFromStr(action);
		
		BranchServerRedirectionHandler handler = BranchServerRedirectionHandler.getInstance();
		
		switch(redirectAction)
		{			
			case update:
			{
				/* add/update */
				Integer iana_id = Integer.valueOf(Utils.getParameter(request, s, "iana_id"));
				String sn = String.valueOf(Utils.getParameter(request, s, "sn"));				
				String host1 = String.valueOf(Utils.getParameter(request, s, "host1"));
				String host2 = String.valueOf(Utils.getParameter(request, s, "host2"));
				
				handler.updateMap(iana_id, sn, Arrays.asList(host1, host2));				
			}
			break;
			
			case remove:
			{
				/* remove */
				Integer iana_id = Integer.valueOf(Utils.getParameter(request, s, "iana_id"));
				String sn = String.valueOf(Utils.getParameter(request, s, "sn"));
				
				handler.removeMap(iana_id, sn);	
			}
			break;
	
			default: {
				/* monitor only - no action */						
			}
			break;
		}
				
		response.getWriter().write(JsonUtils.toJson(handler.getDeviceToBranchServersMap().toString()));
		IOUtils.closeQuietly(response.getWriter());
		
		return SUCCESS;
	}
	
	@SkipValidation
	public String monitorDbPool() throws IOException, SQLException
	{
		String s = Utils.getContentStr(request);
		String type = Utils.getParameter(request, s, "type");
		type = (type == null ? "DEFAULT" : type);
		response.setContentType("text/html");
		
		try {
			StringBuilder sb = new StringBuilder();
			List<DBUtil> dbUtilList = DBUtilManager.getAllDbUtilInstance();
			sb.append("<p style=\"font-family:Arial, sans-serif;font-size:14px;\">");
			sb.append("<font color=\"red\">******************** DBUtil Instances: ");
			
			if (dbUtilList != null){
				
				sb.append(dbUtilList.size());
				sb.append(" ******************** <br>");
				sb.append("*******************************************************************</font><br><br>");

				for (DBUtil dbUtil : dbUtilList) {
					sb.append(dbUtil.dumpDBPool());
					sb.append("<br><font color=\"red\">*******************************************************************</font><br>");
				}
			}
			sb.append("</p>");
			
			response.getWriter().write(sb.toString());
		} catch (Exception e)
		{
			response.getWriter().write(e.getMessage());
		}

		IOUtils.closeQuietly(response.getWriter());
		return SUCCESS;
	}

	@SkipValidation
	public String monitorDbInsert() throws IOException
	{
		boolean readonlydbHealth = false;
		boolean readwritedbHealth = false;
		StringBuilder sb = new StringBuilder();

		String s = Utils.getContentStr(request);
		String type = Utils.getParameter(request, s, "type");
		type = (type == null ? "DEFAULT" : type);

		try {
			readonlydbHealth = Monitor.dbTest(true);
			readwritedbHealth = Monitor.dbTest(false);
		} catch (SQLException e) {
			log.error("SQLException", e);
			readonlydbHealth = false;
			readwritedbHealth = false;
		}

		sb.append("|DB_READONLY_ACTIVE=");
		sb.append(readonlydbHealth == true ? RESULT_TRUE : RESULT_FALSE);

		sb.append("|DB_READWRITE_ACTIVE=");
		sb.append(readwritedbHealth == true ? RESULT_TRUE : RESULT_FALSE);

		response.getWriter().write(sb.toString());

		IOUtils.closeQuietly(response.getWriter());
		return SUCCESS;
	}

	@SkipValidation
	public String printQueue() throws IOException, SQLException
	{
		String nobr_s = "";
		String nobr_e = "";
		
		String tmp = null;
		StringBuilder sb = new StringBuilder();
		List<String> strLst = null;
		QueueTaskInfo qtaskInfo = null;
		
		String s = Utils.getContentStr(request);
		String pass = Utils.getParameter(request, s, "pass");
		String orgId = Utils.getParameter(request, s, "orgId");
		tmp = Utils.getParameter(request, s, "size");
		int size = (tmp==null?100:Integer.valueOf(tmp));
		String order = Utils.getParameter(request, s, "order");
		order = (order == null ? "true" : order);
		String type = Utils.getParameter(request, s, "type");
		type = (type == null ? "db" : type);
		String format = Utils.getParameter(request, s, "format");
		format = (format == null ? "chrome" : type);

		if (format.equalsIgnoreCase("nobr"))
		{
			nobr_s ="<NOBR>";
			nobr_e ="</NOBR>";
		}
		
		response.setContentType("text/html");
		sb.append("<FONT size=2 face=\"trebuchet ms\">");
		sb.append(nobr_s);
		sb.append("Cache logged.<hr>");
		sb.append("orgId: "+orgId+"<br>");
		sb.append("size: "+size+"<br>");
		sb.append("format: "+format+"<br>");
		sb.append("<a href=\"clearqueue?type="+type+"&orgId="+orgId+"&pass="+pass+"\">clear queue!</a>");
		sb.append("<br><hr><br>\n");
		
		if (orgId!=null)
		{
			if (type.equalsIgnoreCase("cache"))
			{
				strLst = QueueCacheControl.getQueueContent(orgId, size);
			}
			else
			{
				strLst = QueueControl.getQueueContent(orgId, size);
				qtaskInfo = QueueControl.getQueueRunningTasks().get(orgId);
			}
			
			sb.append("<br>");			
			sb.append("type: "+type);
			sb.append("<br><br>");
			
			if (qtaskInfo!=null)
			{
				sb.append(qtaskInfo.toHtml());
			}
			sb.append("<br><br>");
			
			for (String str:strLst)
			{
				sb.append(str);
				sb.append("<br><hr>\n");
			}
			sb.append(nobr_e);
			sb.append("</FONT>");
		}
		
		response.getWriter().write(sb.toString());
		IOUtils.closeQuietly(response.getWriter());
		return SUCCESS;
	}
	
	@SkipValidation
	public String clearQueue() throws IOException, SQLException
	{
		actionLog(request, "clearQueue() is called");
		
		MessageType mt = null;
		String msg = null;
		int size = -1;
		
		String s = Utils.getContentStr(request);
		String orgId = Utils.getParameter(request, s, "orgId");
		String type = Utils.getParameter(request, s, "type");
		type = (type == null ? "db" : type);
		String msg_type = Utils.getParameter(request, s, "msg_type");
		
		String pass = Utils.getParameter(request, s, "pass");
		if (orgId == null || pass == null || pass.compareToIgnoreCase("abc778899") != 0)
		{
			response.getWriter().write("ps="+(pass == null ? "" : pass));
			IOUtils.closeQuietly(response.getWriter());
			return SUCCESS;
		}
				
		if (type.equalsIgnoreCase("db"))
		{
			/* clear db queue */
			if (msg_type!=null)
			{
				mt = MessageType.getMessageType(msg_type);
				size = QueueControl.clearQueueMessageTypes(orgId, mt);
				msg = "message type "+mt+" in orgId "+orgId+" queue is deleted (size="+size+")";
				System.out.println(logPrefix+msg);
				response.getWriter().write(msg);
			}
			else
			{
				size = QueueControl.clearQueue(orgId);
				msg = "orgId "+orgId+" queue is flushed (size="+size+")";
				System.out.println(logPrefix+msg);
				response.getWriter().write(msg);			
			}
		}
		else
		{
			/* clear cache queue */
			if (msg_type!=null)
			{
				mt = MessageType.getMessageType(msg_type);
				size = QueueControl.clearQueueMessageTypes(orgId, mt);
				msg = "message type "+mt+" in orgId "+orgId+" cache queue is deleted (size="+size+")";
				System.out.println(logPrefix+msg);
				response.getWriter().write(msg);
			}
			else
			{
				size = QueueControl.clearQueue(orgId);
				msg = "orgId "+orgId+" cache queue is flushed (size="+size+")";
				System.out.println(logPrefix+msg);
				response.getWriter().write(msg);			
			}
		}
		
		IOUtils.closeQuietly(response.getWriter());
		return SUCCESS;
	}
	
	@SkipValidation
	public String monitorQueue() throws IOException, SQLException
	{
		Map<String, Integer> resultMap = new HashMap<String, Integer>();

		String s = Utils.getContentStr(request);
		String pass = Utils.getParameter(request, s, "pass");
		String type = Utils.getParameter(request, s, "type");
		String order = Utils.getParameter(request, s, "order");
		type = (type == null ? "db" : type);
		order = (order == null ? "true" : order);

		StringBuilder sb = new StringBuilder();
				
		sb.append("type: "+type);
		sb.append("<br>");
		sb.append("Active queue: ");
		if (type.equalsIgnoreCase("cache"))
		{
			Set<String> qSet = QueueCacheControl.getAllQueueSet();
			sb.append(qSet.size());
			sb.append("<br><hr><br>\n");
			
			if (qSet != null)
			{				
				for (String name : QueueCacheControl.getAllQueueSet()) {
					resultMap.put(name, QueueCacheControl.getQueueSize(name));
				}
	
				if (order.compareToIgnoreCase("true") == 0)
					resultMap = com.littlecloud.pool.utils.Utils.getSortedMap(resultMap, false);
	
				for (String name : resultMap.keySet()) {
					sb.append("Queue ");
					sb.append("<a href=\"printqueue?type=cache&orgId="+name+"&pass="+pass+"\">"+name+"</a>");				
					sb.append(":");
					sb.append(QueueCacheControl.getQueueSize(name));
					sb.append("<br>\n");
				}
			}
		}
		else
		{
			Set<String> qSet = QueueControl.getAllQueueSet();
			sb.append(qSet.size());
			sb.append("<br><hr><br>\n");
			
			if (qSet != null)
			{
				for (String name : QueueControl.getAllQueueSet()) {
					resultMap.put(name, QueueControl.getQueueSize(name));
				}
	
				if (order.compareToIgnoreCase("true") == 0)
					resultMap = com.littlecloud.pool.utils.Utils.getSortedMap(resultMap, false);
	
				for (String name : resultMap.keySet()) {
					sb.append("Queue ");
					sb.append("<a href=\"printqueue?orgId="+name+"&pass="+pass+"\">"+name+"</a>");	
					sb.append(":");
					sb.append(QueueControl.getQueueSize(name));
					sb.append("<br>\n");
				}
			}
			else
			{
				sb.append("No active queue");
			}
		}

		response.setContentType("text/html");
		response.getWriter().write(sb.toString());

		IOUtils.closeQuietly(response.getWriter());
		return SUCCESS;
	}

	@SkipValidation
	public String clearCache() throws IOException
	{
		actionLog(request, "clearCache() is called");
		
		String msg = null;
		String s = Utils.getContentStr(request);
		String ip = Utils.getPublicIP(request);
		String pass = Utils.getParameter(request, s, "pass");
		if (pass == null || pass.compareToIgnoreCase("qazwsx") != 0)
		{
			response.getWriter().write(pass == null ? "" : pass);
			IOUtils.closeQuietly(response.getWriter());
			return SUCCESS;
		}

		log.warn("[CUSTOM ACTION] Delete cache is called from IP " + ip);
		msg = "[CUSTOM ACTION] Cache clear!\n##############\n!!!!!!!!!!!!!!!!\n Cache clear !!!!!!!!!!!!!!!!\n################# by ip "+ip+"\n";
		Cluster.getCache(Cluster.CACHENAME.LittleCloudCache).clear();
		log.warnf(msg);
		System.out.println(logPrefix+msg);

		StringBuilder sb = new StringBuilder();
		sb.append("Cache cleared.<hr>");
		response.setContentType("text/html");
		response.getWriter().write(sb.toString());

		IOUtils.closeQuietly(response.getWriter());
		return SUCCESS;
	}

	@SkipValidation
	public String loadBalance() throws IOException
	{
		String monitor = monitorResult("loadBalance");
		String keyword = "active=true|";
		if (monitor.toLowerCase().substring(0, keyword.length()).compareToIgnoreCase(keyword) == 0)
		{
			response.getWriter().write("200 OK");
			IOUtils.closeQuietly(response.getWriter());

			log.info("loadBalance OK");
			return SUCCESS;
		}
		else
		{
			response.setStatus(500);
			IOUtils.closeQuietly(response.getWriter());

			log.error("loadBalance Failure");
			return ERROR;
		}
	}

	@SkipValidation
	public String monitorServer() throws IOException
	{
		String s = Utils.getContentStr(request);
		String type = Utils.getParameter(request, s, "type");
		type = (type == null ? "DEFAULT" : type);

		String result = monitorResult(type);

		Monitor.info(result);
		response.getWriter().write(result);

		IOUtils.closeQuietly(response.getWriter());
		return SUCCESS;
	}

	private String monitorResult(String type) {
		boolean active = false;
		// boolean clusterHealth = false;
		boolean readonlydbHealth = false;
		boolean readwritedbHealth = false;

		/* check restarting */
		if (!Cluster.isbRestarting())
			active = true;

		/* check cluster state */
		if (active)
		{
			switch (Cluster.getMyState())
			{
			case Initial:
				active = false;
				break;

			case Standalone:
				active = true;
				break;

			default:
				break;
			}
		}

		/* check sample cache insert ok */
		if (active)
		{
			// clusterHealth = Monitor.monitorTest(type);
			try {
				readonlydbHealth = Monitor.dbTest(true);
				readwritedbHealth = Monitor.dbTest(false);
			} catch (SQLException e) {
				log.error("MonitorException", e);
				readonlydbHealth = false;
				readwritedbHealth = false;
			}
		}

		/* show result */
		StringBuilder sb = new StringBuilder();
		sb.append("ACTIVE=");
		// sb.append(active == true && clusterHealth == true && readonlydbHealth == true && readwritedbHealth == true ?
		// RESULT_TRUE : RESULT_FALSE);
		sb.append(active == true && readonlydbHealth == true && readwritedbHealth == true ? RESULT_TRUE : RESULT_FALSE);

		sb.append("|CLUSTER_SIZE=");
		sb.append(Cluster.getClusterSize(Cluster.CACHENAME.LittleCloudCache));

		// sb.append("|CLUSTER_INSERT=");
		// sb.append(clusterHealth == true ? RESULT_TRUE : RESULT_FALSE);

		sb.append("|DB_READONLY_ACTIVE=");
		sb.append(readonlydbHealth == true ? RESULT_TRUE : RESULT_FALSE);

		sb.append("|DB_READWRITE_ACTIVE=");
		sb.append(readwritedbHealth == true ? RESULT_TRUE : RESULT_FALSE);

		sb.append("|TYPE=");
		sb.append(type);

		sb.append("|ID=");
		sb.append(Cluster.getClusterName() + "_" + ACService.getServerName());

		return sb.toString();
	}

	@SkipValidation
	public String evictAllCache() throws IOException
	{
		try {
			StringBuilder sb = new StringBuilder();
			ACUtil.evictAllCaches(sb);
			response.setContentType("text/html");
			response.getWriter().write(sb.toString());
		} catch (Exception e)
		{
			log.error("evictAllCache" ,e);
			response.setContentType("text/html");
			response.getWriter().write(e.getMessage());
		}
		
		IOUtils.closeQuietly(response.getWriter());
		return SUCCESS;
	}
	
	@SkipValidation
	public String monitorCache() throws IOException
	{
		try {
		
			StringBuilder sb = new StringBuilder();
			StringBuilder sbCache = new StringBuilder();
			int count = 0;
			int record_count = 100;
			
			String nobr_s = "";
			String nobr_e = "";			
			
			String s = Utils.getContentStr(request);
			String pass = Utils.getParameter(request, s, "pass");
			if (pass == null || pass.compareToIgnoreCase("abc778899") != 0)
			{
				response.getWriter().write(pass == null ? "ps=null" : pass);
				IOUtils.closeQuietly(response.getWriter());
				return SUCCESS;
			}
			String param_count = Utils.getParameter(request, s, "count");
			if (param_count!=null)
				record_count = Integer.valueOf(param_count);
						
			String dev = Utils.getParameter(request, s, "dev");
			String type = Utils.getParameter(request, s, "type");
			
			String msg = "requesting count "+count+" type "+type+" dev "+dev;
			System.out.println(logPrefix+msg);
			
			if (dev != null)
			{			
				Integer iana_id = null;;
				String sn = null;
				
				dev = dev.trim();
				
				count = ACUtil.getStatusCaches(dev, sbCache);
				count += ACUtil.getStatusCaches("BranchInfo", sbCache);
				count += ACUtil.getStatusCaches("OrgInfo", sbCache);
				
				if (dev.contains("_"))
				{
					String sp[] = dev.split("_");
					iana_id = Integer.valueOf(sp[0]);
					sn = sp[1];
				}
				else
				{
					iana_id = 23695;
					sn = dev;
				}
				
				DevicesObject devObj = NetUtils.getDevicesObjectUnsafe(iana_id, sn);
				if (devObj!=null)
				{
					SnsOrganizations snsOrg = BranchUtils.getSnsOrganizationsByIanaIdSn(iana_id, sn);
					if (snsOrg!=null)
					{
						String key = "NetInfosn_pk"+snsOrg.getOrganizationId()+"_"+devObj.getNetworkId();				
						count += ACUtil.getStatusCaches(key, sbCache);
					}
				}
			}
			else
			{
				
				if (type!=null && type.length()<5)
				{
					response.getWriter().write(pass == null ? "Type cannot be shorter than 5 characters." : pass);
					IOUtils.closeQuietly(response.getWriter());
					return SUCCESS;
				}
				
				if (type == null)
				{
					type = "DevOnlineObject";
				}
				count = ACUtil.getStatusCaches(type, sbCache, record_count);
			}
					
			String format = Utils.getParameter(request, s, "format");
			format = (format == null ? "chrome" : format);
	
			if (format.equalsIgnoreCase("nobr"))
			{
				nobr_s ="<NOBR>";
				nobr_e ="</NOBR>";
			}
	
			sb.append("dev: ");
			sb.append(dev);
			sb.append("<br>");
			sb.append("type: ");
			sb.append(type);
			sb.append("<br>");
			sb.append("format: ");
			sb.append(format);
			sb.append("<br>");		
			sb.append("count=");
			sb.append(record_count);
			sb.append("<br>");		
			sb.append("total=");
			sb.append(count);
			sb.append("<br>");
	
			sb.append("<FONT size=2 face=\"trebuchet ms\">Cache logged.<hr>"+ nobr_s + sbCache.toString() + nobr_e + "</FONT>");
			response.setContentType("text/html");
			response.getWriter().write(sb.toString());
		} catch (Exception e)
		{
			log.error("MonitorAction" ,e);
			response.setContentType("text/html");
			response.getWriter().write("Invalid parameters!");
		}
		// log.debug(sb.toString());

		IOUtils.closeQuietly(response.getWriter());
		return SUCCESS;
	}

	@SkipValidation
	public String forceGc() throws IOException
	{
		actionLog(request, "forceGc() is called");
		
		String s = Utils.getContentStr(request);
		String pass = Utils.getParameter(request, s, "pass");
		if (pass == null || pass.compareToIgnoreCase("abc778899") != 0)
		{
			response.getWriter().write(pass == null ? "" : pass);
			IOUtils.closeQuietly(response.getWriter());
			return SUCCESS;
		}

		log.info("Force Full GC begin...");
		System.gc();
		log.info("Force Full GC done");
		System.out.println(logPrefix+"Force Full GC done");

		StringBuilder sb = new StringBuilder();
		sb.append("Force Full GC done.<hr>");
		response.setContentType("text/html");
		response.getWriter().write(sb.toString());

		IOUtils.closeQuietly(response.getWriter());
		return SUCCESS;
	}

	
	@SkipValidation
	public String updateDevOnlineObject() throws IOException
	{
		actionLog(request, "updateDevOnlineObject() is called");		
		String s = Utils.getContentStr(request);
		String orgId = Utils.getParameter(request, s, "org");
		String pass = Utils.getParameter(request, s, "pass");
		if (pass == null || pass.compareToIgnoreCase("qazwsx") != 0)
		{
			response.getWriter().write(pass == null ? "" : pass);
			IOUtils.closeQuietly(response.getWriter());
			return SUCCESS;
		}
		
		int count = 0;
		try {
			Iterator<String> itr = Cluster.getKeys(Cluster.CACHENAME.LittleCloudCache).iterator();
			while (itr.hasNext())
			{				
				String key = (String) itr.next();				
				if (key.contains("DevOnlineObject"))
				{
					DevOnlineObject devO = ACUtil.getPoolObjectByKey(key);
					if (devO!=null)
					{
						SnsOrganizations snsOrg = BranchUtils.getSnsOrganizationsByIanaIdSn(devO.getIana_id(), devO.getSn());
						if (snsOrg!=null && (orgId==null || orgId.equals(snsOrg.getOrganizationId())))
						{						
							devO.setOrganization_id(snsOrg.getOrganizationId());
							DeviceUtils.putOnlineObject(devO);
							count++;
						}
					}
				}
			}			
		} catch (CacheException e) {
			log.error("updateDevOnlineObject() - CacheExceptioin " + e);
		}		
		
		response.getWriter().write("Number of online objects updated: "+count+" (org:"+orgId+")");
		
		IOUtils.closeQuietly(response.getWriter());
		return SUCCESS;
	}
	
	
	@SkipValidation
	public String reloadCache() throws IOException
	{				
		actionLog(request, "reloadCache() is called");
		
		StringBuilder sb = new StringBuilder();		
		String s = Utils.getContentStr(request);
		String type = Utils.getParameter(request, s, "type");
		if (type == null)
		{
			response.getWriter().write("type = "+type);
			IOUtils.closeQuietly(response.getWriter());
			return SUCCESS;
		}
		String pass = Utils.getParameter(request, s, "pass");
		if (pass == null || pass.compareToIgnoreCase("qazwsx") != 0)
		{
			response.getWriter().write(pass == null ? "" : pass);
			IOUtils.closeQuietly(response.getWriter());
			return SUCCESS;
		}
		
		ACUtil.reloadStatusCaches(type, sb);
		response.getWriter().write(sb.toString());
		
		IOUtils.closeQuietly(response.getWriter());
		return SUCCESS;
	}
	
	
	@SkipValidation
	public String deleteCache() throws IOException
	{
		StringBuilder sb = new StringBuilder();
		
		String s = Utils.getContentStr(request);
		String key = Utils.getParameter(request, s, "key");
		String ip = Utils.getPublicIP(request);
		String type = Utils.getParameter(request, s, "type");
		String pass = Utils.getParameter(request, s, "pass");
		if (pass == null || pass.compareToIgnoreCase("qazwsx") != 0)
		{
			response.getWriter().write(pass == null ? "" : pass);
			IOUtils.closeQuietly(response.getWriter());
			return SUCCESS;
		}

		log.warnf("[CUSTOM ACTION] Delete cache is called from IP %s type %s key %s", ip, type, key);

		if ((key == null || key.length() == 0) && (type==null || type.length()==0)) 
		{
			response.getWriter().write("no action. (type:NetInfoObject, OrgInfoObject, ProductInfoObject, BranchInfoObject)");
			IOUtils.closeQuietly(response.getWriter());
			return SUCCESS;
		}

		if (key != null && key.length() != 0)
		{
			try {
				if (Cluster.get(Cluster.CACHENAME.LittleCloudCache, key) == null)
				{
					response.getWriter().write("key " + key + " is not found from grid by ip "+ip);
					IOUtils.closeQuietly(response.getWriter());
					return SUCCESS;
				}
			} catch (CacheException e) {
				log.error("CacheException ", e);
				response.getWriter().write(e.getMessage());
			}
	
			try {
				ClusterOption opt = new ClusterOption();
				opt.setAction(CACHE_ACTION.remove);
				Cluster.remove(Cluster.CACHENAME.LittleCloudCache, key, opt);
				String msg = "key " + key + " is removed from grid by ip "+ip;
				System.out.println(logPrefix+msg);
				response.getWriter().write(msg);
			} catch (CacheException e) {
				log.error("CacheException ", e);
				response.getWriter().write(e.getMessage());
			}
		}
		
		if (type !=null && type.length()!=0)
		{
			if (type.startsWith("NetInfoObject") || type.startsWith("OrgInfoObject") || type.startsWith("ACCommandObject")
					|| type.startsWith("ProductInfoObject") || type.startsWith("BranchInfoObject") || type.startsWith("DevInfoObject") 
					|| type.startsWith("StationListObject") || type.startsWith("DevLocationsObject") || type.startsWith("DevLocationsReportObject")
					|| type.startsWith("StationUsageObject") || type.startsWith("DevOnlineObject") || type.startsWith("DevDetailObject") || type.startsWith("DevBandwidthObject"))
			{
				int count = ACUtil.deleteStatusCaches(type, sb);
				if (count==0)
				{
					String msg = "type " + type + " is not found from grid by ip "+ip;
					System.out.println(logPrefix+msg);
					response.getWriter().write("type " + type + " is not found from grid by ip "+ip);
				}
				else
				{
					String msg = "type " + type + " is removed from grid by ip "+ip;
					System.out.println(logPrefix+msg);
					response.getWriter().write(sb.toString());
				}
			}
		}

		IOUtils.closeQuietly(response.getWriter());
		return SUCCESS;
	}
	
	@SkipValidation
	public String test() throws IOException
	{
		String s = Utils.getContentStr(request);
		String key = Utils.getParameter(request, s, "key");
		if (StringUtils.isEmpty(key))
		{
			response.getWriter().write("key is empty");
			IOUtils.closeQuietly(response.getWriter());
			return SUCCESS;
		}
		
//		DevOnlineObject devO = DeviceUtils.getDevOnlineObject(23695, key);
//		if (devO == null)
//		{
//			response.getWriter().write("DevOnlineObject for key "+key+" is not found!");
//			IOUtils.closeQuietly(response.getWriter());
//			return SUCCESS;
//		}
//		
//		response.getWriter().write("DevOnlineObject = "+devO);
//		IOUtils.closeQuietly(response.getWriter());
//		return SUCCESS;
		
//		ConfigPutObject putObject = new ConfigPutObject();
//		putObject.setSid("sid1824-9CF6-B48C"); // find a valid sn
//		putObject.setFilepath("/tmp/test");
//		putObject.setSn("1824-9CF6-B48C");
//		ACService.<ConfigPutObject> fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_PUT_MASTER, "sid1824-9CF6-B48C", 23695, "1824-9CF6-B48C", putObject);
//		return SUCCESS;

		
		//		List<Products> pLst = ProductUtils.getProductLst();
		//		
		//		log.infof("pLst.size=%d ",pLst.size());
		//		for (Products p:pLst)
		//		{
		//			log.infof("Products %s ",p);
		//		}
		//		
		//		return SUCCESS;
		
		// try {
		// DeviceUpdatesDAO devUpDAO = new DeviceUpdatesDAO("riMA5x", false);
		// devUpDAO.incrementConfUpdateForDevLst(Arrays.asList(5558));
		// } catch (SQLException e) {
		// return ERROR;
		// }
		//
		// return SUCCESS;

		/*
		 * log.info("DH20140123 - test() is called");
		 * DevOnlineObject devOnlineO = null;
		 * final int MINUTE_BEFORE = 15;
		 * try{
		 * Integer ianaId = null;
		 * String sn = "";
		 * log.info("DH20140123 - before check request parameters");
		 * if (request.getParameter("iana_id") != null && request.getParameter("sn") != null){
		 * ianaId = new Integer(request.getParameter("iana_id"));
		 * sn = request.getParameter("sn");
		 * log.debugf("DH20140123 - parameters inan_id: %s, sn: %s", ianaId.toString(), sn);
		 * 
		 * 
		 * devOnlineO = new DevOnlineObject();
		 * devOnlineO.setIana_id(ianaId);
		 * devOnlineO.setSn(sn);
		 * 
		 * devOnlineO = ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
		 * 
		 * }
		 * 
		 * 
		 * int unixTimeNow = DateUtils.getUnixtime();
		 * int unixTime15MinutesBefore = DateUtils.getUnixTimeByDifferent(unixTimeNow, DateUtils.UNIX_TIME_UNIT_MINUTE,
		 * MINUTE_BEFORE, DateUtils.UNIX_TIME_OPERATOR_SUBSTRACT);
		 * 
		 * 
		 * 
		 * if (devOnlineO != null){
		 * log.debugf("DH20140123 - devOnlineO != null, current LastUpdateTime: %s",
		 * (devOnlineO.getLastUpdateTime().getTime() / 1000));
		 * devOnlineO.setLastUpdateTime(DateUtils.convertUnixtime2Date(unixTime15MinutesBefore));
		 * log.debugf("DH20140123 - lastUpdateTimeStamp: %s", unixTime15MinutesBefore);
		 * ACUtil.<DevOnlineObject> cachePoolObjectBySn(devOnlineO, DevOnlineObject.class);
		 * }
		 * 
		 * } catch (Exception e){
		 * log.error("DH20140123 - Exception ", e);
		 * }
		 */

		// ============================================

		// Integer iana_id = 23695;
		// String sn = "1234-1234-1234";
		//
		// InternalCmdObject cmdO = new InternalCmdObject();
		// cmdO.setCommand(CMD.DEL_CFG_TMP_FILE);
		// cmdO.setParam("/var/run/ac/config/abc");
		//
		// QueryInfo<InternalCmdObject> info = new QueryInfo<InternalCmdObject>();
		// info.setType(MessageType.PIPE_INFO_TYPE_INTERNAL_CMD);
		// info.setSid(JsonUtils.genServerRef());
		// info.setIana_id(23695);
		// info.setSn(sn);
		// info.setData(cmdO);
		//

		// ACService.<InternalCmdObject> fetchCommand(MessageType.PIPE_INFO_TYPE_INTERNAL_CMD, JsonUtils.genServerRef(),
		// iana_id, sn, cmdO);
		// =============================
		// ACService.set_dev_op_mode("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", 23695, "2830-A2C8-CF3C",
		// ACService.OP_MODE.OPMODE_NORMAL);

		// DevDetailObject dev = new DevDetailObject();
		// dev.setDevice_id(1);
		// dev.setIana_id(1);
		// dev.setNetwork_id(1);
		// dev.setOrganization_id("1");
		// dev.setSid("1");
		// dev.setSn("1");
		// dev.set

		// =============================
		// String param_orgId = "xoxcXd";
		// Integer param_networkId = 4;
		// boolean bReadOnlyDb = true;
		//
		// HibernateUtil hutil = new HibernateUtil(param_orgId, bReadOnlyDb);
		//
		// ClientUsagesDAO clientUsageDAO = new ClientUsagesDAO(param_orgId, bReadOnlyDb);
		//
		// try {
		// hutil.beginBranchTransaction();
		//
		// List<Object> objectList = clientUsageDAO.getLatestClientCount(param_orgId, param_networkId);
		// Iterator<Object> iterator = objectList.iterator();
		// log.info("deviceId\t clientCount\t datetime");
		// while (iterator.hasNext()) {
		// Object[] obj = (Object[]) iterator.next();
		// log.infof("%d, %d, %s", obj[0], obj[1], obj[2]);
		// }
		//
		// hutil.commitBranchTransaction();
		// } catch (Exception e) {
		// log.error("transaction is rollback - " + e, e);
		// hutil.rollbackBranchTransaction();
		// }

		// =============================
		// int product_id = 1;
		// String org_id = "xoxcXd";
		// int net_id = 4;
		// String fw_url = "http://10.8.102.55/tmp/fw-m700_hd2-6.1.0lc0726-build2251.bin";
		// String fw_version = "6.1.0lc0726";
		// log.infof("performFirmwareUpgrade result = %s", FirmwareUpgrade.performFirmwareUpgrade(product_id, org_id,
		// net_id, fw_url, fw_version));

		// ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_DETAIL, "99999999999999999999999999", 23695,
		// "2830-A2C8-CF3C");

		ACCommandObject acObj = new ACCommandObject();
		acObj.setType(MessageType.PIPE_INFO_TYPE_DEV_DETAIL);
		acObj.setSid("99999999999999999999" + JsonUtils.genServerRef());
		acObj.setIana_id(23695);
		acObj.setSn(key);
		
		QueryInfo info = new QueryInfo();
		info.setIana_id(acObj.getIana_id());
		info.setSn(acObj.getSn());
		info.setSid(acObj.getSid());
		info.setType(MessageType.PIPE_INFO_TYPE_DEV_DETAIL);
		acObj.setJson(JsonUtils.toJsonCompact(info));
		log.debug("Fetch command JSon:" + JsonUtils.toJson(acObj));
		response.getWriter().write(JsonUtils.toJson(acObj));

		try {
			ACUtil.<ACCommandObject> cachePoolObjectBySn(acObj, ACCommandObject.class);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("fail to cachePoolObjectBySn (2)");
		}

		IOUtils.closeQuietly(response.getWriter());		 
		return SUCCESS;
	}
	
	private static void actionLog(HttpServletRequest request, String msg)
	{
		String ip = Utils.getPublicIP(request);		
		System.out.println(logPrefix+" from ip "+ip+msg);
	}
}