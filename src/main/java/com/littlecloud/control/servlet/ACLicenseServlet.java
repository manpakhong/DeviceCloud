package com.littlecloud.control.servlet;

import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.ACLicenseObject;

@WebServlet(name = "Get AC License", description = "This is a servlet to get license information", urlPatterns = "/getAcLicense")
public class ACLicenseServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(ACLicenseServlet.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		ACLicenseObject acLic = null;
		ACLicenseObject acLicResult = null;
		
		String server = request.getParameter("server");		
		
		PrintWriter out = null;
		try 
		{
			out = response.getWriter();
						
			ACService.fetchBroadcast(MessageType.PIPE_INFO_AC_LICENSE, JsonUtils.genServerRef(), 0, ACService.getServerName());			
			
			acLic = new ACLicenseObject();
			if (server!=null && !server.isEmpty())
				acLic.setServer(server);
			else
				acLic.setServer(ACService.getServerName());
			
			acLic = ACUtil.getPoolObjectBySn(acLic, ACLicenseObject.class);
			if (acLic==null)
			{
				out.write("{\"error\":\"No license information.\"}");
				out.flush();
				return;
			}			
			acLic.setServer_time(DateUtils.getUtcUnixtime());			
			
			/* remove dummy fields */
			acLicResult = new ACLicenseObject();
			acLicResult.copyLicenseInfo(acLic);
			
			out.println(JsonUtils.toJson(acLicResult));
		} 
		catch (Exception e) 
		{
			logger.error(e, e);
		}
		finally {
			IOUtils.closeQuietly(out);
		}
	}
}
